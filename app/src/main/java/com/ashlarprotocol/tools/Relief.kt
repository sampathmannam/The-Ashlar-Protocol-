package com.ashlarprotocol.tools

/**
 * The Well — a bundled rotation of short words of relief, surfaced right after a person releases
 * the weight in the Chamber. It is the affordable form of "the Lodge" (Phase 3): the felt sense of
 * being met and not alone, delivered as on-device content — no server, no accounts, no network.
 *
 * Honesty note (see docs/RESEARCH_BASIS.md and the privacy north-star): these are plainly the app's
 * own words, never dressed up as a message from a real person. They carry mutual-aid warmth in the
 * craft's voice without pretending a human sent them. Pure + deterministic.
 */
object Relief {

    val WORDS: List<String> = listOf(
        "You set it down. That took something.",
        "Whatever it was, you don't have to carry it every hour.",
        "You came here instead of holding it alone. That is the work.",
        "The stone is heavy. Setting it down, even for now, is strength — not surrender.",
        "You're still here, still shaping. That counts.",
        "No one builds true while carrying everything. Breathe.",
        "That was honest. Honesty with yourself is a tool you already hold.",
        "The weight may return, and you'll set it down again. That's the practice, not failure."
    )

    /** Safe indexed access with wraparound; tolerates any Int. */
    fun reliefAt(index: Int): String {
        val n = WORDS.size
        val i = ((index % n) + n) % n
        return WORDS[i]
    }
}
