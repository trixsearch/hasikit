package com.trixsearch.hasikit.domain.repository;

import com.trixsearch.hasikit.data.local.entities.FavoriteEntity;
import com.trixsearch.hasikit.data.local.entities.VideoEntity;
import com.trixsearch.hasikit.data.local.entities.WatchHistoryEntity;
import com.trixsearch.hasikit.data.local.entities.WatchLaterEntity;
import com.trixsearch.hasikit.domain.model.DownloadTask;
import com.trixsearch.hasikit.domain.model.Video;
import com.trixsearch.hasikit.domain.model.WatchProgress;
import kotlinx.coroutines.flow.Flow;

@kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000f\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u000e\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0004\bf\u0018\u00002\u00020\u0001J\u0014\u0010\u0002\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\u00040\u0003H&J\u0014\u0010\u0006\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\u00040\u0003H&J\u0018\u0010\u0007\u001a\u0004\u0018\u00010\u00052\u0006\u0010\b\u001a\u00020\tH\u00a6@\u00a2\u0006\u0002\u0010\nJ\u0016\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u0005H\u00a6@\u00a2\u0006\u0002\u0010\u000eJ\u0016\u0010\u000f\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u0005H\u00a6@\u00a2\u0006\u0002\u0010\u000eJ\u0016\u0010\u0010\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u0005H\u00a6@\u00a2\u0006\u0002\u0010\u000eJ\u001c\u0010\u0011\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\u00040\u00032\u0006\u0010\u0012\u001a\u00020\tH&J\u001c\u0010\u0013\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\u00040\u00032\u0006\u0010\u0012\u001a\u00020\tH&J\u0014\u0010\u0014\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\u00040\u0003H&J\u0014\u0010\u0015\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\u00040\u0003H&J\u0014\u0010\u0016\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\u00040\u0003H&J\u0014\u0010\u0017\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\u00040\u0003H&J\u0014\u0010\u0018\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\u00040\u0003H&J\u0014\u0010\u0019\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\u00040\u0003H&J\u000e\u0010\u001a\u001a\u00020\u001bH\u00a6@\u00a2\u0006\u0002\u0010\u001cJ\u000e\u0010\u001d\u001a\u00020\u001eH\u00a6@\u00a2\u0006\u0002\u0010\u001cJ\u0018\u0010\u001f\u001a\u0004\u0018\u00010 2\u0006\u0010!\u001a\u00020\tH\u00a6@\u00a2\u0006\u0002\u0010\nJ\u0016\u0010\"\u001a\u00020\f2\u0006\u0010#\u001a\u00020 H\u00a6@\u00a2\u0006\u0002\u0010$J\u0014\u0010%\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020 0\u00040\u0003H&J\u0016\u0010&\u001a\u00020\f2\u0006\u0010!\u001a\u00020\tH\u00a6@\u00a2\u0006\u0002\u0010\nJ\u0014\u0010\'\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020(0\u00040\u0003H&J\u0014\u0010)\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020(0\u00040\u0003H&J\u0018\u0010*\u001a\u0004\u0018\u00010(2\u0006\u0010!\u001a\u00020\tH\u00a6@\u00a2\u0006\u0002\u0010\nJ\u0016\u0010+\u001a\u00020\f2\u0006\u0010,\u001a\u00020(H\u00a6@\u00a2\u0006\u0002\u0010-J\u0016\u0010.\u001a\u00020\f2\u0006\u0010!\u001a\u00020\tH\u00a6@\u00a2\u0006\u0002\u0010\nJ\u000e\u0010/\u001a\u00020\fH\u00a6@\u00a2\u0006\u0002\u0010\u001cJ\u0014\u00100\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\u00040\u0003H&J\u0014\u00101\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u0002020\u00040\u0003H&J\u0016\u00103\u001a\u0002042\u0006\u0010!\u001a\u00020\tH\u00a6@\u00a2\u0006\u0002\u0010\nJ\u0016\u00105\u001a\u00020\f2\u0006\u00106\u001a\u000202H\u00a6@\u00a2\u0006\u0002\u00107J\u0016\u00108\u001a\u00020\f2\u0006\u0010!\u001a\u00020\tH\u00a6@\u00a2\u0006\u0002\u0010\nJ\u0014\u00109\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\u00040\u0003H&J\u0014\u0010:\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020;0\u00040\u0003H&J\u0016\u0010<\u001a\u0002042\u0006\u0010!\u001a\u00020\tH\u00a6@\u00a2\u0006\u0002\u0010\nJ\u0016\u0010=\u001a\u00020\f2\u0006\u0010>\u001a\u00020;H\u00a6@\u00a2\u0006\u0002\u0010?J\u0016\u0010@\u001a\u00020\f2\u0006\u0010!\u001a\u00020\tH\u00a6@\u00a2\u0006\u0002\u0010\nJ\u0014\u0010A\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\u00040\u0003H&J\u0014\u0010B\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020C0\u00040\u0003H&J\u0016\u0010D\u001a\u00020\f2\u0006\u0010>\u001a\u00020CH\u00a6@\u00a2\u0006\u0002\u0010EJ\u0016\u0010F\u001a\u00020\f2\u0006\u0010!\u001a\u00020\tH\u00a6@\u00a2\u0006\u0002\u0010\n\u00a8\u0006G"}, d2 = {"Lcom/trixsearch/hasikit/domain/repository/VideoRepository;", "", "getAllVideos", "Lkotlinx/coroutines/flow/Flow;", "", "Lcom/trixsearch/hasikit/domain/model/Video;", "getDownloadedVideos", "getVideoById", "id", "", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "insertVideo", "", "video", "(Lcom/trixsearch/hasikit/domain/model/Video;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "updateVideo", "deleteVideo", "searchVideos", "query", "searchDownloadedVideos", "downloadedByNameAsc", "downloadedByDateDesc", "downloadedBySizeDesc", "downloadedByDurationDesc", "downloadedByChannel", "getVideosWithProgress", "countDownloadedVideos", "", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "totalDownloadedSize", "", "getWatchProgress", "Lcom/trixsearch/hasikit/domain/model/WatchProgress;", "videoId", "saveWatchProgress", "progress", "(Lcom/trixsearch/hasikit/domain/model/WatchProgress;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getAllWatchProgress", "deleteWatchProgress", "getAllDownloads", "Lcom/trixsearch/hasikit/domain/model/DownloadTask;", "getActiveDownloads", "getDownload", "saveDownload", "download", "(Lcom/trixsearch/hasikit/domain/model/DownloadTask;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deleteDownload", "clearAllStorage", "getFavoriteVideos", "getAllFavorites", "Lcom/trixsearch/hasikit/data/local/entities/FavoriteEntity;", "isFavorite", "", "addFavorite", "favorite", "(Lcom/trixsearch/hasikit/data/local/entities/FavoriteEntity;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "removeFavorite", "getWatchLaterVideos", "getAllWatchLater", "Lcom/trixsearch/hasikit/data/local/entities/WatchLaterEntity;", "isInWatchLater", "addToWatchLater", "item", "(Lcom/trixsearch/hasikit/data/local/entities/WatchLaterEntity;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "removeFromWatchLater", "getHistoryVideos", "getAllWatchHistory", "Lcom/trixsearch/hasikit/data/local/entities/WatchHistoryEntity;", "addToWatchHistory", "(Lcom/trixsearch/hasikit/data/local/entities/WatchHistoryEntity;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "removeFromWatchHistory", "app_debug"})
public abstract interface VideoRepository {
    
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.trixsearch.hasikit.domain.model.Video>> getAllVideos();
    
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.trixsearch.hasikit.domain.model.Video>> getDownloadedVideos();
    
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getVideoById(@org.jetbrains.annotations.NotNull()
    java.lang.String id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.trixsearch.hasikit.domain.model.Video> $completion);
    
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insertVideo(@org.jetbrains.annotations.NotNull()
    com.trixsearch.hasikit.domain.model.Video video, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object updateVideo(@org.jetbrains.annotations.NotNull()
    com.trixsearch.hasikit.domain.model.Video video, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteVideo(@org.jetbrains.annotations.NotNull()
    com.trixsearch.hasikit.domain.model.Video video, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.trixsearch.hasikit.domain.model.Video>> searchVideos(@org.jetbrains.annotations.NotNull()
    java.lang.String query);
    
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.trixsearch.hasikit.domain.model.Video>> searchDownloadedVideos(@org.jetbrains.annotations.NotNull()
    java.lang.String query);
    
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.trixsearch.hasikit.domain.model.Video>> downloadedByNameAsc();
    
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.trixsearch.hasikit.domain.model.Video>> downloadedByDateDesc();
    
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.trixsearch.hasikit.domain.model.Video>> downloadedBySizeDesc();
    
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.trixsearch.hasikit.domain.model.Video>> downloadedByDurationDesc();
    
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.trixsearch.hasikit.domain.model.Video>> downloadedByChannel();
    
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.trixsearch.hasikit.domain.model.Video>> getVideosWithProgress();
    
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object countDownloadedVideos(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Integer> $completion);
    
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object totalDownloadedSize(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Long> $completion);
    
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getWatchProgress(@org.jetbrains.annotations.NotNull()
    java.lang.String videoId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.trixsearch.hasikit.domain.model.WatchProgress> $completion);
    
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object saveWatchProgress(@org.jetbrains.annotations.NotNull()
    com.trixsearch.hasikit.domain.model.WatchProgress progress, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.trixsearch.hasikit.domain.model.WatchProgress>> getAllWatchProgress();
    
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteWatchProgress(@org.jetbrains.annotations.NotNull()
    java.lang.String videoId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.trixsearch.hasikit.domain.model.DownloadTask>> getAllDownloads();
    
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.trixsearch.hasikit.domain.model.DownloadTask>> getActiveDownloads();
    
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getDownload(@org.jetbrains.annotations.NotNull()
    java.lang.String videoId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.trixsearch.hasikit.domain.model.DownloadTask> $completion);
    
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object saveDownload(@org.jetbrains.annotations.NotNull()
    com.trixsearch.hasikit.domain.model.DownloadTask download, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteDownload(@org.jetbrains.annotations.NotNull()
    java.lang.String videoId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object clearAllStorage(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.trixsearch.hasikit.domain.model.Video>> getFavoriteVideos();
    
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.trixsearch.hasikit.data.local.entities.FavoriteEntity>> getAllFavorites();
    
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object isFavorite(@org.jetbrains.annotations.NotNull()
    java.lang.String videoId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Boolean> $completion);
    
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object addFavorite(@org.jetbrains.annotations.NotNull()
    com.trixsearch.hasikit.data.local.entities.FavoriteEntity favorite, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object removeFavorite(@org.jetbrains.annotations.NotNull()
    java.lang.String videoId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.trixsearch.hasikit.domain.model.Video>> getWatchLaterVideos();
    
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.trixsearch.hasikit.data.local.entities.WatchLaterEntity>> getAllWatchLater();
    
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object isInWatchLater(@org.jetbrains.annotations.NotNull()
    java.lang.String videoId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Boolean> $completion);
    
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object addToWatchLater(@org.jetbrains.annotations.NotNull()
    com.trixsearch.hasikit.data.local.entities.WatchLaterEntity item, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object removeFromWatchLater(@org.jetbrains.annotations.NotNull()
    java.lang.String videoId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.trixsearch.hasikit.domain.model.Video>> getHistoryVideos();
    
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.trixsearch.hasikit.data.local.entities.WatchHistoryEntity>> getAllWatchHistory();
    
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object addToWatchHistory(@org.jetbrains.annotations.NotNull()
    com.trixsearch.hasikit.data.local.entities.WatchHistoryEntity item, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object removeFromWatchHistory(@org.jetbrains.annotations.NotNull()
    java.lang.String videoId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
}