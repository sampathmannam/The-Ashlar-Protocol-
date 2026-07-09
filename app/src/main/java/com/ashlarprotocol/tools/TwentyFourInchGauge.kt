package com.ashlarprotocol.tools

/**
 * The 24-inch Gauge — divide the day into its three parts and see that the plan is honoured.
 *
 * The operative mason's gauge measured the working stone; the speculative gauge measures the day,
 * traditionally divided into three parts. Here those parts are reframed universally and grounded
 * in Behavioral Activation — a first-line, guideline-recommended approach for low mood in which
 * you deliberately schedule meaningful, mastery, and restful activity, then follow through
 * (NICE NG222; Cuijpers et al. 2007). See docs/RESEARCH_BASIS.md §1. Pure, on-device, no network.
 */

enum class DayPart(val display: String, val intent: String) {
    SERVICE("Service", "Something for others, or for what you believe in"),
    WORK("Work", "The day's labour and duty"),
    REST("Rest", "Refreshment, recovery, and sleep")
}

/** One planned activity for a part of the day; [done] tracks that it actually happened. */
data class GaugeItem(
    val id: String,
    val part: DayPart,
    val text: String,
    val done: Boolean = false
)

object Gauge {

    /** Fraction of planned items completed. 0f when nothing is planned. */
    fun completion(items: List<GaugeItem>): Float {
        if (items.isEmpty()) return 0f
        return items.count { it.done }.toFloat() / items.size
    }

    /**
     * Compose an implementation intention — an "if [cue], then I will [action]" plan. Specifying the
     * *when/where* cue alongside the action is the mechanism behind the effect (Gollwitzer & Sheeran
     * 2006, d≈0.65; see docs/RESEARCH_BASIS.md). A missing cue falls back to the bare action.
     */
    fun implementationIntention(cue: String, action: String): String {
        val c = cue.trim()
        val a = action.trim()
        return when {
            a.isEmpty() -> ""
            c.isEmpty() -> a.replaceFirstChar { it.uppercase() }
            else -> "If $c, then I will $a"
        }
    }

    /** The parts of the day that have nothing planned yet, in canonical order. */
    fun missingParts(items: List<GaugeItem>): List<DayPart> {
        val present = items.map { it.part }.toSet()
        return DayPart.values().filter { it !in present }
    }

    /**
     * A day is "complete" — worth counting toward the degree — only when all three parts have at
     * least one planned item and every planned item was actually done. Half a day doesn't count.
     */
    fun isDayComplete(items: List<GaugeItem>): Boolean =
        items.isNotEmpty() && missingParts(items).isEmpty() && items.all { it.done }

    /**
     * A calm, Behavioral-Activation-flavoured nudge based on what's planned:
     *  - nothing yet -> invite dividing the day
     *  - only labour -> nudge toward something that matters and toward rest
     *  - all three   -> affirm the balance
     */
    fun balanceMessage(items: List<GaugeItem>): String {
        if (items.isEmpty()) {
            return "Divide your day into three parts: something that matters, the work itself, and true rest."
        }
        val missing = missingParts(items)
        return when {
            DayPart.SERVICE in missing && DayPart.REST in missing ->
                "Plenty of work planned. Add one thing that matters beyond it — and protect some rest."
            DayPart.SERVICE in missing ->
                "Add one thing that matters beyond the day's work, even a small one."
            DayPart.REST in missing ->
                "Guard a portion for rest and refreshment — the gauge is not all labour."
            DayPart.WORK in missing ->
                "Meaning and rest are set. Name the day's work so the three parts stand together."
            else ->
                "Your day stands squared across all three parts. Now honour the plan, one item at a time."
        }
    }
}
