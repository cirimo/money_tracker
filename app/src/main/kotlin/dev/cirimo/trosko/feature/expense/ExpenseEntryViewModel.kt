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
import dev.cirimo.trosko.domain.repository.RecordRepository
import dev.cirimo.trosko.domain.repository.SaveResult
import dev.cirimo.trosko.format.DayLabel
import dev.cirimo.trosko.format.RecordChange
import dev.cirimo.trosko.format.RecordLine
import dev.cirimo.trosko.format.RecordNotice
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.launch
import java.time.Clock
import java.time.LocalDate
import java.util.Currency

// Keeps the upstream queries alive across a rotation, and lets them go when the screen is gone.
private const val STOP_TIMEOUT_MILLIS = 5_000L

// How many of the latest records the page above the keypad has room to show.
private const val LATEST_COUNT = 6

/**
 * What the user has typed so far lives in [savedState], so it survives the process being killed
 * in the background: nobody should have to type an amount twice because a call came in.
 */
class ExpenseEntryViewModel(
    categories: CategoryRepository,
    private val records: RecordRepository,
    private val clock: Clock,
    private val savedState: SavedStateHandle,
) : ViewModel() {
    // The first release records in euros only. That is a limit of this screen, not of the model.
    private val currency = Currency.getInstance("EUR")

    private val form = MutableStateFlow(ExpenseForm.restoredFrom(savedState, currency))
    private val outcome = MutableStateFlow(SaveOutcome())
    private val today = MutableStateFlow(LocalDate.now(clock))

    val uiState: StateFlow<ExpenseEntryUiState> =
        combine(
            form,
            outcome,
            today,
            categories.observeActive(RecordKind.EXPENSE),
            records.observeLatest(LATEST_COUNT),
            ::expenseEntryUiState,
        ).stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = expenseEntryUiState(form.value, outcome.value, today.value, emptyList(), emptyList()),
        )

    fun onKey(key: AmountKey) {
        outcome.update { it.copy(notice = null) }
        updateForm { it.copy(amount = it.amount.press(key)) }
    }

    /** Choosing the chosen category again lets go of it. */
    fun onCategoryChosen(id: CategoryId) = updateForm { it.copy(categoryId = if (it.categoryId == id) null else id) }

    fun onDayBack() {
        onResumed()
        updateForm { it.copy(pickedDate = (it.pickedDate ?: today.value).minusDays(1)) }
    }

    fun onDayForward() {
        onResumed()
        // Reaching today lets go of the date, so the form means "today" again, whenever saved.
        updateForm { it.copy(pickedDate = it.pickedDate?.plusDays(1)?.takeIf { next -> next < today.value }) }
    }

    fun onNoteChanged(note: String) = updateForm { it.copy(note = note.take(NewRecord.NOTE_MAX_LENGTH)) }

    fun onSave() {
        onResumed()
        val current = form.value
        val now = today.value
        val categoryId = current.categoryId
        if (categoryId == null || outcome.value.isSaving) return
        val checked =
            NewRecord.expense(current.amount.toMoney(), categoryId, current.pickedDate ?: now, current.note, now)
        if (checked !is NewRecordResult.Valid) return

        outcome.update { it.copy(isSaving = true, saveFailed = false) }
        viewModelScope.launch {
            when (val result = records.add(checked.record)) {
                is SaveResult.Saved -> {
                    updateForm { ExpenseForm(AmountInput(currency)) }
                    outcome.value =
                        SaveOutcome(notice = RecordNotice.Saved, landedId = result.id, isLandingPending = true)
                }

                SaveResult.NotSaved -> {
                    outcome.update { it.copy(isSaving = false, saveFailed = true) }
                }
            }
        }
    }

    /** A record opened from this page was corrected or deleted; a corrected one lands again. */
    fun onRecordChanged(change: RecordChange) {
        outcome.value =
            when (change) {
                is RecordChange.Corrected -> {
                    SaveOutcome(notice = RecordNotice.Corrected, landedId = change.id, isLandingPending = true)
                }

                is RecordChange.Deleted -> {
                    SaveOutcome(notice = RecordNotice.Deleted)
                }
            }
    }

    /** The screen has started the landing animation of the record just saved or corrected. */
    fun onLandingShown() = outcome.update { it.copy(isLandingPending = false) }

    /**
     * The screen is in front again; the day may have changed while it was away. Everything here
     * that depends on today calls this first, for the same reason.
     */
    fun onResumed() {
        val now = LocalDate.now(clock)
        today.value = now
        // A picked date that today has caught up with, or passed, is simply today.
        if (form.value.pickedDate?.let { it >= now } == true) updateForm { it.copy(pickedDate = null) }
    }

    private fun updateForm(change: (ExpenseForm) -> ExpenseForm) = form.updateAndGet(change).saveTo(savedState)
}

private data class SaveOutcome(
    val isSaving: Boolean = false,
    val saveFailed: Boolean = false,
    val notice: RecordNotice? = null,
    val landedId: RecordId? = null,
    val isLandingPending: Boolean = false,
)

private fun expenseEntryUiState(
    form: ExpenseForm,
    outcome: SaveOutcome,
    today: LocalDate,
    categories: List<Category>,
    latest: List<Record>,
): ExpenseEntryUiState {
    // A category that is no longer offered (archived meanwhile) counts as not chosen.
    val selected = form.categoryId?.takeIf { id -> categories.any { it.id == id } }
    return ExpenseEntryUiState(
        amount = form.amount,
        categories = categories,
        selectedCategoryId = selected,
        day = DayLabel.of(form.pickedDate ?: today, today),
        canStepDayForward = form.pickedDate != null,
        note = form.note,
        canSave = form.amount.toMoney().isPositive && selected != null && !outcome.isSaving,
        saveFailed = outcome.saveFailed,
        notice = outcome.notice,
        latest = latest.map { RecordLine.of(it, today) },
        landedId = outcome.landedId,
        isLandingPending = outcome.isLandingPending,
    )
}
