package com.trixsearch.hasikit.domain.model

enum class DownloadState {
    QUEUED, DOWNLOADING, PAUSED, COMPLETED, FAILED
}

data class DownloadTask(
    val videoId: String,
    val state: DownloadState,
    val progress: Float,
    val localPath: String? = null,
    val errorCode: Int? = null
)
