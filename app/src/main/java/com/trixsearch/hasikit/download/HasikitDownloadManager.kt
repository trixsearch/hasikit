package com.trixsearch.hasikit.download

import android.content.Context
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Environment
import android.util.Log
import com.trixsearch.hasikit.domain.model.DownloadState
import com.trixsearch.hasikit.domain.model.DownloadTask
import com.trixsearch.hasikit.domain.model.Video
import com.trixsearch.hasikit.domain.repository.VideoRepository
import com.trixsearch.hasikit.telegram.service.TelegramClientService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.drinkless.tdlib.TdApi
import java.io.File
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

    private val _downloadTasks = MutableStateFlow<Map<String, DownloadTask>>(emptyMap())
    val downloadTasks: StateFlow<Map<String, DownloadTask>> = _downloadTasks

    private val activeMonitors = mutableSetOf<String>()
    private val monitorMutex = Mutex()
    // Tracks which videoIds are intentionally paused — prevents auto-resume
    private val pausedIds = mutableSetOf<String>()

    init {
        scope.launch {
            repository.getAllDownloads().collect { tasks ->
                _downloadTasks.value = tasks.associateBy { it.videoId }
                tasks.forEach { task ->
                    // Only auto-resume DOWNLOADING state, never PAUSED
                    if (task.state == DownloadState.DOWNLOADING) {
                        startMonitorIfNeeded(task.videoId)
                    }
                }
            }
        }
        Log.d(TAG, "HasikitDownloadManager initialized")
    }

    fun startDownload(video: Video) {
        val telegramFileId = video.telegramFileId.toLongOrNull()
        if (telegramFileId == null || telegramFileId == 0L) {
            Log.e(TAG, "startDownload — no valid telegramFileId for videoId=${video.id}")
            return
        }
        Log.d(TAG, "startDownload videoId=${video.id} fileId=$telegramFileId title='${video.title}'")
        scope.launch {
            repository.saveDownload(DownloadTask(videoId = video.id, state = DownloadState.DOWNLOADING, progress = 0f))
            repository.insertVideo(video)
            startMonitorIfNeeded(video.id)
            telegramClientService.send(TdApi.DownloadFile(telegramFileId.toInt(), 1, 0, 0, false)) { result ->
                when (result) {
                    is TdApi.File  -> Log.d(TAG, "DownloadFile started fileId=$telegramFileId path=${result.local.path}")
                    is TdApi.Error -> Log.e(TAG, "DownloadFile error ${result.code}: ${result.message}")
                }
            }
        }
    }

    private suspend fun startMonitorIfNeeded(videoId: String) {
        monitorMutex.withLock {
            if (videoId in activeMonitors) return
            activeMonitors.add(videoId)
        }
        scope.launch { monitorDownload(videoId) }
    }

    fun pauseDownload(videoId: String) {
        scope.launch {
            val task = repository.getDownload(videoId) ?: return@launch
            if (task.state != DownloadState.DOWNLOADING) return@launch
            Log.d(TAG, "pauseDownload videoId=$videoId")
            // Mark paused BEFORE cancelling TDLib so monitor loop exits cleanly
            pausedIds.add(videoId)
            repository.saveDownload(task.copy(state = DownloadState.PAUSED))
            val video = repository.getVideoById(videoId)
            video?.telegramFileId?.toLongOrNull()?.let { fileId ->
                telegramClientService.send(TdApi.CancelDownloadFile(fileId.toInt(), false)) {}
            }
        }
    }

    fun resumeDownload(video: Video) {
        scope.launch {
            val task = repository.getDownload(video.id) ?: return@launch
            if (task.state != DownloadState.PAUSED) return@launch
            Log.d(TAG, "resumeDownload videoId=${video.id}")
            pausedIds.remove(video.id)
            repository.saveDownload(task.copy(state = DownloadState.DOWNLOADING))
            startMonitorIfNeeded(video.id)
            val fileId = video.telegramFileId.toLongOrNull() ?: return@launch
            telegramClientService.send(TdApi.DownloadFile(fileId.toInt(), 1, 0, 0, false)) { result ->
                when (result) {
                    is TdApi.File  -> Log.d(TAG, "resumeDownload DownloadFile started fileId=$fileId")
                    is TdApi.Error -> Log.e(TAG, "resumeDownload error ${result.code}: ${result.message}")
                }
            }
        }
    }

    private suspend fun monitorDownload(videoId: String) {
        try {
            var running = true
            // Grace period: TDLib may take a few seconds to start downloading after DownloadFile is sent
            var stallCount = 0
            val stallThreshold = 5 // 5 consecutive inactive polls = stalled
            while (running) {
                val task = repository.getDownload(videoId)
                if (task == null || task.state == DownloadState.COMPLETED || task.state == DownloadState.FAILED) {
                    running = false
                    continue
                }
                if (task.state == DownloadState.PAUSED || pausedIds.contains(videoId)) {
                    running = false
                    continue
                }
                val video = repository.getVideoById(videoId)
                val fileId = video?.telegramFileId?.toLongOrNull()
                if (video == null || fileId == null) {
                    running = false
                    continue
                }
                val file = getFileInfo(fileId.toInt())
                if (file == null) {
                    delay(2000)
                    continue
                }
                val local = file.local
                val progress = if (file.size > 0) local.downloadedSize.toFloat() / file.size else 0f

                when {
                    local.isDownloadingCompleted -> {
                        val destPath = copyToMoviesDir(local.path, video.title, video.id)
                        Log.d(TAG, "Download COMPLETE videoId=$videoId path=$destPath")
                        repository.saveDownload(task.copy(state = DownloadState.COMPLETED, progress = 1f, localPath = destPath))
                        repository.updateVideo(video.copy(isDownloaded = true, localPath = destPath, downloadProgress = 1f))
                        running = false
                    }
                    local.isDownloadingActive -> {
                        // Reset stall counter while actively downloading
                        stallCount = 0
                        if (progress != task.progress) {
                            repository.saveDownload(task.copy(progress = progress))
                            Log.d(TAG, "Download PROGRESS videoId=$videoId ${(progress * 100).toInt()}% (${local.downloadedSize}/${file.size})")
                        }
                        delay(1000)
                    }
                    else -> {
                        // Not active — count consecutive inactive polls before marking failed
                        if (pausedIds.contains(videoId)) {
                            running = false
                        } else {
                            stallCount++
                            if (stallCount >= stallThreshold) {
                                Log.w(TAG, "Download stalled videoId=$videoId after $stallCount polls — marking FAILED")
                                repository.saveDownload(task.copy(state = DownloadState.FAILED))
                                running = false
                            } else {
                                Log.d(TAG, "Download inactive videoId=$videoId stallCount=$stallCount — waiting")
                                delay(2000)
                            }
                        }
                    }
                }
            }
        } finally {
            monitorMutex.withLock { activeMonitors.remove(videoId) }
            Log.d(TAG, "Monitor ended for videoId=$videoId")
        }
    }

    private suspend fun getFileInfo(fileId: Int): TdApi.File? =
        suspendCancellableCoroutine { cont ->
            telegramClientService.send(TdApi.GetFile(fileId)) { result ->
                cont.resume(if (result is TdApi.File) result else null)
            }
            cont.invokeOnCancellation {}
        }

    /** Updated by SettingsViewModel when user picks a custom folder */
    val customDownloadPath = MutableStateFlow("")

    // FIX: galleryVisible flag — synced from SettingsViewModel so download manager can notify MediaScanner
    val galleryVisible = MutableStateFlow(false)

    /**
     * FIX: Download location logic:
     *   galleryVisible=OFF  → private app storage (getExternalFilesDir/Movies)
     *   galleryVisible=ON + customPath set  → write to SAF folder via streams, notify MediaScanner
     *   galleryVisible=ON + no customPath   → write to Download/Hasikit/ public dir, notify MediaScanner
     */
    private fun copyToMoviesDir(sourcePath: String, title: String, videoId: String): String {
        val src = File(sourcePath)
        if (!src.exists()) {
            Log.w(TAG, "copyToMoviesDir — source file not found: $sourcePath")
            return sourcePath
        }
        // Preserve original file extension from source path
        val ext = sourcePath.substringAfterLast('.', "mp4")
        val safeId = videoId.replace(Regex("[^a-zA-Z0-9_\\-]"), "_").take(30)
        val safeTitle = title.replace(Regex("[^a-zA-Z0-9._\\- ]"), "_").take(60)
        val fileName = "${safeTitle}_${safeId}.$ext"
        val customUriStr = customDownloadPath.value
        val isGalleryOn = galleryVisible.value

        Log.d(TAG, "copyToMoviesDir galleryVisible=$isGalleryOn customPath='$customUriStr' fileName=$fileName")

        return when {
            // Gallery ON + custom SAF folder selected — write via SAF streams
            isGalleryOn && customUriStr.isNotBlank() -> {
                try {
                    val treeUri = Uri.parse(customUriStr)
                    val docDir = androidx.documentfile.provider.DocumentFile.fromTreeUri(context, treeUri)
                    if (docDir?.canWrite() == true) {
                        // Determine MIME type from extension
                        val mime = when (ext.lowercase()) {
                            "mkv" -> "video/x-matroska"
                            "webm" -> "video/webm"
                            "mov" -> "video/quicktime"
                            "m4v" -> "video/x-m4v"
                            else -> "video/mp4"
                        }
                        // Delete existing file with same name to avoid duplicates
                        docDir.findFile(fileName)?.delete()
                        val destDoc = docDir.createFile(mime, fileName)
                        if (destDoc != null) {
                            context.contentResolver.openOutputStream(destDoc.uri)?.use { out ->
                                src.inputStream().use { it.copyTo(out) }
                            }
                            // Notify MediaScanner so file appears in Gallery apps
                            android.media.MediaScannerConnection.scanFile(
                                context, arrayOf(destDoc.uri.toString()), arrayOf(mime)
                            ) { path, uri -> Log.d(TAG, "MediaScanner SAF scanned path=$path uri=$uri") }
                            Log.d(TAG, "copyToMoviesDir — SAF write success uri=${destDoc.uri}")
                            // Return the SAF URI string as the stored path
                            destDoc.uri.toString()
                        } else {
                            Log.w(TAG, "copyToMoviesDir — SAF createFile failed, falling back to private storage")
                            copyToPrivateMoviesDir(src, fileName)
                        }
                    } else {
                        Log.w(TAG, "copyToMoviesDir — SAF dir not writable, falling back to private storage")
                        copyToPrivateMoviesDir(src, fileName)
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "copyToMoviesDir — SAF write failed", e)
                    copyToPrivateMoviesDir(src, fileName)
                }
            }
            // Gallery ON + no custom folder — write to public Download/Hasikit/ dir
            isGalleryOn && customUriStr.isBlank() -> {
                try {
                    val publicDir = File(
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                        "Hasikit"
                    ).also { it.mkdirs() }
                    val dest = File(publicDir, fileName)
                    src.copyTo(dest, overwrite = true)
                    // Notify MediaScanner so file appears in Gallery apps
                    android.media.MediaScannerConnection.scanFile(
                        context, arrayOf(dest.absolutePath), null
                    ) { path, uri -> Log.d(TAG, "MediaScanner public scanned path=$path uri=$uri") }
                    Log.d(TAG, "copyToMoviesDir — public Download/Hasikit/ write success path=${dest.absolutePath}")
                    dest.absolutePath
                } catch (e: Exception) {
                    Log.e(TAG, "copyToMoviesDir — public dir write failed", e)
                    copyToPrivateMoviesDir(src, fileName)
                }
            }
            // Gallery OFF — store privately, do NOT notify MediaScanner
            else -> copyToPrivateMoviesDir(src, fileName)
        }
    }

    // Write to app-private Movies dir — not visible in Gallery
    private fun copyToPrivateMoviesDir(src: File, fileName: String): String {
        val destDir = context.getExternalFilesDir(Environment.DIRECTORY_MOVIES) ?: return src.absolutePath
        val dest = File(destDir, fileName)
        return try {
            src.copyTo(dest, overwrite = true)
            Log.d(TAG, "copyToPrivateMoviesDir — private storage path=${dest.absolutePath}")
            dest.absolutePath
        } catch (e: Exception) {
            Log.e(TAG, "copyToPrivateMoviesDir — copy failed, using TDLib path", e)
            src.absolutePath
        }
    }

    // FIX #6 — Auto-download streamable videos when streaming begins
    // Called by PlayerScreen when playback starts for a streamable video not yet downloaded
    fun startBackgroundDownloadIfNeeded(video: Video) {
        scope.launch {
            val existing = repository.getDownload(video.id)
            val isAlreadyDownloaded = video.isDownloaded || existing?.state == DownloadState.COMPLETED
            val isAlreadyDownloading = existing?.state == DownloadState.DOWNLOADING || existing?.state == DownloadState.PAUSED
            if (isAlreadyDownloaded || isAlreadyDownloading) {
                Log.d(TAG, "FIX #6 — Auto-download skipped videoId=${video.id} alreadyDownloaded=$isAlreadyDownloaded alreadyDownloading=$isAlreadyDownloading")
                return@launch
            }
            Log.d(TAG, "FIX #6 — Auto-download started for streaming video videoId=${video.id} title='${video.title}'")
            startDownload(video)
        }
    }

    fun retryDownload(video: Video) {
        scope.launch { repository.deleteDownload(video.id) }
        startDownload(video)
    }

    // FIX #8 — Delete video from DB + physical file + cached TDLib file
    fun deleteDownload(videoId: String) {
        scope.launch {
            val task = repository.getDownload(videoId)
            val video = repository.getVideoById(videoId)
            Log.d(TAG, "FIX #8 — deleteDownload videoId=$videoId localPath=${video?.localPath} taskPath=${task?.localPath}")
            // Cancel any active TDLib download first
            video?.telegramFileId?.toLongOrNull()?.let { fileId ->
                telegramClientService.send(TdApi.CancelDownloadFile(fileId.toInt(), false)) {}
            }
            // FIX #8 — Delete physical file from task localPath
            task?.localPath?.let { path ->
                val file = java.io.File(path)
                val deleted = file.delete()
                Log.d(TAG, "FIX #8 — Deleted task file $path: $deleted")
            }
            // FIX #8 — Delete physical file from video localPath (may differ from task path)
            video?.localPath?.let { path ->
                if (path != task?.localPath) {
                    val file = java.io.File(path)
                    val deleted = file.delete()
                    Log.d(TAG, "FIX #8 — Deleted video file $path: $deleted")
                }
            }
            // FIX #9 — Also check custom download folder for the file
            val customUriStr = customDownloadPath.value
            if (customUriStr.isNotBlank() && video != null) {
                try {
                    val uri = android.net.Uri.parse(customUriStr)
                    val docDir = androidx.documentfile.provider.DocumentFile.fromTreeUri(context, uri)
                    val safeId = videoId.replace(Regex("[^a-zA-Z0-9_\\-]"), "_").take(30)
                    docDir?.listFiles()?.filter { it.name?.contains(safeId) == true }?.forEach {
                        val deleted = it.delete()
                        Log.d(TAG, "FIX #9 — Deleted from custom folder ${it.name}: $deleted")
                    }
                } catch (e: Exception) {
                    Log.w(TAG, "FIX #9 — Could not delete from custom folder", e)
                }
            }
            // FIX #8 — Remove download record from DB
            repository.deleteDownload(videoId)
            // FIX #8 — Update video record to reflect deletion (not fully deleting video row so history is preserved)
            video?.let { repository.updateVideo(it.copy(isDownloaded = false, localPath = null, downloadProgress = 0f)) }
            Log.d(TAG, "FIX #8 — Delete complete videoId=$videoId")
        }
    }
}
