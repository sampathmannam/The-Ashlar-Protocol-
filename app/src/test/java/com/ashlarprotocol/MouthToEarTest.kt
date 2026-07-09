package com.ashlarprotocol

import com.ashlarprotocol.tools.MouthToEar
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Mouth to Ear — pure logic for committing a chosen principle to memory by progressive cue
 * reduction (the oral-tradition "memory work"). Deterministic (no RNG) so it is testable and so
 * recall practice is reproducible. See docs/RESEARCH_BASIS.md.
 */
class MouthToEarTest {

    private fun underscoreTokens(s: String) = s.split(" ").count { it.isNotEmpty() && it.all { c -> c == '_' } }

    @Test
    fun maskAtLevelZeroReturnsTextUnchanged() {
        val text = "so mote it be"
        assertEquals(text, MouthToEar.mask(text, 0f))
    }

    @Test
    fun maskAtLevelOneHidesEveryWord() {
        val text = "so mote it be"
        val masked = MouthToEar.mask(text, 1f)
        assertEquals(4, underscoreTokens(masked))
        assertNotEquals(text, masked)
    }

    @Test
    fun maskAtHalfHidesAboutHalfTheWords() {
        val text = "one two three four"
        val masked = MouthToEar.mask(text, 0.5f)
        assertEquals(2, underscoreTokens(masked))
    }

    @Test
    fun maskPreservesWordLength() {
        val masked = MouthToEar.mask("three", 1f)
        assertEquals("_____", masked) // 5 letters -> 5 underscores
    }

    @Test
    fun scoreRecallIsPerfectIgnoringCaseAndPunctuation() {
        assertEquals(1.0f, MouthToEar.scoreRecall("So mote it be.", "so MOTE it, be"), 0.0001f)
    }

    @Test
    fun scoreRecallCountsPositionalMatches() {
        assertEquals(0.75f, MouthToEar.scoreRecall("one two three four", "one wrong three four"), 0.0001f)
    }

    @Test
    fun scoreRecallIsZeroForEmptyAttempt() {
        assertEquals(0.0f, MouthToEar.scoreRecall("one two three four", ""), 0.0001f)
    }

    @Test
    fun recallCountsAsHeldAtOrAboveThreshold() {
        assertTrue(MouthToEar.isHeld(1.0f))
        assertTrue(MouthToEar.isHeld(MouthToEar.RECALL_HELD_THRESHOLD))
        assertTrue(!MouthToEar.isHeld(MouthToEar.RECALL_HELD_THRESHOLD - 0.01f))
        assertTrue(!MouthToEar.isHeld(0f))
    }
}
