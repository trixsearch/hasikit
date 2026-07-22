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

@kotlin.Metadata(mv = {2, 1, 0}, k = 2, xi = 48, d1 = {"\u0000$\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u001a\u0014\u0010\u0000\u001a\u00020\u0001*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u0004H\u0002\u001a\u001a\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\b2\b\b\u0002\u0010\t\u001a\u00020\nH\u0007\u00a8\u0006\u000b"}, d2 = {"toVideo", "Lcom/trixsearch/hasikit/domain/model/Video;", "Lcom/trixsearch/hasikit/telegram/domain/model/TelegramMedia;", "source", "Lcom/trixsearch/hasikit/telegram/config/TelegramSource;", "SearchScreen", "", "navController", "Landroidx/navigation/NavController;", "viewModel", "Lcom/trixsearch/hasikit/ui/screens/search/SearchViewModel;", "app_debug"})
public final class SearchScreenKt {
    
    private static final com.trixsearch.hasikit.domain.model.Video toVideo(com.trixsearch.hasikit.telegram.domain.model.TelegramMedia $this$toVideo, com.trixsearch.hasikit.telegram.config.TelegramSource source) {
        return null;
    }
    
    @kotlin.OptIn(markerClass = {androidx.compose.material3.ExperimentalMaterial3Api.class})
    @androidx.compose.runtime.Composable()
    public static final void SearchScreen(@org.jetbrains.annotations.NotNull()
    androidx.navigation.NavController navController, @org.jetbrains.annotations.NotNull()
    com.trixsearch.hasikit.ui.screens.search.SearchViewModel viewModel) {
    }
}