package com.trixsearch.hasikit.ui.screens.settings;

import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.compose.foundation.layout.*;
import androidx.compose.material.icons.Icons;
import androidx.compose.material.icons.filled.*;
import androidx.compose.material3.*;
import androidx.compose.runtime.*;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.graphics.vector.ImageVector;
import androidx.compose.ui.layout.ContentScale;
import androidx.compose.ui.text.font.FontWeight;
import androidx.lifecycle.ViewModel;
import androidx.navigation.NavController;
import com.trixsearch.hasikit.BuildConfig;
import com.trixsearch.hasikit.domain.repository.VideoRepository;
import com.trixsearch.hasikit.download.HasikitDownloadManager;
import com.trixsearch.hasikit.ui.theme.AppTheme;
import com.trixsearch.hasikit.telegram.config.TelegramSource;
import com.trixsearch.hasikit.telegram.config.TelegramSourceConfig;
import com.trixsearch.hasikit.telegram.domain.model.TelegramUser;
import com.trixsearch.hasikit.telegram.domain.repository.TelegramAuthRepository;
import com.trixsearch.hasikit.telegram.service.TelegramClientService;
import com.trixsearch.hasikit.ui.navigation.Screen;
import dagger.hilt.android.lifecycle.HiltViewModel;
import dagger.hilt.android.qualifiers.ApplicationContext;
import kotlinx.coroutines.Dispatchers;
import kotlinx.coroutines.flow.*;
import org.drinkless.tdlib.TdApi;
import javax.inject.Inject;

