package com.trixsearch.hasikit.telegram.data.repository

import android.util.Log
import com.trixsearch.hasikit.telegram.config.TelegramSource
import com.trixsearch.hasikit.telegram.domain.model.TelegramMedia
import com.trixsearch.hasikit.telegram.domain.repository.TelegramChannelRepository
import com.trixsearch.hasikit.telegram.service.TelegramClientService
import kotlinx.coroutines.suspendCancellableCoroutine
import org.drinkless.tdlib.TdApi
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

private const val TAG = "TelegramChannelRepo"

private val SUPPORTED_MIME = setOf(
    "video/mp4", "video/x-matroska", "video/webm",
    "video/quicktime", "video/x-m4v"
)
private val SUPPORTED_EXT = setOf("mp4", "mkv", "webm", "mov", "m4v")

@Singleton
class TelegramChannelRepositoryImpl @Inject constructor(
    private val clientService: TelegramClientService
) : TelegramChannelRepository {

    // ── Source resolution ─────────────────────────────────────────────────────

    override suspend fun resolveSource(source: TelegramSource): Result<Long> {
        return when {
            source.isChatId -> {
                // Numeric chat ID — open the chat directly to verify access
                val chatId = source.identifier.toLongOrNull()
                    ?: return Result.failure(Exception("Invalid chat ID: ${source.identifier}"))
                openChatById(chatId)
            }
            source.isInviteLink -> joinOrGetByInviteLink(source.identifier)
            else -> resolveChannel(source.username)
        }
    }

    override suspend fun resolveChannel(username: String): Result<Long> {
        Log.d(TAG, "resolveChannel username=$username")
        return suspendCancellableCoroutine { cont ->
            clientService.send(TdApi.SearchPublicChat(username)) { result ->
                when (result) {
                    is TdApi.Chat -> {
                        val typeLabel = when (val type = result.type) {
                            is TdApi.ChatTypeSupergroup -> if (type.isChannel) "Channel" else "Supergroup"
                            is TdApi.ChatTypeBasicGroup -> "BasicGroup"
                            is TdApi.ChatTypePrivate -> "Private"
                            else -> "Unknown"
                        }
                        Log.i(TAG, "CHANNEL_RESOLVED id=${result.id} title=${result.title} type=$typeLabel")
                        cont.resume(Result.success(result.id))
                    }
                    is TdApi.Error -> {
                        Log.e(TAG, "resolveChannel error ${result.code}: ${result.message}")
                        cont.resume(Result.failure(Exception("${result.code}: ${result.message}")))
                    }
                    else -> cont.resume(Result.failure(Exception("Unexpected result: $result")))
                }
            }
            cont.invokeOnCancellation {}
        }
    }

    private suspend fun openChatById(chatId: Long): Result<Long> {
        Log.d(TAG, "openChatById chatId=$chatId")
        return suspendCancellableCoroutine { cont ->
            clientService.send(TdApi.GetChat(chatId)) { result ->
                when (result) {
                    is TdApi.Chat -> {
                        Log.i(TAG, "openChatById resolved id=${result.id} title=${result.title}")
                        cont.resume(Result.success(result.id))
                    }
                    is TdApi.Error -> {
                        Log.e(TAG, "openChatById error ${result.code}: ${result.message}")
                        cont.resume(Result.failure(Exception("${result.code}: ${result.message}")))
                    }
                    else -> cont.resume(Result.failure(Exception("Unexpected: $result")))
                }
            }
            cont.invokeOnCancellation {}
        }
    }

    private suspend fun joinOrGetByInviteLink(inviteLink: String): Result<Long> {
        Log.d(TAG, "joinOrGetByInviteLink link=$inviteLink")
        return suspendCancellableCoroutine { cont ->
            clientService.send(TdApi.CheckChatInviteLink(inviteLink)) { result ->
                when (result) {
                    is TdApi.ChatInviteLinkInfo -> {
                        val chatId = result.chatId
                        if (chatId != 0L) {
                            Log.i(TAG, "inviteLink resolved chatId=$chatId title=${result.title}")
                            cont.resume(Result.success(chatId))
                        } else {
                            // Need to join first
                            clientService.send(TdApi.JoinChatByInviteLink(inviteLink)) { joinResult ->
                                when (joinResult) {
                                    is TdApi.Chat -> {
                                        Log.i(TAG, "joined via inviteLink chatId=${joinResult.id}")
                                        cont.resume(Result.success(joinResult.id))
                                    }
                                    is TdApi.Error -> {
                                        Log.e(TAG, "joinChatByInviteLink error ${joinResult.code}: ${joinResult.message}")
                                        cont.resume(Result.failure(Exception("${joinResult.code}: ${joinResult.message}")))
                                    }
                                    else -> cont.resume(Result.failure(Exception("Unexpected join result: $joinResult")))
                                }
                            }
                        }
                    }
                    is TdApi.Error -> {
                        Log.e(TAG, "checkInviteLink error ${result.code}: ${result.message}")
                        cont.resume(Result.failure(Exception("${result.code}: ${result.message}")))
                    }
                    else -> cont.resume(Result.failure(Exception("Unexpected: $result")))
                }
            }
            cont.invokeOnCancellation {}
        }
    }

    // ── Media loading ─────────────────────────────────────────────────────────

    override suspend fun getChannelMedia(
        chatId: Long,
        offsetMessageId: Long,
        limit: Int
    ): Result<List<TelegramMedia>> {
        Log.d(TAG, "getChannelMedia chatId=$chatId offset=$offsetMessageId limit=$limit")
        return suspendCancellableCoroutine { cont ->
            clientService.send(
                TdApi.GetChatHistory(chatId, offsetMessageId, 0, limit, false)
            ) { result ->
                when (result) {
                    is TdApi.Messages -> {
                        val media = result.messages.mapNotNull { it.toTelegramMedia(chatId) }
                        Log.d(TAG, "getChannelMedia total=${result.totalCount} loaded=${result.messages.size} media=${media.size}")
                        cont.resume(Result.success(media))
                    }
                    is TdApi.Error -> {
                        Log.e(TAG, "getChannelMedia error ${result.code}: ${result.message}")
                        cont.resume(Result.failure(Exception("${result.code}: ${result.message}")))
                    }
                    else -> cont.resume(Result.failure(Exception("Unexpected: $result")))
                }
            }
            cont.invokeOnCancellation {}
        }
    }

    override suspend fun searchChannelMedia(
        chatId: Long,
        query: String,
        limit: Int
    ): Result<List<TelegramMedia>> {
        Log.d(TAG, "searchChannelMedia chatId=$chatId query=$query")
        return suspendCancellableCoroutine { cont ->
            clientService.send(
                TdApi.SearchChatMessages(
                    chatId, null, query, null, 0, 0, limit,
                    TdApi.SearchMessagesFilterVideo()
                )
            ) { result ->
                when (result) {
                    is TdApi.FoundChatMessages -> {
                        val media = result.messages.mapNotNull { it.toTelegramMedia(chatId) }
                        Log.d(TAG, "searchChannelMedia found=${result.totalCount} media=${media.size}")
                        cont.resume(Result.success(media))
                    }
                    is TdApi.Error -> {
                        Log.e(TAG, "searchChannelMedia error ${result.code}: ${result.message}")
                        cont.resume(Result.failure(Exception("${result.code}: ${result.message}")))
                    }
                    else -> cont.resume(Result.failure(Exception("Unexpected: $result")))
                }
            }
            cont.invokeOnCancellation {}
        }
    }

    // ── File URL resolution ───────────────────────────────────────────────────

    override suspend fun resolveFileUrl(fileId: Long): Result<String> {
        Log.d(TAG, "resolveFileUrl fileId=$fileId")
        return suspendCancellableCoroutine { cont ->
            clientService.send(
                TdApi.DownloadFile(fileId.toInt(), 1, 0, 1, true)
            ) { result ->
                when (result) {
                    is TdApi.File -> {
                        val path = result.local.path
                        Log.d(TAG, "resolveFileUrl fileId=$fileId path=$path isDownloading=${result.local.isDownloadingActive}")
                        if (path.isNotBlank()) cont.resume(Result.success(path))
                        else cont.resume(Result.failure(Exception("TDLib returned empty path for fileId=$fileId")))
                    }
                    is TdApi.Error -> {
                        Log.e(TAG, "resolveFileUrl error ${result.code}: ${result.message}")
                        cont.resume(Result.failure(Exception("${result.code}: ${result.message}")))
                    }
                    else -> cont.resume(Result.failure(Exception("Unexpected: $result")))
                }
            }
            cont.invokeOnCancellation {}
        }
    }

    // ── Thumbnail download ────────────────────────────────────────────────────

    override suspend fun downloadThumbnail(thumbnailFileId: Long): String? {
        Log.d(TAG, "downloadThumbnail fileId=$thumbnailFileId")
        return suspendCancellableCoroutine { cont ->
            // Check if already downloaded
            clientService.send(TdApi.GetFile(thumbnailFileId.toInt())) { fileResult ->
                if (fileResult is TdApi.File && fileResult.local.isDownloadingCompleted && fileResult.local.path.isNotBlank()) {
                    cont.resume(fileResult.local.path)
                    return@send
                }
                // Download with low priority (32 = lowest)
                clientService.send(
                    TdApi.DownloadFile(thumbnailFileId.toInt(), 32, 0, 0, true)
                ) { result ->
                    when (result) {
                        is TdApi.File -> {
                            val path = result.local.path.takeIf { it.isNotBlank() }
                            Log.d(TAG, "downloadThumbnail fileId=$thumbnailFileId path=$path")
                            cont.resume(path)
                        }
                        else -> cont.resume(null)
                    }
                }
            }
            cont.invokeOnCancellation {}
        }
    }
}

