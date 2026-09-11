package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.data.model.ActiveSessionEntity
import com.example.data.model.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUser(user: UserEntity)

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("SELECT * FROM users WHERE LOWER(username) = LOWER(:username) LIMIT 1")
    suspend fun getUserByUsername(username: String): UserEntity?

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    fun getUserById(id: String): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getUserByIdDirect(id: String): UserEntity?

    @Query("SELECT EXISTS(SELECT 1 FROM users WHERE LOWER(username) = LOWER(:username))")
    suspend fun isUsernameTaken(username: String): Boolean

    @Query("SELECT * FROM active_session WHERE id = 1 LIMIT 1")
    fun getActiveSession(): Flow<ActiveSessionEntity?>

    @Query("SELECT * FROM active_session WHERE id = 1 LIMIT 1")
    suspend fun getActiveSessionDirect(): ActiveSessionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setActiveSession(session: ActiveSessionEntity)

    @Query("DELETE FROM active_session WHERE id = 1")
    suspend fun clearActiveSession()

    @Query("DELETE FROM users WHERE id = :userId")
    suspend fun deleteUserRecord(userId: String)

    @Query("DELETE FROM user_library WHERE userId = :userId")
    suspend fun deleteUserLibrary(userId: String)

    @Query("DELETE FROM reading_progress WHERE userId = :userId")
    suspend fun deleteUserReadingProgress(userId: String)

    @Query("DELETE FROM listening_progress WHERE userId = :userId")
    suspend fun deleteUserListeningProgress(userId: String)

    @Query("DELETE FROM manga_progress WHERE userId = :userId")
    suspend fun deleteUserMangaProgress(userId: String)

    @Query("DELETE FROM bookmarks WHERE userId = :userId")
    suspend fun deleteUserBookmarks(userId: String)

    @Query("DELETE FROM reading_history WHERE userId = :userId")
    suspend fun deleteUserHistory(userId: String)

    @Transaction
    suspend fun deleteEntireAccount(userId: String) {
        deleteUserLibrary(userId)
        deleteUserReadingProgress(userId)
        deleteUserListeningProgress(userId)
        deleteUserMangaProgress(userId)
        deleteUserBookmarks(userId)
        deleteUserHistory(userId)
        deleteUserRecord(userId)
        clearActiveSession()
    }
}
