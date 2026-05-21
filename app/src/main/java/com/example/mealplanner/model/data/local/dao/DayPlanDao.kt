package com.example.mealplanner.model.data.local.dao

import androidx.room.*
import com.example.mealplanner.model.data.local.entity.DayMealCrossRef
import com.example.mealplanner.model.data.local.entity.DayPlanEntity
import kotlinx.coroutines.flow.Flow

/** Data class for the JOIN query — carries meal info alongside its slot assignment */
data class MealWithSlot(
    val id: Int,
    val name: String,
    val calories: Int,
    @ColumnInfo(name = "protein_g") val proteinG: Double,
    @ColumnInfo(name = "fat_g") val fatG: Double,
    @ColumnInfo(name = "carbs_g") val carbsG: Double,
    @ColumnInfo(name = "is_custom") val isCustom: Boolean,
    @ColumnInfo(name = "slot_type") val slotType: String,
    @ColumnInfo(name = "day_plan_id") val dayPlanId: Int,
    @ColumnInfo(name = "cross_ref_id") val crossRefId: Int
)

@Dao
interface DayPlanDao {
    // ── Day plan CRUD ────────────────────────────────────────────────────────

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(days: List<DayPlanEntity>)

    @Update
    suspend fun update(day: DayPlanEntity)

    @Query("SELECT * FROM day_plans ORDER BY sort_order ASC")
    fun observeAll(): Flow<List<DayPlanEntity>>

    @Query("SELECT * FROM day_plans WHERE day_name = :dayName LIMIT 1")
    suspend fun getByName(dayName: String): DayPlanEntity?

    @Query("SELECT COUNT(*) FROM day_plans")
    suspend fun count(): Int

    // ── Cross-ref (meal assignments) ─────────────────────────────────────────

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCrossRef(crossRef: DayMealCrossRef)

    @Query("DELETE FROM day_meal_cross_ref WHERE day_plan_id = :dayPlanId AND meal_id = :mealId AND slot_type = :slotType")
    suspend fun deleteCrossRef(dayPlanId: Int, mealId: Int, slotType: String)

    /** Reactive JOIN — re-emits whenever meals or cross-refs change. Used to rebuild the full week plan. */
    @Query("""
        SELECT cr.id AS cross_ref_id, m.id, m.name, m.calories, m.protein_g, m.fat_g, m.carbs_g, m.is_custom,
               cr.slot_type, cr.day_plan_id
        FROM day_meal_cross_ref cr
        INNER JOIN meals m ON cr.meal_id = m.id
    """)
    fun observeAllMealsWithSlots(): Flow<List<MealWithSlot>>
}
