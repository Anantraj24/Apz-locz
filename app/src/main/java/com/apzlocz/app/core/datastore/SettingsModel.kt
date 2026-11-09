package com.apzlocz.app.core.datastore

import com.apzlocz.app.ui.theme.ThemeMode

enum class RelockMode {
    IMMEDIATELY,
    AFTER_10S,
    AFTER_30S,
    AFTER_1MIN,
    ON_PHONE_LOCK
}

enum class CredentialType {
    NONE,
    PIN,
    PASSWORD,
    PATTERN
}

/**
 * Immutable snapshot of application settings stored in DataStore.
 * Matches docs/06_DATABASE_DESIGN.md specifications.
 */
data class AppSettings(
    val lockedPackages: Set<String> = emptySet(),
    val relockMode: RelockMode = RelockMode.IMMEDIATELY,
    val biometricEnabled: Boolean = false,
    val hideNotifications: Boolean = true,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val showSystemApps: Boolean = false,
    val credentialType: CredentialType = CredentialType.NONE,
    val credentialSaltBase64: String? = null,
    val credentialHashEncBase64: String? = null,
    val credentialIvBase64: String? = null,
    val failedAttempts: Int = 0,
    val lockoutUntil: Long = 0L,
    val onboardingDone: Boolean = false,
    val schemaVersion: Int = 1
)
