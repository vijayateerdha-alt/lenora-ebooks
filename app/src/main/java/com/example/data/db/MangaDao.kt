package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.MangaChapterEntity
import com.example.data.model.MangaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MangaDao {

    @Query("SELECT * FROM manga ORDER BY popularityScore DESC LIMIT :limit OFFSET :offset")
    fun getMangaPaged(limit: Int, offset: Int): Flow<List<MangaEntity>>

    @Query("SELECT * FROM manga WHERE id = :id LIMIT 1")
    fun getMangaById(id: String): Flow<MangaEntity?>

    @Query("SELECT * FROM manga WHERE id = :id LIMIT 1")
    suspend fun getMangaByIdDirect(id: String): MangaEntity?

    @Query("SELECT * FROM manga WHERE featuredHero = 1 ORDER BY popularityScore DESC LIMIT 5")
    fun getFeaturedHeroManga(): Flow<List<MangaEntity>>

    @Query("SELECT * FROM manga ORDER BY trendingScore DESC LIMIT :limit")
    fun getTrendingManga(limit: Int = 15): Flow<List<MangaEntity>>

    @Query("SELECT * FROM manga ORDER BY popularityScore DESC LIMIT :limit")
    fun getPopularManga(limit: Int = 15): Flow<List<MangaEntity>>

    @Query("SELECT * FROM manga ORDER BY dateAdded DESC LIMIT :limit")
    fun getRecentlyAddedManga(limit: Int = 15): Flow<List<MangaEntity>>

    @Query("SELECT * FROM manga WHERE genres LIKE '%' || :genre || '%' ORDER BY popularityScore DESC LIMIT :limit")
    fun getMangaByGenre(genre: String, limit: Int = 12): Flow<List<MangaEntity>>

    @Query("""
        SELECT * FROM manga
        WHERE (
            title LIKE '%' || :query || '%'
            OR altTitle LIKE '%' || :query || '%'
            OR author LIKE '%' || :query || '%'
            OR artist LIKE '%' || :query || '%'
            OR genres LIKE '%' || :query || '%'
            OR description LIKE '%' || :query || '%'
        )
        AND (:genre IS NULL OR :genre = '' OR genres LIKE '%' || :genre || '%')
        AND (:status IS NULL OR :status = '' OR status = :status)
        ORDER BY
            CASE WHEN :sortBy = 'popularity' THEN popularityScore END DESC,
            CASE WHEN :sortBy = 'recent' THEN dateAdded END DESC,
            CASE WHEN :sortBy = 'alpha' THEN title END ASC,
            popularityScore DESC
        LIMIT :limit
    """)
    fun searchManga(
        query: String,
        genre: String? = null,
        status: String? = null,
        sortBy: String = "popularity",
        limit: Int = 50
    ): Flow<List<MangaEntity>>

    @Query("SELECT COUNT(*) FROM manga")
    fun getTotalMangaCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM manga")
    suspend fun getCatalogCountDirect(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMangaList(mangaList: List<MangaEntity>)

    @Query("SELECT * FROM manga_chapters WHERE mangaId = :mangaId ORDER BY chapterIndex ASC")
    fun getChaptersForManga(mangaId: String): Flow<List<MangaChapterEntity>>

    @Query("SELECT * FROM manga_chapters WHERE mangaId = :mangaId ORDER BY chapterIndex ASC")
    suspend fun getChaptersForMangaDirect(mangaId: String): List<MangaChapterEntity>

    @Query("SELECT * FROM manga_chapters WHERE mangaId = :mangaId AND chapterIndex = :chapterIndex LIMIT 1")
    suspend fun getChapter(mangaId: String, chapterIndex: Int): MangaChapterEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChapters(chapters: List<MangaChapterEntity>)
}
