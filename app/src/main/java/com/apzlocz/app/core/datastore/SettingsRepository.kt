package com.apzlocz.app.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import com.apzlocz.app.ui.theme.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

interface SettingsRepository {
    val settingsFlow: Flow<AppSettings>
    val lockedPackagesFlow: Flow<Set<String>>

    suspend fun setLockedPackages(packages: Set<String>)
    suspend fun togglePackageLocked(packageName: String): Boolean
    suspend fun setRelockMode(mode: RelockMode)
    suspend fun setBiometricEnabled(enabled: Boolean)
    suspend fun setHideNotifications(hide: Boolean)
    suspend fun setThemeMode(mode: ThemeMode)
    suspend fun setShowSystemApps(show: Boolean)
    suspend fun setOnboardingDone(done: Boolean)
    suspend fun updateCredentialData(
        type: CredentialType,
        saltBase64: String?,
        hashEncBase64: String?,
        ivBase64: String?
    )
    suspend fun clearCredential()
    suspend fun updateLockoutState(failedAttempts: Int, lockoutUntil: Long)
    suspend fun resetLockoutState()
}

class DataStoreSettingsRepository(
    private val dataStore: DataStore<Preferences>
) : SettingsRepository {

    override val settingsFlow: Flow<AppSettings> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            mapPreferencesToSettings(preferences)
        }

    override val lockedPackagesFlow: Flow<Set<String>> = settingsFlow.map { it.lockedPackages }

    override suspend fun setLockedPackages(packages: Set<String>) {
        dataStore.edit { preferences ->
            preferences[SettingsKeys.LOCKED_PACKAGES] = packages
        }
    }

    override suspend fun togglePackageLocked(packageName: String): Boolean {
        var isNowLocked = false
        dataStore.edit { preferences ->
            val current = preferences[SettingsKeys.LOCKED_PACKAGES] ?: emptySet()
            val updated = if (current.contains(packageName)) {
                isNowLocked = false
                current - packageName
            } else {
                isNowLocked = true
                current + packageName
            }
            preferences[SettingsKeys.LOCKED_PACKAGES] = updated
        }
        return isNowLocked
    }

    override suspend fun setRelockMode(mode: RelockMode) {
        dataStore.edit { preferences ->
            preferences[SettingsKeys.RELOCK_MODE] = mode.name
        }
    }

    override suspend fun setBiometricEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[SettingsKeys.BIOMETRIC_ENABLED] = enabled
        }
    }

    override suspend fun setHideNotifications(hide: Boolean) {
        dataStore.edit { preferences ->
            preferences[SettingsKeys.HIDE_NOTIFICATIONS] = hide
        }
    }

    override suspend fun setThemeMode(mode: ThemeMode) {
        dataStore.edit { preferences ->
            preferences[SettingsKeys.THEME_MODE] = mode.name
        }
    }

    override suspend fun setShowSystemApps(show: Boolean) {
        dataStore.edit { preferences ->
            preferences[SettingsKeys.SHOW_SYSTEM_APPS] = show
        }
    }

    override suspend fun setOnboardingDone(done: Boolean) {
        dataStore.edit { preferences ->
            preferences[SettingsKeys.ONBOARDING_DONE] = done
        }
    }

    override suspend fun updateCredentialData(
        type: CredentialType,
        saltBase64: String?,
        hashEncBase64: String?,
        ivBase64: String?
    ) {
        dataStore.edit { preferences ->
            preferences[SettingsKeys.CREDENTIAL_TYPE] = type.name
            if (saltBase64 != null) preferences[SettingsKeys.CREDENTIAL_SALT] = saltBase64
            else preferences.remove(SettingsKeys.CREDENTIAL_SALT)

            if (hashEncBase64 != null) preferences[SettingsKeys.CREDENTIAL_HASH_ENC] = hashEncBase64
            else preferences.remove(SettingsKeys.CREDENTIAL_HASH_ENC)

            if (ivBase64 != null) preferences[SettingsKeys.CREDENTIAL_IV] = ivBase64
            else preferences.remove(SettingsKeys.CREDENTIAL_IV)
        }
    }

    override suspend fun clearCredential() {
        dataStore.edit { preferences ->
            preferences[SettingsKeys.CREDENTIAL_TYPE] = CredentialType.NONE.name
            preferences.remove(SettingsKeys.CREDENTIAL_SALT)
            preferences.remove(SettingsKeys.CREDENTIAL_HASH_ENC)
            preferences.remove(SettingsKeys.CREDENTIAL_IV)
            preferences[SettingsKeys.FAILED_ATTEMPTS] = 0
            preferences[SettingsKeys.LOCKOUT_UNTIL] = 0L
        }
    }

    override suspend fun updateLockoutState(failedAttempts: Int, lockoutUntil: Long) {
        dataStore.edit { preferences ->
            preferences[SettingsKeys.FAILED_ATTEMPTS] = failedAttempts
            preferences[SettingsKeys.LOCKOUT_UNTIL] = lockoutUntil
        }
    }

    override suspend fun resetLockoutState() {
        updateLockoutState(failedAttempts = 0, lockoutUntil = 0L)
    }

    private fun mapPreferencesToSettings(preferences: Preferences): AppSettings {
        val relockModeStr = preferences[SettingsKeys.RELOCK_MODE]
        val relockMode = try {
            relockModeStr?.let { RelockMode.valueOf(it) } ?: RelockMode.IMMEDIATELY
        } catch (_: IllegalArgumentException) {
            RelockMode.IMMEDIATELY
        }

        val themeModeStr = preferences[SettingsKeys.THEME_MODE]
        val themeMode = try {
            themeModeStr?.let { ThemeMode.valueOf(it) } ?: ThemeMode.SYSTEM
        } catch (_: IllegalArgumentException) {
            ThemeMode.SYSTEM
        }

        val credentialTypeStr = preferences[SettingsKeys.CREDENTIAL_TYPE]
        val credentialType = try {
            credentialTypeStr?.let { CredentialType.valueOf(it) } ?: CredentialType.NONE
        } catch (_: IllegalArgumentException) {
            CredentialType.NONE
        }

        return AppSettings(
            lockedPackages = preferences[SettingsKeys.LOCKED_PACKAGES] ?: emptySet(),
            relockMode = relockMode,
            biometricEnabled = preferences[SettingsKeys.BIOMETRIC_ENABLED] ?: false,
            hideNotifications = preferences[SettingsKeys.HIDE_NOTIFICATIONS] ?: true,
            themeMode = themeMode,
            showSystemApps = preferences[SettingsKeys.SHOW_SYSTEM_APPS] ?: false,
            credentialType = credentialType,
            credentialSaltBase64 = preferences[SettingsKeys.CREDENTIAL_SALT],
            credentialHashEncBase64 = preferences[SettingsKeys.CREDENTIAL_HASH_ENC],
            credentialIvBase64 = preferences[SettingsKeys.CREDENTIAL_IV],
            failedAttempts = preferences[SettingsKeys.FAILED_ATTEMPTS] ?: 0,
            lockoutUntil = preferences[SettingsKeys.LOCKOUT_UNTIL] ?: 0L,
            onboardingDone = preferences[SettingsKeys.ONBOARDING_DONE] ?: false,
            schemaVersion = preferences[SettingsKeys.SCHEMA_VERSION] ?: 1
        )
    }
}

