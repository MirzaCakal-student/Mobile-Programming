package com.example.mealplanner.presentation.ui.screens.recipe

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import com.example.mealplanner.model.HardcodedData
import com.example.mealplanner.model.Ingredient
import com.example.mealplanner.presentation.ui.components.AppSearchBar
import com.example.mealplanner.presentation.ui.components.MacroChip
import com.example.mealplanner.presentation.ui.components.MealPlannerTopBar
import com.example.mealplanner.presentation.viewmodel.MealPlannerViewModel

data class IngredientEntry(val ingredient: Ingredient, val grams: Double)

@Composable
fun RecipeBuilderScreen(
    dayName: String,
    slotName: String,
    viewModel: MealPlannerViewModel,
    onBack: () -> Unit
) {
    var recipeName          by remember { mutableStateOf("") }
    var nameError           by remember { mutableStateOf<String?>(null) }
    var searchQuery         by remember { mutableStateOf("") }
    var selectedIngredients by remember { mutableStateOf<List<IngredientEntry>>(emptyList()) }
    var selectedTab         by remember { mutableStateOf(0) }

    // Derived values from local state (no business logic in composable — pure UI computation)
    val totalCal  = selectedIngredients.sumOf { (it.ingredient.caloriesPer100g * it.grams / 100).toInt() }
    val totalProt = selectedIngredients.sumOf { it.ingredient.proteinPer100g * it.grams / 100 }
    val totalCarb = selectedIngredients.sumOf { it.ingredient.carbsPer100g   * it.grams / 100 }
    val totalFat  = selectedIngredients.sumOf { it.ingredient.fatPer100g     * it.grams / 100 }

    val filteredIngredients = HardcodedData.ingredients.filter {
        searchQuery.isBlank() || it.name.contains(searchQuery, ignoreCase = true)
    }

    Scaffold(
        topBar    = { MealPlannerTopBar(title = "Recipe Builder", onBack = onBack) },
        bottomBar = {
            RecipeBottomBar(
                recipeName   = recipeName,
                totalCal     = totalCal,
                canSave      = recipeName.isNotBlank() && selectedIngredients.isNotEmpty(),
                onNameChange = { recipeName = it; nameError = null },
                nameError    = nameError,
                onSave       = {
                    if (recipeName.isBlank()) { nameError = "Recipe name is required"; return@RecipeBottomBar }
                    if (selectedIngredients.isEmpty()) return@RecipeBottomBar
                    viewModel.addCustomRecipeAsMeal(
                        dayName  = dayName, slotName = slotName, name = recipeName,
                        calories = totalCal, proteinG = totalProt, fatG = totalFat, carbsG = totalCarb
                    )
                    onBack()
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            RecipeMacroBar(totalCal = totalCal, totalProt = totalProt, totalCarb = totalCarb, totalFat = totalFat, count = selectedIngredients.size)

            TabRow(selectedTabIndex = selectedTab) {
                Tab(
                    selected = selectedTab == 0,
                    onClick  = { selectedTab = 0 },
                    text     = { Text("Add Ingredients (${selectedIngredients.size})") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick  = { selectedTab = 1 },
                    text     = { Text("My Recipe") }
                )
            }

            when (selectedTab) {
                0 -> Column(modifier = Modifier.fillMaxSize()) {
                    AppSearchBar(
                        query         = searchQuery,
                        onQueryChange = { searchQuery = it },
                        placeholder   = "Search 55 ingredients…",
                        modifier      = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                    )
                    // LazyColumn for ingredients list with search filter
                    LazyColumn(
                        contentPadding      = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(filteredIngredients, key = { it.id }) { ingredient ->
                            val existing = selectedIngredients.find { it.ingredient.id == ingredient.id }
                            IngredientCard(
                                ingredient   = ingredient,
                                currentGrams = existing?.grams ?: 0.0,
                                onAdd        = { grams ->
                                    selectedIngredients = if (existing != null) {
                                        selectedIngredients.map { if (it.ingredient.id == ingredient.id) it.copy(grams = grams) else it }
                                    } else {
                                        selectedIngredients + IngredientEntry(ingredient, grams)
                                    }
                                },
                                onRemove = {
                                    selectedIngredients = selectedIngredients.filter { it.ingredient.id != ingredient.id }
                                }
                            )
                        }
                    }
                }
                1 -> {
                    if (selectedIngredients.isEmpty()) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("🍜", fontSize = 48.sp)
                                Spacer(Modifier.height(12.dp))
                                Text("No ingredients added yet", fontWeight = FontWeight.SemiBold)
                                Text(
                                    "Go back to the first tab to add ingredients",
                                    fontSize = 13.sp,
                                    color    = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                )
                            }
                        }
                    } else {
                        // LazyColumn for selected ingredients in recipe
                        LazyColumn(
                            contentPadding      = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(selectedIngredients, key = { it.ingredient.id }) { entry ->
                                SelectedIngredientRow(
                                    entry    = entry,
                                    onRemove = {
                                        selectedIngredients = selectedIngredients.filter { it.ingredient.id != entry.ingredient.id }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RecipeMacroBar(totalCal: Int, totalProt: Double, totalCarb: Double, totalFat: Double, count: Int) {
    Surface(tonalElevation = 3.dp, color = MaterialTheme.colorScheme.surfaceVariant) {
        Row(
            modifier              = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("$totalCal kcal", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = MaterialTheme.colorScheme.primary)
                Text("$count ingredient(s)", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                MacroChip("P", "${"%.0f".format(totalProt)}g", Color(0xFF1976D2))
                MacroChip("C", "${"%.0f".format(totalCarb)}g", Color(0xFFF57C00))
                MacroChip("F", "${"%.0f".format(totalFat)}g",  Color(0xFF388E3C))
            }
        }
    }
}

@Composable
fun IngredientCard(
    ingredient: Ingredient,
    currentGrams: Double,
    onAdd: (Double) -> Unit,
    onRemove: () -> Unit
) {
    var gramsInput by remember(ingredient.id) { mutableStateOf(if (currentGrams > 0) currentGrams.toInt().toString() else "") }
    val isAdded    = currentGrams > 0

    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(if (isAdded) 2.dp else 1.dp),
        colors    = CardDefaults.cardColors(
            containerColor = if (isAdded) MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
                             else MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier              = Modifier.fillMaxWidth(),
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(ingredient.name, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    Text(
                        "per 100g: ${ingredient.caloriesPer100g} kcal  P:${"%.1f".format(ingredient.proteinPer100g)}g  C:${"%.1f".format(ingredient.carbsPer100g)}g  F:${"%.1f".format(ingredient.fatPer100g)}g",
                        fontSize = 11.sp,
                        color    = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
                if (isAdded) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                    ) {
                        Text(
                            "Added ✓",
                            fontSize   = 11.sp,
                            color      = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            modifier   = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
            Row(
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value         = gramsInput,
                    onValueChange = { gramsInput = it.filter { c -> c.isDigit() } },
                    label         = { Text("Grams", fontSize = 12.sp) },
                    singleLine    = true,
                    modifier      = Modifier.width(90.dp),
                    shape         = RoundedCornerShape(10.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    suffix        = { Text("g", fontSize = 12.sp) }
                )
                Button(
                    onClick  = { val g = gramsInput.toDoubleOrNull() ?: 0.0; if (g > 0) onAdd(g) },
                    enabled  = gramsInput.toDoubleOrNull()?.let { it > 0 } == true,
                    shape    = RoundedCornerShape(10.dp),
                    modifier = Modifier.height(48.dp)
                ) {
                    Icon(if (isAdded) Icons.Filled.Refresh else Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(if (isAdded) "Update" else "Add", fontSize = 13.sp)
                }
                if (isAdded) {
                    IconButton(onClick = onRemove, modifier = Modifier.size(40.dp)) {
                        Icon(Icons.Filled.Delete, contentDescription = "Remove", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun SelectedIngredientRow(entry: IngredientEntry, onRemove: () -> Unit) {
    val calories = (entry.ingredient.caloriesPer100g * entry.grams / 100).toInt()
    val protein  = entry.ingredient.proteinPer100g * entry.grams / 100
    val carbs    = entry.ingredient.carbsPer100g   * entry.grams / 100
    val fat      = entry.ingredient.fatPer100g     * entry.grams / 100

    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), elevation = CardDefaults.cardElevation(1.dp)) {
        Row(
            modifier              = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("${entry.ingredient.name}  ·  ${"%.0f".format(entry.grams)}g", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                Text(
                    "$calories kcal  |  P:${"%.1f".format(protein)}g  C:${"%.1f".format(carbs)}g  F:${"%.1f".format(fat)}g",
                    fontSize = 11.sp,
                    color    = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f)
                )
            }
            IconButton(onClick = onRemove, modifier = Modifier.size(36.dp)) {
                Icon(Icons.Filled.Close, contentDescription = "Remove", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
            }
        }
    }
}

@Composable
fun RecipeBottomBar(
    recipeName: String,
    totalCal: Int,
    canSave: Boolean,
    nameError: String?,
    onNameChange: (String) -> Unit,
    onSave: () -> Unit
) {
    Surface(shadowElevation = 12.dp) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
            OutlinedTextField(
                value          = recipeName,
                onValueChange  = onNameChange,
                label          = { Text("Recipe Name *") },
                placeholder    = { Text("e.g. My Protein Bowl") },
                singleLine     = true,
                isError        = nameError != null,
                modifier       = Modifier.fillMaxWidth(),
                shape          = RoundedCornerShape(12.dp),
                supportingText = if (nameError != null) ({ Text(nameError, color = MaterialTheme.colorScheme.error) }) else null
            )
            Spacer(Modifier.height(8.dp))
            Button(
                onClick  = onSave,
                enabled  = canSave,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape    = RoundedCornerShape(14.dp)
            ) {
                Icon(Icons.Filled.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Save Recipe ($totalCal kcal)", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}
