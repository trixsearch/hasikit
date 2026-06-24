package com.trixsearch.hasikit.ui.screens.home

import androidx.lifecycle.ViewModel
import com.trixsearch.hasikit.domain.model.Video
import com.trixsearch.hasikit.util.SampleData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {

    private val _videos = MutableStateFlow<List<Video>>(SampleData.videos)
    val videos: StateFlow<List<Video>> = _videos

    fun toggleDownload(videoId: String) {
        _videos.value = _videos.value.map {
            if (it.id == videoId) {
                if (it.isDownloaded) it.copy(isDownloaded = false, downloadProgress = 0f)
                else it.copy(isDownloaded = true, downloadProgress = 1f)
            } else it
        }
    }
}
