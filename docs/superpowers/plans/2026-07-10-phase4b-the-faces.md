# Phase 4b — The Faces Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:executing-plans. Steps use checkbox (`- [ ]`).

**Goal:** Finish the research-integration dossier (`docs/RESEARCH_INTEGRATION.md` F4/F5/F6/F8) — turn the one engine toward its wellness and addiction *faces*, all on the same forgiving, evidence-honest spine.

**Architecture:** Four tickets, smallest-and-safest first, the high-care addiction track last. Each = pure-logic (TDD) + a thin Compose surface, reusing the established pattern and the Phase-4 pieces (Cornerstone, Practices, Trowel, never-zero stone). On-device, no new deps.

## Global Constraints (dossier §6 — held lines, verbatim)
- Fully on-device; no network/`INTERNET`/backend/telemetry/new-deps.
- No points/willpower-mechanics/punitive-zero-reset/guilt/FOMO. Approach-framed, autonomy-supportive, process-not-outcome.
- **A lapse is never catastrophic** (anti-AVE, Marlatt); never-zero stone; self-compassion on a slip.
- Honest confidence: sleep/rhythm framed **associational** ("linked to"), never causal/clinical; no craving-surveillance (Milyavskaya).
- **Safety (⚠️):** substance addiction is clinical → prominent unshaming §9/West-Gate handoff; every new string swept by `SafetyAudit` mortality **and** language gates; §9 crisis untouched, always on top.
- Package `com.ashlarprotocol`; build `gradle :app:testDebugUnitTest :app:assembleDebug`; device-verify ZD2232FCR5; one branch, PR at end.

**Known interfaces:** `KindStreak.epochDay(millis,tzOff)`, `comebackMessage()`, `TendOutcome.isComeback`; VM `_streakComeback`/`streakComeback` set at the comeback path. `PowerUps.PowerUp(id,title,invite,steps)` + `POWER_UPS` (swept by SafetyAudit). `WhoFive` object pattern (ITEMS/OPTIONS). `Cornerstone.CueKind`, `data/CornerstoneEntry`, `data/Practice`, `Trowel`.

---

### Task P4b.1 — F8: the fresh-start ramp
**Why:** temporal landmarks re-motivate by filing past failure into a "previous period" (Dai, Milkman & Riis 2014). On a comeback, *offer* a clean page rather than resume a diminished count.

**Files:** Modify `tools/KindStreak.kt` (+`freshStartLine`), `KindStreakTest.kt`, `ui/AshlarAppViewModel.kt` (append to comeback), `SafetyAuditTest.kt`.

- [ ] **Step 1 — failing test:** `KindStreak.freshStartLine(mondayEpochDay)` returns a non-blank, non-guilting line; on a non-Monday returns null. (Epoch day 0 = Thursday, so Monday ⇔ `((d%7)+7)%7 == 4`.) Pick a known Monday: epoch day 20642 (2026-07-13 is a Monday — verify with `((20642%7)+7)%7`).
- [ ] **Step 2 — run FAIL.**
- [ ] **Step 3 — implement:**
```kotlin
/** A landmark "clean page" line when [epochDay] is a Monday (a new week) — the fresh-start effect
 *  (Dai et al. 2014). Null on other days. Never loss-framed. */
fun freshStartLine(epochDay: Long): String? {
    val dow = ((epochDay % 7) + 7) % 7      // 0=Thu … 4=Mon
    return if (dow == 4L) "And a new week begins — a clean page. Take up the stone again." else null
}
```
- [ ] **Step 4 — run PASS.**
- [ ] **Step 5 — wire:** in the VM comeback path, `_streakComeback.value = comebackMessage() + (freshStartLine(today)?.let { "\n\n$it" } ?: "")` (use the same epoch-day the tend used). Sweep the new line in SafetyAuditTest (`freshStartLine(20642)!!`). Device-verify not required (needs a comeback+Monday); build+tests+no-crash + honest note.
- [ ] **Step 6 — commit** `feat(faces): fresh-start ramp — a clean page on a new week (Dai 2014)`.

