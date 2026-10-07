plugins {
    id("trosko.jvm.library")
}

dependencies {
    // `api` because repository interfaces expose Flow in their signatures.
    api(libs.kotlinx.coroutines.core)

    testImplementation(libs.junit)
}
