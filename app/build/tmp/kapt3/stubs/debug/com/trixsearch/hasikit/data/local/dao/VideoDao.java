package com.trixsearch.hasikit.data.local.dao;

import androidx.room.*;
import com.trixsearch.hasikit.data.local.entities.DownloadEntity;
import com.trixsearch.hasikit.data.local.entities.FavoriteEntity;
import com.trixsearch.hasikit.data.local.entities.VideoEntity;
import com.trixsearch.hasikit.data.local.entities.WatchHistoryEntity;
import com.trixsearch.hasikit.data.local.entities.WatchLaterEntity;
import com.trixsearch.hasikit.data.local.entities.WatchProgressEntity;
import kotlinx.coroutines.flow.Flow;

@kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000b\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\b\n\u0002\u0010\u0002\n\u0002\b\t\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\b\u0005\bg\u0018\u00002\u00020\u0001J\u0014\u0010\u0002\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\u00040\u0003H\'J\u0018\u0010\u0006\u001a\u0004\u0018\u00010\u00052\u0006\u0010\u0007\u001a\u00020\bH\u00a7@\u00a2\u0006\u0002\u0010\tJ\u0014\u0010\n\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\u00040\u0003H\'J\u0014\u0010\u000b\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\u00040\u0003H\'J\u0014\u0010\f\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\u00040\u0003H\'J\u0014\u0010\r\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\u00040\u0003H\'J\u0014\u0010\u000e\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\u00040\u0003H\'J\u0014\u0010\u000f\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\u00040\u0003H\'J\u0016\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0013J\u0016\u0010\u0014\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0013J\u0016\u0010\u0015\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0013J\u001c\u0010\u0016\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\u00040\u00032\u0006\u0010\u0017\u001a\u00020\bH\'J\u001c\u0010\u0018\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\u00040\u00032\u0006\u0010\u0017\u001a\u00020\bH\'J\u0014\u0010\u0019\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\u00040\u0003H\'J\u000e\u0010\u001a\u001a\u00020\u001bH\u00a7@\u00a2\u0006\u0002\u0010\u001cJ\u000e\u0010\u001d\u001a\u00020\u001eH\u00a7@\u00a2\u0006\u0002\u0010\u001cJ\u000e\u0010\u001f\u001a\u00020\u0011H\u00a7@\u00a2\u0006\u0002\u0010\u001cJ\u000e\u0010 \u001a\u00020\u0011H\u00a7@\u00a2\u0006\u0002\u0010\u001cJ\u000e\u0010!\u001a\u00020\u0011H\u00a7@\u00a2\u0006\u0002\u0010\u001cJ\u0018\u0010\"\u001a\u0004\u0018\u00010#2\u0006\u0010$\u001a\u00020\bH\u00a7@\u00a2\u0006\u0002\u0010\tJ\u0016\u0010%\u001a\u00020\u00112\u0006\u0010&\u001a\u00020#H\u00a7@\u00a2\u0006\u0002\u0010\'J\u0014\u0010(\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020#0\u00040\u0003H\'J\u0016\u0010)\u001a\u00020\u00112\u0006\u0010$\u001a\u00020\bH\u00a7@\u00a2\u0006\u0002\u0010\tJ\u0014\u0010*\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020+0\u00040\u0003H\'J\u0014\u0010,\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020+0\u00040\u0003H\'J\u0018\u0010-\u001a\u0004\u0018\u00010+2\u0006\u0010$\u001a\u00020\bH\u00a7@\u00a2\u0006\u0002\u0010\tJ\u0016\u0010.\u001a\u00020\u00112\u0006\u0010/\u001a\u00020+H\u00a7@\u00a2\u0006\u0002\u00100J\u0016\u00101\u001a\u00020\u00112\u0006\u0010$\u001a\u00020\bH\u00a7@\u00a2\u0006\u0002\u0010\tJ\u0014\u00102\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\u00040\u0003H\'J\u0014\u00103\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u0002040\u00040\u0003H\'J\u0016\u00105\u001a\u00020\u001b2\u0006\u0010$\u001a\u00020\bH\u00a7@\u00a2\u0006\u0002\u0010\tJ\u0016\u00106\u001a\u00020\u00112\u0006\u00107\u001a\u000204H\u00a7@\u00a2\u0006\u0002\u00108J\u0016\u00109\u001a\u00020\u00112\u0006\u0010$\u001a\u00020\bH\u00a7@\u00a2\u0006\u0002\u0010\tJ\u000e\u0010:\u001a\u00020\u0011H\u00a7@\u00a2\u0006\u0002\u0010\u001cJ\u0014\u0010;\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\u00040\u0003H\'J\u0014\u0010<\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020=0\u00040\u0003H\'J\u0016\u0010>\u001a\u00020\u001b2\u0006\u0010$\u001a\u00020\bH\u00a7@\u00a2\u0006\u0002\u0010\tJ\u0016\u0010?\u001a\u00020\u00112\u0006\u0010@\u001a\u00020=H\u00a7@\u00a2\u0006\u0002\u0010AJ\u0016\u0010B\u001a\u00020\u00112\u0006\u0010$\u001a\u00020\bH\u00a7@\u00a2\u0006\u0002\u0010\tJ\u000e\u0010C\u001a\u00020\u0011H\u00a7@\u00a2\u0006\u0002\u0010\u001cJ\u0014\u0010D\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\u00040\u0003H\'J\u0014\u0010E\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020F0\u00040\u0003H\'J\u0016\u0010G\u001a\u00020\u00112\u0006\u0010@\u001a\u00020FH\u00a7@\u00a2\u0006\u0002\u0010HJ\u0016\u0010I\u001a\u00020\u00112\u0006\u0010$\u001a\u00020\bH\u00a7@\u00a2\u0006\u0002\u0010\tJ\u000e\u0010J\u001a\u00020\u0011H\u00a7@\u00a2\u0006\u0002\u0010\u001c\u00a8\u0006K"}, d2 = {"Lcom/trixsearch/hasikit/data/local/dao/VideoDao;", "", "getAllVideos", "Lkotlinx/coroutines/flow/Flow;", "", "Lcom/trixsearch/hasikit/data/local/entities/VideoEntity;", "getVideoById", "id", "", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getDownloadedVideos", "downloadedByNameAsc", "downloadedByDateDesc", "downloadedBySizeDesc", "downloadedByDurationDesc", "downloadedByChannel", "insertVideo", "", "video", "(Lcom/trixsearch/hasikit/data/local/entities/VideoEntity;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "updateVideo", "deleteVideo", "searchVideos", "query", "searchDownloadedVideos", "getVideosWithProgress", "countDownloadedVideos", "", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "totalDownloadedSize", "", "deleteAllDownloads", "deleteAllVideos", "deleteAllWatchProgress", "getWatchProgress", "Lcom/trixsearch/hasikit/data/local/entities/WatchProgressEntity;", "videoId", "saveWatchProgress", "progress", "(Lcom/trixsearch/hasikit/data/local/entities/WatchProgressEntity;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getAllWatchProgress", "deleteWatchProgress", "getAllDownloads", "Lcom/trixsearch/hasikit/data/local/entities/DownloadEntity;", "getActiveDownloads", "getDownload", "saveDownload", "download", "(Lcom/trixsearch/hasikit/data/local/entities/DownloadEntity;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deleteDownload", "getFavoriteVideos", "getAllFavorites", "Lcom/trixsearch/hasikit/data/local/entities/FavoriteEntity;", "isFavorite", "addFavorite", "favorite", "(Lcom/trixsearch/hasikit/data/local/entities/FavoriteEntity;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "removeFavorite", "deleteAllFavorites", "getWatchLaterVideos", "getAllWatchLater", "Lcom/trixsearch/hasikit/data/local/entities/WatchLaterEntity;", "isInWatchLater", "addToWatchLater", "item", "(Lcom/trixsearch/hasikit/data/local/entities/WatchLaterEntity;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "removeFromWatchLater", "deleteAllWatchLater", "getHistoryVideos", "getAllWatchHistory", "Lcom/trixsearch/hasikit/data/local/entities/WatchHistoryEntity;", "addToWatchHistory", "(Lcom/trixsearch/hasikit/data/local/entities/WatchHistoryEntity;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "removeFromWatchHistory", "deleteAllWatchHistory", "app_debug"})
@androidx.room.Dao()
public abstract interface VideoDao {
    
