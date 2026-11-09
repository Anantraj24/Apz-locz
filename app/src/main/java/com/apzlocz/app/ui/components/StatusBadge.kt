package com.apzlocz.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.apzlocz.app.ui.theme.OneUiTheme

enum class StatusBadgeType {
    SUCCESS,
    WARNING,
    ERROR
}

/**
 * Status indicator badge matching Samsung One UI status chip ergonomics.
 * Used for protection status indicators (Protected, Attention Required, Unprotected).
 */
@Composable
fun StatusBadge(
    text: String,
    type: StatusBadgeType,
    modifier: Modifier = Modifier
) {
    val (dotColor, containerColor) = when (type) {
        StatusBadgeType.SUCCESS -> OneUiTheme.colors.success to OneUiTheme.colors.success.copy(alpha = 0.15f)
        StatusBadgeType.WARNING -> OneUiTheme.colors.warning to OneUiTheme.colors.warning.copy(alpha = 0.15f)
        StatusBadgeType.ERROR -> OneUiTheme.colors.error to OneUiTheme.colors.error.copy(alpha = 0.15f)
    }

    val shape = OneUiTheme.shapes.chip

    Row(
        modifier = modifier
            .clip(shape)
            .background(color = containerColor, shape = shape)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(color = dotColor, shape = CircleShape)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = OneUiTheme.typography.caption,
            color = OneUiTheme.colors.onSurface
        )
    }
}
