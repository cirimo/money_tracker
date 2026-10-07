import com.android.build.api.dsl.ApplicationExtension
import dev.cirimo.trosko.buildlogic.configureAndroidCommon
import dev.cirimo.trosko.buildlogic.libs
import dev.cirimo.trosko.buildlogic.version
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

/**
 * Everything an Android application module needs that is not specific to that one module.
 * What it shares with library modules lives in `configureAndroidCommon`.
 *
 * Kotlin support comes from AGP's built-in Kotlin, so `org.jetbrains.kotlin.android` is not applied.
 */
class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("com.android.application")
            pluginManager.apply("trosko.detekt")

            extensions.configure<ApplicationExtension> {
                configureAndroidCommon(this)
                defaultConfig {
                    targetSdk = libs.version("targetSdk").toInt()
                }
            }
        }
    }
}
