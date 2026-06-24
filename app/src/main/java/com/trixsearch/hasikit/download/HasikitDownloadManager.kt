package com.trixsearch.hasikit.download

import android.content.Context
import com.trixsearch.hasikit.domain.model.DownloadTask
import com.trixsearch.hasikit.domain.model.DownloadState
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HasikitDownloadManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val _downloadTasks = MutableStateFlow<Map<String, DownloadTask>>(emptyMap())
    val downloadTasks: StateFlow<Map<String, DownloadTask>> = _downloadTasks

    fun startDownload(videoId: String, telegramFileId: String) {
        // Implement WorkManager or background service for downloading
    }

    fun pauseDownload(videoId: String) {
        // Implementation
    }

    fun resumeDownload(videoId: String) {
        // Implementation
    }
}
