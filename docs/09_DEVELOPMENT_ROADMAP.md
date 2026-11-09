# 09 — Development Roadmap (exactly 10 phases)

Every phase ends with the **Completion report** in `AGENTS.md §6` (tasks done/pending, failed tests, known issues, files modified, verification evidence), updates `PHASE_STATUS.md`, then STOPS for owner approval. Prerequisite for every phase except 1: previous phase approved.

Requirement → phase map: FR-01 P3-P5 · FR-02 P2,P5 · FR-03 P6 · FR-04/14 P7 · FR-05 P2,P5 · FR-06 P8 · FR-07 P9 · FR-08 P1 · FR-09 all · FR-10/17 P1,P5 · FR-11 P1,P10 · FR-12 P10 · FR-13 P1,P10 · FR-15 P4 · FR-16 P9.

---
## Phase 1 — Foundation & Design System
1. **Objective:** Buildable app with theme and reusable components.
2. **Scope:** FR-08, FR-10, FR-13, FR-11 (visual base).
3. **Tasks:** create project (O8 name/id; ask owner if unconfirmed); version catalog; minSdk per O7; R8 on release; package folders; theme per `04` (light/dark/system); components in `04`; previews; temporary Design Gallery in `MainActivity`; `.gitignore`, MIT LICENSE, README stub; GitHub Actions debug build.
4. **Files:** `build.gradle.kts`, `libs.versions.toml`, `ui/theme/*`, `ui/components/*`, `.github/workflows/build.yml`.
5. **Prerequisites:** owner approves docs and answers O7/O8.
6. **Instructions:** no features; gallery is temporary.
7. **Testing:** `assembleDebug`; launch on S25 FE; light and dark visual check; merged manifest check.
8. **Acceptance:** build passes; gallery shows all components in both themes; no INTERNET permission; scrolling smooth.
9. **Deliverables:** project skeleton, design system, CI workflow.
10. **Out of scope:** any real screen, storage, services.
11. **Report:** per AGENTS.md §6.

## Phase 2 — Credential & Storage Core
1. **Objective:** Secure data layer for credentials, settings and lockout.
2. **Scope:** FR-02 (logic), FR-05 (logic).
3. **Tasks:** `SettingsRepository` (keys in `06`); `CredentialManager` for PIN/password/pattern (validation per `02 §1`); PBKDF2 + Keystore; `LockoutManager` (5 wrong → 30 s, O9); `AppContainer`; unit tests with injected clock.
4. **Files:** `core/datastore/*`, `core/security/*`, `ApzLoczApp.kt`, tests.
5. **Prerequisites:** Phase 1 approved; O1, O2, O9 answered or provisional accepted.
6. **Instructions:** no UI; no plaintext secrets; no logging of secrets.
7. **Testing:** unit tests: valid/invalid PIN lengths, password, pattern, wrong→lockout at 5, timer expiry, change and remove credential.
8. **Acceptance:** `./gradlew test` passes; code search finds no plaintext credential storage/logging; settings persist across restart (temporary debug button, removed after).
9. **Deliverables:** tested security/storage modules.
10. **Out of scope:** lock screen UI, biometrics, device admin.
11. **Report:** per AGENTS.md §6.

## Phase 3 — Foreground App Detection
1. **Objective:** Event-driven detection of the foreground app.
2. **Scope:** FR-01 (detection part), FR-08.
3. **Tasks:** `AppWatcherService` with config for window-state changes only; ignore own app/SystemUI/IME/launcher; expose connection state; debug logging of detected package (debug build only); service description string; temporary debug screen with status and button to open Accessibility settings.
4. **Files:** `service/AppWatcherService.kt`, `res/xml/accessibility_config.xml`, manifest.
5. **Prerequisites:** Phase 2 approved.
6. **Instructions:** no polling, timers, UsageStats; no lock UI yet; request no capabilities beyond need.
7. **Testing:** manual: enable service, open 10 apps, verify detection; 30 min idle persistence; restart persistence.
8. **Acceptance:** foreground change detected within ~200 ms (measured and reported); service stays connected after 30 min idle on S25 FE.
9. **Deliverables:** working service + temporary debug screen.
10. **Out of scope:** locking, app list, relock.
11. **Report:** per AGENTS.md §6.

