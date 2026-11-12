# 00 — Project Overview: Apz Locz

## Vision
A fast, premium-feeling, fully offline app locker for Android, inspired by Samsung One UI, because One UI has no built-in way to lock individual apps in place (Secure Folder moves apps into a separate sandbox instead).

## Problem
Users want to lock chosen apps with a PIN, password, pattern, fingerprint or face unlock. Existing lockers are often heavy, drain battery, show ads, or break on One UI.

## Solution
A native Android app that detects when a locked app opens and covers it with a lock screen. It is event-driven (no polling), works with no network, hides notifications of locked apps, and resists uninstall while a credential is set.

## Target users
Android users (primary: Samsung owners). Owner/developer: Anant, test device Galaxy S25 FE. Distribution: GitHub only (APK in Releases).

## Scope (v1)
In: everything marked CONFIRMED or PROVISIONAL in the Requirement Ledger below.
Out: Play Store release, any network feature, any feature in "Deferred" (section 5).

## Objectives
Reliable locking, smooth animation, low battery, offline, clean open-source repo.

---

## 1. Status legend
- **CONFIRMED** — stated by the owner.
- **PROVISIONAL** — my suggestion/assumption. Becomes binding only when the owner approves this package; the agent may implement it but must not extend it.
- **OPEN** — undecided. The agent must STOP and ask before implementing anything that depends on it.

## 2. Requirement Ledger
| ID | Requirement | Status |
|---|---|---|
| FR-01 | Lock any installed app so it asks for credentials when opened | CONFIRMED |
| FR-02 | Credential types: PIN (4–8 digits), password (characters), pattern; user can set any of them | CONFIRMED (see O1 for how they combine) |
| FR-03 | Unlock with fingerprint or the phone's system face unlock | CONFIRMED |
| FR-04 | Relock timing: 10 s, 30 s, 1 min, and when the phone locks | CONFIRMED |
| FR-05 | 5 wrong attempts → 30-second lockout timer | CONFIRMED |
| FR-06 | Hide notifications of locked apps | CONFIRMED (mechanism = O6) |
| FR-07 | While a credential is set, Apz Locz cannot be uninstalled; remove the credential first, then uninstall works | CONFIRMED (mechanism = O13) |
| FR-08 | 100% offline | CONFIRMED |
| FR-09 | English only | CONFIRMED |
| FR-10 | Light and dark theme | CONFIRMED |
| FR-11 | Samsung One UI–inspired UI, smooth premium animations, lightweight, low battery | CONFIRMED |
| FR-12 | Should work smoothly on Android phones in general; primary target Samsung | CONFIRMED |
| FR-13 | GitHub-friendly, not for Play Store | CONFIRMED |
| NFR-01 | App size is not a concern | CONFIRMED (replaces my earlier 10 MB target) |
| FR-14 | "Immediately" relock option in addition to FR-04 | PROVISIONAL (O3) |
| FR-15 | App search and All/Locked tabs in the app list | PROVISIONAL |
| FR-16 | Protection Status card + guided onboarding for permissions and Samsung battery settings | PROVISIONAL |
| FR-17 | Light/Dark/System theme selector | PROVISIONAL |

## 3. Technical decisions (PROVISIONAL until approval)
Kotlin, Jetpack Compose, single module, MVVM, DataStore, AccessibilityService detection, BiometricPrompt, Android Keystore, DeviceAdmin for uninstall protection, NotificationListenerService for hiding notifications. Details in `08_TECHNICAL_SPECIFICATION.md`.

## 4. Open decisions (agent must stop and ask)
| ID | Question | Provisional default |
|---|---|---|
| O1 | Does the user have ONE active credential type at a time (PIN or password or pattern, switchable), or several at once? | One active type at a time |
| O2 | Min/max length for password and min dots for pattern | Password 4–32 chars, pattern min 4 dots |
| O3 | Include "Immediately" relock option? | Yes, as default |
| O4 | Per-app relock times? | No, global only in v1 |
| O6 | How to hide notification content (cancel and replace with a generic notice vs other)? Must be researched and verified on device in Phase 8 | none |
| O7 | minSdk for "all Android phones" | 26 (Android 8.0) |
| O8 | Application ID (Android IDs can't contain spaces) | `com.apzlocz.app`, display name "Apz Locz", repo `Apz-Locz` |
| O9 | Lockout repeat behavior after the first 30 s | Same 30 s after each further 5 wrong attempts, no escalation |
| O11 | Test devices | Only the S25 FE; claims for other devices must say "untested" |
| O12 | Overlay (`SYSTEM_ALERT_WINDOW`) to avoid flash of the locked app | Add only if measured flash is visible |
| O13 | Exact uninstall-block mechanism and how to protect the admin-deactivation screen (may need `canRetrieveWindowContent`) | Device Admin active only while a credential is set; deactivated automatically when the credential is removed |
| O14 | Forgotten credential recovery (offline app + uninstall block can lock the owner out) | Offline-only model: Zero cloud/remote recovery. Recovery via Android Safe Mode boot (disables 3rd-party accessibility/admin, allowing clean app data reset). Confirmed in Phase 10. |
| O15 | Default for "hide notifications" toggle (on or off) | On |

## 5. Deferred (NOT in v1, never implement without approval)
Intruder selfie, fake crash screen, lock profiles, hiding app icons, per-app relock, vault features, locking the whole system Settings app.

## 6. Contradictions found and how handled
1. No backend exists → `06` documents local storage only, `07` documents internal contracts and states "no network API", backend rules say "none".
2. "Password" vs PIN → all three credential types supported (FR-02).
3. "Works perfectly on all phones" → written as best-effort on Android 8+, verified only on S25 FE; limits go in README.
4. Earlier files used the name OneLock and a 10 MB goal → superseded by this package.

## 7. Documentation audit results
Cross-checks done: PRD↔SRS (every FR has SRS rules), PRD↔UI (every user-facing FR has a screen), UI↔Design System (all components defined), Architecture↔Tech Spec (same components/packages), Database↔API (no network API; storage keys match contracts), Requirements↔Roadmap (every FR mapped in `09`), Acceptance↔Testing (every acceptance item has a test type), Security/Deployment↔Architecture (permissions list identical). Remaining gaps are exactly the OPEN items above.
