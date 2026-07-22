package com.trixsearch.hasikit.ui.screens.home;

import com.trixsearch.hasikit.data.local.entities.FavoriteEntity;
import com.trixsearch.hasikit.data.local.entities.WatchLaterEntity;
import android.util.Log;
import androidx.lifecycle.ViewModel;
import com.trixsearch.hasikit.domain.model.DownloadState;
import com.trixsearch.hasikit.domain.model.Video;
import com.trixsearch.hasikit.domain.model.WatchProgress;
import com.trixsearch.hasikit.domain.repository.VideoRepository;
import com.trixsearch.hasikit.download.HasikitDownloadManager;
import com.trixsearch.hasikit.telegram.config.TelegramSource;
import com.trixsearch.hasikit.telegram.config.TelegramSourceConfig;
import com.trixsearch.hasikit.telegram.domain.model.AuthState;
import com.trixsearch.hasikit.telegram.domain.model.TelegramMedia;
import com.trixsearch.hasikit.telegram.domain.repository.TelegramAuthRepository;
import com.trixsearch.hasikit.telegram.domain.repository.TelegramChannelRepository;
import com.trixsearch.hasikit.search.SearchEngine;
import dagger.hilt.android.lifecycle.HiltViewModel;
import kotlinx.coroutines.flow.*;
import kotlinx.coroutines.Dispatchers;
import javax.inject.Inject;

@kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000\u00aa\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u000e\n\u0002\b\u0006\n\u0002\u0010$\n\u0002\u0010\t\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\f\n\u0002\u0010\b\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\n\n\u0002\u0010\"\n\u0002\b\b\b\u0007\u0018\u00002\u00020\u0001B1\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\t\u0012\u0006\u0010\n\u001a\u00020\u000b\u00a2\u0006\u0004\b\f\u0010\rJ\u0010\u0010)\u001a\u00020*2\b\u0010+\u001a\u0004\u0018\u00010\u001aJ\b\u00107\u001a\u00020*H\u0002J\b\u00108\u001a\u00020*H\u0002J(\u00109\u001a\u0004\u0018\u00010\u00112\u0006\u0010:\u001a\u00020\'2\u0006\u0010;\u001a\u00020\"2\u0006\u0010<\u001a\u00020\u0013H\u0082@\u00a2\u0006\u0002\u0010=J\u0006\u0010B\u001a\u00020*J\u0006\u0010C\u001a\u00020*J\u0016\u0010D\u001a\u00020*2\f\u0010E\u001a\b\u0012\u0004\u0012\u00020F0\u0010H\u0002J\u0006\u0010G\u001a\u00020*J\u000e\u0010S\u001a\u00020*2\u0006\u0010T\u001a\u00020\u001aJ\u0006\u0010U\u001a\u00020*J\u000e\u0010V\u001a\u00020*2\u0006\u0010W\u001a\u000201J\u000e\u0010X\u001a\u00020*2\u0006\u0010Y\u001a\u00020\u001aJ\u000e\u0010Z\u001a\u00020*2\u0006\u0010W\u001a\u000201J\u000e\u0010[\u001a\u00020*2\u0006\u0010Y\u001a\u00020\u001aJ\u000e\u0010a\u001a\u00020*2\u0006\u0010W\u001a\u000201J\u000e\u0010b\u001a\u00020*2\u0006\u0010Y\u001a\u00020\u001aJ\u000e\u0010c\u001a\u00020*2\u0006\u0010Y\u001a\u00020\u001aJ\u000e\u0010d\u001a\u00020*2\u0006\u0010Y\u001a\u00020\u001aR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u000bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001a\u0010\u000e\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00110\u00100\u000fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00130\u000fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\u00130\u0015\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0016R\u0014\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\u00130\u000fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\u00130\u0015\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0016R\u0016\u0010\u0019\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u001a0\u000fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0019\u0010\u001b\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u001a0\u0015\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001c\u0010\u0016R\u0016\u0010\u001d\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u001a0\u000fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0019\u0010\u001e\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u001a0\u0015\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001f\u0010\u0016R\"\u0010 \u001a\u0016\u0012\u0012\u0012\u0010\u0012\u0004\u0012\u00020\"\u0012\u0006\u0012\u0004\u0018\u00010\u001a0!0\u000fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010#\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u001a0\u000fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0019\u0010$\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u001a0\u0015\u00a2\u0006\b\n\u0000\u001a\u0004\b%\u0010\u0016R\u001d\u0010&\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\'0\u00100\u0015\u00a2\u0006\b\n\u0000\u001a\u0004\b(\u0010\u0016R#\u0010,\u001a\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u001a\u0012\u0004\u0012\u00020-0!0\u0015\u00a2\u0006\b\n\u0000\u001a\u0004\b.\u0010\u0016R)\u0010/\u001a\u001a\u0012\u0016\u0012\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u000201\u0012\u0004\u0012\u000202000\u00100\u0015\u00a2\u0006\b\n\u0000\u001a\u0004\b3\u0010\u0016R\u001d\u00104\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u0002010\u00100\u0015\u00a2\u0006\b\n\u0000\u001a\u0004\b5\u0010\u0016R\u000e\u00106\u001a\u00020\u0013X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0011\u0010>\u001a\u00020?8F\u00a2\u0006\u0006\u001a\u0004\b@\u0010AR\u001a\u0010H\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u0002010\u00100\u000fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001d\u0010I\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u0002010\u00100\u0015\u00a2\u0006\b\n\u0000\u001a\u0004\bJ\u0010\u0016R\u0014\u0010K\u001a\b\u0012\u0004\u0012\u00020\u00130\u000fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010L\u001a\b\u0012\u0004\u0012\u00020\u00130\u0015\u00a2\u0006\b\n\u0000\u001a\u0004\bL\u0010\u0016R\u0016\u0010M\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010N0\u000fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0019\u0010O\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010N0\u0015\u00a2\u0006\b\n\u0000\u001a\u0004\bP\u0010\u0016R\u0010\u0010Q\u001a\u0004\u0018\u00010RX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u001d\u0010\\\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u001a0]0\u0015\u00a2\u0006\b\n\u0000\u001a\u0004\b^\u0010\u0016R\u001d\u0010_\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u001a0]0\u0015\u00a2\u0006\b\n\u0000\u001a\u0004\b`\u0010\u0016\u00a8\u0006e"}, d2 = {"Lcom/trixsearch/hasikit/ui/screens/home/HomeViewModel;", "Landroidx/lifecycle/ViewModel;", "repository", "Lcom/trixsearch/hasikit/domain/repository/VideoRepository;", "downloadManager", "Lcom/trixsearch/hasikit/download/HasikitDownloadManager;", "channelRepository", "Lcom/trixsearch/hasikit/telegram/domain/repository/TelegramChannelRepository;", "authRepository", "Lcom/trixsearch/hasikit/telegram/domain/repository/TelegramAuthRepository;", "sourceConfig", "Lcom/trixsearch/hasikit/telegram/config/TelegramSourceConfig;", "<init>", "(Lcom/trixsearch/hasikit/domain/repository/VideoRepository;Lcom/trixsearch/hasikit/download/HasikitDownloadManager;Lcom/trixsearch/hasikit/telegram/domain/repository/TelegramChannelRepository;Lcom/trixsearch/hasikit/telegram/domain/repository/TelegramAuthRepository;Lcom/trixsearch/hasikit/telegram/config/TelegramSourceConfig;)V", "_sourcePages", "Lkotlinx/coroutines/flow/MutableStateFlow;", "", "Lcom/trixsearch/hasikit/ui/screens/home/SourcePage;", "_isLoading", "", "isLoading", "Lkotlinx/coroutines/flow/StateFlow;", "()Lkotlinx/coroutines/flow/StateFlow;", "_isLoadingMore", "isLoadingMore", "_error", "", "error", "getError", "_noAccessMessage", "noAccessMessage", "getNoAccessMessage", "_thumbnailCache", "", "", "_selectedSourceFilter", "selectedSourceFilter", "getSelectedSourceFilter", "availableSources", "Lcom/trixsearch/hasikit/telegram/config/TelegramSource;", "getAvailableSources", "setSourceFilter", "", "sourceDisplayName", "downloadTasks", "Lcom/trixsearch/hasikit/domain/model/DownloadTask;", "getDownloadTasks", "continueWatching", "Lkotlin/Pair;", "Lcom/trixsearch/hasikit/domain/model/Video;", "Lcom/trixsearch/hasikit/domain/model/WatchProgress;", "getContinueWatching", "videos", "getVideos", "isLoadingAllSources", "startAutoRefresh", "loadAllSources", "loadPage", "source", "chatId", "reset", "(Lcom/trixsearch/hasikit/telegram/config/TelegramSource;JZLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "prefetchThreshold", "", "getPrefetchThreshold", "()I", "loadMore", "refresh", "fetchThumbnails", "mediaList", "Lcom/trixsearch/hasikit/telegram/domain/model/TelegramMedia;", "invalidateAndReloadThumbnails", "_searchResults", "searchResults", "getSearchResults", "_isSearching", "isSearching", "_searchIntent", "Lcom/trixsearch/hasikit/search/SearchEngine$SearchIntent;", "searchIntent", "getSearchIntent", "searchJob", "Lkotlinx/coroutines/Job;", "searchTelegram", "query", "clearSearch", "addFavorite", "video", "removeFavorite", "videoId", "addWatchLater", "removeWatchLater", "favoriteIds", "", "getFavoriteIds", "watchLaterIds", "getWatchLaterIds", "startDownload", "deleteDownload", "pauseDownload", "resumeDownload", "app_debug"})
@dagger.hilt.android.lifecycle.HiltViewModel()
public final class HomeViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull()
    private final com.trixsearch.hasikit.domain.repository.VideoRepository repository = null;
    @org.jetbrains.annotations.NotNull()
    private final com.trixsearch.hasikit.download.HasikitDownloadManager downloadManager = null;
    @org.jetbrains.annotations.NotNull()
    private final com.trixsearch.hasikit.telegram.domain.repository.TelegramChannelRepository channelRepository = null;
    @org.jetbrains.annotations.NotNull()
    private final com.trixsearch.hasikit.telegram.domain.repository.TelegramAuthRepository authRepository = null;
    @org.jetbrains.annotations.NotNull()
    private final com.trixsearch.hasikit.telegram.config.TelegramSourceConfig sourceConfig = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.util.List<com.trixsearch.hasikit.ui.screens.home.SourcePage>> _sourcePages = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.Boolean> _isLoading = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.Boolean> isLoading = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.Boolean> _isLoadingMore = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.Boolean> isLoadingMore = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.String> _error = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.String> error = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.String> _noAccessMessage = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.String> noAccessMessage = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.util.Map<java.lang.Long, java.lang.String>> _thumbnailCache = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.String> _selectedSourceFilter = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.String> selectedSourceFilter = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.util.List<com.trixsearch.hasikit.telegram.config.TelegramSource>> availableSources = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.util.Map<java.lang.String, com.trixsearch.hasikit.domain.model.DownloadTask>> downloadTasks = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.util.List<kotlin.Pair<com.trixsearch.hasikit.domain.model.Video, com.trixsearch.hasikit.domain.model.WatchProgress>>> continueWatching = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.util.List<com.trixsearch.hasikit.domain.model.Video>> videos = null;
    @kotlin.jvm.Volatile()
    private volatile boolean isLoadingAllSources = false;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.util.List<com.trixsearch.hasikit.domain.model.Video>> _searchResults = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.util.List<com.trixsearch.hasikit.domain.model.Video>> searchResults = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.Boolean> _isSearching = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.Boolean> isSearching = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<com.trixsearch.hasikit.search.SearchEngine.SearchIntent> _searchIntent = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<com.trixsearch.hasikit.search.SearchEngine.SearchIntent> searchIntent = null;
    @org.jetbrains.annotations.Nullable()
    private kotlinx.coroutines.Job searchJob;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.util.Set<java.lang.String>> favoriteIds = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.util.Set<java.lang.String>> watchLaterIds = null;
    
    @javax.inject.Inject()
    public HomeViewModel(@org.jetbrains.annotations.NotNull()
    com.trixsearch.hasikit.domain.repository.VideoRepository repository, @org.jetbrains.annotations.NotNull()
    com.trixsearch.hasikit.download.HasikitDownloadManager downloadManager, @org.jetbrains.annotations.NotNull()
    com.trixsearch.hasikit.telegram.domain.repository.TelegramChannelRepository channelRepository, @org.jetbrains.annotations.NotNull()
    com.trixsearch.hasikit.telegram.domain.repository.TelegramAuthRepository authRepository, @org.jetbrains.annotations.NotNull()
    com.trixsearch.hasikit.telegram.config.TelegramSourceConfig sourceConfig) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.Boolean> isLoading() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.Boolean> isLoadingMore() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.String> getError() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.String> getNoAccessMessage() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.String> getSelectedSourceFilter() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.util.List<com.trixsearch.hasikit.telegram.config.TelegramSource>> getAvailableSources() {
        return null;
    }
    
    public final void setSourceFilter(@org.jetbrains.annotations.Nullable()
    java.lang.String sourceDisplayName) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.util.Map<java.lang.String, com.trixsearch.hasikit.domain.model.DownloadTask>> getDownloadTasks() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.util.List<kotlin.Pair<com.trixsearch.hasikit.domain.model.Video, com.trixsearch.hasikit.domain.model.WatchProgress>>> getContinueWatching() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.util.List<com.trixsearch.hasikit.domain.model.Video>> getVideos() {
        return null;
    }
    
    private final void startAutoRefresh() {
    }
    
    private final void loadAllSources() {
    }
    
    private final java.lang.Object loadPage(com.trixsearch.hasikit.telegram.config.TelegramSource source, long chatId, boolean reset, kotlin.coroutines.Continuation<? super com.trixsearch.hasikit.ui.screens.home.SourcePage> $completion) {
        return null;
    }
    
    public final int getPrefetchThreshold() {
        return 0;
    }
    
    public final void loadMore() {
    }
    
    public final void refresh() {
    }
    
    private final void fetchThumbnails(java.util.List<com.trixsearch.hasikit.telegram.domain.model.TelegramMedia> mediaList) {
    }
    
    public final void invalidateAndReloadThumbnails() {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.util.List<com.trixsearch.hasikit.domain.model.Video>> getSearchResults() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.Boolean> isSearching() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<com.trixsearch.hasikit.search.SearchEngine.SearchIntent> getSearchIntent() {
        return null;
    }
    
    public final void searchTelegram(@org.jetbrains.annotations.NotNull()
    java.lang.String query) {
    }
    
    public final void clearSearch() {
    }
    
    public final void addFavorite(@org.jetbrains.annotations.NotNull()
    com.trixsearch.hasikit.domain.model.Video video) {
    }
    
    public final void removeFavorite(@org.jetbrains.annotations.NotNull()
    java.lang.String videoId) {
    }
    
    public final void addWatchLater(@org.jetbrains.annotations.NotNull()
    com.trixsearch.hasikit.domain.model.Video video) {
    }
    
    public final void removeWatchLater(@org.jetbrains.annotations.NotNull()
    java.lang.String videoId) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.util.Set<java.lang.String>> getFavoriteIds() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.util.Set<java.lang.String>> getWatchLaterIds() {
        return null;
    }
    
    public final void startDownload(@org.jetbrains.annotations.NotNull()
    com.trixsearch.hasikit.domain.model.Video video) {
    }
    
    public final void deleteDownload(@org.jetbrains.annotations.NotNull()
    java.lang.String videoId) {
    }
    
    public final void pauseDownload(@org.jetbrains.annotations.NotNull()
    java.lang.String videoId) {
    }
    
    public final void resumeDownload(@org.jetbrains.annotations.NotNull()
    java.lang.String videoId) {
    }
}