package com.example.mealplanner.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mealplanner.model.Tip
import com.example.mealplanner.model.repository.tips.TipsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI state for the Tips screen.
 * Same sealed-interface pattern used everywhere else in the app — Init/Loading/Success/Error.
 */
sealed interface TipsUiState {
    data object Init    : TipsUiState
    data object Loading : TipsUiState
    data class  Success(val tips: List<Tip>) : TipsUiState
    data class  Error(val message: String)   : TipsUiState
}

/**
 * ViewModel that bridges the Tips REST API to the UI.
 *
 * Each handler launches in `viewModelScope` (cancelled when the ViewModel clears),
 * calls the suspending repository function, and reduces the Result into a new UiState.
 * The screen just collects `uiState` with `collectAsStateWithLifecycle()`.
 */
@HiltViewModel
class TipsViewModel @Inject constructor(
    private val tipsRepository: TipsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<TipsUiState>(TipsUiState.Init)
    val uiState: StateFlow<TipsUiState> = _uiState.asStateFlow()

    init { refresh() }

    /** GET /posts — load and display all tips. */
    fun refresh() {
        viewModelScope.launch {
            _uiState.value = TipsUiState.Loading
            tipsRepository.fetchAll()
                .onSuccess { _uiState.value = TipsUiState.Success(it) }
                .onFailure { _uiState.value = TipsUiState.Error(it.message ?: "Network error") }
        }
    }

    /** POST /posts — push a new tip up to the server, then refresh. */
    fun addTip(title: String, body: String) {
        viewModelScope.launch {
            tipsRepository.create(title, body)
                .onSuccess { refresh() }
                .onFailure { _uiState.value = TipsUiState.Error(it.message ?: "Failed to create tip") }
        }
    }

    /** PUT /posts/{id} — replace an existing tip. */
    fun editTip(id: Int, title: String, body: String) {
        viewModelScope.launch {
            tipsRepository.update(id, title, body)
                .onSuccess { refresh() }
                .onFailure { _uiState.value = TipsUiState.Error(it.message ?: "Failed to update tip") }
        }
    }

    /** DELETE /posts/{id} — remove a tip from the server. */
    fun removeTip(id: Int) {
        viewModelScope.launch {
            tipsRepository.delete(id)
                .onSuccess { refresh() }
                .onFailure { _uiState.value = TipsUiState.Error(it.message ?: "Failed to delete tip") }
        }
    }
}
