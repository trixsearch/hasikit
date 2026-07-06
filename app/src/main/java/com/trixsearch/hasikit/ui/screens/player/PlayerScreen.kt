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
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.trixsearch.hasikit.telegram.domain.repository.TelegramChannelRepository
import com.trixsearch.hasikit.telegram.service.TelegramClientService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import org.drinkless.tdlib.TdApi
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.math.abs

private const val TAG = "PLAYER_DEBUG"
private const val CONTROLS_HIDE_DELAY = 3000L
private const val SEEK_INCREMENT_MS = 10_000L
private const val LONG_PRESS_THRESHOLD_MS = 500L

private val SPEED_OPTIONS = listOf(0.25f, 0.5f, 0.75f, 1f, 1.25f, 1.5f, 1.75f, 2f, 2.5f, 3f)

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val repository: VideoRepository,
    private val channelRepository: TelegramChannelRepository,
    private val telegramClientService: TelegramClientService
) : ViewModel() {

    fun saveProgress(videoId: String, position: Long, duration: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.saveWatchProgress(
                WatchProgress(videoId = videoId, lastPosition = position, duration = duration, lastWatchedAt = System.currentTimeMillis())
            )
        }
    }

    suspend fun getInitialPosition(videoId: String): Long = withContext(Dispatchers.IO) {
        repository.getWatchProgress(videoId)?.lastPosition ?: 0L
    }

    suspend fun resolvePlayUrl(telegramFileId: String): String? = withContext(Dispatchers.IO) {
        val fileId = telegramFileId.toLongOrNull()?.toInt() ?: return@withContext null
        val existing = getFileInfo(fileId)
        if (existing?.local?.isDownloadingCompleted == true && existing.local.path.isNotBlank()) {
            return@withContext "file://${existing.local.path}"
        }
        telegramClientService.send(TdApi.DownloadFile(fileId, 32, 0, 0, false)) { result ->
            when (result) {
                is TdApi.File  -> Log.d(TAG, "resolvePlayUrl started fileId=$fileId path=${result.local.path}")
                is TdApi.Error -> Log.e(TAG, "resolvePlayUrl error ${result.code}: ${result.message}")
            }
        }
        var attempts = 0
        while (attempts < 60) {
            val file = getFileInfo(fileId)
            if (file != null) {
                val local = file.local
                if (local.isDownloadingCompleted && local.path.isNotBlank()) return@withContext "file://${local.path}"
                if (local.isDownloadingActive && local.downloadedSize > 0 && local.path.isNotBlank()) return@withContext "file://${local.path}"
            }
            delay(1000)
            attempts++
        }
        Log.e(TAG, "resolvePlayUrl timeout fileId=$fileId")
        null
    }

    private suspend fun getFileInfo(fileId: Int): TdApi.File? =
        suspendCancellableCoroutine { cont ->
            telegramClientService.send(TdApi.GetFile(fileId)) { result ->
                cont.resume(if (result is TdApi.File) result else null)
            }
            cont.invokeOnCancellation {}
        }
}

