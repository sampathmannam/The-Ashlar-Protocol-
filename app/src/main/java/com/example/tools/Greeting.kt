package com.example.tools

/**
 * The remembered greeting (SPEC_PHASE1_STONE P0.5 / ticket T1.9) — re-scoped honestly.
 *
 * The spec imagines a companion that "remembers prior context" and greets you warmly. That was written
 * assuming an on-device LLM + compounding-memory layer — which THIS app does not have (no LLM, no
 * network, by design). So the honest form of the "remembered companion" is not a generated persona: it
 * is a consistent, warm voice that genuinely remembers what you told it — your intention and how long
 * you've tended — and reflects it back as a greeting, composed (never generated), fully on-device,
 * nothing uploaded. It meets P0.5's real AC (references remembered context; phrased as welcome, not
 * obligation) without pretending to be something it isn't.
 *
 * Pure logic; the Board renders one line of it on open.
 */
object Greeting {

    data class Context(
        val hourOfDay: Int,
        val daysTended: Int,
        /** The intention from initiation / the Square. May be blank. */
        val intention: String
    )

    private fun timeGreeting(hour: Int): String = when {
        hour < 5 -> "You're up late"
        hour < 12 -> "Good morning"
        hour < 17 -> "Good afternoon"
        else -> "Good evening"
    }

    private fun dayWord(n: Int): String = if (n == 1) "day" else "days"

    /**
     * A warm greeting from what the app remembers about you: the time, the days you've tended, and the
     * intention you set. Welcome, never obligation — no "you should," no streak pressure.
     */
    fun greeting(c: Context): String {
        val hi = timeGreeting(c.hourOfDay)
        val back = if (c.daysTended <= 0) "Good to have you here." else "Good to have you back."
        val tended = if (c.daysTended <= 0) "" else " ${c.daysTended} ${dayWord(c.daysTended)} tended."
        val intent = c.intention.trim().trimEnd('.')
        val holding = if (intent.isEmpty()) "" else " Still holding to what you set: “$intent”."
        return "$hi. $back$tended$holding".trim()
    }
}
