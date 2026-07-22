package com.trixsearch.hasikit.telegram.config;

import android.content.Context;
import android.util.Log;
import com.trixsearch.hasikit.BuildConfig;
import dagger.hilt.android.qualifiers.ApplicationContext;
import kotlinx.coroutines.flow.Flow;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Represents a single Telegram content source.
 * identifier can be:
 *  - @username  (public channel/group)
 *  - -1001234567890  (numeric chat ID)
 *  - https://t.me/+xxxxxxxx  (private invite link)
 * inviteLink is optional — used as fallback when chatId resolution fails for private channels.
 */
@kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0014\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001B-\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0005\u001a\u00020\u0006\u0012\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\u0004\b\b\u0010\tJ\t\u0010\u0013\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0014\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0015\u001a\u00020\u0006H\u00c6\u0003J\u000b\u0010\u0016\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J3\u0010\u0017\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00062\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\u0003H\u00c6\u0001J\u0013\u0010\u0018\u001a\u00020\u00062\b\u0010\u0019\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u001a\u001a\u00020\u001bH\u00d6\u0001J\t\u0010\u001c\u001a\u00020\u0003H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\u000bR\u0011\u0010\u0005\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\rR\u0013\u0010\u0007\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000bR\u0011\u0010\u000f\u001a\u00020\u00038F\u00a2\u0006\u0006\u001a\u0004\b\u0010\u0010\u000bR\u0011\u0010\u0011\u001a\u00020\u00068F\u00a2\u0006\u0006\u001a\u0004\b\u0011\u0010\rR\u0011\u0010\u0012\u001a\u00020\u00068F\u00a2\u0006\u0006\u001a\u0004\b\u0012\u0010\r\u00a8\u0006\u001d"}, d2 = {"Lcom/trixsearch/hasikit/telegram/config/TelegramSource;", "", "identifier", "", "displayName", "isOfficial", "", "inviteLink", "<init>", "(Ljava/lang/String;Ljava/lang/String;ZLjava/lang/String;)V", "getIdentifier", "()Ljava/lang/String;", "getDisplayName", "()Z", "getInviteLink", "username", "getUsername", "isChatId", "isInviteLink", "component1", "component2", "component3", "component4", "copy", "equals", "other", "hashCode", "", "toString", "app_debug"})
public final class TelegramSource {
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String identifier = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String displayName = null;
    private final boolean isOfficial = false;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String inviteLink = null;
    
    public TelegramSource(@org.jetbrains.annotations.NotNull()
    java.lang.String identifier, @org.jetbrains.annotations.NotNull()
    java.lang.String displayName, boolean isOfficial, @org.jetbrains.annotations.Nullable()
    java.lang.String inviteLink) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getIdentifier() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getDisplayName() {
        return null;
    }
    
    public final boolean isOfficial() {
        return false;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getInviteLink() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getUsername() {
        return null;
    }
    
    public final boolean isChatId() {
        return false;
    }
    
    public final boolean isInviteLink() {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component1() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component2() {
        return null;
    }
    
    public final boolean component3() {
        return false;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component4() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.trixsearch.hasikit.telegram.config.TelegramSource copy(@org.jetbrains.annotations.NotNull()
    java.lang.String identifier, @org.jetbrains.annotations.NotNull()
    java.lang.String displayName, boolean isOfficial, @org.jetbrains.annotations.Nullable()
    java.lang.String inviteLink) {
        return null;
    }
    
    @java.lang.Override()
    public boolean equals(@org.jetbrains.annotations.Nullable()
    java.lang.Object other) {
        return false;
    }
    
    @java.lang.Override()
    public int hashCode() {
        return 0;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public java.lang.String toString() {
        return null;
    }
}