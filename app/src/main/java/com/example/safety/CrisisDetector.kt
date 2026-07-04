package com.example.safety

/**
 * On-device, fail-safe crisis detector.
 *
 * Design principles (see docs/VISION.md §8 and docs/MASTER_PLAN.md Phase 1A):
 *  - MODEL-INDEPENDENT: pure local string matching. No network, no API, no model.
 *    It works with the phone in airplane mode and cannot be defeated by a failed cloud call.
 *  - HIGH-RECALL / FAIL-TOWARD-HELP: when in doubt, we would rather surface help than miss
 *    someone in crisis. A false positive costs a person a few taps to dismiss; a false
 *    negative can cost a life.
 *  - ADDITIVE: this never blocks, edits, or hides what the user wrote. It only offers help.
 *
 * This is Phase 1A: a curated lexical pass. A small on-device classifier can be layered on
 * top later (see MASTER_PLAN Phase 1A) to raise recall on euphemisms — but this rule pass
 * must always remain as the fail-safe floor.
 */
object CrisisDetector {

    /**
     * Phrases indicating suicidal ideation, intent, or self-harm — direct and euphemistic.
     * Matched as substrings against normalized (lower-cased, punctuation-stripped) text.
     * Kept as multi-word phrases (not bare words) to avoid the worst idiom false-positives
     * ("this is killing me", "dead tired") while still casting a wide, protective net.
     */
    private val CRISIS_PHRASES: List<String> = listOf(
        // Direct suicidal intent
        "kill myself", "killing myself", "kill my self",
        "want to die", "wanna die", "want to be dead", "i want to die",
        "end my life", "end my own life", "take my own life", "take my life",
        "end it all", "want it to end", "make it stop forever",
        "commit suicide", "suicidal", "suicide",
        "hang myself", "overdose", "od myself",
        "slit my", "cut myself", "cutting myself", "burn myself",
        "hurt myself", "harm myself", "harming myself", "self harm", "self-harm",

        // Hopelessness + non-existence (euphemistic — these are the ones keyword-only nets miss)
        "don't want to be here", "dont want to be here",
        "don't want to be alive", "dont want to be alive",
        "don't want to exist", "dont want to exist",
        "want to disappear", "wish i could disappear",
        "no reason to live", "no point in living", "nothing to live for",
        "not worth living", "life isn't worth", "life isnt worth",
        "better off dead", "better off without me",
        "world would be better without me", "world would be lighter without me",
        "everyone would be better without me", "they'd be better without me",
        "can't go on", "cant go on", "can't do this anymore", "cant do this anymore",
        "tired of living", "tired of being alive", "done with life", "give up on life",
        "no way out", "there's no way out", "theres no way out",
        "ready to die", "want to sleep forever", "never wake up",
        "goodbye forever", "this is my goodbye"
    )

    /**
     * Returns true if the text contains any crisis indicator.
     * Any error defaults to `true` (fail-toward-help): if we cannot be sure the text is safe,
     * we offer help rather than risk missing a person in crisis.
     */
    fun detect(text: String?): Boolean {
        if (text.isNullOrBlank()) return false
        return try {
            val normalized = normalize(text)
            CRISIS_PHRASES.any { phrase -> normalized.contains(phrase) }
        } catch (e: Exception) {
            // Fail-safe: never let an exception silently drop a possible crisis signal.
            true
        }
    }

    private fun normalize(text: String): String {
        return text
            .lowercase()
            // Replace punctuation with spaces so "don't"/"dont" and "life,worth" still match.
            .replace(Regex("[^a-z0-9']"), " ")
            .replace(Regex("\\s+"), " ")
            .trim()
    }
}
