package com.apzlocz.app.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.apzlocz.app.ui.theme.OneUiMotion
import com.apzlocz.app.ui.theme.OneUiTheme

enum class PillButtonStyle {
    PRIMARY,
    TONAL,
    OUTLINED
}

/**
 * Pill-shaped button matching Samsung One UI design principles.
 * Height >= 48dp, touch feedback scale animation.
 */
@Composable
fun PillButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: PillButtonStyle = PillButtonStyle.PRIMARY,
    enabled: Boolean = true,
    leadingIcon: (@Composable () -> Unit)? = null,
    minHeight: Dp = 50.dp
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed && enabled) 0.96f else 1.0f,
        animationSpec = OneUiMotion.fastTween(),
        label = "PillButtonScale"
    )

    val (backgroundColor, contentColor) = when (style) {
        PillButtonStyle.PRIMARY -> {
            if (enabled) {
                OneUiTheme.colors.accent to Color.White
            } else {
                OneUiTheme.colors.accent.copy(alpha = 0.4f) to Color.White.copy(alpha = 0.6f)
            }
        }
        PillButtonStyle.TONAL -> {
            if (enabled) {
                OneUiTheme.colors.surfaceVariant to OneUiTheme.colors.onSurface
            } else {
                OneUiTheme.colors.surfaceVariant.copy(alpha = 0.5f) to OneUiTheme.colors.onSurfaceMuted.copy(alpha = 0.5f)
            }
        }
        PillButtonStyle.OUTLINED -> {
            Color.Transparent to (if (enabled) OneUiTheme.colors.accent else OneUiTheme.colors.onSurfaceMuted)
        }
    }

    Box(
        modifier = modifier
            .scale(scale)
            .heightIn(min = minHeight)
            .clip(CircleShape)
            .background(color = backgroundColor, shape = CircleShape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                role = Role.Button,
                onClick = onClick
            )
            .padding(horizontal = OneUiTheme.spacing.xl, vertical = OneUiTheme.spacing.sm),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (leadingIcon != null) {
                leadingIcon()
                Spacer(modifier = Modifier.width(OneUiTheme.spacing.xs))
            }
            Text(
                text = text,
                style = OneUiTheme.typography.body,
                color = contentColor
            )
        }
    }
}

