# Hasikit Architecture

**Project:** Hasikit  
**Developer:** @trixsearch  
**Platform:** Android (Kotlin, Jetpack Compose, Media3, TDLib, Hilt, Room)

---

## Tech Stack

| Technology | Version | Purpose |
|---|---|---|
| Kotlin | 2.1.21 | Primary language |
| Jetpack Compose + Material 3 | BOM 2025.02.00 | UI layer |
| TDLib (libtdjni.so) | bundled | Telegram integration |
| Media3 ExoPlayer | 1.5.1 | Video playback engine |
| Hilt | 2.57.1 | Dependency injection |
| Room | 2.8.4 | Local database |
| DataStore Preferences | 1.1.1 | Settings persistence |
| WorkManager | 2.10.1 | Background downloads |
| Coil | 2.7.0 | Image loading |
| Kotlin Coroutines | 1.10.1 | Async operations |
| Navigation Compose | 2.8.8 | In-app navigation |
| Firebase Remote Config | 33.10.0 | Updates (planned) |

**Min SDK:** 26 | **Target SDK:** 35 | **Compile SDK:** 35 | **JVM Target:** 21

---

## High-Level Architecture

Hasikit follows Clean Architecture with three layers:

```
┌─────────────────────────────────────────┐
│           Presentation Layer            │
│  Jetpack Compose UI + ViewModels        │
│  Navigation Compose (NavGraph/Screen)   │
└────────────────────┬────────────────────┘
                     │
┌────────────────────▼────────────────────┐
│             Domain Layer                │
│  Repository Interfaces                  │
│  Domain Models (Video, DownloadTask,    │
│  WatchProgress, TelegramMedia)          │
└────────────────────┬────────────────────┘
                     │
┌────────────────────▼────────────────────┐
│              Data Layer                 │
│  Room (local DB)                        │
│  TDLib (Telegram)                       │
│  WorkManager (downloads)                │
│  DataStore (settings)                   │
└─────────────────────────────────────────┘
```

No traditional backend server. Telegram is the content delivery layer.

---

## Project Structure

```
app/src/main/java/com/trixsearch/hasikit/
│
├── data/
│   ├── local/
│   │   ├── dao/
│   │   │   └── VideoDao.kt               # Room DAO for all tables
│   │   ├── entities/
│   │   │   ├── VideoEntity.kt
│   │   │   ├── DownloadEntity.kt
│   │   │   ├── WatchProgressEntity.kt
│   │   │   ├── FavoriteEntity.kt
│   │   │   ├── WatchLaterEntity.kt
│   │   │   └── WatchHistoryEntity.kt
│   │   └── HasikitDatabase.kt            # Room database definition
│   └── repository/
│       └── VideoRepositoryImpl.kt        # VideoRepository implementation
│
├── domain/
│   ├── model/
│   │   ├── Video.kt                      # Core video domain model
│   │   ├── DownloadTask.kt               # Download state model
│   │   └── WatchProgress.kt             # Watch position model
│   └── repository/
│       ├── VideoRepository.kt            # Video data interface
│       └── TelegramRepository.kt         # Telegram data interface
│
├── telegram/
│   ├── config/
│   │   └── TelegramSourceConfig.kt       # Source definitions, ALLOW_USER_SOURCES flag
│   ├── data/
│   │   ├── repository/
│   │   │   ├── TelegramAuthRepositoryImpl.kt
│   │   │   ├── TelegramChannelRepositoryImpl.kt
│   │   │   └── TelegramMediaRepositoryImpl.kt
│   │   └── session/
│   │       └── TelegramSessionManager.kt # TDLib session lifecycle
│   ├── domain/
│   │   ├── model/
│   │   │   ├── TelegramMedia.kt
│   │   │   ├── TelegramMediaItem.kt
│   │   │   └── TelegramModels.kt
│   │   └── repository/
│   │       ├── TelegramAuthRepository.kt
│   │       ├── TelegramChannelRepository.kt
│   │       └── TelegramMediaRepository.kt
│   └── service/
│       └── TelegramClientService.kt      # TDLib client lifecycle, auth state machine
│
├── player/
│   └── HasikitPlayer.kt                  # Media3 ExoPlayer singleton
│
├── download/
│   ├── DownloadWorker.kt                 # WorkManager worker
│   └── HasikitDownloadManager.kt         # Download orchestration and state
│
├── ui/
│   ├── components/
│   │   ├── FastScroller.kt
│   │   └── ShimmerComponents.kt
│   ├── navigation/
│   │   ├── NavGraph.kt                   # Navigation graph definition
│   │   └── Screen.kt                     # Typed screen sealed class
│   ├── screens/
│   │   ├── auth/                         # AuthScreen, AuthViewModel
│   │   ├── home/                         # HomeScreen, HomeViewModel
│   │   ├── library/                      # LibraryScreen (4-tab: Downloads, Favorites, Watch Later, History)
│   │   ├── player/                       # PlayerScreen
│   │   ├── request/                      # RequestContentScreen
│   │   ├── search/                       # SearchScreen
│   │   └── settings/                     # SettingsScreen, AdvancedSettingsScreen, LanguageScreen, StorageManagementScreen
│   └── theme/
│       ├── Color.kt
│       ├── Theme.kt
│       └── Type.kt
│
├── di/
│   ├── telegram/
│   │   └── TelegramModule.kt             # Hilt module for Telegram dependencies
│   ├── DatabaseModule.kt                 # Hilt module for Room
│   └── RepositoryModule.kt               # Hilt module for repository bindings
│
├── util/
│   ├── LanguageManager.kt
│   └── SampleData.kt
│
├── HasikitApp.kt                         # Application class, @HiltAndroidApp
└── MainActivity.kt                       # Single activity, theme + navigation host
```

