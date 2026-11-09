# 06 — Database Design

No SQL database (Room is not used). Storage is Jetpack DataStore (Preferences) plus the Android Keystore. No network, no sync.

## DataStore keys
| Key | Type | Default | Notes |
|---|---|---|---|
| `locked_packages` | Set<String> | empty | Package names |
| `relock_mode` | String enum | IMMEDIATELY (O3) | IMMEDIATELY, AFTER_10S, AFTER_30S, AFTER_1MIN, ON_PHONE_LOCK |
| `biometric_enabled` | Boolean | false | |
| `hide_notifications` | Boolean | true (O15, provisional) | |
| `theme_mode` | String enum | SYSTEM | LIGHT, DARK, SYSTEM |
| `show_system_apps` | Boolean | false | PROVISIONAL |
| `credential_type` | String enum | none | PIN, PASSWORD, PATTERN |
| `credential_salt` | Bytes (Base64) | none | random per set |
| `credential_hash_enc` | Bytes (Base64) | none | AES-GCM-encrypted PBKDF2 hash |
| `credential_iv` | Bytes (Base64) | none | GCM IV |
| `failed_attempts` | Int | 0 | |
| `lockout_until` | Long | 0 | reboot clears (limitation) |
| `onboarding_done` | Boolean | false | |
| `schema_version` | Int | 1 | for migrations |

## In-memory only (never persisted)
`package → lastUnlockElapsedRealtime`.

## Keystore
One AES-GCM key alias `apz_locz_cred_key`, non-exportable.

## Migrations
Increment `schema_version`; write a migration function per version; unit-test each migration. Never delete user data silently.

## Constraints
PIN 4–8 digits; password/pattern limits per O2; `locked_packages` entries must be valid package names; missing/uninstalled packages are ignored at read time.
