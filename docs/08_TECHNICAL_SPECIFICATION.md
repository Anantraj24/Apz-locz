# 08 — Technical Specification

Status: PROVISIONAL until the owner approves this package. After approval the stack is locked; changes need written owner permission.

## Stack
- Kotlin (latest stable), Gradle Kotlin DSL, AGP latest stable, Version Catalog `gradle/libs.versions.toml`
- Single module `:app`; minSdk 26 (O7), compileSdk/targetSdk latest stable (verify)
- Jetpack Compose (BOM), Material 3 customised to One UI look, Navigation Compose
- MVVM, ViewModel, StateFlow, Coroutines/Flow; manual DI via `AppContainer` (no Hilt/Koin)
- DataStore (Preferences); no Room
- `androidx.biometric`
- Crypto: JDK PBKDF2WithHmacSHA256 + Android Keystore AES-GCM (no third-party crypto)
- Animations: Compose built-ins only
- App icons: PackageManager + in-memory `LruCache` (no Coil/Glide)
- Tests: JUnit, Compose UI tests; CI: GitHub Actions
- **Rule:** look up latest stable dependency versions from official sources; never guess versions.

## Forbidden without owner permission
Hilt/Dagger/Koin, Room, Retrofit/OkHttp, Firebase, analytics/ads SDKs, Lottie, Coil/Glide, foreground services, UsageStatsManager polling, any network code.

## Folder structure (package `com.apzlocz.app`)
```
app/src/main/java/com/apzlocz/app/
├── ApzLoczApp.kt, MainActivity.kt
├── core/{datastore, security, apps, util}
├── service/{AppWatcherService, ScreenOffReceiver, LockStateManager, NotificationGuardService}
├── lock/{LockActivity, LockScreen, PinInput, PasswordInput, PatternInput, BiometricHelper}
├── admin/ApzAdminReceiver
└── ui/{theme, components, onboarding, home, apps, settings}
```

## Patterns
Unidirectional data flow; UI state as immutable data classes; side effects in ViewModels; repositories expose Flow; services read from in-memory cached copies of settings (collected once), never block on disk in event handlers; files under ~300 lines; comments explain why.

## Performance rules
Lazy lists with stable keys; icons loaded lazily; no heavy work on the main thread; accessibility handler must be minimal and fast.
