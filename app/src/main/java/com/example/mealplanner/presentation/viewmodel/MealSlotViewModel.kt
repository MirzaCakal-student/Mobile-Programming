package com.example.mealplanner.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mealplanner.model.Meal
import com.example.mealplanner.model.MealSlotType
import com.example.mealplanner.model.repository.dayplan.DayPlanRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface MealSlotUiState {
    data object Init    : MealSlotUiState
    data object Loading : MealSlotUiState
    data class  Success(val slotName: String, val meals: List<Meal>) : MealSlotUiState
    data class  Error(val message: String) : MealSlotUiState
}

sealed interface MealSlotNavigationEvent {
    data object ToAddMeal    : MealSlotNavigationEvent
    data object ToAddRecipe  : MealSlotNavigationEvent
    data object GoBack       : MealSlotNavigationEvent
}

@HiltViewModel
class MealSlotViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: DayPlanRepository
) : ViewModel() {

    val dayName:  String = checkNotNull(savedStateHandle["dayName"])
    val slotName: String = checkNotNull(savedStateHandle["slotName"])

    private val slotType: MealSlotType? = MealSlotType.values().firstOrNull {
        it.displayName.equals(slotName, ignoreCase = true)
    }

    private val _uiState = MutableStateFlow<MealSlotUiState>(MealSlotUiState.Loading)
    val uiState: StateFlow<MealSlotUiState> = _uiState.asStateFlow()

    private val _navEvents = Channel<MealSlotNavigationEvent>(Channel.BUFFERED)
    val navEvents = _navEvents.receiveAsFlow()

    init {
        viewModelScope.launch {
            try {
                repository.observeWeekPlan()
                    .map { plan ->
                        val dayPlan = plan[dayName]
                        slotType?.let { dayPlan?.mealsForSlot(it) } ?: emptyList()
                    }
                    .collect { meals -> _uiState.value = MealSlotUiState.Success(slotName, meals) }
            } catch (e: CancellationException) { throw e }
            catch (e: Exception) { _uiState.value = MealSlotUiState.Error(e.message ?: "Error") }
        }
    }

    fun removeMeal(meal: Meal) {
        viewModelScope.launch { repository.removeMealFromSlot(dayName, slotName, meal) }
    }

    fun onAddMeal()   { viewModelScope.launch { _navEvents.send(MealSlotNavigationEvent.ToAddMeal) } }
    fun onAddRecipe() { viewModelScope.launch { _navEvents.send(MealSlotNavigationEvent.ToAddRecipe) } }
    fun onBack()      { viewModelScope.launch { _navEvents.send(MealSlotNavigationEvent.GoBack) } }
}
