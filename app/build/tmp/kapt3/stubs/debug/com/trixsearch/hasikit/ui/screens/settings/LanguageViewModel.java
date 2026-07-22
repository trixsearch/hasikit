package com.trixsearch.hasikit.ui.screens.settings;

import android.app.Activity;
import androidx.compose.foundation.layout.*;
import androidx.compose.material.icons.Icons;
import androidx.compose.material3.*;
import androidx.compose.runtime.*;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.text.font.FontWeight;
import androidx.lifecycle.ViewModel;
import androidx.navigation.NavController;
import com.trixsearch.hasikit.util.AppLanguage;
import com.trixsearch.hasikit.util.LanguageManager;
import dagger.hilt.android.lifecycle.HiltViewModel;
import kotlinx.coroutines.flow.SharingStarted;
import kotlinx.coroutines.flow.StateFlow;
import javax.inject.Inject;

@kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0002\b\u0007\u0018\u00002\u00020\u0001B\u0011\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0004\b\u0004\u0010\u0005J\u000e\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\bR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\n\u00a8\u0006\u000e"}, d2 = {"Lcom/trixsearch/hasikit/ui/screens/settings/LanguageViewModel;", "Landroidx/lifecycle/ViewModel;", "languageManager", "Lcom/trixsearch/hasikit/util/LanguageManager;", "<init>", "(Lcom/trixsearch/hasikit/util/LanguageManager;)V", "selectedLanguage", "Lkotlinx/coroutines/flow/StateFlow;", "Lcom/trixsearch/hasikit/util/AppLanguage;", "getSelectedLanguage", "()Lkotlinx/coroutines/flow/StateFlow;", "setLanguage", "", "language", "app_debug"})
@dagger.hilt.android.lifecycle.HiltViewModel()
public final class LanguageViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull()
    private final com.trixsearch.hasikit.util.LanguageManager languageManager = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<com.trixsearch.hasikit.util.AppLanguage> selectedLanguage = null;
    
    @javax.inject.Inject()
    public LanguageViewModel(@org.jetbrains.annotations.NotNull()
    com.trixsearch.hasikit.util.LanguageManager languageManager) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<com.trixsearch.hasikit.util.AppLanguage> getSelectedLanguage() {
        return null;
    }
    
    public final void setLanguage(@org.jetbrains.annotations.NotNull()
    com.trixsearch.hasikit.util.AppLanguage language) {
    }
}