---

## Database

Room database: `HasikitDatabase`  
DataStore: `hasikit_settings`

### Tables

#### `videos`
Stores all video metadata fetched from Telegram.

| Field | Type | Notes |
|---|---|---|
| id | String (PK) | TDLib fileId |
| title | String | Cleaned title |
| fileId | Long | TDLib file identifier |
| localPath | String? | Path if downloaded |
| isDownloaded | Boolean | |
| isStreamable | Boolean | MessageVideo = true, MessageDocument = false |
| size | Long | File size in bytes |
| duration | Long | Duration in ms |
| thumbnailPath | String? | Local thumbnail path |
| chatId | Long | Source channel chatId |
| messageId | Long | TDLib message ID |

#### `downloads`
Tracks download tasks managed by WorkManager.

| Field | Type | Notes |
|---|---|---|
| videoId | String (PK) | References videos.id |
| state | DownloadState | DOWNLOADING, PAUSED, DOWNLOADED, FAILED |
| progress | Int | 0–100 |
| localPath | String? | Destination file path |
| workerId | String? | WorkManager work ID |

#### `watch_progress`
Stores last playback position per video.

| Field | Type | Notes |
|---|---|---|
| videoId | String (PK) | |
| position | Long | Last position in ms |
| duration | Long | Total duration in ms |
| updatedAt | Long | Timestamp |

#### `favorites`
User-favorited videos.

| Field | Type | Notes |
|---|---|---|
| videoId | String (PK) | |
| addedAt | Long | Timestamp |

#### `watch_later`
User's watch-later list.

| Field | Type | Notes |
|---|---|---|
| videoId | String (PK) | |
| addedAt | Long | Timestamp |

#### `watch_history`
Full viewing history.

| Field | Type | Notes |
|---|---|---|
| videoId | String (PK) | |
| watchedAt | Long | Timestamp |

---

## Download Architecture

Downloads are managed by `HasikitDownloadManager` (Hilt singleton) using WorkManager.

```
User triggers download
        ↓
HasikitDownloadManager.startDownload()
        ↓
WorkManager enqueues DownloadWorker
        ↓
DownloadWorker calls TDLib DownloadFile
        ↓
Progress polled and written to Room (downloads table)
        ↓
On completion: videos.isDownloaded = true, localPath set
```

State model: `DOWNLOADING` → `DOWNLOADED` | `PAUSED` | `FAILED`

Rules:
- Download path always read from `customDownloadPath` at enqueue time (never cached)
- Stall detection: FAILED after 5 consecutive inactive polls
- Exponential backoff retry: 30s base
- Pause: cancels WorkManager worker + TDLib CancelDownloadFile
- Resume: re-enqueues WorkManager worker
- No hidden video cache — only explicit user downloads

---

## Playback Architecture

`HasikitPlayer` is a Hilt singleton wrapping Media3 ExoPlayer 1.5.1.

```
User selects video
        ↓
PlayerScreen receives Video domain model
        ↓
HasikitPlayer.play(uri)
        ↓
If local file exists → play from file
If streamable → play from Telegram stream URL
If document-only → download required first
        ↓
Watch progress auto-saved every 5s and on exit
        ↓
Resume position restored on re-open
```

Surface: TextureView via XML inflation (`player_view_texture.xml`) — eliminates black screen on first open.

Supported formats: MP4, MKV, WebM, MOV, M4V, 3GP

MediaSession active during playback for lock screen, notifications, Bluetooth, Android Auto.

---

## Telegram Architecture

TDLib is integrated via `TelegramClientService` which manages the TDLib client lifecycle.

### Authentication Flow
```
App start
    ↓
TelegramClientService initializes TDLib
    ↓
AuthorizationStateWaitPhoneNumber → AuthScreen shown
    ↓
User enters phone → OTP sent
    ↓
User enters OTP → AuthorizationStateReady
    ↓
Auto-redirect to HomeScreen
```

Session is persisted by TDLib in the app's files directory. On relaunch, if session is valid, auth is skipped.

### Source Resolution (4-step fallback)
1. `GetChat` — TDLib cache lookup
2. `GetChats` — search accessible chats
3. `CreateSupergroupChat` — force-load by supergroup ID
4. Invite link fallback — `CheckChatInviteLink` / `JoinChatByInviteLink`

