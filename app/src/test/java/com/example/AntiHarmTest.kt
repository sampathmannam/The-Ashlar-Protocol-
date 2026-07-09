package com.example

import com.example.tools.AntiHarm
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Anti-harm (SPEC T3.2). On-device care, not analytics: gently answer a compulsive late-night pattern
 * with rest — and NEVER discourage doing the work, never use FOMO/streak framing.
 */
class AntiHarmTest {

    @Test
    fun nudgesRestWhenItsLateAndTheWorkIsAlreadyDone() {
        assertEquals(AntiHarm.REST_NUDGE, AntiHarm.restNudge(hourOfDay = 23, didTodaysWork = true))
        assertEquals(AntiHarm.REST_NUDGE, AntiHarm.restNudge(hourOfDay = 2, didTodaysWork = true))
    }

    @Test
    fun staysSilentDuringTheDay() {
        assertNull(AntiHarm.restNudge(hourOfDay = 14, didTodaysWork = true))
        assertNull(AntiHarm.restNudge(hourOfDay = 22, didTodaysWork = true)) // 22 is not yet "late"
        assertNull(AntiHarm.restNudge(hourOfDay = 5, didTodaysWork = true))  // 5am is morning again
    }

    @Test
    fun neverDiscouragesDoingTheWork() {
        // Late, but they haven't done today's work — say nothing; they may need to.
        assertNull(AntiHarm.restNudge(hourOfDay = 23, didTodaysWork = false))
        assertNull(AntiHarm.restNudge(hourOfDay = 3, didTodaysWork = false))
    }

    @Test
    fun theMessageIsRestNotFomo() {
        val m = AntiHarm.REST_NUDGE.lowercase()
        assertTrue(m.contains("rest"))
        assertFalse(
            "anti-harm copy must never use streak/FOMO framing",
            m.contains("streak") || m.contains("don't lose") || m.contains("keep it going") || m.contains("behind")
        )
    }
}
