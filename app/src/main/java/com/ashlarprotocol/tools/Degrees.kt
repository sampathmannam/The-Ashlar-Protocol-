package com.ashlarprotocol.tools

/**
 * The Degrees — the app's progression spine, mirroring the craft's three degrees.
 *
 * "The work" a person does across the app (showing up daily, journalling, checking a thought on
 * the plumb, dividing the day on the gauge, committing a principle to memory) accrues a score,
 * and the score advances the degree. This is deliberately a slow, earned arc — the reward is
 * competence and visible progress, not points for their own sake (Self-Determination Theory;
 * Amabile & Kramer's progress principle). See docs/RESEARCH_BASIS.md §9. Pure, on-device.
 */

enum class Degree(val display: String, val threshold: Int) {
    ENTERED_APPRENTICE("Entered Apprentice", 0),
    FELLOWCRAFT("Fellowcraft", 15),
    MASTER_MASON("Master Mason", 40)
}

/** A snapshot of the work done so far. All counts default to 0 so callers supply only what they track. */
data class WorkStats(
    val briefingStreak: Int = 0,
    val journalEntries: Int = 0,
    val plumbSessions: Int = 0,
    val gaugeDaysComplete: Int = 0,
    val recallSessions: Int = 0
)

object Degrees {

    /** The deliberate practices (plumb, gauge) weigh double; showing up and journalling weigh one. */
    fun score(stats: WorkStats): Int =
        stats.briefingStreak +
            stats.journalEntries +
            stats.plumbSessions * 2 +
            stats.gaugeDaysComplete * 2 +
            stats.recallSessions

    /** The highest degree whose threshold the score has reached. */
    fun current(score: Int): Degree {
        val s = score.coerceAtLeast(0)
        return Degree.values().last { it.threshold <= s }
    }

    /**
     * Whether a practice that requires [required] is unlocked at the user's [current] degree.
     * Used to veil the later working tools until they're earned. NOTE: safety and basic-grounding
     * practices are never gated through this — they stay open at degree zero (see VISION §6/§8).
     */
    fun isUnlocked(required: Degree, current: Degree): Boolean =
        current.ordinal >= required.ordinal

    /** The degree after this one, or null at the summit. */
    fun next(degree: Degree): Degree? {
        val all = Degree.values()
        val idx = all.indexOf(degree)
        return if (idx in 0 until all.size - 1) all[idx + 1] else null
    }

    /**
     * Progress across the WHOLE arc — rough ashlar (0f) to perfect ashlar (1f) at the final
     * degree's threshold. Drives the central stone visual so it smooths as real work is done.
     */
    fun journeyProgress(score: Int): Float {
        val summit = Degree.values().last().threshold.toFloat()
        if (summit <= 0f) return 1f
        return (score.coerceAtLeast(0).toFloat() / summit).coerceIn(0f, 1f)
    }

    /** Progress from the current degree's threshold toward the next, in 0f..1f. 1f at Master Mason. */
    fun progressToNext(score: Int): Float {
        val s = score.coerceAtLeast(0)
        val cur = current(s)
        val nxt = next(cur) ?: return 1f
        val span = (nxt.threshold - cur.threshold).toFloat()
        if (span <= 0f) return 1f
        return ((s - cur.threshold).toFloat() / span).coerceIn(0f, 1f)
    }
}
