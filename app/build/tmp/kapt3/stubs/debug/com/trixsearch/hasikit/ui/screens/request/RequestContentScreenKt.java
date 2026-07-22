package com.trixsearch.hasikit.ui.screens.request;

import android.util.Log;
import androidx.compose.foundation.layout.*;
import androidx.compose.material.icons.Icons;
import androidx.compose.material.icons.filled.*;
import androidx.compose.material3.*;
import androidx.compose.runtime.*;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.text.font.FontWeight;
import androidx.compose.ui.text.style.TextAlign;
import androidx.lifecycle.ViewModel;
import androidx.navigation.NavController;
import com.trixsearch.hasikit.telegram.service.TelegramClientService;
import dagger.hilt.android.lifecycle.HiltViewModel;
import kotlinx.coroutines.flow.StateFlow;
import org.drinkless.tdlib.TdApi;
import java.util.Calendar;
import javax.inject.Inject;

@kotlin.Metadata(mv = {2, 1, 0}, k = 2, xi = 48, d1 = {"\u00006\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010 \n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u001a;\u0010\u0005\u001a\u00020\u00062\b\u0010\u0007\u001a\u0004\u0018\u00010\b2\u0014\u0010\t\u001a\u0010\u0012\u0006\u0012\u0004\u0018\u00010\b\u0012\u0004\u0012\u00020\u00060\n2\f\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\u00060\fH\u0003\u00a2\u0006\u0002\u0010\r\u001a\u001a\u0010\u000e\u001a\u00020\u00062\u0006\u0010\u000f\u001a\u00020\u00102\b\b\u0002\u0010\u0011\u001a\u00020\u0012H\u0007\"\u000e\u0010\u0000\u001a\u00020\u0001X\u0082T\u00a2\u0006\u0002\n\u0000\"\u000e\u0010\u0002\u001a\u00020\u0001X\u0082T\u00a2\u0006\u0002\n\u0000\"\u0014\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00010\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0013"}, d2 = {"TAG", "", "BOT_USERNAME", "CONTENT_TYPES", "", "YearPickerDialog", "", "currentYear", "", "onYearSelected", "Lkotlin/Function1;", "onDismiss", "Lkotlin/Function0;", "(Ljava/lang/Integer;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function0;)V", "RequestContentScreen", "navController", "Landroidx/navigation/NavController;", "viewModel", "Lcom/trixsearch/hasikit/ui/screens/request/RequestContentViewModel;", "app_debug"})
public final class RequestContentScreenKt {
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String TAG = "RequestContent";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String BOT_USERNAME = "hasikit_m_bot";
    @org.jetbrains.annotations.NotNull()
    private static final java.util.List<java.lang.String> CONTENT_TYPES = null;
    
    @androidx.compose.runtime.Composable()
    private static final void YearPickerDialog(java.lang.Integer currentYear, kotlin.jvm.functions.Function1<? super java.lang.Integer, kotlin.Unit> onYearSelected, kotlin.jvm.functions.Function0<kotlin.Unit> onDismiss) {
    }
    
    @kotlin.OptIn(markerClass = {androidx.compose.material3.ExperimentalMaterial3Api.class})
    @androidx.compose.runtime.Composable()
    public static final void RequestContentScreen(@org.jetbrains.annotations.NotNull()
    androidx.navigation.NavController navController, @org.jetbrains.annotations.NotNull()
    com.trixsearch.hasikit.ui.screens.request.RequestContentViewModel viewModel) {
    }
}