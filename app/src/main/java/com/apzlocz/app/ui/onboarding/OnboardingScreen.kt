package com.apzlocz.app.ui.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.apzlocz.app.core.util.BatteryHelper
import com.apzlocz.app.ui.components.OneUiCard
import com.apzlocz.app.ui.components.OneUiSwitch
import com.apzlocz.app.ui.components.PillButton
import com.apzlocz.app.ui.components.PillButtonStyle
import com.apzlocz.app.ui.components.StatusBadge
import com.apzlocz.app.ui.components.StatusBadgeType
import com.apzlocz.app.ui.theme.OneUiTheme

@Composable
fun OnboardingScreen(
    isCredentialSet: Boolean,
    isBiometricsSupported: Boolean,
    isBiometricsEnabled: Boolean,
    isAccessibilityGranted: Boolean,
    isNotificationAccessGranted: Boolean,
    isBatteryOptimizationIgnored: Boolean,
    onNavigateToCredentialSetup: () -> Unit,
    onToggleBiometrics: (Boolean) -> Unit,
    onOpenAccessibilitySettings: () -> Unit,
    onOpenNotificationSettings: () -> Unit,
    onOpenBatterySettings: () -> Unit,
    onCompleteOnboarding: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentStep by remember { mutableIntStateOf(0) }
    val totalSteps = 6

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(OneUiTheme.colors.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = OneUiTheme.spacing.xl, vertical = OneUiTheme.spacing.md)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Step Indicators
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = OneUiTheme.spacing.sm),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (i in 0 until totalSteps) {
                    val isCurrent = i == currentStep
                    val isPast = i < currentStep
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .size(if (isCurrent) 12.dp else 8.dp)
                            .clip(CircleShape)
                            .background(
                                when {
                                    isCurrent -> OneUiTheme.colors.accent
                                    isPast -> OneUiTheme.colors.accent.copy(alpha = 0.5f)
                                    else -> OneUiTheme.colors.surfaceVariant
                                }
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Step Content
            AnimatedContent(
                targetState = currentStep,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                modifier = Modifier.weight(1f),
                label = "onboarding_steps"
            ) { step ->
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    when (step) {
                        0 -> WelcomeStep()
                        1 -> CredentialStep(
                            isCredentialSet = isCredentialSet,
                            onSetUp = onNavigateToCredentialSetup
                        )
                        2 -> BiometricStep(
                            isSupported = isBiometricsSupported,
                            isEnabled = isBiometricsEnabled,
                            onToggle = onToggleBiometrics
                        )
                        3 -> AccessibilityStep(
                            isGranted = isAccessibilityGranted,
                            onGrant = onOpenAccessibilitySettings
                        )
                        4 -> NotificationStep(
                            isGranted = isNotificationAccessGranted,
                            onGrant = onOpenNotificationSettings
                        )
                        5 -> BatteryStep(
                            isIgnored = isBatteryOptimizationIgnored,
                            onOpenSettings = onOpenBatterySettings
                        )
                    }
                }
            }

            // Bottom Navigation Controls
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (currentStep > 0) {
                    PillButton(
                        text = "Back",
                        onClick = { currentStep -= 1 },
                        style = PillButtonStyle.OUTLINED,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.size(12.dp))
                }

                if (currentStep < totalSteps - 1) {
                    PillButton(
                        text = "Next",
                        onClick = { currentStep += 1 },
                        style = PillButtonStyle.PRIMARY,
                        modifier = Modifier.weight(1f)
                    )
                } else {
                    PillButton(
                        text = "Get Started",
                        onClick = onCompleteOnboarding,
                        style = PillButtonStyle.PRIMARY,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun WelcomeStep() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Apz Locz",
            style = OneUiTheme.typography.display,
            color = OneUiTheme.colors.onSurface,
            textAlign = TextAlign.Center
        )
        Text(
            text = "Pure Offline Privacy",
            style = OneUiTheme.typography.title,
            color = OneUiTheme.colors.accent,
            textAlign = TextAlign.Center
        )
        Text(
            text = "Designed specifically for Samsung One UI. Secure any app on your device with high-speed, local authentication.\n\nZero network permission. Zero data collection.",
            style = OneUiTheme.typography.body,
            color = OneUiTheme.colors.onSurfaceMuted,
            textAlign = TextAlign.Center,
            lineHeight = 22.sp
        )
    }
}

