package com.ashlarprotocol.tools

/**
 * The Rough Edge (Phase 4b, F5) — working one bad habit or compulsion, the way the evidence says
 * actually works, not the way most quit-apps do.
 *
 * The single most important decision here is how a *slip* is handled. A lapse does not cause relapse —
 * *catastrophizing* the lapse does (the abstinence-violation effect; Marlatt & Gordon). A streak-shame
 * reset delivers the exact guilt hit that turns one slip into a spiral, so there is none here: the
 * stone never breaks, a lapse is logged as *data* and met with self-compassion, and the person is
 * pointed forward. The rest of the spine is what the research supports: cue/environment control over
 * willpower (the Cornerstone), a replacement approach-action (a Practice), and urge-surfing rather
 * than suppression (which backfires — Wegner; Bowen's MBRP). NOT craving-surveillance (Milyavskaya).
 *
 * Hard line: substance addiction is clinical. This is a practice, not treatment, and it says so —
 * with an always-reachable, unshaming handoff to real human help (§9 / the West Gate). Pure copy.
 * See docs/RESEARCH_INTEGRATION.md §1.6 / F5.
 */
object RoughEdge {

    /** The anti-AVE response to a slip: a lapse is data, never a verdict. No "failed/relapse/blew it". */
    fun lapseResponse(): String =
        "A slip is data, not a verdict — it doesn't erase the work you've done. Notice the cue that " +
            "caught you, be as kind to yourself as you'd be to a friend, and take the next honest step. " +
            "The stone holds."

    /** The clinical-handoff floor — prominent, unshaming, always paired with a way to reach real help. */
    const val SAFETY_NOTE: String =
        "This is a practice, not treatment. If something has a real grip on you, help from a person is " +
            "stronger than any app — and reaching for it is strength, not weakness. You can find a line any time."

    /** A short, honest framing of what the track is for — approach, never shame. */
    const val INTRO: String =
        "Pick one rough edge to work. You won't white-knuckle it — you'll change the cue around it, " +
            "put something better in its place, and ride out the wave when it comes. A slip won't undo you."
}
