# Testing rules
- Every task ends with a build (`./gradlew assembleDebug`) and relevant tests (`./gradlew test`).
- Logic needs unit tests (inject a clock; no real sleeps). UI inputs need Compose UI tests.
- Device behaviour is verified manually on the Galaxy S25 FE and recorded step-by-step; otherwise write "untested".
- Never tick an acceptance criterion (`docs/14`) without evidence. A failing test blocks phase completion.
- Re-run earlier phase tests at the end of each phase (regression).
