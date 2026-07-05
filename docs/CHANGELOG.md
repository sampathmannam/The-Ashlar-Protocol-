# The Ashlar Protocol — Changelog

> What has actually been built, and its verification state. Newest first.
> Status legend: **✅ built + device-verified** · **🟢 built, compiles, on-device (not fully QA'd)** ·
> **🧪 pure-logic, unit-tested** · **⚠️ known debt**.

---

## [Unreleased] — the current app

Built from a Google AI-Studio scaffold into a full, fully-on-device mental-health practice.
Initial git commit: `99a83db`. Installed and running on device `ZD2232FCR5`.

### Foundation & safety
- 🟢 **Crisis safety layer** — `safety/CrisisDetector.kt` (on-device, fail-toward-help, euphemism-aware)
  + `ui/components/CrisisSupport.kt` (unconditional, tappable hotlines: 988 / Crisis Text Line /
  Samaritans / Find-a-Helpline). Always-on **"NEED HELP?"**; scanning wired into every free-text
  surface (Chamber, journal, initiation). Never gated by degree or onboarding. 🧪 `CrisisDetectorTest`.
- 🟢 **Repositioned** from the template's "tactical operator" framing to a universal *craftsman* voice,
  across all user-facing strings and the app's mentor tone.

### The rite of passage (the core experience)
- 🟢 **Initiation** — `ui/screens/InitiationScreen.kt`: first-run is a solemn 5-step Chamber rite
  (threshold → how-heavy baseline → the weight → intention → crossing in as Entered Apprentice).
  Crisis-scanned; "NEED HELP?" reachable throughout.
- 🟢 **Earned tools** — `ToolsScreen` veils later tools until the degree confers them
  (`Degrees.isUnlocked`). Apprentice: Gauge, Gavel, Level. Fellowcraft: Plumb. Master Mason: Mouth-to-Ear.
- 🟢 **The journey as home** — `BoardScreen`'s "The Path" hero fuses the rough→perfect ashlar with the
  current degree + progress; the stone smooths with **real** progress (`Degrees.journeyProgress`).
  🧪 `DegreesTest`.

### The practices (five real tools)
- 🟢🧪 **The Gauge** — behavioral activation; divide the day into Service / Work / Rest. `GaugeTest`.
- 🟢🧪 **The Plumb** — a real 4-step CBT thought-record (situation → thought → tilt → evidence →
  "squared" reflection composed from the user's own words). `PlumbLineTest`.
- 🟢🧪 **The Level** — a real paced-breathing timer at ~6 breaths/min (0.1 Hz resonance). `BreathPacerTest`.
- 🟢 **The Gavel** — a focus drill (Tamil letter combinations).
- 🟢🧪 **Mouth to Ear** — memorize a chosen principle via progressive cue reduction. `MouthToEarTest`.

### The Chamber & the Lodge (connection)
- 🟢 **Chamber** — two modes: **Let Go** (cathartic release, met by "the Well") and **Keep**
  (meaning-oriented reflections, persisted). 🧪 `ReliefTest`.
- 🟢🧪 **Reach out** — compose a gentle opener, hand it to the phone's own SMS/WhatsApp via a share
  intent (real connection, no server, never gated). `ReachOutTest`.

### Fully on-device (privacy = architecture)
- 🟢 **Removed the paid Gemini daily-briefing** → bundled `tools/DailyWord.kt` (🧪 `DailyWordTest`).
- 🟢 **Dropped the `INTERNET` permission** — the app makes zero network calls; "nothing leaves the
  device" is OS-enforced.
- 🟢 **Removed Firebase / Google-Services / Play-Services** and dead Retrofit/OkHttp/Moshi/Room
  (unused template cruft; cloud dependencies).

### Docs
- ✅ [VISION.md](VISION.md), [MASTER_PLAN.md](MASTER_PLAN.md), [RESEARCH_BASIS.md](RESEARCH_BASIS.md),
  [VERIFICATION.md](VERIFICATION.md), [AUDIT_AND_REDESIGN.md](AUDIT_AND_REDESIGN.md),
  [ARCHITECTURE.md](ARCHITECTURE.md), [BUILD.md](BUILD.md).

### The Honesty Pass (redesign step 1)
- ✅ **Removed all fabricated data from the Board** (device-verified). Deleted `ResilienceChartCard`
  (`Random`) and `WisdomPillar` (hardcoded), and the placeholder Strength/Beauty pillars. Replaced
  with **"The Work So Far"** — an honest card showing *literal counts of real activity* (streak,
  journal notes, thought-records, gauge days, recalls), footer *"no scores, no predictions."* The
  Board now shows only real data.

### ⚠️ Known debt (see [AUDIT_AND_REDESIGN.md](AUDIT_AND_REDESIGN.md))
- ⚠️ Initiation intention/baseline captured but not surfaced; Plumb records not persisted.
  (Redesign steps 2–3: "make it remember" + the honest daily check-in.)
- ⚠️ `com.example` package + `material-icons-extended` (only core icons used) — pre-release cleanups.
- ⚠️ WCAG 1.4.1: gold-as-only-cue for tappable text needs a second cue (palette contrast itself passes).

### Verification state
- **Compiled, installed, launched, no crash** on `ZD2232FCR5`; initiation rite renders; "NEED HELP?"
  present during onboarding.
- **Not yet fully device-QA'd** — the [VERIFICATION.md](VERIFICATION.md) **§1 crisis pass is the
  blocking item** to complete on a real device.
- **Local git only** (commit `99a83db`); **not pushed** to any remote.