// ── Message → TelegramMedia extraction ───────────────────────────────────────

private fun TdApi.Message.toTelegramMedia(chatId: Long): TelegramMedia? {
    return when (val content = this.content) {
        is TdApi.MessageVideo -> {
            val video = content.video
            if (!isSupportedMime(video.mimeType) && !isSupportedExt(video.fileName)) return null
            TelegramMedia(
                messageId = id,
                fileId = video.video.id.toLong(),
                channelId = chatId,
                fileName = video.fileName,
                title = cleanTitle(video.fileName),
                caption = content.caption.text,
                duration = video.duration,
                size = video.video.size,
                thumbnailFileId = video.thumbnail?.file?.id?.toLong(),
                mimeType = video.mimeType,
                uploadDate = date
            )
        }
        is TdApi.MessageDocument -> {
            val doc = content.document
            if (!isSupportedMime(doc.mimeType) && !isSupportedExt(doc.fileName)) return null
            TelegramMedia(
                messageId = id,
                fileId = doc.document.id.toLong(),
                channelId = chatId,
                fileName = doc.fileName,
                title = cleanTitle(doc.fileName),
                caption = content.caption.text,
                duration = 0,
                size = doc.document.size,
                thumbnailFileId = doc.thumbnail?.file?.id?.toLong(),
                mimeType = doc.mimeType,
                uploadDate = date
            )
        }
        else -> null
    }
}

private fun isSupportedMime(mime: String): Boolean = mime in SUPPORTED_MIME
private fun isSupportedExt(fileName: String): Boolean =
    fileName.substringAfterLast('.', "").lowercase() in SUPPORTED_EXT

private fun cleanTitle(raw: String): String {
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
