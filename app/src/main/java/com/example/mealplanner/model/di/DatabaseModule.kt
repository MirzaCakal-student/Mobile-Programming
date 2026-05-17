package com.example.mealplanner.model.di

import android.content.Context
import androidx.room.Room
import com.example.mealplanner.model.data.local.dao.DayPlanDao
import com.example.mealplanner.model.data.local.dao.IngredientDao
import com.example.mealplanner.model.data.local.dao.MealDao
import com.example.mealplanner.model.data.local.dao.UserAccountDao
import com.example.mealplanner.model.data.local.dao.UserProfileDao
import com.example.mealplanner.model.data.local.db.AppDatabase
import com.example.mealplanner.model.repository.auth.AuthRepository
import com.example.mealplanner.model.repository.auth.AuthRepositoryImpl
import com.example.mealplanner.model.repository.dayplan.DayPlanRepository
import com.example.mealplanner.model.repository.dayplan.DayPlanRepositoryImpl
import com.example.mealplanner.model.repository.ingredient.IngredientRepository
import com.example.mealplanner.model.repository.ingredient.IngredientRepositoryImpl
import com.example.mealplanner.model.repository.meal.MealRepository
import com.example.mealplanner.model.repository.meal.MealRepositoryImpl
import com.example.mealplanner.model.repository.profile.UserProfileRepository
import com.example.mealplanner.model.repository.profile.UserProfileRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
        seedCallback: AppDatabase.SeedCallback
    ): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "mealplanner_database")
            .addCallback(seedCallback)
            .fallbackToDestructiveMigration(true) // dev only — data is seeded on create
            .build()
    }

    @Provides @Singleton
    fun provideUserProfileDao(db: AppDatabase): UserProfileDao = db.userProfileDao()

    @Provides @Singleton
    fun provideUserAccountDao(db: AppDatabase): UserAccountDao = db.userAccountDao()

    @Provides @Singleton
    fun provideDayPlanDao(db: AppDatabase): DayPlanDao = db.dayPlanDao()

    @Provides @Singleton
    fun provideMealDao(db: AppDatabase): MealDao = db.mealDao()

    @Provides @Singleton
    fun provideIngredientDao(db: AppDatabase): IngredientDao = db.ingredientDao()

    @Provides @Singleton
    fun provideDayPlanRepository(impl: DayPlanRepositoryImpl): DayPlanRepository = impl

    @Provides @Singleton
    fun provideMealRepository(impl: MealRepositoryImpl): MealRepository = impl

    @Provides @Singleton
    fun provideUserProfileRepository(impl: UserProfileRepositoryImpl): UserProfileRepository = impl

    @Provides @Singleton
    fun provideIngredientRepository(impl: IngredientRepositoryImpl): IngredientRepository = impl

    @Provides @Singleton
    fun provideAuthRepository(impl: AuthRepositoryImpl): AuthRepository = impl
}
