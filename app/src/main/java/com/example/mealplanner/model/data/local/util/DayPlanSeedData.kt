package com.example.mealplanner.model.data.local.util

import com.example.mealplanner.model.data.local.entity.DayPlanEntity

object DayPlanSeedData {
    val dayPlans = listOf(
        DayPlanEntity(dayName = "Monday",    sortOrder = 0),
        DayPlanEntity(dayName = "Tuesday",   sortOrder = 1),
        DayPlanEntity(dayName = "Wednesday", sortOrder = 2),
        DayPlanEntity(dayName = "Thursday",  sortOrder = 3),
        DayPlanEntity(dayName = "Friday",    sortOrder = 4),
        DayPlanEntity(dayName = "Saturday",  sortOrder = 5),
        DayPlanEntity(dayName = "Sunday",    sortOrder = 6)
    )
}
