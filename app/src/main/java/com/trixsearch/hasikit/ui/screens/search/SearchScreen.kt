package com.trixsearch.hasikit.ui.screens.search

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.trixsearch.hasikit.ui.navigation.Screen
import com.trixsearch.hasikit.ui.screens.home.HorizontalVideoCard
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val channelRepository: TelegramChannelRepository,
    private val sourceConfig: TelegramSourceConfig
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query

    // Cache resolved chat IDs per source identifier
    private val chatIdCache = mutableMapOf<String, Long>()

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val results: StateFlow<List<Video>> = _query
        .debounce(300)
        .flatMapLatest { q ->
            if (q.isBlank()) return@flatMapLatest flowOf(emptyList())
            flow {
                val allSources = buildList {
                    addAll(sourceConfig.officialSources)
                    addAll(sourceConfig.userSourcesFlow.first())
                }
                val allResults = coroutineScope {
                    allSources.map { source ->
                        async {
                            val chatId = resolveChatId(source) ?: return@async emptyList<Video>()
                            channelRepository.searchChannelMedia(chatId, q)
                                .getOrNull()
                                ?.map { it.toVideo(source) }
                                ?: emptyList()
                        }
                    }.awaitAll().flatten()
                }
                emit(allResults)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setQuery(q: String) { _query.value = q }

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
    sourceLabel = source.displayName
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    navController: NavController,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val query by viewModel.query.collectAsState()
    val results by viewModel.results.collectAsState()

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
            }
        }
    ) { padding ->
        when {
            query.isBlank() -> {
                Box(Modifier.padding(padding).fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Type to search all sources", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            results.isEmpty() -> {
                Box(Modifier.padding(padding).fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.SearchOff, null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.height(8.dp))
                        Text("No results for \"$query\"", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
            else -> {
                LazyColumn(
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
            }
        }
    }
}
