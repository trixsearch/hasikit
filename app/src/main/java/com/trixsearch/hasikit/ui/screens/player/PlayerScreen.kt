package com.trixsearch.hasikit.ui.screens.player

import android.app.Activity
import android.app.PictureInPictureParams
import android.content.Context
import android.content.pm.ActivityInfo
import android.media.AudioManager
import android.os.Build
import android.util.Log
import android.util.Rational
import android.view.WindowManager
import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.trixsearch.hasikit.domain.model.WatchProgress
import com.trixsearch.hasikit.domain.repository.VideoRepository
import com.trixsearch.hasikit.player.HasikitPlayer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.math.abs

private const val TAG = "PLAYER_DEBUG"
private const val CONTROLS_HIDE_DELAY = 3000L
private const val SEEK_INCREMENT_MS = 10_000L

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val repository: VideoRepository
) : ViewModel() {
    fun saveProgress(videoId: String, position: Long, duration: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d(TAG, "[PROGRESS_SAVE] videoId=$videoId pos=${position}ms dur=${duration}ms")
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

    suspend fun getInitialPosition(videoId: String): Long = withContext(Dispatchers.IO) {
        val pos = repository.getWatchProgress(videoId)?.lastPosition ?: 0L
        Log.d(TAG, "[PROGRESS_LOAD] videoId=$videoId resumePos=${pos}ms")
        pos
    }
}

@OptIn(UnstableApi::class)
@Composable
fun PlayerScreen(
    videoId: String,
    title: String,
    player: HasikitPlayer,
    videoUrl: String,
    localPath: String?,
    onBack: () -> Unit,
    viewModel: PlayerViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val activity = context as? Activity

    Log.d(TAG, "[SCREEN] composed videoId=$videoId title=$title localPath=$localPath")

    // Initialize synchronously before AndroidView factory
    remember(videoId) {
        Log.d(TAG, "[INIT_SYNC] videoId=$videoId")
        player.initialize()
    }

    val isPlaying by player.isPlaying.collectAsState()
    val isBuffering by player.isBuffering.collectAsState()
    val error by player.error.collectAsState()
    val playbackSpeed by player.playbackSpeed.collectAsState()
    val repeatMode by player.repeatMode.collectAsState()
    val audioTracks by player.audioTracks.collectAsState()
    val subtitleTracks by player.subtitleTracks.collectAsState()

    var currentPosition by remember { mutableLongStateOf(0L) }
    var duration by remember { mutableLongStateOf(0L) }
    var showControls by remember { mutableStateOf(true) }
    var isLocked by remember { mutableStateOf(false) }
    var showSpeedMenu by remember { mutableStateOf(false) }
    var showAudioMenu by remember { mutableStateOf(false) }
    var showSubtitleMenu by remember { mutableStateOf(false) }
    var seekFeedback by remember { mutableStateOf<String?>(null) }
    var volumeFeedback by remember { mutableStateOf<String?>(null) }
    var brightnessFeedback by remember { mutableStateOf<String?>(null) }
    var videoScale by remember { mutableFloatStateOf(1f) }
    var isLandscape by remember { mutableStateOf(true) }

    val audioManager = remember { context.getSystemService(Context.AUDIO_SERVICE) as AudioManager }
    val maxSystemVolume = remember { audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC) }

    // Track virtual volume 0-200 (0-100 = system, 101-200 = ExoPlayer boost)
    var virtualVolumePct by remember {
        val current = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        mutableIntStateOf((current * 100f / maxSystemVolume).toInt())
    }

    // Track window brightness 0-100
    var brightnessPct by remember {
        val current = activity?.window?.attributes?.screenBrightness ?: -1f
        val pct = if (current < 0f) 50 else (current * 100).toInt()
        mutableIntStateOf(pct)
    }

    val playUrl = remember(localPath, videoUrl) {
        when {
            !localPath.isNullOrBlank() -> {
                val uri = if (localPath.startsWith("file://")) localPath else "file://$localPath"
                Log.d(TAG, "[URL_RESOLVE] LOCAL localPath=$localPath playUrl=$uri")
                uri
            }
            else -> {
                Log.d(TAG, "[URL_RESOLVE] REMOTE playUrl=$videoUrl")
                videoUrl
            }
        }
    }

    val controlsScope = rememberCoroutineScope()

    fun applyVolume(pct: Int) {
        val clamped = pct.coerceIn(0, 200)
        virtualVolumePct = clamped
        val systemPct = minOf(clamped, 100)
        val newSystemVol = (systemPct * maxSystemVolume / 100f).toInt().coerceIn(0, maxSystemVolume)
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, newSystemVol, 0)
        player.setVolume(clamped)
        val icon = when {
            clamped == 0 -> "🔇"
            clamped <= 50 -> "🔈"
            clamped <= 100 -> "🔉"
            else -> "🔊"
        }
        volumeFeedback = "$icon Volume $clamped%"
    }

    fun applyBrightness(pct: Int) {
        val clamped = pct.coerceIn(1, 100)
        brightnessPct = clamped
        activity?.window?.let { window ->
            val params = window.attributes
            params.screenBrightness = clamped / 100f
            window.attributes = params
        }
        brightnessFeedback = "☀ Brightness $clamped%"
    }

    fun resetControlsTimer() {
        if (!isLocked) {
            showControls = true
            controlsScope.launch {
                delay(CONTROLS_HIDE_DELAY)
                showControls = false
            }
        }
    }

    DisposableEffect(videoId) {
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

        scope.launch {
            val startPos = viewModel.getInitialPosition(videoId)
            Log.d(TAG, "[LAUNCH] videoId=$videoId startPos=${startPos}ms playUrl=$playUrl")
            player.playVideo(url = playUrl, startPosition = startPos, videoId = videoId, title = title)
        }

        scope.launch {
            while (isActive) {
                currentPosition = player.getCurrentPosition()
                duration = player.getDuration()
                delay(500)
            }
        }

        scope.launch {
            while (isActive) {
                delay(5000)
                if (!isActive) break
                val dur = player.getDuration()
                val pos = player.getCurrentPosition()
                if (dur > 0) viewModel.saveProgress(videoId, pos, dur)
            }
        }

        onDispose {
            Log.d(TAG, "[PLAYER_CLOSED] videoId=$videoId pos=${player.getCurrentPosition()}ms")
            scope.cancel()
            Log.d(TAG, "[COROUTINES_CANCELLED] videoId=$videoId")
            viewModel.saveProgress(videoId, player.getCurrentPosition(), player.getDuration())
            player.release()
            Log.d(TAG, "[PLAYER_RELEASED] videoId=$videoId")
        }
    }

    DisposableEffect(Unit) {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        onDispose {
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            // Restore auto brightness on exit
            activity?.window?.let { window ->
                val params = window.attributes
                params.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE
                window.attributes = params
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Video surface
        AndroidView(
            factory = { ctx ->
                Log.d(TAG, "[PLAYER_VIEW] factory instance=${player.getPlayerInstance()}")
                PlayerView(ctx).apply {
                    this.player = player.getPlayerInstance()
                    useController = false
                    resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                }
            },
            update = { view ->
                val instance = player.getPlayerInstance()
                if (view.player !== instance) {
                    Log.d(TAG, "[PLAYER_VIEW] re-attaching instance=$instance")
                    view.player = instance
                }
                view.scaleX = videoScale
                view.scaleY = videoScale
            },
            modifier = Modifier.fillMaxSize()
        )

        if (!isLocked) {
            // LEFT ZONE — brightness (vertical swipe)
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.35f)
                    .align(Alignment.CenterStart)
                    .pointerInput(Unit) {
                        detectVerticalDragGestures { _, dragAmount ->
                            val delta = (-dragAmount / size.height * 100).toInt()
                            applyBrightness(brightnessPct + delta)
                            controlsScope.launch { delay(1200); brightnessFeedback = null }
                        }
                    }
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = { resetControlsTimer() },
                            onDoubleTap = {
                                val newPos = (player.getCurrentPosition() - SEEK_INCREMENT_MS).coerceAtLeast(0L)
                                player.seekTo(newPos)
                                seekFeedback = "⏪ -10s"
                                Log.d(TAG, "[DOUBLE_TAP] backward 10s")
                                controlsScope.launch { delay(800); seekFeedback = null }
                            }
                        )
                    }
            )

            // RIGHT ZONE — volume (vertical swipe)
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.35f)
                    .align(Alignment.CenterEnd)
                    .pointerInput(Unit) {
                        detectVerticalDragGestures { _, dragAmount ->
                            val delta = (-dragAmount / size.height * 100).toInt()
                            applyVolume(virtualVolumePct + delta)
                            controlsScope.launch { delay(1200); volumeFeedback = null }
                        }
                    }
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = { resetControlsTimer() },
                            onDoubleTap = {
                                val newPos = (player.getCurrentPosition() + SEEK_INCREMENT_MS).coerceAtLeast(0L)
                                player.seekTo(newPos)
                                seekFeedback = "⏩ +10s"
                                Log.d(TAG, "[DOUBLE_TAP] forward 10s")
                                controlsScope.launch { delay(800); seekFeedback = null }
                            }
                        )
                    }
            )

            // CENTER ZONE — horizontal seek + tap toggle + pinch zoom
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.3f)
                    .align(Alignment.Center)
                    .pointerInput(Unit) {
                        detectTapGestures(onTap = { resetControlsTimer() })
                    }
                    .pointerInput(Unit) {
                        var totalDrag = 0f
                        var seekBase = 0L
                        detectHorizontalDragGestures(
                            onDragStart = {
                                totalDrag = 0f
                                seekBase = player.getCurrentPosition()
                            },
                            onHorizontalDrag = { _, dragAmount ->
                                totalDrag += dragAmount
                                val seekDelta = (totalDrag / size.width * 60_000L).toLong()
                                val preview = (seekBase + seekDelta).coerceIn(0L, duration)
                                val sign = if (seekDelta >= 0) "⏩" else "⏪"
                                seekFeedback = "$sign ${formatTime(preview)}"
                            },
                            onDragEnd = {
                                val seekDelta = (totalDrag / size.width * 60_000L).toLong()
                                val newPos = (seekBase + seekDelta).coerceIn(0L, duration)
                                player.seekTo(newPos)
                                Log.d(TAG, "[SWIPE_SEEK] delta=${seekDelta}ms newPos=${newPos}ms")
                                controlsScope.launch { delay(800); seekFeedback = null }
                            }
                        )
                    }
                    .pointerInput(Unit) {
                        detectTransformGestures { _, _, zoom, _ ->
                            videoScale = (videoScale * zoom).coerceIn(0.5f, 3f)
                        }
                    }
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTapGestures(onTap = { showControls = !showControls })
                    }
            )
        }

        // Seek feedback — center
        seekFeedback?.let { text ->
            Text(
                text = text,
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.Center)
                    .background(Color.Black.copy(alpha = 0.65f), RoundedCornerShape(10.dp))
                    .padding(horizontal = 20.dp, vertical = 10.dp)
            )
        }

        // Volume feedback — right side
        volumeFeedback?.let { text ->
            FeedbackPill(text = text, modifier = Modifier.align(Alignment.CenterEnd).padding(end = 40.dp))
        }

        // Brightness feedback — left side
        brightnessFeedback?.let { text ->
            FeedbackPill(text = text, modifier = Modifier.align(Alignment.CenterStart).padding(start = 40.dp))
        }

        // Buffering spinner
        if (isBuffering) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = Color.White,
                strokeWidth = 3.dp
            )
        }

        // Error overlay
        if (error != null) {
            Column(
                modifier = Modifier.align(Alignment.Center).padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(Icons.Default.Error, null, tint = Color.Red, modifier = Modifier.size(48.dp))
                Spacer(Modifier.height(8.dp))
                Text(error!!.userMessage, color = Color.White, style = MaterialTheme.typography.bodyLarge)
                if (error!!.httpStatusCode != null)
                    Text("HTTP ${error!!.httpStatusCode}", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                Text(error!!.message, color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                Spacer(Modifier.height(12.dp))
                Button(onClick = { player.playVideo(url = playUrl, startPosition = currentPosition, videoId = videoId, title = title) }) {
                    Text("Retry")
                }
            }
        }

        // Controls overlay
        AnimatedVisibility(
            visible = showControls,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.45f))
            ) {
                // Top bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = {
                        Log.d(TAG, "[BACK] videoId=$videoId pos=${currentPosition}ms")
                        onBack()
                    }) {
                        Icon(Icons.Default.ArrowBack, "Back", tint = Color.White)
                    }
                    Text(
                        title,
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.weight(1f),
                        maxLines = 1
                    )
                    // Speed
                    Box {
                        TextButton(onClick = { showSpeedMenu = true }) {
                            Text("${playbackSpeed}x", color = Color.White, fontSize = 13.sp)
                        }
                        DropdownMenu(expanded = showSpeedMenu, onDismissRequest = { showSpeedMenu = false }) {
                            listOf(0.25f, 0.5f, 0.75f, 1f, 1.25f, 1.5f, 1.75f, 2f).forEach { speed ->
                                DropdownMenuItem(
                                    text = { Text("${speed}x", fontWeight = if (speed == playbackSpeed) FontWeight.Bold else FontWeight.Normal) },
                                    onClick = { player.setSpeed(speed); showSpeedMenu = false }
                                )
                            }
                        }
                    }
                    // Audio tracks
                    if (audioTracks.size > 1) {
                        Box {
                            IconButton(onClick = { showAudioMenu = true }) {
                                Icon(Icons.Default.Audiotrack, "Audio", tint = Color.White)
                            }
                            DropdownMenu(expanded = showAudioMenu, onDismissRequest = { showAudioMenu = false }) {
                                audioTracks.forEach { track ->
                                    DropdownMenuItem(
                                        text = { Text(track.label, fontWeight = if (track.isSelected) FontWeight.Bold else FontWeight.Normal) },
                                        onClick = { player.selectAudioTrack(track.groupIndex); showAudioMenu = false }
                                    )
                                }
                            }
                        }
                    }
                    // Subtitles
                    Box {
                        IconButton(onClick = { showSubtitleMenu = true }) {
                            Icon(Icons.Default.ClosedCaption, "Subtitles", tint = Color.White)
                        }
                        DropdownMenu(expanded = showSubtitleMenu, onDismissRequest = { showSubtitleMenu = false }) {
                            DropdownMenuItem(
                                text = { Text("Off") },
                                onClick = { player.selectSubtitleTrack(null); showSubtitleMenu = false }
                            )
                            subtitleTracks.forEach { track ->
                                DropdownMenuItem(
                                    text = { Text(track.label, fontWeight = if (track.isSelected) FontWeight.Bold else FontWeight.Normal) },
                                    onClick = { player.selectSubtitleTrack(track.groupIndex); showSubtitleMenu = false }
                                )
                            }
                        }
                    }
                    // Rotation toggle
                    IconButton(onClick = {
                        isLandscape = !isLandscape
                        activity?.requestedOrientation = if (isLandscape)
                            ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
                        else
                            ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
                    }) {
                        Icon(Icons.Default.ScreenRotation, "Rotate", tint = Color.White)
                    }
                    // PiP
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        IconButton(onClick = {
                            activity?.enterPictureInPictureMode(
                                PictureInPictureParams.Builder()
                                    .setAspectRatio(Rational(16, 9))
                                    .build()
                            )
                            Log.d(TAG, "[PIP] entered")
                        }) {
                            Icon(Icons.Default.PictureInPicture, "PiP", tint = Color.White)
                        }
                    }
                }

                // Center controls
                if (!isLocked) {
                    Row(
                        modifier = Modifier.align(Alignment.Center),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        IconButton(onClick = { player.seekTo(currentPosition - SEEK_INCREMENT_MS) }) {
                            Icon(Icons.Default.Replay10, "Rewind", tint = Color.White, modifier = Modifier.size(44.dp))
                        }
                        IconButton(
                            onClick = { if (isPlaying) player.pause() else player.resume() },
                            modifier = Modifier.size(64.dp)
                        ) {
                            Icon(
                                if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                "Play/Pause",
                                tint = Color.White,
                                modifier = Modifier.size(64.dp)
                            )
                        }
                        IconButton(onClick = { player.seekTo(currentPosition + SEEK_INCREMENT_MS) }) {
                            Icon(Icons.Default.Forward10, "Forward", tint = Color.White, modifier = Modifier.size(44.dp))
                        }
                    }
                }

                // Bottom bar
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(formatTime(currentPosition), color = Color.White, fontSize = 12.sp)
                        Slider(
                            value = currentPosition.toFloat().coerceIn(0f, duration.toFloat().coerceAtLeast(1f)),
                            onValueChange = { player.seekTo(it.toLong()) },
                            valueRange = 0f..duration.toFloat().coerceAtLeast(1f),
                            modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
                            colors = SliderDefaults.colors(thumbColor = Color.Red, activeTrackColor = Color.Red, inactiveTrackColor = Color.Gray)
                        )
                        Text(formatTime(duration), color = Color.White, fontSize = 12.sp)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Repeat
                        IconButton(onClick = { player.cycleRepeatMode() }) {
                            Icon(
                                if (repeatMode == androidx.media3.common.Player.REPEAT_MODE_ONE) Icons.Default.RepeatOne else Icons.Default.Repeat,
                                "Repeat",
                                tint = if (repeatMode != androidx.media3.common.Player.REPEAT_MODE_OFF) Color.Red else Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        // Zoom reset
                        if (abs(videoScale - 1f) > 0.05f) {
                            TextButton(onClick = { videoScale = 1f }) {
                                Text("Reset Zoom", color = Color.White, fontSize = 11.sp)
                            }
                        }
                        // Lock
                        IconButton(onClick = {
                            isLocked = !isLocked
                            Log.d(TAG, "[LOCK] isLocked=$isLocked")
                        }) {
                            Icon(
                                if (isLocked) Icons.Default.Lock else Icons.Default.LockOpen,
                                "Lock",
                                tint = if (isLocked) Color.Yellow else Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                // Lock indicator
                if (isLocked) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .background(Color.Black.copy(alpha = 0.7f), RoundedCornerShape(12.dp))
                            .padding(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Lock, null, tint = Color.Yellow, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Controls Locked", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FeedbackPill(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        color = Color.White,
        fontSize = 15.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = modifier
            .background(Color.Black.copy(alpha = 0.7f), RoundedCornerShape(10.dp))
            .padding(horizontal = 14.dp, vertical = 8.dp)
    )
}

private fun formatTime(ms: Long): String {
    if (ms <= 0) return "00:00"
    val totalSeconds = ms / 1000
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    return if (hours > 0) "%d:%02d:%02d".format(hours, minutes, seconds)
    else "%02d:%02d".format(minutes, seconds)
}
