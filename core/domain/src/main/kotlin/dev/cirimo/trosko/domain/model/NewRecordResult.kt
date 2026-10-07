package dev.cirimo.trosko.domain.model

/** What checking a record the user wants to write down came to. */
sealed interface NewRecordResult {
    data class Valid(
        val record: NewRecord,
    ) : NewRecordResult

    /** The amount is zero or negative. A record always moves some money. */
    data object AmountNotPositive : NewRecordResult

    /** The date is after today. */
    data object DateInFuture : NewRecordResult

    /** The note is longer than [NewRecord.NOTE_MAX_LENGTH]. */
    data object NoteTooLong : NewRecordResult
}
