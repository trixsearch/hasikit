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

@kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\r\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B#\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\u0010\u0004\u001a\u0004\u0018\u00010\u0005\u0012\b\u0010\u0006\u001a\u0004\u0018\u00010\u0007\u00a2\u0006\u0004\b\b\u0010\tJ\t\u0010\u0010\u001a\u00020\u0003H\u00c6\u0003J\u000b\u0010\u0011\u001a\u0004\u0018\u00010\u0005H\u00c6\u0003J\u000b\u0010\u0012\u001a\u0004\u0018\u00010\u0007H\u00c6\u0003J+\u0010\u0013\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u00052\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u0007H\u00c6\u0001J\u0013\u0010\u0014\u001a\u00020\u00152\b\u0010\u0016\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0017\u001a\u00020\u0018H\u00d6\u0001J\t\u0010\u0019\u001a\u00020\u001aH\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0013\u0010\u0004\u001a\u0004\u0018\u00010\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0013\u0010\u0006\u001a\u0004\u0018\u00010\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000f\u00a8\u0006\u001b"}, d2 = {"Lcom/trixsearch/hasikit/ui/screens/library/LibraryItem;", "", "video", "Lcom/trixsearch/hasikit/domain/model/Video;", "task", "Lcom/trixsearch/hasikit/domain/model/DownloadTask;", "watchProgress", "Lcom/trixsearch/hasikit/domain/model/WatchProgress;", "<init>", "(Lcom/trixsearch/hasikit/domain/model/Video;Lcom/trixsearch/hasikit/domain/model/DownloadTask;Lcom/trixsearch/hasikit/domain/model/WatchProgress;)V", "getVideo", "()Lcom/trixsearch/hasikit/domain/model/Video;", "getTask", "()Lcom/trixsearch/hasikit/domain/model/DownloadTask;", "getWatchProgress", "()Lcom/trixsearch/hasikit/domain/model/WatchProgress;", "component1", "component2", "component3", "copy", "equals", "", "other", "hashCode", "", "toString", "", "app_debug"})
public final class LibraryItem {
    @org.jetbrains.annotations.NotNull()
    private final com.trixsearch.hasikit.domain.model.Video video = null;
    @org.jetbrains.annotations.Nullable()
    private final com.trixsearch.hasikit.domain.model.DownloadTask task = null;
    @org.jetbrains.annotations.Nullable()
    private final com.trixsearch.hasikit.domain.model.WatchProgress watchProgress = null;
    
    public LibraryItem(@org.jetbrains.annotations.NotNull()
    com.trixsearch.hasikit.domain.model.Video video, @org.jetbrains.annotations.Nullable()
    com.trixsearch.hasikit.domain.model.DownloadTask task, @org.jetbrains.annotations.Nullable()
    com.trixsearch.hasikit.domain.model.WatchProgress watchProgress) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.trixsearch.hasikit.domain.model.Video getVideo() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final com.trixsearch.hasikit.domain.model.DownloadTask getTask() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final com.trixsearch.hasikit.domain.model.WatchProgress getWatchProgress() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.trixsearch.hasikit.domain.model.Video component1() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final com.trixsearch.hasikit.domain.model.DownloadTask component2() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final com.trixsearch.hasikit.domain.model.WatchProgress component3() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.trixsearch.hasikit.ui.screens.library.LibraryItem copy(@org.jetbrains.annotations.NotNull()
    com.trixsearch.hasikit.domain.model.Video video, @org.jetbrains.annotations.Nullable()
    com.trixsearch.hasikit.domain.model.DownloadTask task, @org.jetbrains.annotations.Nullable()
    com.trixsearch.hasikit.domain.model.WatchProgress watchProgress) {
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