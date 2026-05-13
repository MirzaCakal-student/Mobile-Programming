package com.example.mealplanner.model.repository.profile

import com.example.mealplanner.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface UserProfileRepository {
    fun observe(): Flow<UserProfile?>
    suspend fun save(profile: UserProfile)
}
