package com.example.mealplanner.model.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Stored login credentials. One row per registered user.
 * Email is the primary key — no two users may share the same email.
 * NOTE: password is stored in plain text for the assignment; in production
 * we would hash it (e.g. BCrypt) before persisting.
 */
@Entity(tableName = "user_account")
data class UserAccountEntity(
    @PrimaryKey
    @ColumnInfo(name = "email")    val email: String,
    @ColumnInfo(name = "name")     val name: String,
    @ColumnInfo(name = "password") val password: String,
    @ColumnInfo(name = "created_at") val createdAt: Long = System.currentTimeMillis()
)
