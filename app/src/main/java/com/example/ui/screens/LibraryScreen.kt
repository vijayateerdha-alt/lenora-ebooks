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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.BookmarkEntity
import com.example.data.model.UserLibraryEntity
import com.example.ui.theme.BrushedGold
import com.example.ui.theme.CreamWhite
import com.example.ui.theme.GoldLight
import com.example.ui.theme.ObsidianBackground
import com.example.ui.theme.ObsidianBorder
import com.example.ui.theme.ObsidianCard
import com.example.ui.theme.ObsidianCardElevated
import com.example.ui.theme.SoftMutedText
import com.example.ui.viewmodel.BookVerseViewModel
import com.example.ui.viewmodel.ScreenRoute

@Composable
fun LibraryScreen(
    viewModel: BookVerseViewModel,
    modifier: Modifier = Modifier
) {
    val libraryEntries by viewModel.userLibraryEntries.collectAsState()
    val bookmarks by viewModel.userBookmarks.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    var selectedTab by remember { mutableStateOf("ALL") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBackground)
            .padding(bottom = 90.dp)
            .testTag("library_screen")
    ) {
        // Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 14.dp)
        ) {
            Text(
                text = "YOUR COLLECTION",
                style = MaterialTheme.typography.labelSmall.copy(
                    letterSpacing = 1.5.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = BrushedGold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "My Library",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                ),
                color = CreamWhite
            )
            Text(
                text = if (currentUser != null) {
                    "${libraryEntries.size} titles saved • ${bookmarks.size} bookmarks (${currentUser!!.username}'s account)"
                } else {
                    "${libraryEntries.size} titles saved • ${bookmarks.size} bookmarks (Guest)"
                },
                style = MaterialTheme.typography.bodySmall,
                color = SoftMutedText
            )
        }

        // Filter Tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val tabs = listOf(
                "ALL" to "All (${libraryEntries.size})",
                "BOOK" to "Books",
                "MANGA" to "Manga",
                "AUDIOBOOK" to "Audiobooks",
                "FAVORITES" to "Favorites",
                "BOOKMARKS" to "Bookmarks (${bookmarks.size})"
            )

            tabs.forEach { (key, label) ->
                val isSelected = selectedTab == key
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) BrushedGold.copy(alpha = 0.2f) else ObsidianCard)
                        .border(
                            1.dp,
                            if (isSelected) BrushedGold else ObsidianBorder,
                            RoundedCornerShape(10.dp)
                        )
                        .clickable { selectedTab = key }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                        .testTag("library_tab_$key")
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        ),
                        color = if (isSelected) BrushedGold else CreamWhite
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (selectedTab == "BOOKMARKS") {
            // Bookmarks List
            if (bookmarks.isEmpty()) {
                EmptyLibraryPlaceholder(
                    message = "No bookmarks saved yet. Tap the bookmark icon while reading to save pages and passages.",
                    onAction = { viewModel.navigateTo(ScreenRoute.Discover) },
                    actionLabel = "Discover Books"
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(bookmarks, key = { it.id }) { bookmark ->
                        BookmarkItemCard(
                            bookmark = bookmark,
                            onOpen = {
                                if (bookmark.contentType == "MANGA") {
                                    viewModel.openMangaReader(
                                        mangaId = bookmark.contentId,
                                        chapterIndex = 0,
                                        initialPage = bookmark.pageIndex
                                    )
                                } else {
                                    viewModel.openReader(bookmark.contentId)
                                }
                            },
                            onDelete = { viewModel.deleteBookmark(bookmark.id) }
                        )
                    }
                }
            }
        } else {
            // Filtered Library Entries
            val filtered = when (selectedTab) {
                "BOOK" -> libraryEntries.filter { it.contentType == "BOOK" }
                "MANGA" -> libraryEntries.filter { it.contentType == "MANGA" }
                "AUDIOBOOK" -> libraryEntries.filter { it.contentType == "AUDIOBOOK" }
                "FAVORITES" -> libraryEntries.filter { it.isFavorite }
                else -> libraryEntries
            }

            if (filtered.isEmpty()) {
                EmptyLibraryPlaceholder(
                    message = when (selectedTab) {
                        "MANGA" -> "No manga in your library yet. Explore our 1,000+ legal manga archive!"
                        "FAVORITES" -> "You haven't marked any titles as favorites yet."
                        else -> "Your personal library is empty. Save classics, audiobooks, and manga to enjoy anytime."
                    },
                    onAction = {
                        if (selectedTab == "MANGA") viewModel.navigateTo(ScreenRoute.Manga)
                        else viewModel.navigateTo(ScreenRoute.Discover)
                    },
                    actionLabel = if (selectedTab == "MANGA") "Browse Manga" else "Browse Catalog"
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize().testTag("library_list")
                ) {
                    items(filtered, key = { "${it.contentType}_${it.contentId}" }) { entry ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .border(1.dp, ObsidianBorder, RoundedCornerShape(12.dp))
                                .clickable {
                                    if (entry.contentType == "MANGA") {
                                        viewModel.openMangaDetail(entry.contentId)
                                    } else {
                                        viewModel.openBookDetail(entry.contentId)
                                    }
                                },
                            color = ObsidianCard
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .width(60.dp)
                                        .aspectRatio(0.68f)
                                        .clip(RoundedCornerShape(6.dp))
                                ) {
                                    AsyncImage(
                                        model = entry.coverImageUrl,
                                        contentDescription = entry.title,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }

                                Spacer(modifier = Modifier.width(14.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Surface(
                                            color = BrushedGold.copy(alpha = 0.15f),
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text(
                                                text = entry.contentType,
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontSize = 8.sp,
                                                    fontWeight = FontWeight.Bold
                                                ),
                                                color = BrushedGold,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                            )
                                        }
                                        if (entry.isFavorite) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Icon(
                                                imageVector = Icons.Default.Favorite,
                                                contentDescription = "Favorite",
                                                tint = Color(0xFFFF5252),
                                                modifier = Modifier.size(12.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(2.dp))

                                    Text(
                                        text = entry.title,
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                        color = CreamWhite,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = entry.author,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = SoftMutedText,
                                        maxLines = 1
                                    )
                                }

                                Row {
                                    IconButton(
                                        onClick = {
                                            if (entry.contentType == "MANGA") {
                                                viewModel.openMangaReader(entry.contentId, 0, 0)
                                            } else {
                                                viewModel.openReader(entry.contentId)
                                            }
                                        }
                                    ) {
                                        Icon(
                                            imageVector = if (entry.contentType == "MANGA") Icons.Default.AutoStories else Icons.Default.MenuBook,
                                            contentDescription = "Read",
                                            tint = BrushedGold
                                        )
                                    }

                                    IconButton(
                                        onClick = {
                                            viewModel.toggleSaveContent(
                                                contentId = entry.contentId,
                                                contentType = entry.contentType,
                                                title = entry.title,
                                                author = entry.author,
                                                coverImageUrl = entry.coverImageUrl,
                                                isCurrentlySaved = true
                                            )
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Remove",
                                            tint = SoftMutedText,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BookmarkItemCard(
    bookmark: BookmarkEntity,
    onOpen: () -> Unit,
    onDelete: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .border(1.dp, ObsidianBorder, RoundedCornerShape(10.dp))
            .clickable { onOpen() },
        color = ObsidianCard
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Bookmark,
                contentDescription = null,
                tint = BrushedGold,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = bookmark.chapterTitle,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = CreamWhite
                )
                if (bookmark.snippet.isNotEmpty()) {
                    Text(
                        text = "\"${bookmark.snippet}\"",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                        color = SoftMutedText,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete bookmark",
                    tint = SoftMutedText,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun EmptyLibraryPlaceholder(
    message: String,
    onAction: () -> Unit,
    actionLabel: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(30.dp))
                .background(ObsidianCardElevated),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.MenuBook,
                contentDescription = null,
                tint = BrushedGold,
                modifier = Modifier.size(30.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Your Library is Ready",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = CreamWhite
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = message,
            style = MaterialTheme.typography.bodySmall,
            color = SoftMutedText,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = onAction,
            colors = ButtonDefaults.buttonColors(
                containerColor = BrushedGold,
                contentColor = Color.Black
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(actionLabel, fontWeight = FontWeight.Bold)
        }
    }
}
