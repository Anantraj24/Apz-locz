# 11 — Security Guidelines

- **Authentication:** local credential only (PIN/password/pattern) + biometrics. No accounts.
- **Storage:** PBKDF2WithHmacSHA256, random salt, high iteration count (look up current OWASP guidance; do not guess), hash encrypted with Keystore AES-GCM. Never store or log raw credentials, patterns or biometric data.
- **Validation:** all inputs validated (PIN 4–8 digits, password/pattern per O2, package names).
- **Authorization:** only the credential holder can change/remove credential, change settings, or deactivate protection.
- **Secrets:** no API keys exist. Release signing keystore and passwords only in GitHub Secrets, never committed.
- **Network:** none; no INTERNET permission. Rate limiting equals the 5-attempts/30 s lockout.
- **Permissions:** only those in `05`; explain each in onboarding.
- **Uninstall/tamper:** FR-07 via Device Admin; gate admin-deactivation and own App Info screens (O13).
- **Notifications:** content of locked apps must never be copied off-device or logged.
- **Known limitations (must be in README):** no non-root locker is unbypassable; reboot clears lockout; OEM background-kill can disable the service until restarted; forgotten credential (O14).
