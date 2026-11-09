package com.apzlocz.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.apzlocz.app.ui.theme.OneUiTheme

/**
 * Container for grouped list items per Samsung One UI design principles.
 * Uses 24dp rounded corners and surface background.
 */
@Composable
fun OneUiGroupedList(
    modifier: Modifier = Modifier,
    backgroundColor: Color = OneUiTheme.colors.surface,
    content: @Composable OneUiGroupedListScope.() -> Unit
) {
    val shape = OneUiTheme.shapes.listGroup
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(color = backgroundColor, shape = shape)
    ) {
        val scope = OneUiGroupedListScopeImpl()
        scope.content()
    }
}

interface OneUiGroupedListScope {
    @Composable
    fun Item(
        title: String,
        modifier: Modifier = Modifier,
        subtitle: String? = null,
        leading: (@Composable () -> Unit)? = null,
        trailing: (@Composable () -> Unit)? = null,
        onClick: (() -> Unit)? = null,
        showDivider: Boolean = true
    )
}

private class OneUiGroupedListScopeImpl : OneUiGroupedListScope {
    @Composable
    override fun Item(
        title: String,
        modifier: Modifier,
        subtitle: String?,
        leading: (@Composable () -> Unit)?,
        trailing: (@Composable () -> Unit)?,
        onClick: (() -> Unit)?,
        showDivider: Boolean
    ) {
        OneUiListItem(
            title = title,
            modifier = modifier,
            subtitle = subtitle,
            leading = leading,
            trailing = trailing,
            onClick = onClick,
            showDivider = showDivider
        )
    }
}

/**
 * Single list item within a grouped list.
 * Touch target adheres to >= 48dp minimum.
 */
@Composable
fun OneUiListItem(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    leading: (@Composable () -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    showDivider: Boolean = true
) {
    val clickModifier = if (onClick != null) {
        Modifier.clickable(role = Role.Button, onClick = onClick)
    } else {
        Modifier
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .then(clickModifier)
                .heightIn(min = OneUiTheme.spacing.minTouchTarget)
                .padding(
                    horizontal = OneUiTheme.spacing.lg,
                    vertical = OneUiTheme.spacing.md
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (leading != null) {
                leading()
                Spacer(modifier = Modifier.width(OneUiTheme.spacing.md))
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.CenterVertically)
            ) {
                Text(
                    text = title,
                    style = OneUiTheme.typography.body,
                    color = OneUiTheme.colors.onSurface
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = OneUiTheme.typography.caption,
                        color = OneUiTheme.colors.onSurfaceMuted,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
            if (trailing != null) {
                Spacer(modifier = Modifier.width(OneUiTheme.spacing.sm))
                trailing()
            }
        }
        if (showDivider) {
            HorizontalDivider(
                modifier = Modifier.padding(
                    start = if (leading != null) 56.dp else OneUiTheme.spacing.lg,
                    end = OneUiTheme.spacing.lg
                ),
                thickness = 0.5.dp,
                color = OneUiTheme.colors.surfaceVariant
            )
        }
    }
}

