package com.trixsearch.hasikit.ui.screens.settings

import android.content.Context
import android.content.Intent
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.trixsearch.hasikit.BuildConfig
import com.trixsearch.hasikit.domain.repository.VideoRepository
import com.trixsearch.hasikit.download.HasikitDownloadManager
import com.trixsearch.hasikit.themeDataStore
import com.trixsearch.hasikit.ui.theme.AppTheme
import com.trixsearch.hasikit.THEME_KEY
import com.trixsearch.hasikit.telegram.config.TelegramSource
import com.trixsearch.hasikit.telegram.config.TelegramSourceConfig
import com.trixsearch.hasikit.telegram.domain.model.TelegramUser
import com.trixsearch.hasikit.telegram.domain.repository.TelegramAuthRepository
import com.trixsearch.hasikit.ui.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "SettingsScreen"
private val Context.settingsDataStore by preferencesDataStore(name = "hasikit_settings")
private val KEY_WIFI_ONLY = booleanPreferencesKey("wifi_only_downloads")
private val KEY_AUTO_PLAY = booleanPreferencesKey("auto_play")
private val KEY_STREAMING_QUALITY = stringPreferencesKey("streaming_quality")
private val KEY_GALLERY_VISIBLE = booleanPreferencesKey("gallery_visible")
private val KEY_DOWNLOAD_PATH = stringPreferencesKey("download_path")
// Added resume playback after phone calls setting
private val KEY_RESUME_AFTER_CALL = booleanPreferencesKey("resume_after_call")
// Added pause on headphone removal setting
private val KEY_PAUSE_ON_HEADPHONE_REMOVAL = booleanPreferencesKey("pause_on_headphone_removal")
// Added background audio (continue when screen locked) setting
private val KEY_BACKGROUND_AUDIO = booleanPreferencesKey("background_audio")
// Added custom aspect ratios stored as comma-separated W:H strings
private val KEY_CUSTOM_ASPECT_RATIOS = stringPreferencesKey("custom_aspect_ratios")

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: VideoRepository,
    private val downloadManager: HasikitDownloadManager,
    private val telegramAuthRepository: TelegramAuthRepository,
    private val telegramSourceConfig: TelegramSourceConfig
) : ViewModel() {

    // Theme — stored in the same DataStore as MainActivity reads
    val appTheme: StateFlow<AppTheme> = context.themeDataStore.data
        .map { prefs -> runCatching { AppTheme.valueOf(prefs[THEME_KEY] ?: AppTheme.DARK.name) }.getOrDefault(AppTheme.DARK) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppTheme.DARK)

    fun setTheme(theme: AppTheme) {
        viewModelScope.launch {
            context.themeDataStore.edit { it[THEME_KEY] = theme.name }
            Log.d(TAG, "theme=$theme")
        }
    }

    // Settings
    val wifiOnly: StateFlow<Boolean> = context.settingsDataStore.data
        .map { it[KEY_WIFI_ONLY] ?: false }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val autoPlay: StateFlow<Boolean> = context.settingsDataStore.data
        .map { it[KEY_AUTO_PLAY] ?: true }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val streamingQuality: StateFlow<String> = context.settingsDataStore.data
        .map { it[KEY_STREAMING_QUALITY] ?: "Auto" }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "Auto")

    fun setWifiOnly(v: Boolean) = viewModelScope.launch { context.settingsDataStore.edit { it[KEY_WIFI_ONLY] = v } }
    fun setAutoPlay(v: Boolean) = viewModelScope.launch { context.settingsDataStore.edit { it[KEY_AUTO_PLAY] = v } }
    fun setStreamingQuality(v: String) = viewModelScope.launch { context.settingsDataStore.edit { it[KEY_STREAMING_QUALITY] = v } }

    // Added resume after call preference
    val resumeAfterCall: StateFlow<Boolean> = context.settingsDataStore.data
        .map { it[KEY_RESUME_AFTER_CALL] ?: true }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    // Added pause on headphone removal preference
    val pauseOnHeadphoneRemoval: StateFlow<Boolean> = context.settingsDataStore.data
        .map { it[KEY_PAUSE_ON_HEADPHONE_REMOVAL] ?: true }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    // Added background audio (continue when screen locked) preference
    val backgroundAudio: StateFlow<Boolean> = context.settingsDataStore.data
        .map { it[KEY_BACKGROUND_AUDIO] ?: true }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    // Added custom aspect ratios stored as comma-separated W:H strings
    val customAspectRatios: StateFlow<List<String>> = context.settingsDataStore.data
        .map { prefs ->
            val raw = prefs[KEY_CUSTOM_ASPECT_RATIOS] ?: ""
            if (raw.isBlank()) emptyList() else raw.split(",").filter { it.isNotBlank() }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setResumeAfterCall(v: Boolean) = viewModelScope.launch { context.settingsDataStore.edit { it[KEY_RESUME_AFTER_CALL] = v } }
    fun setPauseOnHeadphoneRemoval(v: Boolean) = viewModelScope.launch { context.settingsDataStore.edit { it[KEY_PAUSE_ON_HEADPHONE_REMOVAL] = v } }
    fun setBackgroundAudio(v: Boolean) = viewModelScope.launch { context.settingsDataStore.edit { it[KEY_BACKGROUND_AUDIO] = v } }

    fun addCustomAspectRatio(ratio: String) {
        viewModelScope.launch {
            val current = customAspectRatios.value.toMutableList()
            if (!current.contains(ratio.trim())) {
                current.add(ratio.trim())
                context.settingsDataStore.edit { it[KEY_CUSTOM_ASPECT_RATIOS] = current.joinToString(",") }
            }
        }
    }

    fun removeCustomAspectRatio(ratio: String) {
        viewModelScope.launch {
            val updated = customAspectRatios.value.filter { it != ratio }
            context.settingsDataStore.edit { it[KEY_CUSTOM_ASPECT_RATIOS] = updated.joinToString(",") }
        }
    }

    val galleryVisible: StateFlow<Boolean> = context.settingsDataStore.data
        .map { it[KEY_GALLERY_VISIBLE] ?: false }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val downloadPath: StateFlow<String> = context.settingsDataStore.data
        .map { it[KEY_DOWNLOAD_PATH] ?: "" }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    init {
        // Sync persisted download path to download manager on startup
        viewModelScope.launch {
            downloadPath.collect { path ->
                if (path.isNotBlank()) downloadManager.customDownloadPath.value = path
            }
        }
    }

    fun setGalleryVisible(v: Boolean) {
        viewModelScope.launch {
            context.settingsDataStore.edit { it[KEY_GALLERY_VISIBLE] = v }
            if (v) scanDownloadedFilesIntoGallery()
        }
    }

    fun setDownloadPath(uri: String) {
        viewModelScope.launch {
            context.settingsDataStore.edit { it[KEY_DOWNLOAD_PATH] = uri }
            downloadManager.customDownloadPath.value = uri
        }
    }

    private fun scanDownloadedFilesIntoGallery() {
        viewModelScope.launch(Dispatchers.IO) {
            val dir = context.getExternalFilesDir(Environment.DIRECTORY_MOVIES) ?: return@launch
            val files = dir.listFiles()?.filter { it.isFile } ?: return@launch
            if (files.isEmpty()) return@launch
            val paths = files.map { it.absolutePath }.toTypedArray()
            MediaScannerConnection.scanFile(context, paths, null) { path, _ ->
                Log.d(TAG, "MediaScanner scanned: $path")
            }
        }
    }

    // Storage stats
    private val _cacheSize = MutableStateFlow(0L)
    val cacheSize: StateFlow<Long> = _cacheSize

    private val _storageUsed = MutableStateFlow(0L)
    val storageUsed: StateFlow<Long> = _storageUsed

    private val _downloadCount = MutableStateFlow(0)
    val downloadCount: StateFlow<Int> = _downloadCount

    fun refreshStorageStats() {
        viewModelScope.launch(Dispatchers.IO) {
            _cacheSize.value = calcCacheSize()
            _storageUsed.value = calcDownloadStorage()
            _downloadCount.value = countDownloadedFiles()
        }
    }

    fun clearCache() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                context.cacheDir.deleteRecursively()
                context.externalCacheDir?.deleteRecursively()
                _cacheSize.value = 0L
                Log.d(TAG, "Cache cleared")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to clear cache", e)
            }
        }
    }

    fun clearAllStorage() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Delete all downloaded files from disk
                context.getExternalFilesDir(Environment.DIRECTORY_MOVIES)?.let { dir ->
                    dir.listFiles()?.forEach { it.delete() }
                }
                // Clear cache
                context.cacheDir.deleteRecursively()
                context.externalCacheDir?.deleteRecursively()
                // Clear all DB records (downloads, videos, watch progress)
                repository.clearAllStorage()
                Log.d(TAG, "All storage cleared")
                refreshStorageStats()
            } catch (e: Exception) {
                Log.e(TAG, "Failed to clear all storage", e)
            }
        }
    }

    private fun calcCacheSize(): Long {
        var size = context.cacheDir.walkTopDown().filter { it.isFile }.sumOf { it.length() }
        context.externalCacheDir?.walkTopDown()?.filter { it.isFile }?.forEach { size += it.length() }
        return size
    }

    private fun calcDownloadStorage(): Long {
        val dir = context.getExternalFilesDir(Environment.DIRECTORY_MOVIES) ?: return 0L
        return dir.walkTopDown().filter { it.isFile }.sumOf { it.length() }
    }

    private fun countDownloadedFiles(): Int {
        val dir = context.getExternalFilesDir(Environment.DIRECTORY_MOVIES) ?: return 0
        return dir.listFiles()?.count { it.isFile } ?: 0
    }

    // Telegram account
    val currentUser: StateFlow<com.trixsearch.hasikit.telegram.domain.model.TelegramUser?> =
        telegramAuthRepository.authState
            .map { state ->
                if (state is com.trixsearch.hasikit.telegram.domain.model.AuthState.Authenticated) state.user
                else null
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun logout() {
        viewModelScope.launch {
            Log.d(TAG, "logout")
            telegramAuthRepository.logout()
        }
    }

    fun forceDeleteSession() {
        viewModelScope.launch {
            Log.d(TAG, "forceDeleteSession")
            telegramAuthRepository.forceDeleteSession()
        }
    }

    // Telegram source channel
    val officialSources = telegramSourceConfig.officialSources

    val userSources: StateFlow<List<TelegramSource>> = telegramSourceConfig.userSourcesFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addUserSource(identifier: String, displayName: String) {
        viewModelScope.launch {
            telegramSourceConfig.addUserSource(
                TelegramSource(identifier = identifier.trim(), displayName = displayName.trim())
            )
        }
    }

    fun removeUserSource(identifier: String) {
        viewModelScope.launch {
            telegramSourceConfig.removeUserSource(identifier)
        }
    }
}

