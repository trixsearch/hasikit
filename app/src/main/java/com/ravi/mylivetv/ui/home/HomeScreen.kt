package com.ravi.mylivetv.ui.home

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.ravi.mylivetv.navigation.ScreenRoutes
import com.ravi.mylivetv.ui.composable.CategoryItem
import com.ravi.mylivetv.utils.Constants
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val pagerState = rememberPagerState(
        initialPage = uiState.selectedTab,
        pageCount = { Constants.TABS.size }
    )
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val items = listOf("Home", "Favorites", "Settings")
    val icons = listOf(Icons.Filled.Home, Icons.Filled.Favorite, Icons.Filled.Settings)
    val selectedItem = remember { mutableStateOf(items[0]) }

    // Flag to prevent circular updates
    var isUserScrolling by remember { mutableStateOf(false) }

    // Sync pager state with view model (only when user swipes, not when clicking tabs)
    LaunchedEffect(pagerState.currentPage, pagerState.isScrollInProgress) {
        if (pagerState.isScrollInProgress) {
            isUserScrolling = true
        }

        if (!pagerState.isScrollInProgress && isUserScrolling && pagerState.currentPage != uiState.selectedTab) {
            viewModel.selectTab(pagerState.currentPage)
            isUserScrolling = false
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(Modifier.height(12.dp))
                items.forEach { item ->
                    NavigationDrawerItem(
                        icon = { Icon(icons[items.indexOf(item)], contentDescription = null) },
                        label = { Text(item) },
                        selected = item == selectedItem.value,
                        onClick = {
                            scope.launch { drawerState.close() }
                            selectedItem.value = item
                            // TODO: Add navigation logic here
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = "HasikitTv") },
                    navigationIcon = {
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
                    .background(Color(0xFFDCB8E6)) // Light green background
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                // Tabs
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Constants.TABS.forEachIndexed { index, title ->
                        val isSelected = pagerState.currentPage == index

                        // Animate background color
                        val backgroundColor by animateColorAsState(
                            targetValue = if (isSelected)
                                Color(0xFF451BFD) // Darker blue for selected
                            else
                                Color(0xFFADD8E6), // Light blue
                            animationSpec = tween(300),
                            label = "tabBackgroundColor"
                        )

                        // Animate elevation
                        val elevation by animateDpAsState(
                            targetValue = if (isSelected) 4.dp else 0.dp,
                            animationSpec = tween(300),
                            label = "tabElevation"
                        )

                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    if (index != pagerState.currentPage) {
                                        scope.launch {
                                            viewModel.selectTab(index)
                                            pagerState.animateScrollToPage(index)
                                        }
                                    }
                                },
                            color = backgroundColor,
                            shadowElevation = elevation
                        ) {
                            Text(
                                text = title,
                                modifier = Modifier.padding(vertical = 12.dp),
                                fontSize = 16.sp,
                                fontWeight = if (isSelected)
                                    FontWeight.Bold
                                else
                                    FontWeight.Medium,
                                color = Color.Black,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                // Content with HorizontalPager for swipe
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFADD8E6)) // Light blue background
                        .padding(8.dp)
                ) {
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.fillMaxSize()
                    ) { page ->
                        val listState = rememberLazyListState()

                        LazyColumn(
                            state = listState,
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            // Category/Language/Country Items (including "Recently Watched" as first item)
                            val items = when (page) {
                                0 -> Constants.CATEGORIES
                                1 -> Constants.LANGUAGES
                                2 -> Constants.COUNTRIES
                                else -> Constants.CATEGORIES
                            }
                            itemsIndexed(items) { index, item ->
                                CategoryItem(
                                    text = item,
                                    isSelected = uiState.selectedItemIndex == index && page == uiState.selectedTab,
                                    onClick = {
                                        viewModel.selectItem(index)
                                        // Navigate to channel screen with the category
                                        navController.navigate(ScreenRoutes.ChannelScreen.createRoute(item))
                                    }
                                )
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
fun PreviewHomeScreen() {
    val navController = rememberNavController()
    // Note: Preview won't work with HiltViewModel
}
