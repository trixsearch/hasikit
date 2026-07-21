package com.trixsearch.hasikit.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.trixsearch.hasikit.domain.model.Video

// Version 3 migration: added sourceLabel, isStreamable, uploadDate columns
// These enable SQL-level sorting/filtering in VideoDao without loading all rows into memory
@Entity(tableName = "videos")
data class VideoEntity(
    @PrimaryKey val id: String,
    val title: String,
    val thumbnail: String?,
    val videoUrl: String,
    val telegramFileId: String,
    val duration: Long,
    val size: Long,
    val localPath: String?,
    val isDownloaded: Boolean,
    val downloadProgress: Float,
    // Added v3: source channel display name — enables SQL ORDER BY sourceLabel
    val sourceLabel: String = "",
    // Added v3: streamability flag — MessageVideo=true, MessageDocument=false
    val isStreamable: Boolean = true,
    // Added v3: Telegram message upload date (Unix seconds) — enables SQL ORDER BY uploadDate
    val uploadDate: Int = 0
)

fun VideoEntity.toDomain() = Video(
    id = id,
    title = title,
    thumbnail = thumbnail,
    videoUrl = videoUrl,
    telegramFileId = telegramFileId,
    duration = duration,
    size = size,
    localPath = localPath,
    isDownloaded = isDownloaded,
    downloadProgress = downloadProgress,
    sourceLabel = sourceLabel,
    isStreamable = isStreamable
)

fun Video.toEntity() = VideoEntity(
    id = id,
    title = title,
    thumbnail = thumbnail,
    videoUrl = videoUrl,
    telegramFileId = telegramFileId,
    duration = duration,
    size = size,
    localPath = localPath,
    isDownloaded = isDownloaded,
    downloadProgress = downloadProgress,
    sourceLabel = sourceLabel,
    isStreamable = isStreamable
)
