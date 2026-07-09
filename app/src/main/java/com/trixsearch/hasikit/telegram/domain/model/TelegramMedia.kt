package com.trixsearch.hasikit.telegram.domain.model

data class TelegramMedia(
    val messageId: Long,
    val fileId: Long,
    val channelId: Long,
    val fileName: String,
    val title: String,
    val caption: String,
    val duration: Int,       // seconds
    val size: Long,          // bytes
    val thumbnailFileId: Long?,
    val mimeType: String,
    val uploadDate: Int,     // unix timestamp
    // Streamability logic — MessageVideo = true (can stream), MessageDocument = false (download required)
    val isStreamable: Boolean = true
)
