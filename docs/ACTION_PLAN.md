# The Ashlar Protocol — Action Plan

> The detailed, phased path from the app that exists today to the vision in [`VISION.md`](VISION.md).
> Every feature is mapped to its evidence and confidence; every phase carries the design-law
> guardrails from the research. Grounded in [`RESEARCH_BASIS.md`](RESEARCH_BASIS.md).
> **Last updated:** 2026-07-05.

## How to read this
- **Operating principle:** *clinical-grade safety, consumer-grade engagement, honest claims.*
- Confidence tags: **Strong** / **Moderate** / **Metaphor** (design language, not a health claim) / **Built** (already in the app).
- The build order is deliberate and evidence-led (see §"Why this sequence").
- Nothing here requires a backend or a cost — the app stays on-device and free to run, by design (this is also the privacy differentiator).

---

## Why this sequence (the research says so)
1. **Safety first, always** — because staging someone into a rite creates a duty of care, and crisis-robustness is table stakes the field keeps failing.
2. **Fix honesty before adding shine** — an app on a "never fabricate" mission can't show invented data; this is done, and it's the foundation for real personalization.
3. **Build the *static* protocol excellently before anything adaptive** — JITAI has not beaten a good static app; earn adaptation against a working baseline.
4. **Presence and connection are powerful but dangerous** — they come after the core works, and they come *capped*, because unbounded they produce the companion-app harm profile.

---

## Phase 0 — Foundation & Safety *(mostly built; verify + harden)*

**Goal:** a stranger can install it, understand it, use it safely, and be caught in crisis.

| Build | Evidence / rationale | Status |
|---|---|---|
| On-device, fail-safe crisis detector on every free-text surface; always-on "NEED HELP?"; tappable localized hotlines; never gated | Model-independent crisis safety is table stakes; generative/companion bots fail here (APA→FTC advisory; Character.AI deaths). Liminality = duty of care. | **Built** — device-verify §1 of [`VERIFICATION.md`](VERIFICATION.md) |
| Fully on-device; no `INTERNET` permission; no accounts | Privacy is the ownable differentiator (only nonprofit *How We Feel* credibly claims it); "personal sensing" transparency posture (Mohr/Shilton/Hotopf 2020) | **Built** |
| Honest, repeated "a practice, not a clinician" framing | 64% of apps claim effectiveness, 2.7% cite own evidence (Larsen 2019) — we refuse that | **Built** |

**Done when:** the §1 crisis pass is green on a real device, and remains green after every subsequent change.

---

## Phase 1 — The Honest Core: the rite done right *(the heart of the product)*

**Goal:** the static protocol is excellent — real practices delivered as rites, staged Arrive→Build→Reckon, on an honest, atmospheric spine. This is where the app wins or loses.

### 1A. The re-authoring engine (the single most-evidenced lever) — **Strong-ish**
- **Build:** thread *narrative agency* through the whole app. The initiation captures the user's intention; **surface it** everywhere ("You're working toward: ___"); after each rite, a one-line **agency reflection** ("what did *you* do here?"); a periodic, opt-in **"your story so far"** that reflects the user's own words back toward agency and coherence — never a synthetic verdict.
- **Why:** narrative agency *preceded* symptom improvement prospectively (Adler 2012); coherence/agency track wellbeing (McAdams & McLean 2013). This is our strongest science and our biggest current gap (the intention is captured then ignored today).
- **Guardrail:** anti-sycophancy — reflect, never flatter; never declare "you've transformed."

### 1B. The rites (evidence-based practice, delivered as ceremony) — **Strong practices**
Each "tool" is a *rite*: atmospheric, deliberate, guided — not a form. Same evidence underneath.

| Rite | The practice (evidence) | Confidence | Status |
|---|---|---|---|
| The 24-inch Gauge | Behavioral activation; divide the day. Add **implementation intentions** ("if [cue] then I will [action]", d≈0.65) | **Strong** | Built; add if-then |
| The Plumb | CBT thought-record (cognitive restructuring) | **Strong** | Built |
| The Level | Paced breathing ~6/min (0.1 Hz resonance) | **Strong** | Built |
| The Chamber | Cathartic release + meaning/values reflection (expressive writing; meaning-making) | **Moderate** | Built (Let Go / Keep) |
| Mouth-to-Ear | Memorize a chosen principle (retrieval practice) | **Moderate** | Built |
| The Square (new) | Values clarification (ACT) → *becomes* the intention, anchors the Plumb | **Strong (as ACT ingredient)** | New |
| Self-compassion rite (new) | Brief self-compassion — **with a gentle exit** (single-session can surface "backdraft"; §9-relevant) | **Strong program / cap the dose** | New |

