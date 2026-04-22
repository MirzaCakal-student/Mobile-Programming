package com.example.mealplanner.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mealplanner.model.DayPlan
import com.example.mealplanner.model.repository.InMemoryMealPlanRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

// ── ViewModel — scoped to MealPlannerScreen ───────────────────────────────────

class MealPlannerViewModel : ViewModel() {

    /** Full week plan — comes from the shared repository. */
    val weekPlan: StateFlow<Map<String, DayPlan>> = InMemoryMealPlanRepository.weekPlan

    // ── Derived StateFlow ─────────────────────────────────────────────────────

    /** Number of days marked complete — drives the summary bar at the top. */
    val completedDaysCount: StateFlow<Int> = InMemoryMealPlanRepository.weekPlan
        .map { plan -> plan.values.count { it.isComplete } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000L), 0)
}
