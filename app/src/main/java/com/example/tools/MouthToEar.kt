package com.example.tools

/**
 * Mouth to Ear — commit a chosen principle to memory the old way: by saying it back with fewer
 * and fewer cues. Instead of the prototype's static police procedure, the user memorises a
 * principle they actually choose to live by.
 *
 * Deterministic by design (no RNG) so practice is reproducible and unit-testable, and fully
 * on-device. Progressive cue reduction is a plain application of retrieval practice / spaced
 * recall. See docs/RESEARCH_BASIS.md. Pure logic — no Android, no network.
 */

/** A maxim the user is memorising. Free text; a few defaults are offered in the UI. */
data class Principle(val id: String, val text: String)

/** A short, non-dogmatic starter set drawn from the craft's own language. The user can edit or replace. */
val DEFAULT_PRINCIPLES: List<Principle> = listOf(
    Principle("plumb", "Stand by the plumb: upright in what I do, even unseen."),
    Principle("level", "Meet others on the level; no one above, no one below."),
    Principle("gauge", "Divide the day: something that matters, honest work, true rest."),
    Principle("ashlar", "I am rough stone, not broken stone. The work is to shape it.")
)

object MouthToEar {

    /** At or above this recall fraction, the principle counts as "held" (one memory-work session). */
    const val RECALL_HELD_THRESHOLD: Float = 0.7f

    /** Whether a recall score is strong enough to count as a held recall. */
    fun isHeld(score: Float): Boolean = score >= RECALL_HELD_THRESHOLD

    /**
     * Hide a fraction of the words to force recall. [level] 0f reveals everything; 1f hides
     * everything; in between, an evenly-spaced set of words is replaced by underscores of the
     * same length. Whitespace is normalised to single spaces.
     */
    fun mask(text: String, level: Float): String {
        if (level <= 0f) return text
        val tokens = text.trim().split(Regex("\\s+")).filter { it.isNotEmpty() }
        if (tokens.isEmpty()) return ""
        val n = tokens.size
        val hidden = hiddenCount(n, level)
        val hiddenIdx = evenlySpacedIndices(n, hidden).toSet()
        return tokens.mapIndexed { i, t ->
            if (i in hiddenIdx) "_".repeat(t.length) else t
        }.joinToString(" ")
    }

    /**
     * Fraction of words recalled correctly, compared position-by-position and ignoring case and
     * punctuation. 0f for an empty attempt; 1f for a perfect recall.
     */
    fun scoreRecall(original: String, attempt: String): Float {
        val orig = normalizeWords(original)
        if (orig.isEmpty()) return 0f
        val said = normalizeWords(attempt)
        var matches = 0
        for (i in orig.indices) {
            if (i < said.size && said[i] == orig[i]) matches++
        }
        return matches.toFloat() / orig.size
    }

    // --- internals ---

    private fun hiddenCount(n: Int, level: Float): Int = when {
        level <= 0f -> 0
        level >= 1f -> n
        else -> Math.round(level * n)
    }

    /** [count] indices spread evenly across 0 until n. */
    private fun evenlySpacedIndices(n: Int, count: Int): List<Int> = when {
        count <= 0 -> emptyList()
        count >= n -> (0 until n).toList()
        else -> (0 until count).map { (it * n) / count }
    }

    private fun normalizeWords(text: String): List<String> =
        text.lowercase()
            .replace(Regex("[^a-z0-9']"), " ")
            .split(Regex("\\s+"))
            .filter { it.isNotEmpty() }
}
