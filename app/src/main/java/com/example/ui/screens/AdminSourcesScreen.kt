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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SourceEntity
import com.example.data.model.SyncRunEntity
import com.example.ui.theme.AmberGlow
import com.example.ui.theme.BrushedGold
import com.example.ui.theme.CreamWhite
import com.example.ui.theme.GoldLight
import com.example.ui.theme.ObsidianBackground
import com.example.ui.theme.ObsidianBorder
import com.example.ui.theme.ObsidianCard
import com.example.ui.theme.ObsidianCardElevated
import com.example.ui.theme.SoftMutedText
import com.example.ui.viewmodel.BookVerseViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AdminSourcesScreen(
    viewModel: BookVerseViewModel,
    modifier: Modifier = Modifier
) {
    val sources by viewModel.sources.collectAsState()
    val syncRuns by viewModel.syncRuns.collectAsState()
    val isSyncing by viewModel.isSyncing.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBackground)
            .padding(bottom = 90.dp)
            .testTag("admin_sources_screen")
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
                modifier = Modifier.testTag("admin_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = CreamWhite
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "ADMINISTRATION",
                    style = MaterialTheme.typography.labelSmall.copy(
                        letterSpacing = 2.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = BrushedGold
                )
                Text(
                    text = "Content Ingestion & Sources",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = CreamWhite
                )
            }

            // Global sync button
            IconButton(
                onClick = { viewModel.triggerSync("gutenberg") },
                enabled = !isSyncing
            ) {
                if (isSyncing) {
                    CircularProgressIndicator(
                        color = BrushedGold,
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh",
                        tint = GoldLight
                    )
                }
            }
        }

        LazyColumn(
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                // Ingestion Architecture Explainer Box
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, BrushedGold.copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
                    color = ObsidianCardElevated
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.CloudDownload, contentDescription = null, tint = BrushedGold, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Automated Ingestion Pipeline",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = CreamWhite
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Connects to legitimate public-domain archives: Project Gutenberg, LibriVox, Standard Ebooks, and Open Library. Detects new works, verifies open licenses, deduplicates records, and automatically updates the catalog.",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, lineHeight = 17.sp),
                            color = SoftMutedText
                        )
                    }
                }
            }

            item {
                Text(
                    text = "ACTIVE ARCHIVE SOURCES",
                    style = MaterialTheme.typography.labelSmall.copy(
                        letterSpacing = 1.2.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = BrushedGold
                )
            }

            // Sources List
            items(sources, key = { it.sourceId }) { source ->
                SourceCard(
                    source = source,
                    onSyncNow = { viewModel.triggerSync(source.sourceId) },
                    isSyncing = isSyncing
                )
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = null,
                        tint = BrushedGold,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "RECENT SYNCHRONIZATION RUNS",
                        style = MaterialTheme.typography.labelSmall.copy(
                            letterSpacing = 1.2.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = BrushedGold
                    )
                }
            }

            // Historical Sync Runs
            if (syncRuns.isEmpty()) {
                item {
                    Text(
                        text = "No sync runs recorded yet. Tap 'Sync Now' on any source to initiate ingestion.",
                        style = MaterialTheme.typography.bodySmall,
                        color = SoftMutedText
                    )
                }
            } else {
                items(syncRuns, key = { it.id }) { run ->
                    SyncRunItemCard(run)
                }
            }
        }
    }
}

@Composable
fun SourceCard(
    source: SourceEntity,
    onSyncNow: () -> Unit,
    isSyncing: Boolean
) {
    val dateFormat = SimpleDateFormat("MMM d, yyyy • h:mm a", Locale.getDefault())
    val lastSyncStr = if (source.lastSyncTimestamp > 0) {
        dateFormat.format(Date(source.lastSyncTimestamp))
    } else "Never"

    val nextSyncStr = source.nextSyncEstimate

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, ObsidianBorder, RoundedCornerShape(12.dp))
            .testTag("admin_source_card_${source.sourceId}"),
        color = ObsidianCard
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Name & Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = source.name,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = CreamWhite
                    )
                    Text(
                        text = source.url,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = SoftMutedText
                    )
                }

                // Status Indicator
                val (statusBg, statusColor) = when (source.status.uppercase()) {
                    "ONLINE" -> Color(0x264CAF50) to Color(0xFF81C784)
                    "SYNCING" -> Color(0x33FFB300) to AmberGlow
                    else -> Color(0x26F44336) to Color(0xFFE57373)
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(statusBg)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = source.status.uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = statusColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Metrics Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MetricColumn(label = "ITEMS IMPORTED", value = source.itemsImported.toString())
                MetricColumn(label = "NEW ITEMS", value = "+${source.newItemsLastSync}")
                MetricColumn(label = "FAILED ITEMS", value = source.failedItems.toString())
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("LAST SYNC", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), color = SoftMutedText)
                    Text(lastSyncStr, style = MaterialTheme.typography.bodySmall, color = CreamWhite)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("NEXT SYNC", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), color = SoftMutedText)
                    Text(nextSyncStr, style = MaterialTheme.typography.bodySmall, color = SoftMutedText)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // "Sync Now" Button
            Button(
                onClick = onSyncNow,
                enabled = !isSyncing,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .testTag("sync_now_${source.sourceId}"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BrushedGold,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Sync,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Sync Now", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }
    }
}

@Composable
fun MetricColumn(label: String, value: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, letterSpacing = 0.5.sp),
            color = SoftMutedText
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = CreamWhite
        )
    }
}

@Composable
fun SyncRunItemCard(run: SyncRunEntity) {
    val dateFormat = SimpleDateFormat("MMM d, h:mm a", Locale.getDefault())
    val isSuccess = run.status == "SUCCESS"

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, ObsidianBorder, RoundedCornerShape(8.dp)),
        color = ObsidianCard
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (isSuccess) Icons.Default.CheckCircle else Icons.Default.Error,
                contentDescription = null,
                tint = if (isSuccess) Color(0xFF81C784) else Color(0xFFE57373),
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = run.sourceId.replace("_", " ").uppercase(),
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = CreamWhite
                    )
                    Text(
                        text = dateFormat.format(Date(run.timestamp)),
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = SoftMutedText
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = run.message,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                    color = SoftMutedText
                )
            }
        }
    }
}
