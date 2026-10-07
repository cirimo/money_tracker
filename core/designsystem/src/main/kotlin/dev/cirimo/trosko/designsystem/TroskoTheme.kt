package dev.cirimo.trosko.designsystem

import androidx.compose.runtime.Composable

/**
 * The root of the design system. Every screen is drawn inside it.
 *
 * It provides nothing yet, on purpose: colours, type, shapes and motion are defined by the
 * design session (docs/DESIGN.md). It exists now so that screens are already wrapped in it and
 * gain the real theme without being touched.
 */
@Composable
fun TroskoTheme(content: @Composable () -> Unit) {
    content()
}
