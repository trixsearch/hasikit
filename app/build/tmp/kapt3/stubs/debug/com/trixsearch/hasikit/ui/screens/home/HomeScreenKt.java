package com.trixsearch.hasikit.ui.screens.home;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import androidx.compose.foundation.layout.*;
import androidx.compose.foundation.lazy.LazyListState;
import androidx.compose.material.icons.Icons;
import androidx.compose.material.icons.filled.*;
import androidx.compose.material3.*;
import androidx.compose.runtime.*;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.graphics.Brush;
import androidx.compose.ui.layout.ContentScale;
import androidx.compose.ui.text.font.FontWeight;
import androidx.compose.ui.text.style.TextOverflow;
import androidx.compose.ui.tooling.preview.Preview;
import androidx.navigation.NavController;
import com.trixsearch.hasikit.domain.model.DownloadState;
import com.trixsearch.hasikit.domain.model.Video;
import com.trixsearch.hasikit.domain.model.WatchProgress;
import com.trixsearch.hasikit.domain.repository.VideoRepository;
import com.trixsearch.hasikit.ui.navigation.Screen;

@kotlin.Metadata(mv = {2, 1, 0}, k = 2, xi = 48, d1 = {"\u0000h\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\u0006\n\u0002\u0010\u0007\n\u0002\b\n\u001a\"\u0010\u0000\u001a\u00020\u0001*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u00042\f\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00010\u0006H\u0003\u001a\u001a\u0010\u0007\u001a\u00020\u00012\u0006\u0010\b\u001a\u00020\t2\b\b\u0002\u0010\n\u001a\u00020\u000bH\u0007\u001a\u0010\u0010\f\u001a\u00020\u00012\u0006\u0010\r\u001a\u00020\u000eH\u0003\u001a\u001e\u0010\u000f\u001a\u00020\u00012\u0006\u0010\u0010\u001a\u00020\u00112\f\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00010\u0006H\u0007\u001a&\u0010\u0013\u001a\u00020\u00012\u0006\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0014\u001a\u00020\u00152\f\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00010\u0006H\u0007\u001a\u001e\u0010\u0016\u001a\u00020\u00012\u0006\u0010\u0010\u001a\u00020\u00112\f\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00010\u0006H\u0007\u001a\u00ca\u0001\u0010\u0017\u001a\u00020\u00012\u0006\u0010\u0010\u001a\u00020\u00112\f\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00010\u00062\f\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\u00010\u00062\u0010\b\u0002\u0010\u0019\u001a\n\u0012\u0004\u0012\u00020\u0001\u0018\u00010\u00062\u0010\b\u0002\u0010\u001a\u001a\n\u0012\u0004\u0012\u00020\u0001\u0018\u00010\u00062\u0010\b\u0002\u0010\u001b\u001a\n\u0012\u0004\u0012\u00020\u0001\u0018\u00010\u00062\n\b\u0002\u0010\u001c\u001a\u0004\u0018\u00010\u001d2\u0010\b\u0002\u0010\u001e\u001a\n\u0012\u0004\u0012\u00020\u0001\u0018\u00010\u00062\u0010\b\u0002\u0010\u001f\u001a\n\u0012\u0004\u0012\u00020\u0001\u0018\u00010\u00062\u0010\b\u0002\u0010 \u001a\n\u0012\u0004\u0012\u00020\u0001\u0018\u00010\u00062\u0010\b\u0002\u0010!\u001a\n\u0012\u0004\u0012\u00020\u0001\u0018\u00010\u00062\b\b\u0002\u0010\"\u001a\u00020#2\b\b\u0002\u0010$\u001a\u00020#H\u0007\u001a(\u0010%\u001a\u00020\u00012\b\u0010&\u001a\u0004\u0018\u00010\u000e2\n\b\u0002\u0010\'\u001a\u0004\u0018\u00010\u000e2\b\b\u0002\u0010(\u001a\u00020)H\u0007\u001a\u0010\u0010*\u001a\u00020\u000e2\u0006\u0010+\u001a\u00020,H\u0002\u001a\u0010\u0010-\u001a\u00020\u000e2\u0006\u0010.\u001a\u00020,H\u0002\u001a:\u0010/\u001a\u00020\u00112\b\b\u0002\u00100\u001a\u00020\u000e2\b\b\u0002\u0010\r\u001a\u00020\u000e2\b\b\u0002\u00101\u001a\u00020#2\b\b\u0002\u00102\u001a\u0002032\b\b\u0002\u00104\u001a\u00020\u000eH\u0002\u001a\u001c\u00105\u001a\u00020\u00152\b\b\u0002\u00106\u001a\u00020\u000e2\b\b\u0002\u00107\u001a\u000203H\u0002\u001a\b\u00108\u001a\u00020\u0001H\u0003\u001a\b\u00109\u001a\u00020\u0001H\u0003\u001a\b\u0010:\u001a\u00020\u0001H\u0003\u001a\b\u0010;\u001a\u00020\u0001H\u0003\u001a\b\u0010<\u001a\u00020\u0001H\u0003\u00a8\u0006="}, d2 = {"OnNearBottom", "", "Landroidx/compose/foundation/lazy/LazyListState;", "threshold", "", "onNearBottom", "Lkotlin/Function0;", "HomeScreen", "navController", "Landroidx/navigation/NavController;", "viewModel", "Lcom/trixsearch/hasikit/ui/screens/home/HomeViewModel;", "SectionHeader", "title", "", "RecentCard", "video", "Lcom/trixsearch/hasikit/domain/model/Video;", "onClick", "ContinueWatchingCard", "progress", "Lcom/trixsearch/hasikit/domain/model/WatchProgress;", "DownloadedCard", "HorizontalVideoCard", "onDownloadClick", "onPauseDownload", "onResumeDownload", "onDeleteDownload", "downloadState", "Lcom/trixsearch/hasikit/domain/model/DownloadState;", "onAddFavorite", "onRemoveFavorite", "onAddWatchLater", "onRemoveWatchLater", "isFavorite", "", "isWatchLater", "VideoThumbnail", "url", "localVideoPath", "modifier", "Landroidx/compose/ui/Modifier;", "formatBytes", "bytes", "", "formatTime", "ms", "previewVideo", "id", "isDownloaded", "downloadProgress", "", "sourceLabel", "previewProgress", "videoId", "pct", "PreviewHorizontalVideoCard", "PreviewContinueWatchingCard", "PreviewRecentCard", "PreviewDownloadedCard", "PreviewHomeContent", "app_debug"})
public final class HomeScreenKt {
    
