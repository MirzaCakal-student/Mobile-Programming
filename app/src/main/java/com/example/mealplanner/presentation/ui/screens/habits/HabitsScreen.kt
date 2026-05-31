package com.example.mealplanner.presentation.ui.screens.habits

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mealplanner.model.HabitModel
import com.example.mealplanner.presentation.ui.components.AppTextField
import com.example.mealplanner.presentation.ui.components.EmptyState
import com.example.mealplanner.presentation.ui.components.MealPlannerTopBar
import com.example.mealplanner.presentation.ui.components.PrimaryButton
import com.example.mealplanner.presentation.ui.components.SecondaryButton
import com.example.mealplanner.presentation.viewmodel.HabitsUiState
import com.example.mealplanner.presentation.viewmodel.HabitsViewModel

// ─────────────────────────────────────────────────────────────────────────────
// STATEFUL wrapper — handles the state, dispatches callbacks to the stateless body
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun HabitsScreen(
    viewModel: HabitsViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }

    HabitsContent(
        state           = uiState,
        onBack          = onBack,
        onRefresh       = viewModel::refresh,
        onAddClick      = { showAddDialog = true },
        onToggle        = viewModel::toggleCompleted,
        onDelete        = { habit -> viewModel.removeHabit(habit.id) }
    )

    if (showAddDialog) {
        AddHabitDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { title, description, frequency ->
                viewModel.addHabit(title, description, frequency)
                showAddDialog = false
            }
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// STATELESS body — renders one of: Loading spinner / Error message / habit list
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun HabitsContent(
    state: HabitsUiState,
    onBack: () -> Unit,
    onRefresh: () -> Unit,
    onAddClick: () -> Unit,
    onToggle: (HabitModel) -> Unit,
    onDelete: (HabitModel) -> Unit
) {
    Scaffold(
        topBar = {
            MealPlannerTopBar(
                title   = "My Habits",
                onBack  = onBack,
                actions = {
                    IconButton(onClick = onRefresh) {
                        Icon(Icons.Filled.Refresh, contentDescription = "Refresh")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick        = onAddClick,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add habit")
            }
        }
    ) { padding ->
        Box(
            modifier         = Modifier.fillMaxSize().padding(padding),
            contentAlignment = Alignment.Center
        ) {
            when (state) {
                HabitsUiState.Init,
                HabitsUiState.Loading -> CircularProgressIndicator()

                is HabitsUiState.Error -> ErrorView(message = state.message, onRetry = onRefresh)

                is HabitsUiState.Success -> {
                    if (state.habits.isEmpty()) {
                        EmptyState(
                            emoji    = "📝",
                            title    = "No habits yet",
                            subtitle = "Tap + to create your first habit"
                        )
                    } else {
                        HabitList(
                            habits   = state.habits,
                            onToggle = onToggle,
                            onDelete = onDelete
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ErrorView(message: String, onRetry: () -> Unit) {
    Column(
        modifier            = Modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("⚠️ $message", color = MaterialTheme.colorScheme.error)
        Text(
            "Is the backend running on http://10.0.2.2:8000/ ?",
            fontSize = 12.sp,
            color    = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        PrimaryButton(text = "Retry", onClick = onRetry)
    }
}

@Composable
private fun HabitList(
    habits: List<HabitModel>,
    onToggle: (HabitModel) -> Unit,
    onDelete: (HabitModel) -> Unit
) {
    LazyColumn(
        modifier            = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(habits, key = { it.id }) { habit ->
            HabitCard(habit = habit, onToggle = onToggle, onDelete = onDelete)
        }
    }
}

@Composable
private fun HabitCard(
    habit: HabitModel,
    onToggle: (HabitModel) -> Unit,
    onDelete: (HabitModel) -> Unit
) {
    Card(
        shape  = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier             = Modifier.padding(12.dp),
            verticalAlignment    = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(onClick = { onToggle(habit) }) {
                Icon(
                    imageVector        = if (habit.completed) Icons.Filled.CheckCircle else Icons.Filled.RadioButtonUnchecked,
                    contentDescription = if (habit.completed) "Mark incomplete" else "Mark complete",
                    tint               = if (habit.completed) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text           = habit.title,
                    fontSize       = 16.sp,
                    fontWeight     = FontWeight.SemiBold,
                    textDecoration = if (habit.completed) TextDecoration.LineThrough else TextDecoration.None
                )
                if (habit.description.isNotBlank()) {
                    Text(
                        text     = habit.description,
                        fontSize = 13.sp,
                        color    = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
                Text(
                    text     = "⏱  ${habit.frequency}   ·   #${habit.id}",
                    fontSize = 11.sp,
                    color    = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }

            IconButton(onClick = { onDelete(habit) }) {
                Icon(
                    Icons.Filled.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// ADD dialog — three fields, fires POST when "Save" is tapped
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun AddHabitDialog(
    onDismiss: () -> Unit,
    onConfirm: (title: String, description: String, frequency: String) -> Unit
) {
    var title       by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var frequency   by remember { mutableStateOf("Daily") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title            = { Text("New habit") },
        text             = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                AppTextField(
                    value         = title,
                    onValueChange = { title = it },
                    label         = "Title",
                    placeholder   = "e.g. Drink 2L water"
                )
                AppTextField(
                    value         = description,
                    onValueChange = { description = it },
                    label         = "Description",
                    placeholder   = "Why does this habit matter?"
                )
                AppTextField(
                    value         = frequency,
                    onValueChange = { frequency = it },
                    label         = "Frequency",
                    placeholder   = "Daily, 3x/week, weekends..."
                )
            }
        },
        confirmButton = {
            PrimaryButton(
                text    = "Save",
                onClick = { onConfirm(title, description, frequency) },
                enabled = title.isNotBlank()
            )
        },
        dismissButton = {
            SecondaryButton(text = "Cancel", onClick = onDismiss)
        }
    )
}
