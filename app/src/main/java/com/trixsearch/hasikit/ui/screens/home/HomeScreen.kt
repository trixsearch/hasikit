package com.trixsearch.hasikit.ui.screens.home

import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.SubcomposeAsyncImage
import com.trixsearch.hasikit.domain.model.DownloadState
import com.trixsearch.hasikit.domain.model.Video
import com.trixsearch.hasikit.domain.model.WatchProgress
import com.trixsearch.hasikit.ui.navigation.Screen
import com.trixsearch.hasikit.ui.theme.HasikitTheme

@Composable
private fun LazyListState.OnNearBottom(threshold: Int, onNearBottom: () -> Unit) {
    val shouldLoad = remember {
        derivedStateOf {
            val lastVisible = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            val total = layoutInfo.totalItemsCount
            // Infinite Scroll Prefetch Threshold — fetch next page when this many items remain
            total > 0 && lastVisible >= total - threshold
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
    val noAccessMessage by viewModel.noAccessMessage.collectAsState()
    val availableSources by viewModel.availableSources.collectAsState()
    val selectedSourceFilter by viewModel.selectedSourceFilter.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    // Use ViewModel's prefetch threshold so scroll trigger matches pagination config
    listState.OnNearBottom(threshold = viewModel.prefetchThreshold) { viewModel.loadMore() }

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
                    // Status Bar Top Padding — windowInsetsPadding handles the status bar height automatically
                    .windowInsetsPadding(WindowInsets.statusBars)
                    // Title Top Padding — set to 4.dp top to keep title close to status bar
                    .padding(horizontal = 16.dp, vertical = 4.dp)
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
                // Channel filter chips — only shown when multiple sources are available
                if (availableSources.size > 1) {
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = selectedSourceFilter == null,
                            onClick = { viewModel.setSourceFilter(null) },
                            label = { Text("All Sources", style = MaterialTheme.typography.labelMedium) }
                        )
                        availableSources.forEach { source ->
                            FilterChip(
                                selected = selectedSourceFilter == source.displayName,
                                onClick = { viewModel.setSourceFilter(source.displayName) },
                                label = { Text(source.displayName, style = MaterialTheme.typography.labelMedium) }
                            )
                        }
                    }
                }
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
                                Text("Loading content…", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                    return@LazyColumn
                }

                // No access state
                if (noAccessMessage != null && videos.isEmpty() && !isLoading) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.Lock, null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(48.dp))
                                Spacer(Modifier.height(12.dp))
                                Text(noAccessMessage!!, color = MaterialTheme.colorScheme.onSurface, style = MaterialTheme.typography.bodyMedium, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
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
                            // Pass download state and controls to search result cards
                            val dlTask = viewModel.downloadTasks.collectAsState().value[video.id]
                            HorizontalVideoCard(
                                video = video,
                                onClick = { navController.navigate(Screen.Player.createRoute(video.id)) },
                                onDownloadClick = { if (!video.isDownloaded) viewModel.startDownload(video) },
                                onPauseDownload = { viewModel.pauseDownload(video.id) },
                                onResumeDownload = { viewModel.resumeDownload(video.id) },
                                onDeleteDownload = { viewModel.deleteDownload(video.id) },
                                downloadState = dlTask?.state
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
                items(videos, key = { "all_${it.id}" }) { video ->
                    // Pass download state and controls to all-videos cards
                    val dlTask = viewModel.downloadTasks.collectAsState().value[video.id]
                    HorizontalVideoCard(
                        video = video,
                        onClick = { navController.navigate(Screen.Player.createRoute(video.id)) },
                        onDownloadClick = { if (!video.isDownloaded) viewModel.startDownload(video) },
                        onPauseDownload = { viewModel.pauseDownload(video.id) },
                        onResumeDownload = { viewModel.resumeDownload(video.id) },
                        onDeleteDownload = { viewModel.deleteDownload(video.id) },
                        downloadState = dlTask?.state
                    )
                }

                // Bottom loader — shown while fetching next page, removed when complete
                if (isLoadingMore) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                            Spacer(Modifier.width(10.dp))
                            Text("Loading more videos…", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
            // Pass localPath for video frame fallback when Telegram thumbnail is absent
            VideoThumbnail(url = video.thumbnail, localVideoPath = video.localPath, modifier = Modifier.fillMaxWidth().height(95.dp))
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
            // Pass localPath for video frame fallback when Telegram thumbnail is absent
            VideoThumbnail(url = video.thumbnail, localVideoPath = video.localPath, modifier = Modifier.fillMaxWidth().height(108.dp))
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
            // Pass localPath for video frame fallback when Telegram thumbnail is absent
            VideoThumbnail(url = video.thumbnail, localVideoPath = video.localPath, modifier = Modifier.fillMaxWidth().height(95.dp))
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

// Updated HorizontalVideoCard to show actual download state and expose pause/resume/delete actions
@Composable
fun HorizontalVideoCard(
    video: Video,
    onClick: () -> Unit,
    onDownloadClick: () -> Unit,
    // Added download control callbacks for home screen actions
    onPauseDownload: (() -> Unit)? = null,
    onResumeDownload: (() -> Unit)? = null,
    onDeleteDownload: (() -> Unit)? = null,
    // Current download state for showing correct label/actions
    downloadState: DownloadState? = null
) {
    val context = LocalContext.current
    var showDownloadMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp).clickable {
            // Streamability logic — non-streamable documents must be downloaded before playback
            if (!video.isStreamable && !video.isDownloaded) {
                android.widget.Toast.makeText(context, "Video must be downloaded before playback.", android.widget.Toast.LENGTH_SHORT).show()
                onDownloadClick()
            } else {
                onClick()
            }
        },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(modifier = Modifier.height(82.dp)) {
            Box(modifier = Modifier.width(136.dp)) {
                // Pass localPath for video frame fallback when Telegram thumbnail is absent
                VideoThumbnail(url = video.thumbnail, localVideoPath = video.localPath, modifier = Modifier.fillMaxSize())
                // Streamability overlay — only show play icon for streamable videos
                if (video.isStreamable) {
                    Box(
                        modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.PlayCircle, null, tint = Color.White.copy(alpha = 0.75f), modifier = Modifier.size(28.dp))
                    }
                }
                if (video.isDownloaded) {
                    Icon(Icons.Default.DownloadDone, null, tint = Color(0xFF1DB954), modifier = Modifier.align(Alignment.TopEnd).padding(4.dp).size(16.dp))
                }
            }
            Column(modifier = Modifier.weight(1f).padding(horizontal = 12.dp, vertical = 10.dp), verticalArrangement = Arrangement.SpaceBetween) {
                Text(video.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (video.sourceLabel.isNotBlank()) Text(video.sourceLabel, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, maxLines = 1)
                    if (video.duration > 0L) Text(formatTime(video.duration), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    if (video.size > 0L) Text(formatBytes(video.size), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                // Show download state label and progress bar
                when (downloadState) {
                    DownloadState.DOWNLOADING -> {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            LinearProgressIndicator(progress = { video.downloadProgress }, modifier = Modifier.weight(1f).height(2.dp))
                            Text("${(video.downloadProgress * 100).toInt()}%", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, fontSize = 9.sp)
                        }
                    }
                    DownloadState.PAUSED -> {
                        Text("Paused", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    DownloadState.COMPLETED -> {
                        Text("Downloaded", style = MaterialTheme.typography.labelSmall, color = Color(0xFF1DB954))
                    }
                    DownloadState.FAILED -> {
                        Text("Failed", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.error)
                    }
                    else -> {
                        if (video.downloadProgress > 0f && video.downloadProgress < 1f) {
                            LinearProgressIndicator(progress = { video.downloadProgress }, modifier = Modifier.fillMaxWidth().height(2.dp))
                        }
                    }
                }
            }
            // Download action button — shows menu when download is active/paused/completed
            Box(modifier = Modifier.align(Alignment.CenterVertically)) {
                IconButton(onClick = {
                    when (downloadState) {
                        DownloadState.DOWNLOADING, DownloadState.PAUSED, DownloadState.COMPLETED -> showDownloadMenu = true
                        else -> onDownloadClick()
                    }
                }) {
                    Icon(
                        when (downloadState) {
                            DownloadState.COMPLETED -> Icons.Default.DownloadDone
                            DownloadState.DOWNLOADING -> Icons.Default.Downloading
                            DownloadState.PAUSED -> Icons.Default.PauseCircle
                            DownloadState.FAILED -> Icons.Default.ErrorOutline
                            else -> if (video.isDownloaded) Icons.Default.DownloadDone else Icons.Default.Download
                        },
                        "Download",
                        tint = when (downloadState) {
                            DownloadState.COMPLETED -> Color(0xFF1DB954)
                            DownloadState.FAILED -> MaterialTheme.colorScheme.error
                            else -> if (video.isDownloaded) Color(0xFF1DB954) else MaterialTheme.colorScheme.onSurface
                        }
                    )
                }
                // Download control dropdown menu — pause, resume, delete
                DropdownMenu(expanded = showDownloadMenu, onDismissRequest = { showDownloadMenu = false }) {
                    when (downloadState) {
                        DownloadState.DOWNLOADING -> {
                            DropdownMenuItem(
                                text = { Text("Pause") },
                                leadingIcon = { Icon(Icons.Default.Pause, null, modifier = Modifier.size(18.dp)) },
                                onClick = { onPauseDownload?.invoke(); showDownloadMenu = false }
                            )
                        }
                        DownloadState.PAUSED -> {
                            DropdownMenuItem(
                                text = { Text("Resume") },
                                leadingIcon = { Icon(Icons.Default.PlayArrow, null, modifier = Modifier.size(18.dp)) },
                                onClick = { onResumeDownload?.invoke(); showDownloadMenu = false }
                            )
                        }
                        else -> {}
                    }
                    DropdownMenuItem(
                        text = { Text("Delete", color = MaterialTheme.colorScheme.error) },
                        leadingIcon = { Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp)) },
                        onClick = { onDeleteDownload?.invoke(); showDownloadMenu = false }
                    )
                }
            }
        }
    }
}

// Thumbnail fallback: try Telegram path first, then generate from local video file, then Hasikit logo
@Composable
fun VideoThumbnail(url: String?, localVideoPath: String? = null, modifier: Modifier = Modifier) {
    // TDLib returns raw file paths — prefix with file:// for Coil
    val model = remember(url, localVideoPath) {
        when {
            !url.isNullOrBlank() -> when {
                url.startsWith("file://") || url.startsWith("content://") || url.startsWith("http") -> url
                url.startsWith("/") -> "file://$url"
                else -> url
            }
            // No Telegram thumbnail — generate from local video file if available
            !localVideoPath.isNullOrBlank() -> {
                try {
                    val retriever = MediaMetadataRetriever()
                    val path = if (localVideoPath.startsWith("file://")) localVideoPath.removePrefix("file://") else localVideoPath
                    retriever.setDataSource(path)
                    // Extract frame at 10% of duration for a representative thumbnail
                    val durationStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                    val durationMs = durationStr?.toLongOrNull() ?: 0L
                    val frameTimeUs = (durationMs * 0.1 * 1000).toLong().coerceAtLeast(0L)
                    retriever.getFrameAtTime(frameTimeUs, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
                } catch (e: Exception) {
                    null
                }
            }
            else -> null
        }
    }
    SubcomposeAsyncImage(
        model = model,
        contentDescription = null,
        modifier = modifier.clip(RoundedCornerShape(0.dp)),
        contentScale = ContentScale.Crop,
        loading = {
            Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surfaceVariant), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
            }
        },
        error = {
            // Hasikit Fallback Thumbnail — show Hasikit logo instead of blank black card
            Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surfaceVariant), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                    Icon(Icons.Default.PlayCircle, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(32.dp))
                    Spacer(Modifier.height(4.dp))
                    Text("Hasikit", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                }
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

// ─── Preview Helpers ────────────────────────────────────────────────────────

private fun previewVideo(
    id: String = "1",
    title: String = "Sample Movie 1080p BluRay",
    isDownloaded: Boolean = false,
    downloadProgress: Float = 0f,
    sourceLabel: String = "Hasikit"
) = Video(
    id = id,
    title = title,
    thumbnail = null,
    videoUrl = "",
    duration = 5_820_000L,
    size = 1_572_864_000L,
    isDownloaded = isDownloaded,
    downloadProgress = downloadProgress,
    sourceLabel = sourceLabel
)

private fun previewProgress(videoId: String = "1", pct: Float = 0.4f) = WatchProgress(
    videoId = videoId,
    lastPosition = (5_820_000L * pct).toLong(),
    duration = 5_820_000L,
    lastWatchedAt = System.currentTimeMillis()
)

// ─── Card Previews ───────────────────────────────────────────────────────────

@Preview(name = "HorizontalVideoCard", showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun PreviewHorizontalVideoCard() {
    HasikitTheme {
        Column {
            HorizontalVideoCard(video = previewVideo(), onClick = {}, onDownloadClick = {})
            HorizontalVideoCard(video = previewVideo(id = "2", title = "Another Great Film", isDownloaded = true), onClick = {}, onDownloadClick = {})
            HorizontalVideoCard(video = previewVideo(id = "3", title = "Downloading Now…", downloadProgress = 0.6f), onClick = {}, onDownloadClick = {})
        }
    }
}

@Preview(name = "ContinueWatchingCard", showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun PreviewContinueWatchingCard() {
    HasikitTheme {
        LazyRow(contentPadding = PaddingValues(12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(listOf(
                previewVideo(id = "1", title = "Inception") to previewProgress("1", 0.3f),
                previewVideo(id = "2", title = "Interstellar") to previewProgress("2", 0.75f)
            )) { (video, progress) ->
                ContinueWatchingCard(video, progress, onClick = {})
            }
        }
    }
}

@Preview(name = "RecentCard", showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun PreviewRecentCard() {
    HasikitTheme {
        LazyRow(contentPadding = PaddingValues(12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(listOf(
                previewVideo(id = "1", title = "The Dark Knight"),
                previewVideo(id = "2", title = "Avengers: Endgame")
            )) { video ->
                RecentCard(video, onClick = {})
            }
        }
    }
}

@Preview(name = "DownloadedCard", showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun PreviewDownloadedCard() {
    HasikitTheme {
        LazyRow(contentPadding = PaddingValues(12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(listOf(
                previewVideo(id = "1", title = "Saved Movie", isDownloaded = true),
                previewVideo(id = "2", title = "Offline Film", isDownloaded = true)
            )) { video ->
                DownloadedCard(video, onClick = {})
            }
        }
    }
}

// ─── Full Home Content Preview ───────────────────────────────────────────────

@Preview(name = "Home Content – Dark", showBackground = true, backgroundColor = 0xFF000000, widthDp = 400, heightDp = 800)
@Composable
private fun PreviewHomeContent() {
    HasikitTheme {
        val sampleVideos = (1..6).map {
            previewVideo(id = "$it", title = "Movie Title $it", sourceLabel = "Channel $it",
                isDownloaded = it % 3 == 0, downloadProgress = if (it % 4 == 0) 0.5f else 0f)
        }
        val continueWatching = sampleVideos.take(2).map { it to previewProgress(it.id) }
        val recentlyAdded = sampleVideos.take(4)
        val downloadedVideos = sampleVideos.filter { it.isDownloaded }

        LazyColumn(
            modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            item { SectionHeader("Continue Watching") }
            item {
                LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(continueWatching) { (video, progress) -> ContinueWatchingCard(video, progress, onClick = {}) }
                }
            }
            item { SectionHeader("Recently Added") }
            item {
                LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(recentlyAdded) { video -> RecentCard(video, onClick = {}) }
                }
            }
            item { SectionHeader("Downloads") }
            item {
                LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(downloadedVideos) { video -> DownloadedCard(video, onClick = {}) }
                }
            }
            item { SectionHeader("All Videos") }
            items(sampleVideos) { video ->
                HorizontalVideoCard(video = video, onClick = {}, onDownloadClick = {})
            }
        }
    }
}
