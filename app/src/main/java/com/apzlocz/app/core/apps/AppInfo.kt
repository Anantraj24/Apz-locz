package com.apzlocz.app.core.apps

/**
 * Model representing an installed application on the device.
 *
 * @property packageName Unique package identifier (e.g., com.example.app)
 * @property appName Human-readable display label
 * @property isSystemApp Whether the application is installed on the system image
 * @property isLocked Whether the user has chosen to lock this application
 */
data class AppInfo(
    val packageName: String,
    val appName: String,
    val isSystemApp: Boolean,
    val isLocked: Boolean
)
