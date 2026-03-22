package com.example.mealplanner.ui.screens.home

import androidx.lifecycle.ViewModel
import com.example.mealplanner.model.Meal
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class HomeViewModel : ViewModel() {
    // StateFlow to manage the UI state
    private val _meals = MutableStateFlow<List<Meal>>(emptyList())
    val meals: StateFlow<List<Meal>> = _meals.asStateFlow()

    // Function to simulate loading data (or leaving it empty to test edge cases)
    fun loadMeals(isEmpty: Boolean = false) {
        if (isEmpty) {
            _meals.value = emptyList()
        } else {
            _meals.value = listOf(
                Meal("1", "Oatmeal with Berries", "Breakfast"),
                Meal("2", "Grilled Chicken Salad", "Lunch"),
                Meal("3", "Spaghetti Bolognese", "Dinner")
            )
        }
    }
}