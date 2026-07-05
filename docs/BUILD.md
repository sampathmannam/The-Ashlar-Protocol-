# The Ashlar Protocol — Build & Install

> How to build the debug APK and install it to a device. This captures the **hard-won recipe** from
> the first successful build (2026-07-05) so it's never a fight again. Most of the friction was
> environment (a flaky VPN'd network + AI-Studio template cruft), not the code.

**Toolchain used (macOS):**
- **JDK 21** — Android Studio's bundled JBR: `/Applications/Android Studio.app/Contents/jbr/Contents/Home`
- **Android SDK** — `~/Library/Android/sdk` (contains `platform-tools/adb`)
- **Gradle 9.4.1** — required by AGP 9.1.1 / `compileSdk 36.1` (Gradle 8.x is too old)

The easiest path is **Android Studio**: open the project, let it sync, press Run. The notes below
are for the **command line** and to explain the gotchas.

---

## Prerequisites the snapshot is missing

This project may arrive as a source snapshot **without** these machine-specific / generated files.
Create them before building (all are `.gitignore`d):

1. **`local.properties`** at the repo root, pointing at your SDK:
   ```
   sdk.dir=/Users/<you>/Library/Android/sdk
   ```
2. **`debug.keystore`** at the repo root (the debug build type is configured to use it). Generate it
   with the exact alias/passwords the build expects:
   ```bash
   keytool -genkeypair -keystore debug.keystore \
     -storepass android -keypass android -alias androiddebugkey \
     -keyalg RSA -keysize 2048 -validity 10000 \
     -dname "CN=Android Debug,O=Android,C=US"
   ```
   *(Android Studio normally auto-manages a debug keystore; this project pins a custom `debugConfig`
   to `${rootDir}/debug.keystore`, so it must exist.)*
3. **`.env.example`** must exist (it's committed). The Secrets Gradle plugin reads it as the default
   properties file. It only holds a placeholder — the app needs **no** real key.

There is **no Gradle wrapper** (`gradlew`) in the snapshot. Use a system/cached Gradle 9.4.1, or run
`gradle wrapper --gradle-version 9.4.1` once to generate it.

---

## Build & install (command line)

```bash
cd The-Ashlar-Protocol-
export JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"
export ANDROID_HOME="$HOME/Library/Android/sdk"

# Build the debug APK (first build downloads dependencies — needs network).
gradle :app:assembleDebug --no-daemon --no-configuration-cache --console=plain

# Install to a connected device (list devices: adb devices).
"$ANDROID_HOME/platform-tools/adb" -s <DEVICE_ID> install -r \
  app/build/outputs/apk/debug/app-debug.apk

# Run the unit tests (JVM, no device):
gradle :app:testDebugUnitTest
```

APK lands at `app/build/outputs/apk/debug/app-debug.apk` (~20 MB). A clean incremental build is ~17 s.

---

## Gotchas (and their fixes)

### The network flakiness (the big one)
On a VPN'd / IPv6-quirky network, `dl.google.com` intermittently returns **"No route to host"** on
individual artifacts, failing the whole build — even though most artifacts download fine. Fixes,
already applied to the repo:

- **`settings.gradle.kts` scopes `google()` to Android-only groups**, so Kotlin/kotlinx/JUnit/etc.
  resolve from **Maven Central** (reliable) instead of the flaky Google mirror. Keep it that way.
- If a build still fails on a dropped download, **just re-run it** — Gradle caches every successful
  artifact, so each retry gets further and eventually completes.

### Firebase / Google-Services was removed (do not re-add)
The AI-Studio template pulled in **Firebase AI, App Check, Google-Services, and Play-Services** —
all unused, all cloud, and the main source of the download hang. They were removed from
`app/build.gradle.kts` and `build.gradle.kts`. Re-adding them would break the on-device guarantee
(and the build). The app is intentionally network-free.

### `com.example` package
Still the placeholder namespace. Rename to a real application id before any public release (this is a
larger, compiler-in-the-loop change — do it in Android Studio's refactor).

---

## Verify on device

After install, work through [`VERIFICATION.md`](VERIFICATION.md). **The §1 crisis pathway is
blocking** — confirm the help dialog fires on a euphemism and the hotlines dial, on a real device,
before trusting a build. Quick smoke check:

```bash
adb -s <DEVICE_ID> shell monkey -p com.aistudio.ashlarprotocol.plxnm -c android.intent.category.LAUNCHER 1
adb -s <DEVICE_ID> logcat -d | grep -iE "FATAL|AndroidRuntime"   # should be empty
```
