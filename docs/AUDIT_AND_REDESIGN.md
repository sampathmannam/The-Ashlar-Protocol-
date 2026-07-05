# The Ashlar Protocol — Audit & Redesign

> A full-scale audit of the built app, a substance-first redesign plan, and research-grounded ideas
> for what to add. Companion to [`VISION.md`](VISION.md), [`MASTER_PLAN.md`](MASTER_PLAN.md), and
> [`RESEARCH_BASIS.md`](RESEARCH_BASIS.md). **Every claim below carries an honest confidence level;
> we never fabricate and never overclaim** — the same discipline the app itself must hold.

**Date:** 2026-07-05 · **Status:** proposal, pending build. The app is built, installed, and running.

---

## 1. Audit — what's actually there (honest, prioritized)

### 🔴 P0 — The Board shows *fabricated* data about people's mental health
This is the most important finding and it contradicts the app's soul.
- **"Cognitive Resilience & Streak" chart = `Random.nextFloat()`** (`ResilienceChartCard.kt`). Literally random numbers drawn as a resilience trend.
- **"Pillar of Wisdom" chart = a hardcoded list** (`INITIAL_WISDOM_DATA` in `WisdomPillar.kt`) — the same fake line for every user.
- **"Strength: 18.5/25 KM" and "Beauty: Guitar Practice"** = placeholder constants.

An app whose north-star is *"never fabricate, honest claims only"* must not show a suffering person an invented "resilience trend." **This is blocking. Fix before anything else.**

### 🟠 P1 — The app forgets you the moment you enter
The initiation rite asks *"what weighs on you"* and *"what are you working toward,"* then **never uses the answers** (verified: `intention`/`baselineWeight` are captured in DataStore and read nowhere). The most intimate moment in the app is discarded. It is a beautiful but **impersonal** toolbox.

### 🟠 P1 — The work vanishes
Plumb thought-records (real CBT work) are **one-shot and not persisted** — there is no history, no *"look how you reframed that last week."* Only the Chamber's "keep" mode saves anything. Practice with no memory can't show growth.

### 🟡 P2 — Gaps and rough edges
- **No ongoing mood/state self-monitoring** — the most common evidence-based MH-app loop, absent.
- **Thin psychoeducation** — assumes the user knows what a cognitive distortion is, why 6-breaths/min works.
- **Nav semantics** — the Chamber's icon is a *trash can* (`Icons.Default.Delete`).
- **Accessibility unaudited** — gold-on-charcoal contrast, touch targets, screen-reader labels, text scaling.

### ✅ Strengths to keep
The rough→perfect-ashlar metaphor; the rite-of-passage IA (initiation → earned tools → journey-as-home); the on-device fail-safe crisis layer; five evidence-grounded practices; fully on-device / no-cost; the solemn, adult aesthetic (a real differentiator).

---

## 2. The redesign — a *substance* overhaul, not a visual one

The aesthetic and rite structure are assets; don't touch them. The redesign makes the app **honest** and makes it **remember you**. In priority order:

1. **The Honesty Pass** *(blocking, fast)* — remove every fabricated datapoint. **Do not replace fake charts with an overclaimed "resilience score"** (that trades fabrication for a subtler dishonesty — see §3, progress-feedback caveat). Replace with **honest, literal reflection of real activity** ("here's what you did; here's how you said you felt"), framed as self-awareness, and honest empty states where there's no data yet.
2. **Make it remember** — surface the initiation *intention* on the Board (*"You're working toward: ___"*); persist Plumb thought-records for review; make the baseline the first real point of an honest self-report trajectory. (On-device memory, the pattern from NilaMind.)
3. **A gentle daily check-in** — a short state self-report, for self-insight and *real* data, framed honestly (not a treatment claim), and never a guilt-streak.
4. **Deepen the tools with the evidence** — if-then plans in the Gauge; gratitude and self-compassion as Chamber/Beauty practices; a values exercise that *becomes* the intention.
5. **Retention via the Lodge, not streaks** — lean on supportive accountability (real connection); audit every streak for the shame case.

**The fork, resolved:** stay a tool-and-rite app; make it personal through **memory + real human connection**, not a synthetic chatbot voice. The research points the same way (§3): the ethical engagement driver is a *caring human*, not app mechanics — and that's the Lodge, not a bot.

