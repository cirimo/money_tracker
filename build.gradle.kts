plugins {
    // Declared here with `apply false` so every module resolves the same plugin versions.
    // Modules apply them through the convention plugins in build-logic, never directly.
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false

    id("trosko.spotless")
}
