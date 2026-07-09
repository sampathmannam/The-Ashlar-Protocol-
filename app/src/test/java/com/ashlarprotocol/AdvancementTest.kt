package com.ashlarprotocol

import com.ashlarprotocol.tools.Advancement
import com.ashlarprotocol.tools.Degree
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * The Advancement detector fires a rite exactly once per newly-earned degree, in order.
 * It is what turns a silent score threshold into a ceremonial crossing (Phase 2).
 */
class AdvancementTest {

    @Test fun freshApprentice_nothingPending() {
        assertNull(Advancement.pending(acknowledgedOrdinal = 0, score = 0))
    }

    @Test fun reachingFellowcraftThreshold_pendsFellowcraft() {
        assertEquals(Degree.FELLOWCRAFT, Advancement.pending(0, 15))
    }

    @Test fun justBelowThreshold_nothingPending() {
        assertNull(Advancement.pending(0, 14))
    }

    @Test fun onceFellowcraftAcknowledged_notPendingAgain() {
        assertNull(Advancement.pending(acknowledgedOrdinal = 1, score = 20))
    }

    @Test fun bigJump_firesLowerDegreeFirst() {
        // Score is past Master, but only Apprentice is acknowledged: Fellowcraft comes first,
        // so each raising still gets its own rite in order.
        assertEquals(Degree.FELLOWCRAFT, Advancement.pending(0, 99))
    }

    @Test fun afterFellowcraft_masterPendsWhenEarned() {
        assertEquals(Degree.MASTER_MASON, Advancement.pending(1, 40))
    }

    @Test fun masterEarnedButBelowThreshold_nothingPending() {
        assertNull(Advancement.pending(1, 39))
    }

    @Test fun summitAcknowledged_nothingPending() {
        assertNull(Advancement.pending(acknowledgedOrdinal = 2, score = 999))
    }

    @Test fun negativesAreClampedNotCrashed() {
        assertEquals(Degree.FELLOWCRAFT, Advancement.pending(-5, 15))
        assertNull(Advancement.pending(-5, -5))
    }
}
