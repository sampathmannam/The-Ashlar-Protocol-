# Phase 4c — Loose Ends Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:executing-plans. Steps use checkbox (`- [ ]`).

**Goal:** Close the two genuinely-buildable remaining items (the rest are user-gated: petition deploy, backend/payments, signed release, the AI phase). (1) Replace the Gavel's stray AI-Studio placeholder with a real, on-brand micro-practice. (2) Reconcile the stale roadmap docs with reality.

**Architecture:** One pure-logic tool + a rewritten Compose surface (TDD), then a docs accuracy pass. On-device, no new deps, no persistence added.

## Global Constraints
- Fully on-device; no network/deps/spend. Approach-framed, autonomy-supportive, no shame/grit/willpower language (the F7 language gate enforces this). Every new string swept by SafetyAudit (mortality + language). §9 untouched.
- Package `com.ashlarprotocol`; build `gradle :app:testDebugUnitTest :app:assembleDebug`; device-verify ZD2232FCR5; one branch, PR at end.

---

### Task P4c.1 — The Gavel: catch-and-square a rough corner
**Why:** the common gavel knocks the *rough corners* off the ashlar. The current tool is a Tamil-syllable matching drill left over from the AI-Studio scaffold — unrelated to the Craft. Replace it with a real, distinct micro-practice: catch a recurring **reactive pattern** and pre-commit a **competing response** — habit-reversal's competing response (relevant to F5) + an implementation-intention *to interrupt* (Gollwitzer). Distinct from the Plumb (a deliberate thought-record) and Practices (building a *good* habit): the Gavel *interrupts a reactive one in the moment*.

**Files:**
- Create: `app/src/main/java/com/ashlarprotocol/tools/Gavel.kt` (pure) + `app/src/test/java/com/ashlarprotocol/GavelTest.kt`
- Modify: `ui/screens/ToolsScreen.kt` (rewrite `TheGavel`; delete `TamilChallenge`/`TAMIL_CHALLENGES`/`GavelOption`), `SafetyAuditTest.kt`

**Interfaces produced:**
- `Gavel.ROUGH_CORNERS: List<String>` — a few example reactive patterns (e.g. "snapping when I feel criticised", "spiralling after one mistake", "checking my phone the moment I'm bored").
- `Gavel.compose(reaction: String, trigger: String, truerResponse: String): String` — a competing-response if-then: *"When [trigger], I'll notice the urge to [reaction] — and set it down, and [truerResponse] instead."*
- `Gavel.canSquare(reaction: String, truerResponse: String): Boolean` — both non-blank (the trigger is optional).
- `Gavel.allText(): List<String>` — ROUGH_CORNERS + a sample composed line, for the sweep.

- [ ] **Step 1 — failing tests** (`GavelTest.kt`): ROUGH_CORNERS non-empty & non-blank; `canSquare("x","y")`==true, `canSquare("","y")`==false, `canSquare("x","")`==false; `compose("snapping","I feel judged","take one breath")` contains all three fragments; the composed line contains no shame/grit tokens ("must","should","willpower","toughen") — assert clean.
- [ ] **Step 2 — run FAIL → 3 — implement `Gavel.kt` (KDoc cites competing-response / implementation-intention-to-interrupt) → 4 — PASS.**
- [ ] **Step 5 — rewrite the UI:** replace `TheGavel()` with a flow: an optional pick from `ROUGH_CORNERS` (or write your own) → the reaction field → the trigger field → the truer-response field → reflect back `Gavel.compose(...)` as "THE CORNER, KNOCKED OFF". Match the existing tool idiom (Surface card, Gold/Silver, PlumbField/PlumbPrimary already in the file). Delete `TamilChallenge`, `TAMIL_CHALLENGES`, `GavelOption` (now dead). Sweep `Gavel.allText()` in SafetyAuditTest.
- [ ] **Step 6 — build + full suite green; device-verify** the Gavel opens, composes an interrupt, no crash; confirm no Tamil drill remains.
- [ ] **Step 7 — commit** `feat(gavel): catch-and-square a rough corner — replace the placeholder drill`.

---

### Task P4c.2 — Reconcile the stale roadmap docs
**Why:** `MASTER_PLAN.md` / `ACTION_PLAN.md` still say things like "package rename / Phase 3 connection — not started" and "tune the Level to ~6 breaths/min" — all long since shipped. Anyone reading them would be misled. Bring the status in line with reality and the society pivot. (Docs only — no code, no tests.)

**Files:** Modify `docs/MASTER_PLAN.md`, `docs/ACTION_PLAN.md`.

- [ ] **Step 1 — MASTER_PLAN status pass:** update the top status list + the per-phase ✅/⏳/⬜ markers to reflect: Phase 1 done; the Rite of Passage (Phase 2) built; Phase 1C rename done (release-signing still open); the Level/Chamber polish done; connection (West Gate) built; and add a pointer that the discipline research + Phases 4/4b (F1–F8) are built per `docs/RESEARCH_INTEGRATION.md`. Add a one-line note that the society pivot (`docs/superpowers/specs/2026-07-09-society-pivot-design.md`) reframes the roadmap, and that the genuinely-remaining items are: the validation-first petition, the (deferred) society backend/payments, the on-device-intelligence phase, and release signing.
- [ ] **Step 2 — ACTION_PLAN status pass:** same reconciliation against its Phase 0–4 numbering (Phase 3 Lodge = built; Phase 2 presence/mentor-voice + Phase 4 JITAI = the unbuilt AI-dependent phases, gated).
- [ ] **Step 3 — commit** `docs: reconcile MASTER_PLAN/ACTION_PLAN with reality (Phases 1–4b shipped, pivot, remaining)`.

---

## Self-Review
- Buildable-remaining only; user-gated items (petition deploy, backend, signed release, AI phase) explicitly out of scope and stated to the user.
- Gavel is distinct from Plumb/Practices/Rough-Edge (interrupt a reactive pattern, in the moment); no new persistence; both safety gates sweep it; the dead Tamil scaffold is removed.
- Docs pass is accuracy-only; no overclaiming.

## Execution
Inline via **executing-plans** in worktree `feat/phase4c-loose-ends` off `origin/main`; TDD the Gavel; green + device-verify; docs pass; one branch, PR at end; **finishing-a-development-branch**.
