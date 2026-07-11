# The Overhaul — architecture + UI/UX audit and redesign

**Status:** design spec (approved direction; executing in sequence).
**Date:** 2026-07-11
**Goal:** the best *honest* UI for The Ashlar Protocol — calm, clear, accessible, manipulation-free — plus the code structure to sustain it. Audit-driven, incremental (not a rebuild).

---

## 1. How we got here

A 5-thread pass: two auditors read the actual code; three researchers studied best-in-class MH/wellness UX, modern Compose architecture, and UI/design-system craft. The app grew across ~12 phases and accrued real debt, concentrated in two hub files (`BoardScreen.kt` = 1,715 lines; `AshlarAppViewModel.kt` = 561 lines). The `tools/` layer (33 pure files, 206 tests) is a genuine, clean domain core. **Verdict: overhaul, don't rebuild.**

## 2. Audit findings (four clusters + one urgent bug)

- **🔴 P0 — latent data-loss (FIXED, Phase 1).** ~11 write setters read cold `WhileSubscribed` StateFlow `.value`s; an append could silently truncate history if the owning screen was unsubscribed. Fixed to read `dataStore.X.first()` (the pattern already proven in `buildReflectionInput`/`currentBalance`).
- **P1 — the Board is four rival progressions.** ~15 heterogeneous cards stack four progress metaphors (tending stone, degree, 50-course Temple, wage challenges); "days tended"/degree repeated across 4 cards; the primary daily action (mood check-in) buried at item 11.
- **P1 — no design system + systemic a11y gaps.** `bodySmall` undefined → sans-serif Roboto leaks into serif cards; half-populated color scheme leaks Material purple; random eyebrow alpha; dozens of sub-48dp tap targets; hero stone/Temple/heatmap canvases invisible to TalkBack; low-contrast faint text; no reduced-motion; the Chamber tab icon is a **trash can**; all nine tools share one wrench icon.
- **P2 — honesty leaks.** A fake "SYNC" spinner on instant local content; destructive deletes with no undo.
- **P1/P2 — code structure.** God ViewModel (~38 StateFlows, ~31 fns, 15 domains + WorkManager infra); no repository/DI; fat 400-line `LocalDataStore` (JSON-in-preferences, comma-joined hacks, silent `catch→empty` that can erase a feature's history); 1,715-line screen; degree computed 3×.

## 3. Research north-star (what to steal, what to refuse)

**Steal the craft:** calm depth-not-decoration (Calm), one clear task per surface + progressive disclosure (How We Feel), regulating (not rewarding) motion/haptics, a 3-tier design-token system, warm-dark low-arousal palette, generous whitespace, hierarchy by weight/size not color. **Refuse the coercion:** breakable streaks / loss-aversion, confirmshaming, guilt animations (wilting plant), variable rewards, nagging, infinite content, sludge-to-leave, worry-engine check-in prompts. Evidence backstop: a 38-study meta-analysis (Six 2021) found gamification does **not** improve MH outcomes — the honest path costs zero efficacy. Frame: Calm Technology (smallest attention; works when it fails).

## 4. THE DECISION — resolve the four progressions

You are tracking **two honest human dimensions, one engine, and a milestone system that got wired up twice.** Consolidate:

- **The Stone** — *who you're becoming.* Daily, forgiving, present-tense, never a progress bar. Untouched.
- **The Temple** — *what you're building.* The 50-course arc, with the **three degrees as its great thresholds** (they already map to course ranges: Apprentice 1–12 / Fellowcraft 13–31 / Master Mason 32–50).
- **The Day's Work** — the single **engine**: challenges *and* the deliberate-practice tools (Plumb, Gauge, Recall) become the work that feeds the Temple.
- **Everything else** (mirror, check-in, intention, records) → *reflective* surfaces in clear IA sections, not scoreboards.

**Why degree-as-milestone strengthens the rite:** the current degree is earned from practice *counts* (do 15 things) — the hollow achievement-treadmill the app's own research condemns. Each Temple course is a *real cited practice*, so "reaching Fellowcraft" comes to mean *"you genuinely worked the Apprentice curriculum,"* not *"you tallied 15."* The Raising fires when you **build** to the threshold. "Fellowcraft" finally means one thing.

## 5. Design-system direction

Three-tier tokens: **ref** (a warm stone neutral ramp anchored on `#0C0906` warm near-black, an off-white `#EDE3D1` for text — never pure white/black; a gold ramp with `#C9A24A` mid; one desaturated alert red; `space-*` on a 4/8pt scale; `radius-*`, `dur-*`, `easing-*`) → **sys** (Material roles + the missing surface-elevation tiers `surface → surfaceContainer → surfaceContainerHigh`, each ~4% lighter/warm; `outline`/`outlineVariant` for hairline borders that carry depth on dark — no shadows). Two corrections: move crisis red to the semantic **`error`** role; define the full `Typography` (add `bodySmall`, `titleMedium`, a display style for numbers) so nothing falls back to Roboto. Motion: `dur short/medium/long` = 150/250/400ms, standard easing default, emphasized only for the Raising; wrap all transitions in a reduced-motion → cross-fade. A11y musts: 4.5:1 text / 3:1 icons+borders, 48dp targets, dynamic type via `sp`, `semantics{}` on the canvases, visible focus.

## 6. The sequenced plan

1. **🔴 Data-loss fix** — the cold-`.value` setters. *(Phase 1 — DONE.)*
2. **Design system + accessibility** — the 3-tier token layer + complete typography/color; then a sweep fixing sub-48dp targets, canvas semantics, contrast, reduced-motion, real per-tool + nav icons, `NavigationBar` with Tab semantics, and the honesty fixes (kill fake SYNC, add undo-to-delete).
3. **Board information architecture** — enact §4: the Stone (today) · the Temple (the journey, degrees as thresholds) · the Day's Work (engine) · grouped reflective sections; fold Plumb/Gauge/Recall into the engine; retire the standalone degree-score.
4. **Structure** — split the God ViewModel into per-screen VMs + `@Stable` state holders; extract per-section composables out of the 1,715-line screen; introduce a repository seam over `LocalDataStore`; move WorkManager to a `ReminderScheduler`. Package-based layering — **no** multi-module, **no** Hilt, **no** use-case layer (`tools/` already is the domain; Google's own guidance says don't over-modularize a solo on-device app).

## 7. Guardrails (unchanged, locked)
§9 crisis pathway untouched and topmost; on-device only; honest & forgiving (this overhaul *removes* dishonest/unforgiving bits, adds none); anti-gamification foundation held (no points/coins/breakable-streaks); every user-facing string still swept through the safety gates. Each phase ships as its own green, device-verified PR.
