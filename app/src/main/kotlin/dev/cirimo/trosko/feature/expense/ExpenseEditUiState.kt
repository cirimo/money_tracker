package dev.cirimo.trosko.feature.expense

import dev.cirimo.trosko.format.RecordChange
import dev.cirimo.trosko.format.RecordLine

sealed interface ExpenseEditUiState {
    /** Storage has not handed the record over yet. The page stays blank. */
    data object Loading : ExpenseEditUiState

    /** There is no such record any more: nothing to correct and nothing to delete. */
    data object Gone : ExpenseEditUiState

    /**
     * @property pad what the pad shows; its main button may be pressed once something differs
     * from what is written down and can be written.
     * @property preview the record as the list would show it with what the pad says now.
     * @property isAskingToDelete the question whether to delete stands in place of the heading.
     * @property canDelete false while a write is under way.
     * @property saveFailed the last correction did not reach storage; what was typed is still
     * here.
     * @property deleteFailed the last delete did not reach storage; the record is still there.
     * @property finished the record was corrected or deleted and the screen has nothing left to
     * do. It is state the route acts on, not an event, so a rotation cannot lose it.
     */
    data class Editing(
        val pad: ExpensePadState,
        val preview: RecordLine,
        val isAskingToDelete: Boolean,
        val canDelete: Boolean,
        val saveFailed: Boolean,
        val deleteFailed: Boolean,
        val finished: RecordChange?,
    ) : ExpenseEditUiState
}