@kotlin.Metadata(mv = {2, 1, 0}, k = 2, xi = 48, d1 = {"\u0000\u00a8\u0001\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\u0010\u000b\n\u0002\b\n\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0018\n\u0002\u0010\b\n\u0000\n\u0002\u0010\t\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\b\u001a\u001a\u0010\u0019\u001a\u00020\u001a2\u0006\u0010\u001b\u001a\u00020\u001c2\b\b\u0002\u0010\u001d\u001a\u00020\u001eH\u0007\u001a\u00bd\u0001\u0010\u001f\u001a\u00020\u001a2\f\u0010 \u001a\b\u0012\u0004\u0012\u00020!0\u00172\f\u0010\"\u001a\b\u0012\u0004\u0012\u00020!0\u001726\u0010#\u001a2\u0012\u0013\u0012\u00110\u0001\u00a2\u0006\f\b%\u0012\b\b&\u0012\u0004\b\b(\'\u0012\u0013\u0012\u00110\u0001\u00a2\u0006\f\b%\u0012\b\b&\u0012\u0004\b\b((\u0012\u0004\u0012\u00020\u001a0$2!\u0010)\u001a\u001d\u0012\u0013\u0012\u00110\u0001\u00a2\u0006\f\b%\u0012\b\b&\u0012\u0004\b\b(\'\u0012\u0004\u0012\u00020\u001a0*2\f\u0010+\u001a\b\u0012\u0004\u0012\u00020,0\u00172\u0006\u0010-\u001a\u00020\f2\f\u0010.\u001a\b\u0012\u0004\u0012\u00020\u001a0/2\u0018\u00100\u001a\u0014\u0012\n\u0012\b\u0012\u0004\u0012\u00020,0\u0017\u0012\u0004\u0012\u00020\u001a0*H\u0003\u001a\u0012\u00101\u001a\u00020\u001a2\b\u00102\u001a\u0004\u0018\u000103H\u0003\u001a\u0016\u00104\u001a\u00020\u001a2\f\u00105\u001a\b\u0012\u0004\u0012\u00020\u001a0/H\u0003\u001a\u0016\u00106\u001a\u00020\u001a2\f\u00107\u001a\b\u0012\u0004\u0012\u00020\u001a0/H\u0003\u001a\u0016\u00108\u001a\u00020\u001a2\f\u00107\u001a\b\u0012\u0004\u0012\u00020\u001a0/H\u0003\u001a\u001e\u00109\u001a\u00020\u001a2\u0006\u0010:\u001a\u00020;2\f\u0010<\u001a\b\u0012\u0004\u0012\u00020\u001a0/H\u0003\u001a\u00aa\u0001\u0010=\u001a\u00020\u001a2\u0006\u0010>\u001a\u00020\f2\u0006\u0010?\u001a\u00020\u00012\u0012\u0010@\u001a\u000e\u0012\u0004\u0012\u00020\f\u0012\u0004\u0012\u00020\u001a0*2\f\u0010A\u001a\b\u0012\u0004\u0012\u00020\u001a0/2\u0006\u0010B\u001a\u00020\f2\u0012\u0010C\u001a\u000e\u0012\u0004\u0012\u00020\f\u0012\u0004\u0012\u00020\u001a0*2\u0006\u0010D\u001a\u00020\f2\u0012\u0010E\u001a\u000e\u0012\u0004\u0012\u00020\f\u0012\u0004\u0012\u00020\u001a0*2\u0006\u0010F\u001a\u00020\f2\u0012\u0010G\u001a\u000e\u0012\u0004\u0012\u00020\f\u0012\u0004\u0012\u00020\u001a0*2\u0006\u0010H\u001a\u00020\f2\u0012\u0010I\u001a\u000e\u0012\u0004\u0012\u00020\f\u0012\u0004\u0012\u00020\u001a0*H\u0003\u001a\u0016\u0010J\u001a\u00020\u001a2\f\u00107\u001a\b\u0012\u0004\u0012\u00020\u001a0/H\u0003\u001a\\\u0010K\u001a\u00020\u001a2\u0006\u0010L\u001a\u00020\f2\u0012\u0010M\u001a\u000e\u0012\u0004\u0012\u00020\f\u0012\u0004\u0012\u00020\u001a0*2\u0006\u0010N\u001a\u00020\f2\u0012\u0010O\u001a\u000e\u0012\u0004\u0012\u00020\f\u0012\u0004\u0012\u00020\u001a0*2\u0006\u0010P\u001a\u00020\u00012\u0012\u0010Q\u001a\u000e\u0012\u0004\u0012\u00020\u0001\u0012\u0004\u0012\u00020\u001a0*H\u0003\u001a<\u0010R\u001a\u00020\u001a2\u0006\u0010S\u001a\u00020T2\u0006\u0010U\u001a\u00020V2\u0006\u0010W\u001a\u00020V2\f\u0010X\u001a\b\u0012\u0004\u0012\u00020\u001a0/2\f\u0010Y\u001a\b\u0012\u0004\u0012\u00020\u001a0/H\u0003\u001a\b\u0010Z\u001a\u00020\u001aH\u0003\u001a6\u0010[\u001a\u00020\u001a2\u0006\u0010\\\u001a\u00020\u00012\u0006\u0010]\u001a\u00020^2\u001c\u0010_\u001a\u0018\u0012\u0004\u0012\u00020`\u0012\u0004\u0012\u00020\u001a0*\u00a2\u0006\u0002\ba\u00a2\u0006\u0002\bbH\u0001\u001a<\u0010c\u001a\u00020\u001a2\u0006\u0010]\u001a\u00020^2\u0006\u0010\\\u001a\u00020\u00012\u0006\u0010d\u001a\u00020\u00012\u0006\u0010e\u001a\u00020\f2\u0012\u0010f\u001a\u000e\u0012\u0004\u0012\u00020\f\u0012\u0004\u0012\u00020\u001a0*H\u0003\u001a?\u0010g\u001a\u00020\u001a2\u0006\u0010]\u001a\u00020^2\u0006\u0010\\\u001a\u00020\u00012\u0006\u0010d\u001a\u00020\u00012\f\u00107\u001a\b\u0012\u0004\u0012\u00020\u001a0/2\b\b\u0002\u0010h\u001a\u00020iH\u0003\u00a2\u0006\u0004\bj\u0010k\u001a \u0010l\u001a\u00020\u001a2\u0006\u0010]\u001a\u00020^2\u0006\u0010\\\u001a\u00020\u00012\u0006\u0010d\u001a\u00020\u0001H\u0003\u001a(\u0010m\u001a\u00020\u001a2\u0006\u0010]\u001a\u00020^2\u0006\u0010\\\u001a\u00020\u00012\u0006\u0010d\u001a\u00020\u00012\u0006\u0010n\u001a\u00020\u0001H\u0003\u001a\u0010\u0010o\u001a\u00020\u00012\u0006\u0010p\u001a\u00020VH\u0002\"\u000e\u0010\u0000\u001a\u00020\u0001X\u0082T\u00a2\u0006\u0002\n\u0000\"%\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003*\u00020\u00058@X\u0080\u0084\u0002\u00a2\u0006\f\n\u0004\b\b\u0010\t\u001a\u0004\b\u0006\u0010\u0007\"\u0014\u0010\n\u001a\b\u0012\u0004\u0012\u00020\f0\u000bX\u0082\u0004\u00a2\u0006\u0002\n\u0000\"\u0014\u0010\r\u001a\b\u0012\u0004\u0012\u00020\f0\u000bX\u0082\u0004\u00a2\u0006\u0002\n\u0000\"\u0014\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00010\u000bX\u0082\u0004\u00a2\u0006\u0002\n\u0000\"\u0014\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\f0\u000bX\u0082\u0004\u00a2\u0006\u0002\n\u0000\"\u0014\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00010\u000bX\u0082\u0004\u00a2\u0006\u0002\n\u0000\"\u0014\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\f0\u000bX\u0082\u0004\u00a2\u0006\u0002\n\u0000\"\u0014\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\f0\u000bX\u0082\u0004\u00a2\u0006\u0002\n\u0000\"\u0014\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\f0\u000bX\u0082\u0004\u00a2\u0006\u0002\n\u0000\"\u0014\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\u00010\u000bX\u0082\u0004\u00a2\u0006\u0002\n\u0000\"\u0014\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\f0\u000bX\u0082\u0004\u00a2\u0006\u0002\n\u0000\"\u0014\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\u00180\u0017X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006q"}, d2 = {"TAG", "", "settingsDataStore", "Landroidx/datastore/core/DataStore;", "Landroidx/datastore/preferences/core/Preferences;", "Landroid/content/Context;", "getSettingsDataStore", "(Landroid/content/Context;)Landroidx/datastore/core/DataStore;", "settingsDataStore$delegate", "Lkotlin/properties/ReadOnlyProperty;", "KEY_WIFI_ONLY", "Landroidx/datastore/preferences/core/Preferences$Key;", "", "KEY_AUTO_PLAY", "KEY_STREAMING_QUALITY", "KEY_GALLERY_VISIBLE", "KEY_DOWNLOAD_PATH", "KEY_RESUME_AFTER_CALL", "KEY_PAUSE_ON_HEADPHONE_REMOVAL", "KEY_BACKGROUND_AUDIO", "KEY_CUSTOM_ASPECT_RATIOS", "KEY_AUTOPLAY_NEXT", "ALL_SECTIONS", "", "Lcom/trixsearch/hasikit/ui/screens/settings/SettingsSection;", "SettingsScreen", "", "navController", "Landroidx/navigation/NavController;", "viewModel", "Lcom/trixsearch/hasikit/ui/screens/settings/SettingsViewModel;", "TelegramSourcesSection", "officialSources", "Lcom/trixsearch/hasikit/telegram/config/TelegramSource;", "userSources", "onAddSource", "Lkotlin/Function2;", "Lkotlin/ParameterName;", "name", "identifier", "displayName", "onRemoveSource", "Lkotlin/Function1;", "joinedChats", "Lcom/trixsearch/hasikit/ui/screens/settings/SettingsViewModel$TelegramChatEntry;", "isLoadingChats", "onFetchChats", "Lkotlin/Function0;", "onAddFromChats", "AccountSection", "user", "Lcom/trixsearch/hasikit/telegram/domain/model/TelegramUser;", "SignOutSection", "onLogout", "LanguageSection", "onClick", "RequestSection", "AppearanceSection", "appTheme", "Lcom/trixsearch/hasikit/ui/theme/AppTheme;", "onThemeClick", "PlayerSection", "autoPlay", "quality", "onAutoPlay", "onQualityClick", "resumeAfterCall", "onResumeAfterCall", "pauseOnHeadphoneRemoval", "onPauseOnHeadphoneRemoval", "backgroundAudio", "onBackgroundAudio", "autoplayNext", "onAutoplayNext", "AdvancedSection", "DownloadsSection", "wifiOnly", "onWifiOnly", "galleryVisible", "onGalleryVisible", "downloadPath", "onPickFolder", "StorageSection", "downloadCount", "", "storageUsed", "", "cacheSize", "onClearCache", "onClearAll", "AboutSection", "SettingsGroup", "title", "icon", "Landroidx/compose/ui/graphics/vector/ImageVector;", "content", "Landroidx/compose/foundation/layout/ColumnScope;", "Landroidx/compose/runtime/Composable;", "Lkotlin/ExtensionFunctionType;", "SettingsToggleRow", "subtitle", "checked", "onCheckedChange", "SettingsClickRow", "tintColor", "Landroidx/compose/ui/graphics/Color;", "SettingsClickRow-xwkQ0AY", "(Landroidx/compose/ui/graphics/vector/ImageVector;Ljava/lang/String;Ljava/lang/String;Lkotlin/jvm/functions/Function0;J)V", "SettingsInfoRow", "SettingsLinkRow", "url", "formatBytes", "bytes", "app_debug"})
public final class SettingsScreenKt {
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String TAG = "SettingsScreen";
    @org.jetbrains.annotations.NotNull()
    private static final kotlin.properties.ReadOnlyProperty settingsDataStore$delegate = null;
    @org.jetbrains.annotations.NotNull()
    private static final androidx.datastore.preferences.core.Preferences.Key<java.lang.Boolean> KEY_WIFI_ONLY = null;
    @org.jetbrains.annotations.NotNull()
    private static final androidx.datastore.preferences.core.Preferences.Key<java.lang.Boolean> KEY_AUTO_PLAY = null;
    @org.jetbrains.annotations.NotNull()
    private static final androidx.datastore.preferences.core.Preferences.Key<java.lang.String> KEY_STREAMING_QUALITY = null;
    @org.jetbrains.annotations.NotNull()
    private static final androidx.datastore.preferences.core.Preferences.Key<java.lang.Boolean> KEY_GALLERY_VISIBLE = null;
    @org.jetbrains.annotations.NotNull()
    private static final androidx.datastore.preferences.core.Preferences.Key<java.lang.String> KEY_DOWNLOAD_PATH = null;
    @org.jetbrains.annotations.NotNull()
    private static final androidx.datastore.preferences.core.Preferences.Key<java.lang.Boolean> KEY_RESUME_AFTER_CALL = null;
    @org.jetbrains.annotations.NotNull()
    private static final androidx.datastore.preferences.core.Preferences.Key<java.lang.Boolean> KEY_PAUSE_ON_HEADPHONE_REMOVAL = null;
    @org.jetbrains.annotations.NotNull()
    private static final androidx.datastore.preferences.core.Preferences.Key<java.lang.Boolean> KEY_BACKGROUND_AUDIO = null;
    @org.jetbrains.annotations.NotNull()
    private static final androidx.datastore.preferences.core.Preferences.Key<java.lang.String> KEY_CUSTOM_ASPECT_RATIOS = null;
    @org.jetbrains.annotations.NotNull()
    private static final androidx.datastore.preferences.core.Preferences.Key<java.lang.Boolean> KEY_AUTOPLAY_NEXT = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.util.List<com.trixsearch.hasikit.ui.screens.settings.SettingsSection> ALL_SECTIONS = null;
    
