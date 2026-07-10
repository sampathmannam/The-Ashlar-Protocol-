package com.ashlarprotocol

import com.ashlarprotocol.tools.Gavel
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * The Gavel — a competing-response micro-practice (catch a reactive pattern, pre-commit a truer move).
 * It must weave the person's own words into an if-then, tolerate a missing trigger, and never shame.
 */
class GavelTest {

    @Test fun roughCornersAreRealAndNonBlank() {
        assertTrue(Gavel.ROUGH_CORNERS.isNotEmpty())
        Gavel.ROUGH_CORNERS.forEach { assertTrue(it.isNotBlank()) }
    }

    @Test fun canSquareNeedsAReactionAndAResponse() {
        assertTrue(Gavel.canSquare("snapping", "one breath"))
        assertFalse(Gavel.canSquare("", "one breath"))
        assertFalse(Gavel.canSquare("snapping", ""))
    }

    @Test fun composeWeavesAllThree() {
        val s = Gavel.compose("snapping", "I feel judged", "take one breath")
        assertTrue(s.contains("snapping"))
        assertTrue(s.contains("I feel judged"))
        assertTrue(s.contains("take one breath"))
    }

    @Test fun composeHandlesABlankTrigger() {
        val s = Gavel.compose("snapping", "", "take one breath")
        assertTrue("falls back to a general cue", s.contains("When it rises"))
        assertTrue(s.contains("take one breath"))
    }

    @Test fun composedLineHasNoShameOrGritLanguage() {
        val s = Gavel.compose("snapping", "I feel judged", "take one breath").lowercase()
        listOf("must", "should", "willpower", "toughen", "grit", "push through", "no excuses").forEach {
            assertFalse("Gavel copy must not moralise/shame ($it)", s.contains(it))
        }
    }
}
