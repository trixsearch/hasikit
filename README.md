# Hasikit TV📺

A modern Android IPTV player app built with Jetpack Compose and ExoPlayer that streams live TV channels from M3U playlists.
This app is based on the source app of [My Live TV](https://github.com/Ravikant99/MyLiveTv), so please check the repo as well




## 📥 Installation
> **Latest Build:** 🎬 [Download APK via Github v1.0](https://raw.githubusercontent.com/trixsearch/hasikit/main/build/HasikitTv-betaV.apk)

## Features ✨

### 🎬 Video Playback
- **ExoPlayer Integration** - High-performance video streaming with HLS support
- **Live Stream Optimization** - Automatic live edge tracking and buffering management
- **Multiple Resize Modes** - FIT, ZOOM, and FILL options
- **Audio Focus Management** - Automatically pauses other media apps
- **Orientation Support** - Maintains playback state during screen rotation
- **Background Handling** - Proper pause/resume on app lifecycle changes

### 📱 User Interface
- **Modern Material 3 Design** - Clean, intuitive interface with dark theme
- **Swipeable Tabs** - Easy navigation between Categories, Languages, and Countries
- **Search Functionality** - Quick channel discovery
- **Smooth Animations** - Polished screen transitions
- **Android TV Support** - D-pad navigation and optimized layouts
- **Recently Watched** - Quick access to your favorite channels

### 🎯 Channel Management
- **Category Browsing** - Browse channels by category, language, or country
- **Channel Swipe Navigation** - Swipe left/right to switch channels during playback
- **Smart Caching** - Room database for offline channel lists (24-hour cache)
- **Recently Watched History** - Automatically tracks and sorts your viewing history

### 🚀 Performance
- **Optimized Buffering** - Adaptive buffer management for smooth playback
- **Efficient Caching** - Persistent storage with Room Database
- **Network Optimization** - HTTP/HTTPS support with connection pooling
- **Error Handling** - Comprehensive error logging and recovery

## Tech Stack 🛠️

### Core
- **Kotlin** - Modern, concise, and safe programming language
- **Jetpack Compose** - Declarative UI framework
- **Material 3** - Latest Material Design components

### Architecture
- **MVVM** - Clean architecture pattern
- **Hilt** - Dependency injection
- **Coroutines & Flow** - Asynchronous programming
- **Navigation Compose** - Type-safe navigation

### Media
- **Media3 ExoPlayer** - Advanced media playback
  - `media3-exoplayer` - Core player
  - `media3-ui` - Player UI components
  - `media3-exoplayer-hls` - HLS/M3U8 streaming support

### Networking
- **Ktor Client** - HTTP client for API calls
  - `ktor-client-okhttp` - OkHttp engine
  - `ktor-client-logging` - Request/response logging
- **OkHttp Logging Interceptor** - Detailed HTTP logs

### Data
- **Room Database** - Local data persistence
  - Channel caching
  - Recently watched history
- **Coil** - Image loading library

## Project Structure 📂

```
app/src/main/java/com/ravi/mylivetv/
├── data/
│   ├── local/
│   │   ├── dao/                 # Database access objects
│   │   ├── entity/              # Room entities
│   │   └── AppDatabase.kt       # Database configuration
│   ├── model/                   # Data models (DTOs)
│   ├── parser/                  # M3U playlist parser
│   ├── remote/                  # API services
│   └── repository/              # Data repositories
├── di/                          # Dependency injection modules
├── domain/                      # Domain models
├── navigation/                  # Navigation setup & routes
├── ui/
│   ├── channel/                 # Channel list screen
│   ├── composable/              # Reusable composables
│   ├── home/                    # Home screen with tabs
│   ├── player/                  # Video player screen
│   └── theme/                   # App theme & colors
└── utils/                       # Utility classes & helpers
```

## Screenshots 📸

### Home Screen
- Swipeable tabs (Categories, Languages, Countries)
- Recently Watched section
- Grid layout with channel categories

### Channel Screen
- Searchable channel list
- Channel logos and names
- Responsive grid layout

### Player Screen
- Full-screen video playback
- Player controls with auto-hide
- Channel name display
- Resize mode toggle
- Swipe gesture for channel switching

## Setup & Installation 🔧

### Prerequisites
- Android Studio Hedgehog or newer
- Android SDK 24+ (minimum)
- Android SDK 36 (target)
- Kotlin 2.0.21+

### Build Instructions

1. **Clone the repository**
   ```bash
   git clone https://github.com/YOUR_USERNAME/MyLiveTv.git
   cd MyLiveTv
   ```

2. **Open in Android Studio**
   - Open Android Studio
   - Select "Open an Existing Project"
   - Navigate to the cloned directory

3. **Sync Gradle**
   - Let Android Studio sync the project
   - Wait for dependencies to download

4. **Build & Run**
   ```bash
   ./gradlew assembleDebug
   ```
   Or click the "Run" button in Android Studio

## Configuration ⚙️

### Playlist Sources
Update the playlist URLs in `Constants.kt`:

```kotlin
object Constants {
    val TABS = listOf("Categories", "Languages", "Countries")
    val CATEGORIES = listOf("Recently Watched", "All", "Sports", "News", ...)
    val LANGUAGES = listOf("Recently Watched", "English", "Hindi", ...)
    val COUNTRIES = listOf("Recently Watched", "USA", "India", ...)
}
```

### Network Configuration
- HTTP support enabled in `AndroidManifest.xml`
- Configurable timeouts in `NetworkModule.kt`
- Logging level adjustable in `NetworkModule.kt`

## Key Features Implementation 💡

### Audio Issue Fix
Comprehensive audio configuration:
- Audio attributes for media playback
- Mixed MIME type adaptiveness
- Channel count adaptiveness
- Volume management
- Audio focus handling

### Player State Retention
- ViewModel-based state management
- Configuration change handling
- Saved playback position
- Live edge tracking on resume

### Caching Strategy
- Room database for persistent storage
- 24-hour cache expiration
- Category-based cache management
- Recently watched with timestamps

### Android TV Optimization
- D-pad navigation support
- Keyboard control for search
- Optimized grid layouts for TV screens
- Focus management

## Known Issues & Limitations ⚠️

- Requires active internet connection
- Some streams may not be compatible
- Performance varies based on network speed
- HTTP streams only (no DRM support)

## Future Enhancements 🚧

- [ ] Favorites management
- [ ] EPG (Electronic Program Guide) integration
- [ ] Picture-in-Picture (PiP) mode
- [ ] Chromecast support
- [ ] Download for offline viewing
- [ ] Parental controls
- [ ] Multiple playlist sources
- [ ] Subtitle support

## Dependencies 📦

```gradle
// Core Android
androidx.core:core-ktx:1.17.0
androidx.core:core-splashscreen:1.0.1
androidx.lifecycle:lifecycle-runtime-ktx:2.10.0
androidx.activity:activity-compose:1.12.1

// Compose
androidx.compose:compose-bom:2024.09.00
androidx.compose.material3:material3
androidx.compose.foundation:foundation:1.7.6

// Hilt
com.google.dagger:hilt-android:2.57.2
androidx.hilt:hilt-navigation-compose:1.3.0

// ExoPlayer
androidx.media3:media3-exoplayer:1.8.0
androidx.media3:media3-ui:1.8.0
androidx.media3:media3-exoplayer-hls:1.8.0

// Networking
io.ktor:ktor-client-core:2.3.7
io.ktor:ktor-client-okhttp:2.3.7
com.squareup.okhttp3:logging-interceptor:4.12.0

// Database
androidx.room:room-runtime:2.8.4
androidx.room:room-ktx:2.8.4

// Image Loading
io.coil-kt:coil-compose:2.7.0
```

## License 📄

This project is intended for educational purposes. Please ensure you have the rights to stream any content you access through this app.

## Contributing 🤝

Contributions are welcome! Please feel free to submit a Pull Request.

1. Fork the project
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## Author ✍️

1. Source App: [Ravi Kant Sharma](https://github.com/Ravikant99)
2. UI and Functionality : [trixsearch](https://github.com/trixsearch/)

## Acknowledgments 🙏

- ExoPlayer team for excellent media playback library
- Jetpack Compose for modern UI development
- Material Design for UI/UX guidelines
- Android development community

---

**Note**: This app is for educational purposes only. Ensure you have proper rights to stream any content.

