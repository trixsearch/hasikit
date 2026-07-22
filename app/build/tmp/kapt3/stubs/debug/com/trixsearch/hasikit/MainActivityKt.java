package com.trixsearch.hasikit;

import android.content.Intent;
import android.Manifest;
import android.app.PictureInPictureParams;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Rational;
import androidx.activity.ComponentActivity;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.compose.material.icons.Icons;
import androidx.compose.material3.*;
import androidx.compose.runtime.*;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.tooling.preview.Preview;
import androidx.core.content.ContextCompat;
import com.trixsearch.hasikit.player.HasikitPlayer;
import com.trixsearch.hasikit.telegram.domain.repository.TelegramAuthRepository;
import com.trixsearch.hasikit.ui.navigation.Screen;
import com.trixsearch.hasikit.ui.theme.AppTheme;
import com.trixsearch.hasikit.util.AppLanguage;
import dagger.hilt.android.AndroidEntryPoint;
import javax.inject.Inject;

@kotlin.Metadata(mv = {2, 1, 0}, k = 2, xi = 48, d1 = {"\u0000,\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\"\n\u0000\n\u0002\u0010\u0002\n\u0000\u001a\b\u0010\u0010\u001a\u00020\u0011H\u0003\"\u000e\u0010\u0000\u001a\u00020\u0001X\u0082T\u00a2\u0006\u0002\n\u0000\"%\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003*\u00020\u00058FX\u0086\u0084\u0002\u00a2\u0006\f\n\u0004\b\b\u0010\t\u001a\u0004\b\u0006\u0010\u0007\"\u0017\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u00010\u000b\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\r\"\u0014\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00010\u000fX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0012"}, d2 = {"TAG", "", "themeDataStore", "Landroidx/datastore/core/DataStore;", "Landroidx/datastore/preferences/core/Preferences;", "Landroid/content/Context;", "getThemeDataStore", "(Landroid/content/Context;)Landroidx/datastore/core/DataStore;", "themeDataStore$delegate", "Lkotlin/properties/ReadOnlyProperty;", "THEME_KEY", "Landroidx/datastore/preferences/core/Preferences$Key;", "getTHEME_KEY", "()Landroidx/datastore/preferences/core/Preferences$Key;", "HIDE_BOTTOM_NAV_ROUTES", "", "MainScreenPreview", "", "app_debug"})
public final class MainActivityKt {
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String TAG = "MainActivity";
    @org.jetbrains.annotations.NotNull()
    private static final kotlin.properties.ReadOnlyProperty themeDataStore$delegate = null;
    @org.jetbrains.annotations.NotNull()
    private static final androidx.datastore.preferences.core.Preferences.Key<java.lang.String> THEME_KEY = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.util.Set<java.lang.String> HIDE_BOTTOM_NAV_ROUTES = null;
    
    @org.jetbrains.annotations.NotNull()
    public static final androidx.datastore.core.DataStore<androidx.datastore.preferences.core.Preferences> getThemeDataStore(@org.jetbrains.annotations.NotNull()
    android.content.Context $this$themeDataStore) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public static final androidx.datastore.preferences.core.Preferences.Key<java.lang.String> getTHEME_KEY() {
        return null;
    }
    
    @androidx.compose.ui.tooling.preview.Preview(showBackground = true, showSystemUi = true, name = "Main Screen - Dark")
    @androidx.compose.runtime.Composable()
    private static final void MainScreenPreview() {
    }
}