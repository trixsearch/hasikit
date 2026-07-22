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

@kotlin.Metadata(mv = {2, 1, 0}, k = 2, xi = 48, d1 = {"\u0000(\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\"\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0006\u001a\u0016\u0010\u0005\u001a\u0004\u0018\u00010\u0006*\u00020\u00072\u0006\u0010\b\u001a\u00020\tH\u0002\u001a\u0010\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\u0001H\u0002\u001a\u0010\u0010\r\u001a\u00020\u000b2\u0006\u0010\u000e\u001a\u00020\u0001H\u0002\u001a\u0010\u0010\u000f\u001a\u00020\u00012\u0006\u0010\u0010\u001a\u00020\u0001H\u0002\"\u000e\u0010\u0000\u001a\u00020\u0001X\u0082T\u00a2\u0006\u0002\n\u0000\"\u0014\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00010\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\"\u0014\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00010\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0011"}, d2 = {"TAG", "", "SUPPORTED_MIME", "", "SUPPORTED_EXT", "toTelegramMedia", "Lcom/trixsearch/hasikit/telegram/domain/model/TelegramMedia;", "Lorg/drinkless/tdlib/TdApi$Message;", "chatId", "", "isSupportedMime", "", "mime", "isSupportedExt", "fileName", "cleanTitle", "raw", "app_debug"})
public final class TelegramChannelRepositoryImplKt {
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String TAG = "TelegramChannelRepo";
    @org.jetbrains.annotations.NotNull()
    private static final java.util.Set<java.lang.String> SUPPORTED_MIME = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.util.Set<java.lang.String> SUPPORTED_EXT = null;
    
    private static final com.trixsearch.hasikit.telegram.domain.model.TelegramMedia toTelegramMedia(org.drinkless.tdlib.TdApi.Message $this$toTelegramMedia, long chatId) {
        return null;
    }
    
    private static final boolean isSupportedMime(java.lang.String mime) {
        return false;
    }
    
    private static final boolean isSupportedExt(java.lang.String fileName) {
        return false;
    }
    
    private static final java.lang.String cleanTitle(java.lang.String raw) {
        return null;
    }
}