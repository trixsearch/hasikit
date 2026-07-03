PROJECT: Hasikit

Development Workflow

- I use Amazon Q inside VS Code for code generation.
- I use Android Studio only for building, debugging and testing.
- Do not provide IDE instructions.
- Do not provide Android Studio menu instructions.
- Do not provide VS Code instructions.
- Focus only on source code changes.

Technology Stack

- Kotlin
- Jetpack Compose
- Hilt
- Room
- Media3 (ExoPlayer)
- Navigation Compose
- Coroutines
- JDK 21
- Gradle 8.x

Architecture Rules

- Preserve existing architecture.
- Preserve existing package structure.
- Preserve existing navigation flow.
- Preserve existing Hilt setup.
- Preserve existing Room setup.
- Preserve existing Media3 setup.
- Preserve existing repositories and ViewModels.
- Never remove features unless explicitly requested.
- Never simplify existing functionality.
- Never replace working code with placeholder code.
- Never downgrade functionality.

Code Generation Rules

- Always generate production-ready code.
- Always generate complete implementations.
- Do not generate TODO comments.
- Do not generate placeholder implementations.
- Do not generate mock implementations.
- Do not generate demo-only solutions.
- Do not generate pseudo-code.
- Complete all required wiring between UI, ViewModel, Repository and Data layers.

Modification Rules

- Before modifying code, analyze the current implementation.
- Modify only what is necessary.
- Preserve all existing functionality.
- Do not remove existing methods.
- Do not remove existing classes.
- Do not remove existing screens.
- Do not remove existing Room entities.
- Do not remove existing dependencies.
- Extend and improve existing code instead.

Build Rules

- All generated code must compile.
- All generated code must be Kotlin 2.x compatible.
- All generated code must be JDK 21 compatible.
- All generated code must be Compose compatible.
- All generated code must be Media3 compatible.
- All generated code must be Hilt compatible.

Error Handling Rules

- Handle network failures.
- Handle offline situations.
- Handle permission denial.
- Handle null values.
- Handle unexpected exceptions.
- Add logging where appropriate.

Media Rules

Hasikit is a streaming platform.

Every media feature should support:

- MP4
- MOV
- MKV
- WebM
- M4V

Playback requirements:

- Streaming
- Pause
- Resume
- Seek
- Buffering
- Progress tracking
- Retry support

Download requirements:

- Background download
- Pause
- Resume
- Cancel
- Progress tracking
- Offline playback

UI Rules

- Use Material 3.
- Maintain modern Netflix-style UI.
- Respect dark theme.
- Avoid blank screens.
- Avoid dead buttons.
- Every visible screen should have working functionality.

Response Rules

- Return complete code changes.
- Return production-ready solutions.
- Explain why changes are needed.
- Mention all files modified.
- Mention all newly created files.
- Mention all dependencies added or changed.
- Do not provide partial solutions.
- Do not stop halfway through implementation.