@OptIn(UnstableApi::class, ExperimentalMaterial3Api::class)
@Composable
fun PlayerScreen(
    videoId: String,
    title: String,
    player: HasikitPlayer,
    videoUrl: String,
    localPath: String?,
    telegramFileId: String = "",
    onBack: () -> Unit,
    viewModel: PlayerViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val activity = context as? Activity

    remember(videoId) { player.initialize() }

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
    var showSpeedSheet by remember { mutableStateOf(false) }
    var showAudioMenu by remember { mutableStateOf(false) }
    var showSubtitleMenu by remember { mutableStateOf(false) }
    var seekFeedback by remember { mutableStateOf<String?>(null) }
    var volumeFeedback by remember { mutableStateOf<String?>(null) }
    var brightnessFeedback by remember { mutableStateOf<String?>(null) }
    var speedFeedback by remember { mutableStateOf<String?>(null) }
    var videoScale by remember { mutableFloatStateOf(1f) }
    var isLandscape by remember { mutableStateOf(true) }
    // Stores speed before long-press 2x so we can restore on release
    val prevSpeedRef = remember { mutableFloatStateOf(1f) }

    val audioManager = remember { context.getSystemService(Context.AUDIO_SERVICE) as AudioManager }
    val maxSystemVolume = remember { audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC) }

    var virtualVolumePct by remember {
        val current = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        mutableIntStateOf((current * 100f / maxSystemVolume).toInt())
    }
    var brightnessPct by remember {
        val current = activity?.window?.attributes?.screenBrightness ?: -1f
        mutableIntStateOf(if (current < 0f) 50 else (current * 100).toInt())
    }

    val playUrl = remember(localPath, videoUrl, telegramFileId) {
        when {
            !localPath.isNullOrBlank() -> if (localPath.startsWith("file://")) localPath else "file://$localPath"
            videoUrl.isNotBlank() -> videoUrl
            else -> ""
        }
    }

    val controlsScope = rememberCoroutineScope()

    fun applyVolume(pct: Int) {
        val clamped = pct.coerceIn(0, 200)
        virtualVolumePct = clamped
        val newSystemVol = (minOf(clamped, 100) * maxSystemVolume / 100f).toInt().coerceIn(0, maxSystemVolume)
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, newSystemVol, 0)
        player.setVolume(clamped)
        volumeFeedback = "${when { clamped == 0 -> "🔇"; clamped <= 50 -> "🔈"; clamped <= 100 -> "🔉"; else -> "🔊" }} Volume $clamped%"
    }

    fun applyBrightness(pct: Int) {
        val clamped = pct.coerceIn(1, 100)
        brightnessPct = clamped
        activity?.window?.let { w -> val p = w.attributes; p.screenBrightness = clamped / 100f; w.attributes = p }
        brightnessFeedback = "☀ Brightness $clamped%"
    }

    fun resetControlsTimer() {
        if (!isLocked) {
            showControls = true
            controlsScope.launch { delay(CONTROLS_HIDE_DELAY); showControls = false }
        }
    }

    DisposableEffect(videoId) {
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
        scope.launch {
            val startPos = viewModel.getInitialPosition(videoId)
            val resolvedUrl = when {
                playUrl.isNotBlank() -> playUrl
                telegramFileId.isNotBlank() -> viewModel.resolvePlayUrl(telegramFileId) ?: return@launch
                else -> return@launch
            }
            player.playVideo(url = resolvedUrl, startPosition = startPos, videoId = videoId, title = title)
        }
        scope.launch { while (isActive) { currentPosition = player.getCurrentPosition(); duration = player.getDuration(); delay(500) } }
        scope.launch { while (isActive) { delay(5000); if (player.getDuration() > 0) viewModel.saveProgress(videoId, player.getCurrentPosition(), player.getDuration()) } }
        onDispose {
            scope.cancel()
            viewModel.saveProgress(videoId, player.getCurrentPosition(), player.getDuration())
            player.release()
        }
    }

    DisposableEffect(Unit) {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        onDispose {
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            activity?.window?.let { w -> val p = w.attributes; p.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE; w.attributes = p }
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply { this.player = player.getPlayerInstance(); useController = false; resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT }
            },
            update = { view ->
                val instance = player.getPlayerInstance()
                if (view.player !== instance) view.player = instance
                view.scaleX = videoScale; view.scaleY = videoScale
            },
            modifier = Modifier.fillMaxSize()
        )

        if (!isLocked) {
            // LEFT ZONE — brightness swipe + double-tap -10s
            Box(
                modifier = Modifier
                    .fillMaxHeight().fillMaxWidth(0.3f).align(Alignment.CenterStart)
                    .pointerInput(Unit) {
                        detectVerticalDragGestures { _, dragAmount ->
                            applyBrightness(brightnessPct + (-dragAmount / size.height * 100).toInt())
                            controlsScope.launch { delay(1200); brightnessFeedback = null }
                        }
                    }
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = { resetControlsTimer() },
                            onDoubleTap = {
                                player.seekTo((player.getCurrentPosition() - SEEK_INCREMENT_MS).coerceAtLeast(0L))
                                seekFeedback = "⏪ -10s"
                                controlsScope.launch { delay(800); seekFeedback = null }
                            }
                        )
                    }
            )

            // CENTER ZONE — horizontal seek + double-tap play/pause + pinch zoom
            Box(
                modifier = Modifier
                    .fillMaxHeight().fillMaxWidth(0.4f).align(Alignment.Center)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = { resetControlsTimer() },
                            onDoubleTap = { if (isPlaying) player.pause() else player.resume() }
                        )
                    }
                    .pointerInput(Unit) {
                        var totalDrag = 0f; var seekBase = 0L
                        detectHorizontalDragGestures(
                            onDragStart = { totalDrag = 0f; seekBase = player.getCurrentPosition() },
                            onHorizontalDrag = { _, dragAmount ->
                                totalDrag += dragAmount
                                val delta = (totalDrag / size.width * 60_000L).toLong()
                                seekFeedback = "${if (delta >= 0) "⏩" else "⏪"} ${formatTime((seekBase + delta).coerceIn(0L, duration))}"
                            },
                            onDragEnd = {
                                val delta = (totalDrag / size.width * 60_000L).toLong()
                                player.seekTo((seekBase + delta).coerceIn(0L, duration))
                                controlsScope.launch { delay(800); seekFeedback = null }
                            }
                        )
                    }
                    .pointerInput(Unit) {
                        detectTransformGestures { _, _, zoom, _ -> videoScale = (videoScale * zoom).coerceIn(0.5f, 3f) }
                    }
            )

            // RIGHT ZONE — volume swipe + double-tap +10s + long-press 2x speed
            Box(
                modifier = Modifier
                    .fillMaxHeight().fillMaxWidth(0.3f).align(Alignment.CenterEnd)
                    .pointerInput(Unit) {
                        detectVerticalDragGestures { _, dragAmount ->
                            applyVolume(virtualVolumePct + (-dragAmount / size.height * 100).toInt())
                            controlsScope.launch { delay(1200); volumeFeedback = null }
                        }
                    }
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = { resetControlsTimer() },
                            onDoubleTap = {
                                player.seekTo((player.getCurrentPosition() + SEEK_INCREMENT_MS).coerceAtLeast(0L))
                                seekFeedback = "⏩ +10s"
                                controlsScope.launch { delay(800); seekFeedback = null }
                            }
                        )
                    }
                    .pointerInput(Unit) {
                        // Long-press 2x: detect hold ≥500ms, restore on release
                        awaitPointerEventScope {
                            while (true) {
                                awaitFirstDown(requireUnconsumed = false)
                                val downTime = System.currentTimeMillis()
                                var longPressed = false
                                while (true) {
                                    val event = awaitPointerEvent()
                                    if (!longPressed && System.currentTimeMillis() - downTime >= LONG_PRESS_THRESHOLD_MS) {
                                        longPressed = true
                                        prevSpeedRef.floatValue = playbackSpeed
                                        player.setSpeed(2f)
                                        speedFeedback = "⚡ 2x"
                                    }
                                    if (event.changes.all { !it.pressed }) {
                                        if (longPressed) { player.setSpeed(prevSpeedRef.floatValue); speedFeedback = null }
                                        break
                                    }
                                }
                            }
                        }
                    }
            )
        } else {
            Box(modifier = Modifier.fillMaxSize().pointerInput(Unit) { detectTapGestures(onTap = { showControls = !showControls }) })
        }

        // Feedback overlays
        seekFeedback?.let { FeedbackPill(it, Modifier.align(Alignment.Center)) }
        speedFeedback?.let { FeedbackPill(it, Modifier.align(Alignment.CenterEnd).padding(end = 40.dp)) }
        volumeFeedback?.let { FeedbackPill(it, Modifier.align(Alignment.CenterEnd).padding(end = 40.dp)) }
        brightnessFeedback?.let { FeedbackPill(it, Modifier.align(Alignment.CenterStart).padding(start = 40.dp)) }

        if (isBuffering) CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = Color.White, strokeWidth = 3.dp)

        if (error != null) {
            Column(modifier = Modifier.align(Alignment.Center).padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.Error, null, tint = Color.Red, modifier = Modifier.size(48.dp))
                Spacer(Modifier.height(8.dp))
                Text(error!!.userMessage, color = Color.White, style = MaterialTheme.typography.bodyLarge)
                if (error!!.httpStatusCode != null) Text("HTTP ${error!!.httpStatusCode}", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                Text(error!!.message, color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                Spacer(Modifier.height(12.dp))
                Button(onClick = { if (playUrl.isNotBlank()) player.playVideo(url = playUrl, startPosition = currentPosition, videoId = videoId, title = title) }) { Text("Retry") }
            }
        }

        AnimatedVisibility(visible = showControls, enter = fadeIn(), exit = fadeOut(), modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.45f))) {
                // Top bar
                Row(
                    modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter).padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White) }
                    Text(title, color = Color.White, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f), maxLines = 1)
                    // Speed button → bottom sheet
                    TextButton(onClick = { showSpeedSheet = true }) {
                        Text("${playbackSpeed}x", color = Color.White, fontSize = 13.sp)
                    }
                    // Audio tracks
                    if (audioTracks.size > 1) {
                        Box {
                            IconButton(onClick = { showAudioMenu = true }) { Icon(Icons.Default.Audiotrack, "Audio", tint = Color.White) }
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
                        IconButton(onClick = { showSubtitleMenu = true }) { Icon(Icons.Default.ClosedCaption, "Subtitles", tint = Color.White) }
                        DropdownMenu(expanded = showSubtitleMenu, onDismissRequest = { showSubtitleMenu = false }) {
                            DropdownMenuItem(text = { Text("Off") }, onClick = { player.selectSubtitleTrack(null); showSubtitleMenu = false })
                            subtitleTracks.forEach { track ->
                                DropdownMenuItem(
                                    text = { Text(track.label, fontWeight = if (track.isSelected) FontWeight.Bold else FontWeight.Normal) },
                                    onClick = { player.selectSubtitleTrack(track.groupIndex); showSubtitleMenu = false }
                                )
                            }
                        }
                    }
                    // Rotation
                    IconButton(onClick = {
                        isLandscape = !isLandscape
                        activity?.requestedOrientation = if (isLandscape) ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE else ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
                    }) { Icon(Icons.Default.ScreenRotation, "Rotate", tint = Color.White) }
                    // PiP
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        IconButton(onClick = { activity?.enterPictureInPictureMode(PictureInPictureParams.Builder().setAspectRatio(Rational(16, 9)).build()) }) {
                            Icon(Icons.Default.PictureInPicture, "PiP", tint = Color.White)
                        }
                    }
                }

                // Center controls
                if (!isLocked) {
                    Row(modifier = Modifier.align(Alignment.Center), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        IconButton(onClick = { player.seekTo(currentPosition - SEEK_INCREMENT_MS) }) { Icon(Icons.Default.Replay10, "Rewind", tint = Color.White, modifier = Modifier.size(44.dp)) }
                        IconButton(onClick = { if (isPlaying) player.pause() else player.resume() }, modifier = Modifier.size(64.dp)) {
                            Icon(if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow, "Play/Pause", tint = Color.White, modifier = Modifier.size(64.dp))
                        }
                        IconButton(onClick = { player.seekTo(currentPosition + SEEK_INCREMENT_MS) }) { Icon(Icons.Default.Forward10, "Forward", tint = Color.White, modifier = Modifier.size(44.dp)) }
                    }
                }

                // Bottom bar
                Column(modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
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
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { player.cycleRepeatMode() }) {
                            Icon(
                                if (repeatMode == androidx.media3.common.Player.REPEAT_MODE_ONE) Icons.Default.RepeatOne else Icons.Default.Repeat,
                                "Repeat",
                                tint = if (repeatMode != androidx.media3.common.Player.REPEAT_MODE_OFF) Color.Red else Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        if (abs(videoScale - 1f) > 0.05f) {
                            TextButton(onClick = { videoScale = 1f }) { Text("Reset Zoom", color = Color.White, fontSize = 11.sp) }
                        }
                        IconButton(onClick = { isLocked = !isLocked }) {
                            Icon(if (isLocked) Icons.Default.Lock else Icons.Default.LockOpen, "Lock", tint = if (isLocked) Color.Yellow else Color.White, modifier = Modifier.size(20.dp))
                        }
                    }
                }

                if (isLocked) {
                    Box(modifier = Modifier.align(Alignment.Center).background(Color.Black.copy(alpha = 0.7f), RoundedCornerShape(12.dp)).padding(16.dp)) {
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

    // Speed bottom sheet
    if (showSpeedSheet) {
        SpeedBottomSheet(
            currentSpeed = playbackSpeed,
            onSpeedSelected = { speed -> player.setSpeed(speed); showSpeedSheet = false },
            onDismiss = { showSpeedSheet = false }
        )
    }
}

@Composable
private fun SpeedBottomSheet(currentSpeed: Float, onSpeedSelected: (Float) -> Unit, onDismiss: () -> Unit) {
    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = Color(0xFF1A1A2E)) {
        Text("Playback Speed", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp))
        LazyColumn(contentPadding = PaddingValues(bottom = 32.dp)) {
            items(SPEED_OPTIONS) { speed ->
                val selected = speed == currentSpeed
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(if (selected) Color.White.copy(alpha = 0.1f) else Color.Transparent)
                        .pointerInput(Unit) { detectTapGestures(onTap = { onSpeedSelected(speed) }) }
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "${speed}x",
                        color = if (selected) Color(0xFF2AABEE) else Color.White,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 16.sp
                    )
                    if (selected) Icon(Icons.Default.Check, null, tint = Color(0xFF2AABEE), modifier = Modifier.size(20.dp))
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
        modifier = modifier.background(Color.Black.copy(alpha = 0.7f), RoundedCornerShape(10.dp)).padding(horizontal = 14.dp, vertical = 8.dp)
    )
}

private fun formatTime(ms: Long): String {
    if (ms <= 0) return "00:00"
    val totalSeconds = ms / 1000
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    return if (hours > 0) "%d:%02d:%02d".format(hours, minutes, seconds) else "%02d:%02d".format(minutes, seconds)
}
