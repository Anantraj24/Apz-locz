# 13 — Error Handling

## Categories
| Category | Examples | Behaviour |
|---|---|---|
| Validation | PIN not 4–8 digits, too-short password/pattern, mismatched confirmation | Inline message, no crash, nothing saved |
| Authentication | Wrong credential, lockout | Shake + haptic; after 5 wrong → 30 s countdown |
| Biometric | No hardware, none enrolled, cancelled, lockout | Hide/disable biometric UI or fall back to credential |
| Permission | Accessibility/notification access/battery exemption off | Protection Status turns amber with a Fix button; app never crashes |
| System | Package no longer installed, icon load failure | Skip/ placeholder icon |
| Security/Keystore | Key invalidated or missing | Fail closed (treat as credential unavailable), show recovery path per O14 |
| Storage | DataStore read failure | Use defaults, keep locking active for known locked apps |

## Logging
Debug builds only, tag `ApzLocz`. Never log credentials, patterns, notification content or biometric data.

## Recovery & fallback
Service killed → unlock state lost → apps relock (fail safe). If the lock screen cannot launch, send the user to the home screen rather than leaving a locked app open.
