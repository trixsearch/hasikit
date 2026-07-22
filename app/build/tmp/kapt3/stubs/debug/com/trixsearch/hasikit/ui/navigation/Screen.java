package com.trixsearch.hasikit.ui.navigation;

import java.net.URLDecoder;
import java.net.URLEncoder;

@kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000<\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u000e\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\b6\u0018\u00002\u00020\u0001:\n\b\t\n\u000b\f\r\u000e\u000f\u0010\u0011B\u0011\b\u0004\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0004\b\u0004\u0010\u0005R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007\u0082\u0001\n\u0012\u0013\u0014\u0015\u0016\u0017\u0018\u0019\u001a\u001b\u00a8\u0006\u001c"}, d2 = {"Lcom/trixsearch/hasikit/ui/navigation/Screen;", "", "route", "", "<init>", "(Ljava/lang/String;)V", "getRoute", "()Ljava/lang/String;", "Home", "Search", "Library", "Settings", "Auth", "RequestContent", "Language", "AdvancedSettings", "StorageManagement", "Player", "Lcom/trixsearch/hasikit/ui/navigation/Screen$AdvancedSettings;", "Lcom/trixsearch/hasikit/ui/navigation/Screen$Auth;", "Lcom/trixsearch/hasikit/ui/navigation/Screen$Home;", "Lcom/trixsearch/hasikit/ui/navigation/Screen$Language;", "Lcom/trixsearch/hasikit/ui/navigation/Screen$Library;", "Lcom/trixsearch/hasikit/ui/navigation/Screen$Player;", "Lcom/trixsearch/hasikit/ui/navigation/Screen$RequestContent;", "Lcom/trixsearch/hasikit/ui/navigation/Screen$Search;", "Lcom/trixsearch/hasikit/ui/navigation/Screen$Settings;", "Lcom/trixsearch/hasikit/ui/navigation/Screen$StorageManagement;", "app_debug"})
public abstract class Screen {
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String route = null;
    
    private Screen(java.lang.String route) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getRoute() {
        return null;
    }
    
    @kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003\u00a8\u0006\u0004"}, d2 = {"Lcom/trixsearch/hasikit/ui/navigation/Screen$AdvancedSettings;", "Lcom/trixsearch/hasikit/ui/navigation/Screen;", "<init>", "()V", "app_debug"})
    public static final class AdvancedSettings extends com.trixsearch.hasikit.ui.navigation.Screen {
        @org.jetbrains.annotations.NotNull()
        public static final com.trixsearch.hasikit.ui.navigation.Screen.AdvancedSettings INSTANCE = null;
        
        private AdvancedSettings() {
        }
    }
    
    @kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003\u00a8\u0006\u0004"}, d2 = {"Lcom/trixsearch/hasikit/ui/navigation/Screen$Auth;", "Lcom/trixsearch/hasikit/ui/navigation/Screen;", "<init>", "()V", "app_debug"})
    public static final class Auth extends com.trixsearch.hasikit.ui.navigation.Screen {
        @org.jetbrains.annotations.NotNull()
        public static final com.trixsearch.hasikit.ui.navigation.Screen.Auth INSTANCE = null;
        
        private Auth() {
        }
    }
    
    @kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003\u00a8\u0006\u0004"}, d2 = {"Lcom/trixsearch/hasikit/ui/navigation/Screen$Home;", "Lcom/trixsearch/hasikit/ui/navigation/Screen;", "<init>", "()V", "app_debug"})
    public static final class Home extends com.trixsearch.hasikit.ui.navigation.Screen {
        @org.jetbrains.annotations.NotNull()
        public static final com.trixsearch.hasikit.ui.navigation.Screen.Home INSTANCE = null;
        
        private Home() {
        }
    }
    
    @kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003\u00a8\u0006\u0004"}, d2 = {"Lcom/trixsearch/hasikit/ui/navigation/Screen$Language;", "Lcom/trixsearch/hasikit/ui/navigation/Screen;", "<init>", "()V", "app_debug"})
    public static final class Language extends com.trixsearch.hasikit.ui.navigation.Screen {
        @org.jetbrains.annotations.NotNull()
        public static final com.trixsearch.hasikit.ui.navigation.Screen.Language INSTANCE = null;
        
