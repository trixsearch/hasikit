package com.trixsearch.hasikit.telegram.config;

import android.content.Context;
import android.util.Log;
import com.trixsearch.hasikit.BuildConfig;
import dagger.hilt.android.qualifiers.ApplicationContext;
import kotlinx.coroutines.flow.Flow;
import javax.inject.Inject;
import javax.inject.Singleton;

@kotlin.Metadata(mv = {2, 1, 0}, k = 2, xi = 48, d1 = {"\u0000\u001e\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\"\u000e\u0010\u0000\u001a\u00020\u0001X\u0082T\u00a2\u0006\u0002\n\u0000\"%\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003*\u00020\u00058BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\b\u0010\t\u001a\u0004\b\u0006\u0010\u0007\"\u0014\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u00010\u000bX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\f"}, d2 = {"TAG", "", "sourcesDataStore", "Landroidx/datastore/core/DataStore;", "Landroidx/datastore/preferences/core/Preferences;", "Landroid/content/Context;", "getSourcesDataStore", "(Landroid/content/Context;)Landroidx/datastore/core/DataStore;", "sourcesDataStore$delegate", "Lkotlin/properties/ReadOnlyProperty;", "KEY_USER_SOURCES", "Landroidx/datastore/preferences/core/Preferences$Key;", "app_debug"})
public final class TelegramSourceConfigKt {
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String TAG = "TelegramSourceConfig";
    @org.jetbrains.annotations.NotNull()
    private static final kotlin.properties.ReadOnlyProperty sourcesDataStore$delegate = null;
    @org.jetbrains.annotations.NotNull()
    private static final androidx.datastore.preferences.core.Preferences.Key<java.lang.String> KEY_USER_SOURCES = null;
    
    private static final androidx.datastore.core.DataStore<androidx.datastore.preferences.core.Preferences> getSourcesDataStore(android.content.Context $this$sourcesDataStore) {
        return null;
    }
}