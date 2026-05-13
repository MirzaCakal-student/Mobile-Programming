package com.example.mealplanner.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
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
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface DayDetailUiState {
    data object Init    : DayDetailUiState
    data object Loading : DayDetailUiState
    data class  Success(val dayName: String, val dayPlan: DayPlan) : DayDetailUiState
    data class  Error(val message: String) : DayDetailUiState
}

sealed interface DayDetailNavigationEvent {
    data class ToMealSlot(val dayName: String, val slotName: String) : DayDetailNavigationEvent
    data object GoBack : DayDetailNavigationEvent
}

@HiltViewModel
class DayDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: DayPlanRepository
) : ViewModel() {

    val dayName: String = checkNotNull(savedStateHandle["dayName"])

    private val _uiState = MutableStateFlow<DayDetailUiState>(DayDetailUiState.Loading)
    val uiState: StateFlow<DayDetailUiState> = _uiState.asStateFlow()

    private val _navEvents = Channel<DayDetailNavigationEvent>(Channel.BUFFERED)
    val navEvents = _navEvents.receiveAsFlow()

    init {
        viewModelScope.launch {
            try {
                repository.observeWeekPlan()
                    .map { it[dayName] }
                    .collect { plan ->
                        _uiState.value = if (plan != null)
                            DayDetailUiState.Success(dayName, plan)
                        else
                            DayDetailUiState.Error("Day '$dayName' not found")
                    }
            } catch (e: CancellationException) { throw e }
            catch (e: Exception) { _uiState.value = DayDetailUiState.Error(e.message ?: "Error") }
        }
    }

    fun onSlotClick(slotName: String) {
        viewModelScope.launch { _navEvents.send(DayDetailNavigationEvent.ToMealSlot(dayName, slotName)) }
    }

    fun onBack() { viewModelScope.launch { _navEvents.send(DayDetailNavigationEvent.GoBack) } }

    fun markDayComplete() {
        viewModelScope.launch { repository.markDayComplete(dayName) }
    }
}
