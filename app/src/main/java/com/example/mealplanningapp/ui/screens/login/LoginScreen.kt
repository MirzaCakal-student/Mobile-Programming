// ============================================================
// FILE: app/src/main/java/com/example/mealplanner/ui/screens/login/LoginScreen.kt
// ============================================================
package com.example.mealplanner.ui.screens.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mealplanner.ui.components.AppTextField
import com.example.mealplanner.ui.components.LabelDivider
import com.example.mealplanner.ui.components.PrimaryButton
import com.example.mealplanner.viewmodel.AuthViewModel

// ── LoginScreen ───────────────────────────────────────────────────────────────
@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onNavigateToSignUp: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    val state by viewModel.loginState.collectAsState()

    LaunchedEffect(state.loginSuccess) {
        if (state.loginSuccess) {
            viewModel.resetLoginSuccess()
            onLoginSuccess()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFFE8F5E9), Color(0xFFFFFFFF), Color(0xFFFFFFFF))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(70.dp))
            LoginHeader()
            Spacer(Modifier.height(44.dp))
            LoginForm(
                email             = state.email,
                password          = state.password,
                emailError        = state.emailError,
                passwordError     = state.passwordError,
                isLoading         = state.isLoading,
                onEmailChange     = viewModel::onLoginEmailChange,
                onPasswordChange  = viewModel::onLoginPasswordChange,
                onSubmit          = viewModel::onLoginSubmit
            )
            Spacer(Modifier.height(20.dp))
            LabelDivider("or")
            Spacer(Modifier.height(20.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Don't have an account?", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                TextButton(onClick = onNavigateToSignUp) {
                    Text("Sign Up", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
            }
            Spacer(Modifier.height(32.dp))
        }
    }
}

// ── LoginHeader component ─────────────────────────────────────────────────────
// FILE: .../login/components/LoginHeader.kt
@Composable
fun LoginHeader() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier        = Modifier
                .size(96.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(listOf(Color(0xFF43A047), Color(0xFF1B5E20)))
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector        = Icons.Filled.Restaurant,
                contentDescription = "Logo",
                tint               = Color.White,
                modifier           = Modifier.size(52.dp)
            )
        }
        Spacer(Modifier.height(18.dp))
        Text(
            text       = "Welcome back!",
            fontSize   = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            color      = MaterialTheme.colorScheme.onSurface
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text     = "Log in to your MealPlanner account",
            fontSize = 14.sp,
            color    = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f),
            textAlign = TextAlign.Center
        )
    }
}

// ── LoginForm component ───────────────────────────────────────────────────────
// FILE: .../login/components/LoginForm.kt
@Composable
fun LoginForm(
    email: String,
    password: String,
    emailError: String?,
    passwordError: String?,
    isLoading: Boolean,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onSubmit: () -> Unit
) {
    var passwordVisible by remember { mutableStateOf(false) }
    val canSubmit = email.isNotBlank() && password.isNotBlank() && !isLoading

    Card(
        shape     = RoundedCornerShape(20.dp),
        colors    = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier  = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(22.dp)) {
            AppTextField(
                value         = email,
                onValueChange = onEmailChange,
                label         = "Email address",
                placeholder   = "you@example.com",
                errorMessage  = emailError,
                keyboardType  = KeyboardType.Email
            )
            Spacer(Modifier.height(14.dp))
            AppTextField(
                value         = password,
                onValueChange = onPasswordChange,
                label         = "Password",
                errorMessage  = passwordError,
                isPassword    = !passwordVisible,
                keyboardType  = KeyboardType.Password,
                trailingIcon  = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = "Toggle password"
                        )
                    }
                }
            )
            Spacer(Modifier.height(8.dp))
            TextButton(
                onClick  = { /* forgot password – future */ },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Forgot password?", fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
            }
            Spacer(Modifier.height(8.dp))
            if (isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
            }
            PrimaryButton(text = "Log In", onClick = onSubmit, enabled = canSubmit)
        }
    }
}