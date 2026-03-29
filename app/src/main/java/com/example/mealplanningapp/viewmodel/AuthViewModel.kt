// ============================================================
// FILE: app/src/main/java/com/example/mealplanner/viewmodel/AuthViewModel.kt
// ============================================================
package com.example.mealplanner.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// ── State ────────────────────────────────────────────────────

data class LoginUiState(
    val email: String       = "",
    val password: String    = "",
    val emailError: String? = null,
    val passwordError: String? = null,
    val isLoading: Boolean  = false,
    val loginSuccess: Boolean = false,
    val generalError: String? = null
)

data class SignUpUiState(
    val name: String              = "",
    val email: String             = "",
    val password: String          = "",
    val confirmPassword: String   = "",
    val nameError: String?        = null,
    val emailError: String?       = null,
    val passwordError: String?    = null,
    val confirmPasswordError: String? = null,
    val isLoading: Boolean        = false,
    val signUpSuccess: Boolean    = false
)

// ── ViewModel ────────────────────────────────────────────────

class AuthViewModel : ViewModel() {

    private val _loginState  = MutableStateFlow(LoginUiState())
    val loginState: StateFlow<LoginUiState> = _loginState.asStateFlow()

    private val _signUpState = MutableStateFlow(SignUpUiState())
    val signUpState: StateFlow<SignUpUiState> = _signUpState.asStateFlow()

    // ── Login inputs ──────────────────────────────────────────
    fun onLoginEmailChange(value: String) {
        _loginState.update { it.copy(email = value, emailError = null, generalError = null) }
    }

    fun onLoginPasswordChange(value: String) {
        _loginState.update { it.copy(password = value, passwordError = null, generalError = null) }
    }

    fun onLoginSubmit() {
        val state = _loginState.value
        var emailErr: String? = null
        var passErr: String?  = null

        // Validation rule 1 – email must not be blank and contain '@'
        if (state.email.isBlank()) {
            emailErr = "Email is required"
        } else if (!state.email.contains("@")) {
            emailErr = "Enter a valid email address"
        }

        // Validation rule 2 – password at least 6 chars
        if (state.password.isBlank()) {
            passErr = "Password is required"
        } else if (state.password.length < 6) {
            passErr = "Password must be at least 6 characters"
        }

        if (emailErr != null || passErr != null) {
            _loginState.update {
                it.copy(emailError = emailErr, passwordError = passErr)
            }
            return
        }

        // Simulated auth check (Assignment 1 – no real backend yet)
        _loginState.update { it.copy(isLoading = true) }
        // In a real app this would be a coroutine suspend call
        _loginState.update { it.copy(isLoading = false, loginSuccess = true) }
    }

    fun resetLoginSuccess() {
        _loginState.update { it.copy(loginSuccess = false) }
    }

    // ── Sign-up inputs ────────────────────────────────────────
    fun onSignUpNameChange(value: String) {
        _signUpState.update { it.copy(name = value, nameError = null) }
    }

    fun onSignUpEmailChange(value: String) {
        _signUpState.update { it.copy(email = value, emailError = null) }
    }

    fun onSignUpPasswordChange(value: String) {
        _signUpState.update { it.copy(password = value, passwordError = null) }
    }

    fun onSignUpConfirmPasswordChange(value: String) {
        _signUpState.update { it.copy(confirmPassword = value, confirmPasswordError = null) }
    }

    fun onSignUpSubmit() {
        val s = _signUpState.value
        var nameErr: String?    = null
        var emailErr: String?   = null
        var passErr: String?    = null
        var confirmErr: String? = null

        if (s.name.isBlank())                              nameErr    = "Name is required"
        if (s.email.isBlank())                             emailErr   = "Email is required"
        else if (!s.email.contains("@"))                   emailErr   = "Enter a valid email"
        if (s.password.length < 6)                         passErr    = "Minimum 6 characters"
        if (s.confirmPassword != s.password)               confirmErr = "Passwords do not match"

        if (listOf(nameErr, emailErr, passErr, confirmErr).any { it != null }) {
            _signUpState.update {
                it.copy(
                    nameError            = nameErr,
                    emailError           = emailErr,
                    passwordError        = passErr,
                    confirmPasswordError = confirmErr
                )
            }
            return
        }

        _signUpState.update { it.copy(isLoading = true) }
        _signUpState.update { it.copy(isLoading = false, signUpSuccess = true) }
    }

    fun resetSignUpSuccess() {
        _signUpState.update { it.copy(signUpSuccess = false) }
    }
}