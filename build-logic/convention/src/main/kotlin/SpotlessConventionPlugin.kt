import com.diffplug.gradle.spotless.SpotlessExtension
import com.diffplug.spotless.LineEnding
import dev.cirimo.trosko.buildlogic.libs
import dev.cirimo.trosko.buildlogic.version
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

/**
 * Formatting for every Kotlin and Gradle Kotlin DSL file in the repository, build-logic included.
 * Applied once, to the root project, so new modules are covered without any extra setup.
 * ktlint reads its settings from the root `.editorconfig`.
 */
class SpotlessConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("com.diffplug.spotless")

            val ktlintVersion = libs.version("ktlint")

            // A file tree with excludes prunes whole directories. A plain "**/*.kt" target would walk
            // into .gradle and build, which is slow and trips over files Gradle holds locked.
            fun sources(pattern: String) =
                fileTree(rootDir) {
                    include(pattern)
                    exclude("**/build/**", "**/.gradle/**", "**/.kotlin/**", ".git/**", ".idea/**")
                }

            extensions.configure<SpotlessExtension> {
                // The repository is LF everywhere, see .gitattributes.
                lineEndings = LineEnding.UNIX

                kotlin {
                    target(sources("**/*.kt"))
                    ktlint(ktlintVersion)
                }
                kotlinGradle {
                    target(sources("**/*.gradle.kts"))
                    ktlint(ktlintVersion)
                }
            }
        }
    }
}
