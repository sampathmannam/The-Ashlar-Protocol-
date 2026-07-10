package com.ashlarprotocol

import com.ashlarprotocol.tools.DailyWord
import com.ashlarprotocol.tools.Degree
import com.ashlarprotocol.tools.Degrees
import com.ashlarprotocol.tools.GracefulExit
import com.ashlarprotocol.tools.KindStreak
import com.ashlarprotocol.tools.PowerUps
import com.ashlarprotocol.tools.ReachOut
import com.ashlarprotocol.tools.Readiness
import com.ashlarprotocol.tools.Relief
import com.ashlarprotocol.tools.SafetyAudit
import com.ashlarprotocol.tools.Square
import com.ashlarprotocol.tools.Trowel
import com.ashlarprotocol.tools.WestGate
import com.ashlarprotocol.tools.Working
import com.ashlarprotocol.ui.components.RaisingCopy
import com.ashlarprotocol.ui.screens.MEANING_PROMPTS
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
            "Chamber.MEANING_PROMPTS" to MEANING_PROMPTS,
            // The Raising rite (Phase 2). The Master Mason degree is the most mortality-adjacent moment
            // in the source Craft — its copy must stay on becoming, never on death.
            "Raising.allText" to RaisingCopy.allText(),
            // The degree-path card (Phase 2): the legible arc. Static copy + every degree name + label.
            "DegreePathCard" to (
                listOf("THE WORK BENEATH THE STONE", "The work is now lifelong.") +
                    Degree.values().map { it.display } +
                    Degree.values().mapNotNull { Degrees.towardNextLabel(it) }
            ),
            // The West Gate (Phase 3): doorways to real connection — must stay on belonging, never death.
            "WestGate" to WestGate.allText(),
            // The grace reserve copy (Phase 4 / F3): the visible reserve + the "held for you" note.
            "KindStreak.grace" to listOf(
                KindStreak.graceLabel(0), KindStreak.graceLabel(1), KindStreak.graceLabel(2),
                KindStreak.graceMessage(1)!!
            )
        )

        val violations = SafetyAudit.audit(corpus)
        assertTrue(
            "Phase-1 mortality-symbolism gate FAILED — death imagery found: $violations",
            violations.isEmpty()
        )
    }

    /**
     * The West Gate is ORDINARY, non-crisis connection (Phase 3). Crisis help is a separate, always-on
     * surface (§9 / CrisisSupportDialog). The gate must not masquerade as the crisis line — no doorway
     * action should be a crisis call-to-action; that would blur the safety boundary.
     */
    @Test
    fun westGateIsNotTheCrisisSurface() {
        val actions = WestGate.DOORWAYS.mapNotNull { it.action }.joinToString(" ")
        listOf("988", "911", "999", "116123", "741741").forEach { crisisTarget ->
            assertTrue(
                "West Gate doorway must not act as the crisis line ($crisisTarget)",
                !actions.contains(crisisTarget)
            )
        }
    }
}
