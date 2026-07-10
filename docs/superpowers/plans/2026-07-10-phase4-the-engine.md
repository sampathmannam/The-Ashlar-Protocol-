# Phase 4 — The Engine (sub-project 1) Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax.

**Goal:** Close the highest-value gaps from the research dossier — make discipline run on *environment + cued habit + forgiving recovery + honest altitude*, not willpower or achievements.

**Architecture:** Four self-contained tickets, each pure-logic (TDD) + a thin Compose surface, integrated into existing screens. Derived from `docs/RESEARCH_INTEGRATION.md` §4 (features F3, F1, F2, F7). Fully on-device; reuses the established `tools/` (pure) + `data/` + VM StateFlow + Compose pattern.

**Tech Stack:** Kotlin, Jetpack Compose, Material 3, DataStore, JUnit. Native Android, **no new dependencies**.

## Global Constraints (from the dossier §6 — held lines)
- **Fully on-device.** No network, no `INTERNET`, no backend, no telemetry, no new deps.
- **No points/badges/currency; no willpower mechanics; no punitive zero-reset; no guilt/shame/FOMO copy.**
- **Approach-framed, autonomy-supportive** (choice + brief why + non-judgment); **process not outcome**.
- **Honest confidence:** no causal overclaim on garnish practices; no "grit/toughness/push-through" language; identity is framing not a claimed lever.
- **Safety:** every new user-facing string joins `SafetyAuditTest`; §9 crisis untouched and always on top.
- Package `com.ashlarprotocol`; build `gradle :app:testDebugUnitTest :app:assembleDebug --no-daemon --no-configuration-cache` (JBR JAVA_HOME, sdk ANDROID_HOME); device-verify on ZD2232FCR5; PR-per-ticket.

**Known interfaces (verified):** `KindStreak.MAX_GRACE=2`; `StreakState(daysTended, currentRun, graceRemaining, lastTendedDay)`; `TendOutcome(state, alreadyTendedToday, graceUsed, isComeback)`; `KindStreak.tend/comebackMessage/stoneProgress`. `tools/PracticeAuthoring` (isAvoidanceFramed/composePlan/canSave), `data/Practice(anchor, action, reminderMinutesOfDay, facet?)`. `SafetyAudit.mortalityViolations(text): List<String>`; `SafetyAuditTest` sweeps a `corpus` map.

---

### Task P4.1 — F3: visible, scarce grace *reserves* (the stone's held breath)

**Why:** Sharif & Shu (2017/2019) — a reserve *framed as scarce* raises post-miss rebound (~55% vs ~37%) because people protect it; Ashlar's grace is currently *silent*. Make it visible + gently celebrated. `docs/RESEARCH_INTEGRATION.md` F3.

**Files:**
- Modify: `app/src/main/java/com/ashlarprotocol/tools/KindStreak.kt` (add display/message helpers)
- Test: `app/src/test/java/com/ashlarprotocol/KindStreakTest.kt` (add cases)
- Modify: `app/src/main/java/com/ashlarprotocol/ui/AshlarAppViewModel.kt` (expose grace) + `ui/screens/BoardScreen.kt` (show reserve on the stone card) + `SafetyAuditTest.kt` (sweep new copy)

**Interfaces produced:**
- `KindStreak.graceLabel(graceRemaining: Int): String` — e.g. "2 grace held" / "1 grace held" / "grace spent — the stone still holds".
- `KindStreak.graceMessage(graceUsed: Int): String?` — null when none used; else a warm "the stone held for you" line (no guilt).

- [ ] **Step 1 — failing tests** (append to `KindStreakTest.kt`):
```kotlin
@Test fun graceLabelReflectsReserve() {
    assertEquals("2 grace held", KindStreak.graceLabel(2))
    assertEquals("1 grace held", KindStreak.graceLabel(1))
    assertEquals("grace spent — the stone still holds", KindStreak.graceLabel(0))
}
@Test fun graceMessageOnlyWhenSpent_andNeverGuilts() {
    assertNull(KindStreak.graceMessage(0))
    val m = KindStreak.graceMessage(1)!!
    assertTrue(m.isNotBlank())
    listOf("lost","broke","fail","should","behind").forEach { assertFalse(m.lowercase().contains(it)) }
}
```
- [ ] **Step 2 — run, expect FAIL.**
- [ ] **Step 3 — implement** in `KindStreak.kt`:
```kotlin
/** A short, protectable label for the grace reserve — scarce by design (Sharif & Shu). */
fun graceLabel(graceRemaining: Int): String = when (graceRemaining.coerceIn(0, MAX_GRACE)) {
    0 -> "grace spent — the stone still holds"
    1 -> "1 grace held"
    else -> "$graceRemaining grace held"
}
/** Warm, non-guilting note when a tending spent grace to keep the run alive; null if none. */
fun graceMessage(graceUsed: Int): String? =
    if (graceUsed <= 0) null
    else "The stone held for you — a day of grace kept the run. They replenish as you return."
```
- [ ] **Step 4 — run, expect PASS.**
- [ ] **Step 5 — surface it:** in `AshlarAppViewModel` expose `graceRemaining: StateFlow<Int>` from the streak state; on the Board stone card (`TracingBoardVisual` area in `BoardScreen`) render a small, quiet line `KindStreak.graceLabel(grace)` beneath "N DAYS TENDED" — visible, scarce, never a warning. Add both helper strings' outputs to the `SafetyAuditTest` corpus (`"KindStreak.grace" to listOf(KindStreak.graceLabel(0), KindStreak.graceLabel(1), KindStreak.graceMessage(1)!!)`).
- [ ] **Step 6 — build + full suite green; device-verify** the grace line shows on the Board.
- [ ] **Step 7 — commit** `feat(engine): visible scarce grace reserves on the stone (Sharif & Shu)`.

