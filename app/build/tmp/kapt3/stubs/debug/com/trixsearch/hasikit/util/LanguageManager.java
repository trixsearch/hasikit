package com.trixsearch.hasikit.util;

import android.content.Context;
import android.content.res.Configuration;
import dagger.hilt.android.qualifiers.ApplicationContext;
import kotlinx.coroutines.flow.Flow;
import java.util.Locale;
import javax.inject.Inject;
import javax.inject.Singleton;

@javax.inject.Singleton()
@kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0005\b\u0007\u0018\u00002\u00020\u0001B\u0013\b\u0007\u0012\b\b\u0001\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0004\b\u0004\u0010\u0005J\u0016\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\bH\u0086@\u00a2\u0006\u0002\u0010\u000eJ\u0016\u0010\u000f\u001a\u00020\u00032\u0006\u0010\u0010\u001a\u00020\u00032\u0006\u0010\r\u001a\u00020\bR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\n\u00a8\u0006\u0011"}, d2 = {"Lcom/trixsearch/hasikit/util/LanguageManager;", "", "context", "Landroid/content/Context;", "<init>", "(Landroid/content/Context;)V", "selectedLanguage", "Lkotlinx/coroutines/flow/Flow;", "Lcom/trixsearch/hasikit/util/AppLanguage;", "getSelectedLanguage", "()Lkotlinx/coroutines/flow/Flow;", "setLanguage", "", "language", "(Lcom/trixsearch/hasikit/util/AppLanguage;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "applyLocale", "base", "app_debug"})
public final class LanguageManager {
    @org.jetbrains.annotations.NotNull()
    private final android.content.Context context = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.Flow<com.trixsearch.hasikit.util.AppLanguage> selectedLanguage = null;
    
    @javax.inject.Inject()
    public LanguageManager(@dagger.hilt.android.qualifiers.ApplicationContext()
    @org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<com.trixsearch.hasikit.util.AppLanguage> getSelectedLanguage() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object setLanguage(@org.jetbrains.annotations.NotNull()
    com.trixsearch.hasikit.util.AppLanguage language, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    /**
     * Apply locale to a Context — call from Activity.attachBaseContext
     */
    @org.jetbrains.annotations.NotNull()
    public final android.content.Context applyLocale(@org.jetbrains.annotations.NotNull()
    android.content.Context base, @org.jetbrains.annotations.NotNull()
    com.trixsearch.hasikit.util.AppLanguage language) {
        return null;
    }
}