Resolved chatIds are cached in memory to avoid re-resolution on every resume.

### Media Fetch
- `MessageVideo` → streamable, thumbnail available from TDLib
- `MessageDocument` (MKV, large MP4) → download required, no stream
- Title cleaning: strips resolution tags, codec names, dots/underscores → readable title
- Thumbnails fetched in background after feed loads; in-memory cache keyed by TDLib fileId

### Search
- Stage A: `SearchChatMessages` with video type filter
- Stage A2: `SearchChatMessages` with document type filter
- Stage B: full channel history scan matching file name, title, caption

---

## Continue Watching

Stores `videoId` + `position` in `watch_progress` table.

Does not store hidden downloads. Validates file existence before showing resume option. Dead entries (missing file + not streamable) are cleaned up on Home load.

---

## Thumbnail System

1. Feed loads immediately with placeholder thumbnails
2. Background coroutine fetches thumbnails from TDLib per fileId
3. In-memory `_thumbnailCache` (Map<fileId, path>) updated as thumbnails resolve
4. On cache clear: `thumbnailCacheVersion` signal incremented in `HasikitDownloadManager`; `HomeViewModel` observes and calls `invalidateAndReloadThumbnails()` — no app restart required

---

## Settings Architecture

Settings stored in DataStore (`hasikit_settings`). Shared across `SettingsViewModel` and `PlayerScreen`.

| Key | Type | Default | Purpose |
|---|---|---|---|
| theme | String | "system" | Light / Dark / System |
| autoPlay | Boolean | true | Auto-play next video |
| resumeAfterCall | Boolean | true | Resume playback after phone call |
| pauseOnHeadphoneRemoval | Boolean | true | |
| pauseOnScreenOff | Boolean | false | |
| videoEndAction | String | "next" | "next" or "repeat" |
| wifiOnlyDownloads | Boolean | false | |
| customDownloadPath | String | "" | SAF URI for download folder |
| galleryVisible | Boolean | false | MediaScanner visibility |
| customAspectRatios | String | "" | JSON list of W:H strings |

---

## Theme System

Three modes: System / Light / Dark  
Stored in DataStore, applied at `MainActivity` level.  
Dark theme is the default design target.

---

## Storage Architecture

| Type | Location |
|---|---|
| TDLib session + files | `filesDir/tdlib/` |
| Downloaded videos | User-selected SAF folder (or app default) |
| Thumbnail cache | TDLib managed, clearable via Advanced Settings |
| ExoPlayer buffer cache | `cacheDir/exoplayer/`, clearable via Advanced Settings |
| Room database | `databases/hasikit_database` |
| DataStore | `datastore/hasikit_settings.preferences_pb` |

---

## Architecture Decisions

### 2026-07-21 — Stability Fix Pass

- Search: 300ms debounce + Job cancellation added to `HomeViewModel.searchTelegram`; Room and Telegram search moved to `Dispatchers.IO`
- Long-press context menu added to `HorizontalVideoCard` via `combinedClickable`; wired to `addFavorite`, `removeFavorite`, `addWatchLater`, `removeWatchLater` in `HomeViewModel`
- `StorageManagementScreen` created at `Screen.StorageManagement` route; entry point added to `AdvancedSettingsScreen`
- Black screen fix: `playerInitialized` flag triggers null-then-re-attach of player to `PlayerView` after `initialize()` completes
- Login flash fix: `AuthState.Loading` spinner in `AuthScreen` prevents login form from showing during session restore; `NavGraph` only navigates on `Authenticated`
- `[AUTH_RESTORE]` debug logs added to `TelegramAuthRepositoryImpl.restoreSession`
- `[SEARCH]` timing and cancellation logs added to `HomeViewModel.searchTelegram`
- `favoriteIds` and `watchLaterIds` StateFlows added to `HomeViewModel` for per-video menu state

### 2025-07-xx — Initial Architecture

- Clean Architecture with Presentation / Domain / Data layers
- Hilt for DI throughout — ViewModels, Repositories, DownloadManager, Player
- Room with 6 tables: videos, downloads, watch_progress, favorites, watch_later, watch_history
- WorkManager for background downloads (survives app kill and reboot)
- TDLib bundled as local AAR (`app/libs/tdlib.aar`) with native libs in `app/jniLibs/`
- Media3 ExoPlayer singleton (`HasikitPlayer`) — survives screen navigation
- TextureView surface via XML inflation — fixes black screen on first open
- DataStore for settings — replaces SharedPreferences
- Navigation Compose with typed `Screen` sealed class
- `ALLOW_USER_SOURCES` build config flag — controls user source management per build
- No traditional backend server — Telegram is the content delivery layer
- No hidden video cache — only explicit user downloads tracked in Room
- Continue Watching tracks positions only, not hidden downloads
- Thumbnail cache invalidation via signal pattern — no app restart required
- Seek deferred when player is in BUFFERING/IDLE state — queued until STATE_READY
- Volume gesture uses delta from gesture-start base — prevents aggressive accumulation
- Axis lock after 20px threshold — prevents diagonal gesture conflicts
