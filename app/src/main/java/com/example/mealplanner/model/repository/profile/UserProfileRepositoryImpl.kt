package com.example.mealplanner.model.repository.profile

import com.example.mealplanner.model.UserProfile
import com.example.mealplanner.model.data.local.dao.UserProfileDao
import com.example.mealplanner.model.repository.profile.mapper.toDomain
import com.example.mealplanner.model.repository.profile.mapper.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserProfileRepositoryImpl @Inject constructor(
    private val dao: UserProfileDao
) : UserProfileRepository {
    override fun observe(): Flow<UserProfile?> = dao.observe().map { it?.toDomain() }
    override suspend fun save(profile: UserProfile) = dao.insert(profile.toEntity())
}
