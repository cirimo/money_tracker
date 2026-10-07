package dev.cirimo.trosko.feature.expense

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.isSpecified

/**
 * Stacks the page over the pad. The pad takes the height it needs and the page gets what is
 * left of the screen. When the pad alone is taller than the screen (very large text, or the
 * keyboard open for the note), the page keeps a minimum height and the whole thing scrolls.
 *
 * @param minPageHeight the least the page may be given; left unspecified, it is the height the
 * page's own content needs.
 */
@Composable
internal fun PageAbovePad(
    page: @Composable () -> Unit,
    pad: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    minPageHeight: Dp = Dp.Unspecified,
) {
    BoxWithConstraints(modifier = modifier) {
        val viewportHeight = constraints.maxHeight
        val fixedMinPageHeight =
            if (minPageHeight.isSpecified) with(LocalDensity.current) { minPageHeight.roundToPx() } else null

        Layout(
            contents = listOf(page, pad),
            modifier = Modifier.verticalScroll(rememberScrollState()),
        ) { (pageMeasurables, padMeasurables), constraints ->
            val width = constraints.maxWidth
            val padPlaceable = padMeasurables.single().measure(Constraints.fixedWidth(width))
            val pageMeasurable = pageMeasurables.single()
            val leastPageHeight = fixedMinPageHeight ?: pageMeasurable.minIntrinsicHeight(width)
            val pageHeight = maxOf(viewportHeight - padPlaceable.height, leastPageHeight)
            val pagePlaceable = pageMeasurable.measure(Constraints.fixed(width, pageHeight))

            layout(width, pageHeight + padPlaceable.height) {
                pagePlaceable.place(0, 0)
                padPlaceable.place(0, pageHeight)
            }
        }
    }
}
