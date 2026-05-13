package com.example.mealplanner.model.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "day_meal_cross_ref",
    foreignKeys = [
        ForeignKey(
            entity = DayPlanEntity::class,
            parentColumns = ["id"],
            childColumns = ["day_plan_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = MealEntity::class,
            parentColumns = ["id"],
            childColumns = ["meal_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["day_plan_id"]),
        Index(value = ["meal_id"])
    ]
)
data class DayMealCrossRef(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "day_plan_id") val dayPlanId: Int,
    @ColumnInfo(name = "meal_id") val mealId: Int,
    @ColumnInfo(name = "slot_type") val slotType: String // "Breakfast", "Lunch", "Dinner", "Snacks"
)
