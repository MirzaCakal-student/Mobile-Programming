package com.example.mealplanner.model

/**
 * Hard-coded sample data for Assignment 1.
 * Will be replaced by a Room DB in later assignments.
 */
object SampleData {

    // ── 7 days of the week ───────────────────────────────────
    val weekDays = listOf(
        "Monday", "Tuesday", "Wednesday",
        "Thursday", "Friday", "Saturday", "Sunday"
    )

    // ── 25 pre-made meals ────────────────────────────────────
    val premadeMeals: List<Meal> = listOf(
        Meal(1,  "Grilled Chicken Breast",    165, 31.0, 3.6,  0.0),
        Meal(2,  "Oatmeal with Berries",      300, 10.0, 5.0, 54.0),
        Meal(3,  "Greek Yogurt Parfait",      220, 15.0, 3.0, 32.0),
        Meal(4,  "Avocado Toast",             290, 9.0, 15.0,  28.0),
        Meal(5,  "Scrambled Eggs (3 eggs)",   210, 18.0, 14.0,  2.0),
        Meal(6,  "Caesar Salad",              350, 12.0, 26.0, 14.0),
        Meal(7,  "Tuna Wrap",                 420, 30.0, 10.0, 50.0),
        Meal(8,  "Lentil Soup",               230, 18.0,  2.0, 38.0),
        Meal(9,  "Grilled Salmon",            280, 34.0, 14.0,  0.0),
        Meal(10, "Brown Rice & Veggies",      310,  8.0,  3.0, 64.0),
        Meal(11, "Beef Stir-Fry",             450, 35.0, 18.0, 32.0),
        Meal(12, "Pasta Bolognese",           520, 28.0, 14.0, 68.0),
        Meal(13, "Grilled Veggie Wrap",       340, 10.0,  8.0, 52.0),
        Meal(14, "Chicken Noodle Soup",       250, 20.0,  6.0, 28.0),
        Meal(15, "Turkey Sandwich",           390, 28.0, 10.0, 46.0),
        Meal(16, "Baked Potato with Tuna",    400, 32.0,  4.0, 58.0),
        Meal(17, "Smoothie Bowl",             280,  8.0,  5.0, 52.0),
        Meal(18, "Cottage Cheese & Fruit",    190, 22.0,  3.0, 18.0),
        Meal(19, "Protein Pancakes",          360, 28.0,  6.0, 48.0),
        Meal(20, "Quinoa Bowl",               380, 16.0,  9.0, 60.0),
        Meal(21, "Chicken Shawarma Wrap",     480, 32.0, 16.0, 48.0),
        Meal(22, "Veggie Omelette",           220, 16.0, 14.0,  4.0),
        Meal(23, "Peanut Butter Banana Toast",340, 10.0, 14.0, 46.0),
        Meal(24, "Baked Chicken Thighs",      390, 34.0, 24.0,  0.0),
        Meal(25, "Mixed Nuts (30g) + Apple",  240,  6.0, 16.0, 24.0)
    )

