package com.example.mealplanner.model.repository.tips

import com.example.mealplanner.model.Tip

/**
 * Repository abstraction over the remote Tips API.
 * The ViewModel depends on this interface, not on Retrofit — keeps the network detail
 * out of the presentation layer and makes testing trivial (swap in a fake).
 */
interface TipsRepository {
    suspend fun fetchAll(): Result<List<Tip>>
    suspend fun fetchOne(id: Int): Result<Tip>
    suspend fun create(title: String, body: String): Result<Tip>
    suspend fun update(id: Int, title: String, body: String): Result<Tip>
    suspend fun delete(id: Int): Result<Unit>
}
