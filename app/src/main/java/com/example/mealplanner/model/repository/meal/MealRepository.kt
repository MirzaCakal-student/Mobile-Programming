package com.example.mealplanner.model.repository.meal

import com.example.mealplanner.model.Meal
import kotlinx.coroutines.flow.Flow

interface MealRepository {
    suspend fun getAllPremadeMeals(): List<Meal>
    suspend fun search(query: String): List<Meal>
    suspend fun addMeal(meal: Meal): Long
    suspend fun deleteMeal(meal: Meal)
    fun observeCustomMeals(): Flow<List<Meal>>
}
