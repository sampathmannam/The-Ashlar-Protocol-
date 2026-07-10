# The Ashlar Protocol — Master Plan

> The road from the current prototype to the app in [`VISION.md`](VISION.md). Read the vision first. Every claim referenced here is grounded in [`RESEARCH_BASIS.md`](RESEARCH_BASIS.md).

**Last updated:** 2026-07-04
**Guiding rule:** *Help is the only metric.* Ship the safety layer before anything else public-facing.

---

## ⚠️ STATUS RECONCILIATION (2026-07-10) — read this first

This document was written on 2026-07-04, **before the build-out**, and its per-phase ✅/⏳/⬜ markers below are now stale (they still say things like "package rename / Phase 3 connection — not started" that shipped weeks ago). The authoritative current state:

**Shipped and on `origin/main`** (native Android, `com.ashlarprotocol`, fully on-device, compiled + device-verified + tested — this doc's "written but not compiled" caveat no longer applies):

- **Phase 1** — the honest core: the tending stone (never zero-reset), the daily Working, the tools, WHO-5, and the §9 safety + mortality-audit layer.
- **Phase 2 — the Rite of Passage** — initiation, the **Degrees** engine driving tool-unlocking + a legible arc, and the **Raising** advancement ceremony.
- **Phase 3 — the Lodge (connection)** — the **West Gate** (zero-infra doorways to real people/community) + reach-out + the Well.
- **Package rename** (`com.example` → `com.ashlarprotocol`), and the **2C tool polish** (the Level's ~6-breaths/min resonance pacer; the Chamber's keep-or-release meaning mode; and the Gavel now a real "catch-and-square a rough corner" micro-practice, replacing the old placeholder drill).
- **Phase 4 / 4b — the discipline engine + faces** — an 8-feature build (F1–F8) integrating a deep research pass (the Cornerstone/environment design, elevated implementation-intentions, visible grace reserves, an honesty language-gate, automaticity signal, sleep-rhythm anchor, fresh-start ramp, and the Rough-Edge anti-addiction track). See [`RESEARCH_INTEGRATION.md`](RESEARCH_INTEGRATION.md) and the plans in `docs/superpowers/plans/`.

**The roadmap has been reframed** by the society pivot — see [`superpowers/specs/2026-07-09-society-pivot-design.md`](superpowers/specs/2026-07-09-society-pivot-design.md). Read that for current direction.

**Genuinely remaining** (nothing else in the phases below is unbuilt):
1. **The validation-first society petition** — deploy + measure demand (built, `feat/society-petition`; held pending a go-live decision).
2. **The society Phase 0 backend + payments** (accounts, sync, initiation fee + dues) — *deliberately deferred* behind the petition signal; it costs money and reverses the on-device model.
3. **On-device intelligence / personalization** (Phase 4 below) — genuinely unbuilt; needs a model the app doesn't ship and should wait until the static protocol proves out.
4. **Release signing / distribution** — the app is debug-signed only; a real signed release hasn't been cut.

Everything below is retained for historical context; trust this banner over its stale markers.

---

## Build status (2026-07-04)

Written in code, **not yet compiled or device-verified** (no JVM/SDK in the authoring environment; nothing committed — the working copy is a snapshot). Real red→green + the device pass live in [`VERIFICATION.md`](VERIFICATION.md).

- ✅ **Phase 1A — Crisis safety layer.** `safety/CrisisDetector.kt` (on-device, fail-safe, catches euphemisms) + always-on "NEED HELP?" + tappable hotlines, scanning wired into Chamber & journal. *(Verify on device — blocking.)*
- ✅ **Phase 1B — Repositioning.** Tactical/operator copy swept to the craftsman frame across all user-facing strings and the AI voice.
- ✅ **Phase 2 tools (logic + UI).** The Plumb (real 4-step CBT thought-record), the Gauge (behavioral activation), Mouth-to-Ear (real memory practice) — each pure-logic + unit tests. The Degrees logic + a Board card, with plumb/gauge/recall counts **persisted** and feeding progression.
- ✅ **Zero-cost / zero-network.** Removed the paid Gemini daily-briefing (a per-use cost + an extractable API key) → **bundled on-device word rotation** (`tools/DailyWord.kt`, tested). Dropped the `INTERNET` permission: the app now makes **no network calls at all**, so "nothing leaves the device" is OS-enforced. This is the architecture the no-budget constraint and the privacy north-star both demand.
- ✅ **Phase 2C polish.** The Level is a real ~6-breaths/min resonance pacer (`BreathPacer`, tested); the Chamber has a second **KEEP** (meaning) mode alongside cathartic release.
- ⏳ **Remaining Phase 2 — the rite of passage.** *(Mostly done — initiation, earned tools, and journey-as-home are built; see below.)*
- ⬜ **Phase 1C hygiene** (package rename, real release signing/keystore), **Phase 3** (connection), **Phase 4** (on-device intelligence) — not started.

---

## 0. Where we are today (honest audit)

**Stack:** Native Android — Kotlin, Jetpack Compose, Material 3, DataStore (local prefs), WorkManager (reminders). **Fully on-device — no network, no API keys** (the paid Gemini daily-briefing call was removed in favour of a bundled word rotation; the `INTERNET` permission is dropped). Google AI Studio scaffold, package `com.example` (**needs renaming** before release).

**What already exists and is good (keep):**
- **The Board** — dashboard: rough→perfect ashlar animation + "Refinement Chisel" slider; AI "Cognitive Briefing" with streak; resilience chart; After-Action journal with tags + 30-day heatmap; **Wisdom/Strength/Beauty pillar cards** (the spine is already here).
- **The Chamber of Reflection** — write → "purge" with haptics. Strong cathartic ritual organ.
- **The Tools** — Gavel (cognitive drill, *in Tamil* — keep this as a real differentiator), Plumb (thought-checking → CBT), Level (breathing), Mouth to Ear (memorization).
- **Aesthetic** — charcoal/gold/silver, film grain, quiet gravity. On-brand. Keep.
- **Privacy posture** — reflection data is already local-only. Aligns with the vision.

**What's wrong or missing (the work):**
1. **No crisis/safety pathway.** ← *blocking. Fixes in Phase 1.*
2. **Positioning is "tactical operator," not universal.** Copy, `SYS_LOCAL_430`, "operational weight," police-protocol content. ← *reposition in Phase 1.*
3. **The Plumb is a hardcoded stub** — returns a canned string, not real reflection. Mouth-to-Ear is a static police example. ← *make real in Phase 2.*
4. **No degree/progression system** — the single most motivating structure (§9) is absent.
5. **AI is one-shot and cloud-only** — a generic daily mantra; no memory, no personalization, no offline path. Contradicts the on-device-memory principle.
6. **`com.example` package, AI Studio README, debug signing** — pre-release hygiene.
7. **Content is thin/placeholder** — real evidence-based practices need writing (grounded in `RESEARCH_BASIS.md`).
8. **Metrics/telemetry** — none, and by design we only ever add *privacy-safe, local* self-insight, never surveillance analytics.

---

## Phase 1 — Foundation & Safety *(blocking for any release)*

**Goal:** make it safe, make it universal, make it shippable. Nothing public ships before this is done.

### 1A. Crisis Safety Layer *(highest priority, non-negotiable)*
- **On-device crisis detection** on every free-text surface (Chamber, After-Action journal, and any future reflective input). Must be **fail-safe**: detection cannot depend on a cloud call succeeding. Start with a high-recall lexical/rule pass; a small on-device classifier can follow (mirrors the maker's proven pattern elsewhere).
- **Unconditional crisis surface:** when intent is detected, immediately present tappable, localized human help (e.g., 988 in the US, and a maintained per-region list) — never buried behind AI, never conditional on "are you sure."
- **Standing help access:** a quiet, always-reachable "Need help now?" affordance, reachable even during onboarding.
- **Disclaimers:** clear, repeated, honest — "a practice, not a clinician; not for emergencies."
- *Evidence:* this is an ethical floor, not a feature. See [`VISION.md`](VISION.md) §8.

### 1B. Reposition to universal ("craftsman, not commando")
- Rewrite copy from "tactical professional" → "any person doing the work." Replace `SYS_LOCAL_430`/`SECURE`/"operational weight" with craft language ("the work," "the quarry," "lay down the weight").
- Preserve the tactical voice as an **optional theme** ("The Operator's Path") in settings — waste nothing.
- Reframe Mouth-to-Ear away from police protocols toward memorizing *chosen principles/affirmations/values* (or keep the Tamil-language angle as a cognitive-and-heritage practice).

### 1C. Pre-release hygiene
- Rename package `com.example` → e.g. `app.ashlar` (or chosen domain). Replace the AI Studio README with a real one. Proper release signing + a backed-up keystore (learn from prior projects: **back up the keystore**). App icon, store metadata.
- Decide the AI stance: keep cloud Gemini for now behind a clear seam, but design toward the on-device path (Phase 4) so private text need never leave the device.

**Phase 1 done when:** a stranger can install it, understand it's for them, use it safely, and be caught if they're in crisis.

---

## Phase 2 — The Rite of Passage *(the core product)*

**Goal:** make **the journey the spine.** The tools now exist as real practices; the remaining work is to stop presenting them as a flat toolbox and re-form the whole app as a *staged rite of becoming* (see [`VISION.md`](VISION.md) §6). This is the decision that separates "a mental-health app with Masonic branding" from "a genuine Masonic rite of self-becoming."

**The organizing insight:** the Craft's three degrees map onto the actual arc of therapy — *stabilize behavior (Entered Apprentice) → work the mind (Fellowcraft) → find meaning and turn outward (Master Mason).* Build the IA around that arc.

### 2A. Make the journey the home *(the new centerpiece — ⏳ remaining)*
- **Initiation, not a form.** Rebuild first-run as a rite in the **Chamber of Reflection** — a few quiet, solemn questions on where the user stands and what they want to become. Doubles as a gentle clinical baseline. Tone: a threshold with weight, never a test you can fail.
- **Tools received, not listed.** Re-form the flat Tools menu into the **degree path**: Apprentice tools (Gauge, Gavel) open from day one; Fellowcraft/Master tools sit *veiled* ("these await you") and are *given* on advancement. Pedagogy, not paywall — skills arrive in the order that helps.
- **The path is the home screen.** Promote the Degree from a Board card to the app's spine — the rough ashlar visibly becoming true, everything hung from where the user stands on it.
- **The hard line:** gate *depth*, never *safety*. Crisis help, the Chamber's release, and basic grounding are always open, degree zero. No one in distress is ever told to come back at a higher degree.

### 2B. The Degrees engine — *§9* *(✅ logic + persistence built; ⏳ wire to gating/home)*
- Progression logic (`tools/Degrees.kt`), the Board card, and persisted plumb/gauge/recall signals are done. Remaining: drive **tool-unlocking and the home screen** from it, and tune pacing so the *first* advancement comes fast (early small win) then deepens.
- Earned through *practice*, framed as competence/mastery — never pay-gated, never shame-based; streaks encourage, never punish a relapse.

### 2C. Deepen the tools *(✅ core built; ⏳ polish)*
- **The Plumb** (CBT thought-record) and **the Gauge** (behavioral activation) and **Mouth-to-Ear** (memory practice) are real. Remaining polish: **The Level** — tune the pacer to **~6 breaths/min (0.1 Hz resonance)**, not an arbitrary rate (§8); **The Chamber** — add an optional *reflective* mode oriented to **meaning and values**, not "reflect on death" (§3, §10), with keep-or-release per entry; **The Gavel** — evolve the Tamil drill into a real "notice-and-interrupt a pattern" micro-practice.

### 2D. The Three Pillars dashboard — *§7 (as heuristic)*
- Evolve the Wisdom/Strength/Beauty cards into a gentle whole-person view that shows which pillar is thin this week and suggests one small action. A *lens*, never a clinical score.

**Phase 2 done when:** a new user is *initiated* rather than onboarded, receives tools as they advance, and experiences the app as a journey of becoming — while every safety path stays open at degree zero.

---

## Phase 3 — The Lodge (connection) — *§2, §11: the strongest mechanism*

**Goal:** address the single biggest protective factor — social connection — which is also the Craft's deepest principle (brotherhood, meeting on the level, relief).

This is the highest-leverage and hardest phase. Sequence carefully; privacy and safety multiply in a social context.

- **Start solo-social:** "Relief" practices that turn the user outward *without* a full social network — reach-out prompts to a trusted person, acts-of-kindness / gratitude practices (well-supported), a bridge to real-world community.
- **Then, cautiously, peer belonging:** explore lightweight, privacy-preserving forms of shared practice or accountability (anonymous-by-default, heavily moderated, safety-gated). Learn from the Men's Sheds model (§11) — belonging through *shared doing*, not a feed.
- **Never** a dopamine social feed, never public vulnerability by default, never engagement-farming.

**Phase 3 done when:** the app measurably reduces a user's isolation without ever compromising privacy or safety.

---

## Phase 4 — On-Device Intelligence & Personalization

**Goal:** make the companion feel like it knows you — **without your inner life ever leaving the device.**

- **Personalization = growing on-device memory retrieved into context**, never per-user model changes and never server-side profiling of private text. (Directly mirrors the maker's established AI north-star.)
- Move the reflective/AI features toward an **on-device model** path so the Chamber and journal need no network. Cloud stays only for non-sensitive extras (e.g., the generic daily briefing), clearly seamed.
- The AI is a *scribe and mirror*, never an oracle: it reflects the user's own patterns back to them, privately — their patterns, for them.
- **Reliability is a blocking gate:** no hallucinated psychological advice. Safety (Phase 1) remains model-independent regardless of what the AI does.

**Phase 4 done when:** a user can do their entire private practice offline, and the app's "memory" makes it feel personal without a byte of confession leaving the phone.

---

## Cross-cutting: how we'll measure "help" (without surveillance)

We refuse engagement/retention as success metrics. Instead, **privacy-safe, on-device, user-visible** signals only:
- The user's *own* self-rated sense of progress over time (their data, shown to them).
- Whether they report the tools helped (optional, local check-ins).
- Whether the crisis pathway reliably fires in testing (safety QA, not user surveillance).
- Qualitative: does a real person say this changed something? (opt-in, never harvested).

If we ever can't tell whether we're *helping*, that's the problem to solve — not a number to inflate.

---

## Sequencing summary

| Phase | Theme | Blocking? | Core mechanism |
|-------|-------|-----------|----------------|
| **1** | Foundation & Safety | **Yes — gates all release** | Ethical floor + universal repositioning *(✅ built, unverified)* |
| **2** | The Rite of Passage | High | The journey as spine — initiation, earned tools, degree-driven home *(tools ✅; rite ⏳)* |
| **3** | The Lodge (connection) | High-leverage | Social connection (§2,§11) — earned as the Master Mason degree |
| **4** | On-Device Intelligence | Ongoing | Private, on-device personalization |

---

## Immediate next actions (when build resumes)

1. **Verify what's built** — `./gradlew testDebugUnitTest` (real red→green on the pure-logic suites) + the device pass in [`VERIFICATION.md`](VERIFICATION.md), §1 crisis pathway first. Nothing below matters until this is green.
2. **Begin the rite of passage** (2A) — the initiation flow in the Chamber, then gate the Tools menu by degree (Apprentice tools open; later tools veiled), keeping every safety path open at degree zero.
3. **Make the Degree the home** (2A/2B) — promote it from a card to the app's spine and drive tool-unlocking from it.
4. **Pre-release hygiene** (1C) — rename `com.example`, real release signing, **back up the keystore**.
5. **Polish the remaining tools** (2C) — the Level's 6-breaths/min pacer; the Chamber's reflective/meaning mode.

*The perfect ashlar is a direction, not a deadline. Build it true, not fast.*