    @androidx.room.Query(value = "SELECT * FROM videos ORDER BY uploadDate DESC, id DESC")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.trixsearch.hasikit.data.local.entities.VideoEntity>> getAllVideos();
    
    @androidx.room.Query(value = "SELECT * FROM videos WHERE id = :id LIMIT 1")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getVideoById(@org.jetbrains.annotations.NotNull()
    java.lang.String id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.trixsearch.hasikit.data.local.entities.VideoEntity> $completion);
    
    @androidx.room.Query(value = "SELECT * FROM videos WHERE isDownloaded = 1 ORDER BY title ASC")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.trixsearch.hasikit.data.local.entities.VideoEntity>> getDownloadedVideos();
    
    @androidx.room.Query(value = "SELECT * FROM videos WHERE isDownloaded = 1 ORDER BY title ASC")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.trixsearch.hasikit.data.local.entities.VideoEntity>> downloadedByNameAsc();
    
    @androidx.room.Query(value = "SELECT * FROM videos WHERE isDownloaded = 1 ORDER BY uploadDate DESC")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.trixsearch.hasikit.data.local.entities.VideoEntity>> downloadedByDateDesc();
    
    @androidx.room.Query(value = "SELECT * FROM videos WHERE isDownloaded = 1 ORDER BY size DESC")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.trixsearch.hasikit.data.local.entities.VideoEntity>> downloadedBySizeDesc();
    
