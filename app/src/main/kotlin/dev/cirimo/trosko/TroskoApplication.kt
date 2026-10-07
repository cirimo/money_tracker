package dev.cirimo.trosko

import android.app.Application

class TroskoApplication : Application() {
    // Lazy, so process start does no work the first frame does not need.
    val container: AppContainer by lazy { AppContainer(this) }
}
