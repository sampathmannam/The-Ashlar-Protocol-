package com.ashlarprotocol.tools

/**
 * Power-Ups — quick, always-available mood-lifters (SPEC_PHASE1_STONE P0.6 / ticket T2.4).
 *
 * A spike of distress shouldn't have to wait behind a task. These are short, self-contained things a
 * person can pull ANY time, in ≤2 taps, decoupled from the day's Working, the streak, and payment —
 * help is never gated (the app's north-star). They are ordinary steadying, distinct from the crisis
 * pathway (the always-open NEED HELP header), which takes precedence on any risk signal.
 *
 * Each is grounded, not gimmicky: paced breathing ~6/min with a longer exhale (RESEARCH_BASIS §8),
 * 5-4-3-2-1 sensory grounding, a brief body-release (progressive relaxation), a self-compassion line
 * (common humanity; Neff), and a gratitude-outward turn — expressing thanks to a specific person
 * raises wellbeing (Seligman, Steen, Park & Peterson, 2005; RESEARCH_BASIS §11, the turn toward
 * others). Pure content — no Android, no I/O; the sheet just renders it.
 */
object PowerUps {

    /** One quick lifter: a title, a one-line invitation, and the short steps shown when opened. */
    data class PowerUp(
        val id: String,
        val title: String,
        val invite: String,
        val steps: List<String>
    )

    val POWER_UPS: List<PowerUp> = listOf(
        PowerUp(
            id = "breath",
            title = "A slow breath",
            invite = "A minute is enough. Let the out-breath lead.",
            steps = listOf(
                "Breathe in gently for a count of four.",
                "Out for six — longer than the in.",
                "Again: four in… and six, slow, out.",
                "A few rounds is all. The body follows the breath."
            )
        ),
        PowerUp(
            id = "ground",
            title = "Feel the ground",
            invite = "Come back into the room you're in.",
            steps = listOf(
                "Name five things you can see.",
                "Four you can hear.",
                "Three you can feel touching you.",
                "Two you can smell, or two slow breaths.",
                "One thing you're glad is here.",
                "That's the whole of it. You're here now."
            )
        ),
        PowerUp(
            id = "unclench",
            title = "Unclench",
            invite = "Put down what your body is quietly holding.",
            steps = listOf(
                "Let your shoulders drop down from your ears.",
                "Unclench your jaw — part your teeth a little.",
                "Open your hands, spread the fingers, let them fall.",
                "Nothing to fix. Just a little less held."
            )
        ),
        PowerUp(
            id = "kind",
            title = "A kinder word",
            invite = "Say to yourself what you'd say to a friend.",
            steps = listOf(
                "Whatever this is, you're not the only one who has felt it.",
                "You'd not sneer at a friend carrying this. Don't sneer at yourself.",
                "You showed up. That already counts for something.",
                "Be on your own side for the next few minutes."
            )
        ),
        PowerUp(
            id = "thank",
            title = "Thank someone",
            invite = "Turning toward a real person lifts the one who gives thanks, too.",
            steps = listOf(
                "Bring to mind one person who made something a little better for you.",
                "Picture the specific thing they did — small is fine.",
                "Notice that it actually helped. Let that land for a slow breath.",
                "Tell them. One honest line: \"I was just thinking of when you… thank you.\"",
                "Sending it is the whole practice — through the West Gate, or your own way."
            )
        ),
        PowerUp(
            id = "urge",
            title = "Ride the urge",
            invite = "A craving is a wave — it crests and passes if you let it.",
            steps = listOf(
                "Name it: \"a wave of wanting is here.\" You don't have to act on it — or fight it.",
                "Find where it sits in your body. Watch it like weather: curious, not at war.",
                "Breathe with it. Waves rise, peak, and fall — this one will too, usually within minutes.",
                "Notice it easing. You rode it out; and each time you do, the next wave comes a little smaller."
            )
        )
    )

    fun byId(id: String): PowerUp? = POWER_UPS.firstOrNull { it.id == id }
}
