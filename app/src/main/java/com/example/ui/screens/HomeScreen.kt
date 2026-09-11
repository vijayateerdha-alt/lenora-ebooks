package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.data.model.BookEntity
import com.example.data.model.ListeningProgressEntity
import com.example.data.model.MangaProgressEntity
import com.example.data.model.ReadingProgressEntity
import com.example.ui.components.BookCoverCard
import com.example.ui.components.ContinueMangaCard
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
import com.example.ui.viewmodel.ScreenRoute

@Composable
fun HomeScreen(viewModel: BookVerseViewModel) {
    val featuredBooks by viewModel.featuredBooks.collectAsState()
    val trendingBooks by viewModel.trendingBooks.collectAsState()
    val audiobooks by viewModel.audiobooks.collectAsState()
    val recentlyAdded by viewModel.recentlyAddedBooks.collectAsState()
    val totalBooks by viewModel.totalBooksCount.collectAsState()
    val totalManga by viewModel.totalMangaCount.collectAsState()

    val trendingManga by viewModel.trendingManga.collectAsState()
    val popularManga by viewModel.popularManga.collectAsState()

    val continueBooks by viewModel.continueReadingBooks.collectAsState()
    val continueListening by viewModel.continueListeningBooks.collectAsState()
    val continueManga by viewModel.continueReadingManga.collectAsState()

    val currentUser by viewModel.currentUser.collectAsState()
    val isSyncing by viewModel.isSyncing.collectAsState()

    val featuredBook = featuredBooks.firstOrNull() ?: trendingBooks.firstOrNull()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBackground)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 90.dp)
            .testTag("home_screen")
    ) {
        // TOP APP BAR
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Gold insignia logo
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(BrushedGold, GoldMuted)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.MenuBook,
                        contentDescription = "BooksVerse Logo",
                        tint = Color.Black,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = "BOOKSVERSE",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 2.sp,
                            fontSize = 17.sp
                        ),
                        color = CreamWhite
                    )
                    Text(
                        text = "Read. Listen. Manga.",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = BrushedGold
                    )
                }
            }

            // Top action shortcuts (Credits, Search, Auth/Profile)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Credits icon button
                IconButton(
                    onClick = { viewModel.openCredits() },
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(ObsidianCard)
                        .border(1.dp, ObsidianBorder, CircleShape)
                        .testTag("home_credits_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Credits",
                        tint = SoftMutedText,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Search icon button
                IconButton(
                    onClick = { viewModel.navigateTo(ScreenRoute.Search) },
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(ObsidianCard)
                        .border(1.dp, ObsidianBorder, CircleShape)
                        .testTag("home_search_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = SoftMutedText,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Auth status / Profile button
                Surface(
                    color = if (currentUser != null) BrushedGold.copy(alpha = 0.2f) else ObsidianCard,
                    shape = RoundedCornerShape(18.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (currentUser != null) BrushedGold else ObsidianBorder
                    ),
                    modifier = Modifier
                        .clickable {
                            if (currentUser != null) {
                                viewModel.navigateTo(ScreenRoute.Profile)
                            } else {
                                viewModel.openAuthDialog()
                            }
                        }
                        .testTag("home_auth_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = if (currentUser != null) BrushedGold else SoftMutedText,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = currentUser?.username?.take(8) ?: "Sign In",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = if (currentUser != null) CreamWhite else SoftMutedText
                        )
                    }
                }
            }
        }

        // HERO FEATURED MASTERWORK CARD
        if (featuredBook != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(ObsidianCardElevated)
                    .border(1.dp, BrushedGold.copy(alpha = 0.35f), RoundedCornerShape(16.dp))
                    .clickable { viewModel.openBookDetail(featuredBook.id) }
                    .testTag("featured_hero_card")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Cover with gold border
                    Box(
                        modifier = Modifier
                            .width(88.dp)
                            .aspectRatio(0.67f)
                            .clip(RoundedCornerShape(10.dp))
                            .border(1.dp, BrushedGold.copy(alpha = 0.7f), RoundedCornerShape(10.dp))
                    ) {
                        AsyncImage(
                            model = featuredBook.coverImageUrl,
                            contentDescription = featuredBook.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = AmberGlow,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "FEATURED MASTERWORK",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    letterSpacing = 1.2.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                ),
                                color = AmberGlow
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = featuredBook.title,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            ),
                            color = CreamWhite,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )

                        Text(
                            text = featuredBook.author,
                            style = MaterialTheme.typography.bodySmall,
                            color = GoldLight,
                            maxLines = 1
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            // Read Button
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(BrushedGold)
                                    .clickable { viewModel.openReader(featuredBook.id) }
                                    .padding(horizontal = 14.dp, vertical = 6.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.MenuBook,
                                        contentDescription = null,
                                        tint = Color.Black,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Read Now",
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                        color = Color.Black
                                    )
                                }
                            }

                            // Listen Button
                            if (featuredBook.hasAudiobook) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(ObsidianBackground)
                                        .border(1.dp, BrushedGold, RoundedCornerShape(16.dp))
                                        .clickable { viewModel.startAudiobook(featuredBook) }
                                        .padding(horizontal = 14.dp, vertical = 6.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Headphones,
                                            contentDescription = null,
                                            tint = GoldLight,
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Listen",
                                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                                            color = GoldLight
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // PLATFORM STATS STRIP
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(ObsidianCard)
                .border(1.dp, ObsidianBorder, RoundedCornerShape(12.dp))
                .padding(horizontal = 14.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "CATALOG STATS",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 9.sp),
                    color = SoftMutedText
                )
                Text(
                    text = "$totalBooks+ Ebooks & Audiobooks • $totalManga+ Manga Series",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                    color = CreamWhite
                )
            }
            Text(
                text = "100% Legal & Free",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                color = BrushedGold
            )
        }

        // CONTINUE READING (Books & Manga)
        if (continueBooks.isNotEmpty() || continueManga.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            SectionHeader(
                title = "CONTINUE READING",
                onViewAll = { viewModel.navigateTo(ScreenRoute.Library) }
            )

            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Ebook Progress cards
                items(continueBooks, key = { "book_${it.bookId}" }) { progress ->
                    ContinueBookCard(
                        progress = progress,
                        onResume = { viewModel.openReader(progress.bookId) }
                    )
                }

                // Manga Progress cards
                items(continueManga, key = { "manga_${it.mangaId}" }) { mProgress ->
                    ContinueMangaCard(
                        progress = mProgress,
                        onResume = {
                            viewModel.openMangaReader(
                                mangaId = mProgress.mangaId,
                                chapterIndex = mProgress.chapterIndex,
                                initialPage = mProgress.pageIndex
                            )
                        }
                    )
                }
            }
        }

        // CONTINUE LISTENING (Audiobooks)
        if (continueListening.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            SectionHeader(
                title = "CONTINUE LISTENING",
                onViewAll = { viewModel.navigateTo(ScreenRoute.Library) }
            )

            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(continueListening, key = { "audio_${it.bookId}" }) { audioProgress ->
                    ContinueListeningCard(
                        progress = audioProgress,
                        onResume = {
                            viewModel.playAudiobookById(audioProgress.bookId)
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // TRENDING MANGA
        SectionHeader(
            title = "TRENDING MANGA",
            onViewAll = { viewModel.navigateTo(ScreenRoute.Manga) }
        )
        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(trendingManga.take(10), key = { "tr_manga_${it.id}" }) { manga ->
                MangaCoverCard(
                    manga = manga,
                    onClick = { viewModel.openMangaDetail(manga.id) }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // TRENDING BOOKS
        SectionHeader(
            title = "TRENDING BOOKS",
            onViewAll = { viewModel.navigateTo(ScreenRoute.Discover) }
        )
        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(trendingBooks.take(10), key = { "tr_book_${it.id}" }) { book ->
                BookCoverCard(
                    book = book,
                    onClick = { viewModel.openBookDetail(book.id) },
                    width = 130.dp
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // POPULAR MANGA
        SectionHeader(
            title = "POPULAR MANGA",
            onViewAll = { viewModel.navigateTo(ScreenRoute.Manga) }
        )
        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(popularManga.take(10), key = { "pop_manga_${it.id}" }) { manga ->
                MangaCoverCard(
                    manga = manga,
                    onClick = { viewModel.openMangaDetail(manga.id) }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // POPULAR AUDIOBOOKS
        SectionHeader(
            title = "POPULAR AUDIOBOOKS",
            onViewAll = {
                viewModel.searchFilterAudio.value = true
                viewModel.navigateTo(ScreenRoute.Discover)
            }
        )
        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(audiobooks.take(10), key = { "pop_audio_${it.id}" }) { book ->
                BookCoverCard(
                    book = book,
                    onClick = { viewModel.openBookDetail(book.id) },
                    width = 130.dp
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // RECENTLY ADDED BOOKS
        SectionHeader(
            title = "RECENTLY ADDED RELEASES",
            onViewAll = { viewModel.navigateTo(ScreenRoute.Discover) }
        )
        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(recentlyAdded.take(10), key = { "rec_${it.id}" }) { book ->
                BookCoverCard(
                    book = book,
                    onClick = { viewModel.openBookDetail(book.id) },
                    width = 130.dp
                )
            }
        }
    }
}

@Composable
fun ContinueBookCard(
    progress: ReadingProgressEntity,
    onResume: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(220.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onResume() }
            .testTag("continue_book_${progress.bookId}"),
        colors = CardDefaults.cardColors(containerColor = ObsidianCard),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = progress.coverImageUrl,
                contentDescription = progress.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(52.dp, 72.dp)
                    .clip(RoundedCornerShape(8.dp))
            )

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = progress.title,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = CreamWhite,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Chapter ${progress.currentChapter + 1}",
                    style = MaterialTheme.typography.labelSmall,
                    color = GoldLight,
                    maxLines = 1
                )
                Text(
                    text = "${(progress.scrollProgress * 100).toInt()}% completed",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = SoftMutedText
                )

                Spacer(modifier = Modifier.height(6.dp))

                LinearProgressIndicator(
                    progress = { progress.scrollProgress.coerceIn(0f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = BrushedGold,
                    trackColor = ObsidianBorder
                )
            }
        }
    }
}

@Composable
fun ContinueListeningCard(
    progress: ListeningProgressEntity,
    onResume: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(220.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onResume() }
            .testTag("continue_listening_${progress.bookId}"),
        colors = CardDefaults.cardColors(containerColor = ObsidianCard),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp, 72.dp)
                    .clip(RoundedCornerShape(8.dp))
            ) {
                AsyncImage(
                    model = progress.coverImageUrl,
                    contentDescription = progress.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(BrushedGold)
                        .align(Alignment.Center),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = progress.title,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = CreamWhite,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = progress.author,
                    style = MaterialTheme.typography.labelSmall,
                    color = GoldLight,
                    maxLines = 1
                )
                val curMins = (progress.currentPositionMs / 60_000L)
                val durMins = (progress.durationMs / 60_000L).coerceAtLeast(1)
                Text(
                    text = "$curMins min of $durMins min",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = SoftMutedText
                )

                Spacer(modifier = Modifier.height(6.dp))

                val progressFrac = if (progress.durationMs > 0) progress.currentPositionMs.toFloat() / progress.durationMs else 0f
                LinearProgressIndicator(
                    progress = { progressFrac.coerceIn(0f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = AmberGlow,
                    trackColor = ObsidianBorder
                )
            }
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    onViewAll: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                fontSize = 15.sp
            ),
            color = CreamWhite
        )

        Row(
            modifier = Modifier
                .clickable { onViewAll() }
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "View all",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                color = BrushedGold
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = BrushedGold,
                modifier = Modifier.size(13.dp)
            )
        }
    }
}
