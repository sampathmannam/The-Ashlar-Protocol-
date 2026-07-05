# The Ashlar Protocol — Architecture

> How the code is organized and why. Read [`VISION.md`](VISION.md) for the *why of the product*;
> this is the *how of the code*. ~3,950 lines of Kotlin, 26 source files, fully on-device.

**Stack:** Native Android — Kotlin, Jetpack Compose, Material 3, DataStore (local prefs),
WorkManager (reminders). **No network, no `INTERNET` permission, no accounts, no backend, no keys.**
`minSdk 24`, `compileSdk 36.1`, AGP 9.1.1, package `com.example` *(rename to a real id before release)*.

---

## The one idea: pure logic in `tools/`, UI just renders it

The app's "brain" is a set of **pure, UI-free, deterministic Kotlin objects** in `com.example.tools`.
They take inputs and return values — no Android types, no network, no time-of-day randomness
(where avoidable). This is deliberate:

- **Testable** — every one has a JUnit suite that runs on the JVM in milliseconds (no emulator).
- **Honest** — the practices are logic you can read and verify, not a black box.
- **On-device by construction** — pure functions can't phone home.

The Compose UI is a thin layer that gathers input and renders what these objects return. When you
add a feature, put the *logic* here first (test-first), then wire a screen to it.

---

## Package map

```
com.example
├─ MainActivity.kt            App entry; hosts TracingBoardApp.
│
├─ tools/                     THE BRAIN — pure, tested, UI-free logic.
│   ├─ Degrees.kt             Progression: score → degree, journeyProgress, isUnlocked (tool gating).
│   ├─ PlumbLine.kt           CBT thought-record: TILTS (distortions) + composeSquaredReflection().
│   ├─ TwentyFourInchGauge.kt Behavioral activation: DayPart, GaugeItem, completion, isDayComplete.
│   ├─ BreathPacer.kt         Paced breathing: BreathPattern.RESONANCE (~6/min), stateAt().
│   ├─ MouthToEar.kt          Memory practice: mask()/scoreRecall()/isHeld() + DEFAULT_PRINCIPLES.
│   ├─ DailyWord.kt           Bundled daily reflections (replaced the paid Gemini call), wordAt().
│   ├─ Relief.kt              "The Well" — words of relief shown after Chamber release, reliefAt().
│   └─ ReachOut.kt            Opener lines for reaching a trusted person, openerAt().
│
├─ safety/
│   └─ CrisisDetector.kt      On-device, fail-safe (fail-toward-help) euphemism-aware crisis scan.
│
├─ data/                      Persistence — all local (DataStore Preferences), nothing leaves device.
│   ├─ LocalDataStore.kt      Single source of truth: streak, journal, practice counts, initiation,
│   │                          reflections, etc. (JSON via kotlinx.serialization for lists).
│   ├─ AarEntry.kt            @Serializable journal entry (Board's After-Action journal).
│   └─ Reflection.kt          @Serializable kept-reflection (Chamber "keep" mode).
│
├─ ui/
│   ├─ AshlarAppViewModel.kt  AndroidViewModel: exposes DataStore as StateFlows; the only place UI
│   │                          talks to persistence. Holds the daily-word index + record*() methods.
│   ├─ TracingBoardApp.kt     Root composable: crisis provider + dialog, the initiation gate
│   │                          (when(initiated){…}), the 3-tab nav, computes current Degree for Tools.
│   ├─ screens/
│   │   ├─ InitiationScreen.kt First-run rite (5 steps); captures intention + baseline; crisis-scanned.
│   │   ├─ BoardScreen.kt      Home = "The Path" hero (ashlar + degree) + journal + charts*.
│   │   ├─ ChamberScreen.kt    Let Go (release → the Well) / Keep (meaning) + reach-out; crisis-scanned.
│   │   └─ ToolsScreen.kt      The working tools, degree-gated (earned, not listed).
│   ├─ components/
│   │   ├─ CrisisSupport.kt    CrisisController + LocalCrisisController + the unconditional help dialog.
│   │   ├─ ResilienceChartCard.kt   ⚠️ FAKE DATA (Random) — flagged for removal (see AUDIT_AND_REDESIGN).
│   │   └─ WisdomPillar.kt          ⚠️ FAKE DATA (hardcoded) — flagged for removal.
│   └─ theme/                  Color / Type / Theme (charcoal · gold · silver, solemn dark).
│
└─ worker/
    └─ ReminderWorker.kt       WorkManager daily reminder (local notification; no network).
```

---

## Load-bearing design decisions

### 1. Fully on-device (privacy = architecture)
No `INTERNET` permission is declared (see `AndroidManifest.xml`). The app *cannot* make network
calls — "nothing leaves the device" is enforced by the OS, not just promised. All state lives in
DataStore. The daily word is bundled (`DailyWord.kt`), not fetched. Crisis help opens the phone's
*own* dialer/SMS/browser via intents (which need no `INTERNET` permission on this app).

### 2. The crisis pathway is model-independent and fail-safe (see [VISION §8](VISION.md))
`safety/CrisisDetector.detect()` is pure local string-matching that **returns `true` on any
exception** (fail-toward-help) and catches euphemisms, not just keywords. `CrisisSupport` hoists a
`CrisisController` via `LocalCrisisController` so any screen can surface help; the dialog renders once
at the app root, above everything. Every free-text surface (Chamber, journal, initiation) calls
`crisisController.scan(text)`. Help is **never gated** by degree or onboarding.

### 3. The rite of passage is the information architecture
`TracingBoardApp` gates first run on `initiated` (null = loading, avoids cold-start flash) → the
**InitiationScreen** rite → the app. The **Degrees** engine (`tools/Degrees.kt`) computes the current
degree from persisted practice counts; `ToolsScreen` uses `Degrees.isUnlocked()` to veil later tools
until earned; `BoardScreen` uses `journeyProgress()` to smooth the ashlar with *real* progress.

### 4. One persistence path
UI never touches DataStore directly — only `AshlarAppViewModel` does, exposing everything as
`StateFlow`. Lists (journal, reflections) are JSON-encoded via `kotlinx.serialization`. Practice
completions flow: tool UI → `viewModel.record*()` → `LocalDataStore.increment*()` → Flow → degree.

### 5. Known debt (see [AUDIT_AND_REDESIGN.md](AUDIT_AND_REDESIGN.md))
`ResilienceChartCard` and `WisdomPillar` render **fabricated** data and are flagged for removal in
the "Honesty Pass." The initiation intention/baseline are captured but not yet surfaced. Plumb
thought-records are not yet persisted. `com.example` package + `material-icons-extended` (only core
icons used) are pre-release cleanups.

---

## Tests

Pure-logic suites in `app/src/test/` run on the JVM (`./gradlew testDebugUnitTest`), no device needed:

`CrisisDetectorTest` · `PlumbLineTest` · `GaugeTest` · `MouthToEarTest` · `DegreesTest` ·
`BreathPacerTest` · `DailyWordTest` · `ReliefTest` · `ReachOutTest` — **9 suites** covering the brain.
(`Example*` / `Greeting*` are leftover AI-Studio template tests.)

UI is verified on-device against [`VERIFICATION.md`](VERIFICATION.md) — the **§1 crisis pass is
blocking**. See [`BUILD.md`](BUILD.md) for how to build, install, and run.
