package com.example.mealplanner.model.repository.ingredient.mapper

import com.example.mealplanner.model.Ingredient
import com.example.mealplanner.model.data.local.entity.IngredientEntity

fun IngredientEntity.toDomain(): Ingredient = Ingredient(
    id = id, name = name, caloriesPer100g = caloriesPer100g,
    proteinPer100g = proteinPer100g, carbsPer100g = carbsPer100g, fatPer100g = fatPer100g
)

fun Ingredient.toEntity(): IngredientEntity = IngredientEntity(
    id = 0, name = name, caloriesPer100g = caloriesPer100g,
    proteinPer100g = proteinPer100g, carbsPer100g = carbsPer100g, fatPer100g = fatPer100g
)
