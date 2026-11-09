# 07 — API Specification

**There is no network API.** Apz Locz is 100% offline (FR-08); no HTTP endpoints, authentication tokens, or API error codes exist. The agent must not create any.

## Internal contracts (Kotlin interfaces)
```kotlin
interface CredentialManager {
  suspend fun setCredential(type: CredentialType, secret: String): Result<Unit>
  suspend fun verify(secret: String): VerifyResult   // Success | Wrong | LockedOut(remainingMs)
  suspend fun change(old: String, newType: CredentialType, new: String): Result<Unit>
  suspend fun remove(current: String): Result<Unit>   // also deactivates admin (FR-07)
  fun isCredentialSet(): Boolean
}
interface LockStateManager {
  fun needsLock(pkg: String, now: Long, mode: RelockMode): Boolean
  fun recordUnlock(pkg: String, now: Long)
  fun clearAll()
}
interface SettingsRepository { /* Flows for each key in 06 */ }
```
## Intents
`LockActivity` extra: `EXTRA_TARGET_PACKAGE` (String, required). If missing → finish immediately and go home.

## Error type
`sealed class AppError` per `13_ERROR_HANDLING.md`.
