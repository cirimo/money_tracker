package dev.cirimo.trosko.feature.expense

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.key.utf16CodePoint
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import dev.cirimo.trosko.R
import dev.cirimo.trosko.designsystem.component.AmountDisplay
import dev.cirimo.trosko.designsystem.component.CategoryChip
import dev.cirimo.trosko.designsystem.component.LandingSticker
import dev.cirimo.trosko.designsystem.component.TroskoText
import dev.cirimo.trosko.designsystem.component.TroskoTextField
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
import dev.cirimo.trosko.format.RecordLine
import dev.cirimo.trosko.format.RecordLineRow
import dev.cirimo.trosko.format.categoryFill
import dev.cirimo.trosko.format.categoryGlyph
import dev.cirimo.trosko.format.categoryNameText
import dev.cirimo.trosko.format.rememberMoneyFormatter
import java.util.Currency
import kotlin.random.Random

// How far off level a record that has just landed may rest, in degrees either way.
private const val MAX_LANDED_TILT = 2.5f

// Three chips in a row at normal text sizes; two once the text is enlarged enough that three
// names no longer fit side by side.
private const val CHIP_COLUMNS = 3
private const val CHIP_COLUMNS_LARGE_TEXT = 2
private const val LARGE_TEXT_SCALE = 1.3f

// The page above the pad never shrinks below a heading and one record, enlarged text included.
private const val MIN_PAGE_ROWS = 5

/**
 * Recording an expense. The upper part is the notebook page, where what was written lands; the
 * lower part is the pad for writing the next one: amount, date, category, note and keypad, all
 * within reach of a thumb. The pad stays usable while a sticker is still landing.
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
                    showsSavedNote = uiState.showsSavedNote,
                    onLand = onLand,
                )
            },
            pad = {
                EntryPad(
                    uiState = uiState,
                    onKey = onKey,
                    onCategoryClick = onCategoryClick,
                    onDayBack = onDayBack,
                    onDayForward = onDayForward,
                    onNoteChange = onNoteChange,
                    onNoteFocusChange = { isNoteFocused = it },
                    onSave = onSave,
                )
            },
            modifier =
                Modifier
                    .widthIn(max = TroskoDimens.MaxContentWidth)
                    .padding(horizontal = TroskoDimens.ScreenGutter),
        )
    }
}

// Which key of the keypad a key of a physical keyboard stands for, if any.
private fun typeOnKeypad(
    event: KeyEvent,
    onKey: (AmountKey) -> Unit,
): Boolean {
    val typed = event.utf16CodePoint.toChar()
    val key =
        when {
            event.type != KeyEventType.KeyDown -> null
            event.key == Key.Backspace -> AmountKey.Backspace
            typed in '0'..'9' -> AmountKey.Digit(typed.digitToInt())
            typed == ',' || typed == '.' -> AmountKey.Separator
            else -> null
        }
    key?.let(onKey)
    return key != null
}

/**
 * Stacks the page over the pad. The pad takes the height it needs and the page gets what is
 * left of the screen. When the pad alone is taller than the screen (very large text, or the
 * keyboard open for the note), the page keeps a minimum height and the whole thing scrolls.
 */
