package com.example

import com.example.tools.Effort
import com.example.tools.Readiness
import com.example.tools.Working
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * The daily "Working" — a mood-adaptive practice that asks LESS on a hard day and pays MORE
 * acknowledgment, never "you're behind". Grounds Behavioral Activation (floor tasks that are
 * near-impossible to fail are the treatment on low days; Ekers 2014), Fogg's B=MAP (when motivation
 * is low, shrink the action), and Finch's mood-adaptive reward. Pure, on-device. See
 * docs/GAMIFICATION_PLAN.md §4 / docs/SPEC_PHASE1_STONE P0.2.
 */
class WorkingTest {

    @Test
    fun eachReadinessMapsToItsMatchingEffortByDefault() {
        assertEquals(Effort.FLOOR, Working.effortFor(Readiness.LOW))
        assertEquals(Effort.NORMAL, Working.effortFor(Readiness.STEADY))
        assertEquals(Effort.STRETCH, Working.effortFor(Readiness.STRONG))
    }

    @Test
    fun theDifficultyDialShiftsEffortButNeverOutOfRange() {
        // Dial lighter/heavier moves one tier; clamped at the ends.
        assertEquals(Effort.NORMAL, Working.effortFor(Readiness.LOW, dial = 1))
        assertEquals(Effort.FLOOR, Working.effortFor(Readiness.LOW, dial = -5)) // clamped, never below floor
        assertEquals(Effort.NORMAL, Working.effortFor(Readiness.STRONG, dial = -1))
        assertEquals(Effort.STRETCH, Working.effortFor(Readiness.STRONG, dial = 9)) // clamped at stretch
    }

    @Test
    fun aLowDayAlwaysOffersAFloorTaskThatCannotEasilyFail() {
        // Even nudging "heavier" on a low day should not jump past a gentle step.
        assertTrue(Working.isFloor(Working.effortFor(Readiness.LOW)))
        assertFalse(Working.isFloor(Working.effortFor(Readiness.STRONG)))
    }

    @Test
    fun acknowledgmentPaysMoreOnAHardDayAndNeverShames() {
        Readiness.values().forEach { r ->
            val msg = Working.acknowledgment(r)
            assertTrue("non-blank for $r", msg.isNotBlank())
            val lower = msg.lowercase()
            assertFalse("no shame framing for $r", lower.contains("behind") || lower.contains("fail") || lower.contains("lazy"))
        }
        // The low-readiness acknowledgment is distinct and warmer (it explicitly honors showing up).
        val low = Working.acknowledgment(Readiness.LOW).lowercase()
        assertTrue("honors showing up on a hard day", low.contains("hard") || low.contains("brave") || low.contains("counts"))
    }

    @Test
    fun readinessForTodayReturnsTheStoredCheckInOnlyWhenItIsFromToday() {
        // Checked in today -> use it.
        assertEquals(Readiness.LOW, Working.readinessForToday(Readiness.LOW, storedDay = 100L, today = 100L))
        // Yesterday's check-in has expired -> ask again.
        assertEquals(null, Working.readinessForToday(Readiness.STRONG, storedDay = 99L, today = 100L))
        // Never checked in -> null.
        assertEquals(null, Working.readinessForToday(null, storedDay = -1L, today = 100L))
    }
}
