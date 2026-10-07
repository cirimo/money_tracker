package dev.cirimo.trosko.feature.home

import dev.cirimo.trosko.domain.model.NewRecord
import dev.cirimo.trosko.domain.model.NewRecordResult
import dev.cirimo.trosko.domain.model.RecordId
import dev.cirimo.trosko.domain.money.Money
import dev.cirimo.trosko.format.DayLabel
import dev.cirimo.trosko.format.RecordChange
import dev.cirimo.trosko.format.RecordNotice
import dev.cirimo.trosko.testing.FakeRecordRepository
import dev.cirimo.trosko.testing.SettableClock
import dev.cirimo.trosko.testing.builtinCategories
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
class HomeViewModelTest {
    private val euro = Currency.getInstance("EUR")
    private val today = LocalDate.of(2026, 10, 7)
    private val clock = SettableClock(today)

    @Before
    fun replaceMainDispatcher() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun restoreMainDispatcher() {
        Dispatchers.resetMain()
    }

    private fun TestScope.viewModel(records: FakeRecordRepository): HomeViewModel {
        val viewModel = HomeViewModel(records, clock)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }
        return viewModel
    }

    private suspend fun FakeRecordRepository.addExpense(
        minorUnits: Long,
        occurredOn: LocalDate,
    ) {
        val checked = NewRecord.expense(Money(minorUnits, euro), builtinCategories[0].id, occurredOn, null, today)
        add((checked as NewRecordResult.Valid).record)
    }

    @Test
    fun `state is loading until storage answers`() =
        runTest {
            val viewModel = viewModel(FakeRecordRepository(clock, answered = false))

            assertEquals(HomeUiState.Loading, viewModel.uiState.value)
        }

    @Test
    fun `a new install is loaded and empty`() =
        runTest {
            val viewModel = viewModel(FakeRecordRepository(clock))

            assertEquals(HomeUiState.Loaded(emptyList()), viewModel.uiState.value)
        }

    @Test
    fun `records come newest first, as expenses, named by their day`() =
        runTest {
            val records = FakeRecordRepository(clock)
            val viewModel = viewModel(records)

            records.addExpense(100, today.minusDays(1))
            records.addExpense(250, today)

            val latest = (viewModel.uiState.value as HomeUiState.Loaded).latest
            assertEquals(listOf(Money(-250, euro), Money(-100, euro)), latest.map { it.signedAmount })
            assertEquals(listOf(DayLabel.Today, DayLabel.Yesterday), latest.map { it.day })
        }

    @Test
    fun `a corrected record lands again until the landing has been shown`() =
        runTest {
            val records = FakeRecordRepository(clock)
            val viewModel = viewModel(records)
            records.addExpense(100, today)
            val id = (viewModel.uiState.value as HomeUiState.Loaded).latest.single().id

            viewModel.onRecordChanged(RecordChange.Corrected(id))
            val pending = viewModel.uiState.value as HomeUiState.Loaded
            viewModel.onLandingShown()
            val shown = viewModel.uiState.value as HomeUiState.Loaded

            assertEquals(RecordNotice.Corrected, pending.notice)
            assertEquals(id, pending.landedId)
            assertTrue(pending.isLandingPending)
            assertFalse(shown.isLandingPending)
            assertEquals(id, shown.landedId)
        }

    @Test
    fun `a deleted record is said to be deleted and nothing lands`() =
        runTest {
            val records = FakeRecordRepository(clock)
            val viewModel = viewModel(records)

            viewModel.onRecordChanged(RecordChange.Deleted(RecordId(UUID.randomUUID())))

            val state = viewModel.uiState.value as HomeUiState.Loaded
            assertEquals(RecordNotice.Deleted, state.notice)
            assertNull(state.landedId)
            assertFalse(state.isLandingPending)
        }

    @Test
    fun `turning to another page takes the word about the last change away`() =
        runTest {
            val viewModel = viewModel(FakeRecordRepository(clock))
            viewModel.onRecordChanged(RecordChange.Deleted(RecordId(UUID.randomUUID())))

            viewModel.onLeaving()

            assertEquals(HomeUiState.Loaded(emptyList()), viewModel.uiState.value)
        }

    @Test
    fun `coming back the next day renames the days`() =
        runTest {
            val records = FakeRecordRepository(clock)
            val viewModel = viewModel(records)
            records.addExpense(100, today)
            clock.today = today.plusDays(1)

            viewModel.onResumed()

            val latest = (viewModel.uiState.value as HomeUiState.Loaded).latest
            assertEquals(DayLabel.Yesterday, latest.single().day)
        }
}
