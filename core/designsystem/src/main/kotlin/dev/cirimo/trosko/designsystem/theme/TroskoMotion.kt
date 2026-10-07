package dev.cirimo.trosko.designsystem.theme

import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween

/**
 * The only animation specifications the app uses. Things move on springs, so they can be
 * interrupted at any moment without a visible jump. See docs/DESIGN.md, motion.
 */
object TroskoMotion {
    private const val POP_DAMPING = 0.55f
    private const val SETTLE_DAMPING = 0.8f
    private const val REDUCED_FADE_MILLIS = 150

    /** A press going down and coming back: fast and with no bounce. */
    fun <T> press(): FiniteAnimationSpec<T> =
        spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessHigh)

    /** Something landing on the page, such as a saved record: one visible bounce. */
    fun <T> pop(): FiniteAnimationSpec<T> = spring(dampingRatio = POP_DAMPING, stiffness = Spring.StiffnessMediumLow)

    /** Something moving to a new place and settling there without calling attention. */
    fun <T> settle(): FiniteAnimationSpec<T> =
        spring(dampingRatio = SETTLE_DAMPING, stiffness = Spring.StiffnessMediumLow)

    /** What every animation becomes when the user has asked for reduced motion. */
    fun <T> reduced(): FiniteAnimationSpec<T> = tween(durationMillis = REDUCED_FADE_MILLIS)
}
