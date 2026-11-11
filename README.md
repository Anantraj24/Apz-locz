# Apz Locz

An offline, ultra-lightweight Android app locker inspired by Samsung One UI design language. Built with Jetpack Compose, Kotlin, and modern Android architecture. 

Apz Locz allows you to lock any application behind a secure PIN (4–8 digits), password, or pattern, with instant fingerprint and system face unlock support.

---

## 🌟 Key Features

- **Lock Any Application:** Choose exactly which user and system apps to lock with a responsive One UI search and toggle list.
- **Biometric Authentication:** Unlock apps seamlessly with fingerprint or Samsung system face unlock, with graceful fallback to your master credential.
- **Flexible Auto-Relock Timing:**
  - *Immediately:* Relocks as soon as you navigate away from the app.
  - *10 seconds / 30 seconds / 1 minute:* Grace period to switch between apps without re-entering credentials.
  - *When phone locks:* Stays unlocked until the device screen turns off or locks.
- **Brute-Force Lockout Protection:** 5 consecutive failed attempts trigger an automated 30-second lockout countdown timer.
- **Notification Privacy Guard:** Intercepts and suppresses sensitive incoming notifications, banners, and message previews from locked applications while they are locked.
- **Uninstall Protection:** Utilizes Android's native `DeviceAdminReceiver` to prevent Apz Locz from being deleted or uninstalled until you verify your credential.
- **Samsung One UI Design System:**
  - True AMOLED black dark mode (`#000000`).
  - Fluid spring animations and One UI motion curves.
  - Accessible 48dp+ touch targets, clear typography scales, and collapsing header scaffolds.
- **100% Offline & Private:** Zero network permissions (`android.permission.INTERNET` is completely absent), zero third-party tracking, zero analytics, and zero external telemetry.
- **Ultra Lightweight:** The production release APK is under 2 MB (~1.96 MB), using zero heavy external UI or image libraries (no Coil, Glide, or Room).

---

## 🔒 Permissions & Privacy Model

Apz Locz operates strictly on-device without remote servers:

| Permission | Purpose | Privacy Guarantee |
|---|---|---|
| `BIND_ACCESSIBILITY_SERVICE` | Detects when a locked app moves to the foreground. | `canRetrieveWindowContent="false"`. No keystrokes, personal data, or screen content are ever read or logged. |
| `BIND_NOTIFICATION_LISTENER_SERVICE` | Dismisses notifications from locked applications. | Dismisses notifications via system key only (`cancelNotification`). No message text, sender, or notification content is ever stored or transmitted. |
| `BIND_DEVICE_ADMIN` | Prevents unauthorized app uninstallation. | Active only while a master credential is set; automatically deactivated when the credential is removed. |
| `USE_BIOMETRIC` / `USE_FINGERPRINT` | Fingerprint and face authentication. | Relies strictly on Android BiometricPrompt; biometric data never leaves secure hardware. |
| `REQUEST_IGNORE_BATTERY_OPTIMIZATIONS` | Ensures continuous app protection. | Keeps the detection service alive on aggressive battery-saving OEM ROMs like Samsung One UI. |

---

## 📱 Samsung One UI Setup Guide

Samsung One UI aggressively optimizes background processes. To ensure uninterrupted protection:

1. **Accessibility Service:**  
   Open `Settings > Accessibility > Installed apps > Apz Locz` and switch **On**.
2. **Notification Access:**  
   Open `Settings > Apps > Special app access > Notification access > Apz Locz` and switch **On**.
3. **Battery Optimization:**  
   Open `Settings > Apps > Apz Locz > Battery` and select **Unrestricted**.
4. **Lock Screen / Recents:**  
   Apz Locz automatically excludes its lock screen from the Recent Apps list (`excludeFromRecents="true"`) and intercepts Back navigation to return directly to the Home screen, preventing bypasses.

---

## ⚠️ Limitations & Security Notice

As an open-source non-root application:
1. **Offline Credential Recovery:**  
   Because Apz Locz is 100% offline and connects to no servers, **there is no remote password reset**. If you forget your credential while Uninstall Protection (Device Admin) is active, uninstallation requires removing Device Admin privileges in Android Safe Mode or device recovery.
2. **Safe Mode:**  
   Like all Android lockers without root or Knox OEM system integration, booting into Android Safe Mode temporarily disables third-party Accessibility services.
3. **Third-Party Launchers:**  
   Apz Locz is optimized for Samsung One UI Home and standard AOSP launchers.

---

## 🛠️ Building & Developing

### Prerequisites
- JDK 17 (e.g. Temurin or JetBrains Runtime 17)
- Android SDK 35 (Android 15) with `minSdk = 26` (Android 8.0 Oreo)

### Build Commands
```bash
# Run pure JVM unit test suites
./gradlew test

# Build debug APK
./gradlew assembleDebug

# Build optimized, R8-shrunk release APK
./gradlew assembleRelease
```

---

## 📜 Architecture & Tech Stack

- **Language:** Kotlin 2.0+
- **UI Toolkit:** Jetpack Compose with custom Samsung One UI design system tokens
- **Architecture:** MVVM + Clean Architecture with manual Dependency Injection (`AppContainer`)
- **Storage:** Jetpack DataStore Preferences (no SQLite / Room overhead)
- **Cryptography:** Android Keystore AES-GCM + PBKDF2WithHmacSHA256 (random salt, zero plaintext storage)
- **App Icons:** Custom in-memory `LruCache` with asynchronous bitmap rendering

---

## 📄 License

This project is licensed under the [MIT License](LICENSE).
