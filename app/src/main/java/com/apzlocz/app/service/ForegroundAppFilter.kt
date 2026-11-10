package com.apzlocz.app.service

/**
 * Filters out system UI, own package, keyboards, launchers, and debounces rapid
 * duplicate window-state-change events per docs/02_SRS.md §2 and docs/09.
 */
class ForegroundAppFilter(
    private val ownPackageName: String,
    private val launcherPackages: Set<String> = emptySet(),
    private val imePackages: Set<String> = emptySet(),
    private val debounceWindowMs: Long = 200L
) {
    private var lastPackageName: String? = null
    private var lastEventTimeMs: Long = 0L

    companion object {
        val SYSTEM_UI_PACKAGES = setOf(
            "com.android.systemui",
            "com.samsung.android.app.cocktailbarservice", // Samsung edge panel
            "com.sec.android.app.launcher",             // Samsung One UI Home
            "com.google.android.apps.nexuslauncher",     // Pixel Launcher
            "com.android.launcher3"                      // AOSP Launcher
        )

        private val OVERLAY_SYSTEM_UI = setOf(
            "com.android.systemui",
            "com.samsung.android.app.cocktailbarservice"
        )
    }

    /**
     * Identifies transient overlay surfaces (keyboards, system UI dialogs, own lock activity)
     * that do not represent navigating away from the active application.
     */
    fun isTransientOverlay(packageName: String): Boolean {
        if (packageName == ownPackageName) return true
        if (OVERLAY_SYSTEM_UI.contains(packageName)) return true
        if (imePackages.contains(packageName)) return true
        return false
    }

    /**
     * Determines whether the transition from previousPackage to newPackage
     * represents the user genuinely exiting previousPackage (e.g. going to launcher or switching apps).
     */
    fun isAppExit(previousPackage: String, newPackage: String): Boolean {
        if (previousPackage == newPackage) return false
        if (isTransientOverlay(newPackage)) return false
        return true
    }

    /**
     * Returns true if the package should be ignored from foreground locking checks.
     */
    fun shouldIgnore(packageName: String): Boolean {
        if (packageName.isBlank()) return true
        if (packageName == ownPackageName) return true
        if (SYSTEM_UI_PACKAGES.contains(packageName)) return true
        if (launcherPackages.contains(packageName)) return true
        if (imePackages.contains(packageName)) return true
        return false
    }

    /**
     * Debounces rapid duplicate window-state-change events for the same package.
     * Returns true if the event should be dropped (debounced), false if it should be processed.
     */
    fun shouldDebounce(packageName: String, nowMs: Long = System.currentTimeMillis()): Boolean {
        if (packageName == lastPackageName && (nowMs - lastEventTimeMs) < debounceWindowMs) {
            return true
        }
        lastPackageName = packageName
        lastEventTimeMs = nowMs
        return false
    }

    fun reset() {
        lastPackageName = null
        lastEventTimeMs = 0L
    }
}

