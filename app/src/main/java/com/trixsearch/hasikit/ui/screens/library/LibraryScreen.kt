package com.trixsearch.hasikit.ui.screens.library

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.trixsearch.hasikit.ui.navigation.Screen
import com.trixsearch.hasikit.ui.screens.home.HomeViewModel
import com.trixsearch.hasikit.ui.screens.home.VideoCard

@Composable
fun LibraryScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val videos by viewModel.videos.collectAsState()
    val downloadedVideos = videos.filter { it.isDownloaded }

    Scaffold(
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(title = { Text("Library") })
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (downloadedVideos.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                    Text("No downloads yet")
                }
            } else {
                Text(
                    "Downloads",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(16.dp)
                )
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(downloadedVideos) { video ->
                        VideoCard(
                            video = video,
                            onClick = { navController.navigate(Screen.Player.createRoute(video.id)) },
                            onDownloadClick = { viewModel.toggleDownload(video.id) }
                        )
                    }
                }
            }
        }
    }
}
