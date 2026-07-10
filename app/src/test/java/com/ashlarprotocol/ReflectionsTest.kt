package com.ashlarprotocol

import com.ashlarprotocol.tools.ReflectionInput
import com.ashlarprotocol.tools.ReflectionKind
import com.ashlarprotocol.tools.Reflections
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * The reflection engine — a scribe-and-mirror. It must (a) never fabricate a fact it can't back with
 * the input, (b) gate noticings behind a real sample floor, and (c) NEVER phrase a noticing as a cause.
 */
class ReflectionsTest {

    @Test fun emptyInputProducesNoFalseFacts() {
        val r = Reflections.reflect(ReflectionInput())
        assertTrue("nothing tended → no tended fact", r.none { it.text.contains("tended") })
        assertTrue("no rough edge → no rough-edge line", r.none { it.text.contains("rough edge") })
    }

    @Test fun tendedDaysBecomeAFactWithProvenance() {
        val r = Reflections.reflect(ReflectionInput(daysTended = 12, currentRun = 5))
        val f = r.first { it.text.contains("12") }
        assertEquals(ReflectionKind.FACT, f.kind)
        assertTrue("carries provenance", f.provenance.isNotBlank())
        assertTrue(f.text.contains("5 in a row"))
    }

    @Test fun everyReflectionCarriesProvenance() {
        Reflections.reflect(
            ReflectionInput(daysTended = 3, intention = "steadier", whoFiveScores = listOf(60), roughEdgeName = "x")
        ).forEach { assertTrue("provenance for: ${it.text}", it.provenance.isNotBlank()) }
    }

    @Test fun whoFiveNoticingNeedsEnoughSpan() {
        // 2 checks only 3 days apart → below the 14-day floor → no trend noticing.
        assertTrue(
            Reflections.reflect(ReflectionInput(whoFiveScores = listOf(50, 70), whoFiveSpanDays = 3))
                .none { it.kind == ReflectionKind.NOTICING && it.text.contains("moved") }
        )
        // Spanning 21 days → a hedged noticing appears.
        val n = Reflections.reflect(ReflectionInput(whoFiveScores = listOf(50, 70), whoFiveSpanDays = 21))
            .first { it.kind == ReflectionKind.NOTICING && it.text.contains("moved") }
        assertTrue(n.text.contains("worth noticing"))
    }

    @Test fun lapseDayPatternNeedsFourSlips() {
        // 3 slips → below floor → silent.
        assertTrue(
            Reflections.reflect(ReflectionInput(roughEdgeName = "x", roughEdgeLapseDays = listOf(2, 9, 16), todayEpochDay = 20))
                .none { it.text.contains("tended to fall") }
        )
        // 4 weekend slips (epoch days 2,3,9,10 = Sat/Sun) → hedged weekend noticing.
        val n = Reflections.reflect(
            ReflectionInput(roughEdgeName = "scrolling", roughEdgeLapseDays = listOf(2, 3, 9, 10), todayEpochDay = 14)
        ).first { it.text.contains("tended to fall") }
        assertTrue(n.text.contains("weekends"))
        assertTrue(n.text.contains("worth noticing"))
    }

    @Test fun noNoticingIsEverCausal() {
        val big = ReflectionInput(
            whoFiveScores = listOf(40, 80), whoFiveSpanDays = 30,
            roughEdgeName = "x", roughEdgeLapseDays = listOf(2, 3, 9, 10, 16), todayEpochDay = 30
        )
        val noticings = Reflections.reflect(big).filter { it.kind == ReflectionKind.NOTICING }
        assertTrue("there are noticings to check", noticings.isNotEmpty())
        noticings.forEach { n ->
            listOf("causes", "because", "proves", " will ", "makes you").forEach {
                assertFalse("noticings must never claim cause ($it): ${n.text}", n.text.lowercase().contains(it))
            }
        }
    }

    @Test fun sampleTextIsNonEmptyForTheSafetySweep() {
        assertTrue(Reflections.allSampleText().isNotEmpty())
    }
}
