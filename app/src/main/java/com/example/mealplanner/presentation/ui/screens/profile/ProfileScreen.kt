package com.example.mealplanner.presentation.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mealplanner.model.UserProfile
import com.example.mealplanner.presentation.ui.components.AppTextField
import com.example.mealplanner.presentation.ui.components.DangerButton
import com.example.mealplanner.presentation.ui.components.MealPlannerTopBar
import com.example.mealplanner.presentation.ui.components.PrimaryButton
import com.example.mealplanner.presentation.ui.components.SectionHeader
import com.example.mealplanner.presentation.ui.components.StatCard
import com.example.mealplanner.presentation.viewmodel.ProfileFormState
import com.example.mealplanner.presentation.viewmodel.ProfileNavigationEvent
import com.example.mealplanner.presentation.viewmodel.ProfileUiState
import com.example.mealplanner.presentation.viewmodel.ProfileViewModel

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onLogout: () -> Unit,
    onNavigateToHabits: () -> Unit,
    onNavigateToWeather: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.navEvents.collect { event ->
            when (event) {
                ProfileNavigationEvent.Logout -> onLogout()
            }
        }
    }

    when (val s = uiState) {
        ProfileUiState.Init, ProfileUiState.Loading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is ProfileUiState.Error -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(s.message, color = MaterialTheme.colorScheme.error)
            }
        }
        is ProfileUiState.Success -> ProfileScreenContent(
            profile             = s.profile,
            form                = s.form,
            onNameChange        = viewModel::onNameChange,
            onEmailChange       = viewModel::onEmailChange,
            onWeightChange      = viewModel::onWeightChange,
            onHeightChange      = viewModel::onHeightChange,
            onAgeChange         = viewModel::onAgeChange,
            onCalorieGoalChange = viewModel::onCalorieGoalChange,
            onGenderChange      = viewModel::onGenderChange,
            onSaveProfile       = viewModel::onSaveProfile,
            onLogout            = viewModel::onLogout,
            onNavigateToHabits  = onNavigateToHabits,
            onNavigateToWeather = onNavigateToWeather
        )
    }
}

@Composable
fun ProfileScreenContent(
    profile: UserProfile,
    form: ProfileFormState,
    onNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onWeightChange: (String) -> Unit,
    onHeightChange: (String) -> Unit,
    onAgeChange: (String) -> Unit,
    onCalorieGoalChange: (String) -> Unit,
    onGenderChange: (String) -> Unit,
    onSaveProfile: () -> Unit,
    onLogout: () -> Unit,
    onNavigateToHabits: () -> Unit,
    onNavigateToWeather: () -> Unit
) {
    Scaffold(
        topBar = {
            MealPlannerTopBar(
                title   = "My Profile",
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Filled.Logout, contentDescription = "Logout", tint = Color.White)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ProfileBanner(name = form.nameInput)

            Column(
                modifier            = Modifier.padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Spacer(Modifier.height(8.dp))

                if (form.isSaved) {
                    Card(
                        shape  = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Profile saved successfully!", color = Color(0xFF2E7D32), fontWeight = FontWeight.Medium)
                        }
                    }
                }

                if (profile.weightKg > 0 || profile.dailyCalorieGoal > 0) {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        if (profile.weightKg > 0)
                            StatCard("${profile.weightKg} kg", "Weight", Color(0xFF1976D2), modifier = Modifier.weight(1f))
                        if (profile.heightCm > 0)
                            StatCard("${profile.heightCm} cm", "Height", Color(0xFF388E3C), modifier = Modifier.weight(1f))
                        StatCard("${profile.dailyCalorieGoal}", "Cal Goal", Color(0xFFF57C00), modifier = Modifier.weight(1f))
                    }
                }

                // ── Cloud feature entry point — opens Habits (REST API backed) ──
                Card(
                    shape    = RoundedCornerShape(14.dp),
                    colors   = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)),
                    onClick  = onNavigateToHabits,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier             = Modifier.padding(14.dp),
                        verticalAlignment    = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.18f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Filled.Checklist,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text("My Habits", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)

                        }
                        Icon(
                            Icons.Filled.ChevronRight,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    }
                }

                // ── Cloud feature entry point — opens Weather (OpenWeather REST API) ──
                Card(
                    shape    = RoundedCornerShape(14.dp),
                    colors   = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)),
                    onClick  = onNavigateToWeather,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier             = Modifier.padding(14.dp),
                        verticalAlignment    = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.18f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Filled.WbSunny,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Weather Today", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                        }
                        Icon(
                            Icons.Filled.ChevronRight,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    }
                }

                SectionHeader("Personal Info")
                AppTextField(
                    value         = form.nameInput,
                    onValueChange = onNameChange,
                    label         = "Full Name",
                    errorMessage  = form.nameError
                )
                AppTextField(
                    value         = form.emailInput,
                    onValueChange = onEmailChange,
                    label         = "Email",
                    errorMessage  = form.emailError,
                    keyboardType  = KeyboardType.Email
                )

                SectionHeader("Body Measurements")
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    AppTextField(value = form.weightInput, onValueChange = onWeightChange, label = "Weight (kg)", keyboardType = KeyboardType.Decimal, modifier = Modifier.weight(1f))
                    AppTextField(value = form.heightInput, onValueChange = onHeightChange, label = "Height (cm)", keyboardType = KeyboardType.Decimal, modifier = Modifier.weight(1f))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    AppTextField(value = form.ageInput,         onValueChange = onAgeChange,         label = "Age",            keyboardType = KeyboardType.Number, modifier = Modifier.weight(1f))
                    AppTextField(value = form.calorieGoalInput, onValueChange = onCalorieGoalChange, label = "Daily Cal Goal", keyboardType = KeyboardType.Number, modifier = Modifier.weight(1f))
                }

                SectionHeader("Gender")
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    listOf("Male", "Female").forEach { g ->
                        FilterChip(
                            selected = form.gender == g,
                            onClick  = { onGenderChange(g) },
                            label    = { Text(g, fontWeight = if (form.gender == g) FontWeight.Bold else FontWeight.Normal) },
                            modifier = Modifier.weight(1f),
                            colors   = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                selectedLabelColor     = MaterialTheme.colorScheme.primary
                            )
                        )
                    }
                }

                Spacer(Modifier.height(4.dp))
                PrimaryButton(
                    text    = "Save Profile",
                    onClick = onSaveProfile,
                    enabled = form.nameInput.isNotBlank() && form.emailInput.isNotBlank()
                )
                DangerButton(text = "Log Out", onClick = onLogout)
                Spacer(Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun ProfileBanner(name: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .background(Brush.verticalGradient(listOf(Color(0xFF1B5E20), Color(0xFF43A047)))),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.25f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(52.dp))
            }
            Spacer(Modifier.height(10.dp))
            if (name.isNotBlank()) {
                Text(name, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = Color.White)
            }
            Text("MealPlanner Member", fontSize = 13.sp, color = Color.White.copy(alpha = 0.75f))
        }
    }
}
