import androidx.room3.gradle.RoomExtension
import dev.cirimo.trosko.buildlogic.library
import dev.cirimo.trosko.buildlogic.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

/**
 * Room 3 with KSP and the bundled SQLite driver. Apply it after `trosko.android.library`.
 *
 * The schema of every database version is exported to the module's `schemas` directory and
 * committed, because migration tests replay those files. See docs/ARCHITECTURE.md.
 */
class AndroidRoomConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("com.google.devtools.ksp")
            pluginManager.apply("androidx.room3")

            extensions.configure<RoomExtension> {
                schemaDirectory(
                    layout.projectDirectory
                        .dir("schemas")
                        .asFile.path,
                )
            }

            dependencies {
                "implementation"(libs.library("androidx-room-runtime"))
                "implementation"(libs.library("androidx-sqlite-bundled"))
                "ksp"(libs.library("androidx-room-compiler"))

                "androidTestImplementation"(libs.library("androidx-room-testing"))
            }
        }
    }
}
