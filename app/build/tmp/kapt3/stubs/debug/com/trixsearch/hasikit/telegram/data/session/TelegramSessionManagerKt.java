package com.trixsearch.hasikit.telegram.data.session;

import android.content.Context;
import android.util.Log;
import com.trixsearch.hasikit.telegram.domain.model.TelegramUser;
import dagger.hilt.android.qualifiers.ApplicationContext;
import kotlinx.coroutines.flow.Flow;
import javax.inject.Inject;
import javax.inject.Singleton;

@kotlin.Metadata(mv = {2, 1, 0}, k = 2, xi = 48, d1 = {"\u0000*\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\t\n\u0002\b\u0007\"\u000e\u0010\u0000\u001a\u00020\u0001X\u0082T\u00a2\u0006\u0002\n\u0000\"%\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003*\u00020\u00058BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\b\u0010\t\u001a\u0004\b\u0006\u0010\u0007\"\u0014\u0010\n\u001a\b\u0012\u0004\u0012\u00020\f0\u000bX\u0082\u0004\u00a2\u0006\u0002\n\u0000\"\u0014\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u000e0\u000bX\u0082\u0004\u00a2\u0006\u0002\n\u0000\"\u0014\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00010\u000bX\u0082\u0004\u00a2\u0006\u0002\n\u0000\"\u0014\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00010\u000bX\u0082\u0004\u00a2\u0006\u0002\n\u0000\"\u0014\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00010\u000bX\u0082\u0004\u00a2\u0006\u0002\n\u0000\"\u0014\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00010\u000bX\u0082\u0004\u00a2\u0006\u0002\n\u0000\"\u0014\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u00010\u000bX\u0082\u0004\u00a2\u0006\u0002\n\u0000\"\u0014\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\u00010\u000bX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0015"}, d2 = {"TAG", "", "telegramSessionStore", "Landroidx/datastore/core/DataStore;", "Landroidx/datastore/preferences/core/Preferences;", "Landroid/content/Context;", "getTelegramSessionStore", "(Landroid/content/Context;)Landroidx/datastore/core/DataStore;", "telegramSessionStore$delegate", "Lkotlin/properties/ReadOnlyProperty;", "KEY_IS_AUTHENTICATED", "Landroidx/datastore/preferences/core/Preferences$Key;", "", "KEY_USER_ID", "", "KEY_FIRST_NAME", "KEY_LAST_NAME", "KEY_USERNAME", "KEY_PHONE", "KEY_PHOTO_URL", "KEY_SESSION_STRING", "app_debug"})
public final class TelegramSessionManagerKt {
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String TAG = "TelegramSession";
    @org.jetbrains.annotations.NotNull()
    private static final kotlin.properties.ReadOnlyProperty telegramSessionStore$delegate = null;
    @org.jetbrains.annotations.NotNull()
    private static final androidx.datastore.preferences.core.Preferences.Key<java.lang.Boolean> KEY_IS_AUTHENTICATED = null;
    @org.jetbrains.annotations.NotNull()
    private static final androidx.datastore.preferences.core.Preferences.Key<java.lang.Long> KEY_USER_ID = null;
    @org.jetbrains.annotations.NotNull()
    private static final androidx.datastore.preferences.core.Preferences.Key<java.lang.String> KEY_FIRST_NAME = null;
    @org.jetbrains.annotations.NotNull()
    private static final androidx.datastore.preferences.core.Preferences.Key<java.lang.String> KEY_LAST_NAME = null;
    @org.jetbrains.annotations.NotNull()
    private static final androidx.datastore.preferences.core.Preferences.Key<java.lang.String> KEY_USERNAME = null;
    @org.jetbrains.annotations.NotNull()
    private static final androidx.datastore.preferences.core.Preferences.Key<java.lang.String> KEY_PHONE = null;
    @org.jetbrains.annotations.NotNull()
    private static final androidx.datastore.preferences.core.Preferences.Key<java.lang.String> KEY_PHOTO_URL = null;
    @org.jetbrains.annotations.NotNull()
    private static final androidx.datastore.preferences.core.Preferences.Key<java.lang.String> KEY_SESSION_STRING = null;
    
    private static final androidx.datastore.core.DataStore<androidx.datastore.preferences.core.Preferences> getTelegramSessionStore(android.content.Context $this$telegramSessionStore) {
        return null;
    }
}