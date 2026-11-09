# 03 — UI/UX Specification

Language: English only. Themes: Light and Dark (+ System). Portrait phones primary; must not break in landscape or at 1.3× font scale (tablet layouts out of scope).

## Navigation map
```
Onboarding (first run only)
   └─> Home ──> Apps (lock selection)
          ├──> Settings ──> Credential, Biometrics, Relock, Notifications, Theme, About
          └──> Protection Status fixes (deep links to system settings)
LockActivity (system-triggered, over any locked app)
```
Home is the app's root after onboarding. Opening Apz Locz itself requires the credential (Phase 9).

## Screens
### S1 Onboarding (pager, 6 steps) — FR-16
Create credential → biometrics (optional) → Accessibility → Notification access → Battery optimization exemption → Done. Each step: icon, title, 2-line reason ("nothing leaves your phone"), bottom pill button; auto-detects completion on return. States: pending, granted, skipped (only for optional steps).

### S2 Home
Collapsing large title. Protection Status card (Active green / Needs attention amber with a Fix pill per missing item). Summary: number of locked apps, current relock mode. Cards: Apps, Settings.

### S3 Apps (FR-01, FR-15)
Search pill, tabs All/Locked, rows (icon 48dp, name, switch). States: loading (skeleton rows), empty search result, no locked apps yet, error loading.

### S4 Lock screen (FR-01…FR-05)
Top third: locked app icon + name. Middle: credential input by active type:
- PIN: dots + numeric keypad (3×4) with biometric key
- Password: text field (hidden, show/hide toggle) + "Unlock" pill
- Pattern: 3×3 dot grid with drawn line
Biometric prompt auto-opens if enabled. States: idle, entering, wrong (shake + haptic + error tint), locked-out (inputs disabled, "Try again in 30 s" countdown), success (fade out ≤ 250 ms).

### S5 Settings
Grouped cards: Security (credential type/change/remove, biometrics), Relock (radio: Immediately*, 10 s, 30 s, 1 min, When phone locks), Notifications (hide toggle), Appearance (theme), About. (*provisional)

### S6 Change/Set credential
Type selector (PIN/Password/Pattern) → enter → confirm → saved. Validation messages inline.

## Interactions & animations
See `04_DESIGN_SYSTEM.md` motion tokens. Respect the system "remove animations" setting.

## Accessibility
Content descriptions on icons/keys, 48dp targets, font scaling, sufficient contrast in both themes.
