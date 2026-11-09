# AGENTS.md — Apz Locz (read this first, every session)

Project: **Apz Locz**, an offline Android app locker (Samsung One UI inspired). Owner: Anant. Distribution: GitHub only.

## 1. Before coding, read
`docs/00_PROJECT_OVERVIEW.md` (requirement ledger + open decisions), the docs relevant to your phase (`01`–`14`), `docs/09_DEVELOPMENT_ROADMAP.md` (your phase), `docs/PHASE_STATUS.md`, and `.agents/rules/*.md`.

## 2. Hard rules
1. Follow CONFIRMED requirements and approved architecture. PROVISIONAL items may be implemented as written but never extended. OPEN items (docs `00 §4`) → STOP and ask the owner.
2. Never invent requirements, business logic, features, libraries, APIs or version numbers. Verify APIs in official docs (developer.android.com); if you cannot verify, say "unverified" and ask.
3. Never change the approved tech stack (`docs/08`) without written permission.
4. Never implement future-phase features. Ideas go to `IDEAS.md`.
5. Never unnecessarily rewrite or delete working code. Keep diffs small; explain every file you touch.
6. No unnecessary dependencies, no over-engineering.
7. No secrets in code, logs or git. No INTERNET permission, no network code.
8. Validate all inputs and handle errors per `docs/13`.
9. Never skip testing or claim success without verification. Never tick or mark complete anything you did not run. If you cannot run something (e.g. no device), say so plainly.
10. If requirements conflict or a decision is unclear → stop and ask.
11. When an approved implementation change happens, update the affected docs and `docs/15_CHANGELOG.md`.
12. Battery rules: no polling, foreground service, wake locks or periodic jobs.

## 3. Mandatory Phase Execution Protocol
When the owner says `START PHASE N` (N = 1…10) you must:
1. Read the relevant documentation.
2. Verify prerequisites (previous phase approved in `PHASE_STATUS.md`; required OPEN decisions answered). If not met → stop and report.
3. Implement ONLY Phase N, nothing from other phases.
4. Test and verify every deliverable and acceptance criterion.
5. Update relevant docs and `docs/PHASE_STATUS.md`.
6. Send the completion report (§6).
7. STOP and wait for owner approval. Never start another phase automatically. Never mark a phase complete if acceptance criteria or tests fail (use "Blocked" or "Awaiting approval with known failures").

## 4. Code quality
Clean, modular, reusable code; separation of concerns; consistent naming; files under ~300 lines; comments explain why; responsive and accessible UI (content descriptions, 48dp targets, font scaling); automated tests for logic; performance-aware (no main-thread work, lazy lists).

## 5. Conventions
Package `com.apzlocz.app` (O8). Commit message: `phase-N: short description`. Kotlin + Jetpack Compose, MVVM, manual DI.

## 6. Completion report template
```
Phase: N — <name>
Completed tasks: ...
Pending tasks: ...
Failed tests: ... (or none)
Known issues / risks: ...
Files modified: ...
Verification evidence: (commands + output, device test steps + results)
Acceptance criteria status: AC-xx pass/fail ...
Docs updated: ...
Open questions for owner: ...
```

## 7. Test device
Samsung Galaxy S25 FE. Anything not tested on it must be labelled "untested".
