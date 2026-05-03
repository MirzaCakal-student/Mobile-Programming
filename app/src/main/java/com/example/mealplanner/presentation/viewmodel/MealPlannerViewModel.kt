package com.example.mealplanner.presentation.viewmodel

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

// ── ViewModel — scoped to MealPlannerScreen ───────────────────────────────────

@HiltViewModel
class MealPlannerViewModel @Inject constructor(
    private val repository: InMemoryMealPlanRepository
) : ViewModel() {

    /** Full week plan — comes from the shared repository. */
    val weekPlan: StateFlow<Map<String, DayPlan>> = repository.weekPlan

    // ── Derived StateFlow ─────────────────────────────────────────────────────

    /** Number of days marked complete — drives the summary bar at the top. */
    val completedDaysCount: StateFlow<Int> = repository.weekPlan
        .map { plan -> plan.values.count { it.isComplete } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000L), 0)
}
