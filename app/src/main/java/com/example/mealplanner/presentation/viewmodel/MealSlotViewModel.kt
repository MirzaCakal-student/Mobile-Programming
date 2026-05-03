package com.example.mealplanner.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mealplanner.model.Meal
import com.example.mealplanner.model.MealSlotType
import com.example.mealplanner.model.repository.InMemoryMealPlanRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

// ── ViewModel — scoped to MealSlotScreen ─────────────────────────────────────

/**
 * Receives [dayName] and [slotName] from [SavedStateHandle] which are automatically
 * populated by Navigation Compose from the route "meal_slot/{dayName}/{slotName}".
 *
 * [InMemoryMealPlanRepository] is injected by Hilt.
 */
@HiltViewModel
class MealSlotViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: InMemoryMealPlanRepository
) : ViewModel() {

    val dayName:  String = checkNotNull(savedStateHandle["dayName"])
    val slotName: String = checkNotNull(savedStateHandle["slotName"])

    private val slotType: MealSlotType? = MealSlotType.values().firstOrNull {
        it.displayName.equals(slotName, ignoreCase = true)
    }

    /** All meals currently in this slot — updates reactively when meals are added or removed. */
    val meals: StateFlow<List<Meal>> = repository.weekPlan
        .map { plan ->
            val dayPlan = plan[dayName] ?: return@map emptyList()
            slotType?.let { dayPlan.mealsForSlot(it) } ?: emptyList()
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000L), emptyList())

    fun removeMeal(meal: Meal) {
        repository.removeMeal(dayName, slotName, meal)
    }
}
