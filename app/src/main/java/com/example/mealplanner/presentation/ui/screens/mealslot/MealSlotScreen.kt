package com.example.mealplanner.presentation.ui.screens.mealslot

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mealplanner.model.Meal
import com.example.mealplanner.model.MealSlotType
import com.example.mealplanner.presentation.ui.components.EmptyState
import com.example.mealplanner.presentation.ui.components.MacroChip
import com.example.mealplanner.presentation.ui.components.MealListItem
import com.example.mealplanner.presentation.ui.components.MealPlannerTopBar
import com.example.mealplanner.presentation.viewmodel.MealPlannerViewModel

@Composable
fun MealSlotScreen(
    dayName: String,
    slotName: String,
    viewModel: MealPlannerViewModel,
    onAddMeal: () -> Unit,
    onAddRecipe: () -> Unit,
    onBack: () -> Unit
) {
    val state   by viewModel.uiState.collectAsState()
    val dayPlan  = state.weekPlan[dayName]
    val slotType = MealSlotType.values().firstOrNull { it.displayName.equals(slotName, ignoreCase = true) }
    val meals    = slotType?.let { dayPlan?.mealsForSlot(it) } ?: emptyList()

    Scaffold(
        topBar    = { MealPlannerTopBar(title = "$dayName — $slotName", onBack = onBack) },
        bottomBar = { SlotActionBar(onAddMeal = onAddMeal, onAddRecipe = onAddRecipe) }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            SlotTotalsHeader(meals = meals)

            if (meals.isEmpty()) {
                EmptyState(
                    emoji    = "🍽",
                    title    = "No meals yet",
                    subtitle = "Tap 'Add Pre-made Meal' or 'Build Recipe' below to plan your $slotName"
                )
            } else {
                // LazyColumn — scrollable list of meals in this slot
                LazyColumn(
                    contentPadding      = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(meals, key = { it.id }) { meal ->
                        // Uses reusable MealListItem from components/
                        MealListItem(
                            meal                    = meal,
                            onRemove                = { viewModel.removeMeal(dayName, slotName, meal) },
                            showDeleteConfirmDialog  = true
                        )
                    }
                }
            }
        }
    }
}

// ── Slot Totals Header ─────────────────────────────────────────────────────────

@Composable
fun SlotTotalsHeader(meals: List<Meal>) {
    val cal  = meals.sumOf { it.calories }
    val prot = meals.sumOf { it.proteinG }
    val carb = meals.sumOf { it.carbsG }
    val fat  = meals.sumOf { it.fatG }

    Surface(tonalElevation = 3.dp, color = MaterialTheme.colorScheme.surfaceVariant) {
        Row(
            modifier              = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    "$cal kcal",
                    fontWeight = androidx.compose.ui.text.font.FontWeight.ExtraBold,
                    fontSize   = 22.sp,
                    color      = MaterialTheme.colorScheme.primary
                )
                Text("Slot total", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                MacroChip("Protein", "${"%.0f".format(prot)}g", Color(0xFF1976D2))
                MacroChip("Carbs",   "${"%.0f".format(carb)}g", Color(0xFFF57C00))
                MacroChip("Fat",     "${"%.0f".format(fat)}g",  Color(0xFF388E3C))
            }
        }
    }
}

// ── Slot Action Bar ────────────────────────────────────────────────────────────

@Composable
private fun SlotActionBar(onAddMeal: () -> Unit, onAddRecipe: () -> Unit) {
    Surface(shadowElevation = 10.dp) {
        Row(
            modifier              = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick  = onAddMeal,
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp),
                shape    = RoundedCornerShape(14.dp)
            ) {
                Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text("Pre-made Meal", fontSize = 13.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold)
            }
            OutlinedButton(
                onClick  = onAddRecipe,
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp),
                shape    = RoundedCornerShape(14.dp)
            ) {
                Icon(Icons.Filled.MenuBook, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text("Build Recipe", fontSize = 13.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold)
            }
        }
    }
}
