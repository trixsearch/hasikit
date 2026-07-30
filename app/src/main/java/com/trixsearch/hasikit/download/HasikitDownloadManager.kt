package com.trixsearch.hasikit.download

import android.content.Context
import android.util.Log
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.trixsearch.hasikit.domain.model.DownloadState
import com.trixsearch.hasikit.domain.model.DownloadTask
import com.trixsearch.hasikit.domain.model.Video
import com.trixsearch.hasikit.domain.repository.VideoRepository
import com.trixsearch.hasikit.telegram.service.TelegramClientService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import org.drinkless.tdlib.TdApi
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

private const val TAG = "HasikitDownload"

@Singleton
class HasikitDownloadManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: VideoRepository,
    private val telegramClientService: TelegramClientService
) {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val workManager = WorkManager.getInstance(context)

    private val _downloadTasks = MutableStateFlow<Map<String, DownloadTask>>(emptyMap())
    val downloadTasks: StateFlow<Map<String, DownloadTask>> = _downloadTasks

    // Custom download path — set by SettingsViewModel when user picks a folder
    val customDownloadPath = MutableStateFlow("")

    // Gallery visibility — synced from SettingsViewModel; used by DownloadWorker to decide storage location
    val galleryVisible = MutableStateFlow(false)

    // Delete files when deleted in app — true = immediate delete, false = move to trash
    val deleteFilesOnDelete = MutableStateFlow(true)

    // Bug fix #4: Thumbnail cache invalidation signal — SettingsViewModel increments this after
    // clearing thumbnail cache; HomeViewModel observes it and calls invalidateAndReloadThumbnails()
    val thumbnailCacheVersion = MutableStateFlow(0)

    init {
        // Sync in-memory state from Room DB on startup
        scope.launch {
            repository.getAllDownloads().collect { tasks ->
                _downloadTasks.value = tasks.associateBy { it.videoId }
            }
        }
        Log.d(TAG, "HasikitDownloadManager initialized (WorkManager mode)")
    }

    // Bug fix #12: Download location — always read customDownloadPath at enqueue time
    // so re-downloads after deletion use the current selected folder, not a stale path
    fun startDownload(video: Video) {
        val fileId = video.telegramFileId.toLongOrNull()
        if (fileId == null || fileId == 0L) {
            Log.e(TAG, "startDownload — no valid telegramFileId for videoId=${video.id}")
            return
        }
        Log.d(TAG, "startDownload videoId=${video.id} fileId=$fileId title='${video.title}'")

        scope.launch {
            // Trash restore: if deleteFilesOnDelete=false and a matching file exists in trash,
            // restore it immediately instead of downloading again
            val restoredPath = if (!deleteFilesOnDelete.value) restoreFromTrash(video.title) else null
            if (restoredPath != null) {
                Log.d(TAG, "startDownload restored from trash videoId=${video.id} path=$restoredPath")
                repository.insertVideo(video)
                repository.saveDownload(
                    DownloadTask(videoId = video.id, state = DownloadState.COMPLETED, progress = 1f, localPath = restoredPath)
                )
                repository.updateVideo(video.copy(isDownloaded = true, localPath = restoredPath, downloadProgress = 1f))
                return@launch
            }

            repository.saveDownload(
                DownloadTask(videoId = video.id, state = DownloadState.QUEUED, progress = 0f)
            )
            repository.insertVideo(video)

            // Bug fix #12: read current customDownloadPath at enqueue time — never cache old path
            val destDir = customDownloadPath.value.takeIf { it.isNotBlank() }
            Log.d(TAG, "[DOWNLOAD_PATH] startDownload videoId=${video.id} destDir=$destDir")
            val inputData = DownloadWorker.buildInputData(
                videoId = video.id,
                telegramFileId = video.telegramFileId,
                title = video.title,
                destDir = destDir
            )

            val request = OneTimeWorkRequestBuilder<DownloadWorker>()
                .setInputData(inputData)
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                )
                // Exponential backoff — retry after 30s on transient failures
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 30, TimeUnit.SECONDS)
                .build()

            // KEEP_EXISTING prevents re-enqueuing if already running for this video
            workManager.enqueueUniqueWork(
                DownloadWorker.workName(video.id),
                ExistingWorkPolicy.KEEP,
                request
            )
            Log.d(TAG, "startDownload enqueued WorkManager job for videoId=${video.id}")
        }
    }

    // Pause — cancels the WorkManager worker and marks state as PAUSED in DB
    fun pauseDownload(videoId: String) {
        scope.launch {
            val task = repository.getDownload(videoId) ?: return@launch
            if (task.state != DownloadState.DOWNLOADING && task.state != DownloadState.QUEUED) return@launch
            Log.d(TAG, "pauseDownload videoId=$videoId")

            // Cancel the WorkManager worker first
            workManager.cancelUniqueWork(DownloadWorker.workName(videoId))

            // Cancel TDLib download to stop network usage
            val video = repository.getVideoById(videoId)
            video?.telegramFileId?.toLongOrNull()?.let { fileId ->
                telegramClientService.send(TdApi.CancelDownloadFile(fileId.toInt(), false)) {}
            }

            // Mark as PAUSED in DB
            repository.saveDownload(task.copy(state = DownloadState.PAUSED))
        }
    }

    // Resume — re-enqueues the WorkManager worker from current TDLib progress
    fun resumeDownload(video: Video) {
        scope.launch {
            val task = repository.getDownload(video.id) ?: return@launch
            if (task.state != DownloadState.PAUSED) return@launch
            Log.d(TAG, "resumeDownload videoId=${video.id}")

            // Update state to DOWNLOADING before re-enqueuing
            repository.saveDownload(task.copy(state = DownloadState.DOWNLOADING))
        }

        // Bug fix #12: read current customDownloadPath at resume time — user may have changed folder
        val destDir = customDownloadPath.value.takeIf { it.isNotBlank() }
        Log.d(TAG, "[DOWNLOAD_PATH] resumeDownload videoId=${video.id} destDir=$destDir")
        val inputData = DownloadWorker.buildInputData(
            videoId = video.id,
            telegramFileId = video.telegramFileId,
            title = video.title,
            destDir = destDir
        )

        val request = OneTimeWorkRequestBuilder<DownloadWorker>()
            .setInputData(inputData)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 30, TimeUnit.SECONDS)
            .build()

        // REPLACE — cancel any stale worker and start fresh
        workManager.enqueueUniqueWork(
            DownloadWorker.workName(video.id),
            ExistingWorkPolicy.REPLACE,
            request
        )
        Log.d(TAG, "resumeDownload re-enqueued WorkManager job for videoId=${video.id}")
    }

    fun retryDownload(video: Video) {
        scope.launch { repository.deleteDownload(video.id) }
        startDownload(video)
    }

    // Trash folder path — files moved here when deleteFilesOnDelete=false
    private fun trashDir(): File {
        val base = context.getExternalFilesDir(android.os.Environment.DIRECTORY_MOVIES)
            ?: context.filesDir
        return File(base.parentFile ?: base, ".trash").also { it.mkdirs() }
    }

    // Delete download — removes or trashes physical file, clears DB entry, cancels active worker.
    // Behaviour controlled by deleteFilesOnDelete flag:
    //   true  → delete file immediately from storage
    //   false → move file to .trash folder; re-download checks trash first
    fun deleteDownload(videoId: String) {
        scope.launch {
            val task = repository.getDownload(videoId)
            val video = repository.getVideoById(videoId)
            Log.d(TAG, "deleteDownload videoId=$videoId localPath=${video?.localPath} taskPath=${task?.localPath} deleteImmediate=${deleteFilesOnDelete.value}")

            // Cancel WorkManager worker if running
            workManager.cancelUniqueWork(DownloadWorker.workName(videoId))

            // Cancel TDLib download to stop any active network transfer
            video?.telegramFileId?.toLongOrNull()?.let { fileId ->
                telegramClientService.send(TdApi.CancelDownloadFile(fileId.toInt(), false)) {}
            }

            // Collect all physical file paths to handle
            val paths = buildSet {
                task?.localPath?.let { add(it) }
                video?.localPath?.let { add(it) }
            }

            if (deleteFilesOnDelete.value) {
                // Immediate delete — remove files from storage
                paths.forEach { path ->
                    val deleted = File(path).delete()
                    Log.d(TAG, "deleteDownload deleted $path: $deleted")
                }
            } else {
                // Trash mode — move files to .trash folder and record metadata
                val trash = trashDir()
                paths.forEach { path ->
                    val src = File(path)
                    if (src.exists()) {
                        val dest = File(trash, src.name)
                        val moved = src.renameTo(dest)
                        Log.d(TAG, "deleteDownload trashed $path → ${dest.absolutePath}: $moved")
                    }
                }
            }

            // Remove download record from DB
            repository.deleteDownload(videoId)

            // Update video row: mark as NOT DOWNLOADED, clear localPath
            // Video row is kept so history and metadata are preserved
            video?.let {
                repository.updateVideo(
                    it.copy(isDownloaded = false, localPath = null, downloadProgress = 0f)
                )
            }

            Log.d(TAG, "deleteDownload complete videoId=$videoId")
        }
    }

    // Check trash for a previously deleted file and restore it if found.
    // Called by startDownload before enqueuing a new WorkManager job.
    // Returns the restored local path if successful, null otherwise.
    fun restoreFromTrash(title: String): String? {
        val trash = trashDir()
        if (!trash.exists()) return null
        // Match by sanitised title prefix — same logic DownloadWorker uses for file naming
        val sanitised = title.replace(Regex("[^a-zA-Z0-9._\\- ]"), "_").take(80)
        val match = trash.listFiles()?.firstOrNull { it.name.startsWith(sanitised) } ?: return null
        val dest = File(
            context.getExternalFilesDir(android.os.Environment.DIRECTORY_MOVIES) ?: context.filesDir,
            match.name
        )
        return if (match.renameTo(dest)) {
            Log.d(TAG, "restoreFromTrash restored ${match.name} → ${dest.absolutePath}")
            dest.absolutePath
        } else null
    }

    // Observe WorkManager state for a specific video — used by UI to show live progress
    fun getWorkInfoFlow(videoId: String) =
        workManager.getWorkInfosForUniqueWorkLiveData(DownloadWorker.workName(videoId))

    // Cancel TDLib streaming cache for a video — called when clearing temporary cache
    // Does NOT affect permanently downloaded files
    fun clearStreamingCache(fileId: Long) {
        scope.launch {
            telegramClientService.send(TdApi.DeleteFile(fileId.toInt())) { result ->
                Log.d(TAG, "clearStreamingCache fileId=$fileId result=$result")
            }
        }
    }
}
