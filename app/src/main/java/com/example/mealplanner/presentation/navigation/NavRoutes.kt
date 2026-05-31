package com.example.mealplanner.presentation.navigation

object NavRoutes {

    // ── Nested graph identifiers ──────────────────────────────────────────────
    // Used as the `route` parameter of each navigation() block so the NavHost
    // can treat the entire auth flow or main flow as a single destination unit.
    const val AUTH_GRAPH = "auth_graph"
    const val MAIN_GRAPH = "main_graph"

    // ── Auth screens (no bottom nav) ──────────────────────────────────────────
    const val SPLASH        = "splash"
    const val LOGIN         = "login"
    const val SIGNUP        = "signup"

    // ── Main app screens (with bottom nav) ────────────────────────────────────
    const val HOME          = "home"
    const val MEAL_PLANNER  = "meal_planner"
    const val CALORIES_CALC = "calories_calculator"
    const val PROFILE       = "profile"

    // ── Deep screens (back arrow, no bottom nav) ──────────────────────────────
    const val DAY_DETAIL     = "day_detail/{dayName}"
    const val MEAL_SLOT      = "meal_slot/{dayName}/{slotName}"
    const val ADD_MEAL       = "add_meal/{dayName}/{slotName}"
    const val RECIPE_BUILDER = "recipe_builder/{dayName}/{slotName}"
    const val HABITS         = "habits"                 // Lab 11 — opens from Profile → My Habits
    const val WEATHER        = "weather"                // OpenWeather — opens from Profile → Weather Today
    const val COMMUNITY      = "community_recipes"      // Firestore — opens from Profile → Community Recipes

    // ── Parameterised route builders ──────────────────────────────────────────
    fun dayDetail(dayName: String)                       = "day_detail/$dayName"
    fun mealSlot(dayName: String, slotName: String)      = "meal_slot/$dayName/$slotName"
    fun addMeal(dayName: String, slotName: String)       = "add_meal/$dayName/$slotName"
    fun recipeBuilder(dayName: String, slotName: String) = "recipe_builder/$dayName/$slotName"

    // ── Screens that display the bottom navigation bar ────────────────────────
    val bottomNavRoutes = listOf(HOME, MEAL_PLANNER, CALORIES_CALC, PROFILE)
}
