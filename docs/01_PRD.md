# 01 — PRD: Apz Locz

## Goals
G1 Lock chosen apps behind PIN/password/pattern/biometrics. G2 Lock screen appears fast (PROVISIONAL target: under 150 ms; measured, reported honestly). G3 Event-driven, low battery. G4 Premium One UI–style visuals, light and dark. G5 Fully offline.

## Non-goals
Play Store release, ads, analytics, accounts, cloud, root features, other languages, deferred items in `00 §5`.

## User stories
| ID | Story | FR |
|---|---|---|
| US-01 | As a user I pick apps to lock from a list | FR-01, FR-15 |
| US-02 | As a user I open a locked app and must enter my credential | FR-01, FR-02 |
| US-03 | As a user I choose PIN (4–8 digits), password or pattern | FR-02 |
| US-04 | As a user I unlock with fingerprint or face unlock | FR-03 |
| US-05 | As a user I choose when apps relock: 10 s, 30 s, 1 min, when phone locks (and Immediately) | FR-04, FR-14 |
| US-06 | As a user I am locked out for 30 s after 5 wrong attempts | FR-05 |
| US-07 | As a user my locked apps' notifications are hidden | FR-06 |
| US-08 | As a user I cannot uninstall Apz Locz while a credential is set; I remove it first, then can uninstall | FR-07 |
| US-09 | As a user I see whether protection is active and fix problems in one tap | FR-16 |
| US-10 | As a user I can use light, dark or system theme | FR-10, FR-17 |

## Priorities
Must: FR-01…FR-13. Should: FR-14…FR-17.

## Success metrics (manually verified on S25 FE)
- 5 test apps locked reliably over 20 open/close cycles each.
- Protection survives phone restart and 12 h idle after onboarding.
- No network permission in merged manifest.
- All acceptance criteria in `14` pass.