@Composable
private fun CredentialStep(
    isCredentialSet: Boolean,
    onSetUp: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        StatusBadge(
            text = if (isCredentialSet) "Configured" else "Action Required",
            type = if (isCredentialSet) StatusBadgeType.SUCCESS else StatusBadgeType.WARNING
        )

        Text(
            text = "Lock Credential",
            style = OneUiTheme.typography.title,
            color = OneUiTheme.colors.onSurface,
            textAlign = TextAlign.Center
        )

        Text(
            text = "Create a master PIN, password, or pattern to lock your selected apps.\n\nNote: Because Apz Locz is 100% offline, credentials cannot be recovered remotely. Please remember your code.",
            style = OneUiTheme.typography.body,
            color = OneUiTheme.colors.onSurfaceMuted,
            textAlign = TextAlign.Center
        )

        OneUiCard(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onSetUp)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isCredentialSet) "Credential Created" else "Set Credential Now",
                        style = OneUiTheme.typography.body.copy(fontWeight = FontWeight.SemiBold),
                        color = OneUiTheme.colors.onSurface
                    )
                    Text(
                        text = if (isCredentialSet) "Tap to change credential type or code" else "Choose PIN, Password, or Pattern",
                        style = OneUiTheme.typography.caption,
                        color = OneUiTheme.colors.onSurfaceMuted
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isCredentialSet) OneUiTheme.colors.surfaceVariant else OneUiTheme.colors.accent)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = if (isCredentialSet) "Change" else "Create",
                        style = OneUiTheme.typography.label.copy(fontWeight = FontWeight.Bold),
                        color = if (isCredentialSet) OneUiTheme.colors.onSurface else OneUiTheme.colors.background
                    )
                }
            }
        }
    }
}

@Composable
private fun BiometricStep(
    isSupported: Boolean,
    isEnabled: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        StatusBadge(
            text = if (isSupported) "Optional" else "Unavailable",
            type = StatusBadgeType.WARNING
        )

        Text(
            text = "Biometric Unlock",
            style = OneUiTheme.typography.title,
            color = OneUiTheme.colors.onSurface,
            textAlign = TextAlign.Center
        )

        Text(
            text = "Unlock apps in an instant using your device's fingerprint scanner or system face unlock.\n\nYour biometric keys never leave Android's secure hardware.",
            style = OneUiTheme.typography.body,
            color = OneUiTheme.colors.onSurfaceMuted,
            textAlign = TextAlign.Center
        )

        OneUiCard(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Enable Biometrics",
                        style = OneUiTheme.typography.body.copy(fontWeight = FontWeight.SemiBold),
                        color = OneUiTheme.colors.onSurface
                    )
                    Text(
                        text = if (isSupported) "Use fingerprint or face recognition" else "No enrolled biometrics found",
                        style = OneUiTheme.typography.caption,
                        color = OneUiTheme.colors.onSurfaceMuted
                    )
                }
                OneUiSwitch(
                    checked = isEnabled && isSupported,
                    onCheckedChange = onToggle,
                    enabled = isSupported
                )
            }
        }
    }
}

@Composable
private fun AccessibilityStep(
    isGranted: Boolean,
    onGrant: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        StatusBadge(
            text = if (isGranted) "Connected" else "Permission Required",
            type = if (isGranted) StatusBadgeType.SUCCESS else StatusBadgeType.WARNING
        )

        Text(
            text = "Foreground Detection",
            style = OneUiTheme.typography.title,
            color = OneUiTheme.colors.onSurface,
            textAlign = TextAlign.Center
        )

        Text(
            text = "Apz Locz needs Accessibility Service permission strictly to detect when locked apps transition to the foreground.\n\nNo keystrokes, screen contents, or personal information are ever monitored or stored.",
            style = OneUiTheme.typography.body,
            color = OneUiTheme.colors.onSurfaceMuted,
            textAlign = TextAlign.Center
        )

        OneUiCard(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onGrant)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "App Watcher Service",
                        style = OneUiTheme.typography.body.copy(fontWeight = FontWeight.SemiBold),
                        color = OneUiTheme.colors.onSurface
                    )
                    Text(
                        text = if (isGranted) "Service is active and running" else "Tap to enable in Accessibility settings",
                        style = OneUiTheme.typography.caption,
                        color = OneUiTheme.colors.onSurfaceMuted
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isGranted) OneUiTheme.colors.surfaceVariant else OneUiTheme.colors.accent)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = if (isGranted) "Active" else "Enable",
                        style = OneUiTheme.typography.label.copy(fontWeight = FontWeight.Bold),
                        color = if (isGranted) OneUiTheme.colors.onSurface else OneUiTheme.colors.background
                    )
                }
            }
        }
    }
}

