package com.example.mealplanner.model

/**
 * Represents a single meal (either pre-made or user-created).
 * For Assignment 1/2 – data is hardcoded; in later assignments this
 * will come from a Room database.
 */
data class Meal(
    val id: Int,
    val name: String,
    val calories: Int,        // kcal
    val proteinG: Double,     // grams
    val fatG: Double,         // grams
    val carbsG: Double,       // grams
    val isCustom: Boolean = false,
    val imageRes: String = "" // resource name; used in later assignments
)
