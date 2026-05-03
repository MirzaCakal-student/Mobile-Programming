package com.example.mealplanner.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.example.mealplanner.model.UserProfile
import com.example.mealplanner.model.repository.InMemoryProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

// ── UI State ──────────────────────────────────────────────────────────────────

data class ProfileUiState(
    val profile: UserProfile     = UserProfile(),
    val nameInput: String        = "",
    val emailInput: String       = "",
    val weightInput: String      = "",
    val heightInput: String      = "",
    val ageInput: String         = "",
    val calorieGoalInput: String = "2000",
    val gender: String           = "Male",
    val isSaved: Boolean         = false,
    val nameError: String?       = null,
    val emailError: String?      = null
)

// ── ViewModel — scoped to ProfileScreen ──────────────────────────────────────

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileRepository: InMemoryProfileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        // Initialise form fields from the injected repository's current value
        profileRepository.profile.value.let { p ->
            ProfileUiState(
                profile          = p,
                nameInput        = p.name,
                emailInput       = p.email,
                calorieGoalInput = p.dailyCalorieGoal.toString(),
                gender           = p.gender
            )
        }
    )
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
        val nameErr  = if (s.nameInput.isBlank())       "Name is required"    else null
        val emailErr = if (!s.emailInput.contains("@")) "Valid email required" else null

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
        profileRepository.save(updated)
        _uiState.update { it.copy(profile = updated, isSaved = true) }
    }

    fun resetSaved() { _uiState.update { it.copy(isSaved = false) } }
}
