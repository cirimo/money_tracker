package dev.cirimo.trosko.domain.model

/**
 * A category that ships with the app, as it is written into a new database. [key] selects the
 * string resource that names it, so the name follows the app language.
 */
data class BuiltinCategory(
    val id: CategoryId,
    val kind: RecordKind,
    val key: String,
    val colour: CategoryColour,
    val icon: CategoryIcon,
    val sortOrder: Int,
)
