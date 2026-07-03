package com.trixsearch.hasikit.telegram.data.session

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.trixsearch.hasikit.telegram.domain.model.TelegramUser
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "TelegramSession"
private val Context.telegramSessionStore by preferencesDataStore(name = "telegram_session")

private val KEY_IS_AUTHENTICATED = booleanPreferencesKey("is_authenticated")
private val KEY_USER_ID = longPreferencesKey("user_id")
private val KEY_FIRST_NAME = stringPreferencesKey("first_name")
private val KEY_LAST_NAME = stringPreferencesKey("last_name")
private val KEY_USERNAME = stringPreferencesKey("username")
private val KEY_PHONE = stringPreferencesKey("phone")
private val KEY_PHOTO_URL = stringPreferencesKey("photo_url")
// Session string — opaque token from the MTProto client (TDLib session file path or serialized session)
private val KEY_SESSION_STRING = stringPreferencesKey("session_string")

@Singleton
class TelegramSessionManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    val isAuthenticated: Flow<Boolean> = context.telegramSessionStore.data
        .map { it[KEY_IS_AUTHENTICATED] ?: false }

    val savedUser: Flow<TelegramUser?> = context.telegramSessionStore.data
        .map { prefs ->
            if (prefs[KEY_IS_AUTHENTICATED] != true) return@map null
            val id = prefs[KEY_USER_ID] ?: return@map null
            TelegramUser(
                id = id,
                firstName = prefs[KEY_FIRST_NAME] ?: "",
                lastName = prefs[KEY_LAST_NAME] ?: "",
                username = prefs[KEY_USERNAME],
                phoneNumber = prefs[KEY_PHONE] ?: "",
                profilePhotoUrl = prefs[KEY_PHOTO_URL]
            )
        }

    val sessionString: Flow<String?> = context.telegramSessionStore.data
        .map { it[KEY_SESSION_STRING] }

    suspend fun saveSession(user: TelegramUser, sessionString: String) {
        Log.d(TAG, "saveSession userId=${user.id} phone=${user.phoneNumber}")
        context.telegramSessionStore.edit { prefs ->
            prefs[KEY_IS_AUTHENTICATED] = true
            prefs[KEY_USER_ID] = user.id
            prefs[KEY_FIRST_NAME] = user.firstName
            prefs[KEY_LAST_NAME] = user.lastName
            prefs[KEY_USERNAME] = user.username ?: ""
            prefs[KEY_PHONE] = user.phoneNumber
            prefs[KEY_PHOTO_URL] = user.profilePhotoUrl ?: ""
            prefs[KEY_SESSION_STRING] = sessionString
        }
    }

    suspend fun clearSession() {
        Log.d(TAG, "clearSession")
        context.telegramSessionStore.edit { it.clear() }
    }

    suspend fun hasValidSession(): Boolean =
        context.telegramSessionStore.data.first()[KEY_IS_AUTHENTICATED] == true

    suspend fun getSessionString(): String? =
        context.telegramSessionStore.data.first()[KEY_SESSION_STRING]
}
