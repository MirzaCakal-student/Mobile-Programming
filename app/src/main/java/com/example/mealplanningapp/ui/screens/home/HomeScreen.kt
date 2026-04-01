package com.example.mealplanner.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mealplanner.model.SampleData
import com.example.mealplanner.ui.components.SectionHeader
import com.example.mealplanner.ui.components.StatCard
import com.example.mealplanner.viewmodel.MealPlannerViewModel
import com.example.mealplanner.viewmodel.ProfileViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun HomeScreen(
    profileViewModel: ProfileViewModel,
    mealPlannerViewModel: MealPlannerViewModel,
    onNavigateToPlanner: () -> Unit,
    onNavigateToCalories: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    val profileState  by profileViewModel.uiState.collectAsState()
    val plannerState  by mealPlannerViewModel.uiState.collectAsState()
    val completedDays  = plannerState.weekPlan.values.count { it.isComplete }
    val todayIndex     = LocalDate.now().dayOfWeek.value - 1
    val totalCalToday  = plannerState.weekPlan[SampleData.weekDays[todayIndex]]?.totalCalories ?: 0
    val goalCal        = profileState.profile.dailyCalorieGoal

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.horizontalGradient(listOf(Color(0xFF1B5E20), Color(0xFF388E3C))))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            "Good day, ${profileState.profile.name}! \uD83D\uDC4B",
                            fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White
                        )
                        Text(
                            LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, MMMM d")),
                            fontSize = 12.sp, color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                    IconButton(onClick = onNavigateToProfile) {
                        Box(
                            modifier = Modifier.size(38.dp).clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Filled.Person, contentDescription = "Profile", tint = Color.White, modifier = Modifier.size(22.dp))
                        }
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            TodayStatsRow(consumed = totalCalToday, goal = goalCal, completed = completedDays)

            WeeklyProgressCard(
                weekPlan      = plannerState.weekPlan,
                completedDays = completedDays,
                onOpenPlanner = onNavigateToPlanner
            )

            SectionHeader("Quick Actions")
            QuickActionGrid(
                onPlannerClick  = onNavigateToPlanner,
                onCaloriesClick = onNavigateToCalories,
                onProfileClick  = onNavigateToProfile
            )

            TipCard()

            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
fun TodayStatsRow(consumed: Int, goal: Int, completed: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        StatCard("$consumed", "kcal today",  Color(0xFF388E3C), modifier = Modifier.weight(1f))
        StatCard("$goal",     "kcal goal",   Color(0xFF1976D2), modifier = Modifier.weight(1f))
        StatCard("$completed/7", "days done", Color(0xFFF57C00), modifier = Modifier.weight(1f))
    }
}

@Composable
fun WeeklyProgressCard(
    weekPlan: Map<String, com.example.mealplanner.model.DayPlan>,
    completedDays: Int,
    onOpenPlanner: () -> Unit
) {
    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("This Week", fontWeight = FontWeight.Bold, fontSize = 17.sp)
                TextButton(onClick = onOpenPlanner) {
                    Text("See all", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                }
            }
            Text(
                "$completedDays of 7 days planned",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f)
            )
            Spacer(Modifier.height(12.dp))
            LinearProgressIndicator(
                progress   = { completedDays / 7f },
                modifier   = Modifier.fillMaxWidth().height(10.dp).clip(RoundedCornerShape(5.dp)),
                color      = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
            )
            Spacer(Modifier.height(14.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SampleData.weekDays.forEach { day ->
                    val done = weekPlan[day]?.isComplete == true
                    DayBubble(label = day.take(2), done = done)
                }
            }
        }
    }
}

@Composable
private fun DayBubble(label: String, done: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(
                    if (done) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                ),
            contentAlignment = Alignment.Center
        ) {
            if (done) {
                Icon(Icons.Filled.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
            } else {
                Text(label, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            }
        }
        Spacer(Modifier.height(3.dp))
        Text(label, fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
    }
}

@Composable
fun QuickActionGrid(
    onPlannerClick: () -> Unit,
    onCaloriesClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ActionCard(
                icon    = Icons.Filled.CalendarMonth,
                label   = "Meal Planner",
                sub     = "Plan your week",
                bgStart = Color(0xFF2E7D32),
                bgEnd   = Color(0xFF43A047),
                onClick = onPlannerClick,
                modifier = Modifier.weight(1f)
            )
            ActionCard(
                icon    = Icons.Filled.LocalFireDepartment,
                label   = "Calorie Calc",
                sub     = "Find your TDEE",
                bgStart = Color(0xFFE65100),
                bgEnd   = Color(0xFFFF6D00),
                onClick = onCaloriesClick,
                modifier = Modifier.weight(1f)
            )
        }
        ActionCard(
            icon     = Icons.Filled.Person,
            label    = "My Profile",
            sub      = "Update your goals & info",
            bgStart  = Color(0xFF283593),
            bgEnd    = Color(0xFF3949AB),
            onClick  = onProfileClick,
            modifier = Modifier.fillMaxWidth(),
            tall     = false
        )
    }
}

@Composable
private fun ActionCard(
    icon: ImageVector,
    label: String,
    sub: String,
    bgStart: Color,
    bgEnd: Color,
    onClick: () -> Unit,
    modifier: Modifier,
    tall: Boolean = true
) {
    Card(
        onClick   = onClick,
        modifier  = modifier.height(if (tall) 100.dp else 72.dp),
        shape     = RoundedCornerShape(18.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.horizontalGradient(listOf(bgStart, bgEnd)))
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = label, tint = Color.White, modifier = Modifier.size(30.dp))
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(label, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(sub, color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
fun TipCard() {
    val tips = listOf(
        "Prep your meals on Sunday to save time during the week.",
        "Drink at least 8 glasses of water per day for optimal energy.",
        "Fill half your plate with vegetables at every main meal.",
        "Eating at consistent times helps regulate hunger hormones.",
        "Pair your meal plan with 30 minutes of daily movement."
    )
    val tip = remember { tips.random() }
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(16.dp),
        colors   = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1))
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
            Icon(Icons.Filled.EmojiObjects, contentDescription = null, tint = Color(0xFFF9A825), modifier = Modifier.size(22.dp))
            Spacer(Modifier.width(10.dp))
            Column {
                Text("Tip of the Day", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFFF57F17))
                Spacer(Modifier.height(3.dp))
                Text(tip, fontSize = 13.sp, color = Color(0xFF5D4037))
            }
        }
    }
}