    // ── 55 ingredients ───────────────────────────────────────
    val ingredients: List<Ingredient> = listOf(
        Ingredient(1,  "Chicken Breast",     165, 31.0, 3.6,  0.0),
        Ingredient(2,  "Salmon Fillet",      208, 20.0, 13.0, 0.0),
        Ingredient(3,  "Tuna (canned)",      116, 25.6, 0.9,  0.0),
        Ingredient(4,  "Ground Beef (lean)", 215, 26.0, 12.0, 0.0),
        Ingredient(5,  "Turkey Breast",      135, 29.0, 1.5,  0.0),
        Ingredient(6,  "Eggs",               155, 13.0, 11.0, 1.1),
        Ingredient(7,  "Greek Yogurt",        59, 10.0, 0.4,  3.6),
        Ingredient(8,  "Milk (whole)",        61,  3.2, 3.3,  4.8),
        Ingredient(9,  "Cheddar Cheese",     402, 25.0, 33.0, 1.3),
        Ingredient(10, "Cottage Cheese",      98, 11.0, 4.3,  3.4),
        Ingredient(11, "Oats (rolled)",      389, 16.9, 6.9, 66.3),
        Ingredient(12, "Brown Rice",         216,  5.0, 1.8, 44.8),
        Ingredient(13, "White Rice",         130,  2.7, 0.3, 28.2),
        Ingredient(14, "Quinoa",             120,  4.4, 1.9, 21.3),
        Ingredient(15, "Whole Wheat Bread",  247,  8.7, 3.4, 41.0),
        Ingredient(16, "Pasta (dry)",        371, 13.0, 1.5, 74.7),
        Ingredient(17, "Sweet Potato",        86,  1.6, 0.1, 20.1),
        Ingredient(18, "Potato",              77,  2.0, 0.1, 17.5),
        Ingredient(19, "Avocado",            160,  2.0, 14.7, 8.5),
        Ingredient(20, "Banana",              89,  1.1, 0.3, 22.8),
        Ingredient(21, "Apple",               52,  0.3, 0.2, 13.8),
        Ingredient(22, "Blueberries",         57,  0.7, 0.3, 14.5),
        Ingredient(23, "Strawberries",        32,  0.7, 0.3,  7.7),
        Ingredient(24, "Spinach",             23,  2.9, 0.4,  3.6),
        Ingredient(25, "Broccoli",            34,  2.8, 0.4,  6.6),
        Ingredient(26, "Carrots",             41,  0.9, 0.2,  9.6),
        Ingredient(27, "Bell Pepper",         31,  1.0, 0.3,  6.0),
        Ingredient(28, "Tomato",              18,  0.9, 0.2,  3.9),
        Ingredient(29, "Cucumber",            15,  0.7, 0.1,  3.6),
        Ingredient(30, "Lettuce (romaine)",   17,  1.2, 0.3,  3.3),
        Ingredient(31, "Onion",               40,  1.1, 0.1,  9.3),
        Ingredient(32, "Garlic",             149,  6.4, 0.5, 33.1),
        Ingredient(33, "Olive Oil",          884,  0.0,100.0,  0.0),
        Ingredient(34, "Butter",             717,  0.9, 81.1,  0.1),
        Ingredient(35, "Peanut Butter",      588, 22.5, 50.4, 20.1),
        Ingredient(36, "Almonds",            579, 21.2, 49.9, 21.6),
        Ingredient(37, "Walnuts",            654, 15.2, 65.2, 13.7),
        Ingredient(38, "Lentils (boiled)",   116,  9.0, 0.4, 20.1),
        Ingredient(39, "Chickpeas (boiled)", 164,  8.9, 2.6, 27.4),
        Ingredient(40, "Black Beans",        132,  8.9, 0.5, 23.7),
        Ingredient(41, "Protein Powder",     360, 72.0, 6.0, 16.0),
        Ingredient(42, "Honey",              304,  0.3, 0.0, 82.4),
        Ingredient(43, "Maple Syrup",        260,  0.0, 0.1, 67.0),
        Ingredient(44, "Soy Sauce",           53,  5.6, 0.1,  4.9),
        Ingredient(45, "Ketchup",             97,  1.4, 0.2, 25.9),
        Ingredient(46, "Mayonnaise",          680,  1.0, 75.0,  0.6),
        Ingredient(47, "Hummus",             166,  7.9, 9.6, 14.3),
        Ingredient(48, "Orange Juice",        45,  0.7, 0.2, 10.4),
        Ingredient(49, "Whole Milk Yogurt",   61,  3.5, 3.3,  4.7),
        Ingredient(50, "Canned Tomatoes",     32,  1.5, 0.3,  5.5),
        Ingredient(51, "Mushrooms",           22,  3.1, 0.3,  3.3),
        Ingredient(52, "Corn",               365,  9.4, 4.7, 74.3),
        Ingredient(53, "Zucchini",            17,  1.2, 0.3,  3.1),
        Ingredient(54, "Celery",              16,  0.7, 0.2,  3.0),
        Ingredient(55, "Feta Cheese",        264, 14.2, 21.3,  4.1)
    )

    /** Build a fresh week plan (all days empty). */
    fun buildEmptyWeekPlan(): Map<String, DayPlan> =
        weekDays.associateWith { DayPlan(dayName = it) }
}