package com.trixsearch.hasikit.telegram.data.repository

import android.util.Log
import com.trixsearch.hasikit.telegram.config.TelegramSourceConfig
import com.trixsearch.hasikit.telegram.domain.model.TelegramMediaItem
import com.trixsearch.hasikit.telegram.domain.repository.TelegramMediaRepository
import com.trixsearch.hasikit.telegram.service.TelegramClientService
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "TelegramMediaRepo"

/**
 * Stub implementation — compiles without TDLib.
 *
 * When TDLib AAR is added to app/libs/:
 *   1. Add imports: org.drinkless.tdlib.Client, org.drinkless.tdlib.TdApi
 *   2. Replace each method body with the TDLib calls documented in the comments.
 *   3. Restore resolveChatId() and toMediaItem() extension helpers.
 */
@Singleton
class TelegramMediaRepositoryImpl @Inject constructor(
    private val clientService: TelegramClientService,
    private val config: TelegramSourceConfig
) : TelegramMediaRepository {

    /**
     * TDLib wiring:
     *   val client = clientService.getClient() as Client
     *   val chatId = resolveChatId(client, config.sourceChannelUsername)
     *   client.send(TdApi.GetChatHistory(chatId, offsetMessageId, 0, limit, false))
     *   → map TdApi.Messages to List<TelegramMediaItem>
     */
    override suspend fun getChannelMedia(
        offsetMessageId: Long,
        limit: Int
    ): Result<List<TelegramMediaItem>> {
        Log.d(TAG, "getChannelMedia stub — TDLib not yet integrated")
        return Result.success(emptyList())
    }

    /**
     * TDLib wiring:
     *   client.send(TdApi.SearchChatMessages(chatId, query, null, 0, 0, limit,
     *       TdApi.SearchMessagesFilterVideo(), 0))
     *   → map TdApi.FoundChatMessages to List<TelegramMediaItem>
     */
    override suspend fun searchChannelMedia(
        query: String,
        limit: Int
    ): Result<List<TelegramMediaItem>> {
        Log.d(TAG, "searchChannelMedia stub query=$query — TDLib not yet integrated")
        return Result.success(emptyList())
    }

    /**
     * TDLib wiring:
     *   client.send(TdApi.DownloadFile(fileId.toInt(), 1, 0, 0, true))
     *   → return TdApi.File.local.path
     */
    override suspend fun resolveFileUrl(fileId: Long): Result<String> {
        Log.d(TAG, "resolveFileUrl stub fileId=$fileId — TDLib not yet integrated")
        return Result.failure(UnsupportedOperationException("TDLib not yet integrated"))
    }

    /** Converts "My.Movie.1080p.x264.mkv" → "My Movie" */
    fun cleanTitle(raw: String): String {
        val noExt = raw.substringBeforeLast(".")
        return noExt
            .replace(Regex("[._]"), " ")
            .replace(
                Regex(
                    "\\b(1080p|720p|480p|360p|x264|x265|BluRay|WEB-DL|HDRip|DVDRip|HEVC|AAC|mp4|mkv|webm)\\b",
                    RegexOption.IGNORE_CASE
                ), ""
            )
            .replace(Regex("\\s{2,}"), " ")
            .trim()
            .ifBlank { raw }
    }
}
