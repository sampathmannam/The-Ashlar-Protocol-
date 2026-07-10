package com.ashlarprotocol

import com.ashlarprotocol.data.RoughEdgeEntry
import com.ashlarprotocol.tools.PowerUps
import com.ashlarprotocol.tools.RoughEdge
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * The Rough-Edge track (F5) — the addiction face, built on the anti-AVE spine. The two things that
 * MUST hold: a slip is never catastrophized, and there is no streak to break (the ledger only grows).
 */
class RoughEdgeTest {

    @Test fun lapseResponseIsAntiAVE_neverShames() {
        val r = RoughEdge.lapseResponse()
        assertTrue(r.isNotBlank())
        listOf("failed", "relapse", "blew it", "ruined", "hopeless", "broke").forEach {
            assertFalse("lapse response must not catastrophize ($it)", r.lowercase().contains(it))
        }
    }

    @Test fun safetyNoteHandsOffWithoutShame() {
        assertTrue("names its limits honestly", RoughEdge.SAFETY_NOTE.contains("not treatment"))
        listOf("pathetic", "failure", "hopeless", "your fault").forEach {
            assertFalse("safety note must not shame ($it)", RoughEdge.SAFETY_NOTE.lowercase().contains(it))
        }
    }

    @Test fun urgeSurfingLifterExists() {
        val urge = PowerUps.byId("urge")
        assertNotNull("the urge-surfing lifter must exist", urge)
        assertTrue("has real steps", urge!!.steps.size >= 3)
    }

    @Test fun ledgerIsAppendOnly_thereIsNoStreakToBreak() {
        val e = RoughEdgeEntry("late scrolling", "after dinner", "phone charges in another room", "read a page")
        assertTrue("starts with no lapses", e.lapses.isEmpty())
        // A lapse APPENDS — it never resets a counter.
        val after = e.copy(lapses = e.lapses + 1_700_000_000_000L)
        assertEquals(1, after.lapses.size)
        // Round-trips through storage.
        val json = Json.encodeToString(RoughEdgeEntry.serializer(), after)
        assertEquals(after, Json.decodeFromString(RoughEdgeEntry.serializer(), json))
    }
}
