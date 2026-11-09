package com.apzlocz.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.apzlocz.app.ui.theme.OneUiTheme

/**
 * Reachability Header Scaffold matching Samsung One UI layout paradigm:
 * Large title at the top, scrollable content, and easy reachability.
 */
@Composable
fun CollapsingHeaderScaffold(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    bottomBar: (@Composable () -> Unit)? = null,
    content: LazyListScope.() -> Unit
) {
    val listState = rememberLazyListState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(OneUiTheme.colors.background)
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = OneUiTheme.spacing.screenPadding,
                end = OneUiTheme.spacing.screenPadding,
                top = OneUiTheme.spacing.xxl,
                bottom = if (bottomBar != null) 96.dp else OneUiTheme.spacing.xxl
            )
        ) {
            item(key = "header") {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            top = OneUiTheme.spacing.xl,
                            bottom = OneUiTheme.spacing.xl
                        )
                ) {
                    Text(
                        text = title,
                        style = OneUiTheme.typography.display,
                        color = OneUiTheme.colors.onSurface
                    )
                    if (subtitle != null) {
                        Spacer(modifier = Modifier.height(OneUiTheme.spacing.xs))
                        Text(
                            text = subtitle,
                            style = OneUiTheme.typography.body,
                            color = OneUiTheme.colors.onSurfaceMuted
                        )
                    }
                }
            }

            content()
        }

        if (bottomBar != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(androidx.compose.ui.Alignment.BottomCenter)
                    .background(OneUiTheme.colors.background.copy(alpha = 0.95f))
                    .padding(OneUiTheme.spacing.screenPadding)
            ) {
                bottomBar()
            }
        }
    }
}
