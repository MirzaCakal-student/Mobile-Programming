package com.example.mealplanner.model.repository.tips

import com.example.mealplanner.model.Tip
import com.example.mealplanner.model.data.remote.api.TipsApiService
import com.example.mealplanner.model.repository.tips.mapper.toDomain
import com.example.mealplanner.model.repository.tips.mapper.toDto
import javax.inject.Inject

/**
 * Concrete repo — wraps the Retrofit service and maps DTOs to domain models.
 * Every call is funneled through `runCatching` so the ViewModel just inspects a Result
 * instead of try/catching low-level IO exceptions.
 */
class TipsRepositoryImpl @Inject constructor(
    private val api: TipsApiService
) : TipsRepository {

    override suspend fun fetchAll(): Result<List<Tip>> = runCatching {
        api.getAllTips().map { it.toDomain() }
    }

    override suspend fun fetchOne(id: Int): Result<Tip> = runCatching {
        api.getTipById(id).toDomain()
    }

    override suspend fun create(title: String, body: String): Result<Tip> = runCatching {
        val draft = Tip(id = 0, title = title, body = body).toDto()
        api.createTip(draft).toDomain()
    }

    override suspend fun update(id: Int, title: String, body: String): Result<Tip> = runCatching {
        val edit = Tip(id = id, title = title, body = body).toDto()
        api.updateTip(id, edit).toDomain()
    }

    override suspend fun delete(id: Int): Result<Unit> = runCatching {
        api.deleteTip(id)
    }
}