    @androidx.room.Query(value = "SELECT * FROM videos WHERE isDownloaded = 1 ORDER BY duration DESC")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.trixsearch.hasikit.data.local.entities.VideoEntity>> downloadedByDurationDesc();
    
    @androidx.room.Query(value = "SELECT * FROM videos WHERE isDownloaded = 1 ORDER BY sourceLabel ASC, title ASC")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.trixsearch.hasikit.data.local.entities.VideoEntity>> downloadedByChannel();
    
    @androidx.room.Insert(onConflict = 1)
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insertVideo(@org.jetbrains.annotations.NotNull()
    com.trixsearch.hasikit.data.local.entities.VideoEntity video, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Update()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object updateVideo(@org.jetbrains.annotations.NotNull()
    com.trixsearch.hasikit.data.local.entities.VideoEntity video, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Delete()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteVideo(@org.jetbrains.annotations.NotNull()
    com.trixsearch.hasikit.data.local.entities.VideoEntity video, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "\n        SELECT * FROM videos\n        WHERE title LIKE \'%\' || :query || \'%\'\n           OR sourceLabel LIKE \'%\' || :query || \'%\'\n        ORDER BY uploadDate DESC\n    ")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.trixsearch.hasikit.data.local.entities.VideoEntity>> searchVideos(@org.jetbrains.annotations.NotNull()
    java.lang.String query);
    
    @androidx.room.Query(value = "\n        SELECT * FROM videos\n        WHERE isDownloaded = 1\n          AND (title LIKE \'%\' || :query || \'%\' OR sourceLabel LIKE \'%\' || :query || \'%\')\n        ORDER BY title ASC\n    ")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.trixsearch.hasikit.data.local.entities.VideoEntity>> searchDownloadedVideos(@org.jetbrains.annotations.NotNull()
    java.lang.String query);
    
    @androidx.room.Query(value = "\n        SELECT v.* FROM videos v\n        INNER JOIN watch_progress wp ON v.id = wp.videoId\n        WHERE wp.lastPosition > 0\n        ORDER BY wp.lastWatchedAt DESC\n        LIMIT 10\n    ")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.trixsearch.hasikit.data.local.entities.VideoEntity>> getVideosWithProgress();
    
    @androidx.room.Query(value = "SELECT COUNT(*) FROM videos WHERE isDownloaded = 1")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object countDownloadedVideos(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Integer> $completion);
    
    @androidx.room.Query(value = "SELECT COALESCE(SUM(size), 0) FROM videos WHERE isDownloaded = 1")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object totalDownloadedSize(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Long> $completion);
    
    @androidx.room.Query(value = "DELETE FROM downloads")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteAllDownloads(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "DELETE FROM videos")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteAllVideos(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "DELETE FROM watch_progress")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteAllWatchProgress(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "SELECT * FROM watch_progress WHERE videoId = :videoId")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getWatchProgress(@org.jetbrains.annotations.NotNull()
    java.lang.String videoId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.trixsearch.hasikit.data.local.entities.WatchProgressEntity> $completion);
    
    @androidx.room.Insert(onConflict = 1)
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object saveWatchProgress(@org.jetbrains.annotations.NotNull()
    com.trixsearch.hasikit.data.local.entities.WatchProgressEntity progress, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "SELECT * FROM watch_progress ORDER BY lastWatchedAt DESC")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.trixsearch.hasikit.data.local.entities.WatchProgressEntity>> getAllWatchProgress();
    
    @androidx.room.Query(value = "DELETE FROM watch_progress WHERE videoId = :videoId")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteWatchProgress(@org.jetbrains.annotations.NotNull()
    java.lang.String videoId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "SELECT * FROM downloads")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.trixsearch.hasikit.data.local.entities.DownloadEntity>> getAllDownloads();
    
    @androidx.room.Query(value = "SELECT * FROM downloads WHERE state IN (\'DOWNLOADING\', \'QUEUED\', \'PAUSED\')")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.trixsearch.hasikit.data.local.entities.DownloadEntity>> getActiveDownloads();
    
    @androidx.room.Query(value = "SELECT * FROM downloads WHERE videoId = :videoId")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getDownload(@org.jetbrains.annotations.NotNull()
    java.lang.String videoId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.trixsearch.hasikit.data.local.entities.DownloadEntity> $completion);
    
    @androidx.room.Insert(onConflict = 1)
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object saveDownload(@org.jetbrains.annotations.NotNull()
    com.trixsearch.hasikit.data.local.entities.DownloadEntity download, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "DELETE FROM downloads WHERE videoId = :videoId")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteDownload(@org.jetbrains.annotations.NotNull()
    java.lang.String videoId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "\n        SELECT v.* FROM videos v\n        INNER JOIN favorites f ON v.id = f.videoId\n        ORDER BY f.addedAt DESC\n    ")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.trixsearch.hasikit.data.local.entities.VideoEntity>> getFavoriteVideos();
    
    @androidx.room.Query(value = "SELECT * FROM favorites ORDER BY addedAt DESC")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.trixsearch.hasikit.data.local.entities.FavoriteEntity>> getAllFavorites();
    
    @androidx.room.Query(value = "SELECT COUNT(*) FROM favorites WHERE videoId = :videoId")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object isFavorite(@org.jetbrains.annotations.NotNull()
    java.lang.String videoId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Integer> $completion);
    
    @androidx.room.Insert(onConflict = 1)
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object addFavorite(@org.jetbrains.annotations.NotNull()
    com.trixsearch.hasikit.data.local.entities.FavoriteEntity favorite, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "DELETE FROM favorites WHERE videoId = :videoId")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object removeFavorite(@org.jetbrains.annotations.NotNull()
    java.lang.String videoId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "DELETE FROM favorites")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteAllFavorites(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "\n        SELECT v.* FROM videos v\n        INNER JOIN watch_later wl ON v.id = wl.videoId\n        ORDER BY wl.addedAt DESC\n    ")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.trixsearch.hasikit.data.local.entities.VideoEntity>> getWatchLaterVideos();
    
    @androidx.room.Query(value = "SELECT * FROM watch_later ORDER BY addedAt DESC")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.trixsearch.hasikit.data.local.entities.WatchLaterEntity>> getAllWatchLater();
    
    @androidx.room.Query(value = "SELECT COUNT(*) FROM watch_later WHERE videoId = :videoId")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object isInWatchLater(@org.jetbrains.annotations.NotNull()
    java.lang.String videoId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Integer> $completion);
    
    @androidx.room.Insert(onConflict = 1)
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object addToWatchLater(@org.jetbrains.annotations.NotNull()
    com.trixsearch.hasikit.data.local.entities.WatchLaterEntity item, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "DELETE FROM watch_later WHERE videoId = :videoId")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object removeFromWatchLater(@org.jetbrains.annotations.NotNull()
    java.lang.String videoId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "DELETE FROM watch_later")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteAllWatchLater(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "\n        SELECT v.* FROM videos v\n        INNER JOIN watch_history wh ON v.id = wh.videoId\n        ORDER BY wh.watchedAt DESC\n    ")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.trixsearch.hasikit.data.local.entities.VideoEntity>> getHistoryVideos();
    
    @androidx.room.Query(value = "SELECT * FROM watch_history ORDER BY watchedAt DESC")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.trixsearch.hasikit.data.local.entities.WatchHistoryEntity>> getAllWatchHistory();
    
    @androidx.room.Insert(onConflict = 1)
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object addToWatchHistory(@org.jetbrains.annotations.NotNull()
    com.trixsearch.hasikit.data.local.entities.WatchHistoryEntity item, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "DELETE FROM watch_history WHERE videoId = :videoId")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object removeFromWatchHistory(@org.jetbrains.annotations.NotNull()
    java.lang.String videoId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "DELETE FROM watch_history")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteAllWatchHistory(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
}