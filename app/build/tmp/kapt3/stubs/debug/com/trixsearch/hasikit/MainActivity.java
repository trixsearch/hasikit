package com.trixsearch.hasikit;

import android.content.Intent;
import android.Manifest;
import android.app.PictureInPictureParams;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Rational;
import androidx.activity.ComponentActivity;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.compose.material.icons.Icons;
import androidx.compose.material3.*;
import androidx.compose.runtime.*;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.tooling.preview.Preview;
import androidx.core.content.ContextCompat;
import com.trixsearch.hasikit.player.HasikitPlayer;
import com.trixsearch.hasikit.telegram.domain.repository.TelegramAuthRepository;
import com.trixsearch.hasikit.ui.navigation.Screen;
import com.trixsearch.hasikit.ui.theme.AppTheme;
import com.trixsearch.hasikit.util.AppLanguage;
import dagger.hilt.android.AndroidEntryPoint;
import javax.inject.Inject;

@dagger.hilt.android.AndroidEntryPoint()
@kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000H\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\b\u0007\u0018\u00002\u00020\u0001B\u0007\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0012\u0010\u0013\u001a\u00020\u00142\b\u0010\u0015\u001a\u0004\u0018\u00010\u0016H\u0014J\b\u0010\u0017\u001a\u00020\u0014H\u0014J\u0018\u0010\u0018\u001a\u00020\u00142\u0006\u0010\u0019\u001a\u00020\u001a2\u0006\u0010\u001b\u001a\u00020\u001cH\u0016J\b\u0010\u001d\u001a\u00020\u0014H\u0002J\b\u0010\u001e\u001a\u00020\u0014H\u0014J\u0010\u0010\u001f\u001a\u00020\u00142\u0006\u0010 \u001a\u00020!H\u0014R\u001e\u0010\u0004\u001a\u00020\u00058\u0006@\u0006X\u0087.\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0006\u0010\u0007\"\u0004\b\b\u0010\tR\u001e\u0010\n\u001a\u00020\u000b8\u0006@\u0006X\u0087.\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\f\u0010\r\"\u0004\b\u000e\u0010\u000fR\u0014\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00120\u0011X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\""}, d2 = {"Lcom/trixsearch/hasikit/MainActivity;", "Landroidx/activity/ComponentActivity;", "<init>", "()V", "player", "Lcom/trixsearch/hasikit/player/HasikitPlayer;", "getPlayer", "()Lcom/trixsearch/hasikit/player/HasikitPlayer;", "setPlayer", "(Lcom/trixsearch/hasikit/player/HasikitPlayer;)V", "telegramAuthRepository", "Lcom/trixsearch/hasikit/telegram/domain/repository/TelegramAuthRepository;", "getTelegramAuthRepository", "()Lcom/trixsearch/hasikit/telegram/domain/repository/TelegramAuthRepository;", "setTelegramAuthRepository", "(Lcom/trixsearch/hasikit/telegram/domain/repository/TelegramAuthRepository;)V", "notificationPermissionLauncher", "Landroidx/activity/result/ActivityResultLauncher;", "", "onCreate", "", "savedInstanceState", "Landroid/os/Bundle;", "onUserLeaveHint", "onPictureInPictureModeChanged", "isInPiPMode", "", "newConfig", "Landroid/content/res/Configuration;", "requestNotificationPermissionIfNeeded", "onDestroy", "attachBaseContext", "newBase", "Landroid/content/Context;", "app_debug"})
public final class MainActivity extends androidx.activity.ComponentActivity {
    @javax.inject.Inject()
    public com.trixsearch.hasikit.player.HasikitPlayer player;
    @javax.inject.Inject()
    public com.trixsearch.hasikit.telegram.domain.repository.TelegramAuthRepository telegramAuthRepository;
    @org.jetbrains.annotations.NotNull()
    private final androidx.activity.result.ActivityResultLauncher<java.lang.String> notificationPermissionLauncher = null;
    
    public MainActivity() {
        super(0);
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.trixsearch.hasikit.player.HasikitPlayer getPlayer() {
        return null;
    }
    
    public final void setPlayer(@org.jetbrains.annotations.NotNull()
    com.trixsearch.hasikit.player.HasikitPlayer p0) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.trixsearch.hasikit.telegram.domain.repository.TelegramAuthRepository getTelegramAuthRepository() {
        return null;
    }
    
    public final void setTelegramAuthRepository(@org.jetbrains.annotations.NotNull()
    com.trixsearch.hasikit.telegram.domain.repository.TelegramAuthRepository p0) {
    }
    
    @java.lang.Override()
    protected void onCreate(@org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
    }
    
    @java.lang.Override()
    protected void onUserLeaveHint() {
    }
    
    @java.lang.Override()
    public void onPictureInPictureModeChanged(boolean isInPiPMode, @org.jetbrains.annotations.NotNull()
    android.content.res.Configuration newConfig) {
    }
    
    private final void requestNotificationPermissionIfNeeded() {
    }
    
    @java.lang.Override()
    protected void onDestroy() {
    }
    
    @java.lang.Override()
    protected void attachBaseContext(@org.jetbrains.annotations.NotNull()
    android.content.Context newBase) {
    }
}