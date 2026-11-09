# 04 — Design System (Samsung One UI inspired)

Recreate the feel with original assets; do not copy Samsung logos, fonts or proprietary artwork.

## Principles
Reachability (title top, actions bottom), rounded soft cards, one accent colour, motion that confirms an action, no decorative infinite animation.

## Colour tokens (PROVISIONAL values)
| Token | Light | Dark |
|---|---|---|
| background | #F4F4F6 | #000000 |
| surface | #FFFFFF | #1C1C1E |
| surfaceVariant | #EBEBF0 | #2C2C2E |
| onSurface | #1A1A1A | #F2F2F2 |
| onSurfaceMuted | #6E6E73 | #9A9AA0 |
| accent | #3E91FF | #4C9CFF |
| accentContainer | #DCEBFF | #16304F |
| success | #34C759 | #30D158 |
| error | #FF453A | #FF6961 |
| warning | #FFB020 | #FFC247 |

## Typography
System default font only (no bundled fonts). Sizes (sp): Display 34, Title Large 28, Title 20, Body 16, Label 14, Caption 12. Respect font scaling.

## Shape & spacing
Radius: cards 28dp, list groups 24dp, chips 16dp, buttons pill, keys circle. Spacing: 4/8/12/16/24/32dp. Screen padding 16–20dp. Touch targets ≥ 48dp (keys 72dp).

## Components (built in Phase 1)
OneUiCard, OneUiGroupedList, OneUiSwitch, PillButton, CollapsingHeaderScaffold, StatusBadge, PinDots, PinKey. Built later: PatternGrid (Phase 5), PasswordField (Phase 5).

## Motion tokens
durationFast 120 ms · durationMedium 220 ms · durationSlow 350 ms · easeOut cubic-bezier(0.2, 0, 0, 1) · springSoft (damping 0.75, stiffness 400) · springBouncy (damping 0.55, stiffness 500).
Rules: animate alpha, scale, translation only; no animated blur over large areas; honour system animation scale.

## Lock screen motion
Enter: fade + scale 0.96→1.0, ~220 ms. Wrong: horizontal damped-spring shake + haptic + error tint. Success: ≤ 250 ms total. Key press: scale 0.92 + tonal change + light haptic.

## Icons
Small custom vector set (shield, lock, unlock, fingerprint, face, backspace, settings, search, check, warning, eye). 24dp, rounded caps. Adaptive launcher icon with monochrome layer.
