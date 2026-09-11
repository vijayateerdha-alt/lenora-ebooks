package com.example.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "user_library",
    primaryKeys = ["userId", "contentId", "contentType"],
    indices = [
        Index(value = ["userId"]),
        Index(value = ["contentType"])
    ]
)
data class UserLibraryEntity(
    val userId: String,
    val contentId: String,
    val contentType: String = "BOOK", // "BOOK", "MANGA", "AUDIOBOOK"
    val title: String = "",
    val author: String = "",
    val coverImageUrl: String = "",
    val status: String = "READING", // "READING", "FINISHED", "SAVED"
    val savedAt: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false
)

@Entity(
    tableName = "reading_progress",
    primaryKeys = ["userId", "bookId"],
    indices = [
        Index(value = ["userId"]),
        Index(value = ["lastReadTimestamp"])
    ]
)
data class ReadingProgressEntity(
    val userId: String,
    val bookId: String,
    val title: String = "",
    val author: String = "",
    val coverImageUrl: String = "",
    val currentChapter: Int = 0,
    val scrollProgress: Float = 0f, // 0.0 to 1.0
    val lastReadTimestamp: Long = System.currentTimeMillis(),
    val completed: Boolean = false
)

@Entity(
    tableName = "listening_progress",
    primaryKeys = ["userId", "bookId"],
    indices = [
        Index(value = ["userId"]),
        Index(value = ["lastListenedTimestamp"])
    ]
)
data class ListeningProgressEntity(
    val userId: String,
    val bookId: String,
    val title: String = "",
    val author: String = "",
    val coverImageUrl: String = "",
    val currentPositionMs: Long = 0L,
    val durationMs: Long = 0L,
    val currentChapter: Int = 0,
    val lastListenedTimestamp: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "manga_progress",
    primaryKeys = ["userId", "mangaId"],
    indices = [
        Index(value = ["userId"]),
        Index(value = ["lastReadTimestamp"])
    ]
)
data class MangaProgressEntity(
    val userId: String,
    val mangaId: String,
    val title: String = "",
    val coverImageUrl: String = "",
    val chapterIndex: Int = 0,
    val chapterTitle: String = "Chapter 1",
    val pageIndex: Int = 0,
    val totalPages: Int = 12,
    val scrollProgress: Float = 0f,
    val lastReadTimestamp: Long = System.currentTimeMillis(),
    val completed: Boolean = false
)

@Entity(
    tableName = "bookmarks",
    indices = [
        Index(value = ["userId"]),
        Index(value = ["contentId"])
    ]
)
data class BookmarkEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val userId: String = "guest",
    val contentId: String,
    val contentType: String = "BOOK", // "BOOK", "MANGA"
    val chapterIndex: Int = 0,
    val chapterTitle: String = "",
    val pageIndex: Int = 0,
    val snippet: String = "",
    val note: String? = null,
    val progressPercent: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "reading_history",
    indices = [
        Index(value = ["userId"]),
        Index(value = ["timestamp"])
    ]
)
data class ReadingHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val userId: String,
    val contentId: String,
    val contentType: String = "BOOK", // "BOOK", "MANGA", "AUDIOBOOK"
    val title: String,
    val subtitle: String = "",
    val coverImageUrl: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "sources")
data class SourceEntity(
    @PrimaryKey val sourceId: String,
    val name: String,
    val url: String,
    val enabled: Boolean = true,
    val status: String = "ONLINE", // "ONLINE", "SYNCING", "IDLE", "ERROR"
    val lastSyncTimestamp: Long = System.currentTimeMillis(),
    val itemsImported: Int = 0,
    val newItemsLastSync: Int = 0,
    val failedItems: Int = 0,
    val nextSyncEstimate: String = "In 4 hours",
    val termsUrl: String = "",
    val licenseType: String = "Public Domain (US / CC0)"
)

@Entity(tableName = "sync_runs")
data class SyncRunEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val sourceId: String,
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "SUCCESS", // "SUCCESS", "PARTIAL", "FAILED"
    val itemsFetched: Int = 0,
    val itemsInserted: Int = 0,
    val duplicatesSkipped: Int = 0,
    val message: String = ""
)

@Entity(tableName = "user_preferences")
data class UserPreferencesEntity(
    @PrimaryKey val id: Int = 1,
    val readerFontSize: Int = 18,
    val readerLineSpacing: Float = 1.6f,
    val readerTheme: String = "DARK", // "DARK", "LIGHT", "SEPIA", "OLED"
    val audioPlaybackSpeed: Float = 1.0f,
    val displayName: String = "Reader & Listener",
    val avatarIndex: Int = 0,
    val preferredGenres: String = "Classics, Sci-Fi, Mystery, Manga"
)
