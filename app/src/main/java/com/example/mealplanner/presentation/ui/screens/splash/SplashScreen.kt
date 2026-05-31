package com.example.mealplanner.presentation.ui.screens.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onFinished: (isLoggedIn: Boolean) -> Unit, isLoggedIn: Boolean = false) {

    val scale by animateFloatAsState(
        targetValue   = 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label         = "scale"
    )
    val alpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        alpha.animateTo(1f, animationSpec = tween(800, easing = FastOutSlowInEasing))
        delay(1600)
        onFinished(isLoggedIn)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF1B5E20), Color(0xFF2E7D32), Color(0xFF43A047))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .scale(scale)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector        = Icons.Filled.Restaurant,
                    contentDescription = "Logo",
                    tint               = Color.White,
                    modifier           = Modifier.size(62.dp)
                )
            }

            Spacer(Modifier.height(28.dp))

            Text(
                text       = "MealPlanner",
                fontSize   = 34.sp,
                fontWeight = FontWeight.ExtraBold,
                color      = Color.White,
                modifier   = Modifier.alpha(alpha.value)
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text     = "Plan your week. Fuel your life.",
                fontSize = 15.sp,
                color    = Color.White.copy(alpha = 0.8f),
                modifier = Modifier.alpha(alpha.value)
            )

            Spacer(Modifier.height(60.dp))

            CircularProgressIndicator(
                color       = Color.White.copy(alpha = 0.7f),
                modifier    = Modifier
                    .size(30.dp)
                    .alpha(alpha.value),
                strokeWidth = 2.dp
            )
        }
    }
}
