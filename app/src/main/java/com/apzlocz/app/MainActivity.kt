package com.apzlocz.app

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.apzlocz.app.admin.DeviceAdminHelper
import com.apzlocz.app.core.util.BatteryHelper
import com.apzlocz.app.lock.LockActivity
import com.apzlocz.app.ui.home.HomeScreen
import com.apzlocz.app.ui.onboarding.OnboardingScreen
import kotlinx.coroutines.flow.first
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
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
import com.apzlocz.app.core.datastore.CredentialType
import com.apzlocz.app.core.datastore.RelockMode
import com.apzlocz.app.service.AppWatcherService
import com.apzlocz.app.ui.apps.AppsScreen
import com.apzlocz.app.ui.apps.AppsViewModel
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

    private var cachedRelockMode: RelockMode = RelockMode.IMMEDIATELY

    override fun onStart() {
        super.onStart()
        val app = application as? ApzLoczApp ?: return
        val settingsRepo = app.container.settingsRepository
        lifecycleScope.launch {
            settingsRepo.settingsFlow.collect { settings ->
                cachedRelockMode = settings.relockMode
            }
        }
    }

    override fun onResume() {
        super.onResume()
        checkAppLockState()
    }

    override fun onStop() {
        super.onStop()
        val app = application as? ApzLoczApp ?: return
        app.container.lockStateManager.onPackageExited(packageName, cachedRelockMode)
    }

    private fun checkAppLockState() {
        val app = application as? ApzLoczApp ?: return
        val container = app.container
        val credentialManager = container.credentialManager
        val lockStateManager = container.lockStateManager
        val settingsRepo = container.settingsRepository

        lifecycleScope.launch {
            val isCredentialSet = credentialManager.isCredentialSetFlow.first()
            val settings = settingsRepo.settingsFlow.first()
            if (isCredentialSet && settings.onboardingDone) {
                if (lockStateManager.isAppLocked(packageName, settings.relockMode)) {
                    val lockIntent = Intent(this@MainActivity, LockActivity::class.java).apply {
                        putExtra(LockActivity.EXTRA_PACKAGE_NAME, packageName)
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    }
                    startActivity(lockIntent)
                }
            }
        }
    }

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
            val hideNotifications = settings?.hideNotifications ?: true
            val onboardingDone = settings?.onboardingDone ?: false
            val lockedPackages = settings?.lockedPackages ?: emptySet()

            var activeScreen by remember { mutableStateOf("home") }
            val isNotificationAccessGranted = remember(activeScreen) {
                com.apzlocz.app.service.NotificationGuardService.isNotificationAccessGranted(this@MainActivity)
            }
            val isDeviceAdminActive = remember(activeScreen) {
                com.apzlocz.app.admin.DeviceAdminHelper.isDeviceAdminActive(this@MainActivity)
            }
            val isBatteryOptimized = remember(activeScreen) {
                com.apzlocz.app.core.util.BatteryHelper.isBatteryOptimizationIgnored(this@MainActivity)
            }
            val scope = rememberCoroutineScope()

            val appsViewModel = remember {
                AppsViewModel(
                    installedAppsRepository = appContainer.installedAppsRepository,
                    settingsRepository = appContainer.settingsRepository
                )
            }
            val appsUiState by appsViewModel.uiState.collectAsState()

            BackHandler(enabled = activeScreen != "home") {
                activeScreen = "home"
            }

            ApzLoczTheme(themeMode = themeMode) {
                if (settings == null) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(OneUiTheme.colors.background)
                    )
                } else if (!onboardingDone) {
                    OnboardingScreen(
                        isCredentialSet = isCredentialSet,
                        isBiometricsSupported = isBiometricSupported,
                        isBiometricsEnabled = biometricEnabled,
                        isAccessibilityGranted = isConnected,
                        isNotificationAccessGranted = isNotificationAccessGranted,
                        isBatteryOptimizationIgnored = isBatteryOptimized,
                        onNavigateToCredentialSetup = { activeScreen = "credential" },
                        onToggleBiometrics = { enabled ->
                            scope.launch { settingsRepo.setBiometricEnabled(enabled) }
                        },
                        onOpenAccessibilitySettings = {
                            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            }
                            startActivity(intent)
                        },
                        onOpenNotificationSettings = {
                            startActivity(com.apzlocz.app.service.NotificationGuardService.createSettingsIntent())
                        },
                        onOpenBatterySettings = {
                            startActivity(com.apzlocz.app.core.util.BatteryHelper.createRequestIgnoreBatteryOptimizationsIntent(this@MainActivity))
                        },
                        onCompleteOnboarding = {
                            scope.launch { settingsRepo.setOnboardingDone(true) }
                            activeScreen = "home"
                        }
                    )
                } else {
                    Scaffold(
                        bottomBar = {
                            if (activeScreen == "home" || activeScreen == "apps" || activeScreen == "settings") {
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
                                "home" -> {
                                    HomeScreen(
                                        isCredentialSet = isCredentialSet,
                                        isAccessibilityGranted = isConnected,
                                        isNotificationAccessGranted = isNotificationAccessGranted,
                                        isBatteryOptimized = isBatteryOptimized,
                                        isDeviceAdminActive = isDeviceAdminActive,
                                        lockedAppsCount = lockedPackages.size,
                                        relockMode = relockMode,
                                        onNavigateToApps = { activeScreen = "apps" },
                                        onNavigateToSettings = { activeScreen = "settings" },
                                        onNavigateToCredentialSetup = { activeScreen = "credential" },
                                        onOpenAccessibilitySettings = {
                                            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
                                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                            }
                                            startActivity(intent)
                                        },
                                        onOpenNotificationSettings = {
                                            startActivity(com.apzlocz.app.service.NotificationGuardService.createSettingsIntent())
                                        },
                                        onOpenBatterySettings = {
                                            startActivity(com.apzlocz.app.core.util.BatteryHelper.createRequestIgnoreBatteryOptimizationsIntent(this@MainActivity))
                                        },
                                        onToggleDeviceAdmin = {
                                            if (isDeviceAdminActive) {
                                                com.apzlocz.app.admin.DeviceAdminHelper.deactivateDeviceAdmin(this@MainActivity)
                                            } else {
                                                startActivity(com.apzlocz.app.admin.DeviceAdminHelper.createEnableAdminIntent(this@MainActivity))
                                            }
                                        }
                                    )
                                }
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
                                        hideNotifications = hideNotifications,
                                        isNotificationAccessGranted = isNotificationAccessGranted,
                                        onNavigateToCredentialSetup = { activeScreen = "credential" },
                                        onBiometricToggle = { enabled ->
                                            scope.launch { settingsRepo.setBiometricEnabled(enabled) }
                                        },
                                        onRelockModeChange = { newMode ->
                                            scope.launch { settingsRepo.setRelockMode(newMode) }
                                        },
                                        onHideNotificationsToggle = { enabled ->
                                            scope.launch { settingsRepo.setHideNotifications(enabled) }
                                        },
                                        onGrantNotificationAccess = {
                                            startActivity(com.apzlocz.app.service.NotificationGuardService.createSettingsIntent())
                                        },
                                        onThemeModeChange = { newMode ->
                                            scope.launch { settingsRepo.setThemeMode(newMode) }
                                        },
                                        onNavigateBack = { activeScreen = "home" }
                                    )
                                }
                                "credential" -> {
                                    CredentialSettingsScreen(
                                        credentialManager = credentialManager,
                                        isCredentialCurrentlySet = isCredentialSet,
                                        currentCredentialType = credentialType,
                                        settingsRepository = settingsRepo,
                                        currentPinLength = settings?.pinLength ?: 4,
                                        onNavigateBack = { activeScreen = "settings" }
                                    )
                                }
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
                title = "Home",
                isSelected = currentScreen == "home",
                onClick = { onSelectScreen("home") },
                modifier = Modifier.weight(1f)
            )
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
