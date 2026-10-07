package dev.cirimo.trosko.feature.expense

import androidx.lifecycle.SavedStateHandle
import dev.cirimo.trosko.domain.model.NewRecord
import dev.cirimo.trosko.domain.money.AmountKey
import dev.cirimo.trosko.domain.money.Money
import dev.cirimo.trosko.format.DayLabel
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

// Replacing the main dispatcher is how a view model's scope is driven from a test.
@OptIn(ExperimentalCoroutinesApi::class)
class ExpenseEntryViewModelTest {
    private val euro = Currency.getInstance("EUR")
    private val today = LocalDate.of(2026, 10, 7)
    private val groceries = builtinCategories[0]
    private val transport = builtinCategories[2]
    private val clock = SettableClock(today)
    private val records = FakeRecordRepository(clock)
    private val savedState = SavedStateHandle()

    @Before
    fun replaceMainDispatcher() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun restoreMainDispatcher() {
        Dispatchers.resetMain()
    }

    // Without a collector `stateIn` never starts, so every test collects in the background.
    private fun TestScope.viewModel(): ExpenseEntryViewModel {
        val viewModel = ExpenseEntryViewModel(FakeCategoryRepository(), records, clock, savedState)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }
        return viewModel
    }

    private fun ExpenseEntryViewModel.type(amount: String) {
        amount.forEach { char ->
            onKey(if (char == ',') AmountKey.Separator else AmountKey.Digit(char.digitToInt()))
        }
    }

    private val ExpenseEntryViewModel.state get() = uiState.value

    @Test
    fun `a new form offers the categories and cannot be saved`() =
        runTest {
            val viewModel = viewModel()

            assertEquals(builtinCategories, viewModel.state.categories)
            assertTrue(viewModel.state.amount.isEmpty)
            assertNull(viewModel.state.selectedCategoryId)
            assertEquals(DayLabel.Today, viewModel.state.day)
            assertFalse(viewModel.state.canSave)
        }

    @Test
    fun `an amount without a category cannot be saved`() =
        runTest {
            val viewModel = viewModel()

            viewModel.type("12,50")

            assertFalse(viewModel.state.canSave)
        }

    @Test
    fun `a category without an amount cannot be saved`() =
        runTest {
            val viewModel = viewModel()

            viewModel.onCategoryChosen(groceries.id)

            assertFalse(viewModel.state.canSave)
        }

    @Test
    fun `zero cannot be saved however it is typed`() =
        runTest {
            val viewModel = viewModel()
            viewModel.onCategoryChosen(groceries.id)

            viewModel.type("0,00")
            viewModel.onSave()

            assertFalse(viewModel.state.canSave)
            assertTrue(records.added.isEmpty())
        }

    @Test
    fun `one cent and a category can be saved`() =
        runTest {
            val viewModel = viewModel()

            viewModel.type("0,01")
            viewModel.onCategoryChosen(groceries.id)

            assertTrue(viewModel.state.canSave)
        }

    @Test
    fun `choosing the chosen category again lets go of it`() =
        runTest {
            val viewModel = viewModel()

            viewModel.onCategoryChosen(groceries.id)
            viewModel.onCategoryChosen(groceries.id)

            assertNull(viewModel.state.selectedCategoryId)
        }

    @Test
    fun `saving writes down what was entered`() =
        runTest {
            val viewModel = viewModel()
            viewModel.type("12,5")
            viewModel.onCategoryChosen(transport.id)
            viewModel.onNoteChanged(" tram ")

            viewModel.onSave()

            val written = records.added.single()
            assertEquals(Money(1_250, euro), written.amount)
            assertEquals(transport.id, written.categoryId)
            assertEquals(today, written.occurredOn)
            assertEquals("tram", written.note)
        }

    @Test
    fun `after a save the form is empty and the record is waiting to land`() =
        runTest {
            val viewModel = viewModel()
            viewModel.type("12,50")
            viewModel.onCategoryChosen(groceries.id)
            viewModel.onNoteChanged("bread")

            viewModel.onSave()

            val state = viewModel.state
            assertTrue(state.amount.isEmpty)
            assertNull(state.selectedCategoryId)
            assertEquals("", state.note)
            assertFalse(state.canSave)
            assertTrue(state.showsSavedNote)
            assertTrue(state.isLandingPending)
            assertEquals(state.latest.first().id, state.landedId)
            assertEquals(Money(-1_250, euro), state.latest.first().signedAmount)
        }

    @Test
    fun `once the landing has been shown it is not asked for again but the record stays the landed one`() =
        runTest {
            val viewModel = viewModel()
            viewModel.type("3")
            viewModel.onCategoryChosen(groceries.id)
            viewModel.onSave()

            viewModel.onLandingShown()

            assertFalse(viewModel.state.isLandingPending)
            assertEquals(
                viewModel.state.latest
                    .first()
                    .id,
                viewModel.state.landedId,
            )
        }

    @Test
    fun `the next key takes the confirmation away`() =
        runTest {
            val viewModel = viewModel()
            viewModel.type("3")
            viewModel.onCategoryChosen(groceries.id)
            viewModel.onSave()

            viewModel.onKey(AmountKey.Digit(4))

            assertFalse(viewModel.state.showsSavedNote)
        }

    @Test
    fun `a failed save says so and keeps everything that was typed`() =
        runTest {
            val viewModel = viewModel()
            records.failing = true
            viewModel.type("12,50")
            viewModel.onCategoryChosen(groceries.id)
            viewModel.onNoteChanged("bread")

            viewModel.onSave()

            val state = viewModel.state
            assertTrue(state.saveFailed)
            assertEquals("12", state.amount.whole)
            assertEquals(groceries.id, state.selectedCategoryId)
            assertEquals("bread", state.note)
            assertTrue(state.canSave)
            assertNull(state.landedId)
        }

    @Test
    fun `trying again after a failure saves and clears the message`() =
        runTest {
            val viewModel = viewModel()
            records.failing = true
            viewModel.type("12,50")
            viewModel.onCategoryChosen(groceries.id)
            viewModel.onSave()
            records.failing = false

            viewModel.onSave()

            assertFalse(viewModel.state.saveFailed)
            assertTrue(viewModel.state.showsSavedNote)
        }

    @Test
    fun `a second press while the first save is under way writes nothing twice`() =
        runTest {
            val viewModel = viewModel()
            val gate = CompletableDeferred<Unit>()
            records.gate = gate
            viewModel.type("5")
            viewModel.onCategoryChosen(groceries.id)

            viewModel.onSave()
            val canSaveWhileSaving = viewModel.state.canSave
            viewModel.onSave()
            gate.complete(Unit)

            assertFalse(canSaveWhileSaving)
            assertEquals(1, records.added.size)
        }

    @Test
    fun `stepping back a day is yesterday and the record is filed there`() =
        runTest {
            val viewModel = viewModel()
            viewModel.type("5")
            viewModel.onCategoryChosen(groceries.id)

            viewModel.onDayBack()
            val label = viewModel.state.day
            viewModel.onSave()

            assertEquals(DayLabel.Yesterday, label)
            assertEquals(today.minusDays(1), records.added.single().occurredOn)
        }

    @Test
    fun `stepping further back names the date`() =
        runTest {
            val viewModel = viewModel()

            viewModel.onDayBack()
            viewModel.onDayBack()

            assertEquals(DayLabel.On(today.minusDays(2)), viewModel.state.day)
        }

    @Test
    fun `the date cannot be stepped past today`() =
        runTest {
            val viewModel = viewModel()
            val forwardOnToday = viewModel.state.canStepDayForward
            viewModel.onDayBack()
            val forwardOnYesterday = viewModel.state.canStepDayForward

            viewModel.onDayForward()
            viewModel.onDayForward()

            assertFalse(forwardOnToday)
            assertTrue(forwardOnYesterday)
            assertEquals(DayLabel.Today, viewModel.state.day)
            assertFalse(viewModel.state.canStepDayForward)
        }

    @Test
    fun `the date goes back to today after a save`() =
        runTest {
            val viewModel = viewModel()
            viewModel.type("5")
            viewModel.onCategoryChosen(groceries.id)
            viewModel.onDayBack()

            viewModel.onSave()

            assertEquals(DayLabel.Today, viewModel.state.day)
        }

    @Test
    fun `a form left on today over midnight files under the new day`() =
        runTest {
            val viewModel = viewModel()
            viewModel.type("5")
            viewModel.onCategoryChosen(groceries.id)
            clock.today = today.plusDays(1)

            viewModel.onSave()

            assertEquals(today.plusDays(1), records.added.single().occurredOn)
        }

    @Test
    fun `a picked day keeps its date over midnight and is then named differently`() =
        runTest {
            val viewModel = viewModel()
            viewModel.onDayBack()
            clock.today = today.plusDays(1)

            viewModel.onResumed()

            assertEquals(DayLabel.On(today.minusDays(1)), viewModel.state.day)
        }

    @Test
    fun `a note is cut at the longest length that is kept`() =
        runTest {
            val viewModel = viewModel()

            viewModel.onNoteChanged("a".repeat(NewRecord.NOTE_MAX_LENGTH + 10))

            assertEquals(NewRecord.NOTE_MAX_LENGTH, viewModel.state.note.length)
        }

    @Test
    fun `what was typed comes back after the process was killed`() =
        runTest {
            val first = viewModel()
            first.type("12,5")
            first.onCategoryChosen(transport.id)
            first.onDayBack()
            first.onNoteChanged("tram")

            // A new view model on the same saved state is what a restored process gets.
            val restored = viewModel()

            assertEquals("12", restored.state.amount.whole)
            assertEquals("5", restored.state.amount.fraction)
            assertEquals(transport.id, restored.state.selectedCategoryId)
            assertEquals(DayLabel.Yesterday, restored.state.day)
            assertEquals("tram", restored.state.note)
            assertTrue(restored.state.canSave)
        }
}
