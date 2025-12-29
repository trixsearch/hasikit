package com.ravi.mylivetv.ui.player

import android.app.Activity
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.view.View
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.ravi.mylivetv.ui.utils.enterFullscreen
import com.ravi.mylivetv.ui.utils.exitFullscreen
import com.ravi.mylivetv.ui.utils.keepScreenOn

@Composable
fun PlayerScreen(
    navController: NavController,
    streamUrl: String,
    channelName: String = "",
    logoUrl: String = "",
    category: String = "",
    categoryUrl: String = "",
    channelIndex: Int = -1,
    viewModel: PlayerViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val lifecycleOwner = LocalLifecycleOwner.current
    val configuration = LocalConfiguration.current

    val uiState by viewModel.uiState.collectAsState()
    var isControllerVisible by remember { mutableStateOf(true) }

    var resizeMode by rememberSaveable {
        mutableStateOf(PlayerResizeMode.FIT)
    }

    LaunchedEffect(streamUrl) {
        viewModel.initializePlayer(streamUrl, channelName, logoUrl, category, categoryUrl, channelIndex)
    }

    LaunchedEffect(uiState.isPlaying) {
        activity?.keepScreenOn(uiState.isPlaying)
    }

    LaunchedEffect(configuration.orientation) {
        if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            activity?.enterFullscreen()
        } else {
            activity?.exitFullscreen()
        }
    }
    
    // Handle swipe gestures for channel navigation
    var swipeOffsetX by remember { mutableStateOf(0f) }
    val swipeThreshold = 200f // Minimum swipe distance in pixels

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> {
                    viewModel.resumePlayer()

                }
                Lifecycle.Event.ON_STOP -> {
                    viewModel.savePlaybackState()
                    viewModel.pausePlayer()
                }
                else -> Unit
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)

            activity?.keepScreenOn(false)
            activity?.requestedOrientation =
                ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED

            viewModel.releasePlayer()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragEnd = {
                        if (swipeOffsetX > swipeThreshold) {
                            // Swiped right -> Previous channel
                            val prevChannel = viewModel.switchToPreviousChannel()
                            if (prevChannel == null) {
                                Toast.makeText(context, "No previous channel available", Toast.LENGTH_SHORT).show()
                            }
                        } else if (swipeOffsetX < -swipeThreshold) {
                            // Swiped left -> Next channel
                            val nextChannel = viewModel.switchToNextChannel()
                            if (nextChannel == null) {
                                Toast.makeText(context, "No next channel available", Toast.LENGTH_SHORT).show()
                            }
                        }
                        swipeOffsetX = 0f
                    },
                    onDragCancel = {
                        swipeOffsetX = 0f
                    },
                    onHorizontalDrag = { _, dragAmount ->
                        swipeOffsetX += dragAmount
                    }
                )
            }
    ) {

        viewModel.player?.let { exoPlayer ->

            val playerView = remember {
                PlayerView(context).apply {
                    useController = true
                    controllerShowTimeoutMs = 3000 // Auto-hide controls after 3 seconds
                    controllerHideOnTouch = true
                    setShowNextButton(false)
                    setShowPreviousButton(false)
                    setShowBuffering(PlayerView.SHOW_BUFFERING_WHEN_PLAYING)
                    
                    // Enable D-pad navigation for Android TV
                    isFocusable = true
                    isFocusableInTouchMode = true
                    requestFocus()

                    setControllerVisibilityListener(
                        PlayerView.ControllerVisibilityListener { visibility ->
                            isControllerVisible = visibility == View.VISIBLE
                        }
                    )


                    setFullscreenButtonClickListener { isFullScreen ->
                        activity?.requestedOrientation =
                            if (isFullScreen)
                                ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
                            else
                                ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                    }
                }
            }

            AndroidView(
                factory = { playerView },
                update = {
                    if (it.player != exoPlayer) {
                        it.player = exoPlayer
                        // Show controls when player is attached
                        it.showController()
                    }
                    it.resizeMode = when (resizeMode) {
                        PlayerResizeMode.FIT ->
                            AspectRatioFrameLayout.RESIZE_MODE_FIT
                        PlayerResizeMode.ZOOM ->
                            AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                        PlayerResizeMode.FILL ->
                            AspectRatioFrameLayout.RESIZE_MODE_FILL
                    }
                    // Ensure D-pad focus is maintained
                    it.requestFocus()
            },
            modifier = Modifier.fillMaxSize()
        )
        }

        // Initial Loading Indicator - Show custom logo/loader only for FIRST TIME loading
        // After playback has started once, ExoPlayer's built-in buffering will handle mid-playback buffering
        if (uiState.isBuffering && !uiState.hasStartedPlaying && uiState.errorMessage == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                // Channel Thumbnail
                AsyncImage(
                    model = uiState.channelLogoUrl,
                    contentDescription = "Channel Logo",
                    modifier = Modifier
                        .size(160.dp)
                        .alpha(0.85f),
                    contentScale = ContentScale.Fit
                )
                // Loader Overlay
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(260.dp))
                    CircularProgressIndicator(
                        modifier = Modifier.size(48.dp),
                        color = Color.White,
                        strokeWidth = 4.dp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = "Loading stream...", color = Color.White, fontSize = 14.sp)
                }
            }
        }
        // Error Display
        uiState.errorMessage?.let { error ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.8f)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(32.dp)
                ) {
                    Text(text = "⚠️", fontSize = 48.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Playback Error",
                        color = Color.White,
                        fontSize = 20.sp,
                        style = MaterialTheme.typography.titleLarge
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Please Try to Play Another Channel",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = error,
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(onClick = { viewModel.retryPlayback() }) { Text("Retry") }
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = { navController.navigateUp() },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                    ) { Text("Go Back") }
                }
            }
        }

        // Back button and channel title - Top Left
        AnimatedVisibility(
            visible = isControllerVisible,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 10.dp, top = 30.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }

                if (uiState.channelName.isNotEmpty()) {
                    Text(
                        text = uiState.channelName,
                        color = Color.White,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }

        // Resize button - Top Right
        AnimatedVisibility(
            visible = isControllerVisible,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = 10.dp, top = 30.dp)
        ) {
            Surface(
                color = Color.Black.copy(alpha = 0.5f),
                shape = MaterialTheme.shapes.small
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 4.dp)
                ) {
                    IconButton(
                        onClick = {
                            resizeMode = when (resizeMode) {
                                PlayerResizeMode.FIT -> PlayerResizeMode.ZOOM
                                PlayerResizeMode.ZOOM -> PlayerResizeMode.FILL
                                PlayerResizeMode.FILL -> PlayerResizeMode.FIT
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = "Resize video",
                            tint = Color.White
                        )
                    }
                    Text(
                        text = when (resizeMode) {
                            PlayerResizeMode.FIT -> "FIT"
                            PlayerResizeMode.ZOOM -> "ZOOM"
                            PlayerResizeMode.FILL -> "FILL"
                        },
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
            }
        }
    }
}


//@Preview(showBackground = true, showSystemUi = true)
//@Composable
//fun PreviewPlayerScreen() {
//    val navController = rememberNavController()
//    // Note: Preview won't work with HiltViewModel, only for UI reference
//}

enum class PlayerResizeMode {
    FIT,
    ZOOM,
    FILL
}
