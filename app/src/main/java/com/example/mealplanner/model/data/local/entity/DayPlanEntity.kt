package com.example.mealplanner.model.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "day_plans")
data class DayPlanEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "day_name") val dayName: String,
    @ColumnInfo(name = "is_complete") val isComplete: Boolean = false,
    @ColumnInfo(name = "sort_order") val sortOrder: Int = 0, // 0=Monday ... 6=Sunday
    @ColumnInfo(name = "eat_time_breakfast") val eatTimeBreakfast: String = "08:00",
    @ColumnInfo(name = "eat_time_lunch") val eatTimeLunch: String = "13:00",
    @ColumnInfo(name = "eat_time_dinner") val eatTimeDinner: String = "19:00",
    @ColumnInfo(name = "eat_time_snacks") val eatTimeSnacks: String = "16:00"
)
