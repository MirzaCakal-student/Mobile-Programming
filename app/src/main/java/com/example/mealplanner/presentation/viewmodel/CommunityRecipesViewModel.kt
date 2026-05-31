package com.example.mealplanner.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mealplanner.model.CommunityRecipe
import com.example.mealplanner.model.repository.community.CommunityRecipeRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Sealed UI state, same Init/Loading/Success/Error pattern used everywhere else.
 * `currentUserEmail` is held in state so the screen can show a Delete button
 * only on the recipes the logged-in user authored.
 */
sealed interface CommunityRecipesUiState {
    data object Loading : CommunityRecipesUiState
    data class  Success(val recipes: List<CommunityRecipe>, val currentUserEmail: String) : CommunityRecipesUiState
    data class  Error(val message: String) : CommunityRecipesUiState
}

/**
 * Drives the CommunityRecipes screen.
 *
 * Realtime: the repository returns a Flow backed by Firestore's
 * addSnapshotListener. Whenever any device in the world adds / edits / deletes
 * a recipe, this ViewModel's collect block fires and pushes a new Success state.
 */
@HiltViewModel
class CommunityRecipesViewModel @Inject constructor(
    private val repository: CommunityRecipeRepository,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _uiState = MutableStateFlow<CommunityRecipesUiState>(CommunityRecipesUiState.Loading)
    val uiState: StateFlow<CommunityRecipesUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                repository.observeAll().collect { recipes ->
                    _uiState.value = CommunityRecipesUiState.Success(
                        recipes          = recipes,
                        currentUserEmail = auth.currentUser?.email.orEmpty()
                    )
                }
            } catch (e: CancellationException) { throw e }
            catch (e: Exception)                 {
                _uiState.value = CommunityRecipesUiState.Error(e.message ?: "Failed to load recipes")
            }
        }
    }

    fun addRecipe(title: String, description: String, calories: Int, ingredients: String) {
        if (title.isBlank()) return
        viewModelScope.launch {
            repository.add(title, description, calories, ingredients)
                .onFailure { err ->
                    _uiState.value = CommunityRecipesUiState.Error(
                        err.message ?: "Failed to post recipe"
                    )
                }
            // Success → no need to update state manually; the realtime Flow will re-emit
            // with the new recipe included automatically.
        }
    }

    fun deleteRecipe(documentId: String) {
        viewModelScope.launch {
            repository.delete(documentId)
                .onFailure { err ->
                    _uiState.value = CommunityRecipesUiState.Error(
                        err.message ?: "Failed to delete recipe"
                    )
                }
        }
    }
}
