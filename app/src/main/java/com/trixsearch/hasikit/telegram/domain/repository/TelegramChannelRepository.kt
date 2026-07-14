package com.trixsearch.hasikit.telegram.domain.repository

import com.trixsearch.hasikit.telegram.config.TelegramSource
import com.trixsearch.hasikit.telegram.domain.model.TelegramMedia

interface TelegramChannelRepository {

    /**
     * Resolve a source to a numeric chat ID.
     * Handles @username, numeric chat IDs, and private invite links.
     */
    suspend fun resolveSource(source: TelegramSource): Result<Long>

    /** Legacy — resolve by username only */
    suspend fun resolveChannel(username: String): Result<Long>

    /**
     * Load up to [limit] media messages from [chatId].
     * Pass [offsetMessageId] = 0 for the first page.
     * Returns Pair(mediaList, rawMessageCount) — rawCount is used to determine hasMore
     * so non-video messages don't prematurely stop pagination.
     */
    suspend fun getChannelMedia(
        chatId: Long,
        offsetMessageId: Long = 0,
        limit: Int = 50
    ): Result<Pair<List<TelegramMedia>, Int>>

    /**
     * Search the channel history by [query].
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

    /**
     * Download a thumbnail file and return its local path.
     * Returns null if no thumbnail or download fails.
     */
    suspend fun downloadThumbnail(thumbnailFileId: Long): String?
}
