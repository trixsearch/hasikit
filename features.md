# Hasikit — Features & Bug Fix History

**Project:** Hasikit  
**Developer:** @trixsearch  
**Platform:** Android (Kotlin, Jetpack Compose, Media3, TDLib, Hilt, Room)

---

## Architecture

- **UI:** Jetpack Compose + Material 3, dark theme first
- **Player:** Media3 ExoPlayer wrapped in `HasikitPlayer` singleton
- **Storage:** Room database with entities for Videos, Downloads, Watch Progress, Favorites, Watch Later, Watch History
- **Telegram:** TDLib (libtdjni.so) via `TelegramClientService` for auth, media fetch, file download
- **DI:** Hilt throughout — ViewModels, Repositories, DownloadManager, Player
- **Downloads:** WorkManager via `DownloadWorker` + `HasikitDownloadManager`
- **Navigation:** Navigation Compose with typed `Screen` sealed class
- **Settings:** DataStore Preferences (`hasikit_settings`) shared across `SettingsViewModel` and `PlayerScreen`

---

## Features

### Authentication
- Telegram phone number login
- OTP verification
- Session management via TDLib
- Auto-redirect to Home on authenticated state
- Force session delete (for login loop recovery)
- Logout with confirmation dialog

### Home Screen
- Netflix-style layout with horizontal rows
- Sections: Continue Watching, Recently Added, Downloads, All Videos
- Skeleton loader shown until first page of videos is ready (no blank screen flash)
- Pull-to-refresh
- Infinite scroll with prefetch threshold (loads next page when 10 items remain)
- Channel filter chips (shown when multiple sources are available)
- Source filter: All Sources or individual channel
- Auto-refresh every 60 seconds (polls for new content without disturbing scroll)
- Duplicate deduplication by fileId and title+size key
- Continue Watching cleanup: removes dead entries where file is missing and video is not streamable

### Search
- Search bar on Home screen
- **Smart Search V4** — 3-stage pipeline via `SearchEngine` + `HomeViewModel.searchTelegram`
  - Stage 1: instant Room search on IO thread, results emitted immediately
  - Stage 2: Telegram multi-query search via `searchChannelMediaMulti` across all resolved sources
  - Stage 3: fuzzy ranking via `SearchEngine.rank()` — score 0–100, results below 50 filtered out
- **SearchEngine** (`search/SearchEngine.kt`) — pure Kotlin object, no Android dependencies
  - `parseIntent(query)` — extracts movie name, year, audio language, subtitle language, audio type, quality from raw query
  - `toTelegramQueryVariants()` — expands intent into multiple Telegram queries (e.g. "pushpa hindi 1080p" → ["pushpa", "pushpa Hindi", "pushpa 1080p", "pushpa Hindi 1080p"])
  - `normalize(raw)` — strips codecs, resolution tags, language tokens, dots/underscores; collapses repeated letters to 1 (baahubali→bahubali, pushpaa→pushpa)
  - `score(query, candidate)` — exact (100) → startsWith (90) → contains (80) → word-all-match (78) → full-string fuzzy (60–79) → word-level fuzzy (60–79)
  - `scoreVideo(intent, fields)` — scores title + fileName + caption, adds bonus for year/language/quality match; logs normalized query, field scores, and threshold decision
  - `levenshtein(a, b)` — O(n*m) edit distance, no early-exit (early-exit was causing multi-word titles like "Pushpa The Rise" to score 0 against "poshpa")
  - `stringSimilarity(a, b)` — private helper returning 0.0–1.0 similarity ratio
- **Search intent chips** — shown below search bar when query has structured fields (year, language, quality, audio type)
- Empty search state: "No videos found" with spelling tips and **Request Content** button that navigates directly to `RequestContentScreen`
- Search mode never shows feed content — `return@LazyColumn` after search block prevents any feed sections from rendering when query is active

