package com.apzlocz.app

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.apzlocz.app.service.AppWatcherService
import com.apzlocz.app.ui.components.CollapsingHeaderScaffold
import com.apzlocz.app.ui.components.OneUiCard
import com.apzlocz.app.ui.components.OneUiGroupedList
import com.apzlocz.app.ui.components.OneUiSwitch
import com.apzlocz.app.ui.components.OneUiTitledCard
import com.apzlocz.app.ui.components.PillButton
import com.apzlocz.app.ui.components.PillButtonStyle
import com.apzlocz.app.ui.components.PinActionKey
import com.apzlocz.app.ui.components.PinDots
import com.apzlocz.app.ui.components.PinKey
import com.apzlocz.app.ui.components.StatusBadge
import com.apzlocz.app.ui.components.StatusBadgeType
import com.apzlocz.app.ui.theme.ApzLoczTheme
import com.apzlocz.app.ui.theme.OneUiTheme
import com.apzlocz.app.ui.theme.ThemeMode

/**
 * MainActivity hosting the temporary Design Gallery for Phase 1.
 * Allows interactive verification of all One UI design system tokens,
 * components, animations, and light/dark theme rendering.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var themeMode by remember { mutableStateOf(ThemeMode.SYSTEM) }
            val isConnected by AppWatcherService.isConnected.collectAsState()
            val lastPackage by AppWatcherService.lastDetectedPackage.collectAsState()

            ApzLoczTheme(themeMode = themeMode) {
                DesignGalleryScreen(
                    currentThemeMode = themeMode,
                    onThemeModeChange = { themeMode = it },
                    isServiceConnected = isConnected,
                    lastDetectedPackage = lastPackage,
                    onOpenAccessibilitySettings = {
                        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                        startActivity(intent)
                    }
                )
            }
        }
    }
}

@Composable
fun DesignGalleryScreen(
    currentThemeMode: ThemeMode,
    onThemeModeChange: (ThemeMode) -> Unit,
    isServiceConnected: Boolean = false,
    lastDetectedPackage: String? = null,
    onOpenAccessibilitySettings: () -> Unit = {}
) {
    var switch1Checked by remember { mutableStateOf(true) }
    var switch2Checked by remember { mutableStateOf(false) }

    // PIN Keypad state for interactive verification
    var enteredPin by remember { mutableStateOf("") }
    var isPinError by remember { mutableStateOf(false) }

    CollapsingHeaderScaffold(
        title = "Design Gallery",
        subtitle = "Samsung One UI Inspired Component Library"
    ) {
        // Phase 3 — Foreground Detection Debug Card
        item(key = "foreground_detection_debug") {
            OneUiTitledCard(
                title = "Foreground App Detection",
                subtitle = "Event-driven AccessibilityService (Phase 3)"
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Service Status:",
                            style = OneUiTheme.typography.body,
                            color = OneUiTheme.colors.onSurface
                        )
                        StatusBadge(
                            text = if (isServiceConnected) "Active" else "Inactive",
                            type = if (isServiceConnected) StatusBadgeType.SUCCESS else StatusBadgeType.WARNING
                        )
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = OneUiTheme.colors.surfaceVariant,
                                shape = OneUiTheme.shapes.chip
                            )
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "Last Detected Package:",
                            style = OneUiTheme.typography.caption,
                            color = OneUiTheme.colors.onSurfaceMuted
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = lastDetectedPackage ?: "None (open another app to test)",
                            style = OneUiTheme.typography.body,
                            color = if (lastDetectedPackage != null) OneUiTheme.colors.accent else OneUiTheme.colors.onSurface
                        )
                    }

                    PillButton(
                        text = "Open Accessibility Settings",
                        onClick = onOpenAccessibilitySettings,
                        style = PillButtonStyle.PRIMARY,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // 1. Theme Selector
        item(key = "theme_selector") {
            OneUiTitledCard(
                title = "Theme Selector",
                subtitle = "Verify light, dark, and system modes"
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PillButton(
                        text = "System",
                        onClick = { onThemeModeChange(ThemeMode.SYSTEM) },
                        style = if (currentThemeMode == ThemeMode.SYSTEM) PillButtonStyle.PRIMARY else PillButtonStyle.TONAL,
                        modifier = Modifier.weight(1f)
                    )
                    PillButton(
                        text = "Light",
                        onClick = { onThemeModeChange(ThemeMode.LIGHT) },
                        style = if (currentThemeMode == ThemeMode.LIGHT) PillButtonStyle.PRIMARY else PillButtonStyle.TONAL,
                        modifier = Modifier.weight(1f)
                    )
                    PillButton(
                        text = "Dark",
                        onClick = { onThemeModeChange(ThemeMode.DARK) },
                        style = if (currentThemeMode == ThemeMode.DARK) PillButtonStyle.PRIMARY else PillButtonStyle.TONAL,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // 2. Status Badges
        item(key = "status_badges") {
            OneUiTitledCard(
                title = "Status Badges",
                subtitle = "Tokens for system and security status"
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatusBadge(text = "Protected", type = StatusBadgeType.SUCCESS)
                    StatusBadge(text = "Attention", type = StatusBadgeType.WARNING)
                    StatusBadge(text = "Unprotected", type = StatusBadgeType.ERROR)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // 3. One UI Grouped List & Switches
        item(key = "grouped_list") {
            Text(
                text = "Grouped List & Switches",
                style = OneUiTheme.typography.title,
                color = OneUiTheme.colors.onSurface,
                modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
            )
            OneUiGroupedList {
                Item(
                    title = "App Lock Protection",
                    subtitle = "Lock apps automatically on open",
                    trailing = {
                        OneUiSwitch(
                            checked = switch1Checked,
                            onCheckedChange = { switch1Checked = it }
                        )
                    }
                )
                Item(
                    title = "Biometric Authentication",
                    subtitle = "Use fingerprint or face unlock",
                    trailing = {
                        OneUiSwitch(
                            checked = switch2Checked,
                            onCheckedChange = { switch2Checked = it }
                        )
                    }
                )
                Item(
                    title = "Relock Policy",
                    subtitle = "Immediately upon leaving app",
                    trailing = {
                        Text(
                            text = "Immediate",
                            style = OneUiTheme.typography.label,
                            color = OneUiTheme.colors.accent
                        )
                    },
                    showDivider = false
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // 4. Pill Buttons
        item(key = "buttons") {
            OneUiTitledCard(
                title = "Pill Buttons",
                subtitle = "Primary, Tonal, and Disabled states"
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    PillButton(
                        text = "Primary Pill Button",
                        onClick = {},
                        style = PillButtonStyle.PRIMARY,
                        modifier = Modifier.fillMaxWidth()
                    )
                    PillButton(
                        text = "Tonal Pill Button",
                        onClick = {},
                        style = PillButtonStyle.TONAL,
                        modifier = Modifier.fillMaxWidth()
                    )
                    PillButton(
                        text = "Disabled Button",
                        onClick = {},
                        style = PillButtonStyle.PRIMARY,
                        enabled = false,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // 5. Interactive PIN Keypad & Dots Preview
        item(key = "pin_keypad") {
            OneUiTitledCard(
                title = "PIN Dots & Keypad",
                subtitle = "Interactive preview with haptic & shake animation"
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    PinDots(
                        pinLength = 4,
                        enteredCount = enteredPin.length,
                        isError = isPinError,
                        onErrorAnimationEnd = {
                            isPinError = false
                            enteredPin = ""
                        }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Keypad Row 1
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        PinKey("1", onClick = { if (enteredPin.length < 4) enteredPin += "1" })
                        PinKey("2", subLetters = "ABC", onClick = { if (enteredPin.length < 4) enteredPin += "2" })
                        PinKey("3", subLetters = "DEF", onClick = { if (enteredPin.length < 4) enteredPin += "3" })
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    // Keypad Row 2
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        PinKey("4", subLetters = "GHI", onClick = { if (enteredPin.length < 4) enteredPin += "4" })
                        PinKey("5", subLetters = "JKL", onClick = { if (enteredPin.length < 4) enteredPin += "5" })
                        PinKey("6", subLetters = "MNO", onClick = { if (enteredPin.length < 4) enteredPin += "6" })
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    // Keypad Row 3
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        PinKey("7", subLetters = "PQRS", onClick = { if (enteredPin.length < 4) enteredPin += "7" })
                        PinKey("8", subLetters = "TUV", onClick = { if (enteredPin.length < 4) enteredPin += "8" })
                        PinKey("9", subLetters = "WXYZ", onClick = { if (enteredPin.length < 4) enteredPin += "9" })
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    // Keypad Row 4
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        PinActionKey(
                            onClick = { isPinError = true },
                            contentDescriptionText = "Simulate Shake Error"
                        ) {
                            Text(
                                text = "Shake",
                                style = OneUiTheme.typography.caption,
                                color = OneUiTheme.colors.error
                            )
                        }
                        PinKey("0", onClick = { if (enteredPin.length < 4) enteredPin += "0" })
                        PinActionKey(
                            onClick = {
                                if (enteredPin.isNotEmpty()) {
                                    enteredPin = enteredPin.dropLast(1)
                                }
                            },
                            contentDescriptionText = "Delete last digit"
                        ) {
                            Text(
                                text = "⌫",
                                style = OneUiTheme.typography.title,
                                color = OneUiTheme.colors.onSurface
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(name = "Light Theme Gallery", showBackground = true)
@Composable
fun LightGalleryPreview() {
    ApzLoczTheme(themeMode = ThemeMode.LIGHT) {
        DesignGalleryScreen(
            currentThemeMode = ThemeMode.LIGHT,
            onThemeModeChange = {}
        )
    }
}

@Preview(name = "Dark Theme Gallery", showBackground = true)
@Composable
fun DarkGalleryPreview() {
    ApzLoczTheme(themeMode = ThemeMode.DARK) {
        DesignGalleryScreen(
            currentThemeMode = ThemeMode.DARK,
            onThemeModeChange = {}
        )
    }
}

