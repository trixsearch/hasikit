package com.trixsearch.hasikit.ui.screens.library

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.trixsearch.hasikit.domain.model.DownloadState
import com.trixsearch.hasikit.domain.model.DownloadTask
import com.trixsearch.hasikit.domain.model.Video
import com.trixsearch.hasikit.domain.model.WatchProgress
import com.trixsearch.hasikit.domain.repository.VideoRepository
import com.trixsearch.hasikit.download.HasikitDownloadManager
import com.trixsearch.hasikit.ui.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "LibraryScreen"

data class LibraryItem(
    val video: Video,
    val task: DownloadTask?,
    val watchProgress: WatchProgress?
)

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val repository: VideoRepository,
    private val downloadManager: HasikitDownloadManager
) : ViewModel() {

    val downloadedItems: StateFlow<List<LibraryItem>> =
        combine(
            repository.getDownloadedVideos(),
            downloadManager.downloadTasks,
            repository.getAllWatchProgress()
        ) { videos, tasks, progressList ->
            val progressMap = progressList.associateBy { it.videoId }
            videos.map { video ->
                LibraryItem(
                    video = video,
                    task = tasks[video.id],
                    watchProgress = progressMap[video.id]
                )
            }.also { Log.d(TAG, "downloadedItems updated: ${it.size} items") }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeDownloads: StateFlow<List<LibraryItem>> =
        combine(
            repository.getAllVideos(),
            downloadManager.downloadTasks
        ) { videos, tasks ->
            tasks.values
                .filter { it.state == DownloadState.DOWNLOADING || it.state == DownloadState.PAUSED }
                .mapNotNull { task ->
                    videos.find { it.id == task.videoId }?.let { video ->
                        LibraryItem(video = video, task = task, watchProgress = null)
                    }
                }.also { Log.d(TAG, "activeDownloads updated: ${it.size} items") }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun deleteDownload(videoId: String) {
        Log.d(TAG, "deleteDownload videoId=$videoId")
        downloadManager.deleteDownload(videoId)
    }

    fun retryDownload(video: Video) {
        Log.d(TAG, "retryDownload videoId=${video.id}")
        downloadManager.retryDownload(video)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    navController: NavController,
    viewModel: LibraryViewModel = hiltViewModel()
) {
    val downloadedItems by viewModel.downloadedItems.collectAsState()
    val activeDownloads by viewModel.activeDownloads.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Library") }) }
    ) { padding ->
        if (downloadedItems.isEmpty() && activeDownloads.isEmpty()) {
            Box(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.VideoLibrary,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "No downloads yet",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        "Download videos from the Home screen to watch offline",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (activeDownloads.isNotEmpty()) {
                    item {
                        Text(
                            "Downloading",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }
                    items(activeDownloads, key = { it.video.id + "_active" }) { item ->
                        ActiveDownloadCard(item = item)
                    }
                }

                if (downloadedItems.isNotEmpty()) {
                    item {
                        Text(
                            "Downloaded",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                        )
                    }
                    items(downloadedItems, key = { it.video.id }) { item ->
                        DownloadedVideoCard(
                            item = item,
                            onPlay = {
                                Log.d(TAG, "Play offline videoId=${item.video.id} path=${item.video.localPath}")
                                navController.navigate(Screen.Player.createRoute(item.video.id))
                            },
                            onDelete = { viewModel.deleteDownload(item.video.id) },
                            onRedownload = { viewModel.retryDownload(item.video) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ActiveDownloadCard(item: LibraryItem) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = item.video.thumbnail,
                contentDescription = null,
                modifier = Modifier
                    .size(56.dp)
                    .padding(end = 12.dp),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    item.video.title,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                val progress = item.task?.progress ?: 0f
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    "${(progress * 100).toInt()}% — ${if (item.task?.state == DownloadState.PAUSED) "Paused" else "Downloading"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun DownloadedVideoCard(
    item: LibraryItem,
    onPlay: () -> Unit,
    onDelete: () -> Unit,
    onRedownload: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box {
                    AsyncImage(
                        model = item.video.thumbnail,
                        contentDescription = null,
                        modifier = Modifier
                            .size(width = 80.dp, height = 56.dp),
                        contentScale = ContentScale.Crop
                    )
                    item.watchProgress?.let { wp ->
                        if (wp.duration > 0) {
                            LinearProgressIndicator(
                                progress = { wp.progress },
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .fillMaxWidth()
                                    .height(3.dp),
                                color = Color.Red,
                                trackColor = Color.Gray.copy(alpha = 0.4f)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        item.video.title,
                        style = MaterialTheme.typography.titleSmall,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        formatBytes(item.video.size),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    item.watchProgress?.let { wp ->
                        if (wp.duration > 0) {
                            Text(
                                "Watched ${formatTime(wp.lastPosition)} / ${formatTime(wp.duration)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                IconButton(onClick = onPlay) {
                    Icon(Icons.Default.PlayArrow, contentDescription = "Play", tint = MaterialTheme.colorScheme.primary)
                }
                IconButton(onClick = { showDeleteDialog = true }) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Download") },
            text = { Text("Delete \"${item.video.title}\"? This will remove the local file.") },
            confirmButton = {
                TextButton(onClick = {
                    onDelete()
                    showDeleteDialog = false
                }) { Text("Delete", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Cancel") }
            }
        )
    }
}

private fun formatBytes(bytes: Long): String = when {
    bytes >= 1_073_741_824L -> "%.1f GB".format(bytes / 1_073_741_824.0)
    bytes >= 1_048_576L -> "%.1f MB".format(bytes / 1_048_576.0)
    bytes >= 1024L -> "%.1f KB".format(bytes / 1024.0)
    else -> "$bytes B"
}

private fun formatTime(ms: Long): String {
    if (ms <= 0) return "00:00"
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%02d:%02d".format(minutes, seconds)
}
