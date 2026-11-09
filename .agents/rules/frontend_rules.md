# Frontend rules (Jetpack Compose)
- Compose only; MVVM with StateFlow; immutable UI state classes; state hoisting.
- Composables small, previewable (light and dark `@Preview`).
- Lists: `LazyColumn` with stable keys; icons loaded lazily from `IconCache`.
- Animate only alpha/scale/translation using motion tokens from `docs/04`.
- Honour the system "remove animations" setting.
- All strings in `strings.xml` (English only). Content descriptions on icons and keys.
- Touch targets ≥ 48dp; support 1.3× font scale.
- Never block the main thread; no work in composition.
