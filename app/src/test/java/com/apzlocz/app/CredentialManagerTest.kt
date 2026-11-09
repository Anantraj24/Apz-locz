package com.apzlocz.app

import com.apzlocz.app.core.datastore.CredentialType
import com.apzlocz.app.core.security.AesGcmCryptoEngine
import com.apzlocz.app.core.security.DefaultCredentialManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import javax.crypto.KeyGenerator

class CredentialManagerTest {

    private lateinit var fakeRepository: FakeSettingsRepository
    private lateinit var cryptoEngine: AesGcmCryptoEngine
    private lateinit var credentialManager: DefaultCredentialManager

    @Before
    fun setUp() {
        fakeRepository = FakeSettingsRepository()

        // Generate standard in-memory AES-256 key for testing
        val keyGen = KeyGenerator.getInstance("AES")
        keyGen.init(256)
        val secretKey = keyGen.generateKey()
        cryptoEngine = AesGcmCryptoEngine(secretKey)

        credentialManager = DefaultCredentialManager(
            settingsRepository = fakeRepository,
            cryptoEngine = cryptoEngine
        )
    }

    @Test
    fun testInitialCredentialState() = runTest {
        assertFalse(credentialManager.isCredentialSetFlow.first())
        assertEquals(CredentialType.NONE, credentialManager.credentialTypeFlow.first())
        assertFalse(credentialManager.verifyCredential("1234"))
    }

    @Test
    fun testSetAndVerifyPin() = runTest {
        val result = credentialManager.setCredential(CredentialType.PIN, "1234")
        assertTrue(result.isSuccess)
        assertTrue(credentialManager.isCredentialSetFlow.first())
        assertEquals(CredentialType.PIN, credentialManager.credentialTypeFlow.first())

        // Correct PIN
        assertTrue(credentialManager.verifyCredential("1234"))

        // Incorrect PIN
        assertFalse(credentialManager.verifyCredential("0000"))
        assertFalse(credentialManager.verifyCredential("123"))
    }

    @Test
    fun testSetInvalidPinFails() = runTest {
        val tooShort = credentialManager.setCredential(CredentialType.PIN, "12")
        assertTrue(tooShort.isFailure)

        val tooLong = credentialManager.setCredential(CredentialType.PIN, "123456789")
        assertTrue(tooLong.isFailure)

        val nonDigits = credentialManager.setCredential(CredentialType.PIN, "abcd")
        assertTrue(nonDigits.isFailure)

        assertFalse(credentialManager.isCredentialSetFlow.first())
    }

    @Test
    fun testSetAndVerifyPassword() = runTest {
        val password = "SuperSecretPassword#2026"
        val result = credentialManager.setCredential(CredentialType.PASSWORD, password)
        assertTrue(result.isSuccess)
        assertEquals(CredentialType.PASSWORD, credentialManager.credentialTypeFlow.first())

        assertTrue(credentialManager.verifyCredential(password))
        assertFalse(credentialManager.verifyCredential("WrongPassword"))
    }

    @Test
    fun testSetAndVerifyPattern() = runTest {
        val pattern = "0-1-2-5-8"
        val result = credentialManager.setCredential(CredentialType.PATTERN, pattern)
        assertTrue(result.isSuccess)
        assertEquals(CredentialType.PATTERN, credentialManager.credentialTypeFlow.first())

        assertTrue(credentialManager.verifyCredential(pattern))
        assertFalse(credentialManager.verifyCredential("0-1-2-5"))
    }

    @Test
    fun testChangeCredentialSuccess() = runTest {
        credentialManager.setCredential(CredentialType.PIN, "1234")

        // Change from PIN to Password
        val changeResult = credentialManager.changeCredential(
            currentSecret = "1234",
            newType = CredentialType.PASSWORD,
            newSecret = "NewPassword123"
        )
        assertTrue(changeResult.isSuccess)
        assertEquals(CredentialType.PASSWORD, credentialManager.credentialTypeFlow.first())

        // Old PIN no longer works
        assertFalse(credentialManager.verifyCredential("1234"))
        // New Password works
        assertTrue(credentialManager.verifyCredential("NewPassword123"))
    }

    @Test
    fun testChangeCredentialWithWrongCurrentSecretFails() = runTest {
        credentialManager.setCredential(CredentialType.PIN, "1234")

        val changeResult = credentialManager.changeCredential(
            currentSecret = "9999",
            newType = CredentialType.PASSWORD,
            newSecret = "NewPassword123"
        )
        assertTrue(changeResult.isFailure)
        assertEquals(CredentialType.PIN, credentialManager.credentialTypeFlow.first())
        assertTrue(credentialManager.verifyCredential("1234"))
    }

    @Test
    fun testRemoveCredentialSuccess() = runTest {
        credentialManager.setCredential(CredentialType.PIN, "1234")
        assertTrue(credentialManager.isCredentialSetFlow.first())

        val removeResult = credentialManager.removeCredential("1234")
        assertTrue(removeResult.isSuccess)
        assertFalse(credentialManager.isCredentialSetFlow.first())
        assertEquals(CredentialType.NONE, credentialManager.credentialTypeFlow.first())
        assertFalse(credentialManager.verifyCredential("1234"))
    }

    @Test
    fun testRemoveCredentialWithWrongSecretFails() = runTest {
        credentialManager.setCredential(CredentialType.PIN, "1234")

        val removeResult = credentialManager.removeCredential("0000")
        assertTrue(removeResult.isFailure)
        assertTrue(credentialManager.isCredentialSetFlow.first())
        assertTrue(credentialManager.verifyCredential("1234"))
    }

    @Test
    fun testZeroPlaintextStorage() = runTest {
        val rawPin = "7890"
        credentialManager.setCredential(CredentialType.PIN, rawPin)

        val settings = fakeRepository.getCurrentSettings()
        assertNotNull(settings.credentialSaltBase64)
        assertNotNull(settings.credentialHashEncBase64)
        assertNotNull(settings.credentialIvBase64)

        // Ensure plaintext PIN does not appear in any serialized field
        assertFalse(settings.credentialSaltBase64!!.contains(rawPin))
        assertFalse(settings.credentialHashEncBase64!!.contains(rawPin))
        assertFalse(settings.credentialIvBase64!!.contains(rawPin))
    }
}
