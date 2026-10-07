package dev.cirimo.trosko.domain.repository

import dev.cirimo.trosko.domain.model.NewRecord
import dev.cirimo.trosko.domain.model.Record
import dev.cirimo.trosko.domain.model.RecordId
import kotlinx.coroutines.flow.Flow

interface RecordRepository {
    /**
     * Writes [record] down. The write is not cancelled when the caller is: a record the user
     * asked to save is saved even if they have already left the screen.
     */
    suspend fun add(record: NewRecord): SaveResult

    /**
     * Corrects the record [id] so that it says what [record] says. When it was written down, and
     * so its place among the latest, stays as it was. Like [add], the write outlives its caller.
     */
    suspend fun replace(
        id: RecordId,
        record: NewRecord,
    ): ReplaceResult

    /** Removes the record [id] for good. Like [add], the write outlives its caller. */
    suspend fun delete(id: RecordId): DeleteResult

    /** The record [id] as it is now, and null once it does not exist. */
    fun observe(id: RecordId): Flow<Record?>

    /** The [limit] records written down most recently, newest first, whatever their dates. */
    fun observeLatest(limit: Int): Flow<List<Record>>
}
