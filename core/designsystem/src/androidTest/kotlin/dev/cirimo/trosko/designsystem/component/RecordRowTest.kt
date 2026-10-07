package dev.cirimo.trosko.designsystem.component

import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertHasNoClickAction
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.cirimo.trosko.designsystem.theme.TroskoTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RecordRowTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun rowWithAClickIsOneButtonThatSaysTheWholeRecord() {
        var clicks = 0
        composeRule.setContent {
            TroskoTheme {
                RecordRow(
                    "Groceries",
                    "Today",
                    "12.50",
                    InkGlyph.Basket,
                    TroskoTheme.colors.green,
                    onClick = { clicks++ },
                )
            }
        }

        // Finding the row by the category and then by the amount lands on the same merged node.
        composeRule
            .onNodeWithText("Groceries")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Button))
        composeRule.onNodeWithText("12.50").assertHasClickAction().performClick()

        assertEquals(1, clicks)
    }

    @Test
    fun rowWithoutAClickCannotBePressed() {
        composeRule.setContent {
            TroskoTheme {
                RecordRow("Groceries", "Today", "12.50", InkGlyph.Basket, TroskoTheme.colors.green)
            }
        }

        composeRule.onNodeWithText("Groceries").assertHasNoClickAction()
    }
}
