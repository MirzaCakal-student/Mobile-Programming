package com.example.mealplanner.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.mealplanner.presentation.ui.screens.addmeal.AddMealScreen
import com.example.mealplanner.presentation.ui.screens.calories.CaloriesCalculatorScreen
import com.example.mealplanner.presentation.ui.screens.daydetail.DayDetailScreen
import com.example.mealplanner.presentation.ui.screens.home.HomeScreen
import com.example.mealplanner.presentation.ui.screens.login.LoginScreen
import com.example.mealplanner.presentation.ui.screens.mealplanner.MealPlannerScreen
import com.example.mealplanner.presentation.ui.screens.mealslot.MealSlotScreen
import com.example.mealplanner.presentation.ui.screens.profile.ProfileScreen
import com.example.mealplanner.presentation.ui.screens.recipe.RecipeBuilderScreen
import com.example.mealplanner.presentation.ui.screens.signup.SignUpScreen
import com.example.mealplanner.presentation.ui.screens.splash.SplashScreen
import com.example.mealplanner.presentation.viewmodel.AuthViewModel
import com.example.mealplanner.presentation.viewmodel.CaloriesViewModel
import com.example.mealplanner.presentation.viewmodel.MealPlannerViewModel
import com.example.mealplanner.presentation.viewmodel.ProfileViewModel

@Composable
fun AppNavGraph() {
    val navController            = rememberNavController()

    // ViewModels created at graph level — shared across all screens (single source of truth)
    val authViewModel: AuthViewModel               = viewModel()
    val mealPlannerViewModel: MealPlannerViewModel = viewModel()
    val profileViewModel: ProfileViewModel         = viewModel()
    val caloriesViewModel: CaloriesViewModel       = viewModel()

    Scaffold(
        bottomBar = { MealPlannerBottomBar(navController) }
    ) { innerPadding ->

        NavHost(
            navController    = navController,
            startDestination = NavRoutes.SPLASH,
            modifier         = Modifier.padding(innerPadding)
        ) {

            // ── Auth flow ────────────────────────────────────────────────────────
            composable(NavRoutes.SPLASH) {
                SplashScreen(
                    onFinished = {
                        navController.navigate(NavRoutes.LOGIN) {
                            popUpTo(NavRoutes.SPLASH) { inclusive = true }
                        }
                    }
                )
            }

            composable(NavRoutes.LOGIN) {
                LoginScreen(
                    viewModel          = authViewModel,
                    onNavigateToSignUp = { navController.navigate(NavRoutes.SIGNUP) },
                    onLoginSuccess     = {
                        navController.navigate(NavRoutes.HOME) {
                            popUpTo(NavRoutes.LOGIN) { inclusive = true }
                        }
                    }
                )
            }

            composable(NavRoutes.SIGNUP) {
                SignUpScreen(
                    viewModel         = authViewModel,
                    onSignUpSuccess   = {
                        navController.navigate(NavRoutes.HOME) {
                            popUpTo(NavRoutes.LOGIN) { inclusive = true }
                        }
                    },
                    onNavigateToLogin = { navController.navigateUp() }
                )
            }

            // ── Main app screens ────────────────────────────────────────────────
            composable(NavRoutes.HOME) {
                HomeScreen(
                    profileViewModel     = profileViewModel,
                    mealPlannerViewModel = mealPlannerViewModel,
                    onNavigateToPlanner  = { navController.navigate(NavRoutes.MEAL_PLANNER) },
                    onNavigateToCalories = { navController.navigate(NavRoutes.CALORIES_CALC) },
                    onNavigateToProfile  = { navController.navigate(NavRoutes.PROFILE) }
                )
            }

            composable(NavRoutes.MEAL_PLANNER) {
                MealPlannerScreen(
                    viewModel  = mealPlannerViewModel,
                    onDayClick = { navController.navigate(NavRoutes.dayDetail(it)) }
                )
            }

            composable(NavRoutes.CALORIES_CALC) {
                CaloriesCalculatorScreen(viewModel = caloriesViewModel)
            }

            composable(NavRoutes.PROFILE) {
                ProfileScreen(
                    viewModel = profileViewModel,
                    onLogout  = {
                        navController.navigate(NavRoutes.LOGIN) {
                            popUpTo(NavRoutes.HOME) { inclusive = true }
                        }
                    }
                )
            }

            // ── Deep navigation with argument passing ───────────────────────────
            composable(
                route     = NavRoutes.DAY_DETAIL,
                arguments = listOf(navArgument("dayName") { type = NavType.StringType })
            ) { backStackEntry ->
                val dayName = backStackEntry.arguments?.getString("dayName") ?: ""
                DayDetailScreen(
                    dayName     = dayName,
                    viewModel   = mealPlannerViewModel,
                    onSlotClick = { slotName ->
                        navController.navigate(NavRoutes.mealSlot(dayName, slotName))
                    },
                    onBack = { navController.navigateUp() }
                )
            }

            composable(
                route     = NavRoutes.MEAL_SLOT,
                arguments = listOf(
                    navArgument("dayName")  { type = NavType.StringType },
                    navArgument("slotName") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val dayName  = backStackEntry.arguments?.getString("dayName")  ?: ""
                val slotName = backStackEntry.arguments?.getString("slotName") ?: ""
                MealSlotScreen(
                    dayName   = dayName,
                    slotName  = slotName,
                    viewModel = mealPlannerViewModel,
                    onAddMeal   = { navController.navigate(NavRoutes.addMeal(dayName, slotName)) },
                    onAddRecipe = { navController.navigate(NavRoutes.recipeBuilder(dayName, slotName)) },
                    onBack      = { navController.navigateUp() }
                )
            }

            composable(
                route     = NavRoutes.ADD_MEAL,
                arguments = listOf(
                    navArgument("dayName")  { type = NavType.StringType },
                    navArgument("slotName") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val dayName  = backStackEntry.arguments?.getString("dayName")  ?: ""
                val slotName = backStackEntry.arguments?.getString("slotName") ?: ""
                AddMealScreen(
                    dayName   = dayName,
                    slotName  = slotName,
                    viewModel = mealPlannerViewModel,
                    onBack    = { navController.navigateUp() }
                )
            }

            composable(
                route     = NavRoutes.RECIPE_BUILDER,
                arguments = listOf(
                    navArgument("dayName")  { type = NavType.StringType },
                    navArgument("slotName") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val dayName  = backStackEntry.arguments?.getString("dayName")  ?: ""
                val slotName = backStackEntry.arguments?.getString("slotName") ?: ""
                RecipeBuilderScreen(
                    dayName   = dayName,
                    slotName  = slotName,
                    viewModel = mealPlannerViewModel,
                    onBack    = { navController.navigateUp() }
                )
            }
        }
    }
}
