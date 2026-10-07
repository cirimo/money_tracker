package dev.cirimo.trosko

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * The real app on its real database, from launch to the entry screen. It only reads: it never
 * saves anything, so it leaves no records behind in the installed app.
 */
@RunWith(AndroidJUnit4::class)
class AppLaunchTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    // Read from resources so the test passes in every language the device may use.
    private fun text(id: Int): String = composeRule.activity.getString(id)

    private fun waitFor(text: String) {
        composeRule.waitUntil { composeRule.onAllNodesWithText(text).fetchSemanticsNodes().isNotEmpty() }
    }

    @Test
    fun launchShowsHomeWithTheWayToANewExpense() {
        composeRule.onNodeWithText(text(R.string.home_latest_heading)).assertIsDisplayed()
        composeRule.onNodeWithText(text(R.string.home_new_expense_button)).assertIsDisplayed()
    }

    @Test
    fun newExpenseOpensTheEntryScreenWithTheSeededCategories() {
        composeRule.onNodeWithText(text(R.string.home_new_expense_button)).performClick()

        // The chips only appear once the real database has been opened, seeded and queried.
        waitFor(text(R.string.category_groceries))
        composeRule.onNodeWithText(text(R.string.category_other)).assertIsDisplayed()
        composeRule.onNodeWithText(text(R.string.expense_entry_save_button)).assertIsDisplayed()
    }

    @Test
    fun backFromTheEntryScreenReturnsHome() {
        composeRule.onNodeWithText(text(R.string.home_new_expense_button)).performClick()
        waitFor(text(R.string.expense_entry_save_button))

        composeRule.activityRule.scenario.onActivity { it.onBackPressedDispatcher.onBackPressed() }

        waitFor(text(R.string.home_new_expense_button))
        composeRule.onNodeWithText(text(R.string.home_new_expense_button)).assertIsDisplayed()
    }
}
