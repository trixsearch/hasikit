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

@kotlin.Metadata(mv = {2, 1, 0}, k = 2, xi = 48, d1 = {"\u0000\u0010\n\u0000\n\u0002\u0010\u000e\n\u0002\b\b\n\u0002\u0010\b\n\u0000\"\u000e\u0010\u0000\u001a\u00020\u0001X\u0082T\u00a2\u0006\u0002\n\u0000\"\u000e\u0010\u0002\u001a\u00020\u0001X\u0086T\u00a2\u0006\u0002\n\u0000\"\u000e\u0010\u0003\u001a\u00020\u0001X\u0086T\u00a2\u0006\u0002\n\u0000\"\u000e\u0010\u0004\u001a\u00020\u0001X\u0086T\u00a2\u0006\u0002\n\u0000\"\u000e\u0010\u0005\u001a\u00020\u0001X\u0086T\u00a2\u0006\u0002\n\u0000\"\u000e\u0010\u0006\u001a\u00020\u0001X\u0086T\u00a2\u0006\u0002\n\u0000\"\u000e\u0010\u0007\u001a\u00020\u0001X\u0086T\u00a2\u0006\u0002\n\u0000\"\u000e\u0010\b\u001a\u00020\u0001X\u0086T\u00a2\u0006\u0002\n\u0000\"\u000e\u0010\t\u001a\u00020\nX\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u000b"}, d2 = {"TAG", "", "KEY_VIDEO_ID", "KEY_TELEGRAM_FILE_ID", "KEY_VIDEO_TITLE", "KEY_DEST_DIR", "KEY_PROGRESS", "KEY_LOCAL_PATH", "KEY_ERROR", "STALL_THRESHOLD", "", "app_debug"})
public final class DownloadWorkerKt {
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String TAG = "DownloadWorker";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String KEY_VIDEO_ID = "videoId";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String KEY_TELEGRAM_FILE_ID = "telegramFileId";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String KEY_VIDEO_TITLE = "videoTitle";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String KEY_DEST_DIR = "destDir";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String KEY_PROGRESS = "progress";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String KEY_LOCAL_PATH = "localPath";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String KEY_ERROR = "error";
    private static final int STALL_THRESHOLD = 5;
}