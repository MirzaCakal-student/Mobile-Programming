package com.example.mealplanner.model.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * DTO — Data Transfer Object that mirrors the JSON shape returned by the REST API.
 * Kept separate from the domain model so the API can evolve without breaking the UI layer.
 *
 * Endpoint: https://jsonplaceholder.typicode.com/posts
 * The free JSONPlaceholder API treats each "post" as a remote record we read/write.
 * We reuse its schema here as a "Tip" (a cooking/nutrition tip a user can fetch from the cloud).
 */
data class RemoteTipDto(
    @SerializedName("id")     val id: Int? = null,     // null when creating a new tip (server assigns)
    @SerializedName("userId") val userId: Int,
    @SerializedName("title")  val title: String,
    @SerializedName("body")   val body: String
)
