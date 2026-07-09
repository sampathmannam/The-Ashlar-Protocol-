package com.ashlarprotocol.tools

/**
 * The Square — the pure logic of the values-clarification rite. The operative mason's square tests a
 * corner for true; the speculative square is "squaring your actions" to what is right. Here it's an
 * ACT-style values exercise: choose the few things that matter most, and they compose into the
 * intention the whole app then serves (surfaced on the Board; feeds the re-authoring engine).
 *
 * Grounded in ACT values work (full ACT is well-supported; see docs/RESEARCH_BASIS.md — claimed as an
 * ACT ingredient, not a standalone treatment). Pure + deterministic, on-device.
 */
object Square {

    /** A plain, non-dogmatic set of core values. Craft-flavoured but universal. */
    val VALUES: List<String> = listOf(
        "Honesty", "Courage", "Discipline", "Connection", "Service", "Craft", "Steadiness",
        "Compassion", "Growth", "Justice", "Family", "Freedom", "Health", "Curiosity"
    )

    /**
     * Compose an intention from the chosen values, e.g. "To live by courage, honesty, and craft."
     * Uses a serial comma; returns "" if nothing meaningful was chosen.
     */
    fun squareIntention(values: List<String>): String {
        val v = values.map { it.trim() }.filter { it.isNotEmpty() }.map { it.lowercase() }
        return when (v.size) {
            0 -> ""
            1 -> "To live by ${v[0]}."
            2 -> "To live by ${v[0]} and ${v[1]}."
            else -> "To live by ${v.dropLast(1).joinToString(", ")}, and ${v.last()}."
        }
    }
}
