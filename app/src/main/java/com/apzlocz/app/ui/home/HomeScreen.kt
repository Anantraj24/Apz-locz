package com.apzlocz.app.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.apzlocz.app.core.datastore.RelockMode
import com.apzlocz.app.core.util.BatteryHelper
import com.apzlocz.app.ui.components.CollapsingHeaderScaffold
import com.apzlocz.app.ui.components.OneUiCard
import com.apzlocz.app.ui.components.OneUiGroupedList
import com.apzlocz.app.ui.components.OneUiSwitch
import com.apzlocz.app.ui.components.StatusBadge
import com.apzlocz.app.ui.components.StatusBadgeType
import com.apzlocz.app.ui.theme.OneUiTheme

@Composable
fun HomeScreen(
    isCredentialSet: Boolean,
    isAccessibilityGranted: Boolean,
    isNotificationAccessGranted: Boolean,
    isBatteryOptimized: Boolean,
    isDeviceAdminActive: Boolean,
    lockedAppsCount: Int,
    relockMode: RelockMode,
    onNavigateToApps: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToCredentialSetup: () -> Unit,
    onOpenAccessibilitySettings: () -> Unit,
    onOpenNotificationSettings: () -> Unit,
    onOpenBatterySettings: () -> Unit,
    onToggleDeviceAdmin: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isFullyProtected = isCredentialSet && isAccessibilityGranted

    CollapsingHeaderScaffold(
        title = "Apz Locz",
        subtitle = if (isFullyProtected) "Device Protected" else "Setup Required",
        modifier = modifier
    ) {
        // Section 1: Protection Status Card (FR-16, AC-17)
        item(key = "protection_status_card") {
            OneUiCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 6.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(OneUiTheme.spacing.lg),
                    verticalArrangement = Arrangement.spacedBy(OneUiTheme.spacing.md)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Protection Status",
                                style = OneUiTheme.typography.title,
                                color = OneUiTheme.colors.onSurface
                            )
                            Text(
                                text = if (isFullyProtected) "All security services are running" else "Action needed to ensure security",
                                style = OneUiTheme.typography.caption,
                                color = OneUiTheme.colors.onSurfaceMuted
                            )
                        }

                        StatusBadge(
                            text = if (isFullyProtected) "Active" else "Needs Attention",
                            type = if (isFullyProtected) StatusBadgeType.SUCCESS else StatusBadgeType.WARNING
                        )
                    }

                    // Missing items pills / fixes
                    if (!isCredentialSet) {
                        FixStatusItem(
                            title = "Lock credential not configured",
                            actionText = "Set Credential",
                            onClick = onNavigateToCredentialSetup
                        )
                    }

                    if (!isAccessibilityGranted) {
                        FixStatusItem(
                            title = "App Watcher service is disabled",
                            actionText = "Enable Service",
                            onClick = onOpenAccessibilitySettings
                        )
                    }

                    if (!isNotificationAccessGranted) {
                        FixStatusItem(
                            title = "Notification listener is off",
                            actionText = "Enable Guard",
                            onClick = onOpenNotificationSettings
                        )
                    }

                    if (!isBatteryOptimized) {
                        FixStatusItem(
                            title = "Samsung battery not set to Unrestricted",
                            actionText = "Adjust Battery",
                            onClick = onOpenBatterySettings
                        )
                    }
                }
            }
        }

        // Section 2: Summary Stats
        item(key = "summary_stats") {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Locked Apps Counter Card
                OneUiCard(
                    modifier = Modifier
                        .weight(1f)
                        .clickable(onClick = onNavigateToApps)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "$lockedAppsCount",
                            style = OneUiTheme.typography.display.copy(fontSize = 32.sp),
                            color = OneUiTheme.colors.accent,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Locked Apps",
                            style = OneUiTheme.typography.body.copy(fontWeight = FontWeight.SemiBold),
                            color = OneUiTheme.colors.onSurface
                        )
                        Text(
                            text = "Tap to manage",
                            style = OneUiTheme.typography.caption,
                            color = OneUiTheme.colors.onSurfaceMuted
                        )
                    }
                }

                // Relock Mode Summary Card
                OneUiCard(
                    modifier = Modifier
                        .weight(1f)
                        .clickable(onClick = onNavigateToSettings)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = when (relockMode) {
                                RelockMode.IMMEDIATELY -> "Immediate"
                                RelockMode.AFTER_10S -> "10s Grace"
                                RelockMode.AFTER_30S -> "30s Grace"
                                RelockMode.AFTER_1MIN -> "1m Grace"
                                RelockMode.ON_PHONE_LOCK -> "On Screen Off"
                            },
                            style = OneUiTheme.typography.title.copy(fontSize = 18.sp),
                            color = OneUiTheme.colors.accent,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Relock Timing",
                            style = OneUiTheme.typography.body.copy(fontWeight = FontWeight.SemiBold),
                            color = OneUiTheme.colors.onSurface
                        )
                        Text(
                            text = "Tap to configure",
                            style = OneUiTheme.typography.caption,
                            color = OneUiTheme.colors.onSurfaceMuted
                        )
                    }
                }
            }
        }

        // Section 3: Uninstall Protection (FR-07)
        item(key = "section_uninstall_protection") {
            OneUiCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 6.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Uninstall Protection",
                            style = OneUiTheme.typography.body.copy(fontWeight = FontWeight.Medium),
                            color = OneUiTheme.colors.onSurface
                        )
                        Text(
                            text = if (isDeviceAdminActive) {
                                "Device Admin active. Apz Locz cannot be uninstalled without entering security credential."
                            } else {
                                "Prevent unauthorized deletion of Apz Locz using Device Admin."
                            },
                            style = OneUiTheme.typography.caption,
                            color = OneUiTheme.colors.onSurfaceMuted
                        )
                    }

                    OneUiSwitch(
                        checked = isDeviceAdminActive,
                        onCheckedChange = { onToggleDeviceAdmin() },
                        enabled = isCredentialSet
                    )
                }
            }
        }

        // Section 4: Quick Navigation Group
        item(key = "quick_actions") {
            OneUiGroupedList(modifier = Modifier.padding(horizontal = 24.dp, vertical = 6.dp)) {
                Item(
                    title = "App Protection List",
                    subtitle = "Select applications to lock with your credential",
                    onClick = onNavigateToApps
                )
                Item(
                    title = "Security & App Settings",
                    subtitle = "Credential type, biometrics, auto-relock, and theme",
                    onClick = onNavigateToSettings
                )
                Item(
                    title = "Samsung Optimization Guide",
                    subtitle = BatteryHelper.SAMSUNG_GUIDANCE_TEXT,
                    onClick = onOpenBatterySettings,
                    showDivider = false
                )
            }
        }

        item(key = "bottom_space") {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun FixStatusItem(
    title: String,
    actionText: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(OneUiTheme.colors.surfaceVariant)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                style = OneUiTheme.typography.caption.copy(fontWeight = FontWeight.Medium),
                color = OneUiTheme.colors.onSurface,
                modifier = Modifier.weight(1f)
            )

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(OneUiTheme.colors.accent)
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = actionText,
                    style = OneUiTheme.typography.label.copy(fontWeight = FontWeight.Bold, fontSize = 11.sp),
                    color = OneUiTheme.colors.background
                )
            }
        }
    }
}
