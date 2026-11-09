package com.apzlocz.app.ui.theme

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Immutable

/**
 * Motion tokens strictly matching docs/04_DESIGN_SYSTEM.md.
 */
object OneUiMotion {
    const val DurationFast = 120
    const val DurationMedium = 220
    const val DurationSlow = 350

    val EaseOut = CubicBezierEasing(0.2f, 0.0f, 0.0f, 1.0f)

    fun <T> fastTween() = tween<T>(
        durationMillis = DurationFast,
        easing = EaseOut
    )

    fun <T> mediumTween() = tween<T>(
        durationMillis = DurationMedium,
        easing = EaseOut
    )

    fun <T> slowTween() = tween<T>(
        durationMillis = DurationSlow,
        easing = EaseOut
    )

    fun <T> springSoft() = spring<T>(
        dampingRatio = 0.75f,
        stiffness = 400f
    )

    fun <T> springBouncy() = spring<T>(
        dampingRatio = 0.55f,
        stiffness = 500f
    )
}
