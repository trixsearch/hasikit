package com.trixsearch.hasikit.ui.screens.home;

import com.trixsearch.hasikit.data.local.entities.FavoriteEntity;
import com.trixsearch.hasikit.data.local.entities.WatchLaterEntity;
import android.util.Log;
import androidx.lifecycle.ViewModel;
import com.trixsearch.hasikit.domain.model.DownloadState;
import com.trixsearch.hasikit.domain.model.Video;
import com.trixsearch.hasikit.domain.model.WatchProgress;
import com.trixsearch.hasikit.domain.repository.VideoRepository;
import com.trixsearch.hasikit.download.HasikitDownloadManager;
import com.trixsearch.hasikit.telegram.config.TelegramSource;
import com.trixsearch.hasikit.telegram.config.TelegramSourceConfig;
import com.trixsearch.hasikit.telegram.domain.model.AuthState;
import com.trixsearch.hasikit.telegram.domain.model.TelegramMedia;
import com.trixsearch.hasikit.telegram.domain.repository.TelegramAuthRepository;
import com.trixsearch.hasikit.telegram.domain.repository.TelegramChannelRepository;
import com.trixsearch.hasikit.search.SearchEngine;
import dagger.hilt.android.lifecycle.HiltViewModel;
import kotlinx.coroutines.flow.*;
import kotlinx.coroutines.Dispatchers;
import javax.inject.Inject;

@kotlin.Metadata(mv = {2, 1, 0}, k = 2, xi = 48, d1 = {"\u0000\u0016\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\t\n\u0000\"\u000e\u0010\u0000\u001a\u00020\u0001X\u0082T\u00a2\u0006\u0002\n\u0000\"\u000e\u0010\u0002\u001a\u00020\u0003X\u0082T\u00a2\u0006\u0002\n\u0000\"\u000e\u0010\u0004\u001a\u00020\u0003X\u0082T\u00a2\u0006\u0002\n\u0000\"\u000e\u0010\u0005\u001a\u00020\u0006X\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0007"}, d2 = {"TAG", "", "PAGE_SIZE", "", "PREFETCH_THRESHOLD", "AUTO_REFRESH_SECONDS", "", "app_debug"})
public final class HomeViewModelKt {
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String TAG = "HomeViewModel";
    private static final int PAGE_SIZE = 25;
    private static final int PREFETCH_THRESHOLD = 10;
    private static final long AUTO_REFRESH_SECONDS = 60L;
}