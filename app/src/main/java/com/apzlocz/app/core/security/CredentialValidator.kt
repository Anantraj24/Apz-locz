package com.apzlocz.app.core.security

import com.apzlocz.app.core.datastore.CredentialType

sealed interface ValidationResult {
    data object Valid : ValidationResult
    data class Invalid(val reason: String) : ValidationResult
}

/**
 * Validates PIN, Password, and Pattern inputs against constraints
 * defined in docs/02_SRS.md §1 and docs/00_PROJECT_OVERVIEW.md (O2).
 */
object CredentialValidator {

    private val PIN_REGEX = Regex("^[0-9]{4,8}$")
    private const val PASSWORD_MIN_LENGTH = 4
    private const val PASSWORD_MAX_LENGTH = 32
    private const val PATTERN_MIN_DOTS = 4
    private const val PATTERN_MAX_DOTS = 9

    fun validatePin(pin: String): ValidationResult {
        return when {
            pin.isEmpty() -> ValidationResult.Invalid("PIN cannot be empty")
            pin.length < 4 -> ValidationResult.Invalid("PIN must be at least 4 digits")
            pin.length > 8 -> ValidationResult.Invalid("PIN cannot exceed 8 digits")
            !PIN_REGEX.matches(pin) -> ValidationResult.Invalid("PIN must contain only numbers")
            else -> ValidationResult.Valid
        }
    }

    fun validatePassword(password: String): ValidationResult {
        return when {
            password.length < PASSWORD_MIN_LENGTH ->
                ValidationResult.Invalid("Password must be at least $PASSWORD_MIN_LENGTH characters")
            password.length > PASSWORD_MAX_LENGTH ->
                ValidationResult.Invalid("Password cannot exceed $PASSWORD_MAX_LENGTH characters")
            else -> ValidationResult.Valid
        }
    }

    fun validatePattern(pattern: String): ValidationResult {
        val dots = pattern.split("-").mapNotNull { it.trim().toIntOrNull() }
        return when {
            dots.size < PATTERN_MIN_DOTS ->
                ValidationResult.Invalid("Pattern must connect at least $PATTERN_MIN_DOTS dots")
            dots.size > PATTERN_MAX_DOTS ->
                ValidationResult.Invalid("Pattern cannot connect more than $PATTERN_MAX_DOTS dots")
            dots.any { it !in 0..8 } ->
                ValidationResult.Invalid("Pattern dot indices must be between 0 and 8")
            dots.toSet().size != dots.size ->
                ValidationResult.Invalid("Pattern cannot connect the same dot multiple times")
            else -> ValidationResult.Valid
        }
    }

    fun validate(type: CredentialType, secret: String): ValidationResult {
        return when (type) {
            CredentialType.PIN -> validatePin(secret)
            CredentialType.PASSWORD -> validatePassword(secret)
            CredentialType.PATTERN -> validatePattern(secret)
            CredentialType.NONE -> ValidationResult.Invalid("Credential type cannot be NONE")
        }
    }
}
