package com.trixsearch.hasikit.ui.screens.settings

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

private const val TAG = "StorageManagement"

// Storage Management Screen — Settings → Advanced Settings → Storage Management
// Provides granular cache clearing with an Apply button and hides Force Telegram Reset
// behind a More Options section to prevent accidental taps.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StorageManagementScreen(
    navController: NavController,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val cacheSize by viewModel.cacheSize.collectAsState()
    val storageUsed by viewModel.storageUsed.collectAsState()
    val downloadCount by viewModel.downloadCount.collectAsState()

    // Pending action flags — user selects what to clear, then taps Apply
    var clearCache by remember { mutableStateOf(false) }
    var clearDownloads by remember { mutableStateOf(false) }
    var clearThumbnails by remember { mutableStateOf(false) }
    var clearPlayerCache by remember { mutableStateOf(false) }

    // More Options section visibility — hides Force Telegram Reset by default
    var showMoreOptions by remember { mutableStateOf(false) }

    // Confirmation dialogs
    var showApplyDialog by remember { mutableStateOf(false) }
    var showForceResetDialog by remember { mutableStateOf(false) }

    // Applied feedback
    var showAppliedSnackbar by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) { viewModel.refreshStorageStats() }

    // Show snackbar after apply
    LaunchedEffect(showAppliedSnackbar) {
        if (showAppliedSnackbar) {
            snackbarHostState.showSnackbar("Storage cleared successfully")
            showAppliedSnackbar = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Storage Management", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Storage stats overview
            item {
                SettingsGroup("Storage Overview", Icons.Default.Storage) {
                    ListItem(
                        headlineContent = { Text("Downloaded Videos", fontWeight = FontWeight.Medium) },
                        supportingContent = { Text("$downloadCount file${if (downloadCount != 1) "s" else ""}") },
                        leadingContent = { Icon(Icons.Default.VideoFile, null) }
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                    ListItem(
                        headlineContent = { Text("Downloads Used", fontWeight = FontWeight.Medium) },
                        supportingContent = { Text(formatStorageBytes(storageUsed)) },
                        leadingContent = { Icon(Icons.Default.Download, null) }
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                    ListItem(
                        headlineContent = { Text("Cache Size", fontWeight = FontWeight.Medium) },
                        supportingContent = { Text(formatStorageBytes(cacheSize)) },
                        leadingContent = { Icon(Icons.Default.Cached, null) }
                    )
                }
            }

            // Clear options — checkboxes so user can select multiple before applying
            item {
                SettingsGroup("Clear Options", Icons.Default.DeleteSweep) {
                    // Clear Cache option
                    ListItem(
                        headlineContent = { Text("Clear Cache", fontWeight = FontWeight.Medium) },
                        supportingContent = {
                            Text(
                                if (cacheSize > 0L) "Free up ${formatStorageBytes(cacheSize)}"
                                else "No cache to clear",
                                style = MaterialTheme.typography.bodySmall
                            )
                        },
                        leadingContent = { Icon(Icons.Default.DeleteSweep, null, tint = MaterialTheme.colorScheme.error) },
                        trailingContent = {
                            Checkbox(checked = clearCache, onCheckedChange = { clearCache = it })
                        }
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                    // Clear Thumbnail Cache option
                    ListItem(
                        headlineContent = { Text("Clear Thumbnail Cache", fontWeight = FontWeight.Medium) },
                        supportingContent = { Text("Remove cached video thumbnails", style = MaterialTheme.typography.bodySmall) },
                        leadingContent = { Icon(Icons.Default.HideImage, null, tint = MaterialTheme.colorScheme.error) },
                        trailingContent = {
                            Checkbox(checked = clearThumbnails, onCheckedChange = { clearThumbnails = it })
                        }
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                    // Clear Player Cache option
                    ListItem(
                        headlineContent = { Text("Clear Player Cache", fontWeight = FontWeight.Medium) },
                        supportingContent = { Text("Remove ExoPlayer buffer cache", style = MaterialTheme.typography.bodySmall) },
                        leadingContent = { Icon(Icons.Default.VideocamOff, null, tint = MaterialTheme.colorScheme.error) },
                        trailingContent = {
                            Checkbox(checked = clearPlayerCache, onCheckedChange = { clearPlayerCache = it })
                        }
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                    // Clear Downloads option
                    ListItem(
                        headlineContent = { Text("Clear Downloads", fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.error) },
                        supportingContent = { Text("Delete all downloaded videos and metadata", style = MaterialTheme.typography.bodySmall) },
                        leadingContent = { Icon(Icons.Default.DeleteForever, null, tint = MaterialTheme.colorScheme.error) },
                        trailingContent = {
                            Checkbox(checked = clearDownloads, onCheckedChange = { clearDownloads = it })
                        }
                    )
                }
            }

            // Apply button — only enabled when at least one option is selected
            item {
                Button(
                    onClick = { showApplyDialog = true },
                    enabled = clearCache || clearThumbnails || clearPlayerCache || clearDownloads,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Icon(Icons.Default.DeleteSweep, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Apply", fontWeight = FontWeight.Bold)
                }
            }

            // More Options — Force Telegram Reset hidden here to prevent accidental taps
            item {
                TextButton(
                    onClick = { showMoreOptions = !showMoreOptions },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        if (showMoreOptions) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(if (showMoreOptions) "Hide More Options" else "More Options")
                }
            }

            // Force Telegram Reset — only visible after tapping More Options
            if (showMoreOptions) {
                item {
                    SettingsGroup("Danger Zone", Icons.Default.Warning) {
                        ListItem(
                            headlineContent = {
                                Text("Force Telegram Reset", fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.error)
                            },
                            supportingContent = {
                                Text("Delete TDLib database and restart auth flow", style = MaterialTheme.typography.bodySmall)
                            },
                            leadingContent = {
                                Icon(Icons.Default.RestartAlt, null, tint = MaterialTheme.colorScheme.error)
                            },
                            trailingContent = { Icon(Icons.Default.ChevronRight, null) },
                            modifier = androidx.compose.foundation.clickable(onClick = { showForceResetDialog = true })
                        )
                    }
                }
            }

            item { Spacer(Modifier.height(8.dp)) }
        }
    }

    // Apply confirmation dialog — lists what will be cleared
    if (showApplyDialog) {
        val actions = buildList {
            if (clearCache) add("App cache")
            if (clearThumbnails) add("Thumbnail cache")
            if (clearPlayerCache) add("Player cache")
            if (clearDownloads) add("All downloaded videos")
        }
        AlertDialog(
            onDismissRequest = { showApplyDialog = false },
            icon = { Icon(Icons.Default.DeleteSweep, null, tint = MaterialTheme.colorScheme.error) },
            title = { Text("Confirm Clear") },
            text = { Text("This will permanently delete:\n\n${actions.joinToString("\n") { "• $it" }}") },
            confirmButton = {
                Button(
                    onClick = {
                        Log.d(TAG, "[STORAGE] applying: cache=$clearCache thumbnails=$clearThumbnails player=$clearPlayerCache downloads=$clearDownloads")
                        if (clearCache) viewModel.clearCache()
                        if (clearThumbnails) viewModel.clearThumbnailCache()
                        if (clearPlayerCache) viewModel.clearPlayerCache()
                        if (clearDownloads) viewModel.clearAllStorage()
                        // Reset checkboxes after apply
                        clearCache = false; clearThumbnails = false
                        clearPlayerCache = false; clearDownloads = false
                        showApplyDialog = false
                        showAppliedSnackbar = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text("Clear") }
            },
            dismissButton = { TextButton(onClick = { showApplyDialog = false }) { Text("Cancel") } }
        )
    }

    // Force Telegram Reset confirmation dialog
    if (showForceResetDialog) {
        AlertDialog(
            onDismissRequest = { showForceResetDialog = false },
            icon = { Icon(Icons.Default.DeleteForever, null, tint = MaterialTheme.colorScheme.error) },
            title = { Text("Force Clear Session", color = MaterialTheme.colorScheme.error) },
            text = {
                Text("This will delete the TDLib database, all cached Telegram files, and your saved session.\n\nUse this if you are stuck in a login loop.\n\nYou will need to log in again.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        Log.d(TAG, "[STORAGE] force Telegram reset triggered")
                        viewModel.forceDeleteSession()
                        showForceResetDialog = false
                        navController.navigate(com.trixsearch.hasikit.ui.navigation.Screen.Auth.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text("Force Clear") }
            },
            dismissButton = { TextButton(onClick = { showForceResetDialog = false }) { Text("Cancel") } }
        )
    }
}

private fun formatStorageBytes(bytes: Long): String = when {
    bytes >= 1_073_741_824L -> "%.1f GB".format(bytes / 1_073_741_824.0)
    bytes >= 1_048_576L -> "%.1f MB".format(bytes / 1_048_576.0)
    bytes >= 1024L -> "%.1f KB".format(bytes / 1024.0)
    else -> "$bytes B"
}
