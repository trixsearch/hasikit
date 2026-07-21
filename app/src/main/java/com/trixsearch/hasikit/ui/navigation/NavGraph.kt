package com.trixsearch.hasikit.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import android.util.Log
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.trixsearch.hasikit.player.HasikitPlayer
import com.trixsearch.hasikit.telegram.domain.model.AuthState
import com.trixsearch.hasikit.ui.screens.auth.AuthScreen
import com.trixsearch.hasikit.ui.screens.auth.AuthViewModel
import com.trixsearch.hasikit.ui.screens.home.HomeScreen
import com.trixsearch.hasikit.ui.screens.home.HomeViewModel
import com.trixsearch.hasikit.ui.screens.library.LibraryScreen
import com.trixsearch.hasikit.ui.screens.player.PlayerScreen
import com.trixsearch.hasikit.ui.screens.search.SearchScreen
import com.trixsearch.hasikit.ui.screens.request.RequestContentScreen
import com.trixsearch.hasikit.ui.screens.settings.AdvancedSettingsScreen
import com.trixsearch.hasikit.ui.screens.settings.LanguageScreen
import com.trixsearch.hasikit.ui.screens.settings.SettingsScreen
import com.trixsearch.hasikit.ui.screens.settings.StorageManagementScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    player: HasikitPlayer
) {
    val authViewModel: AuthViewModel = hiltViewModel()
    val authState by authViewModel.authState.collectAsState()

    // Single HomeViewModel instance scoped to the NavGraph so HomeScreen and SearchScreen share it
    val homeViewModel: HomeViewModel = hiltViewModel()

    // Always start on Auth; navigate to Home once session restore confirms authenticated.
    NavHost(
        navController = navController,
        startDestination = Screen.Auth.route
    ) {
        composable(Screen.Auth.route) {
            // Login flash fix: only navigate to Home when Authenticated.
            // AuthScreen shows a spinner for AuthState.Loading so the login form
            // never flashes during session restoration.
            LaunchedEffect(authState) {
                if (authState is AuthState.Authenticated) {
                    Log.d("NavGraph", "[AUTH] Authenticated — navigating to Home")
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Auth.route) { inclusive = true }
                    }
                }
            }
            // AuthScreen handles Loading (spinner), CodeSent (OTP), and Unauthenticated (phone form).
            // It never shows the phone form while Loading, so no login flash occurs.
            AuthScreen(
                onAuthenticated = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Auth.route) { inclusive = true }
                    }
                },
                viewModel = authViewModel
            )
        }

        composable(Screen.Home.route) {
            // Reuse the NavGraph-scoped HomeViewModel — prevents double instantiation and double fetches
            HomeScreen(navController, homeViewModel)
        }
        composable(Screen.Search.route) {
            val searchViewModel: com.trixsearch.hasikit.ui.screens.search.SearchViewModel = hiltViewModel()
            // Pass local video cache from the shared HomeViewModel so Stage 1 search works immediately
            val videos by homeViewModel.videos.collectAsState()
            LaunchedEffect(videos) { searchViewModel.setLocalVideos(videos) }
            SearchScreen(navController, searchViewModel)
        }
        composable(Screen.Library.route) {
            LibraryScreen(navController)
        }
        composable(Screen.Settings.route) {
            SettingsScreen(navController)
        }
        composable(Screen.RequestContent.route) {
            RequestContentScreen(navController)
        }
        composable(Screen.Language.route) {
            LanguageScreen(navController)
        }
        // Advanced Settings sub-screen wired into nav graph
        composable(Screen.AdvancedSettings.route) {
            AdvancedSettingsScreen(navController)
        }
        // Storage Management sub-screen under Advanced Settings
        composable(Screen.StorageManagement.route) {
            StorageManagementScreen(navController)
        }
        composable(
            route = Screen.Player.route,
            arguments = listOf(navArgument("videoId") { type = NavType.StringType })
        ) { backStackEntry ->
            val rawId = backStackEntry.arguments?.getString("videoId") ?: ""
            val videoId = Screen.Player.decodeId(rawId)

            if (videoId.startsWith("external_")) {
                PlayerScreen(
                    videoId = videoId,
                    title = "Video",
                    player = player,
                    videoUrl = "",
                    localPath = null,
                    telegramFileId = "",
                    onBack = { navController.popBackStack() }
                )
                return@composable
            }

            // Read videos from the shared HomeViewModel
            val videos by homeViewModel.videos.collectAsState()
            val video = videos.find { it.id == videoId }
            if (video != null) {
                PlayerScreen(
                    videoId = videoId,
                    title = video.title,
                    player = player,
                    videoUrl = video.videoUrl,
                    localPath = video.localPath,
                    telegramFileId = video.telegramFileId,
                    isStreamable = video.isStreamable,
                    isDownloaded = video.isDownloaded,
                    onBack = { navController.popBackStack() }
                )
            } else {
                val parts = videoId.split("_")
                val telegramFileId = if (parts.size == 2) parts[1] else ""
                PlayerScreen(
                    videoId = videoId,
                    title = "Loading…",
                    player = player,
                    videoUrl = "",
                    localPath = null,
                    telegramFileId = telegramFileId,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
