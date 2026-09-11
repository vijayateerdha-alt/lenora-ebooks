package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.BookEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {
    @Query("SELECT * FROM books WHERE isHidden = 0 ORDER BY popularityScore DESC LIMIT :limit OFFSET :offset")
    fun getBooksPaged(limit: Int, offset: Int): Flow<List<BookEntity>>

    @Query("SELECT * FROM books WHERE id = :id LIMIT 1")
    fun getBookById(id: String): Flow<BookEntity?>

    @Query("SELECT * FROM books WHERE id = :id LIMIT 1")
    suspend fun getBookByIdDirect(id: String): BookEntity?

    @Query("SELECT * FROM books WHERE isHidden = 0 AND featuredHero = 1 ORDER BY popularityScore DESC LIMIT 5")
    fun getFeaturedHeroBooks(): Flow<List<BookEntity>>

    @Query("SELECT * FROM books WHERE isHidden = 0 ORDER BY trendingScore DESC LIMIT :limit")
    fun getTrendingBooks(limit: Int = 15): Flow<List<BookEntity>>

    @Query("SELECT * FROM books WHERE isHidden = 0 ORDER BY dateAdded DESC LIMIT :limit")
    fun getRecentlyAddedBooks(limit: Int = 15): Flow<List<BookEntity>>

    @Query("SELECT * FROM books WHERE isHidden = 0 AND hasAudiobook = 1 ORDER BY popularityScore DESC LIMIT :limit")
    fun getAudiobooks(limit: Int = 15): Flow<List<BookEntity>>

    @Query("SELECT * FROM books WHERE isHidden = 0 AND (genres LIKE '%' || :genre || '%' OR subjects LIKE '%' || :genre || '%') ORDER BY popularityScore DESC LIMIT :limit")
    fun getBooksByGenre(genre: String, limit: Int = 12): Flow<List<BookEntity>>

    @Query("""
        SELECT * FROM books 
        WHERE isHidden = 0
        AND (
            title LIKE '%' || :query || '%' 
            OR author LIKE '%' || :query || '%' 
            OR narrator LIKE '%' || :query || '%'
            OR genres LIKE '%' || :query || '%'
            OR subjects LIKE '%' || :query || '%'
        )
        AND (:filterEbook = 0 OR hasEbook = 1)
        AND (:filterAudio = 0 OR hasAudiobook = 1)
        AND (:genre IS NULL OR :genre = '' OR genres LIKE '%' || :genre || '%')
        ORDER BY 
            CASE WHEN :sortBy = 'popularity' THEN popularityScore END DESC,
            CASE WHEN :sortBy = 'recent' THEN dateAdded END DESC,
            CASE WHEN :sortBy = 'alpha' THEN title END ASC,
            popularityScore DESC
        LIMIT :limit
    """)
    fun searchBooks(
        query: String,
        filterEbook: Int = 0,
        filterAudio: Int = 0,
        genre: String? = null,
        sortBy: String = "popularity",
        limit: Int = 50
    ): Flow<List<BookEntity>>

    @Query("SELECT COUNT(*) FROM books WHERE isHidden = 0")
    fun getTotalBooksCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM books WHERE isHidden = 0 AND hasAudiobook = 1")
    fun getTotalAudiobooksCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM books")
    suspend fun getCatalogCountDirect(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooks(books: List<BookEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBook(book: BookEntity)

    @Update
    suspend fun updateBook(book: BookEntity)

    @Query("UPDATE books SET isHidden = :isHidden WHERE id = :id")
    suspend fun setHidden(id: String, isHidden: Boolean)

    @Query("DELETE FROM books WHERE id = :id")
    suspend fun deleteBook(id: String)
}
