plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

android {
    compileSdk = 34
    namespace = "com.example.music.domain"

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
                "proguard-rules.pro")
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
}

dependencies {
    //Internal Project Modules
    implementation(projects.core.data)
    implementation(projects.core.dataTesting)

    // Dependency injection
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.dagger.hilt.android)
    implementation(libs.androidx.activity.compose)
    ksp(libs.dagger.hilt.compiler)

    // Logging
    implementation(libs.slf4j.log)

    // Media3 Controls
    implementation(libs.androidx.media3.ui)
    implementation(libs.androidx.media3.common)
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.session)
    implementation(libs.nextlib)
    implementation(libs.mp3agic)

    // Backwards Compatibility for older APIs to new App versions
    coreLibraryDesugaring(libs.core.jdk.desugaring)

    // Testing
    //testImplementation(libs.junit)
    //testImplementation(libs.kotlinx.coroutines.test)
}
