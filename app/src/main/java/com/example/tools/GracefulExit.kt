package com.example.tools

/**
 * The graceful exit (SPEC_PHASE1_STONE P0.7 / ticket T2.5).
 *
 * Once someone has done the day's work, the app should give them explicit permission to stop —
 * "you've done enough for today" — instead of pulling them back in. This is the opposite of a
 * dark pattern: no FOMO, no "streak at risk", no interstitial standing between the person and
 * closing the app. Resting is framed as part of the work, not a lapse from it.
 *
 * Pure content. The copy here is held to the plan's ⚠️ copy-review gate: it must never use loss,
 * guilt, or FOMO framing (the test enforces this).
 */
object GracefulExit {

    val LINES: List<String> = listOf(
        "You've tended the stone today. That's enough.",
        "Rest is part of the work, not time away from it.",
        "The stone keeps while you're gone. Come back when you're ready — tomorrow, or the day after."
    )
}
