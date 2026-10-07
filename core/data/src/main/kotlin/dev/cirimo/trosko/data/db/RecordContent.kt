package dev.cirimo.trosko.data.db

import androidx.room3.ColumnInfo

/**
 * The columns of a `record` row that a correction rewrites, with the id of the row. What is not
 * listed here (the kind, `created_at`) cannot be changed by one.
 */
internal data class RecordContent(
    val id: String,
    @ColumnInfo(name = "amount_minor") val amountMinor: Long,
    val currency: String,
    @ColumnInfo(name = "category_id") val categoryId: String,
    @ColumnInfo(name = "occurred_on") val occurredOn: String,
    val note: String?,
    @ColumnInfo(name = "updated_at") val updatedAt: Long,
)
