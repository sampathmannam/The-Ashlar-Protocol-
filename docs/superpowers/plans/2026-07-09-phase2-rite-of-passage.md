# Phase 2 — The Rite of Passage Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Turn the already-derived degree score into a felt *rite of passage* — the moment of advancement is ceremonially marked, the progression toward it is legible, and the stone refines by the four cardinal virtues.

**Architecture:** The degree engine (`tools/Degrees.kt`) and degree-gated tools (`ui/screens/ToolsScreen.kt` already veils Plumb/Mouth-to-Ear) exist. Phase 2 adds: (1) a pure `Advancement` detector that fires once per newly-earned degree; (2) DataStore persistence of the last-acknowledged degree + VM flows; (3) a solemn "Raising" ceremony that renders *below* the §9 crisis layer; (4) a Board card making the earned arc legible; (5) virtue→facet stone refinement. All pure-logic parts are TDD'd; all copy passes the `SafetyAudit` mortality gate.

**Tech Stack:** Kotlin, Jetpack Compose, Material 3, DataStore (Preferences), JUnit. Native Android, **no new dependencies**.

## Global Constraints

- **Fully on-device. No network, no `INTERNET` permission, no backend, no telemetry.** (verbatim north-star: "never gather data at any cost"; budget: "i cannot afford to spend money on it"). Nothing in Phase 2 adds a network call or a paid service.
- **No dark patterns:** progression is gated by *doing the work*, never by paying; no earnable/spendable currency; the stone never reaches a "complete/perfected" state and never regresses on a miss.
- **Copy review (⚠️):** invitations not obligations; approach not avoidance; no loss/guilt/FOMO framing.
- **Safety audit (⚠️):** zero mortality symbolism (no skull/grave/hourglass/scythe/"finish your life"/death). Every new copy surface is added to `SafetyAuditTest` and must pass. §9 crisis UI takes precedence over ALL ceremony UI.
- **Package:** `com.ashlarprotocol`. Debug signing = `${rootDir}/debug.keystore`.
- **Build (from worktree):** `JAVA_HOME=<Android Studio JBR>`, `ANDROID_HOME=~/Library/Android/sdk`, `gradle :app:testDebugUnitTest :app:assembleDebug --no-daemon --no-configuration-cache`.

**Degree engine facts (from `tools/Degrees.kt`, do not re-derive):**
- `enum Degree(display, threshold)`: `ENTERED_APPRENTICE`(0), `FELLOWCRAFT`(15), `MASTER_MASON`(40); ordinals 0/1/2.
- `Degrees.score(WorkStats(briefingStreak, journalEntries, plumbSessions, gaugeDaysComplete, recallSessions))` = streak + journal + plumb·2 + gauge·2 + recall.
- `Degrees.current(score): Degree`, `Degrees.next(d): Degree?`, `Degrees.progressToNext(score): Float`.
- Board builds `WorkStats(daysTended, entries.size, plumb, gauge, recall)` from VM flows `briefingStreak(=daysTended)`, `aarEntries`, `plumbSessions`, `gaugeDaysComplete`, `recallSessions`.

---

### Task P2.1: Advancement detector (pure logic)

**Files:**
- Create: `app/src/main/java/com/ashlarprotocol/tools/Advancement.kt`
- Test: `app/src/test/java/com/ashlarprotocol/AdvancementTest.kt`

**Interfaces:**
- Consumes: `Degree`, `Degrees.current` from `tools/Degrees.kt`.
- Produces: `Advancement.pending(acknowledgedOrdinal: Int, score: Int): Degree?` — the *next* degree in order (ordinal `acknowledgedOrdinal + 1`) **iff** it exists and `score >= its threshold`; else `null`. Fires one degree at a time, in order, so each crossing gets its own rite. Never fires for an already-acknowledged degree; never fires past the summit.

