package com.example.mealplanner.viewmodel

import androidx.lifecycle.ViewModel
import com.example.mealplanner.model.DayPlan
import com.example.mealplanner.model.Ingredient
import com.example.mealplanner.model.Meal
import com.example.mealplanner.model.MealSlotType
import com.example.mealplanner.model.SampleData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class MealPlannerUiState(
    val weekPlan: Map<String, DayPlan>   = SampleData.buildEmptyWeekPlan(),
    val premadeMeals: List<Meal>         = SampleData.premadeMeals,
    val ingredients: List<Ingredient>    = SampleData.ingredients,
    val customMealName: String           = "",
    val customMealCalories: String       = "",
    val customMealProtein: String        = "",
    val customMealFat: String            = "",
    val customMealCarbs: String          = "",
    val customMealNameError: String?     = null,
    val customMealCaloriesError: String? = null,
    val addMealSuccess: Boolean          = false,
    val searchQuery: String              = ""
)

class MealPlannerViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(MealPlannerUiState())
    val uiState: StateFlow<MealPlannerUiState> = _uiState.asStateFlow()

    private var nextCustomMealId = 1000

    fun addPremadeMeal(dayName: String, slotName: String, meal: Meal) {
        val slotType = MealSlotType.values().firstOrNull {
            it.displayName.equals(slotName, ignoreCase = true)
        } ?: return

        _uiState.update { state ->
            val updatedPlan = state.weekPlan.toMutableMap()
            val dayPlan     = updatedPlan[dayName] ?: return@update state
            when (slotType) {
                MealSlotType.BREAKFAST -> dayPlan.breakfast.add(meal)
                MealSlotType.LUNCH     -> dayPlan.lunch.add(meal)
                MealSlotType.DINNER    -> dayPlan.dinner.add(meal)
                MealSlotType.SNACKS    -> dayPlan.snacks.add(meal)
            }
            state.copy(weekPlan = updatedPlan)
        }
    }

    fun removeMeal(dayName: String, slotName: String, meal: Meal) {
        val slotType = MealSlotType.values().firstOrNull {
            it.displayName.equals(slotName, ignoreCase = true)
        } ?: return

        _uiState.update { state ->
            val updatedPlan = state.weekPlan.toMutableMap()
            val dayPlan     = updatedPlan[dayName] ?: return@update state
            when (slotType) {
                MealSlotType.BREAKFAST -> dayPlan.breakfast.remove(meal)
                MealSlotType.LUNCH     -> dayPlan.lunch.remove(meal)
                MealSlotType.DINNER    -> dayPlan.dinner.remove(meal)
                MealSlotType.SNACKS    -> dayPlan.snacks.remove(meal)
            }
            state.copy(weekPlan = updatedPlan)
        }
    }

    fun markDayComplete(dayName: String) {
        _uiState.update { state ->
            val updatedPlan = state.weekPlan.toMutableMap()
            val dayPlan     = updatedPlan[dayName] ?: return@update state
            updatedPlan[dayName] = dayPlan.copy(isComplete = true)
            state.copy(weekPlan = updatedPlan)
        }
    }

    fun onCustomMealNameChange(v: String)     { _uiState.update { it.copy(customMealName = v, customMealNameError = null) } }
    fun onCustomMealCaloriesChange(v: String) { _uiState.update { it.copy(customMealCalories = v, customMealCaloriesError = null) } }
    fun onCustomMealProteinChange(v: String)  { _uiState.update { it.copy(customMealProtein = v) } }
    fun onCustomMealFatChange(v: String)      { _uiState.update { it.copy(customMealFat = v) } }
    fun onCustomMealCarbsChange(v: String)    { _uiState.update { it.copy(customMealCarbs = v) } }
    fun onSearchQueryChange(v: String)        { _uiState.update { it.copy(searchQuery = v) } }

    fun submitCustomMeal(dayName: String, slotName: String) {
        val s = _uiState.value
        var nameErr: String? = null
        var calErr: String?  = null

        if (s.customMealName.isBlank()) nameErr = "Meal name is required"
        val calories = s.customMealCalories.toIntOrNull()
        if (calories == null || calories <= 0) calErr = "Enter valid calorie amount"

        if (nameErr != null || calErr != null) {
            _uiState.update { it.copy(customMealNameError = nameErr, customMealCaloriesError = calErr) }
            return
        }

        val meal = Meal(
            id       = nextCustomMealId++,
            name     = s.customMealName.trim(),
            calories = calories!!,
            proteinG = s.customMealProtein.toDoubleOrNull() ?: 0.0,
            fatG     = s.customMealFat.toDoubleOrNull()     ?: 0.0,
            carbsG   = s.customMealCarbs.toDoubleOrNull()   ?: 0.0,
            isCustom = true
        )
        addPremadeMeal(dayName, slotName, meal)
        clearCustomMealForm()
        _uiState.update { it.copy(addMealSuccess = true) }
    }

    fun resetAddMealSuccess() { _uiState.update { it.copy(addMealSuccess = false) } }

    fun addCustomRecipeAsMeal(
        dayName: String, slotName: String,
        name: String, calories: Int,
        proteinG: Double, fatG: Double, carbsG: Double
    ) {
        val meal = Meal(
            id       = nextCustomMealId++,
            name     = name,
            calories = calories,
            proteinG = proteinG,
            fatG     = fatG,
            carbsG   = carbsG,
            isCustom = true
        )
        addPremadeMeal(dayName, slotName, meal)
    }

    private fun clearCustomMealForm() {
        _uiState.update {
            it.copy(
                customMealName          = "",
                customMealCalories      = "",
                customMealProtein       = "",
                customMealFat           = "",
                customMealCarbs         = "",
                customMealNameError     = null,
                customMealCaloriesError = null
            )
        }
    }

    fun filteredMeals(): List<Meal> {
        val q = _uiState.value.searchQuery.trim()
        return if (q.isBlank()) SampleData.premadeMeals
        else SampleData.premadeMeals.filter { it.name.contains(q, ignoreCase = true) }
    }
}