---

### Task P4.2 — F1: The Cornerstone (environment & friction design) *(flagship)*

**Why:** the highest-leverage, best-evidenced lever (Duckworth/Gendler/Gross 2016 — intervene early, not at willpower; Wood 2019 friction) and totally absent (0 files). Self-directed only (population-nudge evidence is contested — Maier 2022). F1.

**Files:**
- Create: `app/src/main/java/com/ashlarprotocol/tools/Cornerstone.kt` (pure: cue kinds + friction moves)
- Test: `app/src/test/java/com/ashlarprotocol/CornerstoneTest.kt`
- Create: `app/src/main/java/com/ashlarprotocol/ui/components/Cornerstone.kt` (the tool sheet)
- Modify: `ui/screens/ToolsScreen.kt` (add "The Cornerstone" tool, Apprentice-open) + `data/LocalDataStore.kt` (persist one cornerstone) + `SafetyAuditTest.kt`

**Interfaces produced:**
- `enum class CueKind(display)` { TIME, PLACE, AFTER_ACTION, OBJECT } ; `enum class FrictionMove(display, kind)` where kind ∈ {REDUCE, ADD} — a curated set of concrete self-directed moves (REDUCE: "lay it out the night before", "keep it in view", "one tap away"; ADD: "put it in another room", "delete the shortcut", "add a sign-in step"). `Cornerstone.reduceMoves()`, `Cornerstone.addMoves()`, `Cornerstone.allText()`.
- `data class CornerstoneEntry(behavior, cueKind, cueDetail, move)` @Serializable.

- [ ] **Step 1 — failing tests** (`CornerstoneTest.kt`): both move-lists non-empty; every REDUCE move has kind REDUCE and every ADD move kind ADD; `allText()` contains every move's display; enums cover time/place/after/object.
- [ ] **Step 2 — run, expect FAIL.**
- [ ] **Step 3 — implement** `Cornerstone.kt` (pure data + the curated moves; KDoc cites Wood 2019 + Duckworth/Gendler/Gross 2016 + the self-nudge/Maier caveat).
- [ ] **Step 4 — run, expect PASS.**
- [ ] **Step 5 — UI + persistence:** `CornerstoneEntry` @Serializable + a single-value store in `LocalDataStore` (`cornerstone` string key, JSON) mirroring the WhoFive pattern; VM `cornerstone: StateFlow<CornerstoneEntry?>` + `setCornerstone(...)`. `ui/components/Cornerstone.kt` = a calm sheet: name the behavior → pick a cue (CueKind + detail) → pick one friction move (reduce for a good habit / add for a bad one) → save. Self-directed copy ("engineer *your* room"), never "we'll nudge you". Add to `ToolsScreen` as "The Cornerstone — square your surroundings", `Degree.ENTERED_APPRENTICE` (open from day one). Sweep `Cornerstone.allText()` in SafetyAuditTest.
- [ ] **Step 6 — build + suite green; device-verify** the Cornerstone tool opens, a plan saves and renders back.
- [ ] **Step 7 — commit** `feat(engine): the Cornerstone — self-directed environment & friction design`.

---

### Task P4.3 — F2: implementation intentions elevated (cue-explicit, downstream of commitment)

**Why:** if-then plans are d=0.65 but only *amplify a committed goal* (Gollwitzer & Sheeran 2006 boundary) and are cue-mediated (Webb & Sheeran 2007). Practices already scaffold "AFTER [anchor], I will [action]"; make the cue explicit and require a committed intention first. F2.

**Files:**
- Modify: `data/Practice.kt` (add optional `cueKind`/link to value) + `tools/PracticeAuthoring.kt` (cue helper) + `ui/components/PracticeAuthoring.kt` (cue picker; gate on intention) + `ui/AshlarAppViewModel.kt` + `ui/screens/BoardScreen.kt` (PracticesCard placement) + `SafetyAuditTest.kt`
- Test: `app/src/test/java/com/ashlarprotocol/PracticeAuthoringTest.kt` (add cases)

