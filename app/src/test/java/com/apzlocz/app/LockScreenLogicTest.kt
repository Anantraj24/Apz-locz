package com.apzlocz.app

import com.apzlocz.app.core.datastore.CredentialType
import com.apzlocz.app.core.security.AesGcmCryptoEngine
import com.apzlocz.app.core.security.DefaultCredentialManager
import com.apzlocz.app.core.security.DefaultLockoutManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import javax.crypto.KeyGenerator

class LockScreenLogicTest {

    private lateinit var fakeRepository: FakeSettingsRepository
    private lateinit var cryptoEngine: AesGcmCryptoEngine
    private lateinit var credentialManager: DefaultCredentialManager
    private lateinit var lockoutManager: DefaultLockoutManager
    private var currentTime = 1_000_000L

    @Before
    fun setUp() {
        fakeRepository = FakeSettingsRepository()

        val keyGen = KeyGenerator.getInstance("AES")
        keyGen.init(256)
        val secretKey = keyGen.generateKey()
        cryptoEngine = AesGcmCryptoEngine(secretKey)

        credentialManager = DefaultCredentialManager(
            settingsRepository = fakeRepository,
            cryptoEngine = cryptoEngine
        )
        lockoutManager = DefaultLockoutManager(
            settingsRepository = fakeRepository,
            timeProvider = { currentTime }
        )
    }

    @Test
    fun testPinUnlockFlow() = runTest {
        // Set up 4-digit PIN
        val setResult = credentialManager.setCredential(CredentialType.PIN, "1234")
        assertTrue(setResult.isSuccess)
        assertEquals(CredentialType.PIN, credentialManager.credentialTypeFlow.first())

        // Wrong PIN -> record failure
        assertFalse(credentialManager.verifyCredential("9999"))
        val state1 = lockoutManager.recordFailedAttempt()
        assertEquals(1, state1.failedAttempts)
        assertFalse(state1.isLockedOut)

        // Correct PIN -> success & reset
        assertTrue(credentialManager.verifyCredential("1234"))
        lockoutManager.recordSuccess()
        val stateReset = lockoutManager.getLockoutState()
        assertEquals(0, stateReset.failedAttempts)
    }

    @Test
    fun testPasswordUnlockFlow() = runTest {
        val setResult = credentialManager.setCredential(CredentialType.PASSWORD, "SecretPass123!")
        assertTrue(setResult.isSuccess)

        assertFalse(credentialManager.verifyCredential("WrongPass"))
        assertTrue(credentialManager.verifyCredential("SecretPass123!"))
    }

    @Test
    fun testPatternUnlockFlow() = runTest {
        // 3x3 pattern "0-1-2-5"
        val setResult = credentialManager.setCredential(CredentialType.PATTERN, "0-1-2-5")
        assertTrue(setResult.isSuccess)

        assertFalse(credentialManager.verifyCredential("0-1-2-3"))
        assertTrue(credentialManager.verifyCredential("0-1-2-5"))
    }

    @Test
    fun testFiveWrongAttemptsTriggers30sLockout() = runTest {
        credentialManager.setCredential(CredentialType.PIN, "4321")

        for (i in 1..4) {
            val state = lockoutManager.recordFailedAttempt()
            assertFalse(state.isLockedOut)
            assertEquals(i, state.failedAttempts)
        }

        // 5th attempt triggers lockout
        val lockedState = lockoutManager.recordFailedAttempt()
        assertTrue(lockedState.isLockedOut)
        assertEquals(30L, lockedState.remainingSeconds)

        // Advance 10s -> still 20s remaining
        currentTime += 10_000L
        val midState = lockoutManager.getLockoutState()
        assertTrue(midState.isLockedOut)
        assertEquals(20L, midState.remainingSeconds)

        // Advance past 30s -> lockout expires
        currentTime += 21_000L
        val expiredState = lockoutManager.getLockoutState()
        assertFalse(expiredState.isLockedOut)
    }

    @Test
    fun testChangeAndRemoveCredential() = runTest {
        credentialManager.setCredential(CredentialType.PIN, "1111")
        assertTrue(credentialManager.isCredentialSetFlow.first())

        // Change from PIN to Pattern
        val changeResult = credentialManager.changeCredential("1111", CredentialType.PATTERN, "0-3-6-7")
        assertTrue(changeResult.isSuccess)
        assertEquals(CredentialType.PATTERN, credentialManager.credentialTypeFlow.first())
        assertTrue(credentialManager.verifyCredential("0-3-6-7"))

        // Remove credential
        val removeResult = credentialManager.removeCredential("0-3-6-7")
        assertTrue(removeResult.isSuccess)
        assertFalse(credentialManager.isCredentialSetFlow.first())
        assertEquals(CredentialType.NONE, credentialManager.credentialTypeFlow.first())
    }
}