@Composable
private fun NotificationStep(
    isGranted: Boolean,
    onGrant: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        StatusBadge(
            text = if (isGranted) "Protected" else "Recommended",
            type = if (isGranted) StatusBadgeType.SUCCESS else StatusBadgeType.WARNING
        )

        Text(
            text = "Notification Hiding",
            style = OneUiTheme.typography.title,
            color = OneUiTheme.colors.onSurface,
            textAlign = TextAlign.Center
        )

        Text(
            text = "Prevents incoming alerts, chats, and previews from locked apps from appearing in your notification shade until you unlock the app.\n\nNotification text is never stored or transmitted.",
            style = OneUiTheme.typography.body,
            color = OneUiTheme.colors.onSurfaceMuted,
            textAlign = TextAlign.Center
        )

        OneUiCard(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onGrant)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Notification Listener",
                        style = OneUiTheme.typography.body.copy(fontWeight = FontWeight.SemiBold),
                        color = OneUiTheme.colors.onSurface
                    )
                    Text(
                        text = if (isGranted) "Notification Guard is active" else "Tap to allow Notification Access",
                        style = OneUiTheme.typography.caption,
                        color = OneUiTheme.colors.onSurfaceMuted
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isGranted) OneUiTheme.colors.surfaceVariant else OneUiTheme.colors.accent)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = if (isGranted) "Granted" else "Grant",
                        style = OneUiTheme.typography.label.copy(fontWeight = FontWeight.Bold),
                        color = if (isGranted) OneUiTheme.colors.onSurface else OneUiTheme.colors.background
                    )
                }
            }
        }
    }
}

@Composable
private fun BatteryStep(
    isIgnored: Boolean,
    onOpenSettings: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        StatusBadge(
            text = if (isIgnored) "Optimized" else "Action Recommended",
            type = if (isIgnored) StatusBadgeType.SUCCESS else StatusBadgeType.WARNING
        )

        Text(
            text = "Samsung Battery Settings",
            style = OneUiTheme.typography.title,
            color = OneUiTheme.colors.onSurface,
            textAlign = TextAlign.Center
        )

        Text(
            text = "One UI automatically puts background background protection services to sleep unless battery optimization is set to Unrestricted.\n\nPath:\n${BatteryHelper.SAMSUNG_GUIDANCE_TEXT}",
            style = OneUiTheme.typography.body,
            color = OneUiTheme.colors.onSurfaceMuted,
            textAlign = TextAlign.Center
        )

        OneUiCard(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onOpenSettings)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Battery Optimization",
                        style = OneUiTheme.typography.body.copy(fontWeight = FontWeight.SemiBold),
                        color = OneUiTheme.colors.onSurface
                    )
                    Text(
                        text = if (isIgnored) "Battery is set to Unrestricted" else "Tap to adjust battery settings",
                        style = OneUiTheme.typography.caption,
                        color = OneUiTheme.colors.onSurfaceMuted
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isIgnored) OneUiTheme.colors.surfaceVariant else OneUiTheme.colors.accent)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = if (isIgnored) "Ready" else "Configure",
                        style = OneUiTheme.typography.label.copy(fontWeight = FontWeight.Bold),
                        color = if (isIgnored) OneUiTheme.colors.onSurface else OneUiTheme.colors.background
                    )
                }
            }
        }
    }
}
