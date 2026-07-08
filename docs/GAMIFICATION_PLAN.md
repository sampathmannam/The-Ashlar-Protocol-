# The Ashlar Protocol — Gamification Design Plan

*How to build a world-class gamified experience for a mental-wellness app **without** the mechanics that quietly harm the people it's meant to help.*

**Status:** Evidence-based design plan (Jul 8, 2026). Brainstorm → converged plan.
**Companion docs:** [MASONIC_DESIGN_PRIMITIVES.md](MASONIC_DESIGN_PRIMITIVES.md) (the metaphor system), [RESEARCH_BASIS.md](RESEARCH_BASIS.md) (therapy evidence + mortality safety gate), [VISION.md](VISION.md), [MASTER_PLAN.md](MASTER_PLAN.md).
**Research provenance:** synthesized from 6 dedicated web-research passes (evidence base, dark-patterns/ethics, habit science, Self-Determination Theory, healthy social mechanics, best-in-class app teardowns). Full citations in the appendix. Confidence is flagged per claim; vendor/marketing numbers are labeled as such and must not be reproduced as our own claims.

---

## 0. The one finding that reshapes everything

The best direct evidence says **gamification does not improve mental-health outcomes and does not fix retention** on its own. The cleanest test (Six et al. 2021, *JMIR Mental Health*, 38 studies / 8,110 participants) found the *number of gamification elements predicted neither symptom improvement (β = −0.03, p = .38) nor adherence*. Real-world wellness-app retention is ~**3–4% at 30 days** (Baumel 2019), and gamification doesn't move it — *reminders + human/relational support + personalization* do.

**This does not kill the vision. It sharpens it.** The one gamified program with a strong positive RCT — **SuperBetter** (d ≈ 0.67 post, 1.05 follow-up; Roepke et al. 2015) — worked because **the game *was* the therapy delivery vehicle**, not a points layer bolted on top. The mechanics with real therapeutic rationale are the ones that double as **behavior-change techniques**: self-monitoring, goal-setting, progress feedback, and narrative/meaning reframing.

**That is exactly what the ashlar metaphor already is** — a narrative/identity/meaning engine, not a token economy. So the strategic thesis:

