# 05 — System Architecture

Single-module Android app, fully offline, no backend, no services except the accessibility and notification-listener system bindings.

## Components
| Component | Role |
|---|---|
| `AppWatcherService` (AccessibilityService) | Detects foreground package changes; launches `LockActivity`; registers screen-off receiver |
| `LockActivity` + `LockScreen` (Compose) | Credential/biometric entry |
| `LockStateManager` | In-memory unlock timestamps + relock decision |
| `CredentialManager` | Set/verify/change/remove credential (PIN/password/pattern), hashing, Keystore |
| `LockoutManager` | Wrong-attempt counter, 30 s lockout |
| `SettingsRepository` | DataStore-backed settings and locked-app set |
| `InstalledAppsRepository` + `IconCache` | App list and icons |
| `NotificationGuardService` (NotificationListenerService) | FR-06, mechanism per O6 |
| `ApzAdminReceiver` (DeviceAdminReceiver) | FR-07 |
| UI (Compose, MVVM) | Onboarding, Home, Apps, Settings |

## Data flow
```
Window event -> AppWatcherService -> locked? -> LockStateManager says "needs lock?"
   -> yes -> LockActivity -> CredentialManager/Biometric -> success -> LockStateManager.recordUnlock
Screen off -> ScreenOffReceiver -> LockStateManager.clear (ON_PHONE_LOCK)
Notification posted -> NotificationGuardService -> locked app? -> hide per O6
UI toggles -> ViewModel -> SettingsRepository (DataStore) -> Flow -> service (cached in memory)
```

## Constraints
No INTERNET permission, no foreground service, no wake lock, no polling, no UsageStatsManager.

## Permissions
BIND_ACCESSIBILITY_SERVICE (service), BIND_NOTIFICATION_LISTENER_SERVICE (service), BIND_DEVICE_ADMIN (receiver), USE_BIOMETRIC, VIBRATE, REQUEST_IGNORE_BATTERY_OPTIMIZATIONS, SYSTEM_ALERT_WINDOW only if O12 approved. No INTERNET, no QUERY_ALL_PACKAGES.

## Failure/restart
Service may be killed by the OS: unlock state is lost, which fails safe (apps relock). Protection Status shows when the service is not connected.
