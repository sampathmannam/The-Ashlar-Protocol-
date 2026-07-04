package com.example

import com.example.tools.Degree
import com.example.tools.Degrees
import com.example.tools.WorkStats
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
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
    fun journeyProgressSpansTheWholeArcRoughToPerfect() {
        // 0 at the start, 1 at the final degree's threshold (Master Mason = 40), clamped beyond.
        assertEquals(0f, Degrees.journeyProgress(0), 0.0001f)
        assertEquals(0.5f, Degrees.journeyProgress(20), 0.0001f)
        assertEquals(1f, Degrees.journeyProgress(40), 0.0001f)
        assertEquals(1f, Degrees.journeyProgress(1000), 0.0001f)
        assertEquals(0f, Degrees.journeyProgress(-10), 0.0001f)
    }

    @Test
    fun progressToNextIsFractionWithinBand() {
        assertEquals(0f, Degrees.progressToNext(0), 0.0001f)     // just entered EA (band 0..15)
        assertEquals(0.2f, Degrees.progressToNext(20), 0.0001f)  // Fellowcraft band 15..40: (20-15)/25
        assertEquals(1f, Degrees.progressToNext(40), 0.0001f)    // Master Mason: nothing above
        assertEquals(1f, Degrees.progressToNext(1000), 0.0001f)
    }
}
