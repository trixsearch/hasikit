package com.trixsearch.hasikit.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.trixsearch.hasikit.domain.model.DownloadState
import com.trixsearch.hasikit.domain.model.DownloadTask

@Entity(tableName = "downloads")
data class DownloadEntity(
    @PrimaryKey val videoId: String,
    val state: String,
    val progress: Float,
    val localPath: String?,
    val errorCode: Int?
)

fun DownloadEntity.toDomain() = DownloadTask(
    videoId = videoId,
    state = DownloadState.valueOf(state),
    progress = progress,
    localPath = localPath,
    errorCode = errorCode
)

fun DownloadTask.toEntity() = DownloadEntity(
    videoId = videoId,
    state = state.name,
    progress = progress,
    localPath = localPath,
    errorCode = errorCode
)
