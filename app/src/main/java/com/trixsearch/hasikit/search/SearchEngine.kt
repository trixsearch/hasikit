package com.trixsearch.hasikit.search

object SearchEngine {

    const val SCORE_EXACT = 100
    const val SCORE_STARTS_WITH = 90
    const val SCORE_CONTAINS = 80
    const val SCORE_FUZZY_MAX = 79
    const val SCORE_FUZZY_MIN = 60
    const val SCORE_IGNORE_BELOW = 50

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

    private val SUBTITLE_TRIGGER_TOKENS = setOf(
        "sub", "subs", "subtitle", "subtitles", "hardsub", "softsub"
    )

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

    private val QUALITY_TOKENS = setOf(
        "240p", "360p", "480p", "720p", "1080p", "1440p", "2160p", "4k", "uhd", "hd", "fhd"
    )

    private val YEAR_RANGE = 1950..2030

    data class SearchIntent(
        val movieName: String,
        val normalizedName: String,
        val year: Int? = null,
        val audioLanguage: String? = null,
        val subtitleLanguage: String? = null,
        val audioType: String? = null,
        val quality: String? = null,
        val extraKeywords: List<String> = emptyList()
    ) {
        val isValid: Boolean get() = movieName.isNotBlank()

        fun toTelegramQuery(): String = movieName.trim()

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

    data class SearchResult<T>(
        val item: T,
        val score: Int,
        val matchedField: String = ""
    )

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
        var nextIsSubtitleLang = false

        var i = 0
        while (i < tokens.size) {
            val token = tokens[i]

            val asInt = token.toIntOrNull()
            if (asInt != null && asInt in YEAR_RANGE) { year = asInt; i++; continue }

            if (token in QUALITY_TOKENS) { quality = token.uppercase().replace("UHD", "4K"); i++; continue }

            if (AUDIO_TYPE_MAP.containsKey(token)) { audioType = AUDIO_TYPE_MAP[token]; i++; continue }

            if (token in SUBTITLE_TRIGGER_TOKENS) { nextIsSubtitleLang = true; i++; continue }

            if (token == "audio") {
                val next = tokens.getOrNull(i + 1)
                if (next != null && AUDIO_LANG_MAP.containsKey(next)) {
                    audioLanguage = AUDIO_LANG_MAP[next]; i += 2; continue
                }
                i++; continue
            }

            val lang = AUDIO_LANG_MAP[token]
            if (lang != null) {
                if (nextIsSubtitleLang) { subtitleLanguage = lang; nextIsSubtitleLang = false }
                else audioLanguage = lang
                i++; continue
            }

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

    fun normalize(raw: String): String {
        var s = raw.lowercase()
        s = s.replace(Regex("\\.(mp4|mkv|webm|mov|m4v|avi|flv|wmv)$"), "")
        s = s.replace(Regex("[._\\-]"), " ")
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
        s = s.replace(Regex("[^a-z0-9 ]"), "")
        // Collapse runs of 3+ identical letters to 1 (baahubali→bahubali, pushpaa→pushpa)
        s = s.replace(Regex("(.)\\1{2,}")) { mr -> mr.groupValues[1] }
        // Collapse runs of exactly 2 identical letters to 1 (baahubali→bahubali, pushpaa→pushpa)
        s = s.replace(Regex("(.)\\1")) { mr -> mr.groupValues[1] }
        s = s.replace(Regex("\\s+"), " ").trim()
        return s
    }

    fun score(normalizedQuery: String, normalizedCandidate: String): Int {
        if (normalizedQuery.isBlank() || normalizedCandidate.isBlank()) return 0
        if (normalizedCandidate == normalizedQuery) return SCORE_EXACT
        if (normalizedCandidate.startsWith(normalizedQuery)) return SCORE_STARTS_WITH
        if (normalizedCandidate.contains(normalizedQuery)) return SCORE_CONTAINS

        // Word-all-match: every query word must appear in (or contain) a candidate word.
        // Candidate words shorter than 3 chars are excluded to prevent false positives:
        // e.g. "poshpa".contains("a") would match "snakes and laders" via the word "a".
        val queryWords = normalizedQuery.split(" ").filter { it.length > 2 }
        val candidateWords = normalizedCandidate.split(" ").filter { it.length > 2 }.toSet()
        if (queryWords.isNotEmpty() && candidateWords.isNotEmpty() && queryWords.all { qw ->
                candidateWords.any { cw -> cw.contains(qw) || qw.contains(cw) }
            }) {
            return SCORE_CONTAINS - 2
        }

        // Full-string fuzzy — no early-exit length guard so multi-word titles are scored correctly
        val fullSim = stringSimilarity(normalizedQuery, normalizedCandidate)
        if (fullSim >= 0.6) {
            val fuzzyScore = SCORE_FUZZY_MIN +
                ((fullSim - 0.6) / 0.4 * (SCORE_FUZZY_MAX - SCORE_FUZZY_MIN)).toInt()
            return fuzzyScore.coerceIn(SCORE_FUZZY_MIN, SCORE_FUZZY_MAX)
        }

        // Word-level fuzzy — query words matched against individual candidate words
        // This handles "poshpa" matching "pushpa the rise" even when full-string sim is low
        val bestWordScore = queryWords.maxOfOrNull { qw ->
            candidateWords.maxOfOrNull { cw ->
                val sim = stringSimilarity(qw, cw)
                if (sim >= 0.6) (SCORE_FUZZY_MIN + (sim - 0.6) / 0.4 * (SCORE_FUZZY_MAX - SCORE_FUZZY_MIN)).toInt() else 0
            } ?: 0
        } ?: 0

        return bestWordScore
    }

    // Levenshtein similarity without the aggressive early-exit that broke multi-word matching
    private fun stringSimilarity(a: String, b: String): Double {
        val dist = levenshtein(a, b)
        val maxLen = maxOf(a.length, b.length).coerceAtLeast(1)
        return 1.0 - dist.toDouble() / maxLen
    }

    data class VideoFields(
        val title: String,
        val fileName: String,
        val caption: String,
        val sourceLabel: String = ""
    )

    fun scoreVideo(intent: SearchIntent, fields: VideoFields): Int {
        if (!intent.isValid) return 0
        val nq = intent.normalizedName
        val nTitle = normalize(fields.title)
        val nFile = normalize(fields.fileName)
        val nCaption = normalize(fields.caption)
        val titleScore = score(nq, nTitle)
        val fileScore = score(nq, nFile)
        val captionScore = score(nq, nCaption)
        var best = maxOf(titleScore, fileScore, captionScore)

        android.util.Log.d(
            "SearchEngine",
            "[SCORE] query='$nq' title='$nTitle' scores=title:$titleScore file:$fileScore caption:$captionScore best=$best threshold=$SCORE_IGNORE_BELOW"
        )

        if (best >= SCORE_IGNORE_BELOW) {
            val rawAll = "${fields.title} ${fields.fileName} ${fields.caption}".lowercase()
            var bonus = 0
            intent.year?.let { y -> if (rawAll.contains(y.toString())) bonus += 3 }
            intent.audioLanguage?.let { lang ->
                val shortCode = AUDIO_LANG_MAP.entries.firstOrNull { it.value == lang }?.key ?: ""
                if (rawAll.contains(lang.lowercase()) || rawAll.contains(shortCode)) bonus += 3
            }
            intent.quality?.let { q -> if (rawAll.contains(q.lowercase())) bonus += 2 }
            best = (best + bonus).coerceAtMost(100)
        }

        return best
    }

    fun <T> rank(
        results: List<SearchResult<T>>,
        threshold: Int = SCORE_IGNORE_BELOW
    ): List<SearchResult<T>> =
        results
            .filter { it.score >= threshold }
            .sortedByDescending { it.score }

    fun levenshtein(a: String, b: String): Int {
        if (a == b) return 0
        if (a.isEmpty()) return b.length
        if (b.isEmpty()) return a.length
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
