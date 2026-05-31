package com.example.mealplanner.model.repository.weather

import com.example.mealplanner.BuildConfig
import com.example.mealplanner.model.Weather
import com.example.mealplanner.model.data.remote.mapper.toDomain
import com.example.mealplanner.model.data.remote.service.WeatherApiService
import javax.inject.Inject

/**
 * Implementation backed by Retrofit. Pulls the API key from BuildConfig,
 * which Gradle populates from local.properties (gitignored).
 *
 * The DTO never leaves this layer — we call `.toDomain()` so the ViewModel
 * receives the clean Weather model.
 */
class WeatherRepositoryImpl @Inject constructor(
    private val api: WeatherApiService
) : WeatherRepository {

    override suspend fun getCurrentWeather(city: String): Weather {
        val dto = api.getCurrentWeather(city = city, apiKey = BuildConfig.OPENWEATHER_KEY)
        return dto.toDomain()
    }
}
