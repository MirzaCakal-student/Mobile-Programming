package com.example.mealplanner.model.repository.auth

import com.example.mealplanner.model.data.local.dao.UserAccountDao
import com.example.mealplanner.model.data.local.dao.UserProfileDao
import com.example.mealplanner.model.data.local.entity.UserAccountEntity
import com.example.mealplanner.model.data.local.entity.UserProfileEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val accountDao: UserAccountDao,
    private val profileDao: UserProfileDao
) : AuthRepository {

    override suspend fun register(
        email: String,
        password: String,
        name: String
    ): Result<Unit> = runCatching {
        val normalizedEmail = email.trim().lowercase()

        // Reject duplicate accounts
        if (accountDao.findByEmail(normalizedEmail) != null) {
            error("An account with this email already exists.")
        }

        // Persist credentials
        accountDao.insert(
            UserAccountEntity(
                email    = normalizedEmail,
                name     = name.trim(),
                password = password
            )
        )

        // Bootstrap (or refresh) the user profile row used by ProfileScreen.
        profileDao.insert(
            UserProfileEntity(
                id    = 1,
                name  = name.trim(),
                email = normalizedEmail
            )
        )
    }

    override suspend fun login(
        email: String,
        password: String
    ): Result<Unit> = runCatching {
        val normalizedEmail = email.trim().lowercase()

        val account = accountDao.findByEmail(normalizedEmail)
            ?: error("No account found for this email.")

        if (account.password != password) {
            error("Incorrect password.")
        }

        // Make sure the profile row reflects the currently logged-in user.
        // If the same user logs back in, we keep their profile metrics intact;
        // if a DIFFERENT user logs in, we wipe the previous user's metrics so
        // names/weights/etc. don't leak across accounts.
        val current      = profileDao.get()
        val isSameUser   = current?.email == account.email
        profileDao.insert(
            UserProfileEntity(
                id               = 1,
                name             = if (isSameUser && !current?.name.isNullOrBlank()) current!!.name else account.name,
                email            = account.email,
                weightKg         = if (isSameUser) current?.weightKg         ?: 0.0  else 0.0,
                heightCm         = if (isSameUser) current?.heightCm         ?: 0.0  else 0.0,
                ageYears         = if (isSameUser) current?.ageYears         ?: 0    else 0,
                dailyCalorieGoal = if (isSameUser) current?.dailyCalorieGoal ?: 2000 else 2000,
                gender           = if (isSameUser) current?.gender           ?: "Male" else "Male"
            )
        )
    }

    // ── LEGACY no-ops ─────────────────────────────────────────────────────────
    // Since Assignment 4 / Lab 12, the active implementation is FirebaseAuthRepositoryImpl.
    // These methods exist only so this legacy class still satisfies the AuthRepository contract.
    // The DI graph no longer binds to this implementation — see DatabaseModule.provideAuthRepository.
    override fun logout()           = Unit
    override fun isLoggedIn(): Boolean = false
}
