package com.example.mealplanner.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary            = Green40,
    onPrimary          = Color.White,
    primaryContainer   = Green80,
    secondary          = Orange40,
    onSecondary        = Color.White,
    secondaryContainer = Orange80,
    background         = Background,
    surface            = Surface,
    onSurface          = OnSurface,
    error              = ErrorRed,
    onError            = Color.White,
)

@Composable
fun MealPlannerTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography  = Typography,
        content     = content
    )
}
