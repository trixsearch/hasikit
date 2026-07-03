package com.trixsearch.hasikit.ui.screens.player

import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.PlayerView
import com.trixsearch.hasikit.domain.model.WatchProgress
import com.trixsearch.hasikit.domain.repository.VideoRepository
import com.trixsearch.hasikit.player.HasikitPlayer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val repository: VideoRepository
) : ViewModel() {
    fun saveProgress(videoId: String, position: Long, duration: Long) {
        viewModelScope.launch {
            repository.saveWatchProgress(
                WatchProgress(
                    videoId = videoId,
                    lastPosition = position,
                    duration = duration,
                    lastWatchedAt = System.currentTimeMillis()
                )
            )
        }
    }

    suspend fun getInitialPosition(videoId: String): Long =
        repository.getWatchProgress(videoId)?.lastPosition ?: 0L
}

@OptIn(UnstableApi::class)
@Composable
fun PlayerScreen(
    videoId: String,
    player: HasikitPlayer,
    videoUrl: String,
    localPath: String?,
    onBack: () -> Unit,
    viewModel: PlayerViewModel = hiltViewModel()
) {
    val isPlaying by player.isPlaying.collectAsState()
    val isBuffering by player.isBuffering.collectAsState()
    val error by player.error.collectAsState()

    var currentPosition by remember { mutableLongStateOf(0L) }
    var duration by remember { mutableLongStateOf(0L) }
    var showControls by remember { mutableStateOf(true) }

    val playUrl = if (!localPath.isNullOrBlank()) "file://$localPath" else videoUrl

    LaunchedEffect(videoId) {
        val startPos = viewModel.getInitialPosition(videoId)
        player.initialize()
        player.playVideo(playUrl, startPos)
    }

    DisposableEffect(videoId) {
        onDispose {
            viewModel.saveProgress(videoId, player.getCurrentPosition(), player.getDuration())
            player.release()
        }
    }

    LaunchedEffect(isPlaying) {
        while (isPlaying) {
            delay(5000)
            val dur = player.getDuration()
            if (dur > 0) viewModel.saveProgress(videoId, player.getCurrentPosition(), dur)
        }
    }

    LaunchedEffect(Unit) {
        while (true) {
            currentPosition = player.getCurrentPosition()
            duration = player.getDuration()
            delay(500)
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    this.player = player.getPlayerInstance()
                    useController = false
                }
            },
            modifier = Modifier.fillMaxSize().clickable { showControls = !showControls }
        )

        if (isBuffering) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = Color.White)
        }

        if (error != null) {
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(Icons.Default.Error, contentDescription = null, tint = Color.Red, modifier = Modifier.size(48.dp))
                Spacer(modifier = Modifier.height(8.dp))
                Text(error!!.userMessage, color = Color.White, style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(4.dp))
                if (error!!.httpStatusCode != null) {
                    Text("HTTP ${error!!.httpStatusCode}", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                }
                Text(error!!.message, color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                Spacer(modifier = Modifier.height(12.dp))
                Button(onClick = { player.playVideo(playUrl, currentPosition) }) {
                    Text("Retry")
                }
            }
        }

        if (showControls) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f))
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.align(Alignment.TopStart).padding(16.dp)
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }

                Row(
                    modifier = Modifier.align(Alignment.Center),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { player.seekTo(currentPosition - 10000) }) {
                        Icon(Icons.Default.Replay10, contentDescription = "Rewind", tint = Color.White, modifier = Modifier.size(48.dp))
                    }
                    IconButton(
                        onClick = { if (isPlaying) player.pause() else player.resume() },
                        modifier = Modifier.size(64.dp)
                    ) {
                        Icon(
                            if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = "Play/Pause",
                            tint = Color.White,
                            modifier = Modifier.size(64.dp)
                        )
                    }
                    IconButton(onClick = { player.seekTo(currentPosition + 10000) }) {
                        Icon(Icons.Default.Forward10, contentDescription = "Forward", tint = Color.White, modifier = Modifier.size(48.dp))
                    }
                }

                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 32.dp, start = 16.dp, end = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(formatTime(currentPosition), color = Color.White)
                        Text(formatTime(duration), color = Color.White)
                    }
                    Slider(
                        value = currentPosition.toFloat().coerceIn(0f, duration.toFloat().coerceAtLeast(1f)),
                        onValueChange = { player.seekTo(it.toLong()) },
                        valueRange = 0f..duration.toFloat().coerceAtLeast(1f),
                        colors = SliderDefaults.colors(
                            thumbColor = Color.Red,
                            activeTrackColor = Color.Red,
                            inactiveTrackColor = Color.Gray
                        )
                    )
                }
            }
        }
    }
}

private fun formatTime(ms: Long): String {
    if (ms <= 0) return "00:00"
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%02d:%02d".format(minutes, seconds)
}
