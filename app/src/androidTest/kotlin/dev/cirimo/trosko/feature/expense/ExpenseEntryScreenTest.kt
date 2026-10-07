package dev.cirimo.trosko.feature.expense

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import dev.cirimo.trosko.R
import dev.cirimo.trosko.designsystem.theme.TroskoTheme
import dev.cirimo.trosko.domain.money.AmountInput
import dev.cirimo.trosko.domain.money.Money
import dev.cirimo.trosko.format.MoneyFormatter
import dev.cirimo.trosko.testing.FakeCategoryRepository
import dev.cirimo.trosko.testing.FakeRecordRepository
import dev.cirimo.trosko.testing.SettableClock
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate
import java.util.Currency

@RunWith(AndroidJUnit4::class)
class ExpenseEntryScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    // Every expected text is read from resources or made by the app's own formatter in the
    // device's language, so the tests pass whatever language the device is set to.
    private val context: Context = InstrumentationRegistry.getInstrumentation().targetContext
    private val formatter = MoneyFormatter(context.resources.configuration.locales[0])
    private val euro = Currency.getInstance("EUR")
    private val clock = SettableClock(LocalDate.of(2026, 10, 7))
    private val records = FakeRecordRepository(clock)

    private val saveButton get() = composeRule.onNodeWithText(context.getString(R.string.expense_entry_save_button))

    @Before
    fun showScreen() {
        val viewModel = ExpenseEntryViewModel(FakeCategoryRepository(), records, clock, SavedStateHandle())
        composeRule.setContent {
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            TroskoTheme {
                ExpenseEntryScreen(
                    uiState = uiState,
                    onKey = viewModel::onKey,
                    onCategoryClick = viewModel::onCategoryChosen,
                    onDayBack = viewModel::onDayBack,
                    onDayForward = viewModel::onDayForward,
                    onNoteChange = viewModel::onNoteChanged,
                    onSave = viewModel::onSave,
                    onLand = viewModel::onLandingShown,
                    onRecordClick = {},
                )
            }
        }
    }

    private fun press(keys: String) {
        keys.forEach { key ->
            if (key == ',') {
                val separator = context.getString(R.string.expense_entry_separator_description)
                composeRule.onNodeWithContentDescription(separator).performClick()
            } else {
                composeRule.onNodeWithText(key.toString()).performClick()
            }
        }
    }

    private fun chooseGroceries() {
        composeRule.onNodeWithText(context.getString(R.string.category_groceries)).performClick()
    }

    private fun amountShowing(text: String) =
        composeRule.onNodeWithContentDescription(context.getString(R.string.expense_entry_amount_description, text))

    @Test
    fun saveIsDisabledUntilThereIsAnAmountAndACategory() {
        saveButton.assertIsNotEnabled()

        press("7")
        saveButton.assertIsNotEnabled()
        chooseGroceries()

        saveButton.assertIsEnabled()
    }

    @Test
    fun zeroCannotBeSaved() {
        chooseGroceries()

        press("0,00")

        saveButton.assertIsNotEnabled()
    }

    @Test
    fun keysWriteTheAmountAndBackspaceTakesTheLastOneBack() {
        press("12,5")
        composeRule
            .onNodeWithContentDescription(context.getString(R.string.expense_entry_backspace_description))
            .performClick()

        amountShowing(formatter.formatTyping(AmountInput(euro, whole = "12", fraction = ""))).assertIsDisplayed()
    }

    @Test
    fun chosenCategorySaysItIsSelected() {
        chooseGroceries()

        composeRule.onNodeWithText(context.getString(R.string.category_groceries)).assertIsSelected()
    }

    @Test
    fun arrowMovesTheDateToYesterday() {
        composeRule
            .onNodeWithContentDescription(context.getString(R.string.expense_entry_day_back_description))
            .performClick()

        composeRule.onNodeWithText(context.getString(R.string.day_yesterday)).assertIsDisplayed()
        composeRule
            .onNodeWithContentDescription(context.getString(R.string.expense_entry_day_forward_description))
            .assertIsEnabled()
    }

    @Test
    fun savingLandsTheRecordOnThePageAndEmptiesTheForm() {
        press("12,50")
        chooseGroceries()

        saveButton.performClick()

        composeRule.onNodeWithText(formatter.format(Money(-1_250, euro))).assertIsDisplayed()
        composeRule.onNodeWithText(context.getString(R.string.expense_entry_saved_message)).assertIsDisplayed()
        amountShowing(formatter.formatTyping(AmountInput(euro))).assertIsDisplayed()
        saveButton.assertIsNotEnabled()
    }

    @Test
    fun failedSaveSaysSoAndKeepsWhatWasTyped() {
        records.failing = true
        press("12,50")
        chooseGroceries()

        saveButton.performClick()

        composeRule.onNodeWithText(context.getString(R.string.expense_entry_save_failed_message)).assertIsDisplayed()
        amountShowing(formatter.formatTyping(AmountInput(euro, whole = "12", fraction = "50"))).assertIsDisplayed()
        saveButton.assertIsEnabled()
    }
}
