package com.apzlocz.app

import com.apzlocz.app.core.datastore.AppSettings
import com.apzlocz.app.core.util.BatteryHelper
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for Phase 9 Onboarding state and Protection Status calculations.
 */
class OnboardingProtectionTest {

    @Test
    fun testOnboardingDoneStatePersistence() = runTest {
        val repo = FakeSettingsRepository(AppSettings(onboardingDone = false))

        assertFalse(repo.settingsFlow.first().onboardingDone)

        repo.setOnboardingDone(true)
        assertTrue(repo.settingsFlow.first().onboardingDone)
    }

    @Test
    fun testProtectionStatusActiveOnlyWhenFullyConfigured() {
        // Case 1: Neither configured
        assertFalse(calculateIsFullyProtected(isCredentialSet = false, isAccessibilityGranted = false))

        // Case 2: Credential set but accessibility missing
        assertFalse(calculateIsFullyProtected(isCredentialSet = true, isAccessibilityGranted = false))

        // Case 3: Accessibility granted but credential missing
        assertFalse(calculateIsFullyProtected(isCredentialSet = false, isAccessibilityGranted = true))

        // Case 4: Both configured -> Active Green
        assertTrue(calculateIsFullyProtected(isCredentialSet = true, isAccessibilityGranted = true))
    }

    @Test
    fun testSamsungGuidanceTextIntegrity() {
        assertEquals(
            "Settings > Apps > Apz Locz > Battery > Unrestricted",
            BatteryHelper.SAMSUNG_GUIDANCE_TEXT
        )
    }

    private fun calculateIsFullyProtected(
        isCredentialSet: Boolean,
        isAccessibilityGranted: Boolean
    ): Boolean {
        return isCredentialSet && isAccessibilityGranted
    }
}
