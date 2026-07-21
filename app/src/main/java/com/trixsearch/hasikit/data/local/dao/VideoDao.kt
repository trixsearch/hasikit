package com.trixsearch.hasikit.data.local.dao

import androidx.room.*
import com.trixsearch.hasikit.data.local.entities.DownloadEntity
import com.trixsearch.hasikit.data.local.entities.FavoriteEntity
import com.trixsearch.hasikit.data.local.entities.VideoEntity
import com.trixsearch.hasikit.data.local.entities.WatchHistoryEntity
import com.trixsearch.hasikit.data.local.entities.WatchLaterEntity
import com.trixsearch.hasikit.data.local.entities.WatchProgressEntity
import kotlinx.coroutines.flow.Flow

// DAO architecture inspired by Metrolist: SQL-level sorting/filtering instead of in-memory
// operations. Each sort variant is a separate @Query so SQLite does the work, not Kotlin.
@Dao
interface VideoDao {

    // ── Videos ────────────────────────────────────────────────────────────────

    // Default feed order: newest upload first (uploadDate DESC, fallback to id DESC)
    @Query("SELECT * FROM videos ORDER BY uploadDate DESC, id DESC")
    fun getAllVideos(): Flow<List<VideoEntity>>

    @Query("SELECT * FROM videos WHERE id = :id LIMIT 1")
    suspend fun getVideoById(id: String): VideoEntity?

    // Downloaded videos sorted by title for Library Downloads tab
    @Query("SELECT * FROM videos WHERE isDownloaded = 1 ORDER BY title ASC")
    fun getDownloadedVideos(): Flow<List<VideoEntity>>

    // Downloaded videos — SQL sort variants (Metrolist pattern: one query per sort)
    @Query("SELECT * FROM videos WHERE isDownloaded = 1 ORDER BY title ASC")
    fun downloadedByNameAsc(): Flow<List<VideoEntity>>

    @Query("SELECT * FROM videos WHERE isDownloaded = 1 ORDER BY uploadDate DESC")
    fun downloadedByDateDesc(): Flow<List<VideoEntity>>

    @Query("SELECT * FROM videos WHERE isDownloaded = 1 ORDER BY size DESC")
    fun downloadedBySizeDesc(): Flow<List<VideoEntity>>

    @Query("SELECT * FROM videos WHERE isDownloaded = 1 ORDER BY duration DESC")
    fun downloadedByDurationDesc(): Flow<List<VideoEntity>>

    @Query("SELECT * FROM videos WHERE isDownloaded = 1 ORDER BY sourceLabel ASC, title ASC")
    fun downloadedByChannel(): Flow<List<VideoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVideo(video: VideoEntity)

    @Update
    suspend fun updateVideo(video: VideoEntity)

    @Delete
    suspend fun deleteVideo(video: VideoEntity)

    // Local Room search — title, sourceLabel. Used as Stage 1 instant results.
    // Parameterized query prevents SQL injection (Room handles escaping automatically).
    @Query("""
        SELECT * FROM videos
        WHERE title LIKE '%' || :query || '%'
           OR sourceLabel LIKE '%' || :query || '%'
        ORDER BY uploadDate DESC
    """)
    fun searchVideos(query: String): Flow<List<VideoEntity>>

    // Search within downloaded videos only (Library Downloads tab search bar)
    @Query("""
        SELECT * FROM videos
        WHERE isDownloaded = 1
          AND (title LIKE '%' || :query || '%' OR sourceLabel LIKE '%' || :query || '%')
        ORDER BY title ASC
    """)
    fun searchDownloadedVideos(query: String): Flow<List<VideoEntity>>

    // Continue Watching: join watch_progress to get only videos with saved progress,
    // ordered by most recently watched. Avoids loading all videos into memory.
    @Query("""
        SELECT v.* FROM videos v
        INNER JOIN watch_progress wp ON v.id = wp.videoId
        WHERE wp.lastPosition > 0
        ORDER BY wp.lastWatchedAt DESC
        LIMIT 10
    """)
    fun getVideosWithProgress(): Flow<List<VideoEntity>>

    // Count downloaded videos — used by storage stats without loading full list
    @Query("SELECT COUNT(*) FROM videos WHERE isDownloaded = 1")
    suspend fun countDownloadedVideos(): Int

    // Total size of downloaded videos — SQL SUM avoids loading all rows
    @Query("SELECT COALESCE(SUM(size), 0) FROM videos WHERE isDownloaded = 1")
    suspend fun totalDownloadedSize(): Long

    @Query("DELETE FROM downloads")
    suspend fun deleteAllDownloads()

    @Query("DELETE FROM videos")
    suspend fun deleteAllVideos()

    @Query("DELETE FROM watch_progress")
    suspend fun deleteAllWatchProgress()

    // ── Watch Progress ────────────────────────────────────────────────────────

    @Query("SELECT * FROM watch_progress WHERE videoId = :videoId")
    suspend fun getWatchProgress(videoId: String): WatchProgressEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveWatchProgress(progress: WatchProgressEntity)

    // All watch progress ordered by most recently watched — used for Continue Watching
    @Query("SELECT * FROM watch_progress ORDER BY lastWatchedAt DESC")
    fun getAllWatchProgress(): Flow<List<WatchProgressEntity>>

    @Query("DELETE FROM watch_progress WHERE videoId = :videoId")
    suspend fun deleteWatchProgress(videoId: String)

    // ── Downloads ─────────────────────────────────────────────────────────────

    @Query("SELECT * FROM downloads")
    fun getAllDownloads(): Flow<List<DownloadEntity>>

    // Active downloads only (DOWNLOADING or QUEUED) — used for active section in Library
    @Query("SELECT * FROM downloads WHERE state IN ('DOWNLOADING', 'QUEUED', 'PAUSED')")
    fun getActiveDownloads(): Flow<List<DownloadEntity>>

    @Query("SELECT * FROM downloads WHERE videoId = :videoId")
    suspend fun getDownload(videoId: String): DownloadEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveDownload(download: DownloadEntity)

    @Query("DELETE FROM downloads WHERE videoId = :videoId")
    suspend fun deleteDownload(videoId: String)

    // ── Favorites ─────────────────────────────────────────────────────────────

    // Favorites joined with video metadata — avoids separate lookup per item
    @Query("""
        SELECT v.* FROM videos v
        INNER JOIN favorites f ON v.id = f.videoId
        ORDER BY f.addedAt DESC
    """)
    fun getFavoriteVideos(): Flow<List<VideoEntity>>

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

    // Watch Later joined with video metadata
    @Query("""
        SELECT v.* FROM videos v
        INNER JOIN watch_later wl ON v.id = wl.videoId
        ORDER BY wl.addedAt DESC
    """)
    fun getWatchLaterVideos(): Flow<List<VideoEntity>>

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

    // History joined with video metadata — latest watch always at top
    @Query("""
        SELECT v.* FROM videos v
        INNER JOIN watch_history wh ON v.id = wh.videoId
        ORDER BY wh.watchedAt DESC
    """)
    fun getHistoryVideos(): Flow<List<VideoEntity>>

    @Query("SELECT * FROM watch_history ORDER BY watchedAt DESC")
    fun getAllWatchHistory(): Flow<List<WatchHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addToWatchHistory(item: WatchHistoryEntity)

    @Query("DELETE FROM watch_history WHERE videoId = :videoId")
    suspend fun removeFromWatchHistory(videoId: String)

    @Query("DELETE FROM watch_history")
    suspend fun deleteAllWatchHistory()
}
