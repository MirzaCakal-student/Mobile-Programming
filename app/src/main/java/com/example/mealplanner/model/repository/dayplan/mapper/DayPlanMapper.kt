package com.example.mealplanner.model.repository.dayplan.mapper

import com.example.mealplanner.model.DayPlan
import com.example.mealplanner.model.Meal
import com.example.mealplanner.model.data.local.dao.MealWithSlot
import com.example.mealplanner.model.data.local.entity.DayPlanEntity

fun MealWithSlot.toMeal(): Meal = Meal(
    id = id, name = name, calories = calories,
    proteinG = proteinG, fatG = fatG, carbsG = carbsG, isCustom = isCustom
)

fun DayPlanEntity.toDomain(
    breakfast: List<Meal> = emptyList(),
    lunch: List<Meal> = emptyList(),
    dinner: List<Meal> = emptyList(),
    snacks: List<Meal> = emptyList()
): DayPlan = DayPlan(
    dayName = dayName,
    breakfast = breakfast.toMutableList(),
    lunch = lunch.toMutableList(),
    dinner = dinner.toMutableList(),
    snacks = snacks.toMutableList(),
    isComplete = isComplete,
    eatTimeBreakfast = eatTimeBreakfast,
    eatTimeLunch = eatTimeLunch,
    eatTimeDinner = eatTimeDinner,
    eatTimeSnacks = eatTimeSnacks
)
