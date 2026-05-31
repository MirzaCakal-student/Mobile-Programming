package com.example.mealplanner.model.data.remote.mapper

import com.example.mealplanner.model.Weather
import com.example.mealplanner.model.data.remote.dto.WeatherDto

/**
 * DTO → Domain conversion for the Weather feature.
 *
 * Picks the FIRST element of the weather array (OpenWeather guarantees at least one),
 * builds the icon URL using their CDN convention, and flattens all nested fields
 * into a single flat Weather model.
 */
fun WeatherDto.toDomain(): Weather {
    val condition = weather.firstOrNull()
    return Weather(
        cityName          = cityName,
        countryCode       = sys.countryCode.orEmpty(),
        description       = condition?.description.orEmpty().replaceFirstChar { it.uppercase() },
        conditionMain     = condition?.main.orEmpty(),
        // OpenWeather icon CDN: https://openweathermap.org/weather-conditions
        iconUrl           = condition?.iconCode?.let { "https://openweathermap.org/img/wn/$it@4x.png" }.orEmpty(),
        temperatureC      = main.temperature,
        feelsLikeC        = main.feelsLike,
        tempMinC          = main.tempMin,
        tempMaxC          = main.tempMax,
        humidityPercent   = main.humidityPercent,
        pressureHpa       = main.pressureHpa,
        windSpeedMps      = wind.speedMps,
        windDirectionDeg  = wind.directionDeg,
        cloudinessPercent = clouds?.cloudinessPercent ?: 0,
        visibilityKm      = (visibilityMeters ?: 0) / 1000.0,
        sunriseUtc        = sys.sunriseUtc,
        sunsetUtc         = sys.sunsetUtc,
        timezoneShiftSeconds = timezoneShiftSeconds
    )
}
