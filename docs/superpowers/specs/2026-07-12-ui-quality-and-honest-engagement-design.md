# UI Quality & the Honest Loop — Design Spec (Phase 5)

**Date:** 2026-07-12 · **Status:** approved, executing · **Branch:** `feat/phase5-craft-and-loop`

## Why

The user asked: *"where else can the UI be improved, bring more quality, get people to use the app frequently — well-backed."* Four parallel research threads (retention science, engagement ethics/dark-patterns, best-in-class UI craft, and a file-level audit of `origin/main`) converged on one conclusion: **the app's anti-manipulation concept is empirically correct — the gap is execution craft, plus a few honest engagement levers that were designed but never wired.**

The honest path to frequency is NOT hooks. It is: value early, a tiny practice anchored to an existing routine, feedback the app actually delivers, and a warm reason to return that it *earned*.

### Research basis (load-bearing citations)
- **Gamification adds no MH benefit:** Six et al. 2021, *JMIR Ment Health* (n=8,110) — not a moderator of symptom reduction (β=−0.03, p=.38), no adherence effect. → the metaphor must carry motivation, never points.
- **Usage ≠ help:** engagement↔outcome r≈0.16 (Psychiatry Research 2025); median 15-day MH-app retention 3.9% (Baumel 2019). → measure help, not DAU.
- **A missed day doesn't break a habit:** Lally 2010 (~66-day automaticity; one miss immaterial). → the never-resetting "tending the stone" is the correct mechanism; "never miss twice" (Clear) + self-compassion (Neff) is the only post-gap nudge.
- **Make it easy, anchor to a cue:** Fogg B=MAP; Wood & Neal 2007; implementation intentions Gollwitzer/Sheeran d=0.65.
- **Feedback must follow the primary action and be perceivable:** Norman.
- **Accessibility = quality:** WCAG 2.2 AA (2.5.5 targets, 1.4.1 not-by-color, 2.3.3 motion, 1.4.4 resize); Material 48dp; Android haptics ("rich not buzzy; if buzzy-or-nothing, choose nothing").
- **Calm Technology:** Weiser & Brown 1995; Case 2015 — minimum attention, use the periphery.

## Scope — Phase 5: "Craft & the Honest Loop"

The audit's cross-cut top-5, in four slices. Ships as **two reviewable PRs**: PR-1 = Slices A+B+C (craft & accessibility, mostly mechanical); PR-2 = Slice D (the honest loop, behavior changes). Build green + device-verified after each, per the overhaul discipline.

### Slice A — Craft foundation *(enabling)*
- **A1. Consume the design tokens.** Reconcile `theme/Tokens.kt` `Radius`/`Space` scales with real usage (radii → 12/18/24/32; space → 4/8/16/24; add missing entries or snap off-scale 14/20/28 values). Refactor call sites to read tokens by name. Removes the 54× copy-pasted `RoundedCornerShape(32.dp)`.
- **A2. One shared card, three emphasis tiers.** `AshlarCard(emphasis = Hero | Standard | Quiet)` (or `Modifier.ashlarCard(...)`). Stone = Hero; record cards = Quiet. Replaces the ~50 copy-pasted `.clip/.background/.border/.padding` chains and gives the Board a focal hierarchy.

### Slice B — Accessibility as quality
- **B1. Targets & type.** Every tap-label → `defaultMinSize(minHeight = A11y.minTarget)` (48dp); raise 8/9/10sp overrides to ≥12sp; the destructive 9sp one-tap "REMOVE" gets a real target + the Chamber's forgiving two-step confirm.
- **B2. Screen-reader state.** `selectable/toggleable` + `Role` + `selected`/`stateDescription` on every color-only toggle (strength chips, rhythm pills, plumb tilts, gauge checkbox, Square values, Temple finishes, mode chips, tag chips…); non-color affordance (checkmark on the Gauge item) where load-bearing; `stateDescription` on the weight/masking sliders.
- **B3. Reduced-motion.** Gate the `TheLevel` breathing pacer + Chamber pulse on `animationsEnabled()`, with a static/stepped fallback so a motion-sensitive user's core tool never silently breaks.

### Slice C — Distinct tool identity
- **C1. Bespoke on-theme glyph per tool** in `theme/AshlarIcons.kt` (plumb bob, square, level bubble, gavel/chisel, trowel, gauge, cornerstone, ear for Mouth-to-Ear, keystone for Board home) + `Role.Button`/merged semantics on tool rows and `contentDescription`. Follows the proven `AshlarFlame` pattern. Remove the dead `Icons.Default.Delete` import.

### Slice D — Repair the honest loop + "one thing for today"
- **D1. Wire the stone to the daily spine.** Call `bumpActionPulse()` on `completeChallenge` and `raiseCourse` (today only Plumb/Gauge/Recall pulse — and those run off-screen on Tools). Add a small local "the stone caught this" acknowledgment inside tool completions.
- **D2. One signature haptic.** Standardize on `LocalHapticFeedback`: a gentle `Confirm` on genuine completions (challenge done, course laid, reflection kept) and a reserved weighty "stone-set" cue for today's tending + laying a course. Replace the ad-hoc raw `Vibrator`. *Feedback, not reward.*
- **D3. Re-sequence the Board around one focus.** Promote the mood check-in (or a compact "one thing for today" = check-in state + top undone challenge) directly beneath the stone; group record cards into the collapsible "THE RECORD" section below; make the Chamber a single scroll container; thin the first-run zero-state (progressively reveal zero-state record cards).

## Guardrails (unchanged, non-negotiable)
- §9 crisis pathway untouched and topmost, reachable from every screen.
- Fully on-device, zero-cost: no network, no telemetry, no accounts.
- Anti-gamification: the pulse and haptic are *feedback*, not rewards. No points, coins, XP, or breakable/punishing streaks. Nothing added without a cited user-benefit rationale.
- Every change either removes a quality/accessibility defect or wires an honest loop already designed. No new dark patterns.

## Testing / verification strategy
- Pure logic (token scale, any "one thing for today" selection helper, pulse/haptic trigger points) → unit tests (TDD where a function is extractable). Existing 205-test suite guards regressions.
- Compose UI (cards, a11y modifiers, icons, board order, scroll) → `assembleDebug` green + on-device verification on `ZD2232FCR5` (screenshots, TalkBack spot-checks for the a11y slice).
- After each slice: build + tests green before commit; device-verify before PR.

## Out of scope → Phase 6 (the deeper engagement levers)
Onboarding quick-win + endowed progress (lay Temple course 1 during onboarding); the monthly mirror (peak-end); co-authored if-then implementation intentions; the invitational user-armed reminder redesign; the "you need me less now" graduation stance. Each is its own spec.
