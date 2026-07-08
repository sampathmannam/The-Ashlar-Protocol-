# Spec — Phase 1: "The Stone That Shapes" (solo core loop)

**Type:** Feature spec / PRD.
**Status:** Draft for build. Gated on Milestone 0 (see §9).
**Owner:** Founder.
**Source of scope:** [GAMIFICATION_PLAN.md § 9 Phase 1](GAMIFICATION_PLAN.md). Evidence: [RESEARCH_BASIS.md](RESEARCH_BASIS.md). Metaphor system: [MASONIC_DESIGN_PRIMITIVES.md](MASONIC_DESIGN_PRIMITIVES.md). Binding safety: RESEARCH_BASIS §10 Safety Gate + the Masonic Safety Wall.

---

## Problem Statement

People who are struggling and can't open up to anyone need a private, on-device space that helps them *actually get better* — but the wellness-app category retains ~3% of users at 30 days (Baumel 2019) and, worse, its standard engagement mechanics (punitive streaks, points economies, leaderboards) are documented to *harm* the exact population they target. The Ashlar Protocol's opportunity is to deliver a complete, motivating self-improvement loop where **the game mechanic and the therapy are the same object** — a self visibly being shaped through evidence-based skills — with none of the shame/comparison/extrinsic-reward traps. Phase 1 proves that core loop, solo, with zero social and zero mortality content.

## Goals

1. **A complete solo core loop** a user can run daily: check in → do one adaptive practice ("the Working") → see the stone honestly reflect real behavior → feel met by a remembered companion.
2. **Prove the framing lands** — that "you are a stone being worked" reads as *empowering*, not *cold/objectifying*, for someone in distress (Milestone 0 gate, §9).
3. **Kind consistency** — a streak/continuity system that builds habit without ever producing shame on a missed day (never zero-resets).
4. **Wellbeing-first measurement** — instrument outcomes (WHO-5 primary) and *harm signals*, not engagement vanity metrics.
5. **Motivation that durably internalizes** — feedback is informational (competence), never a controlling reward, so the behavior survives without the app's prompting.

## Non-Goals (explicitly out of scope for Phase 1)

1. **All social / cooperative / prosocial features** — no cohorts, allies, encouragement, sharing. (Phase 3. Solo-first is a settled decision.)
2. **The three-degree progression system, VIA strengths facets, ceremony transitions** — (Phase 2). Phase 1 ships the stone + a small fixed tool set only.
3. **Any competitive/ranked leaderboard** — (deferred to conditional Phase 4; may never ship).
4. **Any mortality/death symbolism** — no Chamber-of-Reflection death imagery, no memento mori, no Hiram death-arc, no skull/hourglass/countdown. Binding, not a preference (RESEARCH_BASIS §10 Safety Gate).
5. **Full crisis-detection build-out beyond the existing §9 gate** — Phase 1 reuses the current model-independent crisis layer; it does not add new social-moderation infrastructure (none needed with zero social).
6. **Monetization / paywall** — no gating of any support behind payment or task completion in Phase 1.

## User Stories

**Primary persona: "the depleted returner"** — low energy, low self-worth, often opening the app on a bad day.

- As a depleted user, I want the app to ask *less* of me on a bad day, so that I can still do *something* instead of failing and quitting.
- As a user who missed yesterday, I want to be met with warmth and a clear way back, so that one slip doesn't become the end.
- As a user, I want to set practices in my own words and anchor them to my real routine, so that they feel like mine and actually fire at the right moment.
- As a user, I want to see honest evidence that I'm changing (the stone smoothing), so that I feel genuine progress without being scored or ranked.
- As a returning user, I want the companion to remember what I've been carrying, so that I feel met, not re-introduced to a stranger.
- As a user in a spike of distress, I want a quick mood-lifter available anytime, so that help isn't locked behind completing a task.
- As a user, I want to be able to stop for the day and be told that's enough, so that the app respects my limits instead of pulling me back in.

