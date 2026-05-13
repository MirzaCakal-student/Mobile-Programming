package com.example.mealplanner.model.repository.dayplan

import com.example.mealplanner.model.DayPlan
import com.example.mealplanner.model.Meal
import com.example.mealplanner.model.data.local.dao.DayPlanDao
import com.example.mealplanner.model.data.local.dao.MealDao
import com.example.mealplanner.model.data.local.entity.DayMealCrossRef as CrossRef
import com.example.mealplanner.model.repository.dayplan.mapper.toDomain
import com.example.mealplanner.model.repository.dayplan.mapper.toMeal
import com.example.mealplanner.model.repository.meal.mapper.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class DayPlanRepositoryImpl @Inject constructor(
    private val dayPlanDao: DayPlanDao,
    private val mealDao: MealDao
) : DayPlanRepository {

    override fun observeWeekPlan(): Flow<Map<String, DayPlan>> {
        return combine(
            dayPlanDao.observeAll(),
            dayPlanDao.observeAllMealsWithSlots()
        ) { dayEntities, mealsWithSlots ->
            val slotsByDay = mealsWithSlots.groupBy { it.dayPlanId }
            dayEntities.associate { entity ->
                val slots = slotsByDay[entity.id] ?: emptyList()
                val breakfast = slots.filter { it.slotType == "Breakfast" }.map { it.toMeal() }
                val lunch     = slots.filter { it.slotType == "Lunch"     }.map { it.toMeal() }
                val dinner    = slots.filter { it.slotType == "Dinner"    }.map { it.toMeal() }
                val snacks    = slots.filter { it.slotType == "Snacks"    }.map { it.toMeal() }
                entity.dayName to entity.toDomain(breakfast, lunch, dinner, snacks)
            }
        }
    }

    override suspend fun addMealToSlot(dayName: String, slotName: String, meal: Meal) {
        val dayEntity = dayPlanDao.getByName(dayName) ?: return
        // Insert the meal first if it's custom (get the DB id back)
        val mealDbId: Int = if (meal.isCustom) {
            mealDao.insert(meal.toEntity()).toInt()
        } else {
            // Pre-made meals are already in DB — find by name
            mealDao.getPremade().firstOrNull { it.name == meal.name }?.id ?: return
        }
        dayPlanDao.insertCrossRef(
            CrossRef(dayPlanId = dayEntity.id, mealId = mealDbId, slotType = slotName)
        )
    }

    override suspend fun removeMealFromSlot(dayName: String, slotName: String, meal: Meal) {
        val dayEntity = dayPlanDao.getByName(dayName) ?: return
        // Find the actual meal DB id via name match (premade IDs may differ from domain IDs)
        val mealDbId = mealDao.getAll().firstOrNull { it.name == meal.name && it.isCustom == meal.isCustom }?.id ?: return
        dayPlanDao.deleteCrossRef(dayPlanId = dayEntity.id, mealId = mealDbId, slotType = slotName)
    }

    override suspend fun markDayComplete(dayName: String) {
        val entity = dayPlanDao.getByName(dayName) ?: return
        dayPlanDao.update(entity.copy(isComplete = true))
    }

    override suspend fun getNextCustomMealId(): Int {
        // Room auto-generates IDs — this is just a placeholder that the ViewModel no longer needs
        return 0
    }
}
