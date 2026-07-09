package com.example

import com.example.tools.Trowel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * The Trowel — pure logic for the self-compassion rite (Neff's self-compassion break, turned into
 * the Masonic "cement of brotherly love" spread inward). Grounds self-kindness / common-humanity /
 * mindfulness in the evidence, with a backdraft-aware gentle exit (see docs/RESEARCH_BASIS.md).
 * Pure, on-device — these are the guarantees the composable relies on.
 */
class TrowelTest {

    @Test
    fun threeMovementsInOrderAllNonEmpty() {
        assertEquals(3, Trowel.MOVEMENTS.size)
        Trowel.MOVEMENTS.forEach {
            assertTrue(it.label.isNotBlank())
            assertTrue(it.prompt.isNotBlank())
        }
        // Mindfulness → common humanity → self-kindness: the order the break depends on.
        assertEquals("Name it", Trowel.MOVEMENTS[0].label)
        assertTrue(Trowel.MOVEMENTS[1].label.contains("not alone", ignoreCase = true))
        assertTrue(Trowel.MOVEMENTS[2].label.contains("cement", ignoreCase = true))
    }

    @Test
    fun commonHumanityEchoesTheWordsWithoutDistorting() {
        val out = Trowel.commonHumanity("I let everyone down")
        assertTrue(out.contains("I let everyone down")) // their exact words, unaltered
        assertTrue(out.contains("human"))
    }

    @Test
    fun commonHumanityHandlesBlankGently() {
        val out = Trowel.commonHumanity("   ")
        assertTrue(out.isNotBlank())
        assertTrue(out.contains("human"))
    }

    @Test
    fun asABrotherRefamesTowardTheSelf() {
        val out = Trowel.asABrother("I keep failing")
        assertTrue(out.contains("brother"))
        assertTrue(out.contains("I keep failing"))
    }

    @Test
    fun asABrotherHandlesBlank() {
        val out = Trowel.asABrother("")
        assertTrue(out.contains("brother"))
    }

    @Test
    fun closingHandsOwnWordsBack() {
        val out = Trowel.closing("You're doing better than you think")
        assertTrue(out.contains("You're doing better than you think"))
    }

    @Test
    fun closingNeverForcesOutput() {
        val out = Trowel.closing("   ")
        assertTrue(out.isNotBlank())
        // Blank must not be scolded or treated as failure — the kindness still counts.
        assertTrue(out.contains("counts"))
    }

    @Test
    fun groundingAlwaysOffersAWayToStop() {
        assertTrue(Trowel.grounding.isNotEmpty())
        // The gentle exit is guaranteed present: an unconditional permission to stop.
        assertTrue(Trowel.grounding.any { it.contains("stop", ignoreCase = true) })
        // And the honest word about backdraft, so a surge of feeling doesn't read as failure.
        assertTrue(Trowel.grounding.any { it.contains("failure", ignoreCase = true) })
    }
}
