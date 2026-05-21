package com.example.mealplanner.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mealplanner.model.Meal
import com.example.mealplanner.model.repository.dayplan.DayPlanRepository
import com.example.mealplanner.model.repository.meal.MealRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// Form state embedded in UiState
data class AddMealFormState(
    val searchQuery: String          = "",
    val customName: String           = "",
    val customCalories: String       = "",
    val customProtein: String        = "",
    val customFat: String            = "",
    val customCarbs: String          = "",
    val customNameError: String?     = null,
    val customCaloriesError: String? = null
)

sealed interface AddMealUiState {
    data object Init    : AddMealUiState
    data object Loading : AddMealUiState
    data class  Success(
        val slotName: String,
        val allPremadeMeals: List<Meal>,
        val filteredMeals: List<Meal>,
        val form: AddMealFormState = AddMealFormState()
    ) : AddMealUiState
    data class Error(val message: String) : AddMealUiState
}

sealed interface AddMealNavigationEvent {
    data object GoBack : AddMealNavigationEvent
}

@HiltViewModel
class AddMealViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val dayPlanRepository: DayPlanRepository,
    private val mealRepository: MealRepository
) : ViewModel() {

    val slotName: String    = checkNotNull(savedStateHandle["slotName"])
    private val dayName: String = checkNotNull(savedStateHandle["dayName"])

    private val _uiState = MutableStateFlow<AddMealUiState>(AddMealUiState.Loading)
    val uiState: StateFlow<AddMealUiState> = _uiState.asStateFlow()

    private val _navEvents = Channel<AddMealNavigationEvent>(Channel.BUFFERED)
    val navEvents = _navEvents.receiveAsFlow()

    private var allPremadeMeals: List<Meal> = emptyList()

    init {
        viewModelScope.launch {
            try {
                allPremadeMeals = mealRepository.getAllPremadeMeals()
                _uiState.value = AddMealUiState.Success(
                    slotName        = slotName,
                    allPremadeMeals = allPremadeMeals,
                    filteredMeals   = allPremadeMeals
                )
            } catch (e: CancellationException) { throw e }
            catch (e: Exception) { _uiState.value = AddMealUiState.Error(e.message ?: "Error") }
        }
    }

    fun onSearchQueryChange(v: String) {
        val s = _uiState.value as? AddMealUiState.Success ?: return
        val filtered = if (v.isBlank()) allPremadeMeals
                       else allPremadeMeals.filter { it.name.contains(v, ignoreCase = true) }
        _uiState.value = s.copy(form = s.form.copy(searchQuery = v), filteredMeals = filtered)
    }

    fun onCustomNameChange(v: String)    { val s = _uiState.value as? AddMealUiState.Success ?: return; _uiState.value = s.copy(form = s.form.copy(customName     = v, customNameError     = null)) }
    fun onCustomCalChange(v: String)     { val s = _uiState.value as? AddMealUiState.Success ?: return; _uiState.value = s.copy(form = s.form.copy(customCalories = v, customCaloriesError = null)) }
    fun onCustomProteinChange(v: String) { val s = _uiState.value as? AddMealUiState.Success ?: return; _uiState.value = s.copy(form = s.form.copy(customProtein  = v)) }
    fun onCustomFatChange(v: String)     { val s = _uiState.value as? AddMealUiState.Success ?: return; _uiState.value = s.copy(form = s.form.copy(customFat      = v)) }
    fun onCustomCarbsChange(v: String)   { val s = _uiState.value as? AddMealUiState.Success ?: return; _uiState.value = s.copy(form = s.form.copy(customCarbs    = v)) }

    fun addPremadeMeal(meal: Meal) {
        viewModelScope.launch {
            dayPlanRepository.addMealToSlot(dayName, slotName, meal)
            _navEvents.send(AddMealNavigationEvent.GoBack)
        }
    }

    fun submitCustomMeal() {
        val s = _uiState.value as? AddMealUiState.Success ?: return
        val f = s.form
        val nameErr = if (f.customName.isBlank()) "Meal name is required" else null
        val calErr  = if (f.customCalories.toIntOrNull()?.let { it > 0 } != true) "Enter a valid calorie amount" else null
        if (nameErr != null || calErr != null) {
            _uiState.value = s.copy(form = f.copy(customNameError = nameErr, customCaloriesError = calErr))
            return
        }
        viewModelScope.launch {
            val meal = Meal(
                id       = 0,
                name     = f.customName.trim(),
                calories = f.customCalories.toInt(),
                proteinG = f.customProtein.toDoubleOrNull() ?: 0.0,
                fatG     = f.customFat.toDoubleOrNull()     ?: 0.0,
                carbsG   = f.customCarbs.toDoubleOrNull()   ?: 0.0,
                isCustom = true
            )
            dayPlanRepository.addMealToSlot(dayName, slotName, meal)
            _navEvents.send(AddMealNavigationEvent.GoBack)
        }
    }

    fun onBack() { viewModelScope.launch { _navEvents.send(AddMealNavigationEvent.GoBack) } }
}
