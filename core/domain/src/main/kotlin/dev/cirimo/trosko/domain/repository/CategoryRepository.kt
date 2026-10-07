package dev.cirimo.trosko.domain.repository

import dev.cirimo.trosko.domain.model.Category
import dev.cirimo.trosko.domain.model.RecordKind
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    /** The categories of [kind] that can be chosen for a new record, in display order. */
    fun observeActive(kind: RecordKind): Flow<List<Category>>
}
