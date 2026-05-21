package com.example.mealplanner.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * App-level DI module.
 * Database and repository bindings have been moved to [com.example.mealplanner.model.di.DatabaseModule].
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule
