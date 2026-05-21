package com.example.mealplanner.presentation.ui.screens.addmeal

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mealplanner.model.Meal
import com.example.mealplanner.presentation.ui.components.AppSearchBar
import com.example.mealplanner.presentation.ui.components.AppTextField
import com.example.mealplanner.presentation.ui.components.EmptyState
import com.example.mealplanner.presentation.ui.components.MealListItem
import com.example.mealplanner.presentation.ui.components.MealPlannerTopBar
import com.example.mealplanner.presentation.ui.components.PrimaryButton
import com.example.mealplanner.presentation.viewmodel.AddMealFormState
import com.example.mealplanner.presentation.viewmodel.AddMealNavigationEvent
import com.example.mealplanner.presentation.viewmodel.AddMealUiState
import com.example.mealplanner.presentation.viewmodel.AddMealViewModel

@Composable
fun AddMealScreen(
    viewModel: AddMealViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.navEvents.collect { event ->
            when (event) {
                AddMealNavigationEvent.GoBack -> onBack()
            }
        }
    }

    when (val s = uiState) {
        AddMealUiState.Init, AddMealUiState.Loading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is AddMealUiState.Error -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(s.message, color = MaterialTheme.colorScheme.error)
            }
        }
        is AddMealUiState.Success -> AddMealScreenContent(
            state              = s,
            onBack             = viewModel::onBack,
            onSearchQuery      = viewModel::onSearchQueryChange,
            onAddPremade       = viewModel::addPremadeMeal,
            onCustomNameChange = viewModel::onCustomNameChange,
            onCustomCalChange  = viewModel::onCustomCalChange,
            onCustomProtChange = viewModel::onCustomProteinChange,
            onCustomFatChange  = viewModel::onCustomFatChange,
            onCustomCarbChange = viewModel::onCustomCarbsChange,
            onSubmitCustom     = viewModel::submitCustomMeal
        )
    }
}

@Composable
fun AddMealScreenContent(
    state: AddMealUiState.Success,
    onBack: () -> Unit,
    onSearchQuery: (String) -> Unit,
    onAddPremade: (Meal) -> Unit,
    onCustomNameChange: (String) -> Unit,
    onCustomCalChange: (String) -> Unit,
    onCustomProtChange: (String) -> Unit,
    onCustomFatChange: (String) -> Unit,
    onCustomCarbChange: (String) -> Unit,
    onSubmitCustom: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }

    Scaffold(
        topBar = { MealPlannerTopBar(title = "Add to ${state.slotName}", onBack = onBack) }
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
                    query         = state.form.searchQuery,
                    onQuery       = onSearchQuery,
                    filteredMeals = state.filteredMeals,
                    onAdd         = { meal -> onAddPremade(meal) }
                )
                1 -> CustomMealTab(
                    name          = state.form.customName,
                    calories      = state.form.customCalories,
                    protein       = state.form.customProtein,
                    fat           = state.form.customFat,
                    carbs         = state.form.customCarbs,
                    nameError     = state.form.customNameError,
                    caloriesError = state.form.customCaloriesError,
                    onNameChange  = onCustomNameChange,
                    onCalChange   = onCustomCalChange,
                    onProtChange  = onCustomProtChange,
                    onFatChange   = onCustomFatChange,
                    onCarbChange  = onCustomCarbChange,
                    onSubmit      = onSubmitCustom
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
    filteredMeals: List<Meal>,
    onAdd: (Meal) -> Unit
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
