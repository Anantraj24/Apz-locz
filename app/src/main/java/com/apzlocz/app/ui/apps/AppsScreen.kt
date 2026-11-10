package com.apzlocz.app.ui.apps

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.apzlocz.app.core.apps.AppInfo
import com.apzlocz.app.core.apps.IconCache
import com.apzlocz.app.ui.components.CollapsingHeaderScaffold
import com.apzlocz.app.ui.components.OneUiCard
import com.apzlocz.app.ui.components.OneUiSwitch
import com.apzlocz.app.ui.components.PillButton
import com.apzlocz.app.ui.components.PillButtonStyle
import com.apzlocz.app.ui.theme.OneUiTheme

@Composable
fun AppsScreen(
    uiState: AppsUiState,
    iconCache: IconCache,
    onSearchQueryChange: (String) -> Unit,
    onTabSelected: (AppsTab) -> Unit,
    onToggleLock: (String) -> Unit,
    onToggleShowSystemApps: (Boolean) -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    CollapsingHeaderScaffold(
        title = "App Lock",
        subtitle = "${uiState.lockedAppsCount} of ${uiState.totalAppsCount} apps locked",
        modifier = modifier
    ) {
        // Search Pill
        item(key = "search_pill") {
            SearchPill(
                query = uiState.searchQuery,
                onQueryChange = onSearchQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 6.dp)
            )
        }

        // Tabs & System App Toggle
        item(key = "tabs_and_controls") {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 6.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    TabPill(
                        title = "All",
                        count = uiState.totalAppsCount,
                        isSelected = uiState.selectedTab == AppsTab.ALL,
                        onClick = { onTabSelected(AppsTab.ALL) },
                        modifier = Modifier.weight(1f)
                    )
                    TabPill(
                        title = "Locked",
                        count = uiState.lockedAppsCount,
                        isSelected = uiState.selectedTab == AppsTab.LOCKED,
                        onClick = { onTabSelected(AppsTab.LOCKED) },
                        modifier = Modifier.weight(1f)
                    )
                }

                // Show System Apps Toggle
                OneUiCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Include system apps",
                                style = OneUiTheme.typography.body.copy(
                                    fontWeight = FontWeight.Medium
                                ),
                                color = OneUiTheme.colors.onSurface
                            )
                            Text(
                                text = "Show pre-installed system applications",
                                style = OneUiTheme.typography.caption,
                                color = OneUiTheme.colors.onSurfaceMuted
                            )
                        }
                        OneUiSwitch(
                            checked = uiState.showSystemApps,
                            onCheckedChange = onToggleShowSystemApps
                        )
                    }
                }
            }
        }

        // Loading State
        if (uiState.isLoading) {
            item(key = "loading_state") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = OneUiTheme.colors.accent,
                        strokeWidth = 3.dp
                    )
                }
            }
        } else if (uiState.apps.isEmpty()) {
            // Empty State
            item(key = "empty_state") {
                EmptyStateView(
                    searchQuery = uiState.searchQuery,
                    selectedTab = uiState.selectedTab,
                    onClearSearch = { onSearchQueryChange("") },
                    onSwitchToAll = { onTabSelected(AppsTab.ALL) }
                )
            }
        } else {
            // App List Items
            items(
                items = uiState.apps,
                key = { it.packageName }
            ) { app ->
                AppRowCard(
                    app = app,
                    iconCache = iconCache,
                    onToggleLock = { onToggleLock(app.packageName) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 3.dp)
                )
            }
        }

        // Bottom breathing room
        item(key = "bottom_spacer") {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun SearchPill(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(OneUiTheme.colors.surfaceVariant)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "🔍",
                fontSize = 14.sp,
                modifier = Modifier.padding(end = 10.dp)
            )

            Box(modifier = Modifier.weight(1f)) {
                if (query.isEmpty()) {
                    Text(
                        text = "Search apps...",
                        style = OneUiTheme.typography.body,
                        color = OneUiTheme.colors.onSurfaceMuted
                    )
                }
                BasicTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    singleLine = true,
                    textStyle = OneUiTheme.typography.body.copy(
                        color = OneUiTheme.colors.onSurface
                    ),
                    cursorBrush = SolidColor(OneUiTheme.colors.accent),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (query.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(OneUiTheme.colors.onSurfaceMuted.copy(alpha = 0.2f))
                        .clickable { onQueryChange("") },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "×",
                        color = OneUiTheme.colors.onSurface,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun TabPill(
    title: String,
    count: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isSelected) {
        OneUiTheme.colors.accent
    } else {
        OneUiTheme.colors.surface
    }

    val contentColor = if (isSelected) {
        OneUiTheme.colors.background
    } else {
        OneUiTheme.colors.onSurface
    }

    Box(
        modifier = modifier
            .height(44.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = title,
                style = OneUiTheme.typography.label.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                ),
                color = contentColor
            )
            Spacer(modifier = Modifier.width(6.dp))
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(
                        if (isSelected) contentColor.copy(alpha = 0.2f)
                        else OneUiTheme.colors.surfaceVariant
                    )
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(
                    text = count.toString(),
                    style = OneUiTheme.typography.caption.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    ),
                    color = contentColor
                )
            }
        }
    }
}

@Composable
private fun AppRowCard(
    app: AppInfo,
    iconCache: IconCache,
    onToggleLock: () -> Unit,
    modifier: Modifier = Modifier
) {
    OneUiCard(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AppIconView(
                packageName = app.packageName,
                appName = app.appName,
                iconCache = iconCache
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = app.appName,
                    style = OneUiTheme.typography.body.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = OneUiTheme.colors.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (app.isSystemApp) {
                        Text(
                            text = "System • ",
                            style = OneUiTheme.typography.caption,
                            color = OneUiTheme.colors.accent
                        )
                    }
                    Text(
                        text = app.packageName,
                        style = OneUiTheme.typography.caption,
                        color = OneUiTheme.colors.onSurfaceMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            OneUiSwitch(
                checked = app.isLocked,
                onCheckedChange = { onToggleLock() }
            )
        }
    }
}

@Composable
private fun EmptyStateView(
    searchQuery: String,
    selectedTab: AppsTab,
    onClearSearch: () -> Unit,
    onSwitchToAll: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 48.dp, horizontal = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = if (searchQuery.isNotEmpty()) "🔍" else "🔒",
                fontSize = 40.sp
            )
            Text(
                text = if (searchQuery.isNotEmpty()) {
                    "No apps found for \"$searchQuery\""
                } else if (selectedTab == AppsTab.LOCKED) {
                    "No apps locked yet"
                } else {
                    "No launcher apps found"
                },
                style = OneUiTheme.typography.title.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = OneUiTheme.colors.onSurface,
                textAlign = TextAlign.Center
            )
            Text(
                text = if (searchQuery.isNotEmpty()) {
                    "Try checking your spelling or clear the search query."
                } else if (selectedTab == AppsTab.LOCKED) {
                    "Switch to the 'All' tab to select applications you wish to protect."
                } else {
                    "No launchable third-party applications were detected."
                },
                style = OneUiTheme.typography.body,
                color = OneUiTheme.colors.onSurfaceMuted,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            if (searchQuery.isNotEmpty()) {
                PillButton(
                    text = "Clear search",
                    style = PillButtonStyle.TONAL,
                    onClick = onClearSearch
                )
            } else if (selectedTab == AppsTab.LOCKED) {
                PillButton(
                    text = "View all apps",
                    style = PillButtonStyle.PRIMARY,
                    onClick = onSwitchToAll
                )
            }
        }
    }
}
