package dev.cirimo.trosko.data.db

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.Index
import androidx.room3.PrimaryKey

/**
 * A row of the `category` table. Columns hold plain SQLite types on purpose; turning them into
 * domain types is the repository's job, so the mapping is visible in one place.
 *
 * The unique index on (id, kind) exists so that `record` can reference both columns together.
 */
@Entity(
    tableName = "category",
    indices = [Index(value = ["id", "kind"], unique = true)],
)
internal data class CategoryEntity(
    /** A UUID in its canonical text form. */
    @PrimaryKey val id: String,
    /** One of the constants in `RecordKindColumn.kt`. */
    val kind: String,
    /** Set for a category that ships with the app; selects its string resource. */
    @ColumnInfo(name = "builtin_key") val builtinKey: String?,
    /** Set once the user has typed a name; takes precedence over [builtinKey]. */
    @ColumnInfo(name = "custom_name") val customName: String?,
    @ColumnInfo(name = "sort_order") val sortOrder: Int,
    /** Milliseconds since the epoch, UTC; null while the category is in use. */
    @ColumnInfo(name = "archived_at") val archivedAt: Long?,
    @ColumnInfo(name = "created_at") val createdAt: Long,
    @ColumnInfo(name = "updated_at") val updatedAt: Long,
)
