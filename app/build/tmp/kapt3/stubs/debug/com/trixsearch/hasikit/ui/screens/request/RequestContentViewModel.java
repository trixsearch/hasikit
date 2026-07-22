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

@kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000H\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0004\b\u0007\u0018\u00002\u00020\u0001:\u0001\u001eB\u0011\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0004\b\u0004\u0010\u0005J\"\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u00102\b\u0010\u0011\u001a\u0004\u0018\u00010\u00102\b\u0010\u0012\u001a\u0004\u0018\u00010\u0010J\u0006\u0010\u0013\u001a\u00020\u000eJ\u0018\u0010\u0014\u001a\u0004\u0018\u00010\u00152\u0006\u0010\u0016\u001a\u00020\u0010H\u0082@\u00a2\u0006\u0002\u0010\u0017J \u0010\u0018\u001a\u0004\u0018\u00010\u00192\u0006\u0010\u001a\u001a\u00020\u001b2\u0006\u0010\u001c\u001a\u00020\u0010H\u0082@\u00a2\u0006\u0002\u0010\u001dR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\t\u001a\b\u0012\u0004\u0012\u00020\b0\n\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\f\u00a8\u0006\u001f"}, d2 = {"Lcom/trixsearch/hasikit/ui/screens/request/RequestContentViewModel;", "Landroidx/lifecycle/ViewModel;", "telegramClientService", "Lcom/trixsearch/hasikit/telegram/service/TelegramClientService;", "<init>", "(Lcom/trixsearch/hasikit/telegram/service/TelegramClientService;)V", "_sendState", "Lkotlinx/coroutines/flow/MutableStateFlow;", "Lcom/trixsearch/hasikit/ui/screens/request/RequestContentViewModel$SendState;", "sendState", "Lkotlinx/coroutines/flow/StateFlow;", "getSendState", "()Lkotlinx/coroutines/flow/StateFlow;", "sendRequest", "", "contentName", "", "type", "year", "resetState", "searchPublicChat", "Lorg/drinkless/tdlib/TdApi$Chat;", "username", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "sendMessage", "Lorg/drinkless/tdlib/TdApi$Message;", "chatId", "", "text", "(JLjava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "SendState", "app_debug"})
@dagger.hilt.android.lifecycle.HiltViewModel()
public final class RequestContentViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull()
    private final com.trixsearch.hasikit.telegram.service.TelegramClientService telegramClientService = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<com.trixsearch.hasikit.ui.screens.request.RequestContentViewModel.SendState> _sendState = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<com.trixsearch.hasikit.ui.screens.request.RequestContentViewModel.SendState> sendState = null;
    
    @javax.inject.Inject()
    public RequestContentViewModel(@org.jetbrains.annotations.NotNull()
    com.trixsearch.hasikit.telegram.service.TelegramClientService telegramClientService) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<com.trixsearch.hasikit.ui.screens.request.RequestContentViewModel.SendState> getSendState() {
        return null;
    }
    
    public final void sendRequest(@org.jetbrains.annotations.NotNull()
    java.lang.String contentName, @org.jetbrains.annotations.Nullable()
    java.lang.String type, @org.jetbrains.annotations.Nullable()
    java.lang.String year) {
    }
    
    public final void resetState() {
    }
    
    private final java.lang.Object searchPublicChat(java.lang.String username, kotlin.coroutines.Continuation<? super org.drinkless.tdlib.TdApi.Chat> $completion) {
        return null;
    }
    
    private final java.lang.Object sendMessage(long chatId, java.lang.String text, kotlin.coroutines.Continuation<? super org.drinkless.tdlib.TdApi.Message> $completion) {
        return null;
    }
    
    @kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\b6\u0018\u00002\u00020\u0001:\u0004\u0004\u0005\u0006\u0007B\t\b\u0004\u00a2\u0006\u0004\b\u0002\u0010\u0003\u0082\u0001\u0004\b\t\n\u000b\u00a8\u0006\f"}, d2 = {"Lcom/trixsearch/hasikit/ui/screens/request/RequestContentViewModel$SendState;", "", "<init>", "()V", "Idle", "Sending", "Success", "Error", "Lcom/trixsearch/hasikit/ui/screens/request/RequestContentViewModel$SendState$Error;", "Lcom/trixsearch/hasikit/ui/screens/request/RequestContentViewModel$SendState$Idle;", "Lcom/trixsearch/hasikit/ui/screens/request/RequestContentViewModel$SendState$Sending;", "Lcom/trixsearch/hasikit/ui/screens/request/RequestContentViewModel$SendState$Success;", "app_debug"})
    public static abstract class SendState {
        
        private SendState() {
            super();
        }
        
        @kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0007\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0004\b\u0004\u0010\u0005J\t\u0010\b\u001a\u00020\u0003H\u00c6\u0003J\u0013\u0010\t\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u0003H\u00c6\u0001J\u0013\u0010\n\u001a\u00020\u000b2\b\u0010\f\u001a\u0004\u0018\u00010\rH\u00d6\u0003J\t\u0010\u000e\u001a\u00020\u000fH\u00d6\u0001J\t\u0010\u0010\u001a\u00020\u0003H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007\u00a8\u0006\u0011"}, d2 = {"Lcom/trixsearch/hasikit/ui/screens/request/RequestContentViewModel$SendState$Error;", "Lcom/trixsearch/hasikit/ui/screens/request/RequestContentViewModel$SendState;", "message", "", "<init>", "(Ljava/lang/String;)V", "getMessage", "()Ljava/lang/String;", "component1", "copy", "equals", "", "other", "", "hashCode", "", "toString", "app_debug"})
        public static final class Error extends com.trixsearch.hasikit.ui.screens.request.RequestContentViewModel.SendState {
            @org.jetbrains.annotations.NotNull()
            private final java.lang.String message = null;
            
            public Error(@org.jetbrains.annotations.NotNull()
            java.lang.String message) {
            }
            
            @org.jetbrains.annotations.NotNull()
            public final java.lang.String getMessage() {
                return null;
            }
            
            @org.jetbrains.annotations.NotNull()
            public final java.lang.String component1() {
                return null;
            }
            
            @org.jetbrains.annotations.NotNull()
            public final com.trixsearch.hasikit.ui.screens.request.RequestContentViewModel.SendState.Error copy(@org.jetbrains.annotations.NotNull()
            java.lang.String message) {
                return null;
            }
            
            @java.lang.Override()
            public boolean equals(@org.jetbrains.annotations.Nullable()
            java.lang.Object other) {
                return false;
            }
            
            @java.lang.Override()
            public int hashCode() {
                return 0;
            }
            
            @java.lang.Override()
            @org.jetbrains.annotations.NotNull()
            public java.lang.String toString() {
                return null;
            }
        }
        
        @kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003\u00a8\u0006\u0004"}, d2 = {"Lcom/trixsearch/hasikit/ui/screens/request/RequestContentViewModel$SendState$Idle;", "Lcom/trixsearch/hasikit/ui/screens/request/RequestContentViewModel$SendState;", "<init>", "()V", "app_debug"})
        public static final class Idle extends com.trixsearch.hasikit.ui.screens.request.RequestContentViewModel.SendState {
            @org.jetbrains.annotations.NotNull()
            public static final com.trixsearch.hasikit.ui.screens.request.RequestContentViewModel.SendState.Idle INSTANCE = null;
            
            private Idle() {
            }
        }
        
        @kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003\u00a8\u0006\u0004"}, d2 = {"Lcom/trixsearch/hasikit/ui/screens/request/RequestContentViewModel$SendState$Sending;", "Lcom/trixsearch/hasikit/ui/screens/request/RequestContentViewModel$SendState;", "<init>", "()V", "app_debug"})
        public static final class Sending extends com.trixsearch.hasikit.ui.screens.request.RequestContentViewModel.SendState {
            @org.jetbrains.annotations.NotNull()
            public static final com.trixsearch.hasikit.ui.screens.request.RequestContentViewModel.SendState.Sending INSTANCE = null;
            
            private Sending() {
            }
        }
        
        @kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003\u00a8\u0006\u0004"}, d2 = {"Lcom/trixsearch/hasikit/ui/screens/request/RequestContentViewModel$SendState$Success;", "Lcom/trixsearch/hasikit/ui/screens/request/RequestContentViewModel$SendState;", "<init>", "()V", "app_debug"})
        public static final class Success extends com.trixsearch.hasikit.ui.screens.request.RequestContentViewModel.SendState {
            @org.jetbrains.annotations.NotNull()
            public static final com.trixsearch.hasikit.ui.screens.request.RequestContentViewModel.SendState.Success INSTANCE = null;
            
            private Success() {
            }
        }
    }
}