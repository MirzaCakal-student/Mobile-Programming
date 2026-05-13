package com.example.mealplanner.model.data.local.dao

import androidx.room.*
import com.example.mealplanner.model.data.local.entity.UserProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProfileDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(profile: UserProfileEntity)

    @Update
    suspend fun update(profile: UserProfileEntity)

    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    fun observe(): Flow<UserProfileEntity?>

    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    suspend fun get(): UserProfileEntity?

    @Query("SELECT COUNT(*) FROM user_profile")
    suspend fun count(): Int
}
