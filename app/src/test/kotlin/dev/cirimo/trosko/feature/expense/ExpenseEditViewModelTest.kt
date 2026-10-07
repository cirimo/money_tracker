package dev.cirimo.trosko.feature.expense

import androidx.lifecycle.SavedStateHandle
import dev.cirimo.trosko.domain.model.Record
import dev.cirimo.trosko.domain.model.RecordId
import dev.cirimo.trosko.domain.model.RecordKind
import dev.cirimo.trosko.domain.money.AmountKey
import dev.cirimo.trosko.domain.money.Money
import dev.cirimo.trosko.format.DayLabel
import dev.cirimo.trosko.format.RecordChange
import dev.cirimo.trosko.testing.FakeCategoryRepository
import dev.cirimo.trosko.testing.FakeRecordRepository
import dev.cirimo.trosko.testing.SettableClock
import dev.cirimo.trosko.testing.builtinCategories
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDate
import java.util.Currency
import java.util.UUID

// Replacing the main dispatcher is how a view model's scope is driven from a test.
@OptIn(ExperimentalCoroutinesApi::class)
class ExpenseEditViewModelTest {
    private val euro = Currency.getInstance("EUR")
    private val today = LocalDate.of(2026, 10, 7)
    private val groceries = builtinCategories[0]
    private val transport = builtinCategories[2]
    private val clock = SettableClock(today)
    private val records = FakeRecordRepository(clock)
    private val savedState = SavedStateHandle()
    private val written =
        Record(
            id = RecordId(UUID.randomUUID()),
            kind = RecordKind.EXPENSE,
            amount = Money(1_250, euro),
            category = groceries,
            occurredOn = today.minusDays(1),
            note = "bread",
            createdAt = clock.instant(),
        )

    @Before
    fun replaceMainDispatcher() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun restoreMainDispatcher() {
        Dispatchers.resetMain()
    }

