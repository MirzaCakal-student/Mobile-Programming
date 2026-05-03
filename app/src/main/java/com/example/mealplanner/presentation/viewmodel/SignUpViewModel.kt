package com.example.mealplanner.presentation.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

// ── UI State ──────────────────────────────────────────────────────────────────

data class SignUpUiState(
    val name: String                  = "",
    val email: String                 = "",
    val password: String              = "",
    val confirmPassword: String       = "",
    val nameError: String?            = null,
    val emailError: String?           = null,
    val passwordError: String?        = null,
    val confirmPasswordError: String? = null,
    val isLoading: Boolean            = false,
    val signUpSuccess: Boolean        = false
)

// ── ViewModel — scoped to SignUpScreen ────────────────────────────────────────

@HiltViewModel
class SignUpViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(SignUpUiState())
    val uiState: StateFlow<SignUpUiState> = _uiState.asStateFlow()

    fun onNameChange(v: String)            { _uiState.update { it.copy(name            = v, nameError            = null) } }
    fun onEmailChange(v: String)           { _uiState.update { it.copy(email           = v, emailError           = null) } }
    fun onPasswordChange(v: String)        { _uiState.update { it.copy(password        = v, passwordError        = null) } }
    fun onConfirmPasswordChange(v: String) { _uiState.update { it.copy(confirmPassword = v, confirmPasswordError = null) } }

    fun onSubmit() {
        val s = _uiState.value
        val nameErr    = if (s.name.isBlank())                            "Name is required"       else null
        val emailErr   = if (s.email.isBlank() || !s.email.contains("@")) "Enter a valid email"    else null
        val passErr    = if (s.password.length < 6)                       "Minimum 6 characters"   else null
        val confirmErr = if (s.confirmPassword != s.password)             "Passwords do not match" else null

        if (listOf(nameErr, emailErr, passErr, confirmErr).any { it != null }) {
            _uiState.update {
                it.copy(
                    nameError            = nameErr,
                    emailError           = emailErr,
                    passwordError        = passErr,
                    confirmPasswordError = confirmErr
                )
            }
            return
        }

        _uiState.update { it.copy(isLoading = true) }
        // Simulated auth (no real backend yet — will be replaced in a future assignment)
        _uiState.update { it.copy(isLoading = false, signUpSuccess = true) }
    }

    fun resetSignUpSuccess() { _uiState.update { it.copy(signUpSuccess = false) } }
}
