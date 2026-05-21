package com.example.mealplanner.model.data.local.dao

import androidx.room.*
import com.example.mealplanner.model.data.local.entity.IngredientEntity

@Dao
interface IngredientDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(ingredients: List<IngredientEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(ingredient: IngredientEntity): Long

    @Update
    suspend fun update(ingredient: IngredientEntity)

    @Delete
    suspend fun delete(ingredient: IngredientEntity)

    @Query("SELECT * FROM ingredients ORDER BY name ASC")
    suspend fun getAll(): List<IngredientEntity>

    @Query("SELECT * FROM ingredients WHERE name LIKE '%' || :query || '%' ORDER BY name ASC")
    suspend fun search(query: String): List<IngredientEntity>

    @Query("SELECT COUNT(*) FROM ingredients")
    suspend fun count(): Int
}
