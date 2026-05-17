package com.example.mealplanner.model.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.mealplanner.model.data.local.dao.DayPlanDao
import com.example.mealplanner.model.data.local.dao.IngredientDao
import com.example.mealplanner.model.data.local.dao.MealDao
import com.example.mealplanner.model.data.local.dao.UserAccountDao
import com.example.mealplanner.model.data.local.dao.UserProfileDao
import com.example.mealplanner.model.data.local.entity.*
import com.example.mealplanner.model.data.local.util.DayPlanSeedData
import com.example.mealplanner.model.data.local.util.IngredientSeedData
import com.example.mealplanner.model.data.local.util.MealSeedData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

@Database(
    entities = [
        UserProfileEntity::class,
        UserAccountEntity::class,
        DayPlanEntity::class,
        MealEntity::class,
        DayMealCrossRef::class,
        IngredientEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userProfileDao(): UserProfileDao
    abstract fun userAccountDao(): UserAccountDao
    abstract fun dayPlanDao(): DayPlanDao
    abstract fun mealDao(): MealDao
    abstract fun ingredientDao(): IngredientDao

    /** Seeds all hardcoded data on first-ever database creation. */
    class SeedCallback @Inject constructor(
        private val databaseProvider: Provider<AppDatabase>
    ) : Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            CoroutineScope(Dispatchers.IO).launch {
                val database = databaseProvider.get()
                // Seed pre-made meals
                database.mealDao().insertAll(MealSeedData.meals)
                // Seed 7-day plan (empty, ready for user to populate)
                database.dayPlanDao().insertAll(DayPlanSeedData.dayPlans)
                // Seed ingredients for recipe builder
                database.ingredientDao().insertAll(IngredientSeedData.ingredients)
                // No seed account — user must register through the Sign-Up screen.
                // Profile row is created when the first user signs up or logs in.
            }
        }
    }
}
