package dev.cirimo.trosko.domain.repository

/** What deleting a record came to. Not deleted means the record is still there, unchanged. */
sealed interface DeleteResult {
    data object Deleted : DeleteResult

    data object NotDeleted : DeleteResult

    /** There was no such record to delete. */
    data object Gone : DeleteResult
}
