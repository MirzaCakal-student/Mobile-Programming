package com.example.mealplanner.model.repository

import com.example.mealplanner.model.DayPlan
import com.example.mealplanner.model.HardcodedData
import com.example.mealplanner.model.Meal
import com.example.mealplanner.model.MealSlotType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * In-memory repository that acts as the single source of truth for the weekly meal plan.
 *
 * All ViewModels that need to read or modify meal plan data go through here —
 * never manipulate the data directly inside a composable.
 *
 * Uses copy-on-write for DayPlan objects so StateFlow always emits on change:
 *   - old.copy(breakfast = (old.breakfast + meal).toMutableList()) creates a new DayPlan
 *   - the new map reference triggers StateFlow emission correctly
 *
 * NOTE: Will be replaced by a Room-backed repository in Assignment 3 Part C.
 */
object InMemoryMealPlanRepository {

    private val _weekPlan = MutableStateFlow(HardcodedData.buildEmptyWeekPlan())
    val weekPlan: StateFlow<Map<String, DayPlan>> = _weekPlan.asStateFlow()

    private var _nextCustomId = 1000

    /** Returns a unique ID for each user-created meal. */
    fun nextCustomId(): Int = _nextCustomId++

    // ── Write operations ──────────────────────────────────────────────────────

    fun addMeal(dayName: String, slotName: String, meal: Meal) {
        val slot = MealSlotType.values().firstOrNull {
            it.displayName.equals(slotName, ignoreCase = true)
        } ?: return

        _weekPlan.value = _weekPlan.value.toMutableMap().apply {
            val old = get(dayName) ?: return@apply
            put(dayName, when (slot) {
                MealSlotType.BREAKFAST -> old.copy(breakfast = (old.breakfast + meal).toMutableList())
                MealSlotType.LUNCH     -> old.copy(lunch     = (old.lunch     + meal).toMutableList())
                MealSlotType.DINNER    -> old.copy(dinner    = (old.dinner    + meal).toMutableList())
                MealSlotType.SNACKS    -> old.copy(snacks    = (old.snacks    + meal).toMutableList())
            })
        }
    }

    fun removeMeal(dayName: String, slotName: String, meal: Meal) {
        val slot = MealSlotType.values().firstOrNull {
            it.displayName.equals(slotName, ignoreCase = true)
        } ?: return

        _weekPlan.value = _weekPlan.value.toMutableMap().apply {
            val old = get(dayName) ?: return@apply
            put(dayName, when (slot) {
                MealSlotType.BREAKFAST -> old.copy(breakfast = old.breakfast.filter { it != meal }.toMutableList())
                MealSlotType.LUNCH     -> old.copy(lunch     = old.lunch.filter     { it != meal }.toMutableList())
                MealSlotType.DINNER    -> old.copy(dinner    = old.dinner.filter    { it != meal }.toMutableList())
                MealSlotType.SNACKS    -> old.copy(snacks    = old.snacks.filter    { it != meal }.toMutableList())
            })
        }
    }

    fun markDayComplete(dayName: String) {
        _weekPlan.value = _weekPlan.value.toMutableMap().apply {
            val old = get(dayName) ?: return@apply
            put(dayName, old.copy(isComplete = true))
        }
    }
}
