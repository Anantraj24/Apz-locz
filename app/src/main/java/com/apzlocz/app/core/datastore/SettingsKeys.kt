package com.apzlocz.app.core.datastore

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey

/**
 * DataStore preferences keys strictly matching docs/06_DATABASE_DESIGN.md.
 */
object SettingsKeys {
    val LOCKED_PACKAGES = stringSetPreferencesKey("locked_packages")
    val RELOCK_MODE = stringPreferencesKey("relock_mode")
    val BIOMETRIC_ENABLED = booleanPreferencesKey("biometric_enabled")
    val HIDE_NOTIFICATIONS = booleanPreferencesKey("hide_notifications")
    val THEME_MODE = stringPreferencesKey("theme_mode")
    val SHOW_SYSTEM_APPS = booleanPreferencesKey("show_system_apps")
    val CREDENTIAL_TYPE = stringPreferencesKey("credential_type")
    val CREDENTIAL_SALT = stringPreferencesKey("credential_salt")
    val CREDENTIAL_HASH_ENC = stringPreferencesKey("credential_hash_enc")
    val CREDENTIAL_IV = stringPreferencesKey("credential_iv")
    val FAILED_ATTEMPTS = intPreferencesKey("failed_attempts")
    val LOCKOUT_UNTIL = longPreferencesKey("lockout_until")
    val PIN_LENGTH = intPreferencesKey("pin_length")
    val ONBOARDING_DONE = booleanPreferencesKey("onboarding_done")
    val SCHEMA_VERSION = intPreferencesKey("schema_version")
}

