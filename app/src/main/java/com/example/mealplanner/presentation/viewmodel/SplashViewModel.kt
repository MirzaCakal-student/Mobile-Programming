package com.example.mealplanner.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.example.mealplanner.model.repository.auth.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Tiny ViewModel exposed to SplashScreen so it can decide where to navigate next:
 *   - Already logged in → MAIN_GRAPH
 *   - Not logged in     → LOGIN
 *
 * "Persistent login" comes for free from FirebaseAuth: the SDK caches the
 * user's auth token on disk, so `authRepository.isLoggedIn()` returns true
 * even after process death or device reboot.
 */
@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    val isLoggedIn: Boolean get() = authRepository.isLoggedIn()
}
