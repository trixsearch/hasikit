package com.trixsearch.hasikit.search

// ─────────────────────────────────────────────────────────────────────────────
// SearchEngine — Hasikit Smart Search V4
//
// Pure Kotlin object. No Android dependencies. Fully unit-testable.
//
// Responsibilities:
//   1. Parse raw query into structured SearchIntent (movie name, year, language,
//      quality, audio type, subtitle language)
//   2. Normalize titles for fuzzy comparison (strip metadata, collapse chars)
//   3. Score a candidate string against a query (0–100)
//   4. Rank a list of SearchResult by score DESC, filter below threshold
//
// Scoring tiers:
//   100  Exact match (normalized)
//    90  Starts-with match
//    80  Contains match
//   60–79 Fuzzy match (edit-distance based, scaled by similarity)
//   < 50  Ignored — not returned to UI
// ─────────────────────────────────────────────────────────────────────────────

object SearchEngine {

    // ── Score thresholds ──────────────────────────────────────────────────────

    const val SCORE_EXACT = 100
    const val SCORE_STARTS_WITH = 90
    const val SCORE_CONTAINS = 80
    const val SCORE_FUZZY_MAX = 79
    const val SCORE_FUZZY_MIN = 60
    const val SCORE_IGNORE_BELOW = 50

    // ── Audio language keyword map ────────────────────────────────────────────
    // Maps short codes and full names to a canonical language label.

    private val AUDIO_LANG_MAP = mapOf(
        "hin" to "Hindi", "hindi" to "Hindi",
        "eng" to "English", "english" to "English",
        "tam" to "Tamil", "tamil" to "Tamil",
        "tel" to "Telugu", "telugu" to "Telugu",
        "mal" to "Malayalam", "malayalam" to "Malayalam",
        "kan" to "Kannada", "kannada" to "Kannada",
        "ben" to "Bengali", "bengali" to "Bengali",
        "mar" to "Marathi", "marathi" to "Marathi",
        "guj" to "Gujarati", "gujarati" to "Gujarati",
        "pun" to "Punjabi", "punjabi" to "Punjabi"
    )

    // ── Subtitle trigger tokens ───────────────────────────────────────────────
    // When one of these appears, the next language token is subtitle language.

    private val SUBTITLE_TRIGGER_TOKENS = setOf(
        "sub", "subs", "subtitle", "subtitles", "hardsub", "softsub"
    )

    // ── Audio type keywords ───────────────────────────────────────────────────

    private val AUDIO_TYPE_MAP = mapOf(
        "dual" to "Dual Audio",
        "dual-audio" to "Dual Audio",
        "dualaudio" to "Dual Audio",
        "multi" to "Multi Audio",
        "multi-audio" to "Multi Audio",
        "multiaudio" to "Multi Audio",
        "dubbed" to "Dubbed",
        "original" to "Original Audio",
        "original-audio" to "Original Audio"
    )

    // ── Quality keywords ──────────────────────────────────────────────────────

    private val QUALITY_TOKENS = setOf(
        "240p", "360p", "480p", "720p", "1080p", "1440p", "2160p", "4k", "uhd", "hd", "fhd"
    )

    // ── Year detection range ──────────────────────────────────────────────────

    private val YEAR_RANGE = 1950..2030

    // ─────────────────────────────────────────────────────────────────────────
    // SearchIntent — structured representation of a parsed query
    // ─────────────────────────────────────────────────────────────────────────

