package com.trixsearch.hasikit.ui.screens.player;

import android.app.Activity;
import android.app.PictureInPictureParams;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.telecom.TelecomManager;
import android.telephony.TelephonyCallback;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.Rational;
import android.view.WindowManager;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.OptIn;
import androidx.compose.foundation.layout.*;
import androidx.compose.material.icons.Icons;
import androidx.compose.material.icons.filled.*;
import androidx.compose.material3.*;
import androidx.compose.runtime.*;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.text.font.FontWeight;
import androidx.lifecycle.ViewModel;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.ui.AspectRatioFrameLayout;
import androidx.media3.ui.PlayerView;
import com.trixsearch.hasikit.domain.model.WatchProgress;
import com.trixsearch.hasikit.domain.repository.VideoRepository;
import com.trixsearch.hasikit.download.HasikitDownloadManager;
import com.trixsearch.hasikit.player.HasikitPlayer;
import com.trixsearch.hasikit.telegram.domain.repository.TelegramChannelRepository;
import com.trixsearch.hasikit.telegram.service.TelegramClientService;
import dagger.hilt.android.lifecycle.HiltViewModel;
import kotlinx.coroutines.Dispatchers;
import org.drinkless.tdlib.TdApi;
import javax.inject.Inject;

@kotlin.Metadata(mv = {2, 1, 0}, k = 2, xi = 48, d1 = {"\u0000h\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\t\n\u0002\b\u0004\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u0007\n\u0002\b\u0003\n\u0002\u0010 \n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0003\u001ah\u0010\u0019\u001a\u00020\u001a2\u0006\u0010\u001b\u001a\u00020\u00072\u0006\u0010\u001c\u001a\u00020\u00072\u0006\u0010\u001d\u001a\u00020\u001e2\u0006\u0010\u001f\u001a\u00020\u00072\b\u0010 \u001a\u0004\u0018\u00010\u00072\b\b\u0002\u0010!\u001a\u00020\u00072\b\b\u0002\u0010\"\u001a\u00020\u00022\b\b\u0002\u0010#\u001a\u00020\u00022\f\u0010$\u001a\b\u0012\u0004\u0012\u00020\u001a0%2\b\b\u0002\u0010&\u001a\u00020\'H\u0007\u001a2\u0010(\u001a\u00020\u001a2\u0006\u0010)\u001a\u00020\u00112\u0012\u0010*\u001a\u000e\u0012\u0004\u0012\u00020\u0011\u0012\u0004\u0012\u00020\u001a0+2\f\u0010,\u001a\b\u0012\u0004\u0012\u00020\u001a0%H\u0003\u001a\u001a\u0010-\u001a\u00020\u001a2\u0006\u0010.\u001a\u00020\u00072\b\b\u0002\u0010/\u001a\u000200H\u0003\u001a\u0010\u00101\u001a\u00020\u00072\u0006\u00102\u001a\u00020\tH\u0002\"\u0014\u0010\u0000\u001a\b\u0012\u0004\u0012\u00020\u00020\u0001X\u0082\u0004\u00a2\u0006\u0002\n\u0000\"\u0014\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00020\u0001X\u0082\u0004\u00a2\u0006\u0002\n\u0000\"\u0014\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00020\u0001X\u0082\u0004\u00a2\u0006\u0002\n\u0000\"\u0014\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00020\u0001X\u0082\u0004\u00a2\u0006\u0002\n\u0000\"\u000e\u0010\u0006\u001a\u00020\u0007X\u0082T\u00a2\u0006\u0002\n\u0000\"\u000e\u0010\b\u001a\u00020\tX\u0082T\u00a2\u0006\u0002\n\u0000\"\u000e\u0010\n\u001a\u00020\tX\u0082T\u00a2\u0006\u0002\n\u0000\"\u000e\u0010\u000b\u001a\u00020\tX\u0082T\u00a2\u0006\u0002\n\u0000\"\u000e\u0010\f\u001a\u00020\tX\u0082T\u00a2\u0006\u0002\n\u0000\"\u000e\u0010\r\u001a\u00020\u000eX\u0082T\u00a2\u0006\u0002\n\u0000\"\u000e\u0010\u000f\u001a\u00020\u000eX\u0082T\u00a2\u0006\u0002\n\u0000\"\u000e\u0010\u0010\u001a\u00020\u0011X\u0082T\u00a2\u0006\u0002\n\u0000\"\u000e\u0010\u0012\u001a\u00020\u0011X\u0082T\u00a2\u0006\u0002\n\u0000\"\u000e\u0010\u0013\u001a\u00020\u0011X\u0082T\u00a2\u0006\u0002\n\u0000\"\u0014\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\u00110\u0015X\u0082\u0004\u00a2\u0006\u0002\n\u0000\"\u0014\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\u00170\u0015X\u0082\u0004\u00a2\u0006\u0002\n\u0000\"\u000e\u0010\u0018\u001a\u00020\u000eX\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u00063"}, d2 = {"KEY_RESUME_AFTER_CALL", "Landroidx/datastore/preferences/core/Preferences$Key;", "", "KEY_PAUSE_ON_HEADPHONE", "KEY_BACKGROUND_AUDIO", "KEY_AUTOPLAY_NEXT", "TAG", "", "CONTROLS_HIDE_DELAY", "", "SEEK_INCREMENT_MS", "LONG_PRESS_THRESHOLD_MS", "SEEK_SWIPE_RANGE_MS", "BRIGHTNESS_SWIPE_SENSITIVITY", "", "VOLUME_SWIPE_SENSITIVITY", "GESTURE_LOCK_THRESHOLD_PX", "", "DOUBLE_TAP_LEFT_ZONE", "DOUBLE_TAP_RIGHT_ZONE", "SPEED_OPTIONS", "", "FIT_CYCLE", "Lcom/trixsearch/hasikit/ui/screens/player/VideoFitMode;", "STREAM_FAIL_THRESHOLD", "PlayerScreen", "", "videoId", "title", "player", "Lcom/trixsearch/hasikit/player/HasikitPlayer;", "videoUrl", "localPath", "telegramFileId", "isStreamable", "isDownloaded", "onBack", "Lkotlin/Function0;", "viewModel", "Lcom/trixsearch/hasikit/ui/screens/player/PlayerViewModel;", "SpeedBottomSheet", "currentSpeed", "onSpeedSelected", "Lkotlin/Function1;", "onDismiss", "FeedbackPill", "text", "modifier", "Landroidx/compose/ui/Modifier;", "formatTime", "ms", "app_debug"})
public final class PlayerScreenKt {
    @org.jetbrains.annotations.NotNull()
    private static final androidx.datastore.preferences.core.Preferences.Key<java.lang.Boolean> KEY_RESUME_AFTER_CALL = null;
    @org.jetbrains.annotations.NotNull()
    private static final androidx.datastore.preferences.core.Preferences.Key<java.lang.Boolean> KEY_PAUSE_ON_HEADPHONE = null;
    @org.jetbrains.annotations.NotNull()
    private static final androidx.datastore.preferences.core.Preferences.Key<java.lang.Boolean> KEY_BACKGROUND_AUDIO = null;
    @org.jetbrains.annotations.NotNull()
    private static final androidx.datastore.preferences.core.Preferences.Key<java.lang.Boolean> KEY_AUTOPLAY_NEXT = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String TAG = "PLAYER_DEBUG";
    private static final long CONTROLS_HIDE_DELAY = 3000L;
    private static final long SEEK_INCREMENT_MS = 10000L;
    private static final long LONG_PRESS_THRESHOLD_MS = 500L;
    private static final long SEEK_SWIPE_RANGE_MS = 120000L;
    private static final int BRIGHTNESS_SWIPE_SENSITIVITY = 100;
    private static final int VOLUME_SWIPE_SENSITIVITY = 100;
    private static final float GESTURE_LOCK_THRESHOLD_PX = 20.0F;
    private static final float DOUBLE_TAP_LEFT_ZONE = 0.4F;
    private static final float DOUBLE_TAP_RIGHT_ZONE = 0.6F;
    @org.jetbrains.annotations.NotNull()
    private static final java.util.List<java.lang.Float> SPEED_OPTIONS = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.util.List<com.trixsearch.hasikit.ui.screens.player.VideoFitMode> FIT_CYCLE = null;
    private static final int STREAM_FAIL_THRESHOLD = 2;
    
