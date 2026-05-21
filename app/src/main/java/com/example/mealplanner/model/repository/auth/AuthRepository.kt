package com.example.mealplanner.model.repository.auth

interface AuthRepository {
    /**
     * Creates a new account.
     * Fails if an account with this email already exists.
     */
    suspend fun register(email: String, password: String, name: String): Result<Unit>

    /**
     * Verifies credentials against the database.
     * Fails if email is not registered or password does not match.
     */
    suspend fun login(email: String, password: String): Result<Unit>
}
