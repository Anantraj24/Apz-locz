package com.apzlocz.app

import com.apzlocz.app.core.datastore.CredentialType
import com.apzlocz.app.core.security.CredentialValidator
import com.apzlocz.app.core.security.ValidationResult
import org.junit.Assert.assertTrue
import org.junit.Test

class CredentialValidatorTest {

    @Test
    fun testValidPinLengths() {
        assertTrue(CredentialValidator.validatePin("1234") is ValidationResult.Valid)
        assertTrue(CredentialValidator.validatePin("123456") is ValidationResult.Valid)
        assertTrue(CredentialValidator.validatePin("12345678") is ValidationResult.Valid)
    }

    @Test
    fun testInvalidPinLengthsAndFormats() {
        // Less than 4 digits
        assertTrue(CredentialValidator.validatePin("123") is ValidationResult.Invalid)
        assertTrue(CredentialValidator.validatePin("") is ValidationResult.Invalid)

        // Greater than 8 digits
        assertTrue(CredentialValidator.validatePin("123456789") is ValidationResult.Invalid)

        // Non-digits
        assertTrue(CredentialValidator.validatePin("123a") is ValidationResult.Invalid)
        assertTrue(CredentialValidator.validatePin("12-34") is ValidationResult.Invalid)
        assertTrue(CredentialValidator.validatePin("abcd") is ValidationResult.Invalid)
    }

    @Test
    fun testValidPasswordLengths() {
        assertTrue(CredentialValidator.validatePassword("pass") is ValidationResult.Valid)
        assertTrue(CredentialValidator.validatePassword("MySecurePass!123") is ValidationResult.Valid)
        assertTrue(CredentialValidator.validatePassword("12345678901234567890123456789012") is ValidationResult.Valid)
    }

    @Test
    fun testInvalidPasswordLengths() {
        // Less than 4 characters
        assertTrue(CredentialValidator.validatePassword("abc") is ValidationResult.Invalid)
        assertTrue(CredentialValidator.validatePassword("") is ValidationResult.Invalid)

        // Greater than 32 characters
        assertTrue(CredentialValidator.validatePassword("123456789012345678901234567890123") is ValidationResult.Invalid)
    }

    @Test
    fun testValidPatterns() {
        assertTrue(CredentialValidator.validatePattern("0-1-2-5") is ValidationResult.Valid)
        assertTrue(CredentialValidator.validatePattern("0-3-6-7-8") is ValidationResult.Valid)
        assertTrue(CredentialValidator.validatePattern("0-1-2-3-4-5-6-7-8") is ValidationResult.Valid)
    }

    @Test
    fun testInvalidPatterns() {
        // Less than 4 dots
        assertTrue(CredentialValidator.validatePattern("0-1-2") is ValidationResult.Invalid)
        assertTrue(CredentialValidator.validatePattern("") is ValidationResult.Invalid)

        // Out of range dot index (> 8 or < 0)
        assertTrue(CredentialValidator.validatePattern("0-1-2-9") is ValidationResult.Invalid)

        // Repeated dots
        assertTrue(CredentialValidator.validatePattern("0-1-2-0") is ValidationResult.Invalid)

        // More than 9 dots
        assertTrue(CredentialValidator.validatePattern("0-1-2-3-4-5-6-7-8-0") is ValidationResult.Invalid)
    }

    @Test
    fun testTypeValidationDispatch() {
        assertTrue(CredentialValidator.validate(CredentialType.PIN, "5555") is ValidationResult.Valid)
        assertTrue(CredentialValidator.validate(CredentialType.PASSWORD, "strongPass") is ValidationResult.Valid)
        assertTrue(CredentialValidator.validate(CredentialType.PATTERN, "0-1-4-7") is ValidationResult.Valid)
        assertTrue(CredentialValidator.validate(CredentialType.NONE, "none") is ValidationResult.Invalid)
    }
}

