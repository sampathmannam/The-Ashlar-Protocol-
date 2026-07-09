package com.example.tools

/**
 * Cue-anchored practice reminders (SPEC P0.2 / ticket T1.5).
 *
 * A practice fires best at its cue ("after dinner"), not a random clock time. This app deliberately
 * can't SENSE when your dinner is (no context sensing, by privacy design), so the honest form is: you
 * map the anchor to roughly when it happens, and the reminder lands there. Opt-in per practice, gentle,
 * and — a hard rule — NEVER loss-framed: no "streak at risk," no guilt, no "don't lose." The reminder
 * offers the practice and, in the same breath, permission to skip it.
 *
 * Pure logic: the preset times, the reminder copy, and the delay until the next firing. The worker
 * shows it; the ViewModel schedules it.
 */
object PracticeReminder {

    /** A rough time of day to map an anchor onto. minutesOfDay = hour*60 + minute. */
    data class Slot(val label: String, val minutesOfDay: Int)

    val SLOTS: List<Slot> = listOf(
        Slot("Morning", 9 * 60),
        Slot("Midday", 13 * 60),
        Slot("Evening", 19 * 60),
        Slot("Night", 21 * 60)
    )

    fun slotLabel(minutesOfDay: Int?): String =
        SLOTS.firstOrNull { it.minutesOfDay == minutesOfDay }?.label ?: "No reminder"

    /** The notification title — the cue itself, so it reads as "oh right, after my coffee." */
    fun reminderTitle(anchor: String): String = "After ${anchor.trim().trimEnd('.', ',')}"

    /**
     * The notification body: the action, framed as something you chose, with explicit permission to
     * skip. Never loss/guilt/FOMO framing (the test enforces this).
     */
    fun reminderBody(action: String): String =
        "${action.trim().trimEnd('.')} — a small thing you set for yourself. Skip it if now isn't the moment."

    /**
     * Minutes from now until the next firing of [targetMinuteOfDay]: later today if it's still ahead,
     * otherwise the same time tomorrow. Always in 1..1440 (never fires instantly, never negative).
     */
    fun initialDelayMinutes(targetMinuteOfDay: Int, nowMinuteOfDay: Int): Long {
        val t = ((targetMinuteOfDay % 1440) + 1440) % 1440
        val n = ((nowMinuteOfDay % 1440) + 1440) % 1440
        val diff = t - n
        return (if (diff > 0) diff else diff + 1440).toLong()
    }
}
