# Apz Locz

An offline, lightweight app locker for Android, inspired by Samsung One UI. Lock any app behind a PIN (4–8 digits), password or pattern, or use fingerprint / system face unlock.

> Status: documentation phase. Built phase-by-phase with Google Antigravity. See `docs/PHASE_STATUS.md`.

## Planned features
- Lock any installed app; PIN / password / pattern + biometrics
- Relock after 10 s, 30 s, 1 min or when the phone locks
- 5 wrong attempts → 30-second lockout
- Hides notifications of locked apps
- Cannot be uninstalled while a credential is set (remove credential first)
- 100% offline, no ads, no analytics, no network permission
- Light and dark themes, English only

## Docs
All specs are in [`docs/`](docs/). Agent instructions: [`AGENTS.md`](AGENTS.md) and [`.agents/rules/`](.agents/rules/).

## How to work with Antigravity
1. Open this folder in Antigravity.
2. Say: `START PHASE 1`.
3. Review the completion report, test on your device, then reply `approved` and say `START PHASE 2`, and so on.

## Limitations
No non-root app locker is unbypassable. Full limitations are listed here after Phase 10.

## License
MIT (added in Phase 1).
