# On-Device Verification Checklist

Everything below was written and statically checked (grep, wiring, quote/brace balance) but
**not compiled or run** in the authoring environment. This is the pass to run in Android
Studio on a real device before trusting the build. Work top-to-bottom; the **§1 crisis pathway
is blocking** — do not ship or demo without it green.

How to record a run: copy the checklist into a dated note and mark each line
`PASS` / `FAIL` / `N/A` with a one-line observation. A FAIL in §1 stops the release.

---

## 0. Build & launch

- [ ] `.env` created with a real `GEMINI_API_KEY` (see `.env.example`).
- [ ] In `app/build.gradle.kts`, remove the debug-only line noted in the README
      (`signingConfig = signingConfigs.getByName("debugConfig")`) if the import fails.
- [ ] `Build > Make Project` succeeds with **no compile errors**.
      (Priorities to eyeball first — the files changed most recently:
      `ui/AshlarAppViewModel.kt`, `ui/screens/ToolsScreen.kt`, `ui/components/CrisisSupport.kt`,
      `safety/CrisisDetector.kt`, `ui/TracingBoardApp.kt`.)
- [ ] App installs and opens to **The Board** without a crash.
- [ ] Run unit tests — all pure-logic suites **green**: `CrisisDetectorTest`, `PlumbLineTest`,
      `GaugeTest`, `MouthToEarTest`, `DegreesTest`. (These have no Android deps, so
      `./gradlew testDebugUnitTest` runs them fast. This is the real red→green that couldn't run
      during authoring — if any fail, that's a genuine finding, not a false alarm.)

---

## 1. Crisis-safety pathway — BLOCKING

This is the reason the app is allowed to invite people to "lay down the weight." Prove it works.

**1a. Always-available help**
- [ ] The header shows **NEED HELP?** on every screen (Board, Chamber, Tools).
- [ ] Tapping it opens the help dialog **immediately** — no AI call, no "are you sure?", no delay.
- [ ] Dialog opens correctly with the **phone in airplane mode** (proves it's on-device).
- [ ] **During the first-run initiation rite**, the "NEED HELP?" header is present, and typing a
      crisis phrase into the "what weighs on you" step surfaces the help dialog. Safety is never
      gated behind onboarding.

**1b. Auto-detection — direct language**
- [ ] In the **Chamber**, type `I want to kill myself` → help dialog surfaces on its own.
- [ ] In the **journal** (Board → After-the-day record), type the same → dialog surfaces.

**1c. Auto-detection — euphemism (the important one)**
- [ ] In the Chamber, type `the world would be lighter without me` (contains **no** keyword like
      "suicide"/"die") → dialog **still** surfaces. This is the case keyword-only nets miss.
- [ ] Type `I don't want to be here anymore` → dialog surfaces.

**1d. Tappable human help**
- [ ] **CALL 988** opens the phone dialer pre-filled with 988 (it should *not* auto-call).
- [ ] **TEXT HOME TO 741741** opens the SMS app addressed to 741741 with body `HOME`.
- [ ] **CALL 116 123** (Samaritans) opens the dialer with 116123.
- [ ] **OPEN DIRECTORY** opens findahelpline.com in a browser.
- [ ] On a device with no dialer/browser, tapping does **not** crash the app (fails silently).

**1e. Additive + re-arm behavior**
- [ ] The dialog never blocks, edits, or hides what the user typed — text is intact behind it.
- [ ] **CLOSE** dismisses it; the user can keep writing.
- [ ] After dismissing, keep typing in the *same* entry → it does **not** nag on every keystroke.
- [ ] Purge/clear the Chamber (or save & clear the journal), then type a crisis phrase in a
      **new** entry → the dialog surfaces **again** (proves `resetAuto()` re-armed the detector).

**1f. No false alarm on benign text**
- [ ] Type an ordinary entry (`long day, but the guitar practice helped`) → dialog does **not** fire.

---

## 2. Tone / repositioning (craftsman, not commando)

Spot-check that nothing still reads as tactical/military:
- [ ] **Tools** tab reads: Gavel *"Sharpen the Mind"*, Plumb *"Straighten a Thought"*,
      Level *"Steady the Breath"*, Mouth to Ear *"Memory Work"*.
- [ ] The Gavel button says **BEGIN THE DRILL** (not "cognitive shock").
- [ ] The Plumb prompt reads about a thought *standing true / leaning* (not "verticality of your logic"),
      and its result panel says **SQUARED TO REALITY**.
- [ ] Mouth to Ear shows a **values/principle** to memorize — **not** a police procedure (no "PSO 302.1").
- [ ] Board's daily card reads as a warm **reflection/word for today** (not "DAILY INTELLIGENCE").
- [ ] The reminder notification talks about a **daily reflection / journal** (not a "Cognitive
      Resilience briefing"). Trigger it via the reminder worker or wait for the scheduled time.

---

## 3. Core loop smoke test

**First-run initiation rite (before anything else on a fresh install):**
- [ ] On first launch you land in **THE CHAMBER OF REFLECTION**, not the Board — a solemn welcome,
      then: a "how heavily are you carrying things" slider, a "rough edge" free-text step, an
      "intention" step, and a closing that admits you as an **Entered Apprentice**.
- [ ] BACK preserves entries; nothing can be "failed"; the bottom nav is hidden during the rite.
- [ ] After "BEGIN THE WORK" you enter the app, and **force-quitting + reopening does not repeat
      the rite** (it persists `initiated`).
- [ ] On an *already-initiated* install, cold start goes **straight to the Board** with no flash of
      the initiation screen (the null-loading state handles this).


- [ ] **The Path hero** is the top of the Board: the rough→perfect ashlar visual, the current
      **degree name**, and a **"% TOWARD [next degree]"** progress bar — all in one card. The stone's
      smoothness reflects *real journey progress* (it advances as you do the work), **not** a manual
      slider (the old "Refinement Chisel" is gone). At the summit it reads **PERFECT ASHLAR ACHIEVED /
      MASTER OF THE CRAFT**.
- [ ] Daily reflection card shows a **bundled word** (today's on launch); **SYNC** advances to the
      next word; the **streak** increments once/day. Works with the **phone in airplane mode** — no
      network, no API key (the old Gemini call is gone).
- [ ] Journal: add an entry with tags (`#focus #recovery`) → it appears in the list and lights the
      30-day heatmap.
- [ ] **Chamber — two modes** via a LET GO / KEEP toggle:
      - **LET GO**: type, tap **RELEASE IT**, feel the haptic buzz, text clears (nothing saved).
      - **KEEP**: a meaning-oriented prompt (with **ANOTHER** to cycle) → write → **KEEP THIS** saves
        it to **WHAT YOU'VE KEPT** (persists across restart); each kept reflection can be deleted.
      - Both text fields are crisis-scanned (safety net in either mode).
- [ ] **The Level** (real paced-breathing timer): the guiding circle grows for ~4s (**BREATHE IN**)
      and shrinks for ~6s (**BREATHE OUT**) — one full breath every ~10s (≈6/min, the resonance pace),
      with a per-phase seconds countdown in the centre. No more hardcoded "72 BPM".
- [ ] **The Gavel**: the Tamil letter drill advances on tap.
- [ ] **The Plumb** (now a real 4-step CBT thought-record): walk situation → thought → tilt(s) →
      evidence; NEXT is disabled until the situation/thought lines are filled; BACK preserves what
      you typed; the final **SQUARED TO REALITY** card mirrors *your own words* back (your thought,
      the tilt names you picked, your evidence) and ends with an "upright action" prompt.
      Skipping the tilt step and leaving evidence blank still produces a calm, sensible reflection.
      **CHECK ANOTHER THOUGHT** resets the flow.
- [ ] **The Gauge** (new — Divide the Day): pick a part (Service / Work / Rest), add an item; the
      balance message nudges when Service or Rest is empty and affirms ("squared") once all three
      have items; tapping an item toggles its done-box and updates the **HONOURED %**.
- [ ] **Mouth to Ear** (now real): pick a default principle (or write your own) → the principle
      shows with a **HIDE MORE** slider that masks more words as you raise it → type a recall
      attempt → **CHECK RECALL** shows a % and an encouraging line; **CHOOSE ANOTHER** resets.
- [ ] **The degree in the Path hero** advances with the reflection streak, journal entries, **and**
      deliberate practice: completing a Plumb thought-record, a held Mouth-to-Ear recall, or a fully
      honoured Gauge day each nudge it (Plumb/Gauge count double), and the ashlar visibly smooths.
- [ ] **Persistence**: do one Plumb (reach the reflection), then force-quit and reopen — the Degree
      progress reflects it and does **not** reset. (Plumb/Gauge/recall counts are stored in
      DataStore alongside the streak.)
- [ ] **Tools earned by degree** (the rite of passage): as a fresh install (Entered Apprentice),
      the Tools tab shows **Gauge, Gavel, Level open** and **Plumb + Mouth-to-Ear veiled** with an
      "AWAITS THE FELLOWCRAFT / MASTER MASON" label and a lock (not tappable). As the Degree rises,
      the Plumb unlocks at Fellowcraft and Mouth-to-Ear at Master Mason. **Safety unaffected:** the
      Chamber (a separate tab) and the always-on "NEED HELP?" stay open regardless of degree.
- [ ] Data persists across an app restart (DataStore): streak, journal entries, chisel position.

---

## 4. Regression guard

- [ ] No new crashes in Logcat during the run above.
- [ ] Rotating the device / backgrounding + resuming does not lose the crisis dialog's safety
      behavior or the current entry text.

---

### Notes for the tester
- The crisis phrase list lives in `safety/CrisisDetector.kt`; if a euphemism you'd expect isn't
  caught, add it there (it's a plain list) and re-run §1c.
- Crisis numbers are US/UK-centric right now by design; regional localization is a tracked
  follow-on in `MASTER_PLAN.md` Phase 1A. Note the tester's country and whether Find-a-Helpline
  covered it.
