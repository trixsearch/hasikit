package com.trixsearch.hasikit.ui.screens.search

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.trixsearch.hasikit.domain.model.Video
import com.trixsearch.hasikit.telegram.config.TelegramSource
import com.trixsearch.hasikit.telegram.config.TelegramSourceConfig
import com.trixsearch.hasikit.telegram.domain.model.TelegramMedia
import com.trixsearch.hasikit.telegram.domain.repository.TelegramChannelRepository
import com.trixsearch.hasikit.ui.components.FastScrollerBox
import com.trixsearch.hasikit.ui.navigation.Screen
import com.trixsearch.hasikit.ui.screens.home.HorizontalVideoCard
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val channelRepository: TelegramChannelRepository,
    private val sourceConfig: TelegramSourceConfig
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query

    private val _selectedSource = MutableStateFlow<String?>(null) // null = All
    val selectedSource: StateFlow<String?> = _selectedSource

    val availableSources: StateFlow<List<TelegramSource>> = sourceConfig.userSourcesFlow
        .map { userSources -> sourceConfig.officialSources + userSources }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), sourceConfig.officialSources)

    // Cache resolved chat IDs per source identifier
    private val chatIdCache = mutableMapOf<String, Long>()

    // Stage 1 results — local cache hits shown immediately
    private val _localResults = MutableStateFlow<List<Video>>(emptyList())
    val localResults: StateFlow<List<Video>> = _localResults

    // Stage 2 results — Telegram search results merged with local
    private val _telegramResults = MutableStateFlow<List<Video>>(emptyList())
    val telegramResults: StateFlow<List<Video>> = _telegramResults

    // Combined results — local first, then Telegram-only results appended
    val results: StateFlow<List<Video>> = combine(_localResults, _telegramResults) { local, telegram ->
        // Merge: local results first, then Telegram results not already in local
        val localIds = local.map { it.id }.toSet()
        local + telegram.filter { it.id !in localIds }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Loading state for Stage 2 Telegram search
    private val _isTelegramSearching = MutableStateFlow(false)
    val isTelegramSearching: StateFlow<Boolean> = _isTelegramSearching

    // Local video cache injected from HomeViewModel via setLocalVideos()
    private val _localVideoCache = MutableStateFlow<List<Video>>(emptyList())

    fun setLocalVideos(videos: List<Video>) {
        _localVideoCache.value = videos
    }

    // Two-stage search job — started in init block, runs for the lifetime of the ViewModel
    // @OptIn moved to the function because init blocks cannot carry annotations in Kotlin
    init {
        startSearchJob()
    }

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    private fun startSearchJob() {
        viewModelScope.launch {
            combine(_query, _selectedSource) { q, src -> q to src }
                .debounce(300)
                .collectLatest { (q, selectedSrc) ->
                    if (q.isBlank()) {
                        _localResults.value = emptyList()
                        _telegramResults.value = emptyList()
                        return@collectLatest
                    }

                    // Stage 1 — search locally in already loaded/cached videos immediately
                    val localHits = _localVideoCache.value.filter {
                        it.title.contains(q, ignoreCase = true) ||
                        it.sourceLabel.contains(q, ignoreCase = true)
                    }
                    _localResults.value = localHits

                    // Stage 2 — perform Telegram source search for content not yet loaded locally
                    _isTelegramSearching.value = true
                    try {
                        val allSources = buildList {
                            addAll(sourceConfig.officialSources)
                            addAll(sourceConfig.userSourcesFlow.first())
                        }
                        val filteredSources = if (selectedSrc == null) allSources
                            else allSources.filter { it.identifier == selectedSrc }
                        val telegramHits = coroutineScope {
                            filteredSources.map { source ->
                                async {
                                    val chatId = resolveChatId(source) ?: return@async emptyList<Video>()
                                    channelRepository.searchChannelMedia(chatId, q)
                                        .getOrNull()
                                        ?.map { it.toVideo(source) }
                                        ?: emptyList()
                                }
                            }.awaitAll().flatten()
                        }
                        _telegramResults.value = telegramHits
                    } catch (e: Exception) {
                        android.util.Log.e("SearchViewModel", "Telegram search failed", e)
                    } finally {
                        _isTelegramSearching.value = false
                    }
                }
        }
    }

    fun setQuery(q: String) { _query.value = q }
    fun setSelectedSource(identifier: String?) { _selectedSource.value = identifier }

    private suspend fun resolveChatId(source: TelegramSource): Long? {
        chatIdCache[source.identifier]?.let { return it }
        return channelRepository.resolveSource(source)
            .getOrNull()
            ?.also { chatIdCache[source.identifier] = it }
    }
}

private fun TelegramMedia.toVideo(source: TelegramSource) = Video(
    id = "${channelId}_${messageId}",
    title = title.ifBlank { fileName },
    thumbnail = null,
    videoUrl = "",
    telegramFileId = fileId.toString(),
    duration = duration.toLong() * 1000L,
    size = size,
    sourceLabel = source.displayName,
    // Streamability logic — passed from TelegramMedia
    isStreamable = isStreamable
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    navController: NavController,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val query by viewModel.query.collectAsState()
    // Two-stage search results — local + Telegram merged
    val results by viewModel.results.collectAsState()
    val isTelegramSearching by viewModel.isTelegramSearching.collectAsState()
    val availableSources by viewModel.availableSources.collectAsState()
    val selectedSource by viewModel.selectedSource.collectAsState()

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Text("Search", style = MaterialTheme.typography.headlineSmall)
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(
                    value = query,
                    onValueChange = { viewModel.setQuery(it) },
                    placeholder = { Text("Search all sources…") },
                    leadingIcon = { Icon(Icons.Default.Search, null, modifier = Modifier.size(20.dp)) },
                    trailingIcon = {
                        if (query.isNotEmpty()) {
                            IconButton(onClick = { viewModel.setQuery("") }, modifier = Modifier.size(36.dp)) {
                                Icon(Icons.Default.Clear, "Clear", modifier = Modifier.size(18.dp))
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                    )
                )
                Spacer(Modifier.height(8.dp))
                // Source filter chips
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = selectedSource == null,
                        onClick = { viewModel.setSelectedSource(null) },
                        label = { Text("All") }
                    )
                    availableSources.forEach { source ->
                        FilterChip(
                            selected = selectedSource == source.identifier,
                            onClick = { viewModel.setSelectedSource(source.identifier) },
                            label = { Text(source.displayName) }
                        )
                    }
                }
                // Stage 2 indicator — shown while Telegram search is in progress
                if (isTelegramSearching) {
                    Spacer(Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        CircularProgressIndicator(modifier = Modifier.size(14.dp), strokeWidth = 2.dp)
                        Text("Searching Telegram sources…", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    ) { padding ->
        when {
            query.isBlank() -> {
                Box(Modifier.padding(padding).fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Type to search all sources", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            results.isEmpty() && !isTelegramSearching -> {
                Box(Modifier.padding(padding).fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.SearchOff, null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.height(8.dp))
                        Text("No results for \"$query\"", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
            else -> {
                val searchListState = androidx.compose.foundation.lazy.rememberLazyListState()
                // Fast scroller wraps search results for quick navigation through long result lists
                FastScrollerBox(listState = searchListState) {
                LazyColumn(
                    state = searchListState,
                    modifier = Modifier.padding(padding),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(results, key = { "search_${it.id}" }) { video ->
                        HorizontalVideoCard(
                            video = video,
                            onClick = { navController.navigate(Screen.Player.createRoute(video.id)) },
                            onDownloadClick = {}
                        )
                    }
                }
                } // end FastScrollerBox
            }
        }
    }
}
