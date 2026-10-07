plugins {
    id("trosko.android.library")
    id("trosko.android.room")
}

android {
    namespace = "dev.cirimo.trosko.data"
}

dependencies {
    // `api` because this module's public surface is made of domain types.
    api(project(":core:domain"))

    implementation(libs.kotlinx.coroutines.android)

    androidTestImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.kotlinx.coroutines.test)
}
