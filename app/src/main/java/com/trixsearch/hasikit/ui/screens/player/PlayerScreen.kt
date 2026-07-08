package com.trixsearch.hasikit.ui.screens.player

import android.app.Activity
import android.app.PictureInPictureParams
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ActivityInfo
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Build
import android.telephony.PhoneStateListener
import android.telephony.TelephonyCallback
import android.telephony.TelephonyManager
import android.util.Log
import android.util.Rational
import android.view.WindowManager
import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import org.drinkless.tdlib.TdApi
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.math.abs

// Player settings DataStore — reads same store as SettingsViewModel
private val Context.playerSettingsStore by preferencesDataStore(name = "hasikit_settings")
private val KEY_RESUME_AFTER_CALL = booleanPreferencesKey("resume_after_call")
private val KEY_PAUSE_ON_HEADPHONE = booleanPreferencesKey("pause_on_headphone_removal")
private val KEY_BACKGROUND_AUDIO = booleanPreferencesKey("background_audio")

private const val TAG = "PLAYER_DEBUG"
private const val CONTROLS_HIDE_DELAY = 3000L
private const val SEEK_INCREMENT_MS = 10_000L
private const val LONG_PRESS_THRESHOLD_MS = 500L

private val SPEED_OPTIONS = listOf(0.25f, 0.5f, 0.75f, 1f, 1.25f, 1.5f, 1.75f, 2f, 2.5f, 3f)

enum class VideoFitMode(val label: String, val resizeMode: Int) {
    FIT("Fit", AspectRatioFrameLayout.RESIZE_MODE_FIT),
    FILL("Fill", AspectRatioFrameLayout.RESIZE_MODE_FILL),
    ZOOM("Zoom", AspectRatioFrameLayout.RESIZE_MODE_ZOOM),
    FIXED_WIDTH("16:9", AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH),
    FIXED_HEIGHT("4:3", AspectRatioFrameLayout.RESIZE_MODE_FIXED_HEIGHT),
    STRETCH("Stretch", AspectRatioFrameLayout.RESIZE_MODE_FILL),
}

