package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.BookmarkEntity
import com.example.data.model.ListeningProgressEntity
import com.example.data.model.MangaProgressEntity
import com.example.data.model.ReadingHistoryEntity
import com.example.data.model.ReadingProgressEntity
import com.example.data.model.SourceEntity
import com.example.data.model.SyncRunEntity
import com.example.data.model.UserLibraryEntity
import com.example.data.model.UserPreferencesEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LibraryDao {
    @Query("SELECT * FROM user_library WHERE userId = :userId ORDER BY savedAt DESC")
    fun getAllLibraryEntries(userId: String): Flow<List<UserLibraryEntity>>

    @Query("SELECT * FROM user_library WHERE userId = :userId AND contentType = :contentType ORDER BY savedAt DESC")
    fun getLibraryEntriesByType(userId: String, contentType: String): Flow<List<UserLibraryEntity>>

    @Query("SELECT * FROM user_library WHERE userId = :userId AND status = :status ORDER BY savedAt DESC")
    fun getLibraryEntriesByStatus(userId: String, status: String): Flow<List<UserLibraryEntity>>

    @Query("SELECT * FROM user_library WHERE userId = :userId AND isFavorite = 1 ORDER BY savedAt DESC")
    fun getFavoriteEntries(userId: String): Flow<List<UserLibraryEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM user_library WHERE userId = :userId AND contentId = :contentId LIMIT 1)")
    fun isContentSaved(userId: String, contentId: String): Flow<Boolean>

    @Query("SELECT isFavorite FROM user_library WHERE userId = :userId AND contentId = :contentId LIMIT 1")
    fun isContentFavorite(userId: String, contentId: String): Flow<Boolean?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveToLibrary(entry: UserLibraryEntity)

    @Query("DELETE FROM user_library WHERE userId = :userId AND contentId = :contentId")
    suspend fun removeFromLibrary(userId: String, contentId: String)

    @Query("UPDATE user_library SET status = :status WHERE userId = :userId AND contentId = :contentId")
    suspend fun updateStatus(userId: String, contentId: String, status: String)

    @Query("UPDATE user_library SET isFavorite = :isFavorite WHERE userId = :userId AND contentId = :contentId")
    suspend fun updateFavorite(userId: String, contentId: String, isFavorite: Boolean)

    @Query("SELECT COUNT(*) FROM user_library WHERE userId = :userId AND status = 'FINISHED'")
    fun getFinishedBooksCount(userId: String): Flow<Int>
}

@Dao
interface ProgressDao {
    @Query("SELECT * FROM reading_progress WHERE userId = :userId AND bookId = :bookId LIMIT 1")
    fun getReadingProgress(userId: String, bookId: String): Flow<ReadingProgressEntity?>

    @Query("SELECT * FROM reading_progress WHERE userId = :userId AND bookId = :bookId LIMIT 1")
    suspend fun getReadingProgressDirect(userId: String, bookId: String): ReadingProgressEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveReadingProgress(progress: ReadingProgressEntity)

    @Query("SELECT * FROM listening_progress WHERE userId = :userId AND bookId = :bookId LIMIT 1")
    fun getListeningProgress(userId: String, bookId: String): Flow<ListeningProgressEntity?>

    @Query("SELECT * FROM listening_progress WHERE userId = :userId AND bookId = :bookId LIMIT 1")
    suspend fun getListeningProgressDirect(userId: String, bookId: String): ListeningProgressEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveListeningProgress(progress: ListeningProgressEntity)

    @Query("SELECT * FROM reading_progress WHERE userId = :userId AND completed = 0 ORDER BY lastReadTimestamp DESC LIMIT 10")
    fun getRecentReadingProgress(userId: String): Flow<List<ReadingProgressEntity>>

    @Query("SELECT * FROM listening_progress WHERE userId = :userId ORDER BY lastListenedTimestamp DESC LIMIT 10")
    fun getRecentListeningProgress(userId: String): Flow<List<ListeningProgressEntity>>

    // Manga progress
    @Query("SELECT * FROM manga_progress WHERE userId = :userId AND mangaId = :mangaId LIMIT 1")
    fun getMangaProgress(userId: String, mangaId: String): Flow<MangaProgressEntity?>

    @Query("SELECT * FROM manga_progress WHERE userId = :userId AND mangaId = :mangaId LIMIT 1")
    suspend fun getMangaProgressDirect(userId: String, mangaId: String): MangaProgressEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveMangaProgress(progress: MangaProgressEntity)

    @Query("SELECT * FROM manga_progress WHERE userId = :userId AND completed = 0 ORDER BY lastReadTimestamp DESC LIMIT 10")
    fun getRecentMangaProgress(userId: String): Flow<List<MangaProgressEntity>>
}

@Dao
interface BookmarkDao {
    @Query("SELECT * FROM bookmarks WHERE userId = :userId AND contentId = :contentId ORDER BY createdAt DESC")
    fun getBookmarksForContent(userId: String, contentId: String): Flow<List<BookmarkEntity>>

    @Query("SELECT * FROM bookmarks WHERE userId = :userId ORDER BY createdAt DESC")
    fun getAllBookmarks(userId: String): Flow<List<BookmarkEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmark(bookmark: BookmarkEntity)

    @Query("DELETE FROM bookmarks WHERE id = :id")
    suspend fun deleteBookmark(id: Long)
}

@Dao
interface HistoryDao {
    @Query("SELECT * FROM reading_history WHERE userId = :userId ORDER BY timestamp DESC LIMIT 40")
    fun getHistory(userId: String): Flow<List<ReadingHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addHistory(history: ReadingHistoryEntity)

    @Query("DELETE FROM reading_history WHERE id = :id")
    suspend fun deleteHistoryItem(id: Long)

    @Query("DELETE FROM reading_history WHERE userId = :userId")
    suspend fun clearHistory(userId: String)
}

@Dao
interface SourceDao {
    @Query("SELECT * FROM sources ORDER BY itemsImported DESC")
    fun getAllSources(): Flow<List<SourceEntity>>

    @Query("SELECT * FROM sources WHERE sourceId = :sourceId LIMIT 1")
    fun getSource(sourceId: String): Flow<SourceEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSources(sources: List<SourceEntity>)

    @Query("UPDATE sources SET status = :status, lastSyncTimestamp = :timestamp, itemsImported = itemsImported + :newItems, newItemsLastSync = :newItems, failedItems = :failedItems WHERE sourceId = :sourceId")
    suspend fun updateSourceSyncStats(sourceId: String, status: String, timestamp: Long, newItems: Int, failedItems: Int)

    @Query("UPDATE sources SET enabled = :enabled WHERE sourceId = :sourceId")
    suspend fun setSourceEnabled(sourceId: String, enabled: Boolean)

    @Query("SELECT * FROM sync_runs ORDER BY timestamp DESC LIMIT 25")
    fun getAllSyncRuns(): Flow<List<SyncRunEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSyncRun(syncRun: SyncRunEntity)
}

@Dao
interface PreferencesDao {
    @Query("SELECT * FROM user_preferences WHERE id = 1 LIMIT 1")
    fun getPreferences(): Flow<UserPreferencesEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun savePreferences(preferences: UserPreferencesEntity)
}
