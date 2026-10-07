package dev.cirimo.trosko.feature.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.result.LocalResultEventBus
import androidx.navigation3.runtime.result.ResultEffect
import dev.cirimo.trosko.appContainer
import dev.cirimo.trosko.domain.model.RecordId
import dev.cirimo.trosko.format.RecordChange

/**
 * The stateful entry of the home page: it owns the view model and hands plain state to
 * [HomeScreen]. Navigation shows this, never the screen directly.
 */
@Composable
fun HomeRoute(
    onNewExpense: () -> Unit,
    onRecordClick: (RecordId) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = homeViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LifecycleResumeEffect(viewModel) {
        viewModel.onResumed()
        onPauseOrDispose {}
    }

    // What the correction screen did to a record that was opened from here.
    ResultEffect<RecordChange>(RecordChange.RESULT_KEY, LocalResultEventBus.current) { change ->
        viewModel.onRecordChanged(change)
    }

    HomeScreen(
        uiState = uiState,
        onNewExpense = {
            viewModel.onLeaving()
            onNewExpense()
        },
        onRecordClick = { id ->
            viewModel.onLeaving()
            onRecordClick(id)
        },
        onLand = viewModel::onLandingShown,
        modifier = modifier,
    )
}

@Composable
private fun homeViewModel(): HomeViewModel {
    val container = appContainer()
    return viewModel { HomeViewModel(container.repositories.records, container.clock) }
}
