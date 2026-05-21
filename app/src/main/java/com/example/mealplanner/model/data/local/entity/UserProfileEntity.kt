package com.example.mealplanner.model.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey
    val id: Int = 1, // single user app — always id = 1
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "email") val email: String,
    @ColumnInfo(name = "weight_kg") val weightKg: Double = 0.0,
    @ColumnInfo(name = "height_cm") val heightCm: Double = 0.0,
    @ColumnInfo(name = "age_years") val ageYears: Int = 0,
    @ColumnInfo(name = "daily_calorie_goal") val dailyCalorieGoal: Int = 2000,
    @ColumnInfo(name = "gender") val gender: String = "Male"
)
