package com.example.mealplanner.model

/**
 * Holds the complete meal plan for one day of the week.
 */
data class DayPlan(
    val dayName: String,                            // e.g. "Monday"
    val breakfast: MutableList<Meal> = mutableListOf(),
    val lunch: MutableList<Meal>     = mutableListOf(),
    val dinner: MutableList<Meal>    = mutableListOf(),
    val snacks: MutableList<Meal>    = mutableListOf(),
    val isComplete: Boolean          = false,
    val eatTimeBreakfast: String     = "08:00",
    val eatTimeLunch: String         = "13:00",
    val eatTimeDinner: String        = "19:00",
    val eatTimeSnacks: String        = "16:00"
) {
    /** Total calories across all slots for this day. */
    val totalCalories: Int
        get() = (breakfast + lunch + dinner + snacks).sumOf { it.calories }

    val totalProtein: Double
        get() = (breakfast + lunch + dinner + snacks).sumOf { it.proteinG }

    val totalFat: Double
        get() = (breakfast + lunch + dinner + snacks).sumOf { it.fatG }

    val totalCarbs: Double
        get() = (breakfast + lunch + dinner + snacks).sumOf { it.carbsG }

    fun mealsForSlot(slot: MealSlotType): List<Meal> = when (slot) {
        MealSlotType.BREAKFAST -> breakfast
        MealSlotType.LUNCH     -> lunch
        MealSlotType.DINNER    -> dinner
        MealSlotType.SNACKS    -> snacks
    }
}