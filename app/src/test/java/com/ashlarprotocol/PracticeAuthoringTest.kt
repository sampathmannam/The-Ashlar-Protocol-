package com.ashlarprotocol

import com.ashlarprotocol.tools.PracticeAuthoring
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Practice authoring (SPEC T1.4): anchored, approach-framed implementation intentions. The guard that
 * redirects avoidance phrasing is the heart of it.
 */
class PracticeAuthoringTest {

    @Test
    fun avoidancePhrasingIsCaught() {
        assertTrue(PracticeAuthoring.isAvoidanceFramed("stop scrolling"))
        assertTrue(PracticeAuthoring.isAvoidanceFramed("don't check my phone"))
        assertTrue(PracticeAuthoring.isAvoidanceFramed("quit snacking"))
        assertTrue(PracticeAuthoring.isAvoidanceFramed("reduce screen time"))
        assertTrue(PracticeAuthoring.isAvoidanceFramed("cut out sugar"))
        assertTrue(PracticeAuthoring.isAvoidanceFramed("give up coffee"))
    }

    @Test
    fun approachPhrasingIsAllowed() {
        assertFalse(PracticeAuthoring.isAvoidanceFramed("walk for ten minutes"))
        assertFalse(PracticeAuthoring.isAvoidanceFramed("read one page"))
        assertFalse(PracticeAuthoring.isAvoidanceFramed("give myself a break")) // "give" alone ≠ "give up"
        assertFalse(PracticeAuthoring.isAvoidanceFramed("note three good things")) // "note" ≠ "not"
        assertFalse(PracticeAuthoring.isAvoidanceFramed(""))
    }

    @Test
    fun composesTheAfterIWillPlan() {
        assertEquals(
            "After I pour my morning coffee, I will write one line in my journal.",
            PracticeAuthoring.composePlan("I pour my morning coffee", "write one line in my journal")
        )
        // Trims trailing punctuation and whitespace.
        assertEquals(
            "After dinner, I will stretch for two minutes.",
            PracticeAuthoring.composePlan("  dinner.  ", "  stretch for two minutes.  ")
        )
        assertEquals("", PracticeAuthoring.composePlan("", "walk"))
        assertEquals("", PracticeAuthoring.composePlan("dinner", ""))
    }

    @Test
    fun canSaveOnlyWithAnchorApproachActionAndNoAvoidance() {
        assertTrue(PracticeAuthoring.canSave("dinner", "walk ten minutes"))
        assertFalse("needs an anchor", PracticeAuthoring.canSave("", "walk"))
        assertFalse("needs an action", PracticeAuthoring.canSave("dinner", ""))
        assertFalse("avoidance is blocked", PracticeAuthoring.canSave("dinner", "stop scrolling"))
    }

    @Test
    fun requiresIntentionFirstWhenNoCommittedIntention() {
        // If-then plans amplify a committed goal — with no intention set, name one first (F2).
        assertTrue(PracticeAuthoring.requiresIntentionFirst(""))
        assertTrue(PracticeAuthoring.requiresIntentionFirst("   "))
        assertFalse(PracticeAuthoring.requiresIntentionFirst("steadier days"))
    }
}
