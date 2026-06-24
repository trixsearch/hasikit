package com.trixsearch.hasikit.domain.repository

import com.trixsearch.hasikit.domain.model.Video
import kotlinx.coroutines.flow.Flow

interface TelegramRepository {
    fun getVideosFromChannel(channelId: Long): Flow<List<Video>>
    suspend fun downloadVideo(video: Video): Flow<Float>
    suspend fun login(phoneNumber: String): Boolean
    suspend fun verifyOtp(code: String): Boolean
    fun isUserAuthenticated(): Flow<Boolean>
}
