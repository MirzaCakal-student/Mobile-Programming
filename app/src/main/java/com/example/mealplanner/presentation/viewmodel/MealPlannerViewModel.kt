package com.example.mealplanner.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mealplanner.model.DayPlan
import com.example.mealplanner.model.repository.dayplan.DayPlanRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface MealPlannerUiState {
    data object Init    : MealPlannerUiState
    data object Loading : MealPlannerUiState
    data class  Success(
        val weekPlan: Map<String, DayPlan>,
        val completedDaysCount: Int
    ) : MealPlannerUiState
    data class Error(val message: String) : MealPlannerUiState
}

sealed interface MealPlannerNavigationEvent {
    data class ToDayDetail(val dayName: String) : MealPlannerNavigationEvent
}

@HiltViewModel
class MealPlannerViewModel @Inject constructor(
    private val repository: DayPlanRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<MealPlannerUiState>(MealPlannerUiState.Loading)
    val uiState: StateFlow<MealPlannerUiState> = _uiState.asStateFlow()

    private val _navEvents = Channel<MealPlannerNavigationEvent>(Channel.BUFFERED)
    val navEvents = _navEvents.receiveAsFlow()

    init {
        viewModelScope.launch {
            try {
                repository.observeWeekPlan().collect { weekPlan ->
                    _uiState.value = MealPlannerUiState.Success(
                        weekPlan           = weekPlan,
                        completedDaysCount = weekPlan.values.count { it.isComplete }
                    )
                }
            } catch (e: CancellationException) { throw e }
            catch (e: Exception) { _uiState.value = MealPlannerUiState.Error(e.message ?: "Error") }
        }
    }

    fun onDayClick(dayName: String) { viewModelScope.launch { _navEvents.send(MealPlannerNavigationEvent.ToDayDetail(dayName)) } }
}
