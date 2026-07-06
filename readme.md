# Hasikit

> **Telegram-Powered Streaming Platform with Netflix-Style UI and VLC-Level Playback**

**Developed by:** @trixsearch  
**GitHub:** <https://github.com/trixsearch/hasikit>  
**Website:** <https://trixsearch.github.io>  
**Project Web:** <https://trixsearch.github.io/hasikit> *(Currently Under Development)*

***

# 🚀 About Hasikit

Hasikit is an ambitious open-source media streaming platform designed to combine the best features of modern OTT services, Telegram storage, and advanced media players into a single application.

The vision is simple:

> **Browse like Netflix, Stream like Telegram, Play like VLC.**

Instead of relying on expensive cloud servers, Hasikit uses Telegram as the storage and content delivery layer while providing users with a premium streaming experience through a custom-built Android client.

The project is being designed as a modern, offline-capable, highly customizable media platform focused on performance, flexibility, and user control.

***

# 🎯 Project Vision

Hasikit aims to create a complete entertainment ecosystem where users can:

* Login with Telegram
* Browse content through a Netflix-inspired interface
* Stream Telegram-hosted videos
* Download content for offline viewing
* Use advanced VLC/MX Player level playback controls
* Enjoy deep subtitle customization
* Continue watching across sessions
* Manage personal libraries efficiently

***

# 🏗 Core Architecture

Hasikit is built around a client-side architecture:

```text
Android Application
        │
        ├── Telegram TDLib
        │       └── Media Storage & Authentication
        │
        ├── ExoPlayer (Media3)
        │       └── Video Playback Engine
        │
        ├── Room Database
        │       └── Offline Storage
        │
        ├── Firebase Remote Config
        │       └── Updates & Configuration
        │
        └── Jetpack Compose
                └── User Interface
```

No traditional backend server is required.

***

# 🔐 Authentication System

### Telegram-Based Login

Users authenticate using:

* Phone Number
* OTP Verification
* Telegram Session Management

Future support:

* Subscription Keys
* Activation Codes
* Premium Access Validation

***

# 📡 Telegram Integration

Telegram serves as the media backend.

Supported functionality:

* Channel access
* Message retrieval
* Video metadata extraction
* File information retrieval
* Thumbnail extraction
* Media organization

Future TDLib integration includes:

* Real-time content synchronization
* Channel management
* Smart caching
* Background media fetching

***

# 🎥 Core Streaming Logic

The entire Hasikit ecosystem is built around one primary concept:

### Telegram ➜ Cache ➜ Player

Workflow:

```text
User Selects Video
        ↓
Telegram File Request
        ↓
Progressive Download Begins
        ↓
Local Cache Created
        ↓
ExoPlayer Starts Playback
        ↓
Remaining Data Downloads In Background
```

This creates a streaming experience even though files originate from Telegram.

***

# 📥 Download System

### Features

* Background downloads
* Download progress tracking
* Pause downloads
* Resume downloads
* Re-download support
* Local storage management
* Offline playback

### Download Rules

* Use local file if already downloaded
* Skip unnecessary downloads
* Smart file reuse

***

# 💾 Offline Database System

Room Database stores:

### Watch Progress

* Last watched position
* Total duration
* Resume location

### Download Information

* Download status
* Local file path
* Progress percentage

### Favorites

* User favorites
* Watchlist

### Recently Watched

* Viewing history
* Playback tracking

### Search History

* Previous searches
* Recommendations source

***

# 🎬 Advanced Media Player

Hasikit is designed to include an advanced custom media player inspired by:

* VLC
* MX Player
* Nova Player
* YouTube

***

# 🎮 Playback Controls

### Basic Controls

* Play
* Pause
* Stop
* Replay
* Seek

### Playback Speed

* 0.25x
* 0.5x
* 1.0x
* 1.25x
* 1.5x
* 2.0x

***

# 🔥 Gesture Controls

### Double Tap Navigation

Left Side:

```text
10s Backward
20s Backward
30s Backward
40s Backward
...
```

Right Side:

```text
10s Forward
20s Forward
30s Forward
40s Forward
...
```

Continuous taps stack timing exactly like YouTube and MX Player.

***

### Hold To Speed

Long press right side of screen:

```text
Normal Speed
        ↓
Hold
        ↓
2x Playback
        ↓
Release
        ↓
Normal Speed
```

***

### Swipe Controls

#### Left Vertical Swipe

* Screen brightness adjustment

#### Right Vertical Swipe

* Volume control

#### Horizontal Swipe

* Timeline seeking

***

