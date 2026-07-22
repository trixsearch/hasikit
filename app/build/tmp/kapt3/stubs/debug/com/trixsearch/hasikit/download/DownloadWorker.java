package com.trixsearch.hasikit.download;

import android.content.Context;
import android.util.Log;
import androidx.hilt.work.HiltWorker;
import androidx.work.CoroutineWorker;
import androidx.work.Data;
import androidx.work.WorkerParameters;
import com.trixsearch.hasikit.domain.model.DownloadState;
import com.trixsearch.hasikit.domain.model.DownloadTask;
import com.trixsearch.hasikit.domain.repository.VideoRepository;
import com.trixsearch.hasikit.telegram.service.TelegramClientService;
import dagger.assisted.Assisted;
import dagger.assisted.AssistedInject;
import org.drinkless.tdlib.TdApi;
import java.io.File;

@kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000J\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0007\u0018\u0000 \u001f2\u00020\u0001:\u0001\u001fB-\b\u0007\u0012\b\b\u0001\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0001\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\t\u00a2\u0006\u0004\b\n\u0010\u000bJ\u000e\u0010\f\u001a\u00020\rH\u0096@\u00a2\u0006\u0002\u0010\u000eJ\u0018\u0010\u000f\u001a\u0004\u0018\u00010\u00102\u0006\u0010\u0011\u001a\u00020\u0012H\u0082@\u00a2\u0006\u0002\u0010\u0013J\u0018\u0010\u0014\u001a\u0004\u0018\u00010\u00102\u0006\u0010\u0011\u001a\u00020\u0012H\u0082@\u00a2\u0006\u0002\u0010\u0013J*\u0010\u0015\u001a\u00020\u00162\u0006\u0010\u0017\u001a\u00020\u00162\u0006\u0010\u0018\u001a\u00020\u00162\u0006\u0010\u0019\u001a\u00020\u00162\b\u0010\u001a\u001a\u0004\u0018\u00010\u0016H\u0002J\u0018\u0010\u001b\u001a\u00020\u00162\u0006\u0010\u001c\u001a\u00020\u001d2\u0006\u0010\u001e\u001a\u00020\u0016H\u0002R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006 "}, d2 = {"Lcom/trixsearch/hasikit/download/DownloadWorker;", "Landroidx/work/CoroutineWorker;", "context", "Landroid/content/Context;", "params", "Landroidx/work/WorkerParameters;", "repository", "Lcom/trixsearch/hasikit/domain/repository/VideoRepository;", "telegramClientService", "Lcom/trixsearch/hasikit/telegram/service/TelegramClientService;", "<init>", "(Landroid/content/Context;Landroidx/work/WorkerParameters;Lcom/trixsearch/hasikit/domain/repository/VideoRepository;Lcom/trixsearch/hasikit/telegram/service/TelegramClientService;)V", "doWork", "Landroidx/work/ListenableWorker$Result;", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "startTdlibDownload", "Lorg/drinkless/tdlib/TdApi$File;", "fileId", "", "(ILkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getFileInfo", "copyToDestination", "", "sourcePath", "title", "videoId", "destDir", "copyToAppPrivate", "src", "Ljava/io/File;", "fileName", "Companion", "app_debug"})
@androidx.hilt.work.HiltWorker()
public final class DownloadWorker extends androidx.work.CoroutineWorker {
    @org.jetbrains.annotations.NotNull()
    private final android.content.Context context = null;
    @org.jetbrains.annotations.NotNull()
    private final com.trixsearch.hasikit.domain.repository.VideoRepository repository = null;
    @org.jetbrains.annotations.NotNull()
    private final com.trixsearch.hasikit.telegram.service.TelegramClientService telegramClientService = null;
    @org.jetbrains.annotations.NotNull()
    public static final com.trixsearch.hasikit.download.DownloadWorker.Companion Companion = null;
    
    @dagger.assisted.AssistedInject()
    public DownloadWorker(@dagger.assisted.Assisted()
    @org.jetbrains.annotations.NotNull()
    android.content.Context context, @dagger.assisted.Assisted()
    @org.jetbrains.annotations.NotNull()
    androidx.work.WorkerParameters params, @org.jetbrains.annotations.NotNull()
    com.trixsearch.hasikit.domain.repository.VideoRepository repository, @org.jetbrains.annotations.NotNull()
    com.trixsearch.hasikit.telegram.service.TelegramClientService telegramClientService) {
        super(null, null);
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object doWork(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super androidx.work.ListenableWorker.Result> $completion) {
        return null;
    }
    
    private final java.lang.Object startTdlibDownload(int fileId, kotlin.coroutines.Continuation<? super org.drinkless.tdlib.TdApi.File> $completion) {
        return null;
    }
    
    private final java.lang.Object getFileInfo(int fileId, kotlin.coroutines.Continuation<? super org.drinkless.tdlib.TdApi.File> $completion) {
        return null;
    }
    
    private final java.lang.String copyToDestination(java.lang.String sourcePath, java.lang.String title, java.lang.String videoId, java.lang.String destDir) {
        return null;
    }
    
    private final java.lang.String copyToAppPrivate(java.io.File src, java.lang.String fileName) {
        return null;
    }
    
    @kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000\u001c\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u000e\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0005J*\u0010\u0007\u001a\u00020\b2\u0006\u0010\u0006\u001a\u00020\u00052\u0006\u0010\t\u001a\u00020\u00052\u0006\u0010\n\u001a\u00020\u00052\n\b\u0002\u0010\u000b\u001a\u0004\u0018\u00010\u0005\u00a8\u0006\f"}, d2 = {"Lcom/trixsearch/hasikit/download/DownloadWorker$Companion;", "", "<init>", "()V", "workName", "", "videoId", "buildInputData", "Landroidx/work/Data;", "telegramFileId", "title", "destDir", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String workName(@org.jetbrains.annotations.NotNull()
        java.lang.String videoId) {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final androidx.work.Data buildInputData(@org.jetbrains.annotations.NotNull()
        java.lang.String videoId, @org.jetbrains.annotations.NotNull()
        java.lang.String telegramFileId, @org.jetbrains.annotations.NotNull()
        java.lang.String title, @org.jetbrains.annotations.Nullable()
        java.lang.String destDir) {
            return null;
        }
    }
}