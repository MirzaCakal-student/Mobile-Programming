package com.example.mealplanner.di

import com.example.mealplanner.model.repository.InMemoryMealPlanRepository
import com.example.mealplanner.model.repository.InMemoryProfileRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module installed in the [SingletonComponent] — lives for the entire app lifetime.
 *
 * Both repositories are provided as @Singleton so every ViewModel that injects them
 * receives the exact same instance, preserving the shared-state semantics we had
 * when they were Kotlin object singletons.
 *
 * NOTE: In Assignment 3 Part C/E these providers will be replaced by Room-backed
 * repository implementations without changing the ViewModel injection sites.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideMealPlanRepository(): InMemoryMealPlanRepository = InMemoryMealPlanRepository()

    @Provides
    @Singleton
    fun provideProfileRepository(): InMemoryProfileRepository = InMemoryProfileRepository()
}
