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
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "HomeViewModel"
private const val PAGE_SIZE = 25
private const val PREFETCH_THRESHOLD = 10
private const val AUTO_REFRESH_SECONDS = 60L

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

    private val _thumbnailCache = MutableStateFlow<Map<Long, String?>>(emptyMap())

    private val _selectedSourceFilter = MutableStateFlow<String?>(null)
    val selectedSourceFilter: StateFlow<String?> = _selectedSourceFilter

    val availableSources: StateFlow<List<TelegramSource>> = _sourcePages
        .map { pages -> pages.map { it.source } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setSourceFilter(sourceDisplayName: String?) {
        _selectedSourceFilter.value = sourceDisplayName
    }

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
        val seenFileIds = mutableSetOf<Long>()
        val seenTitleSize = mutableSetOf<String>()
        filteredPages.flatMap { page ->
            page.media.mapNotNull { media ->
                val id = "${media.channelId}_${media.messageId}"
                if (media.fileId != 0L && !seenFileIds.add(media.fileId)) return@mapNotNull null
                val titleSizeKey = "${media.title.lowercase().trim()}_${media.size}"
                if (!seenTitleSize.add(titleSizeKey)) return@mapNotNull null
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
                    sourceLabel = page.source.displayName,
                    isStreamable = media.isStreamable
                )
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Guard against concurrent loadAllSources calls — auth collect and userSources collect
    // can both fire simultaneously on startup causing duplicate getChannelMedia requests
    @Volatile private var isLoadingAllSources = false

    init {
        viewModelScope.launch {
            authRepository.authState.collect { state ->
                if (state is AuthState.Authenticated && _sourcePages.value.isEmpty() && !isLoadingAllSources) {
                    loadAllSources()
                    if (AUTO_REFRESH_SECONDS > 0) startAutoRefresh()
                }
            }
        }
        // Reload when user sources change, but only if already authenticated
        viewModelScope.launch {
            sourceConfig.userSourcesFlow.drop(1).collect {
                if (authRepository.authState.value is AuthState.Authenticated && !isLoadingAllSources) {
                    loadAllSources()
                }
            }
        }
    }

    // Auto-refresh: polls for new content at the top without disturbing scroll position
    private fun startAutoRefresh() {
        viewModelScope.launch {
            while (true) {
                delay(AUTO_REFRESH_SECONDS * 1000L)
                if (authRepository.authState.value !is AuthState.Authenticated) continue
                val currentPages = _sourcePages.value
                if (currentPages.isEmpty()) continue
                val allSources = buildList {
                    addAll(sourceConfig.officialSources)
                    addAll(sourceConfig.userSourcesFlow.first())
                }
                val updatedPages = currentPages.map { existingPage ->
                    val source = allSources.find { it.displayName == existingPage.source.displayName }
                        ?: return@map existingPage
                    val freshPage = channelRepository.getChannelMedia(existingPage.chatId, 0L, PAGE_SIZE)
                        .getOrNull()?.first ?: return@map existingPage
                    val existingIds = existingPage.media.map { it.messageId }.toSet()
                    val newItems = freshPage.filter { it.messageId !in existingIds }
                    if (newItems.isEmpty()) return@map existingPage
                    Log.i(TAG, "autoRefresh: ${newItems.size} new items for source=${source.displayName}")
                    existingPage.copy(media = newItems + existingPage.media)
                }
                _sourcePages.value = updatedPages
                fetchThumbnails(updatedPages.flatMap { it.media })
            }
        }
    }

    private fun loadAllSources() {
        viewModelScope.launch {
            if (isLoadingAllSources) return@launch
            isLoadingAllSources = true
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
                fetchThumbnails(resolvedPages.flatMap { it.media })
            }

            _isLoading.value = false
            isLoadingAllSources = false
        }
    }

    private suspend fun loadPage(source: TelegramSource, chatId: Long, reset: Boolean): SourcePage? {
        val existing = if (reset) null else _sourcePages.value.find { it.chatId == chatId }
        val offsetId = if (reset) 0L else existing?.lastMessageId ?: 0L
        if (existing != null && !existing.hasMore && !reset) {
            Log.d(TAG, "loadPage '${source.displayName}' hasMore=false, skipping")
            return existing
        }
        Log.d(TAG, "loadPage '${source.displayName}' chatId=$chatId offsetId=$offsetId reset=$reset")

        return channelRepository.getChannelMedia(chatId, offsetId, PAGE_SIZE)
            .getOrNull()
            ?.let { (page, rawCount) ->
                val allMedia = if (reset) page else (existing?.media ?: emptyList()) + page
                val oldestMessageId = allMedia.minOfOrNull { it.messageId } ?: offsetId
                // hasMore based on rawCount so non-video messages don't prematurely stop pagination
                val hasMore = rawCount >= PAGE_SIZE
                Log.d(TAG, "loadPage '${source.displayName}' fetched=${page.size} raw=$rawCount total=${allMedia.size} oldestMsgId=$oldestMessageId hasMore=$hasMore")
                SourcePage(
                    source = source,
                    chatId = chatId,
                    media = allMedia,
                    lastMessageId = oldestMessageId,
                    hasMore = hasMore
                )
            }
    }

    val prefetchThreshold: Int get() = PREFETCH_THRESHOLD

    fun loadMore() {
        if (_isLoadingMore.value) return
        val pages = _sourcePages.value
        val totalLoaded = pages.sumOf { it.media.size }
        val hasMoreAny = pages.any { it.hasMore }
        Log.d(TAG, "loadMore called: totalLoaded=$totalLoaded hasMore=$hasMoreAny pages=${pages.size}")
        pages.forEach { page ->
            Log.d(TAG, "  source='${page.source.displayName}' loaded=${page.media.size} lastMessageId=${page.lastMessageId} hasMore=${page.hasMore}")
        }
        if (!hasMoreAny) {
            Log.d(TAG, "loadMore: no more pages available, skipping")
            return
        }
        viewModelScope.launch {
            _isLoadingMore.value = true
            val updated = pages.map { page ->
                if (!page.hasMore) return@map page
                Log.d(TAG, "Fetching next page for '${page.source.displayName}' cursor=${page.lastMessageId}")
                val result = loadPage(page.source, page.chatId, reset = false) ?: page
                Log.d(TAG, "After fetch '${page.source.displayName}': loaded=${result.media.size} newCursor=${result.lastMessageId} hasMore=${result.hasMore}")
                result
            }
            _sourcePages.value = updated
            val newTotal = updated.sumOf { it.media.size }
            Log.d(TAG, "loadMore complete: totalLoaded=$newTotal")
            fetchThumbnails(updated.flatMap { it.media })
            _isLoadingMore.value = false
        }
    }

    fun refresh() {
        isLoadingAllSources = false
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

    fun pauseDownload(videoId: String) {
        Log.d(TAG, "pauseDownload videoId=$videoId")
        downloadManager.pauseDownload(videoId)
    }

    fun resumeDownload(videoId: String) {
        val video = videos.value.find { it.id == videoId } ?: return
        Log.d(TAG, "resumeDownload videoId=$videoId")
        downloadManager.resumeDownload(video)
    }
}
