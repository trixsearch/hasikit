package com.trixsearch.hasikit.ui.screens.home;

import com.trixsearch.hasikit.data.local.entities.FavoriteEntity;
import com.trixsearch.hasikit.data.local.entities.WatchLaterEntity;
import android.util.Log;
import androidx.lifecycle.ViewModel;
import com.trixsearch.hasikit.domain.model.DownloadState;
import com.trixsearch.hasikit.domain.model.Video;
import com.trixsearch.hasikit.domain.model.WatchProgress;
import com.trixsearch.hasikit.domain.repository.VideoRepository;
import com.trixsearch.hasikit.download.HasikitDownloadManager;
import com.trixsearch.hasikit.telegram.config.TelegramSource;
import com.trixsearch.hasikit.telegram.config.TelegramSourceConfig;
import com.trixsearch.hasikit.telegram.domain.model.AuthState;
import com.trixsearch.hasikit.telegram.domain.model.TelegramMedia;
import com.trixsearch.hasikit.telegram.domain.repository.TelegramAuthRepository;
import com.trixsearch.hasikit.telegram.domain.repository.TelegramChannelRepository;
import com.trixsearch.hasikit.search.SearchEngine;
import dagger.hilt.android.lifecycle.HiltViewModel;
import kotlinx.coroutines.flow.*;
import kotlinx.coroutines.Dispatchers;
import javax.inject.Inject;

@kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u00006\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0014\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B5\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\f\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u0007\u0012\u0006\u0010\t\u001a\u00020\u0005\u0012\u0006\u0010\n\u001a\u00020\u000b\u00a2\u0006\u0004\b\f\u0010\rJ\t\u0010\u0017\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0018\u001a\u00020\u0005H\u00c6\u0003J\u000f\u0010\u0019\u001a\b\u0012\u0004\u0012\u00020\b0\u0007H\u00c6\u0003J\t\u0010\u001a\u001a\u00020\u0005H\u00c6\u0003J\t\u0010\u001b\u001a\u00020\u000bH\u00c6\u0003JA\u0010\u001c\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\u000e\b\u0002\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u00072\b\b\u0002\u0010\t\u001a\u00020\u00052\b\b\u0002\u0010\n\u001a\u00020\u000bH\u00c6\u0001J\u0013\u0010\u001d\u001a\u00020\u000b2\b\u0010\u001e\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u001f\u001a\u00020 H\u00d6\u0001J\t\u0010!\u001a\u00020\"H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u0017\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0013R\u0011\u0010\t\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0011R\u0011\u0010\n\u001a\u00020\u000b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0016\u00a8\u0006#"}, d2 = {"Lcom/trixsearch/hasikit/ui/screens/home/SourcePage;", "", "source", "Lcom/trixsearch/hasikit/telegram/config/TelegramSource;", "chatId", "", "media", "", "Lcom/trixsearch/hasikit/telegram/domain/model/TelegramMedia;", "lastMessageId", "hasMore", "", "<init>", "(Lcom/trixsearch/hasikit/telegram/config/TelegramSource;JLjava/util/List;JZ)V", "getSource", "()Lcom/trixsearch/hasikit/telegram/config/TelegramSource;", "getChatId", "()J", "getMedia", "()Ljava/util/List;", "getLastMessageId", "getHasMore", "()Z", "component1", "component2", "component3", "component4", "component5", "copy", "equals", "other", "hashCode", "", "toString", "", "app_debug"})
public final class SourcePage {
    @org.jetbrains.annotations.NotNull()
    private final com.trixsearch.hasikit.telegram.config.TelegramSource source = null;
    private final long chatId = 0L;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<com.trixsearch.hasikit.telegram.domain.model.TelegramMedia> media = null;
    private final long lastMessageId = 0L;
    private final boolean hasMore = false;
    
    public SourcePage(@org.jetbrains.annotations.NotNull()
    com.trixsearch.hasikit.telegram.config.TelegramSource source, long chatId, @org.jetbrains.annotations.NotNull()
    java.util.List<com.trixsearch.hasikit.telegram.domain.model.TelegramMedia> media, long lastMessageId, boolean hasMore) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.trixsearch.hasikit.telegram.config.TelegramSource getSource() {
        return null;
    }
    
    public final long getChatId() {
        return 0L;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.trixsearch.hasikit.telegram.domain.model.TelegramMedia> getMedia() {
        return null;
    }
    
    public final long getLastMessageId() {
        return 0L;
    }
    
    public final boolean getHasMore() {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.trixsearch.hasikit.telegram.config.TelegramSource component1() {
        return null;
    }
    
    public final long component2() {
        return 0L;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.trixsearch.hasikit.telegram.domain.model.TelegramMedia> component3() {
        return null;
    }
    
    public final long component4() {
        return 0L;
    }
    
    public final boolean component5() {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.trixsearch.hasikit.ui.screens.home.SourcePage copy(@org.jetbrains.annotations.NotNull()
    com.trixsearch.hasikit.telegram.config.TelegramSource source, long chatId, @org.jetbrains.annotations.NotNull()
    java.util.List<com.trixsearch.hasikit.telegram.domain.model.TelegramMedia> media, long lastMessageId, boolean hasMore) {
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