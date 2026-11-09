package com.apzlocz.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.keyframes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.apzlocz.app.ui.theme.OneUiMotion
import com.apzlocz.app.ui.theme.OneUiTheme
import kotlin.math.roundToInt

/**
 * Animated PIN indicator dots per docs/04_DESIGN_SYSTEM.md.
 * Supports smooth fill animation and damped-spring shake effect on error.
 */
@Composable
fun PinDots(
    pinLength: Int,
    enteredCount: Int,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    onErrorAnimationEnd: (() -> Unit)? = null
) {
    val shakeOffset = remember { Animatable(0f) }

    LaunchedEffect(isError) {
        if (isError) {
            shakeOffset.animateTo(
                targetValue = 0f,
                animationSpec = keyframes {
                    durationMillis = 350
                    0f at 0
                    (-24f) at 50
                    24f at 100
                    (-16f) at 150
                    16f at 200
                    (-8f) at 250
                    8f at 300
                    0f at 350
                }
            )
            onErrorAnimationEnd?.invoke()
        }
    }

    Row(
        modifier = modifier
            .offset { IntOffset(shakeOffset.value.roundToInt(), 0) },
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 0 until pinLength) {
            val isFilled = i < enteredCount
            val dotColor by animateColorAsState(
                targetValue = when {
                    isError -> OneUiTheme.colors.error
                    isFilled -> OneUiTheme.colors.accent
                    else -> OneUiTheme.colors.surfaceVariant
                },
                animationSpec = OneUiMotion.fastTween(),
                label = "PinDotColor_$i"
            )

            Box(
                modifier = Modifier
                    .size(16.dp)
                    .background(color = dotColor, shape = CircleShape)
            )
        }
    }
}
