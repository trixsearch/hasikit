package com.trixsearch.hasikit.telegram.domain.model

data class TelegramMediaItem(
    val messageId: Long,
    val fileId: Long,
    val fileName: String,
    val title: String,
    val caption: String,
    val mimeType: String,
    val duration: Int,       // seconds
    val size: Long,          // bytes
    val thumbnailFileId: Long?,
    val date: Int            // unix timestamp
)
