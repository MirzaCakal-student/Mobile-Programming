package com.example.mealplanner.ui.screens.addmeal

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mealplanner.model.Meal
import com.example.mealplanner.ui.components.AppSearchBar
import com.example.mealplanner.ui.components.AppTextField
import com.example.mealplanner.ui.components.EmptyState
import com.example.mealplanner.ui.components.MealPlannerTopBar
import com.example.mealplanner.ui.components.PrimaryButton
import com.example.mealplanner.viewmodel.MealPlannerViewModel

@Composable
fun AddMealScreen(
    dayName: String,
    slotName: String,
    viewModel: MealPlannerViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableStateOf(0) }

    LaunchedEffect(state.addMealSuccess) {
        if (state.addMealSuccess) { viewModel.resetAddMealSuccess(); onBack() }
    }

    Scaffold(
        topBar = { MealPlannerTopBar(title = "Add to $slotName", onBack = onBack) }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor   = MaterialTheme.colorScheme.surface,
                contentColor     = MaterialTheme.colorScheme.primary
            ) {
                Tab(
                    selected = selectedTab == 0, onClick = { selectedTab = 0 },
                    text = { Text("Pre-made Meals") },
                    icon = { Icon(Icons.Filled.Restaurant, null, Modifier.size(16.dp)) }
                )
                Tab(
                    selected = selectedTab == 1, onClick = { selectedTab = 1 },
                    text = { Text("Custom Meal") },
                    icon = { Icon(Icons.Filled.Edit, null, Modifier.size(16.dp)) }
                )
            }

            when (selectedTab) {
                0 -> PremadeMealsTab(
                    query   = state.searchQuery,
                    onQuery = viewModel::onSearchQueryChange,
                    meals   = viewModel.filteredMeals(),
                    onAdd   = { meal -> viewModel.addPremadeMeal(dayName, slotName, meal); onBack() }
                )
                1 -> CustomMealTab(
                    name          = state.customMealName,
                    calories      = state.customMealCalories,
                    protein       = state.customMealProtein,
                    fat           = state.customMealFat,
                    carbs         = state.customMealCarbs,
                    nameError     = state.customMealNameError,
                    caloriesError = state.customMealCaloriesError,
                    onNameChange  = viewModel::onCustomMealNameChange,
                    onCalChange   = viewModel::onCustomMealCaloriesChange,
                    onProtChange  = viewModel::onCustomMealProteinChange,
                    onFatChange   = viewModel::onCustomMealFatChange,
                    onCarbChange  = viewModel::onCustomMealCarbsChange,
                    onSubmit      = { viewModel.submitCustomMeal(dayName, slotName) }
                )
            }
        }
    }
}

@Composable
fun PremadeMealsTab(
    query: String,
    onQuery: (String) -> Unit,
    meals: List<Meal>,
    onAdd: (Meal) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        AppSearchBar(
            query         = query,
            onQueryChange = onQuery,
            placeholder   = "Search 25 meals\u2026",
            modifier      = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
        )
        if (meals.isEmpty()) {
            EmptyState(emoji = "\uD83D\uDD0D", title = "No meals found", subtitle = "Try a different search term")
        } else {
            LazyColumn(
                contentPadding      = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(meals, key = { it.id }) { meal ->
                    PremadeMealCard(meal = meal, onAdd = { onAdd(meal) })
                }
            }
        }
    }
}

@Composable
fun PremadeMealCard(meal: Meal, onAdd: () -> Unit) {
    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(meal.name, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                Spacer(Modifier.height(3.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("${meal.calories} kcal", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
                    Text("P:${"%.0f".format(meal.proteinG)}g", fontSize = 12.sp, color = Color(0xFF1976D2))
                    Text("C:${"%.0f".format(meal.carbsG)}g",  fontSize = 12.sp, color = Color(0xFFF57C00))
                    Text("F:${"%.0f".format(meal.fatG)}g",    fontSize = 12.sp, color = Color(0xFF388E3C))
                }
            }
            FilledIconButton(
                onClick  = onAdd,
                modifier = Modifier.size(38.dp),
                colors   = IconButtonDefaults.filledIconButtonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add", tint = Color.White, modifier = Modifier.size(20.dp))
            }
        }
    }
}

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
            Text("Enter your meal's nutritional info manually.", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f))
        }
        item {
            AppTextField(value = name, onValueChange = onNameChange, label = "Meal Name *", placeholder = "e.g. Mum's chicken soup", errorMessage = nameError)
        }
        item {
            AppTextField(value = calories, onValueChange = onCalChange, label = "Calories (kcal) *", placeholder = "e.g. 350", errorMessage = caloriesError, keyboardType = KeyboardType.Number)
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
