package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ArrowLeft
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.icons.filled.ViewCarousel
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.data.model.MangaChapterEntity
import com.example.data.model.MangaEntity
import com.example.ui.theme.AmberGlow
import com.example.ui.theme.BrushedGold
import com.example.ui.theme.CreamWhite
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldMuted
import com.example.ui.theme.ObsidianBackground
import com.example.ui.theme.ObsidianBorder
import com.example.ui.theme.ObsidianCard
import com.example.ui.theme.ObsidianCardElevated
import com.example.ui.theme.SoftMutedText
import com.example.ui.viewmodel.BookVerseViewModel
import kotlinx.coroutines.launch

@Composable
fun MangaReaderScreen(
    mangaId: String,
    initialChapterIndex: Int,
    initialPage: Int,
    viewModel: BookVerseViewModel
) {
    val coroutineScope = rememberCoroutineScope()

    var manga by remember { mutableStateOf<MangaEntity?>(null) }
    var currentChapterIndex by remember { mutableIntStateOf(initialChapterIndex) }
    var chapter by remember { mutableStateOf<MangaChapterEntity?>(null) }
    var totalChaptersCount by remember { mutableIntStateOf(10) }

    // Reader modes: "VERTICAL" (Webtoon) vs "HORIZONTAL" (Page-by-page)
    var readerMode by remember { mutableStateOf("VERTICAL") }
    var backgroundColorTheme by remember { mutableStateOf("OLED") } // OLED, DARK, SEPIA, WHITE
    var zoomScale by remember { mutableFloatStateOf(1.0f) }
    var showControls by remember { mutableStateOf(true) }
    var showSettingsModal by remember { mutableStateOf(false) }

    val bgColor = when (backgroundColorTheme) {
        "OLED" -> Color.Black
        "SEPIA" -> Color(0xFFFBF0D9)
        "WHITE" -> Color(0xFFFFFFFF)
        else -> ObsidianBackground
    }

    val textColor = when (backgroundColorTheme) {
        "SEPIA" -> Color(0xFF3C2F1F)
        "WHITE" -> Color(0xFF1E1E1E)
        else -> CreamWhite
    }

    LaunchedEffect(mangaId) {
        manga = viewModel.repository.getMangaByIdDirect(mangaId)
        manga?.let { totalChaptersCount = it.totalChapters }
    }

    LaunchedEffect(mangaId, currentChapterIndex) {
        val loadedChapter = viewModel.repository.getChapter(mangaId, currentChapterIndex)
        chapter = loadedChapter ?: MangaChapterEntity(
            mangaId = mangaId,
            chapterIndex = currentChapterIndex,
            chapterNumber = "Chapter ${currentChapterIndex + 1}",
            title = "Episode ${currentChapterIndex + 1}",
            pageCount = 12,
            pagesData = ""
        )
    }

    val currentChapter = chapter
    val pageCount = currentChapter?.pageCount ?: 12

    // Parse page image list
    val pageItems = remember(currentChapter?.pagesData, currentChapter?.chapterIndex) {
        val raw = currentChapter?.pagesData.orEmpty()
        if (raw.isNotEmpty()) {
            raw.split(";;").map { it.substringBefore("|") }
        } else {
            listOf(
                "https://images.unsplash.com/photo-1578632767115-351597cf2477?w=900&q=85",
                "https://images.unsplash.com/photo-1607604276583-eef5d076aa5f?w=900&q=85",
                "https://images.unsplash.com/photo-1534447677768-be436bb09401?w=900&q=85",
                "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=900&q=85",
                "https://images.unsplash.com/photo-1509198397868-475647b2a1e5?w=900&q=85",
                "https://images.unsplash.com/photo-1519638399535-1b036603ac77?w=900&q=85",
                "https://images.unsplash.com/photo-1563089145-599997674d42?w=900&q=85",
                "https://images.unsplash.com/photo-1550684848-fac1c5b4e853?w=900&q=85",
                "https://images.unsplash.com/photo-1514565131-fce0801e5785?w=900&q=85",
                "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=900&q=85",
                "https://images.unsplash.com/photo-1579783900882-c0d3dad7b119?w=900&q=85",
                "https://images.unsplash.com/photo-1550745165-9bc0b252726f?w=900&q=85"
            )
        }
    }

    var currentPage by remember { mutableIntStateOf(initialPage.coerceIn(0, (pageItems.size - 1).coerceAtLeast(0))) }

    // Page state for horizontal pager
    val horizontalPagerState = rememberPagerState(
        initialPage = initialPage.coerceIn(0, (pageItems.size - 1).coerceAtLeast(0)),
        pageCount = { pageItems.size }
    )

    // Scroll state for vertical webtoon
    val verticalListState = rememberLazyListState(
        initialFirstVisibleItemIndex = initialPage.coerceIn(0, (pageItems.size - 1).coerceAtLeast(0))
    )

    // Save progress on page changes
    LaunchedEffect(currentPage, currentChapterIndex) {
        val m = manga ?: return@LaunchedEffect
        val ch = chapter ?: return@LaunchedEffect
        viewModel.saveMangaProgress(
            mangaId = m.id,
            title = m.title,
            coverImageUrl = m.coverImageUrl,
            chapterIndex = currentChapterIndex,
            chapterTitle = ch.chapterNumber,
            pageIndex = currentPage,
            totalPages = pageItems.size,
            completed = (currentChapterIndex >= totalChaptersCount - 1 && currentPage >= pageItems.size - 1)
        )
    }

    // Synchronize horizontal pager page changes
    LaunchedEffect(horizontalPagerState) {
        snapshotFlow { horizontalPagerState.currentPage }.collect { p ->
            currentPage = p
        }
    }

    // Synchronize vertical list page changes
    LaunchedEffect(verticalListState) {
        snapshotFlow { verticalListState.firstVisibleItemIndex }.collect { p ->
            currentPage = p
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
            .testTag("manga_reader_screen")
    ) {
        // Main Content Area with Zoom & Click to toggle controls
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(
                    scaleX = zoomScale,
                    scaleY = zoomScale
                )
                .clickable { showControls = !showControls }
        ) {
            if (readerMode == "VERTICAL") {
                // Vertical Webtoon Mode
                LazyColumn(
                    state = verticalListState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = if (showControls) 64.dp else 0.dp)
                ) {
                    itemsIndexed(pageItems) { index, imgUrl ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            AsyncImage(
                                model = imgUrl,
                                contentDescription = "Page ${index + 1}",
                                contentScale = ContentScale.FillWidth,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("manga_page_$index")
                            )
                            Text(
                                text = "— Page ${index + 1} of ${pageItems.size} —",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = SoftMutedText,
                                modifier = Modifier.padding(vertical = 6.dp)
                            )
                        }
                    }

                    // Chapter End Navigation Card
                    item {
                        ChapterEndNavCard(
                            hasNext = currentChapterIndex < totalChaptersCount - 1,
                            onNextChapter = {
                                if (currentChapterIndex < totalChaptersCount - 1) {
                                    currentChapterIndex++
                                    currentPage = 0
                                    coroutineScope.launch { verticalListState.scrollToItem(0) }
                                }
                            }
                        )
                    }
                }
            } else {
                // Horizontal Page-by-Page Mode
                HorizontalPager(
                    state = horizontalPagerState,
                    modifier = Modifier.fillMaxSize()
                ) { pageIndex ->
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = pageItems[pageIndex],
                            contentDescription = "Page ${pageIndex + 1}",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .fillMaxSize()
                                .testTag("manga_page_$pageIndex")
                        )
                    }
                }
            }
        }

        // Top App Bar Controls (Animated)
        AnimatedVisibility(
            visible = showControls,
            enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
            modifier = Modifier.align(Alignment.TopCenter)
        ) {
            Surface(
                color = Color(0xF20B0B0E),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, ObsidianBorder, RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { viewModel.navigateBack() },
                            modifier = Modifier.testTag("reader_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Exit Reader",
                                tint = CreamWhite
                            )
                        }
                        Column(modifier = Modifier.padding(start = 4.dp)) {
                            Text(
                                text = manga?.title ?: "Manga",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = CreamWhite,
                                maxLines = 1
                            )
                            Text(
                                text = "${currentChapter?.chapterNumber} • ${currentChapter?.title}",
                                style = MaterialTheme.typography.labelSmall,
                                color = GoldLight
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Toggle Vertical vs Horizontal Mode
                        IconButton(
                            onClick = {
                                readerMode = if (readerMode == "VERTICAL") "HORIZONTAL" else "VERTICAL"
                            },
                            modifier = Modifier.testTag("reader_mode_toggle")
                        ) {
                            Icon(
                                imageVector = if (readerMode == "VERTICAL") Icons.Default.ViewCarousel else Icons.Default.SwapVert,
                                contentDescription = "Toggle Reading Mode",
                                tint = BrushedGold
                            )
                        }

                        // Bookmark current page
                        IconButton(
                            onClick = {
                                val m = manga ?: return@IconButton
                                viewModel.addBookmark(
                                    contentId = m.id,
                                    contentType = "MANGA",
                                    chapterTitle = "${currentChapter?.chapterNumber} • Page ${currentPage + 1}",
                                    snippet = "${m.title} at ${currentChapter?.chapterNumber}",
                                    progress = ((currentPage + 1) * 100) / pageItems.size,
                                    pageIndex = currentPage
                                )
                            },
                            modifier = Modifier.testTag("reader_bookmark_page_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Bookmark,
                                contentDescription = "Bookmark Page",
                                tint = CreamWhite
                            )
                        }

                        // Settings
                        IconButton(
                            onClick = { showSettingsModal = true },
                            modifier = Modifier.testTag("reader_settings_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Reader Settings",
                                tint = CreamWhite
                            )
                        }
                    }
                }
            }
        }

        // Bottom Navigation Bar Controls (Animated)
        AnimatedVisibility(
            visible = showControls,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Surface(
                color = Color(0xF20B0B0E),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, ObsidianBorder, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    // Page Indicator & Slider
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Page ${currentPage + 1} of ${pageItems.size}",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = CreamWhite
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            IconButton(
                                onClick = {
                                    if (zoomScale > 0.8f) zoomScale -= 0.2f
                                },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(imageVector = Icons.Default.ZoomOut, contentDescription = "Zoom Out", tint = SoftMutedText, modifier = Modifier.size(16.dp))
                            }
                            Text(
                                text = "${(zoomScale * 100).toInt()}%",
                                style = MaterialTheme.typography.labelSmall,
                                color = GoldLight,
                                modifier = Modifier.align(Alignment.CenterVertically)
                            )
                            IconButton(
                                onClick = {
                                    if (zoomScale < 2.0f) zoomScale += 0.2f
                                },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(imageVector = Icons.Default.ZoomIn, contentDescription = "Zoom In", tint = SoftMutedText, modifier = Modifier.size(16.dp))
                            }
                        }
                    }

                    Slider(
                        value = currentPage.toFloat(),
                        onValueChange = { newPage ->
                            val target = newPage.toInt().coerceIn(0, pageItems.size - 1)
                            currentPage = target
                            coroutineScope.launch {
                                if (readerMode == "HORIZONTAL") {
                                    horizontalPagerState.scrollToPage(target)
                                } else {
                                    verticalListState.scrollToItem(target)
                                }
                            }
                        },
                        valueRange = 0f..(pageItems.size - 1).coerceAtLeast(1).toFloat(),
                        steps = (pageItems.size - 2).coerceAtLeast(0),
                        colors = SliderDefaults.colors(
                            thumbColor = BrushedGold,
                            activeTrackColor = BrushedGold,
                            inactiveTrackColor = ObsidianBorder
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("reader_page_slider")
                    )

                    // Previous / Next Chapter Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                if (currentChapterIndex > 0) {
                                    currentChapterIndex--
                                    currentPage = 0
                                    coroutineScope.launch {
                                        if (readerMode == "HORIZONTAL") horizontalPagerState.scrollToPage(0)
                                        else verticalListState.scrollToItem(0)
                                    }
                                }
                            },
                            enabled = currentChapterIndex > 0,
                            modifier = Modifier.testTag("reader_prev_chapter_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowLeft,
                                contentDescription = "Previous Chapter",
                                tint = if (currentChapterIndex > 0) BrushedGold else SoftMutedText,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        Text(
                            text = "${currentChapter?.chapterNumber} of $totalChaptersCount",
                            style = MaterialTheme.typography.labelSmall,
                            color = SoftMutedText
                        )

                        IconButton(
                            onClick = {
                                if (currentChapterIndex < totalChaptersCount - 1) {
                                    currentChapterIndex++
                                    currentPage = 0
                                    coroutineScope.launch {
                                        if (readerMode == "HORIZONTAL") horizontalPagerState.scrollToPage(0)
                                        else verticalListState.scrollToItem(0)
                                    }
                                }
                            },
                            enabled = currentChapterIndex < totalChaptersCount - 1,
                            modifier = Modifier.testTag("reader_next_chapter_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowRight,
                                contentDescription = "Next Chapter",
                                tint = if (currentChapterIndex < totalChaptersCount - 1) BrushedGold else SoftMutedText,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }
            }
        }

        // Settings Dialog Modal
        if (showSettingsModal) {
            Dialog(onDismissRequest = { showSettingsModal = false }) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, BrushedGold, RoundedCornerShape(16.dp))
                        .testTag("reader_settings_modal"),
                    colors = CardDefaults.cardColors(containerColor = ObsidianCardElevated),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Reader Settings", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = CreamWhite)
                            IconButton(onClick = { showSettingsModal = false }) {
                                Icon(Icons.Default.Close, contentDescription = "Close", tint = SoftMutedText)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Reading Mode Selection
                        Text("Reading Mode", style = MaterialTheme.typography.labelSmall, color = GoldLight)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ReaderModeButton(
                                label = "Vertical Webtoon",
                                isSelected = readerMode == "VERTICAL",
                                onClick = { readerMode = "VERTICAL" },
                                modifier = Modifier.weight(1f)
                            )
                            ReaderModeButton(
                                label = "Page-by-Page",
                                isSelected = readerMode == "HORIZONTAL",
                                onClick = { readerMode = "HORIZONTAL" },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Background Themes
                        Text("Background Canvas", style = MaterialTheme.typography.labelSmall, color = GoldLight)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val themes = listOf("OLED" to "True Black", "DARK" to "Charcoal", "SEPIA" to "Sepia", "WHITE" to "Paper")
                            themes.forEach { (mode, name) ->
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(
                                            when (mode) {
                                                "OLED" -> Color.Black
                                                "SEPIA" -> Color(0xFFFBF0D9)
                                                "WHITE" -> Color.White
                                                else -> ObsidianBackground
                                            }
                                        )
                                        .border(
                                            2.dp,
                                            if (backgroundColorTheme == mode) BrushedGold else ObsidianBorder,
                                            RoundedCornerShape(8.dp)
                                        )
                                        .clickable { backgroundColorTheme = mode }
                                        .padding(vertical = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = name,
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                        color = if (mode == "SEPIA" || mode == "WHITE") Color.Black else Color.White
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Zoom Reset
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Zoom Scale", style = MaterialTheme.typography.labelSmall, color = GoldLight)
                            Text(
                                text = "Reset (100%)",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = BrushedGold,
                                modifier = Modifier.clickable { zoomScale = 1.0f }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChapterEndNavCard(
    hasNext: Boolean,
    onNextChapter: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = ObsidianCardElevated),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (hasNext) "You've reached the end of this chapter!" else "You've caught up with the latest chapter!",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = CreamWhite
            )
            Spacer(modifier = Modifier.height(8.dp))
            if (hasNext) {
                Surface(
                    color = BrushedGold,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .clickable { onNextChapter() }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .testTag("next_chapter_action_button")
                ) {
                    Text(
                        text = "Read Next Chapter",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.Black
                    )
                }
            } else {
                Text(
                    text = "Check back soon for new releases.",
                    style = MaterialTheme.typography.bodySmall,
                    color = SoftMutedText
                )
            }
        }
    }
}

@Composable
fun ReaderModeButton(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = if (isSelected) BrushedGold else ObsidianCard,
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) BrushedGold else ObsidianBorder),
        modifier = modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier.padding(vertical = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = if (isSelected) Color.Black else CreamWhite
            )
        }
    }
}
