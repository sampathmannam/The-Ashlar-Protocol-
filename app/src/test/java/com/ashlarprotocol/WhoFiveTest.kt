package com.ashlarprotocol

import com.ashlarprotocol.tools.WhoFive
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * WHO-5 — pure logic for the wellbeing index (SPEC T3.1). Scoring, cadence, and a non-clinical
 * reflection. The reflection must never diagnose; the lowest band must point to help.
 */
class WhoFiveTest {

    @Test
    fun formIsTheStandardFiveItemSixOptionWho5() {
        assertEquals(5, WhoFive.ITEMS.size)
        WhoFive.ITEMS.forEach { assertTrue(it.isNotBlank()) }
        assertEquals(6, WhoFive.OPTIONS.size)
        assertEquals(listOf(5, 4, 3, 2, 1, 0), WhoFive.OPTIONS.map { it.value })
    }

    @Test
    fun scoreIsSumTimesFourOnZeroToHundred() {
        assertEquals(100, WhoFive.score(listOf(5, 5, 5, 5, 5)))
        assertEquals(0, WhoFive.score(listOf(0, 0, 0, 0, 0)))
        assertEquals(60, WhoFive.score(listOf(3, 3, 3, 3, 3)))
        // Out-of-range values clamp rather than blow up.
        assertEquals(100, WhoFive.score(listOf(9, 9, 9, 9, 9)))
        assertEquals(0, WhoFive.score(listOf(-3, -3, -3, -3, -3)))
    }

    @Test
    fun offeredAtBaselineThenAboutEveryTwoWeeks() {
        val now = 2_000_000_000_000L
        assertTrue("never taken → baseline", WhoFive.isDue(lastTakenMs = null, nowMs = now))
        val twoWeeks = 14L * 24 * 60 * 60 * 1000
        assertFalse("just took it → not due", WhoFive.isDue(now - twoWeeks + 1000, now))
        assertTrue("two weeks on → due again", WhoFive.isDue(now - twoWeeks, now))
    }

    @Test
    fun reflectionIsWarmNeverClinicalAndPointsToHelpWhenLow() {
        // No diagnostic labels anywhere.
        (0..100).forEach { s ->
            val r = WhoFive.reflection(s).lowercase()
            assertTrue(r.isNotBlank())
            assertFalse("must not diagnose", r.contains("depression") || r.contains("depressed") || r.contains("disorder"))
        }
        // The lowest stretch points to human help.
        assertTrue(WhoFive.reflection(8).lowercase().contains("help"))
    }
}
