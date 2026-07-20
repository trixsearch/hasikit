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
import android.net.Uri
import android.os.Build
import android.telecom.TelecomManager
import android.telephony.TelephonyCallback
import android.telephony.TelephonyManager
import android.util.Log
import android.util.Rational
import android.view.WindowManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeOff
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
import com.trixsearch.hasikit.download.HasikitDownloadManager
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
import com.trixsearch.hasikit.ui.screens.settings.settingsDataStore
import org.drinkless.tdlib.TdApi
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.math.abs

// FIX #14 — DataStore singleton crash: PlayerScreen must NOT create its own preferencesDataStore
// for "hasikit_settings" because SettingsScreen already owns that instance.
// Instead, read the same store via the extension defined in SettingsScreen.kt (same package).
// Keys must match exactly what SettingsViewModel writes.
private val KEY_RESUME_AFTER_CALL = booleanPreferencesKey("resume_after_call")
private val KEY_PAUSE_ON_HEADPHONE = booleanPreferencesKey("pause_on_headphone_removal")
private val KEY_BACKGROUND_AUDIO = booleanPreferencesKey("background_audio")
// Autoplay settings keys — read by PlayerScreen to decide what to do after video ends
private val KEY_AUTOPLAY_NEXT = booleanPreferencesKey("autoplay_next_video")

private const val TAG = "PLAYER_DEBUG"
// Manual control visibility — controls auto-hide; user taps to toggle show/hide
private const val CONTROLS_HIDE_DELAY = 3000L
// Double Tap Seek Duration — ms seeked per double-tap on left/right zones
private const val SEEK_INCREMENT_MS = 10_000L
private const val LONG_PRESS_THRESHOLD_MS = 500L
// Horizontal Seek Sensitivity — total ms seeked per full screen width swipe
private const val SEEK_SWIPE_RANGE_MS = 120_000L
// Brightness Gesture Sensitivity — percent change per full screen height swipe
private const val BRIGHTNESS_SWIPE_SENSITIVITY = 100
// Bug fix #1: Volume Gesture Sensitivity
// One full vertical swipe covers exactly 100% so 0→100 takes one swipe, 100→200 takes a second swipe.
private const val VOLUME_SWIPE_SENSITIVITY = 100
// Gesture direction lock threshold — px of movement before axis is locked
private const val GESTURE_LOCK_THRESHOLD_PX = 20f
// Double Tap Zone — left 40% = backward, center 20% = play/pause, right 40% = forward
private const val DOUBLE_TAP_LEFT_ZONE = 0.40f
private const val DOUBLE_TAP_RIGHT_ZONE = 0.60f

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

