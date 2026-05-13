package com.example.mealplanner.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// ── UiState ───────────────────────────────────────────────────────────────────

sealed interface LoginUiState {
    data object Init    : LoginUiState
    data object Loading : LoginUiState
    data class  Form(
        val email: String          = "",
        val password: String       = "",
        val emailError: String?    = null,
        val passwordError: String? = null
    ) : LoginUiState
    data class  Error(val message: String) : LoginUiState
}

// ── Navigation Events ─────────────────────────────────────────────────────────

sealed interface LoginNavigationEvent {
    data object NavigateToMain   : LoginNavigationEvent
    data object NavigateToSignUp : LoginNavigationEvent
}

// ── ViewModel ─────────────────────────────────────────────────────────────────

@HiltViewModel
class LoginViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Form())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _navEvents = Channel<LoginNavigationEvent>(Channel.BUFFERED)
    val navEvents = _navEvents.receiveAsFlow()

    private val form get() = _uiState.value as? LoginUiState.Form ?: LoginUiState.Form()

    fun onEmailChange(v: String)    { _uiState.value = form.copy(email    = v, emailError    = null) }
    fun onPasswordChange(v: String) { _uiState.value = form.copy(password = v, passwordError = null) }

    fun onSubmit() {
        val f = form
        val emailErr = if (f.email.isBlank() || !f.email.contains("@")) "Enter a valid email address" else null
        val passErr  = if (f.password.length < 6)                        "Password must be at least 6 characters" else null
        if (emailErr != null || passErr != null) {
            _uiState.value = f.copy(emailError = emailErr, passwordError = passErr)
            return
        }
        _uiState.value = LoginUiState.Loading
        viewModelScope.launch {
            // Simulated auth — replace with real backend in a future assignment
            _uiState.value = LoginUiState.Form()
            _navEvents.send(LoginNavigationEvent.NavigateToMain)
        }
    }

    fun onNavigateToSignUp() { viewModelScope.launch { _navEvents.send(LoginNavigationEvent.NavigateToSignUp) } }
}
