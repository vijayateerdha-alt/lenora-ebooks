package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.Forward30
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay10
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.VolumeDown
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.player.AudiobookPlayerManager
import com.example.player.PlayerState
import com.example.ui.theme.BrushedGold
import com.example.ui.theme.CreamWhite
import com.example.ui.theme.GoldLight
import com.example.ui.theme.ObsidianBackground
import com.example.ui.theme.ObsidianBorder
import com.example.ui.theme.ObsidianCard
import com.example.ui.theme.ObsidianCardElevated
import com.example.ui.theme.SoftMutedText

@Composable
fun ExpandedAudioPlayerModal(
    playerState: PlayerState,
    playerManager: AudiobookPlayerManager,
    onClose: () -> Unit
) {
    val book = playerState.currentBook ?: return
    var showSpeedDialog by remember { mutableStateOf(false) }
    var showSleepDialog by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
            color = ObsidianBackground
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Action Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onClose,
                        modifier = Modifier.testTag("close_player_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close player",
                            tint = CreamWhite
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "PLAYING AUDIOBOOK",
                            style = MaterialTheme.typography.labelSmall.copy(
                                letterSpacing = 2.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = BrushedGold
                        )
                        Text(
                            text = book.source,
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = SoftMutedText
                        )
                    }

                    // Sleep timer badge
                    IconButton(onClick = { showSleepDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Bedtime,
                            contentDescription = "Sleep timer",
                            tint = if (playerState.sleepTimerMinutesRemaining != null) BrushedGold else SoftMutedText
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Giant Book Cover Artwork
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.72f)
                        .aspectRatio(0.66f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(ObsidianCardElevated)
                        .border(1.5.dp, ObsidianBorder, RoundedCornerShape(16.dp))
                        .shadow(16.dp, RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = book.coverImageUrl,
                        contentDescription = book.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Ambient gradient overlay
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color(0x990D0C10)
                                    )
                                )
                            )
                    )

                    if (playerState.isBuffering) {
                        CircularProgressIndicator(color = BrushedGold, modifier = Modifier.size(42.dp))
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Book Title & Narrator
                Text(
                    text = book.title,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    ),
                    color = CreamWhite,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "By ${book.author}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = GoldLight,
                    textAlign = TextAlign.Center
                )

                if (!book.narrator.isNullOrEmpty()) {
                    Text(
                        text = "Narrated by ${book.narrator}",
                        style = MaterialTheme.typography.bodySmall,
                        color = SoftMutedText,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Progress Slider & Timestamps
                val currentSec = playerState.currentPositionMs / 1000L
                val totalSec = playerState.durationMs / 1000L

                Slider(
                    value = playerState.currentPositionMs.toFloat(),
                    onValueChange = { newPos ->
                        playerManager.seekTo(newPos.toLong())
                    },
                    valueRange = 0f..playerState.durationMs.toFloat().coerceAtLeast(1000f),
                    colors = SliderDefaults.colors(
                        thumbColor = BrushedGold,
                        activeTrackColor = BrushedGold,
                        inactiveTrackColor = ObsidianCardElevated
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = formatTime(currentSec),
                        style = MaterialTheme.typography.bodySmall,
                        color = SoftMutedText
                    )
                    Text(
                        text = "-${formatTime((totalSec - currentSec).coerceAtLeast(0L))}",
                        style = MaterialTheme.typography.bodySmall,
                        color = SoftMutedText
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Playback Controls Row (15s back, prev, Play/Pause, next, 30s forward)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 15s Rewind
                    IconButton(
                        onClick = { playerManager.skipBack15() },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.FastRewind,
                            contentDescription = "Back 15 seconds",
                            tint = CreamWhite,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    // Previous Track
                    IconButton(
                        onClick = { playerManager.seekTo(0L) },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SkipPrevious,
                            contentDescription = "Restart Track",
                            tint = CreamWhite,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    // Main Play/Pause Button
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(BrushedGold)
                            .clickable { playerManager.togglePlayPause() }
                            .testTag("player_play_pause_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (playerState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (playerState.isPlaying) "Pause" else "Play",
                            tint = Color.Black,
                            modifier = Modifier.size(38.dp)
                        )
                    }

                    // Next Chapter
                    IconButton(
                        onClick = { playerManager.skipForward30() },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SkipNext,
                            contentDescription = "Next Chapter",
                            tint = CreamWhite,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    // 30s Forward
                    IconButton(
                        onClick = { playerManager.skipForward30() },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.FastForward,
                            contentDescription = "Forward 30 seconds",
                            tint = CreamWhite,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Bottom Utilities (Speed chip, Sleep timer badge, Volume slider)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Speed Selector Chip
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(ObsidianCardElevated)
                            .border(1.dp, ObsidianBorder, RoundedCornerShape(20.dp))
                            .clickable { showSpeedDialog = true }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Speed,
                                contentDescription = null,
                                tint = BrushedGold,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${playerState.playbackSpeed}x",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = CreamWhite
                            )
                        }
                    }

                    // Sleep Timer Status
                    if (playerState.sleepTimerMinutesRemaining != null) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(BrushedGold.copy(alpha = 0.15f))
                                .border(1.dp, BrushedGold, RoundedCornerShape(20.dp))
                                .clickable { showSleepDialog = true }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "Sleep: ${playerState.sleepTimerMinutesRemaining}m",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = GoldLight
                            )
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(ObsidianCardElevated)
                                .border(1.dp, ObsidianBorder, RoundedCornerShape(20.dp))
                                .clickable { showSleepDialog = true }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "Sleep Timer",
                                style = MaterialTheme.typography.labelMedium,
                                color = SoftMutedText
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Volume slider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.VolumeDown, contentDescription = null, tint = SoftMutedText, modifier = Modifier.size(18.dp))
                    Slider(
                        value = playerState.volume,
                        onValueChange = { playerManager.setVolume(it) },
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp),
                        colors = SliderDefaults.colors(
                            thumbColor = GoldLight,
                            activeTrackColor = GoldLight,
                            inactiveTrackColor = ObsidianBorder
                        )
                    )
                    Icon(imageVector = Icons.Default.VolumeUp, contentDescription = null, tint = SoftMutedText, modifier = Modifier.size(18.dp))
                }
            }
        }
    }

    // Playback Speed Dialog
    if (showSpeedDialog) {
        Dialog(onDismissRequest = { showSpeedDialog = false }) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = ObsidianCardElevated,
                border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder),
                modifier = Modifier.padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Playback Speed",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = CreamWhite
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    listOf(0.75f, 1.0f, 1.25f, 1.5f, 2.0f).forEach { speed ->
                        val isSelected = playerState.playbackSpeed == speed
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) BrushedGold.copy(alpha = 0.15f) else Color.Transparent)
                                .clickable {
                                    playerManager.setPlaybackSpeed(speed)
                                    showSpeedDialog = false
                                }
                                .padding(vertical = 10.dp, horizontal = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${speed}x Normal",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                ),
                                color = if (isSelected) BrushedGold else CreamWhite
                            )
                            if (isSelected) {
                                Text("✓", color = BrushedGold, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }

    // Sleep Timer Dialog
    if (showSleepDialog) {
        Dialog(onDismissRequest = { showSleepDialog = false }) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = ObsidianCardElevated,
                border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder),
                modifier = Modifier.padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Sleep Timer",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = CreamWhite
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    listOf(
                        "Off" to null,
                        "5 minutes" to 5,
                        "15 minutes" to 15,
                        "30 minutes" to 30,
                        "45 minutes" to 45,
                        "60 minutes" to 60
                    ).forEach { (label, minutes) ->
                        val isSelected = playerState.sleepTimerMinutesRemaining == minutes
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) BrushedGold.copy(alpha = 0.15f) else Color.Transparent)
                                .clickable {
                                    playerManager.setSleepTimer(minutes)
                                    showSleepDialog = false
                                }
                                .padding(vertical = 10.dp, horizontal = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                ),
                                color = if (isSelected) BrushedGold else CreamWhite
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun formatTime(seconds: Long): String {
    val mins = seconds / 60
    val secs = seconds % 60
    return if (mins >= 60) {
        val hrs = mins / 60
        val remMins = mins % 60
        String.format("%d:%02d:%02d", hrs, remMins, secs)
    } else {
        String.format("%02d:%02d", mins, secs)
    }
}
