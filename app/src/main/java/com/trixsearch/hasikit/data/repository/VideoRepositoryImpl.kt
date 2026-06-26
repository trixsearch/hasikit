package com.trixsearch.hasikit.data.repository

import com.trixsearch.hasikit.data.local.dao.VideoDao
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

    override fun getAllVideos(): Flow<List<Video>> {
        return videoDao.getAllVideos().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getDownloadedVideos(): Flow<List<Video>> {
        return videoDao.getDownloadedVideos().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getVideoById(id: String): Video? {
        return videoDao.getVideoById(id)?.toDomain()
    }

    override suspend fun insertVideo(video: Video) {
        videoDao.insertVideo(video.toEntity())
    }

    override suspend fun updateVideo(video: Video) {
        videoDao.updateVideo(video.toEntity())
    }

    override suspend fun deleteVideo(video: Video) {
        videoDao.deleteVideo(video.toEntity())
    }

    override fun searchVideos(query: String): Flow<List<Video>> {
        return videoDao.searchVideos(query).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getWatchProgress(videoId: String): WatchProgress? {
        return videoDao.getWatchProgress(videoId)?.toDomain()
    }

    override suspend fun saveWatchProgress(progress: WatchProgress) {
        videoDao.saveWatchProgress(progress.toEntity())
    }

    override fun getAllWatchProgress(): Flow<List<WatchProgress>> {
        return videoDao.getAllWatchProgress().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getAllDownloads(): Flow<List<DownloadTask>> {
        return videoDao.getAllDownloads().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getDownload(videoId: String): DownloadTask? {
        return videoDao.getDownload(videoId)?.toDomain()
    }

    override suspend fun saveDownload(download: DownloadTask) {
        videoDao.saveDownload(download.toEntity())
    }

    override suspend fun deleteDownload(videoId: String) {
        videoDao.deleteDownload(videoId)
    }
}
