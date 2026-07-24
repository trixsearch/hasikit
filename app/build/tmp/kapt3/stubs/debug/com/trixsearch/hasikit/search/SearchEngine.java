package com.trixsearch.hasikit.search;

@kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000N\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0006\n\u0002\u0010$\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\"\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010\u0006\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\b\b\u00c6\u0002\u0018\u00002\u00020\u0001:\u0003+,-B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u000e\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\rJ\u000e\u0010\u0017\u001a\u00020\r2\u0006\u0010\u0018\u001a\u00020\rJ\u0016\u0010\u0019\u001a\u00020\u00052\u0006\u0010\u001a\u001a\u00020\r2\u0006\u0010\u001b\u001a\u00020\rJ\u0018\u0010\u001c\u001a\u00020\u001d2\u0006\u0010\u001e\u001a\u00020\r2\u0006\u0010\u001f\u001a\u00020\rH\u0002J\u0016\u0010 \u001a\u00020\u00052\u0006\u0010!\u001a\u00020\u00152\u0006\u0010\"\u001a\u00020#J6\u0010$\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u0002H\'0&0%\"\u0004\b\u0000\u0010\'2\u0012\u0010(\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u0002H\'0&0%2\b\b\u0002\u0010)\u001a\u00020\u0005J\u0016\u0010*\u001a\u00020\u00052\u0006\u0010\u001e\u001a\u00020\r2\u0006\u0010\u001f\u001a\u00020\rR\u000e\u0010\u0004\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u001a\u0010\u000b\u001a\u000e\u0012\u0004\u0012\u00020\r\u0012\u0004\u0012\u00020\r0\fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\r0\u000fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001a\u0010\u0010\u001a\u000e\u0012\u0004\u0012\u00020\r\u0012\u0004\u0012\u00020\r0\fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\r0\u000fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0012\u001a\u00020\u0013X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006."}, d2 = {"Lcom/trixsearch/hasikit/search/SearchEngine;", "", "<init>", "()V", "SCORE_EXACT", "", "SCORE_STARTS_WITH", "SCORE_CONTAINS", "SCORE_FUZZY_MAX", "SCORE_FUZZY_MIN", "SCORE_IGNORE_BELOW", "AUDIO_LANG_MAP", "", "", "SUBTITLE_TRIGGER_TOKENS", "", "AUDIO_TYPE_MAP", "QUALITY_TOKENS", "YEAR_RANGE", "Lkotlin/ranges/IntRange;", "parseIntent", "Lcom/trixsearch/hasikit/search/SearchEngine$SearchIntent;", "rawQuery", "normalize", "raw", "score", "normalizedQuery", "normalizedCandidate", "stringSimilarity", "", "a", "b", "scoreVideo", "intent", "fields", "Lcom/trixsearch/hasikit/search/SearchEngine$VideoFields;", "rank", "", "Lcom/trixsearch/hasikit/search/SearchEngine$SearchResult;", "T", "results", "threshold", "levenshtein", "SearchIntent", "SearchResult", "VideoFields", "app_debug"})
public final class SearchEngine {
    public static final int SCORE_EXACT = 100;
    public static final int SCORE_STARTS_WITH = 90;
    public static final int SCORE_CONTAINS = 80;
    public static final int SCORE_FUZZY_MAX = 79;
    public static final int SCORE_FUZZY_MIN = 60;
    public static final int SCORE_IGNORE_BELOW = 50;
    @org.jetbrains.annotations.NotNull()
    private static final java.util.Map<java.lang.String, java.lang.String> AUDIO_LANG_MAP = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.util.Set<java.lang.String> SUBTITLE_TRIGGER_TOKENS = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.util.Map<java.lang.String, java.lang.String> AUDIO_TYPE_MAP = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.util.Set<java.lang.String> QUALITY_TOKENS = null;
    @org.jetbrains.annotations.NotNull()
    private static final kotlin.ranges.IntRange YEAR_RANGE = null;
    @org.jetbrains.annotations.NotNull()
    public static final com.trixsearch.hasikit.search.SearchEngine INSTANCE = null;
    
    private SearchEngine() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.trixsearch.hasikit.search.SearchEngine.SearchIntent parseIntent(@org.jetbrains.annotations.NotNull()
    java.lang.String rawQuery) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String normalize(@org.jetbrains.annotations.NotNull()
    java.lang.String raw) {
        return null;
    }
    
