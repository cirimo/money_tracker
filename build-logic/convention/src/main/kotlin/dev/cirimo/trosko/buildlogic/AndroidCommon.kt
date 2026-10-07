package dev.cirimo.trosko.buildlogic

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask

/**
 * What every Android module shares, application or library: SDK levels, Java level, Lint policy,
 * Kotlin compiler strictness, and compiling the instrumented tests as part of `check`.
 */
internal fun Project.configureAndroidCommon(extension: CommonExtension) {
    extension.apply {
        compileSdk {
            version =
                release(libs.version("compileSdk").toInt()) {
                    minorApiLevel = libs.version("compileSdkMinor").toInt()
                }
        }
        buildToolsVersion = libs.version("buildTools")

        defaultConfig.apply {
            minSdk = libs.version("minSdk").toInt()
            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }

        compileOptions.apply {
            sourceCompatibility = JavaVersion.VERSION_17
            targetCompatibility = JavaVersion.VERSION_17
        }

        lint.apply {
            // A warning that is allowed to linger gets ignored, so every warning fails the build.
            // Fix the cause, or suppress it at the narrowest scope with a comment saying why.
            warningsAsErrors = true
            abortOnError = true
            checkDependencies = true
            // These fire whenever a newer library or plugin version is published, which would
            // break CI on commits that changed nothing. Upgrades are done deliberately instead.
            disable += setOf("GradleDependency", "NewerVersionAvailable", "AndroidGradlePluginVersion")
        }
    }

    tasks.withType<KotlinCompilationTask<*>>().configureEach {
        compilerOptions.allWarningsAsErrors.set(true)
    }

    // `check` cannot run instrumented tests (they need a device), but it must at least
    // compile them, otherwise they rot unnoticed until someone plugs in a phone.
    tasks.named("check").configure {
        dependsOn(tasks.matching { it.name == "compileDebugAndroidTestKotlin" })
    }
}
