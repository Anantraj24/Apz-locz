package com.apzlocz.app.service

import android.os.SystemClock
import com.apzlocz.app.core.datastore.RelockMode
import java.util.concurrent.ConcurrentHashMap

interface LockStateManager {
    fun recordUnlock(packageName: String)
    fun isAppLocked(packageName: String, mode: RelockMode): Boolean
    fun onPackageExited(packageName: String, mode: RelockMode)
    fun onScreenOff()
    fun clearAll()
}

/**
 * Manages runtime, in-memory unlock state for applications.
 * Implements docs/02_SRS.md §4 and FR-04, FR-14.
 *
 * Never persisted to disk. Relies strictly on event triggers (window change, screen off)
 * without Handler, Timer, delay loops, or alarms.
 */
class DefaultLockStateManager(
    private val timeProvider: () -> Long = { SystemClock.elapsedRealtime() }
) : LockStateManager {

    // In-memory mapping: packageName -> elapsedRealtime (ms) when unlocked
    private val unlockTimestampMap = ConcurrentHashMap<String, Long>()

    override fun recordUnlock(packageName: String) {
        unlockTimestampMap[packageName] = timeProvider()
    }

    override fun isAppLocked(packageName: String, mode: RelockMode): Boolean {
        val lastUnlock = unlockTimestampMap[packageName] ?: return true
        val now = timeProvider()

        return when (mode) {
            RelockMode.IMMEDIATELY -> {
                // Returns false while app is foregrounded; cleared upon exit
                false
            }
            RelockMode.AFTER_10S -> {
                val expired = (now - lastUnlock) > 10_000L
                if (expired) {
                    unlockTimestampMap.remove(packageName)
                    true
                } else {
                    false
                }
            }
            RelockMode.AFTER_30S -> {
                val expired = (now - lastUnlock) > 30_000L
                if (expired) {
                    unlockTimestampMap.remove(packageName)
                    true
                } else {
                    false
                }
            }
            RelockMode.AFTER_1MIN -> {
                val expired = (now - lastUnlock) > 60_000L
                if (expired) {
                    unlockTimestampMap.remove(packageName)
                    true
                } else {
                    false
                }
            }
            RelockMode.ON_PHONE_LOCK -> {
                // Stays unlocked until ScreenOffReceiver triggers onScreenOff()
                false
            }
        }
    }

    override fun onPackageExited(packageName: String, mode: RelockMode) {
        if (mode == RelockMode.IMMEDIATELY) {
            unlockTimestampMap.remove(packageName)
        }
    }

    override fun onScreenOff() {
        unlockTimestampMap.clear()
    }

    override fun clearAll() {
        unlockTimestampMap.clear()
    }
}
