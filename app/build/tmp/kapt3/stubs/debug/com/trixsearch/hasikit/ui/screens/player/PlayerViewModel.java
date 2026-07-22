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

@kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000F\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\t\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\b\u0007\u0018\u00002\u00020\u0001B)\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\t\u00a2\u0006\u0004\b\n\u0010\u000bJ\u001e\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u0011J\u0016\u0010\u0013\u001a\u00020\u00112\u0006\u0010\u000e\u001a\u00020\u000fH\u0086@\u00a2\u0006\u0002\u0010\u0014J\u0018\u0010\u0015\u001a\u0004\u0018\u00010\u000f2\u0006\u0010\u0016\u001a\u00020\u000fH\u0086@\u00a2\u0006\u0002\u0010\u0014J\u0018\u0010\u0017\u001a\u0004\u0018\u00010\u00182\u0006\u0010\u0019\u001a\u00020\u001aH\u0082@\u00a2\u0006\u0002\u0010\u001bR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u001c"}, d2 = {"Lcom/trixsearch/hasikit/ui/screens/player/PlayerViewModel;", "Landroidx/lifecycle/ViewModel;", "repository", "Lcom/trixsearch/hasikit/domain/repository/VideoRepository;", "downloadManager", "Lcom/trixsearch/hasikit/download/HasikitDownloadManager;", "channelRepository", "Lcom/trixsearch/hasikit/telegram/domain/repository/TelegramChannelRepository;", "telegramClientService", "Lcom/trixsearch/hasikit/telegram/service/TelegramClientService;", "<init>", "(Lcom/trixsearch/hasikit/domain/repository/VideoRepository;Lcom/trixsearch/hasikit/download/HasikitDownloadManager;Lcom/trixsearch/hasikit/telegram/domain/repository/TelegramChannelRepository;Lcom/trixsearch/hasikit/telegram/service/TelegramClientService;)V", "saveProgress", "", "videoId", "", "position", "", "duration", "getInitialPosition", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "resolvePlayUrl", "telegramFileId", "getFileInfo", "Lorg/drinkless/tdlib/TdApi$File;", "fileId", "", "(ILkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_debug"})
@dagger.hilt.android.lifecycle.HiltViewModel()
public final class PlayerViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull()
    private final com.trixsearch.hasikit.domain.repository.VideoRepository repository = null;
    @org.jetbrains.annotations.NotNull()
    private final com.trixsearch.hasikit.download.HasikitDownloadManager downloadManager = null;
    @org.jetbrains.annotations.NotNull()
    private final com.trixsearch.hasikit.telegram.domain.repository.TelegramChannelRepository channelRepository = null;
    @org.jetbrains.annotations.NotNull()
    private final com.trixsearch.hasikit.telegram.service.TelegramClientService telegramClientService = null;
    
    @javax.inject.Inject()
    public PlayerViewModel(@org.jetbrains.annotations.NotNull()
    com.trixsearch.hasikit.domain.repository.VideoRepository repository, @org.jetbrains.annotations.NotNull()
    com.trixsearch.hasikit.download.HasikitDownloadManager downloadManager, @org.jetbrains.annotations.NotNull()
    com.trixsearch.hasikit.telegram.domain.repository.TelegramChannelRepository channelRepository, @org.jetbrains.annotations.NotNull()
    com.trixsearch.hasikit.telegram.service.TelegramClientService telegramClientService) {
        super();
    }
    
    public final void saveProgress(@org.jetbrains.annotations.NotNull()
    java.lang.String videoId, long position, long duration) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getInitialPosition(@org.jetbrains.annotations.NotNull()
    java.lang.String videoId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Long> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object resolvePlayUrl(@org.jetbrains.annotations.NotNull()
    java.lang.String telegramFileId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.String> $completion) {
        return null;
    }
    
    private final java.lang.Object getFileInfo(int fileId, kotlin.coroutines.Continuation<? super org.drinkless.tdlib.TdApi.File> $completion) {
        return null;
    }
}