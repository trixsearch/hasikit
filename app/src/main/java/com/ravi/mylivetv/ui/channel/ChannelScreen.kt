package com.ravi.mylivetv.ui.channel

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.ravi.mylivetv.MyApplication
import com.ravi.mylivetv.domain.model.Channel
import com.ravi.mylivetv.ui.composable.ChannelGrid
import com.ravi.mylivetv.utils.CategoryMapper
import com.ravi.mylivetv.utils.ChannelListHolder
import com.ravi.mylivetv.utils.Constants
import com.ravi.mylivetv.utils.Resource
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChannelScreen(
    navController: NavController,
    category: String,
    viewModel: ChannelViewModel = hiltViewModel()
) {
    var searchQuery by rememberSaveable { mutableStateOf("") }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Get configuration for responsive layout
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val screenWidthDp = configuration.screenWidthDp

    // Keyboard controller and focus manager for Android TV
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val searchFocusRequester = remember { FocusRequester() }
    var isSearchFocused by remember { mutableStateOf(false) }

    // Calculate grid columns based on screen size and orientation
    val gridColumns = when {
        screenWidthDp >= 1200 -> 6  // Extra large screens (TV/Desktop)
        screenWidthDp >= 840 -> 5   // Large tablets landscape
        screenWidthDp >= 600 && isLandscape -> 4  // Tablets landscape
        screenWidthDp >= 600 -> 3   // Tablets portrait
        isLandscape -> 3            // Phone landscape
        else -> 2                   // Phone portrait
    }

    // Retain scroll position
    val gridState = rememberLazyGridState()

    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    var showDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val application = context.applicationContext as MyApplication

    // Load channels only once when screen is first composed or category changes
    LaunchedEffect(category) {
        if (category == "Recently Watched") {
            // For Recently Watched, we don't need a URL
            viewModel.loadChannels("", forceRefresh = false, category = category)
        } else {
            val url = when {
                CategoryMapper.isCategory(category) -> CategoryMapper.getCategoryUrl(category)
                CategoryMapper.isLanguage(category) -> CategoryMapper.getLanguageUrl(category)
                CategoryMapper.isCountry(category) -> CategoryMapper.getCountryUrl(category)
                else -> CategoryMapper.getCategoryUrl(category)
            }
            // Will use cached data if available, won't make API call
            viewModel.loadChannels(url, forceRefresh = false, category = category)
        }
    }

    // Determine category URL for recently watched (skip if it's "Recently Watched" itself)
    val categoryUrl = remember(category) {
        if (category == "Recently Watched") {
            ""
        } else {
            when {
                CategoryMapper.isCategory(category) -> CategoryMapper.getCategoryUrl(category)
                CategoryMapper.isLanguage(category) -> CategoryMapper.getLanguageUrl(category)
                CategoryMapper.isCountry(category) -> CategoryMapper.getCountryUrl(category)
                else -> CategoryMapper.getCategoryUrl(category)
            }
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(Modifier.height(12.dp))
//                Constants.TABS.forEachIndexed { index, title ->
//                    val icon = when (index) {
//                        0 -> Icons.Default.Category
//                        1 -> Icons.Default.Language
//                        2 -> Icons.Default.Public
//                        else -> Icons.Default.Category
//                    }
//                    NavigationDrawerItem(
//                        icon = { Icon(icon, contentDescription = null) },
//                        label = { Text(title) },
//                        selected = false,
//                        onClick = {
//                            scope.launch { drawerState.close() }
//                        },
//                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
//                    )
//                }
//                Adding Toggle button in Hamburger menu
                NavigationDrawerItem(
                    icon = {
                        Icon(
                            imageVector = if (application.isDarkTheme.value) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = null
                        )
                    },
                    label = { Text(if (application.isDarkTheme.value) "Switch to Light Mode" else "Switch to Dark Mode") },
                    selected = false,
                    onClick = {
                        application.toggleTheme()
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                Spacer(Modifier.weight(1f))
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Code, contentDescription = "Developer info") },
                    label = { Text("Developer info") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        showDialog = true
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
            }
        }
    )
    {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = category) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
                    ),
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            scope.launch {
                                drawerState.apply {
                                    if (isClosed) open() else close()
                                }
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Menu"
                            )
                        }
                    }
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                // Search bar with icon
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(5.dp))
                            .focusRequester(searchFocusRequester)
                            .onFocusChanged { focusState ->
                                isSearchFocused = focusState.isFocused
                                if (!focusState.isFocused) {
                                    keyboardController?.hide()
                                }
                            },
                        placeholder = { Text("Search your channel") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                        ),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Search
                        ),
                        keyboardActions = KeyboardActions(
                            onSearch = {
                                keyboardController?.hide()
                                focusManager.clearFocus()
                            }
                        ),
                        // Disable auto-show keyboard on Android TV
                        readOnly = false
                    )
                }

                // Channels Grid
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(8.dp)
                ) {
                    when (uiState) {
                        is Resource.Loading -> {
                            CircularProgressIndicator(
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }

                        is Resource.Success -> {
                            val channels = (uiState as Resource.Success<List<Channel>>).data
                            val filteredChannels = channels.filter {
                                it.name.contains(searchQuery, ignoreCase = true)
                            }

                            if (filteredChannels.isEmpty()) {
                                Text(
                                    text = if (searchQuery.isEmpty()) "No channels available" else "No channels found for \"$searchQuery\"",
                                    modifier = Modifier
                                        .align(Alignment.Center)
                                        .padding(bottom = 100.dp),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            } else {
                                // Store channel list for navigation
                                LaunchedEffect(filteredChannels) {
                                    ChannelListHolder.setChannels(filteredChannels, category)
                                }

                                ChannelGrid(
                                    channels = filteredChannels,
                                    gridState = gridState,
                                    columns = gridColumns,
                                    onChannelClick = { channel ->
                                        // Find channel index in filtered list
                                        val channelIndex =
                                            filteredChannels.indexOfFirst { it.streamUrl == channel.streamUrl }

                                        // Navigate to player screen with stream URL, channel name, logo, category, categoryUrl, and channel index
                                        navController.navigate(
                                            com.ravi.mylivetv.navigation.ScreenRoutes.PlayerScreen.createRoute(
                                                streamUrl = channel.streamUrl,
                                                channelName = channel.name,
                                                logoUrl = channel.logo,
                                                category = category,
                                                categoryUrl = categoryUrl,
                                                channelIndex = channelIndex
                                            )
                                        )
                                    }
                                )
                            }
                        }

                        is Resource.Error -> {
                            Column(
                                modifier = Modifier.align(Alignment.Center),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Failed to load channels",
                                    fontSize = 16.sp,
                                    color = Color.Red,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = (uiState as Resource.Error).message
                                        ?: "Unknown error",
                                    fontSize = 14.sp,
                                    color = Color.Gray
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(onClick = {
                                    if (category == "Recently Watched") {
                                        viewModel.loadChannels(
                                            "",
                                            forceRefresh = true,
                                            category = category
                                        )
                                    } else {
                                        val url = when {
                                            CategoryMapper.isCategory(category) -> CategoryMapper.getCategoryUrl(
                                                category
                                            )

                                            CategoryMapper.isLanguage(category) -> CategoryMapper.getLanguageUrl(
                                                category
                                            )

                                            CategoryMapper.isCountry(category) -> CategoryMapper.getCountryUrl(
                                                category
                                            )

                                            else -> CategoryMapper.getCategoryUrl(category)
                                        }
                                        // Force refresh on retry
                                        viewModel.loadChannels(
                                            url,
                                            forceRefresh = true,
                                            category = category
                                        )
                                    }
                                }) {
                                    Text("Retry")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewChannelScreen() {
    val navController = rememberNavController()
    ChannelScreen(navController, category = "Animation")
}
