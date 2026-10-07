package dev.cirimo.trosko.data.db

import dev.cirimo.trosko.domain.model.RecordKind

// The text stored in the `kind` columns. It is spelled out here, instead of using the enum's
// name, so that renaming an enum constant in Kotlin can never change what is in users' databases.
internal const val KIND_EXPENSE = "EXPENSE"
internal const val KIND_INCOME = "INCOME"

internal fun RecordKind.toColumn(): String =
    when (this) {
        RecordKind.EXPENSE -> KIND_EXPENSE
        RecordKind.INCOME -> KIND_INCOME
    }

internal fun recordKindFromColumn(value: String): RecordKind =
    when (value) {
        KIND_EXPENSE -> RecordKind.EXPENSE
        KIND_INCOME -> RecordKind.INCOME
        else -> error("Unknown record kind in the database: '$value'")
    }
