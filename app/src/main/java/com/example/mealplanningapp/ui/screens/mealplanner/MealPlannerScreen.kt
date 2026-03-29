package com.example.mealplanner.ui.screens.mealplanner

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mealplanner.model.DayPlan
import com.example.mealplanner.model.SampleData
import com.example.mealplanner.ui.components.MealPlannerTopBar
import com.example.mealplanner.ui.components.SectionHeader
import com.example.mealplanner.viewmodel.MealPlannerViewModel

@Composable
fun MealPlannerScreen(
    viewModel: MealPlannerViewModel,
    onDayClick: (String) -> Unit
) {
    val state         by viewModel.uiState.collectAsState()
    val completedDays  = state.weekPlan.values.count { it.isComplete }

    Scaffold(
        topBar = { MealPlannerTopBar(title = "Weekly Meal Plan") }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            item { WeekSummaryBar(completedDays = completedDays) }
            item { SectionHeader("Days of the Week") }

            items(SampleData.weekDays) { dayName ->
                val dayPlan = state.weekPlan[dayName]
                DayCard(dayName = dayName, dayPlan = dayPlan, onClick = { onDayClick(dayName) })
            }
        }
    }
}

@Composable
fun WeekSummaryBar(completedDays: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SummaryChip(
            icon     = Icons.Filled.CheckCircle,
            label    = "$completedDays / 7 done",
            color    = MaterialTheme.colorScheme.primary,
            modifier = Modifier.weight(1f)
        )
        SummaryChip(
            icon     = Icons.Filled.RadioButtonUnchecked,
            label    = "${7 - completedDays} remaining",
            color    = Color(0xFFF57C00),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun SummaryChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    color: Color,
    modifier: Modifier
) {
    Surface(
        shape    = RoundedCornerShape(12.dp),
        color    = color.copy(alpha = 0.1f),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(6.dp))
            Text(label, fontSize = 13.sp, color = color, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun DayCard(
    dayName: String,
    dayPlan: DayPlan?,
    onClick: () -> Unit
) {
    val mealCount     = (dayPlan?.breakfast?.size ?: 0) + (dayPlan?.lunch?.size ?: 0) + (dayPlan?.dinner?.size ?: 0)
    val totalCalories = dayPlan?.totalCalories ?: 0
    val isComplete    = dayPlan?.isComplete == true

    Card(
        onClick   = onClick,
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isComplete) 0.dp else 2.dp),
        colors    = CardDefaults.cardColors(
            containerColor = if (isComplete) MaterialTheme.colorScheme.primary.copy(alpha = 0.07f)
                             else MaterialTheme.colorScheme.surface
        ),
        border    = if (isComplete) ButtonDefaults.outlinedButtonBorder else null
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(
                        if (isComplete) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isComplete) {
                    Icon(Icons.Filled.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
                } else {
                    Text(dayName.take(2), fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
                }
            }

            Spacer(Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(dayName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    if (isComplete) {
                        Spacer(Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                        ) {
                            Text(
                                "Done \u2713", fontSize = 10.sp, color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                Spacer(Modifier.height(3.dp))
                if (mealCount == 0) {
                    Text("Tap to plan meals", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f))
                } else {
                    Text(
                        "$mealCount meal(s)  \u00B7  $totalCalories kcal",
                        fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
                    )
                }
            }

            if (mealCount > 0) {
                Column(horizontalAlignment = Alignment.End) {
                    Text("${dayPlan?.totalProtein?.toInt() ?: 0}g P", fontSize = 11.sp, color = Color(0xFF1976D2), fontWeight = FontWeight.SemiBold)
                    Text("${dayPlan?.totalCarbs?.toInt()  ?: 0}g C", fontSize = 11.sp, color = Color(0xFFF57C00), fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(Modifier.width(6.dp))
            Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp))
        }
    }
}
