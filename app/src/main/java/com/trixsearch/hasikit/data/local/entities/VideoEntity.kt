package com.trixsearch.hasikit.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.trixsearch.hasikit.domain.model.Video

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
    val downloadProgress: Float
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
    downloadProgress = downloadProgress
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
    downloadProgress = downloadProgress
)
