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

@kotlin.Metadata(mv = {2, 1, 0}, k = 2, xi = 48, d1 = {"\u0000J\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0010\t\n\u0002\b\u0003\u001a\u001a\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u0007H\u0007\u001a\u0018\u0010\b\u001a\u00020\u00032\u0006\u0010\t\u001a\u00020\u00012\u0006\u0010\n\u001a\u00020\u000bH\u0003\u001a\u0012\u0010\f\u001a\u00020\u00032\b\b\u0002\u0010\r\u001a\u00020\u000eH\u0003\u001a,\u0010\u000f\u001a\u00020\u00032\u0006\u0010\u0010\u001a\u00020\u00112\f\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00030\u00132\f\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\u00030\u0013H\u0007\u001a`\u0010\u0015\u001a\u00020\u00032\u0006\u0010\u0010\u001a\u00020\u00112\f\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\u00030\u00132\f\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\u00030\u00132\f\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\u00030\u00132\b\b\u0002\u0010\u0019\u001a\u00020\u001a2\b\b\u0002\u0010\u001b\u001a\u00020\u001a2\u0010\b\u0002\u0010\u001c\u001a\n\u0012\u0004\u0012\u00020\u0003\u0018\u00010\u0013H\u0007\u001a\u0010\u0010\u001d\u001a\u00020\u00012\u0006\u0010\u001e\u001a\u00020\u001fH\u0002\u001a\u0010\u0010 \u001a\u00020\u00012\u0006\u0010!\u001a\u00020\u001fH\u0002\"\u000e\u0010\u0000\u001a\u00020\u0001X\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\""}, d2 = {"TAG", "", "LibraryScreen", "", "navController", "Landroidx/navigation/NavController;", "viewModel", "Lcom/trixsearch/hasikit/ui/screens/library/LibraryViewModel;", "SectionLabel", "title", "icon", "Landroidx/compose/ui/graphics/vector/ImageVector;", "EmptyLibraryState", "modifier", "Landroidx/compose/ui/Modifier;", "ActiveDownloadCard", "item", "Lcom/trixsearch/hasikit/ui/screens/library/LibraryItem;", "onPause", "Lkotlin/Function0;", "onResume", "DownloadedVideoCard", "onPlay", "onDelete", "onRedownload", "isSelected", "", "selectionMode", "onLongPress", "formatBytes", "bytes", "", "formatTime", "ms", "app_debug"})
public final class LibraryScreenKt {
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String TAG = "LibraryScreen";
    
    @androidx.compose.runtime.Composable()
    public static final void LibraryScreen(@org.jetbrains.annotations.NotNull()
    androidx.navigation.NavController navController, @org.jetbrains.annotations.NotNull()
    com.trixsearch.hasikit.ui.screens.library.LibraryViewModel viewModel) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void SectionLabel(java.lang.String title, androidx.compose.ui.graphics.vector.ImageVector icon) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void EmptyLibraryState(androidx.compose.ui.Modifier modifier) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void ActiveDownloadCard(@org.jetbrains.annotations.NotNull()
    com.trixsearch.hasikit.ui.screens.library.LibraryItem item, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onPause, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onResume) {
    }
    
    @kotlin.OptIn(markerClass = {androidx.compose.foundation.ExperimentalFoundationApi.class})
    @androidx.compose.runtime.Composable()
    public static final void DownloadedVideoCard(@org.jetbrains.annotations.NotNull()
    com.trixsearch.hasikit.ui.screens.library.LibraryItem item, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onPlay, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onDelete, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onRedownload, boolean isSelected, boolean selectionMode, @org.jetbrains.annotations.Nullable()
    kotlin.jvm.functions.Function0<kotlin.Unit> onLongPress) {
    }
    
    private static final java.lang.String formatBytes(long bytes) {
        return null;
    }
    
    private static final java.lang.String formatTime(long ms) {
        return null;
    }
}