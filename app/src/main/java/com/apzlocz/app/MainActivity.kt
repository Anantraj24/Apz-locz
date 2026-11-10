package com.apzlocz.app

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.apzlocz.app.core.datastore.CredentialType
import com.apzlocz.app.service.AppWatcherService
import com.apzlocz.app.ui.apps.AppsScreen
import com.apzlocz.app.ui.apps.AppsViewModel
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
import com.apzlocz.app.ui.settings.CredentialSettingsScreen
import com.apzlocz.app.ui.settings.SettingsScreen
import com.apzlocz.app.ui.theme.ApzLoczTheme
import com.apzlocz.app.ui.theme.OneUiTheme
import com.apzlocz.app.ui.theme.ThemeMode
import kotlinx.coroutines.launch

/**
 * MainActivity hosting the primary screens for Apz Locz.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val appContainer = (application as ApzLoczApp).container

        setContent {
            val settingsRepo = appContainer.settingsRepository
            val credentialManager = appContainer.credentialManager
            val settings by settingsRepo.settingsFlow.collectAsState(initial = null)
            val themeMode = settings?.themeMode ?: ThemeMode.SYSTEM
            val relockMode = settings?.relockMode ?: com.apzlocz.app.core.datastore.RelockMode.IMMEDIATELY

            val isCredentialSet by credentialManager.isCredentialSetFlow.collectAsState(initial = false)
            val credentialType by credentialManager.credentialTypeFlow.collectAsState(initial = CredentialType.NONE)
            val biometricEnabled = settings?.biometricEnabled ?: false
            val isBiometricSupported = remember {
                com.apzlocz.app.lock.BiometricHelper.isBiometricAvailable(this@MainActivity)
            }

            val isConnected by AppWatcherService.isConnected.collectAsState()
            val lastPackage by AppWatcherService.lastDetectedPackage.collectAsState()

            var activeScreen by remember { mutableStateOf("apps") }
            val scope = rememberCoroutineScope()

            val appsViewModel = remember {
                AppsViewModel(
                    installedAppsRepository = appContainer.installedAppsRepository,
                    settingsRepository = appContainer.settingsRepository
                )
            }
            val appsUiState by appsViewModel.uiState.collectAsState()

            BackHandler(enabled = activeScreen != "apps") {
                activeScreen = "apps"
            }

            ApzLoczTheme(themeMode = themeMode) {
                Scaffold(
                    bottomBar = {
                        if (activeScreen == "apps" || activeScreen == "settings" || activeScreen == "gallery") {
                            BottomNavigationBar(
                                currentScreen = activeScreen,
                                onSelectScreen = { activeScreen = it }
                            )
                        }
                    }
                ) { paddingValues ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = paddingValues.calculateBottomPadding())
                    ) {
                        when (activeScreen) {
                            "apps" -> {
                                AppsScreen(
                                    uiState = appsUiState,
                                    iconCache = appContainer.iconCache,
                                    onSearchQueryChange = appsViewModel::onSearchQueryChange,
                                    onTabSelected = appsViewModel::onTabSelected,
                                    onToggleLock = appsViewModel::onToggleLock,
                                    onToggleShowSystemApps = appsViewModel::onToggleShowSystemApps,
                                    onRefresh = appsViewModel::refresh
                                )
                            }
                            "settings" -> {
                                SettingsScreen(
                                    currentThemeMode = themeMode,
                                    currentRelockMode = relockMode,
                                    credentialType = credentialType,
                                    isCredentialSet = isCredentialSet,
                                    isBiometricEnabled = biometricEnabled,
                                    isBiometricSupported = isBiometricSupported,
                                    onNavigateToCredentialSetup = { activeScreen = "credential" },
                                    onBiometricToggle = { enabled ->
                                        scope.launch { settingsRepo.setBiometricEnabled(enabled) }
                                    },
                                    onRelockModeChange = { newMode ->
                                        scope.launch { settingsRepo.setRelockMode(newMode) }
                                    },
                                    onThemeModeChange = { newMode ->
                                        scope.launch { settingsRepo.setThemeMode(newMode) }
                                    },
                                    onNavigateBack = { activeScreen = "apps" }
                                )
                            }
                            "credential" -> {
                                CredentialSettingsScreen(
                                    credentialManager = credentialManager,
                                    isCredentialCurrentlySet = isCredentialSet,
                                    currentCredentialType = credentialType,
                                    onNavigateBack = { activeScreen = "settings" }
                                )
                            }
                            "gallery" -> {
                                DesignGalleryScreen(
                                    currentThemeMode = themeMode,
                                    onThemeModeChange = { newMode ->
                                        scope.launch { settingsRepo.setThemeMode(newMode) }
                                    },
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
            }
        }
    }
}

@Composable
private fun BottomNavigationBar(
    currentScreen: String,
    onSelectScreen: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(OneUiTheme.colors.surface)
            .navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavTabPill(
                title = "Apps",
                isSelected = currentScreen == "apps",
                onClick = { onSelectScreen("apps") },
                modifier = Modifier.weight(1f)
            )
            NavTabPill(
                title = "Settings",
                isSelected = currentScreen == "settings",
                onClick = { onSelectScreen("settings") },
                modifier = Modifier.weight(1f)
            )
            NavTabPill(
                title = "Gallery",
                isSelected = currentScreen == "gallery",
                onClick = { onSelectScreen("gallery") },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun NavTabPill(
    title: String,
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
            text = title,
            style = OneUiTheme.typography.body.copy(
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            ),
            color = if (isSelected) OneUiTheme.colors.background else OneUiTheme.colors.onSurface
        )
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

    var enteredPin by remember { mutableStateOf("") }
    var isPinError by remember { mutableStateOf(false) }

    CollapsingHeaderScaffold(
        title = "Design Gallery",
        subtitle = "Samsung One UI Inspired Component Library"
    ) {
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
                        style = PillButtonStyle.PRIMARY,
                        onClick = onOpenAccessibilitySettings
                    )
                }
            }
        }

        item(key = "section_buttons") {
            OneUiTitledCard(
                title = "Pill Buttons",
                subtitle = ">= 48dp Touch Targets & One UI Spring Motion"
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    PillButton(
                        text = "Primary Action",
                        style = PillButtonStyle.PRIMARY,
                        onClick = {}
                    )
                    PillButton(
                        text = "Tonal Action",
                        style = PillButtonStyle.TONAL,
                        onClick = {}
                    )
                    PillButton(
                        text = "Outlined Action",
                        style = PillButtonStyle.OUTLINED,
                        onClick = {}
                    )
                }
            }
        }

        item(key = "section_switches") {
            OneUiTitledCard(
                title = "Switches & Toggles",
                subtitle = "Custom One UI Switches with Spring Physics"
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Biometric Unlock",
                            style = OneUiTheme.typography.body,
                            color = OneUiTheme.colors.onSurface
                        )
                        OneUiSwitch(
                            checked = switch1Checked,
                            onCheckedChange = { switch1Checked = it }
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Hide Notifications",
                            style = OneUiTheme.typography.body,
                            color = OneUiTheme.colors.onSurface
                        )
                        OneUiSwitch(
                            checked = switch2Checked,
                            onCheckedChange = { switch2Checked = it }
                        )
                    }
                }
            }
        }

        item(key = "section_badges") {
            OneUiTitledCard(
                title = "Status Badges",
                subtitle = "Protection Status & Alerts"
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatusBadge(text = "Active", type = StatusBadgeType.SUCCESS)
                    StatusBadge(text = "Needs Attention", type = StatusBadgeType.WARNING)
                    StatusBadge(text = "Locked", type = StatusBadgeType.ERROR)
                }
            }
        }

        item(key = "section_grouped_list") {
            OneUiGroupedList {
                Item(
                    title = "Change Credential",
                    subtitle = "PIN, Password, or Pattern",
                    onClick = {}
                )
                Item(
                    title = "Relock Timing",
                    subtitle = "Immediately upon exit",
                    onClick = {}
                )
                Item(
                    title = "Uninstall Protection",
                    subtitle = "Device Admin active",
                    showDivider = false,
                    onClick = {}
                )
            }
        }

        item(key = "section_pin_keypad") {
            OneUiTitledCard(
                title = "Credential Input (PIN Keypad)",
                subtitle = "Haptic Feedback & Error Shake Motion"
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    PinDots(
                        pinLength = 4,
                        enteredCount = enteredPin.length,
                        isError = isPinError
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                            PinKey(digit = "1", subLetters = "", onClick = { if (enteredPin.length < 4) enteredPin += "1" })
                            PinKey(digit = "2", subLetters = "ABC", onClick = { if (enteredPin.length < 4) enteredPin += "2" })
                            PinKey(digit = "3", subLetters = "DEF", onClick = { if (enteredPin.length < 4) enteredPin += "3" })
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                            PinKey(digit = "4", subLetters = "GHI", onClick = { if (enteredPin.length < 4) enteredPin += "4" })
                            PinKey(digit = "5", subLetters = "JKL", onClick = { if (enteredPin.length < 4) enteredPin += "5" })
                            PinKey(digit = "6", subLetters = "MNO", onClick = { if (enteredPin.length < 4) enteredPin += "6" })
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                            PinKey(digit = "7", subLetters = "PQRS", onClick = { if (enteredPin.length < 4) enteredPin += "7" })
                            PinKey(digit = "8", subLetters = "TUV", onClick = { if (enteredPin.length < 4) enteredPin += "8" })
                            PinKey(digit = "9", subLetters = "WXYZ", onClick = { if (enteredPin.length < 4) enteredPin += "9" })
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                            PinActionKey(
                                onClick = { isPinError = true },
                                contentDescriptionText = "Error"
                            ) {
                                Text(text = "Err", fontSize = 12.sp, color = OneUiTheme.colors.onSurface)
                            }
                            PinKey(digit = "0", subLetters = "+", onClick = { if (enteredPin.length < 4) enteredPin += "0" })
                            PinActionKey(
                                onClick = {
                                    if (enteredPin.isNotEmpty()) enteredPin = enteredPin.dropLast(1)
                                    isPinError = false
                                },
                                contentDescriptionText = "Delete"
                            ) {
                                Text(text = "⌫", fontSize = 18.sp, color = OneUiTheme.colors.onSurface)
                            }
                        }
                    }
                }
            }
        }

        item(key = "section_cards") {
            OneUiCard {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Standard One UI Card",
                        style = OneUiTheme.typography.title,
                        color = OneUiTheme.colors.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "26dp rounded squircle corners, elevated surface with distinct contrast.",
                        style = OneUiTheme.typography.body,
                        color = OneUiTheme.colors.onSurfaceMuted
                    )
                }
            }
        }
    }
}
