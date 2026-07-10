# The Ashlar Protocol — Research → Integration Dossier

**What this is:** the captured output of a deep research pass on *building discipline, mental wellness, and breaking addictions*, audited against the app as it actually exists on `origin/main`, with the feature specs required to fold the research into a highly-integrated, state-of-the-art product.

**What this is NOT:** the sequenced build-plan. Per instruction, the ticketed implementation plan (files, TDD steps, order) is a **separate later step**. This document is the evidence, the audit, the gaps, and the *what/why/how* of each feature — the thing every future ticket must trace back to.

**Discipline this obeys** (from the project's north-star): every design decision cites its evidence and hedges to true confidence; nothing is fabricated; effect sizes and their limits are stated honestly. Confidence tags: **[HIGH]** replicated/meta-analytic, **[MOD]** strong but single-sample/inferential, **[CONTESTED]** active dispute, **[POP]** popular-science, not a validated construct.

---

## 0. The one-paragraph thesis

Discipline is not willpower. The people with the most self-control exert the *least* effortful resistance — they win by **habits, environment, and identity** that make the good choice automatic and the bad choice high-friction. Achievements/points don't durably change behavior and can corrode the intrinsic motivation that discipline runs on. So the product is not a task-list with rewards; it is an **engine**: small, values-anchored daily actions → cued by if-then plans and a designed environment → sustained by autonomy, identity, and connection → recovered with forgiving, framed-reserve mechanics. **Discipline, wellness, and breaking an addiction are three *faces* of that one engine, not three products.** Ashlar already embodies most of it; this dossier closes the specific gaps.

---

## 1. The evidence base (consolidated, confidence-flagged)

### 1.1 The core reframe — discipline ≠ willpower
- **Galla & Duckworth (2015)**, *JPSP* 109(3):508–525, 6 studies, N=2,274: trait self-control predicts good outcomes **through beneficial habits and automaticity**, with *less* effortful inhibition. **[HIGH]**
- **de Ridder et al. (2012)**, *PSPR*, meta-analysis k=102, N=32,648: trait self-control predicts **automatic/habitual** behavior far more than effortful behavior. **[HIGH]**
- **Hofmann, Baumeister, Förster & Vohs (2012)**, *JPSP* 102(6): experience-sampling, 7,827 desire reports — high self-control people simply *feel fewer/weaker desires*, they don't out-muscle them. **[HIGH]**
- **Milyavskaya & Inzlicht (2017)**, *SPPS*: goal attainment is predicted by *fewer temptations*, not more resisting; effortful resistance was ~unrelated to attainment and predicted more depletion. **[MOD]**
- **Ego depletion is effectively dead:** Hagger et al. (2016, 23 labs, null) and **Vohs et al. (2021)**, *Psych Science*, 36 labs, N=3,531, **d=0.06, Bayesian 4× toward null** (co-led by an original proponent); the glucose model has "little evidential value" (Vadillo). **[HIGH]** Do not build willpower-budget/"you're out of discipline" mechanics.
- **Moffitt et al. (2011)**, *PNAS*, Dunedin cohort N=1,000 birth→32: a childhood **self-control** gradient predicts adult health, wealth, and criminal record, independent of IQ and class. **[HIGH]**
- **Grit is oversold:** Duckworth (2007) grit R²≈.04; **Credé et al. (2017)**, meta k=88 N=66,807: grit ≈ conscientiousness (r≈.84, one construct), perseverance does the work, passion adds little. **Sell "consistency/conscientiousness," never "grit/toughness."** **[HIGH]**
- **Situational strategies** (Duckworth, Gendler & Gross 2016): 5 leverage points — *situation selection → situation modification → attention → reappraisal → response inhibition (raw willpower, the last/weakest)*. **Intervene early; willpower is the last resort.** **[MOD, model]**

### 1.2 Habits & implementation intentions
- A habit is **context-cued automaticity**, not frequency (Wood & Rünger 2016; Gardner 2015): "cue → response" learned by repetition in a *stable context*, goal-independent once formed. **Frequency ≠ habit.** **[HIGH]** Measure **automaticity** ("I did it without thinking"), not just completion counts.
- **~43% of daily behavior is habitual** (Wood, Quinn & Kashy 2002: 35% Study 1, 43% Study 2 — "a third to a half"). **[HIGH]** (Cite 35–43%, not a bare "45%.")
- **Lally et al. (2010)**: automaticity forms in a **median 66 days, range 18–254** (kills "21 days," a distortion of Maltz 1960). **A single missed day cost <0.5 pts and "did not materially affect" formation — but a *week* of misses does hurt.** ~half never habituated. **[HIGH]**
- **Implementation intentions** (Gollwitzer 1999; **Gollwitzer & Sheeran 2006**, 94 tests, **d=0.65**): "When situation X, I will do Y" delegates control to a cue → automatic initiation (mediated by cue accessibility + cue-response strength, Webb & Sheeran 2007). **[HIGH]** *Boundary conditions to respect:* (a) they **amplify a committed goal, they don't create motivation** — if commitment is weak, they do little; (b) real-world effects are *smaller and decay*: physical activity g≈0.31→**0.24 at follow-up** (Bélanger-Gravel 2013), cutting a bad habit d≈0.29 (Adriaanse 2011), mental-health d≈0.99 rests on an outlier-excluded N=1,636 with acknowledged publication bias (Toli 2016). **Direction rock-solid; magnitude modest. Position as "a cheap converter of existing commitment," not a big lever.**
- **Environment/friction is the real lever** (Wood 2019, *Good Habits, Bad Habits*): reduce friction for good behavior, add friction for bad; **habit-discontinuity** — a changed context (move, new job, transition) removes old cues and opens a change window. **[HIGH, directional; book synthesis].** Caveat: *self-directed* environment design is well-supported; third-party population "nudges" are **[CONTESTED]** (Maier et al. 2022 collapse the nudge meta to ~0). Frame as "*you* engineer *your* environment," never "we nudge you."
- **Fogg B=MAP** (2009): Behavior = Motivation × Ability × Prompt at one moment; lower the ability cost ("make it tiny") and less motivation is needed. **[PEER-REVIEWED model, lightly validated]** — one 2025 scoping review found only 6 qualifying studies. Tiny Habits/celebration and "habit stacking" = sound underlying mechanism, thin *branded-method* RCT evidence (one gratitude RCT, d=0.85, high attrition). Use as heuristics; don't oversell.
- **"Keystone habits" [POP]:** Duhigg's coinage, not a validated construct. The nearest science — behavioral spillover (Maki et al. 2019 meta, 22 studies) — finds cascades *small at best and sometimes negative* (d+≈0.17 on intentions, ~0 on behavior). **Never market a domino cascade as science;** frame a strong habit as a plausible on-ramp, not a chain.

### 1.3 Misses, streaks, self-compassion — the design crux
- **A punitive zero-reset streak is plausibly iatrogenic.** It manufactures the **what-the-hell effect** (Cochran & Tesser; Polivy & Herman) and the **abstinence violation effect** (Marlatt & Gordon) — lapse + self-blame → "I've blown it" → quit. **Perfectionistic concerns** (self-critical) predict depression at **r≈.40** (Limburg et al. 2017, 284 studies) — so all-or-nothing mechanics harm exactly the most at-risk users. **[HIGH]**
- **Framed "emergency reserves" beat both rigid streaks and silent slack** — the single most actionable finding. **Sharif & Shu (2017/2019)**: a small number of skips *framed as a scarce reserve* raised next-day rebound after a miss to **~55% vs ~37%** (rigid), because a labeled, limited reserve carries a psychological cost so people *protect* it. Duolingo's streak-freeze echoes it (+3.3% D14 retention). **[HIGH for the mechanism; MED for the Duolingo numbers]**
- **Self-forgiveness after a lapse → less future lapsing** (Wohl et al. 2010, via reduced negative affect; Adams & Leary 2007 *causal* — a one-line self-compassion prime cut post-violation disinhibition; Sirois 2015 meta, self-compassion↔health behaviors r≈.25). **[MOD–HIGH]**
- **Fresh-start effect** (Dai, Milkman & Riis 2014): temporal landmarks ("new week," "today") let people file past failure into a "previous period" and re-engage. **[HIGH]** Design *offers* a restart, never forces a broken counter.
- **Goal-gradient** (Kivetz et al. 2006): effort accelerates toward a visible goal; *perceived* progress motivates (pre-filled progress completes faster). Useful for a legible arc — **but avoid the post-reward drop-off and the loss-aversion cliff of a hard reset.** **[HIGH]**

### 1.4 Motivation, identity, meaning
- **Self-Determination Theory is the best account of *sustained* motivation** (Deci & Ryan): **autonomy, competence, relatedness**. **Ng et al. (2012)** meta, 184 datasets: autonomy support → autonomous motivation → better mental/physical health. **Teixeira et al. (2012)**, 66 studies: **autonomous** motivation predicts long-term adherence; **guilt/introjected** regulation predicts short-term compliance that then *collapses*. **[HIGH]** → autonomy-supportive design (choice, rationale, non-judgment); ban guilt/shame/pressure.
- **Overjustification** (Lepper 1973; **Deci, Koestner & Ryan 1999** meta): tangible, expected rewards **undermine** intrinsic motivation. **[HIGH]** → points/badges on a self-improvement behavior are a latent liability.
- **Identity is sound framing, not a proven lever.** Bem self-perception (behavior → self-concept); habit-identity r≈.55 (Zhu 2025 meta); non-smoker identity predicts abstinence (Vangeli & West 2012). *But* the one RCT that *directly added* identity content found **no advantage** (Husband/Rhodes 2019). **[HIGH that identity correlates with maintenance; LOW that targeting it is a proven cause]** → use identity framing (low-risk, mechanistically sound); don't *claim* it as a validated intervention. Same for "systems > goals": the parts are real, the slogan isn't tested.
- **Meaning/values durably help:** self-concordant goals → upward spiral (Sheldon & Elliot 1999) **[HIGH]**; purpose-in-life → lower all-cause mortality over 14y (Hill & Turiano 2014, MIDUS ~7,000) **[HIGH, observational]**; ACT values-action d≈0.42 but not superior to CBT, isolated values-component under-tested **[MOD]**.
- **Approach > avoidance framing** (Elliot 1999): avoidance goals ("don't fail," "stop X") are resource-depleting and predict lower wellbeing/attainment. **[HIGH]** **Process > outcome goals** (process d=1.36 vs outcome d=0.09, Williams 2022 — sport, inflated but directionally reliable). → track *"you did the thing,"* never *"you're X% less depressed."*

### 1.5 Daily practices, ranked by evidence (headline the top; never headline the bottom)
- **ROBUST:** **Social connection** — Holt-Lunstad et al. 2010, 148 studies/308k, **OR 1.50** (~on par with quitting smoking) — *the strongest lever in the whole corpus* **[HIGH]**. **Movement** — Singh 2023 umbrella (97 reviews): depression −0.43, anxiety −0.42 **[HIGH]**. **Behavioral Activation** — Ekers 2014, 26 RCTs, **SMD −0.74** (≈ full CBT), simple, low-dropout **[HIGH]**. **Sleep-wake *regularity*** — Windred 2024 (UK Biobank N=60,977, mortality HR 0.70) + Li 2025 (N=79,666, incident depression HR 0.62) — regularity beats duration; **[HIGH association, OBSERVATIONAL]**.
- **REAL BUT MODEST / OVERHYPED (include, don't headline):** mindfulness (Goyal 2014 d≈0.3, no better than active controls; Van Dam 2018 "Mind the Hype") **[MOD real / HIGH overhyped]**; nature (White 2019, ≥120 min/wk, correlational).
- **WEAK / GARNISH (never the promise):** gratitude (Cregg & Cheavens 2021 small; authors themselves say symptom-seekers use stronger tools); expressive writing (Frattaroli 2006, r≈.075); general positive-psychology interventions shrink toward null after bias correction (White/Uttl/Holder 2019). **[HIGH that these are overclaimed]**
- **Clinical, cite carefully:** rhythm-stabilization is most evidence-backed exactly where clinical risk is highest — **bipolar** (IPSRT, Frank 2005 RCT, HR 0.34). A cite-and-handoff line, not a casual feature claim.

### 1.6 Breaking addictions (incl. behavioral/digital)
- **The slip is the whole design decision.** Anti-**AVE** (Marlatt): a lapse ≠ relapse; *catastrophizing* it drives relapse. **A hard streak reset for someone quitting a compulsion delivers the exact shame hit that causes relapse.** **[HIGH]**
- **Cue/environment control beats willpower** (§1.1–1.2): remove/av­oid triggers, add friction; digital self-control tools (Lyngs et al. 2019). **[HIGH directional]**
- **Urge surfing** (Bowen/Witkiewitz MBRP): a craving is a wave that crests and passes; **suppression backfires** (Wegner ironic process). *In-the-moment* riding — **not** constant craving-surveillance (Milyavskaya: more logged temptations → worse). **[MOD]**
- **Replacement, not deletion** (Wood): you swap the routine for the same cue/reward; you can't null a habit loop.
- **Identity + connection + self-efficacy** (Bandura) carry recovery; and a hard line: substance addiction is **clinical** — unshaming **handoff**, never pretend to treat.

### 1.7 Gamification — the trap, quantified
- Meta-analyses of gamified mental-health apps: **no significant clinical-outcome advantage** from gamification (depression apps overall Hedges g≈−0.27, but **gamification not a significant moderator**); the **"Engagement–Efficacy Gap"** — gamification buys engagement, not outcomes (MDPI 2026 review). **[HIGH]** Combined with overjustification (§1.4): **points/badges/leaderboards are the weakest and most dangerous lever.** Hold the line.

---

## 2. The current app, audited against the research

Ashlar was built research-grounded, so it is already **~75% aligned**. Feature-by-feature (`origin/main`, `com.ashlarprotocol`):

| Current feature (file) | Maps to research | Verdict |
|---|---|---|
| **Tending stone, never zero-reset** (`KindStreak`, `stoneProgress`) | anti-AVE, anti what-the-hell, Lally "one miss ≠ reset" | ✅ **Aligned** (best-in-class) |
| **Grace days, cap 2** (`KindStreak.MAX_GRACE`) | Sharif & Shu emergency reserves | ⚠️ **Refine** — currently *silent/auto-applied*; evidence says make them **visible, scarce, protectable** |
| **Practices — "AFTER [anchor], I will [action]," approach-guarded** (`PracticeAuthoring`) | implementation intentions (d=0.65), approach>avoidance | ✅ **Aligned** — but under-surfaced; should be **first-class + explicitly cued + downstream of commitment** |
| **The Gauge — "Divide the Day"** (`TwentyFourInchGauge`) + **Working** (mood-adaptive) | Behavioral Activation (SMD −0.74) — the strongest causal daily practice | ✅ **Aligned** (undersold — this is a headline practice) |
| **The Trowel — self-compassion / common humanity** | self-forgiveness after lapse (Adams & Leary, Wohl) | ✅ **Aligned** |
| **Comeback message** (`KindStreak.comebackMessage`) | fresh-start effect, self-compassion re-entry | ✅ **Aligned** — could add an explicit "new week, take up the stone" landmark |
| **The West Gate — connection doorways** (`WestGate`) | Holt-Lunstad (OR 1.50, the strongest lever) | ✅ **Aligned** — should be *elevated to headline*, not a Chamber footer |
| **Degrees + the Raising + veiled tools** (`Degrees`, `Advancement`) | identity-based change, competence feedback (informational, not reward) | ✅ **Aligned** — keep as *framing*; don't *claim* identity as a proven lever |
| **Intention / Square (values)** | self-concordance, values-action, autonomy | ✅ **Aligned** |
| **The Plumb (CBT thought record)** | cognitive reappraisal (situational-strategy point 4) | ✅ **Aligned** |
| **Power-Ups** (breath, ground, unclench, kinder-word) | in-moment steadying; never gated | ✅ **Aligned** |
| **"Thank someone" gratitude Power-Up** (added P3.4) | gratitude — *weak/garnish tier* | ⚠️ **Demote** — keep as optional; never headline over connection |
| **WHO-5** (`WhoFive`) | process/outcome measurement, gentle cadence | ✅ **Aligned** — pair with an **automaticity** signal (see F4) |
| **§9 crisis + mortality SafetyAudit** | clinical handoff, no mortality symbolism | ✅ **Aligned** (binding) |
| **No points/badges/leaderboards; metaphor = the gamification** | overjustification, engagement-efficacy gap | ✅ **Aligned** (the load-bearing "unique" decision) |
| **Environment / cue-friction design** | Wood 2019 friction; situational strategies (the biggest lever) | ❌ **Missing** (0 files) |
| **Automaticity as the progress signal (SRHI-lite)** | Gardner 2015, Wood & Rünger 2016 | ❌ **Missing** (0 files) |
| **Sleep-wake *rhythm* anchor** | Windred 2024 / Li 2025 regularity | ❌ **Missing** (no rhythm feature) |
| **The addiction / "rough edge" track** (anti-AVE, cue-avoidance, urge-surfing) | Marlatt, Bowen, Lyngs | ❌ **Missing** (0 "craving") |
| **Visible fresh-start landmarks** | Dai 2014 | ◻️ **Partial** (comeback exists; no explicit landmark ramp) |

**Reading:** nothing in the app *contradicts* the research (a genuinely rare position). The work is (a) **build the 4 missing levers**, (b) **refine 2** (grace→reserves, practices→first-class), (c) **re-weight the altitude** (elevate connection/BA; demote gratitude), (d) **hold the lines** (no achievements, no willpower mechanics, no keystone/identity overclaims).

---

## 3. The gaps, prioritized by (evidence strength × current absence)

1. **Environment/Cue engine (friction design).** Biggest lever, totally absent. **[HIGH]**
2. **Grace → visible scarce reserves.** Cheapest high-confidence upgrade to something already present. **[HIGH]**
3. **Implementation intentions elevated + cued + downstream of commitment.** Exists but buried; d=0.65 tool. **[HIGH]**
4. **The Rough-Edge (anti-addiction) track.** The user's explicit "get rid of addictions" goal; a real wedge *if the slip is handled right*. **[HIGH on the anti-AVE spine]**
5. **Automaticity as the true progress signal.** Reframes "success" from streak-count to "it's becoming automatic." **[HIGH]**
6. **Rhythm anchor (sleep-wake regularity).** Strong association, currently absent; must be framed associational + non-shaming. **[HIGH assoc.]**
7. **Altitude/honesty pass.** Elevate connection + BA to headline; demote gratitude/mindfulness to garnish; kill any grit language. **[HIGH]**
8. **Fresh-start landmark ramp.** Small, high-confidence add to the comeback flow. **[HIGH]**

---

## 4. Feature specs (the things to build)

Each: **What · Why (evidence) · How it integrates into existing Ashlar surfaces · Acceptance criteria · Guardrails.** (Sizes are rough; the ordered plan comes later.)

### F1 — The Cornerstone: environment & friction design *(the flagship gap)*
- **What:** a guided flow to *engineer the situation* around one target behavior — **remove/av­oid a cue** for a bad default and **place a cue / cut friction** for a good one. Concretely: name the behavior → identify its trigger (time/place/preceding-action/object) → choose one **situation-modification** move (put the phone in another room; lay out the shoes; delete the app icon; add a login step). It composes with a Practice (F2) into a full plan: *"I will change [environment] so that after [cue] I will [approach action]."*
- **Why:** the single highest-leverage, best-evidenced move (Duckworth/Gendler/Gross 2016 — intervene *early*, not at willpower; Wood 2019 friction; Galla & Duckworth automaticity). **[HIGH]** Nobody in this category does self-environment-design well.
- **How it integrates:** a new **Tool** ("The Cornerstone" — the stone the whole structure squares to) in `ToolsScreen`, and an optional step appended to Practice authoring. On-device only; it *prompts the user to act in the world*, it does not automate anything.
- **Acceptance:** produces a saved, specific environment change tied to a cue; copy is **self-directed** ("engineer *your* room"), never "we'll nudge you"; success metric is "did you set it up," not app time.
- **Guardrails:** frame as self-nudging (the population-nudge evidence is contested — Maier 2022); never claim a big effect; one change at a time (Fogg B=MAP — lower the ability cost).

### F2 — Implementation intentions, elevated *(refine what exists)*
- **What:** promote `Practices` from a buried Board card to a **first-class daily object**, make the **cue explicit** (choose an anchor from the user's *actual* routine, or a time/place — not just free text), and gate it **downstream of a committed intention/value** (link each practice to a chosen value from the Square/Intention).
- **Why:** implementation intentions are d=0.65 **but only amplify a committed goal** (Gollwitzer & Sheeran 2006 boundary condition) and are cue-mediated (Webb & Sheeran 2007). Ashlar already has the "AFTER [anchor], I will [action]" scaffold — this makes the cue real and the commitment explicit. **[HIGH direction; modest magnitude — position honestly]**
- **How it integrates:** extend `PracticeAuthoring` (cue picker) + `Practice` model (link to value/degree) + surface active practices on the Board and in the Working. Reuse the existing approach-framing guard.
- **Acceptance:** every practice has (a) a linked value/intention, (b) an explicit cue, (c) approach phrasing; effect claims in copy stay humble ("a cue to make it automatic," not "this will change your life").
- **Guardrails:** if the user hasn't set an intention/value, route them there *first* (commitment before if-then).

### F3 — The held breath: visible, scarce grace reserves *(refine what exists)*
- **What:** surface the existing grace days as a **visible, limited, protectable reserve** on the stone — e.g., "the stone holds 2 grace tokens; a missed day spends one, and they replenish slowly." Show them; let the user *feel* the scarcity; celebrate *not* spending them.
- **Why:** Sharif & Shu — a reserve *framed as scarce* raises post-miss rebound to ~55% vs ~37% because people protect it; silent/auto-applied slack (Ashlar's current design) leaves that motivation on the table. **[HIGH]**
- **How it integrates:** `KindStreak` already tracks `graceRemaining`/`graceUsed` — expose them in the stone/Board UI with reserve framing; add slow replenishment. No new data model.
- **Acceptance:** grace tokens are visible and named as scarce; spending one is framed *gently* ("the stone held for you today"), never as failure; never zero-resets the stone.
- **Guardrails:** honest — "one miss is fine" must not become "consistency doesn't matter" (Lally: a *week* of misses hurts); after sustained absence, offer a **fresh start** (F8), not a shame counter.

### F4 — The true signal: automaticity over counts
- **What:** a light periodic check — "did today's practice feel automatic, or did you have to push?" (2–3 SRHI-style items) — and reframe the stone's story around **"becoming automatic,"** not raw days. The number that matters internally is *automaticity*, not streak length.
- **Why:** a habit *is* automaticity, not frequency (Gardner 2015; Wood & Rünger 2016); a high streak isn't a formed habit. **[HIGH]** This is what makes the app *honest* about progress.
- **How it integrates:** a tiny optional prompt after the Working / a practice; store an automaticity trend; let the stone's copy reflect "the work is becoming yours" as automaticity rises. Pairs with WHO-5 (outcome) as the two honest signals.
- **Acceptance:** never adds pressure (skippable, ≤2 taps); framed as noticing, not grading; no leaderboard/number-chasing.
- **Guardrails:** don't make users *surveil* — this is a gentle occasional check, not a daily craving/temptation diary (Milyavskaya: surveillance backfires).

### F5 — The Rough-Edge track *(the addiction face — new, high-care)*
- **What:** an opt-in path to work **one** bad habit / compulsion (doom-scrolling, late-night snacking, a vice), built entirely on the anti-AVE spine: (1) name the rough edge + its cue; (2) an **environment move** (F1) to reduce contact with the cue; (3) a **replacement** approach-action for the same cue (F2); (4) an **urge-surfing** Power-Up for the moment a craving hits; (5) a **lapse is data, not a verdict** response — a slip never breaks the stone, triggers a self-compassion reframe (Trowel), and files the day as "a wave I rode / one I didn't," then points forward.
- **Why:** most "quit-X" apps are streak-shame machines that weaponize the AVE; the evidence says the opposite works — cue-avoidance > willpower, urge-surfing not suppression (Wegner), lapse-tolerance, identity, connection. **[HIGH on the spine]** This is genuinely differentiated *and* safer.
- **How it integrates:** reuses F1 (environment) + F2 (replacement practice) + Trowel (self-compassion) + KindStreak (never-zero) + West Gate (connection); adds an **urge-surfing** Power-Up and a "rough edge" object. It is a *lens* over the engine, not a separate app.
- **Acceptance:** no streak-shame anywhere; a lapse produces compassion + a forward step, never a broken counter; urge-surfing frames the craving as a passing wave.
- **Guardrails (⚠️ safety):** **substance addiction is clinical** — a prominent, unshaming handoff to real help (reuse §9 / West Gate), and explicit "this is a practice, not treatment." Do **not** ask users to log every craving.

### F6 — The Rhythm anchor *(sleep-wake regularity — new, framed carefully)*
- **What:** a gentle, optional daily "rise/rest" anchor — pick a consistent wake and wind-down time, get a soft rhythm reflection (not an alarm, not tracking). Ties into the Working ("how did you sleep / arrive").
- **Why:** sleep-wake **regularity** (not duration) is strongly associated with lower depression/anxiety/mortality (Windred 2024; Li 2025) and is the circadian anchor of daily rhythm (social zeitgeber). **[HIGH association]**
- **How it integrates:** a light card / anchor in the Board or Working; feeds F2 (a natural cue for anchoring practices).
- **Acceptance:** framed **associational** ("regular rhythm is *linked to* steadier mood"), never causal or clinical; targets *consistency*, not *earliness* or a fixed duration (no "5 AM club"); never shames a bad night.
- **Guardrails:** reverse-causation aware — poor sleep is often a *symptom*; the feature must be supportive, not blaming. If aimed anywhere near mood-disorder users, cite IPSRT properly and keep the §9 handoff (bipolar caveat).

### F7 — The altitude & honesty pass *(re-weighting, mostly copy/placement)*
- **What:** (a) **elevate** the West Gate (connection) and the Gauge/BA to *headline* daily practices (they're the strongest evidence and currently under-placed); (b) **demote** the gratitude Power-Up and any mindfulness content to clearly-optional garnish; (c) **audit all copy** to remove any "grit/toughness/push-through" language (sell consistency); (d) keep identity framing but never *claim* it as a proven lever; (e) ensure every prompt is **autonomy-supportive** (choice + a brief why + non-judgment) and **approach-framed**.
- **Why:** the evidence ranking (§1.5) + SDT (guilt→dropout) + honest-confidence copy as a **trust moat** that fits the research-grounded ethic. **[HIGH]**
- **How it integrates:** placement + copy changes across `BoardScreen`, `ChamberScreen`, `PowerUps`, `ToolsScreen`; extend `SafetyAudit` with a *language* check (flag "grit/toughen/push through/discipline-as-resistance" and any causal overclaim on garnish practices).
- **Acceptance:** connection + activation are the most prominent daily invitations; no overclaim on gratitude/mindfulness; no grit language; a lint-style test guards it.

### F8 — The fresh-start ramp
- **What:** after a lapse or a lapse-run, offer an explicit **landmark restart** ("It's a new week — take up the stone again") rather than a re-entry into a diminished counter.
- **Why:** fresh-start effect (Dai 2014) — landmarks re-motivate by filing past failure into a prior period. **[HIGH]**
- **How it integrates:** extend `KindStreak.comebackMessage` / the comeback card with a landmark-aware line; pairs with F3 (reserves) and F5 (rough-edge lapses).
- **Acceptance:** the restart is invitational and forward-facing; never guilt or "you lost N days."

---

## 5. Why this is state-of-the-art and unique

Every competitor optimizes the two things the evidence says are *weakest*: **willpower** (grind harder) and **achievements** (points/streak-shame). Ashlar's differentiation is that it does the opposite, and now with the full evidence base:

1. **It designs the environment, not the willpower** (F1) — the highest-leverage lever, and almost no consumer app touches it.
2. **It treats the slip correctly** (F3, F5, F8) — never-zero + visible scarce reserves + self-compassion + fresh starts — which is the exact opposite of every streak-shame quit-app, and is *safer* for at-risk users.
3. **It's honest about progress** (F4) — automaticity, not vanity counts.
4. **It's one engine with three faces** (discipline/wellness/addiction) held together by the ashlar metaphor — so the metaphor *is* the motivation (dodging overjustification) instead of bolted-on points.
5. **Its claims are calibrated to the evidence** — a trust moat competitors built on hype can't copy.

The through-line: **the least manipulative, most evidence-honest product in the category — and, because the evidence says manipulation doesn't durably work, also the most effective.**

---

## 6. Explicitly NOT to build (held lines)

- **No points, badges, leaderboards, or any earnable/spendable currency** (overjustification; engagement-efficacy gap).
- **No willpower mechanics** — no "discipline meter," "willpower budget," or "you're out of discipline" (ego depletion is dead).
- **No punitive zero-reset streaks, no loss-aversion cliffs, no guilt/shame copy** (AVE; perfectionism harm; guilt→dropout).
- **No "keystone habit transforms your life" or unqualified "identity change" claims** (pop-science / unproven lever).
- **No causal claims on garnish practices** (gratitude/mindfulness/nature) or on general routine→wellbeing (correlational).
- **No backend, accounts, payments, telemetry, or `INTERNET` permission** (budget + privacy north-star; the society backend stays deferred behind the petition signal).
- **No pretending to treat clinical addiction or mood disorders** — unshaming §9 handoff always on top.

---

## 7. Next step (later, on your word)

Turn §4 into a **sequenced implementation plan** — likely a Phase 4 ("The Cornerstone / the engine") starting with **F1 (environment) + F2 (elevated implementation intentions) + F3 (visible reserves)** as the highest evidence×absence, then **F7 (altitude pass)**, then the **F5 rough-edge track** (highest care), with **F4/F6/F8** folded in. Each ticket TDD'd, on-device, PR-per-piece, device-verified — the same discipline as Phases 1–3.

---

## 8. Citation appendix (verified this pass)

Self-control & willpower: Galla & Duckworth 2015 (PMC4731333); de Ridder 2012; Hofmann/Baumeister/Vohs 2012; Milyavskaya & Inzlicht 2017; Hagger 2016 RRR; Vohs 2021 (36-lab); Moffitt 2011 (PNAS); Duckworth 2007; Credé 2017; Duckworth/Gendler/Gross 2016.
Habits & if-then: Wood & Rünger 2016; Wood & Neal 2007; Gardner 2015; Wood/Quinn/Kashy 2002; Lally 2010; Gollwitzer 1999; Gollwitzer & Sheeran 2006; Webb & Sheeran 2007; Bélanger-Gravel 2013; Adriaanse 2011; Toli 2016; Wood 2019; Fogg 2009; Tiny-Habits gratitude RCT 2022; Maki 2019 (spillover); Maier 2022 (nudge).
Streaks/misses/compassion: Sharif & Shu 2017/2019; Kivetz 2006; Dai/Milkman/Riis 2014; Cochran & Tesser; Polivy & Herman; Marlatt & Gordon 1985; Limburg 2017; Wohl 2010; Adams & Leary 2007; Sirois 2015.
Motivation/identity/meaning: Deci & Ryan; Ng 2012; Teixeira 2012; Deci/Koestner/Ryan 1999; Bem; Zhu 2025; Vangeli & West 2012; Husband/Rhodes 2019; Sheldon & Elliot 1999; Hill & Turiano 2014; Elliot 1999; Williams 2022; Powers 2009 (ACT).
Daily practices: Holt-Lunstad 2010; Singh 2023; Ekers 2014; Cuijpers 2007; Windred 2024; Li 2025; Frank 2005 (IPSRT); Ehlers/Frank/Kupfer 1988; Goyal 2014; Van Dam 2018; Cregg & Cheavens 2021; Frattaroli 2006; White/Uttl/Holder 2019; White 2019 (nature).
Addiction/gamification: Marlatt relapse prevention; Bowen/Witkiewitz MBRP; Wegner (ironic process); Lyngs 2019; mental-health-app gamification meta-analyses + Engagement–Efficacy–Ethics review 2026.

*(Full URLs are in the session research briefs; confidence tags above reflect what was verified against primary sources vs. secondary/again-flagged.)*
