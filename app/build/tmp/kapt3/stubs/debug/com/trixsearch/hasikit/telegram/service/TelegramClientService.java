package com.trixsearch.hasikit.telegram.service;

import android.content.Context;
import android.util.Log;
import com.trixsearch.hasikit.BuildConfig;
import com.trixsearch.hasikit.telegram.config.TelegramSourceConfig;
import com.trixsearch.hasikit.telegram.domain.model.AuthResult;
import com.trixsearch.hasikit.telegram.domain.model.TelegramUser;
import dagger.hilt.android.qualifiers.ApplicationContext;
import org.drinkless.tdlib.Client;
import org.drinkless.tdlib.TdApi;
import java.io.File;
import javax.inject.Inject;
import javax.inject.Singleton;

@javax.inject.Singleton()
@kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000d\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\b\u0007\u0018\u00002\u00020\u0001B\u001b\b\u0007\u0012\b\b\u0001\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0004\b\u0006\u0010\u0007J\u0006\u0010\u0013\u001a\u00020\u000eJ\b\u0010\u0014\u001a\u00020\u000eH\u0002J\u0010\u0010\u0015\u001a\u00020\u000e2\u0006\u0010\u0016\u001a\u00020\u0017H\u0002J\b\u0010\u0018\u001a\u00020\u000eH\u0002J\u0016\u0010\u0019\u001a\u00020\u001a2\u0006\u0010\u001b\u001a\u00020\u0010H\u0086@\u00a2\u0006\u0002\u0010\u001cJ&\u0010\u001d\u001a\u00020\u001a2\u0006\u0010\u001b\u001a\u00020\u00102\u0006\u0010\u001e\u001a\u00020\u00102\u0006\u0010\u001f\u001a\u00020\u0010H\u0086@\u00a2\u0006\u0002\u0010 J\u000e\u0010!\u001a\u00020\u0010H\u0086@\u00a2\u0006\u0002\u0010\"J\u0018\u0010#\u001a\u0004\u0018\u00010$2\u0006\u0010%\u001a\u00020\u0010H\u0086@\u00a2\u0006\u0002\u0010\u001cJ\u000e\u0010&\u001a\u00020\u000eH\u0086@\u00a2\u0006\u0002\u0010\"J\b\u0010\'\u001a\u0004\u0018\u00010\tJ\u0006\u0010(\u001a\u00020)J\u001a\u0010*\u001a\u00020\u000e2\n\u0010+\u001a\u0006\u0012\u0002\b\u00030,2\u0006\u0010-\u001a\u00020.J\u0010\u0010/\u001a\u0004\u0018\u00010\u0010H\u0086@\u00a2\u0006\u0002\u0010\"J\"\u00100\u001a\u00020)2\u0012\u00101\u001a\u000e\u0012\u0004\u0012\u00020\u000b\u0012\u0004\u0012\u00020)0\rH\u0082@\u00a2\u0006\u0002\u00102J\u0010\u00103\u001a\u0004\u0018\u00010\u000bH\u0082@\u00a2\u0006\u0002\u0010\"R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\b\u001a\u0004\u0018\u00010\tX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\n\u001a\u0004\u0018\u00010\u000bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u001c\u0010\f\u001a\u0010\u0012\u0004\u0012\u00020\u000b\u0012\u0004\u0012\u00020\u000e\u0018\u00010\rX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u000f\u001a\u00020\u00108BX\u0082\u0004\u00a2\u0006\u0006\u001a\u0004\b\u0011\u0010\u0012\u00a8\u00064"}, d2 = {"Lcom/trixsearch/hasikit/telegram/service/TelegramClientService;", "", "context", "Landroid/content/Context;", "config", "Lcom/trixsearch/hasikit/telegram/config/TelegramSourceConfig;", "<init>", "(Landroid/content/Context;Lcom/trixsearch/hasikit/telegram/config/TelegramSourceConfig;)V", "client", "Lorg/drinkless/tdlib/Client;", "currentAuthState", "Lorg/drinkless/tdlib/TdApi$AuthorizationState;", "pendingAuthHandler", "Lkotlin/Function1;", "", "tdlibDbDir", "", "getTdlibDbDir", "()Ljava/lang/String;", "initClient", "createClient", "handleUpdate", "update", "Lorg/drinkless/tdlib/TdApi$Object;", "sendTdlibParameters", "sendCode", "Lcom/trixsearch/hasikit/telegram/domain/model/AuthResult;", "phoneNumber", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "verifyCode", "phoneCodeHash", "code", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "exportSession", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "importSession", "Lcom/trixsearch/hasikit/telegram/domain/model/TelegramUser;", "sessionString", "logout", "getClient", "isReady", "", "send", "query", "Lorg/drinkless/tdlib/TdApi$Function;", "handler", "Lorg/drinkless/tdlib/Client$ResultHandler;", "loadProfilePhoto", "waitForState", "predicate", "(Lkotlin/jvm/functions/Function1;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "waitForStableAuthState", "app_debug"})
public final class TelegramClientService {
    @org.jetbrains.annotations.NotNull()
    private final android.content.Context context = null;
    @org.jetbrains.annotations.NotNull()
    private final com.trixsearch.hasikit.telegram.config.TelegramSourceConfig config = null;
    @kotlin.jvm.Volatile()
    @org.jetbrains.annotations.Nullable()
    private volatile org.drinkless.tdlib.Client client;
    @kotlin.jvm.Volatile()
    @org.jetbrains.annotations.Nullable()
    private volatile org.drinkless.tdlib.TdApi.AuthorizationState currentAuthState;
    @kotlin.jvm.Volatile()
    @org.jetbrains.annotations.Nullable()
    private volatile kotlin.jvm.functions.Function1<? super org.drinkless.tdlib.TdApi.AuthorizationState, kotlin.Unit> pendingAuthHandler;
    
    @javax.inject.Inject()
    public TelegramClientService(@dagger.hilt.android.qualifiers.ApplicationContext()
    @org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    com.trixsearch.hasikit.telegram.config.TelegramSourceConfig config) {
        super();
    }
    
    private final java.lang.String getTdlibDbDir() {
        return null;
    }
    
    public final void initClient() {
    }
    
    private final void createClient() {
    }
    
    private final void handleUpdate(org.drinkless.tdlib.TdApi.Object update) {
    }
    
    private final void sendTdlibParameters() {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object sendCode(@org.jetbrains.annotations.NotNull()
    java.lang.String phoneNumber, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.trixsearch.hasikit.telegram.domain.model.AuthResult> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object verifyCode(@org.jetbrains.annotations.NotNull()
    java.lang.String phoneNumber, @org.jetbrains.annotations.NotNull()
    java.lang.String phoneCodeHash, @org.jetbrains.annotations.NotNull()
    java.lang.String code, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.trixsearch.hasikit.telegram.domain.model.AuthResult> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object exportSession(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.String> $completion) {
        return null;
    }
    
    /**
     * Called during session restore. TDLib may still be cycling through
     * WaitTdlibParameters when this is called — wait for a stable state first.
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object importSession(@org.jetbrains.annotations.NotNull()
    java.lang.String sessionString, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.trixsearch.hasikit.telegram.domain.model.TelegramUser> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object logout(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    /**
     * Exposes the live TDLib client for use by media repositories.
     * Only valid when [currentAuthState] is [TdApi.AuthorizationStateReady].
     */
    @org.jetbrains.annotations.Nullable()
    public final org.drinkless.tdlib.Client getClient() {
        return null;
    }
    
    public final boolean isReady() {
        return false;
    }
    
    /**
     * Send a TDLib request and deliver the result to [handler].
     * Logs an error if the client is null or not ready.
     */
    public final void send(@org.jetbrains.annotations.NotNull()
    org.drinkless.tdlib.TdApi.Function<?> query, @org.jetbrains.annotations.NotNull()
    org.drinkless.tdlib.Client.ResultHandler handler) {
    }
    
    /**
     * Downloads the current user's profile photo small thumbnail.
     * TDLib caches the file locally — subsequent calls return the same path without re-downloading.
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object loadProfilePhoto(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.String> $completion) {
        return null;
    }
    
    private final java.lang.Object waitForState(kotlin.jvm.functions.Function1<? super org.drinkless.tdlib.TdApi.AuthorizationState, java.lang.Boolean> predicate, kotlin.coroutines.Continuation<? super java.lang.Boolean> $completion) {
        return null;
    }
    
    /**
     * Waits until TDLib is past the initialisation phase.
     * Returns the first state that is not WaitTdlibParameters.
     */
    private final java.lang.Object waitForStableAuthState(kotlin.coroutines.Continuation<? super org.drinkless.tdlib.TdApi.AuthorizationState> $completion) {
        return null;
    }
}