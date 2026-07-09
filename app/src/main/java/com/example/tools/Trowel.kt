package com.example.tools

/**
 * The Trowel — the working tool that "spreads the cement of brotherly love and affection."
 * Turned inward, that cement is self-compassion: treating yourself as you would treat a brother
 * who sat where you sit. See RESEARCH_BASIS.md §self-compassion.
 *
 * The three movements mirror Kristin Neff's self-compassion break — mindfulness (name the pain
 * without shrinking or inflating it), common humanity (you are not alone in it), and self-kindness
 * (offer yourself the words you'd offer a brother). Grounded, brief, and never forced.
 *
 * Safety note — "backdraft" (Germer & Neff): opening a door of kindness can let old pain rush in,
 * the way air feeds a smouldering fire. A brief practice can transiently raise distress, especially
 * for the highly self-critical. So the rite is capped, never demands emotional depth, hands the
 * person's own words back rather than prescribing feeling, and always offers a way to stop and
 * ground. The crisis path (see safety/CrisisDetector) stays open independent of any of this.
 *
 * Pure logic only — no Android, no UI, no I/O. The composable renders what these functions return.
 */
object Trowel {

    /** One movement of the self-compassion break: a short label and the prompt that opens it. */
    data class Movement(val label: String, val prompt: String)

    /**
     * The three movements, in order. Mindfulness first (you can't be kind to a pain you won't name),
     * common humanity second (the antidote to isolation), self-kindness last (the cement, spread inward).
     */
    val MOVEMENTS: List<Movement> = listOf(
        Movement(
            label = "Name it",
            prompt = "What are you carrying right now? Name it plainly — no smaller than it is, no larger."
        ),
        Movement(
            label = "You are not alone in it",
            prompt = "This is the part where you remember: others have sat exactly here."
        ),
        Movement(
            label = "Spread the cement",
            prompt = "If a brother came to you carrying this, you would not sneer at him. Say to yourself what you would say to him."
        )
    )

    /**
     * Common-humanity reflection — the antidote to "I am the only one." Echoes the person's own
     * words back rather than paraphrasing them (no distortion), then sets the pain inside shared
     * human experience. Blank input still returns a grounded, honest line.
     */
    fun commonHumanity(struggle: String): String {
        val s = struggle.trim()
        return if (s.isEmpty()) {
            "Whatever you are carrying, you are not the first to carry it, and you will not be the last. " +
                "This is part of being human — not a fault in you."
        } else {
            "You wrote: “$s”. You are not the only one who has felt this. Others have sat exactly " +
                "where you sit. It is part of being human — not a fault in you."
        }
    }

    /**
     * The turn inward — the compassionate-writing reframe (treat yourself as you'd treat a brother).
     * This is the best-supported, least backdraft-prone form of the practice: it asks for words, not
     * for a forced feeling. Echoes the struggle so the prompt is concrete.
     */
    fun asABrother(struggle: String): String {
        val s = struggle.trim()
        return if (s.isEmpty()) {
            "If a brother came to you carrying what you carry, you would not sneer at him. " +
                "What would you say to him? Say it to yourself now."
        } else {
            "If a brother came to you carrying “$s”, you would not sneer at him. " +
                "What would you say to him? Say it to yourself now."
        }
    }

    /**
     * Hands the person's own kind words back to them to keep. Never forces output: if they wrote
     * nothing, the kindness still counts and the door stays open. If they did, the words are theirs.
     */
    fun closing(kindWords: String): String {
        val k = kindWords.trim()
        return if (k.isEmpty()) {
            "Even unspoken, the kindness counts. Come back to it when you're ready — the trowel keeps."
        } else {
            "You said to yourself: “$k”. Those are your words. Keep them. " +
                "The trowel spreads the same cement inward as it does out."
        }
    }

    /**
     * The gentle exit — grounding offered throughout the rite, and the honest word about backdraft.
     * The first line is an unconditional permission to stop; the last names what a surge of feeling
     * is, so it doesn't read as failure. This is the off-ramp the research demands.
     */
    val grounding: List<String> = listOf(
        "You can stop at any point. Nothing here has to be finished.",
        "Feel your feet on the floor. Feel the weight of you in the chair.",
        "Three slow breaths — let the out-breath run longer than the in.",
        "If this opened something heavy, that isn't failure. It's old pain meeting air. " +
            "You can close the door, rest, and reach for help if you need it."
    )
}
