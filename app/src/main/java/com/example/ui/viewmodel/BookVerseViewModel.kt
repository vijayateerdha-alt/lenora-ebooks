package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.catalog.CatalogSeeder
import com.example.data.catalog.MangaSeeder
import com.example.data.db.AppDatabase
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
import com.example.data.repository.BookRepository
import com.example.player.AudiobookPlayerManager
import com.example.player.PlayerState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class ScreenRoute {
    data object Home : ScreenRoute()
    data object Manga : ScreenRoute()
    data object Discover : ScreenRoute()
    data object Search : ScreenRoute()
    data object Library : ScreenRoute()
    data object Profile : ScreenRoute()
    data object Credits : ScreenRoute()
    data class BookDetail(val bookId: String) : ScreenRoute()
    data class Reader(val bookId: String) : ScreenRoute()
    data class MangaDetail(val mangaId: String) : ScreenRoute()
    data class MangaReader(val mangaId: String, val initialChapterIndex: Int = 0, val initialPage: Int = 0) : ScreenRoute()
    data object AdminSources : ScreenRoute()
    data object LegalLicensing : ScreenRoute()
}

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class BookVerseViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getInstance(application)
    val repository = BookRepository(database)
    val playerManager = AudiobookPlayerManager(application)

    // Navigation Backstack State
    private val _currentScreen = MutableStateFlow<ScreenRoute>(ScreenRoute.Home)
    val currentScreen: StateFlow<ScreenRoute> = _currentScreen.asStateFlow()

    private val screenStack = mutableListOf<ScreenRoute>(ScreenRoute.Home)

    // Player Expanded Modal Sheet
    private val _isPlayerExpanded = MutableStateFlow(false)
    val isPlayerExpanded: StateFlow<Boolean> = _isPlayerExpanded.asStateFlow()

    // Sync & Action Toast
    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    // Auth modal state & error
    private val _isAuthDialogOpen = MutableStateFlow(false)
    val isAuthDialogOpen: StateFlow<Boolean> = _isAuthDialogOpen.asStateFlow()

    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError.asStateFlow()

    // User authentication flow
    val currentUser: StateFlow<UserEntity?> = repository.getActiveSession()
        .flatMapLatest { session ->
            if (session != null) {
                repository.getUserById(session.userId)
            } else {
                flowOf(null)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val currentUserId: String
        get() = currentUser.value?.id ?: "guest_user"

    // Preferences
    val userPreferences: StateFlow<UserPreferencesEntity> = repository.getPreferences()
        .map { it ?: UserPreferencesEntity() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserPreferencesEntity())

    // Player State
    val playerState: StateFlow<PlayerState> = playerManager.playerState

    // -------------------------------------------------------------------------
    // BOOKS CATALOG FEEDS
    // -------------------------------------------------------------------------
    val featuredBooks: StateFlow<List<BookEntity>> = repository.getFeaturedHeroBooks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val trendingBooks: StateFlow<List<BookEntity>> = repository.getTrendingBooks(18)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentlyAddedBooks: StateFlow<List<BookEntity>> = repository.getRecentlyAddedBooks(18)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val audiobooks: StateFlow<List<BookEntity>> = repository.getAudiobooks(18)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalBooksCount: StateFlow<Int> = repository.getTotalBooksCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 1050)

    val totalAudiobooksCount: StateFlow<Int> = repository.getTotalAudiobooksCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 350)

    // -------------------------------------------------------------------------
    // MANGA CATALOG FEEDS
    // -------------------------------------------------------------------------
    val featuredManga: StateFlow<List<MangaEntity>> = repository.getFeaturedHeroManga()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val trendingManga: StateFlow<List<MangaEntity>> = repository.getTrendingManga(18)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val popularManga: StateFlow<List<MangaEntity>> = repository.getPopularManga(18)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentlyAddedManga: StateFlow<List<MangaEntity>> = repository.getRecentlyAddedManga(18)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalMangaCount: StateFlow<Int> = repository.getTotalMangaCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 1020)

    // Manga Search & Filters
    val mangaSearchQuery = MutableStateFlow("")
    val mangaSelectedGenre = MutableStateFlow<String?>(null)
    val mangaSelectedStatus = MutableStateFlow<String?>(null)
    val mangaSortBy = MutableStateFlow("popularity")

    val mangaSearchResults: StateFlow<List<MangaEntity>> = combine(
        mangaSearchQuery.debounce(250).distinctUntilChanged(),
        mangaSelectedGenre,
        mangaSelectedStatus,
        mangaSortBy
    ) { q, genre, status, sort ->
        MangaFilterParams(q, genre, status, sort)
    }.flatMapLatest { p ->
        repository.searchManga(
            query = p.query,
            genre = p.genre,
            status = p.status,
            sortBy = p.sort,
            limit = 60
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // -------------------------------------------------------------------------
    // USER-SPECIFIC DATA (Isolated per Authenticated Account)
    // -------------------------------------------------------------------------
    val continueReadingBooks: StateFlow<List<ReadingProgressEntity>> = currentUser
        .flatMapLatest { user ->
            val uid = user?.id ?: "guest_user"
            repository.getRecentReadingProgress(uid)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val continueListeningBooks: StateFlow<List<ListeningProgressEntity>> = currentUser
        .flatMapLatest { user ->
            val uid = user?.id ?: "guest_user"
            repository.getRecentListeningProgress(uid)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val continueReadingManga: StateFlow<List<MangaProgressEntity>> = currentUser
        .flatMapLatest { user ->
            val uid = user?.id ?: "guest_user"
            repository.getRecentMangaProgress(uid)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userLibraryEntries: StateFlow<List<UserLibraryEntity>> = currentUser
        .flatMapLatest { user ->
            val uid = user?.id ?: "guest_user"
            repository.getLibraryEntries(uid)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userFavorites: StateFlow<List<UserLibraryEntity>> = currentUser
        .flatMapLatest { user ->
            val uid = user?.id ?: "guest_user"
            repository.getLibraryFavorites(uid)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userBookmarks: StateFlow<List<BookmarkEntity>> = currentUser
        .flatMapLatest { user ->
            val uid = user?.id ?: "guest_user"
            repository.getAllBookmarks(uid)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userHistory: StateFlow<List<ReadingHistoryEntity>> = currentUser
        .flatMapLatest { user ->
            val uid = user?.id ?: "guest_user"
            repository.getHistory(uid)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Global Book Search
    val searchQuery = MutableStateFlow("")
    val searchFilterEbook = MutableStateFlow(false)
    val searchFilterAudio = MutableStateFlow(false)
    val searchGenre = MutableStateFlow<String?>(null)
    val searchSortBy = MutableStateFlow("popularity")

    val searchResults: StateFlow<List<BookEntity>> = combine(
        searchQuery.debounce(250).distinctUntilChanged(),
        searchFilterEbook,
        searchFilterAudio,
        searchGenre,
        searchSortBy
    ) { q, ebook, audio, genre, sort ->
        SearchFilterParams(q, ebook, audio, genre, sort)
    }.flatMapLatest { params ->
        repository.searchBooks(
            query = params.query,
            filterEbook = params.ebook,
            filterAudio = params.audio,
            genre = params.genre,
            sortBy = params.sort,
            limit = 80
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val sources: StateFlow<List<SourceEntity>> = repository.getAllSources()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val syncRuns: StateFlow<List<SyncRunEntity>> = repository.getAllSyncRuns()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    init {
        // Tie Audiobook progress saver to current user id dynamically
        playerManager.currentUserIdProvider = { currentUserId }

        // Seed catalogs asynchronously
        viewModelScope.launch(Dispatchers.IO) {
            CatalogSeeder.seedInitialCatalogIfEmpty(database)
            MangaSeeder.seedInitialMangaIfEmpty(database)
        }
    }

    // -------------------------------------------------------------------------
    // AUTHENTICATION METHODS
    // -------------------------------------------------------------------------
    fun openAuthDialog() {
        _authError.value = null
        _isAuthDialogOpen.value = true
    }

    fun closeAuthDialog() {
        _isAuthDialogOpen.value = false
        _authError.value = null
    }

    fun registerUser(username: String, password: String, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            _authError.value = null
            val result = repository.registerUser(username, password)
            result.onSuccess { user ->
                closeAuthDialog()
                showToast("Welcome to BooksVerse, ${user.username}!")
                onSuccess()
            }.onFailure { err ->
                _authError.value = err.message ?: "Registration failed. Please try again."
            }
        }
    }

    fun loginUser(username: String, password: String, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            _authError.value = null
            val result = repository.loginUser(username, password)
            result.onSuccess { user ->
                closeAuthDialog()
                showToast("Welcome back, ${user.username}!")
                onSuccess()
            }.onFailure { err ->
                _authError.value = err.message ?: "Login failed. Please check credentials."
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
            showToast("Signed out successfully.")
        }
    }

    fun deleteAccount() {
        val uid = currentUser.value?.id ?: return
        viewModelScope.launch {
            repository.deleteAccount(uid)
            showToast("Account deleted.")
        }
    }

    // -------------------------------------------------------------------------
    // NAVIGATION METHODS
    // -------------------------------------------------------------------------
    fun navigateTo(route: ScreenRoute) {
        if (_currentScreen.value != route) {
            screenStack.add(route)
            _currentScreen.value = route
        }
    }

    fun navigateBack(): Boolean {
        if (screenStack.size > 1) {
            screenStack.removeAt(screenStack.size - 1)
            _currentScreen.value = screenStack.last()
            return true
        }
        return false
    }

    fun openBookDetail(bookId: String) {
        navigateTo(ScreenRoute.BookDetail(bookId))
    }

    fun openReader(bookId: String) {
        navigateTo(ScreenRoute.Reader(bookId))
    }

    fun openMangaDetail(mangaId: String) {
        navigateTo(ScreenRoute.MangaDetail(mangaId))
    }

    fun openMangaReader(mangaId: String, chapterIndex: Int = 0, initialPage: Int = 0) {
        navigateTo(ScreenRoute.MangaReader(mangaId, chapterIndex, initialPage))
    }

    fun openCredits() {
        navigateTo(ScreenRoute.Credits)
    }

    fun openPlayerModal() {
        _isPlayerExpanded.value = true
    }

    fun closePlayerModal() {
        _isPlayerExpanded.value = false
    }

    fun startAudiobook(book: BookEntity) {
        playerManager.loadAndPlay(book)
        recordHistory(
            contentId = book.id,
            contentType = "AUDIOBOOK",
            title = book.title,
            subtitle = book.author,
            coverImageUrl = book.coverImageUrl
        )
    }

    fun playAudiobookById(bookId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val book = repository.getBookByIdDirect(bookId)
            if (book != null) {
                kotlinx.coroutines.withContext(Dispatchers.Main) {
                    startAudiobook(book)
                }
            }
        }
    }

    // -------------------------------------------------------------------------
    // READING & MANGA PROGRESS METHODS
    // -------------------------------------------------------------------------
    fun saveReadingProgress(
        bookId: String,
        title: String,
        author: String,
        coverImageUrl: String,
        chapter: Int,
        scrollProgress: Float,
        completed: Boolean = false
    ) {
        viewModelScope.launch {
            repository.saveReadingProgress(
                ReadingProgressEntity(
                    userId = currentUserId,
                    bookId = bookId,
                    title = title,
                    author = author,
                    coverImageUrl = coverImageUrl,
                    currentChapter = chapter,
                    scrollProgress = scrollProgress,
                    lastReadTimestamp = System.currentTimeMillis(),
                    completed = completed
                )
            )
            recordHistory(
                contentId = bookId,
                contentType = "BOOK",
                title = title,
                subtitle = author,
                coverImageUrl = coverImageUrl
            )
        }
    }

    fun saveMangaProgress(
        mangaId: String,
        title: String,
        coverImageUrl: String,
        chapterIndex: Int,
        chapterTitle: String,
        pageIndex: Int,
        totalPages: Int,
        completed: Boolean = false
    ) {
        viewModelScope.launch {
            repository.saveMangaProgress(
                MangaProgressEntity(
                    userId = currentUserId,
                    mangaId = mangaId,
                    title = title,
                    coverImageUrl = coverImageUrl,
                    chapterIndex = chapterIndex,
                    chapterTitle = chapterTitle,
                    pageIndex = pageIndex,
                    totalPages = totalPages,
                    scrollProgress = if (totalPages > 0) pageIndex.toFloat() / totalPages else 0f,
                    lastReadTimestamp = System.currentTimeMillis(),
                    completed = completed
                )
            )
            recordHistory(
                contentId = mangaId,
                contentType = "MANGA",
                title = title,
                subtitle = chapterTitle,
                coverImageUrl = coverImageUrl
            )
        }
    }

    // -------------------------------------------------------------------------
    // USER LIBRARY & FAVORITES
    // -------------------------------------------------------------------------
    fun toggleSaveContent(
        contentId: String,
        contentType: String,
        title: String,
        author: String,
        coverImageUrl: String,
        isCurrentlySaved: Boolean
    ) {
        viewModelScope.launch {
            repository.toggleSaveToLibrary(
                userId = currentUserId,
                contentId = contentId,
                contentType = contentType,
                title = title,
                author = author,
                coverImageUrl = coverImageUrl,
                isCurrentlySaved = isCurrentlySaved
            )
            showToast(if (isCurrentlySaved) "Removed from library" else "Saved to My Library")
        }
    }

    fun toggleFavoriteContent(
        contentId: String,
        contentType: String,
        title: String,
        author: String,
        coverImageUrl: String,
        isCurrentlyFavorite: Boolean
    ) {
        viewModelScope.launch {
            repository.toggleFavorite(
                userId = currentUserId,
                contentId = contentId,
                contentType = contentType,
                title = title,
                author = author,
                coverImageUrl = coverImageUrl,
                isCurrentlyFavorite = isCurrentlyFavorite
            )
            showToast(if (isCurrentlyFavorite) "Removed from favorites" else "Added to Favorites")
        }
    }

    fun addBookmark(
        contentId: String,
        contentType: String,
        chapterTitle: String,
        snippet: String,
        progress: Int,
        pageIndex: Int = 0
    ) {
        viewModelScope.launch {
            repository.addBookmark(
                BookmarkEntity(
                    userId = currentUserId,
                    contentId = contentId,
                    contentType = contentType,
                    chapterTitle = chapterTitle,
                    snippet = snippet.take(160),
                    progressPercent = progress,
                    pageIndex = pageIndex
                )
            )
            showToast("Bookmark added")
        }
    }

    fun deleteBookmark(bookmarkId: Long) {
        viewModelScope.launch {
            repository.deleteBookmark(bookmarkId)
            showToast("Bookmark removed")
        }
    }

    fun recordHistory(
        contentId: String,
        contentType: String,
        title: String,
        subtitle: String,
        coverImageUrl: String
    ) {
        viewModelScope.launch {
            repository.recordHistory(
                userId = currentUserId,
                contentId = contentId,
                contentType = contentType,
                title = title,
                subtitle = subtitle,
                coverImageUrl = coverImageUrl
            )
        }
    }

    fun deleteHistoryItem(id: Long) {
        viewModelScope.launch {
            repository.deleteHistoryItem(id)
            showToast("Removed from history")
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            repository.clearHistory(currentUserId)
            showToast("History cleared")
        }
    }

    // -------------------------------------------------------------------------
    // PREFERENCES & SYNC
    // -------------------------------------------------------------------------
    fun updatePreferences(
        fontSize: Int? = null,
        theme: String? = null,
        lineSpacing: Float? = null,
        displayName: String? = null
    ) {
        viewModelScope.launch {
            val current = userPreferences.value
            val updated = current.copy(
                readerFontSize = fontSize ?: current.readerFontSize,
                readerTheme = theme ?: current.readerTheme,
                readerLineSpacing = lineSpacing ?: current.readerLineSpacing,
                displayName = displayName ?: current.displayName
            )
            repository.savePreferences(updated)
        }
    }

    fun triggerSync(sourceId: String) {
        viewModelScope.launch {
            _isSyncing.value = true
            showToast("Syncing $sourceId catalog...")
            val result = repository.triggerSourceSync(sourceId)
            _isSyncing.value = false
            showToast(result.message)
        }
    }

    fun showToast(message: String) {
        _toastMessage.value = message
    }

    fun clearToast() {
        _toastMessage.value = null
    }

    override fun onCleared() {
        super.onCleared()
        playerManager.releasePlayer()
    }
}

private data class SearchFilterParams(
    val query: String,
    val ebook: Boolean,
    val audio: Boolean,
    val genre: String?,
    val sort: String
)

private data class MangaFilterParams(
    val query: String,
    val genre: String?,
    val status: String?,
    val sort: String
)
