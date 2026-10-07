package dev.cirimo.trosko.navigation

import androidx.compose.animation.ContentTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.runtime.result.rememberResultEventBus
import androidx.navigation3.runtime.result.rememberResultEventBusNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import dev.cirimo.trosko.designsystem.theme.TroskoMotion
import dev.cirimo.trosko.designsystem.theme.TroskoTheme
import dev.cirimo.trosko.domain.model.RecordId
import dev.cirimo.trosko.feature.expense.ExpenseEditKey
import dev.cirimo.trosko.feature.expense.ExpenseEditRoute
import dev.cirimo.trosko.feature.expense.ExpenseEntryKey
import dev.cirimo.trosko.feature.expense.ExpenseEntryRoute
import dev.cirimo.trosko.feature.home.HomeKey
import dev.cirimo.trosko.feature.home.HomeRoute

/**
 * The whole app's navigation: it owns the back stack and is the only place that maps a key to
 * the route that draws it. Features do not know about each other; they meet here.
 *
 * Moving between screens turns the page: the new page comes in from the side over the old one,
 * and going back takes it away the same way. Under reduced motion the pages cross-fade instead.
 */
@Composable
fun TroskoNavDisplay(modifier: Modifier = Modifier) {
    val backStack = rememberNavBackStack(HomeKey)
    val resultBus = rememberResultEventBus()
    // A second press on a row, before the page has turned, must not open the record twice.
    val openRecord: (RecordId) -> Unit = { id ->
        if (backStack.lastOrNull() !is ExpenseEditKey) backStack.add(ExpenseEditKey(id.value.toString()))
    }
    val reducedMotion = TroskoTheme.reducedMotion
    val turnForward = if (reducedMotion) crossFade() else pageIn()
    val turnBack = if (reducedMotion) crossFade() else pageOut()

    NavDisplay(
        backStack = backStack,
        modifier = modifier,
        onBack = { backStack.removeLastOrNull() },
        // The second decorator gives every entry its own view model store, cleared when the
        // entry leaves the back stack. The third lets one screen hand a result to another.
        entryDecorators =
            listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator(),
                rememberResultEventBusNavEntryDecorator(resultBus),
            ),
        transitionSpec = { turnForward },
        popTransitionSpec = { turnBack },
        predictivePopTransitionSpec = { turnBack },
        entryProvider =
            entryProvider {
                entry<HomeKey> {
                    HomeRoute(onNewExpense = { backStack.add(ExpenseEntryKey) }, onRecordClick = openRecord)
                }
                entry<ExpenseEntryKey> { ExpenseEntryRoute(onRecordClick = openRecord) }
                entry<ExpenseEditKey> { key ->
                    ExpenseEditRoute(
                        recordId = key.recordId,
                        onDone = { if (backStack.lastOrNull() == key) backStack.removeLastOrNull() },
                    )
                }
            },
    )
}

// How far the page underneath shifts while the page on top travels the full width.
private const val UNDER_PAGE_SHIFT_DIVISOR = 4

// The page on top travels the whole width; the page underneath only gives way a little.
private fun pageIn(): ContentTransform =
    slideInHorizontally(TroskoMotion.settle()) { width -> width } togetherWith
        slideOutHorizontally(TroskoMotion.settle()) { width -> -width / UNDER_PAGE_SHIFT_DIVISOR }

private fun pageOut(): ContentTransform =
    slideInHorizontally(TroskoMotion.settle()) { width -> -width / UNDER_PAGE_SHIFT_DIVISOR } togetherWith
        slideOutHorizontally(TroskoMotion.settle()) { width -> width }

private fun crossFade(): ContentTransform = fadeIn(TroskoMotion.reduced()) togetherWith fadeOut(TroskoMotion.reduced())
