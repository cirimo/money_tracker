package dev.cirimo.trosko.domain.model

import java.util.UUID

/**
 * The categories every new database starts with.
 *
 * Their ids are fixed, not generated, so that two devices agree on which category is which when
 * data is exported, restored or one day synchronised. An id or a key listed here must never
 * change or be reused once a release has shipped; add new entries instead.
 */
object BuiltinCategories {
    val all: List<BuiltinCategory> =
        listOf(
            expense("89c6c9df-5bc3-47d8-840e-e06e8c84dfd5", "groceries", CategoryColour.GREEN, CategoryIcon.BASKET),
            expense("2106011f-1117-4f7a-9513-549db5608e17", "eating_out", CategoryColour.ORANGE, CategoryIcon.CUP),
            expense("c03611b3-8e2f-4de1-8ea3-621a8882df0d", "transport", CategoryColour.CYAN, CategoryIcon.BUS),
            expense("8386464a-fe22-4348-9be9-82ef2b081c6e", "home", CategoryColour.VIOLET, CategoryIcon.HOUSE),
            expense("0e1a8e9d-d96f-4438-8c22-bc5ce7607c7e", "bills", CategoryColour.YELLOW, CategoryIcon.BOLT),
            expense("3f1bcab2-64f6-481b-a07f-620072b4e889", "health", CategoryColour.PINK, CategoryIcon.HEART),
            expense("eb9131f0-7964-4b6e-963b-8f78d2d2c601", "fun", CategoryColour.CYAN, CategoryIcon.TICKET),
            expense("b67e2f68-97d3-470a-a165-2431524b775c", "shopping", CategoryColour.GREEN, CategoryIcon.BAG),
            expense("75cca1a6-5bbb-4117-8cb4-c39942fa6bf2", "other", CategoryColour.ORANGE, CategoryIcon.DOTS),
        ).mapIndexed { index, category -> category.copy(sortOrder = index) }

    private fun expense(
        id: String,
        key: String,
        colour: CategoryColour,
        icon: CategoryIcon,
    ) = BuiltinCategory(
        id = CategoryId(UUID.fromString(id)),
        kind = RecordKind.EXPENSE,
        key = key,
        colour = colour,
        icon = icon,
        sortOrder = 0,
    )
}
