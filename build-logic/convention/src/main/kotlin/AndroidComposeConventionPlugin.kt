import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import dev.cirimo.trosko.buildlogic.library
import dev.cirimo.trosko.buildlogic.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.compose.compiler.gradle.ComposeCompilerGradlePluginExtension

/**
 * Enables Jetpack Compose and wires the BOM plus the tooling every Compose module needs.
 * Apply it after `trosko.android.application` or `trosko.android.library`.
 *
 * Only the Compose runtime, foundation and tooling are added here. Higher-level UI libraries
 * (Material or otherwise) are a design decision and belong in the module that needs them.
 */
class AndroidComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("org.jetbrains.kotlin.plugin.compose")

            pluginManager.withPlugin("com.android.application") {
                extensions.configure<ApplicationExtension> { buildFeatures { compose = true } }
            }
            pluginManager.withPlugin("com.android.library") {
                extensions.configure<LibraryExtension> { buildFeatures { compose = true } }
            }

            extensions.configure<ComposeCompilerGradlePluginExtension> {
                // Tells the compiler which types from non-Compose modules are immutable, so that
                // composables taking them can skip. See docs/ARCHITECTURE.md, performance.
                stabilityConfigurationFiles.add(
                    rootProject.layout.projectDirectory.file("config/compose/stability.conf"),
                )
            }

            dependencies {
                val bom = platform(libs.library("androidx-compose-bom"))
                "implementation"(bom)
                "androidTestImplementation"(bom)

                "implementation"(libs.library("androidx-compose-ui"))
                "implementation"(libs.library("androidx-compose-foundation"))
                "implementation"(libs.library("androidx-compose-ui-tooling-preview"))
                "debugImplementation"(libs.library("androidx-compose-ui-tooling"))

                "androidTestImplementation"(libs.library("androidx-compose-ui-test-junit4"))
                // Compose UI tests pull in an old Espresso transitively, and that one crashes on
                // Android 14 and newer (InputManager.getInstance was removed). Pin a current one.
                "androidTestImplementation"(libs.library("androidx-test-espresso-core"))
                "debugImplementation"(libs.library("androidx-compose-ui-test-manifest"))
            }
        }
    }
}
