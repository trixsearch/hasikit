package com.trixsearch.hasikit.ui.screens.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trixsearch.hasikit.domain.model.DownloadState
import com.trixsearch.hasikit.domain.model.Video
import com.trixsearch.hasikit.domain.model.WatchProgress
import com.trixsearch.hasikit.domain.repository.VideoRepository
import com.trixsearch.hasikit.download.HasikitDownloadManager
import com.trixsearch.hasikit.telegram.domain.model.TelegramMedia
import com.trixsearch.hasikit.telegram.domain.repository.TelegramAuthRepository
import com.trixsearch.hasikit.telegram.domain.repository.TelegramChannelRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "HomeViewModel"
private const val SOURCE_CHANNEL = "testhasikit"
private const val PAGE_SIZE = 50

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: VideoRepository,
    private val downloadManager: HasikitDownloadManager,
    private val channelRepository: TelegramChannelRepository,
    private val authRepository: TelegramAuthRepository
) : ViewModel() {

    // Resolved chat ID for @testhasikit — null until resolved
    private val _chatId = MutableStateFlow<Long?>(null)

    // Raw TelegramMedia pages loaded so far
    private val _telegramMedia = MutableStateFlow<List<TelegramMedia>>(emptyList())

    // Pagination state
    private val _isLoadingMore = MutableStateFlow(false)
    val isLoadingMore: StateFlow<Boolean> = _isLoadingMore

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private var lastMessageId = 0L
    private var hasMore = true

    // Continue Watching — from Room watch progress
    val continueWatching: StateFlow<List<Pair<Video, WatchProgress>>> =
        repository.getAllWatchProgress()
            .map { progressList ->
                progressList.take(5).mapNotNull { progress ->
                    val video = repository.getVideoById(progress.videoId)
                    video?.let { it to progress }
                }
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Merge Telegram catalog with Room download state
    val videos: StateFlow<List<Video>> = combine(
        _telegramMedia,
        repository.getAllVideos(),
        downloadManager.downloadTasks
    ) { telegramItems, dbVideos, downloadTasks ->
        val dbById = dbVideos.associateBy { it.id }
        telegramItems.map { media ->
            val id = media.messageId.toString()
            val db = dbById[id]
            val task = downloadTasks[id]
            val isDownloaded = db?.isDownloaded == true || task?.state == DownloadState.COMPLETED
            val localPath = db?.localPath ?: task?.localPath
            val downloadProgress = when {
                isDownloaded -> 1f
                task != null -> task.progress
                else -> 0f
            }
            Video(
                id               = id,
                title            = media.title.ifBlank { media.fileName },
                thumbnail        = null, // resolved lazily via TDLib thumbnail download
                videoUrl         = "",   // resolved on play via resolveFileUrl
                telegramFileId   = media.fileId.toString(),
                duration         = media.duration.toLong() * 1000L,
                size             = media.size,
                localPath        = localPath,
                isDownloaded     = isDownloaded,
                downloadProgress = downloadProgress
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        // Start loading when authenticated
        viewModelScope.launch {
            authRepository.authState.collect { state ->
                if (state is com.trixsearch.hasikit.telegram.domain.model.AuthState.Authenticated
                    && _chatId.value == null) {
                    resolveAndLoad()
                }
            }
        }
    }

    private fun resolveAndLoad() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            channelRepository.resolveChannel(SOURCE_CHANNEL)
                .onSuccess { chatId ->
                    _chatId.value = chatId
                    Log.i(TAG, "Channel resolved chatId=$chatId")
                    loadNextPage(chatId, reset = true)
                }
                .onFailure { e ->
                    Log.e(TAG, "resolveChannel failed: ${e.message}")
                    _error.value = "Could not access @$SOURCE_CHANNEL: ${e.message}"
                }
            _isLoading.value = false
        }
    }

    private suspend fun loadNextPage(chatId: Long, reset: Boolean = false) {
        if (reset) {
            lastMessageId = 0L
            hasMore = true
            _telegramMedia.value = emptyList()
        }
        if (!hasMore) return

        Log.d(TAG, "loadNextPage chatId=$chatId offset=$lastMessageId")
        channelRepository.getChannelMedia(chatId, lastMessageId, PAGE_SIZE)
            .onSuccess { page ->
                if (page.isEmpty()) {
                    hasMore = false
                    Log.d(TAG, "loadNextPage — no more items")
                } else {
                    lastMessageId = page.last().messageId
                    _telegramMedia.value = _telegramMedia.value + page
                    Log.d(TAG, "loadNextPage — loaded ${page.size}, total=${_telegramMedia.value.size}")
                }
            }
            .onFailure { e ->
                Log.e(TAG, "loadNextPage failed: ${e.message}")
                _error.value = e.message
            }
    }

    /** Called by HomeScreen when the user scrolls near the bottom. */
    fun loadMore() {
        val chatId = _chatId.value ?: return
        if (_isLoadingMore.value || !hasMore) return
        viewModelScope.launch {
            _isLoadingMore.value = true
            loadNextPage(chatId)
            _isLoadingMore.value = false
        }
    }

    fun refresh() {
        val chatId = _chatId.value
        if (chatId != null) {
            viewModelScope.launch {
                _isLoading.value = true
                loadNextPage(chatId, reset = true)
                _isLoading.value = false
            }
        } else {
            resolveAndLoad()
        }
    }

    fun startDownload(video: Video) {
        Log.d(TAG, "startDownload videoId=${video.id}")
        downloadManager.startDownload(video)
    }

    fun deleteDownload(videoId: String) {
        Log.d(TAG, "deleteDownload videoId=$videoId")
        downloadManager.deleteDownload(videoId)
    }
}
