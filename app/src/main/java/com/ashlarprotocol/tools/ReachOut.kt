package com.ashlarprotocol.tools

/**
 * Reach Out — the pure part of the "turn outward" action (Phase 3, the Lodge).
 *
 * The hardest part of reaching a real person is the first few words. These are gentle, low-pressure,
 * EDITABLE openers the app can pre-fill before handing the message to the phone's own messaging apps
 * via a share intent (see ChamberScreen). The app hosts no relationship, sees no contacts, and sends
 * nothing itself — real connection through the user's existing channels, no server, no cost.
 */
object ReachOut {

    val OPENERS: List<String> = listOf(
        "Hey — I've been carrying some weight lately. Are you around to talk?",
        "Could use a friendly ear today. Free to chat sometime?",
        "Not doing my best right now. Would it be alright if I called you?",
        "Thinking I shouldn't sit with this alone. Got a few minutes for me?",
        "It's been a heavy stretch. Can we catch up soon?"
    )

    /** Safe indexed access with wraparound; tolerates any Int. */
    fun openerAt(index: Int): String {
        val n = OPENERS.size
        val i = ((index % n) + n) % n
        return OPENERS[i]
    }
}
