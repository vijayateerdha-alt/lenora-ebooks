package com.example.data.repository

import com.example.data.db.AppDatabase
import com.example.data.model.ActiveSessionEntity
import com.example.data.model.BookmarkEntity
import com.example.data.model.BookEntity
import com.example.data.model.ListeningProgressEntity
import com.example.data.model.MangaChapterEntity
import com.example.data.model.MangaEntity
import com.example.data.model.MangaProgressEntity
import com.example.data.model.ReadingHistoryEntity
import com.example.data.model.ReadingProgressEntity
import com.example.data.model.SourceEntity
import com.example.data.model.SyncRunEntity
import com.example.data.model.UserEntity
import com.example.data.model.UserLibraryEntity
import com.example.data.model.UserPreferencesEntity
import com.example.data.security.PasswordHasher
import com.example.data.sync.ContentSyncEngine
import com.example.data.sync.SyncResult
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class BookRepository(private val database: AppDatabase) {

    private val bookDao = database.bookDao()
    private val mangaDao = database.mangaDao()
    private val userDao = database.userDao()
    private val libraryDao = database.libraryDao()
    private val progressDao = database.progressDao()
    private val bookmarkDao = database.bookmarkDao()
    private val historyDao = database.historyDao()
    private val sourceDao = database.sourceDao()
    private val preferencesDao = database.preferencesDao()

    // -------------------------------------------------------------
    // USER AUTHENTICATION & SESSION
    // -------------------------------------------------------------

    suspend fun registerUser(username: String, password: String): Result<UserEntity> {
        val trimmed = username.trim()
        if (trimmed.length < 3) {
            return Result.failure(IllegalArgumentException("Username must be at least 3 characters long."))
        }
        if (password.length < 4) {
            return Result.failure(IllegalArgumentException("Password must be at least 4 characters long."))
        }
        if (userDao.isUsernameTaken(trimmed)) {
            return Result.failure(IllegalArgumentException("Username '$trimmed' is already taken. Please choose another."))
        }

        val salt = PasswordHasher.generateSalt()
        val hash = PasswordHasher.hashPassword(password, salt)
        val user = UserEntity(
            id = UUID.randomUUID().toString(),
            username = trimmed,
            passwordHash = hash,
            salt = salt,
            createdAt = System.currentTimeMillis(),
            lastLoginAt = System.currentTimeMillis(),
            displayName = trimmed
        )
        userDao.insertUser(user)
        userDao.setActiveSession(ActiveSessionEntity(id = 1, userId = user.id))
        return Result.success(user)
    }

    suspend fun loginUser(username: String, password: String): Result<UserEntity> {
        val trimmed = username.trim()
        val user = userDao.getUserByUsername(trimmed)
            ?: return Result.failure(IllegalArgumentException("Account not found. Please check your username or register."))

        val isValid = PasswordHasher.verifyPassword(password, user.salt, user.passwordHash)
        if (!isValid) {
            return Result.failure(IllegalArgumentException("Incorrect password. Please try again."))
        }

        val updated = user.copy(lastLoginAt = System.currentTimeMillis())
        userDao.updateUser(updated)
        userDao.setActiveSession(ActiveSessionEntity(id = 1, userId = user.id))
        return Result.success(updated)
    }

    suspend fun logout() {
        userDao.clearActiveSession()
    }

    suspend fun deleteAccount(userId: String) {
        userDao.deleteEntireAccount(userId)
    }

    fun getActiveSession(): Flow<ActiveSessionEntity?> = userDao.getActiveSession()

    suspend fun getActiveSessionDirect(): ActiveSessionEntity? = userDao.getActiveSessionDirect()

    fun getUserById(userId: String): Flow<UserEntity?> = userDao.getUserById(userId)

    suspend fun getUserByIdDirect(userId: String): UserEntity? = userDao.getUserByIdDirect(userId)

    // -------------------------------------------------------------
    // BOOKS & CATALOG
    // -------------------------------------------------------------

    fun getBooksPaged(limit: Int = 40, offset: Int = 0): Flow<List<BookEntity>> =
        bookDao.getBooksPaged(limit, offset)

    fun getBookById(id: String): Flow<BookEntity?> =
        bookDao.getBookById(id)

    suspend fun getBookByIdDirect(id: String): BookEntity? =
        bookDao.getBookByIdDirect(id)

    fun getFeaturedHeroBooks(): Flow<List<BookEntity>> =
        bookDao.getFeaturedHeroBooks()

    fun getTrendingBooks(limit: Int = 15): Flow<List<BookEntity>> =
        bookDao.getTrendingBooks(limit)

    fun getRecentlyAddedBooks(limit: Int = 15): Flow<List<BookEntity>> =
        bookDao.getRecentlyAddedBooks(limit)

    fun getAudiobooks(limit: Int = 15): Flow<List<BookEntity>> =
        bookDao.getAudiobooks(limit)

    fun getBooksByGenre(genre: String, limit: Int = 12): Flow<List<BookEntity>> =
        bookDao.getBooksByGenre(genre, limit)

    fun searchBooks(
        query: String,
        filterEbook: Boolean = false,
        filterAudio: Boolean = false,
        genre: String? = null,
        sortBy: String = "popularity",
        limit: Int = 60
    ): Flow<List<BookEntity>> =
        bookDao.searchBooks(
            query = query,
            filterEbook = if (filterEbook) 1 else 0,
            filterAudio = if (filterAudio) 1 else 0,
            genre = genre,
            sortBy = sortBy,
            limit = limit
        )

    fun getTotalBooksCount(): Flow<Int> = bookDao.getTotalBooksCount()
    fun getTotalAudiobooksCount(): Flow<Int> = bookDao.getTotalAudiobooksCount()

    suspend fun setBookHidden(bookId: String, isHidden: Boolean) =
        bookDao.setHidden(bookId, isHidden)

    suspend fun updateBook(book: BookEntity) =
        bookDao.updateBook(book)

    // -------------------------------------------------------------
    // MANGA SECTION
    // -------------------------------------------------------------

    fun getMangaPaged(limit: Int = 40, offset: Int = 0): Flow<List<MangaEntity>> =
        mangaDao.getMangaPaged(limit, offset)

    fun getMangaById(id: String): Flow<MangaEntity?> =
        mangaDao.getMangaById(id)

    suspend fun getMangaByIdDirect(id: String): MangaEntity? =
        mangaDao.getMangaByIdDirect(id)

    fun getFeaturedHeroManga(): Flow<List<MangaEntity>> =
        mangaDao.getFeaturedHeroManga()

    fun getTrendingManga(limit: Int = 15): Flow<List<MangaEntity>> =
        mangaDao.getTrendingManga(limit)

    fun getPopularManga(limit: Int = 15): Flow<List<MangaEntity>> =
        mangaDao.getPopularManga(limit)

    fun getRecentlyAddedManga(limit: Int = 15): Flow<List<MangaEntity>> =
        mangaDao.getRecentlyAddedManga(limit)

    fun getMangaByGenre(genre: String, limit: Int = 12): Flow<List<MangaEntity>> =
        mangaDao.getMangaByGenre(genre, limit)

    fun searchManga(
        query: String,
        genre: String? = null,
        status: String? = null,
        sortBy: String = "popularity",
        limit: Int = 60
    ): Flow<List<MangaEntity>> =
        mangaDao.searchManga(
            query = query,
            genre = genre,
            status = status,
            sortBy = sortBy,
            limit = limit
        )

    fun getTotalMangaCount(): Flow<Int> = mangaDao.getTotalMangaCount()

    fun getChaptersForManga(mangaId: String): Flow<List<MangaChapterEntity>> =
        mangaDao.getChaptersForManga(mangaId)

    suspend fun getChaptersForMangaDirect(mangaId: String): List<MangaChapterEntity> =
        mangaDao.getChaptersForMangaDirect(mangaId)

    suspend fun getChapter(mangaId: String, chapterIndex: Int): MangaChapterEntity? =
        mangaDao.getChapter(mangaId, chapterIndex)

    // -------------------------------------------------------------
    // USER LIBRARY (Isolated by userId)
    // -------------------------------------------------------------

    fun getLibraryEntries(userId: String): Flow<List<UserLibraryEntity>> =
        libraryDao.getAllLibraryEntries(userId)

    fun getLibraryEntriesByType(userId: String, contentType: String): Flow<List<UserLibraryEntity>> =
        libraryDao.getLibraryEntriesByType(userId, contentType)

    fun getLibraryFavorites(userId: String): Flow<List<UserLibraryEntity>> =
        libraryDao.getFavoriteEntries(userId)

    fun isContentSaved(userId: String, contentId: String): Flow<Boolean> =
        libraryDao.isContentSaved(userId, contentId)

    fun isContentFavorite(userId: String, contentId: String): Flow<Boolean?> =
        libraryDao.isContentFavorite(userId, contentId)

    suspend fun toggleSaveToLibrary(
        userId: String,
        contentId: String,
        contentType: String,
        title: String,
        author: String,
        coverImageUrl: String,
        isCurrentlySaved: Boolean
    ) {
        if (isCurrentlySaved) {
            libraryDao.removeFromLibrary(userId, contentId)
        } else {
            libraryDao.saveToLibrary(
                UserLibraryEntity(
                    userId = userId,
                    contentId = contentId,
                    contentType = contentType,
                    title = title,
                    author = author,
                    coverImageUrl = coverImageUrl,
                    status = "READING",
                    savedAt = System.currentTimeMillis()
                )
            )
        }
    }

    suspend fun toggleFavorite(
        userId: String,
        contentId: String,
        contentType: String,
        title: String,
        author: String,
        coverImageUrl: String,
        isCurrentlyFavorite: Boolean
    ) {
        val newFav = !isCurrentlyFavorite
        libraryDao.saveToLibrary(
            UserLibraryEntity(
                userId = userId,
                contentId = contentId,
                contentType = contentType,
                title = title,
                author = author,
                coverImageUrl = coverImageUrl,
                status = "SAVED",
                savedAt = System.currentTimeMillis(),
                isFavorite = newFav
            )
        )
    }

    suspend fun updateLibraryStatus(userId: String, contentId: String, status: String) =
        libraryDao.updateStatus(userId, contentId, status)

    fun getFinishedCount(userId: String): Flow<Int> =
        libraryDao.getFinishedBooksCount(userId)

    // -------------------------------------------------------------
    // PROGRESS TRACKING (Isolated by userId)
    // -------------------------------------------------------------

    fun getReadingProgress(userId: String, bookId: String): Flow<ReadingProgressEntity?> =
        progressDao.getReadingProgress(userId, bookId)

    suspend fun saveReadingProgress(progress: ReadingProgressEntity) =
        progressDao.saveReadingProgress(progress)

    fun getRecentReadingProgress(userId: String): Flow<List<ReadingProgressEntity>> =
        progressDao.getRecentReadingProgress(userId)

    fun getListeningProgress(userId: String, bookId: String): Flow<ListeningProgressEntity?> =
        progressDao.getListeningProgress(userId, bookId)

    suspend fun saveListeningProgress(progress: ListeningProgressEntity) =
        progressDao.saveListeningProgress(progress)

    fun getRecentListeningProgress(userId: String): Flow<List<ListeningProgressEntity>> =
        progressDao.getRecentListeningProgress(userId)

    fun getMangaProgress(userId: String, mangaId: String): Flow<MangaProgressEntity?> =
        progressDao.getMangaProgress(userId, mangaId)

    suspend fun saveMangaProgress(progress: MangaProgressEntity) =
        progressDao.saveMangaProgress(progress)

    fun getRecentMangaProgress(userId: String): Flow<List<MangaProgressEntity>> =
        progressDao.getRecentMangaProgress(userId)

    // -------------------------------------------------------------
    // BOOKMARKS (Isolated by userId)
    // -------------------------------------------------------------

    fun getBookmarksForContent(userId: String, contentId: String): Flow<List<BookmarkEntity>> =
        bookmarkDao.getBookmarksForContent(userId, contentId)

    fun getAllBookmarks(userId: String): Flow<List<BookmarkEntity>> =
        bookmarkDao.getAllBookmarks(userId)

    suspend fun addBookmark(bookmark: BookmarkEntity) =
        bookmarkDao.insertBookmark(bookmark)

    suspend fun deleteBookmark(id: Long) =
        bookmarkDao.deleteBookmark(id)

    // -------------------------------------------------------------
    // READING & LISTENING HISTORY (Isolated by userId)
    // -------------------------------------------------------------

    fun getHistory(userId: String): Flow<List<ReadingHistoryEntity>> =
        historyDao.getHistory(userId)

    suspend fun recordHistory(
        userId: String,
        contentId: String,
        contentType: String,
        title: String,
        subtitle: String,
        coverImageUrl: String
    ) {
        historyDao.addHistory(
            ReadingHistoryEntity(
                userId = userId,
                contentId = contentId,
                contentType = contentType,
                title = title,
                subtitle = subtitle,
                coverImageUrl = coverImageUrl,
                timestamp = System.currentTimeMillis()
            )
        )
    }

    suspend fun deleteHistoryItem(id: Long) =
        historyDao.deleteHistoryItem(id)

    suspend fun clearHistory(userId: String) =
        historyDao.clearHistory(userId)

    // -------------------------------------------------------------
    // SOURCES & SYNC
    // -------------------------------------------------------------

    fun getAllSources(): Flow<List<SourceEntity>> =
        sourceDao.getAllSources()

    fun getAllSyncRuns(): Flow<List<SyncRunEntity>> =
        sourceDao.getAllSyncRuns()

    suspend fun toggleSourceEnabled(sourceId: String, enabled: Boolean) =
        sourceDao.setSourceEnabled(sourceId, enabled)

    suspend fun triggerSourceSync(sourceId: String): SyncResult =
        ContentSyncEngine.triggerSync(database, sourceId)

    // -------------------------------------------------------------
    // PREFERENCES
    // -------------------------------------------------------------

    fun getPreferences(): Flow<UserPreferencesEntity?> =
        preferencesDao.getPreferences()

    suspend fun savePreferences(preferences: UserPreferencesEntity) =
        preferencesDao.savePreferences(preferences)
}