### Home Feed Startup
- TDLib cold-start undercount fix: `loadAllSources(isStartup=true)` detects `totalFetched < PAGE_SIZE` and schedules a single 2-second retry
- `startupRetryScheduled` flag prevents multiple retries
- `[FEED_STARTUP]` logs: source list, total videos fetched, retry trigger

### Share System
- Long-press Share uses `FileProvider` (`${applicationId}.fileprovider`) to share actual video file when downloaded
- Falls back to Telegram link text if not downloaded
- Falls back to title + channel text if no Telegram link
- Copy Telegram Link option added to long-press menu
- `telegramLink` field added to `Video` domain model — computed as `https://t.me/c/{positiveChannelId}/{messageId}`
- `file_provider_paths.xml` added to `res/xml/`; `FileProvider` declared in `AndroidManifest.xml`

### Library UX
- Favorites tab: Play (navigate to player), Share, Remove buttons per item
- Watch Later tab: Play (navigate to player), Share, Remove buttons per item
- History tab: Resume (navigate to player), Share, Remove buttons per item; sorted newest-first by `watchedAt`
- `[FAVORITES]`, `[WATCH_LATER]`, `[HISTORY]` debug logs added
- Results show only video items (MP4, MKV, WebM, MOV, M4V)
- Clear search restores full feed

### Telegram Sources
- Official sources (always active, not shown in UI)
- User-added sources: channels, groups, supergroups
- Add manually by @username, numeric chatId, or invite link
- Import from Telegram: fetches user's joined channels/groups with multi-select
- Import dialog has search bar to filter joined chats by name
- Remove user sources
- Source resolution with 4-step fallback:
  1. GetChat (TDLib cache)
  2. Search accessible chats (GetChats)
  3. CreateSupergroupChat (force-load by supergroup ID)
  4. Invite link fallback (CheckChatInviteLink / JoinChatByInviteLink)
- Resolved chatIds cached in memory to avoid re-resolution on every resume

### Video Feed
- Supports: MP4, MKV, WebM, MOV, M4V (video type + document type)
- Streamable flag: `MessageVideo` = streamable, `MessageDocument` = download required
- Title cleaning: strips resolution tags, codec names, dots/underscores → readable title
- Thumbnail fetched from TDLib in background; feed visible immediately while thumbnails load
- Thumbnail fallback: Hasikit logo shown when no thumbnail available
- Video frame extraction from local file as secondary thumbnail fallback

### Media Player (`HasikitPlayer` + `PlayerScreen`)
- ExoPlayer (Media3 1.5.1) singleton — survives screen navigation
- TextureView surface via XML inflation (`player_view_texture.xml`) — fixes black screen on first open
- MediaSession for lock screen, notification, Bluetooth, Android Auto
- Playback: play, pause, resume, stop, restart from beginning
- Seek: immediate when STATE_READY, deferred when STATE_BUFFERING/IDLE
- Playback speed: 0.25x, 0.5x, 0.75x, 1x, 1.25x, 1.5x, 1.75x, 2x, 2.5x, 3x
- Repeat mode: Off / Repeat One (cycled via button)
- Audio track selection (multi-track videos)
- Subtitle track selection (embedded subtitles)
- External subtitle loading via file picker (.srt, .ass, .vtt, .sub)
- Aspect ratio cycling: Fit, Fill, Stretch, 16:9, 4:3, Zoom
- Video scale via pinch-to-zoom (0.5x–3x), Reset Zoom button
- Portrait / Landscape rotation toggle (independent of system setting)
- Picture-in-Picture (PiP) with correct video aspect ratio
- Chromecast icon (placeholder, future implementation)
- Watch progress auto-saved every 5 seconds and on exit
- Resume from last position on re-open
- Auto-download streamable videos in background when playback begins

### Gesture Controls
- **Horizontal swipe:** seek timeline (120s range per full screen width)
  - Seek preview overlay shows target timestamp while dragging
  - Seek committed on finger release
