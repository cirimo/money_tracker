package dev.cirimo.trosko.data.repository

import androidx.sqlite.SQLiteException
import dev.cirimo.trosko.data.db.RecordDao
import dev.cirimo.trosko.data.db.RecordEntity
import dev.cirimo.trosko.data.db.RecordWithCategory
import dev.cirimo.trosko.data.db.recordKindFromColumn
import dev.cirimo.trosko.data.db.toColumn
import dev.cirimo.trosko.domain.model.NewRecord
import dev.cirimo.trosko.domain.model.Record
import dev.cirimo.trosko.domain.model.RecordId
import dev.cirimo.trosko.domain.money.Money
import dev.cirimo.trosko.domain.repository.RecordRepository
import dev.cirimo.trosko.domain.repository.SaveResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.util.Currency
import java.util.UUID

/**
 * @param writeScope outlives every screen. A write is started in it and only awaited by the
 * caller, so leaving the screen cancels the waiting and never the write.
 * @param newId where record ids come from; replaced in tests that need to know the id.
 */
internal class RoomRecordRepository(
    private val dao: RecordDao,
    private val writeScope: CoroutineScope,
    private val clock: Clock,
    private val newId: () -> UUID = UUID::randomUUID,
) : RecordRepository {
    override suspend fun add(record: NewRecord): SaveResult = writeScope.async { insert(record) }.await()

    override fun observeLatest(limit: Int): Flow<List<Record>> =
        dao.observeLatest(limit).map { rows -> rows.map { it.toDomain() } }

    private suspend fun insert(record: NewRecord): SaveResult {
        val id = RecordId(newId())
        val now = clock.millis()
        val row =
            RecordEntity(
                id = id.value.toString(),
                kind = record.kind.toColumn(),
                amountMinor = record.amount.minorUnits,
                currency = record.amount.currency.currencyCode,
                categoryId = record.categoryId.value.toString(),
                occurredOn = record.occurredOn.toString(),
                note = record.note,
                createdAt = now,
                updatedAt = now,
            )
        return try {
            dao.insert(row)
            SaveResult.Saved(id)
        } catch (_: SQLiteException) {
            // The insert is one statement, so a refusal leaves the table exactly as it was.
            SaveResult.NotSaved
        }
    }
}

private fun RecordWithCategory.toDomain(): Record =
    Record(
        id = RecordId(UUID.fromString(record.id)),
        kind = recordKindFromColumn(record.kind),
        amount = Money(record.amountMinor, Currency.getInstance(record.currency)),
        category = category.toDomain(),
        occurredOn = LocalDate.parse(record.occurredOn),
        note = record.note,
        createdAt = Instant.ofEpochMilli(record.createdAt),
    )