    // Without a collector `stateIn` never starts, so every test collects in the background.
    private fun TestScope.viewModel(
        record: Record? = written,
        categories: FakeCategoryRepository = FakeCategoryRepository(),
    ): ExpenseEditViewModel {
        if (record != null) records.seed(record)
        val viewModel = ExpenseEditViewModel(written.id, categories, records, clock, savedState)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }
        return viewModel
    }

    private val ExpenseEditViewModel.editing get() = uiState.value as ExpenseEditUiState.Editing

    // Takes the amount back key by key and types [amount] in its place.
    private fun ExpenseEditViewModel.retype(amount: String) {
        repeat(AMOUNT_KEYS) { onKey(AmountKey.Backspace) }
        amount.forEach { char ->
            onKey(if (char == ',') AmountKey.Separator else AmountKey.Digit(char.digitToInt()))
        }
    }

    @Test
    fun `the screen opens with what was written down and nothing to save`() =
        runTest {
            val viewModel = viewModel()

            val state = viewModel.editing
            assertEquals(Money(1_250, euro), state.pad.amount.toMoney())
            assertEquals("50", state.pad.amount.fraction)
            assertEquals(groceries.id, state.pad.selectedCategoryId)
            assertEquals(DayLabel.Yesterday, state.pad.day)
            assertEquals("bread", state.pad.note)
            assertFalse(state.pad.canSave)
            assertTrue(state.canDelete)
            assertFalse(state.isAskingToDelete)
        }

    @Test
    fun `the state is loading until storage hands the record over`() =
        runTest {
            val unanswered = FakeRecordRepository(clock, answered = false)
            val viewModel = ExpenseEditViewModel(written.id, FakeCategoryRepository(), unanswered, clock, savedState)
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }

            assertEquals(ExpenseEditUiState.Loading, viewModel.uiState.value)
        }

    @Test
    fun `a record that is not there is gone`() =
        runTest {
            val viewModel = viewModel(record = null)

            assertEquals(ExpenseEditUiState.Gone, viewModel.uiState.value)
        }

    @Test
    fun `a changed amount can be saved and the record on the page follows it`() =
        runTest {
            val viewModel = viewModel()

            viewModel.retype("15,20")

            assertTrue(viewModel.editing.pad.canSave)
            assertEquals(Money(-1_520, euro), viewModel.editing.preview.signedAmount)
        }

    @Test
    fun `typing the same amount again leaves nothing to save`() =
        runTest {
            val viewModel = viewModel()

            viewModel.retype("12,5")

            assertFalse(viewModel.editing.pad.canSave)
        }

    @Test
    fun `zero cannot be saved`() =
        runTest {
            val viewModel = viewModel()

            viewModel.retype("0")
            viewModel.onSave()

            assertFalse(viewModel.editing.pad.canSave)
            assertTrue(records.replaced.isEmpty())
        }

    @Test
    fun `letting go of the category leaves nothing to save and the page keeps the old one`() =
        runTest {
            val viewModel = viewModel()
            viewModel.retype("15")

            viewModel.onCategoryChosen(groceries.id)

            assertNull(viewModel.editing.pad.selectedCategoryId)
            assertFalse(viewModel.editing.pad.canSave)
            assertEquals(groceries, viewModel.editing.preview.category)
        }

    @Test
    fun `saving writes the correction and finishes with the record corrected`() =
        runTest {
            val viewModel = viewModel()
            viewModel.retype("15,20")
            viewModel.onCategoryChosen(transport.id)
            viewModel.onDayStepped(days = -1)
            viewModel.onNoteChanged(" tram ")

            viewModel.onSave()

            val (id, correction) = records.replaced.single()
            assertEquals(written.id, id)
            assertEquals(Money(1_520, euro), correction.amount)
            assertEquals(transport.id, correction.categoryId)
            assertEquals(today.minusDays(2), correction.occurredOn)
            assertEquals("tram", correction.note)
            assertEquals(RecordChange.Corrected(written.id), viewModel.editing.finished)
            assertFalse(viewModel.editing.pad.canSave)
        }

    @Test
    fun `taking the note away saves the record without one`() =
        runTest {
            val viewModel = viewModel()

            viewModel.onNoteChanged("  ")
            viewModel.onSave()

            assertNull(
                records.replaced
                    .single()
                    .second.note,
            )
            assertNull(viewModel.editing.preview.note)
        }

    @Test
    fun `the date steps forward to today and no further`() =
        runTest {
            val viewModel = viewModel()
            val forwardOnYesterday = viewModel.editing.pad.canStepDayForward

            viewModel.onDayStepped(days = 1)
            viewModel.onDayStepped(days = 1)

            assertTrue(forwardOnYesterday)
            assertEquals(DayLabel.Today, viewModel.editing.pad.day)
            assertFalse(viewModel.editing.pad.canStepDayForward)
            assertTrue(viewModel.editing.pad.canSave)
        }

    @Test
    fun `a day chosen from the calendar is where the record is filed`() =
        runTest {
            val viewModel = viewModel()

            viewModel.onDayPicked(today.minusDays(20))
            viewModel.onSave()

            assertEquals(today.minusDays(20), viewModel.editing.days.selected)
            assertEquals(
                today.minusDays(20),
                records.replaced
                    .single()
                    .second.occurredOn,
            )
        }

    @Test
    fun `a day after today cannot be chosen and the calendar knows where it ends`() =
        runTest {
            val viewModel = viewModel()

            viewModel.onDayPicked(today.plusDays(1))

            assertEquals(today.minusDays(1), viewModel.editing.days.selected)
            assertEquals(today, viewModel.editing.days.latest)
            assertEquals(today, viewModel.editing.days.today)
        }

    @Test
    fun `a record dated after today keeps its date when only the amount is corrected`() =
        runTest {
            val ahead = written.copy(occurredOn = today.plusDays(1))
            val viewModel = viewModel(record = ahead)

            viewModel.retype("15")
            val forward = viewModel.editing.pad.canStepDayForward
            viewModel.onSave()

            assertFalse(forward)
            assertEquals(
                today.plusDays(1),
                records.replaced
                    .single()
                    .second.occurredOn,
            )
        }

    @Test
    fun `a failed save says so and keeps what was changed`() =
        runTest {
            val viewModel = viewModel()
            records.failing = true
            viewModel.retype("15,20")

            viewModel.onSave()

            val state = viewModel.editing
            assertTrue(state.saveFailed)
            assertNull(state.finished)
            assertEquals(Money(1_520, euro), state.pad.amount.toMoney())
            assertTrue(state.pad.canSave)
        }

    @Test
    fun `saving a record that has gone meanwhile says it is gone`() =
        runTest {
            val viewModel = viewModel()
            viewModel.retype("15,20")
            records.delete(written.id)

            viewModel.onSave()

            assertEquals(ExpenseEditUiState.Gone, viewModel.uiState.value)
        }

    @Test
    fun `a second press while the first save is under way writes nothing twice`() =
        runTest {
            val viewModel = viewModel()
            val gate = CompletableDeferred<Unit>()
            records.gate = gate
            viewModel.retype("15,20")

            viewModel.onSave()
            val busy = viewModel.editing
            viewModel.onSave()
            viewModel.onDeleteConfirmed()
            gate.complete(Unit)

            assertFalse(busy.pad.canSave)
            assertFalse(busy.canDelete)
            assertEquals(1, records.replaced.size)
            assertTrue(records.deleted.isEmpty())
        }

    @Test
    fun `deleting asks first and keeping the record deletes nothing`() =
        runTest {
            val viewModel = viewModel()

            viewModel.onDeleteQuestion(isAsking = true)
            val asking = viewModel.editing.isAskingToDelete
            viewModel.onDeleteQuestion(isAsking = false)

            assertTrue(asking)
            assertFalse(viewModel.editing.isAskingToDelete)
            assertTrue(records.deleted.isEmpty())
        }

    @Test
    fun `confirming deletes the record and finishes with it deleted`() =
        runTest {
            val viewModel = viewModel()
            viewModel.onDeleteQuestion(isAsking = true)

            viewModel.onDeleteConfirmed()

            assertEquals(listOf(written.id), records.deleted)
            assertEquals(RecordChange.Deleted(written.id), viewModel.editing.finished)
        }

    @Test
    fun `a failed delete says so and the record is still there to correct`() =
        runTest {
            val viewModel = viewModel()
            records.failing = true
            viewModel.onDeleteQuestion(isAsking = true)

            viewModel.onDeleteConfirmed()

            val state = viewModel.editing
            assertTrue(state.deleteFailed)
            assertFalse(state.isAskingToDelete)
            assertTrue(state.canDelete)
            assertNull(state.finished)
        }

    @Test
    fun `what was changed comes back after the process was killed, not what was written down`() =
        runTest {
            val first = viewModel()
            first.retype("15,2")
            first.onCategoryChosen(transport.id)
            first.onNoteChanged("tram")

            // A new view model on the same saved state is what a restored process gets.
            val restored = viewModel(record = null)

            val state = restored.editing
            assertEquals("15", state.pad.amount.whole)
            assertEquals("2", state.pad.amount.fraction)
            assertEquals(transport.id, state.pad.selectedCategoryId)
            assertEquals(DayLabel.Yesterday, state.pad.day)
            assertEquals("tram", state.pad.note)
            assertTrue(state.pad.canSave)
        }

    @Test
    fun `a category no longer offered stays on the pad of a record filed under it`() =
        runTest {
            val offered = FakeCategoryRepository(builtinCategories.drop(1))
            val viewModel = viewModel(categories = offered)

            assertEquals(groceries.id, viewModel.editing.pad.selectedCategoryId)
            assertEquals(
                groceries,
                viewModel.editing.pad.categories
                    .last(),
            )
        }

    private companion object {
        // More backspaces than any amount in these tests has keys.
        const val AMOUNT_KEYS = 12
    }
}
