# 02 — SRS: System Behaviour

## 1. Credentials (FR-02)
- Types: PIN, PASSWORD, PATTERN. One active type (O1, provisional).
- PIN: digits only, length 4–8, validated on set/change.
- Password: any characters; length per O2 (provisional 4–32).
- Pattern: 3×3 grid; min dots per O2 (provisional 4). Stored as ordered dot indices ("0-1-2-5").
- All types normalize to a string and are hashed identically (PBKDF2-SHA256, random salt), then encrypted with an Android Keystore key. Raw credentials are never stored or logged.
- Changing type or credential requires the current credential.
- "Remove credential" requires the current credential; it also deactivates uninstall protection (FR-07).

## 2. Lock flow (FR-01)
1. Accessibility service receives a window-state-change event with package P.
2. Ignore: own package, SystemUI, input methods, launcher.
3. If P is not in the locked set → do nothing.
4. If P is unlocked per relock rules (§4) → do nothing.
5. Else launch `LockActivity` with extra P.
6. Correct credential/biometric → record unlock time for P, close `LockActivity`, locked app visible.
7. Back or Home on lock screen → go to home screen; never reveal P.
Edge cases: rapid app switching (debounce duplicate events for the same package within a short window), lock screen already showing, process death of service (state is in-memory; re-lock on restart), device rotation, split-screen (document result).

## 3. Lockout (FR-05, O9)
- Counter of consecutive wrong credential attempts (biometric failures do not count; PIN/password/pattern do).
- At 5 wrong attempts → input disabled for 30 s with visible countdown. After the timer, the counter resets.
- Correct credential resets the counter.
- Provisional: lockout state survives process death; a reboot clears it (known limitation).

## 4. Relock rules (FR-04, FR-14)
Modes: IMMEDIATELY (provisional), AFTER_10S, AFTER_30S, AFTER_1MIN, ON_PHONE_LOCK.
- Unlock state is a map package → `elapsedRealtime` of last unlock, kept in memory only.
- Timed modes: locked again when `now − lastUnlock > duration` at the next time the app is opened.
- ON_PHONE_LOCK: a screen-off event clears all unlock state.
- IMMEDIATELY: each time the app is left and re-opened it is locked.
- No timers or polling loops.

## 4b. Biometrics (FR-03)
BiometricPrompt accepting BIOMETRIC_STRONG or BIOMETRIC_WEAK (face unlock on many Samsung phones is weak class). Negative button "Use credential". Failure/cancel/lockout always leaves the credential input usable. Biometric toggle in Settings.

## 5. Notification hiding (FR-06, O6)
- Requires the user to grant Notification access.
- Behavior goal: notification content from locked apps is not visible while the app is locked.
- Mechanism is OPEN (O6): must be researched and verified on device in Phase 8 before implementation. Must not forward content to anything outside the device.

## 6. Uninstall protection (FR-07, O13, O14)
- While a credential is set, Device Admin is active for Apz Locz so Android refuses to uninstall it.
- Removing the credential (after entering it) deactivates Device Admin, then uninstall works normally.
- Opening the system screen that deactivates the admin, and Apz Locz's own App Info screen, must be gated by the credential (mechanism per O13).
- Forgotten credential: OPEN (O14).

## 7. Validation & errors
See `13_ERROR_HANDLING.md`. Every input is validated; never crash on missing permission, missing package, or failed biometric hardware.

## 8. Settings
Credential type/change/remove, biometrics toggle, relock mode, hide notifications toggle, theme (Light/Dark/System), show system apps toggle (PROVISIONAL), About (version, GitHub link).
