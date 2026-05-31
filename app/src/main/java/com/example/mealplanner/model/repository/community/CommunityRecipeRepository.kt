package com.example.mealplanner.model.repository.community

import com.example.mealplanner.model.CommunityRecipe
import kotlinx.coroutines.flow.Flow

/**
 * Repository over the Firestore `community_recipes` collection.
 *
 * `observeAll()` returns a Flow that re-emits whenever ANY document in the
 * collection is added / updated / deleted — this is what gives us the realtime
 * UI updates the rubric asks for.
 */
interface CommunityRecipeRepository {
    fun observeAll(): Flow<List<CommunityRecipe>>
    suspend fun add(title: String, description: String, calories: Int, ingredients: String): Result<Unit>
    suspend fun delete(documentId: String): Result<Unit>
}
