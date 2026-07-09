package com.example.tools

/**
 * Character strengths — the app's intrinsic progression currency (identity + mastery, not points).
 *
 * Faithful to the VIA Classification (Peterson & Seligman, 2004): 24 character strengths grouped
 * under 6 core virtues. The flagship exercise — using one of your SIGNATURE strengths "in a new way"
 * each day — raised happiness and lowered depression for up to six months (Seligman, Steen, Park &
 * Peterson, 2005). Here each strength can dress a facet of the stone. Pure, on-device. See
 * docs/GAMIFICATION_PLAN.md §3 and docs/RESEARCH_BASIS.md §9.
 */

enum class Virtue(val display: String) {
    WISDOM("Wisdom"),
    COURAGE("Courage"),
    HUMANITY("Humanity"),
    JUSTICE("Justice"),
    TEMPERANCE("Temperance"),
    TRANSCENDENCE("Transcendence")
}

enum class Strength(val display: String, val virtue: Virtue) {
    // Wisdom & Knowledge
    CREATIVITY("Creativity", Virtue.WISDOM),
    CURIOSITY("Curiosity", Virtue.WISDOM),
    JUDGMENT("Judgment", Virtue.WISDOM),
    LOVE_OF_LEARNING("Love of Learning", Virtue.WISDOM),
    PERSPECTIVE("Perspective", Virtue.WISDOM),
    // Courage
    BRAVERY("Bravery", Virtue.COURAGE),
    PERSEVERANCE("Perseverance", Virtue.COURAGE),
    HONESTY("Honesty", Virtue.COURAGE),
    ZEST("Zest", Virtue.COURAGE),
    // Humanity
    LOVE("Love", Virtue.HUMANITY),
    KINDNESS("Kindness", Virtue.HUMANITY),
    SOCIAL_INTELLIGENCE("Social Intelligence", Virtue.HUMANITY),
    // Justice
    TEAMWORK("Teamwork", Virtue.JUSTICE),
    FAIRNESS("Fairness", Virtue.JUSTICE),
    LEADERSHIP("Leadership", Virtue.JUSTICE),
    // Temperance
    FORGIVENESS("Forgiveness", Virtue.TEMPERANCE),
    HUMILITY("Humility", Virtue.TEMPERANCE),
    PRUDENCE("Prudence", Virtue.TEMPERANCE),
    SELF_REGULATION("Self-Regulation", Virtue.TEMPERANCE),
    // Transcendence
    APPRECIATION_OF_BEAUTY("Appreciation of Beauty", Virtue.TRANSCENDENCE),
    GRATITUDE("Gratitude", Virtue.TRANSCENDENCE),
    HOPE("Hope", Virtue.TRANSCENDENCE),
    HUMOR("Humor", Virtue.TRANSCENDENCE),
    SPIRITUALITY("Spirituality", Virtue.TRANSCENDENCE)
}

object Strengths {

    /** All 24 VIA strengths, in canonical (virtue-grouped) order. */
    fun all(): List<Strength> = Strength.values().toList()

    /** The strengths belonging to one virtue. */
    fun forVirtue(virtue: Virtue): List<Strength> = all().filter { it.virtue == virtue }

    /**
     * The user's top [n] "signature" strengths from their own ranking (highest first). Clamped: a
     * non-positive [n] or empty ranking yields an empty list; asking for more than exist returns all.
     */
    fun signature(ranked: List<Strength>, n: Int): List<Strength> =
        if (n <= 0) emptyList() else ranked.take(n)

    /**
     * The Seligman "use it in a new way" prompt for a signature [strength] — the evidence-based core
     * loop. Names the strength and invites a fresh, deliberate use today.
     */
    fun newWayPrompt(strength: Strength): String =
        "Use your ${strength.display} in a new way today — somewhere you normally wouldn't think to."

    /**
     * Today's strength to use "in a new way" — a deterministic rotation through the user's
     * [signature] set keyed on [epochDay], so it's stable within a day and cycles fairly through
     * them. Returns null when no signature strengths have been chosen yet.
     */
    fun strengthOfTheDay(signature: List<Strength>, epochDay: Long): Strength? =
        if (signature.isEmpty()) null
        else signature[Math.floorMod(epochDay, signature.size.toLong()).toInt()]
}