## Phase 4 — App List & Lock Selection
1. **Objective:** Users choose which apps are locked.
2. **Scope:** FR-01 (selection), FR-15.
3. **Tasks:** `InstalledAppsRepository` via `<queries>` (no QUERY_ALL_PACKAGES); `IconCache`; Apps screen (search, All/Locked tabs, switches, loading/empty/error states); persist locked set; service sees updates live; show-system-apps toggle (provisional).
4. **Files:** `core/apps/*`, `ui/apps/*`, navigation.
5. **Prerequisites:** Phases 2–3 approved.
6. **Instructions:** lazy icon loading; stable keys; no Coil.
7. **Testing:** list with 100+ apps scroll check; persistence across restart; Phase 3 log fires only for locked apps; UI tests for toggle and search.
8. **Acceptance:** smooth scrolling; selection persists and reaches the service without restart.
9. **Deliverables:** Apps screen.
10. **Out of scope:** lock screen, credentials UI.
11. **Report:** per AGENTS.md §6.

## Phase 5 — Lock Screen & Credential Entry
1. **Objective:** Locked apps are covered by a lock screen using PIN/password/pattern.
2. **Scope:** FR-01, FR-02 (UI), FR-05 (UI), FR-10/17 (theme selector), FR-11.
3. **Tasks:** `LockActivity` (excludeFromRecents, own task affinity, singleTask); inputs `PinInput`, `PasswordInput`, `PatternInput`; lock screen states per `03 S4`; animations per `04`; Set/Change/Remove credential screens (S6); minimal Settings shell with credential and theme selector; service launches `LockActivity`; back/home → home screen; for now apps relock immediately on leaving.
4. **Files:** `lock/*`, `ui/settings/*`, service launch logic.
5. **Prerequisites:** Phases 2–4 approved.
6. **Instructions:** use `CredentialManager` and `LockoutManager` only; no biometrics, no relock timers, no overlay unless O12 approved.
7. **Testing:** manual: all three credential types; 5 wrong attempts → 30 s lockout with countdown; Recents exclusion; measure time to lock screen; UI tests for each input.
8. **Acceptance:** correct credential reveals app; wrong shakes/clears; lockout works; not in Recents; timing reported honestly.
9. **Deliverables:** lock screen, credential screens, Settings shell.
10. **Out of scope:** biometrics, relock modes, notifications, uninstall protection.
11. **Report:** per AGENTS.md §6.

## Phase 6 — Biometric Unlock
1. **Objective:** Fingerprint and system face unlock with fallback.
2. **Scope:** FR-03.
3. **Tasks:** `BiometricHelper` with STRONG or WEAK; availability check; auto-prompt; key to re-trigger; settings toggle; error handling.
4. **Files:** `lock/BiometricHelper.kt`, lock screen, settings.
5. **Prerequisites:** Phase 5 approved.
6. **Instructions:** no `DEVICE_CREDENTIAL`; no deprecated FingerprintManager; store no biometric data.
7. **Testing:** fingerprint; face unlock if enrolled; cancel, error, lockout → credential input still works.
8. **Acceptance:** both biometrics work on S25 FE; fallback always available.
9. **Deliverables:** biometric unlock.
10. **Out of scope:** relock, notifications.
11. **Report:** per AGENTS.md §6.

