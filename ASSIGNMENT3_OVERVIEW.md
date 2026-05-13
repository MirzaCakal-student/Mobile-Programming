# Meal Planner — Assignment 3 Complete Technical Overview

> **Purpose:** Full explanation of the project architecture, every file's role, how data flows,
> and answers to every question likely to come up at the oral defence.

---

## Table of Contents

1. [What Was Built](#1-what-was-built)
2. [Project Structure](#2-project-structure)
3. [Technology Stack](#3-technology-stack)
4. [Layer 1 — Domain Models](#4-layer-1--domain-models)
5. [Layer 2 — Room Database](#5-layer-2--room-database)
6. [Layer 3 — Repository Pattern](#6-layer-3--repository-pattern)
7. [Layer 4 — Dependency Injection (Hilt)](#7-layer-4--dependency-injection-hilt)
8. [Layer 5 — ViewModels (Sealed UiState + Channel)](#8-layer-5--viewmodels-sealed-uistate--channel)
9. [Layer 6 — UI Screens (Stateful / Stateless)](#9-layer-6--ui-screens-stateful--stateless)
10. [Layer 7 — Navigation](#10-layer-7--navigation)
11. [Complete Data Flow: End-to-End Example](#11-complete-data-flow-end-to-end-example)
12. [Key Design Decisions (Why, Not Just What)](#12-key-design-decisions-why-not-just-what)
13. [Defence Q&A](#13-defence-qa)

---

## 1. What Was Built

A **Meal Planner Android app** written in **Kotlin** using **Jetpack Compose** for the UI.
The app lets a user:
- View a 7-day weekly meal plan
- Add pre-made or custom meals to any day/slot (Breakfast, Lunch, Dinner, Snacks)
- Build custom recipes from ingredients with automatic macro calculations
- Calculate daily calorie needs (BMR/TDEE) using the Mifflin-St Jeor formula
- Edit their personal profile (name, weight, height, calorie goal)
- Mark days as complete

All data is persisted in a **Room (SQLite) database** that is seeded with 25 meals, 55 ingredients
and 7 day-plan rows the first time the app runs.

---

## 2. Project Structure

```
app/src/main/java/com/example/mealplanner/
│
├── MealPlannerApplication.kt          ← Hilt entry point (@HiltAndroidApp)
├── MainActivity.kt                    ← Single Activity (@AndroidEntryPoint)
│
├── di/
│   └── AppModule.kt                   ← Empty Hilt module (repos now in DatabaseModule)
│
├── model/                             ← DATA LAYER
│   ├── Meal.kt                        ← Domain model
│   ├── DayPlan.kt                     ← Domain model
│   ├── Ingredient.kt                  ← Domain model
│   ├── MealSlotType.kt                ← Enum (Breakfast/Lunch/Dinner/Snacks)
│   ├── UserProfile.kt                 ← Domain model
│   ├── HardcodedData.kt               ← Legacy (weekDays list still used for ordering)
│   │
│   ├── data/local/
│   │   ├── entity/
│   │   │   ├── UserProfileEntity.kt   ← Room @Entity (table: user_profile)
│   │   │   ├── DayPlanEntity.kt       ← Room @Entity (table: day_plans)
│   │   │   ├── MealEntity.kt          ← Room @Entity (table: meals)
│   │   │   ├── DayMealCrossRef.kt     ← Room @Entity junction table (day ↔ meal)
│   │   │   └── IngredientEntity.kt    ← Room @Entity (table: ingredients)
│   │   ├── dao/
│   │   │   ├── UserProfileDao.kt      ← @Dao (observe, insert/replace)
│   │   │   ├── DayPlanDao.kt          ← @Dao (observe, CRUD, cross-ref ops, JOIN)
│   │   │   ├── MealDao.kt             ← @Dao (insert, getAll, getPremade, search)
│   │   │   └── IngredientDao.kt       ← @Dao (getAll, search)
│   │   ├── db/
│   │   │   └── AppDatabase.kt         ← @Database, SeedCallback
│   │   └── util/
│   │       ├── MealSeedData.kt        ← 25 pre-made meals
│   │       ├── DayPlanSeedData.kt     ← 7 day-plan rows (Monday–Sunday)
│   │       └── IngredientSeedData.kt  ← 55 ingredients with per-100g macros
│   │
│   ├── repository/
│   │   ├── dayplan/
│   │   │   ├── DayPlanRepository.kt          ← Interface
│   │   │   ├── DayPlanRepositoryImpl.kt       ← Room-backed implementation
│   │   │   └── mapper/DayPlanMapper.kt        ← Entity ↔ Domain converters
│   │   ├── meal/
│   │   │   ├── MealRepository.kt
│   │   │   ├── MealRepositoryImpl.kt
│   │   │   └── mapper/MealMapper.kt
│   │   ├── profile/
│   │   │   ├── UserProfileRepository.kt
│   │   │   ├── UserProfileRepositoryImpl.kt
│   │   │   └── mapper/UserProfileMapper.kt
│   │   └── ingredient/
│   │       ├── IngredientRepository.kt
│   │       ├── IngredientRepositoryImpl.kt
│   │       └── mapper/IngredientMapper.kt
│   │
│   └── di/
│       └── DatabaseModule.kt          ← Hilt module: provides DB, DAOs, repo bindings
│
└── presentation/                      ← UI LAYER
    ├── navigation/
    │   ├── AppNavGraph.kt             ← NavHost, all routes, screen wiring
    │   ├── NavRoutes.kt               ← Route constants and builders
    │   └── BottomNavBar.kt            ← Bottom navigation bar composable
    │
    ├── theme/                         ← Material3 colours, typography, shapes
    │
    ├── ui/
    │   ├── components/                ← Shared composables (AppTextField, PrimaryButton…)
    │   └── screens/
    │       ├── splash/SplashScreen.kt
    │       ├── login/LoginScreen.kt
    │       ├── signup/SignUpScreen.kt
    │       ├── home/HomeScreen.kt
    │       ├── mealplanner/MealPlannerScreen.kt
    │       ├── daydetail/DayDetailScreen.kt
    │       ├── mealslot/MealSlotScreen.kt
    │       ├── addmeal/AddMealScreen.kt
    │       ├── recipe/RecipeBuilderScreen.kt
    │       ├── profile/ProfileScreen.kt
    │       └── calories/CaloriesCalculatorScreen.kt
    │
    └── viewmodel/
        ├── LoginViewModel.kt
        ├── SignUpViewModel.kt
        ├── HomeViewModel.kt
        ├── MealPlannerViewModel.kt
        ├── DayDetailViewModel.kt
        ├── MealSlotViewModel.kt
        ├── AddMealViewModel.kt
        ├── RecipeBuilderViewModel.kt
        ├── ProfileViewModel.kt
        └── CaloriesViewModel.kt
```

---

## 3. Technology Stack

| Technology | Version | Why used |
|---|---|---|
| **Kotlin** | 2.2.10 | Primary language — null-safe, concise, coroutine support |
| **Jetpack Compose** | BOM 2024.09 | Declarative UI — no XML layouts, state-driven rendering |
| **Room** | 2.6.1 | Jetpack ORM over SQLite — type-safe queries, Flow support |
| **Hilt** | 2.51.1 | Dependency injection — removes manual singleton management |
| **KSP** | 2.2.10-1.0.29 | Annotation processor for Hilt + Room (faster than KAPT) |
| **Navigation Compose** | 2.7.7 | Type-safe navigation between Compose screens |
| **Kotlin Coroutines** | bundled | Async DB calls without blocking the main thread |
| **StateFlow / Channel** | bundled | Reactive state management and one-shot navigation events |
| **lifecycle-runtime-compose** | 2.10.0 | `collectAsStateWithLifecycle()` — lifecycle-aware state collection |

---

## 4. Layer 1 — Domain Models

**Location:** `model/*.kt`

These are plain Kotlin `data class` objects — they have **no Android dependencies** and no
Room annotations. They represent the app's business concepts.

| Class | Fields | Notes |
|---|---|---|
| `Meal` | id, name, calories, proteinG, fatG, carbsG, isCustom | Pre-made OR user-created |
| `DayPlan` | dayName, breakfast/lunch/dinner/snacks (MutableList<Meal>), isComplete, eatTimes | Computed properties: `totalCalories`, `totalMealCount` |
| `Ingredient` | id, name, caloriesPer100g, proteinPer100g, fatPer100g, carbsPer100g | Per-100g values for recipe builder |
| `UserProfile` | name, email, weightKg, heightCm, ageYears, dailyCalorieGoal, gender | All fields have defaults |
| `MealSlotType` | BREAKFAST, LUNCH, DINNER, SNACKS (enum with `displayName`) | Maps slot strings to enum values |

**Why separate domain models from Room entities?**
Because Room entities are tied to the database schema. If the database changes (e.g., a column
is renamed), only the entity and mapper change — the ViewModels and Screens see no change.
This is the **separation of concerns** principle.

---

## 5. Layer 2 — Room Database

### 5.1 What is Room?

Room is Google's official persistence library. It sits on top of Android's built-in SQLite
database and generates boilerplate SQL code at compile time using KSP annotations.

**No MySQL is needed.** Room uses the SQLite engine that ships with every Android device.
The database file lives in the app's private storage (`/data/data/com.example.mealplanner/`).

### 5.2 Entities (Tables)

**`UserProfileEntity`** — table `user_profile`
```
id (PK = 1 always) | name | email | weight_kg | height_cm | age_years | daily_calorie_goal | gender
```
Single-user app — always exactly one row with `id = 1`. On save, `@Insert(onConflict = REPLACE)`
upserts (replaces) the existing row.

**`DayPlanEntity`** — table `day_plans`
```
id (PK, auto) | day_name | is_complete | sort_order | eat_time_breakfast | eat_time_lunch | eat_time_dinner | eat_time_snacks
```
Always 7 rows (Monday–Sunday), seeded on first launch. `sort_order` (0–6) ensures correct ordering.

**`MealEntity`** — table `meals`
```
id (PK, auto) | name | calories | protein_g | fat_g | carbs_g | is_custom
```
25 rows seeded (pre-made meals). Custom meals added by users are inserted with `is_custom = 1`.

**`DayMealCrossRef`** — table `day_meal_cross_ref` (JUNCTION TABLE)
```
id (PK, auto) | day_plan_id (FK → day_plans) | meal_id (FK → meals) | slot_type
```
This is the **many-to-many** relationship table. A meal can be in multiple days/slots.
`slot_type` stores `"Breakfast"`, `"Lunch"`, `"Dinner"`, or `"Snacks"`.
Foreign keys use `CASCADE DELETE` — removing a day removes all its meal assignments.
Indices on `day_plan_id` and `meal_id` make JOIN queries fast.

**`IngredientEntity`** — table `ingredients`
```
id (PK, auto) | name | calories_per100g | protein_per100g | fat_per100g | carbs_per100g
```
55 rows seeded. Read-only — users never add ingredients directly.

### 5.3 AppDatabase

**Location:** `model/data/local/db/AppDatabase.kt`

```kotlin
@Database(
    entities = [UserProfileEntity::class, DayPlanEntity::class, MealEntity::class,
                DayMealCrossRef::class, IngredientEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userProfileDao(): UserProfileDao
    abstract fun dayPlanDao(): DayPlanDao
    abstract fun mealDao(): MealDao
    abstract fun ingredientDao(): IngredientDao
}
```

`fallbackToDestructiveMigration(true)` is set in `DatabaseModule` — during development, if the
schema changes, Room drops and recreates the database (re-seeds automatically).

### 5.4 Seed Data (SeedCallback)

**Location:** `AppDatabase.SeedCallback` (inner class) + `model/data/local/util/`

`SeedCallback` extends `RoomDatabase.Callback` and overrides `onCreate()`.
This fires exactly once — the very first time the database is created on a fresh install.

```kotlin
override fun onCreate(db: SupportSQLiteDatabase) {
    super.onCreate(db)
    CoroutineScope(Dispatchers.IO).launch {
        val database = databaseProvider.get()   // avoid circular dependency via Provider<>
        database.mealDao().insertAll(MealSeedData.meals)
        database.dayPlanDao().insertAll(DayPlanSeedData.dayPlans)
        database.ingredientDao().insertAll(IngredientSeedData.ingredients)
        database.userProfileDao().insert(
            UserProfileEntity(id = 1, name = "Mirza", email = "mirza@example.com", dailyCalorieGoal = 2000)
        )
    }
}
```

The `Provider<AppDatabase>` is used instead of `AppDatabase` directly to avoid a circular
dependency (Hilt would try to create `AppDatabase` → which needs `SeedCallback` → which needs
`AppDatabase` again). `Provider` breaks the cycle by deferring the get.

**Where does seed data come from?**
- `MealSeedData.kt` — the same 25 meals that were in `HardcodedData.premadeMeals` in Assignment 2, now stored as `MealEntity` objects with `id = 0` (Room auto-generates IDs)
- `DayPlanSeedData.kt` — 7 empty day plans (Monday–Sunday), `sort_order` 0–6
- `IngredientSeedData.kt` — the same 55 ingredients from `HardcodedData.ingredients`

### 5.5 DAOs

**`UserProfileDao`** — reactive profile observation
```kotlin
@Insert(onConflict = OnConflictStrategy.REPLACE)
suspend fun insert(profile: UserProfileEntity)

@Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
fun observe(): Flow<UserProfileEntity?>   // ← no suspend! Returns cold Flow
```

**`DayPlanDao`** — the most complex DAO, includes a JOIN query
```kotlin
@Query("SELECT * FROM day_plans ORDER BY sort_order ASC")
fun observeAll(): Flow<List<DayPlanEntity>>

@Query("""
    SELECT cr.id AS cross_ref_id, m.id, m.name, m.calories, m.protein_g, m.fat_g,
           m.carbs_g, m.is_custom, cr.slot_type, cr.day_plan_id
    FROM day_meal_cross_ref cr
    INNER JOIN meals m ON cr.meal_id = m.id
""")
fun observeAllMealsWithSlots(): Flow<List<MealWithSlot>>
```

The result of the JOIN is mapped into `MealWithSlot` data class which carries both the
meal data AND which day/slot it belongs to.

**Why no `suspend` on Flow-returning DAO methods?**
`Flow` is already cold and async — it doesn't need `suspend`. Only one-shot operations
(insert, update, delete, getById) need `suspend`.

---

## 6. Layer 3 — Repository Pattern

### 6.1 Why Repositories?

ViewModels should not talk directly to DAOs. Reasons:
1. **Testability** — you can fake a repository in tests without a real database
2. **Abstraction** — ViewModel uses the interface, not the Room class directly
3. **Thread management** — the repository owns `Dispatchers.IO`, ViewModel stays on main

### 6.2 The Interface + Implementation Split

Each repository domain has:
- `XRepository.kt` — interface (what the ViewModel sees)
- `XRepositoryImpl.kt` — Room-backed implementation (injected by Hilt)
- `mapper/XMapper.kt` — extension functions: `Entity.toDomain()` and `Domain.toEntity()`

### 6.3 DayPlanRepositoryImpl — The Hardest One

**Location:** `model/repository/dayplan/DayPlanRepositoryImpl.kt`

The week plan is stored in two separate tables. To reconstruct `Map<String, DayPlan>` we
need to JOIN them. Room does the JOIN, but we need to combine two reactive Flows:

```kotlin
override fun observeWeekPlan(): Flow<Map<String, DayPlan>> {
    return combine(
        dayPlanDao.observeAll(),                  // Flow<List<DayPlanEntity>>
        dayPlanDao.observeAllMealsWithSlots()     // Flow<List<MealWithSlot>>
    ) { dayEntities, mealsWithSlots ->
        // This lambda runs every time EITHER flow emits a new value
        val slotsByDay = mealsWithSlots.groupBy { it.dayPlanId }
        dayEntities.associate { entity ->
            val slots = slotsByDay[entity.id] ?: emptyList()
            entity.dayName to entity.toDomain(
                breakfast = slots.filter { it.slotType == "Breakfast" }.map { it.toMeal() },
                lunch     = slots.filter { it.slotType == "Lunch"     }.map { it.toMeal() },
                dinner    = slots.filter { it.slotType == "Dinner"    }.map { it.toMeal() },
                snacks    = slots.filter { it.slotType == "Snacks"    }.map { it.toMeal() }
            )
        }
    }
}
```

`combine()` merges two cold Flows into one. Every time a meal is added or removed,
Room re-emits from `observeAllMealsWithSlots()`, `combine()` fires its lambda,
a new `Map<String, DayPlan>` is built, and the ViewModel's StateFlow updates,
which re-composes every screen observing it — all automatically.

**Adding a meal to a slot:**
```kotlin
override suspend fun addMealToSlot(dayName: String, slotName: String, meal: Meal) {
    val dayEntity = dayPlanDao.getByName(dayName) ?: return
    val mealDbId = if (meal.isCustom) {
        mealDao.insert(meal.toEntity()).toInt()  // insert custom meal, get its auto-generated ID
    } else {
        mealDao.getPremade().firstOrNull { it.name == meal.name }?.id ?: return
    }
    dayPlanDao.insertCrossRef(
        DayMealCrossRef(dayPlanId = dayEntity.id, mealId = mealDbId, slotType = slotName)
    )
}
```

After `insertCrossRef`, Room emits a new value from `observeAllMealsWithSlots()`,
`combine()` reacts, the full week map rebuilds, and the UI updates.

---

## 7. Layer 4 — Dependency Injection (Hilt)

### 7.1 What is Hilt?

Hilt is Google's DI framework built on top of Dagger. It automatically provides dependencies
without manual factory classes or service locators.

### 7.2 Setup

| Annotation | Location | What it does |
|---|---|---|
| `@HiltAndroidApp` | `MealPlannerApplication` | Generates the Hilt component tree |
| `@AndroidEntryPoint` | `MainActivity` | Allows Hilt to inject into this Activity |
| `@HiltViewModel` | Every ViewModel | Hilt creates and manages the ViewModel |
| `@Inject constructor(...)` | ViewModels, Repos, DAOs | Hilt knows how to create this class |
| `@Module @InstallIn(SingletonComponent::class)` | `DatabaseModule` | Hilt module that lives for app lifetime |
| `@Provides @Singleton` | Methods in DatabaseModule | Functions that tell Hilt how to build dependencies |

### 7.3 DatabaseModule — How Hilt Wires Everything

**Location:** `model/di/DatabaseModule.kt`

```kotlin
@Provides @Singleton
fun provideDatabase(@ApplicationContext context: Context, seedCallback: AppDatabase.SeedCallback): AppDatabase {
    return Room.databaseBuilder(context, AppDatabase::class.java, "mealplanner_database")
        .addCallback(seedCallback)
        .fallbackToDestructiveMigration(true)
        .build()
}

@Provides @Singleton
fun provideDayPlanRepository(impl: DayPlanRepositoryImpl): DayPlanRepository = impl
```

The last line is the key DI binding — `DayPlanRepository` (interface) is bound to
`DayPlanRepositoryImpl` (class). When a ViewModel asks for `DayPlanRepository`,
Hilt gives it `DayPlanRepositoryImpl`.

### 7.4 Singleton Scope

All repositories and the database are `@Singleton`. This means:
- There is exactly **one** `AppDatabase` instance for the entire app lifetime
- All ViewModels that inject `DayPlanRepository` receive the **same** instance
- State changes in one screen are immediately visible in all other screens

### 7.5 How hiltViewModel() Works in Navigation

In `AppNavGraph.kt`, each composable destination calls:
```kotlin
val viewModel: HomeViewModel = hiltViewModel()
```

`hiltViewModel()` (from `androidx.hilt:hilt-navigation-compose`) retrieves the ViewModel
scoped to that particular back-stack entry. When you navigate back and the entry is removed,
the ViewModel is cleared. When you rotate the screen, the same ViewModel instance is reused.

---

## 8. Layer 5 — ViewModels (Sealed UiState + Channel)

### 8.1 The Sealed Interface Pattern

Every screen has a sealed interface for its UI state:

```kotlin
sealed interface HomeUiState {
    data object Init    : HomeUiState    // before data loading starts
    data object Loading : HomeUiState    // database query in progress
    data class  Success(                 // query completed, data ready
        val weekPlan: Map<String, DayPlan>,
        val profile: UserProfile,
        val suggestedMeals: List<Meal>,
        val completedDaysCount: Int,
        val totalWeeklyCalories: Int,
        val totalWeeklyMeals: Int
    ) : HomeUiState
    data class Error(val message: String) : HomeUiState  // something went wrong
}
```

**Why sealed interface instead of a plain data class?**
- A plain `data class HomeUiState(val isLoading: Boolean, val data: X?, val error: String?)` 
  creates impossible states: what if both `isLoading = true` AND `data != null`?
- A sealed interface makes illegal states **unrepresentable**. If the state is `Success`,
  you are guaranteed the data exists. If it is `Loading`, there is no stale data to show.
- The Kotlin compiler forces exhaustive `when` expressions — you cannot forget to handle `Error`.

### 8.2 The Channel Pattern for Navigation Events

```kotlin
private val _navEvents = Channel<HomeNavigationEvent>(Channel.BUFFERED)
val navEvents = _navEvents.receiveAsFlow()

fun onNavigateToPlanner() {
    viewModelScope.launch { _navEvents.send(HomeNavigationEvent.ToPlanner) }
}
```

**Why Channel instead of StateFlow for navigation?**
- `StateFlow` keeps its last value. If navigation to `HomeNavigationEvent.ToPlanner` was stored
  in a StateFlow, after returning from Planner → Home, the screen would read the stale
  `ToPlanner` value and immediately navigate again — an infinite loop.
- `Channel` is a one-shot queue. Each event is consumed exactly once and then gone.
- `Channel.BUFFERED` ensures events are not lost if the collector (screen) is temporarily paused.

### 8.3 ViewModel init block — Loading Data

```kotlin
init {
    viewModelScope.launch {
        _uiState.value = HomeUiState.Loading
        try {
            val suggestedMeals = mealRepository.getAllPremadeMeals().take(8)
            combine(
                dayPlanRepository.observeWeekPlan(),
                profileRepository.observe()
            ) { weekPlan, profile ->
                HomeUiState.Success(weekPlan = weekPlan, profile = profile ?: UserProfile(), ...)
            }.collect { state -> _uiState.value = state }
        } catch (e: CancellationException) { throw e }     // MUST re-throw
        catch (e: Exception) { _uiState.value = HomeUiState.Error(e.message ?: "Error") }
    }
}
```

**Why re-throw `CancellationException`?**
Coroutines use `CancellationException` to signal normal cancellation (e.g., user navigated away).
If you swallow it in a generic `catch (e: Exception)`, the coroutine never stops — it stays
alive and wastes resources. Re-throwing it lets the coroutine clean up normally.

**Why `collect` inside `init`?**
Room's `Flow` is cold and infinite — it never completes; it emits a new value every time the
database changes. `collect` suspends the coroutine and processes each emission. Since this is
in `viewModelScope`, it automatically cancels when the ViewModel is cleared (screen leaves
the back stack).

### 8.4 Form-state ViewModels (Login, SignUp, Calories, Profile)

For screens that are mostly forms (no initial DB read), the pattern is slightly simpler:

```kotlin
sealed interface LoginUiState {
    data object Init    : LoginUiState
    data object Loading : LoginUiState
    data class  Form(val email: String = "", val password: String = "",
                     val emailError: String? = null, val passwordError: String? = null) : LoginUiState
    data class  Error(val message: String) : LoginUiState
}
```

`Form` is a sub-state that holds all form fields. The ViewModel starts with `Form()` (all defaults).
When the user types, field values are updated via `copy()`. When submit is pressed:
- Validation errors go back as a new `Form` with error messages set
- On success → `Loading` → send `NavigateToMain` via Channel

### 8.5 SavedStateHandle for Deep Screens

`DayDetailViewModel`, `MealSlotViewModel`, `AddMealViewModel`, and `RecipeBuilderViewModel`
all receive route arguments:

```kotlin
@HiltViewModel
class DayDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,     // ← Hilt provides this automatically
    private val repository: DayPlanRepository
) : ViewModel() {
    val dayName: String = checkNotNull(savedStateHandle["dayName"])
```

`SavedStateHandle` is automatically populated by Navigation Compose from the route arguments
defined in `AppNavGraph.kt`. `checkNotNull` crashes early with a clear message if the argument
is missing (defensive programming — should never happen in practice).

`SavedStateHandle` also survives process death — if Android kills the app (low memory) and the
user returns, the ViewModel can rebuild from the saved arguments.

---

## 9. Layer 6 — UI Screens (Stateful / Stateless)

### 9.1 The Two-Composable Pattern

Every screen file has two composables:

**Outer (stateful)** — connects ViewModel to UI:
```kotlin
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToPlanner: () -> Unit,   // callbacks wired by AppNavGraph
    ...
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Handle one-shot navigation events
    LaunchedEffect(Unit) {
        viewModel.navEvents.collect { event ->
            when (event) {
                HomeNavigationEvent.ToPlanner -> onNavigateToPlanner()
                ...
            }
        }
    }

    // Route to the right UI based on state
    when (val s = uiState) {
        HomeUiState.Init, HomeUiState.Loading -> CircularProgressIndicator()
        is HomeUiState.Error   -> Text(s.message)
        is HomeUiState.Success -> HomeScreenContent(state = s, ...)
    }
}
```

**Inner (stateless)** — pure UI, no ViewModel references:
```kotlin
@Composable
fun HomeScreenContent(
    state: HomeUiState.Success,
    onNavigateToPlanner: () -> Unit,
    ...
) {
    // All the actual UI — Scaffold, LazyColumn, etc.
    // Receives DATA as parameters, fires CALLBACKS when user interacts
    // No collectAsState(), no LaunchedEffect for nav, no ViewModel
}
```

**Why this split?**
- **Testability** — `HomeScreenContent` can be tested in isolation with fake data
- **Previews** — `@Preview` works on stateless composables without a real ViewModel
- **Separation of concerns** — data wiring vs. rendering are clearly separated

### 9.2 collectAsStateWithLifecycle() vs collectAsState()

`collectAsState()` collects the Flow even when the app is in the background (screen off, Home pressed).
`collectAsStateWithLifecycle()` automatically **pauses** collection when the lifecycle is below
`Lifecycle.State.STARTED` (i.e., when the app is backgrounded). This saves battery and CPU.

Requires: `androidx.lifecycle:lifecycle-runtime-compose` (added to `build.gradle.kts`).

### 9.3 LaunchedEffect(Unit) for Navigation

```kotlin
LaunchedEffect(Unit) {
    viewModel.navEvents.collect { event -> ... }
}
```

`LaunchedEffect(Unit)` runs once when the composable enters the composition and cancels
when it leaves. `Unit` as the key means it never restarts (unlike `LaunchedEffect(someState)`
which restarts every time `someState` changes). This is the correct pattern for collecting
a Channel that should be alive for the screen's lifetime.

---

## 10. Layer 7 — Navigation

### 10.1 NavRoutes

**Location:** `presentation/navigation/NavRoutes.kt`

```kotlin
const val DAY_DETAIL = "day_detail/{dayName}"         // route template
fun dayDetail(dayName: String) = "day_detail/$dayName" // route builder
```

Deep screens use `{placeholder}` syntax. Arguments are declared as `navArgument` in
`AppNavGraph.kt` and extracted via `SavedStateHandle` in ViewModels.

### 10.2 Nested Graphs

The NavHost has two nested navigation graphs:
- `AUTH_GRAPH` — Splash → Login → SignUp (no bottom nav bar)
- `MAIN_GRAPH` — Home, Planner, Calories, Profile + deep screens (with bottom nav bar)

When login succeeds, the app navigates to `MAIN_GRAPH` and pops the entire `AUTH_GRAPH`
off the back stack (`inclusive = true`). This means pressing Back from Home exits the app
rather than returning to Login.

### 10.3 Bottom Navigation Visibility

`BottomNavBar.kt` uses `navController.currentBackStackEntryAsState()` and checks if
the current route is in `NavRoutes.bottomNavRoutes`. The bar hides automatically on
DayDetail, MealSlot, AddMeal, RecipeBuilder screens.

---

## 11. Complete Data Flow: End-to-End Example

**Scenario: User adds "Grilled Chicken" to Monday's Breakfast**

```
1. USER taps "Grilled Chicken" in AddMealScreen
   ↓
2. AddMealScreen calls viewModel::addPremadeMeal(meal)
   ↓
3. AddMealViewModel.addPremadeMeal(meal) {
       viewModelScope.launch {
           dayPlanRepository.addMealToSlot("Monday", "Breakfast", meal)  // suspend call
           _navEvents.send(AddMealNavigationEvent.GoBack)
       }
   }
   ↓
4. DayPlanRepositoryImpl.addMealToSlot(...) {
       val dayEntity = dayPlanDao.getByName("Monday")    // SELECT from SQLite
       val mealDbId = mealDao.getPremade()
           .first { it.name == "Grilled Chicken Breast" }.id
       dayPlanDao.insertCrossRef(
           DayMealCrossRef(dayPlanId = dayEntity.id, mealId = mealDbId, slotType = "Breakfast")
       )                                                  // INSERT into SQLite
   }
   ↓
5. Room detects change in day_meal_cross_ref table
   ↓
6. observeAllMealsWithSlots() Flow emits a new List<MealWithSlot>
   ↓
7. combine() in DayPlanRepositoryImpl fires its lambda
   → rebuilds Map<String, DayPlan> with the new meal in Monday's breakfast
   ↓
8. HomeViewModel.init collects the Flow → HomeUiState.Success(...) updated
   MealPlannerViewModel.init collects → MealPlannerUiState.Success(...) updated
   DayDetailViewModel.init collects → DayDetailUiState.Success(...) updated
   (all happen automatically, all screens recompose)
   ↓
9. _navEvents.send(GoBack) → AddMealScreen LaunchedEffect collects it → calls onBack()
   ↓
10. NavController.navigateUp() → MealSlotScreen shown, meal list already updated
```

---

## 12. Key Design Decisions (Why, Not Just What)

### Why not use MySQL / a remote server?
Room uses SQLite which is built into Android. No internet connection required, no server costs,
no authentication to a remote database. For a mobile-first app, local storage is the right choice.
A future assignment could add a backend API on top.

### Why MutableStateFlow + asStateFlow()?
`MutableStateFlow` is the private mutable version. `asStateFlow()` exposes a read-only view.
Screens can only **observe** state — they cannot set it directly. All state changes go through
ViewModel functions, which gives a single source of truth and makes debugging predictable.

### Why `@Singleton` for repositories?
Multiple screens (Home, MealPlanner, DayDetail) all show week plan data. If each had its own
repository instance with its own database connection, they would not share state. Singleton ensures
every screen sees the same data and that database writes are immediately visible everywhere.

### Why Repository interface instead of using the Impl directly?
When a ViewModel declares `private val repository: DayPlanRepository` (the interface), it does not
know or care whether data comes from Room, a network API, or a fake in-memory store. This makes
the ViewModel easy to unit test by injecting a `FakeDayPlanRepository` that doesn't need a database.

### Why KSP instead of KAPT?
KSP (Kotlin Symbol Processing) runs at compile time and is significantly faster than KAPT
(Kotlin Annotation Processing Tool). Both Hilt and Room support KSP. Faster builds mean
faster development cycle.

### Why sealed interface instead of sealed class?
`sealed interface` is more flexible — a class can implement multiple sealed interfaces.
`sealed class` locks the hierarchy to one parent. For UiState, interface is preferred because
it uses `data object` and `data class` variants cleanly without redundant constructors.

---

## 13. Defence Q&A

**Q: What is MVVM?**
Model-View-ViewModel. The Model (Room + Repositories) holds data. The ViewModel transforms it
into UI-ready state and exposes it via StateFlow. The View (Composable screens) observes the
StateFlow and re-renders when it changes. The View never writes to the Model directly — it calls
ViewModel functions which delegate to repositories.

**Q: What is a StateFlow?**
A `StateFlow<T>` is a hot observable stream that always holds the current value and emits it to
new collectors immediately. It is like LiveData but for coroutines. `MutableStateFlow` has a
`value` property you can set. Screens use `collectAsStateWithLifecycle()` to read it as Compose
state, which triggers recomposition when the value changes.

**Q: What is the difference between Flow and StateFlow?**
A `Flow` is cold — it only starts producing values when someone collects it, and each collector
gets its own independent stream. A `StateFlow` is hot — it runs regardless of collectors and
replays the last value to any new collector. Room DAOs return cold Flows; ViewModels convert them
to hot StateFlows using `stateIn()` or by collecting inside `viewModelScope.launch`.

**Q: What is coroutine scope and why viewModelScope?**
`viewModelScope` is a `CoroutineScope` tied to the ViewModel's lifecycle. When the ViewModel is
cleared (user navigates permanently away), all coroutines launched in `viewModelScope` are
automatically cancelled. This prevents memory leaks and database access after the screen is gone.

**Q: What is Dispatchers.IO and why does the repository use it (implicitly)?**
`Dispatchers.IO` is a thread pool designed for blocking I/O operations like reading from a database
or network. Room operations must not run on the main thread (they would freeze the UI). Room's Kotlin
extensions automatically run on a background thread — you only need to mark functions as `suspend`
and Room handles the dispatcher internally.

**Q: What is a DAO?**
Data Access Object. It is an interface annotated with `@Dao` that declares the database operations
as Kotlin functions. Room generates the actual SQL implementation at compile time. You never write
SQL statements manually in the app code — they are written as `@Query("SELECT ...")` annotations.

**Q: What is a junction table and why does the app need one?**
A junction table (also called a cross-reference or associative table) implements a many-to-many
relationship. A single meal (e.g., "Grilled Chicken") can appear in multiple days and multiple slots.
A single day can have multiple meals. Rather than duplicating meal rows, `DayMealCrossRef` stores
pairs of (day_plan_id, meal_id, slot_type) — one row per assignment.

**Q: What is the difference between @Insert and @Query in Room?**
`@Insert` is a pre-built operation — Room generates the INSERT SQL for you. You just pass the entity.
`@Query` lets you write custom SQL for complex operations like JOINs, WHERE filters, or aggregates.

**Q: Why does observeWeekPlan() use combine()?**
The week plan data lives in two tables: `day_plans` (the structure) and `day_meal_cross_ref` joined
with `meals` (the assignments). Each table has its own reactive Flow. `combine()` merges them: when
either Flow emits, the lambda runs with the latest value from both, rebuilding the complete
`Map<String, DayPlan>`. Without `combine()`, you would need to query both tables every time and
manually correlate the results.

**Q: What is the Channel used for navigation?**
`Channel<T>` is a coroutine primitive that acts as a one-shot queue. When the ViewModel sends a
navigation event (e.g., `NavigateToMain`), it goes into the queue. The screen's `LaunchedEffect`
collects it and triggers the nav callback exactly once. Using `StateFlow` instead would replay the
last navigation event on every recomposition, causing repeated navigations.

**Q: Why is SeedCallback using Provider<AppDatabase> instead of AppDatabase directly?**
Hilt creates `AppDatabase` using `SeedCallback`. If `SeedCallback` also required `AppDatabase`
in its constructor, Hilt would enter an infinite creation loop (circular dependency). `Provider<T>`
delays the retrieval of `AppDatabase` until `databaseProvider.get()` is called inside `onCreate()`,
by which time Hilt has already finished creating the database instance.

**Q: What happens when a user clears the app data?**
The SQLite file is deleted. On next launch, Room creates a fresh database. `SeedCallback.onCreate()`
fires and re-seeds all 25 meals, 7 day plans, 55 ingredients, and the default user profile.

**Q: What is `fallbackToDestructiveMigration`?**
When the Room database schema changes (e.g., a new column added), Room needs a `Migration` object
to upgrade the existing database. During development, `fallbackToDestructiveMigration(true)` tells
Room to simply drop and recreate the database if no migration is provided. This is fine during
development but should be replaced with proper migrations in a production app.

**Q: Can two screens see the same data at the same time?**
Yes. Because `DayPlanRepository` is a `@Singleton`, `observeWeekPlan()` returns a Flow backed by
the same SQLite database. Both `HomeViewModel` and `MealPlannerViewModel` collect from this Flow.
When a meal is added via `AddMealViewModel`, Room emits to the Flow, and both screens recompose
simultaneously with the updated data.

**Q: What is Hilt and how is it different from manual dependency injection?**
Without DI, you would write `val repo = DayPlanRepositoryImpl(DayPlanDao(...), MealDao(...))` in
every class that needs it — duplicated, error-prone, and impossible to unit test. Hilt builds a
dependency graph at compile time. You declare what you need in a constructor (`@Inject constructor`)
and declare how to build it in a `@Module`. Hilt wires everything together automatically.

**Q: Why does the app use a single Activity?**
Jetpack Compose + Navigation Compose is designed for single-activity apps. The `NavHost` manages
the back stack of screens entirely in Compose — there are no Fragment transactions. Multiple
activities would complicate navigation and data sharing between screens.