- **Left vertical swipe:** screen brightness (1–100%)
- **Right vertical swipe:** volume (0–200%)
  - One full swipe = 0→100% (system volume)
  - Second swipe = 100→200% (ExoPlayer software amplification)
  - Gesture-start base captured so delta is relative to start position (no aggressive accumulation)
- **Double tap left zone (0–40%):** seek backward 10s (stackable)
- **Double tap right zone (60–100%):** seek forward 10s (stackable)
- **Double tap center zone (40–60%):** toggle play/pause
- **Long press right side:** activate 2x speed; release restores previous speed
- **Pinch:** zoom video scale
- Axis lock: gesture direction locked after 20px movement threshold

### Player Controls UI
- Top bar: Back, Title (marquee), Aspect Ratio, Speed, Audio Track, Subtitles, Rotate, PiP
- Center: Rewind 10s, Play/Pause/Replay, Forward 10s
- Bottom: Seek bar with time labels, Repeat, Reset Zoom, Lock
- Feedback pills: seek time, volume %, brightness %, speed, aspect ratio label
- Controls toggle: single tap shows/hides; no auto-hide timer
- **Safe area insets:** top bar uses `windowInsetsPadding(WindowInsets.displayCutout)` — icons clear punch hole and notch

### Player Lock
- Lock button hides all controls and disables gestures
- Tap anywhere while locked shows "Unlock player first" message
- Unlock button shown in center when controls are visible in locked state

### Mute Audio
- "Mute Audio" in audio track menu calls `player.muteAudio()` — sets ExoPlayer volume to 0f
- Mute state tracked separately from volume level
- Selecting any audio track calls `player.unmuteIfMuted()` — restores previous volume

### Video End Logic
- 5-second countdown overlay shown when video reaches STATE_ENDED
- Countdown shows "Playing next in N…" or "Replaying in N…" based on settings
- Cancel button dismisses countdown and leaves player in ended state
- After countdown: restarts playback (auto-play next / repeat same per setting)

### Audio Features
- System volume control (0–100%) via gesture and AudioManager
- Software amplification (101–200%) via ExoPlayer volume gain
- Audio focus management (request on enter, abandon on exit)
- Pause on phone call (TelephonyCallback API 31+, BroadcastReceiver API 26–30)
- Resume after call (configurable in Settings)
- Pause on headphone removal (ACTION_AUDIO_BECOMING_NOISY)
- Pause when screen turns off (configurable in Settings)

### Downloads
- Background download via WorkManager (`DownloadWorker`)
- Survives app kill and device reboot
- Progress tracking (reported to DB every poll cycle)
- Pause download (cancels WorkManager worker + TDLib CancelDownloadFile)
- Resume download (re-enqueues WorkManager worker)
- Retry download (deletes old record, starts fresh)
- Delete download (cancels worker, deletes physical file, clears DB record)
- Stall detection: marks FAILED after 5 consecutive inactive polls
- Exponential backoff retry (30s base)
- Download path: always reads `customDownloadPath` at enqueue time (not cached)
- Download location logged with `[DOWNLOAD_PATH]` tag

### Library Screen
- **4-tab layout:** Downloads | Favorites | Watch Later | History
- **Downloads tab:**
  - Active downloads section (Downloading / Paused)
  - Downloaded videos section with search, sort, bulk select
  - Sort options: Name A–Z, Name Z–A, Newest, Oldest, Largest, Smallest, Longest, Shortest, Channel, Downloaded, Downloading, Paused
  - Bulk select: long-press to enter, select all, delete selected, pause/resume selected
  - Watch progress bar on each card
  - Delete confirmation dialog
- **Favorites tab:** lists favorited videos from Room, remove button
- **Watch Later tab:** lists watch-later videos from Room, remove button
- **History tab:** lists watch history from Room, remove button
- Empty state shown for each tab when no data exists

