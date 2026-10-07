import java.util.Properties

plugins {
    id("trosko.android.application")
    id("trosko.android.compose")
    id("trosko.kotlin.serialization")
}

// Release signing credentials live in an untracked file, see keystore.properties.example.
// When the file is absent (fresh clone, CI) the release build is simply left unsigned.
val keystorePropertiesFile = rootProject.file("keystore.properties")
val keystoreProperties =
    Properties().apply {
        if (keystorePropertiesFile.exists()) {
            keystorePropertiesFile.inputStream().use(::load)
        }
    }

android {
    namespace = "dev.cirimo.trosko"

    defaultConfig {
        applicationId = "dev.cirimo.trosko"
        versionCode = 1
        versionName = "0.1.0"
    }

    androidResources {
        // Generates the locale config from the values-* folders, which gives users
        // the per-app language picker in system settings on Android 13 and newer.
        generateLocaleConfig = true
        // Drops the dozens of translations that libraries ship for languages we do not support.
        // Add a language here when its values-* folder is added.
        localeFilters += listOf("en", "hr")
    }

    signingConfigs {
        if (keystorePropertiesFile.exists()) {
            create("release") {
                storeFile = rootProject.file(keystoreProperties.getProperty("storeFile"))
                storePassword = keystoreProperties.getProperty("storePassword")
                keyAlias = keystoreProperties.getProperty("keyAlias")
                keyPassword = keystoreProperties.getProperty("keyPassword")
            }
        }
    }

    buildTypes {
        debug {
            // Lets a debug build sit next to a release build on the same phone.
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.findByName("release")
        }
    }
}

dependencies {
    implementation(project(":core:data"))
    implementation(project(":core:designsystem"))

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.navigation3.ui)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)

    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.test.runner)
}
