package com.example.tools

/**
 * Anti-harm (SPEC_PHASE1_STONE anti-harm instrumentation / ticket T3.2).
 *
 * The plan's anti-harm goal is to catch the ways an engagement app can hurt the people it targets:
 * streak-anxiety, engagement-without-improvement, and compulsive late-night checking. For a wellness
 * app that is FULLY ON-DEVICE with ZERO telemetry (no INTERNET permission, no analytics SDK), the
 * honest form of "instrumentation" is NOT founder analytics — it is on-device CARE turned back to the
 * user. There is no server watching; the app simply notices a compulsive signature and answers with
 * rest, not another nudge.
 *
 * Because the app sends nothing and has no INTERNET permission, the AC's hard rule is met structurally:
 * time-on-device, session length, and DAU CANNOT be optimization targets — they are never measured,
 * never leave the phone, and the app has no way to reward them. The only "metric" that matters is the
 * user's own WHO-5 (T3.1), which is theirs alone.
 *
 * Pure logic; the Board renders the nudge.
 */
object AntiHarm {

    /**
     * A caring rest message when the pattern looks compulsive rather than helpful: it's late at night
     * AND today's work is already done, so there's nothing here the person needs right now. Returns
     * null the rest of the time — including when the work ISN'T done yet (never discourage doing it).
     * Never streak-threat, never FOMO — the opposite.
     */
    fun restNudge(hourOfDay: Int, didTodaysWork: Boolean): String? {
        val lateNight = hourOfDay >= 23 || hourOfDay < 5
        return if (lateNight && didTodaysWork) REST_NUDGE else null
    }

    const val REST_NUDGE: String =
        "It's late, and you've already tended the stone today. Nothing here needs finishing now — " +
            "it keeps while you sleep. Rest is part of the work, not time away from it."
}
