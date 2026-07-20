package com.trixsearch.hasikit.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

// WatchHistory table — persists full viewing history locally, independent of download state
@Entity(tableName = "watch_history")
data class WatchHistoryEntity(
    @PrimaryKey val videoId: String,
    val title: String,
    val thumbnail: String?,
    val source: String,
    // Timestamp of the most recent watch event for this video
    val watchedAt: Long = System.currentTimeMillis()
)
