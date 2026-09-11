package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.MangaChapterEntity
import com.example.data.model.MangaEntity
import com.example.data.model.MangaProgressEntity
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MangaDetailScreen(
    mangaId: String,
    viewModel: BookVerseViewModel
) {
    var manga by remember { mutableStateOf<MangaEntity?>(null) }
    var chapters by remember { mutableStateOf<List<MangaChapterEntity>>(emptyList()) }

    val userLibrary by viewModel.userLibraryEntries.collectAsState()
    val isSaved = userLibrary.any { it.contentId == mangaId && it.contentType == "MANGA" }
    val isFavorite = userLibrary.any { it.contentId == mangaId && it.contentType == "MANGA" && it.isFavorite }

    LaunchedEffect(mangaId, viewModel.currentUserId) {
        manga = viewModel.repository.getMangaByIdDirect(mangaId)
        chapters = viewModel.repository.getChaptersForMangaDirect(mangaId)
    }

    // Reactively observe progress
    val effectiveProgressState by viewModel.repository.getMangaProgress(viewModel.currentUserId, mangaId).collectAsState(null)
    val effectiveProgress = effectiveProgressState

    val currentManga = manga

    if (currentManga == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(ObsidianBackground),
            contentAlignment = Alignment.Center
        ) {
            Text("Loading series details...", color = SoftMutedText)
        }
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBackground)
            .testTag("manga_detail_screen")
    ) {
        // Hero Banner with Back Button
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
            ) {
                AsyncImage(
                    model = currentManga.coverImageUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(0x99000000),
                                    Color(0xEE08080A),
                                    ObsidianBackground
                                )
                            )
                        )
                )

                // Top Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .align(Alignment.TopCenter),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { viewModel.navigateBack() },
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Color(0x88000000))
                            .testTag("manga_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = CreamWhite
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        // Favorite toggle
                        IconButton(
                            onClick = {
                                viewModel.toggleFavoriteContent(
                                    contentId = currentManga.id,
                                    contentType = "MANGA",
                                    title = currentManga.title,
                                    author = currentManga.author,
                                    coverImageUrl = currentManga.coverImageUrl,
                                    isCurrentlyFavorite = isFavorite
                                )
                            },
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(Color(0x88000000))
                                .testTag("manga_favorite_button")
                        ) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Favorite",
                                tint = if (isFavorite) Color(0xFFFF5252) else CreamWhite
                            )
                        }

                        // Bookmark / Library toggle
                        IconButton(
                            onClick = {
                                viewModel.toggleSaveContent(
                                    contentId = currentManga.id,
                                    contentType = "MANGA",
                                    title = currentManga.title,
                                    author = currentManga.author,
                                    coverImageUrl = currentManga.coverImageUrl,
                                    isCurrentlySaved = isSaved
                                )
                            },
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(Color(0x88000000))
                                .testTag("manga_bookmark_button")
                        ) {
                            Icon(
                                imageVector = if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = "Save to Library",
                                tint = if (isSaved) BrushedGold else CreamWhite
                            )
                        }
                    }
                }

                // Poster and Meta Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .align(Alignment.BottomStart),
                    verticalAlignment = Alignment.Bottom
                ) {
                    AsyncImage(
                        model = currentManga.coverImageUrl,
                        contentDescription = currentManga.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .width(110.dp)
                            .aspectRatio(0.7f)
                            .clip(RoundedCornerShape(12.dp))
                            .border(2.dp, BrushedGold, RoundedCornerShape(12.dp))
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Surface(
                            color = if (currentManga.status == "Completed") Color(0xFF1B4332) else Color(0xFF5C2C16),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = currentManga.status.uppercase(),
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 9.sp),
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = currentManga.title,
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = CreamWhite,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )

                        if (!currentManga.altTitle.isNullOrEmpty()) {
                            Text(
                                text = currentManga.altTitle,
                                style = MaterialTheme.typography.bodySmall,
                                color = GoldLight
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = AmberGlow, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${currentManga.rating} • ${currentManga.releaseYear} • ${currentManga.totalChapters} Chs",
                                style = MaterialTheme.typography.labelSmall,
                                color = SoftMutedText
                            )
                        }
                    }
                }
            }
        }

        // Details Body
        item {
            Column(modifier = Modifier.padding(20.dp)) {
                // Author & Artist
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("AUTHOR", style = MaterialTheme.typography.labelSmall, color = SoftMutedText)
                        Text(currentManga.author, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold), color = CreamWhite)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("ARTIST", style = MaterialTheme.typography.labelSmall, color = SoftMutedText)
                        Text(currentManga.artist, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold), color = CreamWhite)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Genres Chips
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    currentManga.genres.split(",").forEach { genre ->
                        Surface(
                            color = ObsidianCard,
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder)
                        ) {
                            Text(
                                text = genre.trim(),
                                style = MaterialTheme.typography.labelSmall,
                                color = GoldLight,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // User Progress Notice if exists
                if (effectiveProgress != null) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, BrushedGold.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            .testTag("manga_reading_progress_card"),
                        colors = CardDefaults.cardColors(containerColor = ObsidianCardElevated),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoStories,
                                contentDescription = null,
                                tint = BrushedGold,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Your Reading Progress",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = GoldLight
                                )
                                Text(
                                    text = "${effectiveProgress.chapterTitle} • Page ${effectiveProgress.pageIndex + 1} of ${effectiveProgress.totalPages}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = CreamWhite
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                LinearProgressIndicator(
                                    progress = { effectiveProgress.scrollProgress.coerceIn(0f, 1f) },
                                    modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
                                    color = BrushedGold,
                                    trackColor = ObsidianBorder
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Action CTA Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (effectiveProgress != null) {
                        Button(
                            onClick = {
                                val progress = effectiveProgress
                                if (progress != null) {
                                    viewModel.openMangaReader(
                                        mangaId = currentManga.id,
                                        chapterIndex = progress.chapterIndex,
                                        initialPage = progress.pageIndex
                                    )
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = BrushedGold,
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("manga_continue_button")
                        ) {
                            Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Continue (Ch. ${effectiveProgress.chapterIndex + 1})", fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = { viewModel.openMangaReader(currentManga.id, 0, 0) },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = CreamWhite),
                            border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.height(48.dp).testTag("manga_start_over_button")
                        ) {
                            Text("Ch. 1")
                        }
                    } else {
                        Button(
                            onClick = { viewModel.openMangaReader(currentManga.id, 0, 0) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = BrushedGold,
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("manga_start_reading_button")
                        ) {
                            Icon(imageVector = Icons.Default.AutoStories, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Start Reading Chapter 1", fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Synopsis
                Text(
                    text = "SYNOPSIS",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = CreamWhite
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = currentManga.description,
                    style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp),
                    color = SoftMutedText
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Licensing & Source
                Surface(
                    color = ObsidianCard,
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("DISTRIBUTION & LICENSING", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = BrushedGold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${currentManga.source} • ${currentManga.license}. Legally available for digital reading and archiving.",
                            style = MaterialTheme.typography.bodySmall,
                            color = SoftMutedText
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Chapter List Header
                Text(
                    text = "CHAPTERS (${chapters.size.coerceAtLeast(currentManga.totalChapters)})",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = CreamWhite
                )
            }
        }

        // Chapters List
        val displayChapters = if (chapters.isNotEmpty()) chapters else {
            (0 until currentManga.totalChapters.coerceAtMost(10)).map { idx ->
                MangaChapterEntity(
                    mangaId = currentManga.id,
                    chapterIndex = idx,
                    chapterNumber = "Chapter ${idx + 1}",
                    title = "Act ${idx + 1}",
                    pageCount = 12,
                    releaseDate = "${currentManga.releaseYear}"
                )
            }
        }

        itemsIndexed(displayChapters, key = { _, ch -> "${ch.mangaId}_${ch.chapterIndex}" }) { index, chapter ->
            val isCurrentProgress = effectiveProgress?.chapterIndex == chapter.chapterIndex
            val isCompleted = effectiveProgress != null && effectiveProgress.chapterIndex > chapter.chapterIndex

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 4.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .clickable {
                        viewModel.openMangaReader(currentManga.id, chapter.chapterIndex, 0)
                    }
                    .testTag("chapter_item_${chapter.chapterIndex}"),
                colors = CardDefaults.cardColors(
                    containerColor = if (isCurrentProgress) ObsidianCardElevated else ObsidianCard
                ),
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isCurrentProgress) BrushedGold else ObsidianBorder
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(if (isCompleted) Color(0xFF1B4332) else if (isCurrentProgress) BrushedGold else ObsidianBorder),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isCompleted) {
                                Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            } else {
                                Text(
                                    text = "${chapter.chapterIndex + 1}",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = if (isCurrentProgress) Color.Black else CreamWhite
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = chapter.chapterNumber,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = if (isCurrentProgress) BrushedGold else CreamWhite
                            )
                            Text(
                                text = "${chapter.title} • ${chapter.pageCount} pages",
                                style = MaterialTheme.typography.labelSmall,
                                color = SoftMutedText
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Read Chapter",
                        tint = if (isCurrentProgress) BrushedGold else SoftMutedText,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(90.dp))
        }
    }
}
