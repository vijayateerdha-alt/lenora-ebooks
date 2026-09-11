package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.ReadingProgressEntity
import com.example.ui.theme.BrushedGold
import com.example.ui.theme.CreamWhite
import com.example.ui.theme.GoldLight
import com.example.ui.theme.ObsidianBackground
import com.example.ui.theme.ObsidianBorder
import com.example.ui.theme.ObsidianCard
import com.example.ui.theme.ObsidianCardElevated
import com.example.ui.theme.SoftMutedText
import com.example.ui.viewmodel.BookVerseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun ReaderScreen(
    bookId: String,
    viewModel: BookVerseViewModel,
    modifier: Modifier = Modifier
) {
    val bookState by viewModel.repository.getBookById(bookId).collectAsState(initial = null)
    val preferences by viewModel.userPreferences.collectAsState()

    var showControls by remember { mutableStateOf(true) }
    var showFormatDialog by remember { mutableStateOf(false) }
    var currentChapter by remember { mutableIntStateOf(1) }

    val scrollState = rememberScrollState()

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

    // Determine reader palette from preferences
    val (readerBg, readerTextColor) = when (preferences.readerTheme) {
        "SEPIA" -> Color(0xFFF4ECD8) to Color(0xFF433422)
        "OLED" -> Color(0xFF000000) to Color(0xFFE0E0E0)
        "LIGHT" -> Color(0xFFFAFAFA) to Color(0xFF1A1A1A)
        else -> Color(0xFF0D0C10) to Color(0xFFF3EEE3) // DARK (Default Obsidian)
    }

    // Compute reading progress
    val progressPercent = if (scrollState.maxValue > 0) {
        ((scrollState.value.toFloat() / scrollState.maxValue.toFloat()) * 100).toInt().coerceIn(0, 100)
    } else 0

    // Auto-save progress
    LaunchedEffect(progressPercent) {
        viewModel.saveReadingProgress(
            bookId = book.id,
            title = book.title,
            author = book.author,
            coverImageUrl = book.coverImageUrl,
            chapter = currentChapter,
            scrollProgress = progressPercent / 100f
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(readerBg)
            .testTag("reader_screen")
    ) {
        // Reader Content Scrollable Area
        Column(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                    indication = null
                ) {
                    showControls = !showControls
                }
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp, vertical = 60.dp)
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // Book & Chapter Header
            Text(
                text = book.title.uppercase(),
                style = MaterialTheme.typography.labelSmall.copy(
                    letterSpacing = 2.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = if (preferences.readerTheme == "SEPIA" || preferences.readerTheme == "LIGHT") Color(0xFF8B6508) else BrushedGold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Chapter $currentChapter",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                ),
                color = readerTextColor
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Reader text body
            val fullContent = generateReaderContent(book, currentChapter)

            Text(
                text = fullContent,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = preferences.readerFontSize.sp,
                    lineHeight = (preferences.readerFontSize * preferences.readerLineSpacing).sp,
                    fontFamily = FontFamily.Serif
                ),
                color = readerTextColor
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Chapter Navigation Footer
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (currentChapter > 1) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (preferences.readerTheme == "SEPIA") Color(0x22000000) else ObsidianCardElevated)
                            .clickable {
                                currentChapter--
                            }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text("← Previous Chapter", color = readerTextColor, style = MaterialTheme.typography.labelMedium)
                    }
                } else {
                    Spacer(modifier = Modifier.width(1.dp))
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (preferences.readerTheme == "SEPIA") Color(0x22000000) else ObsidianCardElevated)
                        .clickable {
                            currentChapter++
                        }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text("Next Chapter →", color = readerTextColor, style = MaterialTheme.typography.labelMedium)
                }
            }

            Spacer(modifier = Modifier.height(80.dp))
        }

        // Top Navigation Bar (Animated Overlay)
        AnimatedVisibility(
            visible = showControls,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.TopCenter)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding(),
                color = if (preferences.readerTheme == "SEPIA") Color(0xF2F4ECD8) else Color(0xF20D0C10),
                tonalElevation = 6.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { viewModel.navigateBack() },
                        modifier = Modifier.testTag("reader_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = readerTextColor
                        )
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = book.title,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = readerTextColor,
                            maxLines = 1
                        )
                        Text(
                            text = "$progressPercent% completed",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = if (preferences.readerTheme == "SEPIA") Color(0xFF6B583E) else SoftMutedText
                        )
                    }

                    // Format Settings
                    IconButton(
                        onClick = { showFormatDialog = true },
                        modifier = Modifier.testTag("reader_settings_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.FormatSize,
                            contentDescription = "Formatting",
                            tint = readerTextColor
                        )
                    }

                    // Bookmark CTA
                    IconButton(
                        onClick = {
                            viewModel.addBookmark(
                                contentId = book.id,
                                contentType = "BOOK",
                                chapterTitle = "Chapter $currentChapter",
                                snippet = book.description.take(120),
                                progress = progressPercent
                            )
                        },
                        modifier = Modifier.testTag("reader_bookmark_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.BookmarkBorder,
                            contentDescription = "Add bookmark",
                            tint = readerTextColor
                        )
                    }

                    // Audiobook shortcut if available
                    if (book.hasAudiobook) {
                        IconButton(onClick = { viewModel.startAudiobook(book) }) {
                            Icon(
                                imageVector = Icons.Default.Headphones,
                                contentDescription = "Listen to Audiobook",
                                tint = BrushedGold
                            )
                        }
                    }
                }
            }
        }

        // Bottom Progress Bar (Animated Overlay)
        AnimatedVisibility(
            visible = showControls,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding(),
                color = if (preferences.readerTheme == "SEPIA") Color(0xF2F4ECD8) else Color(0xF20D0C10)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    LinearProgressIndicator(
                        progress = { progressPercent / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(3.dp),
                        color = BrushedGold,
                        trackColor = Color(0x33000000),
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Chapter $currentChapter of 24",
                            style = MaterialTheme.typography.bodySmall,
                            color = readerTextColor
                        )
                        Text(
                            text = "$progressPercent%",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                            color = BrushedGold
                        )
                    }
                }
            }
        }
    }

    // Reader Typography & Appearance Dialog
    if (showFormatDialog) {
        Dialog(onDismissRequest = { showFormatDialog = false }) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = ObsidianCardElevated,
                border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder),
                modifier = Modifier.padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Reader Preferences",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = CreamWhite
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Font Size Slider
                    Text(
                        text = "Font Size: ${preferences.readerFontSize}sp",
                        style = MaterialTheme.typography.bodySmall,
                        color = SoftMutedText
                    )
                    Slider(
                        value = preferences.readerFontSize.toFloat(),
                        onValueChange = { viewModel.updatePreferences(fontSize = it.toInt()) },
                        valueRange = 14f..26f,
                        steps = 5,
                        colors = SliderDefaults.colors(
                            thumbColor = BrushedGold,
                            activeTrackColor = BrushedGold
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Reading Themes (Dark, Sepia, OLED, Light)
                    Text(
                        text = "Reading Theme",
                        style = MaterialTheme.typography.bodySmall,
                        color = SoftMutedText
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(
                            "DARK" to "Obsidian",
                            "SEPIA" to "Sepia",
                            "OLED" to "OLED",
                            "LIGHT" to "Light"
                        ).forEach { (mode, label) ->
                            val isSel = preferences.readerTheme == mode
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSel) BrushedGold.copy(alpha = 0.2f) else ObsidianCard)
                                    .border(1.dp, if (isSel) BrushedGold else ObsidianBorder, RoundedCornerShape(8.dp))
                                    .clickable { viewModel.updatePreferences(theme = mode) }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = if (isSel) BrushedGold else CreamWhite
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Line Spacing
                    Text(
                        text = "Line Spacing",
                        style = MaterialTheme.typography.bodySmall,
                        color = SoftMutedText
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(
                            1.3f to "Compact",
                            1.6f to "Comfort",
                            1.9f to "Relaxed"
                        ).forEach { (space, label) ->
                            val isSel = preferences.readerLineSpacing == space
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSel) BrushedGold.copy(alpha = 0.2f) else ObsidianCard)
                                    .border(1.dp, if (isSel) BrushedGold else ObsidianBorder, RoundedCornerShape(8.dp))
                                    .clickable { viewModel.updatePreferences(lineSpacing = space) }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = if (isSel) BrushedGold else CreamWhite
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun generateReaderContent(book: com.example.data.model.BookEntity, chapter: Int): String {
    val sample = book.sampleContent
    return if (sample.isNotEmpty() && chapter == 1) {
        sample + "\n\n" + """The wind whispered through the tall archways, carrying the scent of cedar and aged parchment. Every thought seemed clear, elevated by the stillness of the afternoon.

"There are moments in existence," wrote the narrator, "when the path before us reveals itself not through thunder or command, but through the gentle turning of a single page."

As daylight drifted towards dusk, reflections formed in quiet succession. The truth, when once acknowledged, refused to be hidden again. It stood patiently, immutable and bright, awaiting whoever possessed the quiet resolve to comprehend it.

Thus the journey continued, step by step, word by word, into the enduring heart of the narrative."""
    } else {
        """Chapter $chapter: The Unfolding Horizon

The subsequent hours brought both unexpected clarity and quiet resolve. Words spoken in haste earlier in the chronicle found their true weight, as the characters surveyed the terrain of choices before them.

"We must proceed with care," cautioned the companion, pausing beside the lantern-lit hearth. "History remembers not only what was accomplished, but the dignity with which each challenge was met."

Outside, the stars cast their familiar light over the silent expanse. In that tranquil atmosphere, every passage carried an intimate grace, inviting the reader to reflect on enduring themes of honor, wisdom, courage, and redemption.

The narrative moved onward with quiet momentum, promising deeper revelations in the chapters yet to come."""
    }
}
