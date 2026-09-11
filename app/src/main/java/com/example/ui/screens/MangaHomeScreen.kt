package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MangaEntity
import com.example.ui.components.ContinueMangaCard
import com.example.ui.components.LargeMangaHighlightCard
import com.example.ui.components.MangaCoverCard
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

@Composable
fun MangaHomeScreen(viewModel: BookVerseViewModel) {
    val featuredMangaList by viewModel.featuredManga.collectAsState()
    val trendingManga by viewModel.trendingManga.collectAsState()
    val popularManga by viewModel.popularManga.collectAsState()
    val recentlyAdded by viewModel.recentlyAddedManga.collectAsState()
    val continueMangaList by viewModel.continueReadingManga.collectAsState()
    val totalCount by viewModel.totalMangaCount.collectAsState()

    val searchQuery by viewModel.mangaSearchQuery.collectAsState()
    val selectedGenre by viewModel.mangaSelectedGenre.collectAsState()
    val selectedStatus by viewModel.mangaSelectedStatus.collectAsState()
    val sortBy by viewModel.mangaSortBy.collectAsState()
    val searchResults by viewModel.mangaSearchResults.collectAsState()

    val isSearching = searchQuery.isNotEmpty() || selectedGenre != null || selectedStatus != null

    val heroManga = featuredMangaList.firstOrNull() ?: trendingManga.firstOrNull()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBackground)
            .testTag("manga_home_screen")
    ) {
        // Sticky Header / Search Bar
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(ObsidianCardElevated)
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .border(width = 1.dp, color = ObsidianBorder, shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp))
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(BrushedGold),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoStories,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "MANGA ARCHIVE",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            ),
                            color = CreamWhite
                        )
                        Text(
                            text = "$totalCount+ Legal & Open-Source Series",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = BrushedGold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Search text field
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.mangaSearchQuery.value = it },
                placeholder = { Text("Search manga by title, author, artist, tags...", color = SoftMutedText, fontSize = 13.sp) },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = BrushedGold)
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.mangaSearchQuery.value = "" }) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear search", tint = SoftMutedText)
                        }
                    }
                },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = CreamWhite,
                    unfocusedTextColor = CreamWhite,
                    focusedContainerColor = ObsidianCard,
                    unfocusedContainerColor = ObsidianCard,
                    focusedBorderColor = BrushedGold,
                    unfocusedBorderColor = ObsidianBorder
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("manga_search_input")
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Quick Genre Filter Chips
            val genres = listOf("All", "Action", "Fantasy", "Sci-Fi", "Supernatural", "Slice of Life", "Historical", "Comedy", "Mystery")
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                genres.forEach { genre ->
                    val isSelected = (genre == "All" && selectedGenre == null) || selectedGenre == genre
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            viewModel.mangaSelectedGenre.value = if (genre == "All") null else genre
                        },
                        label = { Text(genre, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = BrushedGold,
                            selectedLabelColor = Color.Black,
                            containerColor = ObsidianCard,
                            labelColor = SoftMutedText
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            borderColor = if (isSelected) BrushedGold else ObsidianBorder,
                            selectedBorderColor = BrushedGold,
                            enabled = true,
                            selected = isSelected
                        )
                    )
                }
            }

            // Status and Sort Filters
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Status toggles
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    val statuses = listOf(null to "All Status", "Ongoing" to "Ongoing", "Completed" to "Completed")
                    statuses.forEach { (statusVal, label) ->
                        val isSel = selectedStatus == statusVal
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 10.sp,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal
                            ),
                            color = if (isSel) BrushedGold else SoftMutedText,
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(if (isSel) ObsidianBorder else Color.Transparent)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                .clickable { viewModel.mangaSelectedStatus.value = statusVal }
                        )
                    }
                }

                // Sort toggle
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable {
                        viewModel.mangaSortBy.value = when (sortBy) {
                            "popularity" -> "recent"
                            "recent" -> "alpha"
                            else -> "popularity"
                        }
                    }
                ) {
                    Icon(imageVector = Icons.Default.Sort, contentDescription = null, tint = BrushedGold, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = when (sortBy) {
                            "popularity" -> "Top Popular"
                            "recent" -> "Newest"
                            else -> "A to Z"
                        },
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.SemiBold),
                        color = CreamWhite
                    )
                }
            }
        }

        // Body Content
        if (isSearching) {
            // Search Results Grid
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "Results (${searchResults.size} titles found)",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = GoldLight,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                if (searchResults.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("No manga found matching filters.", color = SoftMutedText)
                            Text("Try searching another title or clearing filters.", color = GoldMuted, fontSize = 12.sp)
                        }
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(130.dp),
                        contentPadding = PaddingValues(bottom = 90.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize().testTag("manga_search_results_grid")
                    ) {
                        items(searchResults, key = { it.id }) { manga ->
                            MangaCoverCard(
                                manga = manga,
                                onClick = { viewModel.openMangaDetail(manga.id) }
                            )
                        }
                    }
                }
            }
        } else {
            // Standard Discovery Feed
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 90.dp)
            ) {
                // Featured Hero
                if (heroManga != null) {
                    Box(modifier = Modifier.padding(16.dp)) {
                        LargeMangaHighlightCard(
                            manga = heroManga,
                            onReadClick = { viewModel.openMangaReader(heroManga.id, 0, 0) },
                            onDetailClick = { viewModel.openMangaDetail(heroManga.id) }
                        )
                    }
                }

                // Continue Reading (Manga) Section
                if (continueMangaList.isNotEmpty()) {
                    Column(modifier = Modifier.padding(vertical = 8.dp)) {
                        MangaSectionHeader(
                            title = "Continue Reading Manga",
                            subtitle = "Pick up right where you left off"
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(continueMangaList, key = { it.mangaId }) { progress ->
                                ContinueMangaCard(
                                    progress = progress,
                                    onResume = {
                                        viewModel.openMangaReader(
                                            mangaId = progress.mangaId,
                                            chapterIndex = progress.chapterIndex,
                                            initialPage = progress.pageIndex
                                        )
                                    }
                                )
                            }
                        }
                    }
                }

                // Trending Manga
                MangaCarouselSection(
                    title = "Trending Manga",
                    subtitle = "Most read series this week",
                    items = trendingManga,
                    onItemClick = { viewModel.openMangaDetail(it.id) }
                )

                // Popular Manga
                MangaCarouselSection(
                    title = "Popular Masterworks",
                    subtitle = "Top community rated series",
                    items = popularManga,
                    onItemClick = { viewModel.openMangaDetail(it.id) }
                )

                // Recently Added
                MangaCarouselSection(
                    title = "Recently Added Releases",
                    subtitle = "Fresh legal serializations and classic restorations",
                    items = recentlyAdded,
                    onItemClick = { viewModel.openMangaDetail(it.id) }
                )

                // Recommended For You
                MangaCarouselSection(
                    title = "Recommended For You",
                    subtitle = "Handcrafted recommendations for story lovers",
                    items = featuredMangaList.drop(1),
                    onItemClick = { viewModel.openMangaDetail(it.id) }
                )
            }
        }
    }
}

@Composable
fun MangaCarouselSection(
    title: String,
    subtitle: String,
    items: List<MangaEntity>,
    onItemClick: (MangaEntity) -> Unit
) {
    if (items.isEmpty()) return

    Column(modifier = Modifier.padding(vertical = 12.dp)) {
        MangaSectionHeader(title = title, subtitle = subtitle)
        Spacer(modifier = Modifier.height(10.dp))
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(items, key = { it.id }) { manga ->
                MangaCoverCard(
                    manga = manga,
                    onClick = { onItemClick(manga) }
                )
            }
        }
    }
}

@Composable
fun MangaSectionHeader(title: String, subtitle: String) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = CreamWhite
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = SoftMutedText
        )
    }
}
