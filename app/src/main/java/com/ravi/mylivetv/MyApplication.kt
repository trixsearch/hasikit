package com.ravi.mylivetv

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication : Application() {

    val isDarkTheme = mutableStateOf(false)

    override fun onCreate() {
        super.onCreate()
    }

    fun toggleTheme() {
        isDarkTheme.value = !isDarkTheme.value
    }
}