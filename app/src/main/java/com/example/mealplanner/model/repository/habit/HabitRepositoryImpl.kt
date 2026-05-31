package com.example.mealplanner.model.repository.habit

import com.example.mealplanner.model.data.remote.dto.CreateHabitDto
import com.example.mealplanner.model.data.remote.dto.HabitDto
import com.example.mealplanner.model.data.remote.dto.UpdateHabitDto
import com.example.mealplanner.model.data.remote.service.HabitApiService
import javax.inject.Inject

/**
 * Concrete repo — thin pass-through to Retrofit.
 * Each method forwards to the matching service call exactly as Lab 11 specifies:
 *   getHabits()        → api.getHabits()
 *   getHabitById(id)   → api.getHabitById(id)
 *   createHabit(habit) → api.createHabit(habit)
 *   updateHabit(...)   → api.updateHabit(...)
 *   deleteHabit(id)    → api.deleteHabit(id)
 *
 * Network exceptions (IOException, HttpException) bubble up and are caught
 * one level higher in the ViewModel so it can surface Loading/Success/Error.
 */
class HabitRepositoryImpl @Inject constructor(
    private val api: HabitApiService
) : HabitRepository {

    override suspend fun getHabits(): List<HabitDto> =
        api.getHabits()

    override suspend fun getHabitById(id: Int): HabitDto =
        api.getHabitById(id)

    override suspend fun createHabit(habit: CreateHabitDto): HabitDto =
        api.createHabit(habit)

    override suspend fun updateHabit(id: Int, habit: UpdateHabitDto): HabitDto =
        api.updateHabit(id, habit)

    override suspend fun deleteHabit(id: Int) {
        api.deleteHabit(id)
    }
}
