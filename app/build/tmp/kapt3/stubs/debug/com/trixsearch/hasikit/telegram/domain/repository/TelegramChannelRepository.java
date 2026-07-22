package com.trixsearch.hasikit.telegram.domain.repository;

import com.trixsearch.hasikit.telegram.config.TelegramSource;
import com.trixsearch.hasikit.telegram.domain.model.TelegramMedia;

@kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u00008\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\u0010\b\n\u0002\b\u0014\bf\u0018\u00002\u00020\u0001J\u001e\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\u0006\u0010\u0005\u001a\u00020\u0006H\u00a6@\u00a2\u0006\u0004\b\u0007\u0010\bJ\u001e\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\u0006\u0010\n\u001a\u00020\u000bH\u00a6@\u00a2\u0006\u0004\b\f\u0010\rJD\u0010\u000e\u001a\u001a\u0012\u0016\u0012\u0014\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00110\u0010\u0012\u0004\u0012\u00020\u00120\u000f0\u00032\u0006\u0010\u0013\u001a\u00020\u00042\b\b\u0002\u0010\u0014\u001a\u00020\u00042\b\b\u0002\u0010\u0015\u001a\u00020\u0012H\u00a6@\u00a2\u0006\u0004\b\u0016\u0010\u0017J6\u0010\u0018\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00110\u00100\u00032\u0006\u0010\u0013\u001a\u00020\u00042\u0006\u0010\u0019\u001a\u00020\u000b2\b\b\u0002\u0010\u0015\u001a\u00020\u0012H\u00a6@\u00a2\u0006\u0004\b\u001a\u0010\u001bJ<\u0010\u001c\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00110\u00100\u00032\u0006\u0010\u0013\u001a\u00020\u00042\f\u0010\u001d\u001a\b\u0012\u0004\u0012\u00020\u000b0\u00102\b\b\u0002\u0010\u0015\u001a\u00020\u0012H\u00a6@\u00a2\u0006\u0004\b\u001e\u0010\u001fJ\u001e\u0010 \u001a\b\u0012\u0004\u0012\u00020\u000b0\u00032\u0006\u0010!\u001a\u00020\u0004H\u00a6@\u00a2\u0006\u0004\b\"\u0010#J\u0018\u0010$\u001a\u0004\u0018\u00010\u000b2\u0006\u0010%\u001a\u00020\u0004H\u00a6@\u00a2\u0006\u0002\u0010#\u00a8\u0006&"}, d2 = {"Lcom/trixsearch/hasikit/telegram/domain/repository/TelegramChannelRepository;", "", "resolveSource", "Lkotlin/Result;", "", "source", "Lcom/trixsearch/hasikit/telegram/config/TelegramSource;", "resolveSource-gIAlu-s", "(Lcom/trixsearch/hasikit/telegram/config/TelegramSource;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "resolveChannel", "username", "", "resolveChannel-gIAlu-s", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getChannelMedia", "Lkotlin/Pair;", "", "Lcom/trixsearch/hasikit/telegram/domain/model/TelegramMedia;", "", "chatId", "offsetMessageId", "limit", "getChannelMedia-BWLJW6A", "(JJILkotlin/coroutines/Continuation;)Ljava/lang/Object;", "searchChannelMedia", "query", "searchChannelMedia-BWLJW6A", "(JLjava/lang/String;ILkotlin/coroutines/Continuation;)Ljava/lang/Object;", "searchChannelMediaMulti", "queryVariants", "searchChannelMediaMulti-BWLJW6A", "(JLjava/util/List;ILkotlin/coroutines/Continuation;)Ljava/lang/Object;", "resolveFileUrl", "fileId", "resolveFileUrl-gIAlu-s", "(JLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "downloadThumbnail", "thumbnailFileId", "app_debug"})
public abstract interface TelegramChannelRepository {
    
    /**
     * Download a thumbnail file and return its local path.
     * Returns null if no thumbnail or download fails.
     */
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object downloadThumbnail(long thumbnailFileId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.String> $completion);
    
    @kotlin.Metadata(mv = {2, 1, 0}, k = 3, xi = 48)
    public static final class DefaultImpls {
    }
}