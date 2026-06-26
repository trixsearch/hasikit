package com.trixsearch.hasikit.download

import android.app.DownloadManager
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Environment
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
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HasikitDownloadManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: VideoRepository
) {
    private val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val _downloadTasks = MutableStateFlow<Map<String, DownloadTask>>(emptyMap())
    val downloadTasks: StateFlow<Map<String, DownloadTask>> = _downloadTasks

    init {
        scope.launch {
            repository.getAllDownloads().collect { tasks ->
                _downloadTasks.value = tasks.associateBy { it.videoId }
                tasks.forEach { task ->
                    if (task.state == DownloadState.DOWNLOADING) {
                        monitorDownload(task.videoId)
                    }
                }
            }
        }
    }

    fun startDownload(video: Video) {
        val request = DownloadManager.Request(Uri.parse(video.videoUrl))
            .setTitle(video.title)
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalFilesDir(context, Environment.DIRECTORY_MOVIES, "${video.id}.mp4")
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(true)

        val downloadId = downloadManager.enqueue(request)
        
        scope.launch {
            val task = DownloadTask(
                videoId = video.id,
                state = DownloadState.DOWNLOADING,
                progress = 0f,
                downloadId = downloadId
            )
            repository.saveDownload(task)
            repository.insertVideo(video) // Ensure video is in DB
            monitorDownload(video.id)
        }
    }

    private fun monitorDownload(videoId: String) {
        scope.launch {
            var isDownloading = true
            while (isDownloading) {
                val task = repository.getDownload(videoId) ?: break
                if (task.downloadId == null) break

                val query = DownloadManager.Query().setFilterById(task.downloadId)
                val cursor = downloadManager.query(query)
                if (cursor.moveToFirst()) {
                    val status = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS))
                    val bytesDownloaded = cursor.getLong(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
                    val bytesTotal = cursor.getLong(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
                    
                    val progress = if (bytesTotal > 0) bytesDownloaded.toFloat() / bytesTotal else 0f

                    when (status) {
                        DownloadManager.STATUS_SUCCESSFUL -> {
                            val localUri = cursor.getString(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_LOCAL_URI))
                            val localPath = Uri.parse(localUri).path
                            val updatedTask = task.copy(state = DownloadState.COMPLETED, progress = 1f, localPath = localPath)
                            repository.saveDownload(updatedTask)
                            
                            val video = repository.getVideoById(videoId)
                            video?.let {
                                repository.updateVideo(it.copy(isDownloaded = true, localPath = localPath))
                            }
                            isDownloading = false
                        }
                        DownloadManager.STATUS_FAILED -> {
                            val reason = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_REASON))
                            repository.saveDownload(task.copy(state = DownloadState.FAILED, errorCode = reason))
                            isDownloading = false
                        }
                        else -> {
                            if (progress != task.progress) {
                                repository.saveDownload(task.copy(progress = progress))
                            }
                        }
                    }
                }
                cursor.close()
                delay(1000)
            }
        }
    }

    fun deleteDownload(videoId: String) {
        scope.launch {
            val task = repository.getDownload(videoId)
            task?.downloadId?.let {
                downloadManager.remove(it)
            }
            repository.deleteDownload(videoId)
            val video = repository.getVideoById(videoId)
            video?.let {
                it.localPath?.let { path ->
                    File(path).delete()
                }
                repository.updateVideo(it.copy(isDownloaded = false, localPath = null))
            }
        }
    }
}