// Streamability fallback — track consecutive stream failures per video session
// After 2 failures, mark video as download-required and show toast
private const val STREAM_FAIL_THRESHOLD = 2

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val repository: VideoRepository,
    // downloadManager kept for future explicit download actions from player
    private val downloadManager: HasikitDownloadManager,
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

    // Hidden cache removed: streaming videos are streamed only.
    // Download is an explicit user action from HomeScreen or LibraryScreen.

    suspend fun getInitialPosition(videoId: String): Long = withContext(Dispatchers.IO) {
        repository.getWatchProgress(videoId)?.lastPosition ?: 0L
    }

    suspend fun resolvePlayUrl(telegramFileId: String): String? = withContext(Dispatchers.IO) {
        val fileId = telegramFileId.toLongOrNull()?.toInt() ?: return@withContext null
        Log.d(TAG, "resolvePlayUrl fileId=$fileId")

        // Check if already fully downloaded before issuing a new DownloadFile request
        val existing = getFileInfo(fileId)
        if (existing?.local?.isDownloadingCompleted == true && existing.local.path.isNotBlank()) {
            Log.d(TAG, "resolvePlayUrl fileId=$fileId already complete path=${existing.local.path}")
            return@withContext "file://${existing.local.path}"
        }

        // If TDLib already has a path (partial download in progress), return it immediately
        if (existing?.local?.path?.isNotBlank() == true && existing.local.downloadedSize > 0) {
            Log.d(TAG, "resolvePlayUrl fileId=$fileId partial path=${existing.local.path} downloaded=${existing.local.downloadedSize}")
            return@withContext "file://${existing.local.path}"
        }

        // Start download with high priority (1 = highest), synchronous=true so TDLib allocates path immediately
        val downloadResult = suspendCancellableCoroutine<TdApi.File?> { cont ->
            telegramClientService.send(TdApi.DownloadFile(fileId, 1, 0, 0, true)) { result ->
                when (result) {
                    is TdApi.File -> {
                        Log.d(TAG, "resolvePlayUrl DownloadFile result fileId=$fileId path=${result.local.path} active=${result.local.isDownloadingActive} complete=${result.local.isDownloadingCompleted}")
                        cont.resume(result)
                    }
                    is TdApi.Error -> {
                        Log.e(TAG, "resolvePlayUrl DownloadFile error ${result.code}: ${result.message}")
                        cont.resume(null)
                    }
                    else -> cont.resume(null)
                }
            }
            cont.invokeOnCancellation {}
        }

        // If synchronous DownloadFile returned a usable path, return it immediately
        if (downloadResult != null) {
            val local = downloadResult.local
            if (local.path.isNotBlank() && (local.isDownloadingCompleted || local.isDownloadingActive || local.downloadedSize > 0)) {
                Log.d(TAG, "resolvePlayUrl immediate path fileId=$fileId path=${local.path}")
                return@withContext "file://${local.path}"
            }
        }

        // Poll until TDLib allocates the file path (path becomes non-blank once TDLib reserves disk space)
        var attempts = 0
        while (attempts < 60) { // 60s max
            val file = getFileInfo(fileId)
            if (file != null) {
                val local = file.local
                when {
                    local.isDownloadingCompleted && local.path.isNotBlank() -> {
                        Log.d(TAG, "resolvePlayUrl complete fileId=$fileId path=${local.path}")
                        return@withContext "file://${local.path}"
                    }
                    // Return as soon as path is allocated — ExoPlayer can stream from a partial file
                    local.path.isNotBlank() && local.downloadedSize > 0 -> {
                        Log.d(TAG, "resolvePlayUrl streaming fileId=$fileId path=${local.path} downloaded=${local.downloadedSize}")
                        return@withContext "file://${local.path}"
                    }
                    // Path allocated but no bytes yet — still return so ExoPlayer can start buffering
                    local.path.isNotBlank() && local.isDownloadingActive -> {
                        Log.d(TAG, "resolvePlayUrl path allocated fileId=$fileId path=${local.path}")
                        return@withContext "file://${local.path}"
                    }
                }
            }
            delay(500)
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
    isStreamable: Boolean = true,
    isDownloaded: Boolean = false,
    onBack: () -> Unit,
    viewModel: PlayerViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val activity = context as? Activity

    val isPlaying by player.isPlaying.collectAsState()
    val isBuffering by player.isBuffering.collectAsState()
    // Video completion state — true when ExoPlayer reaches STATE_ENDED
    val isEnded by player.isEnded.collectAsState()
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
    // Local subtitle URI selected by the user via file picker — persisted per session
    var localSubtitleUri by remember { mutableStateOf<Uri?>(null) }
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
    // Horizontal seek preview — shows target timestamp while finger is dragging
    var seekPreviewTime by remember { mutableLongStateOf(0L) }
    var showSeekPreview by remember { mutableStateOf(false) }
    // Streamability fallback — counts consecutive stream errors; after threshold triggers download
    var streamFailCount by remember { mutableIntStateOf(0) }
    var showDownloadFallbackToast by remember { mutableStateOf(false) }
    // Bug fix #8: Video end countdown — 5-second overlay before auto-play or repeat
    var endCountdown by remember { mutableIntStateOf(0) }
    var showEndCountdown by remember { mutableStateOf(false) }

    // File picker launcher for local subtitle files (.srt .ass .vtt .sub)
    val subtitlePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            localSubtitleUri = uri
            // Persist read permission across sessions
            try {
                context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            } catch (e: Exception) {
                Log.w(TAG, "takePersistableUriPermission failed: ${e.message}")
            }
            player.loadLocalSubtitle(uri.toString())
            Log.d(TAG, "Local subtitle selected: $uri")
        }
    }

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

    // FIX #14 — Read player preferences from the shared settingsDataStore (same instance as SettingsViewModel)
    // Using settingsDataStore extension from SettingsScreen.kt to avoid multiple DataStore instances on same file
    val resumeAfterCall by context.settingsDataStore.data
        .map { it[KEY_RESUME_AFTER_CALL] ?: true }
        .collectAsState(initial = true)
    val pauseOnHeadphoneRemoval by context.settingsDataStore.data
        .map { it[KEY_PAUSE_ON_HEADPHONE] ?: true }
        .collectAsState(initial = true)
    val backgroundAudio by context.settingsDataStore.data
        .map { it[KEY_BACKGROUND_AUDIO] ?: true }
        .collectAsState(initial = true)
    // Autoplay next video preference — default true (auto play next after completion)
    val autoplayNext by context.settingsDataStore.data
        .map { it[KEY_AUTOPLAY_NEXT] ?: true }
        .collectAsState(initial = true)

    // Track whether playback was interrupted by a phone call so we can resume
    var wasPlayingBeforeCall by remember { mutableStateOf(false) }

    // Audio focus: request focus before playback to prevent crashes with other media apps
    // minSdk=26 so AudioFocusRequest is always available — no version guard needed
    val audioFocusRequest = remember {
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
    }

    // Request audio focus on enter, abandon on exit — minSdk=26 so AudioFocusRequest is always available
    DisposableEffect(Unit) {
        if (audioFocusRequest != null) audioManager.requestAudioFocus(audioFocusRequest)
        onDispose {
            if (audioFocusRequest != null) audioManager.abandonAudioFocusRequest(audioFocusRequest)
        }
    }

    // Resume playback after phone calls
    // API 31+: TelephonyCallback (no permission needed for call state)
    // API 26–30: TelecomManager.isInCall() polled via audio focus loss — no READ_PHONE_STATE required
    DisposableEffect(resumeAfterCall) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
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
            // Guard against SecurityException if READ_PHONE_STATE permission is not granted
            val registered = try {
                telephonyManager.registerTelephonyCallback(context.mainExecutor, callback)
                true
            } catch (e: SecurityException) {
                Log.w(TAG, "TelephonyCallback not registered — READ_PHONE_STATE not granted: ${e.message}")
                false
            }
            onDispose { if (registered) telephonyManager.unregisterTelephonyCallback(callback) }
        } else {
            // API 26–30: TelecomManager.isInCall() requires no permission for self-managed calls
            // Audio focus loss (AUDIOFOCUS_LOSS) already pauses playback during calls via audioFocusRequest.
            // Here we additionally track call end via TelecomManager to support resumeAfterCall.
            val telecomManager = context.getSystemService(Context.TELECOM_SERVICE) as TelecomManager
            val receiver = object : android.content.BroadcastReceiver() {
                override fun onReceive(ctx: Context?, intent: Intent?) {
                    if (intent?.action != TelephonyManager.ACTION_PHONE_STATE_CHANGED) return
                    val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
                    when (state) {
                        TelephonyManager.EXTRA_STATE_RINGING,
                        TelephonyManager.EXTRA_STATE_OFFHOOK -> {
                            if (player.isPlaying.value) { wasPlayingBeforeCall = true; player.pause() }
                        }
                        TelephonyManager.EXTRA_STATE_IDLE -> {
                            // Double-check with TelecomManager — no permission needed
                            if (!telecomManager.isInCall) {
                                if (resumeAfterCall && wasPlayingBeforeCall) { wasPlayingBeforeCall = false; player.resume() }
                                else wasPlayingBeforeCall = false
                            }
                        }
                    }
                }
            }
            // ACTION_PHONE_STATE_CHANGED broadcast requires no permission to receive on API 26+
            val filter = IntentFilter(TelephonyManager.ACTION_PHONE_STATE_CHANGED)
            context.registerReceiver(receiver, filter)
            onDispose { context.unregisterReceiver(receiver) }
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

    // Bug fix #1: Volume Gesture Sensitivity
    // One full vertical swipe = 0→100%. Second swipe = 100→200% (software amplification).
    // 0-100% controls system volume; 101-200% is ExoPlayer software gain.
    fun applyVolume(pct: Int) {
        val clamped = pct.coerceIn(0, 200)
        virtualVolumePct = clamped
        // 0-100%: map to system volume range
        val newSystemVol = (minOf(clamped, 100) * maxSystemVolume / 100f).toInt().coerceIn(0, maxSystemVolume)
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, newSystemVol, 0)
        // 101-200%: ExoPlayer software amplification (handled in HasikitPlayer.setVolume)
        player.setVolume(clamped)
        volumeFeedback = "${when { clamped == 0 -> "🔇"; clamped <= 50 -> "🔈"; clamped <= 100 -> "🔉"; else -> "🔊" }} Volume $clamped%"
    }

    fun applyBrightness(pct: Int) {
        val clamped = pct.coerceIn(1, 100)
        brightnessPct = clamped
        activity?.window?.let { w -> val p = w.attributes; p.screenBrightness = clamped / 100f; w.attributes = p }
        brightnessFeedback = "☀ Brightness $clamped%"
    }

    // Manual control toggle — single tap shows if hidden, hides if visible; no auto-hide
    fun toggleControls() {
        if (!isLocked) showControls = !showControls
    }

    // resetControlsTimer kept for lock/unlock flow — just shows controls without auto-hide
    fun resetControlsTimer() {
        if (!isLocked) showControls = true
    }

    DisposableEffect(videoId) {
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
        scope.launch {
            // Re-initialize player in case it was released by a previous session
            player.initialize()
            val startPos = viewModel.getInitialPosition(videoId)
            val resolvedUrl = when {
                playUrl.isNotBlank() -> playUrl
                telegramFileId.isNotBlank() -> viewModel.resolvePlayUrl(telegramFileId) ?: return@launch
                else -> return@launch
            }
            player.playVideo(url = resolvedUrl, startPosition = startPos, videoId = videoId, title = title, thumbnailPath = null)
        }
        scope.launch { while (isActive) { currentPosition = player.getCurrentPosition(); duration = player.getDuration(); delay(500) } }
        scope.launch { while (isActive) { delay(5000); if (player.getDuration() > 0) viewModel.saveProgress(videoId, player.getCurrentPosition(), player.getDuration()) } }
        onDispose {
            scope.cancel()
            viewModel.saveProgress(videoId, player.getCurrentPosition(), player.getDuration())
            // Stop and reset player instead of full release — player is @Singleton and reused across screens
            player.stop()
        }
    }

    DisposableEffect(Unit) {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        onDispose {
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            activity?.window?.let { w -> val p = w.attributes; p.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE; w.attributes = p }
        }
    }

    // Bug fix #8: Video end countdown — show 5s countdown then auto-play next or repeat
    LaunchedEffect(isEnded) {
        if (isEnded) {
            showEndCountdown = true
            endCountdown = 5
            while (endCountdown > 0) {
                delay(1000L)
                endCountdown--
            }
            showEndCountdown = false
            // Restart from beginning for both autoplayNext and repeat modes
            // (next-video queue wiring is handled at the navigation layer)
            player.restartFromBeginning()
        } else {
            showEndCountdown = false
            endCountdown = 0
        }
    }

    // Streamability fallback — if stream fails twice, show toast
    LaunchedEffect(error) {
        if (error != null) {
            streamFailCount++
            if (streamFailCount >= STREAM_FAIL_THRESHOLD) {
                showDownloadFallbackToast = true
            }
        }
    }

    // Show download fallback toast
    if (showDownloadFallbackToast) {
        LaunchedEffect(Unit) {
            android.widget.Toast.makeText(
                context,
                "This video cannot be streamed. Please download first.",
                android.widget.Toast.LENGTH_LONG
            ).show()
            showDownloadFallbackToast = false
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        AndroidView(
            factory = { ctx ->
                // FIX: black screen — inflate PlayerView from XML so app:surface_type="texture_view"
                // can be applied. PlayerView in Media3 1.5.1 does not expose setSurfaceType() or
                // setVideoTextureView() as public APIs; the only supported way to set surface type
                // programmatically is via XML inflation.
                val inflated = android.view.LayoutInflater.from(ctx)
                    .inflate(com.trixsearch.hasikit.R.layout.player_view_texture, null, false) as PlayerView
                inflated.apply {
                    this.player = player.getPlayerInstance()
                    useController = false
                    resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                    post { requestLayout(); invalidate() }
                }
            },
            update = { view ->
                val instance = player.getPlayerInstance()
                if (view.player !== instance) {
                    // FIX: black screen — when player instance changes, re-attach and force surface refresh
                    view.player = instance
                    Log.d(TAG, "[PLAYER_VIEW] player instance changed — forcing surface refresh")
                    view.post { view.requestLayout(); view.invalidate() }
                }
                view.resizeMode = fitMode.resizeMode
                view.scaleX = videoScale; view.scaleY = videoScale
            },
            modifier = Modifier.fillMaxSize()
        )

        if (!isLocked) {
            // Unified full-screen gesture handler
            // Priority: horizontal dominant → seek | left-vertical → brightness | right-vertical → volume
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(isLocked) {
                        // Axis-locked gesture: determine direction on first movement, then commit
                        awaitEachGesture {
                            val down = awaitFirstDown(requireUnconsumed = false)
                            val startX = down.position.x
                            val startY = down.position.y
                            var totalX = 0f
                            var totalY = 0f
                            var axis: String? = null // "h" = horizontal seek, "vL" = brightness, "vR" = volume
                            var seekBase = 0L
                            // Bug fix #1: capture gesture-start values so delta is relative to start, not accumulated per-event
                            var volumeBase = 0
                            var brightnessBase = 0

                            while (true) {
                                val event = awaitPointerEvent()
                                val change = event.changes.firstOrNull() ?: break
                                if (!change.pressed) break
                                val dx = change.position.x - startX
                                val dy = change.position.y - startY
                                totalX = dx
                                totalY = dy

                                // Lock axis once movement exceeds threshold
                                if (axis == null && (kotlin.math.abs(dx) > GESTURE_LOCK_THRESHOLD_PX || kotlin.math.abs(dy) > GESTURE_LOCK_THRESHOLD_PX)) {
                                    axis = when {
                                        // Horizontal dominant → seek
                                        kotlin.math.abs(dx) >= kotlin.math.abs(dy) -> { seekBase = player.getCurrentPosition(); "h" }
                                        // Left half of screen → brightness
                                        startX < size.width / 2f -> { brightnessBase = brightnessPct; "vL" }
                                        // Right half of screen → volume
                                        else -> { volumeBase = virtualVolumePct; "vR" }
                                    }
                                }

                                when (axis) {
                                    "h" -> {
                                        // Horizontal Seek Sensitivity — SEEK_SWIPE_RANGE_MS controls total range
                                        val delta = (totalX / size.width * SEEK_SWIPE_RANGE_MS).toLong()
                                        val target = (seekBase + delta).coerceIn(0L, duration)
                                        // Show seek preview overlay while dragging
                                        seekPreviewTime = target
                                        showSeekPreview = true
                                        seekFeedback = "${if (delta >= 0) "⏩" else "⏪"} ${formatTime(target)}"
                                    }
                                    "vL" -> {
                                        // Bug fix #1: Volume Gesture Sensitivity — delta relative to gesture start
                                        val delta = (-totalY / size.height * BRIGHTNESS_SWIPE_SENSITIVITY).toInt()
                                        applyBrightness(brightnessBase + delta)
                                    }
                                    "vR" -> {
                                        // Bug fix #1: Volume Gesture Sensitivity — delta relative to gesture start, one swipe = 100%
                                        val delta = (-totalY / size.height * VOLUME_SWIPE_SENSITIVITY).toInt()
                                        applyVolume(volumeBase + delta)
                                    }
                                }
                                change.consume()
                            }

                            // On release: commit seek if horizontal gesture
                            if (axis == "h") {
                                val delta = (totalX / size.width * SEEK_SWIPE_RANGE_MS).toLong()
                                player.seekTo((seekBase + delta).coerceIn(0L, duration))
                                showSeekPreview = false
                                controlsScope.launch { delay(800); seekFeedback = null }
                            } else if (axis == "vL") {
                                controlsScope.launch { delay(1200); brightnessFeedback = null }
                            } else if (axis == "vR") {
                                controlsScope.launch { delay(1200); volumeFeedback = null }
                            }
                        }
                    }
                    .pointerInput(isLocked) {
                        detectTapGestures(
                            // Single tap toggles controls: show if hidden, hide if visible
                            onTap = { toggleControls() },
                            onDoubleTap = { tapPos ->
                                // Double Tap Zone — left 40% = backward, center 20% = play/pause, right 40% = forward
                                when {
                                    tapPos.x < size.width * DOUBLE_TAP_LEFT_ZONE -> {
                                        player.seekTo((player.getCurrentPosition() - SEEK_INCREMENT_MS).coerceAtLeast(0L))
                                        seekFeedback = "⏪ -10s"
                                        controlsScope.launch { delay(800); seekFeedback = null }
                                    }
                                    tapPos.x > size.width * DOUBLE_TAP_RIGHT_ZONE -> {
                                        player.seekTo((player.getCurrentPosition() + SEEK_INCREMENT_MS).coerceAtLeast(0L))
                                        seekFeedback = "⏩ +10s"
                                        controlsScope.launch { delay(800); seekFeedback = null }
                                    }
                                    else -> {
                                        // Center zone — double tap toggles play/pause
                                        if (isPlaying) player.pause() else player.resume()
                                    }
                                }
                            },
                            onLongPress = { tapPos ->
                                // Long press right side — activate 2x speed
                                if (tapPos.x >= size.width / 2f) {
                                    prevSpeedRef.floatValue = playbackSpeed
                                    player.setSpeed(2f)
                                    speedFeedback = "⚡ 2x"
                                }
                            }
                        )
                    }
                    // Release detection for long-press 2x speed restore
                    .pointerInput(isLocked) {
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
                    .pointerInput(isLocked) {
                        detectTransformGestures { _, _, zoom, _ -> videoScale = (videoScale * zoom).coerceIn(0.5f, 3f) }
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

        // Overlay Label Position — all feedback pills placed above center controls to avoid overlapping play/pause
        // Adjust the offset(y = ...) value to move labels higher or lower relative to screen center
        seekFeedback?.let { FeedbackPill(it, Modifier.align(Alignment.Center).offset(y = (-80).dp)) }
        // Seek preview overlay — large timestamp shown during horizontal swipe
        if (showSeekPreview) {
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(y = (-120).dp)
                    .background(Color.Black.copy(alpha = 0.75f), RoundedCornerShape(10.dp))
                    .padding(horizontal = 20.dp, vertical = 10.dp)
            ) {
                Text(
                    text = formatTime(seekPreviewTime),
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        speedFeedback?.let { FeedbackPill(it, Modifier.align(Alignment.Center).offset(y = (-80).dp)) }
        // Volume indicator on left side so finger on right doesn’t cover it
        volumeFeedback?.let { FeedbackPill(it, Modifier.align(Alignment.CenterStart).padding(start = 40.dp).offset(y = (-80).dp)) }
        // Brightness indicator on right side so finger on left doesn’t cover it
        brightnessFeedback?.let { FeedbackPill(it, Modifier.align(Alignment.CenterEnd).padding(end = 40.dp).offset(y = (-80).dp)) }
        // Aspect ratio label — same above-center position
        fitOverlayText?.let { FeedbackPill(it, Modifier.align(Alignment.Center).offset(y = (-80).dp)) }
        lockedTapMessage?.let { FeedbackPill(it, Modifier.align(Alignment.Center).offset(y = (-80).dp)) }

        // Buffering indicator — shown centered whenever player is in BUFFERING state
        // Hidden when error is shown to avoid overlapping indicators
        if (isBuffering && error == null) CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = Color.White, strokeWidth = 3.dp)

        // Bug fix #8: Video end countdown overlay — 5s before auto-play/repeat, user can cancel
        if (showEndCountdown) {
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .background(Color.Black.copy(alpha = 0.75f), RoundedCornerShape(16.dp))
                    .padding(horizontal = 28.dp, vertical = 20.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        if (autoplayNext) "Playing next in $endCountdown…" else "Replaying in $endCountdown…",
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(12.dp))
                    TextButton(onClick = {
                        // Cancel countdown — hide overlay and leave player in ended state
                        showEndCountdown = false
                        endCountdown = 0
                    }) {
                        Text("Cancel", color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }

        if (error != null) {
            Column(modifier = Modifier.align(Alignment.Center).padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.Error, null, tint = Color.Red, modifier = Modifier.size(48.dp))
                Spacer(Modifier.height(8.dp))
                Text(error!!.userMessage, color = Color.White, style = MaterialTheme.typography.bodyLarge)
                if (error!!.httpStatusCode != null) Text("HTTP ${error!!.httpStatusCode}", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                Text(error!!.message, color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                Spacer(Modifier.height(12.dp))
                Button(onClick = { if (playUrl.isNotBlank()) player.playVideo(url = playUrl, startPosition = currentPosition, videoId = videoId, title = title, thumbnailPath = null) }) { Text("Retry") }
            }
        }

        AnimatedVisibility(visible = showControls, enter = fadeIn(), exit = fadeOut(), modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = if (isLocked) 0.2f else 0.45f))) {
                if (!isLocked) {
                    // Bug fix #7: Safe area — add displayCutout insets so top bar clears punch hole/notch
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.TopCenter)
                            .windowInsetsPadding(WindowInsets.displayCutout)
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White) }
                        // Player Title Overlay — marquee scrolls right-to-left for long titles, hides with controls
                        Text(
                            text = title,
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier
                                .weight(1f)
                                .basicMarquee(iterations = Int.MAX_VALUE),
                            maxLines = 1
                        )
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
                                // Bug fix #3: Mute Audio — call muteAudio() so ExoPlayer volume is set to 0f
                                // and mute state is tracked for restore when user selects a track
                                DropdownMenuItem(
                                    text = { Text("Mute Audio") },
                                    leadingIcon = { Icon(Icons.AutoMirrored.Filled.VolumeOff, null, modifier = Modifier.size(18.dp)) },
                                    onClick = { player.muteAudio(); showAudioMenu = false }
                                )
                            }
                        }
                        Box {
                            IconButton(onClick = { showSubtitleMenu = true }) { Icon(Icons.Default.ClosedCaption, "Subtitles", tint = Color.White) }
                            DropdownMenu(expanded = showSubtitleMenu, onDismissRequest = { showSubtitleMenu = false }) {
                                // Embedded subtitles — off option
                                DropdownMenuItem(text = { Text("Off") }, onClick = { player.selectSubtitleTrack(null); showSubtitleMenu = false })
                                // Embedded subtitle tracks from the media file
                                subtitleTracks.forEach { track ->
                                    DropdownMenuItem(
                                        text = { Text(track.label, fontWeight = if (track.isSelected) FontWeight.Bold else FontWeight.Normal) },
                                        onClick = { player.selectSubtitleTrack(track.groupIndex); showSubtitleMenu = false }
                                    )
                                }
                                // External subtitle — load local file via Android file picker (.srt .ass .vtt .sub)
                                DropdownMenuItem(
                                    text = { Text("Load Local Subtitle") },
                                    leadingIcon = { Icon(Icons.Default.FolderOpen, null, modifier = Modifier.size(18.dp)) },
                                    onClick = {
                                        showSubtitleMenu = false
                                        subtitlePickerLauncher.launch(arrayOf(
                                            "application/x-subrip",
                                            "text/x-ssa",
                                            "text/vtt",
                                            "application/octet-stream",
                                            "text/plain"
                                        ))
                                    }
                                )
                                // Show currently loaded local subtitle filename if any
                                if (localSubtitleUri != null) {
                                    DropdownMenuItem(
                                        text = { Text("✓ ${localSubtitleUri?.lastPathSegment ?: "subtitle"}", fontWeight = FontWeight.Bold, maxLines = 1) },
                                        onClick = { showSubtitleMenu = false }
                                    )
                                }
                            }
                        }
                        IconButton(onClick = {
                            isLandscape = !isLandscape
                            activity?.requestedOrientation = if (isLandscape) ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE else ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
                        }) { Icon(Icons.Default.ScreenRotation, "Rotate", tint = Color.White) }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            // PiP — use actual video dimensions for correct surface sizing; no empty regions
                            IconButton(onClick = {
                                val vw = player.getVideoWidth()
                                val vh = player.getVideoHeight()
                                val rational = if (vw > 0 && vh > 0) {
                                    // Clamp to Android PiP allowed range (min 1:2.39, max 2.39:1)
                                    val ratio = vw.toFloat() / vh
                                    val clamped = ratio.coerceIn(1f / 2.39f, 2.39f)
                                    if (clamped >= 1f) Rational((clamped * 100).toInt(), 100)
                                    else Rational(100, (100 / clamped).toInt().coerceAtLeast(1))
                                } else Rational(16, 9)
                                activity?.enterPictureInPictureMode(
                                    PictureInPictureParams.Builder().setAspectRatio(rational).build()
                                )
                            }) {
                                Icon(Icons.Default.PictureInPicture, "PiP", tint = Color.White)
                            }
                        }
                    }

                    // Center controls
                    Row(modifier = Modifier.align(Alignment.Center), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        IconButton(onClick = { player.seekTo(currentPosition - SEEK_INCREMENT_MS) }) { Icon(Icons.Default.Replay10, "Rewind", tint = Color.White, modifier = Modifier.size(44.dp)) }
                        // Video completion — if ended, show Replay icon; press restarts from beginning (YouTube-style)
                        IconButton(onClick = {
                            when {
                                isEnded -> player.restartFromBeginning()
                                isPlaying -> player.pause()
                                else -> player.resume()
                            }
                        }, modifier = Modifier.size(64.dp)) {
                            Icon(
                                when {
                                    isEnded -> Icons.Default.Replay
                                    isPlaying -> Icons.Default.Pause
                                    else -> Icons.Default.PlayArrow
                                },
                                "Play/Pause",
                                tint = Color.White,
                                modifier = Modifier.size(64.dp)
                            )
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
