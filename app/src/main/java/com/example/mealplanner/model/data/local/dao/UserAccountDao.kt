package com.example.mealplanner.model.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mealplanner.model.data.local.entity.UserAccountEntity

@Dao
interface UserAccountDao {

    /** Inserts a new account. Throws if email already exists (PK conflict). */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(account: UserAccountEntity)

    /** Returns the account for the given email, or null if no such account exists. */
    @Query("SELECT * FROM user_account WHERE email = :email LIMIT 1")
    suspend fun findByEmail(email: String): UserAccountEntity?

    /** Useful for debugging — shows every registered user. */
    @Query("SELECT * FROM user_account ORDER BY created_at DESC")
    suspend fun getAll(): List<UserAccountEntity>

    @Query("SELECT COUNT(*) FROM user_account")
    suspend fun count(): Int
}