---

### Task P4b.2 — F4: automaticity over counts
**Why:** a habit *is* context-cued automaticity, not frequency (Gardner 2015; Wood & Rünger 2016). The honest progress signal is "it's becoming automatic," not streak length. Gentle, occasional — NOT craving-surveillance.

**Files:** Create `tools/Automaticity.kt` + `AutomaticityTest.kt`; Modify `data/LocalDataStore.kt` (store last reading + day), `ui/AshlarAppViewModel.kt`, `ui/screens/BoardScreen.kt` (a light optional prompt/among the Working), `SafetyAuditTest.kt`.

**Interfaces:** `Automaticity.PROMPT: String`; `Automaticity.LEVELS: List<Level>` where `Level(label, value)` (e.g. "Still takes effort"=0, "Getting easier"=1, "Almost automatic"=2); `Automaticity.reflection(value): String` (non-grading); `Automaticity.isDue(lastDay, today): Boolean` (gentle cadence, ~weekly).

- [ ] **Step 1 — failing tests:** LEVELS non-empty, values distinct & ascending; `reflection` non-blank for each; `isDue(-1, t)`==true (never asked), `isDue(t, t)`==false (asked today), `isDue(t-7, t)`==true.
- [ ] **Step 2 — FAIL → 3 — implement (pure) → 4 — PASS.**
- [ ] **Step 5 — persist + surface:** LocalDataStore `automaticityLevel`/`automaticityDay` (ints); VM flow + `recordAutomaticity(value)`; a small skippable card on the Board when `isDue`, ≤2 taps, framed as *noticing* not grading. Sweep `PROMPT` + `LEVELS` labels + `reflection` outputs.
- [ ] **Step 6 — build + suite green; device-verify** the card renders + a tap records + it stops being due.
- [ ] **Step 7 — commit** `feat(faces): automaticity as the true progress signal (SRHI-lite, gentle)`.

---

### Task P4b.3 — F6: the rhythm anchor
**Why:** sleep-wake **regularity** (not duration) is strongly *associated* with lower depression/anxiety (Windred 2024; Li 2025). Frame associational, target consistency not earliness, never shame a bad night.

**Files:** Create `tools/Rhythm.kt` + `RhythmTest.kt` + `data/RhythmAnchor.kt` (@Serializable, wake+windDown minutesOfDay); Modify `LocalDataStore`, VM, `BoardScreen` (a light card), `SafetyAuditTest.kt`.

**Interfaces:** `data class RhythmAnchor(wakeMinutesOfDay: Int, windDownMinutesOfDay: Int)`; `Rhythm.reflection(anchor): String` (associational, non-clinical, e.g. "A steady rise and wind-down is *linked to* steadier days — no perfect night required."); `Rhythm.SLOTS`/formatting helper `Rhythm.formatTime(minutesOfDay): String`.

- [ ] **Step 1 — failing tests:** `formatTime(6*60)=="6:00 AM"`, `formatTime(22*60+30)=="10:30 PM"`, `formatTime(0)=="12:00 AM"`; `reflection` non-blank and contains no causal verb ("causes"/"cures") — assert it says "linked" not "causes".
- [ ] **Step 2 — FAIL → 3 — implement → 4 — PASS.**
- [ ] **Step 5 — persist + surface:** RhythmAnchor store; VM flow + `setRhythm(wake, windDown)`; a small optional Board card to set/adjust the two times + show the associational reflection; NEVER an alarm, never shame. Sweep the reflection.
- [ ] **Step 6 — build + suite green; device-verify** the card renders + times save.
- [ ] **Step 7 — commit** `feat(faces): rhythm anchor — sleep-wake regularity (associational, non-clinical)`.

---

