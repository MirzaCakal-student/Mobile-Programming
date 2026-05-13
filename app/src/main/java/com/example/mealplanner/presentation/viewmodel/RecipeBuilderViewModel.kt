package com.example.mealplanner.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mealplanner.model.Ingredient
import com.example.mealplanner.model.Meal
import com.example.mealplanner.model.repository.dayplan.DayPlanRepository
import com.example.mealplanner.model.repository.ingredient.IngredientRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface RecipeBuilderUiState {
    data object Init    : RecipeBuilderUiState
    data object Loading : RecipeBuilderUiState
    data class  Success(val ingredients: List<Ingredient>) : RecipeBuilderUiState
    data class  Error(val message: String) : RecipeBuilderUiState
}

sealed interface RecipeBuilderNavigationEvent {
    data object GoBack : RecipeBuilderNavigationEvent
}

@HiltViewModel
class RecipeBuilderViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val dayPlanRepository: DayPlanRepository,
    private val ingredientRepository: IngredientRepository
) : ViewModel() {

    private val dayName:  String = checkNotNull(savedStateHandle["dayName"])
    private val slotName: String = checkNotNull(savedStateHandle["slotName"])

    private val _uiState = MutableStateFlow<RecipeBuilderUiState>(RecipeBuilderUiState.Loading)
    val uiState: StateFlow<RecipeBuilderUiState> = _uiState.asStateFlow()

    private val _navEvents = Channel<RecipeBuilderNavigationEvent>(Channel.BUFFERED)
    val navEvents = _navEvents.receiveAsFlow()

    init {
        viewModelScope.launch {
            try {
                val ingredients = ingredientRepository.getAll()
                _uiState.value = RecipeBuilderUiState.Success(ingredients)
            } catch (e: CancellationException) { throw e }
            catch (e: Exception) { _uiState.value = RecipeBuilderUiState.Error(e.message ?: "Error") }
        }
    }

    fun saveRecipe(name: String, calories: Int, proteinG: Double, fatG: Double, carbsG: Double) {
        viewModelScope.launch {
            val meal = Meal(
                id = 0, name = name, calories = calories,
                proteinG = proteinG, fatG = fatG, carbsG = carbsG, isCustom = true
            )
            dayPlanRepository.addMealToSlot(dayName, slotName, meal)
            _navEvents.send(RecipeBuilderNavigationEvent.GoBack)
        }
    }

    fun onBack() { viewModelScope.launch { _navEvents.send(RecipeBuilderNavigationEvent.GoBack) } }
}
