package com.example.ui.screens

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
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
import kotlinx.coroutines.launch

@Composable
fun BookDetailScreen(
    bookId: String,
    viewModel: BookVerseViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val bookState by viewModel.repository.getBookById(bookId).collectAsState(initial = null)
    val libraryEntries by viewModel.userLibraryEntries.collectAsState()
    val isSaved = libraryEntries.any { it.contentId == bookId }
    val trendingBooks by viewModel.trendingBooks.collectAsState()

    val book = bookState

    if (book == null) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(ObsidianBackground),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = BrushedGold)
        }
        return
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBackground)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 90.dp)
            .testTag("book_detail_screen")
    ) {
        // Top App Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { viewModel.navigateBack() },
                modifier = Modifier.testTag("detail_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = CreamWhite
                )
            }

            Text(
                text = "BOOK OVERVIEW",
                style = MaterialTheme.typography.labelSmall.copy(
                    letterSpacing = 2.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = BrushedGold
            )

            // Save Toggle
            IconButton(
                onClick = {
                    viewModel.toggleSaveContent(
                        contentId = book.id,
                        contentType = "BOOK",
                        title = book.title,
                        author = book.author,
                        coverImageUrl = book.coverImageUrl,
                        isCurrentlySaved = isSaved
                    )
                },
                modifier = Modifier.testTag("save_book_button")
            ) {
                Icon(
                    imageVector = if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                    contentDescription = if (isSaved) "Saved" else "Save",
                    tint = if (isSaved) BrushedGold else CreamWhite
                )
            }
        }

        // Cover & Main Presentation
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .width(140.dp)
                    .aspectRatio(0.66f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(ObsidianCardElevated)
                    .border(1.5.dp, BrushedGold.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                    .shadow(16.dp, RoundedCornerShape(12.dp))
            ) {
                AsyncImage(
                    model = book.coverImageUrl,
                    contentDescription = book.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = book.title,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                ),
                color = CreamWhite,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            if (!book.subtitle.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = book.subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = SoftMutedText,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "By ${book.author}",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = GoldLight
            )

            if (!book.narrator.isNullOrEmpty()) {
                Text(
                    text = "Narrated by ${book.narrator}",
                    style = MaterialTheme.typography.bodySmall,
                    color = SoftMutedText
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Genre and metadata chips
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                book.genres.split(",").take(3).forEach { g ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(ObsidianCardElevated)
                            .border(0.5.dp, ObsidianBorder, RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = g.trim(),
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                            color = CreamWhite
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Action Buttons Row (Read, Listen)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = { viewModel.openReader(book.id) },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("detail_read_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BrushedGold,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(imageVector = Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Read Ebook", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }

            if (book.hasAudiobook) {
                OutlinedButton(
                    onClick = { viewModel.startAudiobook(book) },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("detail_listen_button"),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = GoldLight
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BrushedGold),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.Headphones, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Listen", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Synopsis Section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            Text(
                text = "SYNOPSIS",
                style = MaterialTheme.typography.labelSmall.copy(
                    letterSpacing = 1.5.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = BrushedGold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = book.description,
                style = MaterialTheme.typography.bodyMedium.copy(
                    lineHeight = 22.sp,
                    fontSize = 14.sp
                ),
                color = CreamWhite
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Book Specifications & Source Integrity
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .clip(RoundedCornerShape(12.dp))
                .border(1.dp, ObsidianBorder, RoundedCornerShape(12.dp)),
            color = ObsidianCard
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "SPECIFICATIONS & RIGHTS",
                    style = MaterialTheme.typography.labelSmall.copy(
                        letterSpacing = 1.2.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = BrushedGold
                )
                Spacer(modifier = Modifier.height(10.dp))

                SpecRow(label = "Source Archive", value = book.source)
                SpecRow(label = "Publication Year", value = book.publicationYear.toString())
                SpecRow(label = "Language", value = book.language)
                SpecRow(label = "License", value = book.licenseInfo)
                SpecRow(label = "Public Domain Verified", value = "Yes (United States & Open)")

                Spacer(modifier = Modifier.height(8.dp))

                // External Source Link
                Row(
                    modifier = Modifier
                        .clickable {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(book.sourceUrl))
                            context.startActivity(intent)
                        }
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.OpenInNew,
                        contentDescription = null,
                        tint = GoldLight,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "View Archive Record on ${book.source}",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                        color = GoldLight
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Related Works Section
        Text(
            text = "YOU MAY ALSO ENJOY",
            style = MaterialTheme.typography.labelSmall.copy(
                letterSpacing = 1.5.sp,
                fontWeight = FontWeight.Bold
            ),
            color = BrushedGold,
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        LazyRow(
            contentPadding = PaddingValues(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(trendingBooks.filter { it.id != book.id }.take(8), key = { it.id }) { related ->
                BookCoverCard(
                    book = related,
                    onClick = { viewModel.openBookDetail(related.id) },
                    width = 120.dp
                )
            }
        }
    }
}

@Composable
fun SpecRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = SoftMutedText)
        Text(text = value, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium), color = CreamWhite)
    }
}
