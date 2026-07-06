package com.trixsearch.hasikit.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.SubcomposeAsyncImage
import com.trixsearch.hasikit.domain.model.Video
import com.trixsearch.hasikit.domain.model.WatchProgress
import com.trixsearch.hasikit.ui.navigation.Screen

@Composable
private fun LazyListState.OnNearBottom(buffer: Int = 5, onNearBottom: () -> Unit) {
    val shouldLoad = remember {
        derivedStateOf {
            val lastVisible = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            val total = layoutInfo.totalItemsCount
            total > 0 && lastVisible >= total - buffer
        }
    }
    LaunchedEffect(shouldLoad.value) {
        if (shouldLoad.value) onNearBottom()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val videos by viewModel.videos.collectAsState()
    val continueWatching by viewModel.continueWatching.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isLoadingMore by viewModel.isLoadingMore.collectAsState()
    val error by viewModel.error.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    listState.OnNearBottom { viewModel.loadMore() }

    val filteredVideos = remember(videos, searchQuery) {
        if (searchQuery.isBlank()) videos
        else videos.filter { it.title.contains(searchQuery, ignoreCase = true) }
    }
    val downloadedVideos = remember(videos) { videos.filter { it.isDownloaded } }
    val recentlyAdded = remember(videos) { videos.take(5) }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                MaterialTheme.colorScheme.surface,
                                MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                            )
                        )
                    )
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.PlayCircle, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(26.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Hasikit", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary, letterSpacing = 0.5.sp)
                }
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search videos…", style = MaterialTheme.typography.bodyMedium) },
                    leadingIcon = { Icon(Icons.Default.Search, null, modifier = Modifier.size(20.dp)) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }, modifier = Modifier.size(36.dp)) {
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
        PullToRefreshBox(
            isRefreshing = isLoading && videos.isNotEmpty(),
            onRefresh = { viewModel.refresh() },
            modifier = Modifier.padding(padding).fillMaxSize()
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                // Initial loading
                if (isLoading && videos.isEmpty()) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(vertical = 48.dp), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator()
                                Spacer(Modifier.height(12.dp))
                                Text("Loading @testhasikit…", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                    return@LazyColumn
                }

                // Error state
                if (error != null && videos.isEmpty()) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.ErrorOutline, null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(40.dp))
                                Spacer(Modifier.height(8.dp))
                                Text(error!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                                Spacer(Modifier.height(12.dp))
                                Button(onClick = { viewModel.refresh() }) { Text("Retry") }
                            }
                        }
                    }
                    return@LazyColumn
                }

                // Search results
                if (searchQuery.isNotBlank()) {
                    item { SectionHeader("Results for \"$searchQuery\"") }
                    if (filteredVideos.isEmpty()) {
                        item {
                            Box(modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp), contentAlignment = Alignment.Center) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Default.SearchOff, null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Spacer(Modifier.height(8.dp))
                                    Text("No results found", color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    } else {
                        items(filteredVideos, key = { "search_${it.id}" }) { video ->
                            HorizontalVideoCard(
                                video = video,
                                onClick = { navController.navigate(Screen.Player.createRoute(video.id)) },
                                onDownloadClick = {
                                    if (video.isDownloaded) viewModel.deleteDownload(video.id)
                                    else viewModel.startDownload(video)
                                }
                            )
                        }
                    }
                    return@LazyColumn
                }

                // Continue Watching
                if (continueWatching.isNotEmpty()) {
                    item { SectionHeader("Continue Watching") }
                    item {
                        LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            items(continueWatching, key = { "cw_${it.first.id}" }) { (video, progress) ->
                                ContinueWatchingCard(video, progress) { navController.navigate(Screen.Player.createRoute(video.id)) }
                            }
                        }
                    }
                }

                // Recently Added
                if (recentlyAdded.isNotEmpty()) {
                    item { SectionHeader("Recently Added") }
                    item {
                        LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            items(recentlyAdded, key = { "recent_${it.id}" }) { video ->
                                RecentCard(video) { navController.navigate(Screen.Player.createRoute(video.id)) }
                            }
                        }
                    }
                }

                // Downloads
                if (downloadedVideos.isNotEmpty()) {
                    item { SectionHeader("Downloads") }
                    item {
                        LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            items(downloadedVideos, key = { "dl_${it.id}" }) { video ->
                                DownloadedCard(video) { navController.navigate(Screen.Player.createRoute(video.id)) }
                            }
                        }
                    }
                }

                // All Videos
                item { SectionHeader("All Videos") }
                items(videos, key = { it.id }) { video ->
                    HorizontalVideoCard(
                        video = video,
                        onClick = { navController.navigate(Screen.Player.createRoute(video.id)) },
                        onDownloadClick = {
                            if (video.isDownloaded) viewModel.deleteDownload(video.id)
                            else viewModel.startDownload(video)
                        }
                    )
                }

                // Pagination footer
                if (isLoadingMore) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(modifier = Modifier.size(28.dp), strokeWidth = 2.dp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 6.dp)
    )
}

