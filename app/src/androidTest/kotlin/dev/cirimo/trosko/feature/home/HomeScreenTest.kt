package dev.cirimo.trosko.feature.home

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import dev.cirimo.trosko.R
import dev.cirimo.trosko.designsystem.theme.TroskoTheme
import dev.cirimo.trosko.domain.model.RecordId
import dev.cirimo.trosko.domain.money.Money
import dev.cirimo.trosko.format.DayLabel
import dev.cirimo.trosko.format.MoneyFormatter
import dev.cirimo.trosko.format.RecordLine
import dev.cirimo.trosko.testing.builtinCategories
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.Currency
import java.util.UUID

@RunWith(AndroidJUnit4::class)
class HomeScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    private val context: Context = InstrumentationRegistry.getInstrumentation().targetContext
    private val euro = Currency.getInstance("EUR")

    private fun show(
        uiState: HomeUiState,
        onNewExpense: () -> Unit = {},
    ) {
        composeRule.setContent {
            TroskoTheme { HomeScreen(uiState = uiState, onNewExpense = onNewExpense) }
        }
    }

    @Test
    fun emptyPageInvitesTheFirstEntry() {
        show(HomeUiState.Loaded(latest = emptyList()))

        composeRule.onNodeWithText(context.getString(R.string.home_empty_message)).assertIsDisplayed()
    }

    @Test
    fun recordShowsItsCategoryDayNoteAndAmount() {
        val line =
            RecordLine(
                id = RecordId(UUID.randomUUID()),
                category = builtinCategories[0],
                signedAmount = Money(-1_250, euro),
                day = DayLabel.Yesterday,
                note = "kruh",
            )
        val amount = MoneyFormatter(context.resources.configuration.locales[0]).format(line.signedAmount)
        val dayAndNote =
            context.getString(R.string.record_row_day_and_note, context.getString(R.string.day_yesterday), "kruh")

        show(HomeUiState.Loaded(latest = listOf(line)))

        composeRule.onNodeWithText(context.getString(R.string.category_groceries)).assertIsDisplayed()
        composeRule.onNodeWithText(dayAndNote).assertIsDisplayed()
        composeRule.onNodeWithText(amount).assertIsDisplayed()
    }

    @Test
    fun buttonAsksForANewExpense() {
        var asked = 0
        show(HomeUiState.Loaded(latest = emptyList()), onNewExpense = { asked++ })

        composeRule.onNodeWithText(context.getString(R.string.home_new_expense_button)).performClick()

        assertEquals(1, asked)
    }
}
