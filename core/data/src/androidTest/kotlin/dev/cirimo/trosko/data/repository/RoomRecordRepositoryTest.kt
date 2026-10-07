package dev.cirimo.trosko.data.repository

import android.content.Context
import androidx.room3.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.cirimo.trosko.data.db.BuiltinCategorySeed
import dev.cirimo.trosko.data.db.RecordEntity
import dev.cirimo.trosko.data.db.TroskoDatabase
import dev.cirimo.trosko.domain.model.BuiltinCategories
import dev.cirimo.trosko.domain.model.CategoryId
import dev.cirimo.trosko.domain.model.NewRecord
import dev.cirimo.trosko.domain.model.NewRecordResult
import dev.cirimo.trosko.domain.model.RecordId
import dev.cirimo.trosko.domain.model.RecordKind
import dev.cirimo.trosko.domain.money.Money
import dev.cirimo.trosko.domain.repository.DeleteResult
import dev.cirimo.trosko.domain.repository.ReplaceResult
import dev.cirimo.trosko.domain.repository.SaveResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.util.Currency
import java.util.UUID

private const val TIMEOUT_MILLIS = 5_000L

@RunWith(AndroidJUnit4::class)
class RoomRecordRepositoryTest {
    private val euro = Currency.getInstance("EUR")
    private val today = LocalDate.of(2026, 10, 7)
    private val groceries = BuiltinCategories.all[0]
    private val transport = BuiltinCategories.all[2]
    private val clock = SteppingClock()
    private val writeScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private lateinit var database: TroskoDatabase
    private lateinit var repository: RoomRecordRepository

    @Before
    fun openDatabase() {
        database =
            Room
                .inMemoryDatabaseBuilder<TroskoDatabase>(ApplicationProvider.getApplicationContext<Context>())
                .setDriver(BundledSQLiteDriver())
                .addCallback(BuiltinCategorySeed())
                .build()
        repository = RoomRecordRepository(database.recordDao(), writeScope, clock)
    }

    @After
    fun closeDatabase() {
        writeScope.cancel()
        database.close()
    }

    @Test
    fun savedExpenseReadsBackWithItsCategory() =
        runBlocking {
            val saved = repository.add(expense(1_250, groceries.id, today.minusDays(1), "bread"))

            val record = repository.observeLatest(limit = 10).first().single()
            assertEquals(SaveResult.Saved(record.id), saved)
            assertEquals(RecordKind.EXPENSE, record.kind)
            assertEquals(Money(1_250, euro), record.amount)
            assertEquals(groceries.id, record.category.id)
            assertEquals(groceries.colour, record.category.colour)
            assertEquals(today.minusDays(1), record.occurredOn)
            assertEquals("bread", record.note)
        }

    @Test
    fun expenseWithoutANoteReadsBackWithoutOne() =
        runBlocking {
            repository.add(expense(100, groceries.id))

            assertNull(
                repository
                    .observeLatest(limit = 10)
                    .first()
                    .single()
                    .note,
            )
        }

    @Test
    fun largestAmountSurvivesTheRoundTrip() =
        runBlocking {
            repository.add(expense(Long.MAX_VALUE, groceries.id))

            assertEquals(
                Long.MAX_VALUE,
                repository
                    .observeLatest(limit = 10)
                    .first()
                    .single()
                    .amount.minorUnits,
            )
        }

    @Test
    fun latestComeBackByWhenTheyWereWrittenNotByTheirDate() =
        runBlocking {
            repository.add(expense(100, groceries.id, today))
            repository.add(expense(200, transport.id, today.minusDays(30)))
            repository.add(expense(300, groceries.id, today.minusDays(1)))

            val latest = repository.observeLatest(limit = 2).first()

            assertEquals(listOf(300L, 200L), latest.map { it.amount.minorUnits })
        }

    @Test
    fun expenseFiledUnderAMissingCategoryIsNotSavedAndLeavesNothingBehind() =
        runBlocking {
            val result = repository.add(expense(100, CategoryId(UUID.randomUUID())))

            assertEquals(SaveResult.NotSaved, result)
            assertTrue(repository.observeLatest(limit = 10).first().isEmpty())
        }

    @Test
    fun writeFinishesEvenWhenTheCallerIsCancelledFirst() =
        runBlocking {
            val callerScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
            // Undispatched, so the write has been handed to the write scope before the cancel.
            callerScope.launch(start = CoroutineStart.UNDISPATCHED) { repository.add(expense(100, groceries.id)) }
            callerScope.cancel()

            val latest = withTimeout(TIMEOUT_MILLIS) { repository.observeLatest(limit = 10).first { it.isNotEmpty() } }

            assertEquals(100L, latest.single().amount.minorUnits)
        }

