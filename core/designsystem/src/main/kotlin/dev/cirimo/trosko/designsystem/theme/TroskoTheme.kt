// A theme is the legitimate use of composition locals, and this file is the only place in the
// app that defines any. The rule stays on everywhere else.
@file:Suppress("ktlint:compose:compositionlocal-allowlist")

package dev.cirimo.trosko.designsystem.theme

import android.provider.Settings
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext

private val LocalTroskoColors = staticCompositionLocalOf { LightColors }
private val LocalTroskoTypography = staticCompositionLocalOf { DefaultTypography }
private val LocalTroskoShapes = staticCompositionLocalOf { DefaultShapes }
private val LocalReducedMotion = staticCompositionLocalOf { false }

/**
 * The root of the design system. Every screen and every preview is drawn inside it.
 *
 * The theme follows the device: dark when the system is dark. It deliberately ignores the
 * system's dynamic colours, because the palette is part of Troško's identity.
 */
@Composable
fun TroskoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    // Read once per composition root: the setting changes rarely, and never mid-gesture.
    val reducedMotion =
        remember(context) {
            Settings.Global.getFloat(context.contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f) == 0f
        }

    CompositionLocalProvider(
        LocalTroskoColors provides if (darkTheme) DarkColors else LightColors,
        LocalTroskoTypography provides DefaultTypography,
        LocalTroskoShapes provides DefaultShapes,
        LocalReducedMotion provides reducedMotion,
        content = content,
    )
}

/** Access to the current theme from inside a composable, as `TroskoTheme.colors.ink`. */
object TroskoTheme {
    val colors: TroskoColors
        @Composable @ReadOnlyComposable
        get() = LocalTroskoColors.current

    val typography: TroskoTypography
        @Composable @ReadOnlyComposable
        get() = LocalTroskoTypography.current

    val shapes: TroskoShapes
        @Composable @ReadOnlyComposable
        get() = LocalTroskoShapes.current

    /** True when the user has switched animations off in the system settings. */
    val reducedMotion: Boolean
        @Composable @ReadOnlyComposable
        get() = LocalReducedMotion.current
}
