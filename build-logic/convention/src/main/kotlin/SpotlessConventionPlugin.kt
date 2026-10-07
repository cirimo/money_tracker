import com.diffplug.gradle.spotless.SpotlessExtension
import com.diffplug.spotless.LineEnding
import dev.cirimo.trosko.buildlogic.library
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
            // Compose-specific rules (modifier parameters, state hoisting, naming), run by ktlint.
            // The detekt flavour of the same rules needs detekt 2, which is not stable yet.
            val composeRules =
                libs.library("compose-rules-ktlint").get().let { rules ->
                    "${rules.module.group}:${rules.module.name}:${rules.versionConstraint.requiredVersion}"
                }

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
                    ktlint(ktlintVersion).customRuleSets(listOf(composeRules))
                }
                kotlinGradle {
                    target(sources("**/*.gradle.kts"))
                    ktlint(ktlintVersion)
                }
            }
        }
    }
}
