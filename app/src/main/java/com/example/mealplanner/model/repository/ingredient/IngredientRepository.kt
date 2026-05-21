package com.example.mealplanner.model.repository.ingredient

import com.example.mealplanner.model.Ingredient

interface IngredientRepository {
    suspend fun getAll(): List<Ingredient>
    suspend fun search(query: String): List<Ingredient>
}
