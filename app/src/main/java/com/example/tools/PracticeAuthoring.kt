package com.example.tools

/**
 * Practice authoring (SPEC_PHASE1_STONE P0.2 / ticket T1.4).
 *
 * Lets a person write their OWN practice in their own words — the autonomy that makes a practice
 * feel like theirs and actually fire. Two evidence-based constraints (Gollwitzer implementation
 * intentions, d≈0.65):
 *  1. Anchor it to an existing routine — "AFTER [anchor], I will [action]" — so it has a real cue.
 *  2. Frame it as APPROACH, not avoidance — name what you WILL do, not what you'll stop. Avoidance
 *     goals ("stop scrolling") are weaker and more self-punishing than approach goals ("read a page").
 *
 * So the authoring redirects avoidance phrasing to an approach reframe before it can be saved. Pure
 * logic — no Android; the dialog enforces it and stores the composed plan.
 */
object PracticeAuthoring {

    /** Whether the action is framed as avoidance (something to stop/not-do) rather than approach. */
    fun isAvoidanceFramed(action: String): Boolean {
        val trimmed = action.trim().lowercase()
        if (trimmed.isEmpty()) return false
        val firstWord = trimmed.split(Regex("\\s+")).first().trim('.', ',', '\'', '"')
        val avoidanceStarters = setOf(
            "stop", "don't", "dont", "quit", "avoid", "resist", "never", "not", "no",
            "reduce", "limit", "less", "cut"
        )
        if (firstWord in avoidanceStarters) return true
        val avoidancePhrases = listOf("give up", "cut out", "cut down", "cut back", "no more", "stop myself")
        return avoidancePhrases.any { trimmed.contains(it) }
    }

    /** The nudge shown when someone writes an avoidance action — points them to an approach reframe. */
    const val REFRAME_HINT: String =
        "Name what you WILL do, not what you'll stop. Instead of \"stop scrolling,\" try \"read one " +
            "page\" or \"step outside for a minute.\" Approach goals hold better than avoidance ones."

    /** Compose the stored plan: "After [anchor], I will [action]." Both trimmed; blanks → empty. */
    fun composePlan(anchor: String, action: String): String {
        val a = anchor.trim().trimEnd('.', ',')
        val act = action.trim().trimEnd('.')
        if (a.isEmpty() || act.isEmpty()) return ""
        return "After $a, I will $act."
    }

    /** A practice is ready to save only with an anchor, an approach action, and no avoidance framing. */
    fun canSave(anchor: String, action: String): Boolean =
        anchor.trim().isNotEmpty() && action.trim().isNotEmpty() && !isAvoidanceFramed(action)
}
