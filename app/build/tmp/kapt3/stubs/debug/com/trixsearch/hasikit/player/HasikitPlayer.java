package com.trixsearch.hasikit.player;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import androidx.media3.common.C;
import androidx.media3.common.MediaItem;
import androidx.media3.common.MediaMetadata;
import androidx.media3.common.MimeTypes;
import androidx.media3.common.PlaybackException;
import androidx.media3.common.PlaybackParameters;
import androidx.media3.common.Player;
import androidx.media3.common.Tracks;
import androidx.media3.common.VideoSize;
import androidx.media3.datasource.DefaultDataSource;
import androidx.media3.datasource.DefaultHttpDataSource;
import androidx.media3.datasource.HttpDataSource;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory;
import androidx.media3.session.MediaSession;
import dagger.hilt.android.qualifiers.ApplicationContext;
import kotlinx.coroutines.Dispatchers;
import kotlinx.coroutines.flow.StateFlow;
import javax.inject.Inject;
import javax.inject.Singleton;

@javax.inject.Singleton()
@kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000|\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\b\n\u0002\b\u0004\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0007\n\u0002\b\u0006\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\b\n\u0002\u0010\t\n\u0002\b\u001d\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0007\u0018\u00002\u00020\u0001:\u0001[B\u0013\b\u0007\u0012\b\b\u0001\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0004\b\u0004\u0010\u0005J\u0006\u0010\u000e\u001a\u00020\fJ\u0006\u0010\u000f\u001a\u00020\fJ\u0006\u0010,\u001a\u00020-J\u0017\u0010.\u001a\u0004\u0018\u00010\f2\u0006\u0010/\u001a\u000200H\u0002\u00a2\u0006\u0002\u00101J\u001f\u00102\u001a\u0002032\u0006\u0010/\u001a\u0002002\b\u00104\u001a\u0004\u0018\u00010\fH\u0002\u00a2\u0006\u0002\u00105J\u0010\u00106\u001a\u00020\u00112\u0006\u0010/\u001a\u000200H\u0002J\u001f\u00107\u001a\u0002032\u0006\u0010/\u001a\u0002002\b\u00104\u001a\u0004\u0018\u00010\fH\u0002\u00a2\u0006\u0002\u00105J\u0012\u00108\u001a\u0004\u0018\u0001032\u0006\u00109\u001a\u000203H\u0002J8\u0010:\u001a\u00020-2\u0006\u00109\u001a\u0002032\b\b\u0002\u0010;\u001a\u00020<2\b\b\u0002\u0010=\u001a\u0002032\b\b\u0002\u0010>\u001a\u0002032\n\b\u0002\u0010?\u001a\u0004\u0018\u000103J\u000e\u0010@\u001a\u00020-2\u0006\u0010A\u001a\u000203J\u0006\u0010B\u001a\u00020-J\u0006\u0010C\u001a\u00020-J\u0006\u0010D\u001a\u00020-J\u0006\u0010E\u001a\u00020-J\u000e\u0010F\u001a\u00020-2\u0006\u0010G\u001a\u00020<J\u000e\u0010H\u001a\u00020-2\u0006\u0010I\u001a\u00020\u001eJ\u0006\u0010J\u001a\u00020-J\u000e\u0010K\u001a\u00020-2\u0006\u0010L\u001a\u00020\fJ\u0015\u0010M\u001a\u00020-2\b\u0010L\u001a\u0004\u0018\u00010\f\u00a2\u0006\u0002\u0010NJ\u0006\u0010Q\u001a\u00020-J\u0006\u0010R\u001a\u00020-J\u000e\u0010S\u001a\u00020-2\u0006\u0010T\u001a\u00020\fJ\u0006\u0010U\u001a\u00020<J\u0006\u0010V\u001a\u00020<J\u0006\u0010W\u001a\u00020\fJ\u0006\u0010X\u001a\u00020-J\b\u0010Y\u001a\u0004\u0018\u00010ZR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0006\u001a\u0004\u0018\u00010\u0007X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\b\u001a\u0004\u0018\u00010\tX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0014\u0010\n\u001a\b\u0012\u0004\u0012\u00020\f0\u000bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\r\u001a\b\u0012\u0004\u0012\u00020\f0\u000bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00110\u000bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00110\u0013\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0014R\u0014\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\u00110\u000bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\u00110\u0013\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0014R\u0014\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\u00110\u000bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\u00110\u0013\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0014R\u0016\u0010\u0019\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u001a0\u000bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0019\u0010\u001b\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u001a0\u0013\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001c\u0010\u0014R\u0014\u0010\u001d\u001a\b\u0012\u0004\u0012\u00020\u001e0\u000bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u001f\u001a\b\u0012\u0004\u0012\u00020\u001e0\u0013\u00a2\u0006\b\n\u0000\u001a\u0004\b \u0010\u0014R\u0014\u0010!\u001a\b\u0012\u0004\u0012\u00020\f0\u000bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\"\u001a\b\u0012\u0004\u0012\u00020\f0\u0013\u00a2\u0006\b\n\u0000\u001a\u0004\b#\u0010\u0014R\u001a\u0010$\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020&0%0\u000bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001d\u0010\'\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020&0%0\u0013\u00a2\u0006\b\n\u0000\u001a\u0004\b(\u0010\u0014R\u001a\u0010)\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020&0%0\u000bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001d\u0010*\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020&0%0\u0013\u00a2\u0006\b\n\u0000\u001a\u0004\b+\u0010\u0014R\u000e\u0010O\u001a\u00020\u0011X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010P\u001a\u00020\fX\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006\\"}, d2 = {"Lcom/trixsearch/hasikit/player/HasikitPlayer;", "", "context", "Landroid/content/Context;", "<init>", "(Landroid/content/Context;)V", "exoPlayer", "Landroidx/media3/exoplayer/ExoPlayer;", "mediaSession", "Landroidx/media3/session/MediaSession;", "_videoWidth", "Lkotlinx/coroutines/flow/MutableStateFlow;", "", "_videoHeight", "getVideoWidth", "getVideoHeight", "_isPlaying", "", "isPlaying", "Lkotlinx/coroutines/flow/StateFlow;", "()Lkotlinx/coroutines/flow/StateFlow;", "_isEnded", "isEnded", "_isBuffering", "isBuffering", "_error", "Lcom/trixsearch/hasikit/player/HasikitPlayer$PlaybackError;", "error", "getError", "_playbackSpeed", "", "playbackSpeed", "getPlaybackSpeed", "_repeatMode", "repeatMode", "getRepeatMode", "_audioTracks", "", "Lcom/trixsearch/hasikit/player/TrackInfo;", "audioTracks", "getAudioTracks", "_subtitleTracks", "subtitleTracks", "getSubtitleTracks", "initialize", "", "extractHttpCode", "e", "Landroidx/media3/common/PlaybackException;", "(Landroidx/media3/common/PlaybackException;)Ljava/lang/Integer;", "buildUserMessage", "", "httpCode", "(Landroidx/media3/common/PlaybackException;Ljava/lang/Integer;)Ljava/lang/String;", "isSslError", "buildTechnicalMessage", "mimeTypeForUrl", "url", "playVideo", "startPosition", "", "videoId", "title", "thumbnailPath", "loadLocalSubtitle", "subtitleUri", "pause", "resume", "restartFromBeginning", "stop", "seekTo", "position", "setSpeed", "speed", "cycleRepeatMode", "selectAudioTrack", "groupIndex", "selectSubtitleTrack", "(Ljava/lang/Integer;)V", "isMuted", "volumeBeforeMute", "muteAudio", "unmuteIfMuted", "setVolume", "percent", "getCurrentPosition", "getDuration", "getBufferedPercentage", "release", "getPlayerInstance", "Landroidx/media3/common/Player;", "PlaybackError", "app_debug"})
public final class HasikitPlayer {
    @org.jetbrains.annotations.NotNull()
    private final android.content.Context context = null;
    @org.jetbrains.annotations.Nullable()
    private androidx.media3.exoplayer.ExoPlayer exoPlayer;
    @org.jetbrains.annotations.Nullable()
    private androidx.media3.session.MediaSession mediaSession;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.Integer> _videoWidth = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.Integer> _videoHeight = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.Boolean> _isPlaying = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.Boolean> isPlaying = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.Boolean> _isEnded = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.Boolean> isEnded = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.Boolean> _isBuffering = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.Boolean> isBuffering = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<com.trixsearch.hasikit.player.HasikitPlayer.PlaybackError> _error = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<com.trixsearch.hasikit.player.HasikitPlayer.PlaybackError> error = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.Float> _playbackSpeed = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.Float> playbackSpeed = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.Integer> _repeatMode = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.Integer> repeatMode = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.util.List<com.trixsearch.hasikit.player.TrackInfo>> _audioTracks = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.util.List<com.trixsearch.hasikit.player.TrackInfo>> audioTracks = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.util.List<com.trixsearch.hasikit.player.TrackInfo>> _subtitleTracks = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.util.List<com.trixsearch.hasikit.player.TrackInfo>> subtitleTracks = null;
    private boolean isMuted = false;
    private int volumeBeforeMute = 100;
    
