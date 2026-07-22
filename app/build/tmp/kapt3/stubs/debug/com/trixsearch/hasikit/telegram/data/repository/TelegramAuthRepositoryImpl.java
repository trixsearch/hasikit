package com.trixsearch.hasikit.telegram.data.repository;

import android.content.Context;
import android.util.Log;
import com.trixsearch.hasikit.telegram.data.session.TelegramSessionManager;
import com.trixsearch.hasikit.telegram.domain.model.AuthResult;
import com.trixsearch.hasikit.telegram.domain.model.AuthState;
import com.trixsearch.hasikit.telegram.domain.model.TelegramUser;
import com.trixsearch.hasikit.telegram.domain.repository.TelegramAuthRepository;
import com.trixsearch.hasikit.telegram.service.TelegramClientService;
import dagger.hilt.android.qualifiers.ApplicationContext;
import kotlinx.coroutines.Dispatchers;
import kotlinx.coroutines.flow.StateFlow;
import java.io.File;
import javax.inject.Inject;
import javax.inject.Singleton;

@javax.inject.Singleton()
@kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000P\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\r\b\u0007\u0018\u00002\u00020\u0001B#\b\u0007\u0012\b\b\u0001\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\u0004\b\b\u0010\tJ\u0010\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u0016H\u0002J\u0016\u0010\u0017\u001a\u00020\u00182\u0006\u0010\u0019\u001a\u00020\u001aH\u0096@\u00a2\u0006\u0002\u0010\u001bJ&\u0010\u001c\u001a\u00020\u00182\u0006\u0010\u0019\u001a\u00020\u001a2\u0006\u0010\u001d\u001a\u00020\u001a2\u0006\u0010\u001e\u001a\u00020\u001aH\u0096@\u00a2\u0006\u0002\u0010\u001fJ\u0010\u0010 \u001a\u0004\u0018\u00010\u0016H\u0096@\u00a2\u0006\u0002\u0010!J\u000e\u0010\"\u001a\u00020\u0014H\u0096@\u00a2\u0006\u0002\u0010!J\u000e\u0010#\u001a\u00020\u0014H\u0096@\u00a2\u0006\u0002\u0010!J\u000e\u0010$\u001a\u00020\u0014H\u0096@\u00a2\u0006\u0002\u0010!J\u0016\u0010%\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u0016H\u0096@\u00a2\u0006\u0002\u0010&R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\n\u001a\b\u0012\u0004\u0012\u00020\f0\u000bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001a\u0010\r\u001a\b\u0012\u0004\u0012\u00020\f0\u000eX\u0096\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u000e\u0010\u0011\u001a\u00020\u0012X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\'"}, d2 = {"Lcom/trixsearch/hasikit/telegram/data/repository/TelegramAuthRepositoryImpl;", "Lcom/trixsearch/hasikit/telegram/domain/repository/TelegramAuthRepository;", "context", "Landroid/content/Context;", "clientService", "Lcom/trixsearch/hasikit/telegram/service/TelegramClientService;", "sessionManager", "Lcom/trixsearch/hasikit/telegram/data/session/TelegramSessionManager;", "<init>", "(Landroid/content/Context;Lcom/trixsearch/hasikit/telegram/service/TelegramClientService;Lcom/trixsearch/hasikit/telegram/data/session/TelegramSessionManager;)V", "_authState", "Lkotlinx/coroutines/flow/MutableStateFlow;", "Lcom/trixsearch/hasikit/telegram/domain/model/AuthState;", "authState", "Lkotlinx/coroutines/flow/StateFlow;", "getAuthState", "()Lkotlinx/coroutines/flow/StateFlow;", "repoScope", "Lkotlinx/coroutines/CoroutineScope;", "loadAndCacheProfilePhoto", "", "user", "Lcom/trixsearch/hasikit/telegram/domain/model/TelegramUser;", "sendCode", "Lcom/trixsearch/hasikit/telegram/domain/model/AuthResult;", "phoneNumber", "", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "verifyCode", "phoneCodeHash", "code", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getCurrentUser", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "restoreSession", "logout", "forceDeleteSession", "loginAsDemo", "(Lcom/trixsearch/hasikit/telegram/domain/model/TelegramUser;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_debug"})
public final class TelegramAuthRepositoryImpl implements com.trixsearch.hasikit.telegram.domain.repository.TelegramAuthRepository {
    @org.jetbrains.annotations.NotNull()
    private final android.content.Context context = null;
    @org.jetbrains.annotations.NotNull()
    private final com.trixsearch.hasikit.telegram.service.TelegramClientService clientService = null;
    @org.jetbrains.annotations.NotNull()
    private final com.trixsearch.hasikit.telegram.data.session.TelegramSessionManager sessionManager = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<com.trixsearch.hasikit.telegram.domain.model.AuthState> _authState = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<com.trixsearch.hasikit.telegram.domain.model.AuthState> authState = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.CoroutineScope repoScope = null;
    
    @javax.inject.Inject()
    public TelegramAuthRepositoryImpl(@dagger.hilt.android.qualifiers.ApplicationContext()
    @org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    com.trixsearch.hasikit.telegram.service.TelegramClientService clientService, @org.jetbrains.annotations.NotNull()
    com.trixsearch.hasikit.telegram.data.session.TelegramSessionManager sessionManager) {
        super();
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public kotlinx.coroutines.flow.StateFlow<com.trixsearch.hasikit.telegram.domain.model.AuthState> getAuthState() {
        return null;
    }
    
    private final void loadAndCacheProfilePhoto(com.trixsearch.hasikit.telegram.domain.model.TelegramUser user) {
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object sendCode(@org.jetbrains.annotations.NotNull()
    java.lang.String phoneNumber, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.trixsearch.hasikit.telegram.domain.model.AuthResult> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object verifyCode(@org.jetbrains.annotations.NotNull()
    java.lang.String phoneNumber, @org.jetbrains.annotations.NotNull()
    java.lang.String phoneCodeHash, @org.jetbrains.annotations.NotNull()
    java.lang.String code, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.trixsearch.hasikit.telegram.domain.model.AuthResult> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object getCurrentUser(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.trixsearch.hasikit.telegram.domain.model.TelegramUser> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object restoreSession(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object logout(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object forceDeleteSession(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object loginAsDemo(@org.jetbrains.annotations.NotNull()
    com.trixsearch.hasikit.telegram.domain.model.TelegramUser user, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
}