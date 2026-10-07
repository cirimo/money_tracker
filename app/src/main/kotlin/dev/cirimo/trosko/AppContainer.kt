package dev.cirimo.trosko

import android.content.Context
import android.content.pm.ApplicationInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalContext
import dev.cirimo.trosko.data.Repositories
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import java.time.Clock

/**
 * The composition root: the one place where the app's long-lived objects are created and wired
 * together. There is no dependency injection framework; everything else receives what it needs
 * through its constructor. See docs/ARCHITECTURE.md, dependency injection.
 */
class AppContainer(
    context: Context,
) {
    /** The only source of the current date and time in the app. */
    val clock: Clock = DeviceClock()

    // Lives as long as the process, so a write started from a screen finishes after the screen
    // is gone. A supervisor, so one failed write does not cancel the others. This is the one
    // place a dispatcher is named; everything else receives its scope.
    private val writeScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    // Only a debuggable build may throw its database away; see Repositories.
    val repositories =
        Repositories(
            context = context,
            allowDestructiveMigration = context.isDebuggable,
            writeScope = writeScope,
            clock = clock,
        )
}

private val Context.isDebuggable: Boolean
    get() = applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0

/** The app's container, for routes that need to build a view model. */
@Composable
@ReadOnlyComposable
fun appContainer(): AppContainer = (LocalContext.current.applicationContext as TroskoApplication).container
