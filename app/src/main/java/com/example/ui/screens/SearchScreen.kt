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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TrendingUp
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.BookEntity
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
fun SearchScreen(
    viewModel: BookVerseViewModel,
    modifier: Modifier = Modifier
) {
    val query by viewModel.searchQuery.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val filterAudio by viewModel.searchFilterAudio.collectAsState()
    val filterEbook by viewModel.searchFilterEbook.collectAsState()

    val popularKeywords = listOf(
        "Sherlock Holmes", "Jane Austen", "Dracula", "Philosophy",
        "Sci-Fi", "Audiobooks", "Dostoevsky", "Shakespeare", "Edgar Allan Poe"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBackground)
            .padding(bottom = 90.dp)
            .testTag("search_screen")
    ) {
        // Search Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            Text(
                text = "SEARCH CATALOG",
                style = MaterialTheme.typography.labelSmall.copy(
                    letterSpacing = 1.5.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = BrushedGold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Find Any Masterpiece",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                ),
                color = CreamWhite
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Search input field
            OutlinedTextField(
                value = query,
                onValueChange = { viewModel.searchQuery.value = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("search_text_input"),
                placeholder = {
                    Text(
                        text = "Search by title, author, narrator, genre...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = SoftMutedText
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = BrushedGold
                    )
                },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(
                            onClick = { viewModel.searchQuery.value = "" },
                            modifier = Modifier.testTag("search_clear_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear search",
                                tint = SoftMutedText
                            )
                        }
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = ObsidianCardElevated,
                    unfocusedContainerColor = ObsidianCard,
                    focusedBorderColor = BrushedGold,
                    unfocusedBorderColor = ObsidianBorder,
                    focusedTextColor = CreamWhite,
                    unfocusedTextColor = CreamWhite
                ),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )
        }

        // Filter Pills
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterBadge(
                label = "All",
                isSelected = !filterAudio && !filterEbook,
                onClick = {
                    viewModel.searchFilterAudio.value = false
                    viewModel.searchFilterEbook.value = false
                }
            )
            FilterBadge(
                label = "Audiobooks Only",
                icon = Icons.Default.Headphones,
                isSelected = filterAudio,
                onClick = {
                    viewModel.searchFilterAudio.value = true
                    viewModel.searchFilterEbook.value = false
                }
            )
            FilterBadge(
                label = "Ebooks Only",
                icon = Icons.Default.MenuBook,
                isSelected = filterEbook,
                onClick = {
                    viewModel.searchFilterAudio.value = false
                    viewModel.searchFilterEbook.value = true
                }
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Suggested Queries
        if (query.isEmpty()) {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.TrendingUp,
                        contentDescription = null,
                        tint = BrushedGold,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Popular Searches",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = CreamWhite
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    popularKeywords.forEach { kw ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(ObsidianCardElevated)
                                .border(1.dp, ObsidianBorder, RoundedCornerShape(16.dp))
                                .clickable { viewModel.searchQuery.value = kw }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = kw,
                                style = MaterialTheme.typography.bodySmall,
                                color = SoftMutedText
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Search Results List
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "${searchResults.size} results found",
                style = MaterialTheme.typography.bodySmall,
                color = SoftMutedText
            )
        }

        LazyColumn(
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(searchResults, key = { it.id }) { book ->
                SearchResultRowItem(
                    book = book,
                    onClick = { viewModel.openBookDetail(book.id) },
                    onRead = { viewModel.openReader(book.id) },
                    onListen = { viewModel.startAudiobook(book) }
                )
            }
        }
    }
}

@Composable
fun FilterBadge(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (isSelected) BrushedGold.copy(alpha = 0.2f) else ObsidianCard)
            .border(
                1.dp,
                if (isSelected) BrushedGold else ObsidianBorder,
                RoundedCornerShape(16.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isSelected) BrushedGold else SoftMutedText,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
            }
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                ),
                color = if (isSelected) BrushedGold else CreamWhite
            )
        }
    }
}

@Composable
fun SearchResultRowItem(
    book: BookEntity,
    onClick: () -> Unit,
    onRead: () -> Unit,
    onListen: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .border(1.dp, ObsidianBorder, RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .testTag("search_result_${book.id}"),
        color = ObsidianCard
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Cover
            Box(
                modifier = Modifier
                    .width(55.dp)
                    .aspectRatio(0.66f)
                    .clip(RoundedCornerShape(6.dp))
                    .background(ObsidianCardElevated)
            ) {
                AsyncImage(
                    model = book.coverImageUrl,
                    contentDescription = book.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Information
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = book.title,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    ),
                    color = CreamWhite,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = book.author,
                    style = MaterialTheme.typography.bodySmall,
                    color = SoftMutedText,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = book.genres.split(",").firstOrNull()?.trim() ?: "Literature",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = GoldLight
                    )
                    if (book.hasAudiobook) {
                        Text(
                            text = "• Audiobook",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = BrushedGold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Action icon
            IconButton(
                onClick = { if (book.hasAudiobook) onListen() else onRead() }
            ) {
                Icon(
                    imageVector = if (book.hasAudiobook) Icons.Default.Headphones else Icons.Default.MenuBook,
                    contentDescription = "Open",
                    tint = BrushedGold,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
