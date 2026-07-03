package com.trixsearch.hasikit.telegram.domain.repository

import com.trixsearch.hasikit.telegram.domain.model.TelegramMediaItem

interface TelegramMediaRepository {

    /**
     * Fetch the first [limit] media messages from the configured source channel.
     * Pass [offsetMessageId] = 0 for the initial load; pass the last loaded
     * message id to paginate forward.
     */
    suspend fun getChannelMedia(
        offsetMessageId: Long = 0,
        limit: Int = 50
    ): Result<List<TelegramMediaItem>>

    /**
     * Search the full channel history by [query] (matches fileName, caption, title).
     * Returns up to [limit] results.
     */
    suspend fun searchChannelMedia(
        query: String,
        limit: Int = 50
    ): Result<List<TelegramMediaItem>>

    /**
     * Resolve a local file path for [fileId].
     * Downloads the file if not already cached by TDLib.
     * Returns the absolute local path on success.
     */
    suspend fun resolveFileUrl(fileId: Long): Result<String>
}
