package com.example.mealplanner.model.data.remote.mapper

import com.example.mealplanner.model.HabitModel
import com.example.mealplanner.model.data.remote.dto.HabitDto

/**
 * Lab 11 mapping — converts the wire-format DTO into the UI-facing domain model.
 * Straight 1:1 passthrough; the only real work is the snake_case → camelCase
 * (`user_id` → `userId`) which @SerializedName already handles in the DTO.
 */
fun HabitDto.toModel(): HabitModel = HabitModel(
    id          = id,
    title       = title,
    description = description,
    frequency   = frequency,
    completed   = completed,
    userId      = userId
)
