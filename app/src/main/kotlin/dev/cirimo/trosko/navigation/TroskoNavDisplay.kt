package dev.cirimo.trosko.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import dev.cirimo.trosko.feature.placeholder.PlaceholderKey
import dev.cirimo.trosko.feature.placeholder.PlaceholderRoute

/**
 * The whole app's navigation: it owns the back stack and is the only place that maps a key to
 * the route that draws it. Features do not know about each other; they meet here.
 */
@Composable
fun TroskoNavDisplay(modifier: Modifier = Modifier) {
    val backStack = rememberNavBackStack(PlaceholderKey)

    NavDisplay(
        backStack = backStack,
        modifier = modifier,
        onBack = { backStack.removeLastOrNull() },
        // The second decorator gives every entry its own view model store, cleared when the
        // entry leaves the back stack.
        entryDecorators =
            listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator(),
            ),
        entryProvider =
            entryProvider {
                entry<PlaceholderKey> { PlaceholderRoute() }
            },
    )
}
