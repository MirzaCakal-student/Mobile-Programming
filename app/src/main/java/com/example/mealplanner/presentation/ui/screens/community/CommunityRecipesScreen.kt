package com.example.mealplanner.presentation.ui.screens.community

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mealplanner.model.CommunityRecipe
import com.example.mealplanner.presentation.ui.components.AppTextField
import com.example.mealplanner.presentation.ui.components.EmptyState
import com.example.mealplanner.presentation.ui.components.MealPlannerTopBar
import com.example.mealplanner.presentation.ui.components.PrimaryButton
import com.example.mealplanner.presentation.ui.components.SecondaryButton
import com.example.mealplanner.presentation.viewmodel.CommunityRecipesUiState
import com.example.mealplanner.presentation.viewmodel.CommunityRecipesViewModel
import java.text.DateFormat
import java.util.Date

// ─────────────────────────────────────────────────────────────────────────────
// STATEFUL wrapper
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun CommunityRecipesScreen(
    viewModel: CommunityRecipesViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }

    CommunityRecipesContent(
        state       = uiState,
        onBack      = onBack,
        onAddClick  = { showAddDialog = true },
        onDelete    = { recipe -> viewModel.deleteRecipe(recipe.documentId) }
    )

    if (showAddDialog) {
        AddRecipeDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { title, description, caloriesText, ingredients ->
                val cal = caloriesText.toIntOrNull() ?: 0
                viewModel.addRecipe(title, description, cal, ingredients)
                showAddDialog = false
            }
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// STATELESS content
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun CommunityRecipesContent(
    state: CommunityRecipesUiState,
    onBack: () -> Unit,
    onAddClick: () -> Unit,
    onDelete: (CommunityRecipe) -> Unit
) {
    Scaffold(
        topBar = { MealPlannerTopBar(title = "Community Recipes", onBack = onBack) },
        floatingActionButton = {
            FloatingActionButton(
                onClick        = onAddClick,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add recipe")
            }
        }
    ) { padding ->
        Box(
            modifier         = Modifier.fillMaxSize().padding(padding),
            contentAlignment = Alignment.Center
        ) {
            when (state) {
                CommunityRecipesUiState.Loading -> CircularProgressIndicator()
                is CommunityRecipesUiState.Error -> Text("⚠️ ${state.message}", color = MaterialTheme.colorScheme.error)
                is CommunityRecipesUiState.Success -> {
                    if (state.recipes.isEmpty()) {
                        EmptyState(
                            emoji    = "🍽️",
                            title    = "No community recipes yet",
                            subtitle = "Tap + to share your first recipe with everyone"
                        )
                    } else {
                        RecipeList(
                            recipes          = state.recipes,
                            currentUserEmail = state.currentUserEmail,
                            onDelete         = onDelete
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RecipeList(
    recipes: List<CommunityRecipe>,
    currentUserEmail: String,
    onDelete: (CommunityRecipe) -> Unit
) {
    LazyColumn(
        modifier            = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(recipes, key = { it.documentId }) { recipe ->
            RecipeCard(
                recipe        = recipe,
                isOwnedByUser = recipe.authorEmail == currentUserEmail,
                onDelete      = { onDelete(recipe) }
            )
        }
    }
}

@Composable
private fun RecipeCard(
    recipe: CommunityRecipe,
    isOwnedByUser: Boolean,
    onDelete: () -> Unit
) {
    Card(
        shape  = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {

            // Title row + delete (only for the author's own recipes)
            Row(verticalAlignment = Alignment.Top) {
                Text(
                    text       = recipe.title,
                    fontSize   = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier   = Modifier.weight(1f)
                )
                if (isOwnedByUser) {
                    IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                        Icon(
                            Icons.Filled.Delete,
                            contentDescription = "Delete (yours)",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            if (recipe.description.isNotBlank()) {
                Text(
                    text     = recipe.description,
                    fontSize = 13.sp,
                    color    = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
                )
            }
            if (recipe.ingredients.isNotBlank()) {
                Text(
                    text     = "🧂  ${recipe.ingredients}",
                    fontSize = 12.sp,
                    color    = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
                )
            }

            Spacer(Modifier.height(2.dp))

            Row(
                verticalAlignment    = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.LocalFireDepartment, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(2.dp))
                    Text("${recipe.calories} kcal", fontSize = 11.sp)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Person, null, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(2.dp))
                    Text(recipe.authorName, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                }
                Spacer(Modifier.weight(1f))
                Text(
                    text     = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(Date(recipe.timestampMillis)),
                    fontSize = 10.sp,
                    color    = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// ADD dialog
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun AddRecipeDialog(
    onDismiss: () -> Unit,
    onConfirm: (title: String, description: String, calories: String, ingredients: String) -> Unit
) {
    var title       by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var calories    by remember { mutableStateOf("") }
    var ingredients by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title            = { Text("Share a recipe") },
        text             = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                AppTextField(
                    value = title, onValueChange = { title = it },
                    label = "Title", placeholder = "e.g. Grilled Chicken Salad"
                )
                AppTextField(
                    value = description, onValueChange = { description = it },
                    label = "Description", placeholder = "What makes this great?"
                )
                AppTextField(
                    value        = calories, onValueChange = { calories = it.filter(Char::isDigit) },
                    label        = "Calories (kcal)",
                    placeholder  = "e.g. 420",
                    keyboardType = KeyboardType.Number
                )
                AppTextField(
                    value = ingredients, onValueChange = { ingredients = it },
                    label = "Ingredients", placeholder = "chicken, lettuce, tomato..."
                )
            }
        },
        confirmButton = {
            PrimaryButton(
                text    = "Post",
                onClick = { onConfirm(title, description, calories, ingredients) },
                enabled = title.isNotBlank()
            )
        },
        dismissButton = { SecondaryButton(text = "Cancel", onClick = onDismiss) }
    )
}
