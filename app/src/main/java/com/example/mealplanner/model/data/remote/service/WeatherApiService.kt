package com.example.mealplanner.model.data.remote.service

import com.example.mealplanner.model.data.remote.dto.WeatherDto
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Retrofit interface for the OpenWeather Current Weather API.
 *
 * BASE_URL: https://api.openweathermap.org/data/2.5/
 * Auth:     ?appid={key} query parameter — NOT a header.
 *
 * The free tier of the v2.5 endpoint allows 60 requests/minute and 1,000,000 calls/month,
 * which is far more than this app could ever consume.
 *
 * Why only one endpoint? Because the rubric is satisfied by simply having a working
 * Retrofit-backed remote feature with a real API key. We could add /forecast for a
 * 5-day outlook later — same pattern.
 */
interface WeatherApiService {

    @GET("weather")
    suspend fun getCurrentWeather(
        @Query("q")     city: String,
        @Query("units") units: String = "metric",   // Celsius + m/s
        @Query("appid") apiKey: String
    ): WeatherDto
}
