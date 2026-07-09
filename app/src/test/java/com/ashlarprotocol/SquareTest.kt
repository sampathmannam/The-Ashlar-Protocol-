package com.ashlarprotocol

import com.ashlarprotocol.tools.Square
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * The Square — pure logic for the values-clarification rite (ACT). The user picks the few values
 * that matter most; those compose into an intention that becomes the app's north-star. Grounds the
 * Masonic "square your actions" in ACT values work (see docs/RESEARCH_BASIS.md). Pure, on-device.
 */
class SquareTest {

    @Test
    fun valuesAreNonEmptyAndClean() {
        assertTrue(Square.VALUES.size >= 8)
        Square.VALUES.forEach { assertTrue(it.isNotBlank()) }
        assertEquals(Square.VALUES.size, Square.VALUES.toSet().size) // no duplicates
    }

    @Test
    fun oneValueComposesASimpleIntention() {
        assertEquals("To live by courage.", Square.squareIntention(listOf("Courage")))
    }

    @Test
    fun twoValuesJoinWithAnd() {
        assertEquals("To live by courage and honesty.", Square.squareIntention(listOf("Courage", "Honesty")))
    }

    @Test
    fun threeValuesUseSerialComma() {
        assertEquals(
            "To live by courage, honesty, and craft.",
            Square.squareIntention(listOf("Courage", "Honesty", "Craft"))
        )
    }

    @Test
    fun noValuesGivesEmpty() {
        assertEquals("", Square.squareIntention(emptyList()))
        assertEquals("", Square.squareIntention(listOf("  ")))
    }
}
