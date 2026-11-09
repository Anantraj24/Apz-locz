# 15 — Changelog

## [Unreleased]
- Phase 1: Foundation & One UI Design System implementation.
  - Initialized Gradle Kotlin DSL project with Version Catalog (`gradle/libs.versions.toml`).
  - Added One UI design system tokens (colors, typography, shape, spacing, motion).
  - Built core components: `OneUiCard`, `OneUiGroupedList`, `OneUiSwitch`, `PillButton`, `CollapsingHeaderScaffold`, `StatusBadge`, `PinDots`, and `PinKey`.
  - Created temporary Design Gallery screen in `MainActivity` with live theme switching and interactive PIN keypad.
  - Added unit test suite `OneUiThemeTest` verifying tokens.
  - Configured GitHub Actions CI workflow in `.github/workflows/build.yml`.
  - Added MIT License and `.gitignore`.
- Documentation package created (supersedes earlier "OneLock" draft files).
