package com.ashlarprotocol.tools

import com.ashlarprotocol.data.RhythmAnchor

/**
 * The rhythm anchor (Phase 4b, F6) — a steady rise and wind-down.
 *
 * Sleep-wake **regularity** (not duration, not earliness) is what the large cohort evidence associates
 * with lower depression/anxiety and mortality (Windred 2024, UK Biobank N≈61k; Li 2025, N≈80k). The
 * mechanism is circadian/social-rhythm entrainment (social zeitgeber theory). This is a gentle anchor,
 * NOT an alarm and NOT tracking. Framing is deliberately **associational** ("linked to"), never causal
 * or clinical — the general-population evidence is observational, and low mood itself disrupts routine,
 * so a rhythm feature must support, never shame. Pure. See docs/RESEARCH_INTEGRATION.md §1.5 / F6.
 */
object Rhythm {

    /** Format a minutes-of-day value as a 12-hour clock string, e.g. 390 → "6:30 AM", 0 → "12:00 AM". */
    fun formatTime(minutesOfDay: Int): String {
        val m = ((minutesOfDay % 1440) + 1440) % 1440
        val h24 = m / 60
        val min = m % 60
        val ampm = if (h24 < 12) "AM" else "PM"
        val h12 = when {
            h24 == 0 -> 12
            h24 > 12 -> h24 - 12
            else -> h24
        }
        return "%d:%02d %s".format(h12, min, ampm)
    }

    /** Common rise times (5:30–8:00) and wind-down times (9:00–11:00 PM) — consistency, not earliness. */
    val WAKE_SLOTS: List<Int> = listOf(5 * 60 + 30, 6 * 60, 6 * 60 + 30, 7 * 60, 7 * 60 + 30, 8 * 60)
    val WIND_DOWN_SLOTS: List<Int> = listOf(21 * 60, 21 * 60 + 30, 22 * 60, 22 * 60 + 30, 23 * 60)

    /** An honest, associational reflection on the anchor — no causal or clinical claim, never shaming. */
    fun reflection(anchor: RhythmAnchor): String =
        "A steady rise (${formatTime(anchor.wakeMinutesOfDay)}) and wind-down " +
            "(${formatTime(anchor.windDownMinutesOfDay)}) is linked, in the research, to steadier days — " +
            "less about the hours than the regularity. No perfect night required; just aim to keep the rhythm."
}
