# Hasikit

> **Open-source Android OTT streaming platform powered by Telegram.**

**Developer:** [@trixsearch](https://github.com/trixsearch)  
**GitHub:** [github.com/trixsearch/hasikit](https://github.com/trixsearch/hasikit)  
**Website:** [trixsearch.github.io](https://trixsearch.github.io)

---

## What is Hasikit?

Hasikit turns Telegram channels and groups into a full-featured streaming platform. Instead of expensive cloud infrastructure, it uses Telegram as the content delivery layer while providing a Netflix-inspired browsing experience and a VLC-level media player on Android.

**Browse like Netflix. Stream from Telegram. Play like VLC.**

---

## Why Hasikit?

- No server costs — Telegram is the backend
- Works with public and private channels you already have access to
- Full offline support — download once, watch anywhere
- Advanced player with gesture controls, subtitle support, multi-audio, and PiP
- Clean Architecture with a maintainable, contributor-friendly codebase

---

## Screenshots

> *Screenshots coming soon.*

---

## Project Status

| Feature | Status |
|---|---|
| Telegram Authentication | ✅ Done |
| OTT Feed with Infinite Scroll | ✅ Done |
| Telegram Search (TDLib) | ✅ Done |
| Advanced Media Player | ✅ Done |
| Gesture Controls | ✅ Done |
| Downloads (WorkManager) | ✅ Done |
| Offline Playback | ✅ Done |
| Continue Watching | ✅ Done |
| Library (Favorites, History, Watch Later) | ✅ Done |
| Picture-in-Picture | ✅ Done |
| Theme System (Light / Dark / System) | ✅ Done |
| Firebase Remote Config (Updates) | 🔜 Planned |
| Chromecast | 🔜 Planned |
| Subtitle Customization | 🔜 Planned |

---

## Features

### Authentication
- Telegram phone number login with OTP verification
- Session restore on app relaunch via TDLib
- Force session reset for login loop recovery
- Logout with confirmation dialog

### Home Feed
- Netflix-style layout: Continue Watching, Recently Added, Downloads, All Videos
- Infinite scroll with prefetch (loads next page when 10 items remain)
- Pull-to-refresh
- Channel filter chips when multiple sources are active
- Auto-refresh every 60 seconds without disturbing scroll position
- Skeleton loader on first load — no blank screen flash
- Duplicate deduplication by fileId and title+size key

### Telegram Sources
- Official sources always active
- User-added sources: channels, groups, supergroups
- Add by @username, numeric chatId, or invite link
- Import from Telegram: multi-select from your joined channels/groups with search filter
- 4-step source resolution fallback (GetChat → GetChats → CreateSupergroupChat → InviteLink)
- Resolved chatIds cached in memory to avoid re-resolution on every resume
- Feature flag `ALLOW_USER_SOURCES` to control user source management per build

### Search
- Telegram-native search via TDLib `SearchChatMessages` across all resolved sources
- Searches video-type messages (caption + text) and document-type messages (MKV/large files)
- Full channel history scan as fallback for file name and caption matching
- "Searching Telegram…" spinner during active search
- Clear search restores full feed

### Media Player
- Media3 ExoPlayer 1.5.1 singleton — survives screen navigation
- TextureView surface (XML-inflated) — eliminates black screen on first open
- MediaSession for lock screen controls, notifications, Bluetooth, Android Auto
- Playback speeds: 0.25x, 0.5x, 0.75x, 1x, 1.25x, 1.5x, 1.75x, 2x, 2.5x, 3x
- Repeat mode: Off / Repeat One
- Audio track selection (multi-track videos)
- Subtitle track selection (embedded) + external subtitle loading (.srt, .ass, .vtt, .sub)
- Aspect ratio cycling: Fit, Fill, Stretch, 16:9, 4:3, Zoom
- Pinch-to-zoom (0.5x–3x) with Reset Zoom button
- Portrait / Landscape toggle independent of system setting
- Picture-in-Picture with correct video aspect ratio
- Watch progress auto-saved every 5 seconds and on exit
- Resume from last position on re-open
- 5-second countdown overlay at video end (Auto Play Next / Repeat Same)
- Auto-download streamable videos in background when playback begins

### Gesture Controls
- **Horizontal swipe:** seek timeline (120s range per full screen width) with seek preview overlay
- **Left vertical swipe:** screen brightness (1–100%)
- **Right vertical swipe:** volume (0–200%; 0–100% system, 101–200% ExoPlayer software amplification)
- **Double tap left (0–40%):** seek backward 10s (stackable)
- **Double tap right (60–100%):** seek forward 10s (stackable)
- **Double tap center (40–60%):** toggle play/pause
- **Long press right:** activate 2x speed; release restores previous speed
- **Pinch:** zoom video scale
- Axis lock after 20px movement threshold

### Player Controls
- Top bar: Back, Title (marquee), Aspect Ratio, Speed, Audio Track, Subtitles, Rotate, PiP
- Center: Rewind 10s, Play/Pause/Replay, Forward 10s
- Bottom: Seek bar with time labels, Repeat, Reset Zoom, Lock
- Feedback pills for seek time, volume %, brightness %, speed, aspect ratio
- Safe area insets — icons clear punch hole and notch

### Player Lock
- Lock button disables all gestures and controls
- Tap while locked shows "Unlock player first" message
- Unlock button shown in center

### Audio
- System volume (0–100%) via gesture and AudioManager
- Software amplification (101–200%) via ExoPlayer volume gain
- Audio focus management (request on enter, abandon on exit)
- Pause on phone call (TelephonyCallback API 31+, BroadcastReceiver API 26–30)
- Resume after call (configurable)
- Pause on headphone removal
- Pause when screen turns off (configurable)
- Mute Audio option in audio track menu

### Downloads
- Background downloads via WorkManager — survives app kill and device reboot
- Progress tracking reported to Room DB every poll cycle
- Pause, Resume, Retry, Delete per download
- Stall detection: marks FAILED after 5 consecutive inactive polls
- Exponential backoff retry (30s base)
- Download path always read at enqueue time (not cached)
- Configurable download location via SAF folder picker

### Library
- **Downloads tab:** active downloads (Downloading / Paused) + downloaded videos with search, sort, bulk select
  - Sort by: Name, Date, Size, Duration, Channel, Status
  - Bulk select: long-press, select all, delete selected, pause/resume selected
- **Favorites tab:** favorited videos from Room
- **Watch Later tab:** watch-later list from Room
- **History tab:** full viewing history from Room
- Empty state shown per tab

### Settings
- Searchable settings with keyword filtering
- Account: Telegram profile, phone number, username, profile photo
- Telegram Sources management
- Player: auto-play, resume after calls, pause on headphone removal, screen-off behavior, video end action
- Downloads: Wi-Fi only toggle, download location, gallery visibility
- Appearance: System / Light / Dark theme
- Language screen (full localization planned)
- Advanced Settings: clear cache, clear thumbnails, clear player cache, clear all storage, force Telegram reset, custom aspect ratios
- About: version, developer, GitHub, website, Telegram links
- Sign Out with confirmation

### Thumbnail System
- Thumbnails fetched from TDLib in background after feed loads
- In-memory cache keyed by TDLib fileId
- Cache invalidation signal — thumbnails reload without app restart after clearing

---

## Architecture

Hasikit follows Clean Architecture with a strict separation of concerns:

```
Presentation Layer    →  Jetpack Compose UI, ViewModels, Navigation Compose
Domain Layer          →  Repository interfaces, domain models
Data Layer            →  Room, TDLib, WorkManager, DataStore
```

Key architectural decisions:

- **Repository Pattern** — all data access goes through domain repository interfaces
- **Hilt DI** — ViewModels, Repositories, DownloadManager, and Player are all injected
- **WorkManager Downloads** — download workers survive app kills and device reboots
- **TDLib Integration Layer** — auth, channel loading, media fetch, and file download are isolated in `telegram/`
- **Media3 Singleton Player** — `HasikitPlayer` is a Hilt singleton that survives screen navigation
- **DataStore Preferences** — settings stored in `hasikit_settings` DataStore, shared across ViewModels
- **Room Database** — 6 tables covering all persistent state (see below)

### Room Database

| Table | Purpose |
|---|---|
| `videos` | All video metadata (title, fileId, localPath, isDownloaded, etc.) |
| `downloads` | Download tasks (state, progress, localPath) |
| `watch_progress` | Last position + duration per video |
| `favorites` | User-favorited videos |
| `watch_later` | User's watch-later list |
| `watch_history` | Full viewing history |

---

## Tech Stack

| Technology | Version | Purpose |
|---|---|---|
| Kotlin | 2.1.21 | Primary language |
| Jetpack Compose + Material 3 | BOM 2025.02.00 | UI |
| TDLib (libtdjni.so) | bundled | Telegram integration |
| Media3 ExoPlayer | 1.5.1 | Video playback |
| Hilt | 2.57.1 | Dependency injection |
| Room | 2.8.4 | Local database |
| DataStore Preferences | 1.1.1 | Settings persistence |
| WorkManager | 2.10.1 | Background downloads |
| Coil | 2.7.0 | Image loading |
| Kotlin Coroutines | 1.10.1 | Async operations |
| Navigation Compose | 2.8.8 | In-app navigation |
| Firebase Remote Config | 33.10.0 | Updates (planned) |

**Min SDK:** 26 (Android 8.0) | **Target SDK:** 35 | **Compile SDK:** 35

---

## Project Structure

```
app/src/main/java/com/trixsearch/hasikit/
│
├── data/
│   ├── local/
│   │   ├── dao/          # Room DAOs
│   │   ├── entities/     # Room entities (Video, Download, WatchProgress, Favorite, WatchLater, WatchHistory)
│   │   └── HasikitDatabase.kt
│   └── repository/       # Repository implementations
│
├── domain/
│   ├── model/            # Domain models (Video, DownloadTask, WatchProgress)
│   └── repository/       # Repository interfaces (VideoRepository, TelegramRepository)
│
├── telegram/
│   ├── config/           # TelegramSourceConfig — source definitions and feature flags
│   ├── data/
│   │   ├── repository/   # TelegramAuthRepositoryImpl, TelegramChannelRepositoryImpl, TelegramMediaRepositoryImpl
│   │   └── session/      # TelegramSessionManager
│   ├── domain/
│   │   ├── model/        # TelegramMedia, TelegramMediaItem, TelegramModels
│   │   └── repository/   # TelegramAuthRepository, TelegramChannelRepository, TelegramMediaRepository
│   └── service/          # TelegramClientService — TDLib lifecycle management
│
├── player/
│   └── HasikitPlayer.kt  # Media3 ExoPlayer singleton with all playback logic
│
├── download/
│   ├── DownloadWorker.kt         # WorkManager worker for background downloads
│   └── HasikitDownloadManager.kt # Download orchestration, state management
│
├── ui/
│   ├── components/       # FastScroller, ShimmerComponents
│   ├── navigation/       # NavGraph, Screen sealed class
│   ├── screens/
│   │   ├── auth/         # AuthScreen, AuthViewModel
│   │   ├── home/         # HomeScreen, HomeViewModel
│   │   ├── library/      # LibraryScreen
│   │   ├── player/       # PlayerScreen
│   │   ├── request/      # RequestContentScreen
│   │   ├── search/       # SearchScreen
│   │   └── settings/     # SettingsScreen, AdvancedSettingsScreen, LanguageScreen
│   └── theme/            # Color, Theme, Type
│
├── di/
│   ├── telegram/         # TelegramModule
│   ├── DatabaseModule.kt
│   └── RepositoryModule.kt
│
├── util/
│   ├── LanguageManager.kt
│   └── SampleData.kt
│
├── HasikitApp.kt         # Application class, Hilt entry point
└── MainActivity.kt
```

---

## Documentation

- [Architecture Guide](architecture.md) — detailed project architecture, folder structure, data flow, database design, architectural decisions, and implementation details. This is the primary technical reference for contributors.
- [Features & Changelog](features.md) — complete feature list, bug fix history, implementation details, debug log reference, and project evolution. Tracks all implemented functionality and historical changes.

---

## Installation

### Prerequisites

- Android Studio Meerkat or newer
- JDK 21
- A Telegram API ID and API Hash from [my.telegram.org](https://my.telegram.org)

### Setup

1. Clone the repository:
   ```bash
   git clone https://github.com/trixsearch/hasikit.git
   cd hasikit
   ```

2. Copy the example properties file:
   ```bash
   cp example.local.properties local.properties
   ```

3. Fill in `local.properties`:
   ```properties
   TELEGRAM_API_ID=your_api_id
   TELEGRAM_API_HASH=your_api_hash
   TELEGRAM_SOURCE_CHANNEL=@your_channel
   ALLOW_USER_SOURCES=true
   ```

4. Open the project in Android Studio and sync Gradle.

5. Build and run on a device or emulator (API 26+).

---

## Build Instructions

```bash
# Debug build
./gradlew assembleDebug

# Release build
./gradlew assembleRelease
```

The TDLib native library (`libtdjni.so`) is bundled in `app/jniLibs/` for all supported ABIs: `arm64-v8a`, `armeabi-v7a`, `x86`, `x86_64`.

---

## Telegram Setup

Hasikit requires a Telegram API application:

1. Go to [my.telegram.org](https://my.telegram.org) and log in
2. Navigate to **API development tools**
3. Create a new application
4. Copy the **App api_id** and **App api_hash** into `local.properties`
5. Set `TELEGRAM_SOURCE_CHANNEL` to the @username or chatId of your content channel

To control whether users can add their own sources, set `ALLOW_USER_SOURCES=true` or `false` in `local.properties`.

---

## Roadmap

### Completed
- ✅ Telegram Login + Session Restore
- ✅ OTT Feed with Infinite Scroll
- ✅ Telegram-native Search (TDLib)
- ✅ Advanced Media Player (ExoPlayer Media3)
- ✅ Full Gesture Controls
- ✅ Downloads with WorkManager
- ✅ Offline Playback
- ✅ Continue Watching
- ✅ Library (Favorites, Watch Later, History)
- ✅ Picture-in-Picture
- ✅ Theme System
- ✅ Settings with DataStore

### Planned
- 🔜 Chromecast support
- 🔜 Subtitle appearance customization (color, size, position, delay)
- 🔜 Custom subtitle font import (.ttf)
- 🔜 Equalizer / bass enhancement / audio presets
- 🔜 Firebase Remote Config (version checking, force updates)
- 🔜 Smart recommendations
- 🔜 Auto-play next video queue
- 🔜 Adaptive streaming quality selection
- 🔜 Full multi-language localization (Hindi, English, Marathi, Tamil, Telugu, and more)
- 🔜 Desktop / Linux / Web support

---

## Contributing

Contributions are welcome.

Before contributing, read:

- [Architecture Guide](architecture.md) — understand the project structure and patterns
- [Features & Changelog](features.md) — understand what is implemented and what is planned

When contributing:

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Update `features.md` and `architecture.md` to reflect your changes
5. Submit a pull request

Documentation updates are required as part of every contribution. A task is not complete until both `features.md` and `architecture.md` are updated.

---

## License

This project is open source. License details to be added.

---

*Built by [@trixsearch](https://github.com/trixsearch)*