    public final int score(@org.jetbrains.annotations.NotNull()
    java.lang.String normalizedQuery, @org.jetbrains.annotations.NotNull()
    java.lang.String normalizedCandidate) {
        return 0;
    }
    
    private final double stringSimilarity(java.lang.String a, java.lang.String b) {
        return 0.0;
    }
    
    public final int scoreVideo(@org.jetbrains.annotations.NotNull()
    com.trixsearch.hasikit.search.SearchEngine.SearchIntent intent, @org.jetbrains.annotations.NotNull()
    com.trixsearch.hasikit.search.SearchEngine.VideoFields fields) {
        return 0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final <T extends java.lang.Object>java.util.List<com.trixsearch.hasikit.search.SearchEngine.SearchResult<T>> rank(@org.jetbrains.annotations.NotNull()
    java.util.List<com.trixsearch.hasikit.search.SearchEngine.SearchResult<T>> results, int threshold) {
        return null;
    }
    
    public final int levenshtein(@org.jetbrains.annotations.NotNull()
    java.lang.String a, @org.jetbrains.annotations.NotNull()
    java.lang.String b) {
        return 0;
    }
    
    @kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0005\n\u0002\u0010 \n\u0002\b\u000f\n\u0002\u0010\u000b\n\u0002\b\u0012\b\u0086\b\u0018\u00002\u00020\u0001Bc\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\n\b\u0002\u0010\u0005\u001a\u0004\u0018\u00010\u0006\u0012\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\b\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\t\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\n\u001a\u0004\u0018\u00010\u0003\u0012\u000e\b\u0002\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\u00030\f\u00a2\u0006\u0004\b\r\u0010\u000eJ\u0006\u0010\u001e\u001a\u00020\u0003J\f\u0010\u001f\u001a\b\u0012\u0004\u0012\u00020\u00030\fJ\t\u0010 \u001a\u00020\u0003H\u00c6\u0003J\t\u0010!\u001a\u00020\u0003H\u00c6\u0003J\u0010\u0010\"\u001a\u0004\u0018\u00010\u0006H\u00c6\u0003\u00a2\u0006\u0002\u0010\u0013J\u000b\u0010#\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\u000b\u0010$\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\u000b\u0010%\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\u000b\u0010&\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\u000f\u0010\'\u001a\b\u0012\u0004\u0012\u00020\u00030\fH\u00c6\u0003Jn\u0010(\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\n\b\u0002\u0010\u0005\u001a\u0004\u0018\u00010\u00062\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\b\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\t\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\n\u001a\u0004\u0018\u00010\u00032\u000e\b\u0002\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\u00030\fH\u00c6\u0001\u00a2\u0006\u0002\u0010)J\u0013\u0010*\u001a\u00020\u001c2\b\u0010+\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010,\u001a\u00020\u0006H\u00d6\u0001J\t\u0010-\u001a\u00020\u0003H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0010R\u0015\u0010\u0005\u001a\u0004\u0018\u00010\u0006\u00a2\u0006\n\n\u0002\u0010\u0014\u001a\u0004\b\u0012\u0010\u0013R\u0013\u0010\u0007\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0010R\u0013\u0010\b\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0010R\u0013\u0010\t\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0010R\u0013\u0010\n\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0010R\u0017\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\u00030\f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u001aR\u0011\u0010\u001b\u001a\u00020\u001c8F\u00a2\u0006\u0006\u001a\u0004\b\u001b\u0010\u001d\u00a8\u0006."}, d2 = {"Lcom/trixsearch/hasikit/search/SearchEngine$SearchIntent;", "", "movieName", "", "normalizedName", "year", "", "audioLanguage", "subtitleLanguage", "audioType", "quality", "extraKeywords", "", "<init>", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Integer;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/util/List;)V", "getMovieName", "()Ljava/lang/String;", "getNormalizedName", "getYear", "()Ljava/lang/Integer;", "Ljava/lang/Integer;", "getAudioLanguage", "getSubtitleLanguage", "getAudioType", "getQuality", "getExtraKeywords", "()Ljava/util/List;", "isValid", "", "()Z", "toTelegramQuery", "toTelegramQueryVariants", "component1", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "copy", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Integer;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/util/List;)Lcom/trixsearch/hasikit/search/SearchEngine$SearchIntent;", "equals", "other", "hashCode", "toString", "app_debug"})
    public static final class SearchIntent {
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String movieName = null;
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String normalizedName = null;
        @org.jetbrains.annotations.Nullable()
        private final java.lang.Integer year = null;
        @org.jetbrains.annotations.Nullable()
        private final java.lang.String audioLanguage = null;
        @org.jetbrains.annotations.Nullable()
        private final java.lang.String subtitleLanguage = null;
        @org.jetbrains.annotations.Nullable()
        private final java.lang.String audioType = null;
        @org.jetbrains.annotations.Nullable()
        private final java.lang.String quality = null;
        @org.jetbrains.annotations.NotNull()
        private final java.util.List<java.lang.String> extraKeywords = null;
        
        public SearchIntent(@org.jetbrains.annotations.NotNull()
        java.lang.String movieName, @org.jetbrains.annotations.NotNull()
        java.lang.String normalizedName, @org.jetbrains.annotations.Nullable()
        java.lang.Integer year, @org.jetbrains.annotations.Nullable()
        java.lang.String audioLanguage, @org.jetbrains.annotations.Nullable()
        java.lang.String subtitleLanguage, @org.jetbrains.annotations.Nullable()
        java.lang.String audioType, @org.jetbrains.annotations.Nullable()
        java.lang.String quality, @org.jetbrains.annotations.NotNull()
        java.util.List<java.lang.String> extraKeywords) {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String getMovieName() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String getNormalizedName() {
            return null;
        }
        
        @org.jetbrains.annotations.Nullable()
        public final java.lang.Integer getYear() {
            return null;
        }
        
        @org.jetbrains.annotations.Nullable()
        public final java.lang.String getAudioLanguage() {
            return null;
        }
        
        @org.jetbrains.annotations.Nullable()
        public final java.lang.String getSubtitleLanguage() {
            return null;
        }
        
        @org.jetbrains.annotations.Nullable()
        public final java.lang.String getAudioType() {
            return null;
        }
        
        @org.jetbrains.annotations.Nullable()
        public final java.lang.String getQuality() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.List<java.lang.String> getExtraKeywords() {
            return null;
        }
        
        public final boolean isValid() {
            return false;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String toTelegramQuery() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.List<java.lang.String> toTelegramQueryVariants() {
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
        
        @org.jetbrains.annotations.Nullable()
        public final java.lang.Integer component3() {
            return null;
        }
        
        @org.jetbrains.annotations.Nullable()
        public final java.lang.String component4() {
            return null;
        }
        
        @org.jetbrains.annotations.Nullable()
        public final java.lang.String component5() {
            return null;
        }
        
        @org.jetbrains.annotations.Nullable()
        public final java.lang.String component6() {
            return null;
        }
        
        @org.jetbrains.annotations.Nullable()
        public final java.lang.String component7() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.List<java.lang.String> component8() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.trixsearch.hasikit.search.SearchEngine.SearchIntent copy(@org.jetbrains.annotations.NotNull()
        java.lang.String movieName, @org.jetbrains.annotations.NotNull()
        java.lang.String normalizedName, @org.jetbrains.annotations.Nullable()
        java.lang.Integer year, @org.jetbrains.annotations.Nullable()
        java.lang.String audioLanguage, @org.jetbrains.annotations.Nullable()
        java.lang.String subtitleLanguage, @org.jetbrains.annotations.Nullable()
        java.lang.String audioType, @org.jetbrains.annotations.Nullable()
        java.lang.String quality, @org.jetbrains.annotations.NotNull()
        java.util.List<java.lang.String> extraKeywords) {
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
    
    @kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u000f\n\u0002\u0010\u000b\n\u0002\b\u0004\b\u0086\b\u0018\u0000*\u0004\b\u0000\u0010\u00012\u00020\u0002B!\u0012\u0006\u0010\u0003\u001a\u00028\u0000\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\u0004\b\b\u0010\tJ\u000e\u0010\u0011\u001a\u00028\u0000H\u00c6\u0003\u00a2\u0006\u0002\u0010\u000bJ\t\u0010\u0012\u001a\u00020\u0005H\u00c6\u0003J\t\u0010\u0013\u001a\u00020\u0007H\u00c6\u0003J2\u0010\u0014\u001a\b\u0012\u0004\u0012\u00028\u00000\u00002\b\b\u0002\u0010\u0003\u001a\u00028\u00002\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u0007H\u00c6\u0001\u00a2\u0006\u0002\u0010\u0015J\u0013\u0010\u0016\u001a\u00020\u00172\b\u0010\u0018\u001a\u0004\u0018\u00010\u0002H\u00d6\u0003J\t\u0010\u0019\u001a\u00020\u0005H\u00d6\u0001J\t\u0010\u001a\u001a\u00020\u0007H\u00d6\u0001R\u0013\u0010\u0003\u001a\u00028\u0000\u00a2\u0006\n\n\u0002\u0010\f\u001a\u0004\b\n\u0010\u000bR\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u0011\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010\u00a8\u0006\u001b"}, d2 = {"Lcom/trixsearch/hasikit/search/SearchEngine$SearchResult;", "T", "", "item", "score", "", "matchedField", "", "<init>", "(Ljava/lang/Object;ILjava/lang/String;)V", "getItem", "()Ljava/lang/Object;", "Ljava/lang/Object;", "getScore", "()I", "getMatchedField", "()Ljava/lang/String;", "component1", "component2", "component3", "copy", "(Ljava/lang/Object;ILjava/lang/String;)Lcom/trixsearch/hasikit/search/SearchEngine$SearchResult;", "equals", "", "other", "hashCode", "toString", "app_debug"})
    public static final class SearchResult<T extends java.lang.Object> {
        private final T item = null;
        private final int score = 0;
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String matchedField = null;
        
        public SearchResult(T item, int score, @org.jetbrains.annotations.NotNull()
        java.lang.String matchedField) {
            super();
        }
        
        public final T getItem() {
            return null;
        }
        
        public final int getScore() {
            return 0;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String getMatchedField() {
            return null;
        }
        
        public final T component1() {
            return null;
        }
        
        public final int component2() {
            return 0;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String component3() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.trixsearch.hasikit.search.SearchEngine.SearchResult<T> copy(T item, int score, @org.jetbrains.annotations.NotNull()
        java.lang.String matchedField) {
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
    
    @kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0010\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001B)\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0003\u00a2\u0006\u0004\b\u0007\u0010\bJ\t\u0010\u000e\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u000f\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0010\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0011\u001a\u00020\u0003H\u00c6\u0003J1\u0010\u0012\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u0003H\u00c6\u0001J\u0013\u0010\u0013\u001a\u00020\u00142\b\u0010\u0015\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0016\u001a\u00020\u0017H\u00d6\u0001J\t\u0010\u0018\u001a\u00020\u0003H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\nR\u0011\u0010\u0005\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\nR\u0011\u0010\u0006\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\n\u00a8\u0006\u0019"}, d2 = {"Lcom/trixsearch/hasikit/search/SearchEngine$VideoFields;", "", "title", "", "fileName", "caption", "sourceLabel", "<init>", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V", "getTitle", "()Ljava/lang/String;", "getFileName", "getCaption", "getSourceLabel", "component1", "component2", "component3", "component4", "copy", "equals", "", "other", "hashCode", "", "toString", "app_debug"})
    public static final class VideoFields {
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String title = null;
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String fileName = null;
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String caption = null;
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String sourceLabel = null;
        
        public VideoFields(@org.jetbrains.annotations.NotNull()
        java.lang.String title, @org.jetbrains.annotations.NotNull()
        java.lang.String fileName, @org.jetbrains.annotations.NotNull()
        java.lang.String caption, @org.jetbrains.annotations.NotNull()
        java.lang.String sourceLabel) {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String getTitle() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String getFileName() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String getCaption() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String getSourceLabel() {
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
        public final java.lang.String component3() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String component4() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.trixsearch.hasikit.search.SearchEngine.VideoFields copy(@org.jetbrains.annotations.NotNull()
        java.lang.String title, @org.jetbrains.annotations.NotNull()
        java.lang.String fileName, @org.jetbrains.annotations.NotNull()
        java.lang.String caption, @org.jetbrains.annotations.NotNull()
        java.lang.String sourceLabel) {
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
}