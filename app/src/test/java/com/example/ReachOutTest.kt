package com.example

import com.example.tools.ReachOut
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Reach Out — the zero-infrastructure "turn outward" of Phase 3 (the Lodge). The app never hosts a
 * relationship or a server; it just lowers the activation energy of the first message and hands it
 * to the phone's own SMS/WhatsApp/etc. via a share intent. These are the editable opener lines.
 */
class ReachOutTest {

    @Test
    fun openersAreNonEmptyAndClean() {
        assertTrue(ReachOut.OPENERS.size >= 4)
        ReachOut.OPENERS.forEach { assertTrue(it.isNotBlank()) }
    }

    @Test
    fun openerAtWrapsAndIsStable() {
        assertEquals(ReachOut.OPENERS[0], ReachOut.openerAt(0))
        assertEquals(ReachOut.OPENERS[0], ReachOut.openerAt(ReachOut.OPENERS.size))
        assertEquals(ReachOut.openerAt(2), ReachOut.openerAt(2))
    }

    @Test
    fun openerAtHandlesNegativeSafely() {
        assertEquals(ReachOut.OPENERS.last(), ReachOut.openerAt(-1))
    }
}
