package com.example.mealplanner.ui.screens.profile

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
import com.example.mealplanner.ui.components.AppTextField
import com.example.mealplanner.ui.components.DangerButton
import com.example.mealplanner.ui.components.MealPlannerTopBar
import com.example.mealplanner.ui.components.PrimaryButton
import com.example.mealplanner.ui.components.SectionHeader
import com.example.mealplanner.ui.components.StatCard
import com.example.mealplanner.viewmodel.ProfileViewModel

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onLogout: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

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
            ProfileBanner(name = state.nameInput)

            Column(
                modifier = Modifier.padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Spacer(Modifier.height(8.dp))

                if (state.isSaved) {
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

                if (state.profile.weightKg > 0 || state.profile.dailyCalorieGoal > 0) {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        if (state.profile.weightKg > 0)
                            StatCard("${state.profile.weightKg} kg", "Weight", Color(0xFF1976D2), modifier = Modifier.weight(1f))
                        if (state.profile.heightCm > 0)
                            StatCard("${state.profile.heightCm} cm", "Height", Color(0xFF388E3C), modifier = Modifier.weight(1f))
                        StatCard("${state.profile.dailyCalorieGoal}", "Cal Goal", Color(0xFFF57C00), modifier = Modifier.weight(1f))
                    }
                }

                SectionHeader("Personal Info")
                AppTextField(value = state.nameInput,  onValueChange = viewModel::onNameChange,  label = "Full Name", errorMessage = state.nameError)
                AppTextField(value = state.emailInput, onValueChange = viewModel::onEmailChange, label = "Email",     errorMessage = state.emailError, keyboardType = KeyboardType.Email)

                SectionHeader("Body Measurements")
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    AppTextField(value = state.weightInput, onValueChange = viewModel::onWeightChange, label = "Weight (kg)", keyboardType = KeyboardType.Decimal, modifier = Modifier.weight(1f))
                    AppTextField(value = state.heightInput, onValueChange = viewModel::onHeightChange, label = "Height (cm)", keyboardType = KeyboardType.Decimal, modifier = Modifier.weight(1f))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    AppTextField(value = state.ageInput,         onValueChange = viewModel::onAgeChange,         label = "Age",            keyboardType = KeyboardType.Number, modifier = Modifier.weight(1f))
                    AppTextField(value = state.calorieGoalInput, onValueChange = viewModel::onCalorieGoalChange, label = "Daily Cal Goal", keyboardType = KeyboardType.Number, modifier = Modifier.weight(1f))
                }

                SectionHeader("Gender")
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    listOf("Male", "Female").forEach { g ->
                        FilterChip(
                            selected = state.gender == g,
                            onClick  = { viewModel.onGenderChange(g) },
                            label    = { Text(g, fontWeight = if (state.gender == g) FontWeight.Bold else FontWeight.Normal) },
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
                    onClick = viewModel::onSaveProfile,
                    enabled = state.nameInput.isNotBlank() && state.emailInput.isNotBlank()
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
                modifier = Modifier.size(80.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.25f)),
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
