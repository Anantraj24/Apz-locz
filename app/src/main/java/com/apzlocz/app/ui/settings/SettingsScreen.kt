package com.apzlocz.app.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.apzlocz.app.core.datastore.CredentialType
import com.apzlocz.app.core.datastore.RelockMode
import com.apzlocz.app.ui.components.CollapsingHeaderScaffold
import com.apzlocz.app.ui.components.OneUiCard
import com.apzlocz.app.ui.components.OneUiGroupedList
import com.apzlocz.app.ui.components.OneUiSwitch
import com.apzlocz.app.ui.theme.OneUiTheme
import com.apzlocz.app.ui.theme.ThemeMode

@Composable
fun SettingsScreen(
    currentThemeMode: ThemeMode,
    currentRelockMode: RelockMode,
    credentialType: CredentialType,
    isCredentialSet: Boolean,
    isBiometricEnabled: Boolean,
    isBiometricSupported: Boolean,
    hideNotifications: Boolean,
    isNotificationAccessGranted: Boolean,
    onNavigateToCredentialSetup: () -> Unit,
    onBiometricToggle: (Boolean) -> Unit,
    onRelockModeChange: (RelockMode) -> Unit,
    onHideNotificationsToggle: (Boolean) -> Unit,
    onGrantNotificationAccess: () -> Unit,
    onThemeModeChange: (ThemeMode) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    CollapsingHeaderScaffold(
        title = "Settings",
        subtitle = "Preferences & Security",
        modifier = modifier
    ) {
        // Group 1: Security
        item(key = "section_security") {
            OneUiGroupedList(modifier = Modifier.padding(horizontal = 24.dp, vertical = 6.dp)) {
                Item(
                    title = if (isCredentialSet) "Change Lock Credential" else "Set Lock Credential",
                    subtitle = if (isCredentialSet) "Active lock type: $credentialType" else "No credential configured",
                    onClick = onNavigateToCredentialSetup
                )
            }
        }

        // Group 2: Biometrics (Phase 6)
        item(key = "section_biometrics") {
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
                            text = "Biometric Unlock",
                            style = OneUiTheme.typography.body.copy(fontWeight = FontWeight.Medium),
                            color = OneUiTheme.colors.onSurface
                        )
                        Text(
                            text = if (isBiometricSupported) {
                                "Use fingerprint or face unlock to open locked apps"
                            } else {
                                "Biometrics not enrolled or supported on this device"
                            },
                            style = OneUiTheme.typography.caption,
                            color = OneUiTheme.colors.onSurfaceMuted
                        )
                    }

                    OneUiSwitch(
                        checked = isBiometricEnabled && isBiometricSupported,
                        onCheckedChange = onBiometricToggle,
                        enabled = isBiometricSupported && isCredentialSet
                    )
                }
            }
        }

        // Group 3: Relock Timing (Phase 7)
        item(key = "section_relock") {
            OneUiCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 6.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Auto Relock Timing",
                        style = OneUiTheme.typography.title,
                        color = OneUiTheme.colors.onSurface
                    )
                    Text(
                        text = "Choose when an unlocked app requires credential entry again.",
                        style = OneUiTheme.typography.caption,
                        color = OneUiTheme.colors.onSurfaceMuted
                    )

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        RelockOptionRow(
                            label = "Immediately",
                            description = "Relock as soon as you leave the app",
                            isSelected = currentRelockMode == RelockMode.IMMEDIATELY,
                            onClick = { onRelockModeChange(RelockMode.IMMEDIATELY) }
                        )
                        RelockOptionRow(
                            label = "After 10 seconds",
                            description = "Keep unlocked for 10 seconds",
                            isSelected = currentRelockMode == RelockMode.AFTER_10S,
                            onClick = { onRelockModeChange(RelockMode.AFTER_10S) }
                        )
                        RelockOptionRow(
                            label = "After 30 seconds",
                            description = "Keep unlocked for 30 seconds",
                            isSelected = currentRelockMode == RelockMode.AFTER_30S,
                            onClick = { onRelockModeChange(RelockMode.AFTER_30S) }
                        )
                        RelockOptionRow(
                            label = "After 1 minute",
                            description = "Keep unlocked for 1 minute",
                            isSelected = currentRelockMode == RelockMode.AFTER_1MIN,
                            onClick = { onRelockModeChange(RelockMode.AFTER_1MIN) }
                        )
                        RelockOptionRow(
                            label = "When phone locks",
                            description = "Stay unlocked until the device screen turns off",
                            isSelected = currentRelockMode == RelockMode.ON_PHONE_LOCK,
                            onClick = { onRelockModeChange(RelockMode.ON_PHONE_LOCK) }
                        )
                    }
                }
            }
        }

        // Group 4: Notification Privacy (Phase 8)
        item(key = "section_notifications") {
            OneUiCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 6.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Hide Locked App Notifications",
                                style = OneUiTheme.typography.body.copy(fontWeight = FontWeight.Medium),
                                color = OneUiTheme.colors.onSurface
                            )
                            Text(
                                text = "Suppress preview content and alerts from locked apps until unlocked",
                                style = OneUiTheme.typography.caption,
                                color = OneUiTheme.colors.onSurfaceMuted
                            )
                        }

                        OneUiSwitch(
                            checked = hideNotifications && isNotificationAccessGranted,
                            onCheckedChange = onHideNotificationsToggle,
                            enabled = isCredentialSet
                        )
                    }

                    if (!isNotificationAccessGranted) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(OneUiTheme.colors.surfaceVariant)
                                .clickable(onClick = onGrantNotificationAccess)
                                .padding(12.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Notification Access Required",
                                        style = OneUiTheme.typography.body.copy(fontWeight = FontWeight.SemiBold),
                                        color = OneUiTheme.colors.onSurface
                                    )
                                    Text(
                                        text = "Tap here to allow Apz Locz in system settings to hide notifications",
                                        style = OneUiTheme.typography.caption,
                                        color = OneUiTheme.colors.onSurfaceMuted
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(OneUiTheme.colors.accent)
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = "Grant",
                                        style = OneUiTheme.typography.label.copy(fontWeight = FontWeight.Bold),
                                        color = OneUiTheme.colors.background
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Group 5: Appearance & Theme
        item(key = "section_appearance") {
            OneUiCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 6.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Theme",
                        style = OneUiTheme.typography.title,
                        color = OneUiTheme.colors.onSurface
                    )
                    Text(
                        text = "Choose light, dark (AMOLED true black), or system default.",
                        style = OneUiTheme.typography.caption,
                        color = OneUiTheme.colors.onSurfaceMuted
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ThemePill(
                            label = "Light",
                            isSelected = currentThemeMode == ThemeMode.LIGHT,
                            onClick = { onThemeModeChange(ThemeMode.LIGHT) },
                            modifier = Modifier.weight(1f)
                        )
                        ThemePill(
                            label = "Dark",
                            isSelected = currentThemeMode == ThemeMode.DARK,
                            onClick = { onThemeModeChange(ThemeMode.DARK) },
                            modifier = Modifier.weight(1f)
                        )
                        ThemePill(
                            label = "System",
                            isSelected = currentThemeMode == ThemeMode.SYSTEM,
                            onClick = { onThemeModeChange(ThemeMode.SYSTEM) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Group 4: About
        item(key = "section_about") {
            OneUiGroupedList(modifier = Modifier.padding(horizontal = 24.dp, vertical = 6.dp)) {
                Item(
                    title = "Apz Locz",
                    subtitle = "Version 1.0.0 (GitHub Open Source)",
                    onClick = null
                )
                Item(
                    title = "Privacy & Offline Guarantee",
                    subtitle = "100% Offline • Zero INTERNET permission",
                    showDivider = false,
                    onClick = null
                )
            }
        }

        item(key = "bottom_space") {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun ThemePill(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(44.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(if (isSelected) OneUiTheme.colors.accent else OneUiTheme.colors.surfaceVariant)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = OneUiTheme.typography.body.copy(
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            ),
            color = if (isSelected) OneUiTheme.colors.background else OneUiTheme.colors.onSurface
        )
    }
}

@Composable
private fun RelockOptionRow(
    label: String,
    description: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = OneUiTheme.typography.body.copy(
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                ),
                color = if (isSelected) OneUiTheme.colors.accent else OneUiTheme.colors.onSurface
            )
            Text(
                text = description,
                style = OneUiTheme.typography.caption,
                color = OneUiTheme.colors.onSurfaceMuted
            )
        }

        Box(
            modifier = Modifier
                .size(20.dp)
                .clip(CircleShape)
                .border(
                    width = 2.dp,
                    color = if (isSelected) OneUiTheme.colors.accent else OneUiTheme.colors.surfaceVariant,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(OneUiTheme.colors.accent)
                )
            }
        }
    }
}
