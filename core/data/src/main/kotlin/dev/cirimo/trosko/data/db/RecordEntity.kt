package dev.cirimo.trosko.data.db

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.Index
import androidx.room3.PrimaryKey

/**
 * A row of the `record` table: one expense or one income.
 *
 * The foreign key covers (category_id, kind), not just the id, so the database itself refuses an
 * expense filed under an income source. Deleting a category that still has records is refused
 * too; such a category is archived instead.
 */
@Entity(
    tableName = "record",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id", "kind"],
            childColumns = ["category_id", "kind"],
            onDelete = ForeignKey.RESTRICT,
            onUpdate = ForeignKey.RESTRICT,
        ),
    ],
    indices = [
        Index(value = ["category_id", "kind"]),
        Index(value = ["occurred_on"]),
        Index(value = ["created_at"]),
    ],
)
internal data class RecordEntity(
    /** A UUID in its canonical text form. */
    @PrimaryKey val id: String,
    /** One of the constants in `RecordKindColumn.kt`. */
    val kind: String,
    /** Always positive, in the smallest unit of [currency]; the sign follows from [kind]. */
    @ColumnInfo(name = "amount_minor") val amountMinor: Long,
    /** ISO 4217 code, for example EUR. */
    val currency: String,
    @ColumnInfo(name = "category_id") val categoryId: String,
    /** The calendar date the user gave, as ISO 8601 text (2026-10-07). It has no time zone. */
    @ColumnInfo(name = "occurred_on") val occurredOn: String,
    val note: String?,
    /** Milliseconds since the epoch, UTC. Also orders records within one day. */
    @ColumnInfo(name = "created_at") val createdAt: Long,
    @ColumnInfo(name = "updated_at") val updatedAt: Long,
)
