package com.ashlarprotocol.tools

/**
 * What the Stone Remembers — the reflection engine (the "AI phase", done as a *scribe and mirror*).
 *
 * It reflects the user's own on-device data back to them. There is **no model and no generation** — it
 * can only surface what is literally present in [ReflectionInput], so it is **zero-hallucination by
 * construction** (MASTER_PLAN Phase 4; the maker's AI north-star). Two kinds:
 *  - **FACTS** — literal truths (your history, totals, your own words). Zero inference.
 *  - **NOTICINGS** — a few carefully hedged co-occurrence observations, each **sample-floor-gated** and
 *    phrased as an invitation to notice, **never as cause** (the honesty line the whole research rests
 *    on). Only the noticings that are honestly computable from persisted per-day data are produced;
 *    cross-dimension co-occurrence is deliberately absent (no per-day arrival history is stored).
 *
 * Pure and time-free: the ViewModel precomputes epoch-days and spans at the boundary so this stays
 * deterministic and trivially testable. Safety is model-independent — this never gives advice.
 */

enum class ReflectionKind { FACT, NOTICING }

/** One reflection, always carrying its [provenance] (which data it came from) — nothing free-floats. */
data class Reflection(val kind: ReflectionKind, val text: String, val provenance: String)

/** A plain snapshot of already-persisted data — all primitives, precomputed at the VM boundary. */
data class ReflectionInput(
    val daysTended: Int = 0,
    val currentRun: Int = 0,
    val degreeDisplay: String = "",
    val intention: String = "",
    val practicesCount: Int = 0,
    val journalCount: Int = 0,
    val plumbCount: Int = 0,
    val gaugeDays: Int = 0,
    val recallCount: Int = 0,
    val keptReflectionsCount: Int = 0,
    val signatureStrengths: List<String> = emptyList(),
    val automaticityLevel: Int = -1,
    val rhythmWake: String? = null,
    val rhythmWindDown: String? = null,
    val roughEdgeName: String? = null,
    /** Lapse days as epoch-day integers (any order). */
    val roughEdgeLapseDays: List<Long> = emptyList(),
    val todayEpochDay: Long = 0,
    /** WHO-5 scores in chronological order. */
    val whoFiveScores: List<Int> = emptyList(),
    /** Days between the first and last WHO-5 check. */
    val whoFiveSpanDays: Int = 0
)

object Reflections {

    private const val WHO5_MIN_SPAN_DAYS = 14
    private const val LAPSE_MIN_COUNT = 4
    private const val CLUSTER_THRESHOLD = 0.60

