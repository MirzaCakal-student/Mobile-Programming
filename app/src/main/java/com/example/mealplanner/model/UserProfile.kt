package com.example.mealplanner.model

/** User profile data used for calorie-target calculations. */
data class UserProfile(
    val name: String          = "",
    val email: String         = "",
    val ageYears: Int         = 0,
    val weightKg: Double      = 0.0,
    val heightCm: Double      = 0.0,
    val dailyCalorieGoal: Int = 2000,
    val gender: String        = "Male"   // "Male" | "Female"
)
