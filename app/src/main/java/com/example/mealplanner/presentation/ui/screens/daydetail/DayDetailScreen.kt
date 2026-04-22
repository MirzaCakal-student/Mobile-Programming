package com.example.mealplanner.presentation.ui.screens.daydetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.example.mealplanner.model.Meal
import com.example.mealplanner.model.MealSlotType
import com.example.mealplanner.presentation.ui.components.CalorieStatusBanner
import com.example.mealplanner.presentation.ui.components.MacroChip
import com.example.mealplanner.presentation.ui.components.MealPlannerTopBar
import com.example.mealplanner.presentation.viewmodel.DayDetailViewModel

@Composable
fun DayDetailScreen(
    viewModel: DayDetailViewModel,
    onSlotClick: (String) -> Unit,
    onBack: () -> Unit
) {
    val dayPlan by viewModel.dayPlan.collectAsState()
    val goal    = 2000

    Scaffold(
        topBar    = { MealPlannerTopBar(title = viewModel.dayName, onBack = onBack) },
        bottomBar = {
            DayDoneBottomBar(
                isComplete = dayPlan?.isComplete == true,
                onMarkDone = viewModel::markDayComplete
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            DailyTotalsCard(dayPlan = dayPlan)
            CalorieStatusBanner(consumed = dayPlan?.totalCalories ?: 0, goal = goal)

            MealSlotType.values().forEach { slot ->
                val meals = dayPlan?.mealsForSlot(slot) ?: emptyList()
                val time  = when (slot) {
                    MealSlotType.BREAKFAST -> dayPlan?.eatTimeBreakfast ?: "08:00"
                    MealSlotType.LUNCH     -> dayPlan?.eatTimeLunch     ?: "13:00"
                    MealSlotType.DINNER    -> dayPlan?.eatTimeDinner    ?: "19:00"
                    MealSlotType.SNACKS    -> dayPlan?.eatTimeSnacks    ?: "16:00"
                }
                MealSlotCard(
                    slot    = slot,
                    meals   = meals,
                    eatTime = time,
                    onClick = { onSlotClick(slot.displayName) }
                )
            }
        }
    }
}

@Composable
fun DailyTotalsCard(dayPlan: DayPlan?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(18.dp),
        colors   = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text("Daily Totals", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = Color.White.copy(alpha = 0.8f))
            Spacer(Modifier.height(4.dp))
            Text(
                "${dayPlan?.totalCalories ?: 0} kcal",
                fontWeight = FontWeight.ExtraBold,
                fontSize   = 32.sp,
                color      = Color.White
            )
            Spacer(Modifier.height(14.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                MacroChipWhite("Protein", "${"%.1f".format(dayPlan?.totalProtein ?: 0.0)}g", modifier = Modifier.weight(1f))
                MacroChipWhite("Carbs",   "${"%.1f".format(dayPlan?.totalCarbs   ?: 0.0)}g", modifier = Modifier.weight(1f))
                MacroChipWhite("Fat",     "${"%.1f".format(dayPlan?.totalFat     ?: 0.0)}g", modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun MacroChipWhite(label: String, value: String, modifier: Modifier = Modifier) {
    Surface(
        shape    = RoundedCornerShape(10.dp),
        color    = Color.White.copy(alpha = 0.2f),
        modifier = modifier
    ) {
        Column(
            modifier            = Modifier.padding(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
            Text(label, fontSize   = 10.sp, color = Color.White.copy(alpha = 0.8f))
        }
    }
}

@Composable
fun MealSlotCard(
    slot: MealSlotType,
    meals: List<Meal>,
    eatTime: String,
    onClick: () -> Unit
) {
    val (icon, color) = when (slot) {
        MealSlotType.BREAKFAST -> Pair(Icons.Filled.WbSunny,     Color(0xFFF9A825))
        MealSlotType.LUNCH     -> Pair(Icons.Filled.LunchDining,  Color(0xFF388E3C))
        MealSlotType.DINNER    -> Pair(Icons.Filled.DinnerDining, Color(0xFF1976D2))
        MealSlotType.SNACKS    -> Pair(Icons.Filled.Cookie,       Color(0xFFE64A19))
    }
    val slotCalories = meals.sumOf { it.calories }

    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier              = Modifier.fillMaxWidth(),
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(color.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
                    }
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text(slot.displayName, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.AccessTime, contentDescription = null, modifier = Modifier.size(11.dp), tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f))
                            Spacer(Modifier.width(3.dp))
                            Text(eatTime, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f))
                            if (slotCalories > 0) {
                                Text("  ·  $slotCalories kcal", fontSize = 11.sp, color = color, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
                FilledTonalButton(
                    onClick        = onClick,
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                    colors         = ButtonDefaults.filledTonalButtonColors(containerColor = color.copy(alpha = 0.12f))
                ) {
                    Icon(Icons.Filled.Add, contentDescription = null, tint = color, modifier = Modifier.size(15.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Add", fontSize = 12.sp, color = color, fontWeight = FontWeight.SemiBold)
                }
            }

            if (meals.isNotEmpty()) {
                Spacer(Modifier.height(10.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.07f))
                Spacer(Modifier.height(8.dp))
                meals.forEach { meal -> SlotMealRow(meal = meal) }
            } else {
                Spacer(Modifier.height(8.dp))
                Text(
                    "No meals yet — tap Add to plan ${slot.displayName.lowercase()}",
                    fontSize = 12.sp,
                    color    = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                )
            }
        }
    }
}

@Composable
private fun SlotMealRow(meal: Meal) {
    Row(
        modifier              = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(MaterialTheme.colorScheme.primary)
            )
            Spacer(Modifier.width(8.dp))
            Column {
                Text(meal.name, fontWeight = FontWeight.Medium, fontSize = 13.sp)
                Text(
                    "P:${"%.0f".format(meal.proteinG)}g  C:${"%.0f".format(meal.carbsG)}g  F:${"%.0f".format(meal.fatG)}g",
                    fontSize = 10.sp,
                    color    = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
        }
        Text("${meal.calories} kcal", fontWeight = FontWeight.SemiBold, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
    }
}

@Composable
fun DayDoneBottomBar(isComplete: Boolean, onMarkDone: () -> Unit) {
    Surface(shadowElevation = 10.dp) {
        Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            Button(
                onClick  = onMarkDone,
                enabled  = !isComplete,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape    = RoundedCornerShape(14.dp),
                colors   = ButtonDefaults.buttonColors(
                    containerColor         = if (isComplete) Color(0xFF2E7D32) else MaterialTheme.colorScheme.primary,
                    disabledContainerColor = Color(0xFF2E7D32)
                )
            ) {
                if (isComplete) {
                    Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = Color.White)
                    Spacer(Modifier.width(8.dp))
                    Text("Day Complete ✓", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                } else {
                    Icon(Icons.Filled.Check, contentDescription = null, tint = Color.White)
                    Spacer(Modifier.width(8.dp))
                    Text("Mark Day as Done", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    }
}
