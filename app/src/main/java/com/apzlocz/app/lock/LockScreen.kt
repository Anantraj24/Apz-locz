package com.apzlocz.app.lock

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.apzlocz.app.core.apps.IconCache
import com.apzlocz.app.core.datastore.CredentialType
import com.apzlocz.app.core.security.LockoutState
import com.apzlocz.app.ui.apps.AppIconView
import com.apzlocz.app.ui.components.PillButton
import com.apzlocz.app.ui.components.PillButtonStyle
import com.apzlocz.app.ui.theme.OneUiTheme

@Composable
fun LockScreen(
    packageName: String,
    appName: String,
    credentialType: CredentialType,
    lockoutState: LockoutState,
    isError: Boolean,
    isBiometricAvailable: Boolean,
    iconCache: IconCache,
    onCredentialSubmit: (String) -> Unit,
    onBiometricTrigger: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(OneUiTheme.colors.background)
            .padding(horizontal = 24.dp, vertical = 32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Section: Locked App Identity
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 24.dp)
            ) {
                AppIconView(
                    packageName = packageName,
                    appName = appName,
                    iconCache = iconCache
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = appName,
                    style = OneUiTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    ),
                    color = OneUiTheme.colors.onSurface
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = if (lockoutState.isLockedOut) {
                        "Locked out — Try again in ${lockoutState.remainingSeconds}s"
                    } else if (isError) {
                        "Incorrect credential. Try again."
                    } else {
                        "Locked application"
                    },
                    style = OneUiTheme.typography.body.copy(
                        fontWeight = if (isError || lockoutState.isLockedOut) FontWeight.SemiBold else FontWeight.Normal
                    ),
                    color = if (lockoutState.isLockedOut || isError) OneUiTheme.colors.error else OneUiTheme.colors.onSurfaceMuted,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Middle Section: Active Credential Input
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                val inputEnabled = !lockoutState.isLockedOut

                when (credentialType) {
                    CredentialType.PIN -> {
                        PinInput(
                            pinLength = 4,
                            onPinComplete = onCredentialSubmit,
                            isError = isError,
                            enabled = inputEnabled,
                            onBiometricClick = if (isBiometricAvailable) onBiometricTrigger else null
                        )
                    }
                    CredentialType.PASSWORD -> {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            PasswordInput(
                                onPasswordSubmit = onCredentialSubmit,
                                isError = isError,
                                enabled = inputEnabled
                            )
                            if (isBiometricAvailable && onBiometricTrigger != null) {
                                PillButton(
                                    text = "Unlock with Biometrics",
                                    style = PillButtonStyle.TONAL,
                                    enabled = inputEnabled,
                                    onClick = onBiometricTrigger
                                )
                            }
                        }
                    }
                    CredentialType.PATTERN -> {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            PatternInput(
                                onPatternComplete = onCredentialSubmit,
                                isError = isError,
                                enabled = inputEnabled
                            )
                            if (isBiometricAvailable && onBiometricTrigger != null) {
                                PillButton(
                                    text = "Unlock with Biometrics",
                                    style = PillButtonStyle.TONAL,
                                    enabled = inputEnabled,
                                    onClick = onBiometricTrigger
                                )
                            }
                        }
                    }
                    CredentialType.NONE -> {
                        Text(
                            text = "No lock credential configured.",
                            style = OneUiTheme.typography.body,
                            color = OneUiTheme.colors.onSurfaceMuted
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
