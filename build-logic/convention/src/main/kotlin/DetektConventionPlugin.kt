import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType

/**
 * Static analysis for Kotlin. Formatting is deliberately left to Spotless and ktlint,
 * so detekt's formatting rule set is not used and the two tools never disagree.
 */
class DetektConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("io.gitlab.arturbosch.detekt")

            extensions.configure<DetektExtension> {
                buildUponDefaultConfig = true
                parallel = true
                config.setFrom(rootProject.file("config/detekt/detekt.yml"))
                // Listed explicitly so the task covers every source set, tests included.
                source.setFrom(
                    "src/main/kotlin",
                    "src/test/kotlin",
                    "src/androidTest/kotlin",
                )
            }

            tasks.withType<Detekt>().configureEach {
                jvmTarget = "17"
                reports {
                    html.required.set(true)
                    sarif.required.set(true)
                    xml.required.set(false)
                    txt.required.set(false)
                    md.required.set(false)
                }
            }
        }
    }
}
