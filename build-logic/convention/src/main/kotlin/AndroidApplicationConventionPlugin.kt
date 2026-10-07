import com.android.build.api.dsl.ApplicationExtension
import dev.cirimo.trosko.buildlogic.libs
import dev.cirimo.trosko.buildlogic.version
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask

/**
 * Everything an Android application module needs that is not specific to that one module:
 * SDK levels, Java level, Kotlin compiler strictness, Lint policy and detekt.
 *
 * Kotlin support comes from AGP's built-in Kotlin, so `org.jetbrains.kotlin.android` is not applied.
 */
class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("com.android.application")
            pluginManager.apply("trosko.detekt")

            extensions.configure<ApplicationExtension> {
                compileSdk {
                    version =
                        release(libs.version("compileSdk").toInt()) {
                            minorApiLevel = libs.version("compileSdkMinor").toInt()
                        }
                }
                buildToolsVersion = libs.version("buildTools")

                defaultConfig {
                    minSdk = libs.version("minSdk").toInt()
                    targetSdk = libs.version("targetSdk").toInt()
                    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                }

                compileOptions {
                    sourceCompatibility = JavaVersion.VERSION_17
                    targetCompatibility = JavaVersion.VERSION_17
                }

                lint {
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
    }
}
