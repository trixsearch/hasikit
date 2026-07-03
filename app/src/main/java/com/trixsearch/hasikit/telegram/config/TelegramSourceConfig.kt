package com.trixsearch.hasikit.telegram.config

import com.trixsearch.hasikit.BuildConfig
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TelegramSourceConfig @Inject constructor() {
    val apiId: Int = BuildConfig.TELEGRAM_API_ID
    val apiHash: String = BuildConfig.TELEGRAM_API_HASH

    /**
     * Configurable source channel username or link.
     * Set TELEGRAM_SOURCE_CHANNEL in local.properties.
     * Example: @my_channel or https://t.me/my_channel
     */
    val sourceChannel: String = BuildConfig.TELEGRAM_SOURCE_CHANNEL

    /** Normalised username without @ prefix, or empty if not configured. */
    val sourceChannelUsername: String
        get() = sourceChannel
            .removePrefix("https://t.me/")
            .removePrefix("@")
            .trim()
}
