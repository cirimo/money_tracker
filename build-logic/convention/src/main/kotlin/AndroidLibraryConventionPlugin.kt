import com.android.build.api.dsl.LibraryExtension
import dev.cirimo.trosko.buildlogic.configureAndroidCommon
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

/**
 * An Android library module: code that needs the Android SDK but is not the application itself.
 * The module's own build script only sets its namespace and dependencies.
 */
class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("com.android.library")
            pluginManager.apply("trosko.detekt")

            extensions.configure<LibraryExtension> {
                configureAndroidCommon(this)
            }
        }
    }
}
