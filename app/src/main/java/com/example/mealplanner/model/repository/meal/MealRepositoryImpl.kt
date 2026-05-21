package com.example.mealplanner.model.repository.meal

import com.example.mealplanner.model.Meal
import com.example.mealplanner.model.data.local.dao.MealDao
import com.example.mealplanner.model.repository.meal.mapper.toDomain
import com.example.mealplanner.model.repository.meal.mapper.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MealRepositoryImpl @Inject constructor(
    private val mealDao: MealDao
) : MealRepository {
    override suspend fun getAllPremadeMeals(): List<Meal> = mealDao.getPremade().map { it.toDomain() }
    override suspend fun search(query: String): List<Meal> = mealDao.search(query).map { it.toDomain() }
    override suspend fun addMeal(meal: Meal): Long = mealDao.insert(meal.toEntity())
    override suspend fun deleteMeal(meal: Meal) = mealDao.delete(meal.toEntity())
    override fun observeCustomMeals(): Flow<List<Meal>> = mealDao.observeCustom().map { list -> list.map { it.toDomain() } }
}
