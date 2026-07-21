package com.trixsearch.hasikit.domain.repository

import com.trixsearch.hasikit.data.local.entities.FavoriteEntity
import com.trixsearch.hasikit.data.local.entities.VideoEntity
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

    // Stage 1 local search — instant results from Room before Telegram search completes
    fun searchVideos(query: String): Flow<List<Video>>
    // Search within downloaded videos only (Library Downloads tab)
    fun searchDownloadedVideos(query: String): Flow<List<Video>>

    // SQL-level sort variants for Library Downloads tab (Metrolist pattern)
    fun downloadedByNameAsc(): Flow<List<Video>>
    fun downloadedByDateDesc(): Flow<List<Video>>
    fun downloadedBySizeDesc(): Flow<List<Video>>
    fun downloadedByDurationDesc(): Flow<List<Video>>
    fun downloadedByChannel(): Flow<List<Video>>

    // Continue Watching — SQL JOIN with watch_progress, ordered by most recently watched
    fun getVideosWithProgress(): Flow<List<Video>>

    // Storage stats — SQL aggregates, no full table scan in Kotlin
    suspend fun countDownloadedVideos(): Int
    suspend fun totalDownloadedSize(): Long

    // ── Watch Progress ────────────────────────────────────────────────────────
    suspend fun getWatchProgress(videoId: String): WatchProgress?
    suspend fun saveWatchProgress(progress: WatchProgress)
    fun getAllWatchProgress(): Flow<List<WatchProgress>>
    suspend fun deleteWatchProgress(videoId: String)

    // ── Downloads ─────────────────────────────────────────────────────────────
    fun getAllDownloads(): Flow<List<DownloadTask>>
    fun getActiveDownloads(): Flow<List<DownloadTask>>
    suspend fun getDownload(videoId: String): DownloadTask?
    suspend fun saveDownload(download: DownloadTask)
    suspend fun deleteDownload(videoId: String)
    // Clear all downloaded files and DB entries — does NOT remove login session
    suspend fun clearAllStorage()

    // ── Favorites ─────────────────────────────────────────────────────────────
    // Returns full Video objects joined from videos table — no separate lookup needed
    fun getFavoriteVideos(): Flow<List<Video>>
    fun getAllFavorites(): Flow<List<FavoriteEntity>>
    suspend fun isFavorite(videoId: String): Boolean
    suspend fun addFavorite(favorite: FavoriteEntity)
    suspend fun removeFavorite(videoId: String)

    // ── Watch Later ───────────────────────────────────────────────────────────
    // Returns full Video objects joined from videos table
    fun getWatchLaterVideos(): Flow<List<Video>>
    fun getAllWatchLater(): Flow<List<WatchLaterEntity>>
    suspend fun isInWatchLater(videoId: String): Boolean
    suspend fun addToWatchLater(item: WatchLaterEntity)
    suspend fun removeFromWatchLater(videoId: String)

    // ── Watch History ─────────────────────────────────────────────────────────
    // Returns full Video objects joined from videos table — latest watch at top
    fun getHistoryVideos(): Flow<List<Video>>
    fun getAllWatchHistory(): Flow<List<WatchHistoryEntity>>
    suspend fun addToWatchHistory(item: WatchHistoryEntity)
    suspend fun removeFromWatchHistory(videoId: String)
}
