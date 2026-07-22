package com.trixsearch.hasikit.util;

import android.content.Context;
import android.content.res.Configuration;
import dagger.hilt.android.qualifiers.ApplicationContext;
import kotlinx.coroutines.flow.Flow;
import java.util.Locale;
import javax.inject.Inject;
import javax.inject.Singleton;

@kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0015\b\u0086\u0081\u0002\u0018\u0000 \u00172\b\u0012\u0004\u0012\u00020\u00000\u0001:\u0001\u0017B!\b\u0002\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0003\u00a2\u0006\u0004\b\u0006\u0010\u0007R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\tR\u0011\u0010\u0005\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\tj\u0002\b\fj\u0002\b\rj\u0002\b\u000ej\u0002\b\u000fj\u0002\b\u0010j\u0002\b\u0011j\u0002\b\u0012j\u0002\b\u0013j\u0002\b\u0014j\u0002\b\u0015j\u0002\b\u0016\u00a8\u0006\u0018"}, d2 = {"Lcom/trixsearch/hasikit/util/AppLanguage;", "", "code", "", "displayName", "nativeName", "<init>", "(Ljava/lang/String;ILjava/lang/String;Ljava/lang/String;Ljava/lang/String;)V", "getCode", "()Ljava/lang/String;", "getDisplayName", "getNativeName", "SYSTEM", "ENGLISH", "HINDI", "MARATHI", "TAMIL", "TELUGU", "KANNADA", "MALAYALAM", "GUJARATI", "PUNJABI", "BENGALI", "Companion", "app_debug"})
public enum AppLanguage {
    /*public static final*/ SYSTEM /* = new SYSTEM(null, null, null) */,
    /*public static final*/ ENGLISH /* = new ENGLISH(null, null, null) */,
    /*public static final*/ HINDI /* = new HINDI(null, null, null) */,
    /*public static final*/ MARATHI /* = new MARATHI(null, null, null) */,
    /*public static final*/ TAMIL /* = new TAMIL(null, null, null) */,
    /*public static final*/ TELUGU /* = new TELUGU(null, null, null) */,
    /*public static final*/ KANNADA /* = new KANNADA(null, null, null) */,
    /*public static final*/ MALAYALAM /* = new MALAYALAM(null, null, null) */,
    /*public static final*/ GUJARATI /* = new GUJARATI(null, null, null) */,
    /*public static final*/ PUNJABI /* = new PUNJABI(null, null, null) */,
    /*public static final*/ BENGALI /* = new BENGALI(null, null, null) */;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String code = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String displayName = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String nativeName = null;
    @org.jetbrains.annotations.NotNull()
    public static final com.trixsearch.hasikit.util.AppLanguage.Companion Companion = null;
    
    AppLanguage(java.lang.String code, java.lang.String displayName, java.lang.String nativeName) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getCode() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getDisplayName() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getNativeName() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public static kotlin.enums.EnumEntries<com.trixsearch.hasikit.util.AppLanguage> getEntries() {
        return null;
    }
    
    @kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u000e\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007\u00a8\u0006\b"}, d2 = {"Lcom/trixsearch/hasikit/util/AppLanguage$Companion;", "", "<init>", "()V", "fromCode", "Lcom/trixsearch/hasikit/util/AppLanguage;", "code", "", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.trixsearch.hasikit.util.AppLanguage fromCode(@org.jetbrains.annotations.NotNull()
        java.lang.String code) {
            return null;
        }
    }
}