package com.example.mealplanner.model.data.remote.api

import com.example.mealplanner.model.data.remote.dto.RemoteTipDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

/**
 * Retrofit service interface — declarative HTTP client.
 *
 * Retrofit generates an implementation at runtime from these annotations.
 * Each `suspend` function runs on a background dispatcher and returns the parsed body.
 *
 * Covers all four required REST verbs:
 *   - GET    /posts            → list all tips
 *   - GET    /posts/{id}       → single tip
 *   - POST   /posts            → create a new tip
 *   - PUT    /posts/{id}       → replace an existing tip
 *   - DELETE /posts/{id}       → remove a tip
 */
interface TipsApiService {

    @GET("posts")
    suspend fun getAllTips(): List<RemoteTipDto>

    @GET("posts/{id}")
    suspend fun getTipById(@Path("id") id: Int): RemoteTipDto

    @POST("posts")
    suspend fun createTip(@Body tip: RemoteTipDto): RemoteTipDto

    @PUT("posts/{id}")
    suspend fun updateTip(@Path("id") id: Int, @Body tip: RemoteTipDto): RemoteTipDto

    @DELETE("posts/{id}")
    suspend fun deleteTip(@Path("id") id: Int)
}
