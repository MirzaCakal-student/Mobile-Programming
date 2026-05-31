package com.example.mealplanner.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mealplanner.model.Weather
import com.example.mealplanner.model.repository.weather.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Sealed UI state — same Init/Loading/Success/Error pattern used everywhere else.
 * `searchCity` is kept on Success/Error so the user does not lose what they typed
 * when an error happens.
 */
sealed interface WeatherUiState {
    data object Init    : WeatherUiState
    data class  Loading(val city: String) : WeatherUiState
    data class  Success(val weather: Weather, val city: String) : WeatherUiState
    data class  Error(val message: String, val city: String) : WeatherUiState
}

/**
 * Bridges the WeatherRepository to the UI.
 *
 * Default city is Sarajevo — the user can type a different one in the
 * search bar and tap Search to refresh.
 */
@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<WeatherUiState>(WeatherUiState.Init)
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    // What the user has typed into the search field.
    private val _cityInput = MutableStateFlow("Sarajevo")
    val cityInput: StateFlow<String> = _cityInput.asStateFlow()

    init { fetchWeather(DEFAULT_CITY) }

    fun onCityInputChange(value: String) { _cityInput.value = value }

    /** User tapped Search or the screen first opened — kick off a network call. */
    fun fetchWeather(city: String = _cityInput.value) {
        val trimmed = city.trim().ifBlank { DEFAULT_CITY }
        viewModelScope.launch {
            _uiState.value = WeatherUiState.Loading(trimmed)
            try {
                val weather = weatherRepository.getCurrentWeather(trimmed)
                _uiState.value = WeatherUiState.Success(weather, trimmed)
            } catch (e: CancellationException) {
                throw e // never swallow — keeps coroutine cancellation working
            } catch (e: Exception) {
                _uiState.value = WeatherUiState.Error(
                    message = e.message ?: "Failed to load weather",
                    city    = trimmed
                )
            }
        }
    }

    companion object {
        private const val DEFAULT_CITY = "Sarajevo"
    }
}
