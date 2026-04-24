package com.example.mealplanner.presentation.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mealplanner.model.Meal

/**
 * Reusable composable for displaying a single meal in a list.
 * Used by AddMealScreen, MealSlotScreen, and the Home suggested-meals row.
 *
 * Placed in components/ so it can be reused across multiple screens.
 */
@Composable
fun MealListItem(
    meal: Meal,
    modifier: Modifier = Modifier,
    onAdd: (() -> Unit)? = null,
    onRemove: (() -> Unit)? = null,
    showDeleteConfirmDialog: Boolean = false
) {
    var showConfirm by remember { mutableStateOf(false) }

    Card(
        modifier  = modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier              = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // ── Meal info ────────────────────────────────────────────────────────
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text       = meal.name,
                        fontWeight = FontWeight.SemiBold,
                        fontSize   = 15.sp,
                        maxLines   = 1
                    )
                    if (meal.isCustom) {
                        Spacer(Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text     = "Custom",
                                fontSize = 10.sp,
                                color    = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                Spacer(Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text       = "${meal.calories} kcal",
                        fontWeight = FontWeight.Bold,
                        fontSize   = 13.sp,
                        color      = MaterialTheme.colorScheme.primary
                    )
                    Text("P:${"%.0f".format(meal.proteinG)}g", fontSize = 12.sp, color = Color(0xFF1976D2))
                    Text("C:${"%.0f".format(meal.carbsG)}g",  fontSize = 12.sp, color = Color(0xFFF57C00))
                    Text("F:${"%.0f".format(meal.fatG)}g",    fontSize = 12.sp, color = Color(0xFF388E3C))
                }
            }

            // ── Action buttons ───────────────────────────────────────────────────
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                if (onAdd != null) {
                    FilledIconButton(
                        onClick  = onAdd,
                        modifier = Modifier.size(38.dp),
                        colors   = IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(
                            Icons.Filled.Add,
                            contentDescription = "Add meal",
                            tint     = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                if (onRemove != null) {
                    IconButton(
                        onClick  = { if (showDeleteConfirmDialog) showConfirm = true else onRemove() },
                        modifier = Modifier.size(38.dp)
                    ) {
                        Icon(
                            Icons.Filled.Delete,
                            contentDescription = "Remove meal",
                            tint     = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }

    // Delete confirmation dialog
    if (showConfirm) {
        AlertDialog(
            onDismissRequest = { showConfirm = false },
            title  = { Text("Remove meal?") },
            text   = { Text("Remove \"${meal.name}\" from this slot?") },
            confirmButton = {
                TextButton(onClick = { onRemove?.invoke(); showConfirm = false }) {
                    Text("Remove", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirm = false }) { Text("Cancel") }
            }
        )
    }
}

/**
 * Compact horizontal card version of a meal — used inside LazyRow.
 * Displays the meal name and calorie count in a compact card.
 */
@Composable
fun MealCardCompact(
    meal: Meal,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Card(
        onClick   = onClick,
        modifier  = modifier
            .width(160.dp)
            .height(90.dp),
        shape     = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier            = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text       = meal.name,
                fontWeight = FontWeight.SemiBold,
                fontSize   = 13.sp,
                maxLines   = 2,
                lineHeight = 17.sp,
                color      = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text       = "${meal.calories} kcal",
                fontWeight = FontWeight.ExtraBold,
                fontSize   = 14.sp,
                color      = MaterialTheme.colorScheme.primary
            )
        }
    }
}
