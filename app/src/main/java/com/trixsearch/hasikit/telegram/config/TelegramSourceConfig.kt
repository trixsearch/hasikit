package com.trixsearch.hasikit.telegram.config

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.trixsearch.hasikit.BuildConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "TelegramSourceConfig"
private val Context.sourcesDataStore by preferencesDataStore(name = "hasikit_sources")
private val KEY_USER_SOURCES = stringPreferencesKey("user_sources")

/**
 * Represents a single Telegram content source.
 * identifier can be:
 *   - @username  (public channel/group)
 *   - -1001234567890  (numeric chat ID)
 *   - https://t.me/+xxxxxxxx  (private invite link)
 */
data class TelegramSource(
    val identifier: String,
    val displayName: String,
    val isOfficial: Boolean = false
) {
    /** Normalised for SearchPublicChat — strips @ and https://t.me/ */
    val username: String
        get() = identifier
            .removePrefix("https://t.me/")
            .removePrefix("@")
            .trim()

    /** True if this is a numeric chat ID (negative supergroup ID) */
    val isChatId: Boolean
        get() = identifier.trimStart('-').all { it.isDigit() } && identifier.startsWith("-")

    /** True if this is a private invite link */
    val isInviteLink: Boolean
        get() = identifier.startsWith("https://t.me/+") || identifier.startsWith("t.me/+")
}

@Singleton
class TelegramSourceConfig @Inject constructor(
    @ApplicationContext private val context: Context
) {
    val apiId: Int = BuildConfig.TELEGRAM_API_ID
    val apiHash: String = BuildConfig.TELEGRAM_API_HASH

    /**
     * Official sources bundled in the app.
     * Users cannot delete these.
     * Add more channels/groups here as the platform grows.
     */
    val officialSources: List<TelegramSource> = listOf(
        TelegramSource(
            identifier = "@testhasikit",
            displayName = "Hasikit",
            isOfficial = true
        )
        // Add more official sources here:
        // TelegramSource(identifier = "-1001234567890", displayName = "Movies", isOfficial = true),
        // TelegramSource(identifier = "https://t.me/+privatelink", displayName = "Premium", isOfficial = true),
    )

    /** User-added sources stored in DataStore (serialised as comma-separated "identifier|name" pairs) */
    val userSourcesFlow: Flow<List<TelegramSource>> = context.sourcesDataStore.data
        .map { prefs ->
            val raw = prefs[KEY_USER_SOURCES] ?: ""
            if (raw.isBlank()) emptyList()
            else raw.split(";;").mapNotNull { entry ->
                val parts = entry.split("|", limit = 2)
                if (parts.size == 2 && parts[0].isNotBlank())
                    TelegramSource(identifier = parts[0], displayName = parts[1], isOfficial = false)
                else null
            }
        }

    suspend fun addUserSource(source: TelegramSource) {
        context.sourcesDataStore.edit { prefs ->
            val existing = prefs[KEY_USER_SOURCES] ?: ""
            val entries = if (existing.isBlank()) mutableListOf() else existing.split(";;").toMutableList()
            val newEntry = "${source.identifier}|${source.displayName}"
            if (!entries.contains(newEntry)) {
                entries.add(newEntry)
                prefs[KEY_USER_SOURCES] = entries.joinToString(";;")
                Log.d(TAG, "addUserSource: ${source.identifier}")
            }
        }
    }

    suspend fun removeUserSource(identifier: String) {
        context.sourcesDataStore.edit { prefs ->
            val existing = prefs[KEY_USER_SOURCES] ?: ""
            val entries = existing.split(";;").filter { entry ->
                !entry.startsWith("$identifier|")
            }
            prefs[KEY_USER_SOURCES] = entries.joinToString(";;")
            Log.d(TAG, "removeUserSource: $identifier")
        }
    }

    // Legacy compat — single source channel from BuildConfig
    val sourceChannel: String = BuildConfig.TELEGRAM_SOURCE_CHANNEL
    val sourceChannelUsername: String
        get() = sourceChannel.removePrefix("https://t.me/").removePrefix("@").trim()
}
