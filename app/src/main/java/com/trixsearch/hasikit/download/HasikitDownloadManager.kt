package com.trixsearch.hasikit.download

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import com.trixsearch.hasikit.domain.model.DownloadState
import com.trixsearch.hasikit.domain.model.DownloadTask
import com.trixsearch.hasikit.domain.model.Video
import com.trixsearch.hasikit.domain.repository.VideoRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "HasikitDownload"
private const val USER_AGENT = "Hasikit/1.0 (Android)"

private val SUPPORTED_EXTENSIONS = setOf("mp4", "mov", "mkv", "webm", "m4v")

private val MIME_TO_EXT = mapOf(
    "video/mp4" to "mp4",
    "video/quicktime" to "mov",
    "video/x-matroska" to "mkv",
    "video/webm" to "webm",
    "video/x-m4v" to "m4v"
)

private val URL_EXT_REGEX = Regex("""\.([a-zA-Z0-9]{2,4})(?:[?#].*)?$""")

private fun resolveExtension(url: String, mimeType: String?): String {
    val fromUrl = URL_EXT_REGEX.find(url)?.groupValues?.get(1)?.lowercase()
    if (fromUrl in SUPPORTED_EXTENSIONS) return fromUrl!!
    return MIME_TO_EXT[mimeType] ?: "mp4"
}

