package com.trixsearch.hasikit.ui.screens.library

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.trixsearch.hasikit.domain.model.Video
import com.trixsearch.hasikit.domain.repository.VideoRepository
import com.trixsearch.hasikit.download.HasikitDownloadManager
import com.trixsearch.hasikit.ui.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val repository: VideoRepository,
    private val downloadManager: HasikitDownloadManager
) : ViewModel() {
    val downloadedVideos: StateFlow<List<Video>> = repository.getDownloadedVideos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun deleteDownload(videoId: String) {
        downloadManager.deleteDownload(videoId)
    }
}

@Composable
fun LibraryScreen(
    navController: NavController,
    viewModel: LibraryViewModel = hiltViewModel()
) {
    val videos by viewModel.downloadedVideos.collectAsState()

    Scaffold(
        topBar = {
            @OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
            TopAppBar(title = { Text("Library") })
        }
    ) { padding ->
        if (videos.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No downloads yet")
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding).fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(videos) { video ->
                    DownloadedVideoItem(
                        video = video,
                        onPlay = { navController.navigate(Screen.Player.createRoute(video.id)) },
                        onDelete = { viewModel.deleteDownload(video.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun DownloadedVideoItem(video: Video, onPlay: () -> Unit, onDelete: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(video.title, style = MaterialTheme.typography.titleMedium)
                Text("${video.size / 1024 / 1024} MB", style = MaterialTheme.typography.bodySmall)
            }
            IconButton(onClick = onPlay) {
                Icon(Icons.Default.PlayArrow, contentDescription = "Play")
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete")
            }
        }
    }
}
