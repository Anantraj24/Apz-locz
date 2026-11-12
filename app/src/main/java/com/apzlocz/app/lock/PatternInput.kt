package com.apzlocz.app.lock

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.keyframes
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.apzlocz.app.ui.theme.OneUiTheme
import kotlin.math.hypot
import kotlin.math.roundToInt

/**
 * 3x3 dot pattern unlock grid following docs/02_SRS.md §1.
 * Dot indices are normalized as ordered dash-separated string (e.g., "0-1-2-5").
 */
@Composable
fun PatternInput(
    onPatternComplete: (String) -> Unit,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    enabled: Boolean = true,
    gridSizeDp: Int = 300
) {
    val selectedDots = remember { mutableStateListOf<Int>() }
    var currentTouchPoint by remember { mutableStateOf<Offset?>(null) }

    val shakeOffset = remember { Animatable(0f) }

    LaunchedEffect(isError) {
        if (isError) {
            shakeOffset.animateTo(
                targetValue = 0f,
                animationSpec = keyframes {
                    durationMillis = 350
                    0f at 0
                    (-20f) at 50
                    20f at 100
                    (-14f) at 150
                    14f at 200
                    0f at 350
                }
            )
            selectedDots.clear()
            currentTouchPoint = null
        }
    }

    val normalDotColor = OneUiTheme.colors.surfaceVariant
    val activeColor = if (isError) OneUiTheme.colors.error else OneUiTheme.colors.accent

    Box(
        modifier = modifier
            .offset { IntOffset(shakeOffset.value.roundToInt(), 0) }
            .size(gridSizeDp.dp)
            .pointerInput(enabled) {
                if (!enabled) return@pointerInput

                detectDragGestures(
                    onDragStart = { offset ->
                        selectedDots.clear()
                        val hit = findHitDot(offset, size.width.toFloat(), size.height.toFloat())
                        if (hit != null) {
                            selectedDots.add(hit)
                        }
                        currentTouchPoint = offset
                    },
                    onDrag = { change, _ ->
                        change.consume()
                        currentTouchPoint = change.position
                        val hit = findHitDot(change.position, size.width.toFloat(), size.height.toFloat())
                        if (hit != null && !selectedDots.contains(hit)) {
                            selectedDots.add(hit)
                        }
                    },
                    onDragEnd = {
                        currentTouchPoint = null
                        if (selectedDots.size >= 4) {
                            val patternString = selectedDots.joinToString("-")
                            onPatternComplete(patternString)
                        } else if (selectedDots.isNotEmpty()) {
                            // Pattern too short (min 4 dots)
                            selectedDots.clear()
                        }
                    },
                    onDragCancel = {
                        selectedDots.clear()
                        currentTouchPoint = null
                    }
                )
            }
    ) {
        Canvas(modifier = Modifier.size(gridSizeDp.dp)) {
            val stepX = size.width / 3f
            val stepY = size.height / 3f
            val dotRadius = 8.dp.toPx()
            val ringRadius = 24.dp.toPx()
            val strokeWidth = 5.dp.toPx()

            fun getDotCenter(index: Int): Offset {
                val row = index / 3
                val col = index % 3
                return Offset(
                    x = (col + 0.5f) * stepX,
                    y = (row + 0.5f) * stepY
                )
            }

            // Draw connecting lines between selected dots
            if (selectedDots.size > 1) {
                for (i in 0 until selectedDots.size - 1) {
                    val start = getDotCenter(selectedDots[i])
                    val end = getDotCenter(selectedDots[i + 1])
                    drawLine(
                        color = activeColor,
                        start = start,
                        end = end,
                        strokeWidth = strokeWidth,
                        cap = StrokeCap.Round
                    )
                }
            }

            // Draw line to current touch point while dragging
            val currentTouch = currentTouchPoint
            if (selectedDots.isNotEmpty() && currentTouch != null) {
                val lastDotCenter = getDotCenter(selectedDots.last())
                drawLine(
                    color = activeColor.copy(alpha = 0.6f),
                    start = lastDotCenter,
                    end = currentTouch,
                    strokeWidth = strokeWidth * 0.8f,
                    cap = StrokeCap.Round
                )
            }

            // Draw all 9 dots & selection rings
            for (i in 0 until 9) {
                val center = getDotCenter(i)
                val isSelected = selectedDots.contains(i)

                if (isSelected) {
                    // Outer accent ring
                    drawCircle(
                        color = activeColor.copy(alpha = 0.25f),
                        radius = ringRadius,
                        center = center
                    )
                    drawCircle(
                        color = activeColor,
                        radius = ringRadius,
                        center = center,
                        style = Stroke(width = 2.dp.toPx())
                    )
                    // Inner solid dot
                    drawCircle(
                        color = activeColor,
                        radius = dotRadius,
                        center = center
                    )
                } else {
                    // Inactive dot
                    drawCircle(
                        color = normalDotColor,
                        radius = dotRadius,
                        center = center
                    )
                }
            }
        }
    }
}

private fun findHitDot(offset: Offset, width: Float, height: Float): Int? {
    val stepX = width / 3f
    val stepY = height / 3f
    val hitThreshold = (stepX * 0.35f)

    for (i in 0 until 9) {
        val row = i / 3
        val col = i % 3
        val centerX = (col + 0.5f) * stepX
        val centerY = (row + 0.5f) * stepY

        val distance = hypot((offset.x - centerX).toDouble(), (offset.y - centerY).toDouble()).toFloat()
        if (distance <= hitThreshold) {
            return i
        }
    }
    return null
}

