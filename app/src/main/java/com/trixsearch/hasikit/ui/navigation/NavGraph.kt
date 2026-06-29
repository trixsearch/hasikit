package com.trixsearch.hasikit.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.trixsearch.hasikit.player.HasikitPlayer
import com.trixsearch.hasikit.ui.screens.home.HomeScreen
import com.trixsearch.hasikit.ui.screens.home.HomeViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import com.trixsearch.hasikit.ui.screens.library.LibraryScreen
import com.trixsearch.hasikit.ui.screens.player.PlayerScreen
import com.trixsearch.hasikit.ui.screens.search.SearchScreen
import com.trixsearch.hasikit.ui.screens.settings.SettingsScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    player: HasikitPlayer
) {
    // Sharing HomeViewModel to get the same video list context if needed, 
    // but here we just need to find the video by ID.
    val homeViewModel: HomeViewModel = hiltViewModel()
    val videos by homeViewModel.videos.collectAsState()

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
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
                    player = player,
                    videoUrl = it.videoUrl,
                    localPath = it.localPath,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
