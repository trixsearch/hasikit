package com.trixsearch.hasikit.ui.screens.library

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.DownloadDone
import androidx.compose.material.icons.filled.Downloading
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.trixsearch.hasikit.ui.components.FastScrollerBox
import com.trixsearch.hasikit.ui.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "LibraryScreen"

// Library sort options
enum class LibrarySortOrder(val label: String) {
    NAME_AZ("Name A\u2013Z"),
    NAME_ZA("Name Z\u2013A"),
    NEWEST_FIRST("Newest First"),
    OLDEST_FIRST("Oldest First"),
    LARGEST_SIZE("Largest Size"),
    SMALLEST_SIZE("Smallest Size"),
    LONGEST_DURATION("Longest Duration"),
    SHORTEST_DURATION("Shortest Duration"),
    CHANNEL_NAME("Channel Name"),
    DOWNLOADED("Downloaded"),
    DOWNLOADING("Downloading"),
    PAUSED("Paused")
}

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

    // Bug fix #11: Expose favorites from Room so Library screen can display them
    val favorites: StateFlow<List<com.trixsearch.hasikit.data.local.entities.FavoriteEntity>> =
        repository.getAllFavorites()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Bug fix #11: Expose watch later from Room so Library screen can display them
    val watchLater: StateFlow<List<com.trixsearch.hasikit.data.local.entities.WatchLaterEntity>> =
        repository.getAllWatchLater()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Bug fix #11: Expose watch history from Room so Library screen can display them
    val watchHistory: StateFlow<List<com.trixsearch.hasikit.data.local.entities.WatchHistoryEntity>> =
        repository.getAllWatchHistory()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun removeFavorite(videoId: String) {
        viewModelScope.launch { repository.removeFavorite(videoId) }
    }

    fun removeFromWatchLater(videoId: String) {
        viewModelScope.launch { repository.removeFromWatchLater(videoId) }
    }

    fun removeFromHistory(videoId: String) {
        viewModelScope.launch { repository.removeFromWatchHistory(videoId) }
    }

    fun deleteDownload(videoId: String) {
        Log.d(TAG, "deleteDownload videoId=$videoId")
        downloadManager.deleteDownload(videoId)
    }

    fun retryDownload(video: Video) {
        Log.d(TAG, "retryDownload videoId=${video.id}")
        downloadManager.retryDownload(video)
    }

    fun pauseDownload(videoId: String) {
        Log.d(TAG, "pauseDownload videoId=$videoId")
        downloadManager.pauseDownload(videoId)
    }

    fun resumeDownload(video: Video) {
        Log.d(TAG, "resumeDownload videoId=${video.id}")
        downloadManager.resumeDownload(video)
    }
}

