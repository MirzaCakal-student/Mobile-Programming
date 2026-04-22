package com.example.mealplanner.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.example.mealplanner.model.UserProfile
import com.example.mealplanner.model.repository.InMemoryProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// ── UI State ──────────────────────────────────────────────────────────────────

data class ProfileUiState(
    val profile: UserProfile     = InMemoryProfileRepository.profile.value,
    val nameInput: String        = InMemoryProfileRepository.profile.value.name,
    val emailInput: String       = InMemoryProfileRepository.profile.value.email,
    val weightInput: String      = "",
    val heightInput: String      = "",
    val ageInput: String         = "",
    val calorieGoalInput: String = InMemoryProfileRepository.profile.value.dailyCalorieGoal.toString(),
    val gender: String           = InMemoryProfileRepository.profile.value.gender,
    val isSaved: Boolean         = false,
    val nameError: String?       = null,
    val emailError: String?      = null
)

// ── ViewModel — scoped to ProfileScreen ──────────────────────────────────────

class ProfileViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    fun onNameChange(v: String)        { _uiState.update { it.copy(nameInput        = v, nameError  = null) } }
    fun onEmailChange(v: String)       { _uiState.update { it.copy(emailInput       = v, emailError = null) } }
    fun onWeightChange(v: String)      { _uiState.update { it.copy(weightInput      = v) } }
    fun onHeightChange(v: String)      { _uiState.update { it.copy(heightInput      = v) } }
    fun onAgeChange(v: String)         { _uiState.update { it.copy(ageInput         = v) } }
    fun onCalorieGoalChange(v: String) { _uiState.update { it.copy(calorieGoalInput = v) } }
    fun onGenderChange(v: String)      { _uiState.update { it.copy(gender           = v) } }

    fun onSaveProfile() {
        val s = _uiState.value
        val nameErr  = if (s.nameInput.isBlank())       "Name is required"      else null
        val emailErr = if (!s.emailInput.contains("@")) "Valid email required"   else null

        if (nameErr != null || emailErr != null) {
            _uiState.update { it.copy(nameError = nameErr, emailError = emailErr) }
            return
        }

        val updated = UserProfile(
            name             = s.nameInput,
            email            = s.emailInput,
            weightKg         = s.weightInput.toDoubleOrNull()   ?: 0.0,
            heightCm         = s.heightInput.toDoubleOrNull()   ?: 0.0,
            ageYears         = s.ageInput.toIntOrNull()         ?: 0,
            dailyCalorieGoal = s.calorieGoalInput.toIntOrNull() ?: 2000,
            gender           = s.gender
        )
        // Write to repository so HomeViewModel's profile StateFlow updates automatically
        InMemoryProfileRepository.save(updated)
        _uiState.update { it.copy(profile = updated, isSaved = true) }
    }

    fun resetSaved() { _uiState.update { it.copy(isSaved = false) } }
}