    @javax.inject.Inject()
    public HasikitPlayer(@dagger.hilt.android.qualifiers.ApplicationContext()
    @org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        super();
    }
    
    public final int getVideoWidth() {
        return 0;
    }
    
    public final int getVideoHeight() {
        return 0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.Boolean> isPlaying() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.Boolean> isEnded() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.Boolean> isBuffering() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<com.trixsearch.hasikit.player.HasikitPlayer.PlaybackError> getError() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.Float> getPlaybackSpeed() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.Integer> getRepeatMode() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.util.List<com.trixsearch.hasikit.player.TrackInfo>> getAudioTracks() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.util.List<com.trixsearch.hasikit.player.TrackInfo>> getSubtitleTracks() {
        return null;
    }
    
    public final void initialize() {
    }
    
    private final java.lang.Integer extractHttpCode(androidx.media3.common.PlaybackException e) {
        return null;
    }
    
    private final java.lang.String buildUserMessage(androidx.media3.common.PlaybackException e, java.lang.Integer httpCode) {
        return null;
    }
    
    private final boolean isSslError(androidx.media3.common.PlaybackException e) {
        return false;
    }
    
    private final java.lang.String buildTechnicalMessage(androidx.media3.common.PlaybackException e, java.lang.Integer httpCode) {
        return null;
    }
    
    private final java.lang.String mimeTypeForUrl(java.lang.String url) {
        return null;
    }
    
    public final void playVideo(@org.jetbrains.annotations.NotNull()
    java.lang.String url, long startPosition, @org.jetbrains.annotations.NotNull()
    java.lang.String videoId, @org.jetbrains.annotations.NotNull()
    java.lang.String title, @org.jetbrains.annotations.Nullable()
    java.lang.String thumbnailPath) {
    }
    
    public final void loadLocalSubtitle(@org.jetbrains.annotations.NotNull()
    java.lang.String subtitleUri) {
    }
    
    public final void pause() {
    }
    
    public final void resume() {
    }
    
    public final void restartFromBeginning() {
    }
    
    public final void stop() {
    }
    
    public final void seekTo(long position) {
    }
    
    public final void setSpeed(float speed) {
    }
    
    public final void cycleRepeatMode() {
    }
    
    public final void selectAudioTrack(int groupIndex) {
    }
    
    public final void selectSubtitleTrack(@org.jetbrains.annotations.Nullable()
    java.lang.Integer groupIndex) {
    }
    
    public final void muteAudio() {
    }
    
    public final void unmuteIfMuted() {
    }
    
    public final void setVolume(int percent) {
    }
    
    public final long getCurrentPosition() {
        return 0L;
    }
    
    public final long getDuration() {
        return 0L;
    }
    
    public final int getBufferedPercentage() {
        return 0;
    }
    
    public final void release() {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final androidx.media3.common.Player getPlayerInstance() {
        return null;
    }
    
    @kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0002\b\u000f\n\u0002\u0010\u000b\n\u0002\b\u0004\b\u0086\b\u0018\u00002\u00020\u0001B#\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0003\u00a2\u0006\u0004\b\u0007\u0010\bJ\t\u0010\u000f\u001a\u00020\u0003H\u00c6\u0003J\u0010\u0010\u0010\u001a\u0004\u0018\u00010\u0005H\u00c6\u0003\u00a2\u0006\u0002\u0010\fJ\t\u0010\u0011\u001a\u00020\u0003H\u00c6\u0003J.\u0010\u0012\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u0003H\u00c6\u0001\u00a2\u0006\u0002\u0010\u0013J\u0013\u0010\u0014\u001a\u00020\u00152\b\u0010\u0016\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0017\u001a\u00020\u0005H\u00d6\u0001J\t\u0010\u0018\u001a\u00020\u0003H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u0015\u0010\u0004\u001a\u0004\u0018\u00010\u0005\u00a2\u0006\n\n\u0002\u0010\r\u001a\u0004\b\u000b\u0010\fR\u0011\u0010\u0006\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\n\u00a8\u0006\u0019"}, d2 = {"Lcom/trixsearch/hasikit/player/HasikitPlayer$PlaybackError;", "", "message", "", "httpStatusCode", "", "userMessage", "<init>", "(Ljava/lang/String;Ljava/lang/Integer;Ljava/lang/String;)V", "getMessage", "()Ljava/lang/String;", "getHttpStatusCode", "()Ljava/lang/Integer;", "Ljava/lang/Integer;", "getUserMessage", "component1", "component2", "component3", "copy", "(Ljava/lang/String;Ljava/lang/Integer;Ljava/lang/String;)Lcom/trixsearch/hasikit/player/HasikitPlayer$PlaybackError;", "equals", "", "other", "hashCode", "toString", "app_debug"})
    public static final class PlaybackError {
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String message = null;
        @org.jetbrains.annotations.Nullable()
        private final java.lang.Integer httpStatusCode = null;
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String userMessage = null;
        
        public PlaybackError(@org.jetbrains.annotations.NotNull()
        java.lang.String message, @org.jetbrains.annotations.Nullable()
        java.lang.Integer httpStatusCode, @org.jetbrains.annotations.NotNull()
        java.lang.String userMessage) {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String getMessage() {
            return null;
        }
        
        @org.jetbrains.annotations.Nullable()
        public final java.lang.Integer getHttpStatusCode() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String getUserMessage() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String component1() {
            return null;
        }
        
        @org.jetbrains.annotations.Nullable()
        public final java.lang.Integer component2() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String component3() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.trixsearch.hasikit.player.HasikitPlayer.PlaybackError copy(@org.jetbrains.annotations.NotNull()
        java.lang.String message, @org.jetbrains.annotations.Nullable()
        java.lang.Integer httpStatusCode, @org.jetbrains.annotations.NotNull()
        java.lang.String userMessage) {
            return null;
        }
        
        @java.lang.Override()
        public boolean equals(@org.jetbrains.annotations.Nullable()
        java.lang.Object other) {
            return false;
        }
        
        @java.lang.Override()
        public int hashCode() {
            return 0;
        }
        
        @java.lang.Override()
        @org.jetbrains.annotations.NotNull()
        public java.lang.String toString() {
            return null;
        }
    }
}