package com.trixsearch.hasikit.telegram.service;

import android.content.Context;
import android.util.Log;
import com.trixsearch.hasikit.BuildConfig;
import com.trixsearch.hasikit.telegram.config.TelegramSourceConfig;
import com.trixsearch.hasikit.telegram.domain.model.AuthResult;
import com.trixsearch.hasikit.telegram.domain.model.TelegramUser;
import dagger.hilt.android.qualifiers.ApplicationContext;
import org.drinkless.tdlib.Client;
import org.drinkless.tdlib.TdApi;
import java.io.File;
import javax.inject.Inject;
import javax.inject.Singleton;

@kotlin.Metadata(mv = {2, 1, 0}, k = 2, xi = 48, d1 = {"\u0000\u0012\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\u001a\f\u0010\u0002\u001a\u00020\u0003*\u00020\u0004H\u0002\"\u000e\u0010\u0000\u001a\u00020\u0001X\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0005"}, d2 = {"TAG", "", "toTelegramUser", "Lcom/trixsearch/hasikit/telegram/domain/model/TelegramUser;", "Lorg/drinkless/tdlib/TdApi$User;", "app_debug"})
public final class TelegramClientServiceKt {
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String TAG = "TelegramClientService";
    
    private static final com.trixsearch.hasikit.telegram.domain.model.TelegramUser toTelegramUser(org.drinkless.tdlib.TdApi.User $this$toTelegramUser) {
        return null;
    }
}