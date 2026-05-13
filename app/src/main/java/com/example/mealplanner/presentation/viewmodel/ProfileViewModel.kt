package com.example.mealplanner.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mealplanner.model.UserProfile
import com.example.mealplanner.model.repository.profile.UserProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileFormState(
    val nameInput: String        = "",
    val emailInput: String       = "",
    val weightInput: String      = "",
    val heightInput: String      = "",
    val ageInput: String         = "",
    val calorieGoalInput: String = "2000",
    val gender: String           = "Male",
    val nameError: String?       = null,
    val emailError: String?      = null,
    val isSaved: Boolean         = false
)

sealed interface ProfileUiState {
    data object Init    : ProfileUiState
    data object Loading : ProfileUiState
    data class  Success(val profile: UserProfile, val form: ProfileFormState) : ProfileUiState
    data class  Error(val message: String) : ProfileUiState
}

sealed interface ProfileNavigationEvent {
    data object Logout : ProfileNavigationEvent
}

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileRepository: UserProfileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private val _navEvents = Channel<ProfileNavigationEvent>(Channel.BUFFERED)
    val navEvents = _navEvents.receiveAsFlow()

    init {
        viewModelScope.launch {
            try {
                profileRepository.observe().collect { profile ->
                    val p = profile ?: UserProfile()
                    val current = (_uiState.value as? ProfileUiState.Success)?.form
                    val form = current ?: ProfileFormState(
                        nameInput        = p.name,
                        emailInput       = p.email,
                        weightInput      = if (p.weightKg > 0) p.weightKg.toString() else "",
                        heightInput      = if (p.heightCm > 0) p.heightCm.toString() else "",
                        ageInput         = if (p.ageYears > 0) p.ageYears.toString() else "",
                        calorieGoalInput = p.dailyCalorieGoal.toString(),
                        gender           = p.gender
                    )
                    _uiState.value = ProfileUiState.Success(p, form)
                }
            } catch (e: CancellationException) { throw e }
            catch (e: Exception) { _uiState.value = ProfileUiState.Error(e.message ?: "Error") }
        }
    }

    private val success get() = _uiState.value as? ProfileUiState.Success

    fun onNameChange(v: String)        { success?.let { _uiState.value = it.copy(form = it.form.copy(nameInput        = v, nameError  = null)) } }
    fun onEmailChange(v: String)       { success?.let { _uiState.value = it.copy(form = it.form.copy(emailInput       = v, emailError = null)) } }
    fun onWeightChange(v: String)      { success?.let { _uiState.value = it.copy(form = it.form.copy(weightInput      = v)) } }
    fun onHeightChange(v: String)      { success?.let { _uiState.value = it.copy(form = it.form.copy(heightInput      = v)) } }
    fun onAgeChange(v: String)         { success?.let { _uiState.value = it.copy(form = it.form.copy(ageInput         = v)) } }
    fun onCalorieGoalChange(v: String) { success?.let { _uiState.value = it.copy(form = it.form.copy(calorieGoalInput = v)) } }
    fun onGenderChange(v: String)      { success?.let { _uiState.value = it.copy(form = it.form.copy(gender           = v)) } }

    fun onSaveProfile() {
        val s = success ?: return
        val f = s.form
        val nameErr  = if (f.nameInput.isBlank())       "Name is required"    else null
        val emailErr = if (!f.emailInput.contains("@")) "Valid email required" else null
        if (nameErr != null || emailErr != null) {
            _uiState.value = s.copy(form = f.copy(nameError = nameErr, emailError = emailErr))
            return
        }
        viewModelScope.launch {
            val updated = UserProfile(
                name             = f.nameInput,
                email            = f.emailInput,
                weightKg         = f.weightInput.toDoubleOrNull()   ?: 0.0,
                heightCm         = f.heightInput.toDoubleOrNull()   ?: 0.0,
                ageYears         = f.ageInput.toIntOrNull()         ?: 0,
                dailyCalorieGoal = f.calorieGoalInput.toIntOrNull() ?: 2000,
                gender           = f.gender
            )
            profileRepository.save(updated)
            val newS = (_uiState.value as? ProfileUiState.Success) ?: return@launch
            _uiState.value = newS.copy(form = newS.form.copy(isSaved = true))
        }
    }

    fun resetSaved() {
        success?.let { _uiState.value = it.copy(form = it.form.copy(isSaved = false)) }
    }

    fun onLogout() { viewModelScope.launch { _navEvents.send(ProfileNavigationEvent.Logout) } }
}
