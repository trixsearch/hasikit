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

@kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0002\b\r\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\u0019\b\u0002\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0004\b\u0006\u0010\u0007R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bj\u0002\b\fj\u0002\b\rj\u0002\b\u000ej\u0002\b\u000fj\u0002\b\u0010j\u0002\b\u0011\u00a8\u0006\u0012"}, d2 = {"Lcom/trixsearch/hasikit/ui/screens/player/VideoFitMode;", "", "label", "", "resizeMode", "", "<init>", "(Ljava/lang/String;ILjava/lang/String;I)V", "getLabel", "()Ljava/lang/String;", "getResizeMode", "()I", "FIT", "FILL", "ZOOM", "FIXED_WIDTH", "FIXED_HEIGHT", "STRETCH", "app_debug"})
public enum VideoFitMode {
    /*public static final*/ FIT /* = new FIT(null, 0) */,
    /*public static final*/ FILL /* = new FILL(null, 0) */,
    /*public static final*/ ZOOM /* = new ZOOM(null, 0) */,
    /*public static final*/ FIXED_WIDTH /* = new FIXED_WIDTH(null, 0) */,
    /*public static final*/ FIXED_HEIGHT /* = new FIXED_HEIGHT(null, 0) */,
    /*public static final*/ STRETCH /* = new STRETCH(null, 0) */;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String label = null;
    private final int resizeMode = 0;
    
    VideoFitMode(java.lang.String label, int resizeMode) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getLabel() {
        return null;
    }
    
    public final int getResizeMode() {
        return 0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public static kotlin.enums.EnumEntries<com.trixsearch.hasikit.ui.screens.player.VideoFitMode> getEntries() {
        return null;
    }
}