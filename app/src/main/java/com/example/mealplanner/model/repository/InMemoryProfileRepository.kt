package com.example.mealplanner.model.repository

import com.example.mealplanner.model.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * In-memory repository for the logged-in user's profile.
 *
 * ProfileViewModel writes here when the user saves their profile.
 * HomeViewModel reads here to display the greeting and calorie goal.
 *
 * NOTE: Will be replaced by a Room-backed repository in Assignment 3 Part C.
 */
object InMemoryProfileRepository {

    private val _profile = MutableStateFlow(
        UserProfile(name = "Mirza", email = "mirza@example.com", dailyCalorieGoal = 2000)
    )
    val profile: StateFlow<UserProfile> = _profile.asStateFlow()

    fun save(profile: UserProfile) {
        _profile.value = profile
    }
}
