package com.ashlarprotocol.tools

/**
 * The rite of passage's trigger.
 *
 * The degree score ([Degrees.score]) advances silently as work accrues. This turns a threshold
 * crossing into a *ceremony*: [pending] returns the next unacknowledged degree the moment it is
 * earned, so the app can mark the raising with a rite — then never again once acknowledged.
 *
 * It advances **one degree at a time, in order** (never skips a rite even on a big jump), and never
 * fires past the summit. Pure, on-device — no clock, no network, no state of its own; the caller
 * owns "which degree has been acknowledged" (persisted in DataStore).
 */
object Advancement {

    /**
     * The next degree to celebrate, or null if there is nothing to raise the member into.
     *
     * @param acknowledgedOrdinal the ordinal of the highest degree already ceremonially marked
     *   (0 = Entered Apprentice, the entry degree, which needs no raising).
     * @param score the current [Degrees.score].
     */
    fun pending(acknowledgedOrdinal: Int, score: Int): Degree? {
        val all = Degree.values()
        val nextOrdinal = acknowledgedOrdinal.coerceAtLeast(0) + 1
        if (nextOrdinal >= all.size) return null              // summit already acknowledged
        val candidate = all[nextOrdinal]
        return if (score.coerceAtLeast(0) >= candidate.threshold) candidate else null
    }
}
