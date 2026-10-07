package dev.cirimo.trosko.domain.model

/**
 * Whether money left or arrived. An expense and an income are the same kind of thing apart from
 * this, so they share one model; a category belongs to exactly one kind.
 */
enum class RecordKind {
    EXPENSE,
    INCOME,
}
