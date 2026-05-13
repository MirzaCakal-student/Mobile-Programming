package com.example.mealplanner.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CaloriesFormState(
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

sealed interface CaloriesUiState {
    data object Init    : CaloriesUiState
    data class  Form(val form: CaloriesFormState = CaloriesFormState()) : CaloriesUiState
}

// No navigation events needed for Calories screen (no navigation out)

@HiltViewModel
class CaloriesViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow<CaloriesUiState>(CaloriesUiState.Form())
    val uiState: StateFlow<CaloriesUiState> = _uiState.asStateFlow()

    private val activityMultipliers = mapOf(
        "Sedentary"         to 1.2,
        "Lightly Active"    to 1.375,
        "Moderately Active" to 1.55,
        "Very Active"       to 1.725,
        "Extra Active"      to 1.9
    )

    val activityOptions = activityMultipliers.keys.toList()

    private val form get() = (_uiState.value as? CaloriesUiState.Form)?.form ?: CaloriesFormState()

    fun onWeightChange(v: String)   { _uiState.value = CaloriesUiState.Form(form.copy(weight        = v, weightError = null)) }
    fun onHeightChange(v: String)   { _uiState.value = CaloriesUiState.Form(form.copy(height        = v, heightError = null)) }
    fun onAgeChange(v: String)      { _uiState.value = CaloriesUiState.Form(form.copy(age           = v, ageError    = null)) }
    fun onGenderChange(v: String)   { _uiState.value = CaloriesUiState.Form(form.copy(gender        = v)) }
    fun onActivityChange(v: String) { _uiState.value = CaloriesUiState.Form(form.copy(activityLevel = v)) }

    fun onCalculate() {
        val f = form
        val w = f.weight.toDoubleOrNull()
        val h = f.height.toDoubleOrNull()
        val a = f.age.toIntOrNull()
        val wErr = if (w == null || w <= 0) "Enter valid weight (kg)" else null
        val hErr = if (h == null || h <= 0) "Enter valid height (cm)" else null
        val aErr = if (a == null || a <= 0) "Enter valid age"         else null
        if (wErr != null || hErr != null || aErr != null) {
            _uiState.value = CaloriesUiState.Form(f.copy(weightError = wErr, heightError = hErr, ageError = aErr))
            return
        }
        val bmr = if (f.gender == "Male") 10 * w!! + 6.25 * h!! - 5 * a!! + 5
                  else                     10 * w!! + 6.25 * h!! - 5 * a!! - 161
        val tdee = bmr * (activityMultipliers[f.activityLevel] ?: 1.2)
        _uiState.value = CaloriesUiState.Form(f.copy(bmr = bmr, tdee = tdee))
    }

    fun onReset() { _uiState.value = CaloriesUiState.Form() }
}
