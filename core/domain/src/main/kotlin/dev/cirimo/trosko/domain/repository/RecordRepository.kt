package dev.cirimo.trosko.domain.repository

import dev.cirimo.trosko.domain.model.NewRecord
import dev.cirimo.trosko.domain.model.Record
import kotlinx.coroutines.flow.Flow

interface RecordRepository {
    /**
     * Writes [record] down. The write is not cancelled when the caller is: a record the user
     * asked to save is saved even if they have already left the screen.
     */
    suspend fun add(record: NewRecord): SaveResult

    /** The [limit] records written down most recently, newest first, whatever their dates. */
    fun observeLatest(limit: Int): Flow<List<Record>>
}
