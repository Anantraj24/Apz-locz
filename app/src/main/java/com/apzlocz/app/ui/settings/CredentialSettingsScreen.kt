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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.apzlocz.app.admin.DeviceAdminHelper
import com.apzlocz.app.core.datastore.CredentialType
import com.apzlocz.app.core.security.CredentialManager
import com.apzlocz.app.lock.PasswordInput
import com.apzlocz.app.lock.PatternInput
import com.apzlocz.app.lock.PinInput
import com.apzlocz.app.ui.components.CollapsingHeaderScaffold
import com.apzlocz.app.ui.components.OneUiCard
import com.apzlocz.app.ui.components.PillButton
import com.apzlocz.app.ui.components.PillButtonStyle
import com.apzlocz.app.ui.theme.OneUiTheme
import kotlinx.coroutines.launch

private enum class SetupStep {
    VERIFY_CURRENT,
    CHOOSE_TYPE,
    ENTER_NEW,
    CONFIRM_NEW
}

@Composable
fun CredentialSettingsScreen(
    credentialManager: CredentialManager,
    isCredentialCurrentlySet: Boolean,
    currentCredentialType: CredentialType,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var step by remember {
        mutableStateOf(if (isCredentialCurrentlySet) SetupStep.VERIFY_CURRENT else SetupStep.CHOOSE_TYPE)
    }
    var selectedType by remember { mutableStateOf(CredentialType.PIN) }
    var currentSecretInput by remember { mutableStateOf("") }
    var newSecretInput by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()

    CollapsingHeaderScaffold(
        title = if (isCredentialCurrentlySet) "Change Credential" else "Set Credential",
        subtitle = "PIN, Password, or Pattern lock",
        modifier = modifier
    ) {
        item(key = "credential_wizard_content") {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                if (errorMessage != null) {
                    OneUiCard(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = errorMessage ?: "",
                            style = OneUiTheme.typography.body.copy(fontWeight = FontWeight.SemiBold),
                            color = OneUiTheme.colors.error,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }

                if (successMessage != null) {
                    OneUiCard(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = successMessage ?: "",
                                style = OneUiTheme.typography.body.copy(fontWeight = FontWeight.Bold),
                                color = OneUiTheme.colors.success
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            PillButton(
                                text = "Done",
                                style = PillButtonStyle.PRIMARY,
                                onClick = onNavigateBack
                            )
                        }
                    }
                } else {
                    when (step) {
                        SetupStep.VERIFY_CURRENT -> {
                            Text(
                                text = "Enter your existing credential to proceed",
                                style = OneUiTheme.typography.body,
                                color = OneUiTheme.colors.onSurfaceMuted,
                                textAlign = TextAlign.Center
                            )

                            when (currentCredentialType) {
                                CredentialType.PIN -> {
                                    PinInput(
                                        pinLength = 4,
                                        isError = isError,
                                        onPinComplete = { input ->
                                            scope.launch {
                                                isError = false
                                                errorMessage = null
                                                if (credentialManager.verifyCredential(input)) {
                                                    currentSecretInput = input
                                                    step = SetupStep.CHOOSE_TYPE
                                                } else {
                                                    isError = true
                                                    errorMessage = "Incorrect current credential."
                                                }
                                            }
                                        }
                                    )
                                }
                                CredentialType.PASSWORD -> {
                                    PasswordInput(
                                        isError = isError,
                                        onPasswordSubmit = { input ->
                                            scope.launch {
                                                isError = false
                                                errorMessage = null
                                                if (credentialManager.verifyCredential(input)) {
                                                    currentSecretInput = input
                                                    step = SetupStep.CHOOSE_TYPE
                                                } else {
                                                    isError = true
                                                    errorMessage = "Incorrect current credential."
                                                }
                                            }
                                        }
                                    )
                                }
                                CredentialType.PATTERN -> {
                                    PatternInput(
                                        isError = isError,
                                        onPatternComplete = { input ->
                                            scope.launch {
                                                isError = false
                                                errorMessage = null
                                                if (credentialManager.verifyCredential(input)) {
                                                    currentSecretInput = input
                                                    step = SetupStep.CHOOSE_TYPE
                                                } else {
                                                    isError = true
                                                    errorMessage = "Incorrect current credential."
                                                }
                                            }
                                        }
                                    )
                                }
                                CredentialType.NONE -> {
                                    step = SetupStep.CHOOSE_TYPE
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Remove credential option
                            val context = LocalContext.current
                            PillButton(
                                text = "Remove Lock Credential",
                                style = PillButtonStyle.OUTLINED,
                                onClick = {
                                    scope.launch {
                                        val result = credentialManager.removeCredential(currentSecretInput)
                                        if (result.isSuccess) {
                                            DeviceAdminHelper.deactivateDeviceAdmin(context)
                                            successMessage = "Credential removed and uninstall protection deactivated."
                                        } else {
                                            errorMessage = "Failed to remove credential: ${result.exceptionOrNull()?.message}"
                                        }
                                    }
                                }
                            )
                        }

                        SetupStep.CHOOSE_TYPE -> {
                            Text(
                                text = "Select lock type",
                                style = OneUiTheme.typography.title,
                                color = OneUiTheme.colors.onSurface
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                CredentialTypePill(
                                    label = "PIN",
                                    isSelected = selectedType == CredentialType.PIN,
                                    onClick = { selectedType = CredentialType.PIN },
                                    modifier = Modifier.weight(1f)
                                )
                                CredentialTypePill(
                                    label = "Password",
                                    isSelected = selectedType == CredentialType.PASSWORD,
                                    onClick = { selectedType = CredentialType.PASSWORD },
                                    modifier = Modifier.weight(1f)
                                )
                                CredentialTypePill(
                                    label = "Pattern",
                                    isSelected = selectedType == CredentialType.PATTERN,
                                    onClick = { selectedType = CredentialType.PATTERN },
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            PillButton(
                                text = "Continue",
                                style = PillButtonStyle.PRIMARY,
                                onClick = {
                                    isError = false
                                    errorMessage = null
                                    step = SetupStep.ENTER_NEW
                                }
                            )
                        }

                        SetupStep.ENTER_NEW -> {
                            Text(
                                text = "Enter new $selectedType",
                                style = OneUiTheme.typography.title,
                                color = OneUiTheme.colors.onSurface
                            )

                            when (selectedType) {
                                CredentialType.PIN -> {
                                    PinInput(
                                        pinLength = 4,
                                        isError = isError,
                                        onPinComplete = { input ->
                                            newSecretInput = input
                                            step = SetupStep.CONFIRM_NEW
                                        }
                                    )
                                }
                                CredentialType.PASSWORD -> {
                                    PasswordInput(
                                        isError = isError,
                                        onPasswordSubmit = { input ->
                                            if (input.length < 4) {
                                                errorMessage = "Password must be at least 4 characters."
                                                isError = true
                                            } else {
                                                newSecretInput = input
                                                step = SetupStep.CONFIRM_NEW
                                            }
                                        }
                                    )
                                }
                                CredentialType.PATTERN -> {
                                    PatternInput(
                                        isError = isError,
                                        onPatternComplete = { input ->
                                            newSecretInput = input
                                            step = SetupStep.CONFIRM_NEW
                                        }
                                    )
                                }
                                CredentialType.NONE -> Unit
                            }
                        }

                        SetupStep.CONFIRM_NEW -> {
                            Text(
                                text = "Confirm your $selectedType",
                                style = OneUiTheme.typography.title,
                                color = OneUiTheme.colors.onSurface
                            )

                            when (selectedType) {
                                CredentialType.PIN -> {
                                    PinInput(
                                        pinLength = 4,
                                        isError = isError,
                                        onPinComplete = { input ->
                                            if (input == newSecretInput) {
                                                saveNewCredential(scope, credentialManager, isCredentialCurrentlySet, currentSecretInput, selectedType, input) {
                                                    successMessage = "New credential saved successfully!"
                                                }
                                            } else {
                                                isError = true
                                                errorMessage = "PINs do not match. Try again."
                                            }
                                        }
                                    )
                                }
                                CredentialType.PASSWORD -> {
                                    PasswordInput(
                                        isError = isError,
                                        onPasswordSubmit = { input ->
                                            if (input == newSecretInput) {
                                                saveNewCredential(scope, credentialManager, isCredentialCurrentlySet, currentSecretInput, selectedType, input) {
                                                    successMessage = "New password saved successfully!"
                                                }
                                            } else {
                                                isError = true
                                                errorMessage = "Passwords do not match."
                                            }
                                        }
                                    )
                                }
                                CredentialType.PATTERN -> {
                                    PatternInput(
                                        isError = isError,
                                        onPatternComplete = { input ->
                                            if (input == newSecretInput) {
                                                saveNewCredential(scope, credentialManager, isCredentialCurrentlySet, currentSecretInput, selectedType, input) {
                                                    successMessage = "New pattern saved successfully!"
                                                }
                                            } else {
                                                isError = true
                                                errorMessage = "Patterns do not match. Try again."
                                            }
                                        }
                                    )
                                }
                                CredentialType.NONE -> Unit
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                PillButton(
                    text = "Back",
                    style = PillButtonStyle.TONAL,
                    onClick = {
                        if (step == SetupStep.CONFIRM_NEW) {
                            step = SetupStep.ENTER_NEW
                        } else if (step == SetupStep.ENTER_NEW) {
                            step = SetupStep.CHOOSE_TYPE
                        } else {
                            onNavigateBack()
                        }
                    }
                )
            }
        }
    }
}

private fun saveNewCredential(
    scope: kotlinx.coroutines.CoroutineScope,
    credentialManager: CredentialManager,
    isAlreadySet: Boolean,
    currentSecret: String,
    newType: CredentialType,
    newSecret: String,
    onSuccess: () -> Unit
) {
    scope.launch {
        val result = if (isAlreadySet && currentSecret.isNotEmpty()) {
            credentialManager.changeCredential(currentSecret, newType, newSecret)
        } else {
            credentialManager.setCredential(newType, newSecret)
        }
        if (result.isSuccess) {
            onSuccess()
        }
    }
}

@Composable
private fun CredentialTypePill(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(48.dp)
            .clip(RoundedCornerShape(24.dp))
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

