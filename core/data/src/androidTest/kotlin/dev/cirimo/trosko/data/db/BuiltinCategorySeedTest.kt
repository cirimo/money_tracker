package dev.cirimo.trosko.data.db

import android.content.Context
import androidx.room3.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.cirimo.trosko.data.repository.toDomain
import dev.cirimo.trosko.domain.model.BuiltinCategories
import dev.cirimo.trosko.domain.model.CategoryName
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BuiltinCategorySeedTest {
    private lateinit var database: TroskoDatabase

    @Before
    fun openDatabase() {
        database =
            Room
                .inMemoryDatabaseBuilder<TroskoDatabase>(ApplicationProvider.getApplicationContext<Context>())
                .setDriver(BundledSQLiteDriver())
                .addCallback(BuiltinCategorySeed())
                .build()
    }

    @After
    fun closeDatabase() {
        database.close()
    }

    @Test
    fun newDatabaseHoldsEveryBuiltinCategoryInOrder() =
        runTest {
            val rows = database.categoryDao().observeActive(KIND_EXPENSE).first()

            assertEquals(BuiltinCategories.all.map { it.id.value.toString() }, rows.map { it.id })
        }

    @Test
    fun seededRowsReadBackAsTheCategoriesThatWereSeeded() =
        runTest {
            val categories =
                database
                    .categoryDao()
                    .observeActive(KIND_EXPENSE)
                    .first()
                    .map { it.toDomain() }

            BuiltinCategories.all.zip(categories).forEach { (seed, category) ->
                assertEquals(seed.id, category.id)
                assertEquals(CategoryName.Builtin(seed.key), category.name)
                assertEquals(seed.colour, category.colour)
                assertEquals(seed.icon, category.icon)
                assertEquals(seed.sortOrder, category.sortOrder)
            }
        }

    @Test
    fun seedTimestampsAreZeroSoAnyLaterChangeIsNewer() =
        runTest {
            val rows = database.categoryDao().observeActive(KIND_EXPENSE).first()

            assertTrue(rows.all { it.createdAt == 0L && it.updatedAt == 0L })
        }
}
