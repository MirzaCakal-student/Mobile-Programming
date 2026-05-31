package com.example.mealplanner.presentation.ui.screens.calories

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mealplanner.presentation.ui.components.AppTextField
import com.example.mealplanner.presentation.ui.components.MealPlannerTopBar
import com.example.mealplanner.presentation.ui.components.PrimaryButton
import com.example.mealplanner.presentation.ui.components.SecondaryButton
import com.example.mealplanner.presentation.ui.components.SectionHeader
import com.example.mealplanner.presentation.viewmodel.CaloriesFormState
import com.example.mealplanner.presentation.viewmodel.CaloriesUiState
import com.example.mealplanner.presentation.viewmodel.CaloriesViewModel

@Composable
fun CaloriesCalculatorScreen(
    viewModel: CaloriesViewModel,
    onNavigateToCommunity: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (val s = uiState) {
        CaloriesUiState.Init -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is CaloriesUiState.Form -> CaloriesCalculatorContent(
            form                  = s.form,
            activityOptions       = viewModel.activityOptions,
            onWeightChange        = viewModel::onWeightChange,
            onHeightChange        = viewModel::onHeightChange,
            onAgeChange           = viewModel::onAgeChange,
            onGenderChange        = viewModel::onGenderChange,
            onActivityChange      = viewModel::onActivityChange,
            onCalculate           = viewModel::onCalculate,
            onReset               = viewModel::onReset,
            onNavigateToCommunity = onNavigateToCommunity
        )
    }
}

