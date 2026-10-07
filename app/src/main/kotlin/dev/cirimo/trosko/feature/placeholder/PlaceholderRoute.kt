package dev.cirimo.trosko.feature.placeholder

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.cirimo.trosko.appContainer

/**
 * The stateful entry of the placeholder feature: it owns the view model and hands plain state
 * to [PlaceholderScreen]. Navigation shows this, never the screen directly.
 */
@Composable
fun PlaceholderRoute(
    modifier: Modifier = Modifier,
    viewModel: PlaceholderViewModel = placeholderViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    PlaceholderScreen(uiState = uiState, modifier = modifier)
}

@Composable
private fun placeholderViewModel(): PlaceholderViewModel {
    val container = appContainer()
    return viewModel { PlaceholderViewModel(container.repositories.categories) }
}
