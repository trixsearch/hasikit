package com.trixsearch.hasikit.download;

import android.content.Context;
import android.util.Log;
import androidx.work.BackoffPolicy;
import androidx.work.Constraints;
import androidx.work.ExistingWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import com.trixsearch.hasikit.domain.model.DownloadState;
import com.trixsearch.hasikit.domain.model.DownloadTask;
import com.trixsearch.hasikit.domain.model.Video;
import com.trixsearch.hasikit.domain.repository.VideoRepository;
import com.trixsearch.hasikit.telegram.service.TelegramClientService;
import dagger.hilt.android.qualifiers.ApplicationContext;
import kotlinx.coroutines.Dispatchers;
import kotlinx.coroutines.flow.StateFlow;
import org.drinkless.tdlib.TdApi;
import java.io.File;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import javax.inject.Singleton;

@javax.inject.Singleton()
@kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000x\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010$\n\u0002\u0010\u000e\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\t\n\u0000\b\u0007\u0018\u00002\u00020\u0001B#\b\u0007\u0012\b\b\u0001\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\u0004\b\b\u0010\tJ\u000e\u0010 \u001a\u00020!2\u0006\u0010\"\u001a\u00020#J\u000e\u0010$\u001a\u00020!2\u0006\u0010%\u001a\u00020\u0011J\u000e\u0010&\u001a\u00020!2\u0006\u0010\"\u001a\u00020#J\u000e\u0010\'\u001a\u00020!2\u0006\u0010\"\u001a\u00020#J\u000e\u0010(\u001a\u00020!2\u0006\u0010%\u001a\u00020\u0011J\u001a\u0010)\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020,0+0*2\u0006\u0010%\u001a\u00020\u0011J\u000e\u0010-\u001a\u00020!2\u0006\u0010.\u001a\u00020/R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u000bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\rX\u0082\u0004\u00a2\u0006\u0002\n\u0000R \u0010\u000e\u001a\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u0011\u0012\u0004\u0012\u00020\u00120\u00100\u000fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R#\u0010\u0013\u001a\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u0011\u0012\u0004\u0012\u00020\u00120\u00100\u0014\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0016R\u0017\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\u00110\u000f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0019R\u0017\u0010\u001a\u001a\b\u0012\u0004\u0012\u00020\u001b0\u000f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001c\u0010\u0019R\u0017\u0010\u001d\u001a\b\u0012\u0004\u0012\u00020\u001e0\u000f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001f\u0010\u0019\u00a8\u00060"}, d2 = {"Lcom/trixsearch/hasikit/download/HasikitDownloadManager;", "", "context", "Landroid/content/Context;", "repository", "Lcom/trixsearch/hasikit/domain/repository/VideoRepository;", "telegramClientService", "Lcom/trixsearch/hasikit/telegram/service/TelegramClientService;", "<init>", "(Landroid/content/Context;Lcom/trixsearch/hasikit/domain/repository/VideoRepository;Lcom/trixsearch/hasikit/telegram/service/TelegramClientService;)V", "scope", "Lkotlinx/coroutines/CoroutineScope;", "workManager", "Landroidx/work/WorkManager;", "_downloadTasks", "Lkotlinx/coroutines/flow/MutableStateFlow;", "", "", "Lcom/trixsearch/hasikit/domain/model/DownloadTask;", "downloadTasks", "Lkotlinx/coroutines/flow/StateFlow;", "getDownloadTasks", "()Lkotlinx/coroutines/flow/StateFlow;", "customDownloadPath", "getCustomDownloadPath", "()Lkotlinx/coroutines/flow/MutableStateFlow;", "galleryVisible", "", "getGalleryVisible", "thumbnailCacheVersion", "", "getThumbnailCacheVersion", "startDownload", "", "video", "Lcom/trixsearch/hasikit/domain/model/Video;", "pauseDownload", "videoId", "resumeDownload", "retryDownload", "deleteDownload", "getWorkInfoFlow", "Landroidx/lifecycle/LiveData;", "", "Landroidx/work/WorkInfo;", "clearStreamingCache", "fileId", "", "app_debug"})
public final class HasikitDownloadManager {
    @org.jetbrains.annotations.NotNull()
    private final android.content.Context context = null;
    @org.jetbrains.annotations.NotNull()
    private final com.trixsearch.hasikit.domain.repository.VideoRepository repository = null;
    @org.jetbrains.annotations.NotNull()
    private final com.trixsearch.hasikit.telegram.service.TelegramClientService telegramClientService = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.CoroutineScope scope = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.work.WorkManager workManager = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.util.Map<java.lang.String, com.trixsearch.hasikit.domain.model.DownloadTask>> _downloadTasks = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.util.Map<java.lang.String, com.trixsearch.hasikit.domain.model.DownloadTask>> downloadTasks = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.String> customDownloadPath = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.Boolean> galleryVisible = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.Integer> thumbnailCacheVersion = null;
    
    @javax.inject.Inject()
    public HasikitDownloadManager(@dagger.hilt.android.qualifiers.ApplicationContext()
    @org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    com.trixsearch.hasikit.domain.repository.VideoRepository repository, @org.jetbrains.annotations.NotNull()
    com.trixsearch.hasikit.telegram.service.TelegramClientService telegramClientService) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.util.Map<java.lang.String, com.trixsearch.hasikit.domain.model.DownloadTask>> getDownloadTasks() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.MutableStateFlow<java.lang.String> getCustomDownloadPath() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.MutableStateFlow<java.lang.Boolean> getGalleryVisible() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.MutableStateFlow<java.lang.Integer> getThumbnailCacheVersion() {
        return null;
    }
    
    public final void startDownload(@org.jetbrains.annotations.NotNull()
    com.trixsearch.hasikit.domain.model.Video video) {
    }
    
    public final void pauseDownload(@org.jetbrains.annotations.NotNull()
    java.lang.String videoId) {
    }
    
    public final void resumeDownload(@org.jetbrains.annotations.NotNull()
    com.trixsearch.hasikit.domain.model.Video video) {
    }
    
    public final void retryDownload(@org.jetbrains.annotations.NotNull()
    com.trixsearch.hasikit.domain.model.Video video) {
    }
    
    public final void deleteDownload(@org.jetbrains.annotations.NotNull()
    java.lang.String videoId) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.LiveData<java.util.List<androidx.work.WorkInfo>> getWorkInfoFlow(@org.jetbrains.annotations.NotNull()
    java.lang.String videoId) {
        return null;
    }
    
    public final void clearStreamingCache(long fileId) {
    }
}