# 10 — Testing Strategy

| Type | What | Tools |
|---|---|---|
| Unit | CredentialManager (all types), LockoutManager, LockStateManager, migrations, validators | JUnit, injected clock |
| Integration | DataStore persistence, service ↔ settings flow | instrumented tests |
| UI | PIN/password/pattern inputs, apps list toggle/search, onboarding steps | Compose UI tests |
| E2E (manual on S25 FE) | Lock/unlock across 20 cycles × 5 apps; relock modes table; lockout; restart; 12 h idle; uninstall flow | checklist in `14` |
| Security | No plaintext secrets, no INTERNET, bypass attempts (Recents, split-screen, pop-up, notification shade) | grep, manifest check, manual |
| Regression | Re-run all earlier phase tests at the end of each phase | CI + manual |
| Performance/battery | time-to-lock-screen, jank check, 24 h battery stats | profiler, system battery stats |

Rules: tests run before a phase is called complete; failures block approval; the report must include evidence (command output or step results). Anything not testable on a device is labelled "untested".
