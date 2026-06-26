package com.trixsearch.hasikit.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trixsearch.hasikit.domain.model.DownloadState
import com.trixsearch.hasikit.domain.model.Video
import com.trixsearch.hasikit.domain.repository.VideoRepository
import com.trixsearch.hasikit.download.HasikitDownloadManager
import com.trixsearch.hasikit.util.SampleData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: VideoRepository,
    private val downloadManager: HasikitDownloadManager
) : ViewModel() {

    // For MVP, we combine local DB videos with download status
    val videos: StateFlow<List<Video>> = combine(
        repository.getAllVideos(),
        downloadManager.downloadTasks
    ) { dbVideos, downloadTasks ->
        // If DB is empty (first run), use SampleData
        val baseVideos = dbVideos.ifEmpty { SampleData.videos }
        
        baseVideos.map { video ->
            val task = downloadTasks[video.id]
            video.copy(
                isDownloaded = task?.state == DownloadState.COMPLETED,
                downloadProgress = task?.progress ?: 0f
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SampleData.videos)

    fun startDownload(video: Video) {
        downloadManager.startDownload(video)
    }

    fun deleteDownload(videoId: String) {
        downloadManager.deleteDownload(videoId)
    }
}
