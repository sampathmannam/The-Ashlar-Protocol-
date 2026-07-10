package com.ashlarprotocol.tools

/**
 * The Temple — the long-arc progression. You earn WAGES (corn, wine, oil) by doing the challenges and
 * tending the stone, and LAY them to raise the COURSES of your Temple. A "course" is both a layer of
 * stone and a unit of study: raising one affirms a real, research-grounded practice. Spending is
 * building — wages laid become permanent stone, spent but never lost. Deterministic and pure.
 *
 * MVP authors the Apprentice tranche of a planned 50-course curriculum (docs/superpowers/specs/
 * 2026-07-10-the-temple-design.md). Guardrail: raising a course NEVER gates a tool — the practices
 * stay open; the course is the teaching, not a lock.
 */
data class Course(
    val index: Int,
    val name: String,
    val degree: Degree,
    val cost: Int,
    val unlocks: String,
    val basis: String
)

object Temple {
    /** Deterministic wages — the fruit of work, never random. Costs are tuned so a course is ~a few
     *  days to a week of genuine engagement, never a grind. */
    const val DAILY_WAGE = 1
    const val WEEKLY_WAGE = 3

    /** The full planned journey is 50 courses; MVP authors the Apprentice tranche below. */
    const val PLANNED_COURSES = 50

