package com.ravi.mylivetv

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.mutableStateOf
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication : Application() {

    val isDarkTheme = mutableStateOf(false)
    private lateinit var prefs: SharedPreferences

    override fun onCreate() {
        super.onCreate()
        prefs = getSharedPreferences("theme_prefs", Context.MODE_PRIVATE)
        isDarkTheme.value = prefs.getBoolean("is_dark_theme", false)
    }

    fun toggleTheme() {
        isDarkTheme.value = !isDarkTheme.value
        prefs.edit().putBoolean("is_dark_theme", isDarkTheme.value).apply()
    }
}