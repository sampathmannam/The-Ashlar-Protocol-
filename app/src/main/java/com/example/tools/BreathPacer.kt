package com.example.tools

import kotlin.math.ceil

/**
 * The Level — pure logic for a paced-breathing timer.
 *
 * The operative mason's level tests for true horizontal; the speculative level is equanimity. Here
 * it's a breathing pacer set, by default, to ~6 breaths per minute (0.1 Hz "resonance frequency")
 * with a longer exhale — the pace that most reliably raises HRV / cardiac vagal tone and is linked
 * to reduced stress and anxiety (see docs/RESEARCH_BASIS.md §8). Pure + deterministic so it's
 * testable and the visual only renders it.
 */

enum class BreathPhase { INHALE, HOLD_IN, EXHALE, HOLD_OUT }

/** A breathing pattern, in milliseconds per phase. Holds of 0 are simply skipped. */
data class BreathPattern(
    val inhaleMs: Int,
    val holdInMs: Int,
    val exhaleMs: Int,
    val holdOutMs: Int
) {
    val totalMs: Int get() = inhaleMs + holdInMs + exhaleMs + holdOutMs

    companion object {
        /** ~6 breaths/min (0.1 Hz resonance); longer exhale for vagal tone. RESEARCH_BASIS §8. */
        val RESONANCE = BreathPattern(inhaleMs = 4000, holdInMs = 0, exhaleMs = 6000, holdOutMs = 0)
    }
}

/**
 * A snapshot of the breath at a moment in time.
 * @param scale 0f fully exhaled … 1f fully inhaled — drives the size of the guiding circle.
 * @param secondsLeft whole seconds remaining in the current phase (for a countdown).
 */
data class BreathState(val phase: BreathPhase, val scale: Float, val secondsLeft: Int)

object BreathPacer {

    /** The breath state at [elapsedMs] into a continuously-repeating [pattern]. */
    fun stateAt(elapsedMs: Long, pattern: BreathPattern): BreathState {
        val total = pattern.totalMs
        if (total <= 0) return BreathState(BreathPhase.INHALE, 0f, 0)

        // A stray negative elapsed is treated as the very start (INHALE), not wrapped to the cycle's end.
        val safeElapsed = if (elapsedMs < 0L) 0L else elapsedMs
        val t = (safeElapsed % total).toInt()

        val inhaleEnd = pattern.inhaleMs
        val holdInEnd = inhaleEnd + pattern.holdInMs
        val exhaleEnd = holdInEnd + pattern.exhaleMs

        return when {
            t < inhaleEnd -> BreathState(
                BreathPhase.INHALE,
                if (pattern.inhaleMs == 0) 1f else t.toFloat() / pattern.inhaleMs,
                secondsFor(inhaleEnd - t)
            )
            t < holdInEnd -> BreathState(BreathPhase.HOLD_IN, 1f, secondsFor(holdInEnd - t))
            t < exhaleEnd -> BreathState(
                BreathPhase.EXHALE,
                if (pattern.exhaleMs == 0) 0f else 1f - (t - holdInEnd).toFloat() / pattern.exhaleMs,
                secondsFor(exhaleEnd - t)
            )
            else -> BreathState(BreathPhase.HOLD_OUT, 0f, secondsFor(total - t))
        }
    }

    private fun secondsFor(remainingMs: Int): Int = ceil(remainingMs / 1000.0).toInt()
}