### Settings Screen
- Searchable settings with keyword filtering
- **Account:** Telegram profile, phone number, username, profile photo
- **Telegram Sources:** add/remove/import sources
- **Player settings:**
  - Auto-play toggle
  - Streaming quality (reserved for future adaptive quality)
  - Resume playback after calls toggle
  - Pause on headphone removal toggle
  - Continue audio when screen locked toggle
  - After video ends: Auto Play Next Video / Repeat Same Video (radio group)
- **Downloads settings:**
  - Wi-Fi only downloads toggle
  - Download location picker (SAF folder picker)
  - Show in Gallery toggle (triggers MediaScanner)
- **Appearance:** Theme selector (System / Light / Dark)
- **Language:** navigates to Language screen
- **Advanced Settings:** (separate screen)
  - Clear Cache (with size display)
  - Clear Thumbnail Cache
  - Clear Player Cache (ExoPlayer buffer)
  - Clear All Storage (downloads + cache, session preserved)
  - Force Telegram Reset (deletes TDLib session, redirects to auth)
  - Custom Aspect Ratios (add/remove W:H ratios, preset suggestions)
- **Request Content:** links to @hasikit_m_bot
- **About:** version, developer, GitHub, website, Telegram links
- **Sign Out:** confirmation dialog, redirects to auth

### Thumbnail System
- Thumbnails fetched from TDLib in background after feed loads
- In-memory cache (`_thumbnailCache`) keyed by TDLib fileId
- After clearing thumbnail cache in Advanced Settings:
  - `thumbnailCacheVersion` signal incremented in `HasikitDownloadManager`
  - `HomeViewModel` observes signal and calls `invalidateAndReloadThumbnails()`
  - In-memory cache cleared, all thumbnails re-fetched from TDLib
  - No app restart required

### Update System
- Firebase Remote Config integration (version checking, force updates)

### Theme
- System / Light / Dark theme stored in DataStore
- Applied at `MainActivity` level via `themeDataStore`

### Language
- Language screen (UI scaffold, full localization planned)
- Planned: Hindi, English, Marathi, Tamil, Telugu, Bengali, Gujarati, Kannada, Malayalam, Punjabi, Urdu, Odia, Assamese, Nepali, Sanskrit

---

## Bug Fixes

### Build Fixes
- **`PlayerScreen.kt`** — `setVideoSurfaceView(null)` and `videoSurfaceType` do not exist on `PlayerView` in Media3 1.5.1; replaced with `setSurfaceType(PlayerView.SURFACE_TYPE_TEXTURE_VIEW)`, then further replaced with XML inflation approach (`player_view_texture.xml`) since `setSurfaceType` is also not public API
- **`SettingsScreen.kt`** — `galleryVisible` unresolved: `HasikitDownloadManager` was missing the `galleryVisible: MutableStateFlow<Boolean>` property; added it
- **`TelegramChannelRepositoryImpl.kt`** — Duplicate JVM class name for `BatchResult`: two identical `data class BatchResult` declarations inside the same `suspend` function caused a JVM naming collision; fixed by extracting to `private data class SearchBatchResult` at file scope

### Runtime Bug Fixes

