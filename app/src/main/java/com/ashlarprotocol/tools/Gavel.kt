package com.ashlarprotocol.tools

/**
 * The Gavel — knock a rough corner off the stone.
 *
 * The common gavel is the tool for breaking the rough corners from the ashlar. Here it is a quick,
 * in-the-moment micro-practice for catching a recurring *reactive* pattern and pre-committing a
 * **competing response**: name the reaction, its trigger, and the truer move you'll make instead. This
 * is habit-reversal's competing response (relevant to tools/RoughEdge) crossed with an
 * implementation-intention *to interrupt* (Gollwitzer 1999 — "when X, I will Y" also shields ongoing
 * goals from derailment). It is distinct from the Plumb (a deliberate thought-record) and Practices
 * (building a *good* habit): the Gavel *interrupts a reactive one, as it rises.* Pure, on-device.
 */
object Gavel {

    /** A few common rough corners, to pick from or spark your own. Approach-framed, never a diagnosis. */
    val ROUGH_CORNERS: List<String> = listOf(
        "snapping when I feel criticised",
        "spiralling after one mistake",
        "checking my phone the moment I'm bored",
        "going quiet and pulling away when I'm hurt",
        "putting myself down before anyone else can"
    )

    /** Ready to square once there's a reaction to catch and a truer move to make instead. Trigger is optional. */
    fun canSquare(reaction: String, truerResponse: String): Boolean =
        reaction.isNotBlank() && truerResponse.isNotBlank()

    /**
     * The competing-response if-then, in the person's own words. Trigger is optional (falls back to a
     * general "when it rises"). No shame, no "should" — a plan to interrupt, not a rule to obey.
     */
    fun compose(reaction: String, trigger: String, truerResponse: String): String {
        val r = reaction.trim().ifBlank { "react the old way" }
        val tr = truerResponse.trim().ifBlank { "make the truer move" }
        val whenClause = trigger.trim().let { if (it.isNotBlank()) "When $it" else "When it rises" }
        return "$whenClause, I'll catch the urge to $r — set it down, and $tr instead."
    }

    /** Every user-facing string, for the safety sweep (examples + a sample composed line). */
    fun allText(): List<String> =
        ROUGH_CORNERS + listOf(compose("react the old way", "the cue hits", "take one breath and choose"))
}
