package com.apzlocz.app.core.security

import com.apzlocz.app.core.datastore.SettingsRepository
import kotlinx.coroutines.flow.first
import kotlin.math.ceil

data class LockoutState(
    val isLockedOut: Boolean,
    val remainingSeconds: Long,
    val failedAttempts: Int
)

interface LockoutManager {
    suspend fun getLockoutState(): LockoutState
    suspend fun recordFailedAttempt(): LockoutState
    suspend fun recordSuccess()
}

/**
 * Manages wrong credential attempts and 30-second lockout timer.
 * Follows docs/02_SRS.md §3 and docs/00_PROJECT_OVERVIEW.md (FR-05, O9).
 *
 * @param settingsRepository persistence for failed attempts and lockout expiration
 * @param timeProvider injected clock for deterministic, sleep-free unit testing
 */
class DefaultLockoutManager(
    private val settingsRepository: SettingsRepository,
    private val timeProvider: () -> Long = { System.currentTimeMillis() }
) : LockoutManager {

    companion object {
        const val MAX_ATTEMPTS = 5
        const val LOCKOUT_DURATION_MS = 30_000L // 30 seconds
    }

    override suspend fun getLockoutState(): LockoutState {
        val settings = settingsRepository.settingsFlow.first()
        val now = timeProvider()
        val lockoutUntil = settings.lockoutUntil
        val failedAttempts = settings.failedAttempts

        return if (lockoutUntil > now) {
            val remainingMs = lockoutUntil - now
            val remainingSec = ceil(remainingMs / 1000.0).toLong().coerceAtLeast(1L)
            LockoutState(
                isLockedOut = true,
                remainingSeconds = remainingSec,
                failedAttempts = failedAttempts
            )
        } else {
            if (lockoutUntil > 0L) {
                // Lockout period expired; reset counter
                settingsRepository.resetLockoutState()
                LockoutState(isLockedOut = false, remainingSeconds = 0L, failedAttempts = 0)
            } else {
                LockoutState(
                    isLockedOut = false,
                    remainingSeconds = 0L,
                    failedAttempts = failedAttempts
                )
            }
        }
    }

    override suspend fun recordFailedAttempt(): LockoutState {
        val currentState = getLockoutState()
        if (currentState.isLockedOut) {
            return currentState
        }

        val now = timeProvider()
        val newFailedAttempts = currentState.failedAttempts + 1

        return if (newFailedAttempts >= MAX_ATTEMPTS) {
            val lockoutUntil = now + LOCKOUT_DURATION_MS
            settingsRepository.updateLockoutState(
                failedAttempts = newFailedAttempts,
                lockoutUntil = lockoutUntil
            )
            LockoutState(
                isLockedOut = true,
                remainingSeconds = LOCKOUT_DURATION_MS / 1000L,
                failedAttempts = newFailedAttempts
            )
        } else {
            settingsRepository.updateLockoutState(
                failedAttempts = newFailedAttempts,
                lockoutUntil = 0L
            )
            LockoutState(
                isLockedOut = false,
                remainingSeconds = 0L,
                failedAttempts = newFailedAttempts
            )
        }
    }

    override suspend fun recordSuccess() {
        settingsRepository.resetLockoutState()
    }
}
