package dev.cirimo.trosko.data.repository

import dev.cirimo.trosko.data.db.CategoryEntity
import dev.cirimo.trosko.data.db.categoryColourFromColumn
import dev.cirimo.trosko.data.db.categoryIconFromColumn
import dev.cirimo.trosko.data.db.recordKindFromColumn
import dev.cirimo.trosko.domain.model.Category
import dev.cirimo.trosko.domain.model.CategoryId
import dev.cirimo.trosko.domain.model.CategoryName
import java.time.Instant
import java.util.UUID

// The one place where a `category` row becomes a domain category.
internal fun CategoryEntity.toDomain(): Category =
    Category(
        id = CategoryId(UUID.fromString(id)),
        kind = recordKindFromColumn(kind),
        name = name(),
        colour = categoryColourFromColumn(colour),
        icon = categoryIconFromColumn(icon),
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
