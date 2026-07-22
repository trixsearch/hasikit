package com.trixsearch.hasikit.domain.model;

@kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\t\n\u0002\b\u0018\n\u0002\u0010\u000b\n\u0002\b\u0004\b\u0086\b\u0018\u00002\u00020\u0001BC\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\n\b\u0002\u0010\b\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\t\u001a\u0004\u0018\u00010\n\u0012\n\b\u0002\u0010\u000b\u001a\u0004\u0018\u00010\f\u00a2\u0006\u0004\b\r\u0010\u000eJ\t\u0010\u001c\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u001d\u001a\u00020\u0005H\u00c6\u0003J\t\u0010\u001e\u001a\u00020\u0007H\u00c6\u0003J\u000b\u0010\u001f\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\u0010\u0010 \u001a\u0004\u0018\u00010\nH\u00c6\u0003\u00a2\u0006\u0002\u0010\u0017J\u0010\u0010!\u001a\u0004\u0018\u00010\fH\u00c6\u0003\u00a2\u0006\u0002\u0010\u001aJP\u0010\"\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\n\b\u0002\u0010\b\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\t\u001a\u0004\u0018\u00010\n2\n\b\u0002\u0010\u000b\u001a\u0004\u0018\u00010\fH\u00c6\u0001\u00a2\u0006\u0002\u0010#J\u0013\u0010$\u001a\u00020%2\b\u0010&\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\'\u001a\u00020\nH\u00d6\u0001J\t\u0010(\u001a\u00020\u0003H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012R\u0011\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014R\u0013\u0010\b\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0010R\u0015\u0010\t\u001a\u0004\u0018\u00010\n\u00a2\u0006\n\n\u0002\u0010\u0018\u001a\u0004\b\u0016\u0010\u0017R\u0015\u0010\u000b\u001a\u0004\u0018\u00010\f\u00a2\u0006\n\n\u0002\u0010\u001b\u001a\u0004\b\u0019\u0010\u001a\u00a8\u0006)"}, d2 = {"Lcom/trixsearch/hasikit/domain/model/DownloadTask;", "", "videoId", "", "state", "Lcom/trixsearch/hasikit/domain/model/DownloadState;", "progress", "", "localPath", "errorCode", "", "downloadId", "", "<init>", "(Ljava/lang/String;Lcom/trixsearch/hasikit/domain/model/DownloadState;FLjava/lang/String;Ljava/lang/Integer;Ljava/lang/Long;)V", "getVideoId", "()Ljava/lang/String;", "getState", "()Lcom/trixsearch/hasikit/domain/model/DownloadState;", "getProgress", "()F", "getLocalPath", "getErrorCode", "()Ljava/lang/Integer;", "Ljava/lang/Integer;", "getDownloadId", "()Ljava/lang/Long;", "Ljava/lang/Long;", "component1", "component2", "component3", "component4", "component5", "component6", "copy", "(Ljava/lang/String;Lcom/trixsearch/hasikit/domain/model/DownloadState;FLjava/lang/String;Ljava/lang/Integer;Ljava/lang/Long;)Lcom/trixsearch/hasikit/domain/model/DownloadTask;", "equals", "", "other", "hashCode", "toString", "app_debug"})
public final class DownloadTask {
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String videoId = null;
    @org.jetbrains.annotations.NotNull()
    private final com.trixsearch.hasikit.domain.model.DownloadState state = null;
    private final float progress = 0.0F;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String localPath = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.Integer errorCode = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.Long downloadId = null;
    
    public DownloadTask(@org.jetbrains.annotations.NotNull()
    java.lang.String videoId, @org.jetbrains.annotations.NotNull()
    com.trixsearch.hasikit.domain.model.DownloadState state, float progress, @org.jetbrains.annotations.Nullable()
    java.lang.String localPath, @org.jetbrains.annotations.Nullable()
    java.lang.Integer errorCode, @org.jetbrains.annotations.Nullable()
    java.lang.Long downloadId) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getVideoId() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.trixsearch.hasikit.domain.model.DownloadState getState() {
        return null;
    }
    
    public final float getProgress() {
        return 0.0F;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getLocalPath() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Integer getErrorCode() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Long getDownloadId() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component1() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.trixsearch.hasikit.domain.model.DownloadState component2() {
        return null;
    }
    
    public final float component3() {
        return 0.0F;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component4() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Integer component5() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Long component6() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.trixsearch.hasikit.domain.model.DownloadTask copy(@org.jetbrains.annotations.NotNull()
    java.lang.String videoId, @org.jetbrains.annotations.NotNull()
    com.trixsearch.hasikit.domain.model.DownloadState state, float progress, @org.jetbrains.annotations.Nullable()
    java.lang.String localPath, @org.jetbrains.annotations.Nullable()
    java.lang.Integer errorCode, @org.jetbrains.annotations.Nullable()
    java.lang.Long downloadId) {
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