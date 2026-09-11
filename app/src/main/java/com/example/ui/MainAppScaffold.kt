package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AudioMiniPlayer
import com.example.ui.components.AuthDialog
import com.example.ui.components.ExpandedAudioPlayerModal
import com.example.ui.screens.AdminSourcesScreen
import com.example.ui.screens.BookDetailScreen
import com.example.ui.screens.CreditsScreen
import com.example.ui.screens.DiscoverScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LegalScreen
import com.example.ui.screens.LibraryScreen
import com.example.ui.screens.MangaDetailScreen
import com.example.ui.screens.MangaHomeScreen
import com.example.ui.screens.MangaReaderScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.ReaderScreen
import com.example.ui.screens.SearchScreen
import com.example.ui.theme.BrushedGold
import com.example.ui.theme.CreamWhite
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldMuted
import com.example.ui.theme.ObsidianBackground
import com.example.ui.theme.ObsidianBorder
import com.example.ui.theme.ObsidianCardElevated
import com.example.ui.theme.SoftMutedText
import com.example.ui.viewmodel.BookVerseViewModel
import com.example.ui.viewmodel.ScreenRoute

@Composable
fun MainAppScaffold(viewModel: BookVerseViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val playerState by viewModel.playerState.collectAsState()
    val isPlayerExpanded by viewModel.isPlayerExpanded.collectAsState()
    val toastMessage by viewModel.toastMessage.collectAsState()

    val isImmersiveReader = currentScreen is ScreenRoute.Reader || currentScreen is ScreenRoute.MangaReader

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(toastMessage) {
        toastMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearToast()
        }
    }

    // Hardware back handler
    BackHandler(enabled = currentScreen !is ScreenRoute.Home) {
        if (!viewModel.navigateBack()) {
            viewModel.navigateTo(ScreenRoute.Home)
        }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize().background(ObsidianBackground)) {
        val isWideScreen = maxWidth >= 700.dp

        if (isWideScreen) {
            // Tablet / Desktop Canonical Layout: Side Navigation Rail
            Row(modifier = Modifier.fillMaxSize()) {
                if (!isImmersiveReader) {
                    DesktopNavigationRail(
                        currentRoute = currentScreen,
                        onNavigate = { viewModel.navigateTo(it) }
                    )
                }

                Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                    ScreenContent(currentScreen, viewModel)

                    // Sticky Mini Player on bottom
                    if (playerState.currentBook != null && !isImmersiveReader) {
                        AudioMiniPlayer(
                            playerState = playerState,
                            onTogglePlay = { viewModel.playerManager.togglePlayPause() },
                            onExpand = { viewModel.openPlayerModal() },
                            onSkipForward = { viewModel.playerManager.skipForward30() },
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .fillMaxWidth()
                        )
                    }
                }
            }
        } else {
            // Mobile Canonical Layout: Scaffold with Bottom Navigation
            Scaffold(
                bottomBar = {
                    if (!isImmersiveReader) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            // Persistent Mini Player above bottom nav
                            if (playerState.currentBook != null) {
                                AudioMiniPlayer(
                                    playerState = playerState,
                                    onTogglePlay = { viewModel.playerManager.togglePlayPause() },
                                    onExpand = { viewModel.openPlayerModal() },
                                    onSkipForward = { viewModel.playerManager.skipForward30() },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }

                            MobileBottomNavigation(
                                currentRoute = currentScreen,
                                onNavigate = { viewModel.navigateTo(it) }
                            )
                        }
                    }
                },
                snackbarHost = {
                    SnackbarHost(
                        hostState = snackbarHostState,
                        modifier = Modifier.padding(bottom = if (playerState.currentBook != null) 140.dp else 80.dp)
                    ) { data ->
                        Snackbar(
                            containerColor = ObsidianCardElevated,
                            contentColor = CreamWhite,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.border(1.dp, BrushedGold, RoundedCornerShape(10.dp))
                        ) {
                            Text(data.visuals.message, fontWeight = FontWeight.SemiBold)
                        }
                    }
                },
                containerColor = ObsidianBackground
            ) { padding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {
                    ScreenContent(currentScreen, viewModel)
                }
            }
        }

        // Authentication Dialog (Sign In / Register)
        AuthDialog(viewModel = viewModel)

        // Full-screen Modal Player Sheet
        if (isPlayerExpanded && playerState.currentBook != null) {
            ExpandedAudioPlayerModal(
                playerState = playerState,
                playerManager = viewModel.playerManager,
                onClose = { viewModel.closePlayerModal() }
            )
        }
    }
}

