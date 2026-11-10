package com.apzlocz.app.ui.settings

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.apzlocz.app.core.datastore.CredentialType
import com.apzlocz.app.ui.components.CollapsingHeaderScaffold
import com.apzlocz.app.ui.components.OneUiCard
import com.apzlocz.app.ui.components.OneUiGroupedList
import com.apzlocz.app.ui.components.OneUiSwitch
import com.apzlocz.app.ui.theme.OneUiTheme
import com.apzlocz.app.ui.theme.ThemeMode

@Composable
fun SettingsScreen(
    currentThemeMode: ThemeMode,
    credentialType: CredentialType,
    isCredentialSet: Boolean,
    isBiometricEnabled: Boolean,
    isBiometricSupported: Boolean,
    onNavigateToCredentialSetup: () -> Unit,
    onBiometricToggle: (Boolean) -> Unit,
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

        // Group 3: Appearance & Theme
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
