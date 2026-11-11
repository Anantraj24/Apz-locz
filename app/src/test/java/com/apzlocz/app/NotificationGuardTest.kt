package com.apzlocz.app

import com.apzlocz.app.core.datastore.AppSettings
import com.apzlocz.app.service.shouldHideNotification
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for Phase 8 Notification Guard logic and preference handling.
 */
class NotificationGuardTest {

    @Test
    fun testNotificationNotHiddenWhenFeatureDisabled() {
        val shouldHide = shouldHideNotification(
            packageName = "com.whatsapp",
            isLockedApp = true,
            hideEnabled = false,
            isCurrentlyLocked = true
        )
        assertFalse(shouldHide)
    }

    @Test
    fun testNotificationNotHiddenForUnlockedApp() {
        val shouldHide = shouldHideNotification(
            packageName = "com.google.android.youtube",
            isLockedApp = false,
            hideEnabled = true,
            isCurrentlyLocked = false
        )
        assertFalse(shouldHide)
    }

    @Test
    fun testNotificationNotHiddenWhenAppWithinGracePeriod() {
        // App is locked in settings, but user recently unlocked it and it's within grace period
        val shouldHide = shouldHideNotification(
            packageName = "com.whatsapp",
            isLockedApp = true,
            hideEnabled = true,
            isCurrentlyLocked = false
        )
        assertFalse(shouldHide)
    }

    @Test
    fun testNotificationHiddenWhenAppLockedAndCurrentlyLocked() {
        val shouldHide = shouldHideNotification(
            packageName = "com.whatsapp",
            isLockedApp = true,
            hideEnabled = true,
            isCurrentlyLocked = true
        )
        assertTrue(shouldHide)
    }

    @Test
    fun testHideNotificationsPreferencePersistence() = runTest {
        val fakeRepo = FakeSettingsRepository(AppSettings(hideNotifications = true))

        assertTrue(fakeRepo.settingsFlow.first().hideNotifications)

        fakeRepo.setHideNotifications(false)
        assertFalse(fakeRepo.settingsFlow.first().hideNotifications)

        fakeRepo.setHideNotifications(true)
        assertTrue(fakeRepo.settingsFlow.first().hideNotifications)
    }
}
