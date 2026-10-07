package dev.cirimo.trosko.feature.expense

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.cirimo.trosko.appContainer

/**
 * The stateful entry of recording an expense: it owns the view model and hands plain state and
 * plain functions to [ExpenseEntryScreen]. Navigation shows this, never the screen directly.
 */
@Composable
fun ExpenseEntryRoute(
    modifier: Modifier = Modifier,
    viewModel: ExpenseEntryViewModel = expenseEntryViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Coming back to the app the next morning must not leave "today" pointing at yesterday.
    LifecycleResumeEffect(viewModel) {
        viewModel.onResumed()
        onPauseOrDispose {}
    }

    ExpenseEntryScreen(
        uiState = uiState,
        onKey = viewModel::onKey,
        onCategoryClick = viewModel::onCategoryChosen,
        onDayBack = viewModel::onDayBack,
        onDayForward = viewModel::onDayForward,
        onNoteChange = viewModel::onNoteChanged,
        onSave = viewModel::onSave,
        onLand = viewModel::onLandingShown,
        modifier = modifier,
    )
}

@Composable
private fun expenseEntryViewModel(): ExpenseEntryViewModel {
    val container = appContainer()
    return viewModel {
        ExpenseEntryViewModel(
            categories = container.repositories.categories,
            records = container.repositories.records,
            clock = container.clock,
            savedState = createSavedStateHandle(),
        )
    }
}
