package com.example.tools

/**
 * The daily "Working" — the mood-adaptive practice at the heart of the day.
 *
 * It asks LESS on a hard day (a "floor" task engineered to be nearly impossible to fail — which for
 * low mood is not a compromise but the Behavioral-Activation treatment itself; Ekers 2014) and pays
 * MORE acknowledgment, never "you're behind" (Finch's mood-adaptive reward). Motivation fluctuates;
 * ability is the reliable lever, so on a low day we shrink the ask rather than push (Fogg B=MAP).
 * Pure, on-device. See docs/GAMIFICATION_PLAN.md §4 and docs/SPEC_PHASE1_STONE P0.2.
 */

/** How the person arrives today — a light check-in that scales the day's ask. */
enum class Readiness(val display: String) {
    LOW("Low"), STEADY("Steady"), STRONG("Strong")
}

/** The size of today's practice. FLOOR is the gentle, near-unfailable step. */
enum class Effort(val display: String) {
    FLOOR("Floor"), NORMAL("Normal"), STRETCH("Stretch")
}

object Working {

    /**
     * The effort tier for how the person arrives, adjusted by their difficulty [dial] (negative =
     * lighter, positive = heavier). Clamped so a low day never skips its gentle floor and a strong
     * day never overreaches — the person keeps a hand on the dial, not just the algorithm.
     */
    fun effortFor(readiness: Readiness, dial: Int = 0): Effort {
        val idx = (readiness.ordinal + dial).coerceIn(0, Effort.values().lastIndex)
        return Effort.values()[idx]
    }

    /** True for the gentlest tier — a task engineered to be nearly impossible to fail. */
    fun isFloor(effort: Effort): Boolean = effort == Effort.FLOOR

    /**
     * Warm acknowledgment for showing up — paying MORE on a hard day and never shaming. Turning up
     * when it's hard is the braver thing, and the copy says so.
     */
    fun acknowledgment(readiness: Readiness): String = when (readiness) {
        Readiness.LOW ->
            "Showing up on a hard day is the braver thing — that counts, and it counts double."
        Readiness.STEADY -> "Good — steady hands on the stone today."
        Readiness.STRONG -> "Strong day. A little further than usual, if you feel like it."
    }
}
