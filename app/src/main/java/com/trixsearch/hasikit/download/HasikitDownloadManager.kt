package com.trixsearch.hasikit.download

import android.content.Context
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

    private fun copyToMoviesDir(sourcePath: String, title: String, videoId: String): String {
        val src = File(sourcePath)
        if (!src.exists()) return sourcePath
        val ext = sourcePath.substringAfterLast('.', "mp4")
        val safeId = videoId.replace(Regex("[^a-zA-Z0-9_\\-]"), "_").take(30)
        val safeTitle = title.replace(Regex("[^a-zA-Z0-9._\\- ]"), "_").take(60)
        val customUriStr = customDownloadPath.value
        val destDir: File = if (customUriStr.isNotBlank()) {
            try {
                val uri = Uri.parse(customUriStr)
                val docFile = androidx.documentfile.provider.DocumentFile.fromTreeUri(context, uri)
                if (docFile?.canWrite() == true) {
                    // Use app-specific Movies dir as fallback since SAF write needs streams
                    context.getExternalFilesDir(Environment.DIRECTORY_MOVIES) ?: return sourcePath
                } else {
                    context.getExternalFilesDir(Environment.DIRECTORY_MOVIES) ?: return sourcePath
                }
            } catch (e: Exception) {
                context.getExternalFilesDir(Environment.DIRECTORY_MOVIES) ?: return sourcePath
            }
        } else {
            context.getExternalFilesDir(Environment.DIRECTORY_MOVIES) ?: return sourcePath
        }
        val dest = File(destDir, "${safeTitle}_${safeId}.$ext")
        return try {
            src.copyTo(dest, overwrite = true)
            Log.d(TAG, "Copied to: ${dest.absolutePath}")
            dest.absolutePath
        } catch (e: Exception) {
            Log.e(TAG, "Copy failed, using TDLib path: $sourcePath", e)
            sourcePath
        }
    }

    fun retryDownload(video: Video) {
        scope.launch { repository.deleteDownload(video.id) }
        startDownload(video)
    }

    fun deleteDownload(videoId: String) {
        scope.launch {
            val task = repository.getDownload(videoId)
            val video = repository.getVideoById(videoId)
            video?.telegramFileId?.toLongOrNull()?.let { fileId ->
                telegramClientService.send(TdApi.CancelDownloadFile(fileId.toInt(), false)) {}
            }
            task?.localPath?.let { path -> File(path).delete() }
            video?.localPath?.let { path -> if (path != task?.localPath) File(path).delete() }
            repository.deleteDownload(videoId)
            video?.let { repository.updateVideo(it.copy(isDownloaded = false, localPath = null, downloadProgress = 0f)) }
            Log.d(TAG, "Download record removed videoId=$videoId")
        }
    }
}
