package com.example.mealplanner.model

/**
 * Domain model for the "Weather Today" feature.
 *
 * Built from WeatherDto via WeatherMapper. The ViewModel and UI work only with
 * this clean type — they never see OpenWeather's nested DTOs or @SerializedName.
 *
 * `iconUrl` is built in the mapper so the UI just passes it straight to Coil
 * without knowing the openweathermap.org CDN convention.
 */
data class Weather(
    val cityName: String,
    val countryCode: String,
    val description: String,         // "scattered clouds"
    val conditionMain: String,       // "Clouds", "Rain"...
    val iconUrl: String,             // e.g. https://openweathermap.org/img/wn/04d@2x.png
    val temperatureC: Double,
    val feelsLikeC: Double,
    val tempMinC: Double,
    val tempMaxC: Double,
    val humidityPercent: Int,
    val pressureHpa: Int,
    val windSpeedMps: Double,
    val windDirectionDeg: Int,
    val cloudinessPercent: Int,
    val visibilityKm: Double,
    val sunriseUtc: Long,
    val sunsetUtc: Long,
    val timezoneShiftSeconds: Int
)
