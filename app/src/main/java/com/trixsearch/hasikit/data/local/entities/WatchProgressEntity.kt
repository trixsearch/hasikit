package com.trixsearch.hasikit.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.trixsearch.hasikit.domain.model.WatchProgress

@Entity(tableName = "watch_progress")
data class WatchProgressEntity(
    @PrimaryKey val videoId: String,
    val lastPosition: Long,
    val duration: Long,
    val lastWatchedAt: Long
)

fun WatchProgressEntity.toDomain() = WatchProgress(
    videoId = videoId,
    lastPosition = lastPosition,
    duration = duration,
    lastWatchedAt = lastWatchedAt
)

fun WatchProgress.toEntity() = WatchProgressEntity(
    videoId = videoId,
    lastPosition = lastPosition,
    duration = duration,
    lastWatchedAt = lastWatchedAt
)