| # | Bug | Fix |
|---|-----|-----|
| 1 | Volume gesture too aggressive | Capture `volumeBase` at gesture start; delta is relative to start position; one full swipe = 0→100% |
| 2 | Black screen on video open | Use `SURFACE_TYPE_TEXTURE_VIEW` via XML inflation (`player_view_texture.xml`); TextureView is always ready immediately after view attachment |
| 3 | Mute Audio does nothing | `muteAudio()` sets ExoPlayer `volume = 0f` and tracks mute state; `selectAudioTrack` calls `unmuteIfMuted()` to restore volume |
| 4 | Thumbnails don't reload after cache clear | `thumbnailCacheVersion` signal in `HasikitDownloadManager`; `SettingsViewModel` increments on clear; `HomeViewModel` observes and calls `invalidateAndReloadThumbnails()` |
| 5 | Home shows only 1 video initially | `PAGE_SIZE = 25`; skeleton loader shown until first full page ready; `_isLoading = false` only after pages are set |
| 6 | Seek bar restarts video from beginning | Deferred seek: if `STATE_BUFFERING/IDLE`, seek queued via `Player.Listener` until `STATE_READY`; logs added for `currentPos`, `duration`, `targetPos`, `isLocal` |
| 7 | Controls overlap punch hole / notch | Top bar Row uses `windowInsetsPadding(WindowInsets.displayCutout)` |
| 8 | No video end behavior | 5-second countdown overlay; Cancel button; respects Auto Play Next / Repeat Same setting |
| 9 | Search only searches loaded cards | `searchTelegram()` calls TDLib `SearchChatMessages` (video + document) + full history scan across all resolved sources |
| 10 | Home search is local-only | Same fix as #9 — `LaunchedEffect(searchQuery)` triggers `searchTelegram()` |
| 11 | Favorites / Watch Later / History not visible | Library screen rebuilt with 4-tab layout; all three Room tables now exposed via `LibraryViewModel` StateFlows |
| 12 | Re-download uses old deleted path | `startDownload` and `resumeDownload` always read `customDownloadPath.value` at enqueue time; logged with `[DOWNLOAD_PATH]` |
| 13 | "poshpa" doesn't match "Pushpa" | Removed aggressive early-exit from `levenshtein()`; repeated letters collapsed to 1 in `normalize()`; word-level fuzzy threshold lowered to 0.6 |
| 14 | Search empty state shows feed content | `return@LazyColumn` confirmed present; search block always returns before feed sections render |
| 15 | No results shows blank screen | Empty search state shows: "No videos found", spelling tips, and **Request Content** button |
| 16 | Home feed shows 1-2 videos after app restart | TDLib cold-start undercount: first `getChannelMedia` call returns only 1 message because local DB hasn't loaded. Fixed by detecting `totalFetched < PAGE_SIZE` on startup and scheduling a single 2-second retry. `[FEED_STARTUP]` logs added. |
| 17 | Share only shares text title | Long-press Share now uses `FileProvider` to share actual video file if downloaded; falls back to Telegram link; falls back to title+channel text. `[SHARE]` logs added. |
| 18 | Watch Later / Favorites / History have no actions | All three tabs now show Play (navigates to player), Share, and Remove buttons per item. History sorted newest-first. `[WATCH_LATER]`, `[FAVORITES]`, `[HISTORY]` logs added. |

### Build Stability Fix — 2026-07-21

| # | Issue | Fix |
|---|-------|-----|
| B1 | R.jar locked by stale process on Windows | Disabled `org.gradle.configuration-cache` (primary cause: holds exclusive file handles on intermediates between builds) |
| B2 | Gradle daemon OOM-stalls mid-task, leaves handles open | Increased JVM heap from `-Xmx2048m` to `-Xmx4096m -XX:MaxMetaspaceSize=1024m` |
| B3 | KAPT reads R.jar while AGP still holds write lock | Enabled `kapt.use.worker.api=true` (KAPT runs in isolated worker process, not in Gradle daemon JVM) |
| B4 | Incremental KAPT reads stale R.jar snapshots | Disabled `kapt.incremental.apt` |
| B5 | Stale daemons linger and hold locks across sessions | Added `org.gradle.daemon.idletimeout=900000` (15-minute kill) |
| B6 | No recovery path without manual folder deletion | Added `deepClean` Gradle task + `clean-rebuild.bat` script |

### Stability Fix Pass — 2026-07-21

