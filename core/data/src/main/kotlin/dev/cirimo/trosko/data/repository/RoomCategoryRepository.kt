package dev.cirimo.trosko.data.repository

import dev.cirimo.trosko.data.db.CategoryDao
import dev.cirimo.trosko.data.db.toColumn
import dev.cirimo.trosko.domain.model.Category
import dev.cirimo.trosko.domain.model.RecordKind
import dev.cirimo.trosko.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class RoomCategoryRepository(
    private val dao: CategoryDao,
) : CategoryRepository {
    override fun observeActive(kind: RecordKind): Flow<List<Category>> =
        dao.observeActive(kind.toColumn()).map { rows -> rows.map { it.toDomain() } }
}