// ─── Section model for search filtering ───────────────────────────────────────

private data class SettingsSection(
    val id: String,
    val title: String,
    val icon: ImageVector,
    val keywords: List<String>
)

// Settings section order: Account, Sources, Player, Downloads, Appearance, Language, Advanced, Request, About, Sign Out
private val ALL_SECTIONS = listOf(
    SettingsSection("account", "Account", Icons.Default.AccountCircle, listOf("account", "telegram", "login", "profile", "phone", "user")),
    SettingsSection("sources", "Telegram Sources", Icons.Default.Subscriptions, listOf("source", "channel", "telegram", "media", "content")),
    SettingsSection("player", "Player", Icons.Default.PlayCircle, listOf("player", "playback", "auto", "quality", "speed", "stream", "call", "headphone", "background", "audio", "lock")),
    SettingsSection("downloads", "Downloads", Icons.Default.Download, listOf("download", "wifi", "network", "location", "folder")),
    SettingsSection("appearance", "Appearance", Icons.Default.Palette, listOf("appearance", "theme", "dark", "light", "color")),
    SettingsSection("language", "Language", Icons.Default.Language, listOf("language", "hindi", "english", "locale", "translation")),
    // Advanced settings section — dangerous options moved here from main settings
    SettingsSection("advanced", "Advanced Settings", Icons.Default.Tune, listOf("advanced", "cache", "clear", "delete", "telegram", "reset", "aspect", "ratio", "custom")),
    SettingsSection("request", "Request Content", Icons.Default.MovieFilter, listOf("request", "movie", "anime", "series", "content", "bot")),
    SettingsSection("about", "About", Icons.Default.Info, listOf("about", "version", "developer", "github", "website", "trixsearch")),
    // Sign Out moved to bottom — users rarely need it
    SettingsSection("signout", "Sign Out", Icons.AutoMirrored.Filled.ExitToApp, listOf("logout", "sign out", "exit", "session"))
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val appTheme by viewModel.appTheme.collectAsState()
    val wifiOnly by viewModel.wifiOnly.collectAsState()
    val autoPlay by viewModel.autoPlay.collectAsState()
    val streamingQuality by viewModel.streamingQuality.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val userSources by viewModel.userSources.collectAsState()
    val officialSources = viewModel.officialSources
    val galleryVisible by viewModel.galleryVisible.collectAsState()
    val downloadPath by viewModel.downloadPath.collectAsState()
    val resumeAfterCall by viewModel.resumeAfterCall.collectAsState()
    val pauseOnHeadphoneRemoval by viewModel.pauseOnHeadphoneRemoval.collectAsState()
    val backgroundAudio by viewModel.backgroundAudio.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var showThemeDialog by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { viewModel.refreshStorageStats() }

    val visibleSections = remember(searchQuery) {
        if (searchQuery.isBlank()) ALL_SECTIONS
        else ALL_SECTIONS.filter { section ->
            section.keywords.any { it.contains(searchQuery.trim(), ignoreCase = true) } ||
                section.title.contains(searchQuery.trim(), ignoreCase = true)
        }
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.statusBars)
                    // Title Top Padding — reduce to tighten header spacing
                    .padding(horizontal = 16.dp, vertical = 0.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Settings, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(22.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Settings", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search settings…", style = MaterialTheme.typography.bodyMedium) },
                    leadingIcon = { Icon(Icons.Default.Search, null, modifier = Modifier.size(20.dp)) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }, modifier = Modifier.size(36.dp)) {
                                Icon(Icons.Default.Clear, "Clear", modifier = Modifier.size(18.dp))
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                    )
                )
            }
        }
    ) { padding ->
        if (visibleSections.isEmpty()) {
            Box(
                modifier = Modifier.padding(padding).fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.SearchOff, null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(8.dp))
                    Text("No settings found for \"$searchQuery\"", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding).fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(visibleSections, key = { it.id }) { section ->
                    when (section.id) {
                        "account" -> AccountSection(currentUser)
                        "sources" -> TelegramSourcesSection(
                            officialSources = officialSources,
                            userSources = userSources,
                            onAddSource = viewModel::addUserSource,
                            onRemoveSource = viewModel::removeUserSource
                        )
                        "player" -> PlayerSection(
                            autoPlay = autoPlay,
                            quality = streamingQuality,
                            onAutoPlay = viewModel::setAutoPlay,
                            // Reserved for future adaptive quality support
                            onQualityClick = {},
                            resumeAfterCall = resumeAfterCall,
                            onResumeAfterCall = viewModel::setResumeAfterCall,
                            pauseOnHeadphoneRemoval = pauseOnHeadphoneRemoval,
                            onPauseOnHeadphoneRemoval = viewModel::setPauseOnHeadphoneRemoval,
                            backgroundAudio = backgroundAudio,
                            onBackgroundAudio = viewModel::setBackgroundAudio
                        )
                        "downloads" -> DownloadsSection(
                            wifiOnly = wifiOnly,
                            onWifiOnly = viewModel::setWifiOnly,
                            galleryVisible = galleryVisible,
                            onGalleryVisible = viewModel::setGalleryVisible,
                            downloadPath = downloadPath,
                            onPickFolder = viewModel::setDownloadPath
                        )
                        "appearance" -> AppearanceSection(appTheme, onThemeClick = { showThemeDialog = true })
                        "language" -> LanguageSection(onClick = { navController.navigate(Screen.Language.route) })
                        // Advanced settings navigates to dedicated sub-screen
                        "advanced" -> AdvancedSection(onClick = { navController.navigate(Screen.AdvancedSettings.route) })
                        "request" -> RequestSection(onClick = { navController.navigate(Screen.RequestContent.route) })
                        "about" -> AboutSection()
                        // Sign Out section — placed at bottom so users don’t tap it accidentally
                        "signout" -> SignOutSection(onLogout = { showLogoutDialog = true })
                    }
                }
                item { Spacer(Modifier.height(8.dp)) }
            }
        }
    }

    // Theme dialog
    if (showThemeDialog) {
        AlertDialog(
            onDismissRequest = { showThemeDialog = false },
            icon = { Icon(Icons.Default.Palette, null) },
            title = { Text("App Theme") },
            text = {
                Column {
                    listOf(AppTheme.SYSTEM to "System Default", AppTheme.LIGHT to "Light", AppTheme.DARK to "Dark").forEach { (theme, label) ->
                        Row(
                            modifier = Modifier.fillMaxWidth().clickable { viewModel.setTheme(theme); showThemeDialog = false }.padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(selected = appTheme == theme, onClick = { viewModel.setTheme(theme); showThemeDialog = false })
                            Text(label, modifier = Modifier.padding(start = 8.dp))
                        }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { showThemeDialog = false }) { Text("Cancel") } }
        )
    }

    // Logout dialog
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            icon = { Icon(Icons.AutoMirrored.Filled.ExitToApp, null, tint = MaterialTheme.colorScheme.error) },
            title = { Text("Sign Out") },
            text = { Text("Sign out of your Telegram account? You will need to log in again to access content.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.logout()
                        showLogoutDialog = false
                        navController.navigate(com.trixsearch.hasikit.ui.navigation.Screen.Auth.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text("Sign Out") }
            },
            dismissButton = { TextButton(onClick = { showLogoutDialog = false }) { Text("Cancel") } }
        )
    }
}

