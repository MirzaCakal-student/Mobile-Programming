package com.example.mealplanner.presentation.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class CaloriesUiState(
    val weight: String        = "",
    val height: String        = "",
    val age: String           = "",
    val gender: String        = "Male",
    val activityLevel: String = "Sedentary",
    val bmr: Double?          = null,
    val tdee: Double?         = null,
    val weightError: String?  = null,
    val heightError: String?  = null,
    val ageError: String?     = null
)

class CaloriesViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(CaloriesUiState())
    val uiState: StateFlow<CaloriesUiState> = _uiState.asStateFlow()

    private val activityMultipliers = mapOf(
        "Sedentary"         to 1.2,
        "Lightly Active"    to 1.375,
        "Moderately Active" to 1.55,
        "Very Active"       to 1.725,
        "Extra Active"      to 1.9
    )

    val activityOptions = activityMultipliers.keys.toList()

    fun onWeightChange(v: String)   { _uiState.update { it.copy(weight = v, weightError = null) } }
    fun onHeightChange(v: String)   { _uiState.update { it.copy(height = v, heightError = null) } }
    fun onAgeChange(v: String)      { _uiState.update { it.copy(age = v, ageError = null) } }
    fun onGenderChange(v: String)   { _uiState.update { it.copy(gender = v) } }
    fun onActivityChange(v: String) { _uiState.update { it.copy(activityLevel = v) } }

    fun onCalculate() {
        val s = _uiState.value
        val w = s.weight.toDoubleOrNull()
        val h = s.height.toDoubleOrNull()
        val a = s.age.toIntOrNull()

        val wErr = if (w == null || w <= 0) "Enter valid weight (kg)" else null
        val hErr = if (h == null || h <= 0) "Enter valid height (cm)" else null
        val aErr = if (a == null || a <= 0) "Enter valid age"         else null

        if (wErr != null || hErr != null || aErr != null) {
            _uiState.update { it.copy(weightError = wErr, heightError = hErr, ageError = aErr) }
            return
        }

        // Mifflin-St Jeor BMR formula
        val bmr = if (s.gender == "Male")
            10 * w!! + 6.25 * h!! - 5 * a!! + 5
        else
            10 * w!! + 6.25 * h!! - 5 * a!! - 161

        val multiplier = activityMultipliers[s.activityLevel] ?: 1.2
        val tdee       = bmr * multiplier

        _uiState.update { it.copy(bmr = bmr, tdee = tdee) }
    }

    fun onReset() { _uiState.update { CaloriesUiState() } }
}
