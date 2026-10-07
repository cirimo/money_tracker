package dev.cirimo.trosko.feature.expense

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.cirimo.trosko.domain.model.Category
import dev.cirimo.trosko.domain.model.CategoryId
import dev.cirimo.trosko.domain.model.NewRecord
import dev.cirimo.trosko.domain.model.NewRecordResult
import dev.cirimo.trosko.domain.model.Record
import dev.cirimo.trosko.domain.model.RecordId
import dev.cirimo.trosko.domain.model.RecordKind
import dev.cirimo.trosko.domain.money.AmountInput
import dev.cirimo.trosko.domain.money.AmountKey
import dev.cirimo.trosko.domain.repository.CategoryRepository
import dev.cirimo.trosko.domain.repository.DeleteResult
import dev.cirimo.trosko.domain.repository.RecordRepository
import dev.cirimo.trosko.domain.repository.ReplaceResult
import dev.cirimo.trosko.format.DayLabel
import dev.cirimo.trosko.format.RecordChange
import dev.cirimo.trosko.format.RecordLine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Clock
import java.time.LocalDate

// Keeps the upstream queries alive across a rotation, and lets them go when the screen is gone.
private const val STOP_TIMEOUT_MILLIS = 5_000L

/**
 * Correcting or deleting the expense [recordId]. The record is read once, when the screen opens;
 * from then on the form is the user's. What they have changed lives in [savedState], so it
 * survives the process being killed in the background.
 */
class ExpenseEditViewModel(
    private val recordId: RecordId,
    categories: CategoryRepository,
    private val records: RecordRepository,
    private val clock: Clock,
    private val savedState: SavedStateHandle,
) : ViewModel() {
    private val form = MutableStateFlow<ExpenseForm?>(null)
    private val status = MutableStateFlow(EditStatus())
    private val today = MutableStateFlow(LocalDate.now(clock))

    val uiState: StateFlow<ExpenseEditUiState> =
        combine(form, status, today, categories.observeActive(RecordKind.EXPENSE), ::expenseEditUiState)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
                initialValue = ExpenseEditUiState.Loading,
            )

    init {
        viewModelScope.launch {
            val record = records.observe(recordId).first()
            if (record == null) {
                status.update { it.copy(isGone = true) }
            } else {
                form.value =
                    if (ExpenseForm.isSavedIn(savedState)) {
                        ExpenseForm.restoredFrom(savedState, record.amount.currency)
                    } else {
                        ExpenseForm(
                            AmountInput.of(record.amount),
                            record.category.id,
                            record.occurredOn,
                            record.note.orEmpty(),
                        )
                    }
                status.update { it.copy(original = record) }
            }
        }
    }

    fun onKey(key: AmountKey) = updateForm { it.copy(amount = it.amount.press(key)) }

    /** Choosing the chosen category again lets go of it. */
    fun onCategoryChosen(id: CategoryId) = updateForm { it.copy(categoryId = if (it.categoryId == id) null else id) }

    /** An arrow beside the date was pressed: [days] is -1 or 1. */
    fun onDayStepped(days: Long) {
        form.value?.pickedDate?.let { onDayPicked(it.plusDays(days)) }
    }

    /** A day later than a correction may carry changes nothing. */
    fun onDayPicked(date: LocalDate) {
        onResumed()
        val latest = status.value.original?.latestDay(today.value)
        if (latest != null && date <= latest) updateForm { it.copy(pickedDate = date) }
    }

    fun onNoteChanged(note: String) = updateForm { it.copy(note = note.take(NewRecord.NOTE_MAX_LENGTH)) }

    fun onSave() {
        onResumed()
        val current = form.value
        val original = status.value.original
        // The category is only as good as the list it was chosen from, which the state knows.
        val canSave = (uiState.value as? ExpenseEditUiState.Editing)?.pad?.canSave == true
        if (current == null || original == null || !canSave) return
        val checked = current.checkedAgainst(original, today.value)
        if (checked !is NewRecordResult.Valid) return

        status.update { it.copy(isBusy = true, saveFailed = false, deleteFailed = false, isAskingToDelete = false) }
        viewModelScope.launch {
            when (records.replace(recordId, checked.record)) {
                ReplaceResult.Replaced -> status.update { it.copy(finished = RecordChange.Corrected(recordId)) }
                ReplaceResult.NotSaved -> status.update { it.copy(isBusy = false, saveFailed = true) }
                ReplaceResult.Gone -> status.update { it.copy(isGone = true) }
            }
        }
    }

    /** The user pressed delete ([isAsking] true), or answered the question with keeping it. */
    fun onDeleteQuestion(isAsking: Boolean) =
        status.update { if (it.isBusy) it else it.copy(isAskingToDelete = isAsking, deleteFailed = false) }

    fun onDeleteConfirmed() {
        if (status.value.isBusy || status.value.original == null) return
        status.update { it.copy(isBusy = true, saveFailed = false, deleteFailed = false) }
        viewModelScope.launch {
            when (records.delete(recordId)) {
                // A record that was already gone is as deleted as the user wanted it.
                DeleteResult.Deleted, DeleteResult.Gone -> {
                    status.update { it.copy(finished = RecordChange.Deleted(recordId)) }
                }

                DeleteResult.NotDeleted -> {
                    status.update { it.copy(isBusy = false, deleteFailed = true, isAskingToDelete = false) }
                }
            }
        }
    }

    /**
     * The screen is in front again; the day may have changed while it was away. Everything here
     * that depends on today calls this first, for the same reason.
     */
    fun onResumed() {
        today.value = LocalDate.now(clock)
    }

    private fun updateForm(change: (ExpenseForm) -> ExpenseForm) {
        form.update { it?.let(change) }
        form.value?.saveTo(savedState)
    }
}

