package com.example.mealplanner.model.repository.ingredient

import com.example.mealplanner.model.Ingredient
import com.example.mealplanner.model.data.local.dao.IngredientDao
import com.example.mealplanner.model.repository.ingredient.mapper.toDomain
import javax.inject.Inject

class IngredientRepositoryImpl @Inject constructor(
    private val dao: IngredientDao
) : IngredientRepository {
    override suspend fun getAll(): List<Ingredient> = dao.getAll().map { it.toDomain() }
    override suspend fun search(query: String): List<Ingredient> = dao.search(query).map { it.toDomain() }
}
