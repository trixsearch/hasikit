package com.trixsearch.hasikit.ui.screens.search;

import androidx.compose.foundation.layout.*;
import androidx.compose.foundation.lazy.LazyListState;
import androidx.compose.material.icons.Icons;
import androidx.compose.material3.*;
import androidx.compose.runtime.*;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.Modifier;
import androidx.lifecycle.ViewModel;
import androidx.navigation.NavController;
import com.trixsearch.hasikit.domain.model.Video;
import com.trixsearch.hasikit.telegram.config.TelegramSource;
import com.trixsearch.hasikit.telegram.config.TelegramSourceConfig;
import com.trixsearch.hasikit.telegram.domain.model.TelegramMedia;
import com.trixsearch.hasikit.telegram.domain.repository.TelegramChannelRepository;
import com.trixsearch.hasikit.ui.navigation.Screen;
import dagger.hilt.android.lifecycle.HiltViewModel;
import kotlinx.coroutines.ExperimentalCoroutinesApi;
import kotlinx.coroutines.FlowPreview;
import kotlinx.coroutines.flow.*;
import javax.inject.Inject;

@kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000X\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010%\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\n\b\u0007\u0018\u00002\u00020\u0001B\u0019\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0004\b\u0006\u0010\u0007J\u0014\u0010&\u001a\u00020\'2\f\u0010(\u001a\b\u0012\u0004\u0012\u00020\u001a0\u0013J\b\u0010)\u001a\u00020\'H\u0002J\u000e\u0010*\u001a\u00020\'2\u0006\u0010+\u001a\u00020\nJ\u0010\u0010,\u001a\u00020\'2\b\u0010-\u001a\u0004\u0018\u00010\nJ\u0018\u0010.\u001a\u0004\u0018\u00010\u00182\u0006\u0010/\u001a\u00020\u0014H\u0082@\u00a2\u0006\u0002\u00100R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\b\u001a\b\u0012\u0004\u0012\u00020\n0\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\n0\f\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u0016\u0010\u000f\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\n0\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0019\u0010\u0010\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\n0\f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u000eR\u001d\u0010\u0012\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00140\u00130\f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u000eR\u001a\u0010\u0016\u001a\u000e\u0012\u0004\u0012\u00020\n\u0012\u0004\u0012\u00020\u00180\u0017X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001a\u0010\u0019\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u001a0\u00130\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001d\u0010\u001b\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u001a0\u00130\f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001c\u0010\u000eR\u001a\u0010\u001d\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u001a0\u00130\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001d\u0010\u001e\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u001a0\u00130\f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001f\u0010\u000eR\u001d\u0010 \u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u001a0\u00130\f\u00a2\u0006\b\n\u0000\u001a\u0004\b!\u0010\u000eR\u0014\u0010\"\u001a\b\u0012\u0004\u0012\u00020#0\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010$\u001a\b\u0012\u0004\u0012\u00020#0\f\u00a2\u0006\b\n\u0000\u001a\u0004\b$\u0010\u000eR\u001a\u0010%\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u001a0\u00130\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u00061"}, d2 = {"Lcom/trixsearch/hasikit/ui/screens/search/SearchViewModel;", "Landroidx/lifecycle/ViewModel;", "channelRepository", "Lcom/trixsearch/hasikit/telegram/domain/repository/TelegramChannelRepository;", "sourceConfig", "Lcom/trixsearch/hasikit/telegram/config/TelegramSourceConfig;", "<init>", "(Lcom/trixsearch/hasikit/telegram/domain/repository/TelegramChannelRepository;Lcom/trixsearch/hasikit/telegram/config/TelegramSourceConfig;)V", "_query", "Lkotlinx/coroutines/flow/MutableStateFlow;", "", "query", "Lkotlinx/coroutines/flow/StateFlow;", "getQuery", "()Lkotlinx/coroutines/flow/StateFlow;", "_selectedSource", "selectedSource", "getSelectedSource", "availableSources", "", "Lcom/trixsearch/hasikit/telegram/config/TelegramSource;", "getAvailableSources", "chatIdCache", "", "", "_localResults", "Lcom/trixsearch/hasikit/domain/model/Video;", "localResults", "getLocalResults", "_telegramResults", "telegramResults", "getTelegramResults", "results", "getResults", "_isTelegramSearching", "", "isTelegramSearching", "_localVideoCache", "setLocalVideos", "", "videos", "startSearchJob", "setQuery", "q", "setSelectedSource", "identifier", "resolveChatId", "source", "(Lcom/trixsearch/hasikit/telegram/config/TelegramSource;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_debug"})
@dagger.hilt.android.lifecycle.HiltViewModel()
public final class SearchViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull()
    private final com.trixsearch.hasikit.telegram.domain.repository.TelegramChannelRepository channelRepository = null;
    @org.jetbrains.annotations.NotNull()
    private final com.trixsearch.hasikit.telegram.config.TelegramSourceConfig sourceConfig = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.String> _query = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.String> query = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.String> _selectedSource = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.String> selectedSource = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.util.List<com.trixsearch.hasikit.telegram.config.TelegramSource>> availableSources = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.Map<java.lang.String, java.lang.Long> chatIdCache = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.util.List<com.trixsearch.hasikit.domain.model.Video>> _localResults = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.util.List<com.trixsearch.hasikit.domain.model.Video>> localResults = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.util.List<com.trixsearch.hasikit.domain.model.Video>> _telegramResults = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.util.List<com.trixsearch.hasikit.domain.model.Video>> telegramResults = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.util.List<com.trixsearch.hasikit.domain.model.Video>> results = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.Boolean> _isTelegramSearching = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.Boolean> isTelegramSearching = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.util.List<com.trixsearch.hasikit.domain.model.Video>> _localVideoCache = null;
    
    @javax.inject.Inject()
    public SearchViewModel(@org.jetbrains.annotations.NotNull()
    com.trixsearch.hasikit.telegram.domain.repository.TelegramChannelRepository channelRepository, @org.jetbrains.annotations.NotNull()
    com.trixsearch.hasikit.telegram.config.TelegramSourceConfig sourceConfig) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.String> getQuery() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.String> getSelectedSource() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.util.List<com.trixsearch.hasikit.telegram.config.TelegramSource>> getAvailableSources() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.util.List<com.trixsearch.hasikit.domain.model.Video>> getLocalResults() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.util.List<com.trixsearch.hasikit.domain.model.Video>> getTelegramResults() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.util.List<com.trixsearch.hasikit.domain.model.Video>> getResults() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.Boolean> isTelegramSearching() {
        return null;
    }
    
    public final void setLocalVideos(@org.jetbrains.annotations.NotNull()
    java.util.List<com.trixsearch.hasikit.domain.model.Video> videos) {
    }
    
    @kotlin.OptIn(markerClass = {kotlinx.coroutines.FlowPreview.class, kotlinx.coroutines.ExperimentalCoroutinesApi.class})
    private final void startSearchJob() {
    }
    
    public final void setQuery(@org.jetbrains.annotations.NotNull()
    java.lang.String q) {
    }
    
    public final void setSelectedSource(@org.jetbrains.annotations.Nullable()
    java.lang.String identifier) {
    }
    
    private final java.lang.Object resolveChatId(com.trixsearch.hasikit.telegram.config.TelegramSource source, kotlin.coroutines.Continuation<? super java.lang.Long> $completion) {
        return null;
    }
}