package com.ashlarprotocol

import com.ashlarprotocol.tools.Cornerstone
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * The Cornerstone — pure logic for self-directed environment/friction design (F1). These guarantees
 * are what the sheet relies on: every move is correctly categorized, and the two lists partition the
 * whole set (a move must be either reduce-friction-for-good or add-friction-for-bad, never neither).
 */
class CornerstoneTest {

    @Test fun offersBothDirections() {
        assertTrue(Cornerstone.reduceMoves().isNotEmpty())
        assertTrue(Cornerstone.addMoves().isNotEmpty())
    }

    @Test fun everyMoveIsCorrectlyDirected() {
        Cornerstone.reduceMoves().forEach { assertEquals(Cornerstone.Direction.REDUCE, it.direction) }
        Cornerstone.addMoves().forEach { assertEquals(Cornerstone.Direction.ADD, it.direction) }
    }

    @Test fun reduceAndAddPartitionAllMoves() {
        // No move is uncategorized or double-counted.
        assertEquals(Cornerstone.MOVES.size, Cornerstone.reduceMoves().size + Cornerstone.addMoves().size)
    }

    @Test fun cueKindsCoverTimePlaceActionObject() {
        val names = Cornerstone.CueKind.values().map { it.name }.toSet()
        assertTrue(names.containsAll(setOf("TIME", "PLACE", "AFTER_ACTION", "OBJECT")))
    }

    @Test fun noMoveOrCueIsBlank() {
        Cornerstone.MOVES.forEach { assertTrue(it.display.isNotBlank()) }
        Cornerstone.CueKind.values().forEach { assertTrue(it.display.isNotBlank()) }
    }

    @Test fun allTextGathersEveryVisibleString() {
        val text = Cornerstone.allText()
        Cornerstone.MOVES.forEach { assertTrue(text.contains(it.display)) }
        Cornerstone.CueKind.values().forEach { assertTrue(text.contains(it.display)) }
    }
}
