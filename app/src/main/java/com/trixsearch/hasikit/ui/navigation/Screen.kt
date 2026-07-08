package com.trixsearch.hasikit.ui.navigation

import java.net.URLDecoder
import java.net.URLEncoder

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Search : Screen("search")
    object Library : Screen("library")
    object Settings : Screen("settings")
    object Auth : Screen("auth")
    object RequestContent : Screen("request_content")
    object Language : Screen("language")
    // Advanced Settings sub-screen for dangerous/advanced options
    object AdvancedSettings : Screen("advanced_settings")
    object Player : Screen("player/{videoId}") {
        fun createRoute(videoId: String): String {
            val encoded = URLEncoder.encode(videoId, "UTF-8")
            return "player/$encoded"
        }
        fun decodeId(raw: String): String = URLDecoder.decode(raw, "UTF-8")
    }
}
