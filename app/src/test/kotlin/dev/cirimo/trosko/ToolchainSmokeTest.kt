package dev.cirimo.trosko

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Exists only to prove that the JVM unit test pipeline compiles and runs, locally and in CI.
 * Delete it once the first real unit test lands.
 */
class ToolchainSmokeTest {
    @Test
    fun `unit test pipeline runs`() {
        assertEquals(4, 2 + 2)
    }
}