**Interfaces produced:**
- `PracticeAuthoring.requiresIntentionFirst(intention: String): Boolean` — true when no committed intention/value is set (route the user to set one before authoring a practice).
- `Practice` gains `cueKind: String? = null` (back-compat nullable; reuse `Cornerstone.CueKind` names) — the explicit cue behind the anchor.

- [ ] **Step 1 — failing tests** (`PracticeAuthoringTest.kt`): `requiresIntentionFirst("")==true`, `requiresIntentionFirst("steadier days")==false`; existing approach-guard tests still pass; `Practice` with `cueKind` serializes round-trip.
- [ ] **Step 2 — run, expect FAIL.**
- [ ] **Step 3 — implement** `requiresIntentionFirst` (pure) + add nullable `cueKind` to `Practice` (default null, back-compat).
- [ ] **Step 4 — run, expect PASS.**
- [ ] **Step 5 — UI:** in the Practice authoring dialog, if `requiresIntentionFirst(intention)` → show a gentle "first, name what you're working toward" that routes to the Square/intention, *before* the if-then fields. Add an optional cue-kind pill row (time/place/after/object) that frames the anchor. Keep the existing approach-framing guard. Copy stays humble ("a cue to make it automatic"). Sweep any new strings.
- [ ] **Step 6 — build + suite green; device-verify** authoring gates on intention and the cue picker renders.
- [ ] **Step 7 — commit** `feat(engine): implementation intentions — explicit cue + downstream of commitment`.

---

### Task P4.4 — F7: the altitude & honesty pass

**Why:** the evidence ranking + SDT (guilt→dropout) + honest-confidence copy as a trust moat. Elevate connection + behavioral activation; demote gratitude/mindfulness; kill "grit" language; keep identity as framing. F7.

**Files:**
- Modify: `tools/SafetyAudit.kt` (add a *language* check) + `SafetyAuditTest.kt` (enforce it over all copy) + placement/copy edits in `ui/screens/BoardScreen.kt`, `ui/screens/ChamberScreen.kt`, `ui/components/PowerUps.kt` as needed.
- Test: `SafetyAuditTest.kt`

**Interfaces produced:**
- `SafetyAudit.languageViolations(text: String): List<String>` — flags forbidden framing tokens: `grit`, `toughen`, `push through`, `no excuses`, `willpower`, `discipline yourself`, `earn`, `unlock reward`, `don't break` — the anti-patterns from the dossier.

- [ ] **Step 1 — failing tests:** `languageViolations("Toughen up and push through")` contains `toughen`+`push through`; `languageViolations("Tend the stone")` is empty; a corpus test feeds **every** Phase-1..4 copy surface through `languageViolations` and asserts clean.
- [ ] **Step 2 — run, expect FAIL** (either the fn is missing or a real surface trips it → then fix the *copy*).
- [ ] **Step 3 — implement** `languageViolations` (mirror `mortalityViolations`, case-insensitive substring set).
- [ ] **Step 4 — run; fix any copy that trips it** (there should be little/none — Ashlar was built clean); re-run to PASS.
- [ ] **Step 5 — altitude placement:** ensure the West Gate (connection) and the Gauge (behavioral activation) read as *headline* daily invitations (a one-line elevation on the Board pointing to them), and the gratitude Power-Up stays a plain optional lifter (no elevation). No mechanic changes — copy/placement only.
- [ ] **Step 6 — build + suite green; device-verify** no regressions; the language gate is green.
- [ ] **Step 7 — commit** `feat(engine): altitude & honesty pass — language gate + elevate connection/activation`.

---

## Self-Review
- **Coverage:** F3→P4.1, F1→P4.2, F2→P4.3, F7→P4.4 (dossier §4). F4/F5/F6/F8 are the explicit next sub-project (Phase 4b), not this plan.
- **No placeholders:** pure-logic tickets (P4.1, P4.4, and the pure parts of P4.2/P4.3) have real test + impl code; UI steps name exact files + the precise surface + acceptance (filled during execution against the read files, as in Phases 1–3).
- **Types consistent:** `graceLabel/graceMessage`, `CueKind/FrictionMove/CornerstoneEntry`, `requiresIntentionFirst`, `languageViolations` used consistently; `Practice.cueKind` nullable/back-compat.
- **Held lines:** no new deps/network; no points/willpower/punitive mechanics; every new string swept by SafetyAudit (mortality **and** language); §9 untouched.

## Execution
Inline via **executing-plans** in worktree `feat/phase4-engine` off `origin/main`; TDD each; `:app:testDebugUnitTest :app:assembleDebug` green after each; device-verify on ZD2232FCR5; **PR-per-ticket** (merge each before the next to avoid self-conflict on shared files: BoardScreen, SafetyAuditTest). Then **finishing-a-development-branch**.
