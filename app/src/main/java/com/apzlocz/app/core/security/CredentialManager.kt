package com.apzlocz.app.core.security

import com.apzlocz.app.core.datastore.CredentialType
import com.apzlocz.app.core.datastore.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.security.MessageDigest
import java.util.Base64

interface CredentialManager {
    val isCredentialSetFlow: Flow<Boolean>
    val credentialTypeFlow: Flow<CredentialType>

    suspend fun setCredential(type: CredentialType, secret: String): Result<Unit>
    suspend fun verifyCredential(input: String): Boolean
    suspend fun changeCredential(
        currentSecret: String,
        newType: CredentialType,
        newSecret: String
    ): Result<Unit>
    suspend fun removeCredential(currentSecret: String): Result<Unit>
}

class DefaultCredentialManager(
    private val settingsRepository: SettingsRepository,
    private val cryptoEngine: CryptoEngine
) : CredentialManager {

    override val isCredentialSetFlow: Flow<Boolean> = settingsRepository.settingsFlow.map {
        it.credentialType != CredentialType.NONE &&
                it.credentialHashEncBase64 != null &&
                it.credentialSaltBase64 != null &&
                it.credentialIvBase64 != null
    }

    override val credentialTypeFlow: Flow<CredentialType> = settingsRepository.settingsFlow.map {
        it.credentialType
    }

    override suspend fun setCredential(type: CredentialType, secret: String): Result<Unit> {
        val validation = CredentialValidator.validate(type, secret)
        if (validation is ValidationResult.Invalid) {
            return Result.failure(IllegalArgumentException(validation.reason))
        }

        val salt = Pbkdf2Helper.generateSalt()
        val hash = Pbkdf2Helper.hash(secret.toCharArray(), salt)
        val encryptedPayload = cryptoEngine.encrypt(hash)

        val saltBase64 = Base64.getEncoder().encodeToString(salt)
        val hashEncBase64 = Base64.getEncoder().encodeToString(encryptedPayload.ciphertext)
        val ivBase64 = Base64.getEncoder().encodeToString(encryptedPayload.iv)

        settingsRepository.updateCredentialData(
            type = type,
            saltBase64 = saltBase64,
            hashEncBase64 = hashEncBase64,
            ivBase64 = ivBase64
        )
        return Result.success(Unit)
    }

    override suspend fun verifyCredential(input: String): Boolean {
        val settings = settingsRepository.settingsFlow.first()
        if (settings.credentialType == CredentialType.NONE ||
            settings.credentialSaltBase64 == null ||
            settings.credentialHashEncBase64 == null ||
            settings.credentialIvBase64 == null
        ) {
            return false
        }

        return try {
            val salt = Base64.getDecoder().decode(settings.credentialSaltBase64)
            val ciphertext = Base64.getDecoder().decode(settings.credentialHashEncBase64)
            val iv = Base64.getDecoder().decode(settings.credentialIvBase64)

            val decryptedHash = cryptoEngine.decrypt(ciphertext, iv)
            val inputHash = Pbkdf2Helper.hash(input.toCharArray(), salt)

            // Constant-time array comparison to prevent timing attacks
            MessageDigest.isEqual(decryptedHash, inputHash)
        } catch (_: Exception) {
            false
        }
    }

    override suspend fun changeCredential(
        currentSecret: String,
        newType: CredentialType,
        newSecret: String
    ): Result<Unit> {
        if (!verifyCredential(currentSecret)) {
            return Result.failure(SecurityException("Current credential does not match"))
        }
        return setCredential(newType, newSecret)
    }

    override suspend fun removeCredential(currentSecret: String): Result<Unit> {
        if (!verifyCredential(currentSecret)) {
            return Result.failure(SecurityException("Current credential does not match"))
        }
        settingsRepository.clearCredential()
        return Result.success(Unit)
    }
}
