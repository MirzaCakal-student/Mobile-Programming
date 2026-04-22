package com.example.mealplanner.presentation.ui.screens.addmeal

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mealplanner.presentation.ui.components.AppSearchBar
import com.example.mealplanner.presentation.ui.components.AppTextField
import com.example.mealplanner.presentation.ui.components.EmptyState
import com.example.mealplanner.presentation.ui.components.MealListItem
import com.example.mealplanner.presentation.ui.components.MealPlannerTopBar
import com.example.mealplanner.presentation.ui.components.PrimaryButton
import com.example.mealplanner.presentation.viewmodel.AddMealViewModel

@Composable
fun AddMealScreen(
    viewModel: AddMealViewModel,
    onBack: () -> Unit
) {
    val state         by viewModel.uiState.collectAsState()
    // Derived StateFlow — no filtering logic inside composable
    val filteredMeals by viewModel.filteredMeals.collectAsState()
    var selectedTab   by remember { mutableStateOf(0) }

    LaunchedEffect(state.addSuccess) {
        if (state.addSuccess) {
            viewModel.resetAddSuccess()
            onBack()
        }
    }

    Scaffold(
        topBar = { MealPlannerTopBar(title = "Add to ${viewModel.slotName}", onBack = onBack) }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor   = MaterialTheme.colorScheme.surface,
                contentColor     = MaterialTheme.colorScheme.primary
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick  = { selectedTab = 0 },
                    text     = { Text("Pre-made Meals") },
                    icon     = { Icon(Icons.Filled.Restaurant, null, Modifier.size(16.dp)) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick  = { selectedTab = 1 },
                    text     = { Text("Custom Meal") },
                    icon     = { Icon(Icons.Filled.Edit, null, Modifier.size(16.dp)) }
                )
            }

            when (selectedTab) {
                0 -> PremadeMealsTab(
                    query         = state.searchQuery,
                    onQuery       = viewModel::onSearchQueryChange,
                    filteredMeals = filteredMeals,
                    onAdd         = { meal -> viewModel.addPremadeMeal(meal) }
                )
                1 -> CustomMealTab(
                    name          = state.customName,
                    calories      = state.customCalories,
                    protein       = state.customProtein,
                    fat           = state.customFat,
                    carbs         = state.customCarbs,
                    nameError     = state.customNameError,
                    caloriesError = state.customCaloriesError,
                    onNameChange  = viewModel::onCustomNameChange,
                    onCalChange   = viewModel::onCustomCalChange,
                    onProtChange  = viewModel::onCustomProteinChange,
                    onFatChange   = viewModel::onCustomFatChange,
                    onCarbChange  = viewModel::onCustomCarbsChange,
                    onSubmit      = viewModel::submitCustomMeal
                )
            }
        }
    }
}

// ── Pre-made Meals Tab ─────────────────────────────────────────────────────────

@Composable
fun PremadeMealsTab(
    query: String,
    onQuery: (String) -> Unit,
    filteredMeals: List<com.example.mealplanner.model.Meal>,
    onAdd: (com.example.mealplanner.model.Meal) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        AppSearchBar(
            query         = query,
            onQueryChange = onQuery,
            placeholder   = "Search 25 meals…",
            modifier      = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
        )

        if (filteredMeals.isEmpty()) {
            EmptyState(
                emoji    = "🔍",
                title    = "No meals found",
                subtitle = "Try a different search term"
            )
        } else {
            // LazyColumn for pre-made meals — uses reusable MealListItem from components/
            LazyColumn(
                contentPadding      = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredMeals, key = { it.id }) { meal ->
                    MealListItem(
                        meal  = meal,
                        onAdd = { onAdd(meal) }
                    )
                }
            }
        }
    }
}

// ── Custom Meal Tab ────────────────────────────────────────────────────────────

@Composable
fun CustomMealTab(
    name: String, calories: String, protein: String, fat: String, carbs: String,
    nameError: String?, caloriesError: String?,
    onNameChange: (String) -> Unit, onCalChange: (String) -> Unit,
    onProtChange: (String) -> Unit, onFatChange: (String) -> Unit, onCarbChange: (String) -> Unit,
    onSubmit: () -> Unit
) {
    val canSubmit = name.isNotBlank() && calories.isNotBlank()

    // LazyColumn for the custom meal form — handles keyboard scrolling gracefully
    LazyColumn(
        contentPadding      = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text("Custom Meal", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text(
                "Enter your meal's nutritional info manually.",
                fontSize = 13.sp,
                color    = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f)
            )
        }
        item {
            AppTextField(
                value         = name,
                onValueChange = onNameChange,
                label         = "Meal Name *",
                placeholder   = "e.g. Mum's chicken soup",
                errorMessage  = nameError
            )
        }
        item {
            AppTextField(
                value         = calories,
                onValueChange = onCalChange,
                label         = "Calories (kcal) *",
                placeholder   = "e.g. 350",
                errorMessage  = caloriesError,
                keyboardType  = KeyboardType.Number
            )
        }
        item {
            Text("Macros (optional)", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                AppTextField(value = protein, onValueChange = onProtChange, label = "Protein (g)", keyboardType = KeyboardType.Decimal, modifier = Modifier.weight(1f))
                AppTextField(value = carbs,   onValueChange = onCarbChange, label = "Carbs (g)",   keyboardType = KeyboardType.Decimal, modifier = Modifier.weight(1f))
                AppTextField(value = fat,     onValueChange = onFatChange,  label = "Fat (g)",     keyboardType = KeyboardType.Decimal, modifier = Modifier.weight(1f))
            }
        }
        item {
            Spacer(Modifier.height(4.dp))
            PrimaryButton(text = "Add Custom Meal", onClick = onSubmit, enabled = canSubmit)
        }
    }
}
