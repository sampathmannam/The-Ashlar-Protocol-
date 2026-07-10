package com.ashlarprotocol.tools

/**
 * The Cornerstone — squaring your surroundings so the right choice is the easy one (Phase 4, F1).
 *
 * The highest-leverage, best-evidenced move in self-discipline is NOT resisting temptation in the
 * moment — it is arranging the situation *before* the impulse arrives, so the good behaviour is
 * low-friction and cued, and the bad default is high-friction. People with the most self-control
 * exert the least willpower; they engineer their environment (Duckworth, Gendler & Gross 2016 —
 * situation selection/modification beats response inhibition; Wood 2019 — reduce friction for good
 * habits, add it for bad; Galla & Duckworth 2015 — habits/automaticity, not effort). See
 * docs/RESEARCH_INTEGRATION.md §1.1–1.2, F1.
 *
 * IMPORTANT framing (honesty): this helps *you* redesign *your own* environment (self-nudging, which
 * is well-supported). It is NOT a claim that the app nudges you — third-party population "nudge"
 * effects are contested (Maier et al. 2022). The copy says "engineer your room," never "we'll nudge
 * you." One change at a time (Fogg B=MAP — lower the ability cost). Pure, on-device: the tool only
 * *prompts you to act in the world*; it stores your plan and automates nothing.
 */
object Cornerstone {

    /** Where the behaviour is triggered — the cue you'll design around. */
    enum class CueKind(val display: String) {
        TIME("a time of day"),
        PLACE("a place"),
        AFTER_ACTION("after something you already do"),
        OBJECT("an object within reach")
    }

    /** Whether a move makes a GOOD habit easier (reduce friction) or a BAD default harder (add it). */
    enum class Direction { REDUCE, ADD }

    data class FrictionMove(val display: String, val direction: Direction)

    /** Curated, concrete, self-directed moves — generic (no brand/app specifics), one small step each. */
    val MOVES: List<FrictionMove> = listOf(
        // REDUCE friction — make the good choice the path of least resistance.
        FrictionMove("Lay it out the night before, ready to go", Direction.REDUCE),
        FrictionMove("Keep it in plain sight where the cue is", Direction.REDUCE),
        FrictionMove("Put it one step away — nothing between you and starting", Direction.REDUCE),
        FrictionMove("Leave yourself a note where you'll pass it", Direction.REDUCE),
        // ADD friction — put a few honest steps between you and the easy wrong choice.
        FrictionMove("Move it to another room", Direction.ADD),
        FrictionMove("Remove the shortcut — make it take a few deliberate steps", Direction.ADD),
        FrictionMove("Add one small step before you can begin", Direction.ADD),
        FrictionMove("Hand it to someone, or put it out of reach", Direction.ADD)
    )

    /** Moves that make a good habit easier. */
    fun reduceMoves(): List<FrictionMove> = MOVES.filter { it.direction == Direction.REDUCE }

    /** Moves that make a bad default harder. */
    fun addMoves(): List<FrictionMove> = MOVES.filter { it.direction == Direction.ADD }

    /** Every user-facing string, for the safety sweep. */
    fun allText(): List<String> = CueKind.values().map { it.display } + MOVES.map { it.display }
}
