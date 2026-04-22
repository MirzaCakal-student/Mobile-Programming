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
import com.example.mealplanner.presentation.viewmodel.AddMealViewModel
import com.example.mealplanner.presentation.viewmodel.CaloriesViewModel
import com.example.mealplanner.presentation.viewmodel.DayDetailViewModel
import com.example.mealplanner.presentation.viewmodel.HomeViewModel
import com.example.mealplanner.presentation.viewmodel.LoginViewModel
import com.example.mealplanner.presentation.viewmodel.MealPlannerViewModel
import com.example.mealplanner.presentation.viewmodel.MealSlotViewModel
import com.example.mealplanner.presentation.viewmodel.ProfileViewModel
import com.example.mealplanner.presentation.viewmodel.RecipeBuilderViewModel
import com.example.mealplanner.presentation.viewmodel.SignUpViewModel

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
            // Each screen creates its own ViewModel — 1 screen = 1 ViewModel.
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
                    SplashScreen(
                        onFinished = {
                            navController.navigate(NavRoutes.LOGIN) {
                                popUpTo(NavRoutes.SPLASH) { inclusive = true }
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
                    val viewModel: LoginViewModel = viewModel()
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
                    val viewModel: SignUpViewModel = viewModel()
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
            // Each composable creates its own ViewModel via viewModel().
            // Deep-screen VMs (DayDetail, MealSlot, AddMeal, RecipeBuilder)
            // receive nav arguments automatically via SavedStateHandle.
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
                    val viewModel: HomeViewModel = viewModel()
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
                    val viewModel: MealPlannerViewModel = viewModel()
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
                    val viewModel: CaloriesViewModel = viewModel()
                    CaloriesCalculatorScreen(viewModel = viewModel)
                }

                composable(
                    route              = NavRoutes.PROFILE,
                    enterTransition    = { enterSlide() },
                    exitTransition     = { exitSlide() },
                    popEnterTransition = { popEnterSlide() },
                    popExitTransition  = { popExitSlide() }
                ) {
                    val viewModel: ProfileViewModel = viewModel()
                    ProfileScreen(
                        viewModel = viewModel,
                        onLogout  = {
                            navController.navigate(NavRoutes.LOGIN) {
                                popUpTo(NavRoutes.MAIN_GRAPH) { inclusive = true }
                            }
                        }
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
                    val viewModel: DayDetailViewModel = viewModel()
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
                    val viewModel: MealSlotViewModel = viewModel()
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
                    val viewModel: AddMealViewModel = viewModel()
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
                    val viewModel: RecipeBuilderViewModel = viewModel()
                    RecipeBuilderScreen(
                        viewModel = viewModel,
                        onBack    = { navController.navigateUp() }
                    )
                }
            }
        }
    }
}
