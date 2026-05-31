package com.example.mealplanner.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mealplanner.model.HabitModel
import com.example.mealplanner.model.data.remote.dto.CreateHabitDto
import com.example.mealplanner.model.data.remote.dto.UpdateHabitDto
import com.example.mealplanner.model.data.remote.mapper.toModel
import com.example.mealplanner.model.repository.habit.HabitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Lab 11 UI-state pattern — sealed interface with Init / Loading / Success / Error.
 * Matches the convention used by HomeViewModel, LoginViewModel, etc.
 */
sealed interface HabitsUiState {
    data object Init    : HabitsUiState
    data object Loading : HabitsUiState
    data class  Success(val habits: List<HabitModel>) : HabitsUiState
    data class  Error(val message: String)            : HabitsUiState
}

/**
 * ViewModel that bridges the Habits REST API to the UI.
 *
 * Every network call follows the exact lifecycle the lab prescribes:
 *   1. Set state to Loading before the call
 *   2. Wrap in try/catch
 *   3. Always rethrow CancellationException (so coroutine cancellation still works)
 *   4. Success → Success(mapped result)
 *   5. Failure → Error(message)
 *
 * The DTO never leaks past this boundary — calls go through `.toModel()` so the
 * UI only ever sees `HabitModel`.
 */
@HiltViewModel
class HabitsViewModel @Inject constructor(
    private val habitRepository: HabitRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<HabitsUiState>(HabitsUiState.Init)
    val uiState: StateFlow<HabitsUiState> = _uiState.asStateFlow()

    init { refresh() }

    /** GET /habits/ → maps every DTO to a domain model and publishes Success. */
    fun refresh() {
        viewModelScope.launch {
            _uiState.value = HabitsUiState.Loading
            try {
                val habits = habitRepository.getHabits().map { it.toModel() }
                _uiState.value = HabitsUiState.Success(habits)
            } catch (e: CancellationException) {
                throw e // never swallow — keeps structured concurrency intact
            } catch (e: Exception) {
                _uiState.value = HabitsUiState.Error(e.message ?: "Failed to load habits")
            }
        }
    }

    /** POST /habits/ — push a new habit, then refresh the list. */
    fun addHabit(
        title: String,
        description: String,
        frequency: String,
        userId: Int = 1
    ) {
        if (title.isBlank()) return
        viewModelScope.launch {
            try {
                habitRepository.createHabit(
                    CreateHabitDto(
                        title       = title.trim(),
                        description = description.trim(),
                        frequency   = frequency.trim().ifBlank { "Daily" },
                        completed   = false,
                        userId      = userId
                    )
                )
                refresh()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.value = HabitsUiState.Error(e.message ?: "Failed to create habit")
            }
        }
    }

    /** PUT /habits/{id} — partial update; only non-null fields are sent over the wire. */
    fun editHabit(
        id: Int,
        title: String? = null,
        description: String? = null,
        frequency: String? = null,
        completed: Boolean? = null
    ) {
        viewModelScope.launch {
            try {
                habitRepository.updateHabit(
                    id,
                    UpdateHabitDto(
                        title       = title?.trim(),
                        description = description?.trim(),
                        frequency   = frequency?.trim(),
                        completed   = completed
                    )
                )
                refresh()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.value = HabitsUiState.Error(e.message ?: "Failed to update habit")
            }
        }
    }

    /** Convenience: flip the `completed` flag without touching anything else. */
    fun toggleCompleted(habit: HabitModel) {
        editHabit(id = habit.id, completed = !habit.completed)
    }

    /** DELETE /habits/{id} — remove a habit and refresh. */
    fun removeHabit(id: Int) {
        viewModelScope.launch {
            try {
                habitRepository.deleteHabit(id)
                refresh()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.value = HabitsUiState.Error(e.message ?: "Failed to delete habit")
            }
        }
    }
}
