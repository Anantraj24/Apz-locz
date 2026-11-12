package com.apzlocz.app.lock

import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.fragment.app.FragmentActivity
import com.apzlocz.app.ApzLoczApp
import com.apzlocz.app.core.datastore.CredentialType
import com.apzlocz.app.core.security.LockoutState
import com.apzlocz.app.ui.theme.ApzLoczTheme
import com.apzlocz.app.ui.theme.ThemeMode
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LockActivity : FragmentActivity() {

    companion object {
        const val EXTRA_PACKAGE_NAME = "extra_target_package"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val targetPackage = intent.getStringExtra(EXTRA_PACKAGE_NAME) ?: run {
            finish()
            return
        }

        // Intercept back button: navigate to Home Screen so the user cannot bypass the lock
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val homeIntent = Intent(Intent.ACTION_MAIN).apply {
                    addCategory(Intent.CATEGORY_HOME)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                startActivity(homeIntent)
                finish()
            }
        })

        val app = application as ApzLoczApp
        val container = app.container
        val credentialManager = container.credentialManager
        val lockoutManager = container.lockoutManager
        val settingsRepo = container.settingsRepository
        val iconCache = container.iconCache
        val lockStateManager = container.lockStateManager

        val targetAppName = try {
            val appInfo = packageManager.getApplicationInfo(targetPackage, 0)
            packageManager.getApplicationLabel(appInfo).toString()
        } catch (e: Exception) {
            targetPackage
        }

        setContent {
            val settings by settingsRepo.settingsFlow.collectAsState(initial = null)
            val themeMode = settings?.themeMode ?: ThemeMode.SYSTEM
            val biometricEnabled = settings?.biometricEnabled ?: false
            val credentialType by credentialManager.credentialTypeFlow.collectAsState(initial = CredentialType.NONE)

            var lockoutState by remember {
                mutableStateOf(LockoutState(isLockedOut = false, remainingSeconds = 0, failedAttempts = 0))
            }
            var isError by remember { mutableStateOf(false) }
            val scope = rememberCoroutineScope()

            val isBiometricAvailable = remember {
                BiometricHelper.isBiometricAvailable(this@LockActivity)
            }

            fun triggerBiometrics() {
                if (isBiometricAvailable && !lockoutState.isLockedOut) {
                    BiometricHelper.showBiometricPrompt(
                        activity = this@LockActivity,
                        title = "Unlock $targetAppName",
                        subtitle = "Confirm fingerprint or face",
                        negativeButtonText = "Use credential",
                        onSuccess = {
                            scope.launch {
                                lockStateManager.recordUnlock(targetPackage)
                                lockoutManager.recordSuccess()
                                finish()
                            }
                        },
                        onError = { _, _ ->
                            // Fallback to manual credential entry without lockout
                        },
                        onFailed = {
                            // Biometric failed once; user can retry or enter credential
                        }
                    )
                }
            }

            // Auto-prompt biometrics once on launch if enabled and not locked out
            LaunchedEffect(biometricEnabled) {
                if (biometricEnabled && isBiometricAvailable) {
                    val currentLockout = lockoutManager.getLockoutState()
                    if (!currentLockout.isLockedOut) {
                        triggerBiometrics()
                    }
                }
            }

            // Lockout countdown timer
            LaunchedEffect(Unit) {
                while (true) {
                    val state = lockoutManager.getLockoutState()
                    lockoutState = state
                    if (state.isLockedOut) {
                        delay(1000L)
                    } else {
                        delay(3000L)
                    }
                }
            }

            ApzLoczTheme(themeMode = themeMode) {
                LockScreen(
                    packageName = targetPackage,
                    appName = targetAppName,
                    credentialType = credentialType,
                    pinLength = settings?.pinLength ?: 4,
                    lockoutState = lockoutState,
                    isError = isError,
                    isBiometricAvailable = isBiometricAvailable && biometricEnabled,
                    iconCache = iconCache,
                    onCredentialSubmit = { secretInput ->
                        scope.launch {
                            isError = false
                            val isCorrect = credentialManager.verifyCredential(secretInput)
                            if (isCorrect) {
                                lockStateManager.recordUnlock(targetPackage)
                                lockoutManager.recordSuccess()
                                finish()
                            } else {
                                val updatedLockout = lockoutManager.recordFailedAttempt()
                                lockoutState = updatedLockout
                                isError = true
                            }
                        }
                    },
                    onBiometricTrigger = {
                        triggerBiometrics()
                    }
                )
            }
        }
    }
}
