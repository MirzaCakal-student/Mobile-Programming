package com.example.mealplanner.presentation.ui.screens.weather

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.mealplanner.model.Weather
import com.example.mealplanner.presentation.ui.components.AppTextField
import com.example.mealplanner.presentation.ui.components.MealPlannerTopBar
import com.example.mealplanner.presentation.ui.components.PrimaryButton
import com.example.mealplanner.presentation.viewmodel.WeatherUiState
import com.example.mealplanner.presentation.viewmodel.WeatherViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

// ─────────────────────────────────────────────────────────────────────────────
// STATEFUL wrapper — observes ViewModel, dispatches actions to stateless body
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun WeatherScreen(
    viewModel: WeatherViewModel,
    onBack: () -> Unit
) {
    val uiState  by viewModel.uiState.collectAsStateWithLifecycle()
    val cityInput by viewModel.cityInput.collectAsStateWithLifecycle()

    WeatherContent(
        state          = uiState,
        cityInput      = cityInput,
        onCityChange   = viewModel::onCityInputChange,
        onSearch       = { viewModel.fetchWeather() },
        onRetry        = { viewModel.fetchWeather() },
        onBack         = onBack
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// STATELESS body
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun WeatherContent(
    state: WeatherUiState,
    cityInput: String,
    onCityChange: (String) -> Unit,
    onSearch: () -> Unit,
    onRetry: () -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = { MealPlannerTopBar(title = "Weather Today", onBack = onBack) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // ── Search bar ───────────────────────────────────────────────
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment     = Alignment.CenterVertically
            ) {
                AppTextField(
                    value         = cityInput,
                    onValueChange = onCityChange,
                    label         = "City",
                    placeholder   = "e.g. Sarajevo, London, Tokyo",
                    modifier      = Modifier.weight(1f)
                )
                IconButton(
                    onClick  = onSearch,
                    modifier = Modifier
                        .size(52.dp)
                        .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(12.dp))
                ) {
                    Icon(Icons.Filled.Search, contentDescription = "Search", tint = Color.White)
                }
            }

            // ── State-driven content ─────────────────────────────────────
            when (state) {
                WeatherUiState.Init       -> Spacer(Modifier.height(32.dp))
                is WeatherUiState.Loading -> CenteredSpinner(label = "Loading weather for ${state.city}…")
                is WeatherUiState.Error   -> ErrorCard(message = state.message, onRetry = onRetry)
                is WeatherUiState.Success -> WeatherSuccessBody(weather = state.weather)
            }
        }
    }
}

@Composable
private fun CenteredSpinner(label: String) {
    Column(
        modifier            = Modifier.fillMaxWidth().padding(top = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        CircularProgressIndicator()
        Text(label, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
    }
}

@Composable
private fun ErrorCard(message: String, onRetry: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("⚠️ $message", color = MaterialTheme.colorScheme.onErrorContainer, fontWeight = FontWeight.SemiBold)
            Text(
                "Check the city name spelling, or the OpenWeather API key may not be active yet (can take 10-30 min after registration).",
                fontSize = 12.sp,
                color    = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.8f)
            )
            PrimaryButton(text = "Retry", onClick = onRetry)
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// SUCCESS body — the main weather card + stats grid + sun times
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun WeatherSuccessBody(weather: Weather) {
    // ── Hero card — city, temperature, icon, condition ─────────────────
    Card(
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier.background(
                Brush.verticalGradient(listOf(Color(0xFF1B5E20), Color(0xFF43A047)))
            )
        ) {
            Column(
                modifier            = Modifier.fillMaxWidth().padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    "${weather.cityName}${if (weather.countryCode.isNotBlank()) ", ${weather.countryCode}" else ""}",
                    fontSize   = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color      = Color.White
                )
                AsyncImage(
                    model = weather.iconUrl,
                    contentDescription = weather.conditionMain,
                    modifier = Modifier.size(140.dp)
                )
                Text(
                    "${weather.temperatureC.toInt()}°C",
                    fontSize   = 56.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color      = Color.White
                )
                Text(weather.description, fontSize = 16.sp, color = Color.White.copy(alpha = 0.9f))
                Text(
                    "Feels like ${weather.feelsLikeC.toInt()}°C  ·  Low ${weather.tempMinC.toInt()}°C  ·  High ${weather.tempMaxC.toInt()}°C",
                    fontSize = 12.sp,
                    color    = Color.White.copy(alpha = 0.8f)
                )
            }
        }
    }

    // ── Stats grid (2 columns × 3 rows) ────────────────────────────────
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        WeatherStatCard("Humidity", "${weather.humidityPercent}%",    Icons.Filled.WaterDrop, Modifier.weight(1f))
        WeatherStatCard("Wind",     "${"%.1f".format(weather.windSpeedMps)} m/s", Icons.Filled.Air, Modifier.weight(1f))
    }
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        WeatherStatCard("Pressure",   "${weather.pressureHpa} hPa", Icons.Filled.Thermostat, Modifier.weight(1f))
        WeatherStatCard("Cloudiness", "${weather.cloudinessPercent}%", Icons.Filled.Cloud, Modifier.weight(1f))
    }
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        WeatherStatCard("Visibility", "${"%.1f".format(weather.visibilityKm)} km", Icons.Filled.Visibility, Modifier.weight(1f))
        WeatherStatCard(
            label    = "Sunrise / Sunset",
            value    = "${formatTime(weather.sunriseUtc, weather.timezoneShiftSeconds)} · ${formatTime(weather.sunsetUtc, weather.timezoneShiftSeconds)}",
            icon     = Icons.Filled.WbSunny,
            modifier = Modifier.weight(1f)
        )
    }

    Spacer(Modifier.height(20.dp))
}

@Composable
private fun WeatherStatCard(
    label: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        shape    = RoundedCornerShape(14.dp),
        modifier = modifier,
        colors   = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier            = Modifier.fillMaxWidth().padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                Text(label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
            }
            Text(value, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

/** OpenWeather sunrise/sunset are UTC seconds; we shift by the city's timezone offset. */
private fun formatTime(unixUtc: Long, tzShiftSeconds: Int): String {
    val fmt = SimpleDateFormat("HH:mm", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }
    return fmt.format(Date((unixUtc + tzShiftSeconds) * 1000L))
}
