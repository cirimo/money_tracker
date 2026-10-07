package dev.cirimo.trosko.feature.expense

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isSelectable
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
import dev.cirimo.trosko.domain.model.Record
import dev.cirimo.trosko.domain.model.RecordId
import dev.cirimo.trosko.domain.model.RecordKind
import dev.cirimo.trosko.domain.money.AmountInput
import dev.cirimo.trosko.domain.money.Money
import dev.cirimo.trosko.format.MoneyFormatter
import dev.cirimo.trosko.testing.FakeCategoryRepository
import dev.cirimo.trosko.testing.FakeRecordRepository
import dev.cirimo.trosko.testing.SettableClock
import dev.cirimo.trosko.testing.builtinCategories
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate
import java.util.Currency
import java.util.UUID

@RunWith(AndroidJUnit4::class)
class ExpenseEditScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    // Every expected text is read from resources or made by the app's own formatter in the
    // device's language, so the tests pass whatever language the device is set to.
    private val context: Context = InstrumentationRegistry.getInstrumentation().targetContext
    private val formatter = MoneyFormatter(context.resources.configuration.locales[0])
    private val euro = Currency.getInstance("EUR")
    private val clock = SettableClock(LocalDate.of(2026, 10, 7))
    private val records = FakeRecordRepository(clock)
    private val written =
        Record(
            id = RecordId(UUID.randomUUID()),
            kind = RecordKind.EXPENSE,
            amount = Money(1_250, euro),
            category = builtinCategories[0],
            occurredOn = LocalDate.of(2026, 10, 6),
            note = "kruh",
            createdAt = clock.instant(),
        )
    private var timesBack = 0

    private val saveButton get() = composeRule.onNodeWithText(context.getString(R.string.expense_entry_save_button))
    private val deleteButton get() = composeRule.onNodeWithText(context.getString(R.string.expense_edit_delete_button))
    private val keepButton get() =
        composeRule.onNodeWithText(context.getString(R.string.expense_edit_delete_keep_button))
    private val question get() = composeRule.onNodeWithText(context.getString(R.string.expense_edit_delete_question))

    private fun show(record: Record? = written) {
        if (record != null) records.seed(record)
        val viewModel = ExpenseEditViewModel(written.id, FakeCategoryRepository(), records, clock, SavedStateHandle())
        composeRule.setContent {
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            TroskoTheme {
                ExpenseEditScreen(
                    uiState = uiState,
                    onKey = viewModel::onKey,
                    onCategoryClick = viewModel::onCategoryChosen,
                    onDayBack = viewModel::onDayBack,
                    onDayForward = viewModel::onDayForward,
                    onNoteChange = viewModel::onNoteChanged,
                    onSave = viewModel::onSave,
                    onDeleteClick = { viewModel.onDeleteQuestion(isAsking = true) },
                    onDeleteConfirm = viewModel::onDeleteConfirmed,
                    onDeleteDismiss = { viewModel.onDeleteQuestion(isAsking = false) },
                    onBack = { timesBack++ },
                )
            }
        }
    }

    private fun amountShowing(text: String) =
        composeRule.onNodeWithContentDescription(context.getString(R.string.expense_entry_amount_description, text))

    @Test
    fun screenOpensWithWhatWasWrittenDownAndNothingToSave() {
        show()

        amountShowing(formatter.formatTyping(AmountInput(euro, whole = "12", fraction = "50"))).assertIsDisplayed()
        // The category is on the page too, in the record; the chip is the one that can be chosen.
        composeRule
            .onNode(hasText(context.getString(R.string.category_groceries)) and isSelectable())
            .assertIsSelected()
        composeRule.onNodeWithText("kruh").assertIsDisplayed()
        composeRule.onNodeWithText(context.getString(R.string.expense_edit_heading)).assertIsDisplayed()
        saveButton.assertIsNotEnabled()
    }

    @Test
    fun changedAmountShowsOnTheRecordAndIsWrittenWhenSaved() {
        show()

        composeRule.onNodeWithText("7").performClick()

        // 12,50 with a third decimal refused stays 12,50; backspace then 7 makes it 12,57.
        saveButton.assertIsNotEnabled()
        composeRule
            .onNodeWithContentDescription(context.getString(R.string.expense_entry_backspace_description))
            .performClick()
        composeRule.onNodeWithText("7").performClick()
        composeRule.onNodeWithText(formatter.format(Money(-1_257, euro)), substring = true).assertIsDisplayed()
        saveButton.assertIsEnabled().performClick()

        assertEquals(
            Money(1_257, euro),
            records.replaced
                .single()
                .second.amount,
        )
    }

    @Test
    fun deleteAsksFirstAndKeepingLeavesTheRecord() {
        show()

        deleteButton.performClick()
        question.assertIsDisplayed()
        keepButton.performClick()

        composeRule.onNodeWithText(context.getString(R.string.expense_edit_heading)).assertIsDisplayed()
        assertTrue(records.deleted.isEmpty())
    }

    @Test
    fun confirmingTheQuestionDeletesTheRecord() {
        show()

        deleteButton.performClick()
        deleteButton.performClick()

        assertEquals(listOf(written.id), records.deleted)
    }

    @Test
    fun failedDeleteSaysTheRecordIsStillThere() {
        records.failing = true
        show()

        deleteButton.performClick()
        deleteButton.performClick()

        composeRule.onNodeWithText(context.getString(R.string.expense_edit_delete_failed_message)).assertIsDisplayed()
    }

    @Test
    fun recordThatIsGoneSaysSoAndOffersTheWayBack() {
        show(record = null)

        composeRule.onNodeWithText(context.getString(R.string.expense_edit_gone_message)).assertIsDisplayed()
        composeRule.onNodeWithText(context.getString(R.string.expense_edit_back_button)).performClick()

        assertEquals(1, timesBack)
    }
}
