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

