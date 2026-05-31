// ============================================================
// FILE: app/build.gradle.kts
// ============================================================
import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.google.services)      // reads google-services.json → generates Firebase config
}

// ── Load secrets from local.properties (gitignored) ──────────────────────────
// We never hardcode API keys in source — they live in local.properties on the
// developer's machine, and Gradle injects them at build time as BuildConfig fields.
val localProperties = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) FileInputStream(file).use { load(it) }
}
val openWeatherKey: String = localProperties.getProperty("OPENWEATHER_KEY", "")

android {
    namespace = "com.example.mealplanner"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.mealplanner"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Expose the OpenWeather API key to Kotlin code as BuildConfig.OPENWEATHER_KEY.
        // If local.properties is missing the key, the value is empty — the app still
        // compiles but the Weather screen will surface an error message at runtime.
        buildConfigField("String", "OPENWEATHER_KEY", "\"$openWeatherKey\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
        buildConfig = true   // required so buildConfigField generates the BuildConfig class
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    // Navigation Compose
    implementation("androidx.navigation:navigation-compose:2.7.7")

    // ViewModel Compose
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")

    // ViewModel KTX — provides viewModelScope for derived StateFlows
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")

    // Icons Extended
    implementation("androidx.compose.material:material-icons-extended:1.6.3")

    // Hilt — dependency injection
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    // hiltViewModel() helper for Compose navigation
    implementation(libs.hilt.navigation.compose)

    // Room — local database
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    // Retrofit — network layer (REST API, Lab 11)
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)

    // Coil — async image loading for weather icons from openweathermap.org/img/wn/
    implementation(libs.coil.compose)

    // Firebase — Authentication + Firestore (Lab 12)
    // BoM aligns versions; individual libraries inherit the BoM version.
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth.ktx)
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.kotlinx.coroutines.play.services)   // .await() on Firebase Tasks

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}