@Composable
fun RecentCard(video: Video, onClick: () -> Unit) {
    Card(modifier = Modifier.width(160.dp).clickable { onClick() }, shape = RoundedCornerShape(12.dp), elevation = CardDefaults.cardElevation(3.dp)) {
        Box {
            VideoThumbnail(url = video.thumbnail, modifier = Modifier.fillMaxWidth().height(95.dp))
            Box(modifier = Modifier.fillMaxWidth().height(95.dp).background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.75f)))))
            Text(video.title, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Medium, maxLines = 2, overflow = TextOverflow.Ellipsis, modifier = Modifier.align(Alignment.BottomStart).padding(6.dp))
        }
    }
}

@Composable
fun ContinueWatchingCard(video: Video, progress: WatchProgress, onClick: () -> Unit) {
    val percent = if (progress.duration > 0) progress.lastPosition.toFloat() / progress.duration else 0f
    Card(modifier = Modifier.width(190.dp).clickable { onClick() }, shape = RoundedCornerShape(12.dp), elevation = CardDefaults.cardElevation(4.dp)) {
        Box {
            VideoThumbnail(url = video.thumbnail, modifier = Modifier.fillMaxWidth().height(108.dp))
            Box(modifier = Modifier.fillMaxWidth().height(108.dp).background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.75f)))))
            Icon(Icons.Default.PlayCircle, null, tint = Color.White.copy(alpha = 0.85f), modifier = Modifier.align(Alignment.Center).size(36.dp))
            Text(video.title, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Medium, maxLines = 2, overflow = TextOverflow.Ellipsis, modifier = Modifier.align(Alignment.BottomStart).padding(start = 6.dp, end = 6.dp, bottom = 10.dp))
            LinearProgressIndicator(progress = { percent }, modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth().height(3.dp), color = Color.Red, trackColor = Color.Gray.copy(alpha = 0.4f))
        }
    }
}

@Composable
fun DownloadedCard(video: Video, onClick: () -> Unit) {
    Card(modifier = Modifier.width(150.dp).clickable { onClick() }, shape = RoundedCornerShape(12.dp), elevation = CardDefaults.cardElevation(3.dp)) {
        Box {
            VideoThumbnail(url = video.thumbnail, modifier = Modifier.fillMaxWidth().height(95.dp))
            Box(modifier = Modifier.fillMaxWidth().height(95.dp).background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.75f)))))
            Surface(modifier = Modifier.align(Alignment.TopEnd).padding(5.dp), shape = RoundedCornerShape(4.dp), color = Color(0xFF1DB954).copy(alpha = 0.9f)) {
                Row(modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.DownloadDone, null, tint = Color.White, modifier = Modifier.size(10.dp))
                    Spacer(Modifier.width(2.dp))
                    Text("Saved", color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                }
            }
            Text(video.title, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Medium, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.align(Alignment.BottomStart).padding(6.dp))
        }
    }
}

@Composable
fun HorizontalVideoCard(video: Video, onClick: () -> Unit, onDownloadClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp).clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(modifier = Modifier.height(82.dp)) {
            Box(modifier = Modifier.width(136.dp)) {
                VideoThumbnail(url = video.thumbnail, modifier = Modifier.fillMaxSize())
                if (video.isDownloaded) {
                    Icon(Icons.Default.DownloadDone, null, tint = Color(0xFF1DB954), modifier = Modifier.align(Alignment.TopEnd).padding(4.dp).size(16.dp))
                }
            }
            Column(modifier = Modifier.weight(1f).padding(horizontal = 12.dp, vertical = 10.dp), verticalArrangement = Arrangement.SpaceBetween) {
                Text(video.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (video.duration > 0L) Text(formatTime(video.duration), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    if (video.size > 0L) Text(formatBytes(video.size), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                if (video.downloadProgress > 0f && video.downloadProgress < 1f) {
                    LinearProgressIndicator(progress = { video.downloadProgress }, modifier = Modifier.fillMaxWidth().height(2.dp))
                }
            }
            IconButton(onClick = onDownloadClick, modifier = Modifier.align(Alignment.CenterVertically)) {
                Icon(
                    when {
                        video.isDownloaded -> Icons.Default.DownloadDone
                        video.downloadProgress > 0f -> Icons.Default.Downloading
                        else -> Icons.Default.Download
                    },
                    "Download",
                    tint = if (video.isDownloaded) Color(0xFF1DB954) else MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
fun VideoThumbnail(url: String?, modifier: Modifier = Modifier) {
    SubcomposeAsyncImage(
        model = url,
        contentDescription = null,
        modifier = modifier.clip(RoundedCornerShape(0.dp)),
        contentScale = ContentScale.Crop,
        loading = {
            Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surfaceVariant), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
            }
        },
        error = {
            Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surfaceVariant), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.BrokenImage, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(28.dp))
            }
        }
    )
}

private fun formatBytes(bytes: Long): String = when {
    bytes >= 1_073_741_824L -> "%.1f GB".format(bytes / 1_073_741_824.0)
    bytes >= 1_048_576L     -> "%.1f MB".format(bytes / 1_048_576.0)
    bytes >= 1024L          -> "%.1f KB".format(bytes / 1024.0)
    else                    -> "$bytes B"
}

private fun formatTime(ms: Long): String {
    if (ms <= 0) return ""
    val totalSeconds = ms / 1000
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    return if (hours > 0) "%d:%02d:%02d".format(hours, minutes, seconds)
    else "%02d:%02d".format(minutes, seconds)
}
