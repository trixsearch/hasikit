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

@kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000\u0090\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\n\n\u0002\u0010 \n\u0002\b\u0013\n\u0002\u0018\u0002\n\u0002\u0010\t\n\u0002\b\u0006\n\u0002\u0010\b\n\u0002\b\u000b\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0018\u0002\n\u0002\b\t\b\u0007\u0018\u00002\u00020\u0001:\u0001kB;\b\u0007\u0012\b\b\u0001\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\t\u0012\u0006\u0010\n\u001a\u00020\u000b\u0012\u0006\u0010\f\u001a\u00020\r\u00a2\u0006\u0004\b\u000e\u0010\u000fJ\u000e\u0010\u0015\u001a\u00020\u00162\u0006\u0010\u0017\u001a\u00020\u0012J\u000e\u0010 \u001a\u00020!2\u0006\u0010\"\u001a\u00020\u0019J\u000e\u0010#\u001a\u00020!2\u0006\u0010\"\u001a\u00020\u0019J\u000e\u0010$\u001a\u00020!2\u0006\u0010\"\u001a\u00020\u001eJ\u000e\u0010.\u001a\u00020!2\u0006\u0010\"\u001a\u00020\u0019J\u000e\u0010/\u001a\u00020!2\u0006\u0010\"\u001a\u00020\u0019J\u000e\u00100\u001a\u00020!2\u0006\u0010\"\u001a\u00020\u0019J\u000e\u00103\u001a\u00020!2\u0006\u0010\"\u001a\u00020\u0019J\u000e\u00104\u001a\u00020\u00162\u0006\u00105\u001a\u00020\u001eJ\u000e\u00106\u001a\u00020\u00162\u0006\u00105\u001a\u00020\u001eJ\u000e\u0010;\u001a\u00020\u00162\u0006\u0010\"\u001a\u00020\u0019J\u000e\u0010<\u001a\u00020\u00162\u0006\u0010=\u001a\u00020\u001eJ\b\u0010>\u001a\u00020\u0016H\u0002J\u0006\u0010K\u001a\u00020\u0016J\u0006\u0010L\u001a\u00020\u0016J\u0006\u0010M\u001a\u00020\u0016J\u0006\u0010N\u001a\u00020\u0016J\u0006\u0010O\u001a\u00020\u0016J\b\u0010P\u001a\u00020AH\u0002J\b\u0010Q\u001a\u00020AH\u0002J\b\u0010R\u001a\u00020HH\u0002J\u0006\u0010V\u001a\u00020\u0016J\u0006\u0010W\u001a\u00020\u0016J\u0016\u0010^\u001a\u00020\u00162\u0006\u0010_\u001a\u00020\u001e2\u0006\u0010`\u001a\u00020\u001eJ\u000e\u0010a\u001a\u00020\u00162\u0006\u0010_\u001a\u00020\u001eJ\u0006\u0010h\u001a\u00020\u0016J\u0014\u0010i\u001a\u00020\u00162\f\u0010j\u001a\b\u0012\u0004\u0012\u00020c0,R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u000bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\rX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00120\u0011\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014R\u0017\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\u00190\u0011\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001a\u0010\u0014R\u0017\u0010\u001b\u001a\b\u0012\u0004\u0012\u00020\u00190\u0011\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001c\u0010\u0014R\u0017\u0010\u001d\u001a\b\u0012\u0004\u0012\u00020\u001e0\u0011\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001f\u0010\u0014R\u0017\u0010%\u001a\b\u0012\u0004\u0012\u00020\u00190\u0011\u00a2\u0006\b\n\u0000\u001a\u0004\b&\u0010\u0014R\u0017\u0010\'\u001a\b\u0012\u0004\u0012\u00020\u00190\u0011\u00a2\u0006\b\n\u0000\u001a\u0004\b(\u0010\u0014R\u0017\u0010)\u001a\b\u0012\u0004\u0012\u00020\u00190\u0011\u00a2\u0006\b\n\u0000\u001a\u0004\b*\u0010\u0014R\u001d\u0010+\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u001e0,0\u0011\u00a2\u0006\b\n\u0000\u001a\u0004\b-\u0010\u0014R\u0017\u00101\u001a\b\u0012\u0004\u0012\u00020\u00190\u0011\u00a2\u0006\b\n\u0000\u001a\u0004\b2\u0010\u0014R\u0017\u00107\u001a\b\u0012\u0004\u0012\u00020\u00190\u0011\u00a2\u0006\b\n\u0000\u001a\u0004\b8\u0010\u0014R\u0017\u00109\u001a\b\u0012\u0004\u0012\u00020\u001e0\u0011\u00a2\u0006\b\n\u0000\u001a\u0004\b:\u0010\u0014R\u0014\u0010?\u001a\b\u0012\u0004\u0012\u00020A0@X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010B\u001a\b\u0012\u0004\u0012\u00020A0\u0011\u00a2\u0006\b\n\u0000\u001a\u0004\bC\u0010\u0014R\u0014\u0010D\u001a\b\u0012\u0004\u0012\u00020A0@X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010E\u001a\b\u0012\u0004\u0012\u00020A0\u0011\u00a2\u0006\b\n\u0000\u001a\u0004\bF\u0010\u0014R\u0014\u0010G\u001a\b\u0012\u0004\u0012\u00020H0@X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010I\u001a\b\u0012\u0004\u0012\u00020H0\u0011\u00a2\u0006\b\n\u0000\u001a\u0004\bJ\u0010\u0014R\u0019\u0010S\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010T0\u0011\u00a2\u0006\b\n\u0000\u001a\u0004\bU\u0010\u0014R\u0017\u0010X\u001a\b\u0012\u0004\u0012\u00020Y0,\u00a2\u0006\b\n\u0000\u001a\u0004\bZ\u0010[R\u001d\u0010\\\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020Y0,0\u0011\u00a2\u0006\b\n\u0000\u001a\u0004\b]\u0010\u0014R\u001a\u0010b\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020c0,0@X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001d\u0010d\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020c0,0\u0011\u00a2\u0006\b\n\u0000\u001a\u0004\be\u0010\u0014R\u0014\u0010f\u001a\b\u0012\u0004\u0012\u00020\u00190@X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010g\u001a\b\u0012\u0004\u0012\u00020\u00190\u0011\u00a2\u0006\b\n\u0000\u001a\u0004\bg\u0010\u0014\u00a8\u0006l"}, d2 = {"Lcom/trixsearch/hasikit/ui/screens/settings/SettingsViewModel;", "Landroidx/lifecycle/ViewModel;", "context", "Landroid/content/Context;", "repository", "Lcom/trixsearch/hasikit/domain/repository/VideoRepository;", "downloadManager", "Lcom/trixsearch/hasikit/download/HasikitDownloadManager;", "telegramAuthRepository", "Lcom/trixsearch/hasikit/telegram/domain/repository/TelegramAuthRepository;", "telegramSourceConfig", "Lcom/trixsearch/hasikit/telegram/config/TelegramSourceConfig;", "telegramClientService", "Lcom/trixsearch/hasikit/telegram/service/TelegramClientService;", "<init>", "(Landroid/content/Context;Lcom/trixsearch/hasikit/domain/repository/VideoRepository;Lcom/trixsearch/hasikit/download/HasikitDownloadManager;Lcom/trixsearch/hasikit/telegram/domain/repository/TelegramAuthRepository;Lcom/trixsearch/hasikit/telegram/config/TelegramSourceConfig;Lcom/trixsearch/hasikit/telegram/service/TelegramClientService;)V", "appTheme", "Lkotlinx/coroutines/flow/StateFlow;", "Lcom/trixsearch/hasikit/ui/theme/AppTheme;", "getAppTheme", "()Lkotlinx/coroutines/flow/StateFlow;", "setTheme", "", "theme", "wifiOnly", "", "getWifiOnly", "autoPlay", "getAutoPlay", "streamingQuality", "", "getStreamingQuality", "setWifiOnly", "Lkotlinx/coroutines/Job;", "v", "setAutoPlay", "setStreamingQuality", "resumeAfterCall", "getResumeAfterCall", "pauseOnHeadphoneRemoval", "getPauseOnHeadphoneRemoval", "backgroundAudio", "getBackgroundAudio", "customAspectRatios", "", "getCustomAspectRatios", "setResumeAfterCall", "setPauseOnHeadphoneRemoval", "setBackgroundAudio", "autoplayNext", "getAutoplayNext", "setAutoplayNext", "addCustomAspectRatio", "ratio", "removeCustomAspectRatio", "galleryVisible", "getGalleryVisible", "downloadPath", "getDownloadPath", "setGalleryVisible", "setDownloadPath", "uri", "scanDownloadedFilesIntoGallery", "_cacheSize", "Lkotlinx/coroutines/flow/MutableStateFlow;", "", "cacheSize", "getCacheSize", "_storageUsed", "storageUsed", "getStorageUsed", "_downloadCount", "", "downloadCount", "getDownloadCount", "refreshStorageStats", "clearThumbnailCache", "clearPlayerCache", "clearCache", "clearAllStorage", "calcCacheSize", "calcDownloadStorage", "countDownloadedFiles", "currentUser", "Lcom/trixsearch/hasikit/telegram/domain/model/TelegramUser;", "getCurrentUser", "logout", "forceDeleteSession", "officialSources", "Lcom/trixsearch/hasikit/telegram/config/TelegramSource;", "getOfficialSources", "()Ljava/util/List;", "userSources", "getUserSources", "addUserSource", "identifier", "displayName", "removeUserSource", "_joinedChats", "Lcom/trixsearch/hasikit/ui/screens/settings/SettingsViewModel$TelegramChatEntry;", "joinedChats", "getJoinedChats", "_isLoadingChats", "isLoadingChats", "fetchJoinedChats", "addUserSourcesFromChats", "selected", "TelegramChatEntry", "app_debug"})
@dagger.hilt.android.lifecycle.HiltViewModel()
public final class SettingsViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull()
    private final android.content.Context context = null;
    @org.jetbrains.annotations.NotNull()
    private final com.trixsearch.hasikit.domain.repository.VideoRepository repository = null;
    @org.jetbrains.annotations.NotNull()
    private final com.trixsearch.hasikit.download.HasikitDownloadManager downloadManager = null;
    @org.jetbrains.annotations.NotNull()
    private final com.trixsearch.hasikit.telegram.domain.repository.TelegramAuthRepository telegramAuthRepository = null;
    @org.jetbrains.annotations.NotNull()
    private final com.trixsearch.hasikit.telegram.config.TelegramSourceConfig telegramSourceConfig = null;
    @org.jetbrains.annotations.NotNull()
    private final com.trixsearch.hasikit.telegram.service.TelegramClientService telegramClientService = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<com.trixsearch.hasikit.ui.theme.AppTheme> appTheme = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.Boolean> wifiOnly = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.Boolean> autoPlay = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.String> streamingQuality = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.Boolean> resumeAfterCall = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.Boolean> pauseOnHeadphoneRemoval = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.Boolean> backgroundAudio = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.util.List<java.lang.String>> customAspectRatios = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.Boolean> autoplayNext = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.Boolean> galleryVisible = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.String> downloadPath = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.Long> _cacheSize = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.Long> cacheSize = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.Long> _storageUsed = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.Long> storageUsed = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.Integer> _downloadCount = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.Integer> downloadCount = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<com.trixsearch.hasikit.telegram.domain.model.TelegramUser> currentUser = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<com.trixsearch.hasikit.telegram.config.TelegramSource> officialSources = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.util.List<com.trixsearch.hasikit.telegram.config.TelegramSource>> userSources = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.util.List<com.trixsearch.hasikit.ui.screens.settings.SettingsViewModel.TelegramChatEntry>> _joinedChats = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.util.List<com.trixsearch.hasikit.ui.screens.settings.SettingsViewModel.TelegramChatEntry>> joinedChats = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.Boolean> _isLoadingChats = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.Boolean> isLoadingChats = null;
    
    @javax.inject.Inject()
    public SettingsViewModel(@dagger.hilt.android.qualifiers.ApplicationContext()
    @org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    com.trixsearch.hasikit.domain.repository.VideoRepository repository, @org.jetbrains.annotations.NotNull()
    com.trixsearch.hasikit.download.HasikitDownloadManager downloadManager, @org.jetbrains.annotations.NotNull()
    com.trixsearch.hasikit.telegram.domain.repository.TelegramAuthRepository telegramAuthRepository, @org.jetbrains.annotations.NotNull()
    com.trixsearch.hasikit.telegram.config.TelegramSourceConfig telegramSourceConfig, @org.jetbrains.annotations.NotNull()
    com.trixsearch.hasikit.telegram.service.TelegramClientService telegramClientService) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<com.trixsearch.hasikit.ui.theme.AppTheme> getAppTheme() {
        return null;
    }
    
    public final void setTheme(@org.jetbrains.annotations.NotNull()
    com.trixsearch.hasikit.ui.theme.AppTheme theme) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.Boolean> getWifiOnly() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.Boolean> getAutoPlay() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.String> getStreamingQuality() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.Job setWifiOnly(boolean v) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.Job setAutoPlay(boolean v) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.Job setStreamingQuality(@org.jetbrains.annotations.NotNull()
    java.lang.String v) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.Boolean> getResumeAfterCall() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.Boolean> getPauseOnHeadphoneRemoval() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.Boolean> getBackgroundAudio() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.util.List<java.lang.String>> getCustomAspectRatios() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.Job setResumeAfterCall(boolean v) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.Job setPauseOnHeadphoneRemoval(boolean v) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.Job setBackgroundAudio(boolean v) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.Boolean> getAutoplayNext() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.Job setAutoplayNext(boolean v) {
        return null;
    }
    
    public final void addCustomAspectRatio(@org.jetbrains.annotations.NotNull()
    java.lang.String ratio) {
    }
    
    public final void removeCustomAspectRatio(@org.jetbrains.annotations.NotNull()
    java.lang.String ratio) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.Boolean> getGalleryVisible() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.String> getDownloadPath() {
        return null;
    }
    
    public final void setGalleryVisible(boolean v) {
    }
    
    public final void setDownloadPath(@org.jetbrains.annotations.NotNull()
    java.lang.String uri) {
    }
    
    private final void scanDownloadedFilesIntoGallery() {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.Long> getCacheSize() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.Long> getStorageUsed() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.Integer> getDownloadCount() {
        return null;
    }
    
    public final void refreshStorageStats() {
    }
    
    public final void clearThumbnailCache() {
    }
    
    public final void clearPlayerCache() {
    }
    
    public final void clearCache() {
    }
    
    public final void clearAllStorage() {
    }
    
    private final long calcCacheSize() {
        return 0L;
    }
    
    private final long calcDownloadStorage() {
        return 0L;
    }
    
    private final int countDownloadedFiles() {
        return 0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<com.trixsearch.hasikit.telegram.domain.model.TelegramUser> getCurrentUser() {
        return null;
    }
    
    public final void logout() {
    }
    
    public final void forceDeleteSession() {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.trixsearch.hasikit.telegram.config.TelegramSource> getOfficialSources() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.util.List<com.trixsearch.hasikit.telegram.config.TelegramSource>> getUserSources() {
        return null;
    }
    
    public final void addUserSource(@org.jetbrains.annotations.NotNull()
    java.lang.String identifier, @org.jetbrains.annotations.NotNull()
    java.lang.String displayName) {
    }
    
    public final void removeUserSource(@org.jetbrains.annotations.NotNull()
    java.lang.String identifier) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.util.List<com.trixsearch.hasikit.ui.screens.settings.SettingsViewModel.TelegramChatEntry>> getJoinedChats() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.Boolean> isLoadingChats() {
        return null;
    }
    
    public final void fetchJoinedChats() {
    }
    
    public final void addUserSourcesFromChats(@org.jetbrains.annotations.NotNull()
    java.util.List<com.trixsearch.hasikit.ui.screens.settings.SettingsViewModel.TelegramChatEntry> selected) {
    }
    
    @kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u000e\n\u0002\b\r\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001B\u001f\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0005\u00a2\u0006\u0004\b\u0007\u0010\bJ\t\u0010\u000e\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u000f\u001a\u00020\u0005H\u00c6\u0003J\t\u0010\u0010\u001a\u00020\u0005H\u00c6\u0003J\'\u0010\u0011\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u0005H\u00c6\u0001J\u0013\u0010\u0012\u001a\u00020\u00132\b\u0010\u0014\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0015\u001a\u00020\u0016H\u00d6\u0001J\t\u0010\u0017\u001a\u00020\u0005H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0011\u0010\u0006\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\f\u00a8\u0006\u0018"}, d2 = {"Lcom/trixsearch/hasikit/ui/screens/settings/SettingsViewModel$TelegramChatEntry;", "", "chatId", "", "title", "", "type", "<init>", "(JLjava/lang/String;Ljava/lang/String;)V", "getChatId", "()J", "getTitle", "()Ljava/lang/String;", "getType", "component1", "component2", "component3", "copy", "equals", "", "other", "hashCode", "", "toString", "app_debug"})
    public static final class TelegramChatEntry {
        private final long chatId = 0L;
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String title = null;
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String type = null;
        
        public TelegramChatEntry(long chatId, @org.jetbrains.annotations.NotNull()
        java.lang.String title, @org.jetbrains.annotations.NotNull()
        java.lang.String type) {
            super();
        }
        
        public final long getChatId() {
            return 0L;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String getTitle() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String getType() {
            return null;
        }
        
        public final long component1() {
            return 0L;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String component2() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String component3() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.trixsearch.hasikit.ui.screens.settings.SettingsViewModel.TelegramChatEntry copy(long chatId, @org.jetbrains.annotations.NotNull()
        java.lang.String title, @org.jetbrains.annotations.NotNull()
        java.lang.String type) {
            return null;
        }
        
        @java.lang.Override()
        public boolean equals(@org.jetbrains.annotations.Nullable()
        java.lang.Object other) {
            return false;
        }
        
        @java.lang.Override()
        public int hashCode() {
            return 0;
        }
        
        @java.lang.Override()
        @org.jetbrains.annotations.NotNull()
        public java.lang.String toString() {
            return null;
        }
    }
}