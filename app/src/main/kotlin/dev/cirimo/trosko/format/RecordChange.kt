package dev.cirimo.trosko.format

import dev.cirimo.trosko.domain.model.RecordId

/**
 * What the correction screen did to a record, told to the list it was opened from so that the
 * list can answer: the corrected record lands again, and either way a word says what happened.
 */
sealed interface RecordChange {
    val id: RecordId

    data class Corrected(
        override val id: RecordId,
    ) : RecordChange

    data class Deleted(
        override val id: RecordId,
    ) : RecordChange

    companion object {
        /** The name this result travels under between screens. */
        const val RESULT_KEY = "record_change"
    }
}