@Composable
fun ScreenContent(route: ScreenRoute, viewModel: BookVerseViewModel) {
    when (route) {
        is ScreenRoute.Home -> HomeScreen(viewModel)
        is ScreenRoute.Manga -> MangaHomeScreen(viewModel)
        is ScreenRoute.Discover -> DiscoverScreen(viewModel)
        is ScreenRoute.Search -> SearchScreen(viewModel)
        is ScreenRoute.Library -> LibraryScreen(viewModel)
        is ScreenRoute.Profile -> ProfileScreen(viewModel)
        is ScreenRoute.Credits -> CreditsScreen(viewModel)
        is ScreenRoute.BookDetail -> BookDetailScreen(route.bookId, viewModel)
        is ScreenRoute.Reader -> ReaderScreen(route.bookId, viewModel)
        is ScreenRoute.MangaDetail -> MangaDetailScreen(route.mangaId, viewModel)
        is ScreenRoute.MangaReader -> MangaReaderScreen(route.mangaId, route.initialChapterIndex, route.initialPage, viewModel)
        is ScreenRoute.AdminSources -> AdminSourcesScreen(viewModel)
        is ScreenRoute.LegalLicensing -> LegalScreen(viewModel)
    }
}

@Composable
fun MobileBottomNavigation(
    currentRoute: ScreenRoute,
    onNavigate: (ScreenRoute) -> Unit
) {
    NavigationBar(
        containerColor = ObsidianCardElevated,
        tonalElevation = 8.dp,
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, ObsidianBorder)
            .testTag("bottom_navigation")
    ) {
        val navItems = listOf(
            Triple(ScreenRoute.Home, "Home", Icons.Default.Home),
            Triple(ScreenRoute.Manga, "Manga", Icons.Default.AutoStories),
            Triple(ScreenRoute.Discover, "Discover", Icons.Default.Explore),
            Triple(ScreenRoute.Search, "Search", Icons.Default.Search),
            Triple(ScreenRoute.Library, "Library", Icons.Default.Bookmarks),
            Triple(ScreenRoute.Profile, "Profile", Icons.Default.Person)
        )

        navItems.forEach { (route, label, icon) ->
            val isSelected = currentRoute::class == route::class
            NavigationBarItem(
                selected = isSelected,
                onClick = { onNavigate(route) },
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        modifier = Modifier.size(22.dp)
                    )
                },
                label = {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 10.sp
                        )
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.Black,
                    selectedTextColor = BrushedGold,
                    indicatorColor = BrushedGold,
                    unselectedIconColor = SoftMutedText,
                    unselectedTextColor = SoftMutedText
                ),
                modifier = Modifier.testTag("nav_${label.lowercase()}")
            )
        }
    }
}

@Composable
fun DesktopNavigationRail(
    currentRoute: ScreenRoute,
    onNavigate: (ScreenRoute) -> Unit
) {
    NavigationRail(
        containerColor = ObsidianCardElevated,
        header = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(vertical = 16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(BrushedGold),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.MenuBook,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "BOOKSVERSE",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
                    color = CreamWhite
                )
            }
        },
        modifier = Modifier
            .fillMaxHeight()
            .border(1.dp, ObsidianBorder)
    ) {
        val navItems = listOf(
            Triple(ScreenRoute.Home, "Home", Icons.Default.Home),
            Triple(ScreenRoute.Manga, "Manga", Icons.Default.AutoStories),
            Triple(ScreenRoute.Discover, "Discover", Icons.Default.Explore),
            Triple(ScreenRoute.Search, "Search", Icons.Default.Search),
            Triple(ScreenRoute.Library, "Library", Icons.Default.Bookmarks),
            Triple(ScreenRoute.Profile, "Profile", Icons.Default.Person)
        )

        navItems.forEach { (route, label, icon) ->
            val isSelected = currentRoute::class == route::class
            NavigationRailItem(
                selected = isSelected,
                onClick = { onNavigate(route) },
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = { Text(label) },
                colors = NavigationRailItemDefaults.colors(
                    selectedIconColor = Color.Black,
                    selectedTextColor = BrushedGold,
                    indicatorColor = BrushedGold,
                    unselectedIconColor = SoftMutedText,
                    unselectedTextColor = SoftMutedText
                )
            )
        }
    }
}
