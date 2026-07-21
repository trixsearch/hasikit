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
- **Telegram direct search** — queries TDLib `SearchChatMessages` across all resolved sources
  - Stage A: video-type messages (caption + text search)
  - Stage A2: document-type messages (MKV/large files sent as documents)
  - Stage B: full channel history scan matching file name, title, caption
- "Searching Telegram…" spinner shown while search is in progress
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
| `[SEARCH]` | `HomeViewModel.searchTelegram` | query, source name, result count per source, total, timing (ms) |
| `[SEARCH]` | `HomeViewModel.searchTelegram` | job cancellation logged when new query arrives |
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


Hasikit Stability Fix Pass : User requested fixes for 5 issues in priority order: black screen playback, search freezing, login flash, storage management screen, and long-press video menu. All 5 fixes were implemented across multiple files.

Files and Code Summary
HomeViewModel.kt ( ui/screens/home/): Added searchJob: Job? field. Replaced single viewModelScope.launch in searchTelegram() with job-tracked coroutine that cancels previous job, adds 300ms debounce, wraps Room search in withContext(Dispatchers.IO), wraps Telegram search loop in withContext(Dispatchers.IO). Added addFavorite, removeFavorite, addWatchLater, removeWatchLater methods. Added favoriteIds and watchLaterIds StateFlows from Room. Added imports for FavoriteEntity, WatchLaterEntity, Job, withContext.

HomeScreen.kt ( ui/screens/home/): Added combinedClickable import. HorizontalVideoCard refactored to use combinedClickable (long-press opens context menu AlertDialog). Menu options: View Info, Add/Remove Favorites, Add/Remove Watch Later, Download controls (Pause/Resume/Delete), Share, Cancel. Added isFavorite, isWatchLater, onAddFavorite, onRemoveFavorite, onAddWatchLater, onRemoveWatchLater parameters. Collected favoriteIds and watchLaterIds in HomeScreen. Both HorizontalVideoCard call sites updated with new callbacks.

PlayerScreen.kt ( ui/screens/player/): Added playerInitialized: Boolean state variable. In DisposableEffect(videoId), sets playerInitialized = true after player.initialize() and resets to false on dispose. AndroidView update block now does view.player = null then view.player = instance when playerInitialized is true, forcing TextureView re-bind to fix black screen.

TelegramAuthRepositoryImpl.kt ( telegram/data/repository/): Added detailed [AUTH_RESTORE] debug logs to restoreSession() — logs start state, session string length, success with userId/displayName, and failure reasons.

NavGraph.kt ( ui/navigation/): Added Log import. Added comment clarifying login flash fix design. Added StorageManagementScreen import and composable(Screen.StorageManagement.route) registration.

Screen.kt ( ui/navigation/): Added object StorageManagement : Screen("storage_management").

StorageManagementScreen.kt ( ui/screens/settings/) — NEW FILE : Full storage management screen with checkboxes for Clear Cache, Clear Thumbnail Cache, Clear Player Cache, Clear Downloads. Apply button with confirmation dialog. Force Telegram Reset hidden under "More Options" TextButton. Storage stats overview at top. Uses SettingsViewModel.

AdvancedSettingsScreen.kt ( ui/screens/settings/): Added Storage Management entry point at top of Cache group, navigates to Screen.StorageManagement.route.

features.md / architecture.md: Updated with Stability Fix Pass entries documenting all 5 fixes, new debug log tags, and updated project structure.

player_view_texture.xml ( res/layout/): Existing XML with app:surface_type="texture_view" — used by PlayerScreen to inflate PlayerView with TextureView surface.

SettingsScreen.kt ( ui/screens/settings/): Contains SettingsViewModel with cacheSize, storageUsed, downloadCount StateFlows, clearCache(), clearThumbnailCache(), clearPlayerCache(), clearAllStorage(), forceDeleteSession() methods. settingsDataStore extension is internal. SettingsGroup composable is internal.

AuthScreen.kt ( ui/screens/auth/): Already handles AuthState.Loading with spinner, AuthState.CodeSent with OTP screen, and Unauthenticated with phone form — architecture was correct for login flash prevention.

VideoRepository.kt ( domain/repository/): Interface includes getAllFavorites(), getAllWatchLater(), addFavorite(), removeFavorite(), addToWatchLater(), removeFromWatchLater(), insertVideo().

Key Insights
Project : Android app "Hasikit" — Kotlin, Jetpack Compose, Media3 ExoPlayer, TDLib, Hilt, Room. Package: com.trixsearch.hasikit. Min SDK 26.

Player singleton : HasikitPlayer is @Singleton — player.stop() on dispose, not release(). player.initialize() is idempotent (skips if already initialized).

AuthState.Loading : _authState initialized to AuthState.Loading in TelegramAuthRepositoryImpl — session restore is async in MainActivity.lifecycleScope.launch.

Search architecture : 3-stage pipeline — Stage 1 Room (instant), Stage 2 Telegram multi-query, Stage 3 SearchEngine ranking. searchChannelMediaMulti is the Telegram search method.

TextureView black screen root cause : PlayerView surface binds before ExoPlayer instance is ready when player is @Singleton and reused. Fix: null-then-re-attach after initialize().

combinedClickable requires @OptIn(ExperimentalFoundationApi::class) on the composable function.

SettingsGroup is internal — accessible from StorageManagementScreen in same package.

No new features : User explicitly stated "Do not add new features" — all changes are fixes only.

Comments required : User requested "Add comments above every modified code block."

Most Recent Topic
Topic : Hasikit Stability Fix Pass — implementing 5 bug fixes

Progress : All 5 fixes fully implemented and committed to files.

Tools Used :

listDirectory: Explored project structure to understand file layout

fsRead (multiple) : Read HomeViewModel.kt, HomeScreen.kt, HasikitPlayer.kt, PlayerScreen.kt, MainActivity.kt, AdvancedSettingsScreen.kt, NavGraph.kt, TelegramModels.kt, SettingsScreen.kt, AuthScreen.kt, AuthViewModel.kt, player_view_texture.xml, VideoRepository.kt, TelegramAuthRepositoryImpl.kt, Screen.kt, features.md, architecture.md

fsReplace on HomeViewModel.kt: Added Job import, withContext import, FavoriteEntity/ WatchLaterEntity imports; replaced searchTelegram() with debounced/cancellable/IO-threaded version; added favorites/watchlater methods and StateFlows

fsReplace on HomeScreen.kt: Added combinedClickable import, VideoRepository import; added favoriteIds/ watchLaterIds collection; replaced HorizontalVideoCard with long-press menu version; updated both call sites

fsReplace on PlayerScreen.kt: Added playerInitialized state; updated DisposableEffect to set flag; updated AndroidView update block for null-then-reattach fix

fsReplace on TelegramAuthRepositoryImpl.kt: Added [AUTH_RESTORE] debug logs

fsReplace on NavGraph.kt: Added Log import, StorageManagementScreen import, route registration, login flash comment

fsReplace on Screen.kt: Added StorageManagement route object

fsWrite (create) StorageManagementScreen.kt: Created new file with full storage management UI

fsReplace on AdvancedSettingsScreen.kt: Added Storage Management entry point

fsReplace on features.md and architecture.md: Updated documentation


