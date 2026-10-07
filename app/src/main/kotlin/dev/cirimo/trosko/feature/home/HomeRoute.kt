package dev.cirimo.trosko.feature.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.cirimo.trosko.appContainer

/**
 * The stateful entry of the home page: it owns the view model and hands plain state to
 * [HomeScreen]. Navigation shows this, never the screen directly.
 */
@Composable
fun HomeRoute(
    onNewExpense: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = homeViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LifecycleResumeEffect(viewModel) {
        viewModel.onResumed()
        onPauseOrDispose {}
    }

    HomeScreen(uiState = uiState, onNewExpense = onNewExpense, modifier = modifier)
}

@Composable
private fun homeViewModel(): HomeViewModel {
    val container = appContainer()
    return viewModel { HomeViewModel(container.repositories.records, container.clock) }
}