// ─── Section composables ──────────────────────────────────────────────────────

@Composable
private fun TelegramSourcesSection(
    officialSources: List<com.trixsearch.hasikit.telegram.config.TelegramSource>,
    userSources: List<com.trixsearch.hasikit.telegram.config.TelegramSource>,
    onAddSource: (identifier: String, displayName: String) -> Unit,
    onRemoveSource: (identifier: String) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var newIdentifier by remember { mutableStateOf("") }
    var newDisplayName by remember { mutableStateOf("") }

    SettingsGroup("Content Sources", Icons.Default.Subscriptions) {
        // Official sources — always visible, cannot be removed
        officialSources.forEach { source ->
            ListItem(
                headlineContent = { Text(source.displayName, fontWeight = FontWeight.Medium) },
                supportingContent = { Text(source.identifier, style = MaterialTheme.typography.bodySmall) },
                leadingContent = { Icon(Icons.Default.Verified, null, tint = MaterialTheme.colorScheme.primary) },
                trailingContent = {
                    Surface(shape = RoundedCornerShape(4.dp), color = MaterialTheme.colorScheme.primaryContainer) {
                        Text("Official", modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                    }
                }
            )
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
        }
        // User sources — only shown when ALLOW_USER_SOURCES is enabled
        if (BuildConfig.ALLOW_USER_SOURCES) {
            userSources.forEach { source ->
                ListItem(
                    headlineContent = { Text(source.displayName, fontWeight = FontWeight.Medium) },
                    supportingContent = { Text(source.identifier, style = MaterialTheme.typography.bodySmall) },
                    leadingContent = { Icon(Icons.Default.Person, null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
                    trailingContent = {
                        IconButton(onClick = { onRemoveSource(source.identifier) }, modifier = Modifier.size(36.dp)) {
                            Icon(Icons.Default.Delete, "Remove", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                        }
                    }
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
            }
            // Add source button — only shown when ALLOW_USER_SOURCES=true in local.properties
            SettingsClickRow(
                icon = Icons.Default.Add,
                title = "Add My Source",
                subtitle = "Add a public channel, private channel, or group",
                onClick = { showAddDialog = true }
            )
        }
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false; newIdentifier = ""; newDisplayName = "" },
            icon = { Icon(Icons.Default.Add, null) },
            title = { Text("Add Source") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = newIdentifier,
                        onValueChange = { newIdentifier = it },
                        label = { Text("Channel / Group") },
                        placeholder = { Text("@channel, -1001234567890, or t.me/+link") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )
                    OutlinedTextField(
                        value = newDisplayName,
                        onValueChange = { newDisplayName = it },
                        label = { Text("Display Name") },
                        placeholder = { Text("My Channel") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newIdentifier.isNotBlank() && newDisplayName.isNotBlank()) {
                            onAddSource(newIdentifier, newDisplayName)
                            showAddDialog = false
                            newIdentifier = ""
                            newDisplayName = ""
                        }
                    },
                    enabled = newIdentifier.isNotBlank() && newDisplayName.isNotBlank()
                ) { Text("Add") }
            },
            dismissButton = { TextButton(onClick = { showAddDialog = false; newIdentifier = ""; newDisplayName = "" }) { Text("Cancel") } }
        )
    }
}

@Composable
private fun AccountSection(
    user: com.trixsearch.hasikit.telegram.domain.model.TelegramUser?
) {
    SettingsGroup("Account", Icons.Default.AccountCircle) {
        if (user != null) {
            ListItem(
                headlineContent = { Text(user.displayName, fontWeight = FontWeight.SemiBold) },
                supportingContent = {
                    Column {
                        if (user.phoneNumber.isNotBlank()) Text(user.phoneNumber, style = MaterialTheme.typography.bodySmall)
                        user.username?.let { Text("@$it", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary) }
                    }
                },
                leadingContent = {
                    // Profile photo: load from Telegram if available, fallback to initials avatar
                    if (!user.profilePhotoUrl.isNullOrBlank()) {
                        coil.compose.AsyncImage(
                            model = if (user.profilePhotoUrl.startsWith("/")) "file://${user.profilePhotoUrl}" else user.profilePhotoUrl,
                            contentDescription = null,
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape),
                            contentScale = androidx.compose.ui.layout.ContentScale.Crop
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                user.firstName.take(1).uppercase(),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            )
        } else {
            ListItem(
                headlineContent = { Text("Not signed in", fontWeight = FontWeight.Medium) },
                leadingContent = { Icon(Icons.Default.AccountCircle, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(44.dp)) }
            )
        }
    }
}

// Sign Out section — placed at bottom of settings list so users don’t tap it accidentally
@Composable
private fun SignOutSection(onLogout: () -> Unit) {
    SettingsGroup("Sign Out", Icons.AutoMirrored.Filled.ExitToApp) {
        SettingsClickRow(
            icon = Icons.AutoMirrored.Filled.ExitToApp,
            title = "Sign Out",
            subtitle = "Sign out of your Telegram account",
            onClick = onLogout,
            tintColor = MaterialTheme.colorScheme.error
        )
    }
}

@Composable
private fun LanguageSection(onClick: () -> Unit) {
    SettingsGroup("Language", Icons.Default.Language) {
        SettingsClickRow(
            icon = Icons.Default.Translate,
            title = "App Language",
            subtitle = "Change display language",
            onClick = onClick
        )
    }
}

@Composable
private fun RequestSection(onClick: () -> Unit) {
    SettingsGroup("Request Content", Icons.Default.MovieFilter) {
        SettingsClickRow(
            icon = Icons.Default.Send,
            title = "Request a Movie / Anime / Series",
            subtitle = "Send a request to @hasikit_m_bot via Telegram",
            onClick = onClick
        )
    }
}

@Composable
private fun AppearanceSection(appTheme: AppTheme, onThemeClick: () -> Unit) {
    SettingsGroup("Appearance", Icons.Default.Palette) {
        SettingsClickRow(
            icon = Icons.Default.DarkMode,
            title = "Theme",
            subtitle = when (appTheme) {
                AppTheme.DARK -> "Dark"
                AppTheme.LIGHT -> "Light"
                AppTheme.SYSTEM -> "System Default"
            },
            onClick = onThemeClick
        )
    }
}

@Composable
private fun PlayerSection(
    autoPlay: Boolean,
    quality: String,
    onAutoPlay: (Boolean) -> Unit,
    onQualityClick: () -> Unit,
    // Added resume after call toggle
    resumeAfterCall: Boolean,
    onResumeAfterCall: (Boolean) -> Unit,
    // Added pause on headphone removal toggle
    pauseOnHeadphoneRemoval: Boolean,
    onPauseOnHeadphoneRemoval: (Boolean) -> Unit,
    // Added background audio (continue when screen locked) toggle
    backgroundAudio: Boolean,
    onBackgroundAudio: (Boolean) -> Unit
) {
    SettingsGroup("Player", Icons.Default.PlayCircle) {
        SettingsToggleRow(Icons.Default.PlayArrow, "Auto-play", "Automatically play next video", autoPlay, onAutoPlay)
        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
        // Reserved for future adaptive quality support — Telegram currently provides one source, quality UI hidden
        // SettingsClickRow(Icons.Default.HighQuality, "Streaming Quality", quality, onQualityClick)
        // HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
        // Added resume playback after phone calls setting
        SettingsToggleRow(Icons.Default.Phone, "Resume Playback After Calls", "Auto-resume when a phone call ends", resumeAfterCall, onResumeAfterCall)
        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
        // Added pause on headphone removal setting
        SettingsToggleRow(Icons.Default.HeadsetOff, "Pause On Headphone Removal", "Pause when headphones are disconnected", pauseOnHeadphoneRemoval, onPauseOnHeadphoneRemoval)
        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
        // Added background audio (continue when screen locked) setting
        SettingsToggleRow(Icons.Default.ScreenLockPortrait, "Continue Audio When Screen Locked", "Keep playing audio when screen turns off", backgroundAudio, onBackgroundAudio)
    }
}

// Added Advanced Settings entry point — dangerous options moved here from main settings
@Composable
private fun AdvancedSection(onClick: () -> Unit) {
    SettingsGroup("Advanced Settings", Icons.Default.Tune) {
        SettingsClickRow(
            icon = Icons.Default.Tune,
            title = "Advanced Settings",
            subtitle = "Cache, Telegram reset, custom aspect ratios",
            onClick = onClick
        )
    }
}

@Composable
private fun DownloadsSection(
    wifiOnly: Boolean,
    onWifiOnly: (Boolean) -> Unit,
    galleryVisible: Boolean,
    onGalleryVisible: (Boolean) -> Unit,
    downloadPath: String,
    onPickFolder: (String) -> Unit
) {
    val folderPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree()
    ) { uri ->
        uri?.let { onPickFolder(it.toString()) }
    }
    val displayPath = when {
        downloadPath.isBlank() -> "App-specific storage (default)"
        else -> try { Uri.parse(downloadPath).lastPathSegment ?: downloadPath } catch (e: Exception) { downloadPath }
    }
    SettingsGroup("Downloads", Icons.Default.Download) {
        SettingsToggleRow(Icons.Default.Wifi, "Wi-Fi Only", "Only download on Wi-Fi", wifiOnly, onWifiOnly)
        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
        SettingsClickRow(
            icon = Icons.Default.FolderOpen,
            title = "Download Location",
            subtitle = displayPath,
            onClick = { folderPickerLauncher.launch(null) }
        )
        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
        SettingsToggleRow(
            icon = Icons.Default.PhotoLibrary,
            title = "Show in Gallery",
            subtitle = "Make downloaded videos visible in Gallery apps",
            checked = galleryVisible,
            onCheckedChange = onGalleryVisible
        )
    }
}

// Storage section shows stats only — dangerous clear actions moved to AdvancedSettings
@Composable
private fun StorageSection(
    downloadCount: Int,
    storageUsed: Long,
    cacheSize: Long,
    onClearCache: () -> Unit,
    onClearAll: () -> Unit
) {
    SettingsGroup("Storage", Icons.Default.Storage) {
        SettingsInfoRow(Icons.Default.VideoFile, "Downloaded Videos", "$downloadCount file${if (downloadCount != 1) "s" else ""}")
        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
        SettingsInfoRow(Icons.Default.Download, "Downloads Used", formatBytes(storageUsed))
        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
        SettingsInfoRow(Icons.Default.Cached, "Cache Size", formatBytes(cacheSize))
    }
}

@Composable
private fun AboutSection() {
    val context = LocalContext.current
    SettingsGroup("About", Icons.Default.Info) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {}
                Icon(Icons.Default.PlayCircle, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(32.dp))
            }
            Spacer(Modifier.width(14.dp))
            Column {
                Text("Hasikit", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold)
                Text("Version 1.0.0", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(
                    "by @trixsearch",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable {
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/trixsearch")))
                    }
                )
            }
        }
        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
        SettingsLinkRow(
            icon = Icons.Default.Code,
            title = "GitHub",
            subtitle = "github.com/trixsearch/hasikit",
            url = "https://github.com/trixsearch/hasikit"
        )
        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
        SettingsLinkRow(
            icon = Icons.Default.Language,
            title = "Website",
            subtitle = "trixsearch.github.io/",
            url = "https://trixsearch.github.io/"
        )
        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
        SettingsLinkRow(
            icon = Icons.Default.Send,
            title = "Telegram",
            subtitle = "@hasikit_m_bot",
            url = "https://t.me/hasikit_m_bot"
        )
    }
}

// ─── Reusable row components ──────────────────────────────────────────────────

@Composable
// Changed from private to internal so AdvancedSettingsScreen in the same module can access it
internal fun SettingsGroup(title: String, icon: ImageVector, content: @Composable ColumnScope.() -> Unit) {
    Column {
        Row(modifier = Modifier.padding(bottom = 6.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(6.dp))
            Text(title, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
        }
        Card(shape = RoundedCornerShape(14.dp), elevation = CardDefaults.cardElevation(1.dp), modifier = Modifier.fillMaxWidth()) {
            Column(content = content)
        }
    }
}

@Composable
private fun SettingsToggleRow(icon: ImageVector, title: String, subtitle: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    ListItem(
        headlineContent = { Text(title, fontWeight = FontWeight.Medium) },
        supportingContent = { Text(subtitle, style = MaterialTheme.typography.bodySmall) },
        leadingContent = { Icon(icon, null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
        trailingContent = { Switch(checked = checked, onCheckedChange = onCheckedChange) }
    )
}

@Composable
private fun SettingsClickRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    tintColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    ListItem(
        headlineContent = { Text(title, fontWeight = FontWeight.Medium) },
        supportingContent = { Text(subtitle, style = MaterialTheme.typography.bodySmall) },
        leadingContent = { Icon(icon, null, tint = tintColor) },
        trailingContent = { Icon(Icons.Default.ChevronRight, null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
        modifier = Modifier.clickable(onClick = onClick)
    )
}

@Composable
private fun SettingsInfoRow(icon: ImageVector, title: String, subtitle: String) {
    ListItem(
        headlineContent = { Text(title, fontWeight = FontWeight.Medium) },
        supportingContent = { Text(subtitle, style = MaterialTheme.typography.bodySmall) },
        leadingContent = { Icon(icon, null, tint = MaterialTheme.colorScheme.onSurfaceVariant) }
    )
}

@Composable
private fun SettingsLinkRow(icon: ImageVector, title: String, subtitle: String, url: String) {
    val context = LocalContext.current
    ListItem(
        headlineContent = { Text(title, fontWeight = FontWeight.Medium) },
        supportingContent = { Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary) },
        leadingContent = { Icon(icon, null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
        trailingContent = { Icon(Icons.Default.OpenInNew, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp)) },
        modifier = Modifier.clickable {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        }
    )
}

private fun formatBytes(bytes: Long): String = when {
    bytes >= 1_073_741_824L -> "%.1f GB".format(bytes / 1_073_741_824.0)
    bytes >= 1_048_576L -> "%.1f MB".format(bytes / 1_048_576.0)
    bytes >= 1024L -> "%.1f KB".format(bytes / 1024.0)
    else -> "$bytes B"
}
