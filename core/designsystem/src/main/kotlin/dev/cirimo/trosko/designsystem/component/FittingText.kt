package dev.cirimo.trosko.designsystem.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import dev.cirimo.trosko.designsystem.theme.TroskoTheme

/**
 * How small a single word on a button or chip may shrink to stay on one line: down to the
 * caption size as it looks without any text enlargement. So enlarged text may give some of its
 * enlargement back to fit a narrow sticker, but it never ends up smaller than the smallest text
 * the app shows to everyone.
 */
@Composable
@ReadOnlyComposable
internal fun smallestFittingSize(): TextUnit =
    (TroskoTheme.typography.caption.fontSize.value / LocalDensity.current.fontScale.coerceAtLeast(1f)).sp
