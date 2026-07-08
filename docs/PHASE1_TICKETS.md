# Phase 1 — Engineering Ticket Breakdown

Derived from [SPEC_PHASE1_STONE.md](SPEC_PHASE1_STONE.md). Tickets are ordered by milestone. **M0 gates everything** — no stone visual work (M2) starts until M0 passes. Each ticket lists its spec requirement, acceptance criteria, and dependencies. Sizes are rough (S ≤1d, M ~2–4d, L ~1wk+).

Legend: 🔒 = blocking gate · ⚠️ = safety-critical (needs design+copy sign-off) · 🧠 = reuses existing on-device infra (memory/persona/§9).

---

## Milestone 0 — Framing pre-test 🔒 (no code; gates M2)

- **T0.1 — Build two static onboarding mockups (stone vs plain).** *(S, design)* Two counterbalanced screens per SPEC §9. No mortality imagery. → deliverable feeds the reaction test. *(Mockups drafted — see the Onboarding Framing Test artifact.)*
- **T0.2 — Run the 5–8 person reaction test & decide.** *(S, founder/research)* Apply the pass/soften/pivot rule. **Output gates T2.x.** AC: written decision recorded in-repo; if "pivot," swap central metaphor before any M2 work.

---

## Milestone 1 — The loop (behind a placeholder stone)

Goal: the full daily loop works on-device with a **placeholder** stone graphic, so mechanics are validated before art investment.

### Data & foundation
- **T1.0 — Practice data model.** *(M)* Model a `Practice` (self-authored text, approach-framed, if-then anchor, tiny/floor variant, cadence) and a `PracticeCompletion` (timestamp, mood-at-time, which stone-facet refined). **P2 forward-compat:** include an optional `facet`/`strength` field now (unused in P1) so Phase 2 VIA mapping doesn't require a migration. AC: completions are append-only; nothing is ever decremented/deleted on a miss. Deps: none.
- **T1.1 — On-device continuity store ("practice log").** *(M)* Monotonic cumulative counter of days-worked (only grows) + grace-day ledger (cap 2, auto-applied). AC: survives timezone travel / clock changes (Open Q5); fully on-device; a missed day never zeroes the cumulative log. Deps: T1.0.

### The daily "Working" (P0.2)
- **T1.2 — Daily mood/energy check-in.** *(S)* Lightweight, skippable. Feeds difficulty scaling + stores mood-at-time on completion. AC: logging a low mood routes to floor-task path. Deps: T1.0.
- **T1.3 — Mood-adaptive task selection + difficulty dial.** *(M)* Low state → floor task (near-impossible to fail); good state → optional stretch; user-controllable "lighter/heavier" override. AC per SPEC P0.2. Deps: T1.2.
- **T1.4 — Practice authoring flow (if-then + approach framing).** *(M)* User writes their own practice, must attach an existing-routine anchor and phrase as an approach action. AC: setup rejects/redirects avoidance phrasing ("stop…"→prompt to reframe); stores the full "After [anchor], I will [action]" plan. Deps: T1.0.
- **T1.5 — Cue-anchored reminders.** *(M)* Fire at the anchor moment; gentle, skippable copy. ⚠️ AC: **no loss/guilt framing** anywhere (copy checklist); no "streak at risk" notifications. Deps: T1.4.

### The three tools (P0.1 tools; Q1-resolved set)
- **T1.6 — Tool: Integrity Check (Square).** *(S)* Nightly "were your actions square with what matters?" — slider + one optional line; feeds a stone-facet refinement. AC: reflective, never graded ("failed"). Deps: T1.0.
- **T1.7 — Tool: Letting Go (Gavel) + Self-Compassion (Level) as a MATCHED PAIR.** *(M)* ⚠️ **Ship together, never apart.** Letting Go = name a "rough edge," mark when resisted/reframed; every Letting Go surface links its Self-Compassion counterweight (common-humanity reframe). AC: no path exists to use Letting Go without the Self-Compassion counterweight being present/offered. Deps: T1.0. **Safety sign-off required.**

