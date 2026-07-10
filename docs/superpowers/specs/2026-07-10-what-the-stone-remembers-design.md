# What the Stone Remembers — Design Spec

**The "AI phase" (MASTER_PLAN Phase 4), done as the plan actually describes it:** a *scribe and mirror, never an oracle* — it reflects the user's own patterns back to them, privately. **No model. No generation. No network. Zero-hallucination by construction** (it can only surface what is literally in the on-device data).

## Decisions (locked in brainstorming, 2026-07-10)
- **Deterministic engine, no generative model.** (Zero-cost, on-device, zero-hallucination — the north-star; avoids the NilaMind LLM pain.)
- **Faithful scribe + guarded noticings.** The scribe (literal facts) is the robust core; a small number of hedged co-occurrence "noticings" are additive and heavily guardrailed. The **active coach is deferred**; a generative model is held unless explicitly revisited.
- **Pull-only surface** ("What the Stone Remembers"), reached from the Board — **no 4th tab** (keep the BOARD/CHAMBER/TOOLS IA). Pull-only sidesteps the JITAI "mistiming backfires" trap.
- **Safety stays model-independent.** §9 untouched; this layer is pure reflection, never advice.

## The engine — `tools/Reflections.kt` (pure, TDD'd)
`Reflections.reflect(input: ReflectionInput): List<Reflection>` where:
- `data class Reflection(kind: Kind, text: String, provenance: String)`, `enum Kind { FACT, NOTICING }`. Every reflection cites its data (`provenance`) — nothing free-floats.
- `ReflectionInput` is a plain snapshot of the already-persisted data (built at the VM boundary): `daysTended, currentRun, graceRemaining, degreeDisplay, intention, whoFive: List<WhoFiveResult>, roughEdge: RoughEdgeEntry?, cornerstone: CornerstoneEntry?, practicesCount: Int, journalCount: Int, plumbCount: Int, gaugeDays: Int, recallCount: Int, keptReflectionsCount: Int, signatureStrengths: List<Strength>, automaticityLevel: Int (-1=none), rhythm: RhythmAnchor?`.

### Facts (the scribe) — always true, zero inference
Days tended + current run + grace held; the earned degree; the intention ("you said you're working toward '…'") and that practices point toward it; WHO-5 latest score; the Rough-Edge plan + "N slips, each logged and forgiven, M days since the last"; the Cornerstone you set; counts (practices, journal notes, Plumb thoughts squared, Gauge days, memory-work, kept reflections); signature strengths; the rhythm you set. Each only emitted when its data exists (e.g. no WHO-5 line until there's a result).

### Noticings (guarded) — only what is honestly computable from persisted per-day data
Given what actually persists (timestamped WHO-5 results; the Rough-Edge lapse-timestamp ledger), v1 ships exactly these, each **sample-floor-gated** and **hedged, never causal**:
1. **WHO-5 direction:** with ≥2 checks spanning ≥ ~2 weeks — "Your wellbeing check has moved X → Y since [first] — worth noticing." (Silent with fewer.)
2. **Rough-Edge lapse day-pattern:** with ≥4 logged slips — if they cluster on weekends/weekdays, "Your slips have tended to fall on {weekends|weekdays} — worth noticing, not proof (from N)." (Silent below the floor or with no clustering.)
3. **Rough-Edge longest clear stretch:** "That's your longest clear stretch yet — N days." (Encouraging, factual.)
> **Explicitly deferred (honest):** cross-dimension co-occurrence ("on Working days you arrived steady") needs per-day arrival/behaviour history the app does **not** persist today (only *today's* readiness). Faking it on absent/tiny data would violate the honesty line. A future ticket could add lightweight per-day logging to unlock it — not v1.

## Honesty guardrails (the crux — all unit-tested)
- **Sample floor:** a noticing surfaces only above its data threshold; below it, silence (tested).
- **Never causal:** noticings contain none of `causes / because / proves / will / makes you` (asserted in tests).
- **Small-sample honesty** baked into the copy ("worth noticing, not proof (from N)").
- **Provenance** on every reflection.
- All reflection copy swept by the existing **mortality + language SafetyAudit gates**.

## The surface — `ui/components/StoneRemembers.kt` (or a Board section)
A calm, scrollable "WHAT THE STONE REMEMBERS" view: facts first (your history, grouped), then any noticings clearly set apart beneath a divider and visibly hedged. Reached from a quiet Board entry line (e.g. under the stone), not a tab. Reuses the existing card idiom. Empty-early state: "The stone is still learning your shape — tend it a while, and it will remember."

## Testing & out-of-scope
- **Engine:** exhaustive unit tests — each fact type (present/absent), each noticing (below floor = silent, above = hedged text), the no-causal-language assertion, provenance non-empty. Both safety gates over all copy.
- **Surface:** device-verify it renders (facts + a noticing when data supports one), no crash.
- **Out of scope (YAGNI / deferred):** the active coach; any push/notification; cross-dimension co-occurrence (needs new per-day logging); any generative model.
