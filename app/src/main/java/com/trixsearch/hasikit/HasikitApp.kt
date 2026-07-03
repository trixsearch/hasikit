package com.trixsearch.hasikit

import android.app.Application
import android.util.Log
import dagger.hilt.android.HiltAndroidApp

private const val TAG = "HasikitApp"

@HiltAndroidApp
class HasikitApp : Application() {

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Application started — packageName=$packageName")
        Log.d(TAG, "Android SDK=${android.os.Build.VERSION.SDK_INT} device=${android.os.Build.MODEL}")
        logStorageInfo()
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