- [ ] **Step 1 — failing tests** (`AdvancementTest.kt`):
```kotlin
package com.ashlarprotocol
import com.ashlarprotocol.tools.Advancement
import com.ashlarprotocol.tools.Degree
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AdvancementTest {
    @Test fun freshApprentice_nothingPending() =
        assertNull(Advancement.pending(acknowledgedOrdinal = 0, score = 0))

    @Test fun reachingFellowcraftThreshold_pendsFellowcraft() =
        assertEquals(Degree.FELLOWCRAFT, Advancement.pending(0, 15))

    @Test fun justBelowThreshold_nothingPending() =
        assertNull(Advancement.pending(0, 14))

    @Test fun onceFellowcraftAcknowledged_notPendingAgain() =
        assertNull(Advancement.pending(acknowledgedOrdinal = 1, score = 20))

    @Test fun bigJump_firesLowerDegreeFirst() =
        // score past Master but only Apprentice acknowledged → Fellowcraft first (ordered rite)
        assertEquals(Degree.FELLOWCRAFT, Advancement.pending(0, 99))

    @Test fun afterFellowcraft_masterPendsWhenEarned() =
        assertEquals(Degree.MASTER_MASON, Advancement.pending(1, 40))

    @Test fun summitAcknowledged_nothingPending() =
        assertNull(Advancement.pending(acknowledgedOrdinal = 2, score = 999))
}
```
- [ ] **Step 2 — run, expect FAIL** (`Advancement` unresolved).
- [ ] **Step 3 — implement**:
```kotlin
package com.ashlarprotocol.tools

/**
 * Detects a *newly earned* degree so the app can mark the crossing with a rite (Phase 2).
 * Advances one degree at a time, in order — each raising gets its own ceremony. Pure, on-device.
 */
object Advancement {
    fun pending(acknowledgedOrdinal: Int, score: Int): Degree? {
        val all = Degree.values()
        val nextOrdinal = acknowledgedOrdinal.coerceAtLeast(0) + 1
        if (nextOrdinal >= all.size) return null          // summit already acknowledged
        val candidate = all[nextOrdinal]
        return if (score.coerceAtLeast(0) >= candidate.threshold) candidate else null
    }
}
```
- [ ] **Step 4 — run, expect PASS.**
- [ ] **Step 5 — commit** `feat(degrees): Advancement detector — fires one rite per newly-earned degree`.

---

### Task P2.2: Persist acknowledged degree + ViewModel wiring

**Files:**
- Modify: `app/src/main/java/com/ashlarprotocol/data/LocalDataStore.kt` (add key + flow + setter, mirroring `plumbSessions`)
- Modify: `app/src/main/java/com/ashlarprotocol/ui/AshlarAppViewModel.kt` (expose `currentDegree`, `pendingAdvancement`, add `acknowledgeAdvancement()`)
- Test: `app/src/test/java/com/ashlarprotocol/AdvancementTest.kt` (add a wiring-shape test that mirrors the VM combine using pure calls)

**Interfaces:**
- Consumes: `Advancement.pending`, `Degrees.score`, `Degrees.current`, VM flows `briefingStreak`, `aarEntries`, `plumbSessions`, `gaugeDaysComplete`, `recallSessions`.
- Produces (LocalDataStore): `val acknowledgedDegreeOrdinal: Flow<Int>` (default 0); `suspend fun setAcknowledgedDegreeOrdinal(ordinal: Int)`.
- Produces (VM): `val currentDegree: StateFlow<Degree>`; `val pendingAdvancement: StateFlow<Degree?>`; `fun acknowledgeAdvancement()` — persists the pending degree's ordinal so the rite never re-fires.