/**
 * @property original the record as it was when the screen opened; null until it has been read.
 * @property isBusy a write is under way, or has finished the screen's work.
 */
private data class EditStatus(
    val original: Record? = null,
    val isGone: Boolean = false,
    val isBusy: Boolean = false,
    val saveFailed: Boolean = false,
    val deleteFailed: Boolean = false,
    val isAskingToDelete: Boolean = false,
    val finished: RecordChange? = null,
)

// The latest day a correction may carry. Normally today; but a record dated after today (written
// in a time zone that is ahead of this one) may keep its date, or fixing its amount would be
// refused for a date the user never touched.
private fun Record.latestDay(today: LocalDate): LocalDate = maxOf(today, occurredOn)

private fun ExpenseForm.checkedAgainst(
    original: Record,
    today: LocalDate,
): NewRecordResult? {
    val category = categoryId ?: return null
    return NewRecord.expense(
        amount.toMoney(),
        category,
        pickedDate ?: original.occurredOn,
        note,
        original.latestDay(today),
    )
}

private fun NewRecord.differsFrom(original: Record): Boolean =
    amount != original.amount ||
        categoryId != original.category.id ||
        occurredOn != original.occurredOn ||
        note != original.note

private fun expenseEditUiState(
    form: ExpenseForm?,
    status: EditStatus,
    today: LocalDate,
    active: List<Category>,
): ExpenseEditUiState {
    val original = status.original
    return when {
        status.isGone -> ExpenseEditUiState.Gone
        form == null || original == null -> ExpenseEditUiState.Loading
        else -> editing(form, original, status, today, active)
    }
}

private fun editing(
    form: ExpenseForm,
    original: Record,
    status: EditStatus,
    today: LocalDate,
    active: List<Category>,
): ExpenseEditUiState.Editing {
    // The record's own category stays on offer even when it is no longer offered for new ones.
    val categories = if (active.any { it.id == original.category.id }) active else active + original.category
    val selected = categories.firstOrNull { it.id == form.categoryId }
    val date = form.pickedDate ?: original.occurredOn
    val checked = if (selected == null) null else form.checkedAgainst(original, today)
    val canBeWritten = checked is NewRecordResult.Valid && checked.record.differsFrom(original)

    return ExpenseEditUiState.Editing(
        pad =
            ExpensePadState(
                amount = form.amount,
                categories = categories,
                selectedCategoryId = selected?.id,
                day = DayLabel.of(date, today),
                canStepDayForward = date < original.latestDay(today),
                note = form.note,
                canSave = canBeWritten && !status.isBusy,
            ),
        preview =
            RecordLine(
                id = original.id,
                category = selected ?: original.category,
                signedAmount = -form.amount.toMoney(),
                day = DayLabel.of(date, today),
                note = form.note.trim().ifEmpty { null },
            ),
        days = DayPickerDays(selected = date, today = today, latest = original.latestDay(today)),
        isAskingToDelete = status.isAskingToDelete,
        canDelete = !status.isBusy,
        saveFailed = status.saveFailed,
        deleteFailed = status.deleteFailed,
        finished = status.finished,
    )
}