    @Test
    fun correctedRecordSaysTheNewThingsAndKeepsItsIdAndPlace() =
        runBlocking {
            val saved = repository.add(expense(1_250, groceries.id, today, "bread")) as SaveResult.Saved
            repository.add(expense(300, groceries.id))
            val before = storedRow(saved.id)

            val result = repository.replace(saved.id, expense(1_520, transport.id, today.minusDays(3), "tram"))

            val after = storedRow(saved.id)
            val record = repository.observe(saved.id).first()
            assertEquals(ReplaceResult.Replaced, result)
            assertEquals(Money(1_520, euro), record?.amount)
            assertEquals(transport.id, record?.category?.id)
            assertEquals(today.minusDays(3), record?.occurredOn)
            assertEquals("tram", record?.note)
            assertEquals(before.createdAt, after.createdAt)
            assertTrue(after.updatedAt > before.updatedAt)
            // It was written first, so it is still second among the latest.
            assertEquals(saved.id, repository.observeLatest(limit = 10).first()[1].id)
        }

    @Test
    fun correctionFiledUnderAMissingCategoryIsRefusedAndChangesNothing() =
        runBlocking {
            val saved = repository.add(expense(1_250, groceries.id, today, "bread")) as SaveResult.Saved
            val before = storedRow(saved.id)

            val result = repository.replace(saved.id, expense(999, CategoryId(UUID.randomUUID())))

            assertEquals(ReplaceResult.NotSaved, result)
            assertEquals(before, storedRow(saved.id))
        }

    @Test
    fun correctingARecordThatIsNotThereSaysItIsGoneAndWritesNothing() =
        runBlocking {
            val result = repository.replace(RecordId(UUID.randomUUID()), expense(100, groceries.id))

            assertEquals(ReplaceResult.Gone, result)
            assertTrue(repository.observeLatest(limit = 10).first().isEmpty())
        }

    @Test
    fun deletedRecordIsReallyGoneAndTheOthersStay() =
        runBlocking {
            val doomed = repository.add(expense(100, groceries.id)) as SaveResult.Saved
            val kept = repository.add(expense(200, transport.id)) as SaveResult.Saved

            val result = repository.delete(doomed.id)

            assertEquals(DeleteResult.Deleted, result)
            assertNull(repository.observe(doomed.id).first())
            assertEquals(listOf(kept.id.value.toString()), allRows().map { it.id })
        }

    @Test
    fun deletingARecordThatIsNotThereSaysItIsGone() =
        runBlocking {
            repository.add(expense(100, groceries.id))

            val result = repository.delete(RecordId(UUID.randomUUID()))

            assertEquals(DeleteResult.Gone, result)
            assertEquals(1, allRows().size)
        }

    @Test
    fun deleteFinishesEvenWhenTheCallerIsCancelledFirst() =
        runBlocking {
            val saved = repository.add(expense(100, groceries.id)) as SaveResult.Saved
            val callerScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
            callerScope.launch(start = CoroutineStart.UNDISPATCHED) { repository.delete(saved.id) }
            callerScope.cancel()

            val latest = withTimeout(TIMEOUT_MILLIS) { repository.observeLatest(limit = 10).first { it.isEmpty() } }

            assertTrue(latest.isEmpty())
        }

    // Every row as the table holds it, timestamps included, which the domain does not show.
    private suspend fun allRows(): List<RecordEntity> = database.recordDao().observeInRange("0000", "9999").first()

    private suspend fun storedRow(id: RecordId): RecordEntity = allRows().single { it.id == id.value.toString() }

    private fun expense(
        minorUnits: Long,
        categoryId: CategoryId,
        occurredOn: LocalDate = today,
        note: String? = null,
    ): NewRecord =
        (NewRecord.expense(Money(minorUnits, euro), categoryId, occurredOn, note, today) as NewRecordResult.Valid)
            .record
}

/** A clock that moves forward a second every time it is read, so writes have distinct times. */
private class SteppingClock : Clock() {
    private var now = Instant.parse("2026-10-07T12:00:00Z")

    override fun instant(): Instant {
        now = now.plusSeconds(1)
        return now
    }

    override fun getZone() = ZoneOffset.UTC

    override fun withZone(zone: java.time.ZoneId?): Clock = this
}