- [ ] **Step 1 — DataStore** (in `LocalDataStore.kt`, beside the other int keys/flows):
```kotlin
private val ACK_DEGREE_KEY = androidx.datastore.preferences.core.intPreferencesKey("acknowledged_degree_ordinal")
val acknowledgedDegreeOrdinal: Flow<Int> = context.dataStore.data.map { it[ACK_DEGREE_KEY] ?: 0 }
suspend fun setAcknowledgedDegreeOrdinal(ordinal: Int) {
    context.dataStore.edit { it[ACK_DEGREE_KEY] = ordinal.coerceAtLeast(0) }
}
```
- [ ] **Step 2 — VM flows** (in `AshlarAppViewModel.kt`; use the same `combine` idiom the file already uses, and `import com.ashlarprotocol.tools.*` as needed):
```kotlin
private val ackDegreeOrdinal: StateFlow<Int> = dataStore.acknowledgedDegreeOrdinal
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

// score = the same WorkStats the Board builds
private val degreeScore: StateFlow<Int> = combine(
    briefingStreak, aarEntries, plumbSessions, gaugeDaysComplete, recallSessions
) { streak, entries, plumb, gauge, recall ->
    com.ashlarprotocol.tools.Degrees.score(
        com.ashlarprotocol.tools.WorkStats(streak, entries.size, plumb, gauge, recall)
    )
}.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

val currentDegree: StateFlow<com.ashlarprotocol.tools.Degree> = degreeScore
    .map { com.ashlarprotocol.tools.Degrees.current(it) }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), com.ashlarprotocol.tools.Degree.ENTERED_APPRENTICE)

val pendingAdvancement: StateFlow<com.ashlarprotocol.tools.Degree?> = combine(
    ackDegreeOrdinal, degreeScore
) { ack, score -> com.ashlarprotocol.tools.Advancement.pending(ack, score) }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

fun acknowledgeAdvancement() {
    val d = pendingAdvancement.value ?: return
    viewModelScope.launch { dataStore.setAcknowledgedDegreeOrdinal(d.ordinal) }
}
```
> NOTE: if `combine` of 5 flows needs the vararg form, use `combine(listOf(...)) { arr -> ... }`; check the file's existing combine arity. `aarEntries` is the journal list flow (its `.size` is `journalEntries`). Confirm the exact flow names by grep before wiring; adapt names, not shapes.
- [ ] **Step 3 — build + existing tests green** (`:app:testDebugUnitTest`); this ticket is wiring, verified by compile + the P2.1 tests still passing.
- [ ] **Step 4 — commit** `feat(degrees): persist acknowledged degree; expose currentDegree + pendingAdvancement`.

---

### Task P2.3: The Raising — advancement ceremony (⚠️ safety-audited, §9-subordinate)

**Files:**
- Create: `app/src/main/java/com/ashlarprotocol/ui/components/Advancement.kt` (the ceremony composable + its copy as a testable object)
- Modify: `app/src/main/java/com/ashlarprotocol/ui/TracingBoardApp.kt` (render ceremony when pending, **below** the crisis dialog)
- Modify: `app/src/test/java/com/ashlarprotocol/SafetyAuditTest.kt` (feed ceremony copy → assert mortality-clean)

**Interfaces:**
- Consumes: `viewModel.pendingAdvancement`, `viewModel.acknowledgeAdvancement()`, `Degree`.
- Produces: `object RaisingCopy { fun forDegree(d: Degree): RaisingText }` where `RaisingText(overline, degreeLine, toolsGiven, meaning, cta)`; `@Composable fun AdvancementCeremony(degree: Degree, onAcknowledge: () -> Unit)`.

