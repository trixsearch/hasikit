# HASIKIT DOCUMENTATION MAINTENANCE RULE

These files are mandatory and must be maintained automatically:

- features.md
- architecture.md

Whenever any code is added, modified, removed, refactored, migrated, optimized, or fixed:

Update both files as part of the same task.

Do not wait for explicit instructions.

A task is NOT complete until documentation is updated.

---

# features.md

Purpose:

Track all application features, bug fixes, improvements, migrations, settings, and user-visible functionality.

Structure:

# Hasikit Features

## Current Features

### Authentication
- Telegram Login
- OTP Verification
- Session Restore
- Logout

### Telegram Integration
- Public Channels
- Private Channels
- Source Resolution
- Telegram Search

### Home Screen
- Feed Loading
- Infinite Scroll
- Pull To Refresh
- Channel Filtering

### Player
- Play/Pause
- Double Tap Gestures
- Volume Gestures
- Brightness Gestures
- Aspect Ratio Controls
- Audio Track Selection
- Subtitle Support
- PiP
- Player Lock

### Downloads
- Download Manager
- Pause
- Resume
- Delete
- Download Location Selection
- Gallery Visibility

### Library
- Downloads
- Favorites
- Watch Later
- Watch History
- Bulk Selection
- Sorting

### Search
- Local Search
- Telegram Search

### Themes
- Dark Mode
- Light Mode
- Theme Presets

### Settings
- Player Settings
- Storage Settings
- Advanced Settings

### Notifications
- Media Session
- Lock Screen Controls

---

## Recent Improvements

### YYYY-MM-DD

- Added...
- Fixed...
- Improved...

---

## Known Issues

- Issue...
- Limitation...

---

## Future Roadmap

- Planned Feature...
- Planned Improvement...

Rules:

1. Add new features.
2. Update modified features.
3. Remove deleted features.
4. Add all bug fixes.
5. Add all migrations.
6. Add all architecture-impacting changes.
7. Keep entries concise and human readable.

---
CHAT HISTORY COMPACTION RULE

Amazon Q sometimes triggers:

"Compact chat history?"

when the conversation context becomes full.

Whenever chat compaction happens, or whenever Amazon Q summarizes previous work before compacting history:

1. Treat the compaction summary as an official project update.

2. Automatically update features.md.

3. Automatically update architecture.md if architecture, database, storage, search, player, download, or project structure information was included in the compaction summary.

4. Add a new section in features.md:

## Development History

### YYYY-MM-DD

and record:

- Features completed
- Bugs fixed
- Refactors performed
- Architecture changes
- Storage changes
- Search improvements
- Player improvements
- Database changes
- Download system changes

5. If the compaction summary contains implementation details, copy the important outcomes into the appropriate section of features.md rather than storing the raw summary.

6. If new files, folders, Room entities, DAOs, repositories, ViewModels, Workers, services, modules, or screens were created, update architecture.md automatically.

7. If architecture decisions were made during the conversation, add them under:

# Architecture Decisions

with the date.

Examples:

### 2026-07-21

- Downloads migrated to WorkManager.
- Favorites table added.
- WatchLater table added.
- WatchHistory table added.
- Continue Watching stores playback position only.
- Hidden video cache removed.
- Download model uses only DOWNLOADED and NOT_DOWNLOADED states.

8. Documentation must survive chat compaction.

Nothing important from the compaction summary should be lost.

9. Before ending a task, verify:

- features.md reflects all completed work.
- architecture.md reflects all architectural changes.

10. A task is not considered complete until both documentation files are synchronized with the latest chat compaction summary.

Compacted chat history summaries are treated as official project records and must be merged into project documentation automatically.

---

# architecture.md

Purpose:

Act as the source of truth for Hasikit architecture.

Structure:

# Hasikit Architecture

## Tech Stack

- Kotlin
- Jetpack Compose
- TDLib
- Media3 ExoPlayer
- Hilt
- Room
- DataStore
- WorkManager

---

## High Level Architecture

Presentation Layer

UI
ViewModels
Navigation

Domain Layer

Repository Interfaces
Use Cases

Data Layer

Repositories
Room
Telegram
Storage

---

## Project Structure

app/

data/
local/
remote/
repository/

domain/
model/
repository/

ui/
screens/
components/
theme/

player/

download/

telegram/

di/

---

## Database

Tables:

VideoEntity
DownloadEntity
WatchProgressEntity
FavoriteEntity
WatchLaterEntity
WatchHistoryEntity

Document:

Fields
Relationships
Purpose

---

## Download Architecture

Current strategy:

- WorkManager
- TDLib DownloadFile
- Background Downloads
- Pause/Resume

State Model:

DOWNLOADED
NOT_DOWNLOADED

No hidden video cache allowed.

---

## Continue Watching

Stores:

videoId
position

Does not store hidden downloads.

Validates file existence before resume.

---

## Playback Architecture

Media3 ExoPlayer

Supported:

MP4
MKV
M4V
MOV
WEBM
3GP

Document known format limitations.

---

## Telegram Architecture

Authentication
Session Restore
Channel Loading
Pagination
Search

Document:

Chat IDs
Invite Links
Source Resolution

---

## Storage Architecture

Streaming Cache

Downloaded Files

Thumbnail Cache

Player Cache

Document exact paths.

---

## Theme System

Document all supported themes.

---

## Settings Architecture

Player
Downloads
Storage
Advanced Settings

---

## Architecture Decisions

Keep a historical log of important decisions.

Example:

### 2026-07-20

- Migrated downloads to WorkManager.
- Added Favorites table.
- Added WatchLater table.
- Added WatchHistory table.
- Removed hidden video cache.
- Continue Watching now tracks positions only.

---

Rules:

1. Update folder structure whenever new folders/files are created.
2. Update database diagram whenever entities change.
3. Update architecture decisions whenever the implementation strategy changes.
4. Document all major refactors.
5. Keep architecture.md synchronized with the real codebase.
6. Remove obsolete architecture descriptions.

---

Documentation Update Rule

Every completed coding task must end with:

1. Updating features.md
2. Updating architecture.md
3. Verifying documentation matches the implementation

Documentation is considered part of the codebase, not optional.