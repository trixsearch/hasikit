package com.trixsearch.hasikit.player

import android.content.Context
import android.util.Log
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.HttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "HasikitPlayer"
private const val USER_AGENT = "Hasikit/1.0 (Android; ExoPlayer)"

@Singleton
class HasikitPlayer @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var exoPlayer: ExoPlayer? = null

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying

    private val _isBuffering = MutableStateFlow(false)
    val isBuffering: StateFlow<Boolean> = _isBuffering

    private val _error = MutableStateFlow<PlaybackError?>(null)
    val error: StateFlow<PlaybackError?> = _error

    data class PlaybackError(
        val message: String,
        val httpStatusCode: Int? = null,
        val userMessage: String
    )

    fun initialize() {
        if (exoPlayer != null) {
            Log.d(TAG, "initialize() skipped — already initialized")
            return
        }

        val httpDataSourceFactory = DefaultHttpDataSource.Factory()
            .setAllowCrossProtocolRedirects(true)
            .setConnectTimeoutMs(15_000)
            .setReadTimeoutMs(15_000)
            .setUserAgent(USER_AGENT)
            .setDefaultRequestProperties(
                mapOf(
                    "Accept" to "video/*, */*",
                    "Accept-Encoding" to "identity"
                )
            )

        val mediaSourceFactory = DefaultMediaSourceFactory(context)
            .setDataSourceFactory(httpDataSourceFactory)

        exoPlayer = ExoPlayer.Builder(context)
            .setMediaSourceFactory(mediaSourceFactory)
            .build()
            .apply {
                addListener(object : Player.Listener {
                    override fun onIsPlayingChanged(isPlaying: Boolean) {
                        _isPlaying.value = isPlaying
                        Log.d(TAG, "isPlaying=$isPlaying pos=${currentPosition}ms dur=${duration}ms")
                    }

                    override fun onPlaybackStateChanged(playbackState: Int) {
                        val stateName = when (playbackState) {
                            Player.STATE_IDLE -> "IDLE"
                            Player.STATE_BUFFERING -> "BUFFERING"
                            Player.STATE_READY -> "READY"
                            Player.STATE_ENDED -> "ENDED"
                            else -> "UNKNOWN($playbackState)"
                        }
                        _isBuffering.value = playbackState == Player.STATE_BUFFERING
                        Log.d(TAG, "playbackState=$stateName dur=${duration}ms")
                    }

                    override fun onPlayerError(e: PlaybackException) {
                        val httpCode = extractHttpCode(e)
                        val userMsg = buildUserMessage(e, httpCode)
                        Log.e(
                            TAG,
                            "ExoPlayer error: errorCode=${e.errorCode} http=$httpCode msg=${e.message}",
                            e
                        )
                        _error.value = PlaybackError(
                            message = buildTechnicalMessage(e, httpCode),
                            httpStatusCode = httpCode,
                            userMessage = userMsg
                        )
                    }
                })
            }

        Log.d(TAG, "ExoPlayer initialized with USER_AGENT=$USER_AGENT")
    }

    private fun extractHttpCode(e: PlaybackException): Int? {
        var cause: Throwable? = e
        while (cause != null) {
            if (cause is HttpDataSource.InvalidResponseCodeException) return cause.responseCode
            cause = cause.cause
        }
        return null
    }

    private fun buildUserMessage(e: PlaybackException, httpCode: Int?): String = when {
        httpCode == 403 -> "Access denied (403 Forbidden). This video may be protected."
        httpCode == 404 -> "Video not found (404). The URL may be invalid."
        httpCode == 429 -> "Too many requests (429). Please wait and retry."
        httpCode != null && httpCode >= 500 -> "Server error ($httpCode). Please try again later."
        httpCode != null -> "HTTP error $httpCode."
        e.errorCode == PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_FAILED ->
            "Network connection failed. Check your internet connection."
        e.errorCode == PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_TIMEOUT ->
            "Connection timed out. Check your internet connection."
        e.errorCode == PlaybackException.ERROR_CODE_IO_BAD_HTTP_STATUS ->
            "Bad HTTP response from server."
        e.errorCode == PlaybackException.ERROR_CODE_PARSING_CONTAINER_UNSUPPORTED ->
            "Unsupported video format."
        e.errorCode == PlaybackException.ERROR_CODE_PARSING_MANIFEST_UNSUPPORTED ->
            "Unsupported stream format."
        e.errorCode == PlaybackException.ERROR_CODE_DECODER_INIT_FAILED ->
            "Failed to initialize video decoder."
        else -> "Playback failed. Please retry."
    }

    private fun buildTechnicalMessage(e: PlaybackException, httpCode: Int?): String {
        val parts = mutableListOf("errorCode=${e.errorCode}")
        if (httpCode != null) parts.add("http=$httpCode")
        parts.add("msg=${e.message}")
        return parts.joinToString(" | ")
    }

    fun playVideo(url: String, startPosition: Long = 0) {
        _error.value = null
        Log.d(TAG, "playVideo url=$url startPos=${startPosition}ms")
        exoPlayer?.apply {
            setMediaItem(MediaItem.fromUri(url))
            seekTo(startPosition)
            prepare()
            play()
        } ?: Log.e(TAG, "playVideo called but ExoPlayer is null — call initialize() first")
    }

    fun pause() {
        Log.d(TAG, "pause pos=${exoPlayer?.currentPosition}ms")
        exoPlayer?.pause()
    }

    fun resume() {
        Log.d(TAG, "resume pos=${exoPlayer?.currentPosition}ms")
        exoPlayer?.play()
    }

    fun seekTo(position: Long) {
        val clamped = position.coerceAtLeast(0L)
        Log.d(TAG, "seekTo ${clamped}ms")
        exoPlayer?.seekTo(clamped)
    }

    fun getCurrentPosition(): Long = exoPlayer?.currentPosition ?: 0L
    fun getDuration(): Long = exoPlayer?.duration?.coerceAtLeast(0L) ?: 0L

    fun release() {
        Log.d(TAG, "release pos=${exoPlayer?.currentPosition}ms")
        exoPlayer?.release()
        exoPlayer = null
        _isPlaying.value = false
        _isBuffering.value = false
        _error.value = null
    }

    fun getPlayerInstance(): Player? = exoPlayer
}
