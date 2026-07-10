package com.ashlarprotocol.tools

/**
 * The kind streak — "tending the stone."
 *
 * Consistency that builds a habit WITHOUT the shame that makes people quit. Two numbers are kept:
 *  - [StreakState.daysTended] is CUMULATIVE and only ever grows. It never drops for a missed day,
 *    so the central stone (which it feeds) can never visibly regress. This is the primary signal.
 *  - [StreakState.currentRun] is the softer "on a roll" count. A single missed day is absorbed by a
 *    small capped reserve of grace days and the run continues; only a real lapse resets the run —
 *    and even then to 1 (a fresh start), never to zero and never framed as a loss.
 *
 * Evidence: Lally et al. (2010) — one missed day does not harm habit formation, so a miss must not
 * wipe progress; Sharif & Shu (2017) — a small capped reserve beats rigid all-or-nothing goals;
 * Wohl et al. (2010) — self-forgiveness after a lapse reduces future lapses. See
 * docs/GAMIFICATION_PLAN.md §5 and docs/RESEARCH_BASIS.md §9.
 *
 * Days are epoch-day integers so the logic is pure and deterministic; timezone/calendar handling
 * stays at the ViewModel boundary (see SPEC_PHASE1_STONE Open Q5). Pure, on-device.
 */

data class StreakState(
    /** Total days the person has tended the stone. Monotonic — only ever increases. */
    val daysTended: Int = 0,
    /** The current consecutive run, softened by grace days. Never shown as zero once started. */
    val currentRun: Int = 0,
    /** Remaining grace days that silently absorb the odd missed day. Capped at [KindStreak.MAX_GRACE]. */
    val graceRemaining: Int = KindStreak.MAX_GRACE,
    /** The epoch-day of the last tending, or -1 if never tended. */
    val lastTendedDay: Long = -1L
)

/** The result of tending on a given day: the new [state] plus what happened, for warm messaging. */
data class TendOutcome(
    val state: StreakState,
    /** True when the user already tended today — nothing changed (idempotent). */
    val alreadyTendedToday: Boolean = false,
    /** How many grace days this tending spent to keep the run alive. */
    val graceUsed: Int = 0,
    /** True when the user returned after a real lapse — a warm comeback, never a "lost streak". */
    val isComeback: Boolean = false
)

object KindStreak {

    /** A small, capped reserve — Sharif & Shu found more than a couple of grace days adds little. */
    const val MAX_GRACE = 2

    /**
     * Tend the stone on [today] (an epoch-day). Returns the updated state. Idempotent within a day.
     * The cumulative total never decreases; a single miss is absorbed by grace; a real lapse yields
     * a warm comeback with a fresh run of 1.
     */
    fun tend(state: StreakState, today: Long): TendOutcome {
        // First tending ever.
        if (state.lastTendedDay < 0L) {
            return TendOutcome(
                state = StreakState(
                    daysTended = 1,
                    currentRun = 1,
                    graceRemaining = MAX_GRACE,
                    lastTendedDay = today
                )
            )
        }

        val gap = today - state.lastTendedDay
        // Same day (or a backwards clock) — already tended; change nothing.
        if (gap <= 0L) {
            return TendOutcome(state = state, alreadyTendedToday = true)
        }

        val missedDays = (gap - 1).toInt()
        return when {
            // On time — the next consecutive day. Grace replenishes toward the cap.
            missedDays == 0 -> TendOutcome(
                state = state.copy(
                    daysTended = state.daysTended + 1,
                    currentRun = state.currentRun + 1,
                    graceRemaining = minOf(MAX_GRACE, state.graceRemaining + 1),
                    lastTendedDay = today
                )
            )
            // A short gap the reserve can cover — the run continues, quietly.
            missedDays <= state.graceRemaining -> TendOutcome(
                state = state.copy(
                    daysTended = state.daysTended + 1,
                    currentRun = state.currentRun + 1,
                    graceRemaining = state.graceRemaining - missedDays,
                    lastTendedDay = today
                ),
                graceUsed = missedDays
            )
            // A real lapse — welcome them back. The total still grows; the run starts fresh at 1.
            else -> TendOutcome(
                state = state.copy(
                    daysTended = state.daysTended + 1,
                    currentRun = 1,
                    graceRemaining = MAX_GRACE,
                    lastTendedDay = today
                ),
                isComeback = true
            )
        }
    }

