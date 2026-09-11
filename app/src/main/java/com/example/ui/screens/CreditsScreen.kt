package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
fun CreditsScreen(viewModel: BookVerseViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBackground)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 90.dp)
            .testTag("credits_screen")
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { viewModel.navigateBack() },
                modifier = Modifier.testTag("credits_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = CreamWhite
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "CREDITS & CONTRIBUTORS",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    ),
                    color = CreamWhite
                )
                Text(
                    text = "The creators & foundations behind BooksVerse",
                    style = MaterialTheme.typography.bodySmall,
                    color = SoftMutedText
                )
            }
        }

        // Hero Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color(0xFF2C2416), ObsidianCardElevated)
                    )
                )
                .border(1.dp, BrushedGold.copy(alpha = 0.6f), RoundedCornerShape(20.dp))
                .padding(24.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(BrushedGold),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.MenuBook,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "BooksVerse",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black),
                    color = CreamWhite
                )

                Text(
                    text = "Digital Reading, Listening & Manga Platform",
                    style = MaterialTheme.typography.bodyMedium,
                    color = BrushedGold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Dedicated to universal access to world literature, audio storytelling, and open manga artwork.",
                    style = MaterialTheme.typography.bodySmall,
                    color = SoftMutedText,
                    lineHeight = 18.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Lead Architects & Creators
        Text(
            text = "PLATFORM CREATORS & LEAD ARCHITECTS",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
            color = GoldLight,
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Creator: Bhuvan
        CreatorCard(
            name = "Bhuvan",
            role = "Lead Architect & Developer",
            description = "Conceptualized and engineered the BooksVerse platform, core reader systems, offline audio streaming synchronization, and manga digital reading engine.",
            highlightTag = "PROJECT LEAD"
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Creator: Sravan
        CreatorCard(
            name = "Sravan",
            role = "Lead Architect & System Designer",
            description = "Architected high-performance SQLite database catalogs supporting 1,000+ titles, user data isolation, and the Obsidian & Brushed Gold design system.",
            highlightTag = "SYSTEM ARCHITECT"
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Open Source & Public Domain Acknowledgements
        Text(
            text = "OPEN ARCHIVES & LICENSED PROVIDERS",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
            color = GoldLight,
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        ArchiveCreditCard(
            title = "Project Gutenberg",
            subtitle = "Over 70,000 free eBooks in the public domain",
            license = "Public Domain (US / CC0)"
        )

        Spacer(modifier = Modifier.height(8.dp))

        ArchiveCreditCard(
            title = "LibriVox Audiobooks",
            subtitle = "Acoustical liberation of books in the public domain recorded by volunteers",
            license = "Public Domain Dedication"
        )

        Spacer(modifier = Modifier.height(8.dp))

        ArchiveCreditCard(
            title = "Standard Ebooks",
            subtitle = "Carefully formatted, beautiful digital editions of timeless literature",
            license = "Public Domain / CC0 1.0"
        )

        Spacer(modifier = Modifier.height(8.dp))

        ArchiveCreditCard(
            title = "Open Manga & Creative Commons Comic Archives",
            subtitle = "Open source serializations including Pepper & Carrot (David Revoy) and classic Japanese ukiyo-e restorations",
            license = "Creative Commons Attribution 4.0 / CC0"
        )
    }
}

@Composable
fun CreatorCard(
    name: String,
    role: String,
    description: String,
    highlightTag: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .border(1.dp, BrushedGold.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
            .testTag("creator_card_${name.lowercase()}"),
        colors = CardDefaults.cardColors(containerColor = ObsidianCardElevated),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(BrushedGold),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = name,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = CreamWhite
                    )
                    Surface(
                        color = BrushedGold.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(4.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BrushedGold)
                    ) {
                        Text(
                            text = highlightTag,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black, fontSize = 9.sp),
                            color = BrushedGold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Text(
                    text = role,
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                    color = GoldLight
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = SoftMutedText,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

@Composable
fun ArchiveCreditCard(
    title: String,
    subtitle: String,
    license: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        colors = CardDefaults.cardColors(containerColor = ObsidianCard),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = CreamWhite
                )
                Text(
                    text = license,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = BrushedGold
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = SoftMutedText
            )
        }
    }
}
