# Database / storage rules
- Storage = Jetpack DataStore (Preferences) + Android Keystore. No Room/SQLite unless the owner approves.
- Keys and types exactly as in `docs/06`. Changing a key requires a `schema_version` bump and a migration with a unit test.
- Credentials: PBKDF2WithHmacSHA256 + random salt + Keystore AES-GCM. Never store or log raw secrets, patterns or biometric data.
- Unlock timestamps live in memory only.
