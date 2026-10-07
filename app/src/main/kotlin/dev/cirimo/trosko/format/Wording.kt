package dev.cirimo.trosko.format

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import dev.cirimo.trosko.R
import dev.cirimo.trosko.designsystem.component.InkGlyph
import dev.cirimo.trosko.designsystem.theme.TroskoTheme
import dev.cirimo.trosko.domain.model.CategoryColour
import dev.cirimo.trosko.domain.model.CategoryIcon
import dev.cirimo.trosko.domain.model.CategoryName
import java.time.format.DateTimeFormatter
import java.util.Locale

// Where domain values meet the screen: each function here turns one of them into the words, the
// colour or the drawing the current language and theme call for.

/** The locale the app is shown in, which follows the per-app language setting. */
@Composable
@ReadOnlyComposable
fun currentLocale(): Locale = LocalConfiguration.current.locales[0]

@Composable
fun rememberMoneyFormatter(): MoneyFormatter {
    val locale = currentLocale()
    return remember(locale) { MoneyFormatter(locale) }
}

@Composable
fun dayLabelText(day: DayLabel): String =
    when (day) {
        DayLabel.Today -> {
            stringResource(R.string.day_today)
        }

        DayLabel.Yesterday -> {
            stringResource(R.string.day_yesterday)
        }

        is DayLabel.On -> {
            val locale = currentLocale()
            val pattern = stringResource(R.string.day_pattern)
            remember(locale, pattern) { DateTimeFormatter.ofPattern(pattern, locale) }.format(day.date)
        }
    }

@Composable
@ReadOnlyComposable
fun categoryNameText(name: CategoryName): String =
    when (name) {
        is CategoryName.Custom -> name.text
        is CategoryName.Builtin -> stringResource(builtinCategoryName(name.key))
    }

// A key this build does not know can only come from a database written by a newer build. It is
// shown as "Other" instead of crashing or showing the raw key.
private fun builtinCategoryName(key: String): Int =
    when (key) {
        "groceries" -> R.string.category_groceries
        "eating_out" -> R.string.category_eating_out
        "transport" -> R.string.category_transport
        "home" -> R.string.category_home
        "bills" -> R.string.category_bills
        "health" -> R.string.category_health
        "fun" -> R.string.category_fun
        "shopping" -> R.string.category_shopping
        else -> R.string.category_other
    }

@Composable
@ReadOnlyComposable
fun categoryFill(colour: CategoryColour): Color =
    when (colour) {
        CategoryColour.YELLOW -> TroskoTheme.colors.yellow
        CategoryColour.PINK -> TroskoTheme.colors.pink
        CategoryColour.CYAN -> TroskoTheme.colors.cyan
        CategoryColour.GREEN -> TroskoTheme.colors.green
        CategoryColour.ORANGE -> TroskoTheme.colors.orange
        CategoryColour.VIOLET -> TroskoTheme.colors.violet
    }

fun categoryGlyph(icon: CategoryIcon): InkGlyph =
    when (icon) {
        CategoryIcon.BASKET -> InkGlyph.Basket
        CategoryIcon.CUP -> InkGlyph.Cup
        CategoryIcon.BUS -> InkGlyph.Bus
        CategoryIcon.HOUSE -> InkGlyph.House
        CategoryIcon.BOLT -> InkGlyph.Bolt
        CategoryIcon.HEART -> InkGlyph.Heart
        CategoryIcon.TICKET -> InkGlyph.Ticket
        CategoryIcon.BAG -> InkGlyph.Bag
        CategoryIcon.DOTS -> InkGlyph.Dots
    }
