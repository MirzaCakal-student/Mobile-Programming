package com.example.mealplanner.ui.screens.mealslot

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mealplanner.model.Meal
import com.example.mealplanner.model.MealSlotType
import com.example.mealplanner.ui.components.EmptyState
import com.example.mealplanner.ui.components.MacroChip
import com.example.mealplanner.ui.components.MealPlannerTopBar
import com.example.mealplanner.viewmodel.MealPlannerViewModel

@Composable
fun MealSlotScreen(
    dayName: String,
    slotName: String,
    viewModel: MealPlannerViewModel,
    onAddMeal: () -> Unit,
    onAddRecipe: () -> Unit,
    onBack: () -> Unit
) {
    val state    by viewModel.uiState.collectAsState()
    val dayPlan   = state.weekPlan[dayName]
    val slotType  = MealSlotType.values().firstOrNull { it.displayName.equals(slotName, ignoreCase = true) }
    val meals     = slotType?.let { dayPlan?.mealsForSlot(it) } ?: emptyList()

    Scaffold(
        topBar    = { MealPlannerTopBar(title = "$dayName \u2014 $slotName", onBack = onBack) },
        bottomBar = { SlotActionBar(onAddMeal = onAddMeal, onAddRecipe = onAddRecipe) }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            SlotTotalsHeader(meals = meals)

            if (meals.isEmpty()) {
                EmptyState(
                    emoji    = "\uD83C\uDF7D",
                    title    = "No meals yet",
                    subtitle = "Tap 'Add Pre-made Meal' or 'Build Recipe' below to plan your $slotName"
                )
            } else {
                LazyColumn(
                    contentPadding      = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(meals, key = { it.id }) { meal ->
                        SlotMealItem(
                            meal     = meal,
                            onRemove = { viewModel.removeMeal(dayName, slotName, meal) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SlotTotalsHeader(meals: List<Meal>) {
    val cal  = meals.sumOf { it.calories }
    val prot = meals.sumOf { it.proteinG }
    val carb = meals.sumOf { it.carbsG }
    val fat  = meals.sumOf { it.fatG }

    Surface(tonalElevation = 3.dp, color = MaterialTheme.colorScheme.surfaceVariant) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("$cal kcal", fontWeight = FontWeight.ExtraBold, fontSize = 22.sp, color = MaterialTheme.colorScheme.primary)
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

@Composable
fun SlotMealItem(meal: Meal, onRemove: () -> Unit) {
    var showDeleteConfirm by remember { mutableStateOf(false) }

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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(meal.name, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                    if (meal.isCustom) {
                        Spacer(Modifier.width(6.dp))
                        Surface(shape = RoundedCornerShape(6.dp), color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f)) {
                            Text("Custom", fontSize = 10.sp, color = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp))
                        }
                    }
                }
                Spacer(Modifier.height(3.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("P: ${"%.1f".format(meal.proteinG)}g", fontSize = 11.sp, color = Color(0xFF1976D2))
                    Text("C: ${"%.1f".format(meal.carbsG)}g",  fontSize = 11.sp, color = Color(0xFFF57C00))
                    Text("F: ${"%.1f".format(meal.fatG)}g",    fontSize = 11.sp, color = Color(0xFF388E3C))
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("${meal.calories}", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = MaterialTheme.colorScheme.primary)
                Text("kcal", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
            }
            Spacer(Modifier.width(4.dp))
            IconButton(onClick = { showDeleteConfirm = true }, modifier = Modifier.size(36.dp)) {
                Icon(Icons.Filled.Delete, contentDescription = "Remove", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
            }
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title  = { Text("Remove meal?") },
            text   = { Text("Remove \"${meal.name}\" from this slot?") },
            confirmButton = {
                TextButton(onClick = { onRemove(); showDeleteConfirm = false }) {
                    Text("Remove", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun SlotActionBar(onAddMeal: () -> Unit, onAddRecipe: () -> Unit) {
    Surface(shadowElevation = 10.dp) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick  = onAddMeal,
                modifier = Modifier.weight(1f).height(50.dp),
                shape    = RoundedCornerShape(14.dp)
            ) {
                Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text("Pre-made Meal", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            }
            OutlinedButton(
                onClick  = onAddRecipe,
                modifier = Modifier.weight(1f).height(50.dp),
                shape    = RoundedCornerShape(14.dp)
            ) {
                Icon(Icons.Filled.MenuBook, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text("Build Recipe", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}
