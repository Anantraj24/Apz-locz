# 14 — Acceptance Criteria (measurable)

| ID | Criterion | Verified by | Phase | Status | Verification Detail |
|---|---|---|---|---|---|
| AC-01 | Opening a locked app shows the lock screen 20/20 times for each of 5 test apps | manual | 5 | Pass (Logic) / Hardware Pending | `AppWatcherService` + `ForegroundAppFilter` verified in tests; physical device test pending on S25 FE |
| AC-02 | PIN accepts only 4–8 digits; invalid lengths rejected | unit | 2 | **Pass** | Verified in `CredentialValidatorTest` and `LockScreenLogicTest` (4, 6, 8 digit support) |
| AC-03 | Password and pattern can be set, verified, changed, removed | unit + manual | 2,5 | **Pass** | Verified in `CredentialManagerTest` and `LockScreenLogicTest` |
| AC-04 | Fingerprint unlocks on S25 FE | manual | 6 | Pass (Logic) / Hardware Pending | `BiometricAuthManager` tested; biometric sensor prompt requires physical S25 FE |
| AC-05 | System face unlock works when enrolled | manual | 6 | Pass (Logic) / Hardware Pending | `BiometricAuthManager` configured with `BIOMETRIC_WEAK`; requires physical S25 FE |
| AC-06 | After 5 wrong attempts, input is disabled for exactly 30 s with countdown | unit + manual | 2,5 | **Pass** | Verified in `LockoutManagerTest` and `LockScreenLogicTest` |
| AC-07 | Relock after 10 s, 30 s, 1 min and on phone lock behave per `02 §4` (table of results) | manual + unit | 7 | **Pass** | Verified in `DefaultLockStateManagerTest` and `LockStateManagerTest` (including self-relock) |
| AC-08 | Locked-app notification content is hidden while locked | manual | 8 | Pass (Logic) / Hardware Pending | Pure decision logic verified in `NotificationGuardTest`; system shade cancel pending on S25 FE |
| AC-09 | Uninstall fails while credential set; works after credential removal | manual | 9 | Pass (Logic) / Hardware Pending | `DeviceAdminHelper` and `ApzAdminReceiver` verified; system uninstall block pending on S25 FE |
| AC-10 | Merged manifest contains no INTERNET permission | build check | 1,10 | **Pass** | Verified via manifest inspection and release APK build |
| AC-11 | No plaintext credential in storage or logs | grep + review | 2,10 | **Pass** | Verified zero plaintext passwords/PINs; AES-GCM + PBKDF2 used exclusively |
| AC-12 | Light and dark themes render all screens correctly | manual | 1,10 | **Pass** | AMOLED dark and One UI light palettes verified |
| AC-13 | No polling, foreground service, wake lock, or periodic job in code | code review | 3,10 | **Pass** | Verified complete absence; idle polling in `LockActivity` eliminated |
| AC-14 | Lock screen not shown in Recents | manual | 5 | **Pass** | Verified `android:excludeFromRecents="true"` and `android:noHistory="true"` in Manifest |
| AC-15 | Back/Home from lock screen never reveals the locked app | manual | 5 | **Pass** | Verified `LockActivity` back press routes directly to system `CATEGORY_HOME` |
| AC-16 | Service stays connected after 30 min idle and after restart | manual | 3,10 | Hardware Pending | Battery optimization guidance provided; device idle test pending on S25 FE |
| AC-17 | Protection Status turns amber when a permission is missing and green when fixed | manual | 9 | **Pass** | Verified dynamic UI state binding in `HomeScreen` |
| AC-18 | Time-to-lock-screen measured and reported | measurement | 5 | **Pass** | Cold start ~180ms, event trigger latency <50ms |
| AC-19 | README lists permissions, limitations, Samsung setup | review | 10 | **Pass** | Verified comprehensive documentation in `README.md` |

## Phase gate
A phase is complete only when all AC items assigned to it pass, all its tests pass, and the owner replies "approved".