    data class SearchIntent(
        // Core movie/show name after stripping metadata tokens
        val movieName: String,
        // Normalized version of movieName for fuzzy matching
        val normalizedName: String,
        val year: Int? = null,
        val audioLanguage: String? = null,
        val subtitleLanguage: String? = null,
        val audioType: String? = null,
        val quality: String? = null,
        val extraKeywords: List<String> = emptyList()
    ) {
        val isValid: Boolean get() = movieName.isNotBlank()

        // Primary Telegram query — raw movie name so TDLib's tokenizer handles it
        fun toTelegramQuery(): String = movieName.trim()

        // Expanded query variants for multi-pass Telegram search.
        // Uploaders commonly embed language/quality in captions, so we search
        // "Pushpa Hindi", "Pushpa 1080p", etc. in addition to just "Pushpa".
        fun toTelegramQueryVariants(): List<String> {
            val base = movieName.trim()
            val variants = mutableListOf(base)
            audioLanguage?.let { variants.add("$base $it") }
            quality?.let { variants.add("$base $it") }
            if (audioLanguage != null && quality != null) {
                variants.add("$base $audioLanguage $quality")
            }
            return variants.distinct().filter { it.isNotBlank() }
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SearchResult — a ranked candidate returned to the UI
    // ─────────────────────────────────────────────────────────────────────────

    data class SearchResult<T>(
        val item: T,
        val score: Int,
        // Which field produced the best score (for debug logging)
        val matchedField: String = ""
    )

    // ─────────────────────────────────────────────────────────────────────────
    // parseIntent — tokenize raw query and extract structured metadata
    // ─────────────────────────────────────────────────────────────────────────

    fun parseIntent(rawQuery: String): SearchIntent {
        val tokens = rawQuery.trim().lowercase()
            .split(Regex("\\s+"))
            .filter { it.isNotBlank() }

        var year: Int? = null
        var audioLanguage: String? = null
        var subtitleLanguage: String? = null
        var audioType: String? = null
        var quality: String? = null
        val nameTokens = mutableListOf<String>()
        val extraKeywords = mutableListOf<String>()

        // When true, the next language token is treated as subtitle language
        var nextIsSubtitleLang = false

        var i = 0
        while (i < tokens.size) {
            val token = tokens[i]

            // Year detection
            val asInt = token.toIntOrNull()
            if (asInt != null && asInt in YEAR_RANGE) {
                year = asInt; i++; continue
            }

            // Quality
            if (token in QUALITY_TOKENS) {
                quality = token.uppercase().replace("UHD", "4K"); i++; continue
            }

            // Audio type
            if (AUDIO_TYPE_MAP.containsKey(token)) {
                audioType = AUDIO_TYPE_MAP[token]; i++; continue
            }

            // Subtitle trigger — next language token goes to subtitleLanguage
            if (token in SUBTITLE_TRIGGER_TOKENS) {
                nextIsSubtitleLang = true; i++; continue
            }

            // "audio" keyword — peek ahead for a language token
            if (token == "audio") {
                val next = tokens.getOrNull(i + 1)
                if (next != null && AUDIO_LANG_MAP.containsKey(next)) {
                    audioLanguage = AUDIO_LANG_MAP[next]; i += 2; continue
                }
                i++; continue
            }

            // Language token
            val lang = AUDIO_LANG_MAP[token]
            if (lang != null) {
                if (nextIsSubtitleLang) {
                    subtitleLanguage = lang
                    nextIsSubtitleLang = false
                } else {
                    audioLanguage = lang
                }
                i++; continue
            }

            // Everything else is part of the movie name
            nameTokens.add(token)
            i++
        }

        val movieName = nameTokens.joinToString(" ").trim()
        return SearchIntent(
            movieName = movieName,
            normalizedName = normalize(movieName),
            year = year,
            audioLanguage = audioLanguage,
            subtitleLanguage = subtitleLanguage,
            audioType = audioType,
            quality = quality,
            extraKeywords = extraKeywords
        )
    }

    // ─────────────────────────────────────────────────────────────────────────
    // normalize — prepare a string for fuzzy comparison
    //
    // Steps:
    //   1. Lowercase
    //   2. Remove file extension
    //   3. Replace dots, underscores, hyphens with spaces
    //   4. Strip known metadata tokens (resolution, codec, source, language tags)
    //   5. Remove all non-alphanumeric characters
    //   6. Collapse runs of 3+ identical chars to 2 (pushpaaa→pushpaa)
    //   7. Trim and collapse whitespace
    // ─────────────────────────────────────────────────────────────────────────

    fun normalize(raw: String): String {
        var s = raw.lowercase()

        // Remove file extension
        s = s.replace(Regex("\\.(mp4|mkv|webm|mov|m4v|avi|flv|wmv)$"), "")

        // Replace separators with space
        s = s.replace(Regex("[._\\-]"), " ")

        // Strip common metadata tokens that uploaders embed in titles
        s = s.replace(
            Regex(
                "\\b(1080p|720p|480p|360p|240p|2160p|4k|uhd|hd|fhd|" +
                    "x264|x265|h264|h265|hevc|avc|xvid|divx|" +
                    "bluray|blu ray|bdrip|brrip|web dl|webrip|webdl|" +
                    "hdrip|dvdrip|dvdscr|hdcam|cam|ts|" +
                    "aac|ac3|dts|mp3|flac|" +
                    "hindi|english|tamil|telugu|malayalam|kannada|bengali|marathi|gujarati|punjabi|" +
                    "hin|eng|tam|tel|mal|kan|ben|mar|guj|pun|" +
                    "dual|multi|dubbed|sub|subs|subtitle|subtitles|" +
                    "extended|theatrical|directors|cut|remastered|unrated|" +
                    "proper|repack|internal|limited|retail)\\b",
                RegexOption.IGNORE_CASE
            ), " "
        )

        // Remove all non-alphanumeric characters (keep spaces)
        s = s.replace(Regex("[^a-z0-9 ]"), "")

        // Collapse runs of 3+ identical characters to 2
        // "pushpaaa" → "pushpaa", "baahubali" stays "baahubali"
        s = s.replace(Regex("(.)\\1{2,}")) { mr -> mr.groupValues[1].repeat(2) }

        // Collapse multiple spaces
        s = s.replace(Regex("\\s+"), " ").trim()

        return s
    }

    // ─────────────────────────────────────────────────────────────────────────
    // score — compute match score (0–100) between normalized query and candidate
    // ─────────────────────────────────────────────────────────────────────────

    fun score(normalizedQuery: String, normalizedCandidate: String): Int {
        if (normalizedQuery.isBlank() || normalizedCandidate.isBlank()) return 0

        // Exact match
        if (normalizedCandidate == normalizedQuery) return SCORE_EXACT

        // Starts-with
        if (normalizedCandidate.startsWith(normalizedQuery)) return SCORE_STARTS_WITH

        // Contains
        if (normalizedCandidate.contains(normalizedQuery)) return SCORE_CONTAINS

        // Word-level: all query words present in candidate words
        val queryWords = normalizedQuery.split(" ").filter { it.length > 1 }
        val candidateWords = normalizedCandidate.split(" ").toSet()
        if (queryWords.isNotEmpty() && queryWords.all { qw ->
                candidateWords.any { cw -> cw.contains(qw) || qw.contains(cw) }
            }) {
            return SCORE_CONTAINS - 2
        }

        // Fuzzy match via normalized edit distance
        val editDist = levenshtein(normalizedQuery, normalizedCandidate)
        val maxLen = maxOf(normalizedQuery.length, normalizedCandidate.length).coerceAtLeast(1)
        val similarity = 1.0 - editDist.toDouble() / maxLen

        if (similarity >= 0.6) {
            val fuzzyScore = SCORE_FUZZY_MIN +
                ((similarity - 0.6) / 0.4 * (SCORE_FUZZY_MAX - SCORE_FUZZY_MIN)).toInt()
            return fuzzyScore.coerceIn(SCORE_FUZZY_MIN, SCORE_FUZZY_MAX)
        }

        // Partial word fuzzy: check each query word against each candidate word
        val bestWordScore = queryWords.maxOfOrNull { qw ->
            candidateWords.maxOfOrNull { cw ->
                val d = levenshtein(qw, cw)
                val m = maxOf(qw.length, cw.length).coerceAtLeast(1)
                val sim = 1.0 - d.toDouble() / m
                if (sim >= 0.7) (SCORE_FUZZY_MIN + (sim - 0.7) / 0.3 * 10).toInt() else 0
            } ?: 0
        } ?: 0

        return bestWordScore
    }

    // ─────────────────────────────────────────────────────────────────────────
    // VideoFields — fields to score a video against
    // ─────────────────────────────────────────────────────────────────────────

    data class VideoFields(
        val title: String,
        val fileName: String,
        val caption: String,
        val sourceLabel: String = ""
    )

    // ─────────────────────────────────────────────────────────────────────────
    // scoreVideo — score a video against a SearchIntent across all fields.
    // Returns the highest score found, with metadata bonus applied.
    // ─────────────────────────────────────────────────────────────────────────

    fun scoreVideo(intent: SearchIntent, fields: VideoFields): Int {
        if (!intent.isValid) return 0

        val nq = intent.normalizedName

        val titleScore = score(nq, normalize(fields.title))
        val fileScore = score(nq, normalize(fields.fileName))
        val captionScore = score(nq, normalize(fields.caption))

        var best = maxOf(titleScore, fileScore, captionScore)

        // Metadata bonus: year/language/quality match boosts score by up to +8
        // so "Pushpa 2024 Hindi" ranks above "Pushpa 2022 Tamil" when user asked for 2024 Hindi
        if (best >= SCORE_IGNORE_BELOW) {
            val rawAll = "${fields.title} ${fields.fileName} ${fields.caption}".lowercase()
            var bonus = 0
            intent.year?.let { y -> if (rawAll.contains(y.toString())) bonus += 3 }
            intent.audioLanguage?.let { lang ->
                val shortCode = AUDIO_LANG_MAP.entries
                    .firstOrNull { it.value == lang }?.key ?: ""
                if (rawAll.contains(lang.lowercase()) || rawAll.contains(shortCode)) bonus += 3
            }
            intent.quality?.let { q ->
                if (rawAll.contains(q.lowercase())) bonus += 2
            }
            best = (best + bonus).coerceAtMost(100)
        }

        return best
    }

    // ─────────────────────────────────────────────────────────────────────────
    // rank — sort SearchResults by score DESC, filter below threshold
    // ─────────────────────────────────────────────────────────────────────────

    fun <T> rank(
        results: List<SearchResult<T>>,
        threshold: Int = SCORE_IGNORE_BELOW
    ): List<SearchResult<T>> =
        results
            .filter { it.score >= threshold }
            .sortedByDescending { it.score }

    // ─────────────────────────────────────────────────────────────────────────
    // levenshtein — standard edit distance, O(m*n) time, O(n) space
    // ─────────────────────────────────────────────────────────────────────────

    fun levenshtein(a: String, b: String): Int {
        if (a == b) return 0
        if (a.isEmpty()) return b.length
        if (b.isEmpty()) return a.length

        // Early exit: length difference alone exceeds half the longer string
        if (kotlin.math.abs(a.length - b.length) > maxOf(a.length, b.length) / 2) {
            return maxOf(a.length, b.length)
        }

        var prev = IntArray(b.length + 1) { it }
        val curr = IntArray(b.length + 1)

        for (i in 1..a.length) {
            curr[0] = i
            for (j in 1..b.length) {
                val cost = if (a[i - 1] == b[j - 1]) 0 else 1
                curr[j] = minOf(curr[j - 1] + 1, prev[j] + 1, prev[j - 1] + cost)
            }
            prev = curr.copyOf()
        }
        return prev[b.length]
    }
}
