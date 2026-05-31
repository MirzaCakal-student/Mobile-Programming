package com.example.mealplanner.model.repository.auth

interface AuthRepository {
    /**
     * Creates a new account.
     * Fails if the email is already registered or the password is too weak.
     */
    suspend fun register(email: String, password: String, name: String): Result<Unit>

    /**
     * Verifies credentials against the backend.
     * Fails if the email is not registered or the password does not match.
     */
    suspend fun login(email: String, password: String): Result<Unit>

    /**
     * Signs the current user out. Clears the cached auth token so
     * SplashScreen will route to Login next time the app starts.
     */
    fun logout()

    /**
     * True if a user is currently signed in (auth token cached on disk).
     * Used by SplashScreen to skip Login for already-authenticated users.
     */
    fun isLoggedIn(): Boolean
}
