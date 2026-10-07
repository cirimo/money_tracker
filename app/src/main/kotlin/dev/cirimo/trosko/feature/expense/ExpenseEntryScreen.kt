package dev.cirimo.trosko.feature.expense

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.tooling.preview.Preview
import dev.cirimo.trosko.R
import dev.cirimo.trosko.designsystem.theme.TroskoDimens
import dev.cirimo.trosko.designsystem.theme.TroskoTheme
import dev.cirimo.trosko.domain.model.BuiltinCategories
import dev.cirimo.trosko.domain.model.Category
import dev.cirimo.trosko.domain.model.CategoryId
import dev.cirimo.trosko.domain.model.CategoryName
import dev.cirimo.trosko.domain.model.RecordId
import dev.cirimo.trosko.domain.money.AmountInput
import dev.cirimo.trosko.domain.money.AmountKey
import dev.cirimo.trosko.format.DayLabel
import dev.cirimo.trosko.format.LatestHeading
import dev.cirimo.trosko.format.RecordLine
import dev.cirimo.trosko.format.RecordLineSticker
import dev.cirimo.trosko.format.RecordNotice
import java.util.Currency

// The page above the pad never shrinks below a heading and one record, enlarged text included.
private const val MIN_PAGE_ROWS = 5

/**
 * Recording an expense. The upper part is the notebook page, where what was written lands; the
 * lower part is the pad for writing the next one: amount, date, category, note and keypad, all
 * within reach of a thumb. The pad stays usable while a sticker is still landing. Pressing a
 * record on the page opens it to be corrected.
 */
@Composable
fun ExpenseEntryScreen(
    uiState: ExpenseEntryUiState,
    onKey: (AmountKey) -> Unit,
    onCategoryClick: (CategoryId) -> Unit,
    onDayBack: () -> Unit,
    onDayForward: () -> Unit,
    onNoteChange: (String) -> Unit,
    onSave: () -> Unit,
    onLand: () -> Unit,
    onRecordClick: (RecordId) -> Unit,
    modifier: Modifier = Modifier,
) {
    // A physical keyboard types the amount too, except while it is writing the note.
    var isNoteFocused by remember { mutableStateOf(false) }

    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(TroskoTheme.colors.paper)
                .safeDrawingPadding()
                .onKeyEvent { event ->
                    !isNoteFocused && typeOnKeypad(event, onKey)
                },
        contentAlignment = Alignment.TopCenter,
    ) {
        PageAbovePad(
            page = {
                LatestRecords(
                    latest = uiState.latest,
                    landedId = uiState.landedId,
                    isLandingPending = uiState.isLandingPending,
                    notice = uiState.notice,
                    onLand = onLand,
                    onRecordClick = onRecordClick,
                )
            },
            pad = {
                ExpensePad(
                    state = uiState.pad,
                    onKey = onKey,
                    onCategoryClick = onCategoryClick,
                    onDayBack = onDayBack,
                    onDayForward = onDayForward,
                    onNoteChange = onNoteChange,
                    onNoteFocusChange = { isNoteFocused = it },
                    onSave = onSave,
                    // A screen reader starts with the pad, although the page is drawn above it.
                    modifier =
                        Modifier.semantics {
                            isTraversalGroup = true
                            traversalIndex = -1f
                        },
                    failure =
                        if (uiState.saveFailed) stringResource(R.string.expense_entry_save_failed_message) else null,
                )
            },
            modifier =
                Modifier
                    .widthIn(max = TroskoDimens.MaxContentWidth)
                    .padding(horizontal = TroskoDimens.ScreenGutter),
            minPageHeight = TroskoDimens.MinTouchTarget * MIN_PAGE_ROWS,
        )
    }
}

@Composable
private fun LatestRecords(
    latest: List<RecordLine>,
    landedId: RecordId?,
    isLandingPending: Boolean,
    notice: RecordNotice?,
    onLand: () -> Unit,
    onRecordClick: (RecordId) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(top = TroskoDimens.SpaceL),
        verticalArrangement = Arrangement.spacedBy(TroskoDimens.SpaceS),
    ) {
        LatestHeading(notice = notice)
        Column(
            modifier =
                Modifier
                    .weight(1f)
                    // Rows that do not fit are cut off at the bottom edge only, so a sticker
                    // dropping in from above, larger than life, is never clipped.
                    .drawWithContent {
                        clipRect(
                            left = -size.width,
                            top = -size.height,
                            right = 2 * size.width,
                            bottom = size.height,
                        ) { this@drawWithContent.drawContent() }
                    }.wrapContentHeight(align = Alignment.Top, unbounded = true)
                    .padding(top = TroskoDimens.SpaceS),
            verticalArrangement = Arrangement.spacedBy(TroskoDimens.SpaceS),
        ) {
            latest.forEach { line ->
                key(line.id) {
                    RecordLineSticker(
                        line = line,
                        isLanded = line.id == landedId,
                        isLandingPending = isLandingPending,
                        onLand = onLand,
                        onClick = { onRecordClick(line.id) },
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, heightDp = 914, widthDp = 412)
@Composable
private fun ExpenseEntryScreenPreview() {
    val euro = Currency.getInstance("EUR")
    val categories =
        BuiltinCategories.all.map {
            Category(it.id, it.kind, CategoryName.Builtin(it.key), it.colour, it.icon, it.sortOrder, archivedAt = null)
        }
    TroskoTheme {
        ExpenseEntryScreen(
            uiState =
                ExpenseEntryUiState(
                    amount = AmountInput(euro, whole = "12", fraction = "5"),
                    categories = categories,
                    selectedCategoryId = categories.first().id,
                    day = DayLabel.Today,
                    canStepDayForward = false,
                    note = "",
                    canSave = true,
                    saveFailed = false,
                    notice = null,
                    latest = emptyList(),
                    landedId = null,
                    isLandingPending = false,
                ),
            onKey = {},
            onCategoryClick = {},
            onDayBack = {},
            onDayForward = {},
            onNoteChange = {},
            onSave = {},
            onLand = {},
            onRecordClick = {},
        )
    }
}
