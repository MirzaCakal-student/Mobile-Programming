package com.example.mealplanner.model.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "meals")
data class MealEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "calories") val calories: Int,
    @ColumnInfo(name = "protein_g") val proteinG: Double,
    @ColumnInfo(name = "fat_g") val fatG: Double,
    @ColumnInfo(name = "carbs_g") val carbsG: Double,
    @ColumnInfo(name = "is_custom") val isCustom: Boolean = false
)
