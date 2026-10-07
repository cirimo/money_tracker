package dev.cirimo.trosko.data.repository

import dev.cirimo.trosko.data.db.CategoryDao
import dev.cirimo.trosko.data.db.CategoryEntity
import dev.cirimo.trosko.data.db.recordKindFromColumn
import dev.cirimo.trosko.data.db.toColumn
import dev.cirimo.trosko.domain.model.Category
import dev.cirimo.trosko.domain.model.CategoryId
import dev.cirimo.trosko.domain.model.CategoryName
import dev.cirimo.trosko.domain.model.RecordKind
import dev.cirimo.trosko.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.util.UUID

internal class RoomCategoryRepository(
    private val dao: CategoryDao,
) : CategoryRepository {
    override fun observeActive(kind: RecordKind): Flow<List<Category>> =
        dao.observeActive(kind.toColumn()).map { rows -> rows.map { it.toDomain() } }
}

private fun CategoryEntity.toDomain(): Category =
    Category(
        id = CategoryId(UUID.fromString(id)),
        kind = recordKindFromColumn(kind),
        name = name(),
        sortOrder = sortOrder,
        archivedAt = archivedAt?.let(Instant::ofEpochMilli),
    )

private fun CategoryEntity.name(): CategoryName {
    val custom = customName
    val builtin = builtinKey
    return when {
        custom != null -> CategoryName.Custom(custom)
        builtin != null -> CategoryName.Builtin(builtin)
        else -> error("Category $id has neither a built-in key nor a custom name")
    }
}
