package com.apzlocz.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.apzlocz.app.ui.theme.OneUiTheme

/**
 * Standard One UI Card with 28dp rounded corners and surface background.
 * Per docs/04_DESIGN_SYSTEM.md.
 */
@Composable
fun OneUiCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = OneUiTheme.colors.surface,
    padding: Dp = OneUiTheme.spacing.lg,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val shape = OneUiTheme.shapes.card
    val clickableModifier = if (onClick != null) {
        Modifier
            .clip(shape)
            .clickable(role = Role.Button, onClick = onClick)
    } else {
        Modifier.clip(shape)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .then(clickableModifier)
            .background(color = backgroundColor, shape = shape)
            .padding(padding)
    ) {
        content()
    }
}

/**
 * Convenience One UI Card with standard title, optional subtitle, and content slot.
 */
@Composable
fun OneUiTitledCard(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    onClick: (() -> Unit)? = null,
    content: (@Composable () -> Unit)? = null
) {
    OneUiCard(
        modifier = modifier,
        onClick = onClick
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = title,
                style = OneUiTheme.typography.title,
                color = OneUiTheme.colors.onSurface
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = OneUiTheme.typography.body,
                    color = OneUiTheme.colors.onSurfaceMuted,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            if (content != null) {
                Box(modifier = Modifier.padding(top = OneUiTheme.spacing.md)) {
                    content()
                }
            }
        }
    }
}

