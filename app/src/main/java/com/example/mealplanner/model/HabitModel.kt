package com.example.mealplanner.model

/**
 * Domain model the UI/ViewModel work with.
 * Kept independent of the network DTO so the API can evolve without breaking the UI.
 *
 * Lab 11 mapping (verified against live Swagger UI):
 *   HabitDto.id          → HabitModel.id
 *   HabitDto.title       → HabitModel.title
 *   HabitDto.description → HabitModel.description
 *   HabitDto.frequency   → HabitModel.frequency  (kept as String — e.g. "Daily", "3x/week")
 *   HabitDto.completed   → HabitModel.completed
 *   HabitDto.user_id     → HabitModel.userId
 */
data class HabitModel(
    val id: Int,
    val title: String,
    val description: String,
    val frequency: String,
    val completed: Boolean,
    val userId: Int
)
