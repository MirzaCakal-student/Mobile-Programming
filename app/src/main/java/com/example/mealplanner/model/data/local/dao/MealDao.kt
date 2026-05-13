package com.example.mealplanner.model.data.local.dao

import androidx.room.*
import com.example.mealplanner.model.data.local.entity.MealEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MealDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(meals: List<MealEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(meal: MealEntity): Long // returns new row ID

    @Update
    suspend fun update(meal: MealEntity)

    @Delete
    suspend fun delete(meal: MealEntity)

    @Query("SELECT * FROM meals")
    suspend fun getAll(): List<MealEntity>

    @Query("SELECT * FROM meals WHERE is_custom = 0")
    suspend fun getPremade(): List<MealEntity>

    @Query("SELECT * FROM meals WHERE is_custom = 1")
    fun observeCustom(): Flow<List<MealEntity>>

    @Query("SELECT * FROM meals WHERE name LIKE '%' || :query || '%'")
    suspend fun search(query: String): List<MealEntity>

    @Query("SELECT COUNT(*) FROM meals")
    suspend fun count(): Int
}
