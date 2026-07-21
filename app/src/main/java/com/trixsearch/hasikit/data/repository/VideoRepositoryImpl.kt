package com.trixsearch.hasikit.data.repository

import com.trixsearch.hasikit.data.local.dao.VideoDao
import com.trixsearch.hasikit.data.local.entities.FavoriteEntity
import com.trixsearch.hasikit.data.local.entities.VideoEntity
import com.trixsearch.hasikit.data.local.entities.WatchHistoryEntity
import com.trixsearch.hasikit.data.local.entities.WatchLaterEntity
import com.trixsearch.hasikit.data.local.entities.toDomain
import com.trixsearch.hasikit.data.local.entities.toEntity
import com.trixsearch.hasikit.domain.model.DownloadTask
import com.trixsearch.hasikit.domain.model.Video
import com.trixsearch.hasikit.domain.model.WatchProgress
import com.trixsearch.hasikit.domain.repository.VideoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class VideoRepositoryImpl @Inject constructor(
    private val videoDao: VideoDao
) : VideoRepository {

    override fun getAllVideos(): Flow<List<Video>> =
        videoDao.getAllVideos().map { it.map { e -> e.toDomain() } }

    override fun getDownloadedVideos(): Flow<List<Video>> =
        videoDao.getDownloadedVideos().map { it.map { e -> e.toDomain() } }

    override suspend fun getVideoById(id: String): Video? =
        videoDao.getVideoById(id)?.toDomain()

    override suspend fun insertVideo(video: Video) =
        videoDao.insertVideo(video.toEntity())

    override suspend fun updateVideo(video: Video) =
        videoDao.updateVideo(video.toEntity())

    override suspend fun deleteVideo(video: Video) =
        videoDao.deleteVideo(video.toEntity())

    // Stage 1 local search — SQL LIKE on title and sourceLabel
    override fun searchVideos(query: String): Flow<List<Video>> =
        videoDao.searchVideos(query).map { it.map { e -> e.toDomain() } }

    // Search within downloaded videos only (Library Downloads tab)
    override fun searchDownloadedVideos(query: String): Flow<List<Video>> =
        videoDao.searchDownloadedVideos(query).map { it.map { e -> e.toDomain() } }

    // SQL-level sort variants for Library Downloads tab (Metrolist pattern)
    override fun downloadedByNameAsc(): Flow<List<Video>> =
        videoDao.downloadedByNameAsc().map { it.map { e -> e.toDomain() } }

    override fun downloadedByDateDesc(): Flow<List<Video>> =
        videoDao.downloadedByDateDesc().map { it.map { e -> e.toDomain() } }

    override fun downloadedBySizeDesc(): Flow<List<Video>> =
        videoDao.downloadedBySizeDesc().map { it.map { e -> e.toDomain() } }

    override fun downloadedByDurationDesc(): Flow<List<Video>> =
        videoDao.downloadedByDurationDesc().map { it.map { e -> e.toDomain() } }

    override fun downloadedByChannel(): Flow<List<Video>> =
        videoDao.downloadedByChannel().map { it.map { e -> e.toDomain() } }

    // Continue Watching — SQL JOIN with watch_progress, ordered by most recently watched
    override fun getVideosWithProgress(): Flow<List<Video>> =
        videoDao.getVideosWithProgress().map { it.map { e -> e.toDomain() } }

    // Storage stats — SQL aggregates
    override suspend fun countDownloadedVideos(): Int =
        videoDao.countDownloadedVideos()

    override suspend fun totalDownloadedSize(): Long =
        videoDao.totalDownloadedSize()

    // ── Watch Progress ────────────────────────────────────────────────────────

    override suspend fun getWatchProgress(videoId: String): WatchProgress? =
        videoDao.getWatchProgress(videoId)?.toDomain()

    override suspend fun saveWatchProgress(progress: WatchProgress) =
        videoDao.saveWatchProgress(progress.toEntity())

    override fun getAllWatchProgress(): Flow<List<WatchProgress>> =
        videoDao.getAllWatchProgress().map { it.map { e -> e.toDomain() } }

    // Delete watch progress for a specific video — used to clean up invalid Continue Watching entries
    override suspend fun deleteWatchProgress(videoId: String) =
        videoDao.deleteWatchProgress(videoId)

    // ── Downloads ─────────────────────────────────────────────────────────────

    override fun getAllDownloads(): Flow<List<DownloadTask>> =
        videoDao.getAllDownloads().map { it.map { e -> e.toDomain() } }

    // Active downloads only (DOWNLOADING, QUEUED, PAUSED) — SQL filter, not Kotlin filter
    override fun getActiveDownloads(): Flow<List<DownloadTask>> =
        videoDao.getActiveDownloads().map { it.map { e -> e.toDomain() } }

    override suspend fun getDownload(videoId: String): DownloadTask? =
        videoDao.getDownload(videoId)?.toDomain()

    override suspend fun saveDownload(download: DownloadTask) =
        videoDao.saveDownload(download.toEntity())

    override suspend fun deleteDownload(videoId: String) =
        videoDao.deleteDownload(videoId)

    override suspend fun clearAllStorage() {
        videoDao.deleteAllDownloads()
        videoDao.deleteAllVideos()
        videoDao.deleteAllWatchProgress()
    }

    // ── Favorites ─────────────────────────────────────────────────────────────

    // SQL JOIN returns full Video objects — no separate getVideoById lookup per item
    override fun getFavoriteVideos(): Flow<List<Video>> =
        videoDao.getFavoriteVideos().map { it.map { e -> e.toDomain() } }

    override fun getAllFavorites(): Flow<List<FavoriteEntity>> =
        videoDao.getAllFavorites()

    override suspend fun isFavorite(videoId: String): Boolean =
        videoDao.isFavorite(videoId) > 0

    override suspend fun addFavorite(favorite: FavoriteEntity) =
        videoDao.addFavorite(favorite)

    override suspend fun removeFavorite(videoId: String) =
        videoDao.removeFavorite(videoId)

    // ── Watch Later ───────────────────────────────────────────────────────────

    // SQL JOIN returns full Video objects
    override fun getWatchLaterVideos(): Flow<List<Video>> =
        videoDao.getWatchLaterVideos().map { it.map { e -> e.toDomain() } }

    override fun getAllWatchLater(): Flow<List<WatchLaterEntity>> =
        videoDao.getAllWatchLater()

    override suspend fun isInWatchLater(videoId: String): Boolean =
        videoDao.isInWatchLater(videoId) > 0

    override suspend fun addToWatchLater(item: WatchLaterEntity) =
        videoDao.addToWatchLater(item)

    override suspend fun removeFromWatchLater(videoId: String) =
        videoDao.removeFromWatchLater(videoId)

    // ── Watch History ─────────────────────────────────────────────────────────

    // SQL JOIN returns full Video objects — latest watch at top
    override fun getHistoryVideos(): Flow<List<Video>> =
        videoDao.getHistoryVideos().map { it.map { e -> e.toDomain() } }

    override fun getAllWatchHistory(): Flow<List<WatchHistoryEntity>> =
        videoDao.getAllWatchHistory()

    override suspend fun addToWatchHistory(item: WatchHistoryEntity) =
        videoDao.addToWatchHistory(item)

    override suspend fun removeFromWatchHistory(videoId: String) =
        videoDao.removeFromWatchHistory(videoId)
}
