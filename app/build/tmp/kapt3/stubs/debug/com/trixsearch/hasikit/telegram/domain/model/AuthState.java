package com.trixsearch.hasikit.telegram.domain.model;

@kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\b6\u0018\u00002\u00020\u0001:\u0005\u0004\u0005\u0006\u0007\bB\t\b\u0004\u00a2\u0006\u0004\b\u0002\u0010\u0003\u0082\u0001\u0005\t\n\u000b\f\r\u00a8\u0006\u000e"}, d2 = {"Lcom/trixsearch/hasikit/telegram/domain/model/AuthState;", "", "<init>", "()V", "Loading", "Unauthenticated", "CodeSent", "Authenticated", "Error", "Lcom/trixsearch/hasikit/telegram/domain/model/AuthState$Authenticated;", "Lcom/trixsearch/hasikit/telegram/domain/model/AuthState$CodeSent;", "Lcom/trixsearch/hasikit/telegram/domain/model/AuthState$Error;", "Lcom/trixsearch/hasikit/telegram/domain/model/AuthState$Loading;", "Lcom/trixsearch/hasikit/telegram/domain/model/AuthState$Unauthenticated;", "app_debug"})
public abstract class AuthState {
    
    private AuthState() {
        super();
    }
    
    @kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0004\b\u0004\u0010\u0005J\t\u0010\b\u001a\u00020\u0003H\u00c6\u0003J\u0013\u0010\t\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u0003H\u00c6\u0001J\u0013\u0010\n\u001a\u00020\u000b2\b\u0010\f\u001a\u0004\u0018\u00010\rH\u00d6\u0003J\t\u0010\u000e\u001a\u00020\u000fH\u00d6\u0001J\t\u0010\u0010\u001a\u00020\u0011H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007\u00a8\u0006\u0012"}, d2 = {"Lcom/trixsearch/hasikit/telegram/domain/model/AuthState$Authenticated;", "Lcom/trixsearch/hasikit/telegram/domain/model/AuthState;", "user", "Lcom/trixsearch/hasikit/telegram/domain/model/TelegramUser;", "<init>", "(Lcom/trixsearch/hasikit/telegram/domain/model/TelegramUser;)V", "getUser", "()Lcom/trixsearch/hasikit/telegram/domain/model/TelegramUser;", "component1", "copy", "equals", "", "other", "", "hashCode", "", "toString", "", "app_debug"})
    public static final class Authenticated extends com.trixsearch.hasikit.telegram.domain.model.AuthState {
        @org.jetbrains.annotations.NotNull()
        private final com.trixsearch.hasikit.telegram.domain.model.TelegramUser user = null;
        
        public Authenticated(@org.jetbrains.annotations.NotNull()
        com.trixsearch.hasikit.telegram.domain.model.TelegramUser user) {
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.trixsearch.hasikit.telegram.domain.model.TelegramUser getUser() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.trixsearch.hasikit.telegram.domain.model.TelegramUser component1() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.trixsearch.hasikit.telegram.domain.model.AuthState.Authenticated copy(@org.jetbrains.annotations.NotNull()
        com.trixsearch.hasikit.telegram.domain.model.TelegramUser user) {
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
    
    @kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\n\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001B\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\u0004\b\u0005\u0010\u0006J\t\u0010\n\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u000b\u001a\u00020\u0003H\u00c6\u0003J\u001d\u0010\f\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u0003H\u00c6\u0001J\u0013\u0010\r\u001a\u00020\u000e2\b\u0010\u000f\u001a\u0004\u0018\u00010\u0010H\u00d6\u0003J\t\u0010\u0011\u001a\u00020\u0012H\u00d6\u0001J\t\u0010\u0013\u001a\u00020\u0003H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\b\u00a8\u0006\u0014"}, d2 = {"Lcom/trixsearch/hasikit/telegram/domain/model/AuthState$CodeSent;", "Lcom/trixsearch/hasikit/telegram/domain/model/AuthState;", "phone", "", "phoneCodeHash", "<init>", "(Ljava/lang/String;Ljava/lang/String;)V", "getPhone", "()Ljava/lang/String;", "getPhoneCodeHash", "component1", "component2", "copy", "equals", "", "other", "", "hashCode", "", "toString", "app_debug"})
    public static final class CodeSent extends com.trixsearch.hasikit.telegram.domain.model.AuthState {
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String phone = null;
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String phoneCodeHash = null;
        
        public CodeSent(@org.jetbrains.annotations.NotNull()
        java.lang.String phone, @org.jetbrains.annotations.NotNull()
        java.lang.String phoneCodeHash) {
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String getPhone() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String getPhoneCodeHash() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String component1() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String component2() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.trixsearch.hasikit.telegram.domain.model.AuthState.CodeSent copy(@org.jetbrains.annotations.NotNull()
        java.lang.String phone, @org.jetbrains.annotations.NotNull()
        java.lang.String phoneCodeHash) {
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
    
    @kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0007\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0004\b\u0004\u0010\u0005J\t\u0010\b\u001a\u00020\u0003H\u00c6\u0003J\u0013\u0010\t\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u0003H\u00c6\u0001J\u0013\u0010\n\u001a\u00020\u000b2\b\u0010\f\u001a\u0004\u0018\u00010\rH\u00d6\u0003J\t\u0010\u000e\u001a\u00020\u000fH\u00d6\u0001J\t\u0010\u0010\u001a\u00020\u0003H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007\u00a8\u0006\u0011"}, d2 = {"Lcom/trixsearch/hasikit/telegram/domain/model/AuthState$Error;", "Lcom/trixsearch/hasikit/telegram/domain/model/AuthState;", "message", "", "<init>", "(Ljava/lang/String;)V", "getMessage", "()Ljava/lang/String;", "component1", "copy", "equals", "", "other", "", "hashCode", "", "toString", "app_debug"})
    public static final class Error extends com.trixsearch.hasikit.telegram.domain.model.AuthState {
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
        public final com.trixsearch.hasikit.telegram.domain.model.AuthState.Error copy(@org.jetbrains.annotations.NotNull()
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
    
    @kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003\u00a8\u0006\u0004"}, d2 = {"Lcom/trixsearch/hasikit/telegram/domain/model/AuthState$Loading;", "Lcom/trixsearch/hasikit/telegram/domain/model/AuthState;", "<init>", "()V", "app_debug"})
    public static final class Loading extends com.trixsearch.hasikit.telegram.domain.model.AuthState {
        @org.jetbrains.annotations.NotNull()
        public static final com.trixsearch.hasikit.telegram.domain.model.AuthState.Loading INSTANCE = null;
        
        private Loading() {
        }
    }
    
    @kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003\u00a8\u0006\u0004"}, d2 = {"Lcom/trixsearch/hasikit/telegram/domain/model/AuthState$Unauthenticated;", "Lcom/trixsearch/hasikit/telegram/domain/model/AuthState;", "<init>", "()V", "app_debug"})
    public static final class Unauthenticated extends com.trixsearch.hasikit.telegram.domain.model.AuthState {
        @org.jetbrains.annotations.NotNull()
        public static final com.trixsearch.hasikit.telegram.domain.model.AuthState.Unauthenticated INSTANCE = null;
        
        private Unauthenticated() {
        }
    }
}