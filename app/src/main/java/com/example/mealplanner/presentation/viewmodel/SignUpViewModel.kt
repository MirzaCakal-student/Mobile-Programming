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

sealed interface SignUpUiState {
    data object Init    : SignUpUiState
    data object Loading : SignUpUiState
    data class  Form(
        val name: String                  = "",
        val email: String                 = "",
        val password: String              = "",
        val confirmPassword: String       = "",
        val nameError: String?            = null,
        val emailError: String?           = null,
        val passwordError: String?        = null,
        val confirmPasswordError: String? = null
    ) : SignUpUiState
    data class Error(val message: String) : SignUpUiState
}

sealed interface SignUpNavigationEvent {
    data object NavigateToMain  : SignUpNavigationEvent
    data object NavigateToLogin : SignUpNavigationEvent
}

@HiltViewModel
class SignUpViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow<SignUpUiState>(SignUpUiState.Form())
    val uiState: StateFlow<SignUpUiState> = _uiState.asStateFlow()

    private val _navEvents = Channel<SignUpNavigationEvent>(Channel.BUFFERED)
    val navEvents = _navEvents.receiveAsFlow()

    private val form get() = _uiState.value as? SignUpUiState.Form ?: SignUpUiState.Form()

    fun onNameChange(v: String)            { _uiState.value = form.copy(name            = v, nameError            = null) }
    fun onEmailChange(v: String)           { _uiState.value = form.copy(email           = v, emailError           = null) }
    fun onPasswordChange(v: String)        { _uiState.value = form.copy(password        = v, passwordError        = null) }
    fun onConfirmPasswordChange(v: String) { _uiState.value = form.copy(confirmPassword = v, confirmPasswordError = null) }

    fun onSubmit() {
        val f = form
        val nameErr    = if (f.name.isBlank())                              "Name is required"       else null
        val emailErr   = if (f.email.isBlank() || !f.email.contains("@"))  "Enter a valid email"    else null
        val passErr    = if (f.password.length < 6)                         "Minimum 6 characters"   else null
        val confirmErr = if (f.confirmPassword != f.password)               "Passwords do not match" else null
        if (listOf(nameErr, emailErr, passErr, confirmErr).any { it != null }) {
            _uiState.value = f.copy(nameError = nameErr, emailError = emailErr, passwordError = passErr, confirmPasswordError = confirmErr)
            return
        }
        _uiState.value = SignUpUiState.Loading
        viewModelScope.launch {
            _uiState.value = SignUpUiState.Form()
            _navEvents.send(SignUpNavigationEvent.NavigateToMain)
        }
    }

    fun onNavigateToLogin() { viewModelScope.launch { _navEvents.send(SignUpNavigationEvent.NavigateToLogin) } }
}
