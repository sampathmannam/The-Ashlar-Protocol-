package com.example.tools

/**
 * The mortality-symbolism safety gate, enforced in code (SPEC_PHASE1_STONE P0.8 / ticket T3.3;
 * RESEARCH_BASIS §10 Safety Gate — BINDING, not a preference).
 *
 * Phase 1 carries ZERO death/mortality imagery: no skull, grave, coffin, hourglass-as-death, scythe,
 * countdown, "life in weeks", memento mori, or "finish/complete your life" framing. Mortality-salience
 * failed key replications and is the highest-risk element in the Masonic source material, so the
 * Chamber leans to meaning/purpose, never reflection on death.
 *
 * This turns that rule into a test: [mortalityViolations] flags forbidden terms in any copy string, and
 * [audit] runs it over a whole corpus. The unit test feeds it every Phase-1 copy surface, so death
 * imagery can never slip into the app's words without turning the suite red.
 */
object SafetyAudit {

    /** Forbidden death/mortality symbolism. Chosen to be specific enough to avoid flagging warm copy. */
    val FORBIDDEN_MORTALITY: List<String> = listOf(
        "skull", "graveyard", "coffin", "tomb", "scythe", "hourglass", "memento mori", "corpse",
        "deathbed", "death", "dying", "your last will", "last will and testament", "life in weeks",
        "when you die", "before you die", "if you died", "final breath", "rest in peace",
        "countdown to", "finish your life", "complete your life", "end of your life"
    )

    /** The forbidden terms found in [text] (case-insensitive). Empty list = clean. */
    fun mortalityViolations(text: String): List<String> {
        val t = text.lowercase()
        return FORBIDDEN_MORTALITY.filter { t.contains(it) }
    }

    /**
     * Audit a corpus keyed by source name. Returns only the sources that have violations, mapped to
     * the distinct forbidden terms found — so a red test names exactly what slipped in, and where.
     */
    fun audit(corpus: Map<String, List<String>>): Map<String, List<String>> =
        corpus
            .mapValues { (_, lines) -> lines.flatMap { mortalityViolations(it) }.distinct() }
            .filterValues { it.isNotEmpty() }
}
