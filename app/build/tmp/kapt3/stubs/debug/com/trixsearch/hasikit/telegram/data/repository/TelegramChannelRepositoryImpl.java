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

@javax.inject.Singleton()
@kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000B\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0019\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\u0010\b\n\u0002\b\u0012\b\u0007\u0018\u00002\u00020\u0001B\u0011\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0004\b\u0004\u0010\u0005J\u001e\u0010\n\u001a\b\u0012\u0004\u0012\u00020\t0\u000b2\u0006\u0010\f\u001a\u00020\rH\u0096@\u00a2\u0006\u0004\b\u000e\u0010\u000fJ0\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\t0\u000b2\u0006\u0010\u0011\u001a\u00020\t2\b\u0010\u0012\u001a\u0004\u0018\u00010\b2\u0006\u0010\u0013\u001a\u00020\bH\u0082@\u00a2\u0006\u0004\b\u0014\u0010\u0015J\u001e\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\t0\u000b2\u0006\u0010\u0017\u001a\u00020\tH\u0082@\u00a2\u0006\u0004\b\u0018\u0010\u0019J\u001e\u0010\u001a\u001a\b\u0012\u0004\u0012\u00020\t0\u000b2\u0006\u0010\u0011\u001a\u00020\tH\u0082@\u00a2\u0006\u0004\b\u001b\u0010\u0019J\u0018\u0010\u001c\u001a\u0004\u0018\u00010\t2\u0006\u0010\u001d\u001a\u00020\tH\u0082@\u00a2\u0006\u0002\u0010\u0019J\u001e\u0010\u001e\u001a\b\u0012\u0004\u0012\u00020\t0\u000b2\u0006\u0010\u0012\u001a\u00020\bH\u0082@\u00a2\u0006\u0004\b\u001f\u0010 J\u001e\u0010!\u001a\b\u0012\u0004\u0012\u00020\t0\u000b2\u0006\u0010\"\u001a\u00020\bH\u0096@\u00a2\u0006\u0004\b#\u0010 J\u001e\u0010$\u001a\b\u0012\u0004\u0012\u00020\t0\u000b2\u0006\u0010\"\u001a\u00020\bH\u0082@\u00a2\u0006\u0004\b%\u0010 J@\u0010&\u001a\u001a\u0012\u0016\u0012\u0014\u0012\n\u0012\b\u0012\u0004\u0012\u00020)0(\u0012\u0004\u0012\u00020*0\'0\u000b2\u0006\u0010\u0011\u001a\u00020\t2\u0006\u0010+\u001a\u00020\t2\u0006\u0010,\u001a\u00020*H\u0096@\u00a2\u0006\u0004\b-\u0010.J4\u0010/\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020)0(0\u000b2\u0006\u0010\u0011\u001a\u00020\t2\u0006\u00100\u001a\u00020\b2\u0006\u0010,\u001a\u00020*H\u0096@\u00a2\u0006\u0004\b1\u00102J:\u00103\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020)0(0\u000b2\u0006\u0010\u0011\u001a\u00020\t2\f\u00104\u001a\b\u0012\u0004\u0012\u00020\b0(2\u0006\u0010,\u001a\u00020*H\u0096@\u00a2\u0006\u0004\b5\u00106J\u001e\u00107\u001a\b\u0012\u0004\u0012\u00020\b0\u000b2\u0006\u00108\u001a\u00020\tH\u0096@\u00a2\u0006\u0004\b9\u0010\u0019J\u0018\u0010:\u001a\u0004\u0018\u00010\b2\u0006\u0010;\u001a\u00020\tH\u0096@\u00a2\u0006\u0002\u0010\u0019R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001a\u0010\u0006\u001a\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\t0\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006<"}, d2 = {"Lcom/trixsearch/hasikit/telegram/data/repository/TelegramChannelRepositoryImpl;", "Lcom/trixsearch/hasikit/telegram/domain/repository/TelegramChannelRepository;", "clientService", "Lcom/trixsearch/hasikit/telegram/service/TelegramClientService;", "<init>", "(Lcom/trixsearch/hasikit/telegram/service/TelegramClientService;)V", "resolvedChatCache", "Ljava/util/concurrent/ConcurrentHashMap;", "", "", "resolveSource", "Lkotlin/Result;", "source", "Lcom/trixsearch/hasikit/telegram/config/TelegramSource;", "resolveSource-gIAlu-s", "(Lcom/trixsearch/hasikit/telegram/config/TelegramSource;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "resolvePrivateChatId", "chatId", "inviteLink", "displayName", "resolvePrivateChatId-BWLJW6A", "(JLjava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "createSupergroupChat", "supergroupId", "createSupergroupChat-gIAlu-s", "(JLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getChat", "getChat-gIAlu-s", "findInAccessibleChats", "targetChatId", "resolveByInviteLink", "resolveByInviteLink-gIAlu-s", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "resolveChannel", "username", "resolveChannel-gIAlu-s", "resolvePublicUsername", "resolvePublicUsername-gIAlu-s", "getChannelMedia", "Lkotlin/Pair;", "", "Lcom/trixsearch/hasikit/telegram/domain/model/TelegramMedia;", "", "offsetMessageId", "limit", "getChannelMedia-BWLJW6A", "(JJILkotlin/coroutines/Continuation;)Ljava/lang/Object;", "searchChannelMedia", "query", "searchChannelMedia-BWLJW6A", "(JLjava/lang/String;ILkotlin/coroutines/Continuation;)Ljava/lang/Object;", "searchChannelMediaMulti", "queryVariants", "searchChannelMediaMulti-BWLJW6A", "(JLjava/util/List;ILkotlin/coroutines/Continuation;)Ljava/lang/Object;", "resolveFileUrl", "fileId", "resolveFileUrl-gIAlu-s", "downloadThumbnail", "thumbnailFileId", "app_debug"})
public final class TelegramChannelRepositoryImpl implements com.trixsearch.hasikit.telegram.domain.repository.TelegramChannelRepository {
    @org.jetbrains.annotations.NotNull()
    private final com.trixsearch.hasikit.telegram.service.TelegramClientService clientService = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.concurrent.ConcurrentHashMap<java.lang.String, java.lang.Long> resolvedChatCache = null;
    
    @javax.inject.Inject()
    public TelegramChannelRepositoryImpl(@org.jetbrains.annotations.NotNull()
    com.trixsearch.hasikit.telegram.service.TelegramClientService clientService) {
        super();
    }
    
    /**
     * Fetches the user's accessible chats (GetChats) and checks if the target chatId is present.
     * This works for private channels the user has already joined.
     */
    private final java.lang.Object findInAccessibleChats(long targetChatId, kotlin.coroutines.Continuation<? super java.lang.Long> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object downloadThumbnail(long thumbnailFileId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.String> $completion) {
        return null;
    }
}