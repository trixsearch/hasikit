package com.trixsearch.hasikit.domain.model

data class WatchProgress(
    val videoId: String,
    val lastPosition: Long,
    val duration: Long,
    val lastWatchedAt: Long = System.currentTimeMillis()
) {
    val progress: Float get() = if (duration > 0) lastPosition.toFloat() / duration else 0f
}