    @androidx.annotation.OptIn(markerClass = {androidx.media3.common.util.UnstableApi.class, androidx.compose.material3.ExperimentalMaterial3Api.class})
    @androidx.compose.runtime.Composable()
    public static final void PlayerScreen(@org.jetbrains.annotations.NotNull()
    java.lang.String videoId, @org.jetbrains.annotations.NotNull()
    java.lang.String title, @org.jetbrains.annotations.NotNull()
    com.trixsearch.hasikit.player.HasikitPlayer player, @org.jetbrains.annotations.NotNull()
    java.lang.String videoUrl, @org.jetbrains.annotations.Nullable()
    java.lang.String localPath, @org.jetbrains.annotations.NotNull()
    java.lang.String telegramFileId, boolean isStreamable, boolean isDownloaded, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onBack, @org.jetbrains.annotations.NotNull()
    com.trixsearch.hasikit.ui.screens.player.PlayerViewModel viewModel) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void SpeedBottomSheet(float currentSpeed, kotlin.jvm.functions.Function1<? super java.lang.Float, kotlin.Unit> onSpeedSelected, kotlin.jvm.functions.Function0<kotlin.Unit> onDismiss) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void FeedbackPill(java.lang.String text, androidx.compose.ui.Modifier modifier) {
    }
    
    private static final java.lang.String formatTime(long ms) {
        return null;
    }
}