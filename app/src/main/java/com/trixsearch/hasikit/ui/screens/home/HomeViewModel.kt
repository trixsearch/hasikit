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
import com.trixsearch.hasikit.search.SearchEngine
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
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
                    val video = repository.getVideoById(progress.videoId) ?: return@mapNotNull null

                    // Continue Watching cleanup: if the video is marked downloaded but the file
                    // no longer exists on disk, treat it as not downloaded
                    val fileExists = video.localPath?.let { java.io.File(it).exists() } ?: false
                    val effectivelyDownloaded = video.isDownloaded && fileExists

                    // If video is not streamable AND not effectively downloaded, the entry is dead—
                    // remove it from Continue Watching to prevent dead entries
                    if (!video.isStreamable && !effectivelyDownloaded) {
                        Log.d(TAG, "continueWatching: removing dead entry videoId=${video.id} — not streamable and file missing")
                        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
                            repository.deleteWatchProgress(video.id)
                        }
                        return@mapNotNull null
                    }

                    // If file was deleted but video is streamable, keep the entry (can stream)
                    // Update the video's isDownloaded flag if file is missing
                    val correctedVideo = if (video.isDownloaded && !fileExists) {
                        video.copy(isDownloaded = false, localPath = null)
                    } else video

                    correctedVideo to progress
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
        viewModelScope.launch {
            sourceConfig.userSourcesFlow.drop(1).collect {
                if (authRepository.authState.value is AuthState.Authenticated && !isLoadingAllSources) {
                    loadAllSources()
                }
            }
        }
        // Bug fix #4: observe thumbnailCacheVersion — reload thumbnails when cache is cleared from Settings
        viewModelScope.launch {
            downloadManager.thumbnailCacheVersion.drop(1).collect {
                Log.d(TAG, "[THUMBNAIL] thumbnailCacheVersion changed — invalidating and reloading")
                invalidateAndReloadThumbnails()
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
                // Set pages first so videos StateFlow emits immediately with the full first page
                _sourcePages.value = resolvedPages
                // Fetch thumbnails in background — feed is already visible while thumbnails load
                fetchThumbnails(resolvedPages.flatMap { it.media })
            }

            // isLoading=false only after pages are set so skeleton shows until data is ready
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
                    Log.d(TAG, "[THUMBNAIL] fetched fileId=$fileId path=$path")
                    _thumbnailCache.value = _thumbnailCache.value + (fileId to path)
                }
        }
    }

    // Bug fix #4: Thumbnail reload — invalidate in-memory cache and re-fetch all thumbnails
    // Called after clearing thumbnail cache in AdvancedSettings so UI refreshes without restart
    fun invalidateAndReloadThumbnails() {
        Log.d(TAG, "[THUMBNAIL] invalidateAndReloadThumbnails — clearing cache and re-fetching")
        _thumbnailCache.value = emptyMap()
        val allMedia = _sourcePages.value.flatMap { it.media }
        fetchThumbnails(allMedia)
    }

    // ── Smart Search V4 ────────────────────────────────────────────────────────
    //
    // 3-stage pipeline:
    //   Stage 1 — Instant local Room search (returns immediately while Stage 2 runs)
    //   Stage 2 — Telegram multi-query search across all resolved sources
    //   Stage 3 — Smart ranking via SearchEngine (score 0–100, filter < 50)
    //
    // SearchEngine.parseIntent() extracts: movie name, year, audio language,
    // subtitle language, audio type, quality from the raw query.
    // toTelegramQueryVariants() expands into multiple Telegram queries so
    // "pushpa hindi 1080p" searches "pushpa", "pushpa Hindi", "pushpa 1080p", etc.

    private val _searchResults = MutableStateFlow<List<Video>>(emptyList())
    val searchResults: StateFlow<List<Video>> = _searchResults

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching

    // Parsed intent exposed so HomeScreen can show intent chips (year, language, quality)
    private val _searchIntent = MutableStateFlow<SearchEngine.SearchIntent?>(null)
    val searchIntent: StateFlow<SearchEngine.SearchIntent?> = _searchIntent

    fun searchTelegram(query: String) {
        if (query.isBlank()) { clearSearch(); return }
        viewModelScope.launch {
            _isSearching.value = true
            _searchResults.value = emptyList()

            // Parse query into structured intent
            val intent = SearchEngine.parseIntent(query)
            _searchIntent.value = intent
            Log.d(TAG, "[SEARCH] intent: movie='${intent.movieName}' year=${intent.year} audio=${intent.audioLanguage} sub=${intent.subtitleLanguage} quality=${intent.quality} variants=${intent.toTelegramQueryVariants()}")

            // Stage 1: Instant local Room search — emit immediately so UI shows something
            // while Telegram search is in progress
            val localVideos = repository.searchVideos(intent.movieName)
                .first()
                .ifEmpty { repository.searchVideos(query).first() }

            // Score and rank local results
            val localRanked = localVideos.mapNotNull { video ->
                val s = SearchEngine.scoreVideo(
                    intent,
                    SearchEngine.VideoFields(
                        title = video.title,
                        fileName = video.telegramFileId, // best we have locally
                        caption = "",
                        sourceLabel = video.sourceLabel
                    )
                )
                if (s >= SearchEngine.SCORE_IGNORE_BELOW)
                    SearchEngine.SearchResult(video, s, "local")
                else null
            }
            // Emit local results immediately so UI is not blank during Telegram search
            if (localRanked.isNotEmpty()) {
                _searchResults.value = SearchEngine.rank(localRanked).map { it.item }
                Log.d(TAG, "[SEARCH] Stage-1 local: ${localRanked.size} results emitted")
            }

            // Stage 2: Telegram multi-query search across all resolved sources
            val telegramResults = mutableListOf<Video>()
            val seenIds = mutableSetOf<String>()
            val queryVariants = intent.toTelegramQueryVariants()

            _sourcePages.value.forEach { page ->
                val mediaList = channelRepository.searchChannelMediaMulti(
                    page.chatId, queryVariants, 100
                ).getOrNull() ?: return@forEach

                Log.d(TAG, "[SEARCH] Stage-2 source='${page.source.displayName}' raw=${mediaList.size}")

                mediaList.forEach { media ->
                    val id = "${media.channelId}_${media.messageId}"
                    if (!seenIds.add(id)) return@forEach

                    // Stage 3: Score each Telegram result with SearchEngine
                    val s = SearchEngine.scoreVideo(
                        intent,
                        SearchEngine.VideoFields(
                            title = media.title,
                            fileName = media.fileName,
                            caption = media.caption,
                            sourceLabel = page.source.displayName
                        )
                    )
                    if (s >= SearchEngine.SCORE_IGNORE_BELOW) {
                        telegramResults.add(
                            Video(
                                id = id,
                                title = media.title.ifBlank { media.fileName },
                                thumbnail = _thumbnailCache.value[media.thumbnailFileId],
                                videoUrl = "",
                                telegramFileId = media.fileId.toString(),
                                duration = media.duration.toLong() * 1000L,
                                size = media.size,
                                localPath = null,
                                isDownloaded = false,
                                sourceLabel = page.source.displayName,
                                isStreamable = media.isStreamable
                            )
                        )
                    }
                }
            }

            // Merge local + Telegram results, re-rank the combined set
            val allCandidates = mutableListOf<SearchEngine.SearchResult<Video>>()

            // Re-score local results (they already have thumbnails/download state)
            localVideos.forEach { video ->
                val s = SearchEngine.scoreVideo(
                    intent,
                    SearchEngine.VideoFields(
                        title = video.title,
                        fileName = video.telegramFileId,
                        caption = "",
                        sourceLabel = video.sourceLabel
                    )
                )
                if (s >= SearchEngine.SCORE_IGNORE_BELOW && !seenIds.contains(video.id)) {
                    seenIds.add(video.id)
                    allCandidates.add(SearchEngine.SearchResult(video, s, "local"))
                }
            }

            // Score Telegram results
            telegramResults.forEach { video ->
                val s = SearchEngine.scoreVideo(
                    intent,
                    SearchEngine.VideoFields(
                        title = video.title,
                        fileName = video.telegramFileId,
                        caption = "",
                        sourceLabel = video.sourceLabel
                    )
                )
                allCandidates.add(SearchEngine.SearchResult(video, s, "telegram"))
            }

            val ranked = SearchEngine.rank(allCandidates)
            Log.d(TAG, "[SEARCH] Stage-3 ranked: ${ranked.size} results (local=${localRanked.size} telegram=${telegramResults.size})")

            _searchResults.value = ranked.map { it.item }
            _isSearching.value = false

            // Fetch thumbnails for new Telegram results in background
            val newMedia = _sourcePages.value.flatMap { it.media }
                .filter { media ->
                    val id = "${media.channelId}_${media.messageId}"
                    telegramResults.any { it.id == id }
                }
            if (newMedia.isNotEmpty()) fetchThumbnails(newMedia)
        }
    }

    fun clearSearch() {
        _searchResults.value = emptyList()
        _isSearching.value = false
        _searchIntent.value = null
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