# 🔒 Lock Controls

Player lock functionality:

* Prevent accidental touches
* Disable gestures
* Disable unintended controls
* Dedicated unlock action

***

# 🔊 Advanced Audio Features

### Volume Boost

Up to:

* 100%
* 150%
* 200%

Software audio amplification.

### Future Audio Features

* Equalizer
* Bass Enhancement
* Audio Presets
* Audio Language Selection

***

# 🖥 Video Scaling & Screen Controls

Users can customize video rendering through:

### Aspect Ratio Modes

* Fit
* Fill
* Crop
* Zoom
* Stretch
* Original Size

### Screen Management

* Portrait Mode
* Landscape Mode
* Auto Rotation

Independent from system settings.

***

# 🎨 Advanced Subtitle Engine

### Subtitle Support

Supported formats:

* Embedded Subtitles
* SRT
* VTT

***

# Subtitle Customization

Users can modify:

### Appearance

* Subtitle Color
* Border Color
* Outline Color
* Background Color
* Background Opacity

### Typography

* Font Family
* Font Size
* Font Weight

### Layout

* Subtitle Position
* Subtitle Offset
* Subtitle Delay
* Sync Controls

***

# 🔤 Custom Subtitle Fonts

Font Support:

### Internal Fonts

Built into app

### External Fonts

User-imported:

```text
.ttf
```

Custom subtitle styling similar to professional media players.

***

# 📺 Picture-in-Picture (PiP)

### Features

* Floating playback
* Background viewing
* Playback controls
* Resume functionality

***

# 📡 Chromecast Support

Users will be able to:

* Cast content to TVs
* Control playback remotely
* Stream media on larger displays

***

# 🔍 Search System

### Intelligent Title Processing

Example:

```text
My.Movie.1080p.x264.Final.mkv
```

Becomes:

```text
My Movie
```

Search is performed using cleaned titles rather than raw filenames.

***

# 📚 Library System

Includes:

* Downloaded Videos
* Continue Watching
* Favorites
* History
* Watch Progress

***

# 🏠 Home Screen

Netflix-inspired experience featuring:

* Large thumbnails
* Horizontal rows
* Responsive grids
* Recently Added
* Continue Watching
* Trending Content
* Favorites Section

***

# ⚙ Settings & Preferences

### Account

* Telegram Profile
* Session Information

### Playback Settings

* Gesture Configuration
* Subtitle Preferences
* Audio Settings

### Storage

* Cache Management
* Download Folder Selection
* Storage Statistics

### Application

* Language
* Theme
* Version

***

# 🌐 Multi-Language Support

Planned support:

* Hindi (Default)
* English
* Marathi
* Tamil
* Telugu
* Bengali
* Gujarati
* Kannada
* Malayalam
* Punjabi
* Urdu
* Odia
* Assamese
* Nepali
* Sanskrit

All player controls and application screens will support localization.

***

# 🔄 Update System

Powered by Firebase Remote Config.

Features:

* Version Checking
* Update Notifications
* Force Updates
* APK Distribution

***

# 🎨 Design Philosophy

Hasikit follows:

* Dark Theme First
* Netflix-Inspired Layout
* Smooth Animations
* Gesture-Based Navigation
* Minimal Design
* High Performance
* Offline First
* Media-Focused Experience

***

# 🛣 Future Roadmap

### Phase 1

* Core UI
* Media Player
* Download System
* Library System

### Phase 2

* Telegram TDLib Integration
* Real Content Fetching
* Caching System

### Phase 3

* Advanced Gestures
* Volume Boost
* Subtitle Engine
* Aspect Ratio Controls

### Phase 4

* Chromecast
* PiP Enhancements
* Smart Recommendations

### Phase 5

* Desktop Support
* Linux Support
* Web Integration
* Cross-Platform Expansion

***

# 🌐 Web Ecosystem

### TrixSearch

Website:

<https://trixsearch.github.io>

### Hasikit Web

Project:

<https://trixsearch.github.io/>

> The web version is currently under development, but it represents the long-term vision of making Hasikit available beyond Android and creating a complete cross-platform media ecosystem.

***

# ❤️ Open Source

Hasikit is an open-source project built and maintained by:

**@trixsearch**

GitHub Profile:

<https://github.com/trixsearch>

Repository:

<https://github.com/trixsearch/hasikit>

***

# 🚀 One-Line Description

> **Hasikit is a Telegram-powered, Netflix-Amazon Prime-inspired streaming platform featuring VLC-level media playback, advanced subtitle customization, offline downloads, and a highly customizable user experience.**
