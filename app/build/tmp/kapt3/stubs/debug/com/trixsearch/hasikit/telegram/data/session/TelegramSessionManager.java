package com.trixsearch.hasikit.telegram.data.session;

import android.content.Context;
import android.util.Log;
import com.trixsearch.hasikit.telegram.domain.model.TelegramUser;
import dagger.hilt.android.qualifiers.ApplicationContext;
import kotlinx.coroutines.flow.Flow;
import javax.inject.Inject;
import javax.inject.Singleton;

@javax.inject.Singleton()
@kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u00006\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0006\b\u0007\u0018\u00002\u00020\u0001B\u0013\b\u0007\u0012\b\b\u0001\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0004\b\u0004\u0010\u0005J\u001e\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u000b2\u0006\u0010\r\u001a\u00020\u000eH\u0086@\u00a2\u0006\u0002\u0010\u0013J\u000e\u0010\u0014\u001a\u00020\u0011H\u0086@\u00a2\u0006\u0002\u0010\u0015J\u000e\u0010\u0016\u001a\u00020\bH\u0086@\u00a2\u0006\u0002\u0010\u0015J\u0010\u0010\u000f\u001a\u0004\u0018\u00010\u000eH\u0086@\u00a2\u0006\u0002\u0010\u0015R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\tR\u0019\u0010\n\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u000b0\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\tR\u0019\u0010\r\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u000e0\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\t\u00a8\u0006\u0017"}, d2 = {"Lcom/trixsearch/hasikit/telegram/data/session/TelegramSessionManager;", "", "context", "Landroid/content/Context;", "<init>", "(Landroid/content/Context;)V", "isAuthenticated", "Lkotlinx/coroutines/flow/Flow;", "", "()Lkotlinx/coroutines/flow/Flow;", "savedUser", "Lcom/trixsearch/hasikit/telegram/domain/model/TelegramUser;", "getSavedUser", "sessionString", "", "getSessionString", "saveSession", "", "user", "(Lcom/trixsearch/hasikit/telegram/domain/model/TelegramUser;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "clearSession", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "hasValidSession", "app_debug"})
public final class TelegramSessionManager {
    @org.jetbrains.annotations.NotNull()
    private final android.content.Context context = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.Flow<java.lang.Boolean> isAuthenticated = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.Flow<com.trixsearch.hasikit.telegram.domain.model.TelegramUser> savedUser = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.Flow<java.lang.String> sessionString = null;
    
    @javax.inject.Inject()
    public TelegramSessionManager(@dagger.hilt.android.qualifiers.ApplicationContext()
    @org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.lang.Boolean> isAuthenticated() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<com.trixsearch.hasikit.telegram.domain.model.TelegramUser> getSavedUser() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.lang.String> getSessionString() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object saveSession(@org.jetbrains.annotations.NotNull()
    com.trixsearch.hasikit.telegram.domain.model.TelegramUser user, @org.jetbrains.annotations.NotNull()
    java.lang.String sessionString, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object clearSession(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object hasValidSession(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Boolean> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getSessionString(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.String> $completion) {
        return null;
    }
}