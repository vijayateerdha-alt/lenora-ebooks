package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
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

@Database(
    entities = [
        BookEntity::class,
        MangaEntity::class,
        MangaChapterEntity::class,
        UserEntity::class,
        ActiveSessionEntity::class,
        UserLibraryEntity::class,
        ReadingProgressEntity::class,
        ListeningProgressEntity::class,
        MangaProgressEntity::class,
        BookmarkEntity::class,
        ReadingHistoryEntity::class,
        SourceEntity::class,
        SyncRunEntity::class,
        UserPreferencesEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bookDao(): BookDao
    abstract fun mangaDao(): MangaDao
    abstract fun userDao(): UserDao
    abstract fun libraryDao(): LibraryDao
    abstract fun progressDao(): ProgressDao
    abstract fun bookmarkDao(): BookmarkDao
    abstract fun historyDao(): HistoryDao
    abstract fun sourceDao(): SourceDao
    abstract fun preferencesDao(): PreferencesDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "bookverse_database.db"
                )
                    .fallbackToDestructiveMigration(true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
