package dev.cirimo.trosko.data.db

import android.content.Context
import androidx.room3.Room
import androidx.sqlite.SQLiteException
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TroskoDatabaseTest {
    private lateinit var database: TroskoDatabase

    @Before
    fun openDatabase() {
        database =
            Room
                .inMemoryDatabaseBuilder<TroskoDatabase>(ApplicationProvider.getApplicationContext<Context>())
                .setDriver(BundledSQLiteDriver())
                .build()
    }

    @After
    fun closeDatabase() {
        database.close()
    }

    @Test
    fun activeCategoriesComeBackInSortOrderWithoutArchivedOnes() =
        runTest {
            val dao = database.categoryDao()
            dao.insert(category(id = "c-second", kind = KIND_EXPENSE, sortOrder = 2))
            dao.insert(category(id = "c-first", kind = KIND_EXPENSE, sortOrder = 1))
            dao.insert(category(id = "c-archived", kind = KIND_EXPENSE, sortOrder = 0, archivedAt = 5L))
            dao.insert(category(id = "c-income", kind = KIND_INCOME, sortOrder = 0))

            val active = dao.observeActive(KIND_EXPENSE).first()

            assertEquals(listOf("c-first", "c-second"), active.map { it.id })
        }

    @Test
    fun recordsInRangeComeBackNewestFirstAndExcludeTheEndDate() =
        runTest {
            database.categoryDao().insert(category(id = "c-food", kind = KIND_EXPENSE))
            val dao = database.recordDao()
            dao.insert(record(id = "r-september", occurredOn = "2026-09-30"))
            dao.insert(record(id = "r-first", occurredOn = "2026-10-01"))
            dao.insert(record(id = "r-last-early", occurredOn = "2026-10-31", createdAt = 1L))
            dao.insert(record(id = "r-last-late", occurredOn = "2026-10-31", createdAt = 2L))
            dao.insert(record(id = "r-november", occurredOn = "2026-11-01"))

            val october = dao.observeInRange(fromInclusive = "2026-10-01", toExclusive = "2026-11-01").first()

            assertEquals(listOf("r-last-late", "r-last-early", "r-first"), october.map { it.id })
        }

    @Test
    fun recordFiledUnderACategoryOfTheOtherKindIsRejected() =
        runTest {
            database.categoryDao().insert(category(id = "c-salary", kind = KIND_INCOME))
            val expenseUnderIncomeSource = record(id = "r-wrong", kind = KIND_EXPENSE, categoryId = "c-salary")

            assertTrue(isRejected(expenseUnderIncomeSource))
        }

    @Test
    fun recordFiledUnderAMissingCategoryIsRejected() =
        runTest {
            val orphan = record(id = "r-orphan", categoryId = "c-does-not-exist")

            assertTrue(isRejected(orphan))
        }

    private suspend fun isRejected(record: RecordEntity): Boolean =
        try {
            database.recordDao().insert(record)
            false
        } catch (_: SQLiteException) {
            true
        }

    private fun category(
        id: String,
        kind: String,
        sortOrder: Int = 0,
        archivedAt: Long? = null,
    ) = CategoryEntity(
        id = id,
        kind = kind,
        builtinKey = null,
        customName = id,
        colour = "GREEN",
        icon = "BASKET",
        sortOrder = sortOrder,
        archivedAt = archivedAt,
        createdAt = 0L,
        updatedAt = 0L,
    )

    private fun record(
        id: String,
        kind: String = KIND_EXPENSE,
        categoryId: String = "c-food",
        occurredOn: String = "2026-10-07",
        createdAt: Long = 0L,
    ) = RecordEntity(
        id = id,
        kind = kind,
        amountMinor = 1_250L,
        currency = "EUR",
        categoryId = categoryId,
        occurredOn = occurredOn,
        note = null,
        createdAt = createdAt,
        updatedAt = createdAt,
    )
}
