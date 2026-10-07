package dev.cirimo.trosko.data.db

import androidx.room3.Embedded
import androidx.room3.Relation

/** A record together with the category it is filed under, which the foreign key guarantees. */
internal data class RecordWithCategory(
    @Embedded val record: RecordEntity,
    @Relation(parentColumns = ["category_id", "kind"], entityColumns = ["id", "kind"]) val category: CategoryEntity,
)
