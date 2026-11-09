# 12 — Deployment Guide

No servers. Distribution = GitHub.

- **Environments:** debug (local/CI), release (signed APK).
- **CI (every push):** `./gradlew assembleDebug test`.
- **Release:** pushing tag `vX.Y.Z` builds a signed release APK and attaches it to a GitHub Release. Signing keystore stored as base64 in GitHub Secrets (`KEYSTORE_BASE64`, `KEYSTORE_PASSWORD`, `KEY_ALIAS`, `KEY_PASSWORD`); never committed.
- **Install:** user downloads APK from Releases and sideloads it.
- **Monitoring:** none (no analytics). Issues tracked via GitHub Issues.
- **Rollback:** re-publish the previous release APK; schema migrations must be forward-safe (see `06`).
