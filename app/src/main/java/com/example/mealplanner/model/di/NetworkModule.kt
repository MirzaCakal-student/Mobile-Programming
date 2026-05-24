package com.example.mealplanner.model.di

import com.example.mealplanner.model.data.remote.api.TipsApiService
import com.example.mealplanner.model.repository.tips.TipsRepository
import com.example.mealplanner.model.repository.tips.TipsRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * Hilt module that builds the network stack and provides it app-wide as singletons.
 *
 *   OkHttpClient ─┐
 *                 ├─► Retrofit ─► TipsApiService ─► TipsRepository ─► ViewModel
 *   Gson         ─┘
 *
 * All graph nodes are @Singleton — one shared client, one shared Retrofit instance,
 * one service. This avoids the cost of re-creating connection pools on each request.
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val BASE_URL = "https://jsonplaceholder.typicode.com/"

    /** Logs every request/response line in Logcat — invaluable for debugging during dev. */
    @Provides @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }

    /** Underlying HTTP engine. Connect/read timeouts keep the UI from hanging on flaky networks. */
    @Provides @Singleton
    fun provideOkHttpClient(logging: HttpLoggingInterceptor): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()

    /** Retrofit binds the HTTP client + JSON converter + base URL. */
    @Provides @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    /** Service interface — Retrofit synthesizes the implementation at runtime. */
    @Provides @Singleton
    fun provideTipsApiService(retrofit: Retrofit): TipsApiService =
        retrofit.create(TipsApiService::class.java)

    /** Bind the repository interface to its concrete implementation. */
    @Provides @Singleton
    fun provideTipsRepository(impl: TipsRepositoryImpl): TipsRepository = impl
}
