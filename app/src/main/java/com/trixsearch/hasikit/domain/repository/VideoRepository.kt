package com.trixsearch.hasikit.domain.repository

import com.trixsearch.hasikit.domain.model.DownloadTask
import com.trixsearch.hasikit.domain.model.Video
import com.trixsearch.hasikit.domain.model.WatchProgress
import kotlinx.coroutines.flow.Flow

interface VideoRepository {
    fun getAllVideos(): Flow<List<Video>>
    fun getDownloadedVideos(): Flow<List<Video>>
    suspend fun getVideoById(id: String): Video?
    suspend fun insertVideo(video: Video)
    suspend fun updateVideo(video: Video)
    suspend fun deleteVideo(video: Video)
    fun searchVideos(query: String): Flow<List<Video>>

    // Watch Progress
    suspend fun getWatchProgress(videoId: String): WatchProgress?
    suspend fun saveWatchProgress(progress: WatchProgress)
    fun getAllWatchProgress(): Flow<List<WatchProgress>>

    // Downloads
    fun getAllDownloads(): Flow<List<DownloadTask>>
    suspend fun getDownload(videoId: String): DownloadTask?
    suspend fun saveDownload(download: DownloadTask)
    suspend fun deleteDownload(videoId: String)
    suspend fun clearAllStorage()
}
