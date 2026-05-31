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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.mealplanner.presentation.ui.screens.addmeal.AddMealScreen
import com.example.mealplanner.presentation.ui.screens.calories.CaloriesCalculatorScreen
import com.example.mealplanner.presentation.ui.screens.community.CommunityRecipesScreen
import com.example.mealplanner.presentation.ui.screens.daydetail.DayDetailScreen
import com.example.mealplanner.presentation.ui.screens.habits.HabitsScreen
import com.example.mealplanner.presentation.ui.screens.home.HomeScreen
import com.example.mealplanner.presentation.ui.screens.login.LoginScreen
import com.example.mealplanner.presentation.ui.screens.mealplanner.MealPlannerScreen
import com.example.mealplanner.presentation.ui.screens.mealslot.MealSlotScreen
import com.example.mealplanner.presentation.ui.screens.profile.ProfileScreen
import com.example.mealplanner.presentation.ui.screens.recipe.RecipeBuilderScreen
import com.example.mealplanner.presentation.ui.screens.signup.SignUpScreen
import com.example.mealplanner.presentation.ui.screens.splash.SplashScreen
import com.example.mealplanner.presentation.ui.screens.weather.WeatherScreen
import com.example.mealplanner.presentation.viewmodel.AddMealViewModel
import com.example.mealplanner.presentation.viewmodel.CaloriesViewModel
import com.example.mealplanner.presentation.viewmodel.CommunityRecipesViewModel
import com.example.mealplanner.presentation.viewmodel.DayDetailViewModel
import com.example.mealplanner.presentation.viewmodel.HabitsViewModel
import com.example.mealplanner.presentation.viewmodel.HomeViewModel
import com.example.mealplanner.presentation.viewmodel.LoginViewModel
import com.example.mealplanner.presentation.viewmodel.MealPlannerViewModel
import com.example.mealplanner.presentation.viewmodel.MealSlotViewModel
import com.example.mealplanner.presentation.viewmodel.ProfileViewModel
import com.example.mealplanner.presentation.viewmodel.RecipeBuilderViewModel
import com.example.mealplanner.presentation.viewmodel.SignUpViewModel
import com.example.mealplanner.presentation.viewmodel.SplashViewModel
import com.example.mealplanner.presentation.viewmodel.WeatherViewModel

// ── Slide-transition helpers ──────────────────────────────────────────────────

private const val ANIM_MS = 350

private fun enterSlide()    = slideInHorizontally(tween(ANIM_MS))  { it }
private fun exitSlide()     = slideOutHorizontally(tween(ANIM_MS)) { -it }
private fun popEnterSlide() = slideInHorizontally(tween(ANIM_MS))  { -it }
private fun popExitSlide()  = slideOutHorizontally(tween(ANIM_MS)) { it }

