import com.android.build.api.dsl.ApplicationExtension
import dev.cirimo.trosko.buildlogic.library
import dev.cirimo.trosko.buildlogic.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

/**
 * Enables Jetpack Compose and wires the BOM plus the tooling every Compose module needs.
 * Apply it after `trosko.android.application`.
 *
 * Only the Compose runtime, foundation and tooling are added here. Higher-level UI libraries
 * (Material or otherwise) are a design decision and belong in the module that needs them.
 */
class AndroidComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("org.jetbrains.kotlin.plugin.compose")

            extensions.configure<ApplicationExtension> {
                buildFeatures {
                    compose = true
                }
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
                "debugImplementation"(libs.library("androidx-compose-ui-test-manifest"))
            }
        }
    }
}
