package com.example.mealplanner.presentation.navigation

object NavRoutes {
    // ── Auth (no bottom nav) ──────────────────────────────────────────────────
    const val SPLASH         = "splash"
    const val LOGIN          = "login"
    const val SIGNUP         = "signup"

    // ── Main app (with bottom nav) ────────────────────────────────────────────
    const val HOME           = "home"
    const val MEAL_PLANNER   = "meal_planner"
    const val CALORIES_CALC  = "calories_calculator"
    const val PROFILE        = "profile"

    // ── Deep screens (back arrow instead of bottom nav) ───────────────────────
    const val DAY_DETAIL     = "day_detail/{dayName}"
    const val MEAL_SLOT      = "meal_slot/{dayName}/{slotName}"
    const val ADD_MEAL       = "add_meal/{dayName}/{slotName}"
    const val RECIPE_BUILDER = "recipe_builder/{dayName}/{slotName}"

    // ── Parameterised route builders ──────────────────────────────────────────
    fun dayDetail(dayName: String)                        = "day_detail/$dayName"
    fun mealSlot(dayName: String, slotName: String)       = "meal_slot/$dayName/$slotName"
    fun addMeal(dayName: String, slotName: String)        = "add_meal/$dayName/$slotName"
    fun recipeBuilder(dayName: String, slotName: String)  = "recipe_builder/$dayName/$slotName"

    // ── Bottom nav root routes ─────────────────────────────────────────────────
    val bottomNavRoutes = listOf(HOME, MEAL_PLANNER, CALORIES_CALC, PROFILE)
}
