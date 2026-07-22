package com.trixsearch.hasikit.ui.screens.auth;

import android.util.Log;
import androidx.lifecycle.ViewModel;
import com.trixsearch.hasikit.telegram.domain.model.AuthResult;
import com.trixsearch.hasikit.telegram.domain.model.AuthState;
import com.trixsearch.hasikit.telegram.domain.repository.TelegramAuthRepository;
import dagger.hilt.android.lifecycle.HiltViewModel;
import kotlinx.coroutines.flow.StateFlow;
import javax.inject.Inject;

@kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000:\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\t\b\u0007\u0018\u00002\u00020\u0001B\u0011\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0004\b\u0004\u0010\u0005J\u000e\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u0010J\u001e\u0010\u0016\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u00102\u0006\u0010\u0017\u001a\u00020\u00102\u0006\u0010\u0018\u001a\u00020\u0010J\u0006\u0010\u0019\u001a\u00020\u0014J\u0006\u0010\u001a\u001a\u00020\u0014J\u0006\u0010\u001b\u001a\u00020\u0014J\u0006\u0010\u001c\u001a\u00020\u0014R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u0014\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\r0\fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\r0\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\nR\u0016\u0010\u000f\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00100\fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0019\u0010\u0011\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00100\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\n\u00a8\u0006\u001d"}, d2 = {"Lcom/trixsearch/hasikit/ui/screens/auth/AuthViewModel;", "Landroidx/lifecycle/ViewModel;", "authRepository", "Lcom/trixsearch/hasikit/telegram/domain/repository/TelegramAuthRepository;", "<init>", "(Lcom/trixsearch/hasikit/telegram/domain/repository/TelegramAuthRepository;)V", "authState", "Lkotlinx/coroutines/flow/StateFlow;", "Lcom/trixsearch/hasikit/telegram/domain/model/AuthState;", "getAuthState", "()Lkotlinx/coroutines/flow/StateFlow;", "_isLoading", "Lkotlinx/coroutines/flow/MutableStateFlow;", "", "isLoading", "_errorMessage", "", "errorMessage", "getErrorMessage", "sendCode", "", "phoneNumber", "verifyCode", "phoneCodeHash", "code", "clearError", "restoreSession", "logout", "forceDeleteSession", "app_debug"})
@dagger.hilt.android.lifecycle.HiltViewModel()
public final class AuthViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull()
    private final com.trixsearch.hasikit.telegram.domain.repository.TelegramAuthRepository authRepository = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<com.trixsearch.hasikit.telegram.domain.model.AuthState> authState = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.Boolean> _isLoading = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.Boolean> isLoading = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.String> _errorMessage = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.String> errorMessage = null;
    
    @javax.inject.Inject()
    public AuthViewModel(@org.jetbrains.annotations.NotNull()
    com.trixsearch.hasikit.telegram.domain.repository.TelegramAuthRepository authRepository) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<com.trixsearch.hasikit.telegram.domain.model.AuthState> getAuthState() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.Boolean> isLoading() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.String> getErrorMessage() {
        return null;
    }
    
    public final void sendCode(@org.jetbrains.annotations.NotNull()
    java.lang.String phoneNumber) {
    }
    
    public final void verifyCode(@org.jetbrains.annotations.NotNull()
    java.lang.String phoneNumber, @org.jetbrains.annotations.NotNull()
    java.lang.String phoneCodeHash, @org.jetbrains.annotations.NotNull()
    java.lang.String code) {
    }
    
    public final void clearError() {
    }
    
    public final void restoreSession() {
    }
    
    public final void logout() {
    }
    
    public final void forceDeleteSession() {
    }
}