package com.example.mealplanner.model

/**
 * Domain model for a recipe shared by a user to the community.
 *
 * Stored in Firestore under the `community_recipes` collection. Realtime
 * snapshots from Firestore are mapped to this clean type before the UI ever
 * sees them — same Repository / mapper pattern as the rest of the app.
 *
 * `documentId` is Firestore's auto-generated ID for the document. We need it
 * to delete or update the specific recipe.
 *
 * `timestampMillis` is the createdAt time as Unix epoch — Firestore stores it
 * as a Timestamp and we flatten it to a Long in the mapper.
 */
data class CommunityRecipe(
    val documentId: String,
    val title: String,
    val description: String,
    val calories: Int,
    val ingredients: String,        // free-form comma-separated list
    val authorEmail: String,
    val authorName: String,
    val timestampMillis: Long
)
