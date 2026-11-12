package com.apzlocz.app

import com.apzlocz.app.core.datastore.RelockMode
import com.apzlocz.app.service.DefaultLockStateManager
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for DefaultLockStateManager validating all 5 relock modes per docs/02_SRS.md §4.
 * Uses an injected time provider (no real time waiting or Thread.sleep).
 */
class LockStateManagerTest {

    private var currentTime: Long = 100_000L
    private lateinit var lockStateManager: DefaultLockStateManager

    @Before
    fun setUp() {
        currentTime = 100_000L
        lockStateManager = DefaultLockStateManager(timeProvider = { currentTime })
    }

    @Test
    fun testUnrecordedAppIsAlwaysLocked() {
        assertTrue(lockStateManager.isAppLocked("com.whatsapp", RelockMode.IMMEDIATELY))
        assertTrue(lockStateManager.isAppLocked("com.whatsapp", RelockMode.AFTER_10S))
        assertTrue(lockStateManager.isAppLocked("com.whatsapp", RelockMode.AFTER_30S))
        assertTrue(lockStateManager.isAppLocked("com.whatsapp", RelockMode.AFTER_1MIN))
        assertTrue(lockStateManager.isAppLocked("com.whatsapp", RelockMode.ON_PHONE_LOCK))
    }

    @Test
    fun testModeImmediately_StaysUnlockedUntilAppExited() {
        val app = "com.whatsapp"
        lockStateManager.recordUnlock(app)

        // While inside the app, it is unlocked
        assertFalse(lockStateManager.isAppLocked(app, RelockMode.IMMEDIATELY))

        // When the app is exited, it is immediately cleared
        lockStateManager.onPackageExited(app, RelockMode.IMMEDIATELY)

        // Now it requires unlock again
        assertTrue(lockStateManager.isAppLocked(app, RelockMode.IMMEDIATELY))
    }

    @Test
    fun testModeAfter10Seconds() {
        val app = "com.whatsapp"
        lockStateManager.recordUnlock(app)

        // 5 seconds later: still within 10s grace period
        currentTime += 5_000L
        assertFalse(lockStateManager.isAppLocked(app, RelockMode.AFTER_10S))

        // Exiting package in timed mode does not immediately clear grace period
        lockStateManager.onPackageExited(app, RelockMode.AFTER_10S)
        assertFalse(lockStateManager.isAppLocked(app, RelockMode.AFTER_10S))

        // Exactly at 10s: still unlocked (<= 10_000)
        currentTime = 100_000L + 10_000L
        assertFalse(lockStateManager.isAppLocked(app, RelockMode.AFTER_10S))

        // After 10s (e.g. 10,001 ms): expired and relocked
        currentTime = 100_000L + 10_001L
        assertTrue(lockStateManager.isAppLocked(app, RelockMode.AFTER_10S))
    }

    @Test
    fun testModeAfter30Seconds() {
        val app = "com.instagram.android"
        lockStateManager.recordUnlock(app)

        // 25 seconds later: still unlocked
        currentTime += 25_000L
        assertFalse(lockStateManager.isAppLocked(app, RelockMode.AFTER_30S))

        // 30,001 ms later: relocked
        currentTime = 100_000L + 30_001L
        assertTrue(lockStateManager.isAppLocked(app, RelockMode.AFTER_30S))
    }

    @Test
    fun testModeAfter1Minute() {
        val app = "com.google.android.youtube"
        lockStateManager.recordUnlock(app)

        // 45 seconds later: still unlocked
        currentTime += 45_000L
        assertFalse(lockStateManager.isAppLocked(app, RelockMode.AFTER_1MIN))

        // 60,001 ms later: relocked
        currentTime = 100_000L + 60_001L
        assertTrue(lockStateManager.isAppLocked(app, RelockMode.AFTER_1MIN))
    }

    @Test
    fun testModeOnPhoneLock_StaysUnlockedUntilScreenOff() {
        val app = "com.whatsapp"
        lockStateManager.recordUnlock(app)

        // 10 minutes later: still unlocked because phone hasn't locked
        currentTime += 600_000L
        assertFalse(lockStateManager.isAppLocked(app, RelockMode.ON_PHONE_LOCK))

        // Package exit does not lock in ON_PHONE_LOCK mode
        lockStateManager.onPackageExited(app, RelockMode.ON_PHONE_LOCK)
        assertFalse(lockStateManager.isAppLocked(app, RelockMode.ON_PHONE_LOCK))

        // Screen turned off (power button pressed)
        lockStateManager.onScreenOff()

        // Now app is relocked
        assertTrue(lockStateManager.isAppLocked(app, RelockMode.ON_PHONE_LOCK))
    }

    @Test
    fun testScreenOffClearsAllUnlockedApps() {
        val app1 = "com.whatsapp"
        val app2 = "com.instagram.android"
        lockStateManager.recordUnlock(app1)
        lockStateManager.recordUnlock(app2)

        assertFalse(lockStateManager.isAppLocked(app1, RelockMode.AFTER_1MIN))
        assertFalse(lockStateManager.isAppLocked(app2, RelockMode.AFTER_1MIN))

        // Screen off event
        lockStateManager.onScreenOff()

        assertTrue(lockStateManager.isAppLocked(app1, RelockMode.AFTER_1MIN))
        assertTrue(lockStateManager.isAppLocked(app2, RelockMode.AFTER_1MIN))
    }

    @Test
    fun testClearAll() {
        val app = "com.whatsapp"
        lockStateManager.recordUnlock(app)
        assertFalse(lockStateManager.isAppLocked(app, RelockMode.ON_PHONE_LOCK))

        lockStateManager.clearAll()
        assertTrue(lockStateManager.isAppLocked(app, RelockMode.ON_PHONE_LOCK))
    }

    @Test
    fun testSelfLock_ImmediatelyRelocksOnAppExit() {
        val ownPackage = "com.apzlocz.app"
        lockStateManager.recordUnlock(ownPackage)
        assertFalse(lockStateManager.isAppLocked(ownPackage, RelockMode.IMMEDIATELY))

        // When MainActivity onStop triggers onPackageExited with IMMEDIATELY
        lockStateManager.onPackageExited(ownPackage, RelockMode.IMMEDIATELY)
        assertTrue(lockStateManager.isAppLocked(ownPackage, RelockMode.IMMEDIATELY))
    }

    @Test
    fun testSelfLock_GracePeriodSurvivesAppExitUntilExpiration() {
        val ownPackage = "com.apzlocz.app"
        lockStateManager.recordUnlock(ownPackage)
        assertFalse(lockStateManager.isAppLocked(ownPackage, RelockMode.AFTER_10S))

        // Exiting the app under timed relock mode preserves the grace period
        lockStateManager.onPackageExited(ownPackage, RelockMode.AFTER_10S)
        assertFalse(lockStateManager.isAppLocked(ownPackage, RelockMode.AFTER_10S))

        // Expire the 10s timer
        currentTime += 10_001L
        assertTrue(lockStateManager.isAppLocked(ownPackage, RelockMode.AFTER_10S))
    }
}

