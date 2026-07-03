package com.trixsearch.hasikit.telegram.data.repository

import android.util.Log
import com.trixsearch.hasikit.telegram.config.TelegramSourceConfig
import com.trixsearch.hasikit.telegram.domain.model.TelegramMediaItem
import com.trixsearch.hasikit.telegram.domain.repository.TelegramMediaRepository
import com.trixsearch.hasikit.telegram.service.TelegramClientService
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeout
import org.drinkless.tdlib.TdApi
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

private const val TAG = "TelegramMediaRepo"
private const val TIMEOUT_MS = 30_000L

private val SUPPORTED_MIME = setOf("video/mp4", "video/x-matroska", "video/webm", "video/quicktime", "video/x-m4v")

@Singleton
class TelegramMediaRepositoryImpl @Inject constructor(
    private val clientService: TelegramClientService,
    private val config: TelegramSourceConfig
) : TelegramMediaRepository {

    override suspend fun getChannelMedia(
        offsetMessageId: Long,
        limit: Int
    ): Result<List<TelegramMediaItem>> = runCatching {
        val client = clientService.getClient()
            ?: return Result.failure(IllegalStateException("TDLib client not initialised"))
        val username = config.sourceChannelUsername
        if (username.isBlank()) return Result.success(emptyList())

        val chatId = resolveChatId(client, username)
            ?: return Result.failure(IllegalStateException("Channel not found: $username"))

        val messages = withTimeout(TIMEOUT_MS) {
            suspendCancellableCoroutine { cont ->
                client.send(
                    TdApi.GetChatHistory(chatId, offsetMessageId, 0, limit, false)
                ) { obj ->
                    when (obj) {
                        is TdApi.Messages -> cont.resume(obj.messages.toList())
                        is TdApi.Error -> {
                            Log.e(TAG, "getChannelMedia error: ${obj.message}")
                            cont.resume(emptyList())
                        }
                        else -> cont.resume(emptyList())
                    }
                }
            }
        }

        messages.mapNotNull { it.toMediaItem() }
    }

    override suspend fun searchChannelMedia(
        query: String,
        limit: Int
    ): Result<List<TelegramMediaItem>> = runCatching {
        val client = clientService.getClient()
            ?: return Result.failure(IllegalStateException("TDLib client not initialised"))
        val username = config.sourceChannelUsername
        if (username.isBlank()) return Result.success(emptyList())

        val chatId = resolveChatId(client, username)
            ?: return Result.failure(IllegalStateException("Channel not found: $username"))

        val messages = withTimeout(TIMEOUT_MS) {
            suspendCancellableCoroutine { cont ->
                client.send(
                    TdApi.SearchChatMessages(
                        chatId,
                        query,
                        null,
                        0,
                        0,
                        limit,
                        TdApi.SearchMessagesFilterVideo(),
                        0
                    )
                ) { obj ->
                    when (obj) {
                        is TdApi.FoundChatMessages -> cont.resume(obj.messages.toList())
                        is TdApi.Error -> {
                            Log.e(TAG, "searchChannelMedia error: ${obj.message}")
                            cont.resume(emptyList())
                        }
                        else -> cont.resume(emptyList())
                    }
                }
            }
        }

        messages.mapNotNull { it.toMediaItem() }
    }

    override suspend fun resolveFileUrl(fileId: Long): Result<String> = runCatching {
        val client = clientService.getClient()
            ?: return Result.failure(IllegalStateException("TDLib client not initialised"))

        withTimeout(60_000L) {
            suspendCancellableCoroutine { cont ->
                client.send(TdApi.DownloadFile(fileId.toInt(), 1, 0, 0, true)) { obj ->
                    when (obj) {
                        is TdApi.File -> {
                            val path = obj.local?.path
                            if (!path.isNullOrBlank()) {
                                Log.d(TAG, "resolveFileUrl fileId=$fileId path=$path")
                                cont.resume(path)
                            } else {
                                cont.resumeWith(Result.failure(IllegalStateException("File path empty for fileId=$fileId")))
                            }
                        }
                        is TdApi.Error -> cont.resumeWith(Result.failure(RuntimeException(obj.message)))
                        else -> cont.resumeWith(Result.failure(IllegalStateException("Unexpected response")))
                    }
                }
            }
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private suspend fun resolveChatId(client: org.drinkless.tdlib.Client, username: String): Long? =
        withTimeout(TIMEOUT_MS) {
            suspendCancellableCoroutine { cont ->
                client.send(TdApi.SearchPublicChat(username)) { obj ->
                    when (obj) {
                        is TdApi.Chat -> cont.resume(obj.id)
                        else -> cont.resume(null)
                    }
                }
            }
        }

    private fun TdApi.Message.toMediaItem(): TelegramMediaItem? {
        val caption = (content as? TdApi.MessageVideo)?.caption?.text
            ?: (content as? TdApi.MessageDocument)?.caption?.text
            ?: ""

        return when (val c = content) {
            is TdApi.MessageVideo -> {
                val v = c.video
                TelegramMediaItem(
                    messageId = id,
                    fileId = v.video.id.toLong(),
                    fileName = v.fileName.ifBlank { "video_$id.mp4" },
                    title = cleanTitle(v.fileName.ifBlank { caption }),
                    caption = caption,
                    mimeType = v.mimeType,
                    duration = v.duration,
                    size = v.video.size,
                    thumbnailFileId = v.thumbnail?.file?.id?.toLong(),
                    date = date
                )
            }
            is TdApi.MessageDocument -> {
                val d = c.document
                if (d.mimeType !in SUPPORTED_MIME) return null
                TelegramMediaItem(
                    messageId = id,
                    fileId = d.document.id.toLong(),
                    fileName = d.fileName,
                    title = cleanTitle(d.fileName),
                    caption = caption,
                    mimeType = d.mimeType,
                    duration = 0,
                    size = d.document.size,
                    thumbnailFileId = d.thumbnail?.file?.id?.toLong(),
                    date = date
                )
            }
            else -> null
        }
    }

    /** Converts "My.Movie.1080p.x264.mkv" → "My Movie" */
    private fun cleanTitle(raw: String): String {
        val noExt = raw.substringBeforeLast(".")
        return noExt
            .replace(Regex("[._]"), " ")
            .replace(Regex("\\b(1080p|720p|480p|360p|x264|x265|BluRay|WEB-DL|HDRip|DVDRip|HEVC|AAC|mp4|mkv|webm)\\b", RegexOption.IGNORE_CASE), "")
            .replace(Regex("\\s{2,}"), " ")
            .trim()
            .ifBlank { raw }
    }
}
