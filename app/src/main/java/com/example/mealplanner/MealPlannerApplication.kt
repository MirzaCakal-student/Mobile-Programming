package com.example.mealplanner

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Application entry point for Dagger Hilt.
 *
 * The @HiltAndroidApp annotation triggers Hilt's code generation and creates the
 * application-level dependency container. Every other Hilt component in the app
 * is a child of this component.
 *
 * Registered in AndroidManifest.xml via android:name=".MealPlannerApplication".
 */
@HiltAndroidApp
class MealPlannerApplication : Application()
