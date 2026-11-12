package com.apzlocz.app

import com.apzlocz.app.core.datastore.AppSettings
import com.apzlocz.app.core.datastore.CredentialType
import com.apzlocz.app.core.datastore.RelockMode
import com.apzlocz.app.core.datastore.SettingsRepository
import com.apzlocz.app.ui.theme.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

/**
 * Fast, thread-safe in-memory SettingsRepository for pure JVM unit tests.
 */
class FakeSettingsRepository(
    initialSettings: AppSettings = AppSettings()
) : SettingsRepository {

    private val _settings = MutableStateFlow(initialSettings)
    override val settingsFlow: Flow<AppSettings> = _settings
    override val lockedPackagesFlow: Flow<Set<String>> = _settings.map { it.lockedPackages }

    override suspend fun setLockedPackages(packages: Set<String>) {
        _settings.update { it.copy(lockedPackages = packages) }
    }

    override suspend fun togglePackageLocked(packageName: String): Boolean {
        var isNowLocked = false
        _settings.update { current ->
            val updated = if (current.lockedPackages.contains(packageName)) {
                isNowLocked = false
                current.lockedPackages - packageName
            } else {
                isNowLocked = true
                current.lockedPackages + packageName
            }
            current.copy(lockedPackages = updated)
        }
        return isNowLocked
    }

    override suspend fun setRelockMode(mode: RelockMode) {
        _settings.update { it.copy(relockMode = mode) }
    }

    override suspend fun setBiometricEnabled(enabled: Boolean) {
        _settings.update { it.copy(biometricEnabled = enabled) }
    }

    override suspend fun setHideNotifications(hide: Boolean) {
        _settings.update { it.copy(hideNotifications = hide) }
    }

    override suspend fun setThemeMode(mode: ThemeMode) {
        _settings.update { it.copy(themeMode = mode) }
    }

    override suspend fun setShowSystemApps(show: Boolean) {
        _settings.update { it.copy(showSystemApps = show) }
    }

    override suspend fun setOnboardingDone(done: Boolean) {
        _settings.update { it.copy(onboardingDone = done) }
    }

    override suspend fun updateCredentialData(
        type: CredentialType,
        saltBase64: String?,
        hashEncBase64: String?,
        ivBase64: String?
    ) {
        _settings.update {
            it.copy(
                credentialType = type,
                credentialSaltBase64 = saltBase64,
                credentialHashEncBase64 = hashEncBase64,
                credentialIvBase64 = ivBase64
            )
        }
    }

    override suspend fun clearCredential() {
        _settings.update {
            it.copy(
                credentialType = CredentialType.NONE,
                credentialSaltBase64 = null,
                credentialHashEncBase64 = null,
                credentialIvBase64 = null,
                failedAttempts = 0,
                lockoutUntil = 0L
            )
        }
    }

    override suspend fun setPinLength(length: Int) {
        _settings.update { it.copy(pinLength = length) }
    }

    override suspend fun updateLockoutState(failedAttempts: Int, lockoutUntil: Long) {
        _settings.update {
            it.copy(failedAttempts = failedAttempts, lockoutUntil = lockoutUntil)
        }
    }

    override suspend fun resetLockoutState() {
        updateLockoutState(failedAttempts = 0, lockoutUntil = 0L)
    }

    fun getCurrentSettings(): AppSettings = _settings.value
}