@Composable
fun LibraryScreen(
    navController: NavController,
    viewModel: LibraryViewModel = hiltViewModel()
) {
    val downloadedItems by viewModel.downloadedItems.collectAsState()
    val activeDownloads by viewModel.activeDownloads.collectAsState()
    // Bug fix #11: collect favorites, watch later, history from Room
    val favorites by viewModel.favorites.collectAsState()
    val watchLater by viewModel.watchLater.collectAsState()
    val watchHistory by viewModel.watchHistory.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var sortOrder by remember { mutableStateOf(LibrarySortOrder.NEWEST_FIRST) }
    var showSortMenu by remember { mutableStateOf(false) }
    var selectionMode by remember { mutableStateOf(false) }
    var selectedIds by remember { mutableStateOf(setOf<String>()) }
    // Bug fix #11: tab selection — 0=Downloads, 1=Favorites, 2=Watch Later, 3=History
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Downloads", "Favorites", "Watch Later", "History")

    // Apply sort to downloaded items
    val sortedDownloaded = remember(downloadedItems, sortOrder) {
        when (sortOrder) {
            LibrarySortOrder.NAME_AZ -> downloadedItems.sortedBy { it.video.title.lowercase() }
            LibrarySortOrder.NAME_ZA -> downloadedItems.sortedByDescending { it.video.title.lowercase() }
            LibrarySortOrder.NEWEST_FIRST -> downloadedItems
            LibrarySortOrder.OLDEST_FIRST -> downloadedItems.reversed()
            LibrarySortOrder.LARGEST_SIZE -> downloadedItems.sortedByDescending { it.video.size }
            LibrarySortOrder.SMALLEST_SIZE -> downloadedItems.sortedBy { it.video.size }
            LibrarySortOrder.LONGEST_DURATION -> downloadedItems.sortedByDescending { it.video.duration }
            LibrarySortOrder.SHORTEST_DURATION -> downloadedItems.sortedBy { it.video.duration }
            LibrarySortOrder.CHANNEL_NAME -> downloadedItems.sortedBy { it.video.sourceLabel.lowercase() }
            LibrarySortOrder.DOWNLOADED -> downloadedItems.filter { it.task?.state == DownloadState.COMPLETED || it.video.isDownloaded }
            LibrarySortOrder.DOWNLOADING -> downloadedItems.filter { it.task?.state == DownloadState.DOWNLOADING }
            LibrarySortOrder.PAUSED -> downloadedItems.filter { it.task?.state == DownloadState.PAUSED }
        }
    }

    val filteredDownloaded = remember(sortedDownloaded, searchQuery) {
        if (searchQuery.isBlank()) sortedDownloaded
        else sortedDownloaded.filter { item ->
            item.video.title.contains(searchQuery, ignoreCase = true) ||
                item.video.localPath?.contains(searchQuery, ignoreCase = true) == true
        }
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .padding(horizontal = 16.dp, vertical = 0.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.VideoLibrary,
                        null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Library", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.weight(1f))
                    // Sort button
                    Box {
                        IconButton(onClick = { showSortMenu = true }) {
                            Icon(Icons.Default.Sort, "Sort", tint = MaterialTheme.colorScheme.primary)
                        }
                        DropdownMenu(expanded = showSortMenu, onDismissRequest = { showSortMenu = false }) {
                            Text(
                                "Sort by",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                            )
                            LibrarySortOrder.entries.forEach { order ->
                                // Explicitly typed composable lambda to fix type inference
                                val checkIcon: (@Composable () -> Unit)? = if (sortOrder == order) {
                                    @Composable { Icon(Icons.Default.Check, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp)) }
                                } else null
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            order.label,
                                            fontWeight = if (sortOrder == order) FontWeight.Bold else FontWeight.Normal,
                                            color = if (sortOrder == order) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                        )
                                    },
                                    leadingIcon = checkIcon,
                                    onClick = { sortOrder = order; showSortMenu = false }
                                )
                            }
                        }
                    }
                    // Bulk select toggle — single tap immediately selects all items without a second action
                    IconButton(onClick = {
                        if (selectionMode && selectedIds.isNotEmpty()) {
                            // Already in selection mode with items selected — exit and clear
                            selectionMode = false
                            selectedIds = emptySet()
                        } else if (!selectionMode) {
                            // FIX: single tap enters selection mode AND selects all items immediately
                            selectionMode = true
                            selectedIds = filteredDownloaded.map { it.video.id }.toSet()
                        } else {
                            // In selection mode but nothing selected — exit
                            selectionMode = false
                            selectedIds = emptySet()
                        }
                    }) {
                        Icon(
                            if (selectionMode) Icons.Default.CheckCircle else Icons.Default.CheckBoxOutlineBlank,
                            "Select",
                            tint = if (selectionMode) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                // Bulk action bar — shown when selection mode is active
                if (selectionMode) {
                    val selCount = selectedIds.size
                    val allSelected = filteredDownloaded.isNotEmpty() && filteredDownloaded.all { it.video.id in selectedIds }
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Select All button — immediately selects all visible items on tap
                        TextButton(onClick = {
                            selectedIds = if (allSelected) emptySet()
                            else filteredDownloaded.map { it.video.id }.toSet()
                        }) {
                            Text(if (allSelected) "Deselect All" else "Select All", style = MaterialTheme.typography.labelMedium)
                        }
                        if (selCount > 0) {
                            Text("$selCount selected", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Spacer(Modifier.weight(1f))
                        if (selCount == 1) {
                            // Single selection actions: Play, Resume, Pause, Delete
                            val singleItem = filteredDownloaded.firstOrNull { it.video.id in selectedIds }
                            IconButton(onClick = {
                                singleItem?.let { navController.navigate(Screen.Player.createRoute(it.video.id)) }
                                selectionMode = false; selectedIds = emptySet()
                            }) { Icon(Icons.Default.PlayArrow, "Play", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(22.dp)) }
                            IconButton(onClick = {
                                singleItem?.let { viewModel.resumeDownload(it.video) }
                                selectionMode = false; selectedIds = emptySet()
                            }) { Icon(Icons.Default.PlayCircle, "Resume", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(22.dp)) }
                            IconButton(onClick = {
                                singleItem?.let { viewModel.pauseDownload(it.video.id) }
                                selectionMode = false; selectedIds = emptySet()
                            }) { Icon(Icons.Default.Pause, "Pause", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(22.dp)) }
                            IconButton(onClick = {
                                singleItem?.let { viewModel.deleteDownload(it.video.id) }
                                selectionMode = false; selectedIds = emptySet()
                            }) { Icon(Icons.Default.Delete, "Delete", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(22.dp)) }
                        } else if (selCount > 1) {
                            // Multi selection actions: Play Queue, Pause Downloads, Resume Downloads, Delete Selected
                            IconButton(onClick = {
                                val first = filteredDownloaded.firstOrNull { it.video.id in selectedIds }
                                first?.let { navController.navigate(Screen.Player.createRoute(it.video.id)) }
                                selectionMode = false; selectedIds = emptySet()
                            }) { Icon(Icons.Default.PlayArrow, "Play Queue", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(22.dp)) }
                            IconButton(onClick = {
                                selectedIds.forEach { id -> viewModel.pauseDownload(id) }
                                selectionMode = false; selectedIds = emptySet()
                            }) { Icon(Icons.Default.Pause, "Pause Downloads", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(22.dp)) }
                            IconButton(onClick = {
                                selectedIds.forEach { id ->
                                    filteredDownloaded.find { it.video.id == id }?.let { viewModel.resumeDownload(it.video) }
                                }
                                selectionMode = false; selectedIds = emptySet()
                            }) { Icon(Icons.Default.PlayCircle, "Resume Downloads", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(22.dp)) }
                            IconButton(onClick = {
                                selectedIds.forEach { id -> viewModel.deleteDownload(id) }
                                selectionMode = false; selectedIds = emptySet()
                            }) { Icon(Icons.Default.Delete, "Delete Selected", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(22.dp)) }
                        }
                    }
                }
                if (downloadedItems.isNotEmpty()) {
                    Spacer(Modifier.height(10.dp))
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search downloads\u2026", style = MaterialTheme.typography.bodyMedium) },
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
                // Bug fix #11: tab row for Downloads / Favorites / Watch Later / History
                Spacer(Modifier.height(8.dp))
                androidx.compose.material3.ScrollableTabRow(
                    selectedTabIndex = selectedTab,
                    edgePadding = 0.dp
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = { Text(title, style = MaterialTheme.typography.labelMedium) }
                        )
                    }
                }
            }
        }
    ) { padding ->
        // Bug fix #11: show content based on selected tab
        when (selectedTab) {
            0 -> {
                // Downloads tab — existing downloads + active downloads list
                if (downloadedItems.isEmpty() && activeDownloads.isEmpty()) {
                    EmptyLibraryState(modifier = Modifier.padding(padding))
                } else {
                    val libraryListState = rememberLazyListState()
                    FastScrollerBox(listState = libraryListState, modifier = Modifier.padding(padding)) {
                        LazyColumn(modifier = Modifier.fillMaxSize(), state = libraryListState, contentPadding = PaddingValues(bottom = 24.dp)) {
                            if (activeDownloads.isNotEmpty()) {
                                item { SectionLabel("Downloading", Icons.Default.Downloading) }
                                items(activeDownloads, key = { it.video.id + "_active" }) { item ->
                                    ActiveDownloadCard(item = item, onPause = { viewModel.pauseDownload(item.video.id) }, onResume = { viewModel.resumeDownload(item.video) })
                                }
                            }
                            if (filteredDownloaded.isNotEmpty()) {
                                val dlCount = filteredDownloaded.size
                                item { SectionLabel("Downloaded ($dlCount)", Icons.Default.DownloadDone) }
                                items(filteredDownloaded, key = { it.video.id }) { item ->
                                    val isSelected = item.video.id in selectedIds
                                    DownloadedVideoCard(
                                        item = item, isSelected = isSelected, selectionMode = selectionMode,
                                        onPlay = {
                                            if (selectionMode) selectedIds = if (isSelected) selectedIds - item.video.id else selectedIds + item.video.id
                                            else navController.navigate(Screen.Player.createRoute(item.video.id))
                                        },
                                        onDelete = { viewModel.deleteDownload(item.video.id) },
                                        onRedownload = { viewModel.retryDownload(item.video) },
                                        onLongPress = { selectionMode = true; selectedIds = selectedIds + item.video.id }
                                    )
                                }
                            } else if (searchQuery.isNotBlank()) {
                                item {
                                    Box(modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp), contentAlignment = Alignment.Center) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Icon(Icons.Default.SearchOff, null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                            Spacer(Modifier.height(8.dp))
                                            Text("No results for \"$searchQuery\"", color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            1 -> {
                // Bug fix #11: Favorites tab
                if (favorites.isEmpty()) {
                    Box(modifier = Modifier.padding(padding).fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.VideoLibrary, null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f))
                            Spacer(Modifier.height(8.dp))
                            Text("No favorites yet", style = MaterialTheme.typography.titleMedium)
                            Text("Long-press a video to add it to favorites", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                } else {
                    LazyColumn(modifier = Modifier.padding(padding).fillMaxSize(), contentPadding = PaddingValues(bottom = 24.dp)) {
                        item { SectionLabel("Favorites (${favorites.size})", Icons.Default.VideoLibrary) }
                        items(favorites, key = { it.videoId }) { fav ->
                            Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp), shape = RoundedCornerShape(12.dp)) {
                                Row(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                    AsyncImage(model = fav.thumbnail, contentDescription = null, modifier = Modifier.size(width = 80.dp, height = 56.dp).clip(RoundedCornerShape(8.dp)), contentScale = ContentScale.Crop)
                                    Spacer(Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(fav.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, maxLines = 2, overflow = TextOverflow.Ellipsis)
                                        Text(fav.source, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                                    }
                                    IconButton(onClick = { viewModel.removeFavorite(fav.videoId) }) {
                                        Icon(Icons.Default.Delete, "Remove", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(20.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
            2 -> {
                // Bug fix #11: Watch Later tab
                if (watchLater.isEmpty()) {
                    Box(modifier = Modifier.padding(padding).fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.VideoLibrary, null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f))
                            Spacer(Modifier.height(8.dp))
                            Text("Watch Later is empty", style = MaterialTheme.typography.titleMedium)
                            Text("Save videos to watch them later", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                } else {
                    LazyColumn(modifier = Modifier.padding(padding).fillMaxSize(), contentPadding = PaddingValues(bottom = 24.dp)) {
                        item { SectionLabel("Watch Later (${watchLater.size})", Icons.Default.VideoLibrary) }
                        items(watchLater, key = { it.videoId }) { item ->
                            Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp), shape = RoundedCornerShape(12.dp)) {
                                Row(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                    AsyncImage(model = item.thumbnail, contentDescription = null, modifier = Modifier.size(width = 80.dp, height = 56.dp).clip(RoundedCornerShape(8.dp)), contentScale = ContentScale.Crop)
                                    Spacer(Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(item.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, maxLines = 2, overflow = TextOverflow.Ellipsis)
                                        Text(item.source, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                                    }
                                    IconButton(onClick = { viewModel.removeFromWatchLater(item.videoId) }) {
                                        Icon(Icons.Default.Delete, "Remove", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(20.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
            3 -> {
                // Bug fix #11: History tab
                if (watchHistory.isEmpty()) {
                    Box(modifier = Modifier.padding(padding).fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.VideoLibrary, null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f))
                            Spacer(Modifier.height(8.dp))
                            Text("No watch history", style = MaterialTheme.typography.titleMedium)
                            Text("Videos you watch will appear here", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                } else {
                    LazyColumn(modifier = Modifier.padding(padding).fillMaxSize(), contentPadding = PaddingValues(bottom = 24.dp)) {
                        item { SectionLabel("History (${watchHistory.size})", Icons.Default.VideoLibrary) }
                        items(watchHistory, key = { it.videoId }) { item ->
                            Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp), shape = RoundedCornerShape(12.dp)) {
                                Row(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                    AsyncImage(model = item.thumbnail, contentDescription = null, modifier = Modifier.size(width = 80.dp, height = 56.dp).clip(RoundedCornerShape(8.dp)), contentScale = ContentScale.Crop)
                                    Spacer(Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(item.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, maxLines = 2, overflow = TextOverflow.Ellipsis)
                                        Text(item.source, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                                    }
                                    IconButton(onClick = { viewModel.removeFromHistory(item.videoId) }) {
                                        Icon(Icons.Default.Delete, "Remove", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(20.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionLabel(title: String, icon: ImageVector) {
    Row(
        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(6.dp))
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun EmptyLibraryState(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                Icons.Default.VideoLibrary,
                contentDescription = null,
                modifier = Modifier.size(72.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
            )
            Spacer(Modifier.height(4.dp))
            Text("No downloads yet", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
            Text(
                "Download videos from Home to watch offline",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun ActiveDownloadCard(item: LibraryItem, onPause: () -> Unit, onResume: () -> Unit) {
    val isPaused = item.task?.state == DownloadState.PAUSED
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.height(80.dp).padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = item.video.thumbnail,
                contentDescription = null,
                modifier = Modifier.size(width = 80.dp, height = 56.dp).clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    item.video.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(6.dp))
                val progress = item.task?.progress ?: 0f
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth().height(4.dp),
                    color = if (isPaused) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
                Spacer(Modifier.height(4.dp))
                val progressPct = (progress * 100).toInt()
                val statusText = if (isPaused) "Paused" else "Downloading\u2026"
                Text(
                    "$progressPct% \u2014 $statusText",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(Modifier.width(8.dp))
            IconButton(onClick = { if (isPaused) onResume() else onPause() }, modifier = Modifier.size(40.dp)) {
                Icon(
                    if (isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                    contentDescription = if (isPaused) "Resume" else "Pause",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DownloadedVideoCard(
    item: LibraryItem,
    onPlay: () -> Unit,
    onDelete: () -> Unit,
    onRedownload: () -> Unit,
    isSelected: Boolean = false,
    selectionMode: Boolean = false,
    onLongPress: (() -> Unit)? = null
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    val watchPct = remember(item.watchProgress) {
        val wp = item.watchProgress
        if (wp != null && wp.duration > 0) wp.lastPosition.toFloat() / wp.duration else 0f
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .combinedClickable(onClick = onPlay, onLongClick = { onLongPress?.invoke() }),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        // Highlight selected items in bulk mode
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().height(88.dp).padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(width = 96.dp, height = 64.dp).clip(RoundedCornerShape(8.dp))) {
                AsyncImage(
                    model = item.video.thumbnail,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Surface(
                    modifier = Modifier.align(Alignment.TopEnd).padding(3.dp),
                    shape = RoundedCornerShape(4.dp),
                    color = Color(0xFF1DB954)
                ) {
                    Icon(Icons.Default.DownloadDone, null, tint = Color.White, modifier = Modifier.padding(3.dp).size(10.dp))
                }
                LinearProgressIndicator(
                    progress = { watchPct },
                    modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth().height(3.dp),
                    color = Color.Red,
                    trackColor = Color.Gray.copy(alpha = 0.3f)
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.Center) {
                Text(
                    item.video.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Storage, null, modifier = Modifier.size(12.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.width(3.dp))
                    Text(formatBytes(item.video.size), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    item.watchProgress?.let { wp ->
                        if (wp.duration > 0) {
                            Text(
                                "  \u2022  ${formatTime(wp.lastPosition)}/${formatTime(wp.duration)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                IconButton(onClick = onPlay, modifier = Modifier.size(40.dp)) {
                    Icon(Icons.Default.PlayArrow, "Play", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
                }
                IconButton(onClick = { showDeleteDialog = true }, modifier = Modifier.size(40.dp)) {
                    Icon(Icons.Default.Delete, "Delete", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(20.dp))
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            icon = { Icon(Icons.Default.DeleteForever, null, tint = MaterialTheme.colorScheme.error) },
            title = { Text("Delete Download") },
            text = { Text("Remove \"${item.video.title}\" from your device?") },
            confirmButton = {
                Button(
                    onClick = { onDelete(); showDeleteDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text("Delete") }
            },
            dismissButton = { TextButton(onClick = { showDeleteDialog = false }) { Text("Cancel") } }
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
