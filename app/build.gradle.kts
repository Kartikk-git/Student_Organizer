plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.example.studentorganizer"
    compileSdk {
        version = release(37) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.example.studentorganizer"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            // Enables R8 minification to drastically reduce app size and obfuscate code
            isMinifyEnabled = true
            // Removes unused resources from the final APK
            isShrinkResources = true
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
        viewBinding = true
        compose = true
    }
}

dependencies {
    // Jetpack Compose BOM: Ensures all compose libraries use compatible versions
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.foundation)

    // Core Compose UI components and graphics
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    // Compose Preview tooling for Android Studio
    implementation(libs.androidx.compose.ui.tooling.preview)
    // Material Design 3 UI components for Compose
    implementation(libs.androidx.compose.material3)
    // Extended Material Icons for UI elements
    implementation(libs.androidx.compose.material.icons.extended)
    // Integration between Compose and Activity (setContent)
    implementation(libs.androidx.activity.compose)
    // Lifecycle components for Compose (collectAsStateWithLifecycle)
    implementation(libs.androidx.lifecycle.runtime.compose)
    // ViewModel integration for Compose
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // Tooling for debugging Compose UI (layout inspector)
    debugImplementation(libs.androidx.compose.ui.tooling)

    // AndroidX Core extensions and Activity KTX for simpler API usage
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.core.ktx)
    // Appcompat for backwards compatibility of older Views/Themes
    implementation(libs.androidx.appcompat)
    // ConstraintLayout for complex View-based layouts (if any are still used)
    implementation(libs.androidx.constraintlayout)
    // Google Material Design components for Views
    implementation(libs.material)
    
    // Core SplashScreen API for Android 12+ splash screen backwards compatibility
    implementation("androidx.core:core-splashscreen:1.2.0")
    // ViewPager2 for swipeable page views
    implementation("androidx.viewpager2:viewpager2:1.1.0")

    // Room Database: Local SQLite database ORM for data persistence
    val room_version = "2.8.4"
    implementation("androidx.room:room-runtime:$room_version") // Core Room library
    implementation("androidx.room:room-ktx:$room_version") // Kotlin extensions and Coroutines support for Room
    ksp("androidx.room:room-compiler:$room_version") // Annotation processor to generate database code

    // Testing frameworks
    testImplementation(libs.junit) // Local unit tests
    androidTestImplementation(libs.androidx.espresso.core) // UI testing
    androidTestImplementation(libs.androidx.junit) // Android instrumentation tests
}