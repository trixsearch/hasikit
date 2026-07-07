package com.trixsearch.hasikit

import android.content.Intent
import android.Manifest
import android.app.PictureInPictureParams
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.Rational
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.trixsearch.hasikit.player.HasikitPlayer
import com.trixsearch.hasikit.telegram.domain.repository.TelegramAuthRepository
import com.trixsearch.hasikit.ui.navigation.NavGraph
import com.trixsearch.hasikit.ui.navigation.Screen
import com.trixsearch.hasikit.ui.theme.AppTheme
import com.trixsearch.hasikit.ui.theme.HasikitTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "MainActivity"
val Context.themeDataStore by preferencesDataStore(name = "hasikit_theme")
val THEME_KEY = stringPreferencesKey("app_theme")

// Routes where the bottom navigation bar should be hidden
private val HIDE_BOTTOM_NAV_ROUTES = setOf(
    Screen.Auth.route,
    Screen.Player.route
)

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var player: HasikitPlayer
    @Inject lateinit var telegramAuthRepository: TelegramAuthRepository

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> Log.d(TAG, "POST_NOTIFICATIONS granted=$granted") }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate")
        requestNotificationPermissionIfNeeded()

        // Restore Telegram session before UI renders
        lifecycleScope.launch {
            Log.d(TAG, "restoreSession start")
            telegramAuthRepository.restoreSession()
            Log.d(TAG, "restoreSession complete")
        }

        enableEdgeToEdge()

        // Handle external video intent (ACTION_VIEW from other apps)
        val externalVideoUri = if (intent?.action == Intent.ACTION_VIEW) intent.data else null

        setContent {
            val themeString by themeDataStore.data
                .map { it[THEME_KEY] ?: AppTheme.DARK.name }
                .collectAsStateWithLifecycle(initialValue = AppTheme.DARK.name)

            val appTheme = remember(themeString) {
                runCatching { AppTheme.valueOf(themeString) }.getOrDefault(AppTheme.DARK)
            }

            HasikitTheme(appTheme = appTheme) {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route
                val showBottomBar = currentRoute !in HIDE_BOTTOM_NAV_ROUTES

                // Navigate to external video player if launched via ACTION_VIEW
                LaunchedEffect(externalVideoUri) {
                    if (externalVideoUri != null) {
                        val videoId = "external_${System.currentTimeMillis()}"
                        // Store the URI in player directly and navigate
                        player.initialize()
                        player.playVideo(url = externalVideoUri.toString(), videoId = videoId, title = externalVideoUri.lastPathSegment ?: "Video")
                        navController.navigate(Screen.Player.createRoute(videoId)) {
                            popUpTo(Screen.Home.route)
                        }
                    }
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        if (showBottomBar) {
                            NavigationBar {
                                val items = listOf(
                                    Triple(Screen.Home, "Home", Icons.Default.Home),
                                    Triple(Screen.Search, "Search", Icons.Default.Search),
                                    Triple(Screen.Library, "Library", Icons.Default.VideoLibrary),
                                    Triple(Screen.Settings, "Settings", Icons.Default.Settings)
                                )
                                items.forEach { (screen, label, icon) ->
                                    NavigationBarItem(
                                        icon = { Icon(icon, contentDescription = label) },
                                        label = { Text(label) },
                                        selected = navBackStackEntry?.destination?.hierarchy
                                            ?.any { it.route == screen.route } == true,
                                        onClick = {
                                            navController.navigate(screen.route) {
                                                popUpTo(navController.graph.findStartDestination().id) {
                                                    saveState = true
                                                }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        NavGraph(navController = navController, player = player)
                    }
                }
            }
        }
    }

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && player.isPlaying.value) {
            Log.d(TAG, "onUserLeaveHint — entering PiP")
            enterPictureInPictureMode(
                PictureInPictureParams.Builder()
                    .setAspectRatio(Rational(16, 9))
                    .build()
            )
        }
    }

    override fun onPictureInPictureModeChanged(
        isInPiPMode: Boolean,
        newConfig: android.content.res.Configuration
    ) {
        super.onPictureInPictureModeChanged(isInPiPMode, newConfig)
        Log.d(TAG, "PiP mode changed: isInPiP=$isInPiPMode")
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permission = Manifest.permission.POST_NOTIFICATIONS
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                notificationPermissionLauncher.launch(permission)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy")
    }
}
