package com.example.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "manga",
    indices = [
        Index(value = ["title"]),
        Index(value = ["author"]),
        Index(value = ["artist"]),
        Index(value = ["genres"]),
        Index(value = ["status"]),
        Index(value = ["popularityScore"]),
        Index(value = ["trendingScore"])
    ]
)
data class MangaEntity(
    @PrimaryKey val id: String,
    val title: String,
    val altTitle: String? = null,
    val author: String,
    val artist: String,
    val description: String,
    val coverImageUrl: String,
    val genres: String, // comma separated e.g. "Action, Fantasy, Shounen"
    val status: String = "Ongoing", // "Ongoing", "Completed"
    val releaseYear: Int = 2021,
    val totalChapters: Int = 12,
    val rating: Float = 4.8f,
    val popularityScore: Int = 85,
    val trendingScore: Int = 85,
    val featuredHero: Boolean = false,
    val source: String = "Open Manga Archive",
    val license: String = "Creative Commons / Public Domain",
    val dateAdded: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "manga_chapters",
    primaryKeys = ["mangaId", "chapterIndex"],
    indices = [
        Index(value = ["mangaId"])
    ]
)
data class MangaChapterEntity(
    val mangaId: String,
    val chapterIndex: Int,
    val chapterNumber: String,
    val title: String,
    val pageCount: Int = 12,
    val releaseDate: String = "2024",
    val pagesData: String = "" // Delimited image URLs or panel descriptions
)
