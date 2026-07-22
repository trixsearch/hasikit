package com.trixsearch.hasikit.ui.screens.auth;

import androidx.compose.foundation.layout.*;
import androidx.compose.foundation.text.KeyboardOptions;
import androidx.compose.material.icons.Icons;
import androidx.compose.material.icons.filled.*;
import androidx.compose.material3.*;
import androidx.compose.runtime.*;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.focus.FocusRequester;
import androidx.compose.ui.graphics.Brush;
import androidx.compose.ui.text.font.FontWeight;
import androidx.compose.ui.text.input.ImeAction;
import androidx.compose.ui.text.input.KeyboardType;
import androidx.compose.ui.text.style.TextAlign;
import com.trixsearch.hasikit.telegram.domain.model.AuthState;

@kotlin.Metadata(mv = {2, 1, 0}, k = 2, xi = 48, d1 = {"\u0000F\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0018\u0002\n\u0000\u001a \u0010\u0005\u001a\u00020\u00062\f\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\u00060\b2\b\b\u0002\u0010\t\u001a\u00020\nH\u0007\u001a\u0012\u0010\u000b\u001a\u00020\u00062\b\b\u0002\u0010\f\u001a\u00020\rH\u0003\u001a<\u0010\u000e\u001a\u00020\u00062\u0006\u0010\u000f\u001a\u00020\u00102\b\u0010\u0011\u001a\u0004\u0018\u00010\u00122\u0012\u0010\u0013\u001a\u000e\u0012\u0004\u0012\u00020\u0012\u0012\u0004\u0012\u00020\u00060\u00142\f\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\u00060\bH\u0003\u001aR\u0010\u0016\u001a\u00020\u00062\u0006\u0010\u0017\u001a\u00020\u00122\u0006\u0010\u000f\u001a\u00020\u00102\b\u0010\u0011\u001a\u0004\u0018\u00010\u00122\u0012\u0010\u0018\u001a\u000e\u0012\u0004\u0012\u00020\u0012\u0012\u0004\u0012\u00020\u00060\u00142\f\u0010\u0019\u001a\b\u0012\u0004\u0012\u00020\u00060\b2\f\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\u00060\bH\u0003\u001a\u0010\u0010\u001a\u001a\u00020\u00062\u0006\u0010\u001b\u001a\u00020\u0012H\u0003\u001a\b\u0010\u001c\u001a\u00020\u001dH\u0003\"\u000e\u0010\u0000\u001a\u00020\u0001X\u0082\u0004\u00a2\u0006\u0002\n\u0000\"\u0010\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0004\n\u0002\u0010\u0004\u00a8\u0006\u001e"}, d2 = {"BgGradient", "Landroidx/compose/ui/graphics/Brush;", "PrimaryBlue", "Landroidx/compose/ui/graphics/Color;", "J", "AuthScreen", "", "onAuthenticated", "Lkotlin/Function0;", "viewModel", "Lcom/trixsearch/hasikit/ui/screens/auth/AuthViewModel;", "HasikitLogo", "size", "", "PhoneScreen", "isLoading", "", "errorMessage", "", "onSendCode", "Lkotlin/Function1;", "onClearError", "OtpScreen", "phone", "onVerify", "onBack", "ErrorBanner", "message", "authFieldColors", "Landroidx/compose/material3/TextFieldColors;", "app_debug"})
public final class AuthScreenKt {
    @org.jetbrains.annotations.NotNull()
    private static final androidx.compose.ui.graphics.Brush BgGradient = null;
    private static final long PrimaryBlue = 0L;
    
    @androidx.compose.runtime.Composable()
    public static final void AuthScreen(@org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onAuthenticated, @org.jetbrains.annotations.NotNull()
    com.trixsearch.hasikit.ui.screens.auth.AuthViewModel viewModel) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void HasikitLogo(int size) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void PhoneScreen(boolean isLoading, java.lang.String errorMessage, kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onSendCode, kotlin.jvm.functions.Function0<kotlin.Unit> onClearError) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void OtpScreen(java.lang.String phone, boolean isLoading, java.lang.String errorMessage, kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onVerify, kotlin.jvm.functions.Function0<kotlin.Unit> onBack, kotlin.jvm.functions.Function0<kotlin.Unit> onClearError) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void ErrorBanner(java.lang.String message) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final androidx.compose.material3.TextFieldColors authFieldColors() {
        return null;
    }
}