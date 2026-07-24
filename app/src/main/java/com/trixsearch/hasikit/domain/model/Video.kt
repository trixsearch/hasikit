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
    val sourceLabel: String = "",
    // true if Telegram file can be streamed directly (MessageVideo); false = download required (MessageDocument)
    val isStreamable: Boolean = true,
    // Telegram message link for sharing — format: https://t.me/c/{channelId}/{messageId}
    val telegramLink: String? = null
)
