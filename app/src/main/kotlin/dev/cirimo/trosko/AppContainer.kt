package dev.cirimo.trosko

import android.content.Context
import android.content.pm.ApplicationInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalContext
import dev.cirimo.trosko.data.Repositories

/**
 * The composition root: the one place where the app's long-lived objects are created and wired
 * together. There is no dependency injection framework; everything else receives what it needs
 * through its constructor. See docs/ARCHITECTURE.md, dependency injection.
 */
class AppContainer(
    context: Context,
) {
    // Only a debuggable build may throw its database away; see Repositories.
    val repositories = Repositories(context, allowDestructiveMigration = context.isDebuggable)
}

private val Context.isDebuggable: Boolean
    get() = applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0

/** The app's container, for routes that need to build a view model. */
@Composable
@ReadOnlyComposable
fun appContainer(): AppContainer = (LocalContext.current.applicationContext as TroskoApplication).container
