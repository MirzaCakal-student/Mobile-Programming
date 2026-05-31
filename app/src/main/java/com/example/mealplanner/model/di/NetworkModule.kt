package com.example.mealplanner.model.di

import com.example.mealplanner.model.data.remote.service.HabitApiService
import com.example.mealplanner.model.data.remote.service.WeatherApiService
import com.example.mealplanner.model.repository.habit.HabitRepository
import com.example.mealplanner.model.repository.habit.HabitRepositoryImpl
import com.example.mealplanner.model.repository.weather.WeatherRepository
import com.example.mealplanner.model.repository.weather.WeatherRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton

/**
 * Lab 11 — Hilt module that builds the entire network stack for BOTH backends:
 *
 *   1) HabitsRetrofit    → http://10.0.2.2:8000/     (our local FastAPI Habits API)
 *   2) OpenWeatherRetrofit → https://api.openweathermap.org/data/2.5/
 *                                                    (public OpenWeather REST API)
 *
 * Two backends share the SAME OkHttpClient (connection pool, timeouts, logging),
 * but each gets its OWN Retrofit instance with its own baseUrl. We disambiguate
 * them with @Qualifier annotations so Hilt knows which one to inject where.
 *
 * BASE_URL notes:
 *   - Android emulator → 10.0.2.2 is the special alias for the host machine's localhost.
 *   - Physical device  → would need the PC's LAN IP for habits, but weather already works.
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    // ── Qualifier annotations — used at injection points to pick the right Retrofit ──
    @Qualifier @Retention(AnnotationRetention.BINARY) annotation class HabitsRetrofit
    @Qualifier @Retention(AnnotationRetention.BINARY) annotation class OpenWeatherRetrofit

    private const val HABITS_BASE_URL  = "http://10.0.2.2:8000/"
    private const val WEATHER_BASE_URL = "https://api.openweathermap.org/data/2.5/"

    // ── Shared infrastructure ────────────────────────────────────────────────

    @Provides @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }

    @Provides @Singleton
    fun provideOkHttpClient(logging: HttpLoggingInterceptor): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()

    // ── Habits backend (local FastAPI) ──────────────────────────────────────

    @Provides @Singleton @HabitsRetrofit
    fun provideHabitsRetrofit(client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(HABITS_BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides @Singleton
    fun provideHabitApiService(@HabitsRetrofit retrofit: Retrofit): HabitApiService =
        retrofit.create(HabitApiService::class.java)

    @Provides @Singleton
    fun provideHabitRepository(impl: HabitRepositoryImpl): HabitRepository = impl

    // ── Weather backend (public OpenWeather API) ────────────────────────────

    @Provides @Singleton @OpenWeatherRetrofit
    fun provideWeatherRetrofit(client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(WEATHER_BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides @Singleton
    fun provideWeatherApiService(@OpenWeatherRetrofit retrofit: Retrofit): WeatherApiService =
        retrofit.create(WeatherApiService::class.java)

    @Provides @Singleton
    fun provideWeatherRepository(impl: WeatherRepositoryImpl): WeatherRepository = impl
}
