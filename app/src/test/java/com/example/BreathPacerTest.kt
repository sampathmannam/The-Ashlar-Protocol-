package com.example

import com.example.tools.BreathPacer
import com.example.tools.BreathPattern
import com.example.tools.BreathPhase
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * The Level — pure logic for a paced-breathing timer. Deliberately UI-free so the pacing is
 * testable and the visual just renders it. The default is ~6 breaths/min (0.1 Hz resonance) with a
 * longer exhale — the evidence-based sweet spot (see docs/RESEARCH_BASIS.md §8).
 */
class BreathPacerTest {

    @Test
    fun resonanceIsTenSecondsSixBreathsPerMinute() {
        assertEquals(10000, BreathPattern.RESONANCE.totalMs) // 60_000 / 10_000 = 6 breaths per minute
    }

    @Test
    fun inhaleRisesFromEmptyToFull() {
        val p = BreathPattern.RESONANCE
        assertEquals(BreathPhase.INHALE, BreathPacer.stateAt(0, p).phase)
        assertEquals(0f, BreathPacer.stateAt(0, p).scale, 0.001f)
        assertEquals(0.5f, BreathPacer.stateAt(2000, p).scale, 0.001f) // halfway through the 4s inhale
    }

    @Test
    fun exhaleFallsFromFullToEmpty() {
        val p = BreathPattern.RESONANCE
        assertEquals(BreathPhase.EXHALE, BreathPacer.stateAt(4000, p).phase)
        assertEquals(1f, BreathPacer.stateAt(4000, p).scale, 0.001f)
        assertEquals(0.5f, BreathPacer.stateAt(7000, p).scale, 0.001f) // halfway through the 6s exhale
    }

    @Test
    fun cycleWrapsAround() {
        val p = BreathPattern.RESONANCE
        assertEquals(BreathPhase.INHALE, BreathPacer.stateAt(10000, p).phase) // wraps back to the start
        assertEquals(0f, BreathPacer.stateAt(10000, p).scale, 0.001f)
    }

    @Test
    fun boxBreathingUsesAllFourPhases() {
        val p = BreathPattern(4000, 4000, 4000, 4000)
        assertEquals(16000, p.totalMs)
        assertEquals(BreathPhase.INHALE, BreathPacer.stateAt(1000, p).phase)
        assertEquals(BreathPhase.HOLD_IN, BreathPacer.stateAt(5000, p).phase)
        assertEquals(1f, BreathPacer.stateAt(5000, p).scale, 0.001f)
        assertEquals(BreathPhase.EXHALE, BreathPacer.stateAt(9000, p).phase)
        assertEquals(BreathPhase.HOLD_OUT, BreathPacer.stateAt(13000, p).phase)
        assertEquals(0f, BreathPacer.stateAt(13000, p).scale, 0.001f)
    }

    @Test
    fun secondsLeftCountsDownWithinPhase() {
        val p = BreathPattern.RESONANCE
        assertEquals(4, BreathPacer.stateAt(0, p).secondsLeft)    // 4s inhale, 4 seconds left
        assertEquals(1, BreathPacer.stateAt(3200, p).secondsLeft) // 800ms left -> ceil = 1
    }

    @Test
    fun handlesZeroAndNegativeElapsedSafely() {
        val p = BreathPattern.RESONANCE
        assertEquals(BreathPhase.INHALE, BreathPacer.stateAt(-1, p).phase) // never crashes on a stray negative
    }
}
