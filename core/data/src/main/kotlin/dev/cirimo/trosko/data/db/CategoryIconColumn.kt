package dev.cirimo.trosko.data.db

import dev.cirimo.trosko.domain.model.CategoryIcon

// The text stored in `category.icon`. Spelled out, like the kind, so that renaming an enum
// constant in Kotlin can never change what is in users' databases.
private const val ICON_BASKET = "BASKET"
private const val ICON_CUP = "CUP"
private const val ICON_BUS = "BUS"
private const val ICON_HOUSE = "HOUSE"
private const val ICON_BOLT = "BOLT"
private const val ICON_HEART = "HEART"
private const val ICON_TICKET = "TICKET"
private const val ICON_BAG = "BAG"
private const val ICON_DOTS = "DOTS"

internal fun CategoryIcon.toColumn(): String =
    when (this) {
        CategoryIcon.BASKET -> ICON_BASKET
        CategoryIcon.CUP -> ICON_CUP
        CategoryIcon.BUS -> ICON_BUS
        CategoryIcon.HOUSE -> ICON_HOUSE
        CategoryIcon.BOLT -> ICON_BOLT
        CategoryIcon.HEART -> ICON_HEART
        CategoryIcon.TICKET -> ICON_TICKET
        CategoryIcon.BAG -> ICON_BAG
        CategoryIcon.DOTS -> ICON_DOTS
    }

internal fun categoryIconFromColumn(value: String): CategoryIcon =
    when (value) {
        ICON_BASKET -> CategoryIcon.BASKET
        ICON_CUP -> CategoryIcon.CUP
        ICON_BUS -> CategoryIcon.BUS
        ICON_HOUSE -> CategoryIcon.HOUSE
        ICON_BOLT -> CategoryIcon.BOLT
        ICON_HEART -> CategoryIcon.HEART
        ICON_TICKET -> CategoryIcon.TICKET
        ICON_BAG -> CategoryIcon.BAG
        ICON_DOTS -> CategoryIcon.DOTS
        else -> error("Unknown category icon in the database: '$value'")
    }
