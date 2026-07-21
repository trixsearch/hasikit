package com.trixsearch.hasikit.telegram.data.repository

import android.util.Log
import com.trixsearch.hasikit.telegram.config.TelegramSource
import com.trixsearch.hasikit.telegram.domain.model.TelegramMedia
import com.trixsearch.hasikit.telegram.domain.repository.TelegramChannelRepository
import com.trixsearch.hasikit.telegram.service.TelegramClientService
import kotlinx.coroutines.suspendCancellableCoroutine
import org.drinkless.tdlib.TdApi
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

private const val TAG = "TelegramChannelRepo"

// Extracted from searchChannelMedia local scope to avoid duplicate JVM class name in suspend function
private data class SearchBatchResult(val media: List<TelegramMedia>, val nextFromMessageId: Long)

private val SUPPORTED_MIME = setOf(
    "video/mp4", "video/x-matroska", "video/webm",
    "video/quicktime", "video/x-m4v"
)
private val SUPPORTED_EXT = setOf("mp4", "mkv", "webm", "mov", "m4v")

@Singleton
class TelegramChannelRepositoryImpl @Inject constructor(
    private val clientService: TelegramClientService
) : TelegramChannelRepository {

    // Cache resolved chatIds so we don't re-resolve on every app resume
    private val resolvedChatCache = ConcurrentHashMap<String, Long>()

    // ── Source resolution ─────────────────────────────────────────────────────

    override suspend fun resolveSource(source: TelegramSource): Result<Long> {
        // Return cached result immediately if available
        resolvedChatCache[source.identifier]?.let { cachedId ->
            Log.d(TAG, "resolveSource cache hit identifier=${source.identifier} chatId=$cachedId")
            return Result.success(cachedId)
        }

        val result = when {
            source.isChatId -> resolvePrivateChatId(
                chatId = source.identifier.toLong(),
                inviteLink = source.inviteLink,
                displayName = source.displayName
            )
            source.isInviteLink -> resolveByInviteLink(source.identifier)
            else -> resolvePublicUsername(source.username)
        }

        // Cache on success
        result.onSuccess { chatId ->
            resolvedChatCache[source.identifier] = chatId
            Log.i(TAG, "resolveSource cached identifier=${source.identifier} chatId=$chatId")
        }
        return result
    }

    /**
     * Resolves a private channel by numeric chatId.
     * Step 1: GetChat — works if TDLib already has this chat in local cache.
     * Step 2: Search user's accessible chats for a matching chatId.
     * Step 3: Force-load via CreatePrivateChat / supergroup lookup.
     * Step 4: Invite link fallback if provided.
     */
    private suspend fun resolvePrivateChatId(
        chatId: Long,
        inviteLink: String?,
        displayName: String
    ): Result<Long> {
        Log.d(TAG, "resolvePrivateChatId chatId=$chatId displayName=$displayName")

        // Step 1: GetChat — succeeds if TDLib has this chat cached
        val getChat = getChat(chatId)
        if (getChat.isSuccess) {
            val id = getChat.getOrThrow()
            Log.i(TAG, "resolvePrivateChatId step1 GetChat success chatId=$id")
            clientService.send(TdApi.OpenChat(id)) {}
            return Result.success(id)
        }
        Log.w(TAG, "resolvePrivateChatId step1 GetChat failed: ${getChat.exceptionOrNull()?.message}")

        // Step 2: Search user's accessible chats for matching chatId
        val accessibleChatId = findInAccessibleChats(chatId)
        if (accessibleChatId != null) {
            Log.i(TAG, "resolvePrivateChatId step2 found in accessible chats chatId=$accessibleChatId")
            clientService.send(TdApi.OpenChat(accessibleChatId)) {}
            return Result.success(accessibleChatId)
        }
        Log.w(TAG, "resolvePrivateChatId step2 chatId=$chatId not found in accessible chats")

        // Step 3: Force-load supergroup by extracting supergroup ID from chatId
        // TDLib supergroup chatId = -(1000000000000 + supergroupId)
        // Keep as Long — TdApi.CreateSupergroupChat expects Long
        val supergroupId = if (chatId < -1000000000000L) (-(chatId) - 1000000000000L) else 0L
        if (supergroupId > 0L) {
            Log.d(TAG, "resolvePrivateChatId step3 trying CreateSupergroupChat supergroupId=$supergroupId")
            val sgResult = createSupergroupChat(supergroupId)
            if (sgResult.isSuccess) {
                val id = sgResult.getOrThrow()
                Log.i(TAG, "resolvePrivateChatId step3 CreateSupergroupChat success chatId=$id")
                clientService.send(TdApi.OpenChat(id)) {}
                return Result.success(id)
            }
            Log.w(TAG, "resolvePrivateChatId step3 CreateSupergroupChat failed: ${sgResult.exceptionOrNull()?.message}")
        }

        // Step 4: Invite link fallback
        if (!inviteLink.isNullOrBlank()) {
            Log.d(TAG, "resolvePrivateChatId step4 trying invite link=$inviteLink")
            val inviteResult = resolveByInviteLink(inviteLink)
            if (inviteResult.isSuccess) {
                Log.i(TAG, "resolvePrivateChatId step4 invite link resolved chatId=${inviteResult.getOrThrow()}")
                return inviteResult
            }
            Log.w(TAG, "resolvePrivateChatId step4 invite link failed: ${inviteResult.exceptionOrNull()?.message}")
        }

        return Result.failure(Exception("Cannot access private channel chatId=$chatId displayName=$displayName"))
    }

    /** Forces TDLib to load a supergroup chat by its supergroup ID. */
    private suspend fun createSupergroupChat(supergroupId: Long): Result<Long> =
        suspendCancellableCoroutine { cont ->
            clientService.send(TdApi.CreateSupergroupChat(supergroupId, false)) { result ->
                when (result) {
                    is TdApi.Chat -> cont.resume(Result.success(result.id))
                    is TdApi.Error -> cont.resume(Result.failure(Exception("${result.code}: ${result.message}")))
                    else -> cont.resume(Result.failure(Exception("Unexpected: $result")))
                }
            }
            cont.invokeOnCancellation {}
        }

    /** Calls TdApi.GetChat and returns the chatId on success. */
    private suspend fun getChat(chatId: Long): Result<Long> =
        suspendCancellableCoroutine { cont ->
            Log.d(TAG, "getChat chatId=$chatId")
            clientService.send(TdApi.GetChat(chatId)) { result ->
                when (result) {
                    is TdApi.Chat -> {
                        Log.d(TAG, "getChat success id=${result.id} title=${result.title}")
                        cont.resume(Result.success(result.id))
                    }
                    is TdApi.Error -> {
                        Log.d(TAG, "getChat failed ${result.code}: ${result.message}")
                        cont.resume(Result.failure(Exception("${result.code}: ${result.message}")))
                    }
                    else -> cont.resume(Result.failure(Exception("getChat unexpected: $result")))
                }
            }
            cont.invokeOnCancellation {}
        }

    /**
     * Fetches the user's accessible chats (GetChats) and checks if the target chatId is present.
     * This works for private channels the user has already joined.
     */
    private suspend fun findInAccessibleChats(targetChatId: Long): Long? =
        suspendCancellableCoroutine { cont ->
            // Load up to 500 chats from the user's chat list
            clientService.send(TdApi.GetChats(TdApi.ChatListMain(), 500)) { result ->
                when (result) {
                    is TdApi.Chats -> {
                        Log.d(TAG, "findInAccessibleChats total=${result.chatIds.size} chats loaded")
                        val found = result.chatIds.firstOrNull { it == targetChatId }
                        if (found != null) {
                            Log.d(TAG, "findInAccessibleChats found chatId=$found")
                        } else {
                            Log.d(TAG, "findInAccessibleChats chatId=$targetChatId not in list")
                        }
                        cont.resume(found)
                    }
                    is TdApi.Error -> {
                        Log.w(TAG, "findInAccessibleChats GetChats error ${result.code}: ${result.message}")
                        cont.resume(null)
                    }
                    else -> cont.resume(null)
                }
            }
            cont.invokeOnCancellation {}
        }

    /** Resolves a private invite link — checks if already joined, joins if not. */
    private suspend fun resolveByInviteLink(inviteLink: String): Result<Long> {
        Log.d(TAG, "resolveByInviteLink link=$inviteLink")
        return suspendCancellableCoroutine { cont ->
            clientService.send(TdApi.CheckChatInviteLink(inviteLink)) { result ->
                when (result) {
                    is TdApi.ChatInviteLinkInfo -> {
                        val chatId = result.chatId
                        if (chatId != 0L) {
                            // Already a member — chatId is directly available
                            Log.i(TAG, "resolveByInviteLink already member chatId=$chatId title=${result.title}")
                            cont.resume(Result.success(chatId))
                        } else {
                            // Not a member — join the chat
                            Log.d(TAG, "resolveByInviteLink not a member, joining title=${result.title}")
                            clientService.send(TdApi.JoinChatByInviteLink(inviteLink)) { joinResult ->
                                when (joinResult) {
                                    is TdApi.Chat -> {
                                        Log.i(TAG, "resolveByInviteLink joined chatId=${joinResult.id}")
                                        cont.resume(Result.success(joinResult.id))
                                    }
                                    is TdApi.Error -> {
                                        Log.e(TAG, "resolveByInviteLink join error ${joinResult.code}: ${joinResult.message}")
                                        cont.resume(Result.failure(Exception("${joinResult.code}: ${joinResult.message}")))
                                    }
                                    else -> cont.resume(Result.failure(Exception("Unexpected join result: $joinResult")))
                                }
                            }
                        }
                    }
                    is TdApi.Error -> {
                        Log.e(TAG, "resolveByInviteLink CheckChatInviteLink error ${result.code}: ${result.message}")
                        cont.resume(Result.failure(Exception("${result.code}: ${result.message}")))
                    }
                    else -> cont.resume(Result.failure(Exception("Unexpected: $result")))
                }
            }
            cont.invokeOnCancellation {}
        }
    }

    /** Resolves a public channel by @username via SearchPublicChat. */
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
                        Log.i(TAG, "resolveChannel resolved id=${result.id} title=${result.title} type=$typeLabel")
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

    /** Resolves a source by username (public) — delegates to resolveChannel. */
    private suspend fun resolvePublicUsername(username: String): Result<Long> =
        resolveChannel(username)

    // ── Media loading ─────────────────────────────────────────────────────────

    override suspend fun getChannelMedia(
        chatId: Long,
        offsetMessageId: Long,
        limit: Int
    ): Result<Pair<List<TelegramMedia>, Int>> {
        Log.d(TAG, "getChannelMedia chatId=$chatId offset=$offsetMessageId limit=$limit")
        return suspendCancellableCoroutine { cont ->
            clientService.send(
                TdApi.GetChatHistory(chatId, offsetMessageId, 0, limit, false)
            ) { result ->
                when (result) {
                    is TdApi.Messages -> {
                        val media = result.messages.mapNotNull { it.toTelegramMedia(chatId) }
                        // rawCount: actual messages returned by TDLib before video filter
                        // hasMore must be based on rawCount so non-video messages don't stop pagination
                        val rawCount = result.messages.size
                        Log.d(TAG, "getChannelMedia total=${result.totalCount} raw=$rawCount media=${media.size} offsetId=$offsetMessageId")
                        cont.resume(Result.success(Pair(media, rawCount)))
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

    // Smart Search V4: searchChannelMedia now accepts a list of query variants produced by
    // SearchEngine.parseIntent().toTelegramQueryVariants(). Each variant is sent as a separate
    // TDLib SearchChatMessages call so Telegram's own index handles language/quality tokens.
    // Stage B history scan uses SearchEngine.normalize() for fuzzy file-name matching.
    override suspend fun searchChannelMedia(
        chatId: Long,
        query: String,
        limit: Int
    ): Result<List<TelegramMedia>> = searchChannelMediaMulti(chatId, listOf(query), limit)

    // Multi-query search entry point — called by HomeViewModel with expanded query variants
    override suspend fun searchChannelMediaMulti(
        chatId: Long,
        queryVariants: List<String>,
        limit: Int
    ): Result<List<TelegramMedia>> {
        val primaryQuery = queryVariants.firstOrNull()?.trim() ?: return Result.success(emptyList())
        Log.d(TAG, "searchChannelMediaMulti START chatId=$chatId variants=$queryVariants")
        val allMedia = mutableListOf<TelegramMedia>()
        val seenIds = mutableSetOf<Long>()
        val batchSize = 100

        // Stage A: TDLib text search for each query variant — video-type messages
        // Telegram's index covers caption text and message text, not raw file names.
        for (variant in queryVariants) {
            var fromMessageId = 0L
            var variantCount = 0
            while (true) {
                val batch = suspendCancellableCoroutine<Result<SearchBatchResult>> { cont ->
                    clientService.send(
                        TdApi.SearchChatMessages(
                            chatId, null, variant, null, fromMessageId, 0, batchSize,
                            TdApi.SearchMessagesFilterVideo()
                        )
                    ) { result ->
                        when (result) {
                            is TdApi.FoundChatMessages -> {
                                val media = result.messages
                                    .filter { it.id !in seenIds }
                                    .mapNotNull { it.toTelegramMedia(chatId) }
                                Log.d(TAG, "[SEARCH] Stage-A variant='$variant' fromMsgId=$fromMessageId found=${result.messages.size} new=${media.size}")
                                cont.resume(Result.success(SearchBatchResult(media, result.nextFromMessageId)))
                            }
                            is TdApi.Error -> {
                                Log.e(TAG, "[SEARCH] Stage-A error ${result.code}: ${result.message}")
                                cont.resume(Result.failure(Exception("${result.code}: ${result.message}")))
                            }
                            else -> cont.resume(Result.failure(Exception("Unexpected: $result")))
                        }
                    }
                    cont.invokeOnCancellation {}
                }
                val batchResult = batch.getOrNull() ?: break
                batchResult.media.forEach { seenIds.add(it.messageId) }
                allMedia.addAll(batchResult.media)
                variantCount += batchResult.media.size
                if (batchResult.media.isEmpty() || batchResult.nextFromMessageId == 0L) break
                fromMessageId = batchResult.nextFromMessageId
            }
            Log.d(TAG, "[SEARCH] Stage-A variant='$variant' total=$variantCount")
        }

        // Stage A2: TDLib text search for each variant — document-type messages
        // MKV and large MP4 files are often uploaded as documents, not videos.
        for (variant in queryVariants) {
            var fromMessageId = 0L
            var variantCount = 0
            while (true) {
                val batch = suspendCancellableCoroutine<Result<SearchBatchResult>> { cont ->
                    clientService.send(
                        TdApi.SearchChatMessages(
                            chatId, null, variant, null, fromMessageId, 0, batchSize,
                            TdApi.SearchMessagesFilterDocument()
                        )
                    ) { result ->
                        when (result) {
                            is TdApi.FoundChatMessages -> {
                                val media = result.messages
                                    .filter { it.id !in seenIds }
                                    .mapNotNull { it.toTelegramMedia(chatId) }
                                Log.d(TAG, "[SEARCH] Stage-A2 doc variant='$variant' found=${result.messages.size} new=${media.size}")
                                cont.resume(Result.success(SearchBatchResult(media, result.nextFromMessageId)))
                            }
                            is TdApi.Error -> {
                                Log.w(TAG, "[SEARCH] Stage-A2 doc error ${result.code}: ${result.message}")
                                cont.resume(Result.success(SearchBatchResult(emptyList(), 0L)))
                            }
                            else -> cont.resume(Result.success(SearchBatchResult(emptyList(), 0L)))
                        }
                    }
                    cont.invokeOnCancellation {}
                }
                val batchResult = batch.getOrNull() ?: break
                batchResult.media.forEach { seenIds.add(it.messageId) }
                allMedia.addAll(batchResult.media)
                variantCount += batchResult.media.size
                if (batchResult.media.isEmpty() || batchResult.nextFromMessageId == 0L) break
                fromMessageId = batchResult.nextFromMessageId
            }
            Log.d(TAG, "[SEARCH] Stage-A2 doc variant='$variant' total=$variantCount")
        }

        // Stage B: Full history scan with SearchEngine fuzzy matching on file names.
        // Telegram's text index does NOT index raw file names (e.g. "Pushpa.2021.1080p.mkv").
        // We walk the channel history and apply normalize()+score() to catch these.
        val normalizedPrimary = com.trixsearch.hasikit.search.SearchEngine.normalize(primaryQuery)
        var historyOffset = 0L
        var historyBatches = 0
        val maxHistoryBatches = 20 // cap at 2000 messages to avoid infinite scan
        while (historyBatches < maxHistoryBatches) {
            val historyResult = suspendCancellableCoroutine<TdApi.Messages?> { cont ->
                clientService.send(
                    TdApi.GetChatHistory(chatId, historyOffset, 0, 100, false)
                ) { result ->
                    cont.resume(if (result is TdApi.Messages) result else null)
                }
                cont.invokeOnCancellation {}
            } ?: break

            if (historyResult.messages.isEmpty()) break

            val matched = historyResult.messages.mapNotNull { msg ->
                if (msg.id in seenIds) return@mapNotNull null
                val media = msg.toTelegramMedia(chatId) ?: return@mapNotNull null
                // Smart matching: normalize file name, title, caption and score against query
                val fileScore = com.trixsearch.hasikit.search.SearchEngine.score(
                    normalizedPrimary,
                    com.trixsearch.hasikit.search.SearchEngine.normalize(media.fileName)
                )
                val titleScore = com.trixsearch.hasikit.search.SearchEngine.score(
                    normalizedPrimary,
                    com.trixsearch.hasikit.search.SearchEngine.normalize(media.title)
                )
                val captionScore = com.trixsearch.hasikit.search.SearchEngine.score(
                    normalizedPrimary,
                    com.trixsearch.hasikit.search.SearchEngine.normalize(media.caption)
                )
                val best = maxOf(fileScore, titleScore, captionScore)
                if (best >= com.trixsearch.hasikit.search.SearchEngine.SCORE_IGNORE_BELOW) {
                    seenIds.add(msg.id)
                    media
                } else null
            }
            allMedia.addAll(matched)

            val oldestId = historyResult.messages.minOfOrNull { it.id } ?: break
            historyOffset = oldestId
            historyBatches++
            Log.d(TAG, "[SEARCH] Stage-B batch=$historyBatches msgs=${historyResult.messages.size} matched=${matched.size} oldestId=$oldestId")

            if (historyResult.messages.size < 100) break
        }

        Log.d(TAG, "[SEARCH] COMPLETE chatId=$chatId total=${allMedia.size} historyBatches=$historyBatches")
        return Result.success(allMedia)
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
            clientService.send(TdApi.GetFile(thumbnailFileId.toInt())) { fileResult ->
                if (fileResult is TdApi.File && fileResult.local.isDownloadingCompleted && fileResult.local.path.isNotBlank()) {
                    Log.d(TAG, "downloadThumbnail fileId=$thumbnailFileId already cached path=${fileResult.local.path}")
                    cont.resume(fileResult.local.path)
                    return@send
                }
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
                uploadDate = date,
                // MessageVideo is always streamable — TDLib can progressive-download it
                isStreamable = true
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
                uploadDate = date,
                // MessageDocument requires full download before playback
                isStreamable = false
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
    val cleaned = noExt
        .replace(Regex("[._]"), " ")
        .replace(
            Regex(
                "\\b(1080p|720p|480p|360p|x264|x265|BluRay|WEB-DL|HDRip|DVDRip|HEVC|AAC|mp4|mkv|webm)\\b",
                RegexOption.IGNORE_CASE
            ), ""
        )
        .replace(Regex("\\s{2,}"), " ")
        .trim()
    // If cleaning produced a blank or purely numeric string, fall back to the raw filename without extension
    return if (cleaned.isBlank() || cleaned.all { it.isDigit() }) noExt.ifBlank { raw } else cleaned
}
