package com.example.tools

/**
 * The Plumb — pure logic for a guided CBT thought-record ("is this thought standing true,
 * or is it leaning?").
 *
 * This is deliberately UI-free and network-free so it can be unit-tested and so the practice
 * works fully on-device (privacy-first; see docs/VISION.md §8). The Masonic plumb tests a wall
 * for true vertical; here it tests a thought against the evidence.
 *
 * Grounding: cognitive restructuring / thought records are a core, well-established component of
 * Cognitive Behavioural Therapy (A. T. Beck; J. Beck, *Cognitive Behavior Therapy*, 2011). The
 * "tilts" below are the standard list of cognitive distortions (Burns, *Feeling Good*, 1980).
 * See docs/RESEARCH_BASIS.md. The app never diagnoses — it offers a structured way to look again.
 */

/** A common cognitive distortion — the way a thought can "lean" off true. */
data class Tilt(
    val id: String,
    val name: String,
    /** Plain, non-clinical description shown under the name. */
    val description: String
)

/** The standard cognitive distortions, phrased plainly and without jargon. */
val TILTS: List<Tilt> = listOf(
    Tilt("catastrophising", "Worst-case", "Jumping straight to the worst thing that could happen."),
    Tilt("all_or_nothing", "All-or-nothing", "Seeing it as total success or total failure, nothing between."),
    Tilt("mind_reading", "Mind-reading", "Assuming you know what others are thinking about you."),
    Tilt("fortune_telling", "Fortune-telling", "Treating a fear about the future as if it already happened."),
    Tilt("overgeneralising", "Always / never", "One event becomes a forever pattern ('always', 'never')."),
    Tilt("labelling", "Labelling", "Turning one mistake into a name for your whole self."),
    Tilt("should", "Shoulds", "Beating yourself with rigid rules about how you 'should' be."),
    Tilt("emotional_reasoning", "Feeling = fact", "Taking a feeling as proof ('I feel it, so it's true')."),
    Tilt("filtering", "Filtering", "Keeping only the negatives and screening out the rest."),
    Tilt("personalising", "Self-blame", "Taking responsibility for things outside your control.")
)

fun tiltById(id: String): Tilt? = TILTS.firstOrNull { it.id == id }

/** Everything the person recorded while checking one thought against the plumb line. */
data class PlumbEntry(
    val situation: String,
    val thought: String,
    val tiltIds: List<String>,
    val evidence: String
)

/**
 * Compose a calm, plain-language "squared to reality" reflection from the person's OWN words.
 *
 * This is intentionally rule-based, not generative: it mirrors back what they wrote, names the
 * tilt(s) they identified, holds up the counter-evidence they found, and ends with an invitation
 * to one upright action. No claims, no diagnosis, no invented facts — only their material,
 * reflected back squared. Returns a multi-line string.
 */
fun composeSquaredReflection(entry: PlumbEntry): String {
    val lines = mutableListOf<String>()

    val thought = entry.thought.trim()
    if (thought.isNotEmpty()) {
        lines += "The thought pulling you off-plumb: “$thought”"
    }

    val namedTilts = entry.tiltIds.mapNotNull { tiltById(it) }
    when (namedTilts.size) {
        0 -> { /* no tilt named — that's fine; a thought can be true and still heavy */ }
        1 -> lines += "The lean you spotted: ${namedTilts[0].name.lowercase()} — ${namedTilts[0].description.lowercase().trimEnd('.')}."
        else -> {
            val names = namedTilts.joinToString(", ") { it.name.lowercase() }
            lines += "The leans you spotted: $names. Naming the tilt is half of straightening it."
        }
    }

    val evidence = entry.evidence.trim()
    if (evidence.isNotEmpty()) {
        lines += "Held against the evidence: “$evidence”"
    }

    // Closing invitation — depends on how much straightening work they did.
    val closing = when {
        evidence.isNotEmpty() && namedTilts.isNotEmpty() ->
            "Set against what's actually true, the thought is leaning further than the facts. That's the tilt, not the truth. What is one upright action you can take from here?"
        evidence.isNotEmpty() ->
            "You've put real weight on the other side of the scale. What is one upright action you can take from here?"
        namedTilts.isNotEmpty() ->
            "You've named the tilt. When you're ready, look for one piece of evidence that the thought leaves out."
        else ->
            "Some thoughts are true and still heavy. If this one is true, the question becomes: what is one upright thing to do about it?"
    }
    lines += closing

    return lines.joinToString("\n\n")
}
