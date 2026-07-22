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

@kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0011\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\u0011\b\u0002\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0004\b\u0004\u0010\u0005R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007j\u0002\b\bj\u0002\b\tj\u0002\b\nj\u0002\b\u000bj\u0002\b\fj\u0002\b\rj\u0002\b\u000ej\u0002\b\u000fj\u0002\b\u0010j\u0002\b\u0011j\u0002\b\u0012j\u0002\b\u0013\u00a8\u0006\u0014"}, d2 = {"Lcom/trixsearch/hasikit/ui/screens/library/LibrarySortOrder;", "", "label", "", "<init>", "(Ljava/lang/String;ILjava/lang/String;)V", "getLabel", "()Ljava/lang/String;", "NAME_AZ", "NAME_ZA", "NEWEST_FIRST", "OLDEST_FIRST", "LARGEST_SIZE", "SMALLEST_SIZE", "LONGEST_DURATION", "SHORTEST_DURATION", "CHANNEL_NAME", "DOWNLOADED", "DOWNLOADING", "PAUSED", "app_debug"})
public enum LibrarySortOrder {
    /*public static final*/ NAME_AZ /* = new NAME_AZ(null) */,
    /*public static final*/ NAME_ZA /* = new NAME_ZA(null) */,
    /*public static final*/ NEWEST_FIRST /* = new NEWEST_FIRST(null) */,
    /*public static final*/ OLDEST_FIRST /* = new OLDEST_FIRST(null) */,
    /*public static final*/ LARGEST_SIZE /* = new LARGEST_SIZE(null) */,
    /*public static final*/ SMALLEST_SIZE /* = new SMALLEST_SIZE(null) */,
    /*public static final*/ LONGEST_DURATION /* = new LONGEST_DURATION(null) */,
    /*public static final*/ SHORTEST_DURATION /* = new SHORTEST_DURATION(null) */,
    /*public static final*/ CHANNEL_NAME /* = new CHANNEL_NAME(null) */,
    /*public static final*/ DOWNLOADED /* = new DOWNLOADED(null) */,
    /*public static final*/ DOWNLOADING /* = new DOWNLOADING(null) */,
    /*public static final*/ PAUSED /* = new PAUSED(null) */;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String label = null;
    
    LibrarySortOrder(java.lang.String label) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getLabel() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public static kotlin.enums.EnumEntries<com.trixsearch.hasikit.ui.screens.library.LibrarySortOrder> getEntries() {
        return null;
    }
}