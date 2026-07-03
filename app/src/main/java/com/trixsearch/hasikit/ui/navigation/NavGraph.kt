package com.trixsearch.hasikit.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.trixsearch.hasikit.player.HasikitPlayer
import com.trixsearch.hasikit.ui.screens.auth.AuthScreen
import com.trixsearch.hasikit.ui.screens.auth.AuthViewModel
import com.trixsearch.hasikit.ui.screens.home.HomeScreen
import com.trixsearch.hasikit.ui.screens.home.HomeViewModel
import com.trixsearch.hasikit.ui.screens.library.LibraryScreen
import com.trixsearch.hasikit.ui.screens.player.PlayerScreen
import com.trixsearch.hasikit.ui.screens.search.SearchScreen
import com.trixsearch.hasikit.ui.screens.settings.SettingsScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    player: HasikitPlayer
) {
    val authViewModel: AuthViewModel = hiltViewModel()

    val homeViewModel: HomeViewModel = hiltViewModel()
    val videos by homeViewModel.videos.collectAsState()

    // AUTH BYPASS — temporary for demo/playback testing.
    // Restore auth-gated startDestination after TDLib integration:
    //   val startDestination = when (authState) {
    //       is AuthState.Authenticated -> Screen.Home.route
    //       else -> Screen.Auth.route
    //   }
    val startDestination = Screen.Home.route

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // ── Auth graph ────────────────────────────────────────────────────────
        composable(Screen.Auth.route) {
            AuthScreen(
                onAuthenticated = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Auth.route) { inclusive = true }
                    }
                },
                viewModel = authViewModel
            )
        }

        // ── Main graph ────────────────────────────────────────────────────────
        composable(Screen.Home.route) {
            HomeScreen(navController, homeViewModel)
        }
        composable(Screen.Search.route) {
            SearchScreen(navController)
        }
        composable(Screen.Library.route) {
            LibraryScreen(navController)
        }
        composable(Screen.Settings.route) {
            SettingsScreen(navController)
        }
        composable(
            route = Screen.Player.route,
            arguments = listOf(navArgument("videoId") { type = NavType.StringType })
        ) { backStackEntry ->
            val videoId = backStackEntry.arguments?.getString("videoId") ?: ""
            val video = videos.find { it.id == videoId }
            video?.let {
                PlayerScreen(
                    videoId = videoId,
                    title = it.title,
                    player = player,
                    videoUrl = it.videoUrl,
                    localPath = it.localPath,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
