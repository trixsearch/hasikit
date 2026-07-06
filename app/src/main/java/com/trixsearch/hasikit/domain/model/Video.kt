package com.trixsearch.hasikit.domain.model

data class Video(
    val id: String,
    val title: String,
    val thumbnail: String?,
    val videoUrl: String,
    val telegramFileId: String = "",
    val duration: Long,
    val size: Long,
    val localPath: String? = null,
    val isDownloaded: Boolean = false,
    val downloadProgress: Float = 0f,
    val sourceLabel: String = ""
)
