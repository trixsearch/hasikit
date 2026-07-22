package com.trixsearch.hasikit.telegram.data.repository;

import android.content.Context;
import android.util.Log;
import com.trixsearch.hasikit.telegram.data.session.TelegramSessionManager;
import com.trixsearch.hasikit.telegram.domain.model.AuthResult;
import com.trixsearch.hasikit.telegram.domain.model.AuthState;
import com.trixsearch.hasikit.telegram.domain.model.TelegramUser;
import com.trixsearch.hasikit.telegram.domain.repository.TelegramAuthRepository;
import com.trixsearch.hasikit.telegram.service.TelegramClientService;
import dagger.hilt.android.qualifiers.ApplicationContext;
import kotlinx.coroutines.Dispatchers;
import kotlinx.coroutines.flow.StateFlow;
import java.io.File;
import javax.inject.Inject;
import javax.inject.Singleton;

@kotlin.Metadata(mv = {2, 1, 0}, k = 2, xi = 48, d1 = {"\u0000\b\n\u0000\n\u0002\u0010\u000e\n\u0000\"\u000e\u0010\u0000\u001a\u00020\u0001X\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0002"}, d2 = {"TAG", "", "app_debug"})
public final class TelegramAuthRepositoryImplKt {
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String TAG = "TelegramAuthRepo";
}