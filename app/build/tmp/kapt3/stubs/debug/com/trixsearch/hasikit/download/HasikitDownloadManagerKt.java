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

@kotlin.Metadata(mv = {2, 1, 0}, k = 2, xi = 48, d1 = {"\u0000\b\n\u0000\n\u0002\u0010\u000e\n\u0000\"\u000e\u0010\u0000\u001a\u00020\u0001X\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0002"}, d2 = {"TAG", "", "app_debug"})
public final class HasikitDownloadManagerKt {
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String TAG = "HasikitDownload";
}