### Task P4b.4 — F5: the Rough-Edge track (⚠️ the addiction face, high-care)
**Why:** most quit-apps weaponize the abstinence-violation effect with streak-shame; the evidence says the opposite — cue-avoidance > willpower, urge-surfing not suppression (Wegner), lapse-tolerance, replacement, identity, connection (Marlatt; Bowen; Wood). This is the user's headline goal, done safely.

**Files:** Create `tools/RoughEdge.kt` + `RoughEdgeTest.kt` + `data/RoughEdgeEntry.kt` (@Serializable: name, cue, environmentMove, replacement, lapse ledger as List<Long> timestamps, cleanSince), add an urge-surfing PowerUp to `tools/PowerUps.kt`; Create `ui/components/RoughEdge.kt`; Modify `ToolsScreen` (a "Rough Edge" tool, Apprentice-open), `LocalDataStore`, VM, `SafetyAuditTest.kt`.

**Interfaces:**
- `data class RoughEdgeEntry(name, cue, environmentMove, replacement, lapses: List<Long> = emptyList())` @Serializable.
- `RoughEdge.lapseResponse(): String` — anti-AVE: "A slip is data, not a verdict. It doesn't erase the work. Notice the cue, and take the next honest step." (never "failed"/"relapse"/"blew it").
- `RoughEdge.SAFETY_NOTE: String` — "This is a practice, not treatment. If a substance has a grip on you, real help is stronger than any app —" + points to §9/West Gate.
- A PowerUp `id="urge"`, "Ride the urge", steps that surf the craving as a passing wave (Bowen; no suppression).

- [ ] **Step 1 — failing tests** (`RoughEdgeTest.kt`): `lapseResponse()` non-blank and contains none of {"failed","relapse","blew it","ruined","hopeless"}; `RoughEdgeEntry` round-trips with a lapse appended (list grows, never a reset field); the urge PowerUp exists in `PowerUps.byId("urge")` with ≥3 steps; SafetyAudit language+mortality clean over RoughEdge copy + urge steps.
- [ ] **Step 2 — FAIL → 3 — implement pure (RoughEdge + urge PowerUp) → 4 — PASS.**
- [ ] **Step 5 — persist + UI:** RoughEdgeEntry store (single, nullable) + VM flow + `setRoughEdge`/`recordLapse`; `ui/components/RoughEdge.kt` = a calm flow: name the edge + cue → an environment move (reuse `Cornerstone.reduceMoves/addMoves` framing) → a replacement approach-action → the urge-surf pointer → a "a slip is data" section with a **never-zero** "clean days" note that a lapse *adds to the ledger* but never wipes, + the prominent `SAFETY_NOTE` with a tap to crisis/West Gate. Wire as a Tools entry. Sweep all copy.
- [ ] **Step 6 — build + suite green; device-verify** the tool opens, all sections render, the safety note + crisis reachable, no crash. Screenshot.
- [ ] **Step 7 — commit** `feat(faces): the Rough-Edge track — anti-AVE, urge-surfing, §9-handoff (F5)`.

---

## Self-Review
- Coverage: F8→P4b.1, F4→P4b.2, F6→P4b.3, F5→P4b.4 (dossier §4). Completes the dossier's feature set.
- Held lines: no deps/network; anti-AVE everywhere; associational sleep framing; no craving-surveillance; §9 handoff in F5; every string swept by both gates.
- Types consistent: `freshStartLine`, `Automaticity.{PROMPT,LEVELS,reflection,isDue}`, `RhythmAnchor`/`Rhythm.{reflection,formatTime}`, `RoughEdgeEntry`/`RoughEdge.{lapseResponse,SAFETY_NOTE}`, urge PowerUp id="urge".

## Execution
Inline via **executing-plans** in worktree `feat/phase4b-faces` off `origin/main`; TDD each; green after each; device-verify; one branch, PR at end; then **finishing-a-development-branch**.