**Edge / boundary stories**
- As a first-time user, I want a brief, low-pressure onboarding that lets me name what matters and set one tiny practice, so that I get an early win (Fogg: bank one easy success).
- As a user who opens the app and does nothing, I want the empty state to be gentle and inviting, not guilt-inducing.
- As a user typing something that signals crisis, I want the app to drop everything gamified and route me to support (existing §9 gate takes precedence over all Phase 1 flows).

## Requirements

### Must-Have (P0) — the loop doesn't exist without these

**P0.1 — The Ashlar (the stone avatar)**
- A persistent visual artifact representing the user, that **visibly smooths/refines as cumulative practice accumulates**.
- Progress is an **honest mirror of real behavior** (practices actually done), not a container of points.
- **Never renders as "complete"** — asymptotes toward a finer stone; copy states the work is lifelong (anti-perfectionism guardrail).
- Progress is **cumulative and never decreases** on a missed day.
- *Acceptance:*
  - [ ] Given a user completes a practice, when they return to the home screen, then the stone shows a small, visible refinement tied to that specific action.
  - [ ] Given a user misses one or more days, when they return, then the stone is **unchanged** (never regresses/cracks/degrades).
  - [ ] The stone never displays a "100%/finished" state; a "perfected" label is impossible to reach.

**P0.2 — The daily "Working" (mood-adaptive practice)**
- A lightweight daily **mood/energy check-in** that **scales the day's ask**.
- Low state → offer a **floor task** engineered to be nearly impossible to fail (e.g., one breath, open the curtains, step outside 60s).
- A **user-controllable difficulty dial** ("make today lighter / heavier").
- Practices are **self-authored, approach-framed, anchored to an existing cue** via an if-then plan set at creation ("After [anchor], I will [tiny action]").
- Reminders fire at the **cue moment**, not a random clock time; all reminders are gentle, skippable, and **never loss-framed**.
- *Acceptance:*
  - [ ] Given a user logs a low mood, when the day's Working is offered, then the suggested task is a floor task and the copy pays *more* acknowledgment, not less.
  - [ ] Given a user logs a good mood, when offered, then a gentle stretch is available but never forced.
  - [ ] Given a user creates a practice, when they set it up, then they must phrase it as an approach action and attach an existing-routine anchor.
  - [ ] No notification uses loss/guilt framing ("don't lose…", "you're falling behind"). (Copy review checklist item.)