    @org.jetbrains.annotations.NotNull()
    public static final androidx.datastore.core.DataStore<androidx.datastore.preferences.core.Preferences> getSettingsDataStore(@org.jetbrains.annotations.NotNull()
    android.content.Context $this$settingsDataStore) {
        return null;
    }
    
    @kotlin.OptIn(markerClass = {androidx.compose.material3.ExperimentalMaterial3Api.class})
    @androidx.compose.runtime.Composable()
    public static final void SettingsScreen(@org.jetbrains.annotations.NotNull()
    androidx.navigation.NavController navController, @org.jetbrains.annotations.NotNull()
    com.trixsearch.hasikit.ui.screens.settings.SettingsViewModel viewModel) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void TelegramSourcesSection(java.util.List<com.trixsearch.hasikit.telegram.config.TelegramSource> officialSources, java.util.List<com.trixsearch.hasikit.telegram.config.TelegramSource> userSources, kotlin.jvm.functions.Function2<? super java.lang.String, ? super java.lang.String, kotlin.Unit> onAddSource, kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onRemoveSource, java.util.List<com.trixsearch.hasikit.ui.screens.settings.SettingsViewModel.TelegramChatEntry> joinedChats, boolean isLoadingChats, kotlin.jvm.functions.Function0<kotlin.Unit> onFetchChats, kotlin.jvm.functions.Function1<? super java.util.List<com.trixsearch.hasikit.ui.screens.settings.SettingsViewModel.TelegramChatEntry>, kotlin.Unit> onAddFromChats) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void AccountSection(com.trixsearch.hasikit.telegram.domain.model.TelegramUser user) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void SignOutSection(kotlin.jvm.functions.Function0<kotlin.Unit> onLogout) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void LanguageSection(kotlin.jvm.functions.Function0<kotlin.Unit> onClick) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void RequestSection(kotlin.jvm.functions.Function0<kotlin.Unit> onClick) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void AppearanceSection(com.trixsearch.hasikit.ui.theme.AppTheme appTheme, kotlin.jvm.functions.Function0<kotlin.Unit> onThemeClick) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void PlayerSection(boolean autoPlay, java.lang.String quality, kotlin.jvm.functions.Function1<? super java.lang.Boolean, kotlin.Unit> onAutoPlay, kotlin.jvm.functions.Function0<kotlin.Unit> onQualityClick, boolean resumeAfterCall, kotlin.jvm.functions.Function1<? super java.lang.Boolean, kotlin.Unit> onResumeAfterCall, boolean pauseOnHeadphoneRemoval, kotlin.jvm.functions.Function1<? super java.lang.Boolean, kotlin.Unit> onPauseOnHeadphoneRemoval, boolean backgroundAudio, kotlin.jvm.functions.Function1<? super java.lang.Boolean, kotlin.Unit> onBackgroundAudio, boolean autoplayNext, kotlin.jvm.functions.Function1<? super java.lang.Boolean, kotlin.Unit> onAutoplayNext) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void AdvancedSection(kotlin.jvm.functions.Function0<kotlin.Unit> onClick) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void DownloadsSection(boolean wifiOnly, kotlin.jvm.functions.Function1<? super java.lang.Boolean, kotlin.Unit> onWifiOnly, boolean galleryVisible, kotlin.jvm.functions.Function1<? super java.lang.Boolean, kotlin.Unit> onGalleryVisible, java.lang.String downloadPath, kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onPickFolder) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void StorageSection(int downloadCount, long storageUsed, long cacheSize, kotlin.jvm.functions.Function0<kotlin.Unit> onClearCache, kotlin.jvm.functions.Function0<kotlin.Unit> onClearAll) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void AboutSection() {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void SettingsGroup(@org.jetbrains.annotations.NotNull()
    java.lang.String title, @org.jetbrains.annotations.NotNull()
    androidx.compose.ui.graphics.vector.ImageVector icon, @org.jetbrains.annotations.NotNull()
    androidx.compose.runtime.internal.ComposableFunction1<? super androidx.compose.foundation.layout.ColumnScope, kotlin.Unit> content) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void SettingsToggleRow(androidx.compose.ui.graphics.vector.ImageVector icon, java.lang.String title, java.lang.String subtitle, boolean checked, kotlin.jvm.functions.Function1<? super java.lang.Boolean, kotlin.Unit> onCheckedChange) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void SettingsInfoRow(androidx.compose.ui.graphics.vector.ImageVector icon, java.lang.String title, java.lang.String subtitle) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void SettingsLinkRow(androidx.compose.ui.graphics.vector.ImageVector icon, java.lang.String title, java.lang.String subtitle, java.lang.String url) {
    }
    
    private static final java.lang.String formatBytes(long bytes) {
        return null;
    }
}