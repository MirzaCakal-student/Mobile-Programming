package com.example.mealplanner.model.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * ── Lab 11: Three-DTO pattern ───────────────────────────────────────────────
 * Backend: https://github.com/mobileprogramming2007/lab11-api.git
 * Endpoint: /habits/ (FastAPI server running on http://10.0.2.2:8000/)
 *
 * Schema verified against the live Swagger UI at http://localhost:8000/docs.
 * The README lagged behind the actual code — Swagger is the source of truth.
 *
 * Each habit has 6 fields: id, title, description, frequency, completed, user_id.
 * `user_id` is snake_case on the wire — mapped to `userId` in Kotlin via @SerializedName.
 */

/** Response from GET /habits/ and GET /habits/{id}. `id` is server-assigned. */
data class HabitDto(
    @SerializedName("id")          val id: Int,
    @SerializedName("title")       val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("frequency")   val frequency: String,
    @SerializedName("completed")   val completed: Boolean,
    @SerializedName("user_id")     val userId: Int
)

/** Request body for POST /habits/ — no `id` because the server generates it. */
data class CreateHabitDto(
    @SerializedName("title")       val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("frequency")   val frequency: String,
    @SerializedName("completed")   val completed: Boolean = false,
    @SerializedName("user_id")     val userId: Int = 1
)

/**
 * Request body for PUT /habits/{id} — every field is nullable so the client can
 * send a partial update (only the fields it wants to change).
 * Gson drops null fields from the JSON body by default.
 */
data class UpdateHabitDto(
    @SerializedName("title")       val title: String?       = null,
    @SerializedName("description") val description: String? = null,
    @SerializedName("frequency")   val frequency: String?   = null,
    @SerializedName("completed")   val completed: Boolean?  = null,
    @SerializedName("user_id")     val userId: Int?         = null
)
