package com.trixsearch.hasikit.ui.screens.settings

import android.content.Context
import android.content.SharedPreferences
import android.os.Environment
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

private const val TAG = "SettingsScreen"
private const val PREFS_NAME = "hasikit_settings"
private const val KEY_WIFI_ONLY = "wifi_only_downloads"
private const val KEY_AUTO_PLAY = "auto_play"
private const val KEY_DARK_THEME = "dark_theme"
private const val KEY_STREAMING_QUALITY = "streaming_quality"

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _wifiOnlyDownloads = MutableStateFlow(prefs.getBoolean(KEY_WIFI_ONLY, false))
    val wifiOnlyDownloads: StateFlow<Boolean> = _wifiOnlyDownloads

    private val _autoPlay = MutableStateFlow(prefs.getBoolean(KEY_AUTO_PLAY, true))
    val autoPlay: StateFlow<Boolean> = _autoPlay

    private val _darkTheme = MutableStateFlow(prefs.getBoolean(KEY_DARK_THEME, true))
    val darkTheme: StateFlow<Boolean> = _darkTheme

    private val _streamingQuality = MutableStateFlow(
        prefs.getString(KEY_STREAMING_QUALITY, "Auto") ?: "Auto"
    )
    val streamingQuality: StateFlow<String> = _streamingQuality

    private val _cacheSize = MutableStateFlow(calculateCacheSize(context))
    val cacheSize: StateFlow<Long> = _cacheSize

    private val _storageUsed = MutableStateFlow(calculateDownloadStorage(context))
    val storageUsed: StateFlow<Long> = _storageUsed

    fun setWifiOnlyDownloads(value: Boolean) {
        _wifiOnlyDownloads.value = value
        prefs.edit().putBoolean(KEY_WIFI_ONLY, value).apply()
        Log.d(TAG, "wifiOnlyDownloads=$value")
    }

    fun setAutoPlay(value: Boolean) {
        _autoPlay.value = value
        prefs.edit().putBoolean(KEY_AUTO_PLAY, value).apply()
        Log.d(TAG, "autoPlay=$value")
    }

    fun setDarkTheme(value: Boolean) {
        _darkTheme.value = value
        prefs.edit().putBoolean(KEY_DARK_THEME, value).apply()
        Log.d(TAG, "darkTheme=$value")
    }

    fun setStreamingQuality(value: String) {
        _streamingQuality.value = value
        prefs.edit().putString(KEY_STREAMING_QUALITY, value).apply()
        Log.d(TAG, "streamingQuality=$value")
    }

    fun clearCache() {
        try {
            context.cacheDir.deleteRecursively()
            context.externalCacheDir?.deleteRecursively()
            _cacheSize.value = 0L
            Log.d(TAG, "Cache cleared")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to clear cache", e)
        }
    }

    fun refreshStorageStats() {
        _cacheSize.value = calculateCacheSize(context)
        _storageUsed.value = calculateDownloadStorage(context)
    }

    private fun calculateCacheSize(ctx: Context): Long {
        var size = ctx.cacheDir.walkTopDown().filter { it.isFile }.sumOf { it.length() }
        ctx.externalCacheDir?.walkTopDown()?.filter { it.isFile }?.forEach { size += it.length() }
        return size
    }

    private fun calculateDownloadStorage(ctx: Context): Long {
        val dir = ctx.getExternalFilesDir(Environment.DIRECTORY_MOVIES) ?: return 0L
        return dir.walkTopDown().filter { it.isFile }.sumOf { it.length() }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val wifiOnly by viewModel.wifiOnlyDownloads.collectAsState()
    val autoPlay by viewModel.autoPlay.collectAsState()
    val darkTheme by viewModel.darkTheme.collectAsState()
    val streamingQuality by viewModel.streamingQuality.collectAsState()
    val cacheSize by viewModel.cacheSize.collectAsState()
    val storageUsed by viewModel.storageUsed.collectAsState()

    var showQualityDialog by remember { mutableStateOf(false) }
    var showClearCacheDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { viewModel.refreshStorageStats() }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Settings") }) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            item { SettingsSectionHeader("Playback") }
            item {
                SettingsToggleItem(
                    icon = Icons.Default.PlayArrow,
                    title = "Auto-play",
                    subtitle = "Automatically play next video",
                    checked = autoPlay,
                    onCheckedChange = viewModel::setAutoPlay
                )
            }
            item {
                SettingsClickItem(
                    icon = Icons.Default.HighQuality,
                    title = "Streaming Quality",
                    subtitle = streamingQuality,
                    onClick = { showQualityDialog = true }
                )
            }

            item { SettingsSectionHeader("Downloads") }
            item {
                SettingsToggleItem(
                    icon = Icons.Default.Wifi,
                    title = "Wi-Fi Only Downloads",
                    subtitle = "Only download when connected to Wi-Fi",
                    checked = wifiOnly,
                    onCheckedChange = viewModel::setWifiOnlyDownloads
                )
            }
            item {
                SettingsInfoItem(
                    icon = Icons.Default.FolderOpen,
                    title = "Download Location",
                    subtitle = "Movies folder (app-specific storage)"
                )
            }

            item { SettingsSectionHeader("Storage") }
            item {
                SettingsInfoItem(
                    icon = Icons.Default.Storage,
                    title = "Downloads Used",
                    subtitle = formatBytes(storageUsed)
                )
            }
            item {
                SettingsInfoItem(
                    icon = Icons.Default.Cached,
                    title = "Cache Size",
                    subtitle = formatBytes(cacheSize)
                )
            }
            item {
                SettingsClickItem(
                    icon = Icons.Default.DeleteSweep,
                    title = "Clear Cache",
                    subtitle = "Free up ${formatBytes(cacheSize)} of cache",
                    onClick = { showClearCacheDialog = true }
                )
            }

            item { SettingsSectionHeader("Appearance") }
            item {
                SettingsToggleItem(
                    icon = Icons.Default.DarkMode,
                    title = "Dark Theme",
                    subtitle = "Use dark color scheme",
                    checked = darkTheme,
                    onCheckedChange = viewModel::setDarkTheme
                )
            }

            item { SettingsSectionHeader("About") }
            item {
                SettingsInfoItem(
                    icon = Icons.Default.Info,
                    title = "Version",
                    subtitle = "1.0.0"
                )
            }
            item {
                SettingsInfoItem(
                    icon = Icons.Default.Person,
                    title = "Developer",
                    subtitle = "@trixsearch"
                )
            }
        }
    }

    if (showQualityDialog) {
        val qualities = listOf("Auto", "1080p", "720p", "480p", "360p")
        AlertDialog(
            onDismissRequest = { showQualityDialog = false },
            title = { Text("Streaming Quality") },
            text = {
                Column {
                    qualities.forEach { quality ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = streamingQuality == quality,
                                onClick = {
                                    viewModel.setStreamingQuality(quality)
                                    showQualityDialog = false
                                }
                            )
                            Text(quality, modifier = Modifier.padding(start = 8.dp))
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showQualityDialog = false }) { Text("Cancel") }
            }
        )
    }

    if (showClearCacheDialog) {
        AlertDialog(
            onDismissRequest = { showClearCacheDialog = false },
            title = { Text("Clear Cache") },
            text = {
                Text("This will delete ${formatBytes(cacheSize)} of cached data. Downloaded videos will not be affected.")
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.clearCache()
                    showClearCacheDialog = false
                }) { Text("Clear") }
            },
            dismissButton = {
                TextButton(onClick = { showClearCacheDialog = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(top = 16.dp, bottom = 4.dp)
    )
    HorizontalDivider()
}

@Composable
private fun SettingsToggleItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    ListItem(
        headlineContent = { Text(title) },
        supportingContent = { Text(subtitle, style = MaterialTheme.typography.bodySmall) },
        leadingContent = { Icon(icon, contentDescription = null) },
        trailingContent = { Switch(checked = checked, onCheckedChange = onCheckedChange) }
    )
}

@Composable
private fun SettingsClickItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    ListItem(
        headlineContent = { Text(title) },
        supportingContent = { Text(subtitle, style = MaterialTheme.typography.bodySmall) },
        leadingContent = { Icon(icon, contentDescription = null) },
        trailingContent = { Icon(Icons.Default.ChevronRight, contentDescription = null) },
        modifier = Modifier.clickable(onClick = onClick)
    )
}

@Composable
private fun SettingsInfoItem(
    icon: ImageVector,
    title: String,
    subtitle: String
) {
    ListItem(
        headlineContent = { Text(title) },
        supportingContent = { Text(subtitle, style = MaterialTheme.typography.bodySmall) },
        leadingContent = { Icon(icon, contentDescription = null) }
    )
}

private fun formatBytes(bytes: Long): String = when {
    bytes >= 1_073_741_824L -> "%.1f GB".format(bytes / 1_073_741_824.0)
    bytes >= 1_048_576L -> "%.1f MB".format(bytes / 1_048_576.0)
    bytes >= 1024L -> "%.1f KB".format(bytes / 1024.0)
    else -> "$bytes B"
}
