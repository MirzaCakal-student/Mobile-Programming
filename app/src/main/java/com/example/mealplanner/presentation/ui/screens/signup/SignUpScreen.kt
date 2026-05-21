package com.example.mealplanner.presentation.ui.screens.signup

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mealplanner.presentation.ui.components.AppTextField
import com.example.mealplanner.presentation.ui.components.MealPlannerTopBar
import com.example.mealplanner.presentation.ui.components.PrimaryButton
import com.example.mealplanner.presentation.viewmodel.SignUpNavigationEvent
import com.example.mealplanner.presentation.viewmodel.SignUpUiState
import com.example.mealplanner.presentation.viewmodel.SignUpViewModel

@Composable
fun SignUpScreen(
    viewModel: SignUpViewModel,
    onSignUpSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.navEvents.collect { event ->
            when (event) {
                SignUpNavigationEvent.NavigateToMain  -> onSignUpSuccess()
                SignUpNavigationEvent.NavigateToLogin -> onNavigateToLogin()
            }
        }
    }

    Scaffold(
        topBar = { MealPlannerTopBar(title = "Create Account", onBack = viewModel::onNavigateToLogin) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Brush.verticalGradient(listOf(Color(0xFFE8F5E9), Color(0xFFFFFFFF))))
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(28.dp))
            Text("Join MealPlanner", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
            Text("Create your free account below", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f))
            Spacer(Modifier.height(28.dp))

            when (val s = uiState) {
                is SignUpUiState.Form -> {
                    Card(
                        shape     = RoundedCornerShape(20.dp),
                        colors    = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(4.dp),
                        modifier  = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(22.dp)) {
                            SignUpForm(
                                name             = s.name,
                                email            = s.email,
                                password         = s.password,
                                confirmPassword  = s.confirmPassword,
                                nameError        = s.nameError,
                                emailError       = s.emailError,
                                passwordError    = s.passwordError,
                                confirmPassError = s.confirmPasswordError,
                                isLoading        = false,
                                onNameChange     = viewModel::onNameChange,
                                onEmailChange    = viewModel::onEmailChange,
                                onPasswordChange = viewModel::onPasswordChange,
                                onConfirmChange  = viewModel::onConfirmPasswordChange,
                                onSubmit         = viewModel::onSubmit
                            )
                        }
                    }
                }
                SignUpUiState.Loading -> {
                    Card(
                        shape     = RoundedCornerShape(20.dp),
                        colors    = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(4.dp),
                        modifier  = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(22.dp)) {
                            SignUpForm(
                                name = "", email = "", password = "", confirmPassword = "",
                                nameError = null, emailError = null, passwordError = null, confirmPassError = null,
                                isLoading = true,
                                onNameChange = {}, onEmailChange = {}, onPasswordChange = {}, onConfirmChange = {}, onSubmit = {}
                            )
                        }
                    }
                }
                is SignUpUiState.Error -> {
                    Text(s.message, color = MaterialTheme.colorScheme.error)
                }
                SignUpUiState.Init -> Unit
            }

            Spacer(Modifier.height(20.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Already have an account?", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                TextButton(onClick = viewModel::onNavigateToLogin) {
                    Text("Log In", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
            }
            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
fun SignUpForm(
    name: String, email: String, password: String, confirmPassword: String,
    nameError: String?, emailError: String?, passwordError: String?, confirmPassError: String?,
    isLoading: Boolean,
    onNameChange: (String) -> Unit, onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit, onConfirmChange: (String) -> Unit,
    onSubmit: () -> Unit
) {
    var passVisible    by remember { mutableStateOf(false) }
    var confirmVisible by remember { mutableStateOf(false) }
    val canSubmit = name.isNotBlank() && email.isNotBlank() && password.isNotBlank() && confirmPassword.isNotBlank() && !isLoading

    AppTextField(value = name,  onValueChange = onNameChange,  label = "Full Name",  placeholder = "John Doe",           errorMessage = nameError)
    Spacer(Modifier.height(12.dp))
    AppTextField(value = email, onValueChange = onEmailChange, label = "Email",       placeholder = "you@example.com",    errorMessage = emailError, keyboardType = KeyboardType.Email)
    Spacer(Modifier.height(12.dp))
    AppTextField(
        value = password, onValueChange = onPasswordChange, label = "Password",
        errorMessage = passwordError, isPassword = !passVisible, keyboardType = KeyboardType.Password,
        trailingIcon = {
            IconButton(onClick = { passVisible = !passVisible }) {
                Icon(if (passVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility, null)
            }
        }
    )
    Spacer(Modifier.height(12.dp))
    AppTextField(
        value = confirmPassword, onValueChange = onConfirmChange, label = "Confirm Password",
        errorMessage = confirmPassError, isPassword = !confirmVisible, keyboardType = KeyboardType.Password,
        trailingIcon = {
            IconButton(onClick = { confirmVisible = !confirmVisible }) {
                Icon(if (confirmVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility, null)
            }
        }
    )
    Spacer(Modifier.height(24.dp))
    if (isLoading) {
        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
    }
    PrimaryButton(text = "Create Account", onClick = onSubmit, enabled = canSubmit)
}
