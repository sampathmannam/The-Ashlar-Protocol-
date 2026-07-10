package com.ashlarprotocol.tools

/**
 * Challenges — the research-designed tasks that pay wages. DAILY is a small, optional menu of
 * behavioral-activation micro-actions (an invitation, never a must-do); WEEKLY is the value-aligned
 * spine. Fixed cue, rotating content: the menu rotates deterministically by day so it stays fresh
 * without random reward. Missing a challenge takes nothing. Pure — no Android, no I/O.
 */
enum class Cadence { DAILY, WEEKLY }

data class Challenge(
    val id: String,
    val title: String,
    val invite: String,
    val cadence: Cadence,
    val basis: String
)

object Challenges {
    val DAILY: List<Challenge> = listOf(
        Challenge("lay_one_stone", "Lay one stone", "One small thing that matters. That's the day tended.",
            Cadence.DAILY, "Behavioral activation (Ekers 2014)"),
        Challenge("slow_breath", "A slow breath", "A minute, out-breath leading. That's enough.",
            Cadence.DAILY, "Paced breathing / vagal tone"),
        Challenge("line_of_thanks", "A line of thanks", "Tell one person, in a sentence, what you're grateful for.",
            Cadence.DAILY, "Gratitude expression (Seligman 2005)"),
        Challenge("square_a_corner", "Square one corner", "Catch one rough moment with an if-then.",
            Cadence.DAILY, "Implementation intentions (Gollwitzer 2006)"),
        Challenge("name_the_load", "Name the load", "A ten-second check: how heavy is today?",
            Cadence.DAILY, "Self-monitoring / behavioral activation")
    )

    val WEEKLY: List<Challenge> = listOf(
        Challenge("set_cornerstone", "Set a cornerstone", "One change to your surroundings this week that makes the good choice easier.",
            Cadence.WEEKLY, "Environment/habit design (Wood & Neal 2016)"),
        Challenge("draw_chalk_line", "Draw a chalk line", "Author one if-then Practice this week and run it.",
            Cadence.WEEKLY, "Implementation intentions (Gollwitzer & Sheeran 2006, d=0.65)"),
        Challenge("open_west_gate", "Open the West Gate", "Reach toward one person you trust this week.",
            Cadence.WEEKLY, "Social connection (Holt-Lunstad 2010)"),
        Challenge("divide_the_day", "Divide the day", "Use the Gauge to balance three days this week.",
            Cadence.WEEKLY, "Activity scheduling (Cuijpers 2007)")
    )

    fun wageFor(cadence: Cadence): Int = when (cadence) {
        Cadence.DAILY -> Temple.DAILY_WAGE
        Cadence.WEEKLY -> Temple.WEEKLY_WAGE
    }

    /** A stable per-day menu: a deterministic rotating window over the DAILY catalog (no randomness). */
    fun dailyMenu(epochDay: Long, size: Int = 3): List<Challenge> {
        if (DAILY.isEmpty()) return emptyList()
        val n = size.coerceAtMost(DAILY.size)
        val start = (((epochDay % DAILY.size) + DAILY.size) % DAILY.size).toInt()
        return (0 until n).map { DAILY[(start + it) % DAILY.size] }
    }

    /** One weekly challenge, stable across a 7-day block, rotating week to week. */
    fun weeklyChallenge(epochDay: Long): Challenge {
        val week = Math.floorDiv(epochDay, 7L)
        val idx = (((week % WEEKLY.size) + WEEKLY.size) % WEEKLY.size).toInt()
        return WEEKLY[idx]
    }

    fun allText(): List<String> = (DAILY + WEEKLY).flatMap { listOf(it.title, it.invite) }
}