    /** FACTs first, then guarded NOTICINGs. Emits nothing it cannot back with the input. */
    fun reflect(input: ReflectionInput): List<Reflection> {
        val out = mutableListOf<Reflection>()

        // ── FACTS — literal, zero inference ──────────────────────────────────────────────
        if (input.daysTended > 0) {
            val run = if (input.currentRun > 1) " — ${input.currentRun} in a row right now" else ""
            out += fact("You've tended the stone ${input.daysTended} ${dayWord(input.daysTended)}$run.", "days tended")
        }
        if (input.degreeDisplay.isNotBlank()) out += fact("You stand as ${input.degreeDisplay}.", "degree")
        if (input.intention.isNotBlank()) {
            val p = if (input.practicesCount > 0)
                " Your ${input.practicesCount} ${plural(input.practicesCount, "practice", "practices")} point that way." else ""
            out += fact("You said you're working toward “${input.intention}”.$p", "intention")
        }
        if (input.whoFiveScores.isNotEmpty())
            out += fact("Your last wellbeing check was ${input.whoFiveScores.last()} out of 100.", "WHO-5")
        input.roughEdgeName?.let { name ->
            val slips = input.roughEdgeLapseDays.size
            val slipText = if (slips == 0) "no slips logged yet"
                else "$slips ${plural(slips, "slip", "slips")}, each logged and forgiven"
            val clear = if (input.roughEdgeLapseDays.isEmpty()) 0
                else (input.todayEpochDay - input.roughEdgeLapseDays.max()).toInt().coerceAtLeast(0)
            val clearText = if (clear > 0) " — $clear ${dayWord(clear)} clear" else ""
            out += fact("You're working one rough edge: $name. $slipText$clearText.", "rough edge")
        }
        if (input.automaticityLevel >= 0) {
            val a = when (input.automaticityLevel.coerceIn(0, 2)) {
                0 -> "still takes effort"; 1 -> "getting easier"; else -> "becoming automatic"
            }
            out += fact("Last you checked, the work was $a.", "automaticity")
        }
        val counts = buildList {
            if (input.journalCount > 0) add("${input.journalCount} ${plural(input.journalCount, "note", "notes")} in the journal")
            if (input.plumbCount > 0) add("${input.plumbCount} ${plural(input.plumbCount, "thought", "thoughts")} squared on the Plumb")
            if (input.gaugeDays > 0) add("${input.gaugeDays} ${dayWord(input.gaugeDays)} divided by the Gauge")
            if (input.recallCount > 0) add("${input.recallCount} ${plural(input.recallCount, "principle", "principles")} held to memory")
            if (input.keptReflectionsCount > 0) add("${input.keptReflectionsCount} ${plural(input.keptReflectionsCount, "reflection", "reflections")} kept")
        }
        if (counts.isNotEmpty()) out += fact("The work so far: ${counts.joinToString("; ")}.", "counts")
        if (input.signatureStrengths.isNotEmpty())
            out += fact("Your signature strengths: ${input.signatureStrengths.joinToString(", ")}.", "strengths")
        if (input.rhythmWake != null && input.rhythmWindDown != null)
            out += fact("Your rhythm: rise ${input.rhythmWake}, wind down ${input.rhythmWindDown}.", "rhythm")

        // ── NOTICINGS — guarded, hedged, never causal ────────────────────────────────────
        if (input.whoFiveScores.size >= 2 && input.whoFiveSpanDays >= WHO5_MIN_SPAN_DAYS) {
            val first = input.whoFiveScores.first(); val last = input.whoFiveScores.last()
            if (first != last) {
                val dir = if (last > first) "up" else "down"
                out += noticing(
                    "Your wellbeing check has moved $first → $last ($dir) over about ${input.whoFiveSpanDays} days — worth noticing, not proof.",
                    "WHO-5 trend"
                )
            }
        }
        if (input.roughEdgeLapseDays.size >= LAPSE_MIN_COUNT) {
            val weekendFrac = input.roughEdgeLapseDays.count { isWeekend(it) }.toDouble() / input.roughEdgeLapseDays.size
            val n = input.roughEdgeLapseDays.size
            when {
                weekendFrac >= CLUSTER_THRESHOLD ->
                    out += noticing("Your slips have tended to fall on weekends — worth noticing, not proof (from $n).", "lapse day-pattern")
                (1 - weekendFrac) >= CLUSTER_THRESHOLD ->
                    out += noticing("Your slips have tended to fall on weekdays — worth noticing, not proof (from $n).", "lapse day-pattern")
            }
            val longest = longestClearStretch(input.roughEdgeLapseDays, input.todayEpochDay)
            if (longest > 0) out += noticing("Your longest clear stretch so far is $longest ${dayWord(longest)}.", "clear stretch")
        }
        return out
    }

    /** Representative copy for the safety sweep — a rich, fully-populated reflection set. */
    fun allSampleText(): List<String> = reflect(
        ReflectionInput(
            daysTended = 12, currentRun = 5, degreeDisplay = "Fellowcraft", intention = "steadier days",
            practicesCount = 2, journalCount = 3, plumbCount = 4, gaugeDays = 2, recallCount = 1,
            keptReflectionsCount = 2, signatureStrengths = listOf("Bravery", "Hope"), automaticityLevel = 1,
            rhythmWake = "6:30 AM", rhythmWindDown = "10:00 PM", roughEdgeName = "late scrolling",
            roughEdgeLapseDays = listOf(2, 3, 9, 10, 16), todayEpochDay = 30,
            whoFiveScores = listOf(50, 72), whoFiveSpanDays = 21
        )
    ).map { it.text }

    private fun fact(text: String, provenance: String) = Reflection(ReflectionKind.FACT, text, provenance)
    private fun noticing(text: String, provenance: String) = Reflection(ReflectionKind.NOTICING, text, provenance)
    private fun dayWord(n: Int) = if (n == 1) "day" else "days"
    private fun plural(n: Int, one: String, many: String) = if (n == 1) one else many

    /** Sat/Sun. Epoch day 0 = Thursday, so ((d%7)+7)%7: 2 = Sat, 3 = Sun. */
    private fun isWeekend(epochDay: Long): Boolean {
        val d = ((epochDay % 7) + 7) % 7
        return d == 2L || d == 3L
    }

    private fun longestClearStretch(lapseDays: List<Long>, today: Long): Int {
        if (lapseDays.isEmpty()) return 0
        val sorted = lapseDays.sorted()
        var longest = 0L
        for (i in 1 until sorted.size) longest = maxOf(longest, sorted[i] - sorted[i - 1] - 1)
        longest = maxOf(longest, today - sorted.last())
        return longest.toInt().coerceAtLeast(0)
    }
}
