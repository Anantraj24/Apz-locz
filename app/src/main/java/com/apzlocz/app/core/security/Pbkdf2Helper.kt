package com.apzlocz.app.core.security

import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

/**
 * Key derivation helper using PBKDF2WithHmacSHA256.
 * Adheres strictly to docs/06_DATABASE_DESIGN.md and docs/11_SECURITY_GUIDELINES.md.
 */
object Pbkdf2Helper {
    private const val ALGORITHM = "PBKDF2WithHmacSHA256"
    private const val DEFAULT_ITERATIONS = 10_000
    private const val KEY_LENGTH_BITS = 256
    private const val SALT_LENGTH_BYTES = 16

    private val secureRandom = SecureRandom()

    fun generateSalt(lengthBytes: Int = SALT_LENGTH_BYTES): ByteArray {
        val salt = ByteArray(lengthBytes)
        secureRandom.nextBytes(salt)
        return salt
    }

    fun hash(
        secret: CharArray,
        salt: ByteArray,
        iterations: Int = DEFAULT_ITERATIONS,
        keyLengthBits: Int = KEY_LENGTH_BITS
    ): ByteArray {
        val spec = PBEKeySpec(secret, salt, iterations, keyLengthBits)
        val factory = SecretKeyFactory.getInstance(ALGORITHM)
        return try {
            factory.generateSecret(spec).encoded
        } finally {
            spec.clearPassword()
        }
    }
}
