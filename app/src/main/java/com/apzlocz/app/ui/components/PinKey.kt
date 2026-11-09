package com.apzlocz.app.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.apzlocz.app.ui.theme.OneUiMotion
import com.apzlocz.app.ui.theme.OneUiTheme

/**
 * 72dp circular PIN keypad key matching Samsung One UI keypad styling.
 * Supports digit + letters, press-scale animation (0.92), and haptic feedback.
 */
@Composable
fun PinKey(
    digit: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    subLetters: String? = null,
    enabled: Boolean = true
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val haptic = LocalHapticFeedback.current

    val scale by animateFloatAsState(
        targetValue = if (isPressed && enabled) 0.92f else 1.0f,
        animationSpec = OneUiMotion.fastTween(),
        label = "PinKeyScale"
    )

    val keySize = OneUiTheme.spacing.pinKeySize

    Box(
        modifier = modifier
            .size(keySize)
            .scale(scale)
            .clip(CircleShape)
            .background(
                color = if (isPressed && enabled) {
                    OneUiTheme.colors.surfaceVariant.copy(alpha = 0.8f)
                } else {
                    OneUiTheme.colors.surfaceVariant
                },
                shape = CircleShape
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                role = Role.Button,
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onClick()
                }
            )
            .semantics {
                contentDescription = digit + (subLetters?.let { ", $it" } ?: "")
            },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = digit,
                style = OneUiTheme.typography.titleLarge,
                color = OneUiTheme.colors.onSurface
            )
            if (!subLetters.isNullOrEmpty()) {
                Text(
                    text = subLetters,
                    style = OneUiTheme.typography.caption,
                    color = OneUiTheme.colors.onSurfaceMuted
                )
            }
        }
    }
}

/**
 * Action key (e.g. Backspace, Biometric, Clear) in the 72dp circular format.
 */
@Composable
fun PinActionKey(
    onClick: () -> Unit,
    contentDescriptionText: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val haptic = LocalHapticFeedback.current

    val scale by animateFloatAsState(
        targetValue = if (isPressed && enabled) 0.92f else 1.0f,
        animationSpec = OneUiMotion.fastTween(),
        label = "PinActionKeyScale"
    )

    val keySize = OneUiTheme.spacing.pinKeySize

    Box(
        modifier = modifier
            .size(keySize)
            .scale(scale)
            .clip(CircleShape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                role = Role.Button,
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onClick()
                }
            )
            .semantics {
                contentDescription = contentDescriptionText
            },
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}
