package com.example

import com.example.tools.DailyWord
import com.example.tools.GracefulExit
import com.example.tools.KindStreak
import com.example.tools.PowerUps
import com.example.tools.ReachOut
import com.example.tools.Readiness
import com.example.tools.Relief
import com.example.tools.SafetyAudit
import com.example.tools.Square
import com.example.tools.Trowel
import com.example.tools.Working
import com.example.ui.screens.MEANING_PROMPTS
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * The mortality-symbolism safety gate (SPEC P0.8 / T3.3; RESEARCH_BASIS §10 — BINDING). Enforces in
 * code that ZERO death imagery appears anywhere in Phase 1's copy. If anyone ever adds a skull, a
 * countdown, or "finish your life" framing to any surface below, this suite turns red.
 */
class SafetyAuditTest {

    @Test
    fun detectorFlagsDeathImageryAndPassesWarmCopy() {
        assertTrue(SafetyAudit.mortalityViolations("A quiet, steady morning.").isEmpty())
        assertTrue(SafetyAudit.mortalityViolations("Reflect on the HOURGLASS and the skull.").containsAll(listOf("hourglass", "skull")))
        // Case-insensitive.
        assertEquals(listOf("coffin"), SafetyAudit.mortalityViolations("a Coffin"))
    }

    @Test
    fun auditNamesTheSourceAndTermForAnyViolation() {
        val result = SafetyAudit.audit(mapOf("clean" to listOf("breathe"), "bad" to listOf("a scythe")))
        assertEquals(setOf("bad"), result.keys)
        assertEquals(listOf("scythe"), result["bad"])
    }

    @Test
    fun everyPhase1CopySurfaceIsFreeOfMortalitySymbolism() {
        val corpus: Map<String, List<String>> = mapOf(
            "Relief.WORDS" to Relief.WORDS,
            "GracefulExit.LINES" to GracefulExit.LINES,
            "Square.VALUES" to Square.VALUES,
            "ReachOut.OPENERS" to ReachOut.OPENERS,
            "DailyWord.WORDS" to DailyWord.WORDS,
            "PowerUps" to PowerUps.POWER_UPS.flatMap { listOf(it.title, it.invite) + it.steps },
            "Trowel" to (
                Trowel.MOVEMENTS.flatMap { listOf(it.label, it.prompt) } +
                    Trowel.grounding +
                    listOf(Trowel.closing(""), Trowel.commonHumanity(""), Trowel.asABrother(""))
                ),
            "KindStreak.comeback" to listOf(KindStreak.comebackMessage()),
            "Working" to (Readiness.values().map { Working.acknowledgment(it) } + Readiness.values().map { it.display }),
            // The Chamber is the mortality-sensitive surface (§10) — it must lean to meaning, never death.
            "Chamber.MEANING_PROMPTS" to MEANING_PROMPTS
        )

        val violations = SafetyAudit.audit(corpus)
        assertTrue(
            "Phase-1 mortality-symbolism gate FAILED — death imagery found: $violations",
            violations.isEmpty()
        )
    }
}
