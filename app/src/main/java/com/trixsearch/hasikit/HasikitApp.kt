package com.trixsearch.hasikit

import android.app.Application
import android.util.Log
import com.trixsearch.hasikit.telegram.service.TelegramClientService
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

private const val TAG = "HasikitApp"

@HiltAndroidApp
class HasikitApp : Application() {

    @Inject lateinit var telegramClientService: TelegramClientService

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Application started — packageName=$packageName")
        Log.d(TAG, "Android SDK=${android.os.Build.VERSION.SDK_INT} device=${android.os.Build.MODEL}")
        logBuildConfig()
        logStorageInfo()
        telegramClientService.initClient()
    }

    private fun logBuildConfig() {
        Log.i(TAG, "API_ID=${BuildConfig.TELEGRAM_API_ID}")
        Log.i(TAG, "API_HASH_PRESENT=${BuildConfig.TELEGRAM_API_HASH.isNotBlank()}")
        Log.i(TAG, "DEMO_MODE=${BuildConfig.TELEGRAM_DEMO_MODE}")
    }

    private fun logStorageInfo() {
        try {
            val moviesDir = getExternalFilesDir(android.os.Environment.DIRECTORY_MOVIES)
            Log.d(TAG, "Downloads dir: $moviesDir exists=${moviesDir?.exists()}")
            val cache = cacheDir
            Log.d(TAG, "Cache dir: $cache exists=${cache.exists()}")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to log storage info", e)
        }
    }
}
