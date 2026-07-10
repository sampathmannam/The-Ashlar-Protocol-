package com.ashlarprotocol.tools

/**
 * Automaticity — the honest progress signal (Phase 4b, F4).
 *
 * A habit is not "how many days in a row" — it is a **context-cued automatic response**: doing the
 * thing without having to decide to (Gardner 2015; Wood & Rünger 2016). So the number that actually
 * matters is whether the practice is *becoming automatic*, not the streak length. This is a gentle,
 * occasional self-report (an SRHI-lite) — a moment of *noticing*, never a grade, and NOT a daily
 * craving/temptation surveillance (more self-monitoring predicts worse outcomes — Milyavskaya &
 * Inzlicht 2017). Pure, on-device. See docs/RESEARCH_INTEGRATION.md §1.2 / F4.
 */
object Automaticity {

    const val PROMPT: String = "When you did the work today, how did it feel?"

    data class Level(val label: String, val value: Int)

    /** A short, non-judging scale from effortful to automatic. Values ascend 0 → 2. */
    val LEVELS: List<Level> = listOf(
        Level("Still takes real effort", 0),
        Level("Getting easier", 1),
        Level("Almost automatic now", 2)
    )

    /** A warm, non-grading reflection for a reading — names what automaticity means, not a verdict. */
    fun reflection(value: Int): String = when (value.coerceIn(0, 2)) {
        0 -> "That's how it starts — every repetition wears the groove a little deeper. Keep the cue steady."
        1 -> "The groove is forming. Less deciding, more doing — that's the work becoming yours."
        else -> "It's becoming automatic — which is what a habit really is, not a streak. The stone is yours now."
    }

    /**
     * A gentle cadence: ask about once a week at most, and never twice in a day. This is *noticing*,
     * not surveillance — daily self-monitoring backfires (Milyavskaya). [lastAskedDay] < 0 = never asked.
     */
    fun isDue(lastAskedDay: Long, today: Long): Boolean =
        lastAskedDay < 0L || (today - lastAskedDay) >= 7L
}
