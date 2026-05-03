package com.example.mealplanner.model.repository

import com.example.mealplanner.model.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * In-memory repository for the logged-in user's profile.
 *
 * Annotated with @Singleton so Hilt provides one shared instance across the entire app.
 * ProfileViewModel writes here on save; HomeViewModel reads here for the greeting and goal.
 *
 * NOTE: Will be replaced by a Room-backed repository in Assignment 3 Part C/E.
 */
@Singleton
class InMemoryProfileRepository @Inject constructor() {

    private val _profile = MutableStateFlow(
        UserProfile(name = "Mirza", email = "mirza@example.com", dailyCalorieGoal = 2000)
    )
    val profile: StateFlow<UserProfile> = _profile.asStateFlow()

    fun save(profile: UserProfile) {
        _profile.value = profile
    }
}
