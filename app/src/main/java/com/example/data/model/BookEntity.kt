package com.example.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "books",
    indices = [
        Index(value = ["title"]),
        Index(value = ["author"]),
        Index(value = ["genres"]),
        Index(value = ["source"]),
        Index(value = ["language"]),
        Index(value = ["hasAudiobook"]),
        Index(value = ["popularityScore"])
    ]
)
data class BookEntity(
    @PrimaryKey val id: String,
    val title: String,
    val subtitle: String? = null,
    val author: String,
    val authors: String, // comma separated or single author
    val description: String,
    val coverImageUrl: String,
    val thumbnailUrl: String,
    val genres: String, // comma separated e.g. "Classics, Romance, Drama"
    val subjects: String,
    val language: String = "English",
    val publicationYear: Int = 1900,
    val publisher: String? = null,
    val hasEbook: Boolean = true,
    val ebookUrl: String? = null,
    val hasAudiobook: Boolean = false,
    val audiobookUrl: String? = null,
    val audiobookDurationSeconds: Long = 0L,
    val narrator: String? = null,
    val source: String, // e.g. "Project Gutenberg", "LibriVox", "Standard Ebooks"
    val sourceId: String,
    val sourceUrl: String,
    val licenseInfo: String = "Public Domain",
    val dateAdded: Long = System.currentTimeMillis(),
    val dateUpdated: Long = System.currentTimeMillis(),
    val lastSynchronized: Long = System.currentTimeMillis(),
    val popularityScore: Int = 80,
    val trendingScore: Int = 80,
    val featuredHero: Boolean = false,
    val isHidden: Boolean = false,
    val sampleContent: String = "" // Full text sample or chapters for reading
)
