package com.apzlocz.app.service

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Event-driven AccessibilityService for detecting foreground package transitions.
 * Adheres strictly to docs/05_SYSTEM_ARCHITECTURE.md and docs/09 Phase 3.
 *
 * Runs without polling, without background loops, and requests no capabilities beyond
 * window state observation.
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

        Log.d(TAG, "AppWatcherService connected.")
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

        if (appFilter.shouldIgnore(currentPackage)) {
            return
        }

        val now = System.currentTimeMillis()
        if (appFilter.shouldDebounce(currentPackage, now)) {
            return
        }

        _lastDetectedPackage.value = currentPackage
        Log.d(TAG, "Foreground app detected: $currentPackage at $now")
    }

    override fun onInterrupt() {
        Log.d(TAG, "AppWatcherService interrupted.")
    }

    override fun onUnbind(intent: Intent?): Boolean {
        _isConnected.value = false
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        _isConnected.value = false
        super.onDestroy()
    }

    private fun queryLauncherPackages(): Set<String> {
        val launcherIntent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_HOME)
        }
        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PackageManager.MATCH_DEFAULT_ONLY
        } else {
            0
        }
        val resolveInfoList = packageManager.queryIntentActivities(launcherIntent, flags)
        return resolveInfoList.mapNotNull { it.activityInfo?.packageName }.toSet()
    }

    private fun queryImePackages(): Set<String> {
        val imeIntent = Intent("android.view.InputMethod")
        val resolveInfoList = packageManager.queryIntentServices(imeIntent, 0)
        return resolveInfoList.mapNotNull { it.serviceInfo?.packageName }.toSet()
    }
}
