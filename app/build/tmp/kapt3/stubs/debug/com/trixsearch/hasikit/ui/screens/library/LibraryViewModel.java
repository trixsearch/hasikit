package com.trixsearch.hasikit.ui.screens.library;

import android.util.Log;
import androidx.compose.foundation.ExperimentalFoundationApi;
import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.material.icons.Icons;
import androidx.compose.material3.ButtonDefaults;
import androidx.compose.material3.CardDefaults;
import androidx.compose.material3.OutlinedTextFieldDefaults;
import androidx.compose.runtime.Composable;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.graphics.vector.ImageVector;
import androidx.compose.ui.layout.ContentScale;
import androidx.compose.ui.text.font.FontWeight;
import androidx.compose.ui.text.style.TextOverflow;
import androidx.lifecycle.ViewModel;
import androidx.navigation.NavController;
import com.trixsearch.hasikit.domain.model.DownloadState;
import com.trixsearch.hasikit.domain.model.DownloadTask;
import com.trixsearch.hasikit.domain.model.Video;
import com.trixsearch.hasikit.domain.model.WatchProgress;
import com.trixsearch.hasikit.domain.repository.VideoRepository;
import com.trixsearch.hasikit.download.HasikitDownloadManager;
import com.trixsearch.hasikit.ui.navigation.Screen;
import dagger.hilt.android.lifecycle.HiltViewModel;
import kotlinx.coroutines.flow.SharingStarted;
import kotlinx.coroutines.flow.StateFlow;
import javax.inject.Inject;

@kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000V\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0007\u0018\u00002\u00020\u0001B\u0019\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0004\b\u0006\u0010\u0007J\u000e\u0010\u0019\u001a\u00020\u001a2\u0006\u0010\u001b\u001a\u00020\u001cJ\u000e\u0010\u001d\u001a\u00020\u001a2\u0006\u0010\u001b\u001a\u00020\u001cJ\u000e\u0010\u001e\u001a\u00020\u001a2\u0006\u0010\u001b\u001a\u00020\u001cJ\u000e\u0010\u001f\u001a\u00020\u001a2\u0006\u0010\u001b\u001a\u00020\u001cJ\u000e\u0010 \u001a\u00020\u001a2\u0006\u0010!\u001a\u00020\"J\u000e\u0010#\u001a\u00020\u001a2\u0006\u0010\u001b\u001a\u00020\u001cJ\u000e\u0010$\u001a\u00020\u001a2\u0006\u0010!\u001a\u00020\"R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001d\u0010\b\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u000b0\n0\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u001d\u0010\u000e\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u000b0\n0\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\rR\u001d\u0010\u0010\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00110\n0\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\rR\u001d\u0010\u0013\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00140\n0\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\rR\u001d\u0010\u0016\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00170\n0\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\r\u00a8\u0006%"}, d2 = {"Lcom/trixsearch/hasikit/ui/screens/library/LibraryViewModel;", "Landroidx/lifecycle/ViewModel;", "repository", "Lcom/trixsearch/hasikit/domain/repository/VideoRepository;", "downloadManager", "Lcom/trixsearch/hasikit/download/HasikitDownloadManager;", "<init>", "(Lcom/trixsearch/hasikit/domain/repository/VideoRepository;Lcom/trixsearch/hasikit/download/HasikitDownloadManager;)V", "downloadedItems", "Lkotlinx/coroutines/flow/StateFlow;", "", "Lcom/trixsearch/hasikit/ui/screens/library/LibraryItem;", "getDownloadedItems", "()Lkotlinx/coroutines/flow/StateFlow;", "activeDownloads", "getActiveDownloads", "favorites", "Lcom/trixsearch/hasikit/data/local/entities/FavoriteEntity;", "getFavorites", "watchLater", "Lcom/trixsearch/hasikit/data/local/entities/WatchLaterEntity;", "getWatchLater", "watchHistory", "Lcom/trixsearch/hasikit/data/local/entities/WatchHistoryEntity;", "getWatchHistory", "removeFavorite", "", "videoId", "", "removeFromWatchLater", "removeFromHistory", "deleteDownload", "retryDownload", "video", "Lcom/trixsearch/hasikit/domain/model/Video;", "pauseDownload", "resumeDownload", "app_debug"})
@dagger.hilt.android.lifecycle.HiltViewModel()
public final class LibraryViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull()
    private final com.trixsearch.hasikit.domain.repository.VideoRepository repository = null;
    @org.jetbrains.annotations.NotNull()
    private final com.trixsearch.hasikit.download.HasikitDownloadManager downloadManager = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.util.List<com.trixsearch.hasikit.ui.screens.library.LibraryItem>> downloadedItems = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.util.List<com.trixsearch.hasikit.ui.screens.library.LibraryItem>> activeDownloads = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.util.List<com.trixsearch.hasikit.data.local.entities.FavoriteEntity>> favorites = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.util.List<com.trixsearch.hasikit.data.local.entities.WatchLaterEntity>> watchLater = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.util.List<com.trixsearch.hasikit.data.local.entities.WatchHistoryEntity>> watchHistory = null;
    
    @javax.inject.Inject()
    public LibraryViewModel(@org.jetbrains.annotations.NotNull()
    com.trixsearch.hasikit.domain.repository.VideoRepository repository, @org.jetbrains.annotations.NotNull()
    com.trixsearch.hasikit.download.HasikitDownloadManager downloadManager) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.util.List<com.trixsearch.hasikit.ui.screens.library.LibraryItem>> getDownloadedItems() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.util.List<com.trixsearch.hasikit.ui.screens.library.LibraryItem>> getActiveDownloads() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.util.List<com.trixsearch.hasikit.data.local.entities.FavoriteEntity>> getFavorites() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.util.List<com.trixsearch.hasikit.data.local.entities.WatchLaterEntity>> getWatchLater() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.util.List<com.trixsearch.hasikit.data.local.entities.WatchHistoryEntity>> getWatchHistory() {
        return null;
    }
    
    public final void removeFavorite(@org.jetbrains.annotations.NotNull()
    java.lang.String videoId) {
    }
    
    public final void removeFromWatchLater(@org.jetbrains.annotations.NotNull()
    java.lang.String videoId) {
    }
    
    public final void removeFromHistory(@org.jetbrains.annotations.NotNull()
    java.lang.String videoId) {
    }
    
    public final void deleteDownload(@org.jetbrains.annotations.NotNull()
    java.lang.String videoId) {
    }
    
    public final void retryDownload(@org.jetbrains.annotations.NotNull()
    com.trixsearch.hasikit.domain.model.Video video) {
    }
    
    public final void pauseDownload(@org.jetbrains.annotations.NotNull()
    java.lang.String videoId) {
    }
    
    public final void resumeDownload(@org.jetbrains.annotations.NotNull()
    com.trixsearch.hasikit.domain.model.Video video) {
    }
}