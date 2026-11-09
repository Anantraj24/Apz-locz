package com.apzlocz.app

import com.apzlocz.app.core.security.DefaultLockoutManager
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class LockoutManagerTest {

    private lateinit var fakeRepository: FakeSettingsRepository
    private var simulatedTimeMs: Long = 1_000_000L
    private lateinit var lockoutManager: DefaultLockoutManager

    @Before
    fun setUp() {
        fakeRepository = FakeSettingsRepository()
        simulatedTimeMs = 1_000_000L
        lockoutManager = DefaultLockoutManager(
            settingsRepository = fakeRepository,
            timeProvider = { simulatedTimeMs }
        )
    }

    @Test
    fun testInitialStateNotLockedOut() = runTest {
        val state = lockoutManager.getLockoutState()
        assertFalse(state.isLockedOut)
        assertEquals(0, state.failedAttempts)
        assertEquals(0L, state.remainingSeconds)
    }

    @Test
    fun testFailedAttemptsUnderThresholdDoNotTriggerLockout() = runTest {
        for (i in 1..4) {
            val state = lockoutManager.recordFailedAttempt()
            assertFalse(state.isLockedOut)
            assertEquals(i, state.failedAttempts)
            assertEquals(0L, state.remainingSeconds)
        }
    }

    @Test
    fun testFifthFailedAttemptTriggers30SecondLockout() = runTest {
        for (i in 1..4) {
            lockoutManager.recordFailedAttempt()
        }
        val fifthState = lockoutManager.recordFailedAttempt()

        assertTrue(fifthState.isLockedOut)
        assertEquals(5, fifthState.failedAttempts)
        assertEquals(30L, fifthState.remainingSeconds)
    }

    @Test
    fun testCountdownDecrementsAccurately() = runTest {
        for (i in 1..5) {
            lockoutManager.recordFailedAttempt()
        }

        // Advance simulated time by 10 seconds
        simulatedTimeMs += 10_000L
        val intermediateState = lockoutManager.getLockoutState()
        assertTrue(intermediateState.isLockedOut)
        assertEquals(20L, intermediateState.remainingSeconds)
    }

    @Test
    fun testLockoutExpiresAfter30SecondsAndResetsCounter() = runTest {
        for (i in 1..5) {
            lockoutManager.recordFailedAttempt()
        }

        // Advance simulated time by 30.5 seconds
        simulatedTimeMs += 30_500L
        val expiredState = lockoutManager.getLockoutState()

        assertFalse(expiredState.isLockedOut)
        assertEquals(0L, expiredState.remainingSeconds)
        assertEquals(0, expiredState.failedAttempts)
    }

    @Test
    fun testRecordSuccessClearsFailedAttempts() = runTest {
        lockoutManager.recordFailedAttempt()
        lockoutManager.recordFailedAttempt()
        assertEquals(2, lockoutManager.getLockoutState().failedAttempts)

        lockoutManager.recordSuccess()
        val state = lockoutManager.getLockoutState()
        assertEquals(0, state.failedAttempts)
        assertFalse(state.isLockedOut)
    }
}

