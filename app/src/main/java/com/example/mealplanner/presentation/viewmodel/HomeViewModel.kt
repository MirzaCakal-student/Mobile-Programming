package com.example.mealplanner.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mealplanner.model.DayPlan
import com.example.mealplanner.model.Meal
import com.example.mealplanner.model.UserProfile
import com.example.mealplanner.model.repository.dayplan.DayPlanRepository
import com.example.mealplanner.model.repository.meal.MealRepository
import com.example.mealplanner.model.repository.profile.UserProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface HomeUiState {
    data object Init    : HomeUiState
    data object Loading : HomeUiState
    data class  Success(
        val weekPlan: Map<String, DayPlan>,
        val profile: UserProfile,
        val suggestedMeals: List<Meal>,
        val completedDaysCount: Int,
        val totalWeeklyCalories: Int,
        val totalWeeklyMeals: Int
    ) : HomeUiState
    data class Error(val message: String) : HomeUiState
}

sealed interface HomeNavigationEvent {
    data object ToPlanner         : HomeNavigationEvent
    data object ToCalories        : HomeNavigationEvent
    data object ToProfile         : HomeNavigationEvent
    data class  ToDayDetail(val dayName: String) : HomeNavigationEvent
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val dayPlanRepository: DayPlanRepository,
    private val profileRepository: UserProfileRepository,
    private val mealRepository: MealRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _navEvents = Channel<HomeNavigationEvent>(Channel.BUFFERED)
    val navEvents = _navEvents.receiveAsFlow()

    init {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            try {
                val suggestedMeals = mealRepository.getAllPremadeMeals().take(8)
                combine(
                    dayPlanRepository.observeWeekPlan(),
                    profileRepository.observe()
                ) { weekPlan, profile ->
                    val safeProfile = profile ?: UserProfile()
                    HomeUiState.Success(
                        weekPlan             = weekPlan,
                        profile              = safeProfile,
                        suggestedMeals       = suggestedMeals,
                        completedDaysCount   = weekPlan.values.count { it.isComplete },
                        totalWeeklyCalories  = weekPlan.values.sumOf { it.totalCalories },
                        totalWeeklyMeals     = weekPlan.values.sumOf { it.totalMealCount }
                    )
                }.collect { state -> _uiState.value = state }
            } catch (e: CancellationException) { throw e }
            catch (e: Exception) { _uiState.value = HomeUiState.Error(e.message ?: "Failed to load home data") }
        }
    }

    fun onNavigateToPlanner()         { viewModelScope.launch { _navEvents.send(HomeNavigationEvent.ToPlanner) } }
    fun onNavigateToCalories()        { viewModelScope.launch { _navEvents.send(HomeNavigationEvent.ToCalories) } }
    fun onNavigateToProfile()         { viewModelScope.launch { _navEvents.send(HomeNavigationEvent.ToProfile) } }
    fun onNavigateToDayDetail(day: String) { viewModelScope.launch { _navEvents.send(HomeNavigationEvent.ToDayDetail(day)) } }
}
