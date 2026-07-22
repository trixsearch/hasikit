package com.trixsearch.hasikit;

import android.app.Application;
import android.util.Log;
import androidx.hilt.work.HiltWorkerFactory;
import androidx.work.Configuration;
import com.trixsearch.hasikit.telegram.service.TelegramClientService;
import dagger.hilt.android.HiltAndroidApp;
import javax.inject.Inject;

@dagger.hilt.android.HiltAndroidApp()
@kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0003\b\u0007\u0018\u00002\u00020\u00012\u00020\u0002B\u0007\u00a2\u0006\u0004\b\u0003\u0010\u0004J\b\u0010\u0015\u001a\u00020\u0016H\u0016J\b\u0010\u0017\u001a\u00020\u0016H\u0002J\b\u0010\u0018\u001a\u00020\u0016H\u0002R\u001e\u0010\u0005\u001a\u00020\u00068\u0006@\u0006X\u0087.\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0007\u0010\b\"\u0004\b\t\u0010\nR\u001e\u0010\u000b\u001a\u00020\f8\u0006@\u0006X\u0087.\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\r\u0010\u000e\"\u0004\b\u000f\u0010\u0010R\u0014\u0010\u0011\u001a\u00020\u00128VX\u0096\u0004\u00a2\u0006\u0006\u001a\u0004\b\u0013\u0010\u0014\u00a8\u0006\u0019"}, d2 = {"Lcom/trixsearch/hasikit/HasikitApp;", "Landroid/app/Application;", "Landroidx/work/Configuration$Provider;", "<init>", "()V", "telegramClientService", "Lcom/trixsearch/hasikit/telegram/service/TelegramClientService;", "getTelegramClientService", "()Lcom/trixsearch/hasikit/telegram/service/TelegramClientService;", "setTelegramClientService", "(Lcom/trixsearch/hasikit/telegram/service/TelegramClientService;)V", "workerFactory", "Landroidx/hilt/work/HiltWorkerFactory;", "getWorkerFactory", "()Landroidx/hilt/work/HiltWorkerFactory;", "setWorkerFactory", "(Landroidx/hilt/work/HiltWorkerFactory;)V", "workManagerConfiguration", "Landroidx/work/Configuration;", "getWorkManagerConfiguration", "()Landroidx/work/Configuration;", "onCreate", "", "logBuildConfig", "logStorageInfo", "app_debug"})
public final class HasikitApp extends android.app.Application implements androidx.work.Configuration.Provider {
    @javax.inject.Inject()
    public com.trixsearch.hasikit.telegram.service.TelegramClientService telegramClientService;
    @javax.inject.Inject()
    public androidx.hilt.work.HiltWorkerFactory workerFactory;
    
    public HasikitApp() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.trixsearch.hasikit.telegram.service.TelegramClientService getTelegramClientService() {
        return null;
    }
    
    public final void setTelegramClientService(@org.jetbrains.annotations.NotNull()
    com.trixsearch.hasikit.telegram.service.TelegramClientService p0) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.hilt.work.HiltWorkerFactory getWorkerFactory() {
        return null;
    }
    
    public final void setWorkerFactory(@org.jetbrains.annotations.NotNull()
    androidx.hilt.work.HiltWorkerFactory p0) {
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public androidx.work.Configuration getWorkManagerConfiguration() {
        return null;
    }
    
    @java.lang.Override()
    public void onCreate() {
    }
    
    private final void logBuildConfig() {
    }
    
    private final void logStorageInfo() {
    }
}