package com.apzlocz.app.service

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import com.apzlocz.app.ApzLoczApp
import com.apzlocz.app.BuildConfig
import com.apzlocz.app.core.datastore.RelockMode
import com.apzlocz.app.lock.LockActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Event-driven AccessibilityService for detecting foreground package transitions.
 * Adheres strictly to docs/05_SYSTEM_ARCHITECTURE.md and docs/09 Phases 3, 4, 5, 6, 7.
 *
 * Runs without polling, without background loops, and requests no capabilities beyond
 * window state observation. Dynamically listens to ACTION_SCREEN_OFF for relock triggers.
 */
class AppWatcherService : AccessibilityService() {

    companion object {
        private const val TAG = "AppWatcherService"

        private val _isConnected = MutableStateFlow(false)
        val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()

        private val _lastDetectedPackage = MutableStateFlow<String?>(null)
        val lastDetectedPackage: StateFlow<String?> = _lastDetectedPackage.asStateFlow()
    }

    private lateinit var appFilter: ForegroundAppFilter
    private val serviceScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    private var lockedPackages: Set<String> = emptySet()
    private var currentRelockMode: RelockMode = RelockMode.IMMEDIATELY
    private var lockStateManager: LockStateManager? = null
    private var screenOffReceiver: ScreenOffReceiver? = null
    private var previousForegroundPackage: String? = null

    override fun onServiceConnected() {
        super.onServiceConnected()
        _isConnected.value = true

        val launcherPackages = queryLauncherPackages()
        val imePackages = queryImePackages()

        appFilter = ForegroundAppFilter(
            ownPackageName = packageName,
            launcherPackages = launcherPackages,
            imePackages = imePackages
        )

        val app = application as? ApzLoczApp
        val container = app?.container
        if (container != null) {
            lockStateManager = container.lockStateManager
            val settingsRepo = container.settingsRepository

            // Dynamically listen to settings updates (locked apps & relock timing)
            serviceScope.launch {
                settingsRepo.settingsFlow.collect { settings ->
                    lockedPackages = settings.lockedPackages
                    currentRelockMode = settings.relockMode
                    if (BuildConfig.DEBUG) {
                        Log.d(TAG, "Settings updated: ${lockedPackages.size} locked apps, relockMode=$currentRelockMode")
                    }
                }
            }
        }

        // Register dynamic receiver for Intent.ACTION_SCREEN_OFF (clears unlock cache on phone lock)
        val receiver = ScreenOffReceiver {
            lockStateManager?.onScreenOff()
        }
        screenOffReceiver = receiver
        val filter = IntentFilter(Intent.ACTION_SCREEN_OFF)
        registerReceiver(receiver, filter)

        if (BuildConfig.DEBUG) {
            Log.d(TAG, "AppWatcherService connected and ScreenOffReceiver registered.")
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null || event.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            return
        }

        val pkgSequence = event.packageName ?: return
        val currentPackage = pkgSequence.toString()

        if (!::appFilter.isInitialized) {
            appFilter = ForegroundAppFilter(ownPackageName = packageName)
        }

        val prev = previousForegroundPackage
        if (prev != null && appFilter.isAppExit(prev, currentPackage)) {
            lockStateManager?.onPackageExited(prev, currentRelockMode)
        }
        if (!appFilter.isTransientOverlay(currentPackage)) {
            previousForegroundPackage = currentPackage
        }

        if (appFilter.shouldIgnore(currentPackage)) {
            return
        }

        // Only react if the detected application is in the user's locked set
        if (!lockedPackages.contains(currentPackage)) {
            return
        }

        // Check if the application is still within its relock grace period
        val manager = lockStateManager
        if (manager != null && !manager.isAppLocked(currentPackage, currentRelockMode)) {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "App $currentPackage is within relock grace period ($currentRelockMode). Skipping lock screen.")
            }
            return
        }

        val now = System.currentTimeMillis()
        if (appFilter.shouldDebounce(currentPackage, now)) {
            return
        }

        _lastDetectedPackage.value = currentPackage
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "Locked app detected in foreground: $currentPackage at $now. Launching LockActivity.")
        }

        try {
            val lockIntent = Intent(this, LockActivity::class.java).apply {
                putExtra(LockActivity.EXTRA_PACKAGE_NAME, currentPackage)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            }
            startActivity(lockIntent)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to launch LockActivity: ${e.message}", e)
        }
    }

    override fun onInterrupt() {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "AppWatcherService interrupted.")
        }
    }

    override fun onUnbind(intent: Intent?): Boolean {
        _isConnected.value = false
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        _isConnected.value = false
        serviceScope.cancel()
        screenOffReceiver?.let {
            try {
                unregisterReceiver(it)
            } catch (e: Exception) {
                Log.w(TAG, "Error unregistering ScreenOffReceiver: ${e.message}")
            }
        }
        super.onDestroy()
    }

    private fun queryLauncherPackages(): Set<String> {
        val launcherIntent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_HOME)
        }
        val resolveInfoList = packageManager.queryIntentActivities(
            launcherIntent,
            PackageManager.MATCH_DEFAULT_ONLY
        )
        return resolveInfoList.mapNotNull { it.activityInfo?.packageName }.toSet()
    }

    private fun queryImePackages(): Set<String> {
        val imeIntent = Intent("android.view.InputMethod")
        val resolveInfoList = packageManager.queryIntentServices(imeIntent, 0)
        return resolveInfoList.mapNotNull { it.serviceInfo?.packageName }.toSet()
    }
}
