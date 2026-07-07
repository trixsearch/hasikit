package com.trixsearch.hasikit.util

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.langDataStore by preferencesDataStore(name = "hasikit_language")
private val KEY_LANGUAGE = stringPreferencesKey("app_language")

enum class AppLanguage(val code: String, val displayName: String, val nativeName: String) {
    SYSTEM("system", "System Default", "System Default"),
    ENGLISH("en", "English", "English"),
    HINDI("hi", "Hindi", "हिन्दी"),
    MARATHI("mr", "Marathi", "मराठी"),
    TAMIL("ta", "Tamil", "தமிழ்"),
    TELUGU("te", "Telugu", "తెలుగు"),
    KANNADA("kn", "Kannada", "ಕನ್ನಡ"),
    MALAYALAM("ml", "Malayalam", "മലയാളം"),
    GUJARATI("gu", "Gujarati", "ગુજરાતી"),
    PUNJABI("pa", "Punjabi", "ਪੰਜਾਬੀ"),
    BENGALI("bn", "Bengali", "বাংলা");

    companion object {
        fun fromCode(code: String): AppLanguage =
            entries.firstOrNull { it.code == code } ?: SYSTEM
    }
}

@Singleton
class LanguageManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    val selectedLanguage: Flow<AppLanguage> = context.langDataStore.data
        .map { prefs -> AppLanguage.fromCode(prefs[KEY_LANGUAGE] ?: AppLanguage.SYSTEM.code) }

    suspend fun setLanguage(language: AppLanguage) {
        context.langDataStore.edit { it[KEY_LANGUAGE] = language.code }
    }
}
