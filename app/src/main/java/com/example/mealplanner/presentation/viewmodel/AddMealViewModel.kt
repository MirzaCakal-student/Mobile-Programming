package com.example.mealplanner.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mealplanner.model.HardcodedData
import com.example.mealplanner.model.Meal
import com.example.mealplanner.model.repository.InMemoryMealPlanRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

// ── UI State ──────────────────────────────────────────────────────────────────

data class AddMealUiState(
    val searchQuery: String          = "",
    val customName: String           = "",
    val customCalories: String       = "",
    val customProtein: String        = "",
    val customFat: String            = "",
    val customCarbs: String          = "",
    val customNameError: String?     = null,
    val customCaloriesError: String? = null,
    val addSuccess: Boolean          = false
)

// ── ViewModel — scoped to AddMealScreen ──────────────────────────────────────

/**
 * Receives [dayName] and [slotName] from [SavedStateHandle] which are automatically
 * populated by Navigation Compose from the route "add_meal/{dayName}/{slotName}".
 *
 * [InMemoryMealPlanRepository] is injected by Hilt.
 */
@HiltViewModel
class AddMealViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: InMemoryMealPlanRepository
) : ViewModel() {

    val slotName: String    = checkNotNull(savedStateHandle["slotName"])
    private val dayName: String = checkNotNull(savedStateHandle["dayName"])

    private val _uiState = MutableStateFlow(AddMealUiState())
    val uiState: StateFlow<AddMealUiState> = _uiState.asStateFlow()

    // ── Derived StateFlow — filtered meals (no filtering logic in the composable) ──

    val filteredMeals: StateFlow<List<Meal>> = _uiState
        .map { state ->
            val q = state.searchQuery.trim()
            if (q.isBlank()) HardcodedData.premadeMeals
            else HardcodedData.premadeMeals.filter { it.name.contains(q, ignoreCase = true) }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000L), HardcodedData.premadeMeals)

    // ── Input handlers ────────────────────────────────────────────────────────

    fun onSearchQueryChange(v: String)   { _uiState.update { it.copy(searchQuery    = v) } }
    fun onCustomNameChange(v: String)    { _uiState.update { it.copy(customName     = v, customNameError     = null) } }
    fun onCustomCalChange(v: String)     { _uiState.update { it.copy(customCalories = v, customCaloriesError = null) } }
    fun onCustomProteinChange(v: String) { _uiState.update { it.copy(customProtein  = v) } }
    fun onCustomFatChange(v: String)     { _uiState.update { it.copy(customFat      = v) } }
    fun onCustomCarbsChange(v: String)   { _uiState.update { it.copy(customCarbs    = v) } }

    // ── Actions ───────────────────────────────────────────────────────────────

    fun addPremadeMeal(meal: Meal) {
        repository.addMeal(dayName, slotName, meal)
        _uiState.update { it.copy(addSuccess = true) }
    }

    fun submitCustomMeal() {
        val s = _uiState.value
        val nameErr = if (s.customName.isBlank()) "Meal name is required" else null
        val calErr  = if (s.customCalories.toIntOrNull()?.let { it > 0 } != true)
            "Enter a valid calorie amount" else null

        if (nameErr != null || calErr != null) {
            _uiState.update { it.copy(customNameError = nameErr, customCaloriesError = calErr) }
            return
        }

        val meal = Meal(
            id       = repository.nextCustomId(),
            name     = s.customName.trim(),
            calories = s.customCalories.toInt(),
            proteinG = s.customProtein.toDoubleOrNull() ?: 0.0,
            fatG     = s.customFat.toDoubleOrNull()     ?: 0.0,
            carbsG   = s.customCarbs.toDoubleOrNull()   ?: 0.0,
            isCustom = true
        )
        repository.addMeal(dayName, slotName, meal)
        _uiState.update { AddMealUiState(addSuccess = true) }
    }

    fun resetAddSuccess() { _uiState.update { it.copy(addSuccess = false) } }
}