> **The ashlar IS the gamification. Points, streaks, and leaderboards are the decorative layer — and in a mental-health context that layer adds measurable risk without adding outcomes.** Our world-first, defensible claim is not "a wellness app with a leaderboard" (those exist and mostly aren't good). It is **the first wellness app where the game mechanic and the therapy are the same object** — a self visibly being shaped, using evidence-based skills, retained by a remembered bond rather than by manufactured anxiety.

**Success metric = validated wellbeing change (PHQ-9 / GAD-7 / WHO-5), never engagement.** The "engagement–efficacy gap" (more app usage ≠ better symptoms) is the central documented failure mode of this category. We instrument *against* it: watch for users who spike engagement then churn after a missed day, and check whether high-engagement users actually improve.

---

## 1. What you asked for → what the evidence says to build instead

Every mechanic in the brief is **kept** — reframed into its healthy, higher-retention, non-harmful form. You lose nothing you actually wanted (drive, progression, mastery, daily rhythm, belonging); you shed the harm vectors.

| You asked for | The risk (evidence) | What we build instead |
|---|---|---|
| **Streaks** | Loss-aversion → guilt/shame on break → dropout; a missed day *is* a bad mental-health day, the worst moment to punish (Wohl 2010; Habitica/Duolingo teardowns) | **"Tending the stone"** — consistency *with grace*: never resets to zero, auto-repair, capped grace-days, celebrate the **comeback**, self-forgiveness copy |
| **Levels / unlocks** | Arbitrary grind; extrinsic-reward crowding out (Deci/Koestner/Ryan d ≈ −0.34) | **The three degrees** (Arriving → Building → Integrating): unlock by *demonstrated practice*, not points; each unlock is an *informational* "you can now…" |
| **Daily tasks / goals** | Rigid quotas fail on low days; avoidance/outcome/imposed goals lower wellbeing | **The daily "Working"** — tiny, mood-adaptive, self-set, approach-framed, *process*-rewarded; a one-tap "floor" version always available |
| **Leaderboard** | In a wellness app, rank correlates with how *unwell* someone is → publicly punishes the sickest at their lowest moment (Cheng 2020 recommends cooperative over competitive) | **The three-part "drive + belonging" stack that ranks no one** (§6). Competitive leaderboard = explicit, informed, opt-in-only decision — see §6.4 |
| **Rewards / points** | Expected, contingent, tangible rewards corrode intrinsic motivation (the overjustification effect) | **Informational feedback + identity as the currency** — the stone visibly smooths, VIA character-strengths grow; "you can now," never "you earned N" |
| **Unlock-as-you-progress** | (Good instinct!) | Kept — but gated on *therapeutic milestones* (skills practiced), foreshadowed to build anticipation without pressure (Finch model) |

---

## 2. The design spine: five non-negotiable principles

Every feature decision is checked against these. They come straight from the evidence and they *are* the moat.

1. **Informational, never controlling.** The single knife-edge finding (Deci/Koestner/Ryan 1999): *informational feedback and praise enhance intrinsic motivation; expected tangible contingent rewards destroy it.* So feedback says **"you can now do X"** (competence mirror) — never **"you earned 50 points."** The stone catching the light as it smooths is legitimate *because it's an honest mirror of real behavior*, not a token.

2. **Gentleness is the default, never opt-in.** Habitica's fatal error was making "no damage" a setting you had to find. We invert every punishment: no progress-loss, no zero-reset, no lockouts, no guilt-push, no demotion — by default, for everyone.

3. **Adapt to today's state.** Motivation fluctuates; ability is the reliable lever (Fogg B=MAP). On a low day the app asks *less* (a "floor task" that's nearly impossible to fail — the BA treatment mechanism), and pays *more* acknowledgment (Finch's mood-adaptive rewards), never "you're falling behind."

4. **Self-referenced, cooperative, contributive — never comparative.** Compare the user only to their own past self. Social features build *together* or *give*, they don't rank.

5. **Retain via bond, not loss aversion.** The deepest retention engine isn't the streak — it's the felt relationship with a warm, *remembered* persona (the Woebot/Wysa model: therapeutic-alliance bond scores reached near-human levels within days). This maps directly onto your existing on-device compounding-memory + unified "Nila" persona. **Design for a rising bond curve.** This is your structural advantage over every loss-aversion app.

---

## 3. Progression: the degrees as the level system

The three Masonic degrees (already renamed in [MASONIC_DESIGN_PRIMITIVES.md](MASONIC_DESIGN_PRIMITIVES.md)) *are* the level system — honest gamification, because you advance by doing real wellbeing work, not by grinding points.

- **Arriving** → **Building** → **Integrating.** Each is a *legitimate identity*, not a locked gate. "Building" explicitly names the long messy middle as valid (combats the beginner→mastery cliff).
- **Unlock by demonstrated practice**, not points or time: e.g., you reach "Building" by having practiced a spread of foundational tools, not by logging in N times.
- **Progression currency = identity + mastery + meaning** (PERMA's Accomplishment + Meaning; VIA signature strengths), surfaced as *informational* readouts ("you've become someone who notices their triggers"), never a number you can lose.
- **Tie each facet of the stone to a VIA character strength** (perseverance, hope, kindness…). The evidence-based core loop here is Seligman's *"use one of your signature strengths in a new way"* — shown to raise happiness and cut depression for up to 6 months. This is gamification that is *itself* an intervention.
- **Ceremony at transitions** (full-screen reflective moment + a self-authored commitment), used sparingly at the 3 boundaries so it stays weighty. No silent "Level 2 unlocked" toast.
- **Foreshadow unlocks** (Finch): show the user what's ahead to build anticipation — without pressure or a countdown.

---

## 4. The daily "Working": adaptive tasks that never punish

Replaces "daily tasks users have to do." The word "have to" is the problem; the fix is autonomy + adaptivity.

**Structure of every task (a habit loop, not a bare to-do):**
- **Anchor to an existing cue** via an if-then / habit-stack plan the user writes at setup: *"After [my morning coffee], I will [take one breath]."* (Implementation intentions, Gollwitzer & Sheeran, d ≈ 0.65 — one of the most robust findings in behavior change.) Fire the reminder at the *cue moment*, not a random clock time.
- **Self-set, approach-framed, process-rewarded.** "Take a 5-min walk" (approach) not "don't skip movement" (avoidance). Reward *showing up* (controllable) not *feeling better* (not controllable, and it protects the user on days the good feeling doesn't come).
- **Learning goals for new users** ("this week, notice when your energy is highest"), numeric targets only for behaviors already demonstrated.

**Mood-adaptive difficulty (the Finch mechanic, evidence-grounded):**
- A lightweight daily mood/energy check-in **scales the day's ask**. Low day → shrink to a **floor task** engineered to be nearly impossible to fail (open the curtains; step outside 60s; text one person). For depressed users, *tiny is not a compromise — it is the Behavioral Activation treatment* (Ekers 2014, SMD ≈ 0.74).
- **Pay more acknowledgment on bad days, not less** — structurally eliminates the "too low to earn" spiral.
- A **user-controllable difficulty dial** ("make today lighter/heavier") keeps autonomy with the person, not just the algorithm.
- Bias toward *fewer, well-timed, easier* asks on bad days (JITAI caution: mistimed prompts backfire).

**A Power-Ups library (SuperBetter):** quick mood-lifters the user can pull *anytime*, decoupled from any streak or task — so support is always one tap away, never gated behind "completing the day."

**Hard rule (matches your existing "never gate the talking"):** core support and the ability to reach the companion are **never** locked behind task completion, a streak, or a paywall. Monetizing relief from manufactured pressure (Duolingo hearts) is the canonical anti-pattern.

---

## 5. "Tending the stone": streaks that build habits without shame

Streaks are kept — because consistency genuinely builds habits — but redesigned around what the science actually shows.

- **One miss is harmless — the evidence is explicit.** Lally et al. 2010: missing a single day did *not* measurably harm the habit-formation curve (median 66 days to automaticity, range 18–254). So **resetting to zero on one miss isn't just cruel, it's scientifically wrong.**
- **Never resets to zero.** Use an accumulating *practice log* (total days worked on the stone, which only ever grows) instead of a fragile consecutive-day counter that can be destroyed.
- **Grace-days / auto-repair, forgiveness by default.** A small **capped** reserve (~2; Sharif & Shu show emergency reserves beat rigid goals *and* aid persistence after a failure; Duolingo's data suggests 3 is no better than 2). Prefer **auto-applying** a grace day on a miss (Calm's free retroactive repair) so a user too low to open the app isn't punished. No purchase, ever.
- **Celebrate the comeback as a first-class event.** "You came back — that's the skill that matters." Give the *most* warmth on return after a miss, not a broken flame. ("Never miss twice" = prevent a lapse becoming a collapse.)
- **Self-forgiveness copy after a miss** (Wohl 2010: self-forgiveness → *fewer* future lapses, via reduced negative affect). "Yesterday was hard, and that's okay — one day off doesn't erase who you're becoming." **Never** loss-framed notifications, red broken streaks, or "you lost your streak" language — that manufactures the exact negative affect that predicts dropout, in the exact population the app exists to help.
- **Visual closure over numbers** (the Rings insight, de-weaponized): a *stone being smoothed* / a shape completing is more motivating than a counter — tied to *showing up / checking in*, kept private, never a quantified target with a shaming deadline.

---

## 6. Social: the leaderboard replacement (drive + belonging, ranking no one)

**Why not a competitive leaderboard.** The domain-specific peer-reviewed recommendation (Cheng et al. 2020, *Frontiers*) is to prefer **cooperative over competitive** mechanics in mental health. The mechanism: leaderboard rank operates through competence *satisfaction* (top) vs *frustration* (everyone else); it motivates ~the top 10% and induces avoidance/hopelessness in the rest — and low trait-competitiveness describes much of a distressed population. **The structural killer:** in a wellness app, rank correlates with how unwell someone is, so a public leaderboard *systematically punishes the sickest users at their lowest moment* — the opposite of the product's purpose.

*(Honest counter-evidence, so we don't over-correct: one 2026 cross-sectional study found leaderboards associated with **lower** stress — but indirectly via increased activity, in a non-clinical fitness sample. It does not license a leaderboard for a clinical MH app, but it shows the harm is context-dependent, not automatic. If a competitive element is ever shipped, it must be validated on *our* population.)*

**The three-part stack that delivers the same drive + belonging (§6.1–6.3):**

### 6.1 Self-progress, not rank
A private **personal-best dashboard** — "beat your past self," percentile-free, "you're more consistent than last month." Delivers the leaderboard's progress-hunger through competence *satisfaction* without inflicting competence *frustration* on the ~90% who aren't "winning."

### 6.2 Small cooperative cohort with a shared goal
5–15 people, **opt-in, private, invite/code-based** (no public feed, no stranger discovery). The group **builds one thing together** (a shared edifice/monument/garden — a Masonic "lodge raising a temple" fits perfectly) or pools effort toward a collective target. **No one is eliminated; everyone belongs.** Model: Habitica parties, Finch's town, Headspace's Buddy pattern (tiny capped circle, illustrated avatars, no rank, private "nudge" only). **Soften any cooperative dependency** so a user in a bad week never feels they failed their friends.

### 6.3 Prosocial "give / help" actions — the therapeutic replacement
Structured **"send encouragement / a good vibe"**, gifting, pay-it-forward, and an **"allies"** model (SuperBetter, RCT-backed). This is the leaderboard's best replacement *because the act of giving support is itself therapeutic for the giver* (helper-therapy principle; prosocial behavior causally raises wellbeing — real but *small* effect, so frame as a booster not a treatment). It converts the social layer from a source of comparison stress into a source of wellbeing. Maps directly onto the **"Relief / mutual aid"** principle already in the Masonic doc.

### 6.4 The competitive-leaderboard decision (explicit fork — founder's call)
**DECIDED (founder, Jul 8 2026): "Cooperative now, ranked later."** Ship §6.1–6.3 (the no-ranking stack) for launch; a competitive/ranked leaderboard is deferred to **Phase 4 as an opt-in, instrumented experiment**, not a launch feature. Rationale: it's the single highest-risk mechanic, it contradicts the app's own "Level/self-compassion = don't compare yourself" tool, and the cooperative stack already delivers the drive and belonging we're after.

If/when that Phase-4 element is built, the **only** defensible form is: **opt-in, off by default, small closeable-gap cohort (not global), effort-based (showing up) never outcome-based (mood/symptom scores), no demotion shaming, leaveable without loss-framing — and gated behind a validation study on our own users** (see Phase 4 and §8).

**Non-negotiable social safety (dovetails with the §9 crisis gate):**
- 🚩 Never publicly rank users by any mental-health-correlated metric (mood, streaks, symptom scores).
- 🚩 No open feed / no stranger-discovery of at-risk users — **suicide/self-harm contagion risk.** Moderation is the single variable separating "safe & supportive" from "harmful" in the literature.
- 🚩 Any free-text peer channel requires **moderation + crisis detection (hybrid AI + human)** that routes at-risk content, model-independent-safe, consistent with the existing §9 layer.
- 🚩 Structured/prompted support (send-an-encouragement) over free-text broadcast wherever possible, to constrain content and reduce moderation surface.

---

## 7. Retention: the honest strategy

Because the evidence says gamification *won't* save the ~3% retention cliff, we invest in what does:

1. **The remembered bond** (primary engine) — a warm, consistent, *remembering* companion. Frame mood tracking as **insight** ("here's a pattern I noticed"), check-ins as **invitations** not obligations. Design a rising therapeutic-alliance curve.
2. **Timely, gentle, cue-anchored reminders** — skippable, never loss-framed, fired at the user's chosen anchor moment.
3. **Genuine personalization** — mood-adaptive difficulty, self-authored goals, content tuned to the person.
4. **Graceful exit / "graduation."** A wellbeing app's real goal is often the user needing it *less*. Celebrate stepping back. Build easy stop-points and "you've done enough today" nudges. Never optimize time-on-device — that metric is *misaligned* with the user's welfare here.

---

## 8. Metrics & anti-harm instrumentation

**North-star = wellbeing, not engagement.** Primary: PHQ-9 / GAD-7 / WHO-5 deltas (opt-in, gently paced). Secondary: skill acquisition, self-reported benefit, *sustainable* (not maximal) engagement.

**Instrument for harm (because the backfire evidence, while real, is thin — so we measure it ourselves):**
- Flag users who spike engagement then abruptly churn after a missed day (streak-anxiety signature).
- Check that high-engagement users actually improve on outcome measures (catch the engagement–efficacy gap).
- Monitor for compulsive-use patterns (late-night "don't break it" check-ins).
- A/B test any gamification element **powered on outcomes, not retention**, ideally vs an active control.
- Participatory design / testing with people who have lived experience (Cheng 2020 governance checklist) — test for harm on vulnerable users, not just aggregate lift.

---

## 9. Build order (phased)

**Phase 1 — The stone that shapes (solo core loop).** The ashlar avatar + 3–4 tools (Daily Balance, Letting Go + Self-Compassion as the matched pair, Integrity Check) + the daily mood-adaptive "Working" + "tending the stone" (kind streaks) + informational feedback + the remembered-bond persona. This is a complete, shippable, evidence-aligned product where *the metaphor is the gamification*. **Zero mortality symbolism** (see the Safety Wall in the companion doc). **Zero social.**

**Phase 2 — Identity & progression.** The three degrees as the level system; VIA character-strengths facets; the "signature strength in a new way" loop; ceremony transitions; personal-best dashboard (self-referenced, private).

**Phase 3 — Cooperative & prosocial social (opt-in).** Small private cohort + shared edifice + "send encouragement / allies" — with moderation + crisis detection built in from day one. (This is the "community later" layer already anticipated in the Masonic doc's solo-first decision.)

**Phase 4 (conditional) — Instrumented competitive experiment**, only if §6.4's conditions are met and validated on our own users.

**The single most important early test (do before building the whole visual system):** does "you are a stone being worked" land as *empowering* or as *cold/objectifying* for someone in distress? Run a **5-user reaction test** on two onboarding variants (stone-framing vs plain "your wellness journey") first. Everything downstream rides on that framing working.

---

## 10. The world-first claim, honestly stated

Not "the first Freemasonry-inspired wellness app with gamification." The defensible, evidence-grounded claim:

> **The first mental-wellness app where the game mechanic and the therapy are the same object** — a self visibly being shaped through evidence-based skills, progressed by identity and mastery rather than points, sustained by a remembered bond rather than manufactured anxiety, and social through cooperation and contribution rather than ranking. The innovation is delivering the full motivational payoff of gamification with *none* of the shame, comparison, and extrinsic-reward traps that make the category quietly harmful.

That is a real first. A leaderboard is not.

---

## Appendix — Evidence base (confidence-flagged)

**Gamification efficacy / the core reframe**
- Six et al. (2021), *JMIR Mental Health* — gamification element count predicted neither outcomes nor adherence. **High confidence.** https://pmc.ncbi.nlm.nih.gov/articles/PMC8669581/
- Cheng et al. (2023), *Computers in Human Behavior* — gamified interventions vs control g ≈ 0.38 (whole package, can't isolate the game layer). Medium. https://www.sciencedirect.com/science/article/pii/S0747563222004411
- Baumel et al. (2019) via Nwosu et al. (2022), *Front. Psychiatry* — real-world retention ~3.3–3.9% at 30 days. **High.** https://pmc.ncbi.nlm.nih.gov/articles/PMC9380224/
- Edwards et al. (2016), *BMJ Open* — gamification mechanics that map to behavior-change techniques. Medium. https://pmc.ncbi.nlm.nih.gov/articles/PMC5073629/

**Motivation / SDT / rewards**
- Deci, Koestner & Ryan (1999), *Psychological Bulletin* — overjustification: expected tangible contingent rewards undermine intrinsic motivation (d ≈ −0.28 to −0.40); *informational feedback/praise do not.* **High.** https://home.ubalt.edu/tmitch/642/articles%20syllabus/Deci%20Koestner%20Ryan%20meta%20IM%20psy%20bull%2099.pdf
- Ryan & Deci (2000), *American Psychologist* (SDT); Ryan, Rigby & Przybylski (2006) / Przybylski et al. (2010) — PENS (autonomy/competence/relatedness → game engagement + post-play wellbeing). High/Medium-High. https://selfdeterminationtheory.org/player-experience-of-needs-satisfaction-pens/
- Seligman (2011) PERMA; Peterson & Seligman (2004) VIA; Seligman, Steen, Park & Peterson (2005) — "signature strength in a new way" raised happiness / cut depression up to 6 months. Medium-High (pull primary PDF before citing numbers). https://www.viacharacter.org/research/findings/signature-strengths
- Yu-kai Chou Octalysis (White Hat vs Black Hat); Marczewski RAMP; Lazzaro 4 Keys — practitioner frameworks, design-heuristic only.

**Habit / streak / goal design**
- Lally et al. (2010), *Eur. J. Social Psychology* — median 66 days (range 18–254); one missed day doesn't harm the curve. **High.** https://onlinelibrary.wiley.com/doi/10.1002/ejsp.674
- Gollwitzer & Sheeran (2006) — implementation intentions, d ≈ 0.65. **High.** https://cancercontrol.cancer.gov/sites/default/files/2020-06/goal_intent_attain.pdf
- Sharif & Shu (2017, *JMR*; 2021, *OBHDP*) — emergency reserves / capped slack beat rigid goals, aid persistence after failure. High. https://journals.sagepub.com/doi/abs/10.1509/jmr.15.0231
- Wohl, Pychyl & Bennett (2010) — self-forgiveness → fewer future lapses. **High.** https://www.sciencedirect.com/science/article/abs/pii/S0191886910000474
- Ekers et al. (2014), *PLoS ONE* — Behavioral Activation for depression, SMD ≈ 0.74. **High.** https://journals.plos.org/plosone/article?id=10.1371/journal.pone.0100100
- Fogg B=MAP (behaviormodel.org); Elliot et al. (1997) approach>avoidance; Sheldon & Elliot (1999) self-concordance; process>outcome goals meta-analysis (2022); Csikszentmihalyi flow. High/Medium.
- **Streak-freeze specific numbers (Duolingo)** = company blog / podcast, **not peer-reviewed** — cite the *direction* (small capped reserve helps) via Sharif & Shu, not the percentages.

**Ethics / harms**
- Cheng, Fleming, Kambeitz et al. (2020), *Frontiers in Psychology* — recommends cooperative over competitive mechanics for MH; SDT governance checklist. **High (domain-specific).** https://www.frontiersin.org/journals/psychology/articles/10.3389/fpsyg.2020.586379/full
- Festinger (1954) social comparison; upward-comparison meta-analysis (2023, *Media Psychology*) — real but *small, moderated* effect. High theory / Medium magnitude.
- Nir Eyal *Hooked* + critiques (variable reward / compulsion loop) — mechanism solid, harm writing largely essayistic. Medium.
- Calvo & Peters *Positive Computing*; practitioner ethical-gamification frameworks — design heuristics.
- **Unverified figures flag:** some circulating leaderboard-harm statistics (specific %s attributed to named journals) could **not** be verified — do not cite. The defensible claim rests on Cheng (2020) + Festinger + the comparison meta-analysis.
- Backfire/streak-harm evidence is largely **education/adolescent/qualitative, not MH RCT** — a well-reasoned risk, not a proven clinical effect. Instrument for it.

**Social / peer support**
- Peer support meta-analysis (2021), *Psychiatric Services*; helper-therapy principle (PMC3343315); prosocial→wellbeing (World Happiness Report 2019; *Affective Science* 2023, effect real but **small**). High/Medium.
- Contagion / moderation: MOST+ moderated online social therapy; hybrid AI+human suicide detection in peer support; "keeping users in suicidal crisis safe online." **Moderation is the deciding safety variable.** High (safety-critical).

**App teardowns (mechanics = practitioner/marketing sources; efficacy numbers = vendor-authored, flagged)**
- SuperBetter RCT: Roepke et al. (2015), *Games for Health J.*, d ≈ 0.67 / 1.05 — **note high attrition, self-selected sample, author COI; the "Ohio State study" is a separate brain-injury feasibility trial, not depression efficacy.** https://pubmed.ncbi.nlm.nih.gov/26182069/
- Finch (mood-adaptive rewards, cosmetics-only, no shame) — no RCT, anecdotal. Headspace Buddy pattern / Calm free retroactive repair (gentle streaks). Fabulous (Duke behavioral-econ onboarding; aggressive billing). Habitica (punishment/death = cautionary). Duolingo (streak-reset/leagues/hearts = dark-pattern cautionary). Woebot/Wysa (retain via *bond* not gamification; Woebot consumer app shut down Jun 30 2025 amid FDA/medical-claim burden — a scope-of-claims warning for us).

**Overarching caveat:** the strongest efficacy/engagement figures in the teardown set are vendor or marketing numbers — directionally useful, not independent proof. Per the project's research-grounded, no-fabricated-citations rule, pull primary PDFs before any figure enters user-facing or published copy.
