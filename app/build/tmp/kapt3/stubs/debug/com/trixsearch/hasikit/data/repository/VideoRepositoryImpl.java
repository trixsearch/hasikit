package com.trixsearch.hasikit.data.repository;

import com.trixsearch.hasikit.data.local.dao.VideoDao;
import com.trixsearch.hasikit.data.local.entities.FavoriteEntity;
import com.trixsearch.hasikit.data.local.entities.VideoEntity;
import com.trixsearch.hasikit.data.local.entities.WatchHistoryEntity;
import com.trixsearch.hasikit.data.local.entities.WatchLaterEntity;
import com.trixsearch.hasikit.domain.model.DownloadTask;
import com.trixsearch.hasikit.domain.model.Video;
import com.trixsearch.hasikit.domain.model.WatchProgress;
import com.trixsearch.hasikit.domain.repository.VideoRepository;
import kotlinx.coroutines.flow.Flow;
import javax.inject.Inject;

@kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000n\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u000e\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0004\u0018\u00002\u00020\u0001B\u0011\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0004\b\u0004\u0010\u0005J\u0014\u0010\u0006\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\t0\b0\u0007H\u0016J\u0014\u0010\n\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\t0\b0\u0007H\u0016J\u0018\u0010\u000b\u001a\u0004\u0018\u00010\t2\u0006\u0010\f\u001a\u00020\rH\u0096@\u00a2\u0006\u0002\u0010\u000eJ\u0016\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\tH\u0096@\u00a2\u0006\u0002\u0010\u0012J\u0016\u0010\u0013\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\tH\u0096@\u00a2\u0006\u0002\u0010\u0012J\u0016\u0010\u0014\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\tH\u0096@\u00a2\u0006\u0002\u0010\u0012J\u001c\u0010\u0015\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\t0\b0\u00072\u0006\u0010\u0016\u001a\u00020\rH\u0016J\u001c\u0010\u0017\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\t0\b0\u00072\u0006\u0010\u0016\u001a\u00020\rH\u0016J\u0014\u0010\u0018\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\t0\b0\u0007H\u0016J\u0014\u0010\u0019\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\t0\b0\u0007H\u0016J\u0014\u0010\u001a\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\t0\b0\u0007H\u0016J\u0014\u0010\u001b\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\t0\b0\u0007H\u0016J\u0014\u0010\u001c\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\t0\b0\u0007H\u0016J\u0014\u0010\u001d\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\t0\b0\u0007H\u0016J\u000e\u0010\u001e\u001a\u00020\u001fH\u0096@\u00a2\u0006\u0002\u0010 J\u000e\u0010!\u001a\u00020\"H\u0096@\u00a2\u0006\u0002\u0010 J\u0018\u0010#\u001a\u0004\u0018\u00010$2\u0006\u0010%\u001a\u00020\rH\u0096@\u00a2\u0006\u0002\u0010\u000eJ\u0016\u0010&\u001a\u00020\u00102\u0006\u0010\'\u001a\u00020$H\u0096@\u00a2\u0006\u0002\u0010(J\u0014\u0010)\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020$0\b0\u0007H\u0016J\u0016\u0010*\u001a\u00020\u00102\u0006\u0010%\u001a\u00020\rH\u0096@\u00a2\u0006\u0002\u0010\u000eJ\u0014\u0010+\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020,0\b0\u0007H\u0016J\u0014\u0010-\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020,0\b0\u0007H\u0016J\u0018\u0010.\u001a\u0004\u0018\u00010,2\u0006\u0010%\u001a\u00020\rH\u0096@\u00a2\u0006\u0002\u0010\u000eJ\u0016\u0010/\u001a\u00020\u00102\u0006\u00100\u001a\u00020,H\u0096@\u00a2\u0006\u0002\u00101J\u0016\u00102\u001a\u00020\u00102\u0006\u0010%\u001a\u00020\rH\u0096@\u00a2\u0006\u0002\u0010\u000eJ\u000e\u00103\u001a\u00020\u0010H\u0096@\u00a2\u0006\u0002\u0010 J\u0014\u00104\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\t0\b0\u0007H\u0016J\u0014\u00105\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u0002060\b0\u0007H\u0016J\u0016\u00107\u001a\u0002082\u0006\u0010%\u001a\u00020\rH\u0096@\u00a2\u0006\u0002\u0010\u000eJ\u0016\u00109\u001a\u00020\u00102\u0006\u0010:\u001a\u000206H\u0096@\u00a2\u0006\u0002\u0010;J\u0016\u0010<\u001a\u00020\u00102\u0006\u0010%\u001a\u00020\rH\u0096@\u00a2\u0006\u0002\u0010\u000eJ\u0014\u0010=\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\t0\b0\u0007H\u0016J\u0014\u0010>\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020?0\b0\u0007H\u0016J\u0016\u0010@\u001a\u0002082\u0006\u0010%\u001a\u00020\rH\u0096@\u00a2\u0006\u0002\u0010\u000eJ\u0016\u0010A\u001a\u00020\u00102\u0006\u0010B\u001a\u00020?H\u0096@\u00a2\u0006\u0002\u0010CJ\u0016\u0010D\u001a\u00020\u00102\u0006\u0010%\u001a\u00020\rH\u0096@\u00a2\u0006\u0002\u0010\u000eJ\u0014\u0010E\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\t0\b0\u0007H\u0016J\u0014\u0010F\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020G0\b0\u0007H\u0016J\u0016\u0010H\u001a\u00020\u00102\u0006\u0010B\u001a\u00020GH\u0096@\u00a2\u0006\u0002\u0010IJ\u0016\u0010J\u001a\u00020\u00102\u0006\u0010%\u001a\u00020\rH\u0096@\u00a2\u0006\u0002\u0010\u000eR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006K"}, d2 = {"Lcom/trixsearch/hasikit/data/repository/VideoRepositoryImpl;", "Lcom/trixsearch/hasikit/domain/repository/VideoRepository;", "videoDao", "Lcom/trixsearch/hasikit/data/local/dao/VideoDao;", "<init>", "(Lcom/trixsearch/hasikit/data/local/dao/VideoDao;)V", "getAllVideos", "Lkotlinx/coroutines/flow/Flow;", "", "Lcom/trixsearch/hasikit/domain/model/Video;", "getDownloadedVideos", "getVideoById", "id", "", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "insertVideo", "", "video", "(Lcom/trixsearch/hasikit/domain/model/Video;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "updateVideo", "deleteVideo", "searchVideos", "query", "searchDownloadedVideos", "downloadedByNameAsc", "downloadedByDateDesc", "downloadedBySizeDesc", "downloadedByDurationDesc", "downloadedByChannel", "getVideosWithProgress", "countDownloadedVideos", "", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "totalDownloadedSize", "", "getWatchProgress", "Lcom/trixsearch/hasikit/domain/model/WatchProgress;", "videoId", "saveWatchProgress", "progress", "(Lcom/trixsearch/hasikit/domain/model/WatchProgress;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getAllWatchProgress", "deleteWatchProgress", "getAllDownloads", "Lcom/trixsearch/hasikit/domain/model/DownloadTask;", "getActiveDownloads", "getDownload", "saveDownload", "download", "(Lcom/trixsearch/hasikit/domain/model/DownloadTask;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deleteDownload", "clearAllStorage", "getFavoriteVideos", "getAllFavorites", "Lcom/trixsearch/hasikit/data/local/entities/FavoriteEntity;", "isFavorite", "", "addFavorite", "favorite", "(Lcom/trixsearch/hasikit/data/local/entities/FavoriteEntity;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "removeFavorite", "getWatchLaterVideos", "getAllWatchLater", "Lcom/trixsearch/hasikit/data/local/entities/WatchLaterEntity;", "isInWatchLater", "addToWatchLater", "item", "(Lcom/trixsearch/hasikit/data/local/entities/WatchLaterEntity;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "removeFromWatchLater", "getHistoryVideos", "getAllWatchHistory", "Lcom/trixsearch/hasikit/data/local/entities/WatchHistoryEntity;", "addToWatchHistory", "(Lcom/trixsearch/hasikit/data/local/entities/WatchHistoryEntity;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "removeFromWatchHistory", "app_debug"})
public final class VideoRepositoryImpl implements com.trixsearch.hasikit.domain.repository.VideoRepository {
    @org.jetbrains.annotations.NotNull()
    private final com.trixsearch.hasikit.data.local.dao.VideoDao videoDao = null;
    
    @javax.inject.Inject()
    public VideoRepositoryImpl(@org.jetbrains.annotations.NotNull()
    com.trixsearch.hasikit.data.local.dao.VideoDao videoDao) {
        super();
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public kotlinx.coroutines.flow.Flow<java.util.List<com.trixsearch.hasikit.domain.model.Video>> getAllVideos() {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public kotlinx.coroutines.flow.Flow<java.util.List<com.trixsearch.hasikit.domain.model.Video>> getDownloadedVideos() {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object getVideoById(@org.jetbrains.annotations.NotNull()
    java.lang.String id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.trixsearch.hasikit.domain.model.Video> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object insertVideo(@org.jetbrains.annotations.NotNull()
    com.trixsearch.hasikit.domain.model.Video video, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object updateVideo(@org.jetbrains.annotations.NotNull()
    com.trixsearch.hasikit.domain.model.Video video, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object deleteVideo(@org.jetbrains.annotations.NotNull()
    com.trixsearch.hasikit.domain.model.Video video, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public kotlinx.coroutines.flow.Flow<java.util.List<com.trixsearch.hasikit.domain.model.Video>> searchVideos(@org.jetbrains.annotations.NotNull()
    java.lang.String query) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public kotlinx.coroutines.flow.Flow<java.util.List<com.trixsearch.hasikit.domain.model.Video>> searchDownloadedVideos(@org.jetbrains.annotations.NotNull()
    java.lang.String query) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public kotlinx.coroutines.flow.Flow<java.util.List<com.trixsearch.hasikit.domain.model.Video>> downloadedByNameAsc() {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public kotlinx.coroutines.flow.Flow<java.util.List<com.trixsearch.hasikit.domain.model.Video>> downloadedByDateDesc() {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public kotlinx.coroutines.flow.Flow<java.util.List<com.trixsearch.hasikit.domain.model.Video>> downloadedBySizeDesc() {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public kotlinx.coroutines.flow.Flow<java.util.List<com.trixsearch.hasikit.domain.model.Video>> downloadedByDurationDesc() {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public kotlinx.coroutines.flow.Flow<java.util.List<com.trixsearch.hasikit.domain.model.Video>> downloadedByChannel() {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public kotlinx.coroutines.flow.Flow<java.util.List<com.trixsearch.hasikit.domain.model.Video>> getVideosWithProgress() {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object countDownloadedVideos(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Integer> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object totalDownloadedSize(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Long> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object getWatchProgress(@org.jetbrains.annotations.NotNull()
    java.lang.String videoId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.trixsearch.hasikit.domain.model.WatchProgress> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object saveWatchProgress(@org.jetbrains.annotations.NotNull()
    com.trixsearch.hasikit.domain.model.WatchProgress progress, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public kotlinx.coroutines.flow.Flow<java.util.List<com.trixsearch.hasikit.domain.model.WatchProgress>> getAllWatchProgress() {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object deleteWatchProgress(@org.jetbrains.annotations.NotNull()
    java.lang.String videoId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public kotlinx.coroutines.flow.Flow<java.util.List<com.trixsearch.hasikit.domain.model.DownloadTask>> getAllDownloads() {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public kotlinx.coroutines.flow.Flow<java.util.List<com.trixsearch.hasikit.domain.model.DownloadTask>> getActiveDownloads() {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object getDownload(@org.jetbrains.annotations.NotNull()
    java.lang.String videoId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.trixsearch.hasikit.domain.model.DownloadTask> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object saveDownload(@org.jetbrains.annotations.NotNull()
    com.trixsearch.hasikit.domain.model.DownloadTask download, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object deleteDownload(@org.jetbrains.annotations.NotNull()
    java.lang.String videoId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object clearAllStorage(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public kotlinx.coroutines.flow.Flow<java.util.List<com.trixsearch.hasikit.domain.model.Video>> getFavoriteVideos() {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public kotlinx.coroutines.flow.Flow<java.util.List<com.trixsearch.hasikit.data.local.entities.FavoriteEntity>> getAllFavorites() {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object isFavorite(@org.jetbrains.annotations.NotNull()
    java.lang.String videoId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Boolean> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object addFavorite(@org.jetbrains.annotations.NotNull()
    com.trixsearch.hasikit.data.local.entities.FavoriteEntity favorite, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object removeFavorite(@org.jetbrains.annotations.NotNull()
    java.lang.String videoId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public kotlinx.coroutines.flow.Flow<java.util.List<com.trixsearch.hasikit.domain.model.Video>> getWatchLaterVideos() {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public kotlinx.coroutines.flow.Flow<java.util.List<com.trixsearch.hasikit.data.local.entities.WatchLaterEntity>> getAllWatchLater() {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object isInWatchLater(@org.jetbrains.annotations.NotNull()
    java.lang.String videoId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Boolean> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object addToWatchLater(@org.jetbrains.annotations.NotNull()
    com.trixsearch.hasikit.data.local.entities.WatchLaterEntity item, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object removeFromWatchLater(@org.jetbrains.annotations.NotNull()
    java.lang.String videoId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public kotlinx.coroutines.flow.Flow<java.util.List<com.trixsearch.hasikit.domain.model.Video>> getHistoryVideos() {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public kotlinx.coroutines.flow.Flow<java.util.List<com.trixsearch.hasikit.data.local.entities.WatchHistoryEntity>> getAllWatchHistory() {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object addToWatchHistory(@org.jetbrains.annotations.NotNull()
    com.trixsearch.hasikit.data.local.entities.WatchHistoryEntity item, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object removeFromWatchHistory(@org.jetbrains.annotations.NotNull()
    java.lang.String videoId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
}