---

## 3. Research-grounded ideas (with honest confidence)

Ordered by strength of evidence (strongest, safest-to-build first). All effect sizes verified against primary sources.

| Idea | Do what | Evidence | Confidence |
|---|---|---|---|
| **Implementation intentions** | Turn Gauge items into *"if [cue], then I will [action]"* | 94 tests, **d = 0.65** overall, **d = 0.59** health-behaviour ([Gollwitzer & Sheeran 2006](https://doi.org/10.1016/S0065-2601(06)38002-1)) | **STRONG — best bang-for-buck; build first** |
| **Behavioral Activation (the Gauge)** | Keep it central; optionally add mood-around-activity | BA ≈ full CBT ([Cochrane 2020, RR 0.99](https://pubmed.ncbi.nlm.nih.gov/32628293/)); **SMD −0.74** vs controls ([Ekers 2014](https://pubmed.ncbi.nlm.nih.gov/24936656/)) | **STRONG** — the app's anchor. (Mood-rating step is a BA *component*, not separately proven.) |
| **Self-compassion** | A short guided practice — **with a gentle exit** | MSC program: dep **g ≈ 0.66**, self-comp **g ≈ 0.75** ([Ferrari 2019](https://doi.org/10.1007/s12671-019-01134-6); [Kirby 2017](https://pubmed.ncbi.nlm.nih.gov/29029675/)) | **STRONG (multi-week program)**; a *single* break shifts state only and can cause **"backdraft"/distress** in some → **§9-relevant, needs care** |
| **Values clarification (ACT)** | A short exercise → *becomes* the intention; anchors the Plumb | Full ACT **g ≈ 0.57** ([A-Tjak 2015](https://doi.org/10.1159/000365764)); values-in-isolation only *lightly* tested ([Levin 2020 dismantling](https://doi.org/10.1016/j.brat.2020.103557)) | **Strong as an ACT ingredient**; not proven standalone |
| **Daily state self-monitoring** | Gentle check-in that **feeds a skill**, not a bare tracker | Reactivity small (**d ≈ 0.27–0.30**); standalone apps small (**g ≈ 0.28 dep / 0.26 anx**), and **content beats bare tracking** ([Linardon 2024](https://doi.org/10.1002/wps.21183)) | **Moderate** — self-awareness, never a treatment claim |
| **Worry postponement** | A "set it aside till a worry-time" option (anxiety) | CBT component; small standalone (**d ≈ 0.2–0.3**, [Dippel 2023](https://doi.org/10.1007/s41811-023-00193-x)); full CBT-for-GAD large (g ≈ 1.0) | **Moderate** — a supported technique, not a full GAD treatment |
| **Three Good Things / gratitude** | A light Beauty-pillar habit | **Largely evaporates vs active controls** ([Davis 2016](https://doi.org/10.1037/cou0000107), d ≈ −0.03); small for dep/anx (**g ≈ −0.23**, [Cregg & Cheavens 2021](https://doi.org/10.1007/s10902-020-00236-6)); durability needs *continued* practice ([Seligman 2005](https://doi.org/10.1037/0003-066X.60.5.410)) | **Modest** — a gentle wellbeing habit, **not** a treatment |
| **Psychoeducation** | Brief "why this works" notes | Passive psychoed **d ≈ 0.20** on dep/distress; best as adjunct ([Donker 2009](https://pubmed.ncbi.nlm.nih.gov/20015347/)) | **Small alone** — a component, not a cure |
| **Sleep (Gauge's "rest")** | General wellness tips only; point to CBT-I for insomnia | Full CBT-I first-line/strong ([Trauer 2015](https://doi.org/10.7326/M14-2841); [ACP 2016](https://pubmed.ncbi.nlm.nih.gov/27136449/)); **sleep hygiene alone NOT recommended** ([AASM 2021](https://doi.org/10.5664/jcsm.8986); [Irish 2015](https://doi.org/10.1016/j.smrv.2014.10.001)) | **Position as wellness, never a cure** |

### Three honesty caveats that shape the build
- **Self-serve progress feedback is an evidence *gap*.** The strong evidence that showing outcome data improves outcomes is **clinician-mediated** — the graph goes to a *therapist* ([Shimokawa & Lambert 2010](https://pubmed.ncbi.nlm.nih.gov/20515206/)). No robust evidence a self-serve app's trajectory reproduces that → the honest-trajectory feature is **self-awareness, not a treatment mechanism.**
- **A single self-compassion / gratitude prompt is not the program.** The strong effect sizes belong to multi-week programs. A one-off exercise can even surface distress (self-compassion "backdraft"). Offer a **gentle exit** and never claim single-session therapeutic effect.
- **Bare mood-tracking and psychoeducation are weak alone.** They must *feed* an active, evidence-based skill (BA, if-then, CBT) — content beats tracking (Linardon 2024).

### Accessibility (WCAG 2.2) — one reassurance, one real fix
- **Reassurance:** the gold `#C9A227` on charcoal `#1A1A1A` computes to **≈ 7.19:1** — passes AA for normal text (4.5:1) and *just* clears AAA (7:1) ([WCAG 1.4.3](https://www.w3.org/WAI/WCAG22/Understanding/contrast-minimum)). The palette is genuinely accessible; the margin is thin, so verify small/hairline gold type in context, and use an off-white (`#E0E0E0` ≈ 13:1) for long body text.
- **Real fix — [WCAG 1.4.1 Use of Color](https://www.w3.org/WAI/WCAG22/Understanding/use-of-color.html):** the app uses **gold as the *only* cue** for tappable text (e.g., "reach someone you trust," "ANOTHER"). Color-alone fails 1.4.1 — add a second cue (underline, icon, or button chrome). Also ensure gold icon/border states hit **3:1** ([1.4.11](https://www.w3.org/WAI/WCAG22/Understanding/non-text-contrast.html)).

---

## 4. Engagement & ethics — the reality check

- **~96% of people leave fast.** Real-world MH-app retention is **~4% at day 15, ~3.3% at day 30** ([Baumel et al. 2019](https://doi.org/10.2196/14567)); attrition is the norm, not failure ([Eysenbach 2005](https://doi.org/10.2196/jmir.7.1.e11)). **Implication:** optimize so the *few sessions someone actually does* deliver value — "effective engagement," benefit over time-on-app ([Perski et al. 2017](https://doi.org/10.1007/s13142-016-0453-1)). This *is* "help is the only metric."
- **Streaks are a double-edged sword.** Gamification's outcome evidence is **mixed/low-certainty** ([Cheng et al. 2019](https://mental.jmir.org/2019/6/e13717/)); streaks/reminders can act as dark patterns (guilt, obligation). *Honest caveat:* no high-quality clinical study proves streak **harm** in a MH app specifically — it's a design-ethics concern, not a proven finding. But the app's own vision says *never shame a relapse*; **audit every streak for the shame case.**
- **The ethical retention lever is a caring human.** "Supportive accountability" — feeling answerable to a benevolent person — drives adherence without coercion ([Mohr et al. 2011](https://doi.org/10.2196/jmir.1602)). That is the **Lodge / reach-out**: real connection is simultaneously the strongest *wellbeing* mechanism (Holt-Lunstad, RESEARCH_BASIS §2) and the most *ethical* retention mechanism.

---

## 5. Sequenced plan

| Step | Work | Gate |
|---|---|---|
| ~~**1**~~ | ~~**Honesty Pass** — remove all fabricated data; honest reflection + empty states~~ | ✅ **DONE + device-verified (2026-07-05)** — fake charts/pillars removed; "The Work So Far" shows real counts only |
| **2** | **Memory** — surface intention; persist Plumb records; real baseline | High value — next |
| **3** | **Daily check-in** — gentle self-report, honestly framed | Feeds §1's real data |
| **4** | **Evidence deepening** — if-then Gauge; gratitude; self-compassion; values | Highest-effect adds |
| **5** | **Retention re-frame** — soften streaks (shame audit); lean on the Lodge | Ethics |
| **6** | **Polish** — nav icons, accessibility/contrast pass | P2 |

*Recommended first build: the Honesty Pass. An app built on "never fabricate" cannot show fabricated mental-health data — and it forces the real-data foundation everything else needs.*
