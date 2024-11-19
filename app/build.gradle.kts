plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("kotlin-kapt") // KAPT plugin for annotation processing
}

android {
    namespace = "com.bgwebviewtest.pirtest"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.bgwebviewtest.pirtest"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            isDebuggable = true
            buildConfigField("Boolean", "ENABLE_PROFILER", "true")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    val roomVersion = "2.5.2" // Make sure to check for the latest version

    implementation("androidx.room:room-runtime:$roomVersion")
    kapt("androidx.room:room-compiler:$roomVersion") // KAPT for annotation processing

    // Optional - Room with Kotlin Extensions and Coroutines
    implementation("androidx.room:room-ktx:$roomVersion")

    // Other dependencies
}