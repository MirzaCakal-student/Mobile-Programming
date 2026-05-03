package com.example.mealplanner.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mealplanner.model.DayPlan
import com.example.mealplanner.model.UserProfile
import com.example.mealplanner.model.repository.InMemoryMealPlanRepository
import com.example.mealplanner.model.repository.InMemoryProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

// ── ViewModel — scoped to HomeScreen ─────────────────────────────────────────

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val mealPlanRepository: InMemoryMealPlanRepository,
    private val profileRepository: InMemoryProfileRepository
) : ViewModel() {

    /** Logged-in user's profile (name, calorie goal, etc.). */
    val profile: StateFlow<UserProfile> = profileRepository.profile

    /** Full week plan — reactive to any meal additions or day completions. */
    val weekPlan: StateFlow<Map<String, DayPlan>> = mealPlanRepository.weekPlan

    // ── Derived StateFlows (computed — never stored separately) ───────────────

    /** Number of days the user has marked as complete this week. */
    val completedDaysCount: StateFlow<Int> = mealPlanRepository.weekPlan
        .map { plan -> plan.values.count { it.isComplete } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000L), 0)

    /** Sum of all calories logged across the entire week. */
    val totalWeeklyCalories: StateFlow<Int> = mealPlanRepository.weekPlan
        .map { plan -> plan.values.sumOf { it.totalCalories } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000L), 0)

    /** Total number of meals logged across all days this week. */
    val totalWeeklyMeals: StateFlow<Int> = mealPlanRepository.weekPlan
        .map { plan -> plan.values.sumOf { it.totalMealCount } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000L), 0)
}