@Composable
fun CaloriesCalculatorContent(
    form: CaloriesFormState,
    activityOptions: List<String>,
    onWeightChange: (String) -> Unit,
    onHeightChange: (String) -> Unit,
    onAgeChange: (String) -> Unit,
    onGenderChange: (String) -> Unit,
    onActivityChange: (String) -> Unit,
    onCalculate: () -> Unit,
    onReset: () -> Unit,
    onNavigateToCommunity: () -> Unit
) {
    Scaffold(
        topBar = { MealPlannerTopBar(title = "Calorie Calculator") }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                shape  = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))
            ) {
                Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.Top) {
                    Icon(Icons.Filled.Info, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(10.dp))
                    Text(
                        "Uses the Mifflin-St Jeor formula — the most accurate widely-used BMR formula. Results are estimates.",
                        fontSize = 13.sp,
                        color    = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }

            SectionHeader("Your Details")
            GenderSelector(selected = form.gender, onSelect = onGenderChange)

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                AppTextField(
                    value         = form.weight,
                    onValueChange = onWeightChange,
                    label         = "Weight (kg)",
                    errorMessage  = form.weightError,
                    keyboardType  = KeyboardType.Decimal,
                    modifier      = Modifier.weight(1f),
                    placeholder   = "70"
                )
                AppTextField(
                    value         = form.height,
                    onValueChange = onHeightChange,
                    label         = "Height (cm)",
                    errorMessage  = form.heightError,
                    keyboardType  = KeyboardType.Decimal,
                    modifier      = Modifier.weight(1f),
                    placeholder   = "175"
                )
            }

            AppTextField(
                value         = form.age,
                onValueChange = onAgeChange,
                label         = "Age (years)",
                errorMessage  = form.ageError,
                keyboardType  = KeyboardType.Number,
                placeholder   = "25"
            )

            ActivityDropdown(
                selected = form.activityLevel,
                options  = activityOptions,
                onSelect = onActivityChange
            )

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                PrimaryButton(
                    text     = "Calculate",
                    onClick  = onCalculate,
                    enabled  = form.weight.isNotBlank() && form.height.isNotBlank() && form.age.isNotBlank(),
                    modifier = Modifier.weight(1f)
                )
                SecondaryButton(text = "Reset", onClick = onReset, modifier = Modifier.weight(1f))
            }

            if (form.bmr != null && form.tdee != null) {
                CaloriesResultSection(bmr = form.bmr, tdee = form.tdee)
            }

            // ── Cloud feature entry — opens Community Recipes (Firestore realtime) ──
            // Lives here because both calorie planning and shared recipes are
            // "find ideas for what to eat" — they belong together UX-wise.
            Spacer(Modifier.height(12.dp))
            Card(
                shape    = RoundedCornerShape(14.dp),
                colors   = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)),
                onClick  = onNavigateToCommunity,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier              = Modifier.padding(14.dp),
                    verticalAlignment     = Alignment.CenterVertically,
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
                            Icons.Filled.Groups,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Community Recipes", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                        Text(
                            "Share & discover recipes from everyone (live)",
                            fontSize = 12.sp,
                            color    = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                    Icon(
                        Icons.Filled.ChevronRight,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
fun GenderSelector(selected: String, onSelect: (String) -> Unit) {
    Column {
        Text("Gender", fontWeight = FontWeight.Medium, fontSize = 14.sp)
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            listOf("Male" to Icons.Filled.Male, "Female" to Icons.Filled.Female).forEach { (gender, icon) ->
                val isSelected = selected == gender
                FilterChip(
                    selected    = isSelected,
                    onClick     = { onSelect(gender) },
                    label       = { Text(gender, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                    leadingIcon = { Icon(icon, contentDescription = null, modifier = Modifier.size(16.dp)) },
                    modifier    = Modifier.weight(1f),
                    colors      = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                        selectedLabelColor     = MaterialTheme.colorScheme.primary
                    )
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityDropdown(selected: String, options: List<String>, onSelect: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Column {
        Text("Activity Level", fontWeight = FontWeight.Medium, fontSize = 14.sp)
        Spacer(Modifier.height(8.dp))
        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
            OutlinedTextField(
                value         = selected,
                onValueChange = {},
                readOnly      = true,
                modifier      = Modifier.fillMaxWidth().menuAnchor(),
                shape         = RoundedCornerShape(12.dp),
                trailingIcon  = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                colors        = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.primary)
            )
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text    = { Text(option) },
                        onClick = { onSelect(option); expanded = false }
                    )
                }
            }
        }
    }
}

@Composable
fun CaloriesResultSection(bmr: Double, tdee: Double) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SectionHeader("Your Results")
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            ResultValueCard(label = "BMR",  value = "${"%.0f".format(bmr)} kcal",  sub = "Calories at rest",  color = Color(0xFF1565C0), modifier = Modifier.weight(1f))
            ResultValueCard(label = "TDEE", value = "${"%.0f".format(tdee)} kcal", sub = "Daily total need",  color = Color(0xFF2E7D32), modifier = Modifier.weight(1f))
        }

        SectionHeader("Goal Targets")
        GoalTargetCard(label = "Weight Loss",     kcal = (tdee - 500).toInt(), color = Color(0xFFC62828), description = "500 kcal deficit / day")
        GoalTargetCard(label = "Maintain Weight", kcal = tdee.toInt(),         color = Color(0xFF2E7D32), description = "Eat exactly your TDEE")
        GoalTargetCard(label = "Weight Gain",     kcal = (tdee + 300).toInt(), color = Color(0xFF1565C0), description = "300 kcal surplus / day")

        Card(shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1))) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text("How to use these results", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF5D4037))
                Spacer(Modifier.height(6.dp))
                Text(
                    "Set your daily calorie goal in Profile to match your target. The app will show you whether you're on track each day.",
                    fontSize = 13.sp,
                    color    = Color(0xFF5D4037).copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
private fun ResultValueCard(label: String, value: String, sub: String, color: Color, modifier: Modifier) {
    Card(modifier = modifier, shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))) {
        Column(modifier = Modifier.padding(14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label, fontSize = 12.sp, color = color, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(4.dp))
            Text(value, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = color)
            Text(sub, fontSize = 10.sp, color = color.copy(alpha = 0.7f))
        }
    }
}

@Composable
private fun GoalTargetCard(label: String, kcal: Int, color: Color, description: String) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), elevation = CardDefaults.cardElevation(1.dp)) {
        Row(
            modifier              = Modifier.fillMaxWidth().padding(14.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(shape = RoundedCornerShape(5.dp), color = color, modifier = Modifier.size(10.dp)) {}
                Spacer(Modifier.width(10.dp))
                Column {
                    Text(label, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    Text(description, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                }
            }
            Text("$kcal kcal", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = color)
        }
    }
}
