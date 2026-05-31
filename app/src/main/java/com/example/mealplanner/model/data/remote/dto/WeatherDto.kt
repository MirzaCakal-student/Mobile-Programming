package com.example.mealplanner.model.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * ── OpenWeather Current Weather API response ───────────────────────────────
 * Endpoint: https://api.openweathermap.org/data/2.5/weather?q={city}&units=metric&appid={KEY}
 *
 * The full response has ~30 fields. We only model the ones we need for the
 * "Weather Today" card; Gson silently ignores any other JSON fields.
 *
 * Note the nested DTOs: OpenWeather wraps related fields in sub-objects
 * (main, wind, sys, weather), so our Kotlin shape mirrors that structure.
 */
data class WeatherDto(
    @SerializedName("name")       val cityName: String,
    @SerializedName("main")       val main: MainDto,
    @SerializedName("weather")    val weather: List<WeatherConditionDto>,
    @SerializedName("wind")       val wind: WindDto,
    @SerializedName("clouds")     val clouds: CloudsDto?,
    @SerializedName("visibility") val visibilityMeters: Int?,
    @SerializedName("sys")        val sys: SysDto,
    @SerializedName("dt")         val timestampUtc: Long,
    @SerializedName("timezone")   val timezoneShiftSeconds: Int
)

/** Temperature, feels-like, humidity, pressure. */
data class MainDto(
    @SerializedName("temp")       val temperature: Double,
    @SerializedName("feels_like") val feelsLike: Double,
    @SerializedName("temp_min")   val tempMin: Double,
    @SerializedName("temp_max")   val tempMax: Double,
    @SerializedName("pressure")   val pressureHpa: Int,
    @SerializedName("humidity")   val humidityPercent: Int
)

/**
 * Weather conditions are returned as an array because some moments have several
 * (e.g. Rain + Mist). We always read the first element for the headline.
 */
data class WeatherConditionDto(
    @SerializedName("id")          val id: Int,
    @SerializedName("main")        val main: String,           // "Clear", "Rain", "Clouds"...
    @SerializedName("description") val description: String,    // "scattered clouds"
    @SerializedName("icon")        val iconCode: String        // e.g. "01d" → https://openweathermap.org/img/wn/01d@2x.png
)

data class WindDto(
    @SerializedName("speed") val speedMps: Double,
    @SerializedName("deg")   val directionDeg: Int
)

data class CloudsDto(
    @SerializedName("all") val cloudinessPercent: Int
)

data class SysDto(
    @SerializedName("country") val countryCode: String?,
    @SerializedName("sunrise") val sunriseUtc: Long,
    @SerializedName("sunset")  val sunsetUtc: Long
)
