package com.trixsearch.hasikit.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.DownloadDone
import androidx.compose.material.icons.filled.Downloading
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.trixsearch.hasikit.domain.model.Video
import com.trixsearch.hasikit.domain.model.WatchProgress
import com.trixsearch.hasikit.ui.navigation.Screen

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val videos by viewModel.videos.collectAsState()
    val continueWatching by viewModel.continueWatching.collectAsState()

    Scaffold(
        topBar = {
            @OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
            TopAppBar(title = { Text("Hasikit") })
        }
    ) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.padding(padding).fillMaxSize(),
            contentPadding = PaddingValues(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (continueWatching.isNotEmpty()) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Text(
                        "Continue Watching",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
                item(span = { GridItemSpan(maxLineSpan) }) {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(continueWatching) { (video, progress) ->
                            ContinueWatchingCard(video, progress) {
                                navController.navigate(Screen.Player.createRoute(video.id))
                            }
                        }
                    }
                }
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Text(
                        "Explore",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                    )
                }
            }

            items(videos) { video ->
                VideoCard(
                    video = video,
                    onClick = {
                        navController.navigate(Screen.Player.createRoute(video.id))
                    },
                    onDownloadClick = {
                        if (video.isDownloaded) {
                            viewModel.deleteDownload(video.id)
                        } else {
                            viewModel.startDownload(video)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun ContinueWatchingCard(video: Video, progress: WatchProgress, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(200.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Box {
                AsyncImage(
                    model = video.thumbnail,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    contentScale = ContentScale.Crop
                )
                val percent = if (progress.duration > 0) progress.lastPosition.toFloat() / progress.duration else 0f
                LinearProgressIndicator(
                    progress = { percent },
                    modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth().height(4.dp),
                    color = Color.Red,
                    trackColor = Color.Gray.copy(alpha = 0.5f)
                )
            }
            Text(
                video.title,
                modifier = Modifier.padding(8.dp),
                maxLines = 1,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun VideoCard(
    video: Video,
    onClick: () -> Unit,
    onDownloadClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            Box {
                AsyncImage(
                    model = video.thumbnail,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    contentScale = ContentScale.Crop
                )
                if (video.isDownloaded) {
                    Icon(
                        Icons.Default.DownloadDone,
                        contentDescription = null,
                        tint = Color.Green,
                        modifier = Modifier.align(Alignment.TopEnd).padding(4.dp)
                    )
                }
            }
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = video.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (video.downloadProgress > 0 && video.downloadProgress < 1) {
                    LinearProgressIndicator(
                        progress = { video.downloadProgress },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${video.size / 1024 / 1024} MB",
                        style = MaterialTheme.typography.bodySmall
                    )
                    IconButton(onClick = onDownloadClick) {
                        val icon = when {
                            video.isDownloaded -> Icons.Default.DownloadDone
                            video.downloadProgress > 0 -> Icons.Default.Downloading
                            else -> Icons.Default.Download
                        }
                        Icon(
                            icon,
                            contentDescription = "Download",
                            tint = if (video.isDownloaded) Color.Green else LocalContentColor.current
                        )
                    }
                }
            }
        }
    }
}
