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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BookEntity
import com.example.ui.components.BookCoverCard
import com.example.ui.theme.BrushedGold
import com.example.ui.theme.CreamWhite
import com.example.ui.theme.GoldLight
import com.example.ui.theme.ObsidianBackground
import com.example.ui.theme.ObsidianBorder
import com.example.ui.theme.ObsidianCard
import com.example.ui.theme.ObsidianCardElevated
import com.example.ui.theme.SoftMutedText
import com.example.ui.viewmodel.BookVerseViewModel

@Composable
fun DiscoverScreen(
    viewModel: BookVerseViewModel,
    modifier: Modifier = Modifier
) {
    val searchResults by viewModel.searchResults.collectAsState()
    val totalBooks by viewModel.totalBooksCount.collectAsState()
    val totalAudiobooks by viewModel.totalAudiobooksCount.collectAsState()

    val selectedGenre by viewModel.searchGenre.collectAsState()
    val filterAudio by viewModel.searchFilterAudio.collectAsState()
    val filterEbook by viewModel.searchFilterEbook.collectAsState()
    val currentSort by viewModel.searchSortBy.collectAsState()

    val genres = listOf(
        "All", "Classics", "Fantasy", "Mystery", "Science Fiction",
        "Philosophy", "Romance", "Adventure", "Drama", "History", "Poetry"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBackground)
            .padding(bottom = 90.dp)
            .testTag("discover_screen")
    ) {
        // Screen Title & Summary
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 14.dp)
        ) {
            Text(
                text = "DISCOVER CATALOG",
                style = MaterialTheme.typography.labelSmall.copy(
                    letterSpacing = 1.5.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = BrushedGold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Curated Masterpieces",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                ),
                color = CreamWhite
            )
            Text(
                text = "$totalBooks works available • $totalAudiobooks full-length audiobooks",
                style = MaterialTheme.typography.bodySmall,
                color = SoftMutedText
            )
        }

        // Format Toggle Tabs (All, Ebooks, Audiobooks)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FormatTabChip(
                label = "All Formats",
                icon = null,
                isSelected = !filterAudio && !filterEbook,
                onClick = {
                    viewModel.searchFilterAudio.value = false
                    viewModel.searchFilterEbook.value = false
                }
            )

            FormatTabChip(
                label = "Audiobooks",
                icon = Icons.Default.Headphones,
                isSelected = filterAudio,
                onClick = {
                    viewModel.searchFilterAudio.value = true
                    viewModel.searchFilterEbook.value = false
                }
            )

            FormatTabChip(
                label = "Ebooks",
                icon = Icons.Default.MenuBook,
                isSelected = filterEbook,
                onClick = {
                    viewModel.searchFilterAudio.value = false
                    viewModel.searchFilterEbook.value = true
                }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Horizontal Genre Filter Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            genres.forEach { genre ->
                val isSelected = (genre == "All" && selectedGenre == null) || (selectedGenre == genre)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isSelected) BrushedGold else ObsidianCardElevated)
                        .border(
                            1.dp,
                            if (isSelected) BrushedGold else ObsidianBorder,
                            RoundedCornerShape(20.dp)
                        )
                        .clickable {
                            viewModel.searchGenre.value = if (genre == "All") null else genre
                        }
                        .padding(horizontal = 14.dp, vertical = 7.dp)
                        .testTag("genre_chip_$genre")
                ) {
                    Text(
                        text = genre,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        ),
                        color = if (isSelected) Color.Black else CreamWhite
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Sort Selector Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${searchResults.size} Titles Available",
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                color = SoftMutedText
            )

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf(
                    "popularity" to "Popular",
                    "recent" to "Recently Added",
                    "alpha" to "A-Z"
                ).forEach { (sortKey, label) ->
                    val isSelected = currentSort == sortKey
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        ),
                        color = if (isSelected) BrushedGold else SoftMutedText,
                        modifier = Modifier
                            .clickable { viewModel.searchSortBy.value = sortKey }
                            .padding(4.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Catalog Grid (3 columns on normal screen, scalable)
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 110.dp),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(searchResults, key = { it.id }) { book ->
                BookCoverCard(
                    book = book,
                    onClick = { viewModel.openBookDetail(book.id) },
                    width = 110.dp
                )
            }
        }
    }
}

@Composable
fun FormatTabChip(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector?,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (isSelected) BrushedGold.copy(alpha = 0.2f) else ObsidianCard)
            .border(
                1.dp,
                if (isSelected) BrushedGold else ObsidianBorder,
                RoundedCornerShape(10.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isSelected) BrushedGold else SoftMutedText,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
            }
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                ),
                color = if (isSelected) BrushedGold else CreamWhite
            )
        }
    }
}
