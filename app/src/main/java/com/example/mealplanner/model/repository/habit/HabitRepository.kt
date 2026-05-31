package com.example.mealplanner.model.repository.habit

import com.example.mealplanner.model.data.remote.dto.CreateHabitDto
import com.example.mealplanner.model.data.remote.dto.HabitDto
import com.example.mealplanner.model.data.remote.dto.UpdateHabitDto

/**
 * Repository abstraction — same role as the Room repositories in Lab 10.
 * The ViewModel depends on this interface, not on Retrofit, so the data source
 * could later switch (e.g. to Firebase) without touching the presentation layer.
 *
 * Lab 11 contract: bare DTO-in / DTO-out functions, no Result wrapping at this layer.
 */
interface HabitRepository {
    suspend fun getHabits(): List<HabitDto>
    suspend fun getHabitById(id: Int): HabitDto
    suspend fun createHabit(habit: CreateHabitDto): HabitDto
    suspend fun updateHabit(id: Int, habit: UpdateHabitDto): HabitDto
    suspend fun deleteHabit(id: Int)
}
