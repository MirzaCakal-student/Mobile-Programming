package com.example.mealplanner.model.repository.dayplan

import com.example.mealplanner.model.DayPlan
import com.example.mealplanner.model.Meal
import kotlinx.coroutines.flow.Flow

interface DayPlanRepository {
    fun observeWeekPlan(): Flow<Map<String, DayPlan>>
    suspend fun addMealToSlot(dayName: String, slotName: String, meal: Meal)
    suspend fun removeMealFromSlot(dayName: String, slotName: String, meal: Meal)
    suspend fun markDayComplete(dayName: String)
    suspend fun getNextCustomMealId(): Int
}
