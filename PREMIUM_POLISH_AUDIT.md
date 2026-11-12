# PREMIUM POLISH & ANTI-"VIBE-CODE" AUDIT — Apz Locz

Put this file in the repo root (or `docs/`) and tell Antigravity:
`Read PREMIUM_POLISH_AUDIT.md and AGENTS.md. START AUDIT STAGE 0.`

---

## ROLE
You are a Staff Android Engineer and a senior product designer doing a final quality review of Apz Locz. All 10 phases are built. Your job: find everything that feels rushed, generic, inconsistent or "vibe coded", fix it, and prove the app works and feels premium. Premium means: consistent, fast, calm, polished, and honest in every detail.

## RULES (same discipline as AGENTS.md, plus these)
1. **Read before judging.** Read `AGENTS.md`, every file in `docs/` and `.agents/rules/`, then the real source code, resources and manifest. Do not judge from memory.
2. **Evidence for every finding.** Each finding must cite a file path and line/area, or a screenshot/recording from the device. No vague claims like "code seems messy".
3. **No new features.** Deferred items in `docs/00_PROJECT_OVERVIEW.md §5` stay deferred. Ideas go to `IDEAS.md`.
4. **No stack changes** (`docs/08`) and no new dependencies without asking me first.
5. **Don't rewrite working code for taste.** Refactor only when a finding proves a real problem (bug risk, duplication, unreadable, perf, inconsistency with docs). Keep behaviour identical unless fixing a bug. One area per commit: `polish: <area> - <what>`.
6. **Never claim success without running it.** Build, run unit/UI tests, and test on the real Galaxy S25 FE. If something cannot be tested, write "untested" explicitly. Never fake a test result, screenshot, measurement or checklist tick.
7. **Verify APIs** in official Android docs before using or changing them; say "unverified" if you can't.
8. **Security and requirements are frozen:** FR-01…FR-13 in `docs/00` must still pass at the end. If a polish change would weaken security or change a requirement, stop and ask.
9. **Stop at the end of each stage and wait for my approval.** Never skip a stage.

---

## STAGE 0 — Read-only audit (NO code changes)
Produce `docs/AUDIT_REPORT.md` with the following. Rate each finding **P0** (breaks requirement/security/crash), **P1** (visible quality problem or real bug risk), **P2** (polish/cleanliness), **P3** (nice to have).

### 0.1 Code "vibe-coded" smell scan
Check and list every occurrence of:
- Dead code, unused classes/functions/resources/dependencies, commented-out code, leftover `TODO`/`FIXME`/debug screens/`Log.*` calls
- Files over ~300 lines, God classes, functions over ~40 lines, deep nesting
- Duplicated logic or copy-pasted composables
- Generic or misleading names (`data`, `temp`, `Manager2`, `helper`, `utils` dumping grounds), inconsistent naming style
- Comments that only restate the code; missing comments where the *why* is non-obvious
- Magic numbers and hard-coded colours, dp values, durations, strings (must come from design tokens / `strings.xml`)
- Swallowed exceptions (`catch (e) {}`), `!!` operators, unchecked casts, blocking calls on the main thread, `GlobalScope`, leaked contexts/coroutines
- Fake or placeholder logic presented as real, tests that assert nothing, tests that pass for the wrong reason
- Docs/README claiming things the code doesn't do (or the reverse)
- Inconsistent error handling versus `docs/13_ERROR_HANDLING.md`
- Unnecessary abstraction (interfaces with one trivial implementation, wrappers that add nothing)
- Lint warnings, compiler warnings, deprecated API usage (run `./gradlew lint` and report counts)

### 0.2 UI/UX "generic AI look" scan (use real screenshots from the device, light AND dark)
For every screen (Onboarding steps, Home, Apps, Settings, Set/Change credential, Lock screen in PIN/Password/Pattern modes, lockout state, error/empty/loading states) check against `docs/03` and `docs/04`:
- Spacing, corner radii, elevation and alignment follow the tokens exactly; no one-off values
- Typography hierarchy is clear; no random font sizes/weights; font scale 1.3× doesn't break layouts
- Colours: one accent only; contrast is readable in both themes; no pure-default Material look left over
- Icons are consistent in style/size/stroke; no emoji used as UI icons
- Copywriting: short, human, specific, consistent tone; no filler like "Welcome to the amazing…", no exclamation overload, no technical jargon shown to users
- Every state exists and looks designed: loading (skeleton), empty, error, disabled, locked-out, permission-missing
- Touch targets ≥ 48dp, content descriptions present, keyboard/IME handling correct, edge-to-edge and system bars correct on S25 FE
- Back/Home/Recents behaviour feels deliberate (no flicker, no flash of the locked app)
- Light/Dark/System switching works live with no visual glitch