    val COURSES: List<Course> = listOf(
        Course(1, "The First Stone", Degree.ENTERED_APPRENTICE, 3,
            "Showing up once — the day is tended.", "Behavioral activation (Ekers 2014, SMD −0.74)"),
        Course(2, "The Level", Degree.ENTERED_APPRENTICE, 3,
            "A line of self-kindness on a hard day.", "Self-compassion (Neff 2003)"),
        Course(3, "The Common Gavel", Degree.ENTERED_APPRENTICE, 4,
            "Catching one rough corner with an if-then.", "Implementation intentions (Gollwitzer & Sheeran 2006, d=0.65)"),
        Course(4, "The Twenty-Four-Inch Gauge", Degree.ENTERED_APPRENTICE, 4,
            "Dividing the day into balanced parts.", "Activity scheduling (Cuijpers 2007, d=0.87)"),
        Course(5, "The Cornerstone", Degree.ENTERED_APPRENTICE, 5,
            "One change to your surroundings that makes the good choice easier.", "Environment/habit design (Wood & Neal 2016)"),
        Course(6, "The Chalk Line", Degree.ENTERED_APPRENTICE, 5,
            "Setting one if-then Practice against a known cue.", "Implementation intentions (Gollwitzer & Sheeran 2006)"),
        Course(7, "The Plumb", Degree.ENTERED_APPRENTICE, 6,
            "Straightening one leaning thought.", "Cognitive restructuring (Beck)"),
        Course(8, "The Tracing Board", Degree.ENTERED_APPRENTICE, 6,
            "Naming what you are working toward.", "Values & autonomy (Ryan & Deci, SDT)"),
        Course(9, "The West Gate", Degree.ENTERED_APPRENTICE, 6,
            "Turning toward one person you trust.", "Social connection (Holt-Lunstad 2010, OR 1.5)"),
        Course(10, "The Rough Ashlar", Degree.ENTERED_APPRENTICE, 7,
            "Naming one rough edge honestly, without shame.", "Anti-AVE relapse prevention (Marlatt & Gordon)"),
        Course(11, "The Working Tools", Degree.ENTERED_APPRENTICE, 7,
            "A slow, paced breath as a daily anchor.", "Paced breathing / arousal down-regulation"),
        Course(12, "The Middle Chamber", Degree.ENTERED_APPRENTICE, 8,
            "Pausing to see the work already done.", "Progress principle (Amabile & Kramer 2011)"),

        // ── Fellowcraft — the deeper craft: competence in the core skills (courses 13–31) ──────────
        Course(13, "The Second Degree", Degree.FELLOWCRAFT, 8,
            "Stepping past first things into the deeper work.", "Progress & mastery (Amabile & Kramer 2011)"),
        Course(14, "The Square", Degree.FELLOWCRAFT, 8,
            "Acting by your values when it's hard.", "Values-based action / ACT (Hayes 2006)"),
        Course(15, "The Winding Staircase", Degree.FELLOWCRAFT, 9,
            "Meeting a hard thing one step at a time.", "Graded task / approach (Craske 2014)"),
        Course(16, "The Five Senses", Degree.FELLOWCRAFT, 9,
            "Grounding through what you can see, hear, and feel.", "Sensory grounding / attention"),
        Course(17, "The Liberal Arts", Degree.FELLOWCRAFT, 9,
            "Learning one new thing for its own sake.", "Competence & curiosity (Ryan & Deci, SDT)"),
        Course(18, "The Two Pillars", Degree.FELLOWCRAFT, 10,
            "Holding both a steady routine and room to bend.", "Psychological flexibility (Kashdan & Rottenberg 2010)"),
        Course(19, "The Plumb, Deepened", Degree.FELLOWCRAFT, 10,
            "Catching a thinking pattern that keeps returning.", "Cognitive distortions (Beck; Burns)"),
        Course(20, "The Level, Deepened", Degree.FELLOWCRAFT, 10,
            "Meeting a setback the way you'd meet a friend's.", "Self-compassion & anti-AVE (Neff; Marlatt)"),
        Course(21, "The Fellow's Table", Degree.FELLOWCRAFT, 10,
            "A real conversation with someone you trust.", "Social connection (Holt-Lunstad 2010)"),
        Course(22, "The Restoring Hours", Degree.FELLOWCRAFT, 11,
            "Noticing what genuinely refills you.", "Behavioral activation (Lejuez 2011)"),
        Course(23, "The Steady Rhythm", Degree.FELLOWCRAFT, 11,
            "Holding a regular rise and rest across a week.", "Sleep regularity (associational)"),
        Course(24, "The Rough Edge, Faced", Degree.FELLOWCRAFT, 11,
            "Riding one urge all the way through.", "Urge surfing (Bowen & Marlatt 2009)"),
        Course(25, "The Chisel", Degree.FELLOWCRAFT, 11,
            "One skill practised with care until it comes easier.", "Deliberate practice (Ericsson 1993)"),
        Course(26, "The Working Week", Degree.FELLOWCRAFT, 12,
            "The Gauge held across several days, not just one.", "Activity scheduling (Cuijpers 2007)"),
        Course(27, "The Trestle Board", Degree.FELLOWCRAFT, 12,
            "Planning the week around one thing that matters.", "Implementation intentions + values (Gollwitzer)"),
        Course(28, "The Cable Tow", Degree.FELLOWCRAFT, 12,
            "Reaching for help before things get heavy.", "Help-seeking / social support"),
        Course(29, "The Broached Thurnel", Degree.FELLOWCRAFT, 12,
            "Tending one habit until it runs on its own.", "Habit automaticity (Lally 2010)"),
        Course(30, "The Point Within the Circle", Degree.FELLOWCRAFT, 13,
            "Holding one boundary that keeps you well.", "Boundaries / values-consistent living"),
        Course(31, "The Fellowcraft's Charge", Degree.FELLOWCRAFT, 13,
            "A pause to name who you are becoming.", "Narrative identity (McAdams; Adler 2012)")
    )

    fun courseAt(index: Int): Course? = COURSES.getOrNull(index - 1)

    fun cumulativeCost(coursesRaised: Int): Int =
        COURSES.take(coursesRaised.coerceIn(0, COURSES.size)).sumOf { it.cost }

    fun balance(totalEarned: Int, coursesRaised: Int): Int =
        (totalEarned - cumulativeCost(coursesRaised)).coerceAtLeast(0)

    fun nextCourse(coursesRaised: Int): Course? = COURSES.getOrNull(coursesRaised)

    fun canRaiseNext(totalEarned: Int, coursesRaised: Int): Boolean {
        val next = nextCourse(coursesRaised) ?: return false
        return balance(totalEarned, coursesRaised) >= next.cost
    }

    fun allText(): List<String> = COURSES.flatMap { listOf(it.name, it.unlocks) }
}
