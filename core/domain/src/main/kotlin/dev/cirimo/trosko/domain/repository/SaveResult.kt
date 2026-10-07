package dev.cirimo.trosko.domain.repository

import dev.cirimo.trosko.domain.model.RecordId

/** Whether a write reached storage. Writes are transactional, so not saved means untouched. */
sealed interface SaveResult {
    data class Saved(
        val id: RecordId,
    ) : SaveResult

    data object NotSaved : SaveResult
}
