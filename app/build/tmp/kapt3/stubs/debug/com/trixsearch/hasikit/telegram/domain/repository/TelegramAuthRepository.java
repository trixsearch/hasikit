package com.trixsearch.hasikit.telegram.domain.repository;

import com.trixsearch.hasikit.telegram.domain.model.AuthResult;
import com.trixsearch.hasikit.telegram.domain.model.AuthState;
import com.trixsearch.hasikit.telegram.domain.model.TelegramUser;
import kotlinx.coroutines.flow.StateFlow;

@kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0006\bf\u0018\u00002\u00020\u0001J\u0016\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\nH\u00a6@\u00a2\u0006\u0002\u0010\u000bJ&\u0010\f\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\r\u001a\u00020\n2\u0006\u0010\u000e\u001a\u00020\nH\u00a6@\u00a2\u0006\u0002\u0010\u000fJ\u0010\u0010\u0010\u001a\u0004\u0018\u00010\u0011H\u00a6@\u00a2\u0006\u0002\u0010\u0012J\u000e\u0010\u0013\u001a\u00020\u0014H\u00a6@\u00a2\u0006\u0002\u0010\u0012J\u000e\u0010\u0015\u001a\u00020\u0014H\u00a6@\u00a2\u0006\u0002\u0010\u0012J\u000e\u0010\u0016\u001a\u00020\u0014H\u00a6@\u00a2\u0006\u0002\u0010\u0012J\u0016\u0010\u0017\u001a\u00020\u00142\u0006\u0010\u0018\u001a\u00020\u0011H\u00a6@\u00a2\u0006\u0002\u0010\u0019R\u0018\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003X\u00a6\u0004\u00a2\u0006\u0006\u001a\u0004\b\u0005\u0010\u0006\u00a8\u0006\u001a"}, d2 = {"Lcom/trixsearch/hasikit/telegram/domain/repository/TelegramAuthRepository;", "", "authState", "Lkotlinx/coroutines/flow/StateFlow;", "Lcom/trixsearch/hasikit/telegram/domain/model/AuthState;", "getAuthState", "()Lkotlinx/coroutines/flow/StateFlow;", "sendCode", "Lcom/trixsearch/hasikit/telegram/domain/model/AuthResult;", "phoneNumber", "", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "verifyCode", "phoneCodeHash", "code", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getCurrentUser", "Lcom/trixsearch/hasikit/telegram/domain/model/TelegramUser;", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "restoreSession", "", "logout", "forceDeleteSession", "loginAsDemo", "user", "(Lcom/trixsearch/hasikit/telegram/domain/model/TelegramUser;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_debug"})
public abstract interface TelegramAuthRepository {
    
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.StateFlow<com.trixsearch.hasikit.telegram.domain.model.AuthState> getAuthState();
    
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object sendCode(@org.jetbrains.annotations.NotNull()
    java.lang.String phoneNumber, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.trixsearch.hasikit.telegram.domain.model.AuthResult> $completion);
    
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object verifyCode(@org.jetbrains.annotations.NotNull()
    java.lang.String phoneNumber, @org.jetbrains.annotations.NotNull()
    java.lang.String phoneCodeHash, @org.jetbrains.annotations.NotNull()
    java.lang.String code, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.trixsearch.hasikit.telegram.domain.model.AuthResult> $completion);
    
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getCurrentUser(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.trixsearch.hasikit.telegram.domain.model.TelegramUser> $completion);
    
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object restoreSession(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object logout(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object forceDeleteSession(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object loginAsDemo(@org.jetbrains.annotations.NotNull()
    com.trixsearch.hasikit.telegram.domain.model.TelegramUser user, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
}