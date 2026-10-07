package dev.cirimo.trosko.domain.model

import java.time.Instant

/**
 * What a record is filed under: a category for an expense, a source for an income. The two are
 * one model told apart by [kind]; "source" is only the word the UI uses for the income ones.
 *
 * A category that has records is never deleted, only archived: [archivedAt] is set, it stops
 * being offered for new records, and the old ones keep pointing at it.
 */
data class Category(
    val id: CategoryId,
    val kind: RecordKind,
    val name: CategoryName,
    val sortOrder: Int,
    val archivedAt: Instant?,
)