## Phase 7 — Relock Timing
1. **Objective:** Relock after 10 s / 30 s / 1 min / when phone locks (+ Immediately).
2. **Scope:** FR-04, FR-14.
3. **Tasks:** `LockStateManager` (elapsedRealtime map); dynamic `ACTION_SCREEN_OFF` receiver from service; wire rules in `02 §4`; settings radio UI; unit tests with injected clock.
4. **Files:** `service/LockStateManager.kt`, `service/ScreenOffReceiver.kt`, settings UI.
5. **Prerequisites:** Phase 6 approved; O3 answered or accepted.
6. **Instructions:** no Handler/Timer/delay loops/alarms.
7. **Testing:** manual table of all modes on device; unit tests.
8. **Acceptance:** each mode behaves exactly per SRS; tests pass.
9. **Deliverables:** relock engine + UI.
10. **Out of scope:** per-app relock (O4).
11. **Report:** per AGENTS.md §6.

## Phase 8 — Hide Notifications of Locked Apps
1. **Objective:** FR-06.
2. **Scope:** FR-06.
3. **Tasks:** FIRST a research spike: verify on S25 FE which mechanism works (O6) and report it to the owner; wait for owner approval of the mechanism; then implement `NotificationGuardService`, Notification-access permission flow, settings toggle.
4. **Files:** `service/NotificationGuardService.kt`, manifest, settings.
5. **Prerequisites:** Phase 7 approved; O6 decided.
6. **Instructions:** never send notification content anywhere; do not store content longer than needed (prefer not at all).
7. **Testing:** notifications from 3 locked apps are hidden; from unlocked apps unaffected; behavior after unlock documented.
8. **Acceptance:** locked-app notification content is not visible while locked, verified on device; limits documented.
9. **Deliverables:** notification hiding + documented limits.
10. **Out of scope:** onboarding, admin.
11. **Report:** per AGENTS.md §6.

## Phase 9 — Onboarding, Protection Status & Uninstall Protection
1. **Objective:** Guided setup, status checking, FR-07.
2. **Scope:** FR-07, FR-16.
3. **Tasks:** onboarding pager (S1) with auto-detection; Home with Protection Status; Samsung battery guidance (verify wording on device, don't invent menu names); require credential to open Apz Locz; `ApzAdminReceiver`; admin active while credential set and removed when credential removed; gate admin-deactivation screen and own App Info (mechanism per O13, owner decision if `canRetrieveWindowContent` is needed); forgotten-credential handling per O14.
4. **Files:** `ui/onboarding/*`, `ui/home/*`, `admin/*`, service gating logic.
5. **Prerequisites:** Phase 8 approved; **O13 and O14 answered by owner**.
6. **Instructions:** ask the owner before widening accessibility capabilities; show clear warnings before enabling uninstall protection.
7. **Testing:** fresh install flow; uninstall attempt while credential set fails; remove credential → uninstall works; bypass tests (Recents, split-screen, pop-up, notification shade) documented.
8. **Acceptance:** Protection Status goes amber/green correctly; FR-07 behavior exactly as stated; bypass results written honestly.
9. **Deliverables:** onboarding, status, uninstall protection.
10. **Out of scope:** deferred features in `00 §5`.
11. **Report:** per AGENTS.md §6.

## Phase 10 — Hardening, QA & Release
1. **Objective:** Stable, documented v1.0.0 on GitHub.
2. **Scope:** FR-11, FR-12, FR-13, all NFRs.
3. **Tasks:** remove debug code; performance pass; 24 h battery check; accessibility pass; adaptive + monochrome icon; full regression of `14`; README (features, permissions, Samsung setup, limitations, privacy); release workflow with signing via GitHub secrets; tag v1.0.0.
4. **Files:** README, workflows, icons, any fixes.
5. **Prerequisites:** Phases 1–9 approved.
6. **Instructions:** no new features; ideas to `IDEAS.md`.
7. **Testing:** full regression on S25 FE; fresh-install test of the release APK.
8. **Acceptance:** every item in `14` passes or is documented as a known limitation with owner sign-off.
9. **Deliverables:** release APK, README, changelog.
10. **Out of scope:** new features, Play Store.
11. **Report:** per AGENTS.md §6.
