package dev.cirimo.trosko.testing

import dev.cirimo.trosko.domain.model.BuiltinCategories
import dev.cirimo.trosko.domain.model.Category
import dev.cirimo.trosko.domain.model.CategoryName
import dev.cirimo.trosko.domain.model.NewRecord
import dev.cirimo.trosko.domain.model.Record
import dev.cirimo.trosko.domain.model.RecordId
import dev.cirimo.trosko.domain.model.RecordKind
import dev.cirimo.trosko.domain.repository.CategoryRepository
import dev.cirimo.trosko.domain.repository.DeleteResult
import dev.cirimo.trosko.domain.repository.RecordRepository
import dev.cirimo.trosko.domain.repository.ReplaceResult
import dev.cirimo.trosko.domain.repository.SaveResult
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.UUID

/** The built-in categories as the repository would hand them out. */
val builtinCategories: List<Category> =
    BuiltinCategories.all.map {
        Category(it.id, it.kind, CategoryName.Builtin(it.key), it.colour, it.icon, it.sortOrder, archivedAt = null)
    }

class FakeCategoryRepository(
    categories: List<Category> = builtinCategories,
) : CategoryRepository {
    private val active = MutableStateFlow(categories)

    override fun observeActive(kind: RecordKind): Flow<List<Category>> =
        active.map { all -> all.filter { it.kind == kind } }
}

/**
 * Keeps records in memory. A test can make the next writes fail with [failing], or hold a write
 * open with [gate] to look at the state while it is under way.
 *
 * @param answered false to behave like storage that has not answered yet.
 */
class FakeRecordRepository(
    private val clock: Clock,
    answered: Boolean = true,
) : RecordRepository {
    private val stored = MutableStateFlow(if (answered) emptyList<Record>() else null)

    var failing = false
    var gate: CompletableDeferred<Unit>? = null
    val added = mutableListOf<NewRecord>()
    val replaced = mutableListOf<Pair<RecordId, NewRecord>>()
    val deleted = mutableListOf<RecordId>()

    /** Puts [record] in storage as if it had been written down earlier. */
    fun seed(record: Record) {
        stored.value = listOf(record) + stored.value.orEmpty()
    }

    override suspend fun add(record: NewRecord): SaveResult {
        added += record
        gate?.await()
        if (failing) return SaveResult.NotSaved
        val id = RecordId(UUID.randomUUID())
        val category = builtinCategories.first { it.id == record.categoryId }
        val saved = Record(id, record.kind, record.amount, category, record.occurredOn, record.note, clock.instant())
        stored.value = listOf(saved) + stored.value.orEmpty()
        return SaveResult.Saved(id)
    }

    override suspend fun replace(
        id: RecordId,
        record: NewRecord,
    ): ReplaceResult {
        replaced += id to record
        gate?.await()
        val current = stored.value.orEmpty()
        val old = current.firstOrNull { it.id == id }
        return when {
            failing -> {
                ReplaceResult.NotSaved
            }

            old == null -> {
                ReplaceResult.Gone
            }

            else -> {
                val category = builtinCategories.first { it.id == record.categoryId }
                val new =
                    old.copy(
                        amount = record.amount,
                        category = category,
                        occurredOn = record.occurredOn,
                        note = record.note,
                    )
                stored.value = current.map { if (it.id == id) new else it }
                ReplaceResult.Replaced
            }
        }
    }

    override suspend fun delete(id: RecordId): DeleteResult {
        deleted += id
        gate?.await()
        val current = stored.value.orEmpty()
        return when {
            failing -> {
                DeleteResult.NotDeleted
            }

            current.none { it.id == id } -> {
                DeleteResult.Gone
            }

            else -> {
                stored.value = current.filterNot { it.id == id }
                DeleteResult.Deleted
            }
        }
    }

    override fun observe(id: RecordId): Flow<Record?> =
        stored.filterNotNull().map { all -> all.firstOrNull { it.id == id } }

    override fun observeLatest(limit: Int): Flow<List<Record>> = stored.filterNotNull().map { it.take(limit) }
}

/** A clock a test sets by hand. */
class SettableClock(
    var today: LocalDate,
) : Clock() {
    override fun instant(): Instant = today.atTime(NOON_HOUR, 0).toInstant(ZoneOffset.UTC)

    override fun getZone(): ZoneId = ZoneOffset.UTC

    override fun withZone(zone: ZoneId): Clock = this

    private companion object {
        const val NOON_HOUR = 12
    }
}
