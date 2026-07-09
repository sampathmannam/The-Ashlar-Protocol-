package com.ashlarprotocol.tools

/**
 * The WHO-5 Well-Being Index (SPEC_PHASE1_STONE P0.1 measurement / ticket T3.1).
 *
 * WHO-5 is a short, validated, free wellbeing measure — five positively-worded items scored 0–5 over
 * the last two weeks, summed and ×4 into a 0–100 index (higher = better). It is the plan's PRIMARY,
 * wellbeing-first success metric: the point is to know whether the app actually helps, not whether
 * people open it. Offered gently (baseline, then ~biweekly), always skippable, stored on-device only.
 *
 * It is a check-in, NOT a diagnosis. The reflection below acknowledges the number warmly and, on a
 * low stretch, points to human help — it never labels the person. Pure logic; the sheet renders it.
 */
object WhoFive {

    const val PREAMBLE = "Over the last two weeks…"

    val ITEMS: List<String> = listOf(
        "I have felt cheerful and in good spirits.",
        "I have felt calm and relaxed.",
        "I have felt active and vigorous.",
        "I woke up feeling fresh and rested.",
        "My daily life has been filled with things that interest me."
    )

    data class Option(val label: String, val value: Int)

    /** The standard WHO-5 frequency scale, 5 (all of the time) down to 0 (at no time). */
    val OPTIONS: List<Option> = listOf(
        Option("All of the time", 5),
        Option("Most of the time", 4),
        Option("More than half the time", 3),
        Option("Less than half the time", 2),
        Option("Some of the time", 1),
        Option("At no time", 0)
    )

    /** Sum of the five 0–5 answers, ×4 → a 0–100 index (higher = better). Out-of-range values clamp. */
    fun score(answers: List<Int>): Int {
        val sum = answers.take(5).sumOf { it.coerceIn(0, 5) }
        return sum * 4
    }

    /**
     * Whether to OFFER the check (it's always skippable): baseline if never taken, then gently about
     * every two weeks. Timezone-agnostic elapsed-time, deterministic to test.
     */
    fun isDue(lastTakenMs: Long?, nowMs: Long): Boolean {
        if (lastTakenMs == null) return true
        val twoWeeksMs = 14L * 24 * 60 * 60 * 1000
        return nowMs - lastTakenMs >= twoWeeksMs
    }

    /**
     * A warm, non-clinical reflection of the score — never a diagnosis, never a label. On a low
     * stretch it points to human help (the always-open crisis path), without alarm. Bands are
     * informed by WHO-5 norms (≤50 reduced wellbeing, ≤28 worth a closer look) but worded as care.
     */
    fun reflection(score: Int): String = when {
        score >= 68 -> "A good couple of weeks. Notice what's been feeding it — and keep a little of it."
        score >= 51 -> "A fair balance lately. Some of it is landing."
        score >= 29 -> "It's been a heavier stretch. Worth being gentle with yourself, and steady."
        else -> "This has been a hard couple of weeks. You don't have to carry it alone — reaching out to a person helps, and help is a tap away."
    }
}
