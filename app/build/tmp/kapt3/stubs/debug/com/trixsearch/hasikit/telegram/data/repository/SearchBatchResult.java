package com.trixsearch.hasikit.telegram.data.repository;

import android.util.Log;
import com.trixsearch.hasikit.telegram.config.TelegramSource;
import com.trixsearch.hasikit.telegram.domain.model.TelegramMedia;
import com.trixsearch.hasikit.telegram.domain.repository.TelegramChannelRepository;
import com.trixsearch.hasikit.telegram.service.TelegramClientService;
import org.drinkless.tdlib.TdApi;
import java.util.concurrent.ConcurrentHashMap;
import javax.inject.Inject;
import javax.inject.Singleton;

@kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\n\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0082\b\u0018\u00002\u00020\u0001B\u001d\u0012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u00a2\u0006\u0004\b\u0007\u0010\bJ\u000f\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003H\u00c6\u0003J\t\u0010\u000e\u001a\u00020\u0006H\u00c6\u0003J#\u0010\u000f\u001a\u00020\u00002\u000e\b\u0002\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u0006H\u00c6\u0001J\u0013\u0010\u0010\u001a\u00020\u00112\b\u0010\u0012\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0013\u001a\u00020\u0014H\u00d6\u0001J\t\u0010\u0015\u001a\u00020\u0016H\u00d6\u0001R\u0017\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u0011\u0010\u0005\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\f\u00a8\u0006\u0017"}, d2 = {"Lcom/trixsearch/hasikit/telegram/data/repository/SearchBatchResult;", "", "media", "", "Lcom/trixsearch/hasikit/telegram/domain/model/TelegramMedia;", "nextFromMessageId", "", "<init>", "(Ljava/util/List;J)V", "getMedia", "()Ljava/util/List;", "getNextFromMessageId", "()J", "component1", "component2", "copy", "equals", "", "other", "hashCode", "", "toString", "", "app_debug"})
final class SearchBatchResult {
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<com.trixsearch.hasikit.telegram.domain.model.TelegramMedia> media = null;
    private final long nextFromMessageId = 0L;
    
    public SearchBatchResult(@org.jetbrains.annotations.NotNull()
    java.util.List<com.trixsearch.hasikit.telegram.domain.model.TelegramMedia> media, long nextFromMessageId) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.trixsearch.hasikit.telegram.domain.model.TelegramMedia> getMedia() {
        return null;
    }
    
    public final long getNextFromMessageId() {
        return 0L;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.trixsearch.hasikit.telegram.domain.model.TelegramMedia> component1() {
        return null;
    }
    
    public final long component2() {
        return 0L;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.trixsearch.hasikit.telegram.data.repository.SearchBatchResult copy(@org.jetbrains.annotations.NotNull()
    java.util.List<com.trixsearch.hasikit.telegram.domain.model.TelegramMedia> media, long nextFromMessageId) {
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