package com.trixsearch.hasikit.ui.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdvancedSettingsScreen(
    navController: NavController,
    // Reuse SettingsViewModel since it already owns all cache/session/aspect ratio logic
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val cacheSize by viewModel.cacheSize.collectAsState()
    val customAspectRatios by viewModel.customAspectRatios.collectAsState()

    // FIX #7 — Separate dialogs for each clear action
    var showClearCacheDialog by remember { mutableStateOf(false) }
    var showClearAllDialog by remember { mutableStateOf(false) }
    var showClearThumbnailDialog by remember { mutableStateOf(false) }
    var showClearPlayerDialog by remember { mutableStateOf(false) }
    var showForceDeleteDialog by remember { mutableStateOf(false) }
    // Added custom aspect ratio dialog state
    var showAddRatioDialog by remember { mutableStateOf(false) }
    var newRatioInput by remember { mutableStateOf("") }

    LaunchedEffect(Unit) { viewModel.refreshStorageStats() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Advanced Settings", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Cache management group
            item {
                SettingsGroup("Cache", Icons.Default.Storage) {
                    // Clear app cache — button enabled whenever cache exists; size shown in subtitle
                    ListItem(
                        headlineContent = { Text("Clear Cache", fontWeight = FontWeight.Medium) },
                        supportingContent = {
                            Text(
                                // FIX: always show calculated size; "No cache" only when truly 0 after refresh
                                if (cacheSize > 0L) "Free up ${formatAdvBytes(cacheSize)}" else "No cache to clear",
                                style = MaterialTheme.typography.bodySmall
                            )
                        },
                        leadingContent = {
                            Icon(
                                Icons.Default.DeleteSweep,
                                null,
                                tint = if (cacheSize > 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        trailingContent = { Icon(Icons.Default.ChevronRight, null) },
                        // FIX: always clickable so user can trigger a fresh size recalculation
                        modifier = Modifier.clickable { showClearCacheDialog = true }
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                    // FIX #7 — Clear thumbnail cache only
                    ListItem(
                        headlineContent = { Text("Clear Thumbnail Cache", fontWeight = FontWeight.Medium) },
                        supportingContent = { Text("Remove cached video thumbnails", style = MaterialTheme.typography.bodySmall) },
                        leadingContent = { Icon(Icons.Default.HideImage, null, tint = MaterialTheme.colorScheme.error) },
                        trailingContent = { Icon(Icons.Default.ChevronRight, null) },
                        modifier = Modifier.clickable { showClearThumbnailDialog = true }
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                    // FIX #7 — Clear ExoPlayer buffer cache only
                    ListItem(
                        headlineContent = { Text("Clear Player Cache", fontWeight = FontWeight.Medium) },
                        supportingContent = { Text("Remove ExoPlayer buffer cache", style = MaterialTheme.typography.bodySmall) },
                        leadingContent = { Icon(Icons.Default.VideocamOff, null, tint = MaterialTheme.colorScheme.error) },
                        trailingContent = { Icon(Icons.Default.ChevronRight, null) },
                        modifier = Modifier.clickable { showClearPlayerDialog = true }
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                    // Clear all storage including downloads
                    ListItem(
                        headlineContent = { Text("Clear All Storage", fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.error) },
                        supportingContent = { Text("Delete all downloads, cache and metadata", style = MaterialTheme.typography.bodySmall) },
                        leadingContent = { Icon(Icons.Default.DeleteForever, null, tint = MaterialTheme.colorScheme.error) },
                        trailingContent = { Icon(Icons.Default.ChevronRight, null) },
                        modifier = Modifier.clickable { showClearAllDialog = true }
                    )
                }
            }

            // Telegram reset group
            item {
                SettingsGroup("Telegram", Icons.Default.Send) {
                    // Force Telegram reset — clears TDLib database and session
                    ListItem(
                        headlineContent = { Text("Force Telegram Reset", fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.error) },
                        supportingContent = { Text("Delete TDLib database and restart auth flow", style = MaterialTheme.typography.bodySmall) },
                        leadingContent = { Icon(Icons.Default.RestartAlt, null, tint = MaterialTheme.colorScheme.error) },
                        trailingContent = { Icon(Icons.Default.ChevronRight, null) },
                        modifier = Modifier.clickable { showForceDeleteDialog = true }
                    )
                }
            }

            // Added custom aspect ratios group — user-defined ratios included in player cycling
            item {
                SettingsGroup("Custom Aspect Ratios", Icons.Default.AspectRatio) {
                    if (customAspectRatios.isEmpty()) {
                        ListItem(
                            headlineContent = { Text("No custom ratios added", style = MaterialTheme.typography.bodyMedium) },
                            supportingContent = { Text("Add ratios like 16:10, 18:9, 2.20:1", style = MaterialTheme.typography.bodySmall) },
                            leadingContent = { Icon(Icons.Default.Info, null, tint = MaterialTheme.colorScheme.onSurfaceVariant) }
                        )
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                    } else {
                        customAspectRatios.forEach { ratio ->
                            ListItem(
                                headlineContent = { Text(ratio, fontWeight = FontWeight.Medium) },
                                leadingContent = { Icon(Icons.Default.AspectRatio, null, tint = MaterialTheme.colorScheme.primary) },
                                trailingContent = {
                                    IconButton(onClick = { viewModel.removeCustomAspectRatio(ratio) }, modifier = Modifier.size(36.dp)) {
                                        Icon(Icons.Default.Delete, "Remove", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                                    }
                                }
                            )
                            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                        }
                    }
                    // Add custom ratio button
                    ListItem(
                        headlineContent = { Text("Add Custom Ratio", fontWeight = FontWeight.Medium) },
                        supportingContent = { Text("e.g. 16:10, 18:9, 2.20:1", style = MaterialTheme.typography.bodySmall) },
                        leadingContent = { Icon(Icons.Default.Add, null, tint = MaterialTheme.colorScheme.primary) },
                        trailingContent = { Icon(Icons.Default.ChevronRight, null) },
                        modifier = Modifier.clickable { showAddRatioDialog = true }
                    )
                }
            }

            item { Spacer(Modifier.height(8.dp)) }
        }
    }

    // FIX #7 — Clear thumbnail cache confirmation dialog
    if (showClearThumbnailDialog) {
        AlertDialog(
            onDismissRequest = { showClearThumbnailDialog = false },
            icon = { Icon(Icons.Default.HideImage, null, tint = MaterialTheme.colorScheme.error) },
            title = { Text("Clear Thumbnail Cache") },
            text = { Text("Delete all cached video thumbnails? They will be re-downloaded when needed.") },
            confirmButton = {
                Button(
                    onClick = { viewModel.clearThumbnailCache(); showClearThumbnailDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text("Clear") }
            },
            dismissButton = { TextButton(onClick = { showClearThumbnailDialog = false }) { Text("Cancel") } }
        )
    }

    // FIX #7 — Clear player cache confirmation dialog
    if (showClearPlayerDialog) {
        AlertDialog(
            onDismissRequest = { showClearPlayerDialog = false },
            icon = { Icon(Icons.Default.VideocamOff, null, tint = MaterialTheme.colorScheme.error) },
            title = { Text("Clear Player Cache") },
            text = { Text("Delete ExoPlayer buffer cache? This frees space used by video buffering.") },
            confirmButton = {
                Button(
                    onClick = { viewModel.clearPlayerCache(); showClearPlayerDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text("Clear") }
            },
            dismissButton = { TextButton(onClick = { showClearPlayerDialog = false }) { Text("Cancel") } }
        )
    }

    // Clear cache confirmation dialog — always shown when user taps; size displayed from refreshed value
    if (showClearCacheDialog) {
        AlertDialog(
            onDismissRequest = { showClearCacheDialog = false },
            icon = { Icon(Icons.Default.DeleteSweep, null, tint = MaterialTheme.colorScheme.error) },
            title = { Text("Clear Cache") },
            text = {
                Text(
                    if (cacheSize > 0L)
                        "Delete ${formatAdvBytes(cacheSize)} of cached data? Downloaded videos are not affected."
                    else
                        "Cache appears empty. Clear anyway to remove any residual temp files?"
                )
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.clearCache(); showClearCacheDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text("Clear") }
            },
            dismissButton = { TextButton(onClick = { showClearCacheDialog = false }) { Text("Cancel") } }
        )
    }

    // Clear all storage confirmation dialog — explicitly states session is preserved
    if (showClearAllDialog) {
        AlertDialog(
            onDismissRequest = { showClearAllDialog = false },
            icon = { Icon(Icons.Default.Warning, null, tint = MaterialTheme.colorScheme.error) },
            title = { Text("WARNING", color = MaterialTheme.colorScheme.error) },
            text = { Text("This will permanently delete all downloaded videos, cached media, and download metadata.\n\nYour Telegram login and settings will NOT be affected.\n\nThis action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = { viewModel.clearAllStorage(); showClearAllDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text("Delete Everything") }
            },
            dismissButton = { TextButton(onClick = { showClearAllDialog = false }) { Text("Cancel") } }
        )
    }

    // Force Telegram reset confirmation dialog
    if (showForceDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showForceDeleteDialog = false },
            icon = { Icon(Icons.Default.DeleteForever, null, tint = MaterialTheme.colorScheme.error) },
            title = { Text("Force Clear Session", color = MaterialTheme.colorScheme.error) },
            text = { Text("This will delete the TDLib database, all cached Telegram files, and your saved session.\n\nUse this if you are stuck in a login loop.\n\nYou will need to log in again.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.forceDeleteSession()
                        showForceDeleteDialog = false
                        navController.navigate(com.trixsearch.hasikit.ui.navigation.Screen.Auth.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text("Force Clear") }
            },
            dismissButton = { TextButton(onClick = { showForceDeleteDialog = false }) { Text("Cancel") } }
        )
    }

    // Added custom aspect ratio input dialog
    if (showAddRatioDialog) {
        AlertDialog(
            onDismissRequest = { showAddRatioDialog = false; newRatioInput = "" },
            icon = { Icon(Icons.Default.AspectRatio, null) },
            title = { Text("Add Custom Aspect Ratio") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Enter a ratio in W:H format.", style = MaterialTheme.typography.bodySmall)
                    OutlinedTextField(
                        value = newRatioInput,
                        onValueChange = { newRatioInput = it },
                        label = { Text("Ratio") },
                        placeholder = { Text("e.g. 16:10") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )
                    // Show preset suggestions
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("16:10", "18:9", "2.20:1", "21:9").forEach { preset ->
                            SuggestionChip(onClick = { newRatioInput = preset }, label = { Text(preset, style = MaterialTheme.typography.labelSmall) })
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newRatioInput.isNotBlank()) {
                            viewModel.addCustomAspectRatio(newRatioInput)
                            showAddRatioDialog = false
                            newRatioInput = ""
                        }
                    },
                    enabled = newRatioInput.isNotBlank()
                ) { Text("Add") }
            },
            dismissButton = { TextButton(onClick = { showAddRatioDialog = false; newRatioInput = "" }) { Text("Cancel") } }
        )
    }
}

private fun formatAdvBytes(bytes: Long): String = when {
    bytes >= 1_073_741_824L -> "%.1f GB".format(bytes / 1_073_741_824.0)
    bytes >= 1_048_576L -> "%.1f MB".format(bytes / 1_048_576.0)
    bytes >= 1024L -> "%.1f KB".format(bytes / 1024.0)
    else -> "$bytes B"
}
