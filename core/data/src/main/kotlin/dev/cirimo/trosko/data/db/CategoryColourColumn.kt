package dev.cirimo.trosko.data.db

import dev.cirimo.trosko.domain.model.CategoryColour

// The text stored in `category.colour`. Spelled out, like the kind, so that renaming an enum
// constant in Kotlin can never change what is in users' databases.
private const val COLOUR_YELLOW = "YELLOW"
private const val COLOUR_PINK = "PINK"
private const val COLOUR_CYAN = "CYAN"
private const val COLOUR_GREEN = "GREEN"
private const val COLOUR_ORANGE = "ORANGE"
private const val COLOUR_VIOLET = "VIOLET"

internal fun CategoryColour.toColumn(): String =
    when (this) {
        CategoryColour.YELLOW -> COLOUR_YELLOW
        CategoryColour.PINK -> COLOUR_PINK
        CategoryColour.CYAN -> COLOUR_CYAN
        CategoryColour.GREEN -> COLOUR_GREEN
        CategoryColour.ORANGE -> COLOUR_ORANGE
        CategoryColour.VIOLET -> COLOUR_VIOLET
    }

internal fun categoryColourFromColumn(value: String): CategoryColour =
    when (value) {
        COLOUR_YELLOW -> CategoryColour.YELLOW
        COLOUR_PINK -> CategoryColour.PINK
        COLOUR_CYAN -> CategoryColour.CYAN
        COLOUR_GREEN -> CategoryColour.GREEN
        COLOUR_ORANGE -> CategoryColour.ORANGE
        COLOUR_VIOLET -> CategoryColour.VIOLET
        else -> error("Unknown category colour in the database: '$value'")
    }