@Composable
private fun PageAbovePad(
    page: @Composable () -> Unit,
    pad: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(modifier = modifier) {
        val viewportHeight = constraints.maxHeight
        val minPageHeight = with(LocalDensity.current) { (TroskoDimens.MinTouchTarget * MIN_PAGE_ROWS).roundToPx() }

        Layout(
            contents = listOf(page, pad),
            modifier = Modifier.verticalScroll(rememberScrollState()),
        ) { (pageMeasurables, padMeasurables), constraints ->
            val width = constraints.maxWidth
            val padPlaceable = padMeasurables.single().measure(Constraints.fixedWidth(width))
            val pageHeight = maxOf(viewportHeight - padPlaceable.height, minPageHeight)
            val pagePlaceable = pageMeasurables.single().measure(Constraints.fixed(width, pageHeight))

            layout(width, pageHeight + padPlaceable.height) {
                pagePlaceable.place(0, 0)
                padPlaceable.place(0, pageHeight)
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun LatestRecords(
    latest: List<RecordLine>,
    landedId: RecordId?,
    isLandingPending: Boolean,
    showsSavedNote: Boolean,
    onLand: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(top = TroskoDimens.SpaceL),
        verticalArrangement = Arrangement.spacedBy(TroskoDimens.SpaceS),
    ) {
        // The confirmation sits beside the heading, and under it when enlarged text needs the room.
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(TroskoDimens.SpaceM),
            itemVerticalAlignment = Alignment.CenterVertically,
        ) {
            TroskoText(text = stringResource(R.string.home_latest_heading), style = TroskoTheme.typography.heading)
            // The landing says nothing to someone who cannot see it, so the words are announced.
            Box(modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite }) {
                if (showsSavedNote) {
                    TroskoText(
                        text = stringResource(R.string.expense_entry_saved_message),
                        style = TroskoTheme.typography.note,
                    )
                }
            }
        }
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
                    val isLanded = line.id == landedId
                    // The angle is chosen once per record, a little different every time.
                    val tilt = remember { Random.nextFloat() * 2 * MAX_LANDED_TILT - MAX_LANDED_TILT }
                    LandingSticker(
                        landing = isLanded && isLandingPending,
                        tilt = if (isLanded) tilt else 0f,
                        onLand = onLand,
                    ) {
                        RecordLineRow(line = line)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun EntryPad(
    uiState: ExpenseEntryUiState,
    onKey: (AmountKey) -> Unit,
    onCategoryClick: (CategoryId) -> Unit,
    onDayBack: () -> Unit,
    onDayForward: () -> Unit,
    onNoteChange: (String) -> Unit,
    onNoteFocusChange: (Boolean) -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val formatter = rememberMoneyFormatter()
    val amountText = formatter.formatTyping(uiState.amount)
    val amountDescription = stringResource(R.string.expense_entry_amount_description, amountText)
    val selected = uiState.selectedCategory
    val chipColumns = if (LocalDensity.current.fontScale > LARGE_TEXT_SCALE) CHIP_COLUMNS_LARGE_TEXT else CHIP_COLUMNS

    Column(
        modifier =
            modifier
                .padding(top = TroskoDimens.SpaceM, bottom = TroskoDimens.SpaceL)
                // A screen reader starts with the pad, although the page is drawn above it.
                .semantics {
                    isTraversalGroup = true
                    traversalIndex = -1f
                },
        verticalArrangement = Arrangement.spacedBy(TroskoDimens.SpaceM),
    ) {
        if (uiState.saveFailed) {
            TroskoText(
                text = stringResource(R.string.expense_entry_save_failed_message),
                modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite },
                style = TroskoTheme.typography.note,
            )
        }
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalArrangement = Arrangement.spacedBy(TroskoDimens.SpaceS),
            itemVerticalAlignment = Alignment.CenterVertically,
        ) {
            AmountDisplay(
                text = amountText,
                modifier = Modifier.semantics { contentDescription = amountDescription },
                fill = selected?.let { categoryFill(it.colour) },
            )
            DateStepper(
                day = uiState.day,
                canStepForward = uiState.canStepDayForward,
                onBack = onDayBack,
                onForward = onDayForward,
            )
        }
        FlowRow(
            modifier = Modifier.fillMaxWidth().selectableGroup(),
            // Wide enough that the second outline of the chosen chip clears its neighbours.
            horizontalArrangement = Arrangement.spacedBy(TroskoDimens.SpaceM),
            verticalArrangement = Arrangement.spacedBy(TroskoDimens.SpaceM),
            maxItemsInEachRow = chipColumns,
        ) {
            uiState.categories.forEach { category ->
                CategoryChip(
                    name = categoryNameText(category.name),
                    glyph = categoryGlyph(category.icon),
                    fill = categoryFill(category.colour),
                    selected = category.id == uiState.selectedCategoryId,
                    onClick = { onCategoryClick(category.id) },
                    modifier = Modifier.weight(1f),
                )
            }
        }
        TroskoTextField(
            value = uiState.note,
            onValueChange = onNoteChange,
            placeholder = stringResource(R.string.expense_entry_note_placeholder),
            modifier = Modifier.fillMaxWidth().onFocusChanged { onNoteFocusChange(it.isFocused) },
        )
        ExpenseKeypad(
            separator = formatter.decimalSeparator,
            canSave = uiState.canSave,
            onKey = onKey,
            onSave = onSave,
        )
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
                    showsSavedNote = false,
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
        )
    }
}
