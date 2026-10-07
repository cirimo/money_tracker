plugins {
    id("trosko.android.library")
    id("trosko.android.compose")
}

android {
    namespace = "dev.cirimo.trosko.designsystem"
}

dependencies {
    androidTestImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.test.runner)
}