    @androidx.compose.runtime.Composable()
    private static final void OnNearBottom(androidx.compose.foundation.lazy.LazyListState $this$OnNearBottom, int threshold, kotlin.jvm.functions.Function0<kotlin.Unit> onNearBottom) {
    }
    
    @kotlin.OptIn(markerClass = {androidx.compose.material3.ExperimentalMaterial3Api.class})
    @androidx.compose.runtime.Composable()
    public static final void HomeScreen(@org.jetbrains.annotations.NotNull()
    androidx.navigation.NavController navController, @org.jetbrains.annotations.NotNull()
    com.trixsearch.hasikit.ui.screens.home.HomeViewModel viewModel) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void SectionHeader(java.lang.String title) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void RecentCard(@org.jetbrains.annotations.NotNull()
    com.trixsearch.hasikit.domain.model.Video video, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onClick) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void ContinueWatchingCard(@org.jetbrains.annotations.NotNull()
    com.trixsearch.hasikit.domain.model.Video video, @org.jetbrains.annotations.NotNull()
    com.trixsearch.hasikit.domain.model.WatchProgress progress, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onClick) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void DownloadedCard(@org.jetbrains.annotations.NotNull()
    com.trixsearch.hasikit.domain.model.Video video, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onClick) {
    }
    
    @kotlin.OptIn(markerClass = {androidx.compose.foundation.ExperimentalFoundationApi.class})
    @androidx.compose.runtime.Composable()
    public static final void HorizontalVideoCard(@org.jetbrains.annotations.NotNull()
    com.trixsearch.hasikit.domain.model.Video video, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onClick, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onDownloadClick, @org.jetbrains.annotations.Nullable()
    kotlin.jvm.functions.Function0<kotlin.Unit> onPauseDownload, @org.jetbrains.annotations.Nullable()
    kotlin.jvm.functions.Function0<kotlin.Unit> onResumeDownload, @org.jetbrains.annotations.Nullable()
    kotlin.jvm.functions.Function0<kotlin.Unit> onDeleteDownload, @org.jetbrains.annotations.Nullable()
    com.trixsearch.hasikit.domain.model.DownloadState downloadState, @org.jetbrains.annotations.Nullable()
    kotlin.jvm.functions.Function0<kotlin.Unit> onAddFavorite, @org.jetbrains.annotations.Nullable()
    kotlin.jvm.functions.Function0<kotlin.Unit> onRemoveFavorite, @org.jetbrains.annotations.Nullable()
    kotlin.jvm.functions.Function0<kotlin.Unit> onAddWatchLater, @org.jetbrains.annotations.Nullable()
    kotlin.jvm.functions.Function0<kotlin.Unit> onRemoveWatchLater, boolean isFavorite, boolean isWatchLater) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void VideoThumbnail(@org.jetbrains.annotations.Nullable()
    java.lang.String url, @org.jetbrains.annotations.Nullable()
    java.lang.String localVideoPath, @org.jetbrains.annotations.NotNull()
    androidx.compose.ui.Modifier modifier) {
    }
    
    private static final java.lang.String formatBytes(long bytes) {
        return null;
    }
    
    private static final java.lang.String formatTime(long ms) {
        return null;
    }
    
    private static final com.trixsearch.hasikit.domain.model.Video previewVideo(java.lang.String id, java.lang.String title, boolean isDownloaded, float downloadProgress, java.lang.String sourceLabel) {
        return null;
    }
    
    private static final com.trixsearch.hasikit.domain.model.WatchProgress previewProgress(java.lang.String videoId, float pct) {
        return null;
    }
    
    @androidx.compose.ui.tooling.preview.Preview(name = "HorizontalVideoCard", showBackground = true, backgroundColor = 4279374354L)
    @androidx.compose.runtime.Composable()
    private static final void PreviewHorizontalVideoCard() {
    }
    
    @androidx.compose.ui.tooling.preview.Preview(name = "ContinueWatchingCard", showBackground = true, backgroundColor = 4279374354L)
    @androidx.compose.runtime.Composable()
    private static final void PreviewContinueWatchingCard() {
    }
    
    @androidx.compose.ui.tooling.preview.Preview(name = "RecentCard", showBackground = true, backgroundColor = 4279374354L)
    @androidx.compose.runtime.Composable()
    private static final void PreviewRecentCard() {
    }
    
    @androidx.compose.ui.tooling.preview.Preview(name = "DownloadedCard", showBackground = true, backgroundColor = 4279374354L)
    @androidx.compose.runtime.Composable()
    private static final void PreviewDownloadedCard() {
    }
    
    @androidx.compose.ui.tooling.preview.Preview(name = "Home Content \u2013 Dark", showBackground = true, backgroundColor = 4278190080L, widthDp = 400, heightDp = 800)
    @androidx.compose.runtime.Composable()
    private static final void PreviewHomeContent() {
    }
}