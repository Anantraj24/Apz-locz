# 15 — Changelog

## [Unreleased]
- Phase 3: Foreground App Detection implementation.
  - Implemented `AppWatcherService` (`AccessibilityService`) listening for `TYPE_WINDOW_STATE_CHANGED` events.
  - Added `ForegroundAppFilter` with 200ms debounce and filtering for own package, system UI, launchers, and IMEs.
  - Configured `accessibility_config.xml` requesting zero unnecessary capabilities (`canRetrieveWindowContent="false"`).
  - Added temporary Foreground App Detection status card in `MainActivity` with live service state and quick shortcut to accessibility settings.
  - Added unit test suite `ForegroundAppFilterTest` verifying package filtering and debounce mechanics.
- Phase 2: Credential & Storage Core implementation.
  - Implemented `SettingsRepository` with Jetpack DataStore Preferences and keys matching `docs/06`.
  - Implemented `CredentialValidator` verifying PIN (4–8 digits), password (4–32 chars), and pattern (min 4 dots).
  - Implemented `Pbkdf2Helper` (PBKDF2WithHmacSHA256, random salt) and `CryptoEngine` (AES-GCM encryption with Android Keystore key alias `apz_locz_cred_key`).
  - Implemented `CredentialManager` for setting, constant-time verification, changing, and removing credentials with zero plaintext persistence.
  - Implemented `LockoutManager` enforcing 5 wrong attempts → 30-second lockout timer with countdown and clock injection for testability.
  - Implemented manual dependency injection via `AppContainer` in `ApzLoczApp`.
  - Added unit test suites (`CredentialValidatorTest`, `CredentialManagerTest`, `LockoutManagerTest`) covering all validation, lockout, expiry, and credential lifecycle rules.
- Phase 1: Foundation & One UI Design System implementation.
  - Initialized Gradle Kotlin DSL project with Version Catalog (`gradle/libs.versions.toml`).
  - Added One UI design system tokens (colors, typography, shape, spacing, motion).
  - Built core components: `OneUiCard`, `OneUiGroupedList`, `OneUiSwitch`, `PillButton`, `CollapsingHeaderScaffold`, `StatusBadge`, `PinDots`, and `PinKey`.
  - Created temporary Design Gallery screen in `MainActivity` with live theme switching and interactive PIN keypad.
  - Added unit test suite `OneUiThemeTest` verifying tokens.
  - Configured GitHub Actions CI workflow in `.github/workflows/build.yml`.
  - Added MIT License and `.gitignore`.
- Documentation package created (supersedes earlier "OneLock" draft files).