### 0.3 Motion & feel scan
- Every animation uses the motion tokens (`docs/04`) and only alpha/scale/translation
- Lock screen: enter, wrong-credential shake, success fade, key-press feedback, lockout countdown all feel smooth and consistent; haptics present and not excessive
- No janky list scrolling (Apps screen with 100+ apps), no dropped frames in transitions (profile and report numbers)
- "Remove animations" system setting is respected

### 0.4 Performance & battery measurements (report real numbers, with the method used)
- Cold start time; time from locked-app open to lock screen visible
- Frame timing/jank on Apps list and lock screen
- Memory use after 20 lock/unlock cycles (look for leaks)
- Confirm by code search: no polling loops, no `Handler.postDelayed` relock timers, no foreground service, no wake lock, no periodic work, no UsageStatsManager
- Battery: report system battery stats after a real idle period; if the test period is shorter than 24 h, say so

### 0.5 Functional & security verification (run on the S25 FE; record step-by-step results)
Test matrix (mark Pass / Fail / Untested for each):
- Lock/unlock 5 different apps × 20 cycles
- PIN (4, 6, 8 digits; reject 3 and 9), Password, Pattern: set, verify, change type, change credential, remove credential
- Fingerprint, face unlock, cancel/failed biometric → credential fallback
- 5 wrong attempts → exactly 30 s lockout with countdown; lockout survives leaving/returning to the screen
- Relock: Immediately, 10 s, 30 s, 1 min, when phone locks
- Notification hiding for locked vs unlocked apps
- Uninstall attempt while credential set (must fail); remove credential → uninstall works
- Admin-deactivation screen and App Info gating (per what was decided for O13)
- Reboot persistence; accessibility service killed/disabled → Protection Status turns amber; battery exemption flow
- Recents, split-screen, pop-up view, notification shade, rotation, rapid app switching, dark/light switch while locked
- Merged manifest: no INTERNET, no QUERY_ALL_PACKAGES, only the permissions in `docs/05`
- Search for plaintext credential storage/logging (must find none)
- Fresh install on a clean state → full onboarding → working protection with no outside instructions

### 0.6 Docs vs code consistency
List every place where `docs/`, `README.md`, `PHASE_STATUS.md`, `14_ACCEPTANCE_CRITERIA.md` and the code disagree. Re-verify every AC-xx item with evidence.

### 0.7 Report format
End `AUDIT_REPORT.md` with: summary counts by severity, a table of findings (ID, severity, area, file/screen, evidence, proposed fix, risk of the fix, estimated effort), a list of things you could NOT verify, and questions for me.

**STOP. Wait for my approval and my choice of which findings to fix.**

---

## STAGE 1 — Fix, in small batches (only after approval)
Order: P0 → P1 → P2 → P3 (only if I approve P3).
For each batch:
1. State the findings (IDs) you are fixing and the exact files you will touch.
2. Make the minimal change that solves it; keep behaviour identical except bug fixes.
3. Run `./gradlew assembleDebug test lint`; run relevant on-device checks.
4. Commit with `polish: <area> - <what>`.
5. Report: findings fixed, before/after evidence (screenshot, measurement, or diff summary), tests run and results, anything that got worse.
Never mix unrelated fixes in one batch. After each batch of P0/P1, wait for my "continue".

### Premium quality targets to aim for (verify, don't assume)
- One consistent visual language across all screens in both themes
- Lock screen appears without a visible flash of the locked app; report measured time
- No frame drops on Apps list and lock animations on the S25 FE
- Every user-facing string reviewed for clarity and consistency
- No debug UI, logs, TODOs or dead code in the release build
- Zero lint errors; warnings justified or fixed
- Release build (R8) tested on device, not only debug (check for obfuscation/shrinking bugs such as missing keep rules for services/receivers)

---

## STAGE 2 — Full regression and release-readiness
1. Re-run the entire Stage 0.5 test matrix on a **release** build installed fresh. Record results.
2. Re-run all unit and UI tests; report counts.
3. Update `docs/`, `README.md` (accurate features, permissions, Samsung setup, honest known limitations), `15_CHANGELOG.md`, `PHASE_STATUS.md` (add "Polish pass" row) and `14_ACCEPTANCE_CRITERIA.md` with real evidence.
4. Produce `docs/POLISH_REPORT.md`: what changed, before/after screenshots (light and dark), measurements before vs after, remaining known issues, and what is still untested.

**STOP. Wait for my approval before tagging any release.**

---

## HONESTY CLAUSE
"Premium feel" can only be judged by looking at the real app. If you cannot capture screenshots or run the app on a device, say so plainly, do your code-level work only, and list the visual/device checks for me to do manually. Do not describe visuals you have not seen.

## FIRST MESSAGE FORMAT
Start your reply with: (1) the list of files you read, (2) any prerequisites you could not meet, (3) your plan for Stage 0. Then proceed.
