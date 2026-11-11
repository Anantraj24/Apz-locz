package com.apzlocz.app.service

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import com.apzlocz.app.ApzLoczApp
import com.apzlocz.app.core.datastore.RelockMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Service to intercept and hide incoming notification content for locked applications
 * per docs/02_SRS.md §5 and FR-06.
 *
 * Adheres strictly to security rules: NEVER stores or logs notification titles,
 * text, or content. Only inspects the originating package name.
 */
class NotificationGuardService : NotificationListenerService() {

    companion object {
        private const val TAG = "NotificationGuard"

        private val _isConnected = MutableStateFlow(false)
        val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()

        /**
         * Checks whether notification listener permission is granted for this app.
         */
        fun isNotificationAccessGranted(context: Context): Boolean {
            val enabledPackages = NotificationManagerCompat.getEnabledListenerPackages(context)
            return enabledPackages.contains(context.packageName)
        }

        /**
         * Returns an Intent to open the system Notification Access settings screen.
         */
        fun createSettingsIntent(): Intent {
            return Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        }
    }

    private val serviceScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private var lockedPackages: Set<String> = emptySet()
    private var hideNotificationsEnabled: Boolean = true
    private var currentRelockMode: RelockMode = RelockMode.IMMEDIATELY
    private var lockStateManager: LockStateManager? = null

    override fun onListenerConnected() {
        super.onListenerConnected()
        _isConnected.value = true
        Log.d(TAG, "NotificationGuardService connected.")

        val app = application as? ApzLoczApp
        val container = app?.container
        if (container != null) {
            lockStateManager = container.lockStateManager
            val settingsRepo = container.settingsRepository

            serviceScope.launch {
                settingsRepo.settingsFlow.collect { settings ->
                    lockedPackages = settings.lockedPackages
                    hideNotificationsEnabled = settings.hideNotifications
                    currentRelockMode = settings.relockMode
                }
            }
        }
    }

    override fun onListenerDisconnected() {
        _isConnected.value = false
        Log.d(TAG, "NotificationGuardService disconnected.")
        super.onListenerDisconnected()
    }

    override fun onDestroy() {
        serviceScope.cancel()
        _isConnected.value = false
        super.onDestroy()
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        if (sbn == null) return

        val pkgName = sbn.packageName
        if (pkgName.isNullOrBlank() || pkgName == packageName) return

        val isLocked = lockedPackages.contains(pkgName)
        val isAppLocked = lockStateManager?.isAppLocked(pkgName, currentRelockMode) ?: true

        val shouldHide = shouldHideNotification(
            packageName = pkgName,
            isLockedApp = isLocked,
            hideEnabled = hideNotificationsEnabled,
            isCurrentlyLocked = isAppLocked
        )

        if (shouldHide) {
            try {
                // Cancel notification to hide sensitive message contents from status bar / shade
                cancelNotification(sbn.key)
                Log.d(TAG, "Hidden notification from locked app: $pkgName")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to cancel notification for $pkgName: ${e.message}")
            }
        }
    }
}

/**
 * Pure decision logic for whether a notification from a given package should be hidden.
 * Extracted as a standalone pure function for deterministic JVM unit testing.
 */
fun shouldHideNotification(
    packageName: String,
    isLockedApp: Boolean,
    hideEnabled: Boolean,
    isCurrentlyLocked: Boolean
): Boolean {
    if (!hideEnabled) return false
    if (!isLockedApp) return false
    if (!isCurrentlyLocked) return false
    return true
}
