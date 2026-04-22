package com.example.mealplanner.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.mealplanner.model.Meal
import com.example.mealplanner.model.repository.InMemoryMealPlanRepository

// ── ViewModel — scoped to RecipeBuilderScreen ─────────────────────────────────

/**
 * Receives [dayName] and [slotName] from [SavedStateHandle] which are automatically
 * populated by Navigation Compose from the route "recipe_builder/{dayName}/{slotName}".
 *
 * The ingredient list and recipe name are kept as local composable state inside
 * RecipeBuilderScreen because they are ephemeral form state that does not need to
 * survive configuration changes. This ViewModel's sole responsibility is to persist
 * the finished recipe to the shared meal plan repository.
 */
class RecipeBuilderViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {

    private val dayName:  String = checkNotNull(savedStateHandle["dayName"])
    private val slotName: String = checkNotNull(savedStateHandle["slotName"])

    /**
     * Called when the user taps "Save Recipe".
     * Creates a custom [Meal] from the recipe's totals and adds it to the correct slot.
     */
    fun saveRecipe(
        name: String,
        calories: Int,
        proteinG: Double,
        fatG: Double,
        carbsG: Double
    ) {
        val meal = Meal(
            id       = InMemoryMealPlanRepository.nextCustomId(),
            name     = name,
            calories = calories,
            proteinG = proteinG,
            fatG     = fatG,
            carbsG   = carbsG,
            isCustom = true
        )
        InMemoryMealPlanRepository.addMeal(dayName, slotName, meal)
    }
}
