# What the Stone Remembers — Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:executing-plans. Steps use checkbox (`- [ ]`).

**Goal:** Ship the "AI phase" as a deterministic scribe-and-mirror: reflect the user's own on-device data back to them — faithful facts + a few guarded noticings — zero-hallucination by construction. Design: `docs/superpowers/specs/2026-07-10-what-the-stone-remembers-design.md`.

**Architecture:** A pure engine (`tools/Reflections.kt`) that maps a plain snapshot of already-persisted data to a typed list of reflections (TDD), and a pull-only Board surface that renders them. No model, no network, no new deps.

## Global Constraints
- Fully on-device; zero-cost; no generation/model/network. Zero-hallucination: the engine only surfaces what is literally in its input. Noticings are sample-floor-gated and **never causal**. Every reflection carries provenance.
- Honest confidence; approach-framed; no shame/grit/willpower (F7 language gate); every new string swept by SafetyAudit (mortality + language). §9 untouched, model-independent.
- Package `com.ashlarprotocol`; build `gradle :app:testDebugUnitTest :app:assembleDebug`; device-verify ZD2232FCR5; one branch, PR at end.

**Verified data shapes:** `WhoFiveResult(id, score, timestamp)`, `RoughEdgeEntry(name, cue, environmentMove, replacement, lapses: List<Long>)`, `CornerstoneEntry(behavior, cueKind, cueDetail, move)`, `Strength(display, virtue)`, `RhythmAnchor(wakeMinutesOfDay, windDownMinutesOfDay)`. Per-day arrival history is NOT persisted (only today's `readiness`) → cross-dimension noticings are out of scope.

---

### Task M1 — The reflection engine (pure, TDD)
**Files:** Create `app/src/main/java/com/ashlarprotocol/tools/Reflections.kt` + `app/src/test/java/com/ashlarprotocol/ReflectionsTest.kt`.

**Interfaces produced:**
- `enum class ReflectionKind { FACT, NOTICING }`
- `data class Reflection(val kind: ReflectionKind, val text: String, val provenance: String)`
- `data class ReflectionInput(daysTended: Int = 0, currentRun: Int = 0, graceRemaining: Int = 0, degreeDisplay: String = "", intention: String = "", practicesCount: Int = 0, journalCount: Int = 0, plumbCount: Int = 0, gaugeDays: Int = 0, recallCount: Int = 0, keptReflectionsCount: Int = 0, signatureStrengths: List<String> = emptyList(), automaticityLevel: Int = -1, rhythmWake: String? = null, rhythmWindDown: String? = null, roughEdgeName: String? = null, roughEdgeLapseDays: List<Long> = emptyList(), todayEpochDay: Long = 0, whoFiveScores: List<Int> = emptyList(), whoFiveSpanDays: Int = 0)` — all primitives, precomputed at the VM boundary (keeps the engine pure/time-free).
- `Reflections.reflect(input: ReflectionInput): List<Reflection>` — FACTs first, then NOTICINGs.
- `Reflections.allSampleText(): List<String>` — representative copy for the safety sweep.

**Facts** (each emitted only when its data exists): tended days (+run/grace), degree, intention (+"N practices point that way" if practicesCount>0), latest WHO-5, rough-edge (name + "N slips, each logged and forgiven" + "M days clear"), cornerstone move, a combined counts line (journal/plumb/gauge/recall/kept), signature strengths, automaticity, rhythm.

**Noticings** (guarded): (1) WHO-5 direction — only if `whoFiveScores.size >= 2 && whoFiveSpanDays >= 14`; (2) rough-edge lapse day-pattern — only if `roughEdgeLapseDays.size >= 4` and the weekend/weekday split is clear (≥60%); (3) longest clear stretch from `roughEdgeLapseDays`+`todayEpochDay`. Day-of-week from an epoch day: `((d % 7)+7)%7` (0=Thu … 2=Sat, 3=Sun weekend, 4=Mon).

- [ ] **Step 1 — failing tests** (`ReflectionsTest.kt`), covering:
```kotlin
// empty input → an honest empty-ish result (no fabricated facts)
@Test fun emptyInputProducesNoFalseFacts() {
    val r = Reflections.reflect(ReflectionInput())
    assertTrue(r.none { it.text.contains("tended") })   // nothing tended yet → no tended fact
}
@Test fun tendedDaysBecomeAFact() {
    val r = Reflections.reflect(ReflectionInput(daysTended = 12, currentRun = 5, graceRemaining = 2))
    val f = r.first { it.text.contains("12") }
    assertEquals(ReflectionKind.FACT, f.kind); assertTrue(f.provenance.isNotBlank())
}
@Test fun whoFiveNoticingNeedsEnoughSpan() {
    // 2 checks but only 3 days apart → below the 14-day floor → no WHO-5 noticing
    assertTrue(Reflections.reflect(ReflectionInput(whoFiveScores = listOf(50,70), whoFiveSpanDays = 3))
        .none { it.kind == ReflectionKind.NOTICING && it.text.contains("moved") })
    // spanning 21 days → a hedged noticing appears
    val n = Reflections.reflect(ReflectionInput(whoFiveScores = listOf(50,70), whoFiveSpanDays = 21))
        .first { it.kind == ReflectionKind.NOTICING && it.text.contains("moved") }
    assertTrue(n.text.contains("worth noticing"))
}
@Test fun lapseDayPatternNeedsFourSlips() {
    // 3 slips → below floor → silent
    assertTrue(Reflections.reflect(ReflectionInput(roughEdgeLapseDays = listOf(2,9,16), todayEpochDay = 20))
        .none { it.text.contains("tended to fall") })
    // 4 weekend slips (epoch days 2,3,9,10 = Sat/Sun) → a hedged, non-causal noticing
    val n = Reflections.reflect(ReflectionInput(roughEdgeName = "scrolling", roughEdgeLapseDays = listOf(2,3,9,10), todayEpochDay = 14))
        .first { it.text.contains("tended to fall") }
    assertTrue(n.text.contains("worth noticing"))
}
@Test fun noNoticingIsEverCausal() {
    val big = ReflectionInput(whoFiveScores = listOf(40,80), whoFiveSpanDays = 30,
        roughEdgeName = "x", roughEdgeLapseDays = listOf(2,3,9,10,16), todayEpochDay = 30)
    Reflections.reflect(big).filter { it.kind == ReflectionKind.NOTICING }.forEach { n ->
        listOf("causes","because","proves"," will ","makes you").forEach {
            assertFalse("noticings must never claim cause ($it): ${n.text}", n.text.lowercase().contains(it))
        }
    }
}
```
- [ ] **Step 2 — run FAIL → 3 — implement `Reflections.kt` (KDoc: scribe-and-mirror, zero-hallucination, honesty guardrails) → 4 — PASS.**
- [ ] **Step 5 — commit** `feat(mirror): the reflection engine — faithful scribe + guarded noticings (pure)`.

---

### Task M2 — The surface + wiring + sweep
**Files:** Create `app/src/main/java/com/ashlarprotocol/ui/components/StoneRemembers.kt`; Modify `ui/AshlarAppViewModel.kt` (a `buildReflectionInput()` / `reflections: StateFlow<List<Reflection>>` combining the existing flows + precomputing epoch-days/spans), `ui/screens/BoardScreen.kt` (a quiet entry line under the stone → opens the view), `SafetyAuditTest.kt` (sweep `Reflections.allSampleText()`).

**Interfaces consumed:** all existing VM flows (briefingStreak, streakState/grace, currentDegree, intention, whoFiveResults, roughEdge, cornerstone, practices, aarEntries, plumbSessions/records, gaugeDaysComplete, recallSessions, reflections, signatureStrengths, automaticity, rhythm). Compute `todayEpochDay` + WHO-5 span + lapse epoch-days via `KindStreak.epochDay(...)` at the VM boundary.

- [ ] **Step 1** — VM: expose `reflections: StateFlow<List<Reflection>>` = combine(the flows) → `Reflections.reflect(buildInput(...))`. (Use nested combine or a single `combine(listOf(...))` since there are many flows; precompute primitives inside.)
- [ ] **Step 2** — `StoneRemembers.kt`: a `StoneRemembersView(reflections)` composable — a titled "WHAT THE STONE REMEMBERS" scroll; FACTs grouped first; a divider; NOTICINGs beneath, visibly hedged; empty-early state "The stone is still learning your shape — tend it a while, and it will remember." Reuse the card idiom.
- [ ] **Step 3** — Board: a quiet entry (a small "WHAT THE STONE REMEMBERS →" line under the stone/degree card) that toggles the view (a full-screen overlay or a Board section that expands). Keep the 3-tab IA.
- [ ] **Step 4** — sweep `Reflections.allSampleText()` in SafetyAuditTest.
- [ ] **Step 5** — build + full suite green; **device-verify** the entry opens the view, facts render (the device has real data — days tended, rough edge, etc.), no crash. Screenshot.
- [ ] **Step 6 — commit** `feat(mirror): What the Stone Remembers — the pull-only reflective surface`.

---

## Self-Review
- Zero-hallucination: engine is pure over an explicit input; only literal data becomes facts; noticings are floor-gated + non-causal (tested).
- Scope honest: only computable noticings (WHO-5 direction, lapse day-pattern, longest clear stretch); cross-dimension co-occurrence deferred (no per-day history).
- Held lines: no model/network/deps; safety model-independent; every string swept by both gates; pull-only (no push/mistiming).
- Types consistent: `Reflection`/`ReflectionKind`/`ReflectionInput`/`reflect`/`allSampleText` used the same in M1 and M2.

## Execution
Inline via **executing-plans** in worktree `feat/what-stone-remembers` off `origin/main`; TDD M1; green + device-verify M2; one branch, PR at end; then **finishing-a-development-branch**.
