package com.trixsearch.hasikit.domain.repository;

import com.trixsearch.hasikit.domain.model.Video;
import kotlinx.coroutines.flow.Flow;

@kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0005\bf\u0018\u00002\u00020\u0001J\u001c\u0010\u0002\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\u00040\u00032\u0006\u0010\u0006\u001a\u00020\u0007H&J\u001c\u0010\b\u001a\b\u0012\u0004\u0012\u00020\t0\u00032\u0006\u0010\n\u001a\u00020\u0005H\u00a6@\u00a2\u0006\u0002\u0010\u000bJ\u0016\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000fH\u00a6@\u00a2\u0006\u0002\u0010\u0010J\u0016\u0010\u0011\u001a\u00020\r2\u0006\u0010\u0012\u001a\u00020\u000fH\u00a6@\u00a2\u0006\u0002\u0010\u0010J\u000e\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\r0\u0003H&\u00a8\u0006\u0014"}, d2 = {"Lcom/trixsearch/hasikit/domain/repository/TelegramRepository;", "", "getVideosFromChannel", "Lkotlinx/coroutines/flow/Flow;", "", "Lcom/trixsearch/hasikit/domain/model/Video;", "channelId", "", "downloadVideo", "", "video", "(Lcom/trixsearch/hasikit/domain/model/Video;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "login", "", "phoneNumber", "", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "verifyOtp", "code", "isUserAuthenticated", "app_debug"})
public abstract interface TelegramRepository {
    
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.trixsearch.hasikit.domain.model.Video>> getVideosFromChannel(long channelId);
    
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object downloadVideo(@org.jetbrains.annotations.NotNull()
    com.trixsearch.hasikit.domain.model.Video video, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlinx.coroutines.flow.Flow<java.lang.Float>> $completion);
    
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object login(@org.jetbrains.annotations.NotNull()
    java.lang.String phoneNumber, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Boolean> $completion);
    
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object verifyOtp(@org.jetbrains.annotations.NotNull()
    java.lang.String code, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Boolean> $completion);
    
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.lang.Boolean> isUserAuthenticated();
}