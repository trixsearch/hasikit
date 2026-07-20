package com.trixsearch.hasikit.data.local.dao

import androidx.room.*
import com.trixsearch.hasikit.data.local.entities.DownloadEntity
import com.trixsearch.hasikit.data.local.entities.FavoriteEntity
import com.trixsearch.hasikit.data.local.entities.VideoEntity
import com.trixsearch.hasikit.data.local.entities.WatchHistoryEntity
import com.trixsearch.hasikit.data.local.entities.WatchLaterEntity
import com.trixsearch.hasikit.data.local.entities.WatchProgressEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VideoDao {
    @Query("SELECT * FROM videos ORDER BY id DESC")
    fun getAllVideos(): Flow<List<VideoEntity>>

    @Query("SELECT * FROM videos WHERE id = :id")
    suspend fun getVideoById(id: String): VideoEntity?

    @Query("SELECT * FROM videos WHERE isDownloaded = 1")
    fun getDownloadedVideos(): Flow<List<VideoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVideo(video: VideoEntity)

    @Update
    suspend fun updateVideo(video: VideoEntity)

    @Delete
    suspend fun deleteVideo(video: VideoEntity)

    @Query("SELECT * FROM videos WHERE title LIKE '%' || :query || '%'")
    fun searchVideos(query: String): Flow<List<VideoEntity>>

    // ── Watch Progress ────────────────────────────────────────────────────────

    @Query("SELECT * FROM watch_progress WHERE videoId = :videoId")
    suspend fun getWatchProgress(videoId: String): WatchProgressEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveWatchProgress(progress: WatchProgressEntity)

    @Query("SELECT * FROM watch_progress ORDER BY lastWatchedAt DESC")
    fun getAllWatchProgress(): Flow<List<WatchProgressEntity>>

    @Query("DELETE FROM watch_progress WHERE videoId = :videoId")
    suspend fun deleteWatchProgress(videoId: String)

    // ── Downloads ─────────────────────────────────────────────────────────────

    @Query("SELECT * FROM downloads")
    fun getAllDownloads(): Flow<List<DownloadEntity>>

    @Query("SELECT * FROM downloads WHERE videoId = :videoId")
    suspend fun getDownload(videoId: String): DownloadEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveDownload(download: DownloadEntity)

    @Query("DELETE FROM downloads WHERE videoId = :videoId")
    suspend fun deleteDownload(videoId: String)

    @Query("DELETE FROM downloads")
    suspend fun deleteAllDownloads()

    @Query("DELETE FROM videos")
    suspend fun deleteAllVideos()

    @Query("DELETE FROM watch_progress")
    suspend fun deleteAllWatchProgress()

    // ── Favorites ─────────────────────────────────────────────────────────────

    @Query("SELECT * FROM favorites ORDER BY addedAt DESC")
    fun getAllFavorites(): Flow<List<FavoriteEntity>>

    @Query("SELECT COUNT(*) FROM favorites WHERE videoId = :videoId")
    suspend fun isFavorite(videoId: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorite(favorite: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE videoId = :videoId")
    suspend fun removeFavorite(videoId: String)

    @Query("DELETE FROM favorites")
    suspend fun deleteAllFavorites()

    // ── Watch Later ───────────────────────────────────────────────────────────

    @Query("SELECT * FROM watch_later ORDER BY addedAt DESC")
    fun getAllWatchLater(): Flow<List<WatchLaterEntity>>

    @Query("SELECT COUNT(*) FROM watch_later WHERE videoId = :videoId")
    suspend fun isInWatchLater(videoId: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addToWatchLater(item: WatchLaterEntity)

    @Query("DELETE FROM watch_later WHERE videoId = :videoId")
    suspend fun removeFromWatchLater(videoId: String)

    @Query("DELETE FROM watch_later")
    suspend fun deleteAllWatchLater()

    // ── Watch History ─────────────────────────────────────────────────────────

    @Query("SELECT * FROM watch_history ORDER BY watchedAt DESC")
    fun getAllWatchHistory(): Flow<List<WatchHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addToWatchHistory(item: WatchHistoryEntity)

    @Query("DELETE FROM watch_history WHERE videoId = :videoId")
    suspend fun removeFromWatchHistory(videoId: String)

    @Query("DELETE FROM watch_history")
    suspend fun deleteAllWatchHistory()
}
