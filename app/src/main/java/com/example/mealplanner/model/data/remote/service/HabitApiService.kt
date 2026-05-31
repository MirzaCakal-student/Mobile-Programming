package com.example.mealplanner.model.data.remote.service

import com.example.mealplanner.model.data.remote.dto.CreateHabitDto
import com.example.mealplanner.model.data.remote.dto.HabitDto
import com.example.mealplanner.model.data.remote.dto.UpdateHabitDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

/**
 * Retrofit service interface — Lab 11 Habits REST API.
 * Backend: https://github.com/mobileprogramming2007/lab11-api.git (port 8000).
 *
 * Every endpoint:
 *   - is a `suspend` function (runs on Retrofit's background dispatcher)
 *   - sends `X-Authentication: yes` as required by the lab backend
 *   - uses the three-class DTO pattern: HabitDto for responses,
 *     CreateHabitDto for POST body, UpdateHabitDto for PUT body.
 *
 * Covers all four HTTP verbs the assignment requires.
 */
interface HabitApiService {

    /** GET /habits/ — list all habits. */
    @GET("habits/")
    suspend fun getHabits(
        @Header("X-Authentication") authHeader: String = "yes"
    ): List<HabitDto>

    /** GET /habits/{id} — fetch a single habit. */
    @GET("habits/{id}")
    suspend fun getHabitById(
        @Path("id") id: Int,
        @Header("X-Authentication") authHeader: String = "yes"
    ): HabitDto

    /** POST /habits/ — create a new habit. */
    @POST("habits/")
    suspend fun createHabit(
        @Body habit: CreateHabitDto,
        @Header("X-Authentication") authHeader: String = "yes"
    ): HabitDto

    /** PUT /habits/{id} — partial update; only non-null fields are sent. */
    @PUT("habits/{id}")
    suspend fun updateHabit(
        @Path("id") id: Int,
        @Body habit: UpdateHabitDto,
        @Header("X-Authentication") authHeader: String = "yes"
    ): HabitDto

    /** DELETE /habits/{id} — remove a habit. */
    @DELETE("habits/{id}")
    suspend fun deleteHabit(
        @Path("id") id: Int,
        @Header("X-Authentication") authHeader: String = "yes"
    )
}