    /** Warm, self-forgiving copy for a comeback. Never loss-framed (no "lost"/"broke"/"failed"). */
    fun comebackMessage(): String =
        "Welcome back. Yesterday was hard, and that's okay — one day away doesn't erase the work " +
            "you've done. Pick up the tools again when you're ready."

    /**
     * A landmark "clean page" line when [epochDay] falls on a Monday (a new week). Temporal landmarks
     * re-motivate by letting people file past failure into a previous period — the fresh-start effect
     * (Dai, Milkman & Riis 2014; F8). Null on other days. Never loss-framed. (Epoch day 0 = Thursday,
     * so Monday ⇔ ((d % 7) + 7) % 7 == 4.)
     */
    fun freshStartLine(epochDay: Long): String? {
        val dow = ((epochDay % 7) + 7) % 7
        return if (dow == 4L) "And a new week begins — a clean page. Take up the stone again." else null
    }

    /**
     * Whole days since the Unix epoch in LOCAL time. [tzOffsetMillis] is the timezone's offset at
     * that instant (e.g. `TimeZone.getDefault().getOffset(millis)`), so a "day" rolls over at LOCAL
     * midnight rather than UTC midnight. Pure — the ViewModel supplies the offset at the boundary.
     */
    fun epochDay(millisSinceEpoch: Long, tzOffsetMillis: Int): Long =
        Math.floorDiv(millisSinceEpoch + tzOffsetMillis, 86_400_000L)

    /**
     * Migrate an existing user from the old resettable briefing streak to the kind model WITHOUT
     * wiping their progress: their prior streak becomes both the cumulative total and the current
     * run. A zero or negative legacy value means there was no prior progress.
     */
    fun seedFromLegacy(legacyStreak: Int, lastTendedDay: Long): StreakState =
        if (legacyStreak <= 0) StreakState()
        else StreakState(
            daysTended = legacyStreak,
            currentRun = legacyStreak,
            graceRemaining = MAX_GRACE,
            lastTendedDay = lastTendedDay
        )

    /**
     * How refined the central stone is, from cumulative tending. Asymptotic — always advancing with
     * each day tended, but never reaching "complete": the work is lifelong (anti-perfectionism, SPEC
     * P0.1). Monotonic in [daysTended] (which itself only ever grows), so a missed day never regresses
     * the stone. Early days each move it a visible step; later days refine ever more finely.
     */
    fun stoneProgress(daysTended: Int): Float {
        val d = daysTended.coerceAtLeast(0).toFloat()
        val k = 25f // days at which the stone is ~half-refined; tuned so early days feel visible
        return d / (d + k)
    }

    /**
     * A short, protectable label for the grace reserve. Surfacing the reserve as *scarce* (rather than
     * a silent auto-buffer) raises post-miss persistence, because a limited, named reserve carries a
     * small psychological cost so people protect it (Sharif & Shu, 2017/2019). Never a warning.
     */
    fun graceLabel(graceRemaining: Int): String {
        val g = graceRemaining.coerceIn(0, MAX_GRACE)
        return when (g) {
            0 -> "grace spent — the stone still holds"
            1 -> "1 grace held"
            else -> "$g grace held"
        }
    }

    /**
     * Warm, non-guilting note when a tending spent grace to keep the run alive; null when none spent.
     * A spent grace day is framed as the stone holding *for* you, never as a failure (anti-AVE / Wohl).
     */
    fun graceMessage(graceUsed: Int): String? =
        if (graceUsed <= 0) null
        else "The stone held for you — a day of grace kept the run. They replenish as you return."
}
