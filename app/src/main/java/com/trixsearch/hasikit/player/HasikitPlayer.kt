package com.trixsearch.hasikit.player

import android.content.Context
import android.util.Log
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.PlaybackException
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.common.Tracks
import androidx.media3.common.VideoSize
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.HttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.session.MediaSession
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "PLAYER_DEBUG"
private const val USER_AGENT = "Hasikit/1.0 (Android; ExoPlayer)"

data class TrackInfo(val groupIndex: Int, val trackIndex: Int, val label: String, val isSelected: Boolean)

@Singleton
class HasikitPlayer @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var exoPlayer: ExoPlayer? = null
    // MediaSession — exposes playback to lock screen, notification, Bluetooth, Android Auto
    private var mediaSession: MediaSession? = null

    // Video dimensions — used for correct PiP aspect ratio
    private val _videoWidth = MutableStateFlow(0)
    private val _videoHeight = MutableStateFlow(0)
    fun getVideoWidth(): Int = _videoWidth.value
    fun getVideoHeight(): Int = _videoHeight.value

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying

    // Video completion state — true when playback reaches the end of the media
    private val _isEnded = MutableStateFlow(false)
    val isEnded: StateFlow<Boolean> = _isEnded

    private val _isBuffering = MutableStateFlow(false)
    val isBuffering: StateFlow<Boolean> = _isBuffering

    private val _error = MutableStateFlow<PlaybackError?>(null)
    val error: StateFlow<PlaybackError?> = _error

    private val _playbackSpeed = MutableStateFlow(1f)
    val playbackSpeed: StateFlow<Float> = _playbackSpeed

    private val _repeatMode = MutableStateFlow(Player.REPEAT_MODE_OFF)
    val repeatMode: StateFlow<Int> = _repeatMode

    private val _audioTracks = MutableStateFlow<List<TrackInfo>>(emptyList())
    val audioTracks: StateFlow<List<TrackInfo>> = _audioTracks

    private val _subtitleTracks = MutableStateFlow<List<TrackInfo>>(emptyList())
    val subtitleTracks: StateFlow<List<TrackInfo>> = _subtitleTracks

    data class PlaybackError(
        val message: String,
        val httpStatusCode: Int? = null,
        val userMessage: String
    )

    fun initialize() {
        if (exoPlayer != null) {
            Log.d(TAG, "[INIT] skipped — ExoPlayer already initialized")
            return
        }
        Log.d(TAG, "[INIT] building ExoPlayer | userAgent=$USER_AGENT")

        val httpDataSourceFactory = DefaultHttpDataSource.Factory()
            .setAllowCrossProtocolRedirects(true)
            .setConnectTimeoutMs(15_000)
            .setReadTimeoutMs(15_000)
            .setUserAgent(USER_AGENT)
            .setDefaultRequestProperties(mapOf("Accept" to "video/*, */*", "Accept-Encoding" to "identity"))

        val dataSourceFactory = DefaultDataSource.Factory(context, httpDataSourceFactory)
        val mediaSourceFactory = DefaultMediaSourceFactory(context).setDataSourceFactory(dataSourceFactory)

        exoPlayer = ExoPlayer.Builder(context)
            .setMediaSourceFactory(mediaSourceFactory)
            .build()
            .apply {
                addListener(object : Player.Listener {
                    override fun onPlaybackStateChanged(playbackState: Int) {
                        val name = when (playbackState) {
                            Player.STATE_IDLE -> "IDLE"
                            Player.STATE_BUFFERING -> "BUFFERING"
                            Player.STATE_READY -> "READY"
                            Player.STATE_ENDED -> "ENDED"
                            else -> "UNKNOWN($playbackState)"
                        }
                        _isBuffering.value = playbackState == Player.STATE_BUFFERING
                        // Video completion — set ended flag so UI can show replay option
                        _isEnded.value = playbackState == Player.STATE_ENDED
                        Log.d(TAG, "[STATE] $name | pos=${currentPosition}ms | dur=${duration}ms | bufferedPct=${bufferedPercentage}%")
                    }

                    override fun onIsPlayingChanged(isPlaying: Boolean) {
                        _isPlaying.value = isPlaying
                        Log.d(TAG, "[PLAYING] isPlaying=$isPlaying | pos=${currentPosition}ms | dur=${duration}ms")
                    }

                    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                        Log.d(TAG, "[MEDIA_ITEM] uri=${mediaItem?.localConfiguration?.uri}")
                    }

                    override fun onTracksChanged(tracks: Tracks) {
                        val audio = mutableListOf<TrackInfo>()
                        val subs = mutableListOf<TrackInfo>()
                        tracks.groups.forEachIndexed { gi, group ->
                            val type = group.type
                            val format = if (group.length > 0) group.getTrackFormat(0) else null
                            val selected = (0 until group.length).any { group.isTrackSelected(it) }
                            val label = format?.label ?: format?.language ?: "Track ${gi + 1}"
                            Log.d(TAG, "[TRACKS] group[$gi] type=$type selected=$selected mime=${format?.sampleMimeType} lang=${format?.language}")
                            when (type) {
                                C.TRACK_TYPE_AUDIO -> audio.add(TrackInfo(gi, 0, label, selected))
                                C.TRACK_TYPE_TEXT -> subs.add(TrackInfo(gi, 0, label, selected))
                            }
                        }
                        _audioTracks.value = audio
                        _subtitleTracks.value = subs
                    }

                    override fun onVideoSizeChanged(videoSize: VideoSize) {
                        Log.d(TAG, "[VIDEO_SIZE] ${videoSize.width}x${videoSize.height}")
                        // Track video dimensions for PiP aspect ratio calculation
                        _videoWidth.value = videoSize.width
                        _videoHeight.value = videoSize.height
                    }

                    override fun onRenderedFirstFrame() {
                        Log.d(TAG, "[FIRST_FRAME] pos=${exoPlayer?.currentPosition}ms")
                    }

                    override fun onPositionDiscontinuity(old: Player.PositionInfo, new: Player.PositionInfo, reason: Int) {
                        Log.d(TAG, "[DISCONTINUITY] reason=$reason from=${old.positionMs}ms to=${new.positionMs}ms")
                    }

                    override fun onPlayerError(e: PlaybackException) {
                        val httpCode = extractHttpCode(e)
                        Log.e(TAG, "[ERROR] errorCode=${e.errorCode} http=$httpCode msg=${e.message}", e)
                        _error.value = PlaybackError(
                            message = buildTechnicalMessage(e, httpCode),
                            httpStatusCode = httpCode,
                            userMessage = buildUserMessage(e, httpCode)
                        )
                    }

                    override fun onPlayerErrorChanged(error: PlaybackException?) {
                        if (error == null) Log.d(TAG, "[ERROR_CLEARED]")
                    }
                })
            }
        // MediaSession — connects ExoPlayer to lock screen, notification shade, Bluetooth, Android Auto
        mediaSession = MediaSession.Builder(context, exoPlayer!!).build()
        Log.d(TAG, "[INIT] ExoPlayer + MediaSession ready")
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
        httpCode == 403 -> "Access denied (403). This video may be protected."
        httpCode == 404 -> "Video not found (404)."
        httpCode == 429 -> "Too many requests (429). Please wait."
        httpCode != null && httpCode >= 500 -> "Server error ($httpCode)."
        httpCode != null -> "HTTP error $httpCode."
        isSslError(e) -> "SSL/TLS error."
        e.errorCode == PlaybackException.ERROR_CODE_IO_CLEARTEXT_NOT_PERMITTED -> "Cleartext HTTP blocked."
        e.errorCode == PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_FAILED -> "Network connection failed."
        e.errorCode == PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_TIMEOUT -> "Connection timed out."
        e.errorCode == PlaybackException.ERROR_CODE_PARSING_CONTAINER_UNSUPPORTED -> "Unsupported video format."
        e.errorCode == PlaybackException.ERROR_CODE_DECODER_INIT_FAILED -> "Decoder init failed."
        else -> "Playback failed (${e.errorCode})."
    }

    private fun isSslError(e: PlaybackException): Boolean {
        var cause: Throwable? = e
        while (cause != null) {
            if (cause is javax.net.ssl.SSLException) return true
            cause = cause.cause
        }
        return false
    }

    private fun buildTechnicalMessage(e: PlaybackException, httpCode: Int?): String {
        val parts = mutableListOf("errorCode=${e.errorCode}")
        if (httpCode != null) parts.add("http=$httpCode")
        parts.add("msg=${e.message}")
        return parts.joinToString(" | ")
    }

    private fun mimeTypeForUrl(url: String): String? = when {
        url.endsWith(".mp4", ignoreCase = true) || url.endsWith(".m4v", ignoreCase = true) -> MimeTypes.VIDEO_MP4
        url.endsWith(".webm", ignoreCase = true) -> MimeTypes.VIDEO_WEBM
        url.endsWith(".mkv", ignoreCase = true) -> MimeTypes.VIDEO_MATROSKA
        url.endsWith(".mov", ignoreCase = true) -> MimeTypes.VIDEO_MP4
        else -> null
    }

    fun playVideo(url: String, startPosition: Long = 0L, videoId: String = "", title: String = "") {
        _error.value = null
        // Clear ended state when a new video starts
        _isEnded.value = false
        val mime = mimeTypeForUrl(url)
        val isLocal = url.startsWith("file://")
        Log.d(TAG, "[PLAY] VIDEO_ID=$videoId TITLE=$title SOURCE=${if (isLocal) "LOCAL" else "REMOTE"} URL=$url MIME=${mime ?: "auto"} START=${startPosition}ms")

        // MediaSession — set title in MediaMetadata so lock screen / notification shows it
        val mediaItem = MediaItem.Builder()
            .setUri(url)
            .apply { if (mime != null) setMimeType(mime) }
            .setMediaMetadata(
                androidx.media3.common.MediaMetadata.Builder()
                    .setTitle(title)
                    .build()
            )
            .build()

        exoPlayer?.apply {
            setMediaItem(mediaItem)
            seekTo(startPosition)
            prepare()
            play()
            Log.d(TAG, "[PLAY] prepare()+play() called")
        } ?: Log.e(TAG, "[PLAY] ExoPlayer is null")
    }

    // Load local subtitle file — rebuilds MediaItem with subtitle configuration
    fun loadLocalSubtitle(subtitleUri: String) {
        val player = exoPlayer ?: return
        val currentItem = player.currentMediaItem ?: return
        Log.d(TAG, "[SUBTITLE] loading local subtitle uri=$subtitleUri")
        val subtitleConfig = MediaItem.SubtitleConfiguration.Builder(android.net.Uri.parse(subtitleUri))
            .setMimeType(MimeTypes.APPLICATION_SUBRIP)
            .setSelectionFlags(C.SELECTION_FLAG_DEFAULT)
            .build()
        val newItem = currentItem.buildUpon()
            .setSubtitleConfigurations(listOf(subtitleConfig))
            .build()
        val pos = player.currentPosition
        player.setMediaItem(newItem, pos)
        player.prepare()
        player.play()
        Log.d(TAG, "[SUBTITLE] local subtitle applied, resuming from ${pos}ms")
    }

    fun pause() { exoPlayer?.pause(); Log.d(TAG, "[PAUSE] pos=${exoPlayer?.currentPosition}ms") }
    fun resume() { exoPlayer?.play(); Log.d(TAG, "[RESUME] pos=${exoPlayer?.currentPosition}ms") }

    // Video completion — restart from beginning (YouTube-style: press play after end = restart)
    fun restartFromBeginning() {
        Log.d(TAG, "[RESTART] seeking to 0 and playing")
        _isEnded.value = false
        exoPlayer?.seekTo(0L)
        exoPlayer?.play()
    }

    // Stops playback and resets media without destroying the ExoPlayer instance
    fun stop() {
        Log.d(TAG, "[STOP] pos=${exoPlayer?.currentPosition}ms")
        exoPlayer?.stop()
        exoPlayer?.clearMediaItems()
        _isPlaying.value = false
        _isBuffering.value = false
        _error.value = null
        _playbackSpeed.value = 1f
        _audioTracks.value = emptyList()
        _subtitleTracks.value = emptyList()
        // Reset playback speed to normal
        exoPlayer?.playbackParameters = PlaybackParameters(1f)
        Log.d(TAG, "[STOP] done")
    }

    fun seekTo(position: Long) {
        val clamped = position.coerceAtLeast(0L)
        Log.d(TAG, "[SEEK] ${clamped}ms")
        exoPlayer?.seekTo(clamped)
    }

    fun setSpeed(speed: Float) {
        _playbackSpeed.value = speed
        exoPlayer?.playbackParameters = PlaybackParameters(speed)
        Log.d(TAG, "[SPEED] $speed")
    }

    fun cycleRepeatMode() {
        val next = when (exoPlayer?.repeatMode ?: Player.REPEAT_MODE_OFF) {
            Player.REPEAT_MODE_OFF -> Player.REPEAT_MODE_ONE
            Player.REPEAT_MODE_ONE -> Player.REPEAT_MODE_OFF
            else -> Player.REPEAT_MODE_OFF
        }
        exoPlayer?.repeatMode = next
        _repeatMode.value = next
        Log.d(TAG, "[REPEAT] mode=$next")
    }

    fun selectAudioTrack(groupIndex: Int) {
        val player = exoPlayer ?: return
        val params = player.trackSelectionParameters.buildUpon()
            .setTrackTypeDisabled(C.TRACK_TYPE_AUDIO, false)
        val tracks = player.currentTracks
        if (groupIndex < tracks.groups.size) {
            val group = tracks.groups[groupIndex]
            params.addOverride(androidx.media3.common.TrackSelectionOverride(group.mediaTrackGroup, 0))
        }
        player.trackSelectionParameters = params.build()
        Log.d(TAG, "[AUDIO_TRACK] selected groupIndex=$groupIndex")
    }

    fun selectSubtitleTrack(groupIndex: Int?) {
        val player = exoPlayer ?: return
        val params = player.trackSelectionParameters.buildUpon()
        if (groupIndex == null) {
            params.setTrackTypeDisabled(C.TRACK_TYPE_TEXT, true)
            Log.d(TAG, "[SUBTITLE] disabled")
        } else {
            params.setTrackTypeDisabled(C.TRACK_TYPE_TEXT, false)
            val tracks = player.currentTracks
            if (groupIndex < tracks.groups.size) {
                val group = tracks.groups[groupIndex]
                params.addOverride(androidx.media3.common.TrackSelectionOverride(group.mediaTrackGroup, 0))
            }
            Log.d(TAG, "[SUBTITLE] selected groupIndex=$groupIndex")
        }
        player.trackSelectionParameters = params.build()
    }

    // 0-100 = system volume, 101-200 = ExoPlayer software boost (1x-2x)
    fun setVolume(percent: Int) {
        val clamped = percent.coerceIn(0, 200)
        val exoVol = if (clamped <= 100) 1f else clamped / 100f
        exoPlayer?.volume = exoVol
        Log.d(TAG, "[VOLUME] $clamped% exoVol=$exoVol")
    }

    fun getCurrentPosition(): Long = exoPlayer?.currentPosition ?: 0L
    fun getDuration(): Long = exoPlayer?.duration?.coerceAtLeast(0L) ?: 0L
    fun getBufferedPercentage(): Int = exoPlayer?.bufferedPercentage ?: 0

    fun release() {
        Log.d(TAG, "[RELEASE] pos=${exoPlayer?.currentPosition}ms")
        // MediaSession — release before ExoPlayer to avoid dangling references
        mediaSession?.release()
        mediaSession = null
        exoPlayer?.release()
        exoPlayer = null
        _isPlaying.value = false
        _isBuffering.value = false
        _error.value = null
        _audioTracks.value = emptyList()
        _subtitleTracks.value = emptyList()
        Log.d(TAG, "[RELEASE] done")
    }

    fun getPlayerInstance(): Player? = exoPlayer
}
