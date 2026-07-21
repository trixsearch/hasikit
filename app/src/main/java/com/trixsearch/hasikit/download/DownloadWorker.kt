package com.trixsearch.hasikit.download

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.trixsearch.hasikit.domain.model.DownloadState
import com.trixsearch.hasikit.domain.model.DownloadTask
import com.trixsearch.hasikit.domain.repository.VideoRepository
import com.trixsearch.hasikit.telegram.service.TelegramClientService
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.delay
import kotlinx.coroutines.suspendCancellableCoroutine
import org.drinkless.tdlib.TdApi
import java.io.File
import kotlin.coroutines.resume

private const val TAG = "DownloadWorker"

// WorkManager input keys — passed when enqueuing the worker
const val KEY_VIDEO_ID = "videoId"
const val KEY_TELEGRAM_FILE_ID = "telegramFileId"
const val KEY_VIDEO_TITLE = "videoTitle"
const val KEY_DEST_DIR = "destDir"

// WorkManager output/progress keys
const val KEY_PROGRESS = "progress"
const val KEY_LOCAL_PATH = "localPath"
const val KEY_ERROR = "error"

// Stall detection — mark FAILED after this many consecutive inactive polls
private const val STALL_THRESHOLD = 5

@HiltWorker
class DownloadWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted params: WorkerParameters,
    private val repository: VideoRepository,
    private val telegramClientService: TelegramClientService
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val videoId = inputData.getString(KEY_VIDEO_ID) ?: return Result.failure()
        val fileIdStr = inputData.getString(KEY_TELEGRAM_FILE_ID) ?: return Result.failure()
        val title = inputData.getString(KEY_VIDEO_TITLE) ?: videoId
        val destDir = inputData.getString(KEY_DEST_DIR)
        val fileId = fileIdStr.toLongOrNull()?.toInt() ?: return Result.failure()

        Log.d(TAG, "doWork START videoId=$videoId fileId=$fileId")

        // Mark as DOWNLOADING in DB so UI reflects state immediately
        repository.saveDownload(
            DownloadTask(videoId = videoId, state = DownloadState.DOWNLOADING, progress = 0f)
        )

        // Tell TDLib to start downloading with high priority
        val startResult = startTdlibDownload(fileId)
        if (startResult == null) {
            Log.e(TAG, "doWork TDLib DownloadFile failed videoId=$videoId")
            repository.saveDownload(
                DownloadTask(videoId = videoId, state = DownloadState.FAILED, progress = 0f)
            )
            return Result.failure(workDataOf(KEY_ERROR to "TDLib DownloadFile failed"))
        }

        // Poll TDLib until download completes, fails, or is cancelled
        var stallCount = 0
        while (true) {
            // Check if worker was cancelled (user paused or deleted)
            if (isStopped) {
                Log.d(TAG, "doWork STOPPED videoId=$videoId")
                return Result.failure(workDataOf(KEY_ERROR to "cancelled"))
            }

            val file = getFileInfo(fileId)
            if (file == null) {
                delay(2000)
                continue
            }

            val local = file.local
            val progress = if (file.size > 0) local.downloadedSize.toFloat() / file.size else 0f

            when {
                local.isDownloadingCompleted -> {
                    // Copy file to permanent destination
                    val destPath = copyToDestination(local.path, title, videoId, destDir)
                    Log.d(TAG, "doWork COMPLETE videoId=$videoId path=$destPath")

                    repository.saveDownload(
                        DownloadTask(
                            videoId = videoId,
                            state = DownloadState.COMPLETED,
                            progress = 1f,
                            localPath = destPath
                        )
                    )
                    // Update video row to reflect downloaded state
                    repository.getVideoById(videoId)?.let { video ->
                        repository.updateVideo(
                            video.copy(isDownloaded = true, localPath = destPath, downloadProgress = 1f)
                        )
                    }
                    return Result.success(workDataOf(KEY_LOCAL_PATH to destPath))
                }

                local.isDownloadingActive -> {
                    stallCount = 0
                    // Report progress to WorkManager so UI can observe it
                    setProgress(workDataOf(KEY_PROGRESS to progress))
                    // Update DB progress so HomeScreen download bar reflects current state
                    repository.saveDownload(
                        DownloadTask(videoId = videoId, state = DownloadState.DOWNLOADING, progress = progress)
                    )
                    delay(1000)
                }

                else -> {
                    stallCount++
                    if (stallCount >= STALL_THRESHOLD) {
                        Log.w(TAG, "doWork STALLED videoId=$videoId after $stallCount polls")
                        repository.saveDownload(
                            DownloadTask(videoId = videoId, state = DownloadState.FAILED, progress = progress)
                        )
                        return Result.failure(workDataOf(KEY_ERROR to "stalled"))
                    }
                    delay(2000)
                }
            }
        }
    }

    private suspend fun startTdlibDownload(fileId: Int): TdApi.File? =
        suspendCancellableCoroutine { cont ->
            telegramClientService.send(TdApi.DownloadFile(fileId, 1, 0, 0, false)) { result ->
                cont.resume(if (result is TdApi.File) result else null)
            }
            cont.invokeOnCancellation {}
        }

    private suspend fun getFileInfo(fileId: Int): TdApi.File? =
        suspendCancellableCoroutine { cont ->
            telegramClientService.send(TdApi.GetFile(fileId)) { result ->
                cont.resume(if (result is TdApi.File) result else null)
            }
            cont.invokeOnCancellation {}
        }

    // Copy completed TDLib file to permanent destination.
    // If destDir is a content:// SAF URI, write via DocumentFile so the file lands in the
    // user-selected folder. If it is a plain file path, write directly. Falls back to
    // app-private Movies dir when destDir is null or blank.
    private fun copyToDestination(sourcePath: String, title: String, videoId: String, destDir: String?): String {
        val src = java.io.File(sourcePath)
        if (!src.exists()) {
            Log.w(TAG, "copyToDestination source missing: $sourcePath")
            return sourcePath
        }

        val ext = sourcePath.substringAfterLast('.', "mp4")
        val safeId = videoId.replace(Regex("[^a-zA-Z0-9_\\-]"), "_").take(30)
        val safeTitle = title.replace(Regex("[^a-zA-Z0-9._\\- ]"), "_").take(60)
        val fileName = "${safeTitle}_${safeId}.$ext"

        // SAF path: destDir is a content:// tree URI from the folder picker
        if (!destDir.isNullOrBlank() && destDir.startsWith("content://")) {
            return try {
                val treeUri = android.net.Uri.parse(destDir)
                val docDir = androidx.documentfile.provider.DocumentFile.fromTreeUri(context, treeUri)
                    ?: throw Exception("DocumentFile.fromTreeUri returned null for $destDir")
                // Delete existing file with same name to allow overwrite
                docDir.findFile(fileName)?.delete()
                val mimeType = when (ext.lowercase()) {
                    "mkv" -> "video/x-matroska"
                    "webm" -> "video/webm"
                    "mov" -> "video/quicktime"
                    else -> "video/mp4"
                }
                val destDoc = docDir.createFile(mimeType, fileName)
                    ?: throw Exception("DocumentFile.createFile failed for $fileName in $destDir")
                context.contentResolver.openOutputStream(destDoc.uri)?.use { out ->
                    src.inputStream().use { it.copyTo(out) }
                } ?: throw Exception("openOutputStream returned null for ${destDoc.uri}")
                Log.d(TAG, "[DOWNLOAD_PATH] SAF copy success uri=${destDoc.uri}")
                // Return the content URI string so the player can open it via ContentResolver
                destDoc.uri.toString()
            } catch (e: Exception) {
                Log.e(TAG, "copyToDestination SAF failed, falling back to app-private", e)
                copyToAppPrivate(src, fileName)
            }
        }

        // Plain file path: destDir is an absolute path string
        if (!destDir.isNullOrBlank() && !destDir.startsWith("content://")) {
            return try {
                val dir = java.io.File(destDir).also { it.mkdirs() }
                val dest = java.io.File(dir, fileName)
                src.copyTo(dest, overwrite = true)
                Log.d(TAG, "[DOWNLOAD_PATH] file path copy success path=${dest.absolutePath}")
                dest.absolutePath
            } catch (e: Exception) {
                Log.e(TAG, "copyToDestination file path failed, falling back to app-private", e)
                copyToAppPrivate(src, fileName)
            }
        }

        // No destDir set: use app-private Movies directory
        return copyToAppPrivate(src, fileName)
    }

    // Fallback: copy to app-private Movies directory (always writable, no permissions needed)
    private fun copyToAppPrivate(src: java.io.File, fileName: String): String {
        val dir = context.getExternalFilesDir(android.os.Environment.DIRECTORY_MOVIES)
            ?: context.filesDir
        dir.mkdirs()
        val dest = java.io.File(dir, fileName)
        return try {
            src.copyTo(dest, overwrite = true)
            Log.d(TAG, "[DOWNLOAD_PATH] app-private fallback path=${dest.absolutePath}")
            dest.absolutePath
        } catch (e: Exception) {
            Log.e(TAG, "copyToAppPrivate failed", e)
            src.absolutePath
        }
    }

    companion object {
        // Unique work name per video — prevents duplicate workers for the same video
        fun workName(videoId: String) = "download_$videoId"

        fun buildInputData(
            videoId: String,
            telegramFileId: String,
            title: String,
            destDir: String? = null
        ): Data = workDataOf(
            KEY_VIDEO_ID to videoId,
            KEY_TELEGRAM_FILE_ID to telegramFileId,
            KEY_VIDEO_TITLE to title,
            KEY_DEST_DIR to destDir
        )
    }
}