        private Language() {
        }
    }
    
    @kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003\u00a8\u0006\u0004"}, d2 = {"Lcom/trixsearch/hasikit/ui/navigation/Screen$Library;", "Lcom/trixsearch/hasikit/ui/navigation/Screen;", "<init>", "()V", "app_debug"})
    public static final class Library extends com.trixsearch.hasikit.ui.navigation.Screen {
        @org.jetbrains.annotations.NotNull()
        public static final com.trixsearch.hasikit.ui.navigation.Screen.Library INSTANCE = null;
        
        private Library() {
        }
    }
    
    @kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0004\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u000e\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0005J\u000e\u0010\u0007\u001a\u00020\u00052\u0006\u0010\b\u001a\u00020\u0005\u00a8\u0006\t"}, d2 = {"Lcom/trixsearch/hasikit/ui/navigation/Screen$Player;", "Lcom/trixsearch/hasikit/ui/navigation/Screen;", "<init>", "()V", "createRoute", "", "videoId", "decodeId", "raw", "app_debug"})
    public static final class Player extends com.trixsearch.hasikit.ui.navigation.Screen {
        @org.jetbrains.annotations.NotNull()
        public static final com.trixsearch.hasikit.ui.navigation.Screen.Player INSTANCE = null;
        
        private Player() {
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String createRoute(@org.jetbrains.annotations.NotNull()
        java.lang.String videoId) {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String decodeId(@org.jetbrains.annotations.NotNull()
        java.lang.String raw) {
            return null;
        }
    }
    
    @kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003\u00a8\u0006\u0004"}, d2 = {"Lcom/trixsearch/hasikit/ui/navigation/Screen$RequestContent;", "Lcom/trixsearch/hasikit/ui/navigation/Screen;", "<init>", "()V", "app_debug"})
    public static final class RequestContent extends com.trixsearch.hasikit.ui.navigation.Screen {
        @org.jetbrains.annotations.NotNull()
        public static final com.trixsearch.hasikit.ui.navigation.Screen.RequestContent INSTANCE = null;
        
        private RequestContent() {
        }
    }
    
    @kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003\u00a8\u0006\u0004"}, d2 = {"Lcom/trixsearch/hasikit/ui/navigation/Screen$Search;", "Lcom/trixsearch/hasikit/ui/navigation/Screen;", "<init>", "()V", "app_debug"})
    public static final class Search extends com.trixsearch.hasikit.ui.navigation.Screen {
        @org.jetbrains.annotations.NotNull()
        public static final com.trixsearch.hasikit.ui.navigation.Screen.Search INSTANCE = null;
        
        private Search() {
        }
    }
    
    @kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003\u00a8\u0006\u0004"}, d2 = {"Lcom/trixsearch/hasikit/ui/navigation/Screen$Settings;", "Lcom/trixsearch/hasikit/ui/navigation/Screen;", "<init>", "()V", "app_debug"})
    public static final class Settings extends com.trixsearch.hasikit.ui.navigation.Screen {
        @org.jetbrains.annotations.NotNull()
        public static final com.trixsearch.hasikit.ui.navigation.Screen.Settings INSTANCE = null;
        
        private Settings() {
        }
    }
    
    @kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003\u00a8\u0006\u0004"}, d2 = {"Lcom/trixsearch/hasikit/ui/navigation/Screen$StorageManagement;", "Lcom/trixsearch/hasikit/ui/navigation/Screen;", "<init>", "()V", "app_debug"})
    public static final class StorageManagement extends com.trixsearch.hasikit.ui.navigation.Screen {
        @org.jetbrains.annotations.NotNull()
        public static final com.trixsearch.hasikit.ui.navigation.Screen.StorageManagement INSTANCE = null;
        
        private StorageManagement() {
        }
    }
}