package com.trixsearch.hasikit.ui.screens.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trixsearch.hasikit.domain.model.DownloadState
import com.trixsearch.hasikit.domain.model.Video
import com.trixsearch.hasikit.domain.model.WatchProgress
import com.trixsearch.hasikit.domain.repository.VideoRepository
import com.trixsearch.hasikit.download.HasikitDownloadManager
import com.trixsearch.hasikit.util.SampleData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

private const val TAG = "HomeViewModel"

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: VideoRepository,
    private val downloadManager: HasikitDownloadManager
) : ViewModel() {

    val continueWatching: StateFlow<List<Pair<Video, WatchProgress>>> =
        repository.getAllWatchProgress()
            .map { progressList ->
                progressList.take(5).mapNotNull { progress ->
                    val video = repository.getVideoById(progress.videoId)
                        ?: SampleData.videos.find { it.id == progress.videoId }
                    video?.let { it to progress }
                }
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val videos: StateFlow<List<Video>> = combine(
        repository.getAllVideos(),
        downloadManager.downloadTasks
    ) { dbVideos, downloadTasks ->
        // Always start from the full SampleData catalog so no video ever disappears.
        // DB rows only contribute download/offline state — they never replace the catalog.
        val dbById = dbVideos.associateBy { it.id }

        SampleData.videos.map { sample ->
            val db = dbById[sample.id]
            val task = downloadTasks[sample.id]

            val isDownloaded = db?.isDownloaded == true || task?.state == DownloadState.COMPLETED
            val localPath = db?.localPath ?: task?.localPath
            val downloadProgress = when {
                isDownloaded -> 1f
                task != null -> task.progress
                else -> 0f
            }

            Log.d(TAG, "video id=${sample.id} isDownloaded=$isDownloaded localPath=$localPath progress=$downloadProgress")

            sample.copy(
                isDownloaded = isDownloaded,
                localPath = localPath,
                downloadProgress = downloadProgress
            )
        }.also { Log.d(TAG, "videos updated: ${it.size} items") }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SampleData.videos)

    fun startDownload(video: Video) {
        Log.d(TAG, "startDownload videoId=${video.id}")
        downloadManager.startDownload(video)
    }

    fun deleteDownload(videoId: String) {
        Log.d(TAG, "deleteDownload videoId=$videoId")
        downloadManager.deleteDownload(videoId)
    }
}
