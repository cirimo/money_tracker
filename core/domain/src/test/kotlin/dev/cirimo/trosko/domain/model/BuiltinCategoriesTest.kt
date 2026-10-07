package dev.cirimo.trosko.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test

class BuiltinCategoriesTest {
    private val all = BuiltinCategories.all

    @Test
    fun `every id is used once`() {
        assertEquals(all.size, all.map { it.id }.toSet().size)
    }

    @Test
    fun `every key is used once`() {
        assertEquals(all.size, all.map { it.key }.toSet().size)
    }

    @Test
    fun `the sort order follows the list`() {
        assertEquals(all.indices.toList(), all.map { it.sortOrder })
    }

    @Test
    fun `the id of the first category is the one that shipped`() {
        // Changing a shipped id would orphan it on every device that already has the old one.
        assertEquals(
            "89c6c9df-5bc3-47d8-840e-e06e8c84dfd5",
            all
                .first { it.key == "groceries" }
                .id.value
                .toString(),
        )
    }
}
