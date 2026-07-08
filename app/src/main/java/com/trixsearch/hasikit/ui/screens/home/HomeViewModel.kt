package com.trixsearch.hasikit.ui.screens.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trixsearch.hasikit.domain.model.DownloadState
import com.trixsearch.hasikit.domain.model.Video
import com.trixsearch.hasikit.domain.model.WatchProgress
import com.trixsearch.hasikit.domain.repository.VideoRepository
import com.trixsearch.hasikit.download.HasikitDownloadManager
import com.trixsearch.hasikit.telegram.config.TelegramSource
import com.trixsearch.hasikit.telegram.config.TelegramSourceConfig
import com.trixsearch.hasikit.telegram.domain.model.AuthState
import com.trixsearch.hasikit.telegram.domain.model.TelegramMedia
import com.trixsearch.hasikit.telegram.domain.repository.TelegramAuthRepository
import com.trixsearch.hasikit.telegram.domain.repository.TelegramChannelRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "HomeViewModel"
// Initial fetch size — load minimum 25 videos on first open
private const val PAGE_SIZE = 25

data class SourcePage(
    val source: TelegramSource,
    val chatId: Long,
    val media: List<TelegramMedia>,
    val lastMessageId: Long,
    val hasMore: Boolean
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: VideoRepository,
    private val downloadManager: HasikitDownloadManager,
    private val channelRepository: TelegramChannelRepository,
    private val authRepository: TelegramAuthRepository,
    private val sourceConfig: TelegramSourceConfig
) : ViewModel() {

    private val _sourcePages = MutableStateFlow<List<SourcePage>>(emptyList())
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isLoadingMore = MutableStateFlow(false)
    val isLoadingMore: StateFlow<Boolean> = _isLoadingMore

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _noAccessMessage = MutableStateFlow<String?>(null)
    val noAccessMessage: StateFlow<String?> = _noAccessMessage

    // Reactive thumbnail cache: fileId -> local file path
    private val _thumbnailCache = MutableStateFlow<Map<Long, String?>>(emptyMap())

    private val _selectedSourceFilter = MutableStateFlow<String?>(null) // null = All Sources
    val selectedSourceFilter: StateFlow<String?> = _selectedSourceFilter

    val availableSources: StateFlow<List<TelegramSource>> = _sourcePages
        .map { pages -> pages.map { it.source } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setSourceFilter(sourceDisplayName: String?) {
        _selectedSourceFilter.value = sourceDisplayName
    }

    // Expose download tasks so HomeScreen can read per-video download state
    val downloadTasks = downloadManager.downloadTasks

    val continueWatching: StateFlow<List<Pair<Video, WatchProgress>>> =
        repository.getAllWatchProgress()
            .map { progressList ->
                progressList.take(5).mapNotNull { progress ->
                    repository.getVideoById(progress.videoId)?.let { it to progress }
                }
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val videos: StateFlow<List<Video>> = combine(
        combine(_sourcePages, _thumbnailCache, _selectedSourceFilter) { pages, thumbCache, filter ->
            Triple(pages, thumbCache, filter)
        },
        combine(repository.getAllVideos(), downloadManager.downloadTasks) { dbVideos, tasks ->
            Pair(dbVideos, tasks)
        }
    ) { triple, pair ->
        val pages = triple.first
        val thumbCache = triple.second
        val sourceFilter = triple.third
        val dbVideos = pair.first
        val downloadTasks = pair.second
        val dbById = dbVideos.associateBy { it.id }
        val filteredPages = if (sourceFilter == null) pages else pages.filter { it.source.displayName == sourceFilter }
        filteredPages.flatMap { page ->
            page.media.map { media ->
                val id = "${media.channelId}_${media.messageId}"
                val db = dbById[id]
                val task = downloadTasks[id]
                val isDownloaded = db?.isDownloaded == true || task?.state == DownloadState.COMPLETED
                val localPath = db?.localPath ?: task?.localPath
                val downloadProgress = when {
                    isDownloaded -> 1f
                    task != null -> task.progress
                    else -> 0f
                }
                val thumbnail = thumbCache[media.thumbnailFileId] ?: db?.thumbnail
                Video(
                    id = id,
                    title = media.title.ifBlank { media.fileName },
                    thumbnail = thumbnail,
                    videoUrl = "",
                    telegramFileId = media.fileId.toString(),
                    duration = media.duration.toLong() * 1000L,
                    size = media.size,
                    localPath = localPath,
                    isDownloaded = isDownloaded,
                    downloadProgress = downloadProgress,
                    sourceLabel = page.source.displayName
                )
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            authRepository.authState.collect { state ->
                if (state is AuthState.Authenticated && _sourcePages.value.isEmpty()) {
                    loadAllSources()
                }
            }
        }
        // Also observe user-added sources
        viewModelScope.launch {
            sourceConfig.userSourcesFlow.collect {
                if (authRepository.authState.value is AuthState.Authenticated) {
                    loadAllSources()
                }
            }
        }
    }

    private fun loadAllSources() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _noAccessMessage.value = null

            val allSources = buildList {
                addAll(sourceConfig.officialSources)
                addAll(sourceConfig.userSourcesFlow.first())
            }

            val resolvedPages = allSources.map { source ->
                async {
                    channelRepository.resolveSource(source)
                        .onSuccess { chatId ->
                            Log.i(TAG, "Resolved source '${source.displayName}' chatId=$chatId")
                        }
                        .onFailure { e ->
                            Log.w(TAG, "Cannot access source '${source.displayName}': ${e.message}")
                        }
                        .getOrNull()?.let { chatId ->
                            loadPage(source, chatId, reset = true)
                        }
                }
            }.awaitAll().filterNotNull()

            if (resolvedPages.isEmpty()) {
                _noAccessMessage.value = "You currently do not have access to any Hasikit content sources.\n\nPlease contact:\n@hasikit_m_bot"
            } else {
                _sourcePages.value = resolvedPages
                // Fetch thumbnails in background
                fetchThumbnails(resolvedPages.flatMap { it.media })
            }

            _isLoading.value = false
        }
    }

    private suspend fun loadPage(source: TelegramSource, chatId: Long, reset: Boolean): SourcePage? {
        val existing = if (reset) null else _sourcePages.value.find { it.chatId == chatId }
        val offsetId = if (reset) 0L else existing?.lastMessageId ?: 0L
        if (existing != null && !existing.hasMore && !reset) return existing

        return channelRepository.getChannelMedia(chatId, offsetId, PAGE_SIZE)
            .getOrNull()
            ?.let { page ->
                val allMedia = if (reset) page else (existing?.media ?: emptyList()) + page
                SourcePage(
                    source = source,
                    chatId = chatId,
                    media = allMedia,
                    lastMessageId = page.lastOrNull()?.messageId ?: offsetId,
                    hasMore = page.size >= PAGE_SIZE
                )
            }
    }

    fun loadMore() {
        if (_isLoadingMore.value) return
        val pages = _sourcePages.value
        if (pages.none { it.hasMore }) return
        viewModelScope.launch {
            _isLoadingMore.value = true
            val updated = pages.map { page ->
                if (!page.hasMore) return@map page
                loadPage(page.source, page.chatId, reset = false) ?: page
            }
            _sourcePages.value = updated
            fetchThumbnails(updated.flatMap { it.media })
            _isLoadingMore.value = false
        }
    }

    fun refresh() {
        _sourcePages.value = emptyList()
        loadAllSources()
    }

    private fun fetchThumbnails(mediaList: List<TelegramMedia>) {
        viewModelScope.launch {
            mediaList
                .filter { it.thumbnailFileId != null && !_thumbnailCache.value.containsKey(it.thumbnailFileId) }
                .distinctBy { it.thumbnailFileId }
                .forEach { media ->
                    val fileId = media.thumbnailFileId ?: return@forEach
                    val path = channelRepository.downloadThumbnail(fileId)
                    // Emit new map copy — triggers reactive recomposition immediately
                    _thumbnailCache.value = _thumbnailCache.value + (fileId to path)
                }
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

    // Added pause download from home screen
    fun pauseDownload(videoId: String) {
        Log.d(TAG, "pauseDownload videoId=$videoId")
        downloadManager.pauseDownload(videoId)
    }

    // Added resume download from home screen
    fun resumeDownload(videoId: String) {
        val video = videos.value.find { it.id == videoId } ?: return
        Log.d(TAG, "resumeDownload videoId=$videoId")
        downloadManager.resumeDownload(video)
    }
}
