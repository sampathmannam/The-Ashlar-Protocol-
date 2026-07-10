# The Temple — Fellowcraft tranche (courses 13–31)

> Executes against the approved design spec `docs/superpowers/specs/2026-07-10-the-temple-design.md` (the "author Fellowcraft/Master Mason in tranches" phasing). The engine, persistence, VM, and UI already handle any number of courses — this tranche is real, cited course *content* plus tests and verification.

**Goal:** Extend `Temple.COURSES` with the Fellowcraft degree — courses 13–31, each a genuine, research-cited practice (no filler XP), deepening the Apprentice foundations into competence.

**Global constraints (unchanged, LOCKED):** on-device; deterministic wages; never gate core tools/§9; wages only accrue; a miss takes nothing; quiet numbers; copy holds altitude (no willpower/grit/hustle/earn language) and passes the mortality + language safety gates.

## Task 1: Author the Fellowcraft courses (13–31)

**Files:** Modify `app/src/main/java/com/ashlarprotocol/tools/Temple.kt`; Test `app/src/test/java/com/ashlarprotocol/TempleTest.kt`.

- [ ] Add 19 `Course(...)` entries, index 13–31, `Degree.FELLOWCRAFT`, costs rising 8→13. Deeper craft: values-in-action, graded approach, flexibility, deepened cognitive + self-compassion work, connection, restoration, sleep rhythm, urge-surfing, deliberate practice, boundaries, and an identity reflection. Each carries a real citation.
- [ ] Extend `TempleTest`: assert the ladder now spans two degrees (EA present AND FC present), that course indices remain contiguous 1..N, and that every course (existing generic tests already cover name/unlocks/basis/cost > 0) still holds. Add `fellowcraftTrancheIsRealAndCited()`.
- [ ] Run: `gradle :app:testDebugUnitTest --tests "com.ashlarprotocol.TempleTest" --no-daemon --no-configuration-cache` → PASS.
- [ ] Commit: `feat(temple): the Fellowcraft tranche — courses 13–31 (deeper craft, cited)`.

## Task 2: Full green + safety + device-verify + finish

- [ ] `gradle :app:testDebugUnitTest :app:assembleDebug` → BUILD SUCCESSFUL, failures=0. (`Temple.allText()` in the SafetyAudit corpus auto-covers the new course copy; confirm both gates pass.)
- [ ] Commit any test/corpus follow-ups.
- [ ] Device-verify on ZD2232FCR5: seed `courses_raised`≈13 + wages via the DataStore poke so the Temple shows a Fellowcraft-labelled standing and the next FC course; screenshot; then RESTORE original data.
- [ ] Finish (gated on the founder's go, per no-push-without-ask): push `feat/the-temple-fellowcraft`, PR, merge to `origin/main`, remove worktree, update memory.

## Deferred (future tranches): Master Mason courses 32–50; monthly reflection into the mirror; adornment/customization.
