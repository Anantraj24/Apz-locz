package com.apzlocz.app.ui.theme

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Shape tokens defined in docs/04_DESIGN_SYSTEM.md.
 */
@Immutable
data class OneUiShapes(
    val card: Shape = RoundedCornerShape(28.dp),
    val listGroup: Shape = RoundedCornerShape(24.dp),
    val chip: Shape = RoundedCornerShape(16.dp),
    val pill: Shape = CircleShape,
    val keyCircle: Shape = CircleShape
)

/**
 * Standard spacing tokens per docs/04_DESIGN_SYSTEM.md.
 */
@Immutable
data class OneUiSpacing(
    val xxs: Dp = 4.dp,
    val xs: Dp = 8.dp,
    val sm: Dp = 12.dp,
    val md: Dp = 16.dp,
    val lg: Dp = 20.dp,
    val xl: Dp = 24.dp,
    val xxl: Dp = 32.dp,
    val screenPadding: Dp = 16.dp,
    val minTouchTarget: Dp = 48.dp,
    val pinKeySize: Dp = 72.dp
)

