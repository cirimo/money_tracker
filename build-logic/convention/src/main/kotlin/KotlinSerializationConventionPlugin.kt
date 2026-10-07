import dev.cirimo.trosko.buildlogic.library
import dev.cirimo.trosko.buildlogic.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

/**
 * kotlinx.serialization: the compiler plugin and the JSON runtime together, because one is
 * useless without the other. Apply it after the module's Android or JVM convention plugin.
 */
class KotlinSerializationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("org.jetbrains.kotlin.plugin.serialization")

            dependencies {
                "implementation"(libs.library("kotlinx-serialization-json"))
            }
        }
    }
}
