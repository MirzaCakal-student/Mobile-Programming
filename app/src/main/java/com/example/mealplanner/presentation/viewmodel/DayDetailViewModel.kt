package com.example.mealplanner.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mealplanner.model.DayPlan
import com.example.mealplanner.model.repository.InMemoryMealPlanRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

// ── ViewModel — scoped to DayDetailScreen ────────────────────────────────────

/**
 * Receives [dayName] from [SavedStateHandle] which is automatically populated
 * by Navigation Compose from the route argument "day_detail/{dayName}".
 *
 * [InMemoryMealPlanRepository] is injected by Hilt — same singleton instance
 * shared across all ViewModels, so updates from MealSlot/AddMeal are reflected here.
 */
@HiltViewModel
class DayDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: InMemoryMealPlanRepository
) : ViewModel() {

    /** The day this screen is showing — resolved from the navigation argument. */
    val dayName: String = checkNotNull(savedStateHandle["dayName"])

    /** Live plan for this specific day — emits whenever any meal is added or removed. */
    val dayPlan: StateFlow<DayPlan?> = repository.weekPlan
        .map { plan -> plan[dayName] }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000L), null)

    fun markDayComplete() {
        repository.markDayComplete(dayName)
    }
}
