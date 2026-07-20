package com.trixsearch.hasikit.domain.repository

import com.trixsearch.hasikit.data.local.entities.FavoriteEntity
import com.trixsearch.hasikit.data.local.entities.WatchHistoryEntity
import com.trixsearch.hasikit.data.local.entities.WatchLaterEntity
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

    // ── Watch Progress ────────────────────────────────────────────────────────
    suspend fun getWatchProgress(videoId: String): WatchProgress?
    suspend fun saveWatchProgress(progress: WatchProgress)
    fun getAllWatchProgress(): Flow<List<WatchProgress>>
    // Delete watch progress for a specific video (used when Continue Watching entry is invalid)
    suspend fun deleteWatchProgress(videoId: String)

    // ── Downloads ─────────────────────────────────────────────────────────────
    fun getAllDownloads(): Flow<List<DownloadTask>>
    suspend fun getDownload(videoId: String): DownloadTask?
    suspend fun saveDownload(download: DownloadTask)
    suspend fun deleteDownload(videoId: String)
    // Clear all downloaded files and DB entries — does NOT remove login session
    suspend fun clearAllStorage()

    // ── Favorites ─────────────────────────────────────────────────────────────
    fun getAllFavorites(): Flow<List<FavoriteEntity>>
    suspend fun isFavorite(videoId: String): Boolean
    suspend fun addFavorite(favorite: FavoriteEntity)
    suspend fun removeFavorite(videoId: String)

    // ── Watch Later ───────────────────────────────────────────────────────────
    fun getAllWatchLater(): Flow<List<WatchLaterEntity>>
    suspend fun isInWatchLater(videoId: String): Boolean
    suspend fun addToWatchLater(item: WatchLaterEntity)
    suspend fun removeFromWatchLater(videoId: String)

    // ── Watch History ─────────────────────────────────────────────────────────
    fun getAllWatchHistory(): Flow<List<WatchHistoryEntity>>
    suspend fun addToWatchHistory(item: WatchHistoryEntity)
    suspend fun removeFromWatchHistory(videoId: String)
}
