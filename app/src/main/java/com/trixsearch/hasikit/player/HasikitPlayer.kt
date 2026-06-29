package com.trixsearch.hasikit.player

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HasikitPlayer @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var exoPlayer: ExoPlayer? = null

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying

    private val _isBuffering = MutableStateFlow(false)
    val isBuffering: StateFlow<Boolean> = _isBuffering

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun initialize() {
        if (exoPlayer == null) {
            exoPlayer = ExoPlayer.Builder(context).build().apply {
                addListener(object : Player.Listener {
                    override fun onIsPlayingChanged(isPlaying: Boolean) {
                        _isPlaying.value = isPlaying
                    }

                    override fun onPlaybackStateChanged(playbackState: Int) {
                        _isBuffering.value = playbackState == Player.STATE_BUFFERING
                    }

                    override fun onPlayerError(e: PlaybackException) {
                        _error.value = e.message ?: "Unknown playback error"
                    }
                })
            }
        }
    }

    fun playVideo(url: String, startPosition: Long = 0) {
        _error.value = null
        exoPlayer?.apply {
            setMediaItem(MediaItem.fromUri(url))
            seekTo(startPosition)
            prepare()
            play()
        }
    }

    fun pause() {
        exoPlayer?.pause()
    }

    fun resume() {
        exoPlayer?.play()
    }

    fun seekTo(position: Long) {
        exoPlayer?.seekTo(position)
    }

    fun release() {
        exoPlayer?.release()
        exoPlayer = null
        _isPlaying.value = false
        _isBuffering.value = false
        _error.value = null
    }

    fun getPlayerInstance(): Player? = exoPlayer
}
