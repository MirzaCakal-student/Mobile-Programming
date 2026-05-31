package com.example.mealplanner.model.repository.auth

import com.example.mealplanner.model.data.local.dao.UserProfileDao
import com.example.mealplanner.model.data.local.entity.UserProfileEntity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Firebase-backed implementation of [AuthRepository].
 *
 * Replaces the Room-only AuthRepositoryImpl. The interface stays the same, so
 * LoginViewModel / SignUpViewModel are not affected — that's the entire point
 * of the Repository pattern: swap the data source, keep the contract.
 *
 * Why FirebaseAuth gives us "persistent login" for free:
 *   FirebaseAuth caches the signed-in user's token on disk. After process death
 *   or device reboot, `auth.currentUser` is populated automatically as soon as
 *   the SDK initialises. SplashScreen uses this to skip the Login screen for
 *   already-authenticated users.
 *
 * Local profile row: we still need the Room `user_profiles` table because
 * biometrics (weight, height, calorie goal) are local-only — they're not on the
 * server. So on every successful sign-in we ensure exactly one row exists,
 * keyed to the logged-in user's email. Different user logs in → reset metrics.
 */
@Singleton
class FirebaseAuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val profileDao: UserProfileDao
) : AuthRepository {

    override suspend fun register(
        email: String,
        password: String,
        name: String
    ): Result<Unit> = runCatching {
        val normalizedEmail = email.trim().lowercase()
        val trimmedName     = name.trim()

        // 1. Firebase creates the account. Throws if e-mail is taken or password is too weak.
        val result = auth.createUserWithEmailAndPassword(normalizedEmail, password).await()
        val firebaseUser = result.user ?: error("Firebase returned no user after sign-up.")

        // 2. Attach the user's display name to their Firebase profile.
        //    Lets us read it later via auth.currentUser?.displayName.
        firebaseUser.updateProfile(
            UserProfileChangeRequest.Builder().setDisplayName(trimmedName).build()
        ).await()

        // 3. Bootstrap the local Room profile row used by ProfileScreen.
        profileDao.insert(
            UserProfileEntity(id = 1, name = trimmedName, email = normalizedEmail)
        )
    }

    override suspend fun login(
        email: String,
        password: String
    ): Result<Unit> = runCatching {
        val normalizedEmail = email.trim().lowercase()

        // 1. Firebase verifies the credentials. Throws if no such account or wrong password.
        val result = auth.signInWithEmailAndPassword(normalizedEmail, password).await()
        val firebaseUser = result.user ?: error("Firebase returned no user after sign-in.")

        // 2. Refresh the local profile row. If the SAME user logs in, keep biometrics;
        //    if a different user, reset to defaults so values don't leak across accounts.
        val current      = profileDao.get()
        val isSameUser   = current?.email == normalizedEmail
        val displayName  = firebaseUser.displayName?.takeIf { it.isNotBlank() }
            ?: current?.name?.takeIf { it.isNotBlank() }
            ?: normalizedEmail.substringBefore('@')

        profileDao.insert(
            UserProfileEntity(
                id               = 1,
                name             = displayName,
                email            = normalizedEmail,
                weightKg         = if (isSameUser) current?.weightKg         ?: 0.0  else 0.0,
                heightCm         = if (isSameUser) current?.heightCm         ?: 0.0  else 0.0,
                ageYears         = if (isSameUser) current?.ageYears         ?: 0    else 0,
                dailyCalorieGoal = if (isSameUser) current?.dailyCalorieGoal ?: 2000 else 2000,
                gender           = if (isSameUser) current?.gender           ?: "Male" else "Male"
            )
        )
    }

    override fun logout() {
        // Drops the cached auth token. The local user_profile row is left in place
        // so the next login can preserve biometrics for the SAME user.
        auth.signOut()
    }

    override fun isLoggedIn(): Boolean = auth.currentUser != null
}
