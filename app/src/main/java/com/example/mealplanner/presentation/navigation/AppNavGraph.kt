package com.example.mealplanner.presentation.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
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

// ── Slide-transition helpers ──────────────────────────────────────────────────
// Reused on every composable destination so transitions are consistent.
// Forward  → new screen slides in from right, old screen slides out to left.
// Backward → new screen slides in from left, old screen slides out to right.

private const val ANIM_MS = 350   // transition duration in milliseconds

private fun enterSlide()    = slideInHorizontally(tween(ANIM_MS))  { it }
private fun exitSlide()     = slideOutHorizontally(tween(ANIM_MS)) { -it }
private fun popEnterSlide() = slideInHorizontally(tween(ANIM_MS))  { -it }
private fun popExitSlide()  = slideOutHorizontally(tween(ANIM_MS)) { it }

// ── Root Navigation Graph ─────────────────────────────────────────────────────
@Composable
fun AppNavGraph() {
    val navController = rememberNavController()

    // ViewModels created once at graph level — shared across all screens
    // (single source of truth, survives screen recompositions)
    val authViewModel: AuthViewModel               = viewModel()
    val mealPlannerViewModel: MealPlannerViewModel = viewModel()
    val profileViewModel: ProfileViewModel         = viewModel()
    val caloriesViewModel: CaloriesViewModel       = viewModel()

    Scaffold(
        bottomBar = { MealPlannerBottomBar(navController) }
    ) { innerPadding ->

        // startDestination points to the AUTH_GRAPH nested graph, which
        // immediately resolves to its own startDestination (SPLASH).
        NavHost(
            navController    = navController,
            startDestination = NavRoutes.AUTH_GRAPH,
            modifier         = Modifier.padding(innerPadding)
        ) {

            // ════════════════════════════════════════════════════════════════
            // NESTED AUTH GRAPH
            // Groups Splash → Login → Sign-up so they can be cleared from
            // the back stack with a single popUpTo(NavRoutes.AUTH_GRAPH).
            // ════════════════════════════════════════════════════════════════
            navigation(
                startDestination = NavRoutes.SPLASH,
                route            = NavRoutes.AUTH_GRAPH
            ) {

                // Splash — simple fade in/out, no slide
                composable(
                    route           = NavRoutes.SPLASH,
                    enterTransition = { fadeIn(tween(ANIM_MS)) },
                    exitTransition  = { fadeOut(tween(ANIM_MS)) }
                ) {
                    SplashScreen(
                        onFinished = {
                            navController.navigate(NavRoutes.LOGIN) {
                                popUpTo(NavRoutes.SPLASH) { inclusive = true }
                            }
                        }
                    )
                }

                // Login
                composable(
                    route              = NavRoutes.LOGIN,
                    enterTransition    = { enterSlide() },
                    exitTransition     = { exitSlide() },
                    popEnterTransition = { popEnterSlide() },
                    popExitTransition  = { popExitSlide() }
                ) {
                    LoginScreen(
                        viewModel          = authViewModel,
                        onNavigateToSignUp = { navController.navigate(NavRoutes.SIGNUP) },
                        onLoginSuccess     = {
                            // Navigate into main graph; clear entire auth graph from back stack
                            navController.navigate(NavRoutes.MAIN_GRAPH) {
                                popUpTo(NavRoutes.AUTH_GRAPH) { inclusive = true }
                            }
                        }
                    )
                }

                // Sign-up
                composable(
                    route              = NavRoutes.SIGNUP,
                    enterTransition    = { enterSlide() },
                    exitTransition     = { exitSlide() },
                    popEnterTransition = { popEnterSlide() },
                    popExitTransition  = { popExitSlide() }
                ) {
                    SignUpScreen(
                        viewModel         = authViewModel,
                        onSignUpSuccess   = {
                            // Same as login success — land on Home, clear auth back stack
                            navController.navigate(NavRoutes.MAIN_GRAPH) {
                                popUpTo(NavRoutes.AUTH_GRAPH) { inclusive = true }
                            }
                        },
                        onNavigateToLogin = { navController.navigateUp() }
                    )
                }
            }

            // ════════════════════════════════════════════════════════════════
            // NESTED MAIN GRAPH
            // Contains all post-login screens: bottom-nav tabs + deep screens.
            // Navigating to NavRoutes.MAIN_GRAPH lands on HOME (startDestination).
            // ════════════════════════════════════════════════════════════════
            navigation(
                startDestination = NavRoutes.HOME,
                route            = NavRoutes.MAIN_GRAPH
            ) {

                // ── Bottom-nav screens ────────────────────────────────────

                composable(
                    route              = NavRoutes.HOME,
                    enterTransition    = { fadeIn(tween(ANIM_MS)) },
                    exitTransition     = { exitSlide() },
                    popEnterTransition = { popEnterSlide() },
                    popExitTransition  = { popExitSlide() }
                ) {
                    HomeScreen(
                        profileViewModel      = profileViewModel,
                        mealPlannerViewModel  = mealPlannerViewModel,
                        onNavigateToPlanner   = { navController.navigate(NavRoutes.MEAL_PLANNER) },
                        onNavigateToCalories  = { navController.navigate(NavRoutes.CALORIES_CALC) },
                        onNavigateToProfile   = { navController.navigate(NavRoutes.PROFILE) },
                        // Clicking a day card in "This Week" row navigates directly
                        // to that day's detail screen (demonstrates argument passing)
                        onNavigateToDayDetail = { dayName ->
                            navController.navigate(NavRoutes.dayDetail(dayName))
                        }
                    )
                }

                composable(
                    route              = NavRoutes.MEAL_PLANNER,
                    enterTransition    = { enterSlide() },
                    exitTransition     = { exitSlide() },
                    popEnterTransition = { popEnterSlide() },
                    popExitTransition  = { popExitSlide() }
                ) {
                    MealPlannerScreen(
                        viewModel  = mealPlannerViewModel,
                        onDayClick = { dayName ->
                            navController.navigate(NavRoutes.dayDetail(dayName))
                        }
                    )
                }

                composable(
                    route              = NavRoutes.CALORIES_CALC,
                    enterTransition    = { enterSlide() },
                    exitTransition     = { exitSlide() },
                    popEnterTransition = { popEnterSlide() },
                    popExitTransition  = { popExitSlide() }
                ) {
                    CaloriesCalculatorScreen(viewModel = caloriesViewModel)
                }

                composable(
                    route              = NavRoutes.PROFILE,
                    enterTransition    = { enterSlide() },
                    exitTransition     = { exitSlide() },
                    popEnterTransition = { popEnterSlide() },
                    popExitTransition  = { popExitSlide() }
                ) {
                    ProfileScreen(
                        viewModel = profileViewModel,
                        onLogout  = {
                            // Navigate back to Login; clear entire main graph from back stack
                            navController.navigate(NavRoutes.LOGIN) {
                                popUpTo(NavRoutes.MAIN_GRAPH) { inclusive = true }
                            }
                        }
                    )
                }

                // ── Deep screens (argument passing) ───────────────────────

                // Day Detail — receives dayName as a String argument
                composable(
                    route              = NavRoutes.DAY_DETAIL,
                    arguments          = listOf(
                        navArgument("dayName") { type = NavType.StringType }
                    ),
                    enterTransition    = { enterSlide() },
                    exitTransition     = { exitSlide() },
                    popEnterTransition = { popEnterSlide() },
                    popExitTransition  = { popExitSlide() }
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

                // Meal Slot — receives dayName + slotName as String arguments
                composable(
                    route              = NavRoutes.MEAL_SLOT,
                    arguments          = listOf(
                        navArgument("dayName")  { type = NavType.StringType },
                        navArgument("slotName") { type = NavType.StringType }
                    ),
                    enterTransition    = { enterSlide() },
                    exitTransition     = { exitSlide() },
                    popEnterTransition = { popEnterSlide() },
                    popExitTransition  = { popExitSlide() }
                ) { backStackEntry ->
                    val dayName  = backStackEntry.arguments?.getString("dayName")  ?: ""
                    val slotName = backStackEntry.arguments?.getString("slotName") ?: ""
                    MealSlotScreen(
                        dayName     = dayName,
                        slotName    = slotName,
                        viewModel   = mealPlannerViewModel,
                        onAddMeal   = { navController.navigate(NavRoutes.addMeal(dayName, slotName)) },
                        onAddRecipe = { navController.navigate(NavRoutes.recipeBuilder(dayName, slotName)) },
                        onBack      = { navController.navigateUp() }
                    )
                }

                // Add Meal — receives dayName + slotName as String arguments
                composable(
                    route              = NavRoutes.ADD_MEAL,
                    arguments          = listOf(
                        navArgument("dayName")  { type = NavType.StringType },
                        navArgument("slotName") { type = NavType.StringType }
                    ),
                    enterTransition    = { enterSlide() },
                    exitTransition     = { exitSlide() },
                    popEnterTransition = { popEnterSlide() },
                    popExitTransition  = { popExitSlide() }
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

                // Recipe Builder — receives dayName + slotName as String arguments
                composable(
                    route              = NavRoutes.RECIPE_BUILDER,
                    arguments          = listOf(
                        navArgument("dayName")  { type = NavType.StringType },
                        navArgument("slotName") { type = NavType.StringType }
                    ),
                    enterTransition    = { enterSlide() },
                    exitTransition     = { exitSlide() },
                    popEnterTransition = { popEnterSlide() },
                    popExitTransition  = { popExitSlide() }
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
}
