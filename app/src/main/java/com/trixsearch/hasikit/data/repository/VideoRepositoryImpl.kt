package com.trixsearch.hasikit.data.repository

import com.trixsearch.hasikit.data.local.dao.VideoDao
import com.trixsearch.hasikit.data.local.entities.FavoriteEntity
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

    override fun searchVideos(query: String): Flow<List<Video>> =
        videoDao.searchVideos(query).map { it.map { e -> e.toDomain() } }

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

    override fun getAllFavorites(): Flow<List<FavoriteEntity>> =
        videoDao.getAllFavorites()

    override suspend fun isFavorite(videoId: String): Boolean =
        videoDao.isFavorite(videoId) > 0

    override suspend fun addFavorite(favorite: FavoriteEntity) =
        videoDao.addFavorite(favorite)

    override suspend fun removeFavorite(videoId: String) =
        videoDao.removeFavorite(videoId)

    // ── Watch Later ───────────────────────────────────────────────────────────

    override fun getAllWatchLater(): Flow<List<WatchLaterEntity>> =
        videoDao.getAllWatchLater()

    override suspend fun isInWatchLater(videoId: String): Boolean =
        videoDao.isInWatchLater(videoId) > 0

    override suspend fun addToWatchLater(item: WatchLaterEntity) =
        videoDao.addToWatchLater(item)

    override suspend fun removeFromWatchLater(videoId: String) =
        videoDao.removeFromWatchLater(videoId)

    // ── Watch History ─────────────────────────────────────────────────────────

    override fun getAllWatchHistory(): Flow<List<WatchHistoryEntity>> =
        videoDao.getAllWatchHistory()

    override suspend fun addToWatchHistory(item: WatchHistoryEntity) =
        videoDao.addToWatchHistory(item)

    override suspend fun removeFromWatchHistory(videoId: String) =
        videoDao.removeFromWatchHistory(videoId)
}
