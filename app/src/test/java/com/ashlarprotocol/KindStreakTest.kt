package com.ashlarprotocol

import com.ashlarprotocol.tools.KindStreak
import com.ashlarprotocol.tools.StreakState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * The kind streak — "tending the stone." Consistency that builds a habit WITHOUT the shame that
 * makes people quit. Grounded in: Lally et al. (2010) — one missed day does not harm habit
 * formation, so a single miss must never wipe progress; Sharif & Shu (2017) — a small capped
 * reserve of grace days beats rigid all-or-nothing goals; Wohl et al. (2010) — self-forgiveness
 * after a lapse reduces future lapses. See docs/GAMIFICATION_PLAN.md §5 and docs/RESEARCH_BASIS.md §9.
 *
 * Days are passed as epoch-day integers (timezone handling stays at the ViewModel boundary), so the
 * logic here is pure and deterministic.
 */
class KindStreakTest {

    @Test
    fun firstTendStartsTheRunAndCountsOneDay() {
        val out = KindStreak.tend(StreakState(), today = 100L)
        assertEquals(1, out.state.daysTended)
        assertEquals(1, out.state.currentRun)
        assertFalse(out.alreadyTendedToday)
        assertFalse("first ever tend is not a comeback", out.isComeback)
    }

    @Test
    fun tendingTwiceInOneDayDoesNotDoubleCount() {
        val first = KindStreak.tend(StreakState(), today = 100L).state
        val second = KindStreak.tend(first, today = 100L)
        assertTrue(second.alreadyTendedToday)
        assertEquals("no second count for the same day", 1, second.state.daysTended)
        assertEquals(1, second.state.currentRun)
    }

    @Test
    fun consecutiveDaysGrowBothTheRunAndTheTotal() {
        var s = KindStreak.tend(StreakState(), today = 100L).state
        s = KindStreak.tend(s, today = 101L).state
        s = KindStreak.tend(s, today = 102L).state
        assertEquals(3, s.daysTended)
        assertEquals(3, s.currentRun)
    }

    @Test
    fun oneMissedDayIsAbsorbedByGrace_runContinues_notAComeback() {
        // Build a little run, then skip a single day (gap of 2).
        var s = KindStreak.tend(StreakState(), today = 100L).state
        s = KindStreak.tend(s, today = 101L).state
        val out = KindStreak.tend(s, today = 103L) // day 102 was missed
        assertFalse("a single miss is not a lapse", out.isComeback)
        assertEquals("run keeps going through one grace day", 3, out.state.currentRun)
        assertTrue("a grace day was spent", out.graceUsed >= 1)
    }

    @Test
    fun cumulativeTotalNeverDecreases_evenAfterALongLapse() {
        // Reach five tended days, then vanish for two weeks.
        var s = StreakState()
        for (d in 100L..104L) s = KindStreak.tend(s, today = d).state
        assertEquals(5, s.daysTended)
        val out = KindStreak.tend(s, today = 118L) // 13 missed days, far beyond grace
        assertEquals("the stone never regresses: total only grows", 6, out.state.daysTended)
    }

    @Test
    fun returningAfterGraceIsExhaustedIsAWarmComeback_notAReset() {
        var s = StreakState()
        for (d in 100L..104L) s = KindStreak.tend(s, today = d).state
        val out = KindStreak.tend(s, today = 118L)
        assertTrue("returning after a real lapse is a comeback", out.isComeback)
        assertEquals("the fresh run starts at 1, never shown as zero/lost", 1, out.state.currentRun)
        assertTrue("current run is never zero", out.state.currentRun >= 1)
    }

    @Test
    fun graceReserveIsCappedAndReplenishesWithConsistency() {
        var s = StreakState()
        // A long consistent run should leave grace at the cap, not unbounded.
        for (d in 100L..120L) s = KindStreak.tend(s, today = d).state
        assertEquals(KindStreak.MAX_GRACE, s.graceRemaining)
    }

    @Test
    fun comebackMessageIsWarmAndSelfForgiving_neverLossFramed() {
        val msg = KindStreak.comebackMessage().lowercase()
        // Must model self-forgiveness (Wohl 2010) and never weaponize loss.
        assertFalse("no loss framing", msg.contains("lost") || msg.contains("broke") || msg.contains("failed"))
        assertTrue(
            "welcomes the return",
            msg.contains("back") || msg.contains("welcome") || msg.contains("okay") || msg.contains("again")
        )
    }

    // --- pure helpers for the ViewModel/DataStore boundary ---

    @Test
    fun epochDayCountsWholeDaysFromTheEpochInLocalTime() {
        assertEquals(0L, KindStreak.epochDay(0L, 0))
        assertEquals(1L, KindStreak.epochDay(86_400_000L, 0))
        assertEquals(0L, KindStreak.epochDay(86_400_000L - 1L, 0))
    }

    @Test
    fun epochDayUsesTheTimezoneOffsetSoLocalMidnightWins() {
        // One second before UTC midnight, but a +2s local offset pushes it into the next local day.
        assertEquals(1L, KindStreak.epochDay(86_400_000L - 1_000L, 2_000))
        // Just after the UTC epoch, a negative offset pulls it back into the previous local day.
        assertEquals(-1L, KindStreak.epochDay(1_000L, -2_000))
    }

    @Test
    fun seedFromLegacyPreservesAnExistingStreakSoNoOneLosesProgress() {
        val seeded = KindStreak.seedFromLegacy(legacyStreak = 5, lastTendedDay = 100L)
        assertEquals("existing progress becomes the cumulative total", 5, seeded.daysTended)
        assertEquals(5, seeded.currentRun)
        assertEquals(KindStreak.MAX_GRACE, seeded.graceRemaining)
        assertEquals(100L, seeded.lastTendedDay)
    }

    @Test
    fun seedFromLegacyReturnsAFreshStateWhenThereIsNoPriorStreak() {
        assertEquals(StreakState(), KindStreak.seedFromLegacy(legacyStreak = 0, lastTendedDay = 0L))
        assertEquals(StreakState(), KindStreak.seedFromLegacy(legacyStreak = -3, lastTendedDay = 50L))
    }

    @Test
    fun stoneProgressStartsAtZeroAndClampsNegatives() {
        assertEquals(0f, KindStreak.stoneProgress(0), 0.0001f)
        assertEquals(0f, KindStreak.stoneProgress(-5), 0.0001f) // a stray negative never regresses below 0
    }

    @Test
    fun stoneProgressIsMonotonicAndNeverComplete() {
        // Each further day tended refines the stone a little more — always rising, never "complete".
        var prev = KindStreak.stoneProgress(0)
        for (d in 1..500) {
            val p = KindStreak.stoneProgress(d)
            assertTrue("day $d must not regress", p > prev)
            assertTrue("the stone is never 'complete'", p < 1f)
            prev = p
        }
        assertTrue("asymptotes below 1 — the work is lifelong", KindStreak.stoneProgress(1_000_000) < 1f)
    }

    @Test
    fun stoneProgressGivesVisibleEarlyMovement() {
        // The first day of tending must move the stone off zero by something a person can see.
        assertTrue(KindStreak.stoneProgress(1) > 0.02f)
    }
}