// ── Root Navigation Graph ─────────────────────────────────────────────────────

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { MealPlannerBottomBar(navController) }
    ) { innerPadding ->

        NavHost(
            navController    = navController,
            startDestination = NavRoutes.AUTH_GRAPH,
            modifier         = Modifier.padding(innerPadding)
        ) {

            // ════════════════════════════════════════════════════════════════
            // NESTED AUTH GRAPH
            // hiltViewModel() retrieves a @HiltViewModel-scoped ViewModel for each
            // backstack entry — 1 screen = 1 ViewModel, lifecycle managed by Hilt.
            // ════════════════════════════════════════════════════════════════
            navigation(
                startDestination = NavRoutes.SPLASH,
                route            = NavRoutes.AUTH_GRAPH
            ) {

                composable(
                    route           = NavRoutes.SPLASH,
                    enterTransition = { fadeIn(tween(ANIM_MS)) },
                    exitTransition  = { fadeOut(tween(ANIM_MS)) }
                ) {
                    // Ask the SplashViewModel whether Firebase still holds a cached auth token.
                    // If yes → skip Login entirely; if no → go to Login.
                    val splashViewModel: SplashViewModel = hiltViewModel()
                    SplashScreen(
                        isLoggedIn = splashViewModel.isLoggedIn,
                        onFinished = { loggedIn ->
                            val destination = if (loggedIn) NavRoutes.MAIN_GRAPH else NavRoutes.LOGIN
                            navController.navigate(destination) {
                                popUpTo(NavRoutes.AUTH_GRAPH) { inclusive = true }
                            }
                        }
                    )
                }

                composable(
                    route              = NavRoutes.LOGIN,
                    enterTransition    = { enterSlide() },
                    exitTransition     = { exitSlide() },
                    popEnterTransition = { popEnterSlide() },
                    popExitTransition  = { popExitSlide() }
                ) {
                    val viewModel: LoginViewModel = hiltViewModel()
                    LoginScreen(
                        viewModel          = viewModel,
                        onNavigateToSignUp = { navController.navigate(NavRoutes.SIGNUP) },
                        onLoginSuccess     = {
                            navController.navigate(NavRoutes.MAIN_GRAPH) {
                                popUpTo(NavRoutes.AUTH_GRAPH) { inclusive = true }
                            }
                        }
                    )
                }

                composable(
                    route              = NavRoutes.SIGNUP,
                    enterTransition    = { enterSlide() },
                    exitTransition     = { exitSlide() },
                    popEnterTransition = { popEnterSlide() },
                    popExitTransition  = { popExitSlide() }
                ) {
                    val viewModel: SignUpViewModel = hiltViewModel()
                    SignUpScreen(
                        viewModel         = viewModel,
                        onSignUpSuccess   = {
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
            // hiltViewModel() is used for all screens. Hilt + Navigation Compose
            // automatically populate SavedStateHandle from route arguments for
            // deep screens (DayDetail, MealSlot, AddMeal, RecipeBuilder).
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
                    val viewModel: HomeViewModel = hiltViewModel()
                    HomeScreen(
                        viewModel             = viewModel,
                        onNavigateToPlanner   = { navController.navigate(NavRoutes.MEAL_PLANNER) },
                        onNavigateToCalories  = { navController.navigate(NavRoutes.CALORIES_CALC) },
                        onNavigateToProfile   = { navController.navigate(NavRoutes.PROFILE) },
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
                    val viewModel: MealPlannerViewModel = hiltViewModel()
                    MealPlannerScreen(
                        viewModel  = viewModel,
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
                    val viewModel: CaloriesViewModel = hiltViewModel()
                    CaloriesCalculatorScreen(
                        viewModel             = viewModel,
                        onNavigateToCommunity = { navController.navigate(NavRoutes.COMMUNITY) }
                    )
                }

                composable(
                    route              = NavRoutes.PROFILE,
                    enterTransition    = { enterSlide() },
                    exitTransition     = { exitSlide() },
                    popEnterTransition = { popEnterSlide() },
                    popExitTransition  = { popExitSlide() }
                ) {
                    val viewModel: ProfileViewModel = hiltViewModel()
                    ProfileScreen(
                        viewModel            = viewModel,
                        onLogout             = {
                            navController.navigate(NavRoutes.LOGIN) {
                                popUpTo(NavRoutes.MAIN_GRAPH) { inclusive = true }
                            }
                        },
                        onNavigateToHabits  = { navController.navigate(NavRoutes.HABITS) },
                        onNavigateToWeather = { navController.navigate(NavRoutes.WEATHER) }
                    )
                }

                // ── Deep screens — SavedStateHandle carries nav arguments ──

                // DayDetailViewModel gets dayName from SavedStateHandle automatically
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
                    val dayName   = backStackEntry.arguments?.getString("dayName") ?: ""
                    val viewModel: DayDetailViewModel = hiltViewModel()
                    DayDetailScreen(
                        viewModel   = viewModel,
                        onSlotClick = { slotName ->
                            navController.navigate(NavRoutes.mealSlot(dayName, slotName))
                        },
                        onBack = { navController.navigateUp() }
                    )
                }

                // MealSlotViewModel gets dayName + slotName from SavedStateHandle
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
                    val viewModel: MealSlotViewModel = hiltViewModel()
                    MealSlotScreen(
                        viewModel   = viewModel,
                        onAddMeal   = { navController.navigate(NavRoutes.addMeal(dayName, slotName)) },
                        onAddRecipe = { navController.navigate(NavRoutes.recipeBuilder(dayName, slotName)) },
                        onBack      = { navController.navigateUp() }
                    )
                }

                // AddMealViewModel gets dayName + slotName from SavedStateHandle
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
                ) {
                    val viewModel: AddMealViewModel = hiltViewModel()
                    AddMealScreen(
                        viewModel = viewModel,
                        onBack    = { navController.navigateUp() }
                    )
                }

                // RecipeBuilderViewModel gets dayName + slotName from SavedStateHandle
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
                ) {
                    val viewModel: RecipeBuilderViewModel = hiltViewModel()
                    RecipeBuilderScreen(
                        viewModel = viewModel,
                        onBack    = { navController.navigateUp() }
                    )
                }

                // Lab 11 — Habits (Cloud) screen, opened from Profile → My Habits
                // Backed by the Retrofit network repository (HabitRepository + HabitApiService)
                composable(
                    route              = NavRoutes.HABITS,
                    enterTransition    = { enterSlide() },
                    exitTransition     = { exitSlide() },
                    popEnterTransition = { popEnterSlide() },
                    popExitTransition  = { popExitSlide() }
                ) {
                    val viewModel: HabitsViewModel = hiltViewModel()
                    HabitsScreen(
                        viewModel = viewModel,
                        onBack    = { navController.navigateUp() }
                    )
                }

                // OpenWeather screen — opened from Profile → Weather Today
                // Backed by the second Retrofit instance in NetworkModule (WeatherRepository).
                composable(
                    route              = NavRoutes.WEATHER,
                    enterTransition    = { enterSlide() },
                    exitTransition     = { exitSlide() },
                    popEnterTransition = { popEnterSlide() },
                    popExitTransition  = { popExitSlide() }
                ) {
                    val viewModel: WeatherViewModel = hiltViewModel()
                    WeatherScreen(
                        viewModel = viewModel,
                        onBack    = { navController.navigateUp() }
                    )
                }

                // Lab 12 — Community Recipes (Firestore realtime feed)
                // Opened from Profile → Community Recipes. Backed by FirebaseFirestore.
                composable(
                    route              = NavRoutes.COMMUNITY,
                    enterTransition    = { enterSlide() },
                    exitTransition     = { exitSlide() },
                    popEnterTransition = { popEnterSlide() },
                    popExitTransition  = { popExitSlide() }
                ) {
                    val viewModel: CommunityRecipesViewModel = hiltViewModel()
                    CommunityRecipesScreen(
                        viewModel = viewModel,
                        onBack    = { navController.navigateUp() }
                    )
                }
            }
        }
    }
}
