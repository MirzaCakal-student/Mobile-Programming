package com.example.mealplanner.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mealplanner.ui.screens.home.components.EmptyStateView
import com.example.mealplanner.ui.screens.home.components.MealCard

@Composable
fun HomeScreen(viewModel: HomeViewModel = viewModel()) {
    // Collect the state from the ViewModel
    val meals by viewModel.meals.collectAsState()

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        // Buttons to toggle state for testing the edge-case
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Button(onClick = { viewModel.loadMeals(isEmpty = false) }) {
                Text("Load Meals")
            }
            Button(onClick = { viewModel.loadMeals(isEmpty = true) }) {
                Text("Clear Meals")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Handle the empty list edge-case
        if (meals.isEmpty()) {
            EmptyStateView()
        } else {
            LazyColumn {
                items(meals) { meal ->
                    MealCard(meal = meal)
                }
            }
        }
    }
}