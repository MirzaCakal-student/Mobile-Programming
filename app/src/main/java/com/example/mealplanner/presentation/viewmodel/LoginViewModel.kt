package com.example.mealplanner.presentation.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

// ── UI State ──────────────────────────────────────────────────────────────────

data class LoginUiState(
    val email: String          = "",
    val password: String       = "",
    val emailError: String?    = null,
    val passwordError: String? = null,
    val isLoading: Boolean     = false,
    val loginSuccess: Boolean  = false
)

// ── ViewModel — scoped to LoginScreen ─────────────────────────────────────────

@HiltViewModel
class LoginViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChange(v: String)    { _uiState.update { it.copy(email    = v, emailError    = null) } }
    fun onPasswordChange(v: String) { _uiState.update { it.copy(password = v, passwordError = null) } }

    fun onSubmit() {
        val s = _uiState.value
        val emailErr = if (s.email.isBlank() || !s.email.contains("@"))
            "Enter a valid email address" else null
        val passErr = if (s.password.length < 6)
            "Password must be at least 6 characters" else null

        if (emailErr != null || passErr != null) {
            _uiState.update { it.copy(emailError = emailErr, passwordError = passErr) }
            return
        }

        _uiState.update { it.copy(isLoading = true) }
        // Simulated auth (no real backend yet — will be replaced in a future assignment)
        _uiState.update { it.copy(isLoading = false, loginSuccess = true) }
    }

    fun resetLoginSuccess() { _uiState.update { it.copy(loginSuccess = false) } }
}
