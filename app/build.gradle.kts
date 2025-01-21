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
//    compileOptions {//og music attempt compile versions
//        isCoreLibraryDesugaringEnabled = true
//        sourceCompatibility = JavaVersion.VERSION_11
//        targetCompatibility = JavaVersion.VERSION_11
//    } //switching between 11 and 17 to test ksp, kotlin, hilt versioning support

    compileOptions { //jetcaster compile versions
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions { //og music attempt kotlinOptions
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}
//kotlin { //jetcaster kotlin versions
//    jvmToolchain(17)
//}

dependencies {

    implementation(projects.core.data)
    implementation(projects.core.dataTesting)
    implementation(projects.core.designsys)
    implementation(projects.core.domain)
    implementation(projects.core.domainTesting)
    implementation(projects.glancewidget)

    implementation(platform(libs.androidx.compose.bom))
    testImplementation(libs.junit.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))

    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.collections.immutable)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.palette)

    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.material.icons.core)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material3.adaptive)
    implementation(libs.androidx.compose.material3.adaptive.layout)
    implementation(libs.androidx.compose.material3.adaptive.navigation)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.tooling.preview)
    debugImplementation(libs.androidx.ui.tooling)
    implementation(libs.kotlin.logging)
    implementation(libs.slf4j.log)

    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewModelCompose)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.navigation.compose)

    implementation(libs.androidx.window)
    implementation(libs.androidx.window.core)

    implementation(libs.accompanist.adaptive)
    implementation(libs.coil.kt.compose)
    implementation(libs.coil3.kt.compose)

    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.compose.material)
    implementation(libs.androidx.compose.material3Window)
    implementation(libs.androidx.appcompat)

    implementation(libs.androidx.media3.ui)
    implementation(libs.androidx.media3.cast)// For integrating with Cast
    implementation(libs.androidx.media3.common)
//    implementation(libs.androidx.media3.exoplayer)
//    implementation(libs.androidx.media3.exoplayer.midi)
//    implementation(libs.androidx.media3.extractor)// For extracting data from media containers
    implementation(libs.androidx.media3.session)//For exposing and controlling media sessions
//    implementation(libs.androidx.media3.test.utils)// Utilities for testing media components (including ExoPlayer components)
//    implementation(libs.androidx.media3.transformer)// For transforming media files

    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.test.manifest)

    coreLibraryDesugaring(libs.core.jdk.desugaring)

}