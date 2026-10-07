package dev.cirimo.trosko

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PlaceholderScreenTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun launchShowsPlaceholderMessageOnceStorageAnswers() {
        // Read the text from resources so the test passes in every locale the device may use.
        val message = composeRule.activity.getString(R.string.placeholder_message)

        // The message only appears after the real database has been opened and queried.
        composeRule.waitUntil { composeRule.onAllNodesWithText(message).fetchSemanticsNodes().isNotEmpty() }

        composeRule.onNodeWithText(message).assertIsDisplayed()
    }
}