private val FIT_CYCLE = listOf(
    VideoFitMode.FIT,
    VideoFitMode.FILL,
    VideoFitMode.STRETCH,
    VideoFitMode.FIXED_WIDTH,
    VideoFitMode.FIXED_HEIGHT,
    VideoFitMode.ZOOM,
)

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
        Log.d(TAG, "resolvePlayUrl fileId=$fileId")

        // Check if already fully downloaded
        val existing = getFileInfo(fileId)
        if (existing?.local?.isDownloadingCompleted == true && existing.local.path.isNotBlank()) {
            Log.d(TAG, "resolvePlayUrl fileId=$fileId already complete path=${existing.local.path}")
            return@withContext "file://${existing.local.path}"
        }

        // Start download with high priority (1 = highest)
        telegramClientService.send(TdApi.DownloadFile(fileId, 1, 0, 0, false)) { result ->
            when (result) {
                is TdApi.File -> Log.d(TAG, "resolvePlayUrl DownloadFile started fileId=$fileId path=${result.local.path}")
                is TdApi.Error -> Log.e(TAG, "resolvePlayUrl DownloadFile error ${result.code}: ${result.message}")
            }
        }

        // Wait until TDLib has downloaded enough to start streaming
        // We need: isDownloadingActive=true AND path is set AND downloadedSize > 0
        var attempts = 0
        while (attempts < 90) { // 90s max
            val file = getFileInfo(fileId)
            if (file != null) {
                val local = file.local
                when {
                    local.isDownloadingCompleted && local.path.isNotBlank() -> {
                        Log.d(TAG, "resolvePlayUrl complete fileId=$fileId path=${local.path}")
                        return@withContext "file://${local.path}"
                    }
                    local.isDownloadingActive && local.path.isNotBlank() && local.downloadedSize > 0 -> {
                        Log.d(TAG, "resolvePlayUrl streaming fileId=$fileId path=${local.path} downloaded=${local.downloadedSize}")
                        return@withContext "file://${local.path}"
                    }
                }
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
    var fitMode by remember { mutableStateOf(VideoFitMode.FIT) }
    var fitOverlayText by remember { mutableStateOf<String?>(null) }
    var seekFeedback by remember { mutableStateOf<String?>(null) }
    var volumeFeedback by remember { mutableStateOf<String?>(null) }
    var brightnessFeedback by remember { mutableStateOf<String?>(null) }
    var speedFeedback by remember { mutableStateOf<String?>(null) }
    var lockedTapMessage by remember { mutableStateOf<String?>(null) }
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

    // Read player preferences from DataStore
    val resumeAfterCall by context.playerSettingsStore.data
        .map { it[KEY_RESUME_AFTER_CALL] ?: true }
        .collectAsState(initial = true)
    val pauseOnHeadphoneRemoval by context.playerSettingsStore.data
        .map { it[KEY_PAUSE_ON_HEADPHONE] ?: true }
        .collectAsState(initial = true)
    val backgroundAudio by context.playerSettingsStore.data
        .map { it[KEY_BACKGROUND_AUDIO] ?: true }
        .collectAsState(initial = true)

    // Track whether playback was interrupted by a phone call so we can resume
    var wasPlayingBeforeCall by remember { mutableStateOf(false) }

    // Audio focus: request focus before playback to prevent crashes with other media apps
    val audioFocusRequest = remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MOVIE)
                        .build()
                )
                .setOnAudioFocusChangeListener { focusChange ->
                    when (focusChange) {
                        AudioManager.AUDIOFOCUS_LOSS -> player.pause()
                        AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> player.pause()
                        AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> player.setVolume(30)
                        AudioManager.AUDIOFOCUS_GAIN -> {
                            player.setVolume(100)
                            player.resume()
                        }
                    }
                }
                .build()
        } else null
    }

    // Request audio focus on enter, abandon on exit
    DisposableEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && audioFocusRequest != null) {
            audioManager.requestAudioFocus(audioFocusRequest)
        } else {
            @Suppress("DEPRECATION")
            audioManager.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN)
        }
        onDispose {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && audioFocusRequest != null) {
                audioManager.abandonAudioFocusRequest(audioFocusRequest)
            } else {
                @Suppress("DEPRECATION")
                audioManager.abandonAudioFocus(null)
            }
        }
    }

    // Added resume playback after phone calls — uses TelephonyCallback on API 31+, PhoneStateListener below
    DisposableEffect(resumeAfterCall) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // API 31+: use non-deprecated TelephonyCallback
            val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            val callback = object : TelephonyCallback(), TelephonyCallback.CallStateListener {
                override fun onCallStateChanged(state: Int) {
                    when (state) {
                        TelephonyManager.CALL_STATE_RINGING,
                        TelephonyManager.CALL_STATE_OFFHOOK -> {
                            if (player.isPlaying.value) { wasPlayingBeforeCall = true; player.pause() }
                        }
                        TelephonyManager.CALL_STATE_IDLE -> {
                            if (resumeAfterCall && wasPlayingBeforeCall) { wasPlayingBeforeCall = false; player.resume() }
                            else wasPlayingBeforeCall = false
                        }
                    }
                }
            }
            telephonyManager.registerTelephonyCallback(context.mainExecutor, callback)
            onDispose { telephonyManager.unregisterTelephonyCallback(callback) }
        } else {
            // API < 31: use PhoneStateListener (deprecated but required for older devices)
            val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            @Suppress("DEPRECATION")
            val listener = object : PhoneStateListener() {
                @Suppress("DEPRECATION")
                override fun onCallStateChanged(state: Int, phoneNumber: String?) {
                    when (state) {
                        TelephonyManager.CALL_STATE_RINGING,
                        TelephonyManager.CALL_STATE_OFFHOOK -> {
                            if (player.isPlaying.value) { wasPlayingBeforeCall = true; player.pause() }
                        }
                        TelephonyManager.CALL_STATE_IDLE -> {
                            if (resumeAfterCall && wasPlayingBeforeCall) { wasPlayingBeforeCall = false; player.resume() }
                            else wasPlayingBeforeCall = false
                        }
                    }
                }
            }
            @Suppress("DEPRECATION")
            telephonyManager.listen(listener, PhoneStateListener.LISTEN_CALL_STATE)
            onDispose {
                @Suppress("DEPRECATION")
                telephonyManager.listen(listener, PhoneStateListener.LISTEN_NONE)
            }
        }
    }

    // Added pause on headphone removal listener
    DisposableEffect(pauseOnHeadphoneRemoval) {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(ctx: Context?, intent: Intent?) {
                if (intent?.action == AudioManager.ACTION_AUDIO_BECOMING_NOISY && pauseOnHeadphoneRemoval) {
                    player.pause()
                }
            }
        }
        val filter = IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY)
        context.registerReceiver(receiver, filter)
        onDispose { context.unregisterReceiver(receiver) }
    }

    // Added screen lock / background audio listener
    DisposableEffect(backgroundAudio) {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(ctx: Context?, intent: Intent?) {
                if (intent?.action == Intent.ACTION_SCREEN_OFF && !backgroundAudio) {
                    player.pause()
                }
            }
        }
        val filter = IntentFilter(Intent.ACTION_SCREEN_OFF)
        context.registerReceiver(receiver, filter)
        onDispose { context.unregisterReceiver(receiver) }
    }

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
                view.resizeMode = fitMode.resizeMode
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

            // RIGHT ZONE — volume swipe + double-tap +10s + single-finger long-press 2x speed
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
                            },
                            onLongPress = {
                                // Single-finger long press — activate 2x immediately
                                prevSpeedRef.floatValue = playbackSpeed
                                player.setSpeed(2f)
                                speedFeedback = "⚡ 2x"
                            }
                        )
                    }
                    // Release detection: use awaitPointerEventScope to restore speed on finger lift
                    .pointerInput(Unit) {
                        awaitPointerEventScope {
                            while (true) {
                                awaitFirstDown(requireUnconsumed = false)
                                val downTime = System.currentTimeMillis()
                                var longPressed = false
                                while (true) {
                                    val event = awaitPointerEvent()
                                    if (!longPressed && System.currentTimeMillis() - downTime >= LONG_PRESS_THRESHOLD_MS) {
                                        longPressed = true
                                    }
                                    if (event.changes.all { !it.pressed }) {
                                        if (longPressed && speedFeedback != null) {
                                            player.setSpeed(prevSpeedRef.floatValue)
                                            speedFeedback = null
                                        }
                                        break
                                    }
                                }
                            }
                        }
                    }
            )
        } else {
            // LOCKED — full screen tap shows "Unlock player first" message
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTapGestures(onTap = {
                            lockedTapMessage = "Unlock player first"
                            controlsScope.launch { delay(1500); lockedTapMessage = null }
                            showControls = true
                        })
                    }
            )
        }

        // Feedback overlays
        seekFeedback?.let { FeedbackPill(it, Modifier.align(Alignment.Center)) }
        speedFeedback?.let { FeedbackPill(it, Modifier.align(Alignment.CenterEnd).padding(end = 40.dp)) }
        // Volume gesture is on right side — display indicator on LEFT so finger doesn't cover it
        volumeFeedback?.let { FeedbackPill(it, Modifier.align(Alignment.CenterStart).padding(start = 40.dp)) }
        // Brightness gesture is on left side — display indicator on RIGHT so finger doesn't cover it
        brightnessFeedback?.let { FeedbackPill(it, Modifier.align(Alignment.CenterEnd).padding(end = 40.dp)) }
        // Aspect ratio label shown slightly above center to avoid overlapping play/pause icon
        fitOverlayText?.let { FeedbackPill(it, Modifier.align(Alignment.Center).offset(y = (-80).dp)) }
        lockedTapMessage?.let { FeedbackPill(it, Modifier.align(Alignment.Center)) }

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
            Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = if (isLocked) 0.2f else 0.45f))) {
                if (!isLocked) {
                    // Top bar — hidden when locked
                    Row(
                        modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter).padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White) }
                        Text(title, color = Color.White, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f), maxLines = 1)
                        // Aspect ratio — single tap cycles
                        IconButton(onClick = {
                            val idx = FIT_CYCLE.indexOf(fitMode)
                            fitMode = FIT_CYCLE[(idx + 1) % FIT_CYCLE.size]
                            fitOverlayText = fitMode.label
                            controlsScope.launch { delay(2000); fitOverlayText = null }
                        }) { Icon(Icons.Default.AspectRatio, "Aspect Ratio", tint = Color.White) }
                        TextButton(onClick = { showSpeedSheet = true }) {
                            Text("${playbackSpeed}x", color = Color.White, fontSize = 13.sp)
                        }
                        // Audio track selector — always shown; includes Original Audio and Mute options
                        Box {
                            IconButton(onClick = { showAudioMenu = true }) { Icon(Icons.Default.Audiotrack, "Audio", tint = Color.White) }
                            DropdownMenu(expanded = showAudioMenu, onDismissRequest = { showAudioMenu = false }) {
                                if (audioTracks.isEmpty()) {
                                    // Single track video — show Original Audio and Mute
                                    DropdownMenuItem(
                                        text = { Text("Original Audio", fontWeight = FontWeight.Bold) },
                                        onClick = { player.setVolume(100); showAudioMenu = false }
                                    )
                                } else {
                                    audioTracks.forEach { track ->
                                        DropdownMenuItem(
                                            text = { Text(track.label, fontWeight = if (track.isSelected) FontWeight.Bold else FontWeight.Normal) },
                                            onClick = { player.selectAudioTrack(track.groupIndex); showAudioMenu = false }
                                        )
                                    }
                                }
                                // Mute Audio option always available
                                DropdownMenuItem(
                                    text = { Text("Mute Audio") },
                                    leadingIcon = { Icon(Icons.Default.VolumeOff, null, modifier = Modifier.size(18.dp)) },
                                    onClick = { player.setVolume(0); showAudioMenu = false }
                                )
                            }
                        }
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
                        IconButton(onClick = {
                            isLandscape = !isLandscape
                            activity?.requestedOrientation = if (isLandscape) ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE else ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
                        }) { Icon(Icons.Default.ScreenRotation, "Rotate", tint = Color.White) }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            IconButton(onClick = { activity?.enterPictureInPictureMode(PictureInPictureParams.Builder().setAspectRatio(Rational(16, 9)).build()) }) {
                                Icon(Icons.Default.PictureInPicture, "PiP", tint = Color.White)
                            }
                        }
                    }

                    // Center controls
                    Row(modifier = Modifier.align(Alignment.Center), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        IconButton(onClick = { player.seekTo(currentPosition - SEEK_INCREMENT_MS) }) { Icon(Icons.Default.Replay10, "Rewind", tint = Color.White, modifier = Modifier.size(44.dp)) }
                        IconButton(onClick = { if (isPlaying) player.pause() else player.resume() }, modifier = Modifier.size(64.dp)) {
                            Icon(if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow, "Play/Pause", tint = Color.White, modifier = Modifier.size(64.dp))
                        }
                        IconButton(onClick = { player.seekTo(currentPosition + SEEK_INCREMENT_MS) }) { Icon(Icons.Default.Forward10, "Forward", tint = Color.White, modifier = Modifier.size(44.dp)) }
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
                                // Thickness of Seekbar
                                colors = SliderDefaults.colors(thumbColor = MaterialTheme.colorScheme.primary, activeTrackColor = MaterialTheme.colorScheme.primary, inactiveTrackColor = Color.Gray),
                                thumb = {
                                    // Thickness of Seekbar
                                    Box(modifier = Modifier.size(12.dp).background(MaterialTheme.colorScheme.primary, androidx.compose.foundation.shape.CircleShape))
                                },
                                track = { sliderState ->
                                    // Thickness of Seekbar
                                    SliderDefaults.Track(
                                        sliderState = sliderState,
                                        modifier = Modifier.height(3.dp),
                                        colors = SliderDefaults.colors(activeTrackColor = MaterialTheme.colorScheme.primary, inactiveTrackColor = Color.Gray)
                                    )
                                }
                            )
                            Text(formatTime(duration), color = Color.White, fontSize = 12.sp)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { player.cycleRepeatMode() }) {
                                Icon(
                                    if (repeatMode == androidx.media3.common.Player.REPEAT_MODE_ONE) Icons.Default.RepeatOne else Icons.Default.Repeat,
                                    "Repeat",
                                    tint = if (repeatMode != androidx.media3.common.Player.REPEAT_MODE_OFF) MaterialTheme.colorScheme.primary else Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            if (abs(videoScale - 1f) > 0.05f) {
                                TextButton(onClick = { videoScale = 1f }) { Text("Reset Zoom", color = Color.White, fontSize = 11.sp) }
                            }
                            // Lock button — tap to lock
                            IconButton(onClick = { isLocked = true; showControls = false }) {
                                Icon(Icons.Default.LockOpen, "Lock", tint = Color.White, modifier = Modifier.size(20.dp))
                            }
                        }
                    }
                }

                // Unlock button — only shown when locked
                if (isLocked) {
                    Row(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .background(Color.Black.copy(alpha = 0.7f), RoundedCornerShape(12.dp))
                            .clickable { isLocked = false; resetControlsTimer() }
                            .padding(horizontal = 20.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Lock, null, tint = Color.Yellow, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Unlock", color = Color.White, fontWeight = FontWeight.SemiBold)
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