- [ ] **Step 1 — copy object + the tools each degree confers** (in `ui/components/Advancement.kt`), kept as pure data so the safety test can read it:
```kotlin
data class RaisingText(
    val overline: String, val degreeLine: String,
    val toolsGiven: String, val meaning: String, val cta: String
)
object RaisingCopy {
    fun forDegree(d: Degree): RaisingText = when (d) {
        Degree.FELLOWCRAFT -> RaisingText(
            "YOU ARE RAISED", "to the degree of Fellowcraft",
            "The Plumb is placed in your hands — to straighten a thought against the true vertical.",
            "You have shown up, and kept showing up. The mind's work opens to you now.",
            "TAKE UP THE TOOLS")
        Degree.MASTER_MASON -> RaisingText(
            "YOU ARE RAISED", "to the degree of Master Mason",
            "Mouth to Ear is entrusted to you — the memory work, carried and passed on.",
            "The rough edges are worn smoother. The work from here is lifelong, and it is yours.",
            "TAKE UP THE TOOLS")
        Degree.ENTERED_APPRENTICE -> RaisingText(
            "YOU ARE RECEIVED", "as an Entered Apprentice",
            "The first tools are yours.", "The work begins.", "BEGIN THE WORK")
    }
    fun allText(): List<String> = Degree.values().map { forDegree(it) }.flatMap {
        listOf(it.overline, it.degreeLine, it.toolsGiven, it.meaning, it.cta)
    }
}
```
- [ ] **Step 2 — SafetyAudit test** (in `SafetyAuditTest.kt`, add ceremony copy to the swept surfaces):
```kotlin
@Test fun raisingCeremonyCopy_hasNoMortalitySymbolism() {
    val violations = com.ashlarprotocol.tools.SafetyAudit.mortalityViolations(
        com.ashlarprotocol.ui.components.RaisingCopy.allText().joinToString(" ")
    )
    assertTrue("Raising copy must be mortality-clean: $violations", violations.isEmpty())
}
```
- [ ] **Step 3 — run, expect PASS** (copy is already clean; if it fails, fix the *copy*, never the gate).
- [ ] **Step 4 — the ceremony composable** — a solemn full-bleed `Dialog` (not dismissible by scrim; only the CTA acknowledges), gold/stone palette, centered: overline (letter-spaced, small) · degreeLine (serif, large) · a thin rule · toolsGiven · meaning · CTA button → `onAcknowledge`. Match `CrisisSupportDialog`'s construction idiom already in the codebase.
- [ ] **Step 5 — wire in `TracingBoardApp.kt`**: collect `pendingAdvancement`; render `AdvancementCeremony(it) { viewModel.acknowledgeAdvancement() }` **after/above** the normal content but **before** the crisis dialog composition so §9 stays the top layer (mirror the T3.3 ordering where `CrisisSupportDialog` renders last).
- [ ] **Step 6 — build + all tests green.**
- [ ] **Step 7 — device-verify**: install; seed a crossing via DataStore (adb: set `plumb_sessions`/`gauge_days_complete` or `days_tended` so score ≥ 15) OR do enough work; relaunch → the Raising renders; tap CTA → dismisses and does not re-fire on relaunch. Capture UI dump. If the crossing can't be reached by seeding, verify the composable via a temporary preview trigger and note honestly.
- [ ] **Step 8 — commit** `feat(rite): the Raising — advancement ceremony, subordinate to §9 crisis`.

---

### Task P2.4: The progression made legible (Board card)