**P0.3 — "Tending the stone" (kind streak / continuity)**
- An **accumulating practice log** (total days worked — only ever grows) as the primary continuity signal, **not** a fragile consecutive-day counter.
- If a consecutive-day indicator is shown at all, it uses **auto-applied grace-days** (small cap ≈2) and **never resets to zero on a single miss** (Lally 2010: one miss doesn't harm habit formation).
- **Comeback is a first-class celebrated event** — returning after a miss triggers the *warmest* response, not a broken flame.
- Missed-day copy models **explicit self-forgiveness** (Wohl 2010).
- *Acceptance:*
  - [ ] Given a user misses a day, when they return, then no counter shows zero and no "streak lost" language appears anywhere.
  - [ ] Given a user returns after ≥1 missed day, when they open the app, then they receive an explicit warm "welcome back / that's the skill" message.
  - [ ] Grace-day application is automatic and silent (no requirement to have pre-armed it).

**P0.4 — Informational feedback (not rewards)**
- All progress feedback is **competence-framed** ("you can now…", "you've noticed X four times this week"), **never** a points/coins/badge currency the user earns or can lose.
- Micro-feedback (the chisel strike / stone catching light) is allowed **only** when tied to a real action in that moment (never decoupled/random).
- *Acceptance:*
  - [ ] No screen shows an earnable/spendable numeric reward currency.
  - [ ] Every celebratory animation is triggered by a genuine completed action, not a login or a timer.

**P0.5 — The remembered companion (the bond)**
- A warm, consistent persona that **remembers prior context** (reuses existing on-device compounding-memory + unified persona).
- Frames tracking as **insight** ("here's a pattern I noticed"), check-ins as **invitations** ("want to…?"), never obligations ("you must…").
- Designed for a **rising alliance** over sessions (references what the user has been carrying).
- *Acceptance:*
  - [ ] Given a returning user, when the companion greets them, then it references relevant remembered context (on-device only; nothing uploaded).
  - [ ] All prompts are phrased as invitations; imperative/obligation phrasing is absent (copy review).

**P0.6 — Power-Ups library (always-available support)**
- A set of quick mood-lifters the user can pull **anytime**, **decoupled** from any streak or task.
- *Acceptance:*
  - [ ] Power-Ups are reachable in ≤2 taps from the home screen at all times.
  - [ ] Access to Power-Ups and to the companion is **never** gated behind completing the Working, a streak, or payment.

**P0.7 — Graceful exit**
- Easy per-session stop points and a "you've done enough today" nudge; no dark-pattern friction to leaving.
- *Acceptance:*
  - [ ] Given a user has done one practice, when they linger, then a gentle "enough for today" affordance is available.
  - [ ] No interstitial, streak-threat, or FOMO prompt blocks the user from closing the app.

**P0.8 — Safety precedence (reuse existing §9)**
- The existing model-independent crisis gate **overrides all Phase 1 gamified flows**; any risk signal suppresses stone/streak/task UI and routes to support.
- **Zero mortality symbolism** anywhere in Phase 1 (design + copy review sign-off required).
- *Acceptance:*
  - [ ] Given a crisis signal, when detected, then all gamified UI is suppressed and the §9 support flow takes over.
  - [ ] A design + copy audit confirms no skull/hourglass/countdown/death imagery or "finish/complete-your-life" framing exists.

### Nice-to-Have (P1) — fast-follow, not blocking

- **P1.1** The four-tool set as distinct modules (Daily Balance / Letting Go / Self-Compassion / Integrity Check) with per-tool guided flows. *(P0 can ship with a smaller starting subset — see Open Question Q1.)*
- **P1.2** The Gavel↔Level "matched pair" interplay surfaced to the user (subtract-a-flaw always paired with a self-compassion counterweight).
- **P1.3** WHO-5 (or short mood-trend) visualization back to the user as insight.
- **P1.4** Adaptive-difficulty learning from history (baseline rises only as demonstrated capacity grows).

### Future Considerations (P2) — design so as not to preclude

- **P2.1** Degrees / VIA strengths facets mapping onto the stone (Phase 2) — data model for "which facet did this practice refine" should exist even if unused in P1.
- **P2.2** Cooperative social layer (Phase 3) — keep the data model private-by-default and per-user; don't bake in assumptions that block later opt-in sharing.
- **P2.3** Conditional competitive experiment (Phase 4) — no architectural decisions that would require ranking-oriented data.

## Success Metrics

**Primary (lagging, wellbeing — the north star):**
- **WHO-5 wellbeing delta** over 4–6 weeks for active users (opt-in, gently paced). Success threshold: a positive, clinically-meaningful mean shift vs. baseline; stretch: comparable to the SuperBetter-class effect but validated against our own baseline, not vendor numbers.
- Optional PHQ-9 / GAD-7 deltas where the user opts in.

**Leading (behavioral, but *health*-oriented not vanity):**
- **Floor-task completion rate on low-mood days** (proves the adaptivity works — this is the mechanism, not raw DAU).
- **Comeback rate**: % of users who return within 3 days after a miss (proves the kind-streak design works).
- **Practice self-authoring rate**: % of users who create ≥1 self-worded, anchored practice (autonomy).

**Anti-harm instrumentation (must be built alongside, not after):**
- Flag **engagement-spike-then-churn-after-miss** (streak-anxiety signature).
- Verify **high-engagement users actually improve** on WHO-5 (catch the engagement–efficacy gap).
- Flag **compulsive-use** patterns (e.g., repeated late-night "don't break it" checks) — even though we don't show a breakable streak, watch for it.
- **Explicitly NOT a success metric:** time-on-device, session length, raw DAU. These are misaligned with user welfare here and must not be optimization targets.

## Open Questions

- **Q1 (design/founder, blocking scope):** For P0, do we ship the stone with **one** starter practice type or the **full four-tool set**? Recommendation: start with the **Gavel↔Level matched pair + Integrity Check** (3), since shipping "Letting Go" without its "Self-Compassion" counterweight is unsafe (self-attack risk). Daily Balance can be P1.
- **Q2 (design/founder):** Does the Milestone-0 test compare stone-framing vs. plain "wellness journey," or also test a *third* softer framing ("tending a garden")? Recommendation: two arms (stone vs plain) to keep the test clean; park the garden idea.
- **Q3 (engineering):** Can the existing on-device compounding-memory store surface "remembered context" fast enough for a warm greeting on cold app start, or do we need a cached digest? (Non-blocking; affects P0.5 latency.)
- **Q4 (data/clinical):** WHO-5 cadence — how often can we re-prompt without it feeling like a test? Recommendation: baseline + every ~2 weeks, skippable. (Non-blocking.)
- **Q5 (engineering):** Grace-day accounting — server-authoritative or fully on-device? (On-device preferred for privacy; confirm it survives clock changes / timezone travel.)

## Timeline Considerations

- **Milestone 0 (GATE — see §9):** the 5-user framing pre-test. **No stone visual system is built until this passes.** If "stone" tests as cold/objectifying, pivot the central metaphor *before* investing in art/animation.
- **Milestone 1:** P0.2–P0.5 loop working on-device (Working + kind streak + informational feedback + remembered companion) behind a placeholder stone.
- **Milestone 2:** P0.1 full stone visual system (only after Milestone 0 passes) + P0.6–P0.8.
- **Milestone 3:** anti-harm instrumentation + WHO-5 measurement live; begin outcome validation.
- **Dependency:** reuses the existing §9 crisis gate and on-device memory/persona — no new backend. Confirm both are in a state Phase 1 can build on.

---

## §9. Milestone 0 — the framing pre-test (gating protocol)

**Why it gates everything:** the entire Phase 1 visual and narrative system rests on one unproven assumption — that "you are a stone being worked" feels *empowering* to a distressed user. If it reads as cold or objectifying ("I'm a rock to be chiseled"), every downstream investment compounds a mistake. This is the cheapest, highest-leverage test in the whole plan.

**Method (lightweight, qualitative, n≈5–8):**
- Recruit 5–8 people matching the primary persona (have struggled with low mood; comfortable talking about it). Not a clinical trial — a reaction test.
- Show **two static onboarding mockups**, order counterbalanced:
  - **A — Stone framing:** "You're not broken — you're unfinished. A rough stone, being shaped over a lifetime. This app is your tools."
  - **B — Plain framing:** "Your wellness journey — small daily steps that add up."
- For each: capture first-reaction (one word), then probe — *Does this feel like it's on your side? Does "stone/being shaped" feel hopeful or harsh? Would you want to open this tomorrow?*
- Ask directly which they'd choose and why.

**Decision rule:**
- **Pass (build the stone):** stone framing is net-positive and not experienced as objectifying/cold by ≥ most participants; the "unfinished, not broken" reframe lands as hopeful.
- **Soften:** if it's mixed (hopeful for some, cold for others), keep the stone but adjust language (emphasize "not broken," de-emphasize "chiseled/struck") and retest one round.
- **Pivot:** if it reads as cold/objectifying for most, change the central metaphor before building. (Park a warmer nurture-metaphor — e.g., tending/growing — as the fallback; the *mechanics* in this spec are metaphor-independent.)

**Safety note for the test itself:** screen recruits are not in acute crisis; provide crisis resources; no mortality imagery in either mockup.

---

*Scope discipline: any addition to Phase 1 must come with a removal or an explicit timeline change. Good ideas that aren't P0/P1 go to the P2 parking lot or the Phase 2/3/4 backlog, not into this build.*
