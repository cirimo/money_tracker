import com.android.build.api.dsl.Lint
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

/**
 * A plain Kotlin module with no Android on its classpath. Code placed here cannot reach the
 * Android SDK, Compose or the database even by accident, which is the point.
 */
class JvmLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("org.jetbrains.kotlin.jvm")
            // Android Lint for a non-Android module, so the app's lint run analyses these sources
            // instead of treating the module as an opaque external dependency.
            pluginManager.apply("com.android.lint")
            pluginManager.apply("trosko.detekt")

            extensions.configure<Lint> {
                warningsAsErrors = true
                abortOnError = true
            }

            // The same bytecode level as the Android modules that consume this one.
            extensions.configure<JavaPluginExtension> {
                sourceCompatibility = JavaVersion.VERSION_17
                targetCompatibility = JavaVersion.VERSION_17
            }

            tasks.withType<KotlinJvmCompile>().configureEach {
                compilerOptions {
                    jvmTarget.set(JvmTarget.JVM_17)
                    allWarningsAsErrors.set(true)
                }
            }
        }
    }
}
