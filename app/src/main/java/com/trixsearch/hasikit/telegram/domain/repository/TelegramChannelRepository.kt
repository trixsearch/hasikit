package com.trixsearch.hasikit.telegram.domain.repository

import com.trixsearch.hasikit.telegram.domain.model.TelegramMedia

interface TelegramChannelRepository {

    /**
     * Resolve @testhasikit and log channel info.
     * Returns the numeric chat ID on success.
     */
    suspend fun resolveChannel(username: String): Result<Long>

    /**
     * Load up to [limit] media messages from [chatId].
     * Pass [offsetMessageId] = 0 for the first page;
     * pass the last loaded message ID for subsequent pages.
     */
    suspend fun getChannelMedia(
        chatId: Long,
        offsetMessageId: Long = 0,
        limit: Int = 50
    ): Result<List<TelegramMedia>>

    /**
     * Search the full channel history by [query].
     * Matches fileName, title, caption.
     */
    suspend fun searchChannelMedia(
        chatId: Long,
        query: String,
        limit: Int = 50
    ): Result<List<TelegramMedia>>

    /**
     * Resolve a streaming/download URL for [fileId].
     * Returns the local path after TDLib downloads the file header.
     */
    suspend fun resolveFileUrl(fileId: Long): Result<String>
}
