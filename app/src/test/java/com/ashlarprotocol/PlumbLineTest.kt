package com.ashlarprotocol

import com.ashlarprotocol.tools.PlumbEntry
import com.ashlarprotocol.tools.TILTS
import com.ashlarprotocol.tools.composeSquaredReflection
import com.ashlarprotocol.tools.relativeDay
import com.ashlarprotocol.tools.tiltById
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Locks in the pure logic of The Plumb (the CBT thought-record). No Android/Compose here —
 * this is deliberately UI-free so the practice is testable and runs fully on-device.
 */
class PlumbLineTest {

    @Test
    fun tiltCatalogIsWellFormed() {
        assertTrue("expected the standard distortion set", TILTS.size >= 8)
        // ids unique, nothing blank
        assertEquals(TILTS.size, TILTS.map { it.id }.toSet().size)
        TILTS.forEach {
            assertTrue(it.id.isNotBlank())
            assertTrue(it.name.isNotBlank())
            assertTrue(it.description.isNotBlank())
        }
    }

    @Test
    fun tiltLookupResolvesAndFailsSafely() {
        assertNotNull(tiltById("catastrophising"))
        assertNull(tiltById("not_a_real_tilt"))
    }

    @Test
    fun reflectionMirrorsTheThoughtAndEvidence() {
        val out = composeSquaredReflection(
            PlumbEntry(
                situation = "Sent a message, no reply yet",
                thought = "They must hate me",
                tiltIds = listOf("mind_reading"),
                evidence = "They replied warmly yesterday and said they were busy today"
            )
        )
        assertTrue(out.contains("They must hate me"))
        assertTrue(out.contains("They replied warmly yesterday"))
        assertTrue(out.lowercase().contains("mind-reading"))
        assertTrue(out.lowercase().contains("upright action"))
    }

    @Test
    fun handlesNoTiltAndNoEvidenceGracefully() {
        val out = composeSquaredReflection(
            PlumbEntry(
                situation = "",
                thought = "I failed the exam",
                tiltIds = emptyList(),
                evidence = ""
            )
        )
        // Still returns a calm, non-empty reflection that honors a possibly-true heavy thought.
        assertTrue(out.contains("I failed the exam"))
        assertTrue(out.lowercase().contains("heavy"))
    }

    @Test
    fun multipleTiltsAreNamedTogether() {
        val out = composeSquaredReflection(
            PlumbEntry(
                situation = "One bad meeting",
                thought = "I always ruin everything and everyone knows it",
                tiltIds = listOf("overgeneralising", "mind_reading"),
                evidence = ""
            )
        )
        assertTrue(out.lowercase().contains("always / never".lowercase()) || out.lowercase().contains("always"))
        assertTrue(out.lowercase().contains("mind-reading"))
    }

    @Test
    fun relativeDaySameDayReadsAsToday() {
        val now = 1_000_000_000_000L
        assertEquals("Today", relativeDay(now, now))
        assertEquals("Today", relativeDay(now - 3 * 3_600_000L, now)) // 3 hours ago
        assertEquals("Today", relativeDay(now + 3_600_000L, now))     // slight clock skew → not scolded
    }

    @Test
    fun relativeDayOneDayReadsAsYesterday() {
        val now = 1_000_000_000_000L
        assertEquals("Yesterday", relativeDay(now - 86_400_000L, now))              // exactly 24h
        assertEquals("Yesterday", relativeDay(now - 86_400_000L - 3_600_000L, now)) // 25h
    }

    @Test
    fun relativeDayCountsWholeDays() {
        val now = 1_000_000_000_000L
        assertEquals("3 days ago", relativeDay(now - 3 * 86_400_000L, now))
        assertEquals("10 days ago", relativeDay(now - 10 * 86_400_000L - 5_000L, now))
    }
}
