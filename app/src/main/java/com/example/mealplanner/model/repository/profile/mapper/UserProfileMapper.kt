package com.example.mealplanner.model.repository.profile.mapper

import com.example.mealplanner.model.UserProfile
import com.example.mealplanner.model.data.local.entity.UserProfileEntity

fun UserProfileEntity.toDomain(): UserProfile = UserProfile(
    name = name, email = email, ageYears = ageYears,
    weightKg = weightKg, heightCm = heightCm,
    dailyCalorieGoal = dailyCalorieGoal, gender = gender
)

fun UserProfile.toEntity(): UserProfileEntity = UserProfileEntity(
    id = 1, name = name, email = email, ageYears = ageYears,
    weightKg = weightKg, heightCm = heightCm,
    dailyCalorieGoal = dailyCalorieGoal, gender = gender
)