- **Guardrail:** every rite has a low-friction entry (a small "effort to begin" = IKEA-effect ownership, *not* engineered friction), and none is gated behind a paywall or a degree if it's foundational or safety-adjacent.

### 1C. The Arrive→Build→Reckon spine + earned rites — **Strong (SDT) / Metaphor (the arc)**
- **Build:** the degree engine drives what's unlocked; the journey (the ashlar, smoothing with *real* progress) is the home. Foundational + grounding rites open at Arrive; deeper rites are *received* as the work is done.
- **Why:** SDT competence/autonomy (earned mastery motivates) — *if* self-endorsed, not gate-like. The three-degree arc is honest design architecture (Arrive/Build/Reckon), not a health claim.
- **Guardrail:** unlocking is pedagogy, not a paywall or a loss-aversion streak. First advancement comes fast (early competence win).

### 1D. The honest data layer — **Built (Honesty Pass) + extend**
- **Built:** all fabricated charts removed; "The Work So Far" shows literal real counts ("no scores, no predictions").
- **Extend:** persist the Plumb thought-records (so growth is visible in the user's *own words*); make the initiation baseline the first real point of an honest self-report — framed as *self-awareness*, never a treatment claim (self-serve progress feedback is an evidence gap; the outcome evidence is clinician-mediated).

### 1E. Atmosphere (high ROI, low risk) — **Moderate**
- **Build:** the warm, candlelit, serif "lodge at night" aesthetic (per the mockup direction); optional **natural soundscapes** (the best-supported ambience lever — reduce stress, improve mood); calm, low-arousal design.
- **Guardrail:** aesthetics drive *uptake* but are *decoupled from efficacy* — polish the wrapper, keep the practice the hero, never mistake beauty for effect.
- **Accessibility:** the gold-on-charcoal palette passes WCAG AA (~7.19:1); fix the one real issue — gold-as-only-cue for tappable text needs a second cue (WCAG 1.4.1).

**Phase 1 done when:** a new user is *initiated*, receives rites as they advance, sees their real work reflected back toward agency, in a place that feels like somewhere — with zero fabricated data and the crisis net always open.

---

## Phase 2 — Presence & Personalization *(powerful, and hard-capped)*

**Goal:** it feels like it *knows you and speaks to you* — without becoming an attachment machine.

### 2A. A voice, not a companion — **Moderate (bond) / Cap hard (safety)**
- **Build:** a warm, relational *mentor voice* (the "Worshipful Master" register) that greets, marks progress, and frames rites. It can carry the **supportive-accountability** effect (the most reliable ethical engagement lever — Mohr 2011; Werntz 2023).
- **Guardrails (non-negotiable, from the harm literature):** persistent honest non-personhood ("I'm not a person"); **anti-sycophancy**; boundary reminders; hard crisis hand-off; **never** optimize for time-in-app or bonding-to-the-app. Companion-maximizing is the exact profile getting sued. A relational agent that raises "bond" but not outcomes, and can *cannibalize* the target behavior, is a known failure mode — so the voice always routes *into* the practice.
- **On the AI question:** if a generative voice is ever used, it must be **on-device or the sensitive data never leaves the phone**, safety must be **model-independent** (the rule-based crisis net always runs regardless of the model), and it ships only after the static protocol is proven. Generative agents are the frontier (Therabot: first positive genAI RCT, d≈0.7–0.9) *but gated by safety, not capability.*

### 2B. On-device memory & humble personalization — **Moderate (engagement) / thin (outcomes)**
- **Build:** the app remembers the intention, the values, the patterns — on device — and gently tailors which rite it suggests. Transparent: the user sees and controls what it "knows."
- **Guardrails:** tailoring helps *engagement* modestly, has thin *outcome* evidence, and *asking* tailoring questions can itself cause dropout — so keep it light and optional. **No covert passive mood prediction** (it doesn't generalize: AUC 0.82→0.57 across populations).

**Phase 2 done when:** the app feels personal and present, every safety cap holds, and nothing about it optimizes for dependence.

---

## Phase 3 — The Lodge (connection) *(the strongest wellbeing mechanism; zero-infra)*

**Goal:** reduce isolation — the highest-leverage protective factor — without a server, a social graph, or moderation liability.

- **Build (zero-infra, already begun):** *the Well* (bundled words of relief at the cathartic moment); *reach out* (compose a message, hand off to the phone's own SMS/WhatsApp — real connection, no server); *the West Gate* (a bridge to real, free community — Men's Sheds-style), designed carefully (region-aware, non-appropriative).
- **Why:** social connection is a top protective factor (Holt-Lunstad); Men's Sheds reduce isolation; **supportive accountability** to a caring other is the most ethical retention lever. Real connection is simultaneously the strongest *wellbeing* mechanism and the most ethical *retention* mechanism.
- **Guardrail:** never a dopamine feed; never public vulnerability by default; the Master-Mason "turn outward" is earned and opt-in. If a peer layer is ever added, it is anonymous-by-default and heavily safety-gated — but the default is *your own people and real-world rooms*, not a platform.

**Phase 3 done when:** the app measurably points people toward real connection without compromising privacy or safety.

---

## Phase 4 — Adaptive timing (JITAI) *(only after the static protocol proves itself)*

**Goal:** the right rite at the right moment — the field's genuine unsolved problem.

- **Build (later):** context/self-report triggers that offer the right rite when the user is *receptive* — modeled as decision points, tailoring variables, options (incl. "do nothing"), decision rules (Nahum-Shani framework).
- **Why cautiously:** JITAIs currently show only **g≈0.15 and no proven advantage over a good static/active control**, and **mistimed prompts backfire** via message fatigue/reactance. Whoever cracks *receptivity detection* wins a real frontier — but only against a working baseline.
- **Guardrails:** cue-based/contextual, never reminder-dependent (reminders support repetition but hinder true habit formation); effects habituate, so vary and respect "do nothing"; measure proximal effect honestly (micro-randomization if we ever evaluate rigorously).

**Phase 4 done when:** adaptation demonstrably beats the static protocol *for this app* — or we don't ship it.

---

## Cross-cutting

### Evidence & evaluation discipline
- Every claim in-app is traceable to [`RESEARCH_BASIS.md`](RESEARCH_BASIS.md) at its true confidence. Metaphor is labeled as metaphor.
- The frontier is thin on *narrative/rite-of-passage app* evidence — so build with **eval discipline**: define what "helped" means, measure it honestly (the user's own self-report, opt-in), and be willing to cut what doesn't.
- Watch for the **digital placebo** — much app benefit is expectancy/engagement; that's not nothing, but don't confuse it with an active ingredient.

### Metrics — *help is the only metric*
- **Never** engagement/retention/time-in-app as success. Instead: the user's own self-rated sense of progress (their data, shown to them); whether they report a rite helped (optional, local); whether the crisis net reliably fires (safety QA); does a real person say this changed something (opt-in, never harvested).
- "Effective engagement" (benefit, not time) is the frame — it *is* "help is the only metric."

### Monetization (context, not a near-term ask)
- Category norm is **~$60–80/yr** freemium; freemium converts poorly (~7%); **identity-driven evangelism** is the cheapest growth channel (Hallow, Insight Timer). A "founding initiate" lifetime tier fits.
- **Never gate the talking, the release, or the help.** Charge for depth/customization, never for safety or the core practice. B2B is where the money is but is a poor early fit for a masculine/identity consumer product — lean into high-conviction community.

### Positioning
- Honest one-liner: *"the app that treats becoming well as an initiation, not a subscription to calm."*
- Claim: *first to use Freemasonry's initiatory structure as a mental-health architecture.* Not "first ritual app," not "first men's app."
- App's own voice: *"inspired by the craft metaphor and the evidenced power of structured belonging — a secular, inclusive practice, with no affiliation to any Masonic body."*

### Risk register
| Risk | Mitigation |
|---|---|
| **Brand baggage** (secrecy/conspiracy/male-exclusivity) | Reverent not costume-y; inspired-by not affiliated; secular/inclusive; no Masonic marks/regalia implying ties; a trademark/branding check |
| **Perfectionism harm** | "Perfect ashlar" = direction not destination; self-worth ≠ progress; stone never finished |
| **Death/mortality content harming an at-risk user** | Opt-in, gentle, *firewalled* from crisis flows; crisis logic always outranks it |
| **Companion-attachment harm** | Hard caps on the voice; non-personhood; anti-sycophancy; crisis hand-off; no engagement-maximizing |
| **Overclaiming** | Honesty charter; the retraction we caught is the proof of process |
| **Liminality left unsupported** | The rite is wrapped *inside* the crisis net, never the reverse |

---

## The next five concrete moves
1. **Verify** the current build on device (the §1 crisis pass, blocking) — everything builds on that.
2. **Phase 1A/1D — make it remember:** surface the intention on the home; persist Plumb records; agency reflections. *(Highest-evidence gap, self-contained.)*
3. **Phase 1B — deepen the rites:** add if-then plans to the Gauge (d≈0.65, best buy), the Square (values→intention), and a capped self-compassion rite.
4. **Phase 1E — the atmosphere pass:** the warm serif "lodge at night" system + optional soundscapes + the WCAG 1.4.1 fix.
5. **Correct `RESEARCH_BASIS.md`** for the retraction (Brooks 2016) and re-anchor the ritual claim on Lang & Xygalatas / Legare / Hobson.

*The perfect ashlar is a direction, not a deadline. Build it true, not fast.*
