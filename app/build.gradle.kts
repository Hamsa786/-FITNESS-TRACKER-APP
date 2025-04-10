plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.kapt")
}

android {
    namespace = "com.example.fitnesstracker"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.fitnesstracker"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
        viewBinding = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.8" // Updated to match Kotlin 1.9.22
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

// Use a more permissive resolution strategy that prefers the newest versions
configurations.all {
    resolutionStrategy {
        // Prefer the highest versions for all dependencies rather than failing
        eachDependency {
            if (requested.group == "androidx.core" && requested.name == "core-ktx") {
                useVersion("1.12.0")
            }
            if (requested.group == "androidx.appcompat" && requested.name == "appcompat") {
                useVersion("1.6.1")
            }
            if (requested.group == "androidx.constraintlayout" && requested.name == "constraintlayout") {
                useVersion("2.1.4")
            }
            if (requested.group == "androidx.lifecycle") {
                useVersion("2.7.0")
            }
            if (requested.group == "androidx.activity" && requested.name == "activity-compose") {
                useVersion("1.8.2")
            }
            if (requested.group == "androidx.compose.ui") {
                useVersion("1.6.0")
            }
            if (requested.group == "org.jetbrains.kotlinx" && requested.name == "kotlinx-coroutines-android") {
                useVersion("1.7.3")
            }
            if (requested.group == "androidx.core" && requested.name == "core") {
                useVersion("1.12.0")
            }
            if (requested.group == "androidx.fragment" && requested.name == "fragment") {
                useVersion("1.6.2")
            }
        }

        // Force versions for critical dependencies to resolve conflicts
        force("androidx.core:core-ktx:1.12.0")
        force("androidx.appcompat:appcompat:1.6.1")
        force("androidx.compose.ui:ui:1.6.0")
        force("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    }
}

dependencies {
    // Core Android dependencies
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // Lifecycle components
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")

    // Room Database
    val roomVersion = "2.6.1"
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    kapt("androidx.room:room-compiler:$roomVersion")

    // Compose
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation("androidx.compose.ui:ui:1.6.0")
    implementation("androidx.compose.material3:material3:1.2.0")
    implementation("androidx.compose.ui:ui-tooling-preview:1.6.0")
    debugImplementation("androidx.compose.ui:ui-tooling:1.6.0")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // MPAndroidChart
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
}