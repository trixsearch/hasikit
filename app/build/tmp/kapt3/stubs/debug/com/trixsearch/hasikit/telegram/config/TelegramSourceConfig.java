package com.trixsearch.hasikit.telegram.config;

import android.content.Context;
import android.util.Log;
import com.trixsearch.hasikit.BuildConfig;
import dagger.hilt.android.qualifiers.ApplicationContext;
import kotlinx.coroutines.flow.Flow;
import javax.inject.Inject;
import javax.inject.Singleton;

@javax.inject.Singleton()
@kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000>\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\n\b\u0007\u0018\u00002\u00020\u0001B\u0013\b\u0007\u0012\b\b\u0001\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0004\b\u0004\u0010\u0005J\u0016\u0010\u0017\u001a\u00020\u00182\u0006\u0010\u0019\u001a\u00020\u0010H\u0086@\u00a2\u0006\u0002\u0010\u001aJ\u0016\u0010\u001b\u001a\u00020\u00182\u0006\u0010\u001c\u001a\u00020\u000bH\u0086@\u00a2\u0006\u0002\u0010\u001dR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0006\u001a\u00020\u0007X\u0086D\u00a2\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0014\u0010\n\u001a\u00020\u000bX\u0086D\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0017\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00100\u000f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012R\u001d\u0010\u0013\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00100\u000f0\u0014\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0016R\u0014\u0010\u001e\u001a\u00020\u000bX\u0086D\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001f\u0010\rR\u0011\u0010 \u001a\u00020\u000b8F\u00a2\u0006\u0006\u001a\u0004\b!\u0010\r\u00a8\u0006\""}, d2 = {"Lcom/trixsearch/hasikit/telegram/config/TelegramSourceConfig;", "", "context", "Landroid/content/Context;", "<init>", "(Landroid/content/Context;)V", "apiId", "", "getApiId", "()I", "apiHash", "", "getApiHash", "()Ljava/lang/String;", "officialSources", "", "Lcom/trixsearch/hasikit/telegram/config/TelegramSource;", "getOfficialSources", "()Ljava/util/List;", "userSourcesFlow", "Lkotlinx/coroutines/flow/Flow;", "getUserSourcesFlow", "()Lkotlinx/coroutines/flow/Flow;", "addUserSource", "", "source", "(Lcom/trixsearch/hasikit/telegram/config/TelegramSource;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "removeUserSource", "identifier", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "sourceChannel", "getSourceChannel", "sourceChannelUsername", "getSourceChannelUsername", "app_debug"})
public final class TelegramSourceConfig {
    @org.jetbrains.annotations.NotNull()
    private final android.content.Context context = null;
    private final int apiId = com.trixsearch.hasikit.BuildConfig.TELEGRAM_API_ID;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String apiHash = "8e366250862d2d3b8408493920f17dc6";
    
    /**
     * Official sources bundled in the app.
     * Users cannot delete these.
     * Add more channels/groups here as the platform grows.
     * Identifier
     */
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<com.trixsearch.hasikit.telegram.config.TelegramSource> officialSources = null;
    
    /**
     * User-added sources stored in DataStore (serialised as comma-separated "identifier|name" pairs)
     */
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.Flow<java.util.List<com.trixsearch.hasikit.telegram.config.TelegramSource>> userSourcesFlow = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String sourceChannel = "@testhasikit";
    
    @javax.inject.Inject()
    public TelegramSourceConfig(@dagger.hilt.android.qualifiers.ApplicationContext()
    @org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        super();
    }
    
    public final int getApiId() {
        return 0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getApiHash() {
        return null;
    }
    
    /**
     * Official sources bundled in the app.
     * Users cannot delete these.
     * Add more channels/groups here as the platform grows.
     * Identifier
     */
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.trixsearch.hasikit.telegram.config.TelegramSource> getOfficialSources() {
        return null;
    }
    
    /**
     * User-added sources stored in DataStore (serialised as comma-separated "identifier|name" pairs)
     */
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.util.List<com.trixsearch.hasikit.telegram.config.TelegramSource>> getUserSourcesFlow() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object addUserSource(@org.jetbrains.annotations.NotNull()
    com.trixsearch.hasikit.telegram.config.TelegramSource source, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object removeUserSource(@org.jetbrains.annotations.NotNull()
    java.lang.String identifier, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getSourceChannel() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getSourceChannelUsername() {
        return null;
    }
}