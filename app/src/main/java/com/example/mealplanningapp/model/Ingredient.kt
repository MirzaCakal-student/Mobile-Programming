package com.example.mealplanner.model

/**
 * One of the 50-60 base ingredients users can build recipes from.
 * Nutritional values are per 100 g.
 */
data class Ingredient(
    val id: Int,
    val name: String,
    val caloriesPer100g: Int,
    val proteinPer100g: Double,
    val fatPer100g: Double,
    val carbsPer100g: Double
)