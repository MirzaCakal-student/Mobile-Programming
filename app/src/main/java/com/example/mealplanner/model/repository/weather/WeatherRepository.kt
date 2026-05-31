package com.example.mealplanner.model.repository.weather

import com.example.mealplanner.model.Weather

/**
 * Repository abstraction over the OpenWeather REST API.
 * The ViewModel depends only on this interface — never on Retrofit directly.
 */
interface WeatherRepository {
    /**
     * Fetch the current weather for a city.
     * Returns the mapped domain model; throws on network / API error
     * (the ViewModel wraps the call in try/catch and maps to UiState.Error).
     */
    suspend fun getCurrentWeather(city: String): Weather
}
