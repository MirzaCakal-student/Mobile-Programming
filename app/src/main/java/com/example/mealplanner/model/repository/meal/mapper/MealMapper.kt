package com.example.mealplanner.model.repository.meal.mapper

import com.example.mealplanner.model.Meal
import com.example.mealplanner.model.data.local.entity.MealEntity

fun MealEntity.toDomain(): Meal = Meal(
    id = id, name = name, calories = calories,
    proteinG = proteinG, fatG = fatG, carbsG = carbsG, isCustom = isCustom
)

fun Meal.toEntity(): MealEntity = MealEntity(
    id = if (isCustom) 0 else id, // custom meals get auto-generated ID
    name = name, calories = calories,
    proteinG = proteinG, fatG = fatG, carbsG = carbsG, isCustom = isCustom
)
