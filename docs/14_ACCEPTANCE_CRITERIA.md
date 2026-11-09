# 14 — Acceptance Criteria (measurable)

| ID | Criterion | Verified by | Phase |
|---|---|---|---|
| AC-01 | Opening a locked app shows the lock screen 20/20 times for each of 5 test apps | manual | 5 |
| AC-02 | PIN accepts only 4–8 digits; invalid lengths rejected | unit | 2 |
| AC-03 | Password and pattern can be set, verified, changed, removed | unit + manual | 2,5 |
| AC-04 | Fingerprint unlocks on S25 FE | manual | 6 |
| AC-05 | System face unlock works when enrolled | manual | 6 |
| AC-06 | After 5 wrong attempts, input is disabled for exactly 30 s with countdown | unit + manual | 2,5 |
| AC-07 | Relock after 10 s, 30 s, 1 min and on phone lock behave per `02 §4` (table of results) | manual + unit | 7 |
| AC-08 | Locked-app notification content is hidden while locked | manual | 8 |
| AC-09 | Uninstall fails while credential set; works after credential removal | manual | 9 |
| AC-10 | Merged manifest contains no INTERNET permission | build check | 1,10 |
| AC-11 | No plaintext credential in storage or logs | grep + review | 2,10 |
| AC-12 | Light and dark themes render all screens correctly | manual | 1,10 |
| AC-13 | No polling, foreground service, wake lock, or periodic job in code | code review | 3,10 |
| AC-14 | Lock screen not shown in Recents | manual | 5 |
| AC-15 | Back/Home from lock screen never reveals the locked app | manual | 5 |
| AC-16 | Service stays connected after 30 min idle and after restart | manual | 3,10 |
| AC-17 | Protection Status turns amber when a permission is missing and green when fixed | manual | 9 |
| AC-18 | Time-to-lock-screen measured and reported | measurement | 5 |
| AC-19 | README lists permissions, limitations, Samsung setup | review | 10 |

## Phase gate
A phase is complete only when all AC items assigned to it pass, all its tests pass, and the owner replies "approved".
