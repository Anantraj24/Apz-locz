package com.apzlocz.app

import com.apzlocz.app.core.datastore.AppSettings
import com.apzlocz.app.lock.BiometricStatus
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class BiometricSettingsTest {

    private lateinit var fakeRepository: FakeSettingsRepository

    @Before
    fun setUp() {
        fakeRepository = FakeSettingsRepository(
            initialSettings = AppSettings(biometricEnabled = false)
        )
    }

    @Test
    fun testInitialBiometricStateIsDisabled() = runTest {
        val settings = fakeRepository.settingsFlow.first()
        assertFalse(settings.biometricEnabled)
    }

    @Test
    fun testToggleBiometricSettings() = runTest {
        fakeRepository.setBiometricEnabled(true)
        val enabledSettings = fakeRepository.settingsFlow.first()
        assertTrue(enabledSettings.biometricEnabled)

        fakeRepository.setBiometricEnabled(false)
        val disabledSettings = fakeRepository.settingsFlow.first()
        assertFalse(disabledSettings.biometricEnabled)
    }

    @Test
    fun testBiometricStatusEnumValues() {
        val values = BiometricStatus.values()
        assertTrue(values.contains(BiometricStatus.AVAILABLE))
        assertTrue(values.contains(BiometricStatus.NO_HARDWARE))
        assertTrue(values.contains(BiometricStatus.HW_UNAVAILABLE))
        assertTrue(values.contains(BiometricStatus.NONE_ENROLLED))
        assertTrue(values.contains(BiometricStatus.UNSUPPORTED))
    }
}

