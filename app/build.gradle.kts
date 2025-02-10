plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.example.music"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.music"
        minSdk = 34
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        // Important: change the keystore for a production deployment
        val userKeystore = File(System.getProperty("user.home"), ".android/debug.keystore")
        val localKeystore = rootProject.file("debug_2.keystore")
        val hasKeyInfo = userKeystore.exists()
        create("release") {
            // get from env variables
            storeFile = if (hasKeyInfo) userKeystore else localKeystore
            storePassword = if (hasKeyInfo) "android" else System.getenv("compose_store_password")
            keyAlias = if (hasKeyInfo) "androiddebugkey" else System.getenv("compose_key_alias")
            keyPassword = if (hasKeyInfo) "android" else System.getenv("compose_key_password")
        }
    }

    buildTypes {
        debug {
            signingConfig = signingConfigs.getByName("debug")
        }
        release {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
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

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // Internal Project Modules
    implementation(projects.core.data)
    implementation(projects.core.dataTesting)
    implementation(projects.core.designsys)
    implementation(projects.core.domain)
    implementation(projects.core.domainTesting)
    implementation(projects.glancewidget)

    // Kotlin Support
    implementation(libs.androidx.core.ktx)
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.collections.immutable)

    // Compose
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.material)
    implementation(libs.androidx.compose.material.icons.core)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material3.adaptive)
    implementation(libs.androidx.compose.material3.adaptive.layout)
    implementation(libs.androidx.compose.material3.adaptive.navigation)
    implementation(libs.androidx.compose.material3Window)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    debugImplementation(libs.androidx.ui.tooling)

    // Adaptive Layout Support for Compose
    implementation(libs.accompanist.adaptive)

    // Image Color Palette
    implementation(libs.androidx.palette)

    // Window Manager
    implementation(libs.androidx.window)
    implementation(libs.androidx.window.core)

    // Dependency Injection
    implementation(libs.dagger.hilt.android)
    ksp(libs.dagger.hilt.compiler)

    // Navigation - Lifecycle Support
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewModelCompose)
    implementation(libs.androidx.navigation.compose)

    // Image Loading
    implementation(libs.coil.kt.compose)
    implementation(libs.coil3.kt.compose)

    // DataStore Support
    implementation(libs.androidx.datastore)

    // Logging
    implementation(libs.kotlin.logging)
    implementation(libs.slf4j.log)

    //Media3 Controls
    implementation(libs.androidx.media3.ui)
    implementation(libs.androidx.media3.cast)// For integrating with Cast
    implementation(libs.androidx.media3.common)
    implementation(libs.androidx.media3.exoplayer)
//    implementation(libs.androidx.media3.exoplayer.midi)
//    implementation(libs.androidx.media3.extractor)// For extracting data from media containers
    implementation(libs.androidx.media3.session)// For exposing and controlling media sessions
//    implementation(libs.androidx.media3.test.utils)// Utilities for testing media components (including ExoPlayer components)
//    implementation(libs.androidx.media3.transformer)// For transforming media files

    // Backwards Compatibility for older APIs to new App versions
    coreLibraryDesugaring(libs.core.jdk.desugaring)

    // Backwards Compatibility for older App versions to new APIs
    implementation(libs.androidx.appcompat)

    // Testing
    androidTestImplementation(libs.androidx.test.espresso.core)
    androidTestImplementation(libs.androidx.ui.test.junit4)
    androidTestImplementation(libs.dagger.hilt.android.testing)
    debugImplementation(libs.androidx.ui.test.manifest)
    testImplementation(libs.dagger.hilt.android.testing)
    testImplementation(libs.junit)
}