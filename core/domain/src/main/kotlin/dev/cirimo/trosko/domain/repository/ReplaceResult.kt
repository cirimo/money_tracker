package dev.cirimo.trosko.domain.repository

/** What correcting a record came to. Writes are transactional, so not saved means untouched. */
sealed interface ReplaceResult {
    data object Replaced : ReplaceResult

    data object NotSaved : ReplaceResult

    /** There is no such record any more; nothing was written. */
    data object Gone : ReplaceResult
}