| # | Issue | Fix |
|---|-------|-----|
| S1 | Search freezes / hangs UI | Added 300ms debounce + `Job` cancellation in `HomeViewModel.searchTelegram`; Room search moved to `Dispatchers.IO`; Telegram search runs on `Dispatchers.IO`; UI thread never blocked |
| S2 | Long-press menu missing | `HorizontalVideoCard` uses `combinedClickable`; long-press opens `AlertDialog` with: View Info, Add/Remove Favorites, Add/Remove Watch Later, Download controls (Pause/Resume/Delete), Share, Cancel |
| S3 | Storage Management screen missing | New `StorageManagementScreen` at Settings → Advanced Settings → Storage Management; checkboxes per action; Apply button; Force Telegram Reset hidden under More Options |
| S4 | Black screen on video open | After `player.initialize()`, `playerInitialized` flag set; `AndroidView` update block detaches (`null`) then re-attaches player to `PlayerView`, forcing `TextureView` to re-bind and render first frame |
| S5 | Login screen flashes before Home | `AuthState` starts as `Loading`; `AuthScreen` shows spinner for `Loading` state; login form only shown for `Unauthenticated`; `NavGraph` only navigates to Home on `Authenticated` — no flash possible |

---

## Debug Logs Added

| Tag | Location | Purpose |
|-----|----------|---------|
| `[SEEK]` | `HasikitPlayer.seekTo` | requestedPos, targetPos, currentPos, duration, state, isSeekable, isLocal |
| `[THUMBNAIL]` | `HomeViewModel.fetchThumbnails` | fileId and resolved path per thumbnail |
| `[THUMBNAIL]` | `HomeViewModel.invalidateAndReloadThumbnails` | cache invalidation event |
| `[SEARCH]` | `HomeViewModel.searchTelegram` | query, normalizedName, source name, result count per source, total, timing (ms) |
| `[SEARCH]` | `HomeViewModel.searchTelegram` | accepted/rejected candidate counts after ranking |
| `[SEARCH]` | `HomeViewModel.searchTelegram` | job cancellation logged when new query arrives |
| `[SCORE]` | `SearchEngine.scoreVideo` | normalized query, normalized title, per-field scores (title/file/caption), best score, threshold |
| `[AUTH_RESTORE]` | `TelegramAuthRepositoryImpl.restoreSession` | start state, session string length, success/failure with userId |
| `[RENDERER]` | `PlayerScreen` AndroidView update | player re-attached after initialize() with playerInitialized flag |
| `[STORAGE]` | `StorageManagementScreen` | apply action flags logged before clearing |
| `[DOWNLOAD_PATH]` | `HasikitDownloadManager.startDownload` | videoId and destDir at enqueue time |
| `[DOWNLOAD_PATH]` | `HasikitDownloadManager.resumeDownload` | videoId and destDir at resume time |
| `[RENDERER]` | `PlayerScreen` AndroidView factory | PlayerView created with SURFACE_TYPE_TEXTURE_VIEW |
| `[RENDERER]` | `PlayerScreen` AndroidView update | player instance re-attached |
| `[VOLUME]` | `HasikitPlayer.setVolume` | percent and exoVol |
| `[VOLUME]` | `HasikitPlayer.muteAudio` | muted event |
| `[VOLUME]` | `HasikitPlayer.unmuteIfMuted` | restored volume |

---

## Room Database Tables

| Table | Purpose |
|-------|---------|
| `videos` | All video metadata (title, fileId, localPath, isDownloaded, etc.) |
| `downloads` | Download tasks (state, progress, localPath) |
| `watch_progress` | Last position + duration per video |
| `favorites` | User-favorited videos |
| `watch_later` | User's watch-later list |
| `watch_history` | Full viewing history |

---

## Planned / Not Yet Implemented

- Chromecast casting
- Equalizer / bass enhancement / audio presets
- Custom subtitle font import (.ttf)
- Subtitle appearance customization (color, size, position, delay)
- Smart recommendations
- Desktop / Linux / Web support
- Subscription keys / activation codes
- Next-video queue for auto-play (currently restarts same video)
- Adaptive streaming quality selection

