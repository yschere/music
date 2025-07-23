plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.example.music.data"
    compileSdk = 34

    defaultConfig {
        minSdk = 34

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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

    buildFeatures {
        buildConfig = true
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    // Kotlin Support
    implementation(libs.androidx.core.ktx)

    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.media3.common)
    implementation(libs.androidx.activity.compose)
    androidTestImplementation(platform(libs.androidx.compose.bom))

    // Dependency injection
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.dagger.hilt.android)
    implementation(libs.transport.api)
    ksp(libs.dagger.hilt.compiler)

    // Image Loading
    implementation(libs.coil.kt.compose)
    implementation(libs.coil3.kt.compose)

    // Networking
    implementation(libs.okhttp3)
    implementation(libs.okhttp.logging)
    implementation(libs.rometools.rome)
    //implementation(libs.rometools.modules)

    // Database
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.datastore) // preferences datastore support

    // Logging
    implementation(libs.kotlin.logging)
    implementation(libs.slf4j.log)

    // Backwards Compatibility for older APIs to new App versions
    coreLibraryDesugaring(libs.core.jdk.desugaring)

    // Backwards Compatibility for older App versions to new APIs
    implementation(libs.androidx.appcompat)

    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
}
