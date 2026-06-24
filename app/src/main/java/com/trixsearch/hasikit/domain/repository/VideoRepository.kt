package com.trixsearch.hasikit.domain.repository

import com.trixsearch.hasikit.domain.model.Video
import kotlinx.coroutines.flow.Flow

interface VideoRepository {
    fun getAllVideos(): Flow<List<Video>>
    suspend fun getVideoById(id: String): Video?
    suspend fun insertVideo(video: Video)
    suspend fun deleteVideo(video: Video)
    fun searchVideos(query: String): Flow<List<Video>>
}