### Feedback & companion
- **T1.8 — Informational feedback engine (P0.4).** *(M)* Competence-framed messages ("you can now…", "you've noticed X N times"); **no earnable/spendable currency anywhere.** AC: no numeric reward token in the app; every celebratory cue is tied to a real completed action (never a login/timer). Deps: T1.0.
- **T1.9 — Remembered companion greeting (P0.5).** *(M, 🧠)* Reuse on-device compounding-memory + unified persona. Warm greeting references remembered context on cold start; all prompts phrased as invitations. AC per SPEC P0.5; nothing uploaded. Dep/Open Q3: may need a cached digest for cold-start latency. Deps: existing memory infra.
- **T1.10 — Comeback + self-forgiveness flow (P0.3).** *(S)* ⚠️ Returning after ≥1 miss triggers the warmest response; explicit self-forgiveness copy; no zero-counter, no "streak lost" language. AC per SPEC P0.3. Deps: T1.1.

---

## Milestone 2 — The stone visual system (only after M0 passes 🔒)

- **T2.1 — Stone avatar rendering + refinement states.** *(L)* Persistent artifact that visibly smooths as cumulative practice grows; maps completions → visible refinements. ⚠️ AC: **never reaches a "complete/perfected" state**; never regresses on a miss. Deps: T1.0, T1.8, **M0 pass**.
- **T2.2 — Home screen = the stone (tracing-board-lite).** *(M)* Stone as home; ≤2-tap access to Power-Ups and companion at all times. Deps: T2.1.
- **T2.3 — Micro-feedback (chisel strike / light-catch).** *(S)* Satisfying, but **only** on a real action. AC: no decoupled/random reward animation. Deps: T2.1, T1.8.
- **T2.4 — Power-Ups library (P0.6).** *(M)* Quick mood-lifters pullable anytime, decoupled from streak/task. ⚠️ AC: **never gated** behind the Working, a streak, or payment; ≤2 taps. Deps: T2.2.
- **T2.5 — Graceful exit (P0.7).** *(S)* "Enough for today" affordance; no FOMO/streak-threat friction to leaving. Deps: T2.2.

---

## Milestone 3 — Measurement & anti-harm instrumentation

- **T3.1 — WHO-5 measurement (primary metric).** *(M)* Baseline + ~biweekly, skippable; optional PHQ-9/GAD-7 opt-in. AC: gentle cadence (Open Q4); stored on-device/privacy-preserving. Deps: T1.2.
- **T3.2 — Anti-harm signal instrumentation.** *(M)* ⚠️ Flag spike-then-churn-after-miss; verify high-engagement users improve on WHO-5; flag compulsive late-night checking. AC: **time-on-device / session-length / DAU are NOT optimization targets** (documented). Deps: T1.1, T3.1.
- **T3.3 — Safety precedence integration test (P0.8).** *(S, 🧠⚠️)* Existing §9 crisis gate overrides ALL gamified UI; risk signal suppresses stone/streak/task and routes to support. AC per SPEC P0.8; plus a design+copy audit confirming **zero mortality symbolism** across Phase 1. Deps: existing §9 layer, T2.x.

---

## Cross-cutting / definition-of-done gates (apply to every ticket)

- ⚠️ **Copy review:** no loss/guilt/FOMO framing; invitations not obligations; approach not avoidance.
- ⚠️ **Safety audit:** no death/mortality imagery or "finish your life" framing; §9 takes precedence.
- **Privacy:** on-device by default; nothing uploaded; matches the "nothing leaves your phone" promise.
- **No dark patterns:** no earnable currency, no breakable-to-zero streak, no gated support, no engagement-optimizing metric.

## Suggested build order (critical path)

**M0 (T0.1→T0.2 gate)** → M1 foundation (T1.0, T1.1) → Working (T1.2→T1.5) → tools (T1.6, T1.7) → feedback/companion (T1.8→T1.10) → **[M0 must be green]** → stone visuals (T2.1→T2.5) → measurement/safety (T3.1→T3.3).

Parallelizable once T1.0/T1.1 land: the Working chain, the tools, and the companion greeting are largely independent.
