package com.trixsearch.hasikit.ui.screens.search

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
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
import com.trixsearch.hasikit.telegram.domain.model.TelegramMedia
import com.trixsearch.hasikit.telegram.domain.repository.TelegramChannelRepository
import com.trixsearch.hasikit.ui.navigation.Screen
import com.trixsearch.hasikit.ui.screens.home.HorizontalVideoCard
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import javax.inject.Inject

private const val SOURCE_CHANNEL = "testhasikit"

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val channelRepository: TelegramChannelRepository
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query

    private val _chatId = MutableStateFlow<Long?>(null)

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val results: StateFlow<List<Video>> = _query
        .debounce(300)
        .flatMapLatest { q ->
            if (q.isBlank()) return@flatMapLatest flowOf(emptyList())
            flow {
                val chatId = getOrResolveChatId() ?: run {
                    emit(emptyList()); return@flow
                }
                channelRepository.searchChannelMedia(chatId, q)
                    .onSuccess { emit(it.map { m -> m.toVideo() }) }
                    .onFailure { emit(emptyList()) }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setQuery(q: String) { _query.value = q }

    private suspend fun getOrResolveChatId(): Long? {
        _chatId.value?.let { return it }
        return channelRepository.resolveChannel(SOURCE_CHANNEL)
            .getOrNull()
            ?.also { _chatId.value = it }
    }
}

private fun TelegramMedia.toVideo() = Video(
    id             = messageId.toString(),
    title          = title.ifBlank { fileName },
    thumbnail      = null,
    videoUrl       = "",
    telegramFileId = fileId.toString(),
    duration       = duration.toLong() * 1000L,
    size           = size
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
        topBar = { TopAppBar(title = { Text("Search") }) }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).fillMaxSize()
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = { viewModel.setQuery(it) },
                placeholder = { Text("Search @testhasikit…") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = { viewModel.setQuery("") }) {
                            Icon(Icons.Default.Clear, "Clear")
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            when {
                query.isBlank() -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Type to search channel", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                results.isEmpty() -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No results for \"$query\"", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                else -> {
                    LazyColumn(contentPadding = PaddingValues(vertical = 8.dp)) {
                        items(results, key = { it.id }) { video ->
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
}
