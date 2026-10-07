package dev.cirimo.trosko.feature.expense

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.result.LocalResultEventBus
import dev.cirimo.trosko.appContainer
import dev.cirimo.trosko.domain.model.RecordId
import dev.cirimo.trosko.format.RecordChange
import java.util.UUID

/**
 * The stateful entry of correcting an expense: it owns the view model and hands plain state and
 * plain functions to [ExpenseEditScreen]. Navigation shows this, never the screen directly.
 *
 * @param recordId the record's UUID as text, as [ExpenseEditKey] carries it.
 * @param onDone called when there is nothing left to do here: the record was corrected or
 * deleted, or the user turned back from a record that no longer exists.
 */
@Composable
fun ExpenseEditRoute(
    recordId: String,
    onDone: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ExpenseEditViewModel = expenseEditViewModel(recordId),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val resultBus = LocalResultEventBus.current
    val currentOnDone by rememberUpdatedState(onDone)

    LifecycleResumeEffect(viewModel) {
        viewModel.onResumed()
        onPauseOrDispose {}
    }

    // Once the write is through, the list this was opened from is told and the page turns back.
    val finished = (uiState as? ExpenseEditUiState.Editing)?.finished
    LaunchedEffect(finished) {
        if (finished != null) {
            resultBus.sendResult<RecordChange>(RecordChange.RESULT_KEY, finished)
            currentOnDone()
        }
    }

    ExpenseEditScreen(
        uiState = uiState,
        onKey = viewModel::onKey,
        onCategoryClick = viewModel::onCategoryChosen,
        onDayBack = { viewModel.onDayStepped(days = -1) },
        onDayForward = { viewModel.onDayStepped(days = 1) },
        onDayPick = viewModel::onDayPicked,
        onNoteChange = viewModel::onNoteChanged,
        onSave = viewModel::onSave,
        onDeleteClick = { viewModel.onDeleteQuestion(isAsking = true) },
        onDeleteConfirm = viewModel::onDeleteConfirmed,
        onDeleteDismiss = { viewModel.onDeleteQuestion(isAsking = false) },
        onBack = onDone,
        modifier = modifier,
    )
}

@Composable
private fun expenseEditViewModel(recordId: String): ExpenseEditViewModel {
    val container = appContainer()
    return viewModel {
        ExpenseEditViewModel(
            recordId = RecordId(UUID.fromString(recordId)),
            categories = container.repositories.categories,
            records = container.repositories.records,
            clock = container.clock,
            savedState = createSavedStateHandle(),
        )
    }
}
