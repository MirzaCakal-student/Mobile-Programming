package com.example.mealplanner.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mealplanner.model.Meal
import com.example.mealplanner.model.MealSlotType
import com.example.mealplanner.model.repository.InMemoryMealPlanRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

// ── ViewModel — scoped to MealSlotScreen ─────────────────────────────────────

/**
 * Receives [dayName] and [slotName] from [SavedStateHandle] which are automatically
 * populated by Navigation Compose from the route "meal_slot/{dayName}/{slotName}".
 */
class MealSlotViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {

    val dayName:  String = checkNotNull(savedStateHandle["dayName"])
    val slotName: String = checkNotNull(savedStateHandle["slotName"])

    private val slotType: MealSlotType? = MealSlotType.values().firstOrNull {
        it.displayName.equals(slotName, ignoreCase = true)
    }

    /** All meals currently in this slot — updates reactively when meals are added or removed. */
    val meals: StateFlow<List<Meal>> = InMemoryMealPlanRepository.weekPlan
        .map { plan ->
            val dayPlan = plan[dayName] ?: return@map emptyList()
            slotType?.let { dayPlan.mealsForSlot(it) } ?: emptyList()
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000L), emptyList())

    fun removeMeal(meal: Meal) {
        InMemoryMealPlanRepository.removeMeal(dayName, slotName, meal)
    }
}
