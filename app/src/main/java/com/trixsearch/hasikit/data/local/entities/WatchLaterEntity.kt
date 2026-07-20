package com.trixsearch.hasikit.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

// WatchLater table — persists user's watch-later list locally, independent of download state
@Entity(tableName = "watch_later")
data class WatchLaterEntity(
    @PrimaryKey val videoId: String,
    val title: String,
    val thumbnail: String?,
    val source: String,
    val addedAt: Long = System.currentTimeMillis()
)
