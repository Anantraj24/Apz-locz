package com.apzlocz.app

import com.apzlocz.app.service.ForegroundAppFilter
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ForegroundAppFilterTest {

    private lateinit var filter: ForegroundAppFilter
    private val ownPackage = "com.apzlocz.app"
    private val launchers = setOf("com.sec.android.app.launcher", "com.google.android.apps.nexuslauncher")
    private val imes = setOf("com.samsung.android.honeyboard", "com.google.android.inputmethod.latin")

    @Before
    fun setUp() {
        filter = ForegroundAppFilter(
            ownPackageName = ownPackage,
            launcherPackages = launchers,
            imePackages = imes,
            debounceWindowMs = 200L
        )
    }

    @Test
    fun testIgnoresOwnPackage() {
        assertTrue(filter.shouldIgnore(ownPackage))
    }

    @Test
    fun testIgnoresSystemUi() {
        assertTrue(filter.shouldIgnore("com.android.systemui"))
        assertTrue(filter.shouldIgnore("com.samsung.android.app.cocktailbarservice"))
    }

    @Test
    fun testIgnoresLaunchers() {
        assertTrue(filter.shouldIgnore("com.sec.android.app.launcher"))
        assertTrue(filter.shouldIgnore("com.google.android.apps.nexuslauncher"))
    }

    @Test
    fun testIgnoresInputMethodEditors() {
        assertTrue(filter.shouldIgnore("com.samsung.android.honeyboard"))
        assertTrue(filter.shouldIgnore("com.google.android.inputmethod.latin"))
    }

    @Test
    fun testIgnoresBlankOrEmptyPackage() {
        assertTrue(filter.shouldIgnore(""))
        assertTrue(filter.shouldIgnore("   "))
    }

    @Test
    fun testAllowsUserApplications() {
        assertFalse(filter.shouldIgnore("com.google.android.youtube"))
        assertFalse(filter.shouldIgnore("com.whatsapp"))
        assertFalse(filter.shouldIgnore("com.instagram.android"))
    }

    @Test
    fun testDebouncesRapidBurstEventsForSamePackage() {
        val app = "com.whatsapp"

        // First event at t = 1000ms should pass
        assertFalse(filter.shouldDebounce(app, nowMs = 1000L))

        // Second duplicate event at t = 1080ms (< 200ms debounce window) should be ignored
        assertTrue(filter.shouldDebounce(app, nowMs = 1080L))

        // Event at t = 1250ms (> 200ms debounce window) should pass
        assertFalse(filter.shouldDebounce(app, nowMs = 1250L))
    }

    @Test
    fun testDoesNotDebounceDifferentPackages() {
        // App A at t = 1000ms
        assertFalse(filter.shouldDebounce("com.whatsapp", nowMs = 1000L))

        // App B immediately at t = 1050ms (different package) should pass
        assertFalse(filter.shouldDebounce("com.instagram.android", nowMs = 1050L))
    }

    @Test
    fun testTransientOverlaysAreRecognized() {
        // Keyboards, SystemUI dialogs, and own lock activity
        assertTrue(filter.isTransientOverlay("com.samsung.android.honeyboard"))
        assertTrue(filter.isTransientOverlay("com.google.android.inputmethod.latin"))
        assertTrue(filter.isTransientOverlay("com.android.systemui"))
        assertTrue(filter.isTransientOverlay(ownPackage))

        // Normal apps and launchers are not transient overlays
        assertFalse(filter.isTransientOverlay("com.whatsapp"))
        assertFalse(filter.isTransientOverlay("com.sec.android.app.launcher"))
    }

    @Test
    fun testAppExitDetection() {
        val app = "com.whatsapp"

        // Same package is not an exit
        assertFalse(filter.isAppExit(app, app))

        // Keyboard popping up over app is NOT an exit
        assertFalse(filter.isAppExit(app, "com.samsung.android.honeyboard"))

        // SystemUI (volume slider / notification shade) is NOT an exit
        assertFalse(filter.isAppExit(app, "com.android.systemui"))

        // Apz Locz lock screen itself is NOT an exit
        assertFalse(filter.isAppExit(app, ownPackage))

        // Leaving app for Launcher IS an exit
        assertTrue(filter.isAppExit(app, "com.sec.android.app.launcher"))

        // Switching to another third-party app IS an exit
        assertTrue(filter.isAppExit(app, "com.instagram.android"))
    }
}