@Singleton
class HasikitDownloadManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: VideoRepository
) {
    private val systemDownloadManager =
        context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val _downloadTasks = MutableStateFlow<Map<String, DownloadTask>>(emptyMap())
    val downloadTasks: StateFlow<Map<String, DownloadTask>> = _downloadTasks

    // Tracks which videoIds already have an active monitor coroutine
    private val activeMonitors = mutableSetOf<String>()
    private val monitorMutex = Mutex()

    init {
        scope.launch {
            repository.getAllDownloads().collect { tasks ->
                _downloadTasks.value = tasks.associateBy { it.videoId }
                tasks.forEach { task ->
                    if (task.state == DownloadState.DOWNLOADING) {
                        startMonitorIfNeeded(task.videoId)
                    }
                }
            }
        }
        Log.d(TAG, "HasikitDownloadManager initialized")
    }

    fun startDownload(video: Video) {
        val ext = resolveExtension(video.videoUrl, null)
        val fileName = "${video.id}.$ext"
        Log.d(TAG, "Download START videoId=${video.id} title='${video.title}' url=${video.videoUrl} file=$fileName")

        val request = DownloadManager.Request(Uri.parse(video.videoUrl))
            .setTitle(video.title)
            .setDescription("Downloading via Hasikit")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalFilesDir(context, Environment.DIRECTORY_MOVIES, fileName)
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(true)
            .addRequestHeader("User-Agent", USER_AGENT)
            .addRequestHeader("Accept", "video/*, */*")

        val downloadId = systemDownloadManager.enqueue(request)
        Log.d(TAG, "Enqueued downloadId=$downloadId for videoId=${video.id}")

        scope.launch {
            val task = DownloadTask(
                videoId = video.id,
                state = DownloadState.DOWNLOADING,
                progress = 0f,
                downloadId = downloadId
            )
            repository.saveDownload(task)
            repository.insertVideo(video)
            startMonitorIfNeeded(video.id)
        }
    }

    private suspend fun startMonitorIfNeeded(videoId: String) {
        monitorMutex.withLock {
            if (videoId in activeMonitors) {
                Log.d(TAG, "Monitor already active for videoId=$videoId, skipping")
                return
            }
            activeMonitors.add(videoId)
        }
        Log.d(TAG, "Starting monitor for videoId=$videoId")
        scope.launch { monitorDownload(videoId) }
    }

    private suspend fun monitorDownload(videoId: String) {
        try {
            var lastLoggedProgress = -1
            while (true) {
                val task = repository.getDownload(videoId)
                if (task == null || task.downloadId == null) {
                    Log.d(TAG, "Monitor stopping — no task/downloadId for videoId=$videoId")
                    break
                }

                val cursor = systemDownloadManager.query(
                    DownloadManager.Query().setFilterById(task.downloadId)
                )

                if (!cursor.moveToFirst()) {
                    cursor.close()
                    Log.w(TAG, "Monitor: cursor empty for videoId=$videoId downloadId=${task.downloadId}")
                    delay(2000)
                    continue
                }

                val status = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS))
                val bytesDownloaded =
                    cursor.getLong(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
                val bytesTotal =
                    cursor.getLong(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
                val progress = if (bytesTotal > 0) bytesDownloaded.toFloat() / bytesTotal else 0f
                val progressPct = (progress * 100).toInt()

                when (status) {
                    DownloadManager.STATUS_SUCCESSFUL -> {
                        val localUri =
                            cursor.getString(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_LOCAL_URI))
                        val localPath = Uri.parse(localUri).path
                        cursor.close()
                        Log.d(TAG, "Download COMPLETE videoId=$videoId path=$localPath size=$bytesDownloaded bytes")

                        repository.saveDownload(
                            task.copy(state = DownloadState.COMPLETED, progress = 1f, localPath = localPath)
                        )
                        repository.getVideoById(videoId)?.let {
                            repository.updateVideo(it.copy(isDownloaded = true, localPath = localPath))
                        }
                        break
                    }

                    DownloadManager.STATUS_FAILED -> {
                        val reason =
                            cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_REASON))
                        cursor.close()
                        Log.e(TAG, "Download FAILED videoId=$videoId reason=$reason (${describeFailReason(reason)})")
                        repository.saveDownload(task.copy(state = DownloadState.FAILED, errorCode = reason))
                        break
                    }

                    DownloadManager.STATUS_PAUSED -> {
                        val reason =
                            cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_REASON))
                        cursor.close()
                        Log.d(TAG, "Download PAUSED videoId=$videoId reason=$reason progress=$progressPct%")
                        if (progress != task.progress) {
                            repository.saveDownload(task.copy(progress = progress, state = DownloadState.PAUSED))
                        }
                    }

                    DownloadManager.STATUS_PENDING -> {
                        cursor.close()
                        Log.d(TAG, "Download PENDING videoId=$videoId")
                    }

                    else -> {
                        cursor.close()
                        if (progressPct != lastLoggedProgress) {
                            Log.d(TAG, "Download PROGRESS videoId=$videoId $progressPct% ($bytesDownloaded/$bytesTotal bytes)")
                            lastLoggedProgress = progressPct
                        }
                        if (progress != task.progress) {
                            repository.saveDownload(task.copy(progress = progress))
                        }
                    }
                }
                delay(1000)
            }
        } finally {
            monitorMutex.withLock { activeMonitors.remove(videoId) }
            Log.d(TAG, "Monitor ended for videoId=$videoId")
        }
    }

    fun retryDownload(video: Video) {
        scope.launch {
            val existing = repository.getDownload(video.id)
            if (existing != null) {
                existing.downloadId?.let { systemDownloadManager.remove(it) }
                repository.deleteDownload(video.id)
            }
            Log.d(TAG, "Retrying download for videoId=${video.id}")
        }
        startDownload(video)
    }

    fun deleteDownload(videoId: String) {
        scope.launch {
            val task = repository.getDownload(videoId)
            task?.downloadId?.let {
                systemDownloadManager.remove(it)
                Log.d(TAG, "Cancelled downloadId=$it for videoId=$videoId")
            }
            repository.deleteDownload(videoId)
            repository.getVideoById(videoId)?.let { video ->
                video.localPath?.let { path ->
                    val deleted = File(path).delete()
                    Log.d(TAG, "Deleted local file=$path success=$deleted")
                }
                repository.updateVideo(video.copy(isDownloaded = false, localPath = null, downloadProgress = 0f))
            }
            Log.d(TAG, "Download record removed videoId=$videoId")
        }
    }

    private fun describeFailReason(reason: Int): String = when (reason) {
        DownloadManager.ERROR_CANNOT_RESUME -> "CANNOT_RESUME"
        DownloadManager.ERROR_DEVICE_NOT_FOUND -> "DEVICE_NOT_FOUND"
        DownloadManager.ERROR_FILE_ALREADY_EXISTS -> "FILE_ALREADY_EXISTS"
        DownloadManager.ERROR_FILE_ERROR -> "FILE_ERROR"
        DownloadManager.ERROR_HTTP_DATA_ERROR -> "HTTP_DATA_ERROR"
        DownloadManager.ERROR_INSUFFICIENT_SPACE -> "INSUFFICIENT_SPACE"
        DownloadManager.ERROR_TOO_MANY_REDIRECTS -> "TOO_MANY_REDIRECTS"
        DownloadManager.ERROR_UNHANDLED_HTTP_CODE -> "UNHANDLED_HTTP_CODE"
        DownloadManager.ERROR_UNKNOWN -> "UNKNOWN"
        400 -> "HTTP_400_BAD_REQUEST"
        401 -> "HTTP_401_UNAUTHORIZED"
        403 -> "HTTP_403_FORBIDDEN"
        404 -> "HTTP_404_NOT_FOUND"
        else -> "reason=$reason"
    }
}
