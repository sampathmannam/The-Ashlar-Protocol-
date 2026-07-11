package com.ashlarprotocol

import com.ashlarprotocol.tools.Degree
import com.ashlarprotocol.tools.Degrees
import com.ashlarprotocol.tools.WorkStats
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * The Degrees — the progression spine (Entered Apprentice -> Fellowcraft -> Master Mason).
 * "The work" the user does across the app accrues toward the next degree. Grounds the ritual of
 * advancement in Self-Determination Theory (competence) and the progress principle / small wins
 * (Amabile & Kramer). See docs/RESEARCH_BASIS.md §9. Pure logic — no Android, no network.
 */
class DegreesTest {

    @Test
    fun scoreWeightsDeliberatePracticeMore() {
        // streak(1) + journal(1) + plumb(1*2) + gauge(1*2) + recall(1) = 7
        val s = WorkStats(briefingStreak = 1, journalEntries = 1, plumbSessions = 1, gaugeDaysComplete = 1, recallSessions = 1)
        assertEquals(7, Degrees.score(s))
    }

    @Test
    fun currentDegreeIsHighestThresholdReached() {
        assertEquals(Degree.ENTERED_APPRENTICE, Degrees.current(0))
        assertEquals(Degree.ENTERED_APPRENTICE, Degrees.current(14))
        assertEquals(Degree.FELLOWCRAFT, Degrees.current(15))
        assertEquals(Degree.FELLOWCRAFT, Degrees.current(39))
        assertEquals(Degree.MASTER_MASON, Degrees.current(40))
        assertEquals(Degree.MASTER_MASON, Degrees.current(1000))
    }

    @Test
    fun negativeScoreFallsBackToFirstDegree() {
        assertEquals(Degree.ENTERED_APPRENTICE, Degrees.current(-5))
    }

    @Test
    fun nextDegreeChainEndsAtMaster() {
        assertEquals(Degree.FELLOWCRAFT, Degrees.next(Degree.ENTERED_APPRENTICE))
        assertEquals(Degree.MASTER_MASON, Degrees.next(Degree.FELLOWCRAFT))
        assertNull(Degrees.next(Degree.MASTER_MASON))
    }

    @Test
    fun isUnlockedWhenCurrentDegreeReachesRequirement() {
        // Same degree unlocks.
        assertTrue(Degrees.isUnlocked(Degree.ENTERED_APPRENTICE, Degree.ENTERED_APPRENTICE))
        assertTrue(Degrees.isUnlocked(Degree.FELLOWCRAFT, Degree.FELLOWCRAFT))
        // Higher current unlocks lower requirement.
        assertTrue(Degrees.isUnlocked(Degree.ENTERED_APPRENTICE, Degree.MASTER_MASON))
        assertTrue(Degrees.isUnlocked(Degree.FELLOWCRAFT, Degree.MASTER_MASON))
        // Lower current does NOT unlock a higher requirement.
        assertTrue(!Degrees.isUnlocked(Degree.FELLOWCRAFT, Degree.ENTERED_APPRENTICE))
        assertTrue(!Degrees.isUnlocked(Degree.MASTER_MASON, Degree.FELLOWCRAFT))
    }

    @Test
    fun towardNextLabelNamesTheNextDegreeOrNullAtSummit() {
        assertEquals("Toward Fellowcraft", Degrees.towardNextLabel(Degree.ENTERED_APPRENTICE))
        assertEquals("Toward Master Mason", Degrees.towardNextLabel(Degree.FELLOWCRAFT))
        assertNull(Degrees.towardNextLabel(Degree.MASTER_MASON))   // the work is now lifelong
    }
}