**Files:**
- Modify: `app/src/main/java/com/ashlarprotocol/ui/screens/BoardScreen.kt` (add a `DegreePathCard` item)
- Modify: `app/src/test/java/com/ashlarprotocol/SafetyAuditTest.kt` (add the card's copy to the sweep)

**Interfaces:**
- Consumes: `viewModel.currentDegree`, `degreeScore` (or recompute the same `WorkStats` locally as the Board already does), `Degrees.next`, `Degrees.progressToNext`.
- Produces: a Board card titled "THE WORK BENEATH THE STONE" showing the current degree and a slim progress line toward the *next* degree's name; at the summit shows "Master Mason — the work is now lifelong" with no bar. **Distinct from the stone**, which stays tending-driven and never-complete.

- [ ] **Step 1 — label helper** (small, testable) in `tools/Degrees.kt` or the card file:
```kotlin
// "Toward Fellowcraft" / null at summit
fun towardNextLabel(score: Int): String? = next(current(score))?.let { "Toward ${it.display}" }
```
with a test asserting `towardNextLabel(0) == "Toward Fellowcraft"`, `towardNextLabel(40) == null`.
- [ ] **Step 2 — the card**: current degree name (already shown on the stone, so here frame it as the *path*: e.g. small "YOUR DEGREE · Entered Apprentice"), a thin `LinearProgressIndicator(progressToNext(score))`, and the `towardNextLabel` beneath; summit → a quiet "the work is now lifelong" line, no bar. Copy has no FOMO ("Toward…" is invitational, not "X to go before you lose…").
- [ ] **Step 3 — add card copy to SafetyAuditTest sweep; build + all tests green.**
- [ ] **Step 4 — device-verify** the card renders on the Board (UI dump shows "THE WORK BENEATH THE STONE" + "Toward Fellowcraft").
- [ ] **Step 5 — commit** `feat(rite): Board card makes the degree arc legible (distinct from the never-complete stone)`.

---

### Task P2.5: The stone refines by the four virtues (VIA facets) — scoped honestly

**Files:**
- Create: `app/src/main/java/com/ashlarprotocol/tools/StoneFacets.kt`
- Test: `app/src/test/java/com/ashlarprotocol/StoneFacetsTest.kt`
- Modify (best-effort visual): the stone drawing in `BoardScreen.kt` (`drawAshlar`) to vary edge smoothing subtly per facet.

**Interfaces:**
- Consumes: `Strength`/virtue model from `tools/Strengths.kt` (grep it first to learn the exact virtue enum/type), `signatureStrengths` flow, `WorkStats`.
- Produces: `StoneFacets.refinement(signature: List<Strength>, score: Int): Map<<Virtue>, Float>` — a 0f..1f smoothing per cardinal facet, monotonic in work done, never decreasing. Pure, unit-tested.

- [ ] **Step 0 — grep `tools/Strengths.kt`** to learn the real virtue grouping (cardinal virtues vs 24 VIA strengths). If Strengths has no clean 4-virtue grouping, define the four cardinal *facets* here (Wisdom/Courage/Humanity/Justice-Temperance grouping) mapping each `Strength` → a facet; cite `docs/RESEARCH_BASIS.md` VIA basis in the KDoc.
- [ ] **Step 1 — failing tests** (`StoneFacetsTest.kt`): empty signature + score 0 → all facets 0f; a signature strength in the Courage group + score>0 → Courage facet > 0f; refinement is monotonic (higher score never lowers a facet); every facet ∈ 0f..1f.
- [ ] **Step 2 — run, expect FAIL.**
- [ ] **Step 3 — implement** the pure mapping + refinement (asymptotic like `KindStreak.stoneProgress`, never reaching 1f so the stone never "completes").
- [ ] **Step 4 — run, expect PASS.**
- [ ] **Step 5 — visual (best-effort):** feed the per-facet refinement into `drawAshlar` so different edges of the stone smooth as their virtue's work accrues. **HONEST verification note:** the facet logic is unit-tested; the visual differentiation is subtle and may not be frame-capturable — verify build + no-crash + that the stone still renders, and say so plainly rather than claiming a visible facet change.
- [ ] **Step 6 — commit** `feat(rite): stone refines by the four cardinal virtues (VIA facets) — logic tested, visual best-effort`.

---

## Self-Review

- **Spec coverage:** "degrees" → P2.1/P2.2/P2.4 (detector, persistence, legible arc); "ceremony transitions" → P2.3 (the Raising); "VIA facets on the stone" → P2.5. MASTER_PLAN Phase 2 "tools given on advancement" → P2.3 names the tool each degree confers (already veiled by ToolsScreen). Pivot Phase 0.2 "The Craft, single-player, N=1" → entire plan is on-device solo, no backend.
- **Already-done, not re-built:** degree derivation (Board), tool veiling (ToolsScreen) — Phase 2 does NOT duplicate these.
- **Safety:** every new copy surface (P2.3 RaisingCopy, P2.4 card) is added to `SafetyAuditTest`; the ceremony renders below §9 (P2.3 step 5).
- **Constraints:** no new deps, no network, no currency, stone never completes/regresses (P2.5 asymptotic; stone stays tending-driven in P2.4).
- **Verification honesty:** P2.3 seed-to-cross + UI dump; P2.5 visual explicitly best-effort.

## Execution

Inline via **executing-plans** in an isolated worktree (`feat/phase2-rite` off `origin/main`), one commit per task, `:app:testDebugUnitTest :app:assembleDebug` green after each, device-verify on ZD2232FCR5 where there's something to see, then **finishing-a-development-branch** → one PR for the whole rite.
