package com.ashlarprotocol

import com.ashlarprotocol.data.RhythmAnchor
import com.ashlarprotocol.tools.Rhythm
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * The rhythm anchor (F6) — regularity, framed associationally. The clock formatting must be right, and
 * the reflection must say "linked," never "causes"/"cures" (the general-population evidence is
 * observational, and overclaiming would break the research-integrity line).
 */
class RhythmTest {

    @Test fun formatsTwelveHourClockCorrectly() {
        assertEquals("6:00 AM", Rhythm.formatTime(6 * 60))
        assertEquals("6:30 AM", Rhythm.formatTime(6 * 60 + 30))
        assertEquals("10:30 PM", Rhythm.formatTime(22 * 60 + 30))
        assertEquals("12:00 AM", Rhythm.formatTime(0))       // midnight
        assertEquals("12:00 PM", Rhythm.formatTime(12 * 60)) // noon
        assertEquals("11:00 PM", Rhythm.formatTime(23 * 60))
    }

    @Test fun formatTimeWrapsNegativeAndOverflow() {
        assertEquals("11:59 PM", Rhythm.formatTime(-1))       // wraps to the prior midnight-minus-one
        assertEquals("12:01 AM", Rhythm.formatTime(1440 + 1)) // wraps past a full day
    }

    @Test fun reflectionIsAssociationalNotCausalOrShaming() {
        val r = Rhythm.reflection(RhythmAnchor(wakeMinutesOfDay = 6 * 60 + 30, windDownMinutesOfDay = 22 * 60))
        assertTrue("names both times", r.contains("6:30 AM") && r.contains("10:00 PM"))
        assertTrue("associational framing", r.lowercase().contains("linked"))
        // No causal/clinical overclaim, no shame.
        listOf("causes", "cures", "will fix", "must", "fail").forEach {
            assertFalse("rhythm copy must stay associational/non-shaming ($it)", r.lowercase().contains(it))
        }
    }
}
