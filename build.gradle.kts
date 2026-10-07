plugins {
    // Declared here with `apply false` so every module resolves the same plugin versions.
    // Modules apply them through the convention plugins in build-logic, never directly.
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.room) apply false

    id("trosko.spotless")
}
