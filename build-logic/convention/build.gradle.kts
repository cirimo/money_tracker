plugins {
    `kotlin-dsl`
}

group = "dev.cirimo.trosko.buildlogic"

dependencies {
    // compileOnly: the real plugin versions come from the root build's plugins block,
    // so build-logic never drags a second copy onto the classpath.
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.compose.gradlePlugin)

    implementation(libs.spotless.gradlePlugin)
    implementation(libs.detekt.gradlePlugin)
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "trosko.android.application"
            implementationClass = "AndroidApplicationConventionPlugin"
        }
        register("androidCompose") {
            id = "trosko.android.compose"
            implementationClass = "AndroidComposeConventionPlugin"
        }
        register("detekt") {
            id = "trosko.detekt"
            implementationClass = "DetektConventionPlugin"
        }
        register("spotless") {
            id = "trosko.spotless"
            implementationClass = "SpotlessConventionPlugin"
        }
    }
}
